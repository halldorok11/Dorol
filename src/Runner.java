import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

import java.util.List;


public class Runner {

	private static List<Edge> edges;
	public static void main(String[] args) {
		new LwjglApplication(new MainGame(), "Maze v 1.0", 800, 600, false);

		Maze m = new Maze(16 * 16);

	}

}