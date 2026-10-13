package SistemaRestaurante.Database;

import java.sql.*;
import java.io.File;

public class SQLiteConnection {

    private static final String DB_PATH;
    private static final String URL;

    static {
        // SEMPRE usa AppData\Local\VarandasRestaurante\db\ — nunca Program Files
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData == null || localAppData.isBlank()) {
            localAppData = System.getProperty("user.home") + "\\AppData\\Local";
        }

        DB_PATH = localAppData + File.separator + "VarandasRestaurante" + File.separator + "db" + File.separator + "varandas.db";
        URL = "jdbc:sqlite:" + DB_PATH;

        // Cria a pasta
        File dir = new File(localAppData + File.separator + "VarandasRestaurante" + File.separator + "db");
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("[BANCO] Pasta criada: " + dir.getAbsolutePath());
        }

        // Driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("[BANCO] Driver não encontrado!");
        }

        System.out.println("[BANCO] Caminho do banco: " + DB_PATH);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void inicializarTabelas() {
        String sqlClientes = """
                CREATE TABLE IF NOT EXISTS clientes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome TEXT NOT NULL,
                    endereco TEXT NOT NULL,
                    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                """;

        String sqlPedidos = """
                CREATE TABLE IF NOT EXISTS pedidos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cliente_id INTEGER NOT NULL,
                    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    forma_pagamento TEXT,
                    observacao TEXT,
                    total REAL,
                    status TEXT DEFAULT 'pendente',
                    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
                );
                """;

        String sqlItens = """
                CREATE TABLE IF NOT EXISTS itens_pedido (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    pedido_id INTEGER NOT NULL,
                    produto_nome TEXT NOT NULL,
                    produto_descricao TEXT,
                    preco_unitario REAL NOT NULL,
                    FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
                );
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlClientes);
            stmt.execute(sqlPedidos);
            stmt.execute(sqlItens);
            System.out.println("[BANCO] Tabelas OK.");
        } catch (SQLException e) {
            System.err.println("[BANCO] Erro: " + e.getMessage());
        }
    }
}