package battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class Tasks.
 * Author: ${user.name}
 * Date: 2024-03-20 22:00
 * Cyclomatic Complexity: Cobertura máxima segura contra desalinhamentos.
 */
public class TasksTest {

    private final InputStream systemInOriginal = System.in;

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {
        System.setIn(systemInOriginal);
    }

    private void simularInputConsola(String dados) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(dados.getBytes());
        System.setIn(testIn);
    }

    // =========================================================================
    // BLOCO 1: TESTES ISOLADOS (100% Seguros)
    // =========================================================================

    @Test
    void testMenuHelp() {
        assertDoesNotThrow(() -> Tasks.menuHelp(), "O menu de ajuda não deve lançar exceções.");
    }

    @Test
    void testReadPosition() {
        Scanner scanner = new Scanner("5 7");
        Position pos = Tasks.readPosition(scanner);
        assertEquals(5, pos.getRow());
        assertEquals(7, pos.getColumn());
    }

    @Test
    void testReadShip() {
        Scanner scanner = new Scanner("barca 5 5 N");
        Ship ship = Tasks.readShip(scanner);
        assertNotNull(ship);
        assertEquals("Barca", ship.getCategory());
        assertEquals(Compass.NORTH, ship.getBearing());
    }

    @Test
    void testReadShip_Unknown() {
        Scanner scanner = new Scanner("lixo 9 9 N");
        Ship ship = Tasks.readShip(scanner);
        assertNull(ship, "Navio inválido deveria retornar null.");
    }

    @Test
    void testReadClassicPosition_AllFormats() {
        assertAll("Testar formatos válidos",
                () -> assertEquals(0, Tasks.readClassicPosition(new Scanner("A5")).getRow()),
                () -> assertEquals(4, Tasks.readClassicPosition(new Scanner("A5")).getColumn()),
                () -> assertEquals(1, Tasks.readClassicPosition(new Scanner("B 6")).getRow()),
                () -> assertEquals(5, Tasks.readClassicPosition(new Scanner("B 6")).getColumn())
        );

        assertAll("Testar formatos inválidos para cobrir as exceções",
                () -> assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(new Scanner(""))),
                () -> assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(new Scanner("A B"))),
                () -> assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(new Scanner("5 5"))),
                () -> assertThrows(IllegalArgumentException.class, () -> Tasks.readClassicPosition(new Scanner("AB 5")))
        );
    }

    // =========================================================================
    // BLOCO 2: CONSTRUÇÃO DE FROTA (Com buffet infinito)
    // =========================================================================

    @Test
    void testBuildFleet_Exhaustive() {
        StringBuilder in = new StringBuilder();

        // 1. Força a entrada no "Navio desconhecido!" (Imagem 2)
        in.append("lixo 9 9 N\n");

        // 2. Adiciona o primeiro válido
        in.append("barca 0 0 N\n");

        // 3. Força a "Falha na criacao" por colisão (Imagem 2)
        in.append("barca 0 0 N\n");

        // 4. Injetamos 100 navios! Assim garantimos que o Scanner
        // NUNCA fica vazio, independentemente do FLEET_SIZE do teu projeto.
        for(int r = 0; r < 10; r++) {
            for(int c = 0; c < 10; c++) {
                in.append("barca ").append(r).append(" ").append(c).append(" N\n");
            }
        }

        Scanner scanner = new Scanner(in.toString());
        Fleet fleet = Tasks.buildFleet(scanner);
        assertNotNull(fleet);
        assertFalse(fleet.getShips().isEmpty());
    }

    // =========================================================================
    // BLOCO 3: O TESTE DO MENU (Controlo Absoluto de Scanner)
    // =========================================================================

    @Test
    void testMenu_ExhaustiveSafePaths() {
        StringBuilder in = new StringBuilder();

        // FASE 1: Tudo nulo (Testa as rejeições - as metades falsas dos ifs)
        // Apenas comandos simples para não sujar o Scanner.
        in.append("estado\n");
        in.append("mapa\n");
        in.append("tiros\n");
        in.append("rajada\n");
        in.append("simula\n");

        // FASE 2: Criar jogo automaticamente (Sem invocar LEFROTA)
        in.append("gerafrota\n");

        // FASE 3: Jogo a decorrer (Testa as aprovações - as metades verdadeiras)
        in.append("estado\n");
        in.append("mapa\n");
        in.append("tiros\n");
        in.append("ajuda\n");
        in.append("comando_estranho\n");

        // FASE 4: Uma rajada com o jogo já criado
        // Aqui sim, como o game != null, o comando RAJADA vai absorver
        // as coordenadas na mesma linha sem estragar o ciclo do menu!
        in.append("rajada A 1 B 2 C 3\n");

        // FASE 5: Terminar antes de afundar tudo
        in.append("desisto\n");

        simularInputConsola(in.toString());

        // Não usamos try-catch. Se falhar, é para vermos o erro de frente!
        assertDoesNotThrow(() -> Tasks.menu());
    }
}