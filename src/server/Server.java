package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Server {

    static ArrayList<User> users = new ArrayList<>();

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
                            while (true) {
                                user.getOut().writeUTF("Для авторизации введите /login \n" +
                                        "Для регистрации введите /reg \n");
                                String command = user.getIn().readUTF();
                                if (command.equals("/reg")) {
                                    user.reg();
                                    break;
                                } else if (command.equals("/login")) {
                                    if(user.login()) break;
                                } else {
                                    user.getOut().writeUTF("Введена не верная команда, повторите ввод");
                                }
                            }
                            user.getOut().writeUTF("Добро пожаловать " + user.getName());
                            broadCast(user.getName() + " подключился(лась)");
                            sendOnlineUsers();
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
                                sendOnlineUsers();
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
    public static void sendOnlineUsers() throws IOException {
        JSONArray jsonArray = new JSONArray();
        for(User user: users){
            jsonArray.add(user.getName());          // Создаем массив jsonArray с именами пользователей
        }
        JSONObject jsonObject = new JSONObject();   // Создаем JSON объект внутри которого лежит массив
        jsonObject.put("onlineUsers",jsonArray);   // добавляем поле в котором ключ (onlineUsers) / значение = jsonArray
        broadCast(jsonObject.toJSONString());
    }
}