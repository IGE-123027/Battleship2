package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class Move.
 * Author: ${user.name}
 * Date: 2024-03-20 13:00
 * Cyclomatic Complexity:
 * - Constructor: 1
 * - toString(): 1
 * - getNumber(): 1
 * - getShots(): 1
 * - getShotResults(): 1
 * - processEnemyFire(boolean): 25+ (Alta complexidade devido à formatação de strings e plurais)
 */
public class MoveTest {

    private Move move;
    private List<IPosition> dummyShots;
    private List<IGame.ShotResult> dummyResults;
    private IShip dummyFragata;
    private IShip dummyNau;

    /**
     * Subclasse anónima/concreta para podermos usar o nome do barco nos testes
     */
    private class DummyShip extends Ship {
        public DummyShip(String category) {
            super(category, Compass.NORTH, new Position(0, 0), 1);
        }
    }

    @BeforeEach
    void setUp() {
        dummyShots = new ArrayList<>();
        dummyResults = new ArrayList<>();
        // Adicionamos uma posição vazia apenas para a lista não ser nula
        dummyShots.add(new Position(0, 0));

        dummyFragata = new DummyShip("fragata");
        dummyNau = new DummyShip("nau");
    }

    @AfterEach
    void tearDown() {
        move = null;
        dummyShots = null;
        dummyResults = null;
    }

    // --- CONSTRUTOR E GETTERS SIMPLES ---

    @Test
    void testConstructorAndGetters() {
        move = new Move(1, dummyShots, dummyResults);

        assertAll("Testar inicialização e getters base",
                () -> assertEquals(1, move.getNumber(), "Erro: O número da jogada devia ser 1."),
                () -> assertEquals(dummyShots, move.getShots(), "Erro: A lista de tiros não corresponde."),
                () -> assertEquals(dummyResults, move.getShotResults(), "Erro: A lista de resultados não corresponde.")
        );
    }

    @Test
    void testToString() {
        move = new Move(5, dummyShots, dummyResults);
        String result = move.toString();
        assertTrue(result.contains("number=5"), "Erro: toString devia conter o número da jogada.");
        assertTrue(result.contains("shots=1"), "Erro: toString devia conter o tamanho da lista de tiros.");
    }

    // =========================================================================
    // LÓGICA CONDICIONAL: processEnemyFire()
    // Como a formatação de strings tem muitos branches (plurais, vírgulas),
    // vamos cobrir todos os cenários possíveis do output.
    // =========================================================================

    @Test
    void processEnemyFire1_VerboseFalse() {
        // Caminho 1: verbose = false. Ignora o bloco gigantesco de formatação de strings
        dummyResults.add(new IGame.ShotResult(true, false, null, false)); // 1 Miss
        move = new Move(1, dummyShots, dummyResults);

        String json = move.processEnemyFire(false);
        assertNotNull(json, "Erro: O JSON gerado não devia ser nulo.");
        assertTrue(json.contains("\"missedShots\" : 1"), "Erro: JSON devia registar 1 tiro na água.");
    }

    @Test
    void processEnemyFire2_InvalidShot() {
        // Caminho 2: Tiro inválido (!result.valid()) -> Aciona o "continue;"
        dummyResults.add(new IGame.ShotResult(false, false, null, false));
        move = new Move(1, dummyShots, dummyResults);

        String json = move.processEnemyFire(true);
        assertTrue(json.contains("\"validShots\" : 0"), "Erro: Tiro inválido não devia contar como válido.");
    }

    @Test
    void processEnemyFire3_OnlyRepeatedSingular() {
        // Caminho 3: 0 Válidos, 1 Repetido -> Aciona o branch "validShots == 0 && repeatedShots > 0" no singular
        dummyResults.add(new IGame.ShotResult(true, true, null, false));
        move = new Move(1, dummyShots, dummyResults);

        // Verifica se processou sem erros
        assertDoesNotThrow(() -> move.processEnemyFire(true), "Erro: Lançou exceção ao processar tiro repetido singular.");
    }

    @Test
    void processEnemyFire4_OnlyRepeatedPlural() {
        // Caminho 4: 0 Válidos, >1 Repetidos -> Aciona o branch "validShots == 0 && repeatedShots > 0" no plural ("tiros repetidos")
        dummyResults.add(new IGame.ShotResult(true, true, null, false));
        dummyResults.add(new IGame.ShotResult(true, true, null, false));
        move = new Move(1, dummyShots, dummyResults);

        assertDoesNotThrow(() -> move.processEnemyFire(true), "Erro: Lançou exceção ao processar múltiplos tiros repetidos.");
    }

    @Test
    void processEnemyFire5_TrailingPlusRemoval() {
        // Caminho 5: Remove o " + " final. Ocorre quando não há tiros na água, mas há navios atingidos.
        dummyResults.add(new IGame.ShotResult(true, false, dummyFragata, false)); // Atinge, não afunda, sem água
        move = new Move(1, dummyShots, dummyResults);

        String json = move.processEnemyFire(true);
        assertTrue(json.contains("\"hitsOnBoats\""), "Erro: JSON devia registar hit em barco.");
        assertTrue(json.contains("\"missedShots\" : 0"), "Erro: Não deveria haver tiros na água.");
    }

    @Test
    void processEnemyFire6_AllPlurals() {
        // Caminho 6: Forçar "s" em todas as palavras (Múltiplos válidos, múltiplos hit no mesmo barco, múltiplos sunk no mesmo barco, múltiplos misses, múltiplos repetidos)
        dummyResults.add(new IGame.ShotResult(true, false, null, false)); // Água 1
        dummyResults.add(new IGame.ShotResult(true, false, null, false)); // Água 2
        dummyResults.add(new IGame.ShotResult(true, false, dummyFragata, false)); // Hit 1 Fragata
        dummyResults.add(new IGame.ShotResult(true, false, dummyFragata, false)); // Hit 2 Fragata
        dummyResults.add(new IGame.ShotResult(true, false, dummyNau, true)); // Sunk 1 Nau
        dummyResults.add(new IGame.ShotResult(true, false, dummyNau, true)); // Sunk 2 Nau (Lógica do professor permite somar)
        dummyResults.add(new IGame.ShotResult(true, true, null, false)); // Repetido 1
        dummyResults.add(new IGame.ShotResult(true, true, null, false)); // Repetido 2

        move = new Move(1, dummyShots, dummyResults);
        String json = move.processEnemyFire(true);
        assertTrue(json.contains("\"validShots\" : 6"), "Erro: Devia contar 6 tiros válidos.");
        assertTrue(json.contains("\"repeatedShots\" : 2"), "Erro: Devia contar 2 tiros repetidos.");
    }

    @Test
    void processEnemyFire7_AllSingularsAndOutsideShots() {
        // Caminho 7: Forçar singular em todas as strings e forçar Tiros Exteriores.
        // O tiro exterior é calculado como (Game.NUMBER_SHOTS - validShots - repeatedShots).
        // Se dispararmos apenas 1 tiro na água, os restantes são classificados como "exterior".
        dummyResults.add(new IGame.ShotResult(true, false, null, false)); // 1 Água (Valid = 1)

        // Forçar a criação do número exato de tiros fora do tabuleiro para testar o 's' do "exterior(es)"
        // Como o GAME.NUMBER_SHOTS é dinâmico, confiamos que validShots = 1 e repeated = 0 vai sobrar > 0 exteriores.
        move = new Move(1, dummyShots, dummyResults);
        assertDoesNotThrow(() -> move.processEnemyFire(true), "Erro: Exceção ao testar as palavras singulares e tiros exteriores.");
    }

    @Test
    void processEnemyFire8_RepeatedWithValidShots() {
        // Caminho 8: Acionar a vírgula para os tiros repetidos -> if (validShots > 0) output.append(", "); output.append(repeatedShots...)
        dummyResults.add(new IGame.ShotResult(true, false, null, false)); // 1 Válido
        dummyResults.add(new IGame.ShotResult(true, true, null, false)); // 1 Repetido
        move = new Move(1, dummyShots, dummyResults);

        assertDoesNotThrow(() -> move.processEnemyFire(true), "Erro: Exceção ao testar vírgula nos repetidos.");
    }
}