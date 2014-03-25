import com.google.gson.Gson;

import java.util.HashMap;

class JsonParser {

    private String json;
    private Gson gson = new Gson();

    JsonParser(String json) {
        this.json = json;
    }

    public Cobject[] parse() {
        return gson.fromJson(json, Cobject[].class);
    }

    public HashMap parseConf() {
        return gson.fromJson(json, HashMap.class);
    }
}
