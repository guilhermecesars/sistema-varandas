package SistemaRestaurante.Dominio;

public enum TamanhoMarmitas {
    PP(15),
    P(20),
    M(23),
    G(25),
    Marmitao(60);

    private double preco;

    TamanhoMarmitas(double preco) {
        this.preco = preco;
    }

    public double getPreco() {
        return preco;
    }
}