package SistemaRestaurante.DAO;


import SistemaRestaurante.Database.SQLiteConnection;
import SistemaRestaurante.Dominio.Cliente;

import java.sql.*;

public class ClienteDAO {
    public int salvarCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (nome, endereco) VALUES (?, ?)";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEndereco());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // ID do cliente
                }
            }
        }
        return -1;
    }
}

