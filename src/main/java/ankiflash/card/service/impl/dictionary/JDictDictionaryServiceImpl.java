package ankiflash.card.service.impl.dictionary;

import ankiflash.card.dto.Meaning;
import ankiflash.card.utility.CardHelper;
import ankiflash.card.utility.Constants;
import ankiflash.card.utility.DictHelper;
import ankiflash.card.utility.HtmlHelper;
import ankiflash.card.utility.Translation;
import ankiflash.utility.IOUtility;
import ankiflash.utility.exception.BadRequestException;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDictDictionaryServiceImpl extends DictionaryServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(JDictDictionaryServiceImpl.class);

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

    String urlParameters = String.format("m=dictionary&fn=detail_word&id=%1$s", this.wordId);
    doc = CardHelper.getJDictDoc(Constants.JDICT_URL_VN_JP_OR_JP_VN, urlParameters);
    return doc == null;
  }

  @Override
  public boolean isWordNotFound() {

    Elements elements = doc.select("#txtKanji");
    if (elements.isEmpty()) {
      return true;
    }

    elements = doc.select("#word-detail-info");
    return elements.isEmpty();
  }

  @Override
  public String getWordType() {

    if (type == null) {
      Element element = HtmlHelper.getElement(doc, "label[class*=word-type]", 0);
      type = element != null ? element.text() : "";

      type = type.isEmpty() ? "" : "(" + type + ")";
    }

    return type;
  }

  @Override
  public String getExample() {

    Elements exampleElms = new Elements();
    for (int i = 0; i < 4; i++) {
      Element example = HtmlHelper.getElement(doc, "ul.ul-disc>li>u,ul.ul-disc>li>p", i);
      if (example == null && i == 0) {
        return Constants.NO_EXAMPLE;
      } else if (example == null) {
        break;
      } else {
        exampleElms.add(example);
      }
    }

    List<String> examples = getJDictExamples(exampleElms);
    String lowerWord = this.originalWord.toLowerCase();
    for (int i = 0; i < examples.size(); i++) {
      examples.set(
          i, examples.get(i).toLowerCase().replaceAll(lowerWord, "{{c1::" + lowerWord + "}}"));
    }

    return HtmlHelper.buildExample(examples, true);
  }

  @Override
  public String getPhonetic() {

    if (phonetic == null) {
      phonetic = HtmlHelper.getText(doc, "span.romaji", 0);
    }
    return phonetic;
  }

  @Override
  public String getImage(String ankiDir, String selector) {

    String google_image =
        "<a href=\"https://www.google.com/search?biw=1280&bih=661&tbm=isch&sa=1&q="
            + word
            + "\" style=\"font-size: 15px; color: blue\">Example Images</a>";

    String img_link = HtmlHelper.getAttribute(doc, "a.fancybox.img", 0, "href");
    if (img_link.isEmpty() || img_link.contains("no-image")) {
      return google_image;
    }

    img_link = "https://j-dict.com" + img_link.replaceFirst("\\?w=.*$", "");
    return "<img src=\"" + img_link + "\"/>";
  }

  @Override
  public String getPron(String ankiDir, String selector) {

    String pro_link = HtmlHelper.getAttribute(doc, "a.sound", 0, "data-fn");
    if (pro_link.isEmpty()) {
      return "";
    }

    String pro_name = DictHelper.getLastElement(pro_link);
    boolean isSuccess = false;
    File dir = new File(ankiDir);
    if (dir.exists()) {
      String output = Paths.get(dir.getAbsolutePath(), pro_name).toString();
      isSuccess = IOUtility.download(pro_link, output);
    } else {
      logger.warn("AnkiFlash folder not found! " + ankiDir);
    }

    return isSuccess ? "[sound:" + pro_name + "]" : "";
  }

  @Override
  public String getMeaning() {

    getWordType();
    getPhonetic();

    List<Meaning> meanings = new ArrayList<>();
    Meaning meaning = new Meaning();

    Element meanGroup = HtmlHelper.getElement(doc, "#word-detail-info", 0);
    Element wordType =
        HtmlHelper.getElement(Objects.requireNonNull(meanGroup), "label[class*=word-type]", 0);
    if (wordType != null) {
      meaning.setWordType(wordType.text());
    }
    meanings.add(meaning);

    Elements meanElements = meanGroup.select("ol.ol-decimal>li");
    for (Element meanElem : meanElements) {
      meaning = new Meaning();
      Element mean = HtmlHelper.getElement(meanElem, ".nvmn-meaning", 0);
      if (mean != null) {
        meaning.setMeaning(mean.text());
      }

      Elements exampleElms = meanElem.select("ul.ul-disc>li>u,ul.ul-disc>li>p");
      List<String> innerExamples = getJDictExamples(exampleElms);
      if (!innerExamples.isEmpty()) {
        meaning.setExamples(innerExamples);
      }
      meanings.add(meaning);
    }

    meaning = new Meaning();
    String kanji = HtmlHelper.getOuterHtml(meanGroup, "#search-kanji-list", 0);
    if (!kanji.isEmpty()) {
      meaning.setMeaning(kanji.replaceAll("\n", ""));
    }

    Elements exampleElms =
        meanGroup.select("#word-detail-info>ul.ul-disc>li>u,#word-detail-info>ul.ul-disc>li>p");
    List<String> examples = getJDictExamples(exampleElms);
    if (!examples.isEmpty()) {
      meaning.setExamples(examples);
    }
    meanings.add(meaning);

    return HtmlHelper.buildMeaning(word, type, phonetic, meanings, true);
  }

  @Override
  public String getDictionaryName() {
    return "J-Dict Dictionary";
  }

  private List<String> getJDictExamples(Elements exampleElms) {

    List<String> examples = new ArrayList<>();
    if (!exampleElms.isEmpty()) {
      List<String> jpExamples = new ArrayList<>();
      for (Element exampleElem : exampleElms) {
        if (exampleElem.hasAttr("class")) {
          examples.add(">>>>>" + exampleElem.text());
          jpExamples.add(exampleElem.text());
        } else {
          examples.add(exampleElem.text());
        }
      }

      String sentencesChain = String.join("=>=>=>=>=>", jpExamples);
      String urlParams = String.format("m=dictionary&fn=furigana&keyword=%1$s", sentencesChain);
      Document doc = CardHelper.getJDictDoc(Constants.JDICT_URL_VN_JP_OR_JP_VN, urlParams);
      sentencesChain = doc != null ? doc.body().html().replaceAll("\n", "") : sentencesChain;
      jpExamples = Arrays.asList(sentencesChain.split("=&gt;=&gt;=&gt;=&gt;=&gt;"));

      int index = 0;
      for (int i = 0; i < examples.size(); i++) {
        if (examples.get(i).contains(">>>>>") && index < jpExamples.size()) {
          examples.set(i, jpExamples.get(index));
          index++;
        }
      }
    }

    return examples;
  }
}
