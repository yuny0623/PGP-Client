package org.pgp.openchat;

import org.pgp.configuration.Config;

import java.net.InetAddress;

public class ClientAction {
    private String ipStr;
    private String ip;
    private InetAddress ia;
    private String logo;

    public ClientAction(){
        System.out.println("");
        try {
            ia = InetAddress.getLocalHost();
            ipStr = ia.toString();
            ip = ipStr.substring(ipStr.indexOf("/") + 1); // get ip address from InetAddress.
            System.out.println("[Client] Client ip: " + ip);
            new ClientConnection(ip, Config.TCP_IP_CONNECTION_DEFAULT_PORT);
        }catch(Exception err){
            err.printStackTrace();
        }
    }
}
