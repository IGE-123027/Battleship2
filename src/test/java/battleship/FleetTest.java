package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class Fleet.
 * Author: ${user.name}
 * Date: 2024-03-20 14:00
 * Cyclomatic Complexity:
 * - createRandom(): 3
 * - Constructor: 1
 * - getShips(): 1
 * - addShip(): 5 (If com short-circuits)
 * - getShipsLike(): 3
 * - getFloatingShips(): 3
 * - getSunkShips(): 3
 * - shipAt(): 3
 * - isInsideBoard(): 5
 * - colisionRisk(): 3
 * - print...(): 1 cada
 */
public class FleetTest {

	private Fleet fleet;

	/**
	 * Subclasse concreta de Ship para termos controlo absoluto sobre o tamanho e posição.
	 * Facilita imenso os testes de colisão e limites do tabuleiro.
	 */
	private static class ConcreteTestShip extends Ship {
		public ConcreteTestShip(String category, IPosition pos) {
			super(category, Compass.NORTH, pos, 1);
			this.positions.add(pos); // Garante que o navio ocupa de facto esta posição
		}
	}

	@BeforeEach
	void setUp() {
		fleet = new Fleet();
	}

	@AfterEach
	void tearDown() {
		fleet = null;
	}

	// --- TESTE DO GERADOR ALEATÓRIO ---

	@Test
	void testCreateRandom() {
		// Verifica se a factory consegue instanciar uma frota sem rebentar
		IFleet randomFleet = Fleet.createRandom();
		assertNotNull(randomFleet, "Erro: A frota aleatória gerada não devia ser nula.");
		// Como o gerador pode ter dificuldade em colocar os barcos todos dependendo do random,
		// garantimos apenas que colocou pelo menos 1 barco com sucesso.
		assertFalse(randomFleet.getShips().isEmpty(), "Erro: A frota gerada devia conter navios.");
	}

	// --- TESTE DE CONSTRUTOR E GETTERS SIMPLES ---

	@Test
	void testConstructorAndGetShips() {
		assertNotNull(fleet.getShips(), "Erro: A lista de navios não devia ser nula.");
		assertTrue(fleet.getShips().isEmpty(), "Erro: Uma frota nova devia estar vazia.");
	}

	// --- LÓGICA CONDICIONAL E LIMITES: addShip() e isInsideBoard() ---

	@Test
	void addShip1_Success() {
		// Caminho 1: Tudo válido
		IShip ship = new ConcreteTestShip("Barca", new Position(5, 5));
		assertTrue(fleet.addShip(ship), "Erro: Navio válido devia ser adicionado com sucesso.");
		assertEquals(1, fleet.getShips().size(), "Erro: A frota devia ter 1 navio.");
	}

	@Test
	void addShip2_SizeLimitExceeded() {
		// Caminho 2: Força o erro de limite de tamanho da frota (FLEET_SIZE)
		// Colocamos os navios com 2 espaços de distância para nunca darem erro de colisão
		boolean limitHit = false;
		int row = 0, col = 0;

		for (int i = 0; i < 50; i++) { // 50 tentativas para garantir que ultrapassa o FLEET_SIZE
			IShip ship = new ConcreteTestShip("Barca", new Position(row, col));
			if (!fleet.addShip(ship)) {
				limitHit = true; // Assim que for rejeitado, sabemos que bateu no limite
				break;
			}
			col += 2;
			if (col >= Game.BOARD_SIZE) { col = 0; row += 2; }
		}
		assertTrue(limitHit, "Erro: A frota devia rejeitar adições quando ultrapassa o FLEET_SIZE.");
	}

	@Test
	void addShip3_OutsideLeft() {
		// Caminho 3: isInsideBoard falha na Esquerda (Coluna < 0)
		IShip ship = new ConcreteTestShip("Barca", new Position(5, -1));
		assertFalse(fleet.addShip(ship), "Erro: Navio fora dos limites (esquerda) devia ser rejeitado.");
	}

	@Test
	void addShip4_OutsideRight() {
		// Caminho 4: isInsideBoard falha na Direita (Coluna >= BOARD_SIZE)
		IShip ship = new ConcreteTestShip("Barca", new Position(5, Game.BOARD_SIZE));
		assertFalse(fleet.addShip(ship), "Erro: Navio fora dos limites (direita) devia ser rejeitado.");
	}

	@Test
	void addShip5_OutsideTop() {
		// Caminho 5: isInsideBoard falha em Cima (Linha < 0)
		IShip ship = new ConcreteTestShip("Barca", new Position(-1, 5));
		assertFalse(fleet.addShip(ship), "Erro: Navio fora dos limites (cima) devia ser rejeitado.");
	}

	@Test
	void addShip6_OutsideBottom() {
		// Caminho 6: isInsideBoard falha em Baixo (Linha >= BOARD_SIZE)
		IShip ship = new ConcreteTestShip("Barca", new Position(Game.BOARD_SIZE, 5));
		assertFalse(fleet.addShip(ship), "Erro: Navio fora dos limites (baixo) devia ser rejeitado.");
	}

	@Test
	void addShip7_CollisionRisk() {
		// Caminho 7: colisionRisk falha (Posições adjacentes ou sobrepostas)
		fleet.addShip(new ConcreteTestShip("Barca", new Position(5, 5)));
		IShip overlappingShip = new ConcreteTestShip("Nau", new Position(5, 6)); // 5,6 é adjacente a 5,5
		assertFalse(fleet.addShip(overlappingShip), "Erro: Navio com risco de colisão devia ser rejeitado.");
	}

	// --- LÓGICA DE FILTRAGEM (getShipsLike, Floating, Sunk) ---

	@Test
	void getShipsLike1() {
		IShip barca = new ConcreteTestShip("Barca", new Position(2, 2));
		IShip nau = new ConcreteTestShip("Nau", new Position(8, 8));
		fleet.addShip(barca);
		fleet.addShip(nau);

		List<IShip> result = fleet.getShipsLike("Barca");
		assertEquals(1, result.size(), "Erro: Devia encontrar apenas 1 Barca.");
		assertEquals("Barca", result.get(0).getCategory(), "Erro: A categoria filtrada está incorreta.");
	}

	@Test
	void getFloatingAndSunkShips1() {
		IShip floatingShip = new ConcreteTestShip("Fragata", new Position(1, 1));
		IShip sunkShip = new ConcreteTestShip("Bote", new Position(9, 9));

		fleet.addShip(floatingShip);
		fleet.addShip(sunkShip);

		// Afundar um navio explicitamente
		sunkShip.getPositions().get(0).shoot();

		assertEquals(1, fleet.getFloatingShips().size(), "Erro: Devia haver apenas 1 navio a flutuar.");
		assertEquals(1, fleet.getSunkShips().size(), "Erro: Devia haver apenas 1 navio afundado.");
		assertEquals(floatingShip, fleet.getFloatingShips().get(0), "Erro: O navio a flutuar está incorreto.");
		assertEquals(sunkShip, fleet.getSunkShips().get(0), "Erro: O navio afundado está incorreto.");
	}

	// --- LÓGICA DE DETEÇÃO ESPACIAL: shipAt() ---

	@Test
	void shipAt1() {
		IShip ship = new ConcreteTestShip("Galeao", new Position(5, 5));
		fleet.addShip(ship);

		assertEquals(ship, fleet.shipAt(new Position(5, 5)), "Erro: Devia encontrar o navio na posição (5,5).");
		assertNull(fleet.shipAt(new Position(1, 1)), "Erro: Posição vazia devia devolver null.");
	}

	// --- TESTE DE OUTPUT (PRINTS) ---
	// Estes métodos não devolvem valor, por isso testamos apenas se correm sem lançar exceções.

	@Test
	void testPrinters() {
		fleet.addShip(new ConcreteTestShip("Galeao", new Position(5, 5)));

		assertDoesNotThrow(() -> {
			fleet.printStatus();
			fleet.printAllShips();
			fleet.printFloatingShips();
			fleet.printShipsByCategory("Galeao");
		}, "Erro: Os métodos de impressão não deviam lançar exceções.");
	}

	// =========================================================================
	// TESTES DOS ASSERTS ESCONDIDOS (A rasteira do Professor)
	// =========================================================================

	@Test
	void testAssertsAndExceptions() {
		// Garantimos que todos os métodos disparam um erro lógico caso recebam null
		assertAll("Testar se os asserts de segurança disparam",
				() -> assertThrows(AssertionError.class, () -> fleet.addShip(null)),
				() -> assertThrows(AssertionError.class, () -> fleet.getShipsLike(null)),
				() -> assertThrows(AssertionError.class, () -> fleet.shipAt(null)),
				() -> assertThrows(AssertionError.class, () -> fleet.printShips(null)),
				() -> assertThrows(AssertionError.class, () -> fleet.printShipsByCategory(null))
		);
	}
}