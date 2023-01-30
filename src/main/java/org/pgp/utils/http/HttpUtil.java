package org.pgp.utils.http;

import org.pgp.configuration.Config;

import java.util.HashMap;
import java.util.Map;

public final class HttpUtil {

    public static synchronized int httpRequestParser(String header){
        Map<String, String> httpRequestMap = new HashMap<>();
        String[] rows = header.split("\n");

        String requestType = rows[0].split(" ")[0];
        if(!(requestType.equals("POST") || requestType.equals("GET"))){
            return -1;
        }
        httpRequestMap.put("Request-Method", requestType);
        for(int i = 1; i < rows.length; i++){
            String[] val = rows[i].split(" ");
            httpRequestMap.put(val[0].substring(0, val[0].length() - 1), val[1]);
        }
        return Integer.parseInt(httpRequestMap.get("Content-Length"));
    }

    public static synchronized String buildHttpResponse(String data, String httpStatusCode){
        String httpResponse = Config.HTTP_VERSION + " " + httpStatusCode + Config.HTTP_NEW_LINE + data;
        return httpResponse;
    }
}
