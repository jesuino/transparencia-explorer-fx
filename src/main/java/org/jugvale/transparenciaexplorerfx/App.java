package org.jugvale.transparenciaexplorerfx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

public class App extends Application{

	@Override
	public void start(Stage s){
		StackPane pnlRaiz = new StackPane();

		Scene cena = new Scene(pnlRaiz);
		s.setTitle("Explorando a API de Transparência");
		s.setScene(cena);
		s.show();
	}

}
