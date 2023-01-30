package org.pgp.openchat;

import org.pgp.key.keys.ASymmetricKey;
import org.pgp.key.keys.SymmetricKey;
import org.pgp.securealgorithm.pgp.PGP;
import org.pgp.utils.json.JsonUtil;
import org.pgp.wallet.KeyWallet;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Scanner;

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
    private SymmetricKey symmetricKey;
    private ASymmetricKey aSymmetricKey;

    private Thread thread;
    private Scanner sc = new Scanner(System.in);

    public ClientConnection(String ip, int port){
        this.ip = ip;
        this.port = port;
        while(this.nickname == null) {
            System.out.println("[Client] Type nickname: ");
            this.nickname = sc.nextLine();
        }
        System.out.println("[Client] Creating publicKey.");
        this.publicKey = KeyWallet.getMainASymmetricKey().getPublicKey();
        System.out.println("[Client] Creating privateKey.");
        this.privateKey = KeyWallet.getMainASymmetricKey().getPrivateKey();
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
            // send to server
            String json = JsonUtil.generateJson(this.nickname, this.publicKey);
            out.println(json);
            // initialize symmetrickey
            if(symmetricKey == null){
                symmetricKey = new SymmetricKey();
            }
        }catch(NullPointerException e){
            System.out.println("[Client] Server is not running - " + e.getMessage());
        }
        while(true){
            try{
                str = in.readLine();
                if(str == null){
                    continue;
                }
                Map<String, String> jsonMap = JsonUtil.parseJson(str);
                String publicKey = jsonMap.get("publicKey");
                String message = jsonMap.get("message");
                System.out.println("publicKey: " + publicKey + ", message: " + message);
                boolean isValidPublicKey = isValidInput(publicKey);
                if(!isValidPublicKey){
                    System.out.println("[Client] Invalid PublicKey.");
                }else {
                    System.out.println("[from: " + publicKey + "] " + message + "\n");
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public boolean isValidInput(String str){
        if(str == null || str.isBlank() || str.isEmpty()){
            return false;
        }
        return true;
    }
}
