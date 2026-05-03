package battleship;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class MoveTest {

    private Move move;

    @BeforeEach
    void setUp() {
        move = new Move(1, new ArrayList<>(), new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        move = null;
    }

    @Test
    @DisplayName("Constructor deve guardar número, tiros e resultados")
    void constructorAndGetters() {
        List<IPosition> shots = List.of(new Position(1, 1));
        List<IGame.ShotResult> results = new ArrayList<>();

        move = new Move(3, shots, results);

        assertEquals(3, move.getNumber());
        assertEquals(shots, move.getShots());
        assertEquals(results, move.getShotResults());
    }

    @Test
    @DisplayName("toString deve devolver formato esperado")
    void toStringTest() {
        List<IPosition> shots = List.of(new Position(1, 1), new Position(2, 2));
        move = new Move(5, shots, new ArrayList<>());

        assertEquals("Move{number=5, shots=2, results=0}", move.toString());
    }

    @Test
    @DisplayName("readMove deve ler vários tiros")
    void readMoveMultipleShots() {
        Scanner scanner = new Scanner("3 1 2 3 4 5 6");

        Move result = IMove.readMove(7, scanner);

        assertEquals(7, result.getNumber());
        assertEquals(3, result.getShots().size());
        assertEquals(new Position(1, 2), result.getShots().get(0));
        assertEquals(new Position(3, 4), result.getShots().get(1));
        assertEquals(new Position(5, 6), result.getShots().get(2));
        assertTrue(result.getShotResults().isEmpty());
    }

    @Test
    @DisplayName("processEnemyFire deve contar tiros inválidos")
    void processEnemyFireInvalidShots() {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(false, false, null, false)
        );

        move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        assertTrue(json.contains("\"validShots\" : 0"));
        assertTrue(json.contains("\"repeatedShots\" : 0"));
    }

    @Test
    @DisplayName("processEnemyFire deve contar tiros repetidos")
    void processEnemyFireRepeatedShots() {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, true, null, false)
        );

        move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        assertTrue(json.contains("\"repeatedShots\" : 1"));
    }

    @Test
    @DisplayName("processEnemyFire deve contar tiros na água")
    void processEnemyFireMissedShots() {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, null, false)
        );

        move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        assertTrue(json.contains("\"validShots\" : 1"));
        assertTrue(json.contains("\"missedShots\" : 1"));
    }

    @Test
    @DisplayName("processEnemyFire deve registar acerto em barco")
    void processEnemyFireHitShip() {
        IShip ship = new Barge(Compass.NORTH, new Position(1, 1));

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, ship, false)
        );

        move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        assertTrue(json.contains("\"hitsOnBoats\""));
        assertTrue(json.contains("\"type\" : \"Barca\""));
        assertTrue(json.contains("\"hits\" : 1"));
    }

    @Test
    @DisplayName("processEnemyFire deve registar barco afundado")
    void processEnemyFireSunkShip() {
        IShip ship = new Barge(Compass.NORTH, new Position(1, 1));

        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, ship, true)
        );

        move = new Move(1, new ArrayList<>(), results);

        String json = move.processEnemyFire(false);

        assertTrue(json.contains("\"sunkBoats\""));
        assertTrue(json.contains("\"type\" : \"Barca\""));
        assertTrue(json.contains("\"count\" : 1"));
    }

    @Test
    @DisplayName("processEnemyFire verbose deve executar sem erro")
    void processEnemyFireVerbose() {
        List<IGame.ShotResult> results = List.of(
                new IGame.ShotResult(true, false, null, false),
                new IGame.ShotResult(true, true, null, false)
        );

        move = new Move(1, new ArrayList<>(), results);

        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }
}