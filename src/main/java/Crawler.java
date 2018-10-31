import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

public class Crawler {
    private static final String WD_URL = "http://wiprodigital.com";
    private static final String DOMAIN_NAME = "wiprodigital";
    private static final String GOOGLE = "google";
    private static final String TWITTER = "twitter";
    private static final String OUTPUT_FILE = "out.txt";

    private HashSet<String> listUrlFromParent = new HashSet<>();
    private HashSet<String> finalUrls = new HashSet<>();
    private HashSet<String> iterationSet;

    private int uniqueUrlSize = 0;


    protected void run() {
        createFirstListOfUrl();
        createListOfAllUrls();
        importToFile(finalUrls);
    }

    private void createFirstListOfUrl() {
        parsePage(WD_URL);
    }

    private void createListOfAllUrls() {
        iterationSet = new HashSet<>(listUrlFromParent);
        finalUrls.addAll(listUrlFromParent);

        while (!isFoundAllElements()) {
            for (String str : iterationSet) {
                if(isWDUrl(str)){
                    parsePage(str);
                }
            }
            iterationSet = listUrlFromParent;
            listUrlFromParent.clear();
        }
    }

    private boolean isFoundAllElements() {
        if (finalUrls.size() == uniqueUrlSize) {
            return true;
        } else {
            uniqueUrlSize = finalUrls.size();
            return false;
        }
    }

    private boolean isWDUrl(String url){
        return url.startsWith("http://" + DOMAIN_NAME) || url.startsWith("https://" + DOMAIN_NAME);
    }

    private void parsePage(String wdUrl) {
        try {

            Document doc = Jsoup.connect(wdUrl).get();
            checkUrlOneByOne(doc);
            checkOnImage(doc);

        } catch (HttpStatusException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkUrlOneByOne(Document doc) {
        Elements newsHeadlines = doc.select("a[href]");
        for (Element headline : newsHeadlines) {
            if (idRequiredElement(headline) && !finalUrls.contains(headline.absUrl("href"))) {
                String url = headline.absUrl("href");
                listUrlFromParent.add(url);
                finalUrls.add(url);
            }
        }
    }

    private void checkOnImage(Document doc) {
        Elements imageElements = doc.select("img[src]");
        for (Element url : imageElements) {
            String imgSrc = url.attr("abs:src");
            finalUrls.add(imgSrc);
        }
    }

    private boolean idRequiredElement(Element headline) {
        if (isHttpOrHttps(headline.absUrl("href"))) {
            String s = convertUrl(headline);
            return !s.contains(GOOGLE) && !s.contains(TWITTER) && !headline.absUrl("href").contains("#");
        } else {
            return false;
        }
    }

    private boolean isHttpOrHttps(String s) {
        return s.startsWith("http") || s.startsWith("https");
    }

    private String convertUrl(Element headline) {
        try {
            return new URL(headline.absUrl("href")).getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void importToFile(HashSet<String> urls) {
        try {
            FileWriter fw = new FileWriter(OUTPUT_FILE);
            for (String s : urls) {
                fw.write(s + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}