package battleship;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;

public class TabuleiroGrafico {

    public void exibir(char[][] grelha) {
        // Proteção caso a grelha venha vazia por algum erro no jogo
        if (grelha == null || grelha.length == 0) return;

        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        try (Terminal terminal = factory.createTerminal()) {
            Screen screen = new TerminalScreen(terminal);
            screen.startScreen();
            TextGraphics tg = screen.newTextGraphics();

            // 1. Desenhar Coordenadas (Topo: Números, Lateral: Letras)
            tg.setForegroundColor(TextColor.ANSI.WHITE);
            for (int i = 0; i < grelha[0].length; i++) {
                tg.putString(i * 3 + 4, 1, String.valueOf(i));
            }
            for (int i = 0; i < grelha.length; i++) {
                tg.putString(1, i + 2, String.valueOf((char) ('A' + i)));
            }

            // 2. Desenhar o conteúdo da Grelha
            for (int row = 0; row < grelha.length; row++) {
                for (int col = 0; col < grelha[row].length; col++) {
                    char estado = grelha[row][col];

                    int x = (col * 3) + 4;
                    int y = row + 2;

                    // Lógica de cores baseada nos símbolos que o professor possa usar
                    if (estado == 'X' || estado == '*') { // Navio atingido
                        tg.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
                        tg.putString(x, y, "X");
                    } else if (estado == '~' || estado == '.' || estado == 'o') { // Água / Tiro na água
                        tg.setForegroundColor(TextColor.ANSI.BLUE);
                        tg.putString(x, y, ".");
                    } else if (estado == 'S' || estado == '#') { // Navio intacto
                        tg.setForegroundColor(TextColor.ANSI.GREEN);
                        tg.putString(x, y, "S");
                    } else { // Outros caracteres ou vazio
                        tg.setForegroundColor(TextColor.ANSI.WHITE);
                        // Se for o tal caractere nulo (0x0), desenha um espaço para não dar erro
                        tg.putString(x, y, String.valueOf(estado == '\0' ? ' ' : estado));
                    }
                }
            }

            // 3. Mostrar mensagem e esperar que o utilizador carregue numa tecla
            screen.refresh();
            tg.setForegroundColor(TextColor.ANSI.CYAN);
            tg.putString(1, grelha.length + 4, "Pressiona qualquer tecla para continuar...");
            screen.refresh();

            screen.readInput();
            screen.stopScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}