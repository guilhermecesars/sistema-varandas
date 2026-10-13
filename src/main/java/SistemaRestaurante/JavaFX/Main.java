package SistemaRestaurante.JavaFX;

import SistemaRestaurante.Database.SQLiteConnection;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage palco) {
        SQLiteConnection.inicializarTabelas();
        TelaPrincipal tela = new TelaPrincipal(palco);
        Scene cena = new Scene(tela.getLayout(), 900, 700);

        palco.setTitle("Varandas Restaurante - Sistema de Pedidos");
        palco.setScene(cena);
        palco.setResizable(false);
        palco.centerOnScreen();
        palco.show();
    }

    // Opcional: mantém só para rodar pelo IntelliJ durante desenvolvimento
    public static void main(String[] args) {
        launch(args);
    }
}
