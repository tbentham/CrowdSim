import com.google.gson.*;

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
}
