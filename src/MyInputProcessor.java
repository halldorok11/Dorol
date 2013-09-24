import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import java.util.Observable;

public class MyInputProcessor implements InputProcessor {

	private MainGame game;

	public MyInputProcessor(MainGame g)
	{
		this.game = g;
	}

	@Override
	public boolean keyDown(int i) {
		return false;
	}

	@Override
	public boolean keyUp(int i) {
		if(i ==Input.Keys.M)
		{
			game.ToggleMinimap();
		}

		return false;
	}

	@Override
	public boolean keyTyped(char c) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean touchDown(int i, int i2, int i3, int i4) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean touchUp(int i, int i2, int i3, int i4) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean touchDragged(int i, int i2, int i3) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean mouseMoved(int i, int i2) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean scrolled(int i) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
