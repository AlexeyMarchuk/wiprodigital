import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JsonConverter {

    private Map<String, HashMap<String, ArrayList<String>>> wholeJSONMap = new HashMap<>();
    private HashMap<String, ArrayList<String>> internalMap = new HashMap<>();
    private ArrayList<String> internal = new ArrayList<>();
    private ArrayList<String> external = new ArrayList<>();
    private ArrayList<String> img = new ArrayList<>();
    private String mainUrl;
    private JSONObject finalBlock = new JSONObject();

    private final static String INTERNAL = "Internal URLs";
    private final static String EXTERNAL = "External URLs";
    private final static String IMAGES = "Images";


    protected void addInternal(String url) {
        internal.add(url);
    }

    protected void addExternal(String url) {
        external.add(url);
    }

    protected void addImg(String url) {
        img.add(url);
    }

    protected void setMainUrl(String url) {
        mainUrl = url;
    }

    protected void addRecord() {
        fillInternalMap();
        wholeJSONMap.put(new String(mainUrl), new HashMap<>(internalMap));
        clearLists();
    }

    private void fillInternalMap() {
        internalMap.put(INTERNAL, new ArrayList<>(internal));
        internalMap.put(EXTERNAL, new ArrayList<>(external));
        internalMap.put(IMAGES, new ArrayList<>(img));
    }

    private void clearLists() {
        internal.clear();
        external.clear();
        img.clear();
        internalMap.clear();
    }

    protected JSONObject getJson() {
        return new JSONObject(wholeJSONMap);
    }
}
