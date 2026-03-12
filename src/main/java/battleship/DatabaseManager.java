package battleship;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:battleship.db";

    public static void setupDatabase() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS moves (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "posicao TEXT NOT NULL," +
                    "resultado TEXT NOT NULL," +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ");";
            stmt.execute(sql);
            System.out.println("[BD] Tabela de jogadas verificada/criada.");
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao configurar: " + e.getMessage());
        }
    }

    public static void saveMove(String posicao, String resultado) {
        String sql = "INSERT INTO moves(posicao, resultado) VALUES(?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, posicao);
            pstmt.setString(2, resultado);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[BD] Erro ao guardar jogada: " + e.getMessage());
        }
    }
}