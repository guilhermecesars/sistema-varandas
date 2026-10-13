package SistemaRestaurante.JavaFX;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class TelaPrincipal {

    private VBox layout;

    public TelaPrincipal(Stage palco) {
        Label lblLogo = new Label("VARANDAS");
        lblLogo.setStyle("-fx-font-size: 52px; -fx-font-weight: bold; -fx-text-fill: #D4A843;");

        Label lblSub = new Label("Sistema de Pedidos");
        lblSub.setStyle("-fx-font-size: 22px; -fx-text-fill: #C0C0C0;");

        Button btnNovo = new Button("NOVO PEDIDO");
        btnNovo.setStyle("-fx-font-size: 18px; -fx-background-color: #2E7D32; -fx-text-fill: white;");
        btnNovo.setPrefWidth(300);
        btnNovo.setPrefHeight(60);
        btnNovo.setOnAction(e -> abrirPedido(palco));

        Button btnRelatorio = new Button("📊 RELATÓRIO DO DIA");
        btnRelatorio.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        btnRelatorio.setStyle(
                "-fx-background-color: #D4A017;" +
                        "-fx-text-fill: #3E2723;" +
                        "-fx-padding: 12 30;" +
                        "-fx-background-radius: 8;"
        );
        btnRelatorio.setPrefWidth(300);
        btnRelatorio.setPrefHeight(60);
        btnRelatorio.setOnAction(e -> {
            TelaRelatorio relatorio = new TelaRelatorio(palco);
            Scene cena = new Scene(relatorio.getLayout(), 900, 700);
            palco.setScene(cena);
        });

        Button btnSair = new Button("SAIR");
        btnSair.setStyle("-fx-font-size: 18px; -fx-background-color: #C62828; -fx-text-fill: white;");
        btnSair.setPrefWidth(300);
        btnSair.setPrefHeight(60);
        btnSair.setOnAction(e -> System.exit(0));

        layout = new VBox(30);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(50));
        layout.setStyle("-fx-background-color: #2C1810;");
        layout.getChildren().addAll(lblLogo, lblSub, btnNovo, btnRelatorio, btnSair);  // <-- ADICIONADO AQUI
    }

    public VBox getLayout() {
        return layout;
    }

    private void abrirPedido(Stage palco) {
        TelaNovoPedido tela = new TelaNovoPedido(palco);
        Scene cena = new Scene(tela.getLayout(), 900, 700);
        palco.setScene(cena);
    }
}