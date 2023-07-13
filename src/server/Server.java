package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    static ArrayList<User> users = new ArrayList<>();

    public static void main(String[] args) {
        // local ip 127.0.0.1 port 9123
        try {
            ServerSocket serverSocket = new ServerSocket(9123);
            while (true) {
                Socket socket = serverSocket.accept(); // Ожидаем подключения клиента
                User user = new User(socket);
                users.add(user);
                System.out.println("Клиент подключился");
                DataInputStream is = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            user.getOut().writeUTF("Как тебя зовут?");
                            String name = user.getIn().readUTF();
                            user.setName(name);
                            user.getOut().writeUTF("Добро пожаловать " + user.getName());
                            broadCast(user.getName() + " подключился(лась)");
                            while (true) {
                                String msg = user.getIn().readUTF();
                                System.out.println(user.getName() + " :  " + msg);
                                broadCast(user.getName() + "  " + msg);  // рассылка всем пользователям
                            }
                        } catch (IOException e) {
                            System.out.println("Клиент " + user.getName() + "  <- отключился");
                            users.remove(user);
                            try {
                                broadCast("Клиент " + user.getName() + " <-- отключился");
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                });
                thread.start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void broadCast(String msg) throws IOException {
        for (User user : users) {
            user.getOut().writeUTF(user.getName() + " -> " + msg);
        }
    }
}
