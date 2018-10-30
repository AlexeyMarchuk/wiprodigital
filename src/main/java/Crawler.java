import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;

public class Crawler {
    private static final String WD_URL = "http://wiprodigital.com";
    private static final String DOMAIN_NAME = "wiprodigital";
    private static final String GOOGLE = "http://www.google.com";
    private static final String OUTPUT_FILE = "out.txt";
    private HashSet<String> listUrlFromParent = new HashSet<>();
    private HashSet<String> hsFinal;
    private HashSet<String> uniqueUrl = new HashSet<>();
    private int uniqueUrlSize = 0;


    protected void run() {
        createFirstListOfUrl();
        createListOfAllUrls();
        importToFile(uniqueUrl);
    }

    private void createFirstListOfUrl() {
        makeSearch(WD_URL);
    }

    private void createListOfAllUrls() {
        hsFinal = new HashSet<>(listUrlFromParent);
        uniqueUrl.addAll(listUrlFromParent);

        while (!isFoundAllElements()) {
            for (String str : hsFinal) {
                makeSearch(str);
            }
            hsFinal = listUrlFromParent;
            listUrlFromParent.clear();
        }
    }

    private boolean isFoundAllElements() {
        if (uniqueUrl.size() == uniqueUrlSize) {
            return true;
        } else {
            uniqueUrlSize = uniqueUrl.size();
            return false;
        }
    }

    private void makeSearch(String wdUrl) {
        if (isOnLine()) {
            parsePage(wdUrl);
        } else {
            System.out.println("You are in Off-line mode");
        }
    }


    private void parsePage(String wdUrl) {
        try {
            Document doc = Jsoup.connect(wdUrl).get();
            Elements newsHeadlines = doc.select("a[href]");
            checkUrlOneByOne(newsHeadlines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkUrlOneByOne(Elements newsHeadlines) {
        int count = 0;
        for (Element headline : newsHeadlines) {
            ++count;
            if (idRequiredElement(headline) && !uniqueUrl.contains(headline.absUrl("href"))) {
                String url = headline.absUrl("href");
                listUrlFromParent.add(url);
                uniqueUrl.add(url);
            }
        }
    }

    private boolean idRequiredElement(Element headline) {
        String s = convertUrl(headline);
        return s.contains(DOMAIN_NAME) && !headline.absUrl("href").contains("#");
    }

    private String convertUrl(Element headline) {
        try {
            return new URL(headline.absUrl("href")).getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static boolean isOnLine() {
        try {
            final URL url = new URL(GOOGLE);
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
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