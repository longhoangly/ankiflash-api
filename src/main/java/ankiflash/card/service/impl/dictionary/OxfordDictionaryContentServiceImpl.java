package ankiflash.card.service.impl.dictionary;

import static java.util.Optional.ofNullable;

import ankiflash.card.dto.Meaning;
import ankiflash.card.utility.Constant;
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

public class OxfordDictionaryContentServiceImpl extends DictionaryContentServiceImpl {

  private static final Logger logger =
      LoggerFactory.getLogger(OxfordDictionaryContentServiceImpl.class);

  @Override
  public boolean isConnected(String combinedWord, Translation translation) {

    String[] wordParts = combinedWord.split(Constant.SUB_DELIMITER);
    if (combinedWord.contains(Constant.SUB_DELIMITER) && wordParts.length == 3) {
      this.word = wordParts[0];
      this.wordId = wordParts[1];
      this.originalWord = wordParts[2];
    } else {
      throw new BadRequestException("Incorrect word format: " + combinedWord);
    }

    String url = HtmlHelper.lookupUrl(Constant.OXFORD_URL_EN_EN, this.wordId);
    doc = HtmlHelper.getDocument(url);
    return doc == null;
  }

  @Override
  public boolean isInvalidWord() {

    String title = HtmlHelper.getText(doc, "title", 0);
    if (title.contains(Constant.OXFORD_SPELLING_WRONG)
        || title.contains(Constant.OXFORD_WORD_NOT_FOUND)) {
      return true;
    }

    String word = HtmlHelper.getText(doc, ".headword", 0);
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
        return Constant.NO_EXAMPLE;
      } else if (example.isEmpty()) {
        break;
      } else {
        word = word.toLowerCase();
        example = example.toLowerCase();
        if (example.contains(word)) {
          example = example.replaceAll(word, "{{c1::" + word + "}}");
        } else {
          example = String.format("%s %s", example, "{{c1::...}}");
        }
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
  public void getImages(String ankiDir, boolean isOffline) {

    this.ankiDir = ankiDir;
    String googleImage =
        "<a href=\"https://www.google.com/search?biw=1280&bih=661&tbm=isch&sa=1&q="
            + word
            + "\" style=\"font-size: 15px; color: blue\">Search images by the word</a>";

    imageLink = HtmlHelper.getAttribute(doc, "a.topic", 0, "href");
    if (imageLink.isEmpty()) {
      imageLink = "";
      imageOnline = imageOffline = googleImage;
      return;
    }

    String imageName = DictHelper.getFileName(imageLink);
    imageOnline = "<img src=\"" + imageLink + "\"/>";
    imageOffline = "<img src=\"" + imageName + "\"/>";

    if (isOffline) {
      DictHelper.downloadFiles(ankiDir, imageLink);
    }
  }

  @Override
  public void getSounds(String ankiDir, boolean isOffline) {

    this.ankiDir = ankiDir;
    soundLinks = HtmlHelper.getAttribute(doc, "div.pron-uk", 0, "data-src-mp3");
    if (soundLinks.isEmpty()) {
      soundLinks = soundOnline = soundOffline = "";
      return;
    }

    String usSound = HtmlHelper.getAttribute(doc, "div.pron-us", 0, "data-src-mp3");
    if (!usSound.isEmpty()) {
      soundLinks = String.format("%1$s;%2$s", usSound, soundLinks);
    }

    String[] sounds = soundLinks.split(";");
    for (var soundLink : sounds) {
      String soundName = DictHelper.getFileName(soundLink);
      soundOnline =
          String.format(
              "<audio src=\"%1$s\" type=\"audio/wav\" preload=\"auto\" autobuffer controls>[sound:%2$s]</audio> %3$s",
              soundLink, soundLink, ofNullable(soundOnline).orElse(""));
      soundOffline =
          String.format(
              "<audio src=\"%1$s\" type=\"audio/wav\" preload=\"auto\" autobuffer controls>[sound:%2$s]</audio> %3$s",
              soundName, soundName, ofNullable(soundOffline).orElse(""));
    }

    if (isOffline) {
      DictHelper.downloadFiles(ankiDir, soundLinks);
    }
  }

  @Override
  public String getMeaning() {

    getWordType();
    getPhonetic();

    List<Meaning> meanings = new ArrayList<>();
    Element wordFamilyElm = doc.selectFirst("span.unbox[unbox=\"wordfamily\"]");
    if (wordFamilyElm != null) {
      Elements wordFamilyElms = wordFamilyElm.select("span.p");

      List<String> wordFamilies = new ArrayList<>();
      for (Element wordFamily : wordFamilyElms) {
        wordFamilies.add(wordFamily.text());
      }

      Meaning meaning = new Meaning("", wordFamilies);
      meaning.setWordType("Word Family");
      meanings.add(meaning);
    }

    Element wordFormElm = doc.selectFirst("span.unbox[unbox=\"verbforms\"]");
    if (wordFormElm != null) {
      Elements wordFormElms = wordFormElm.select("td.verbforms");

      List<String> wordForms = new ArrayList<>();
      for (Element wordForm : wordFormElms) {
        wordForms.add(wordForm.text());
      }

      Meaning meaning = new Meaning("", wordForms);
      meaning.setWordType("Verb Forms");
      meanings.add(meaning);
    }

    Elements meanGroup = doc.select(".sense");
    for (Element meanElem : meanGroup) {
      Element defElm = meanElem.selectFirst(".def");

      List<String> examples = new ArrayList<>();
      // SEE ALSO section
      Element subDefElm = meanElem.selectFirst(".xrefs");
      if (subDefElm != null) {
        Element subDefPrefix = subDefElm.selectFirst(".prefix");
        Element subDefLink = subDefElm.selectFirst(".Ref");
        if (subDefPrefix != null
            && subDefLink != null
            && !subDefLink.attr("title").contains("full entry")) {
          examples.add(
              String.format(
                  "<a href=\"%1$s\">%2$s %3$s</a>",
                  subDefLink.attr("href"), subDefPrefix.text().toUpperCase(), subDefLink.text()));
        }
      }

      Elements exampleElms = meanElem.select(".x");
      for (Element exampleElem : exampleElms) {
        examples.add(exampleElem.text());
      }

      meanings.add(new Meaning(defElm != null ? defElm.text() : "", examples));

      Element extraExample =
          HtmlHelper.getElement(meanElem, "span.unbox[unbox=\"extra_examples\"]", 0);
      if (extraExample != null) {
        exampleElms = extraExample.select(".unx");

        examples = new ArrayList<>();
        for (Element exampleElm : exampleElms) {
          examples.add(exampleElm.text());
        }

        Meaning meaning = new Meaning("", examples);
        meaning.setWordType("Extra Examples");
        meanings.add(meaning);
      }
    }

    Element wordOriginElm = doc.selectFirst("span.unbox[unbox=\"wordorigin\"]");
    if (wordOriginElm != null) {
      Element originElm = wordOriginElm.selectFirst(".p");
      if (originElm != null) {
        List<String> wordOrigins = new ArrayList<>();
        wordOrigins.add(originElm.text());

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
