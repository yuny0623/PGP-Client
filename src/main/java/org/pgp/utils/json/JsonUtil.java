package org.pgp.utils.json;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.print.DocFlavor;
import java.util.HashMap;
import java.util.Map;

public final class JsonUtil {

    public JsonUtil(){

    }

    public static synchronized String generateJson(String message, String publicKey){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);
        jsonObject.put("publicKey", publicKey);
        return jsonObject.toJSONString();
    }

    public static synchronized Map<String, String> parseJson(String json){
        Map<String, String> jsonMap = new HashMap<>();
        String result = "";
        if(json.isEmpty() || json.isBlank()){
            System.out.println("Invalid json input!");
            jsonMap.put("publicKey", "");
            jsonMap.put("message", "");
            return jsonMap;
        }
        JSONObject obj = null;
        JSONParser parser;
        String publicKey = null ;
        String message = null ;
        try {
            parser = new JSONParser();
            obj = (JSONObject) parser.parse(json);
            message = (String) obj.get("message");
            publicKey = (String) obj.get("publicKey");
            jsonMap.put("message", message);
            jsonMap.put("publicKey", publicKey);
        }catch(ParseException e){
            e.printStackTrace();
        }
        return jsonMap;
    }
}
