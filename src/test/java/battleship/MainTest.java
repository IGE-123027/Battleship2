package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @Test
    @DisplayName("printTitle deve imprimir o título do jogo")
    void printTitleShouldPrintGameTitle() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            Main.printTitle();
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(output.toString().contains("***  Battleship  ***"));
    }

    @Test
    @DisplayName("main deve executar sem bloquear quando recebe opção de sair")
    void mainShouldRunWithoutBlocking() {
        String input = "0\n";
         InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        try {
            assertDoesNotThrow(() -> Main.main(new String[]{}));
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    @DisplayName("setupEmptyBoard deve preencher o tabuleiro com água")
    void setupEmptyBoardShouldFillBoardWithWater() {
        char[][] board = new char[10][10];

        Main.setupEmptyBoard(board);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertEquals('~', board[i][j]);
            }
        }
    }
}