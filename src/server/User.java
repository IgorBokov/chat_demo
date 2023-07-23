package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class User {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String name;

    public User(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new DataOutputStream(socket.getOutputStream());
        this.in = new DataInputStream(socket.getInputStream());
    }

    public DataOutputStream getOut() {
        return out;
    }

    public DataInputStream getIn() {
        return in;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean reg() {
        try {
            this.getOut().writeUTF(" Введите ваше имя ");
            String name = this.getIn().readUTF();
            this.getOut().writeUTF(" Введите login : ");
            String login = this.getIn().readUTF();
            this.getOut().writeUTF(" Введите pass : ");
            String pass = this.getIn().readUTF();

            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/chat39",
                    "root",
                    "");
            Statement statement = connection.createStatement();
            statement.executeUpdate(
                    "INSERT INTO users (name,login,pass) VALUES ('" + name + "','" + login + "','" + pass + "')"
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    public boolean login(){
        try {
        this.getOut().writeUTF(" Введите login : ");
        String login = this.getIn().readUTF();
        this.getOut().writeUTF(" Введите pass : ");
        String pass = this.getIn().readUTF();
        Connection connection = null;

            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/chat39",
                    "root",
                    "");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM users WHERE login='" + login + "' AND pass='" + pass + "';");
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                this.setName(name);
                return true;
            } else {
                this.getOut().writeUTF("Неверный логин или пароль");
                return  false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

