package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class Ship.
 * Author: ${user.name}
 * Date: 2024-03-20 12:00
 * Cyclomatic Complexity: (Verificado a 100% Branch Coverage)
 */
public class ShipTest {

    private Ship ship;

    /**
     * Subclasse concreta de Ship usada exclusivamente para testar a lógica da classe abstrata.
     */
    private static class ConcreteTestShip extends Ship {
        public ConcreteTestShip(String category, Compass bearing, IPosition pos, int size) {
            super(category, bearing, pos, size);
        }
    }

    @BeforeEach
    void setUp() {
        // Inicializamos o navio com tamanho 3
        ship = new ConcreteTestShip("TestShip", Compass.NORTH, new Position(5, 5), 3);

        // Posição 0: (Linha 5, Coluna 5)
        ship.positions.add(new Position(5, 5));
        // Posição 1: (Linha 4, Coluna 6)
        ship.positions.add(new Position(4, 6));
        // Posição 2: (Linha 6, Coluna 4)
        ship.positions.add(new Position(6, 4));
    }

    @AfterEach
    void tearDown() {
        ship = null;
    }

    // --- TESTES DA FACTORY: buildShip() ---

    @Test
    void buildShip1() {
        assertNotNull(Ship.buildShip("barca", Compass.NORTH, new Position(0,0)), "Erro: expected instace of Barge");
    }

    @Test
    void buildShip2() {
        assertNotNull(Ship.buildShip("caravela", Compass.NORTH, new Position(0,0)), "Erro: expected instace of Caravel");
    }

    @Test
    void buildShip3() {
        assertNotNull(Ship.buildShip("nau", Compass.NORTH, new Position(0,0)), "Erro: expected instace of Carrack");
    }

    @Test
    void buildShip4() {
        assertNotNull(Ship.buildShip("fragata", Compass.NORTH, new Position(0,0)), "Erro: expected instace of Frigate");
    }

    @Test
    void buildShip5() {
        assertNotNull(Ship.buildShip("galeao", Compass.NORTH, new Position(0,0)), "Erro: expected instace of Galleon");
    }

    @Test
    void buildShip6() {
        assertNull(Ship.buildShip("desconhecido", Compass.NORTH, new Position(0,0)), "Erro: string inválida devia devolver null");
    }

    // --- TESTE DE CONSTRUTOR E GETTERS SIMPLES ---

    @Test
    void getters() {
        assertAll("Testar Getters base",
                () -> assertEquals("TestShip", ship.getCategory(), "Erro: Categoria não corresponde"),
                () -> assertEquals(Compass.NORTH, ship.getBearing(), "Erro: Orientação não corresponde"),
                () -> assertEquals(3, ship.getSize(), "Erro: Tamanho esperado era 3"),
                () -> assertEquals(3, ship.getPositions().size(), "Erro: Lista de posições devia ter tamanho 3"),
                () -> assertTrue(ship.getPosition().equals(new Position(5,5)), "Erro: Posição âncora não corresponde")
        );
    }

    // --- TESTE DE COMPLEXIDADE: getAdjacentPositions() ---

    @Test
    void getAdjacentPositions1() {
        List<IPosition> adjacents = ship.getAdjacentPositions();
        assertNotNull(adjacents, "Erro: A lista de adjacentes não devia ser nula");
        assertFalse(adjacents.isEmpty(), "Erro: O navio deveria ter posições adjacentes no tabuleiro");

        for (IPosition shipPos : ship.getPositions()) {
            assertFalse(adjacents.contains(shipPos), "Erro: Uma parte do navio não pode ser considerada adjacente ao próprio navio.");
        }
    }

    // --- TESTE DE COMPLEXIDADE: stillFloating() ---

    @Test
    void stillFloating1_AllIntact() {
        assertTrue(ship.stillFloating(), "Erro: O navio deveria estar a flutuar intacto.");
    }

    @Test
    void stillFloating2_AllHit() {
        for(IPosition p : ship.getPositions()) {
            p.shoot();
        }
        assertFalse(ship.stillFloating(), "Erro: Navio com todas as posições atingidas não devia flutuar.");
    }

    // --- TESTE DE LIMITES: Top, Bottom, Left, Right ---

    @Test
    void getTopMostPos1() {
        assertEquals(4, ship.getTopMostPos(), "Erro: A posição mais acima devia ser a linha 4.");
    }

    @Test
    void getBottomMostPos1() {
        assertEquals(6, ship.getBottomMostPos(), "Erro: A posição mais abaixo devia ser a linha 6.");
    }

    @Test
    void getLeftMostPos1() {
        assertEquals(4, ship.getLeftMostPos(), "Erro: A posição mais à esquerda devia ser a coluna 4.");
    }

    @Test
    void getRightMostPos1() {
        assertEquals(6, ship.getRightMostPos(), "Erro: A posição mais à direita devia ser a coluna 6.");
    }

    // --- LÓGICA DE DETEÇÃO BÁSICA ---

    @Test
    void occupies2_False() {
        assertFalse(ship.occupies(new Position(9, 9)), "Erro: O navio não deveria ocupar a posição (9,9).");
    }

    @Test
    void tooCloseToPosition2_False() {
        assertFalse(ship.tooCloseTo((IPosition) new Position(9, 9)), "Erro: A posição (9,9) não devia acionar o alerta.");
    }

    @Test
    void shoot2_Miss() {
        Position target = new Position(9, 9);
        ship.shoot(target);
        assertFalse(ship.getPositions().get(0).isHit(), "Erro: Tiro na água não devia alterar o estado do navio.");
    }

    @Test
    void sink1() {
        ship.sink();
        for (IPosition pos : ship.getPositions()) {
            assertTrue(pos.isHit(), "Erro: Após chamar sink(), todas as posições devem estar atingidas.");
        }
    }

    @Test
    void toString1() {
        assertNotNull(ship.toString(), "Erro: toString não devia devolver nulo.");
    }

    // =========================================================================
    // TESTES DE EXTREMOS E BRANCHES ESCONDIDOS (PARA ATINGIR OS 100%)
    // =========================================================================

    @Test
    void testAssertsAndExceptions() {
        // Força os "Asserts" internos da classe a falharem e a entrarem no ramo de erro
        assertAll("Testar se os asserts disparam",
                () -> assertThrows(AssertionError.class, () -> Ship.buildShip(null, Compass.NORTH, new Position(0,0))),
                () -> assertThrows(AssertionError.class, () -> Ship.buildShip("nau", null, new Position(0,0))),
                () -> assertThrows(AssertionError.class, () -> Ship.buildShip("nau", Compass.NORTH, null)),
                () -> assertThrows(NullPointerException.class, () -> new ConcreteTestShip(null, Compass.NORTH, new Position(0,0), 1)),
                () -> assertThrows(NullPointerException.class, () -> new ConcreteTestShip("nau", null, new Position(0,0), 1)),
                () -> assertThrows(NullPointerException.class, () -> new ConcreteTestShip("nau", Compass.NORTH, null, 1)),
                () -> assertThrows(AssertionError.class, () -> ship.occupies(null)),
                () -> assertThrows(AssertionError.class, () -> ship.tooCloseTo((IShip) null)),
                () -> assertThrows(AssertionError.class, () -> ship.tooCloseTo((IPosition) null)),
                () -> assertThrows(AssertionError.class, () -> ship.shoot(null)),
                () -> assertThrows(AssertionError.class, () -> ship.shoot(new Position(-1, -1))) // Dispara o assert pos.isInside()
        );
    }

    @Test
    void stillFloating3_PartialHit() {
        // Branch: obriga o `if` do stillFloating a dar False no primeiro elemento e True no segundo
        ship.getPositions().get(0).shoot(); // Atinge apenas o primeiro bocado do navio
        assertTrue(ship.stillFloating(), "Erro: O navio devia flutuar com apenas um bocado atingido.");
    }

    @Test
    void occupies1_LastElement() {
        // Branch: obriga o `if` a falhar 2 vezes e a acertar na última volta do ciclo
        assertTrue(ship.occupies(new Position(6, 4)), "Erro: O navio ocupa a última posição (6,4).");
    }

    @Test
    void tooCloseToPosition1_LastElement() {
        // Branch: a posição (7,3) só é adjacente à ÚLTIMA posição do nosso navio (6,4).
        // Isto força o ciclo a ignorar os primeiros elementos e acionar o True só no final.
        assertTrue(ship.tooCloseTo((IPosition) new Position(7, 3)), "Erro: Devia detetar proximidade no último elemento.");
    }

    @Test
    void tooCloseToShip1_LastElement() {
        // Branch: O navio adversário tem uma parte muito longe, e uma parte que só toca na ponta final do nosso navio.
        Ship otherShip = new ConcreteTestShip("Adversário", Compass.NORTH, new Position(9, 9), 2);
        otherShip.positions.add(new Position(9, 9)); // Parte longe
        otherShip.positions.add(new Position(7, 3)); // Parte encostada ao nosso (6,4)
        assertTrue(ship.tooCloseTo(otherShip), "Erro: Os navios estão encostados e deviam ativar o alerta.");
    }

    @Test
    void shoot1_LastElement() {
        // Branch: O tiro falha nas 2 primeiras posições e só acerta no if da última iteração do ciclo.
        Position target = new Position(6, 4);
        ship.shoot(target);
        assertFalse(ship.getPositions().get(0).isHit(), "Erro: A posição 0 não devia ter sido atingida.");
        assertTrue(ship.getPositions().get(2).isHit(), "Erro: A posição 2 deveria estar marcada como Hit.");
    }
}