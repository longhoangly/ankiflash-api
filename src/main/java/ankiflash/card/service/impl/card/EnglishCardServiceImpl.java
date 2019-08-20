package ankiflash.card.service.impl.card;

import ankiflash.card.dto.Card;
import ankiflash.card.service.DictionaryService;
import ankiflash.card.service.impl.dictionary.CambridgeDictionaryServiceImpl;
import ankiflash.card.service.impl.dictionary.LacVietDictionaryServiceImpl;
import ankiflash.card.service.impl.dictionary.OxfordDictionaryServiceImpl;
import ankiflash.card.utility.Constants;
import ankiflash.card.utility.DictHelper;
import ankiflash.card.utility.HtmlHelper;
import ankiflash.card.utility.Status;
import ankiflash.card.utility.Translation;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnglishCardServiceImpl extends CardServiceImpl {

  private static final Logger logger = LoggerFactory.getLogger(EnglishCardServiceImpl.class);

  @Override
  public List<String> getWords(String word, Translation translation) {

    List<String> engWords = new ArrayList<>();
    if (translation.equals(Translation.EN_EN)) {
      String url = HtmlHelper.lookupUrl(Constants.OXFORD_SEARCH_URL_EN_EN, word);
      Document doc = HtmlHelper.getDocument(url);

      if (doc != null) {
        String firstLink = HtmlHelper.getAttribute(doc, "link", 0, "href");
        String firstWordId = firstLink.isEmpty() ? "" : DictHelper.getLastElement(firstLink);
        engWords.add(word + ":" + firstWordId + ":" + word);

        Elements allMatchesBlocks = doc.select("dl.accordion.ui-grad");
        for (Element allMatches : allMatchesBlocks) {
          Elements lis = allMatches.select("li");
          for (Element li : lis) {
            li.getElementsByTag("pos").remove();
            String matchedWord = li.getElementsByTag("span").text();
            String wordId = DictHelper.getLastElement(li.getElementsByTag("a").attr("href"));
            engWords.add(matchedWord + ":" + wordId + ":" + word);
          }
        }
      } else {
        logger.info("Words not found!");
      }
    } else {
      engWords.add(word);
    }

    return engWords;
  }

  @Override
  public Card generateCard(String word, Translation translation, String ankiDir) {

    logger.info("Word = " + word);
    logger.info("Source = " + translation.getSource());
    logger.info("Target = " + translation.getTarget());

    Card card = new Card(word);
    DictionaryService oxfordDict = new OxfordDictionaryServiceImpl();
    DictionaryService cambridgeDict = new CambridgeDictionaryServiceImpl();
    DictionaryService lacVietDict = new LacVietDictionaryServiceImpl();

    // English to English
    if (translation.equals(Translation.EN_EN)) {

      if (oxfordDict.isConnectionFailed(word, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constants.CONNECTION_FAILED);
        return card;
      } else if (oxfordDict.isWordNotFound()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constants.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(oxfordDict.getWordType());
      card.setPhonetic(oxfordDict.getPhonetic());
      card.setMeaning(oxfordDict.getMeaning());
      card.setCopyright(String.format(Constants.COPYRIGHT, oxfordDict.getDictionaryName()));

      // English to Chinese/French/Japanese
    } else if (translation.equals(Translation.EN_CN_TD)
        || translation.equals(Translation.EN_CN_SP)
        || translation.equals(Translation.EN_JP)
        || translation.equals(Translation.EN_FR)) {

      if (oxfordDict.isConnectionFailed(word, translation)
          || cambridgeDict.isConnectionFailed(word, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constants.CONNECTION_FAILED);
        return card;
      } else if (oxfordDict.isWordNotFound() || cambridgeDict.isWordNotFound()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constants.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(oxfordDict.getWordType());
      card.setPhonetic(oxfordDict.getPhonetic());
      card.setMeaning(cambridgeDict.getMeaning());
      card.setCopyright(
          String.format(
              Constants.COPYRIGHT,
              String.join(
                  ", and ", oxfordDict.getDictionaryName(), cambridgeDict.getDictionaryName())));

      // English to Vietnamese
    } else if (translation.equals(Translation.EN_VN)) {

      if (oxfordDict.isConnectionFailed(word, translation)
          || lacVietDict.isConnectionFailed(word, translation)) {
        card.setStatus(Status.Connection_Failed);
        card.setComment(Constants.CONNECTION_FAILED);
        return card;
      } else if (oxfordDict.isWordNotFound() || lacVietDict.isWordNotFound()) {
        card.setStatus(Status.Word_Not_Found);
        card.setComment(Constants.WORD_NOT_FOUND);
        return card;
      }

      card.setWordType(oxfordDict.getWordType());
      card.setPhonetic(oxfordDict.getPhonetic());
      card.setMeaning(lacVietDict.getMeaning());
      card.setCopyright(
          String.format(
              Constants.COPYRIGHT,
              String.join(
                  ", and ", oxfordDict.getDictionaryName(), lacVietDict.getDictionaryName())));

    } else {
      card.setStatus(Status.Not_Supported_Translation);
      card.setComment(
          String.format(
              Constants.NOT_SUPPORTED_TRANSLATION,
              translation.getSource(),
              translation.getTarget()));
      return card;
    }

    card.setExample(oxfordDict.getExample());
    String ukPron = oxfordDict.getPron(ankiDir, "div.pron-uk");
    String usPron = oxfordDict.getPron(ankiDir, "div.pron-us");
    if (!ukPron.isEmpty() && !usPron.isEmpty()) {
      card.setPron("BrE " + ukPron + " NAmE " + usPron);
    }
    card.setImage(oxfordDict.getImage(ankiDir, "a.topic"));
    card.setTag(oxfordDict.getTag());

    card.setStatus(Status.Success);
    card.setComment(Constants.SUCCESS);

    String cardContent =
        card.getWord()
            + Constants.TAB
            + card.getWordType()
            + Constants.TAB
            + card.getPhonetic()
            + Constants.TAB
            + card.getExample()
            + Constants.TAB
            + card.getPron()
            + Constants.TAB
            + card.getImage()
            + Constants.TAB
            + card.getMeaning()
            + Constants.TAB
            + card.getCopyright()
            + Constants.TAB
            + card.getTag()
            + "\n";
    card.setContent(cardContent);

    return card;
  }
}
