package ru.geekbrains.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name ="";
    private int nameNumber;

    public ClientHandler(Server server, Socket socket, int nameNumber) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        System.out.println("user#"+nameNumber+"("+name+")"+": "+str);
                        if (str.equals("/end")) {
                            break;
                        }
                        if (str.length()>6 && str.startsWith("/name")){
                            this.name = str.substring(6).trim();
                            if (this.name.length()>0){
                                server.broadcastMsg("user#"+nameNumber+ " установил имя: "+name);
                                continue;
                            }
                        }
                        //(name.length()>0?name:"user#"+nameNumber)
                        server.broadcastMsg((name.length()>0?name:"user#"+nameNumber)+": "+str);
                        //server.broadcastMsg(nameNumber+": "+str);
                        //server.broadcastMsg(str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server.unsubscribe(this);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {

        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
