package battleship;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreboardManagerTest {

    private static final File SCOREBOARD_FILE = new File("scoreboard.csv");

    @BeforeEach
    void setUp() {
        if (SCOREBOARD_FILE.exists()) {
            SCOREBOARD_FILE.delete();
        }
    }

    @AfterEach
    void tearDown() {
        if (SCOREBOARD_FILE.exists()) {
            SCOREBOARD_FILE.delete();
        }
    }

    @Test
    @DisplayName("saveScore deve criar o ficheiro scoreboard.csv")
    void saveScoreShouldCreateFile() {
        Game game = new Game(new Fleet());

        ScoreboardManager.saveScore(game);

        assertTrue(SCOREBOARD_FILE.exists());
    }

    @Test
    @DisplayName("saveScore deve escrever cabeçalho quando o ficheiro é novo")
    void saveScoreShouldWriteHeaderWhenFileIsNew() {
        Game game = new Game(new Fleet());

        ScoreboardManager.saveScore(game);

        assertTrue(SCOREBOARD_FILE.exists());
        assertTrue(SCOREBOARD_FILE.length() > 0);
    }

    @Test
    @DisplayName("saveScore deve adicionar nova linha quando o ficheiro já existe")
    void saveScoreShouldAppendWhenFileAlreadyExists() {
        Game game = new Game(new Fleet());

        ScoreboardManager.saveScore(game);
        long firstLength = SCOREBOARD_FILE.length();

        ScoreboardManager.saveScore(game);
        long secondLength = SCOREBOARD_FILE.length();

        assertTrue(secondLength > firstLength);
    }

    @Test
    @DisplayName("saveScore deve registar derrota ou incompleto quando ainda há navios")
    void saveScoreShouldRegisterIncompleteDefeat() throws Exception {
        Fleet fleet = new Fleet();
        fleet.addShip(new Barge(Compass.NORTH, new Position(1, 1)));
        Game game = new Game(fleet);

        ScoreboardManager.saveScore(game);

        String content = java.nio.file.Files.readString(SCOREBOARD_FILE.toPath());

        assertTrue(content.contains("Incompleto/Derrota"));
    }

    @Test
    @DisplayName("printScoreboard deve avisar quando não existe ficheiro")
    void printScoreboardShouldWarnWhenFileDoesNotExist() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            ScoreboardManager.printScoreboard();
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(output.toString().contains("Ainda não existem jogos registados"));
    }

    @Test
    @DisplayName("printScoreboard deve imprimir histórico quando existe ficheiro")
    void printScoreboardShouldPrintHistoryWhenFileExists() {
        Game game = new Game(new Fleet());
        ScoreboardManager.saveScore(game);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            ScoreboardManager.printScoreboard();
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(output.toString().contains("HISTÓRICO DE JOGOS"));
        assertTrue(output.toString().contains("Resultado"));
    }

    @Test
    @DisplayName("saveScore não deve lançar exceção")
    void saveScoreShouldNotThrowException() {
        Game game = new Game(new Fleet());

        assertDoesNotThrow(() -> ScoreboardManager.saveScore(game));
    }

    @Test
    @DisplayName("printScoreboard não deve lançar exceção")
    void printScoreboardShouldNotThrowException() {
        assertDoesNotThrow(() -> ScoreboardManager.printScoreboard());
    }
}