import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

public class Crawler {
    private static final String WD_URL = "http://wiprodigital.com";
    private static final String HTTP_DOMAIN_NAME = "http://wiprodigital";
    private static final String HTTPS_DOMAIN_NAME = "https://wiprodigital";
    private static final String OUTPUT_TXT = "out.txt";
    private static final String OUTPUT_JSON = "out.json";

    private HashSet<String> listUrlFromParent = new HashSet<>();
    private HashSet<String> finalUrls = new HashSet<>();
    private HashSet<String> iterationSet;

    private JsonConverter json = new JsonConverter();

    private int uniqueUrlSize = 0;

    protected void run() {
        createFirstListOfUrl();
        createListOfAllUrls();
        importToFile(finalUrls);
        importToFile(json.getJson());
    }

    private void createFirstListOfUrl() {
        json.setMainUrl(WD_URL);
        parsePage(WD_URL);
    }

    private void createListOfAllUrls() {
        iterationSet = new HashSet<>(listUrlFromParent);
        finalUrls.addAll(listUrlFromParent);

        while (!isFoundAllElements()) {
            for (String str : iterationSet) {
                json.setMainUrl(str);
                if (isWDUrl(str)) {
                    parsePage(str);
                }
                json.addRecord();
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

    private boolean isWDUrl(String url) {
        return url.startsWith(HTTP_DOMAIN_NAME) || url.startsWith(HTTPS_DOMAIN_NAME);
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
        Elements urlString = doc.select("a[href]");
        for (Element url : urlString) {
            String fullUrl = url.absUrl("href");
            if (isWDUrl(url.absUrl("href"))) {
                json.addInternal(fullUrl);
                listUrlFromParent.add(fullUrl);
                finalUrls.add(fullUrl);
            } else {
                json.addExternal(fullUrl);
            }
        }
    }

    private void checkOnImage(Document doc) {
        Elements imageElements = doc.select("img[src]");
        for (Element url : imageElements) {
            String imgSrc = url.attr("abs:src");
            json.addImg(imgSrc);
            finalUrls.add(imgSrc);
        }
    }

    private void importToFile(HashSet<String> urls) {
        try {
            FileWriter fw = new FileWriter(OUTPUT_TXT);
            for (String s : urls) {
                fw.write(s + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void importToFile(JSONObject json) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(OUTPUT_JSON));
            json.write(writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}