package theflash.flashcard.service.impl.dictionary;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import theflash.flashcard.utils.Constants;
import theflash.flashcard.utils.HtmlHelper;
import theflash.flashcard.utils.Meaning;
import theflash.flashcard.utils.Translation;

public class CambridgeDictionaryServiceImpl extends DictionaryServiceImpl {

  @Override
  public boolean isConnectionEstablished(String word, Translation translation) {

    this.word = word;
    this.translation = translation;

    boolean isConnectionEstablished = false;
    String url;
    if (translation.equals(Translation.EN_CN_TD)) {
      url = HtmlHelper.lookupUrl(Constants.DICT_CAMBRIDGE_URL_EN_CN_TD, word);
    } else if (translation.equals(Translation.EN_CN_SP)) {
      url = HtmlHelper.lookupUrl(Constants.DICT_CAMBRIDGE_URL_EN_CN_SP, word);
    } else if (translation.equals(Translation.EN_FR)) {
      url = HtmlHelper.lookupUrl(Constants.DICT_CAMBRIDGE_URL_EN_FR, word);
    } else {
      url = HtmlHelper.lookupUrl(Constants.DICT_CAMBRIDGE_URL_EN_JP, word);
    }
    doc = HtmlHelper.getDocument(url);
    if (doc != null) {
      isConnectionEstablished = true;
    }
    return isConnectionEstablished;
  }

  @Override
  public boolean isWordingCorrect() {

    String title = HtmlHelper.getText(doc, "title", 0);
    if (title.contains(Constants.DICT_CAMBRIDGE_SPELLING_WRONG)) {
      return false;
    }

    String word = HtmlHelper.getText(doc, "span.headword>span,.hw", 0);
    if (word.isEmpty()) {
      return false;
    }

    return true;
  }

  @Override
  public String getWordType() {

    if (type == null) {
      type = "(" + HtmlHelper.getText(doc, "span.pos", 0) + ")";
    }
    return type.isEmpty() ? "" : type;
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
  public String getImage(String username, String selector) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getPron(String username, String selector) {
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
