import com.badlogic.gdx.backends.lwjgl.LwjglApplication;


public class Runner {

	public static void main(String[] args) {
		new LwjglApplication(new MainGame(), "Maze v 1.0", 800, 800, false);
	}

}