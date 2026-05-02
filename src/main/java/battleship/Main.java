package battleship;

public class Main {

	public static void printTitle() {
		System.out.println("***  Battleship  ***");
	}

	public static void setupEmptyBoard(char[][] exemplo) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				exemplo[i][j] = '~';
			}
		}
	}

	public static void main(String[] args) {
		printTitle();

		DatabaseManager.setupDatabase();

		char[][] exemplo = new char[10][10];
		setupEmptyBoard(exemplo);

		TabuleiroGrafico tg = new TabuleiroGrafico();
		tg.exibir(exemplo);

		Tasks.menu();
	}
}