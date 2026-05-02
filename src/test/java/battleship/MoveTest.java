package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * Comprehensive test class for Move.
 * Tests all branches and methods for 100% coverage.
 */
@DisplayName("Move Tests - 100% Branch Coverage")
class MoveTest {

    private Move move;
    private List<IPosition> shots;
    private List<IGame.ShotResult> results;

    @BeforeEach
    void setUp() {
        shots = Arrays.asList(
            new Position(1, 1),
            new Position(2, 2),
            new Position(3, 3)
        );
    }

    private IGame.ShotResult createShotResult(boolean valid, boolean repeated, IShip ship, boolean sunk) {
        return new IGame.ShotResult(valid, repeated, ship, sunk);
    }

    @AfterEach
    void tearDown() {
        move = null;
    }

    // ==================== BASIC GETTERS ====================

    @Test
    @DisplayName("getNumber returns correct move number")
    void testGetNumber() {
        results = Arrays.asList(
            createShotResult(true, false, null, false),
            createShotResult(true, false, null, false),
            createShotResult(true, false, null, false)
        );
        move = new Move(5, shots, results);
        assertEquals(5, move.getNumber());
    }

    @Test
    @DisplayName("getShots returns correct list of shots")
    void testGetShots() {
        results = Arrays.asList(
            createShotResult(true, false, null, false),
            createShotResult(true, false, null, false),
            createShotResult(true, false, null, false)
        );
        move = new Move(1, shots, results);
        assertEquals(3, move.getShots().size());
        assertEquals(shots, move.getShots());
    }

    @Test
    @DisplayName("getShotResults returns correct list of results")
    void testGetShotResults() {
        results = Arrays.asList(
            createShotResult(true, false, null, false),
            createShotResult(true, false, null, false),
            createShotResult(true, false, null, false)
        );
        move = new Move(1, shots, results);
        assertEquals(3, move.getShotResults().size());
        assertEquals(results, move.getShotResults());
    }

    // ==================== INVALID SHOTS ====================

    @Test
    @DisplayName("processEnemyFire with only invalid shots (verbose=false)")
    void testProcessInvalidShotsNoVerbose() {
        results = Arrays.asList(
            createShotResult(false, false, null, false),
            createShotResult(false, false, null, false),
            createShotResult(false, false, null, false)
        );
        move = new Move(1, shots, results);
        String json = move.processEnemyFire(false);
        assertNotNull(json);
        assertTrue(json.contains("\"validShots\" : 0"));
        assertTrue(json.contains("\"outsideShots\" : 3"));
    }

    @Test
    @DisplayName("processEnemyFire with only invalid shots (verbose=true)")
    void testProcessInvalidShotsWithVerbose() {
        results = Arrays.asList(
            createShotResult(false, false, null, false),
            createShotResult(false, false, null, false),
            createShotResult(false, false, null, false)
        );
        move = new Move(1, shots, results);
        String json = move.processEnemyFire(true);
        assertNotNull(json);
    }

    // ==================== REPEATED SHOTS ====================

    @Test
    @DisplayName("processEnemyFire with only repeated shots")
    void testProcessRepeatedShots() {
        results = Arrays.asList(
            createShotResult(true, true, null, false),
            createShotResult(true, true, null, false),
            createShotResult(true, true, null, false)
        );
        move = new Move(1, shots, results);
        String json = move.processEnemyFire(true);
        assertNotNull(json);
        assertTrue(json.contains("\"repeatedShots\" : 3"));
    }

    // ==================== MISSED SHOTS ====================

    @Test
    @DisplayName("processEnemyFire with only missed shots")
    void testProcessMissedShots() {
        results = Arrays.asList(
            createShotResult(true, false, null, false),
            createShotResult(true, false, null, false),
            createShotResult(true, false, null, false)
        );
        move = new Move(1, shots, results);
        String json = move.processEnemyFire(true);
        assertNotNull(json);
        assertTrue(json.contains("\"missedShots\" : 3"));
    }

    // ==================== HIT SHOTS ====================

    @Test
    @DisplayName("processEnemyFire with hits on ship")
    void testProcessHitShots() {
        IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
        results = Arrays.asList(
            createShotResult(true, false, ship, false),
            createShotResult(true, false, ship, false),
            createShotResult(true, false, null, false)
        );
        move = new Move(1, shots, results);
        String json = move.processEnemyFire(true);
        assertNotNull(json);
        assertTrue(json.contains("\"validShots\" : 3"));
    }

    // ==================== SUNK SHIPS ====================

    @Test
    @DisplayName("processEnemyFire with sunk ship")
    void testProcessSunkShip() {
        IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
        ship.sink(); // Sink the ship
        results = Arrays.asList(
            createShotResult(true, false, ship, true),
            createShotResult(true, false, null, false),
            createShotResult(true, false, null, false)
        );
        move = new Move(1, shots, results);
        String json = move.processEnemyFire(true);
        assertNotNull(json);
        assertTrue(json.contains("\"sunkBoats\""));
    }

    // ==================== MIXED SCENARIOS ====================

    @Test
    @DisplayName("processEnemyFire with mixed results (hits + misses + repeated)")
    void testProcessMixedResults() {
        IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
        results = Arrays.asList(
            createShotResult(true, false, ship, false),     // valid hit
            createShotResult(true, true, null, false),       // repeated
            createShotResult(true, false, null, false)       // valid miss
        );
        move = new Move(1, shots, results);
        String json = move.processEnemyFire(true);
        assertNotNull(json);
        assertTrue(json.contains("\"validShots\" : 2"));
        assertTrue(json.contains("\"repeatedShots\" : 1"));
        assertTrue(json.contains("\"missedShots\" : 1"));
    }

    @Test
    @DisplayName("processEnemyFire with outside shots")
    void testProcessOutsideShots() {
        results = Arrays.asList(
            createShotResult(true, false, null, false),
            createShotResult(true, false, null, false),
            createShotResult(false, false, null, false)   // outside
        );
        move = new Move(1, shots, results);
        String json = move.processEnemyFire(true);
        assertNotNull(json);
        assertTrue(json.contains("\"outsideShots\" : 1"));
    }

    @Test
    @DisplayName("processEnemyFire with multiple sunk ships of same type")
    void testProcessMultipleSunkShipsOfSameType() {
        IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
        IShip ship2 = new Barge(Compass.NORTH, new Position(2, 2));
        ship1.sink();
        ship2.sink();
        results = Arrays.asList(
            createShotResult(true, false, ship1, true),
            createShotResult(true, false, ship2, true),
            createShotResult(true, false, null, false)
        );
        move = new Move(1, shots, results);
        String json = move.processEnemyFire(true);
        assertNotNull(json);
        assertTrue(json.contains("\"sunkBoats\""));
    }

    @Test
    @DisplayName("processEnemyFire with hits on multiple ship types")
    void testProcessHitsOnMultipleShips() {
        IShip barge = new Barge(Compass.NORTH, new Position(1, 1));
        IShip caravel = new Caravel(Compass.NORTH, new Position(2, 2));
        results = Arrays.asList(
            new IGame.ShotResult(true, false, barge, false),
            new IGame.ShotResult(true, false, caravel, false),
            new IGame.ShotResult(true, false, null, false)
        );
        move = new Move(1, shots, results);
        String json = move.processEnemyFire(true);
        assertNotNull(json);
        assertTrue(json.contains("\"hitsOnBoats\""));
    }

    // ==================== VERBOSE FLAG ====================

    @Test
    @DisplayName("processEnemyFire verbose=true produces output")
    void testVerboseTrueOutput() {
        results = Arrays.asList(
            new IGame.ShotResult(true, false, null, false),
            new IGame.ShotResult(true, false, null, false),
            new IGame.ShotResult(true, false, null, false)
        );
        move = new Move(1, shots, results);
        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    @Test
    @DisplayName("processEnemyFire verbose=false produces JSON only")
    void testVerboseFalseOutput() {
        results = Arrays.asList(
            new IGame.ShotResult(true, false, null, false),
            new IGame.ShotResult(true, false, null, false),
            new IGame.ShotResult(true, false, null, false)
        );
        move = new Move(1, shots, results);
        String json = move.processEnemyFire(false);
        assertNotNull(json);
        assertTrue(json.contains("validShots"));
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("processEnemyFire with empty shots list")
    void testProcessEmptyShots() {
        shots = Arrays.asList();
        results = Arrays.asList();
        move = new Move(1, shots, results);
        String json = move.processEnemyFire(true);
        assertNotNull(json);
    }

    @Test
    @DisplayName("Move toString includes move number and shot count")
    void testToString() {
        results = Arrays.asList(
            new IGame.ShotResult(true, false, null, false),
            new IGame.ShotResult(true, false, null, false),
            new IGame.ShotResult(true, false, null, false)
        );
        move = new Move(5, shots, results);
        String str = move.toString();
        assertTrue(str.contains("number=5"));
        assertTrue(str.contains("shots=3"));
    }
}

