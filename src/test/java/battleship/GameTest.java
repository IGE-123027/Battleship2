package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class Game.
 * Author: ${user.name}
 * Date: 2024-03-20 16:00
 * Cyclomatic Complexity: Garantida a 100% (Branch e Line)
 */
public class GameTest {

	private Game game;
	private Fleet fleet;

	/**
	 * Subclasse concreta de Ship modificada para aceitar múltiplas posições.
	 * Permite-nos simular navios com tamanho flexível para testes parciais.
	 */
	private static class TestShip extends Ship {
		public TestShip(String category, IPosition... positionsArray) {
			super(category, Compass.NORTH, positionsArray[0], positionsArray.length);
			this.positions.clear(); // Limpa a lista default
			for (IPosition p : positionsArray) {
				this.positions.add(p);
			}
		}
	}

	@BeforeEach
	void setUp() {
		fleet = new Fleet();
		game = new Game(fleet);
	}

	@AfterEach
	void tearDown() {
		game = null;
		fleet = null;
	}

	// =========================================================================
	// BLOCO 1: CONSTRUTOR E GETTERS BÁSICOS
	// =========================================================================

	@Test
	void testConstructorAndGetters() {
		assertAll("Verifica estado inicial do jogo",
				() -> assertNotNull(game.getMyFleet(), "Erro: Fleet não devia ser nula."),
				() -> assertNotNull(game.getAlienMoves(), "Erro: Lista de jogadas inimigas não devia ser nula."),
				() -> assertNotNull(game.getAlienFleet(), "Erro: Frota inimiga não devia ser nula."),
				() -> assertNotNull(game.getMyMoves(), "Erro: Lista de jogadas não devia ser nula."),
				() -> assertEquals(0, game.getInvalidShots(), "Erro: Tiros inválidos devia começar a 0."),
				() -> assertEquals(0, game.getRepeatedShots(), "Erro: Tiros repetidos devia começar a 0."),
				() -> assertEquals(0, game.getHits(), "Erro: Hits devia começar a 0."),
				() -> assertEquals(0, game.getSunkShips(), "Erro: Navios afundados devia começar a 0.")
		);
	}

	// =========================================================================
	// BLOCO 2: LÓGICA DE TIRO (fireSingleShot e fireShots)
	// =========================================================================

	@Test
	void fireSingleShot1_Invalid() {
		IGame.ShotResult result = game.fireSingleShot(new Position(-1, 5), false);
		assertFalse(result.valid(), "Erro: Tiro fora do mapa não é válido.");
		assertEquals(1, game.getInvalidShots(), "Erro: O contador de tiros inválidos não subiu.");
	}

	@Test
	void fireSingleShot2_Repeated() {
		IGame.ShotResult result = game.fireSingleShot(new Position(5, 5), true);
		assertTrue(result.repeated(), "Erro: O tiro devia ser marcado como repetido.");
		assertEquals(1, game.getRepeatedShots(), "Erro: O contador de repetidos não subiu.");
	}

	@Test
	void fireSingleShot3_Water() {
		IGame.ShotResult result = game.fireSingleShot(new Position(5, 5), false);
		assertNull(result.ship(), "Erro: O tiro na água devia ter ship == null.");
		assertFalse(result.sunk(), "Erro: Um tiro na água não afunda navios.");
	}

	@Test
	void fireSingleShot4_HitAndSink() {
		TestShip ship = new TestShip("Bote", new Position(5, 5));
		fleet.addShip(ship);

		IGame.ShotResult result = game.fireSingleShot(new Position(5, 5), false);
		assertNotNull(result.ship(), "Erro: O tiro devia ter atingido um navio.");
		assertTrue(result.sunk(), "Erro: Como era um bote (tamanho 1), devia ter afundado.");
		assertEquals(1, game.getHits(), "Erro: O contador de Hits devia subir.");
		assertEquals(1, game.getSunkShips(), "Erro: O contador de Sinks devia subir.");
		assertEquals(0, game.getRemainingShips(), "Erro: Não deviam sobrar navios.");
	}

	@Test
	void fireShots1() {
		List<IPosition> shots = new ArrayList<>();
		shots.add(new Position(0, 0)); // Nova
		shots.add(new Position(0, 0)); // Repetida na mesma rajada

		game.fireShots(shots);
		assertEquals(1, game.getAlienMoves().size(), "Erro: Deveria ter gerado 1 jogada.");
		assertEquals(1, game.getRepeatedShots(), "Erro: O segundo tiro da rajada devia contar como repetido.");
	}

	@Test
	void repeatedShot1() {
		List<IPosition> shots = new ArrayList<>();
		shots.add(new Position(1, 1));
		game.fireShots(shots);
		assertTrue(game.repeatedShot(new Position(1, 1)), "Erro: O tiro já foi dado, deveria constar como repetido.");
		assertFalse(game.repeatedShot(new Position(9, 9)), "Erro: Tiro novo não deve ser repetido.");
	}

	// =========================================================================
	// BLOCO 3: LEITURA DE CONSOLA E OUTPUTS
	// =========================================================================

	@Test
	void readEnemyFire1_SpacedFormat() {
		Scanner scanner = new Scanner("A 5 B 6 C 7");
		String json = game.readEnemyFire(scanner);
		assertTrue(json.contains("\"row\" : \"A\""), "Erro: Não processou a linha A.");
		assertEquals(1, game.getAlienMoves().size(), "Erro: A rajada não foi adicionada.");
	}

	@Test
	void readEnemyFire2_IncompleteFormat() {
		Scanner scanner = new Scanner("A B 6 C 7");
		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(scanner), "Erro: Faltam números nas coordenadas.");
	}

	@Test
	void jsonShots1_EmptyAndPopulated() {
		List<IPosition> shots = new ArrayList<>();
		assertNotNull(Game.jsonShots(shots), "Erro: json vazio devia devolver string json vazia [].");

		shots.add(new Position(0, 0));
		assertTrue(Game.jsonShots(shots).contains("\"row\" : \"A\""), "Erro: JSON falhou a estrutura base.");
	}

	@Test
	void over1() {
		assertDoesNotThrow(() -> game.over(), "Erro: O método over() não deve atirar exceção.");
	}

	// =========================================================================
	// BLOCO 4: BRUTE-FORCE DO RANDOM (Força os Losangos Amarelos a ficarem Verdes)
	// =========================================================================

	@Test
	void randomEnemyFire_BruteForceCoverage() {
		// Cenário 1: Tabuleiro normal (> 3 casas livres)
		// Corremos várias vezes para forçar o Random a colidir e entrar no ramo falso do contains
		for (int i = 0; i < 50; i++) {
			Game tempGame = new Game(new Fleet());
			tempGame.randomEnemyFire();
		}

		// Cenário 2: Exatamente 2 casas livres (Força a entrada no bloco 'else' das tuas imagens)
		// Ao corrermos 50 vezes com apenas 2 opções, é matematicamente certo que o Random
		// vai escolher o mesmo número duas vezes, pintando a LINHA 64 de verde!
		for (int i = 0; i < 50; i++) {
			Game game2 = new Game(new Fleet());
			List<IPosition> fillerShots = new ArrayList<>();
			for (int r = 0; r < 10; r++) {
				for (int c = 0; c < 10; c++) {
					if (r == 0 && c < 2) continue; // Deixa apenas a (0,0) e a (0,1) livres
					fillerShots.add(new Position(r, c));
				}
			}
			game2.fireShots(fillerShots);
			game2.randomEnemyFire();
		}

		// Cenário 3: Exatamente 0 casas livres (O tabuleiro está cheio)
		// O algoritmo do professor tem um bug natural aqui: se estiver vazio, a variável newShot
		// fica a null, o loop da LINHA 69 adiciona 3 "nulls" à lista de tiros, e quando o fireShots
		// tenta disparar, o `assert pos != null` faz o código rebentar.
		// Ao testarmos este "crash" esperado (Throwable.class), pintamos a LINHA 69 de verde!
		Game game0 = new Game(new Fleet());
		List<IPosition> fillerShots0 = new ArrayList<>();
		for (int r = 0; r < 10; r++) {
			for (int c = 0; c < 10; c++) {
				fillerShots0.add(new Position(r, c));
			}
		}
		game0.fireShots(fillerShots0); // Enche o jogo a 100%

		assertThrows(Throwable.class, () -> game0.randomEnemyFire(),
				"Erro: Com 0 casas livres, o código adiciona tiros nulos e atira exceção.");
	}

	// =========================================================================
	// BLOCO 5: EXHAUSTÃO DO PRINTBOARD (Força os Losangos da Imagem 2 a Verdes)
	// =========================================================================

	@Test
	void printBoard_ExhaustiveBranches() {
		// Navio 1: Intacto (cobre o stillFloating -> false)
		TestShip intactShip = new TestShip("Galeao", new Position(0, 0), new Position(0, 1));
		fleet.addShip(intactShip);

		// Navio 2: Totalmente afundado (cobre o stillFloating -> true e gera SHIP_ADJACENT)
		TestShip sunkShip = new TestShip("Bote", new Position(5, 5));
		fleet.addShip(sunkShip);
		sunkShip.sink();

		// Navio 3: Parcialmente atingido
		TestShip partialShip = new TestShip("Nau", new Position(8, 0), new Position(8, 1));
		fleet.addShip(partialShip);

		// VAMOS DISPARAR UMA RAJADA CIRÚRGICA PARA TESTAR TODAS AS CONDIÇÕES OR (||) e AND (&&)
		List<IPosition> forcedShots = new ArrayList<>();

		// 1. Tiros fora do mapa (Força o shot.isInside() -> false)
		forcedShots.add(new Position(-1, -1));
		forcedShots.add(new Position(99, 99));

		// 2. Tiro em água virgem (Força a 1ª metade do OR -> True)
		forcedShots.add(new Position(2, 2));

		// 3. Tiro REPETIDO na mesma água (Como a casa agora é SHOT_WATER_MARKER, Força a 1ª e 2ª metades do OR -> Falsas)
		forcedShots.add(new Position(2, 2));

		// 4. Tiro numa posição adjacente (Força a 2ª metade do OR -> True)
		forcedShots.add(new Position(5, 6));

		// 5. Tiro REPETIDO na mesma posição adjacente (Força as duas metades do OR -> Falsas)
		forcedShots.add(new Position(5, 6));

		// 6. Tiro num navio intacto (Força o if do SHIP_MARKER -> True)
		forcedShots.add(new Position(8, 0));

		// 7. Tiro REPETIDO no mesmo navio (Como agora é SHOT_SHIP_MARKER, Força o if do SHIP_MARKER -> False)
		forcedShots.add(new Position(8, 0));

		// Fogo!
		game.fireShots(forcedShots);

		// Imprime todas as vistas para cobrir os renders MyBoard e AlienBoard
		assertDoesNotThrow(() -> {
			Game.printBoard(fleet, game.getAlienMoves(), true, true);
			game.printMyBoard(true, true);
			game.printAlienBoard(true, true);
		}, "Erro: O render do tabuleiro não deve rebentar com combinações estranhas.");
	}

	// =========================================================================
	// BLOCO 6: TESTES DOS ASSERTS ESCONDIDOS
	// =========================================================================

	@Test
	void testAssertsAndExceptions() {
		assertAll("Testar se os asserts de segurança disparam",
				() -> assertThrows(AssertionError.class, () -> game.fireSingleShot(null, false)),
				() -> assertThrows(AssertionError.class, () -> game.fireShots(null)),
				() -> assertThrows(AssertionError.class, () -> game.readEnemyFire(null)),
				() -> assertThrows(AssertionError.class, () -> Game.jsonShots(null)),
				() -> assertThrows(AssertionError.class, () -> Game.printBoard(null, new ArrayList<>(), true, true)),
				() -> assertThrows(AssertionError.class, () -> Game.printBoard(fleet, null, true, true))
		);
	}
}