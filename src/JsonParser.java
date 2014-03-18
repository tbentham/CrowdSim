import com.google.gson.Gson;

import java.util.HashMap;

// TODO: Doesn't need an explanation
class JsonParser{
    
    private String json;
    private Gson gson = new Gson();
    
    JsonParser(String json){
    	this.json = json;
    }
    
    public Cobject[] parse(){

    	Cobject[] objs = gson.fromJson(json, Cobject[].class);
    	
        return objs;
    }
    public HashMap parseConf(){

        HashMap<String, Integer> objs = gson.fromJson(json, HashMap.class);

        return objs;
    }
}
