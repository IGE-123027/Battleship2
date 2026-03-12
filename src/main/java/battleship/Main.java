/**
 * 
 */
package battleship;

/**
 * The type Main.
 *
 * @author britoeabreu
 * @author adrianolopes
 * @author miguelgoulao
 */
public class Main
{
	/**
	 * Main.
	 *
	 * @param args the args
	 */
	public static void main(String[] args) {

		char[][] exemplo = new char[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				exemplo[i][j] = '~';
			}
		}

		TabuleiroGrafico tg = new TabuleiroGrafico();
		tg.exibir(exemplo);

		Tasks.menu();
	}

}
