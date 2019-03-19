package Rutas.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionMysql {

    private static  ConnectionMysql instance = new ConnectionMysql();
    private static Connection connection;
    private static final String user="user_rutas";
    private static final String password="estructura_datos";
    private static final String bd = "rutas";
    private static final String url="jdbc:mysql://localhost:3306/"+bd+"?useSSL=false&serverTimezone=UTC";


    private ConnectionMysql() { }


    public static ConnectionMysql getInstance(){
        return instance;
    }


    public Connection getConnection() {
        return connection;
    }

    public boolean openConnection(){
        connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("SUCCESS Connection established to mysql db");
            return true;

        } catch (SQLException ex) {
            System.out.println("ERROR Couldn't establish mysql connection error: " + ex);
            return false;
        }
    }


    public void closeConnection() {
        try{
            if (connection != null) {
                connection.close();
                System.out.println("SUCCESS Connection closed");
            }
            else{
                System.out.println("ERROR Connection is null");
            }

        }catch (SQLException e){
            System.out.println("ERROR Couldn't close connection error: "+ e);
        }

    }

}
