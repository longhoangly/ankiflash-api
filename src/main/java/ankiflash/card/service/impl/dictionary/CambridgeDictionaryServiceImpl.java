package ankiflash.card.service.impl.dictionary;

import ankiflash.card.dto.Meaning;
import ankiflash.card.utility.Constants;
import ankiflash.card.utility.HtmlHelper;
import ankiflash.card.utility.Translation;
import ankiflash.utility.exception.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CambridgeDictionaryServiceImpl extends DictionaryServiceImpl {

  private static final Logger logger =
      LoggerFactory.getLogger(CambridgeDictionaryServiceImpl.class);

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

    String url = "";
    if (translation.equals(Translation.EN_CN_TD)) {
      url = HtmlHelper.lookupUrl(Constants.CAMBRIDGE_URL_EN_CN_TD, this.wordId);
    } else if (translation.equals(Translation.EN_CN_SP)) {
      url = HtmlHelper.lookupUrl(Constants.CAMBRIDGE_URL_EN_CN_SP, this.wordId);
    } else if (translation.equals(Translation.EN_FR)) {
      url = HtmlHelper.lookupUrl(Constants.CAMBRIDGE_URL_EN_FR, this.wordId);
    } else if (translation.equals(Translation.EN_JP)) {
      url = HtmlHelper.lookupUrl(Constants.CAMBRIDGE_URL_EN_JP, this.wordId);
    }
    doc = HtmlHelper.getDocument(url);
    return doc == null;
  }

  @Override
  public boolean isWordNotFound() {

    String title = HtmlHelper.getText(doc, "title", 0);
    if (title.contains(Constants.CAMBRIDGE_SPELLING_WRONG)) {
      return true;
    }

    String word = HtmlHelper.getText(doc, "span.headword>span,.hw", 0);
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
    throw new UnsupportedOperationException();
  }

  @Override
  public String getPhonetic() {
    if (phonetic == null) {
      String phoneticBrE = HtmlHelper.getText(doc, "span.pron", 0);
      String phoneticNAmE = HtmlHelper.getText(doc, "span.pron", 1);
      phonetic = String.format("%1$s %2$s", phoneticBrE, phoneticNAmE);
    }
    return phonetic;
  }

  @Override
  public void preProceedImage(String ankiDir, String selector) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void downloadImage() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void preProceedSound(String ankiDir, String selector) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void downloadSound() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getMeaning() {

    getWordType();
    getPhonetic();

    List<Meaning> meanings = new ArrayList<>();
    Elements headerGroups = doc.select("div[class*=entry-body__el]");
    for (Element headerGroup : headerGroups) {
      // Word Type Header
      Meaning meaning = new Meaning();
      Element wordTypeHeader = headerGroup.selectFirst(".pos-header,div[class*=di-head]");
      if (wordTypeHeader != null) {
        List<String> headerTexts = new ArrayList<>();
        Elements elements = wordTypeHeader.select(".pos,.pron");
        for (Element element : elements) {
          headerTexts.add(element.text());
        }
        meaning.setWordType(String.join(" ", headerTexts));
        meanings.add(meaning);
      }

      List<String> examples;
      Elements meanGroups = headerGroup.select("div.sense-block");
      for (Element meanGroup : meanGroups) {
        // Header
        meaning = new Meaning();
        Element header = meanGroup.selectFirst("h3");
        if (header != null) {
          meaning.setWordType(header.text());
          meanings.add(meaning);
        }

        // Meaning
        Elements meaningElements = meanGroup.select("div[class*=def-block]");
        for (Element meaningElem : meaningElements) {
          meaning = new Meaning();
          Element def = meaningElem.selectFirst("b.def");
          if (def != null) {
            meaning.setMeaning(def.text());
          }

          examples = new ArrayList<>();
          for (Element element : meaningElem.select(".eg,.trans")) {
            examples.add(element.text());
          }
          meaning.setExamples(examples);
          meanings.add(meaning);
        }

        // Extra Examples
        meaning = new Meaning();
        examples = new ArrayList<>();
        Element extraExample = meanGroup.selectFirst(".extraexamps>p");
        if (extraExample != null) {
          meaning.setWordType(extraExample.text());
          for (Element element : meanGroup.select(".extraexamps>ul>li.eg")) {
            examples.add(element.text());
          }
          meaning.setExamples(examples);
          meanings.add(meaning);
        }
      }
    }

    return HtmlHelper.buildMeaning(word, type, phonetic, meanings);
  }

  @Override
  public String getDictionaryName() {
    return "Cambridge Dictionary";
  }
}
