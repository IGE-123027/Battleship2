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
	public static void main(String[] args)
	{
		System.out.println("*** Battleship  ***");

		// Inicializa a base de dados para o requisito #6
		DatabaseManager.setupDatabase();

		Tasks.menu();
	}
}