package battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class TasksTest {

    @Test
    @DisplayName("menuHelp deve executar sem lançar exceções")
    void menuHelpShouldNotThrow() {
        assertDoesNotThrow(Tasks::menuHelp);
    }

    @Test
    @DisplayName("readPosition deve ler linha e coluna")
    void readPositionShouldReadRowAndColumn() {
        Scanner scanner = new Scanner("3 4");

        Position position = Tasks.readPosition(scanner);

        assertEquals(3, position.getRow());
        assertEquals(4, position.getColumn());
    }


    @Test
    @DisplayName("readClassicPosition deve ler formato compacto")
    void readClassicPositionCompact() {
        Scanner scanner = new Scanner("A3");

        IPosition position = Tasks.readClassicPosition(scanner);

        assertEquals(new Position('A', 3), position);
    }

    @Test
    @DisplayName("readClassicPosition deve ler formato separado")
    void readClassicPositionSeparated() {
        Scanner scanner = new Scanner("A 3");

        IPosition position = Tasks.readClassicPosition(scanner);

        assertEquals(new Position('A', 3), position);
    }

    @Test
    @DisplayName("readClassicPosition deve lançar exceção com input vazio")
    void readClassicPositionEmptyThrows() {
        Scanner scanner = new Scanner("");

        assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(scanner));
    }

    @Test
    @DisplayName("readClassicPosition deve lançar exceção com formato inválido")
    void readClassicPositionInvalidThrows() {
        Scanner scanner = new Scanner("ABC");

        assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(scanner));
    }

    @Test
    @DisplayName("menu deve terminar com desisto")
    void menuShouldExitWithDesisto() {
        String input = "desisto\n";

        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        try {
            assertDoesNotThrow(Tasks::menu);
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    @DisplayName("menu deve tratar comando desconhecido e terminar")
    void menuShouldHandleUnknownCommand() {
        String input = "banana desisto\n";

        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        try {
            assertDoesNotThrow(Tasks::menu);
        } finally {
            System.setIn(originalIn);
        }
    }
}