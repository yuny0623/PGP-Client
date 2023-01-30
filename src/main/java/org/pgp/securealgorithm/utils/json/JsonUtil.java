package org.pgp.securealgorithm.utils.json;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;

public final class JsonUtil {

    public JsonUtil(){

    }

    public static synchronized String generateJson(String str){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Notice", str);
        return jsonObject.toJSONString();
    }

    public static synchronized String generateJsonByCommand(String command, String str){
        JSONObject jsonObject = new JSONObject();
        if(command.equals("monsters")){
            jsonObject.put("MonsterInfo", str);
        }else if(command.equals("users")){
            jsonObject.put("UserInfo", str);
        }else{
            jsonObject.put("Notice", str);
        }
        return jsonObject.toJSONString();
    }

    public static synchronized String parseJson(String json){
        if(json.isEmpty() || json.isBlank()){
            System.out.println("Invalid json input!");
        }
        String command = null;
        JSONObject obj = null;
        JSONParser parser;
        String result = "";
        try {
            parser = new JSONParser();
            obj = (JSONObject) parser.parse(json);
            command = (String) obj.get("command");
        }catch(ParseException e){
            e.printStackTrace();
        }
        switch(command){
            case "move":
                String x = (String) obj.get("x");
                String y = (String) obj.get("y");
                result = "move " + x + " " + y;
                break;
            case "attack":
                result = "attack";
                break;
            case "monsters":
                result = "monsters";
                break;
            case "users":
                result = "users";
                break;
            case "chat":
                String opponent = (String) obj.get("opponent");
                String content = (String) obj.get("content");
                result = "chat " + opponent + " " + content;
                break;
            case "nickname":
                String nickname = (String) obj.get("nickname");
                result = "nickname " + nickname;
                break;
            case "bot":
                result = "bot";
                break;
            case "exit bot":
                result = "exit bot";
                break;
            case "potion":
                String item = (String) obj.get("item");
                result = "potion " + item;
                break;
            default:
                return "";
        }
        return result;
    }


    public static synchronized Map<String, String> parseHttpJson(String json){
        String result = "";
        String nickname = null;
        String command = null;
        Map<String, String> jsonMap = new HashMap<>();
        JSONObject obj = null;
        JSONParser parser;
        try {
            parser = new JSONParser();
            obj = (JSONObject) parser.parse(json);
            command = (String) obj.get("command");
            nickname = (String) obj.get("nickname");
        }catch(ParseException e){
            e.printStackTrace();
        }
        jsonMap.put("nickname", nickname);
        jsonMap.put("command", command);
        return jsonMap;
    }
}
