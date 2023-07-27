package server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    private int id;
    private JSONParser jsonParser = new JSONParser();
    private JSONObject jsonObject;

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

    public int getId() {
        return id;
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

    public boolean login() {
        try {
            this.getOut().writeUTF(" Введите login : ");
            String login = this.getIn().readUTF();
            jsonObject = (JSONObject) jsonParser.parse(login);
            login = (String) jsonObject.get("msg");
            this.getOut().writeUTF(" Введите pass : ");
            String pass = this.getIn().readUTF();
            pass = (String) jsonObject.get("msg");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/chat39",
                    "root",
                    "");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM users WHERE login='" + login + "' AND pass='" + pass + "';");
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                this.id = resultSet.getInt("id");
                this.setName(name);
                return true;
            } else {
                this.getOut().writeUTF("Неверный логин или пароль");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

