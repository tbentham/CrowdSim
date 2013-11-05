import com.google.gson.*;

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
}
