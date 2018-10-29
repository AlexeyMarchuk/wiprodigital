import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;

public class Crawler {
    private static final String WD_URL = "http://wiprodigital.com";
    private static final String DOMAIN_NAME = "wiprodigital";
    private static final String GOOGLE = "http://www.google.com";

    protected void run() {
        makeSearch(WD_URL);
    }

    private void makeSearch(String wdUrl) {
        try {
            if (isOnLine()) {
                Document doc = Jsoup.connect(wdUrl).get();
                Elements newsHeadlines = doc.select("a[href]");
                System.out.println(newsHeadlines.size());
                HashSet<String> hs = new HashSet<>();

                for (Element headline : newsHeadlines) {
                    if (idRequiredElement(headline)) {
                        hs.add(headline.absUrl("href"));
                        System.out.println(headline.absUrl("href"));
                    }
                }
                System.out.println(hs.size());
            }else{
                System.out.println("You are in Off-line mode");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean idRequiredElement(Element headline) {
        return convertUrl(headline).contains(DOMAIN_NAME);
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
}