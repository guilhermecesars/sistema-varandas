package SistemaRestaurante.JavaFX;

import SistemaRestaurante.DAO.ClienteDAO;
import SistemaRestaurante.DAO.PedidoDAO;
import SistemaRestaurante.Dominio.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela de criação de novo pedido - Versão remodelada
 * Cores inspiradas na identidade visual do Varandas Restaurante
 */
public class TelaNovoPedido {

    private BorderPane layout;
    private Pedido pedidoAtual;
    private Cliente clienteAtual;
    private List<Produto> cardapio = new ArrayList<>();
    private VBox boxItens;
    private Label lblTotal;
    private TextField txtNome;
    private TextField txtEndereco;
    private ToggleGroup grupoPagamento;
    private TextArea txtObservacao;

    // ====== CORES DO RESTAURANTE ======
    private static final String COR_FUNDO = "#1A0F0A";      // Marrom bem escuro
    private static final String COR_PAINEL = "#2C1810";    // Marrom médio
    private static final String COR_DOURADO = "#D4A843";   // Dourado principal
    private static final String COR_DOURADO_CLARO = "#F5E6C8"; // Bege claro
    private static final String COR_TEXTO = "#FFFFFF";     // Branco
    private static final String COR_TEXTO_ESCURO = "#2C1810"; // Marrom escuro para fundos claros
    private static final String COR_VERDE = "#2E7D32";     // Verde botão
    private static final String COR_VERMELHO = "#C62828";  // Vermelho remover
    private static final String COR_CINZA = "#616161";     // Cinza botão voltar

    public TelaNovoPedido(Stage palco) {
        inicializarCardapio();

        // ====== TOPO: Dados do Cliente ======
        Label lblTitulo = new Label("NOVO PEDIDO");
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        lblTitulo.setTextFill(Color.web(COR_DOURADO));

        txtNome = criarCampoTexto("Nome do cliente");
        txtEndereco = criarCampoTexto("Endereço (Rua e Número)");

        VBox topo = new VBox(10, lblTitulo, txtNome, txtEndereco);
        topo.setPadding(new Insets(15));
        topo.setAlignment(Pos.CENTER_LEFT);
        topo.setStyle("-fx-background-color: " + COR_PAINEL + ";");

        // ====== CENTRO: Cardápio ======
        Label lblCardapio = new Label("🍛 CARDÁPIO");
        lblCardapio.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblCardapio.setTextFill(Color.web(COR_DOURADO));

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(10));

        int col = 0, row = 0;
        for (Produto p : cardapio) {
            Button btn = criarBotaoCardapio(p);
            grid.add(btn, col++, row);
            if (col > 2) {
                col = 0;
                row++;
            }
        }

        VBox centro = new VBox(12, lblCardapio, grid);
        centro.setPadding(new Insets(15));
        centro.setStyle("-fx-background-color: " + COR_FUNDO + ";");

        // ====== DIREITA: Resumo do Pedido ======
        VBox direita = criarPainelResumo(palco);

        // ====== MONTA TUDO ======
        layout = new BorderPane();
        layout.setTop(topo);
        layout.setCenter(centro);
        layout.setRight(direita);
        layout.setStyle("-fx-background-color: " + COR_FUNDO + ";");
    }

    public BorderPane getLayout() {
        return layout;
    }

    // ====== MÉTODOS DE CRIAÇÃO DE COMPONENTES ======

    private TextField criarCampoTexto(String placeholder) {
        TextField campo = new TextField();
        campo.setPromptText(placeholder);
        campo.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-color: " + COR_DOURADO_CLARO + ";" +
                        "-fx-text-fill: " + COR_TEXTO_ESCURO + ";" +
                        "-fx-prompt-text-fill: #8B7355;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-padding: 10px;" +
                        "-fx-border-color: " + COR_DOURADO + ";" +
                        "-fx-border-radius: 8px;" +
                        "-fx-border-width: 1px;"
        );
        return campo;
    }

    private Button criarBotaoCardapio(Produto p) {
        Button btn = new Button(p.getNome() + "\nR$ " + String.format("%.2f", p.getPreco()));
        btn.setPrefSize(170, 80);
        btn.setWrapText(true);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setTextFill(Color.web(COR_TEXTO_ESCURO));
        btn.setStyle(
                "-fx-background-color: " + COR_DOURADO_CLARO + ";" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-color: " + COR_DOURADO + ";" +
                        "-fx-border-radius: 10px;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + COR_DOURADO + ";" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-color: " + COR_DOURADO + ";" +
                        "-fx-border-radius: 10px;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(212,168,67,0.5), 10, 0, 0, 0);"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + COR_DOURADO_CLARO + ";" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-color: " + COR_DOURADO + ";" +
                        "-fx-border-radius: 10px;" +
                        "-fx-border-width: 2px;" +
                        "-fx-cursor: hand;"
        ));
        btn.setOnAction(e -> adicionarProduto(p));
        return btn;
    }

    private VBox criarPainelResumo(Stage palco) {
        // Título
        Label lblResumo = new Label("🛒 RESUMO DO PEDIDO");
        lblResumo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblResumo.setTextFill(Color.web(COR_DOURADO));

        // Lista de itens
        boxItens = new VBox(6);
        boxItens.setPadding(new Insets(8));
        boxItens.setStyle("-fx-background-color: #3E2723; -fx-background-radius: 8px;");
        boxItens.setPrefHeight(180);

        ScrollPane scroll = new ScrollPane(boxItens);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(180);
        scroll.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Total
        lblTotal = new Label("TOTAL: R$ 0,00");
        lblTotal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        lblTotal.setTextFill(Color.web(COR_DOURADO));

        // Pagamento
        Label lblPagamento = new Label("💳 FORMA DE PAGAMENTO:");
        lblPagamento.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lblPagamento.setTextFill(Color.web("#A1887F"));

        grupoPagamento = new ToggleGroup();
        HBox pagBox = new HBox(8);
        pagBox.setAlignment(Pos.CENTER);

        for (FormaDePagamento fp : FormaDePagamento.values()) {
            ToggleButton tb = new ToggleButton(fp.name());
            tb.setToggleGroup(grupoPagamento);
            tb.setUserData(fp);
            tb.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
            tb.setPrefWidth(70);
            tb.setPrefHeight(35);

            // Estilo NORMAL (não selecionado)
            tb.setStyle(
                    "-fx-background-color: " + COR_DOURADO_CLARO + ";" +
                            "-fx-text-fill: " + COR_TEXTO_ESCURO + ";" +
                            "-fx-background-radius: 6px;" +
                            "-fx-border-color: " + COR_DOURADO + ";" +
                            "-fx-border-radius: 6px;" +
                            "-fx-border-width: 1px;"
            );

            // Listener para mudar cor quando selecionado
            tb.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    tb.setStyle(
                            "-fx-background-color: " + COR_VERDE + ";" +
                                    "-fx-text-fill: white;" +
                                    "-fx-background-radius: 6px;" +
                                    "-fx-border-color: " + COR_VERDE + ";" +
                                    "-fx-border-radius: 6px;" +
                                    "-fx-border-width: 2px;" +
                                    "-fx-font-weight: bold;"
                    );
                } else {
                    tb.setStyle(
                            "-fx-background-color: " + COR_DOURADO_CLARO + ";" +
                                    "-fx-text-fill: " + COR_TEXTO_ESCURO + ";" +
                                    "-fx-background-radius: 6px;" +
                                    "-fx-border-color: " + COR_DOURADO + ";" +
                                    "-fx-border-radius: 6px;" +
                                    "-fx-border-width: 1px;"
                    );
                }
            });

            pagBox.getChildren().add(tb);
        }

        // Observação - CORRIGIDO: texto escuro em fundo claro
        Label lblObs = new Label("📝 OBSERVAÇÃO:");
        lblObs.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lblObs.setTextFill(Color.web("#A1887F"));

        txtObservacao = new TextArea();
        txtObservacao.setPromptText("Ex: Tirar cebola, bem passado...");
        txtObservacao.setPrefRowCount(2);
        txtObservacao.setWrapText(true);
        // CORREÇÃO: fundo claro com texto escuro para ficar legível
        txtObservacao.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-background-color: " + COR_DOURADO_CLARO + ";" +
                        "-fx-text-fill: " + COR_TEXTO_ESCURO + ";" +
                        "-fx-prompt-text-fill: #8B7355;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-border-color: " + COR_DOURADO + ";" +
                        "-fx-border-radius: 6px;" +
                        "-fx-border-width: 1px;"
        );

        // Botões
        Button btnFinalizar = new Button("✅ FINALIZAR PEDIDO");
        btnFinalizar.setPrefWidth(300);
        btnFinalizar.setPrefHeight(50);
        btnFinalizar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        btnFinalizar.setTextFill(Color.WHITE);
        btnFinalizar.setStyle(
                "-fx-background-color: " + COR_VERDE + ";" +
                        "-fx-background-radius: 10px;" +
                        "-fx-cursor: hand;"
        );
        btnFinalizar.setOnAction(e -> finalizarPedido());

        Button btnVoltar = new Button("⬅ VOLTAR");
        btnVoltar.setPrefWidth(300);
        btnVoltar.setPrefHeight(40);
        btnVoltar.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnVoltar.setTextFill(Color.WHITE);
        btnVoltar.setStyle(
                "-fx-background-color: " + COR_CINZA + ";" +
                        "-fx-background-radius: 10px;" +
                        "-fx-cursor: hand;"
        );
        btnVoltar.setOnAction(e -> voltarTelaPrincipal(palco));

        VBox direita = new VBox(12, lblResumo, scroll, lblTotal,
                lblPagamento, pagBox, lblObs, txtObservacao,
                new Label(""), btnFinalizar, btnVoltar);
        direita.setPadding(new Insets(18));
        direita.setPrefWidth(340);
        direita.setStyle(
                "-fx-background-color: " + COR_PAINEL + ";" +
                        "-fx-background-radius: 12px;" +
                        "-fx-border-color: " + COR_DOURADO + ";" +
                        "-fx-border-radius: 12px;" +
                        "-fx-border-width: 1px;"
        );

        return direita;
    }

    // ====== MÉTODOS DE LÓGICA ======

    private void inicializarCardapio() {
        cardapio.add(new Marmita("Marmita PP", "1 carne 1 guarnição", TamanhoMarmitas.PP));
        cardapio.add(new Marmita("Marmita P", "Cardápio Completo", TamanhoMarmitas.P));
        cardapio.add(new Marmita("Marmita M", "Cardápio Completo", TamanhoMarmitas.M));
        cardapio.add(new Marmita("Marmita G", "Cardápio Completo", TamanhoMarmitas.G));
        cardapio.add(new Marmita("Marmitão", "Tudo separado para 2 pessoas", TamanhoMarmitas.Marmitao));
        cardapio.add(new PratoFeito("Tilápia", "Arroz, feijão, filé de tilápia, purê, vinagrete e farofa", 35));
        cardapio.add(new PratoFeito("Alcatra", "Arroz, feijão, alcatra acebolada, ovo, banana, fritas e farofa", 35));
        cardapio.add(new PratoFeito("Parmegiana Frango", "Arroz, feijão, parmegiana de frango e fritas", 35));
        cardapio.add(new PratoFeito("Parmegiana Carne", "Arroz, feijão, parmegiana de carne e fritas", 35));
        cardapio.add(new PratoFeito("Bife Milanesa", "Arroz, feijão, bife à milanesa, farofa, purê e ovo", 35));
        cardapio.add(new PratoFeito("Filé Frango", "Arroz, feijão, filé de frango à milanesa, farofa, banana e fritas", 35));
        cardapio.add(new PratoFeito("Kids", "Arroz, feijão, nuggets de frango e fritas", 25));
    }

    private void adicionarProduto(Produto p) {
        if (pedidoAtual == null) {
            String nome = txtNome.getText().trim();
            String endereco = txtEndereco.getText().trim();
            if (nome.isEmpty()) nome = "Cliente";
            if (endereco.isEmpty()) endereco = "Retirada no local";

            clienteAtual = new Cliente(nome, endereco);
            pedidoAtual = new Pedido(clienteAtual);
        }

        pedidoAtual.adicionarProduto(p);
        atualizarResumo();
    }

    private void atualizarResumo() {
        boxItens.getChildren().clear();

        if (pedidoAtual == null || pedidoAtual.getProdutos().isEmpty()) {
            lblTotal.setText("TOTAL: R$ 0,00");
            return;
        }

        for (Produto p : pedidoAtual.getProdutos()) {
            HBox linha = new HBox(10);
            linha.setAlignment(Pos.CENTER_LEFT);
            linha.setPadding(new Insets(4, 0, 4, 0));

            Label lblNome = new Label("• " + p.getNome());
            lblNome.setFont(Font.font("Segoe UI", 13));
            lblNome.setTextFill(Color.web(COR_TEXTO));
            lblNome.setPrefWidth(150);

            Label lblPreco = new Label("R$ " + String.format("%.2f", p.getPreco()));
            lblPreco.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            lblPreco.setTextFill(Color.web("#81C784"));

            Button btnRemover = new Button("✕");
            btnRemover.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            btnRemover.setTextFill(Color.web(COR_VERMELHO));
            btnRemover.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            btnRemover.setOnAction(e -> {
                pedidoAtual.getProdutos().remove(p);
                atualizarResumo();
            });

            linha.getChildren().addAll(lblNome, lblPreco, btnRemover);
            boxItens.getChildren().add(linha);
        }

        lblTotal.setText("TOTAL: R$ " + String.format("%.2f", pedidoAtual.getTotal()));
    }

    private void finalizarPedido() {
        if (pedidoAtual == null || pedidoAtual.getProdutos().isEmpty()) {
            mostrarErro("Adicione pelo menos um produto!");
            return;
        }

        ToggleButton selecionado = (ToggleButton) grupoPagamento.getSelectedToggle();
        if (selecionado == null) {
            mostrarErro("Selecione uma forma de pagamento!");
            return;
        }
        pedidoAtual.setPagamento((FormaDePagamento) selecionado.getUserData());

        String obs = txtObservacao.getText().trim();
        if (!obs.isEmpty()) {
            pedidoAtual.setObservacao(obs);
        }

        try {
            ClienteDAO clienteDAO = new ClienteDAO();
            PedidoDAO pedidoDAO = new PedidoDAO();

            int clienteId = clienteDAO.salvarCliente(clienteAtual);
            int pedidoId = pedidoDAO.salvarPedido(pedidoAtual, clienteId);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText(null);
            alert.setContentText("Pedido #" + pedidoId + " salvo com sucesso!\nTotal: R$ " + String.format("%.2f", pedidoAtual.getTotal()));
            alert.showAndWait();

            limparTela();

        } catch (SQLException ex) {
            mostrarErro("Erro ao salvar no banco: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void limparTela() {
        txtNome.clear();
        txtEndereco.clear();
        txtObservacao.clear();
        pedidoAtual = null;
        clienteAtual = null;
        grupoPagamento.selectToggle(null);
        boxItens.getChildren().clear();
        lblTotal.setText("TOTAL: R$ 0,00");
    }

    private void voltarTelaPrincipal(Stage palco) {
        TelaPrincipal tp = new TelaPrincipal(palco);
        Scene cena = new Scene(tp.getLayout(), 900, 700);
        palco.setScene(cena);
    }

    private void mostrarErro(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}