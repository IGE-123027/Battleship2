package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class Position.
 * Author: ${user.name}
 * Date: 2024-03-20 10:00
 * Cyclomatic Complexity:
 * - randomPosition(): 1
 * - Position(char, int): 1
 * - Position(int, int): 1
 * - getRow(): 1
 * - getColumn(): 1
 * - getClassicRow(): 1
 * - getClassicColumn(): 1
 * - isInside(): 5
 * - isAdjacentTo(): 3
 * - adjacentPositions(): 3
 * - isOccupied(): 1
 * - isHit(): 1
 * - occupy(): 1
 * - shoot(): 1
 * - equals(): 5
 * - hashCode(): 1
 * - toString(): 1
 */
public class PositionTest {

	private Position position;

	@BeforeEach
	void setUp() {
		// Inicializa o objeto antes de cada teste
		position = new Position(5, 5);
	}

	@AfterEach
	void tearDown() {
		// Limpa a instância após cada teste
		position = null;
	}

	// --- CONSTRUTORES E METODOS ESTATICOS ---

	@Test
	void randomPosition1() {
		Position randPos = Position.randomPosition();
		assertNotNull(randPos, "Erro: randomPosition não deve devolver null.");
		assertTrue(randPos.isInside(), "Erro: a posição gerada aleatoriamente tem de estar dentro do tabuleiro.");
	}

	@Test
	void constructorCharInt1() {
		Position pos = new Position('C', 4);
		assertAll("Verifica a conversão de caracteres e índices",
				() -> assertEquals(2, pos.getRow(), "Erro: Linha 'C' devia ser o índice 2."),
				() -> assertEquals(3, pos.getColumn(), "Erro: Coluna 4 devia ser o índice 3."),
				() -> assertFalse(pos.isOccupied(), "Erro: Uma nova posição não deve estar ocupada."),
				() -> assertFalse(pos.isHit(), "Erro: Uma nova posição não deve estar atingida.")
		);
	}

	@Test
	void constructorIntInt1() {
		Position pos = new Position(1, 1);
		assertAll("Verifica a inicialização por índices numéricos",
				() -> assertEquals(1, pos.getRow(), "Erro: esperava linha 1."),
				() -> assertEquals(1, pos.getColumn(), "Erro: esperava coluna 1.")
		);
	}

	// --- GETTERS SIMPLES ---

	@Test
	void getRow1() {
		assertEquals(5, position.getRow(), "Erro: esperava a linha 5.");
	}

	@Test
	void getColumn1() {
		assertEquals(5, position.getColumn(), "Erro: esperava a coluna 5.");
	}

	@Test
	void getClassicRow1() {
		assertEquals('F', position.getClassicRow(), "Erro: O índice 5 deveria corresponder à linha 'F'.");
	}

	@Test
	void getClassicColumn1() {
		assertEquals(6, position.getClassicColumn(), "Erro: O índice 5 deveria corresponder à coluna 6.");
	}

	// --- LÓGICA CONDICIONAL: isInside() (CC = 5) ---

	@Test
	void isInside1() {
		// Caminho 1: Falha logo na primeira condição (row < 0)
		Position pos = new Position(-1, 5);
		assertFalse(pos.isInside(), "Erro: Posição com linha negativa devia ser false.");
	}

	@Test
	void isInside2() {
		// Caminho 2: Passa a linha, mas falha na coluna (column < 0)
		Position pos = new Position(5, -1);
		assertFalse(pos.isInside(), "Erro: Posição com coluna negativa devia ser false.");
	}

	@Test
	void isInside3() {
		// Caminho 3: Falha por passar o limite máximo da linha
		Position pos = new Position(Game.BOARD_SIZE, 5);
		assertFalse(pos.isInside(), "Erro: Linha igual ou superior a BOARD_SIZE devia ser false.");
	}

	@Test
	void isInside4() {
		// Caminho 4: Falha por passar o limite máximo da coluna
		Position pos = new Position(5, Game.BOARD_SIZE);
		assertFalse(pos.isInside(), "Erro: Coluna igual ou superior a BOARD_SIZE devia ser false.");
	}

	@Test
	void isInside5() {
		// Caminho 5: Todas as condições verdadeiras (dentro dos limites)
		Position pos = new Position(0, 0);
		assertTrue(pos.isInside(), "Erro: Posição (0,0) devia estar dentro do tabuleiro.");
	}

	// --- LÓGICA CONDICIONAL: isAdjacentTo() (CC = 3) ---

	@Test
	void isAdjacentTo1() {
		// Caminho 1: A distância na linha é superior a 1
		Position other = new Position(8, 5);
		assertFalse(position.isAdjacentTo(other), "Erro: Posições demasiado distantes na vertical não são adjacentes.");
	}

	@Test
	void isAdjacentTo2() {
		// Caminho 2: Distância da linha <= 1, mas distância da coluna > 1
		Position other = new Position(6, 8);
		assertFalse(position.isAdjacentTo(other), "Erro: Posições demasiado distantes na horizontal não são adjacentes.");
	}

	@Test
	void isAdjacentTo3() {
		// Caminho 3: Ambas as distâncias <= 1 (diagonal válida)
		Position other = new Position(4, 6);
		assertTrue(position.isAdjacentTo(other), "Erro: Posições com distância de 1 deveriam ser adjacentes.");
	}

	// --- LÓGICA CONDICIONAL: adjacentPositions() (CC = 3 devido ao for + if) ---

	@Test
	void adjacentPositions1() {
		// Caminho 1: Posição no meio (todas as 8 direções são válidas)
		List<IPosition> adj = position.adjacentPositions();
		assertEquals(8, adj.size(), "Erro: Uma posição no meio do tabuleiro devia ter 8 adjacentes válidos.");
	}

	@Test
	void adjacentPositions2() {
		// Caminho 2: Posição num canto (ex: 0,0). Só deve adicionar se o isInside() for verdadeiro.
		Position corner = new Position(0, 0);
		List<IPosition> adj = corner.adjacentPositions();
		assertEquals(3, adj.size(), "Erro: Uma posição no canto (0,0) devia ter apenas 3 posições adjacentes válidas.");
	}

	@Test
	void adjacentPositions3() {
		// Caminho 3: Posição num dos limites/bordas (ex: linha 0, meio).
		Position edge = new Position(0, 5);
		List<IPosition> adj = edge.adjacentPositions();
		assertEquals(5, adj.size(), "Erro: Uma posição encostada à borda devia ter apenas 5 posições adjacentes.");
	}

	// --- ESTADO INTERNO ---

	@Test
	void isOccupied1() {
		assertFalse(position.isOccupied(), "Erro: Por omissão, não deve estar ocupado.");
	}

	@Test
	void isHit1() {
		assertFalse(position.isHit(), "Erro: Por omissão, não deve estar atingido.");
	}

	@Test
	void occupy1() {
		position.occupy();
		assertTrue(position.isOccupied(), "Erro: Após chamar occupy(), isOccupied devia ser true.");
	}

	@Test
	void shoot1() {
		position.shoot();
		assertTrue(position.isHit(), "Erro: Após chamar shoot(), isHit devia ser true.");
	}

	// --- LÓGICA CONDICIONAL: equals() (CC = 5) ---

	@Test
	void equals1() {
		// Caminho 1: O mesmo objeto de memória (this == otherPosition)
		assertTrue(position.equals(position), "Erro: Uma posição tem de ser igual a ela própria.");
	}

	@Test
	void equals2() {
		// Caminho 2: O outro objeto é nulo ou não é instância de IPosition
		Object notAPosition = new Object();
		assertFalse(position.equals(notAPosition), "Erro: Comparar com um tipo diferente ou null devia devolver false.");
		assertFalse(position.equals(null), "Erro: Comparar com null devia devolver false.");
	}

	@Test
	void equals3() {
		// Caminho 3: É um IPosition, mas a linha é diferente
		Position other = new Position(4, 5);
		assertFalse(position.equals(other), "Erro: Posições com linhas diferentes não podem ser iguais.");
	}

	@Test
	void equals4() {
		// Caminho 4: É um IPosition, a linha é igual, mas a coluna é diferente
		Position other = new Position(5, 6);
		assertFalse(position.equals(other), "Erro: Posições com colunas diferentes não podem ser iguais.");
	}

	@Test
	void equals5() {
		// Caminho 5: É um IPosition, a linha é igual e a coluna também (Totalmente Iguais)
		Position other = new Position(5, 5);
		assertTrue(position.equals(other), "Erro: Posições com as mesmas coordenadas deviam ser iguais.");
	}

	// --- OVERRIDES GENERICOS ---

	@Test
	void hashCode1() {
		Position same = new Position(5, 5);
		assertEquals(position.hashCode(), same.hashCode(), "Erro: Posições iguais têm de ter hashcodes iguais.");
	}

	@Test
	void toString1() {
		assertEquals("F6", position.toString(), "Erro: A string formatada não corresponde ao esperado.");
	}
}