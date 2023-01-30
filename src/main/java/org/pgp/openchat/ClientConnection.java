package org.pgp.openchat;

import org.pgp.securealgorithm.pgp.PGP;
import org.pgp.wallet.KeyWallet;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class ClientConnection implements Runnable{

    private PGP pgp;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String str;
    private String ip;
    private int port;

    private String nickname;
    private String publicKey;
    private String privateKey;
    private int connectionRequestCount;

    private Thread thread;
    private HashMap<String, String> userMap = new HashMap<>();         // nickname, publicKey
    private HashMap<String, SecretKey> commonKeyMap = new HashMap<>(); // nickname, commonKey -> for DirectMessage

    public ClientConnection(String ip, int port){
        this.ip = ip;
        this.port = port;
        this.nickname = null;
        System.out.println("[Client] Creating publicKey...");
        this.publicKey = KeyWallet.getMainASymmetricKey().getPublicKey();
        System.out.println("[Client] Creating privateKey...");
        this.privateKey = KeyWallet.getMainASymmetricKey().getPrivateKey();
        this.userMap.put(nickname, publicKey);
        initNet(ip, port);
    }

    public boolean checkServerLive(String ip, int port){
        while(true){
            System.out.println("[Client] Connecting to server.");
            try{
                socket = new Socket(ip, port);
            }catch (UnknownHostException e){
                System.out.println("[Client] Different IP Address.");
            }catch(IOException e){
                System.out.println("[Client] Connection failed.");
            }
            if(socket != null){
                return true;
            }else if(connectionRequestCount >= 5){
                System.out.println("[Client] Cannot connect to server.");
                return false;
            }
            else {
                ++connectionRequestCount;
                System.out.println("[Client] Send connection request again - " +connectionRequestCount + "times.");
            }
        }
    }

    public void initNet(String ip, int port){
        System.out.println("[Client] Initializing Socket connection.");
        try{
            if(!checkServerLive(ip, port)){
                System.out.println("[Client] Connection failed.");
                return;
            }
            System.out.println("[Client] Connection success.");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        }catch(UnknownHostException e){
            System.out.println("[Client] Unknown host exception.");
            e.printStackTrace();
        }catch(IOException e){
            System.out.println("[Client] Connection failed.");
            e.printStackTrace();
        }
        thread = new Thread(this, "Client");
        thread.start();
    }

    @Override
    public void run() {
        try {
            out.println(nickname);
            out.println(publicKey);
        }catch(NullPointerException e){
            System.out.println("[Client] Server is not running - " + e.getMessage());
        }
        while(true){
            try{
                str = in.readLine();
                if(str == null){
                    continue;
                }
                if(str.length() >= 18 && str.substring(0, 17 + 1).equals("[userInfoResponse]")){
                    String[] info = str.split(" ");
                    for(int i = 1; i < info.length; i+=2){
                        userMap.put(info[i], info[i+1]);
                    }
                    continue;
                }else if(str.length() >= 11 && str.substring(0, 11 + 1).equals("[New Member]")){
                    String strBody = str.substring(11+1, str.length()); // [sss:aaa]
                    String receivedNickname = strBody.substring(1, strBody.indexOf(":"));
                    String receivedPublicKey = strBody.substring(strBody.indexOf(":") + 1, strBody.indexOf("]"));
                    userMap.put(receivedNickname, receivedPublicKey);
                    continue;
                }
                System.out.println("[Client] " + str + "\n");
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
