package Rutas;

import Rutas.database.ConnectionMysql;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("View/rutas.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Rutas");
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void init() throws Exception {
        super.init();
        if(!ConnectionMysql.getInstance().openConnection()){
            Platform.exit();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        ConnectionMysql.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
