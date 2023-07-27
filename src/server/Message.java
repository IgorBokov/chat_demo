package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Message {
    private int fromUser;
    private int toUser;
    private String message;
    private int id;

    public Message(int fromUser, int toUser, String message) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.message = message;
    }

    public void saveMessage() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/chat39",
                    "root",
                    "");
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO `messages`(`message`, `from_id`, `to_id`)" +
                    " VALUES ('"+this.message+"','"+this.fromUser+"','"+this.toUser+"')");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
