package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 9123);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());
            Scanner scanner = new Scanner(System.in);

            Thread thread = new Thread(new Runnable() {
                String response;

                @Override
                public void run() {
                    try {
                        while (true) {
                            response = is.readUTF(); // Ждём сообщение от сервера
                            System.out.println(response); // Выводим это сообщение на консоль
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.start();
            String msg;
            while (true) {
                msg = scanner.nextLine(); // Ждём сообщение с клавиатуры
                out.writeUTF(msg); // Отправляем сообщение на сервер
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
