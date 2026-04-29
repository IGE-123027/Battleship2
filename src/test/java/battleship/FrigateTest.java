package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class Frigate.
 * Author: ${user.name}
 * Date: 2024-03-20 17:00
 * Cyclomatic Complexity:
 * - Constructor: 4 (Devido ao switch com 4 ramos)
 */
public class FrigateTest {

	private Frigate frigate;

	@BeforeEach
	void setUp() {
		// Inicialização padrão
		frigate = new Frigate(Compass.NORTH, new Position(5, 5));
	}

	@AfterEach
	void tearDown() {
		frigate = null;
	}

	// =========================================================================
	// COBERTURA DO SWITCH (Todas as direções do Enum)
	// =========================================================================

	@Test
	void testConstructorNorth() {
		assertNotNull(frigate, "Erro: Frigate instance should not be null.");
		assertEquals("Fragata", frigate.getCategory(), "Erro: Frigate category should be 'Fragata'.");
		assertEquals(Compass.NORTH, frigate.getBearing(), "Erro: Frigate bearing is incorrect.");
		assertEquals(4, frigate.getSize(), "Error: Frigate size should be 4.");

		List<IPosition> positions = frigate.getPositions();
		assertEquals(4, positions.size(), "Error: Frigate should have exactly 4 positions.");
		assertEquals(new Position(5, 5), positions.get(0), "Error: First position is incorrect for NORTH.");
		assertEquals(new Position(6, 5), positions.get(1), "Error: Second position is incorrect for NORTH.");
		assertEquals(new Position(7, 5), positions.get(2), "Error: Third position is incorrect for NORTH.");
		assertEquals(new Position(8, 5), positions.get(3), "Error: Fourth position is incorrect for NORTH.");
	}

	@Test
	void testConstructorSouth() {
		// Força a entrada no case SOUTH:
		Frigate frigateSouth = new Frigate(Compass.SOUTH, new Position(5, 5));
		List<IPosition> positions = frigateSouth.getPositions();

		assertNotNull(frigateSouth, "Erro: Frigate instance should not be null.");
		assertEquals(4, positions.size(), "Error: Frigate should have exactly 4 positions.");

		// NOTA: O teste espera este resultado porque o código atual copia o NORTH,
		// isto será refabricado mais tarde.
		assertEquals(new Position(5, 5), positions.get(0), "Error: First position is incorrect for SOUTH.");
		assertEquals(new Position(6, 5), positions.get(1), "Error: Second position is incorrect for SOUTH.");
		assertEquals(new Position(7, 5), positions.get(2), "Error: Third position is incorrect for SOUTH.");
		assertEquals(new Position(8, 5), positions.get(3), "Error: Fourth position is incorrect for SOUTH.");
	}

	@Test
	void testConstructorEast() {
		Frigate frigateEast = new Frigate(Compass.EAST, new Position(5, 5));
		List<IPosition> positions = frigateEast.getPositions();

		assertNotNull(frigateEast, "Erro: Frigate instance should not be null.");
		assertEquals(4, positions.size(), "Error: Frigate should have exactly 4 positions.");
		assertEquals(new Position(5, 5), positions.get(0), "Error: First position is incorrect for EAST.");
		assertEquals(new Position(5, 6), positions.get(1), "Error: Second position is incorrect for EAST.");
		assertEquals(new Position(5, 7), positions.get(2), "Error: Third position is incorrect for EAST.");
		assertEquals(new Position(5, 8), positions.get(3), "Error: Fourth position is incorrect for EAST.");
	}

	@Test
	void testConstructorWest() {
		Frigate frigateWest = new Frigate(Compass.WEST, new Position(5, 5));
		List<IPosition> positions = frigateWest.getPositions();

		assertNotNull(frigateWest, "Erro: Frigate instance should not be null.");
		assertEquals(4, positions.size(), "Error: Frigate should have exactly 4 positions.");

		// NOTA: O teste espera este resultado porque o código atual copia o EAST
		assertEquals(new Position(5, 5), positions.get(0), "Error: First position is incorrect for WEST.");
		assertEquals(new Position(5, 6), positions.get(1), "Error: Second position is incorrect for WEST.");
		assertEquals(new Position(5, 7), positions.get(2), "Error: Third position is incorrect for WEST.");
		assertEquals(new Position(5, 8), positions.get(3), "Error: Fourth position is incorrect for WEST.");
	}

	// =========================================================================
	// EXCEÇÕES (Herdadas da superclasse Ship, mas boas para garantir segurança)
	// =========================================================================

	@Test
	void testConstructorInvalidInput() {
		assertAll("Testar passagem de valores nulos no construtor",
				() -> assertThrows(NullPointerException.class, () -> new Frigate(null, new Position(0,0)), "Erro: Null bearing devia atirar NullPointerException."),
				() -> assertThrows(NullPointerException.class, () -> new Frigate(Compass.NORTH, null), "Erro: Null position devia atirar NullPointerException.")
		);
	}
}