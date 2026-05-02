package battleship;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

	private Game game;

	@BeforeEach
	void setUp() {
		game = new Game(new Fleet());
	}

	@AfterEach
	void tearDown() {
		game = null;
	}

	@Test
	@DisplayName("readEnemyFire lê posições no formato compacto")
	void readEnemyFireCompactClassicPositions() {
		Scanner scanner = new Scanner("A1 B2 C3\n");

		String json = game.readEnemyFire(scanner);

		assertNotNull(json);
		assertTrue(json.contains("\"row\""));
		assertTrue(json.contains("\"column\""));
		assertEquals(1, game.getAlienMoves().size());
		assertEquals(3, game.getAlienMoves().get(0).getShots().size());
	}

	@Test
	@DisplayName("Constructor initializes game correctly")
	void constructor() {
		assertNotNull(game);
		assertNotNull(game.getAlienMoves());
		assertTrue(game.getAlienMoves().isEmpty());
		assertEquals(0, game.getInvalidShots());
		assertEquals(0, game.getRepeatedShots());
		assertEquals(0, game.getHits());
		assertEquals(0, game.getSunkShips());
	}

	@Test
	@DisplayName("fireSingleShot invalid position increments invalid shots")
	void fireSingleShotInvalidPosition() {
		IGame.ShotResult result = game.fireSingleShot(new Position(-1, -1), false);

		assertFalse(result.valid());
		assertFalse(result.repeated());
		assertNull(result.ship());
		assertFalse(result.sunk());
		assertEquals(1, game.getInvalidShots());
	}

	@Test
	@DisplayName("fireSingleShot repeated flag increments repeated shots")
	void fireSingleShotRepeatedFlag() {
		IGame.ShotResult result = game.fireSingleShot(new Position(2, 3), true);

		assertTrue(result.valid());
		assertTrue(result.repeated());
		assertNull(result.ship());
		assertFalse(result.sunk());
		assertEquals(1, game.getRepeatedShots());
	}

	@Test
	@DisplayName("fireSingleShot repeated previous shot")
	void fireSingleShotRepeatedPreviousShot() {
		Position position = new Position(2, 3);

		game.fireShots(List.of(position));
		IGame.ShotResult result = game.fireSingleShot(position, false);

		assertTrue(result.valid());
		assertTrue(result.repeated());
		assertEquals(1, game.getRepeatedShots());
	}

	@Test
	@DisplayName("fireSingleShot miss")
	void fireSingleShotMissShot() {
		IGame.ShotResult result = game.fireSingleShot(new Position(2, 3), false);

		assertTrue(result.valid());
		assertFalse(result.repeated());
		assertNull(result.ship());
		assertFalse(result.sunk());
		assertEquals(0, game.getHits());
	}

	@Test
	@DisplayName("fireSingleShot hits ship")
	void fireSingleShotHitShip() {
		Ship ship = new Frigate(Compass.EAST, new Position(2, 3));
		game.getMyFleet().addShip(ship);

		IGame.ShotResult result = game.fireSingleShot(new Position(2, 3), false);

		assertTrue(result.valid());
		assertFalse(result.repeated());
		assertNotNull(result.ship());
		assertFalse(result.sunk());
		assertEquals(1, game.getHits());
		assertEquals(0, game.getSunkShips());
	}

	@Test
	@DisplayName("fireSingleShot sinks ship")
	void fireSingleShotSinkShip() {
		Ship ship = new Barge(Compass.NORTH, new Position(2, 3));
		game.getMyFleet().addShip(ship);

		IGame.ShotResult result = game.fireSingleShot(new Position(2, 3), false);

		assertTrue(result.valid());
		assertFalse(result.repeated());
		assertNotNull(result.ship());
		assertTrue(result.sunk());
		assertEquals(1, game.getHits());
		assertEquals(1, game.getSunkShips());
	}

	@Test
	@DisplayName("fireShots creates move and stores shots")
	void fireShotsValidShots() {
		List<IPosition> positions = List.of(
				new Position(2, 3),
				new Position(2, 4),
				new Position(2, 5)
		);

		game.fireShots(positions);

		assertEquals(1, game.getAlienMoves().size());
		assertEquals(3, game.getAlienMoves().get(0).getShots().size());
	}

	@Test
	@DisplayName("fireShots handles repeated shots inside same move")
	void fireShotsRepeatedInsideSameMove() {
		Position position = new Position(2, 3);

		game.fireShots(List.of(position, position, new Position(4, 4)));

		assertEquals(1, game.getRepeatedShots());
		assertEquals(1, game.getAlienMoves().size());
	}

	@Test
	@DisplayName("repeatedShot returns true after firing position")
	void repeatedShotTrue() {
		Position position = new Position(2, 3);

		game.fireShots(List.of(position));

		assertTrue(game.repeatedShot(position));
	}

	@Test
	@DisplayName("repeatedShot returns false before firing position")
	void repeatedShotFalse() {
		assertFalse(game.repeatedShot(new Position(2, 3)));
	}

	@Test
	@DisplayName("getRemainingShips returns floating ships")
	void getRemainingShips() {
		IFleet fleet = game.getMyFleet();
		Ship ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		Ship ship2 = new Frigate(Compass.EAST, new Position(5, 5));

		fleet.addShip(ship1);
		fleet.addShip(ship2);

		assertEquals(2, game.getRemainingShips());

		ship2.sink();

		assertEquals(1, game.getRemainingShips());
	}

	@Test
	@DisplayName("getters return initialized objects")
	void getters() {
		assertNotNull(game.getMyFleet());
		assertNotNull(game.getAlienFleet());
		assertNotNull(game.getAlienMoves());
		assertNotNull(game.getMyMoves());
		assertTrue(game.getMyMoves().isEmpty());
	}

	@Test
	@DisplayName("jsonShots serializes positions")
	void jsonShotsSerializesPositions() {
		String json = Game.jsonShots(List.of(
				new Position(0, 0),
				new Position(1, 1)
		));

		assertNotNull(json);
		assertTrue(json.contains("\"row\""));
		assertTrue(json.contains("\"column\""));
		assertTrue(json.contains("A"));
		assertTrue(json.contains("1"));
	}

	@Test
	@DisplayName("readEnemyFire reads separated classic positions")
	void readEnemyFireSeparatedClassicPositions() {
		Scanner scanner = new Scanner("A 1 B 2 C 3\n");

		String json = game.readEnemyFire(scanner);

		assertNotNull(json);
		assertTrue(json.contains("\"row\""));
		assertTrue(json.contains("\"column\""));
		assertEquals(1, game.getAlienMoves().size());
		assertEquals(3, game.getAlienMoves().get(0).getShots().size());
	}

	@Test
	@DisplayName("readEnemyFire throws exception for incomplete position")
	void readEnemyFireIncompletePositionThrowsException() {
		Scanner scanner = new Scanner("A\n");

		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(scanner));
	}

	@Test
	@DisplayName("randomEnemyFire generates shots and creates alien move")
	void randomEnemyFireGeneratesShots() {
		String json = game.randomEnemyFire();

		assertNotNull(json);
		assertTrue(json.contains("\"row\""));
		assertTrue(json.contains("\"column\""));
		assertEquals(1, game.getAlienMoves().size());
		assertEquals(Game.NUMBER_SHOTS, game.getAlienMoves().get(0).getShots().size());
	}

	@Test
	@DisplayName("printBoard without shots and without legend")
	void printBoardWithoutShotsAndLegend() {
		Fleet fleet = new Fleet();
		fleet.addShip(new Barge(Compass.NORTH, new Position(1, 1)));

		assertDoesNotThrow(() -> Game.printBoard(fleet, List.of(), false, false));
	}

	@Test
	@DisplayName("printBoard with shots and legend")
	void printBoardWithShotsAndLegend() {
		Fleet fleet = new Fleet();
		Ship ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);

		Move move = new Move(
				1,
				List.of(
						new Position(1, 1),
						new Position(0, 0),
						new Position(99, 99)
				),
				List.of()
		);

		assertDoesNotThrow(() -> Game.printBoard(fleet, List.of(move), true, true));
	}

	@Test
	@DisplayName("printBoard with sunk ship covers adjacent positions")
	void printBoardWithSunkShip() {
		Fleet fleet = new Fleet();
		Ship ship = new Barge(Compass.NORTH, new Position(1, 1));
		fleet.addShip(ship);
		ship.sink();

		Move move = new Move(
				1,
				List.of(new Position(1, 2)),
				List.of()
		);

		assertDoesNotThrow(() -> Game.printBoard(fleet, List.of(move), true, true));
	}

	@Test
	@DisplayName("printMyBoard does not throw")
	void printMyBoard() {
		assertDoesNotThrow(() -> game.printMyBoard(true, true));
	}

	@Test
	@DisplayName("printAlienBoard does not throw")
	void printAlienBoard() {
		assertDoesNotThrow(() -> game.printAlienBoard(false, false));
	}

	@Test
	@DisplayName("over prints final message")
	void over() {
		assertDoesNotThrow(game::over);
	}

	@Test
	@DisplayName("over writes message to output")
	void overPrintsMessage() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream originalOut = System.out;
		System.setOut(new PrintStream(output));

		try {
			game.over();
		} finally {
			System.setOut(originalOut);
		}

		assertTrue(output.toString().contains("Java Sparrow"));
	}
}