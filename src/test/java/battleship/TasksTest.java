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
    @DisplayName("readShip deve criar navio válido")
    void readShipShouldCreateValidShip() {
        Scanner scanner = new Scanner("barca 2 3 N");

        Ship ship = Tasks.readShip(scanner);

        assertNotNull(ship);
        assertEquals("Barca", ship.getCategory());
        assertEquals(new Position(2, 3), ship.getPosition());
    }

    @Test
    @DisplayName("readShip deve devolver null para tipo de navio inválido")
    void readShipShouldReturnNullForInvalidShipType() {
        Scanner scanner = new Scanner("banana 2 3 N");

        Ship ship = Tasks.readShip(scanner);

        assertNull(ship);
    }

    @Test
    @DisplayName("readClassicPosition deve ler formato compacto A3")
    void readClassicPositionCompactFormat() {
        Scanner scanner = new Scanner("A3");

        IPosition position = Tasks.readClassicPosition(scanner);

        assertEquals(new Position('A', 3), position);
    }

    @Test
    @DisplayName("readClassicPosition deve ler formato separado A 3")
    void readClassicPositionSeparatedFormat() {
        Scanner scanner = new Scanner("A 3");

        IPosition position = Tasks.readClassicPosition(scanner);

        assertEquals(new Position('A', 3), position);
    }

    @Test
    @DisplayName("readClassicPosition deve lançar exceção quando não há input")
    void readClassicPositionEmptyInputThrowsException() {
        Scanner scanner = new Scanner("");

        assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(scanner));
    }

    @Test
    @DisplayName("readClassicPosition deve lançar exceção para formato inválido")
    void readClassicPositionInvalidFormatThrowsException() {
        Scanner scanner = new Scanner("ABC");

        assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(scanner));
    }

    @Test
    @DisplayName("menu deve terminar quando recebe desisto")
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
    @DisplayName("menu deve mostrar ajuda e depois terminar")
    void menuShouldShowHelpAndThenExit() {
        String input = "ajuda desisto\n";

        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        try {
            assertDoesNotThrow(Tasks::menu);
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    @DisplayName("menu deve lidar com comando desconhecido e depois terminar")
    void menuShouldHandleUnknownCommandAndThenExit() {
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