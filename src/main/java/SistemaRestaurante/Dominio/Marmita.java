package SistemaRestaurante.Dominio;

public class Marmita extends Produto {
    private TamanhoMarmitas tamanho;

    public Marmita(String nome, String descricao, TamanhoMarmitas tamanho) {
        super(nome, descricao, tamanho.getPreco());
        this.tamanho = tamanho;
    }

    public TamanhoMarmitas getTamanho() {
        return tamanho;
    }
}
