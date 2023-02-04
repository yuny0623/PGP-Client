package org.pgp.openchat;

import org.pgp.utils.json.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class DisplayThread extends Thread {

    Socket socket;
    String strIn;
    BufferedReader in;
    Map<String, String> parsedJson;

    public DisplayThread(Socket socket){
        this.socket = socket;
        parsedJson = new HashMap<>();
    }

    public boolean isValidInput(String input){
        if(input == null || input.isEmpty() || input.isBlank()){
            return false;
        }
        return true;
    }

    public boolean isValidParsedJson(String parsedJson){
        if(parsedJson == null || parsedJson.isEmpty() || parsedJson.isBlank()){
            System.out.println("[Error] parsing json error.");
            return false;
        }
        return true;
    }

    @Override
    public void run(){
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            try {
                strIn = in.readLine();

                // input validation check
                if(!isValidInput(strIn)){
                    continue;
                }

                parsedJson = JsonUtil.parseJson(strIn);

                if(parsedJson.get("publicKey").isEmpty()){
                    System.out.println("[Client] Invalid Input.");
                    continue;
                }

                System.out.println("[Notice] " + parsedJson.get("publicKey") + ":" + parsedJson.get("message"));
            }catch(Exception e){
                if(e.getMessage().equals("Connection reset")) {
                    System.out.println("[Error] Socket - " + e.getMessage());
                    System.out.println("Server is not running...");
                }else{
                    System.out.println(e.getMessage());
                }
                System.out.println("Exit Client.");
                try {
                    socket.close();
                    in.close();
                }catch(IOException err){
                    err.printStackTrace();
                    break;
                }
                break;
            }
        }
    }
}
