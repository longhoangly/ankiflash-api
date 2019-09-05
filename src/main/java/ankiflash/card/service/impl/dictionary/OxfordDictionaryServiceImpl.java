package ankiflash.card.service.impl.dictionary;

import ankiflash.card.dto.Meaning;
import ankiflash.card.utility.Constants;
import ankiflash.card.utility.DictHelper;
import ankiflash.card.utility.HtmlHelper;
import ankiflash.card.utility.Translation;
import ankiflash.utility.exception.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OxfordDictionaryServiceImpl extends DictionaryServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(OxfordDictionaryServiceImpl.class);

  @Override
  public boolean isConnectionFailed(String combinedWord, Translation translation) {

    String[] wordParts = combinedWord.split(":");
    if (combinedWord.contains(":") && wordParts.length == 3) {
      this.word = wordParts[0];
      this.wordId = wordParts[1];
      this.originalWord = wordParts[2];
    } else {
      throw new BadRequestException("Incorrect word format: " + combinedWord);
    }

    String url = HtmlHelper.lookupUrl(Constants.OXFORD_URL_EN_EN, this.wordId);
    doc = HtmlHelper.getDocument(url);
    return doc == null;
  }

  @Override
  public boolean isWordNotFound() {

    String title = HtmlHelper.getText(doc, "title", 0);
    if (title.contains(Constants.OXFORD_SPELLING_WRONG)
        || title.contains(Constants.OXFORD_WORD_NOT_FOUND)) {
      return true;
    }

    String word = HtmlHelper.getText(doc, "h2", 0);
    return word.isEmpty();
  }

  @Override
  public String getWordType() {

    if (type == null) {
      type = HtmlHelper.getText(doc, "span.pos", 0);

      type = type.isEmpty() ? "" : "(" + type + ")";
    }
    return type;
  }

  @Override
  public String getExample() {

    List<String> examples = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      String example = HtmlHelper.getText(doc, "span.x", i);
      if (example.isEmpty() && i == 0) {
        return Constants.NO_EXAMPLE;
      } else if (example.isEmpty()) {
        break;
      } else {
        word = word.toLowerCase();
        example = example.toLowerCase().replaceAll(word, "{{c1::" + word + "}}");
        examples.add(example);
      }
    }

    return HtmlHelper.buildExample(examples);
  }

  @Override
  public String getPhonetic() {

    if (phonetic == null) {
      String phoneticBrE = HtmlHelper.getText(doc, "span.phon", 0);
      String phoneticNAmE = HtmlHelper.getText(doc, "span.phon", 1);
      phonetic = String.format("%1$s %2$s", phoneticBrE, phoneticNAmE).replaceAll("//", " / ");
    }
    return phonetic;
  }

  @Override
  public void preProceedImage(String ankiDir, String selector) {

    this.ankiDir = ankiDir;
    String googleImage =
        "<a href=\"https://www.google.com/search?biw=1280&bih=661&tbm=isch&sa=1&q="
            + word
            + "\" style=\"font-size: 15px; color: blue\">Search images by the word</a>";

    imageLink = HtmlHelper.getAttribute(doc, selector, 0, "href");
    if (imageLink.isEmpty()) {
      imageLink = imageName = "";
      imageOnline = imageOffline = googleImage;
      return;
    }

    imageName = DictHelper.getLastElement(imageLink);
    imageOnline = "<img src=\"" + imageLink + "\"/>";
    imageOffline = "<img src=\"" + imageName + "\"/>";
  }

  @Override
  public void preProceedSound(String ankiDir, String selector) {

    this.ankiDir = ankiDir;
    soundLink = HtmlHelper.getAttribute(doc, selector, 0, "data-src-mp3");
    if (soundLink.isEmpty()) {
      soundName = soundLink = soundOnline = soundOffline = "";
      return;
    }

    soundName = DictHelper.getLastElement(soundLink);
    soundOnline =
        String.format("<source src=\"%1$s\">Native audio playback is not supported.", soundLink);
    soundOffline =
        String.format("<source src=\"%1$s\">Native audio playback is not supported.", soundName);
  }

  @Override
  public String getMeaning() {

    getWordType();
    getPhonetic();

    List<Meaning> meanings = new ArrayList<>();
    Elements meanGroup = doc.select(".sn-g");
    for (Element meanElem : meanGroup) {
      Element defElem = meanElem.selectFirst(".def");

      List<String> examples = new ArrayList<>();
      Element subDefElem = meanElem.selectFirst(".xr-gs");
      if (subDefElem != null) {
        Element subDefPrefix = subDefElem.selectFirst(".prefix");
        Element subDefLink = subDefElem.selectFirst(".Ref");
        if (subDefPrefix != null
            && subDefLink != null
            && !subDefLink.attr("title").contains("full entry")) {
          examples.add(
              String.format(
                  "<a href=\"%1$s\">%2$s %3$s</a>",
                  subDefLink.attr("href"), subDefPrefix.text(), subDefLink.text()));
        }
      }

      Elements exampleElements = meanElem.select(".x");
      for (Element exampleElem : exampleElements) {
        examples.add(exampleElem.text());
      }

      meanings.add(new Meaning(defElem != null ? defElem.text() : "", examples));
    }

    Element extraExamples = doc.selectFirst("span.collapse[title=\"Extra examples\"]");
    if (extraExamples != null) {
      Elements exampleElements = extraExamples.select(".x");

      List<String> examples = new ArrayList<>();
      for (Element exampleElem : exampleElements) {
        examples.add(exampleElem.text());
      }

      Meaning meaning = new Meaning("", examples);
      meaning.setWordType("Extra Examples");
      meanings.add(meaning);
    }

    Element wordFormElem = doc.selectFirst("span.collapse[title=\"Verb Forms\"]");
    if (wordFormElem != null) {
      Elements wordFormElements = wordFormElem.select(".vp");

      List<String> wordForms = new ArrayList<>();
      for (Element wordForm : wordFormElements) {
        wordForms.add(wordForm.text());
      }

      Meaning meaning = new Meaning("", wordForms);
      meaning.setWordType("Verb Forms");
      meanings.add(meaning);
    }

    Element wordOriginElem = doc.selectFirst("span.collapse[title=\"Word Origin\"]");
    if (wordOriginElem != null) {
      Element originElem = wordOriginElem.selectFirst(".p");
      if (originElem != null) {
        List<String> wordOrigins = new ArrayList<>();
        wordOrigins.add(originElem.text());

        Meaning meaning = new Meaning("", wordOrigins);
        meaning.setWordType("Word Origin");
        meanings.add(meaning);
      }
    }

    return HtmlHelper.buildMeaning(word, type, phonetic, meanings);
  }

  @Override
  public String getDictionaryName() {
    return "Oxford Advanced Learner's Dictionary";
  }
}
