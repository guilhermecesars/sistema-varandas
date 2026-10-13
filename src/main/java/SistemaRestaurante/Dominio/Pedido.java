package SistemaRestaurante.Dominio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private static int contador = 0;
    private int id;
    private Cliente cliente;
    private List<Produto> produtos;
    private FormaDePagamento pagamento;
    private String observacao;
    private LocalDateTime dataHora;

    public Pedido(Cliente cliente) {
        this.id = ++contador;
        this.cliente = cliente;
        this.produtos = new ArrayList<>();
        this.dataHora = LocalDateTime.now();
    }

    public void adicionarProduto(Produto produto) {
        this.produtos.add(produto);
    }

    /**
     * Calcula o valor total somando o preço de todos os produtos.
     */
    public double getTotal() {
        double total = 0;
        for (Produto p : produtos) {
            total += p.getPreco();
        }
        return total;
    }

    public void imprimirComanda() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        System.out.println("--> DATA: " + dataHora.format(formatter));
        System.out.println(" - CLIENTE: " + cliente.getNome());
        System.out.println(" - ENDEREÇO: " + cliente.getEndereco());
        System.out.println(" - PEDIDOS: " + produtos.size() + " item(s)");
        for (Produto p : produtos) {
            System.out.println("   • " + p.getNome() + " - R$ " + String.format("%.2f", p.getPreco()));
        }
        System.out.println(" - VALOR TOTAL: R$ " + String.format("%.2f", getTotal()) + " reais");
        System.out.println(" - FORMA DE PAGAMENTO: " + pagamento);
        if (observacao != null && !observacao.isEmpty()) {
            System.out.println(" - OBSERVAÇÃO: " + observacao);
        }
    }

    // ========== GETTERS ==========
    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public FormaDePagamento getPagamento() {
        return pagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    // ========== SETTERS ==========
    public void setPagamento(FormaDePagamento pagamento) {
        this.pagamento = pagamento;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}



