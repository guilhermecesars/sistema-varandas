package SistemaRestaurante.JavaFX;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Tela de Relatório do Dia
 * Mostra todos os pedidos do dia atual, separados por forma de pagamento,
 * com total por tipo e total geral.
 */
public class TelaRelatorio {

    private final Stage palco;
    private final VBox layout;
    private final DatePicker datePicker;
    private final VBox listaPedidos;
    private final Label lblTotalDebito;
    private final Label lblTotalCredito;
    private final Label lblTotalPix;
    private final Label lblTotalDinheiro;
    private final Label lblTotalGeral;
    private final Label lblQtdPedidos;

    // Cores do tema (ajuste se necessário)
    private static final String COR_FUNDO = "#3E2723";
    private static final String COR_PRIMARIA = "#D4A017";
    private static final String COR_TEXTO = "#FFF8E1";
    private static final String COR_CARD = "#5D4037";
    private static final String COR_BOTAO_VOLTAR = "#616161";

    public TelaRelatorio(Stage palco) {
        this.palco = palco;
        this.layout = new VBox(15);
        this.layout.setPadding(new Insets(20));
        this.layout.setStyle("-fx-background-color: " + COR_FUNDO + ";");

        // Título
        Label titulo = new Label("📊 RELATÓRIO DO DIA");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        titulo.setTextFill(Color.web(COR_PRIMARIA));

        // Seletor de data
        HBox dataBox = new HBox(10);
        dataBox.setAlignment(Pos.CENTER_LEFT);
        Label lblData = new Label("Data:");
        lblData.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblData.setTextFill(Color.web(COR_TEXTO));

        datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle("-fx-font-size: 14px;");
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> carregarDados());

        Button btnHoje = new Button("Hoje");
        btnHoje.setStyle(
                "-fx-background-color: " + COR_PRIMARIA + ";" +
                        "-fx-text-fill: " + COR_FUNDO + ";" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 5 15;"
        );
        btnHoje.setOnAction(e -> {
            datePicker.setValue(LocalDate.now());
            carregarDados();
        });

        dataBox.getChildren().addAll(lblData, datePicker, btnHoje);

        // Cards de totais por pagamento
        HBox cardsBox = new HBox(10);
        cardsBox.setAlignment(Pos.CENTER);

        lblTotalDebito = criarLabelValor("R$ 0,00");
        lblTotalCredito = criarLabelValor("R$ 0,00");
        lblTotalPix = criarLabelValor("R$ 0,00");
        lblTotalDinheiro = criarLabelValor("R$ 0,00");

        cardsBox.getChildren().addAll(
                criarCard("💳 DÉBITO", lblTotalDebito, "#EF5350"),
                criarCard("💳 CRÉDITO", lblTotalCredito, "#AB47BC"),
                criarCard("📱 PIX", lblTotalPix, "#26A69A"),
                criarCard("💵 DINHEIRO", lblTotalDinheiro, "#66BB6A")
        );

        // Total geral e quantidade
        HBox totalBox = new HBox(30);
        totalBox.setAlignment(Pos.CENTER);
        totalBox.setPadding(new Insets(10));
        totalBox.setStyle(
                "-fx-background-color: " + COR_PRIMARIA + ";" +
                        "-fx-background-radius: 10;"
        );

        lblQtdPedidos = new Label("0 pedidos");
        lblQtdPedidos.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblQtdPedidos.setTextFill(Color.web(COR_FUNDO));

        lblTotalGeral = new Label("TOTAL: R$ 0,00");
        lblTotalGeral.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblTotalGeral.setTextFill(Color.web(COR_FUNDO));

        totalBox.getChildren().addAll(lblQtdPedidos, lblTotalGeral);

        // Lista de pedidos
        Label lblLista = new Label("📋 PEDIDOS DO DIA");
        lblLista.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblLista.setTextFill(Color.web(COR_PRIMARIA));

        listaPedidos = new VBox(8);
        listaPedidos.setPadding(new Insets(10));

        ScrollPane scroll = new ScrollPane(listaPedidos);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + COR_FUNDO + "; -fx-background-color: transparent;");
        scroll.setPrefHeight(400);

        // Botão voltar
        Button btnVoltar = new Button("← VOLTAR");
        btnVoltar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnVoltar.setStyle(
                "-fx-background-color: " + COR_BOTAO_VOLTAR + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10 30;" +
                        "-fx-background-radius: 5;"
        );
        btnVoltar.setOnAction(e -> voltar());

        // Monta layout
        layout.getChildren().addAll(
                titulo,
                dataBox,
                cardsBox,
                totalBox,
                lblLista,
                scroll,
                btnVoltar
        );
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Carrega dados iniciais
        carregarDados();
    }

    public VBox getLayout() {
        return layout;
    }

    private Label criarLabelValor(String texto) {
        Label lbl = new Label(texto);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lbl.setTextFill(Color.web(COR_TEXTO));
        return lbl;
    }

    private VBox criarCard(String titulo, Label valor, String corBorda) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefWidth(160);
        card.setStyle(
                "-fx-background-color: " + COR_CARD + ";" +
                        "-fx-border-color: " + corBorda + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );

        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lblTitulo.setTextFill(Color.web(corBorda));

        card.getChildren().addAll(lblTitulo, valor);
        return card;
    }

    private void carregarDados() {
        listaPedidos.getChildren().clear();

        double totalDebito = 0, totalCredito = 0, totalPix = 0, totalDinheiro = 0;
        int qtdPedidos = 0;

        String dataSelecionada = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String sql = """
                SELECT p.id, p.data_hora, c.nome, c.endereco,
                       p.forma_pagamento, p.total, p.observacao, p.status
                FROM pedidos p
                JOIN clientes c ON p.cliente_id = c.id
                WHERE DATE(p.data_hora) = ?
                ORDER BY p.data_hora DESC
                """;

        try (Connection conn = SistemaRestaurante.Database.SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dataSelecionada);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                qtdPedidos++;
                int id = rs.getInt("id");
                String dataHora = rs.getString("data_hora");
                String nome = rs.getString("nome");
                String endereco = rs.getString("endereco");
                String pagamento = rs.getString("forma_pagamento");
                double total = rs.getDouble("total");
                String obs = rs.getString("observacao");
                String status = rs.getString("status");

                // Soma por pagamento
                if (pagamento != null) {
                    switch (pagamento.toUpperCase()) {
                        case "DEBITO", "DÉBITO" -> totalDebito += total;
                        case "CREDITO", "CRÉDITO" -> totalCredito += total;
                        case "PIX" -> totalPix += total;
                        case "DINHEIRO" -> totalDinheiro += total;
                    }
                }

                // Adiciona item na lista
                listaPedidos.getChildren().add(criarItemPedido(
                        id, dataHora, nome, endereco, pagamento, total, obs, status
                ));
            }

        } catch (SQLException e) {
            System.err.println("[RELATÓRIO] Erro ao carregar dados: " + e.getMessage());
            Label erro = new Label("Erro ao carregar pedidos: " + e.getMessage());
            erro.setTextFill(Color.web("#EF5350"));
            listaPedidos.getChildren().add(erro);
        }

        // Atualiza cards
        lblTotalDebito.setText(formatarMoeda(totalDebito));
        lblTotalCredito.setText(formatarMoeda(totalCredito));
        lblTotalPix.setText(formatarMoeda(totalPix));
        lblTotalDinheiro.setText(formatarMoeda(totalDinheiro));

        double totalGeral = totalDebito + totalCredito + totalPix + totalDinheiro;
        lblTotalGeral.setText("TOTAL: " + formatarMoeda(totalGeral));
        lblQtdPedidos.setText(qtdPedidos + " pedido" + (qtdPedidos != 1 ? "s" : ""));

        // Mensagem se não houver pedidos
        if (qtdPedidos == 0) {
            Label vazio = new Label("Nenhum pedido encontrado para esta data.");
            vazio.setFont(Font.font("Segoe UI", 14));
            vazio.setTextFill(Color.web("#9E9E9E"));
            vazio.setPadding(new Insets(20));
            listaPedidos.getChildren().add(vazio);
        }
    }

    private HBox criarItemPedido(int id, String dataHora, String nome, String endereco,
                                 String pagamento, double total, String obs, String status) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12));
        item.setStyle(
                "-fx-background-color: " + COR_CARD + ";" +
                        "-fx-background-radius: 8;"
        );

        // Cor do pagamento
        String corPagamento = switch (pagamento != null ? pagamento.toUpperCase() : "") {
            case "DEBITO", "DÉBITO" -> "#EF5350";
            case "CREDITO", "CRÉDITO" -> "#AB47BC";
            case "PIX" -> "#26A69A";
            case "DINHEIRO" -> "#66BB6A";
            default -> "#9E9E9E";
        };

        // Info principal
        VBox info = new VBox(3);
        Label lblId = new Label("Pedido #" + id);
        lblId.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblId.setTextFill(Color.web(COR_PRIMARIA));

        Label lblCliente = new Label("👤 " + nome + "  |  📍 " + endereco);
        lblCliente.setFont(Font.font("Segoe UI", 12));
        lblCliente.setTextFill(Color.web(COR_TEXTO));

        Label lblData = new Label("🕐 " + dataHora);
        lblData.setFont(Font.font("Segoe UI", 11));
        lblData.setTextFill(Color.web("#BDBDBD"));

        if (obs != null && !obs.isBlank()) {
            Label lblObs = new Label("📝 " + obs);
            lblObs.setFont(Font.font("Segoe UI", 11));
            lblObs.setTextFill(Color.web("#BDBDBD"));
            info.getChildren().addAll(lblId, lblCliente, lblData, lblObs);
        } else {
            info.getChildren().addAll(lblId, lblCliente, lblData);
        }

        HBox.setHgrow(info, Priority.ALWAYS);

        // Pagamento e valor
        VBox valores = new VBox(5);
        valores.setAlignment(Pos.CENTER_RIGHT);

        Label lblPagamento = new Label(pagamento != null ? pagamento.toUpperCase() : "N/A");
        lblPagamento.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lblPagamento.setTextFill(Color.web(corPagamento));
        lblPagamento.setStyle("-fx-padding: 2 8; -fx-background-radius: 4;");

        Label lblValor = new Label(formatarMoeda(total));
        lblValor.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblValor.setTextFill(Color.web(COR_TEXTO));

        Label lblStatus = new Label(status != null ? status.toUpperCase() : "PENDENTE");
        lblStatus.setFont(Font.font("Segoe UI", 10));
        lblStatus.setTextFill(Color.web("#BDBDBD"));

        valores.getChildren().addAll(lblPagamento, lblValor, lblStatus);

        item.getChildren().addAll(info, valores);
        return item;
    }

    private String formatarMoeda(double valor) {
        return String.format("R$ %,.2f", valor).replace(",", "X").replace(".", ",").replace("X", ".");
    }

    private void voltar() {
        // Volta para a tela principal
        TelaPrincipal tela = new TelaPrincipal(palco);
        Scene cena = new Scene(tela.getLayout(), 900, 700);
        palco.setScene(cena);
    }
}