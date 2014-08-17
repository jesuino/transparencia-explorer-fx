package org.jugvale.transparenciaexplorerfx;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    final String FXML_INICIAL = "/fxml/app.fxml";
    final String CSS_INICIAL = "/css/app.css";

    public static void main(String args[]) {
        launch();
    }

    @Override
    public void start(Stage s) {
        try {
            URL urlFxml = getClass().getResource(FXML_INICIAL);
            Parent pnlRaiz = FXMLLoader.load(urlFxml);
            Scene cena = new Scene(pnlRaiz);
            cena.getStylesheets().add(CSS_INICIAL);
            s.setTitle("Explorando a API de Transparência");
            s.setScene(cena);
            s.show();
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

}
