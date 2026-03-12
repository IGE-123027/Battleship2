package battleship;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScoreboardManager {
    private static final String FILE_PATH = "scoreboard.csv";

    /**
     * Guarda os resultados da partida no final do jogo.
     */
    public static void saveScore(IGame game) {
        // Se não sobrarem navios inimigos, é porque ganhámos!
        boolean isWin = (game.getRemainingShips() == 0);
        String resultado = isWin ? "Vitória" : "Incompleto/Derrota";

        // Formatar a data e hora atual
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        // Extrair os dados da partida usando os métodos da interface IGame
        String tirosCerteiros = String.valueOf(game.getHits());
        String naviosAfundados = String.valueOf(game.getSunkShips());
        String jogadasFeitas = String.valueOf(game.getMyMoves().size());

        File file = new File(FILE_PATH);
        boolean isNewFile = !file.exists(); // Verifica se é preciso criar o cabeçalho

        // O 'true' no FileWriter significa "Append" (adicionar ao fim do ficheiro sem apagar o que lá está)
        try (CSVWriter writer = new CSVWriter(new FileWriter(file, true))) {
            if (isNewFile) {
                // Escreve o cabeçalho das colunas se o ficheiro for novo
                writer.writeNext(new String[]{"Data", "Resultado", "Tiros Certeiros", "Navios Afundados", "Total Jogadas"});
            }
            // Escreve os dados deste jogo
            writer.writeNext(new String[]{data, resultado, tirosCerteiros, naviosAfundados, jogadasFeitas});

        } catch (Exception e) {
            System.out.println("Erro ao guardar o scoreboard: " + e.getMessage());
        }
    }

    /**
     * Lê o ficheiro CSV e imprime a tabela na consola.
     */
    public static void printScoreboard() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("\nAinda não existem jogos registados no histórico.\n");
            return;
        }

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            System.out.println("\n====================== HISTÓRICO DE JOGOS ======================");
            String[] linha;
            while ((linha = reader.readNext()) != null) {
                // Formatação em colunas alinhadas
                System.out.printf("%-18s | %-18s | Tiros: %-5s | Afundados: %-5s | Jogadas: %-5s%n",
                        linha[0], linha[1], linha[2], linha[3], linha[4]);
            }
            System.out.println("================================================================\n");
        } catch (Exception e) {
            System.out.println("Erro ao ler o ficheiro do Scoreboard.");
        }
    }
}
