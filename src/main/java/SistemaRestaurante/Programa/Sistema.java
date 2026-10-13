package SistemaRestaurante.Programa;

import SistemaRestaurante.Database.SQLiteConnection;
import SistemaRestaurante.DAO.ClienteDAO;
import SistemaRestaurante.DAO.PedidoDAO;
import SistemaRestaurante.Dominio.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Sistema {

    // ========== CORES ANSI ==========
    public static final String RESET = "[0m";
    public static final String VERDE = "[32m";
    public static final String AZUL = "[34m";
    public static final String AMARELO = "[33m";
    public static final String VERMELHO = "[31m";
    public static final String CIANO = "[36m";
    public static final String NEGRITO = "[1m";

    // ========== MÉTODO DE VALIDAÇÃO ==========
    public static int lerNumeroValido(Scanner scanner, int min, int max) {
        int numero = 0;
        boolean isValido = false;

        while (!isValido) {
            try {
                numero = scanner.nextInt();
                scanner.nextLine();
                if (numero >= min && numero <= max) {
                    isValido = true;
                } else {
                    System.out.println(VERMELHO + "Digite um número entre " + min + " e " + max + ":" + RESET);
                }
            } catch (InputMismatchException e) {
                System.out.println(VERMELHO + "Entrada inválida! Digite um número!" + RESET);
                scanner.nextLine();
            }
        }
        return numero;
    }

    // ========== MAIN ==========
    public static void main(String[] args) {
        // Configura UTF-8 no Windows
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            try {
                new ProcessBuilder("cmd", "/c", "chcp", "65001").inheritIO().start().waitFor();
            } catch (Exception e) {
                // ignora
            }
        }

        // ====== INICIALIZA O BANCO DE DADOS ======
        SQLiteConnection.inicializarTabelas();

        // ====== LOGO E CABEÇALHO ======
        System.out.println(AMARELO + "   V   V   AAA   RRRR   AAA   N   N  DDDD   AAA   SSSSS" + RESET);
        System.out.println(AMARELO + "   V   V  A   A  R   R A   A  NN  N  D   D A   A  S" + RESET);
        System.out.println(AMARELO + "   V   V  AAAAA  RRRR  AAAAA  N N N  D   D AAAAA  SSSSS" + RESET);
        System.out.println(AMARELO + "    V V   A   A  R R   A   A  N  NN  D   D A   A      S" + RESET);
        System.out.println(AMARELO + "     V    A   A  R  RR A   A  N   N  DDDD  A   A  SSSSS" + RESET);
        System.out.println(AZUL + "========================================" + RESET);
        System.out.println(NEGRITO + CIANO + "      BEM-VINDO AO VARANDAS" + RESET);
        System.out.println(AZUL + "========================================" + RESET);
        System.out.println();

        // ====== CARDÁPIO ======
        Marmita marmitaPP = new Marmita("Marmita PP", "1 carne 1 guarnição", TamanhoMarmitas.PP);
        Marmita marmitaP = new Marmita("Marmita P", "Cardápio Completo", TamanhoMarmitas.P);
        Marmita marmitaM = new Marmita("Marmita M", "Cardápio Completo", TamanhoMarmitas.M);
        Marmita marmitaG = new Marmita("Marmita G", "Cardápio Completo", TamanhoMarmitas.G);
        Marmita marmitaMtao = new Marmita("Marmita Marmitão", "Tudo separado para 2 pessoas", TamanhoMarmitas.Marmitao);

        PratoFeito tilapia = new PratoFeito("Tilapia", "Arroz, feijão, filé de tilapia, purê de batata, vinagrete e farofa", 35);
        PratoFeito alcatra = new PratoFeito("Alcatra", "Arroz, feijão, alcatra acebolada, ovo frito, banana, fritas e farofa", 35);
        PratoFeito parmegianaDeFrango = new PratoFeito("Parmegiana de Frango", "Arroz, feijão, parmegiana de frango e fritas", 35);
        PratoFeito bifeMilanesa = new PratoFeito("Bife a milanesa", "Arroz, feijão, bife a milanesa, farofa, purê de batata e ovo frito", 35);
        PratoFeito fileDeFrango = new PratoFeito("Filé de frango", "Arroz, feijão, filé de frango a milanesa, farofa, banana e fritas", 35);
        PratoFeito parmegianaDeCarne = new PratoFeito("Parmegiana de Carne", "Arroz, feijão, parmegiana de carne e fritas", 35);
        PratoFeito kids = new PratoFeito("Kids", "Arroz, feijão, nuggets de frango e fritas", 25);

        ArrayList<Produto> cardapio = new ArrayList<>();
        cardapio.add(marmitaPP);
        cardapio.add(marmitaP);
        cardapio.add(marmitaM);
        cardapio.add(marmitaG);
        cardapio.add(marmitaMtao);
        cardapio.add(tilapia);
        cardapio.add(alcatra);
        cardapio.add(parmegianaDeFrango);
        cardapio.add(parmegianaDeCarne);
        cardapio.add(bifeMilanesa);
        cardapio.add(fileDeFrango);
        cardapio.add(kids);

        // ====== SCANNER E DAOs ======
        Scanner scanner = new Scanner(System.in);
        ClienteDAO clienteDAO = new ClienteDAO();
        PedidoDAO pedidoDAO = new PedidoDAO();

        // ====== LOOP PRINCIPAL ======
        int opcao = 0;
        while (opcao != 2) {
            System.out.println(VERDE + "1" + RESET + " - Novo pedido");
            System.out.println(VERDE + "2" + RESET + " - Sair");
            opcao = lerNumeroValido(scanner, 1, 2);

            if (opcao == 1) {
                System.out.println(CIANO + "=== NOVO PEDIDO ===" + RESET);

                System.out.print("Digite o nome do cliente: ");
                String nome = scanner.nextLine();

                System.out.print("Digite o endereço (RUA e NÚMERO): ");
                String endereco = scanner.nextLine();

                Cliente cliente = new Cliente(nome, endereco);
                Pedido pedido = new Pedido(cliente);

                // === EXIBE CARDÁPIO ===
                System.out.println(AZUL + "=== OPÇÕES DO CARDÁPIO ===" + RESET);
                for (int i = 0; i < cardapio.size(); i++) {
                    System.out.println(VERDE + (i + 1) + RESET + " - " + cardapio.get(i).getNome() + " (R$ " + cardapio.get(i).getPreco() + ")");
                }

                // === ESCOLHA DOS PRODUTOS ===
                int continuar = 1;
                while (continuar == 1) {
                    System.out.print("Escolha uma opção de 1 a " + cardapio.size() + ": ");
                    int escolha = lerNumeroValido(scanner, 1, cardapio.size());
                    Produto produtoEscolhido = cardapio.get(escolha - 1);
                    pedido.adicionarProduto(produtoEscolhido);
                    System.out.println(VERDE + "✔ Produto adicionado!" + RESET);

                    System.out.print("Continuar escolhendo? " + VERDE + "1" + RESET + " - Sim, " + VERDE + "2" + RESET + " - Não: ");
                    continuar = lerNumeroValido(scanner, 1, 2);
                }

                // === FORMA DE PAGAMENTO ===
                System.out.println(AZUL + "=== FORMA DE PAGAMENTO ===" + RESET);
                System.out.println(VERDE + "1" + RESET + " - DÉBITO");
                System.out.println(VERDE + "2" + RESET + " - CRÉDITO");
                System.out.println(VERDE + "3" + RESET + " - PIX");
                System.out.println(VERDE + "4" + RESET + " - DINHEIRO");
                int pagar = lerNumeroValido(scanner, 1, 4);
                switch (pagar) {
                    case 1 -> pedido.setPagamento(FormaDePagamento.DEBITO);
                    case 2 -> pedido.setPagamento(FormaDePagamento.CREDITO);
                    case 3 -> pedido.setPagamento(FormaDePagamento.PIX);
                    case 4 -> pedido.setPagamento(FormaDePagamento.DINHEIRO);
                }

                // === OBSERVAÇÕES ===
                System.out.print("Tem alguma observação? " + VERDE + "1" + RESET + " - Sim, " + VERDE + "2" + RESET + " - Não: ");
                int resposta = lerNumeroValido(scanner, 1, 2);
                StringBuilder observacoes = new StringBuilder();

                while (resposta == 1) {
                    System.out.print("Digite a observação: ");
                    String observacao = scanner.nextLine();
                    if (observacoes.length() > 0) {
                        observacoes.append(" | ");
                    }
                    observacoes.append(observacao);
                    System.out.print("Tem mais alguma observação? " + VERDE + "1" + RESET + " - Sim, " + VERDE + "2" + RESET + " - Não: ");
                    resposta = lerNumeroValido(scanner, 1, 2);
                }
                if (observacoes.length() > 0) {
                    pedido.setObservacao(observacoes.toString());
                }

                // === SALVAR NO BANCO SQLITE ===
                try {
                    int clienteId = clienteDAO.salvarCliente(cliente);
                    int pedidoId = pedidoDAO.salvarPedido(pedido, clienteId);
                    System.out.println(VERDE + "✅ Pedido #" + pedidoId + " salvo no banco com sucesso!" + RESET);
                } catch (SQLException e) {
                    System.out.println(VERMELHO + "❌ Erro ao salvar no banco: " + e.getMessage() + RESET);
                    e.printStackTrace();
                }

                // === IMPRIMIR COMANDA NA TELA ===
                System.out.println(AZUL + "=== IMPRIMINDO COMANDA ===" + RESET);
                pedido.imprimirComanda();
                System.out.println(AZUL + "===========================" + RESET + " ");
            }
        }

        System.out.println(AMARELO + "🚪 Sistema encerrado. Volte sempre!" + RESET);
        scanner.close();
    }
}
