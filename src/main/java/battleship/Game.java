package battleship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.*;

public class Game implements IGame
{
	public static void printBoard(IFleet fleet, List<IMove> moves, boolean show_shots, boolean showLegend) {
		assert fleet != null;
		assert moves != null;

		char[][] map = new char[BOARD_SIZE][BOARD_SIZE];

		for (int r = 0; r < BOARD_SIZE; r++)
			for (int c = 0; c < BOARD_SIZE; c++)
				map[r][c] = EMPTY_MARKER;

		for (IShip ship : fleet.getShips()) {
			for (IPosition ship_pos : ship.getPositions())
				map[ship_pos.getRow()][ship_pos.getColumn()] = SHIP_MARKER;
			if (!ship.stillFloating())
				for (IPosition adjacent_pos : ship.getAdjacentPositions())
					map[adjacent_pos.getRow()][adjacent_pos.getColumn()] = SHIP_ADJACENT_MARKER;
		}

		if (show_shots)
			for (IMove move : moves)
				for (IPosition shot : move.getShots()) {
					if (shot.isInside()){
						int row = shot.getRow();
						int col = shot.getColumn();
						if (map[row][col] == SHIP_MARKER)
							map[row][col] = SHOT_SHIP_MARKER;
						if (map[row][col] == EMPTY_MARKER || map[row][col] == SHIP_ADJACENT_MARKER)
							map[row][col] = SHOT_WATER_MARKER;
					}
				}

		System.out.println();
		System.out.print("    ");
		for (int col = 0; col < BOARD_SIZE; col++) {
			System.out.print(" " + (col + 1));
		}
		System.out.println();

		System.out.print("   +-");
		for (int col = 0; col < BOARD_SIZE; col++) {
			System.out.print("--");
		}
		System.out.println("+");

		for (int row = 0; row < BOARD_SIZE; row++) {
			Position pos = new Position(row, 0);
			char rowLabel = pos.getClassicRow();
			System.out.print(" " + rowLabel + " |");
			for (int col = 0; col < BOARD_SIZE; col++)
				System.out.print(" " + map[row][col]);
			System.out.println(" |");
		}

		System.out.print("   +");
		for (int col = 0; col < BOARD_SIZE; col++)
			System.out.print("--");
		System.out.println("-+");

		if (showLegend) {
			System.out.println("          LEGENDA");
			System.out.println("'" + SHIP_MARKER + "'->navio, '" + SHIP_ADJACENT_MARKER + "'->adjacente a navio, '" + EMPTY_MARKER + "'->água");
			System.out.println("'" + SHOT_SHIP_MARKER + "'->Tiro certeiro, '" + SHOT_WATER_MARKER + "'->Tiro na água");
		}
		System.out.println();
	}

	public static String jsonShots(List<IPosition> shots) {
		assert shots != null;
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

		List<Map<String, Object>> simplifiedShots = new ArrayList<>();
		for (IPosition shot : shots) {
			Map<String, Object> simplePos = new LinkedHashMap<>();
			simplePos.put("row", String.valueOf(shot.getClassicRow()));
			simplePos.put("column", shot.getClassicColumn());
			simplifiedShots.add(simplePos);
		}

		String jsonString = null;
		try {
			jsonString = objectMapper.writeValueAsString(simplifiedShots);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Erro ao serializar o JSON", e);
		}
		return jsonString;
	}

	public static final int BOARD_SIZE = 10;
	public static final int NUMBER_SHOTS = 3;

	private static final char EMPTY_MARKER = '.';
	private static final char SHIP_MARKER = '#';
	private static final char SHOT_SHIP_MARKER = '*';
	private static final char SHOT_WATER_MARKER = 'o';
	private static final char SHIP_ADJACENT_MARKER = '-';

	private final IFleet myFleet;
	private final List<IMove> alienMoves;
	private final IFleet alienFleet;
	private final List<IMove> myMoves;

	private Integer countInvalidShots;
	private Integer countRepeatedShots;
	private Integer countHits;
	private Integer countSinks;
	private int moveNumber;

	public Game(IFleet myFleet) {
		this.moveNumber = 1;
		this.alienMoves = new ArrayList<IMove>();
		this.myMoves = new ArrayList<IMove>();
		this.alienFleet = new Fleet();
		this.myFleet = myFleet;
		this.countInvalidShots = 0;
		this.countRepeatedShots = 0;
		this.countHits = 0;
		this.countSinks = 0;
	}

	@Override
	public IFleet getMyFleet() { return myFleet; }
	@Override
	public List<IMove> getAlienMoves() { return alienMoves; }
	@Override
	public IFleet getAlienFleet() { return myFleet; }
	@Override
	public List<IMove> getMyMoves() { return myMoves; }

	public String randomEnemyFire() {
		Random random = new Random(System.currentTimeMillis());
		Set<IPosition> usablePositions = new HashSet<IPosition>();
		for (int r = 0; r < BOARD_SIZE; r++)
			for (int c = 0; c < BOARD_SIZE; c++)
				usablePositions.add(new Position(r, c));

		this.myFleet.getSunkShips().forEach(ship -> usablePositions.removeAll(ship.getAdjacentPositions()));
		this.alienMoves.forEach(move ->  usablePositions.removeAll(move.getShots()));

		List<IPosition> candidateShots = new ArrayList<>(usablePositions);
		List<IPosition> shots = new ArrayList<IPosition>();

		IPosition newShot = null;
		if (candidateShots.size() >= Game.NUMBER_SHOTS)
			while (shots.size() < Game.NUMBER_SHOTS) {
				newShot = candidateShots.get(random.nextInt(candidateShots.size()));
				if (!shots.contains(newShot)) shots.add(newShot);
			}
		else {
			while (shots.size() < candidateShots.size()) {
				newShot = candidateShots.get(random.nextInt(candidateShots.size()));
				if (!shots.contains(newShot)) shots.add(newShot);
			}
			while (shots.size() < Game.NUMBER_SHOTS) shots.add(newShot);
		}

		this.fireShots(shots);
		return Game.jsonShots(shots);
	}

	public String readEnemyFire(Scanner in) {
		assert in != null;
		String input = in.nextLine().trim();
		List<IPosition> shots = new ArrayList<>();
		Scanner inputScanner = new Scanner(input);
		while (shots.size() < NUMBER_SHOTS && inputScanner.hasNext()) {
			String token = inputScanner.next();
			if (token.matches("[A-Za-z]")) {
				if (inputScanner.hasNextInt()) {
					int row = inputScanner.nextInt();
					shots.add(new Position(token.toUpperCase().charAt(0), row));
				} else {
					throw new IllegalArgumentException("Posição incompleta!");
				}
			} else {
				Scanner singleScanner = new Scanner(token);
				shots.add(Tasks.readClassicPosition(singleScanner));
			}
		}
		this.fireShots(shots);
		return Game.jsonShots(shots);
	}

	public void fireShots(List<IPosition> shots) {
		assert shots != null;
		List<ShotResult> shotResults = new ArrayList<ShotResult>();
		List<IPosition> alreadyShot = new ArrayList<IPosition>();
		for (IPosition pos : shots) {
			shotResults.add(fireSingleShot(pos, alreadyShot.contains(pos)));
			alreadyShot.add(pos);
		}
		Move move = new Move(moveNumber, shots, shotResults);
		move.processEnemyFire(true);
		alienMoves.add(move);
		moveNumber++;
	}

	@Override
	public ShotResult fireSingleShot(IPosition pos, boolean isRepeated) {
		assert pos != null;

		if (!pos.isInside()) {
			countInvalidShots++;
			return new ShotResult(false, false, null, false);
		}

		if (isRepeated || repeatedShot(pos)) {
			countRepeatedShots++;
			return new ShotResult(true, true, null, false);
		}

		IShip ship = myFleet.shipAt(pos);

		if (ship == null) {
			// GUARDAR NA BD
			DatabaseManager.saveMove(pos.toString(), "Água");
			return new ShotResult(true, false, null, false);
		} else {
			ship.shoot(pos);
			countHits++;
			String res = ship.stillFloating() ? "Acertou" : "Afundou " + ship.getClass().getSimpleName();
			// GUARDAR NA BD
			DatabaseManager.saveMove(pos.toString(), res);
			if (!ship.stillFloating()) countSinks++;
			return new ShotResult(true, false, ship, !ship.stillFloating());
		}
	}

	@Override
	public int getRepeatedShots() { return this.countRepeatedShots; }
	@Override
	public int getInvalidShots() { return this.countInvalidShots; }
	@Override
	public int getHits() { return this.countHits; }
	@Override
	public int getSunkShips() { return this.countSinks; }
	@Override
	public int getRemainingShips() { return myFleet.getFloatingShips().size(); }

	public boolean repeatedShot(IPosition pos) {
		for (IMove move : alienMoves)
			if (move.getShots().contains(pos)) return true;
		return false;
	}

	public void printMyBoard(boolean s, boolean l) { Game.printBoard(this.myFleet, this.alienMoves, s, l); }
	public void printAlienBoard(boolean s, boolean l) { Game.printBoard(this.alienFleet, this.myMoves, s, l); }

	public void over() {
		System.out.println();
		System.out.println("+--------------------------------------------------------------+");
		System.out.println("| Maldito sejas, Java Sparrow, eu voltarei, glub glub glub ... |");
		System.out.println("+--------------------------------------------------------------+");
	}
}