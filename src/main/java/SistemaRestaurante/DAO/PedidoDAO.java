package SistemaRestaurante.DAO;

import SistemaRestaurante.Database.SQLiteConnection;
import SistemaRestaurante.Dominio.Pedido;
import SistemaRestaurante.Dominio.Produto;

import java.sql.*;
import java.util.List;

public class PedidoDAO {

    /**
     * Salva o pedido e seus itens no banco.
     * Retorna o ID do pedido gerado.
     */
    public int salvarPedido(Pedido pedido, int clienteId) throws SQLException {
        String sqlPedido = """
                INSERT INTO pedidos (cliente_id, forma_pagamento, observacao, total)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, clienteId);
            stmt.setString(2, pedido.getPagamento().toString());
            stmt.setString(3, pedido.getObservacao());
            stmt.setDouble(4, pedido.getTotal());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int pedidoId = rs.getInt(1);
                    salvarItens(pedidoId, pedido.getProdutos(), conn);
                    return pedidoId;
                }
            }
        }
        return -1;
    }

    private void salvarItens(int pedidoId, List<Produto> produtos, Connection conn) throws SQLException {
        String sql = """
                INSERT INTO itens_pedido (pedido_id, produto_nome, produto_descricao, preco_unitario)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Produto p : produtos) {
                stmt.setInt(1, pedidoId);
                stmt.setString(2, p.getNome());
                stmt.setString(3, p.getDescricao());
                stmt.setDouble(4, p.getPreco());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
}
