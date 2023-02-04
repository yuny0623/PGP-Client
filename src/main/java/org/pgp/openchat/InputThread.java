package org.pgp.openchat;

import org.pgp.utils.json.JsonUtil;
import org.pgp.wallet.KeyWallet;

import java.io.*;
import java.net.Socket;

public class InputThread extends Thread{
    Socket socket;
    PrintWriter out;
    BufferedReader br;
    String clientInput;
    String json;
    String nickname;


    public InputThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            br = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 게임 진행
        while(true){
            try {
                clientInput = br.readLine();

                json = JsonUtil.generateJson(clientInput, KeyWallet.getMainASymmetricKey().getPublicKey());
                if(json.isEmpty() || json.isBlank()){
                    System.out.println("Invalid Command.");
                    continue;
                }
                out.println(json);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                    out.close();
                    br.close();
                }catch(IOException err){
                    err.printStackTrace();
                    System.exit(1);
                }
                break;
            }
        }
    }
}
