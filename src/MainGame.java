import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.InputProcessor;

import java.nio.FloatBuffer;
import java.util.*;

/**
 * This class puts everything together and "plays" the game.
 *
 *  @author Halldór Örn Kristjánsson & Ólafur Daði Jónsson
 *  @version 1.0
 */
public class MainGame implements ApplicationListener
{
	//Used to see in what state the game is
	private int state;

    //All possible states of the game
    private static final int MENU = 0;
    private static final int PLAYING = 1;
    private static final int HELP = 2;
    private static final int WON = 3;

	//minimap toggle
	private boolean mtoggle = true;

	//Variable for the Player.
	private Box player;

	//Variable for the Bunny.
	private Box bunny;

	//Variables for the main window
	private ViewPort mainPort;

	//Varables for the minimap
	private WorldWindow miniWindow;
	private ViewPort miniPort;

    //Used for on screen text
	private SpriteBatch spriteBatch;
	private BitmapFont font;

    //All the buffers for each shape to draw
	private FloatBuffer boxBuffer;
	private FloatBuffer mapBuffer;
	private FloatBuffer mazeBoxBuffer;

    //Environment dimensions
	private int env_height;
	private int env_width;

    //Rough maze dimensions
	private int map_height;
	private int map_width;

    //Varibles to control the maze
	private Maze maze;
	private Queue<Edge> edgelist;
	private Box[][] boxes;
	private List<Box> collisionboxes;

    //Used to control the jitter in keypresses
	private MyInputProcessor inputProcessor;

    //Used record time.
	private Stopwatch watch;
	private double winningtime;


    @Override
    /**
     *  The create function is called when the Application is first created.
     *  This is a good place for application initialization.
     */
	public void create()
	{
		inputProcessor = new MyInputProcessor(this); //keypress jitters
		Gdx.input.setInputProcessor(inputProcessor);

        //Set the environment and maze dimensions
		env_height = Gdx.graphics.getHeight();
		env_width = Gdx.graphics.getWidth();
		map_height = map_width = 1000;

        //initial state
		this.state = MENU;

        //initialize the game
		this.initialize();


		//Specify the color (RGB, Alpha) when the color buffers are cleared.
		Gdx.gl11.glClearColor(0f, 0f, 0.2f, 1);

		// Create vertex buffer for a Box.
		this.boxBuffer = BufferUtils.newFloatBuffer(8);
		this.boxBuffer.put(new float[] {0,0, 0,player.width, player.width,0, player.width,player.width});
		this.boxBuffer.rewind();

        //Create vertex buffer for the maze blocks
		this.mazeBoxBuffer = BufferUtils.newFloatBuffer(8);
		this.mazeBoxBuffer.put(new float[] {0,0, 0,32, 32,0, 32,32});
		this.mazeBoxBuffer.rewind();

        //Create a vertex buffer for the background of the minimap
		this.mapBuffer = BufferUtils.newFloatBuffer(8);
		this.mapBuffer.put(new float[] {0,0, map_width,0, 0,map_height, map_width,map_height});
		this.mapBuffer.rewind();

		// Specify the location of data in the vertex buffer that we will draw when
		// we call the glDrawArrays function.
		Gdx.gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, this.boxBuffer);

		// Enable vertex arrays.
		Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
	}

    /**
     * Sets up everything needed to start a new game with a new maze.
     * Resets the player positions
     * Generates a new maze
     */
	private void initialize(){

        //New spritebatch and font to draw text.
		this.spriteBatch = new SpriteBatch();
		this.font = new BitmapFont();

        //Set the size and position of the player and bunny
		int width = 12;
		player = new Box(1,1,width,0,1,1,3);
		bunny = new Box(390,450,width,1,0.6f,0.6f,2);

        //ViewPorts and WorldWindows for the main map and the minimap
		mainPort = new ViewPort(0, 0, env_width, env_height);
		miniWindow = new WorldWindow(0, map_width, 0, map_height);
		miniPort = new ViewPort(0, 0, env_width/4, env_height/4);

        //Initializes the maze
		initializemaze();

        //start the timer
		watch = new Stopwatch();

	}

    /**
     * Creates a new maze and initializes all the maze variables
     */
	private void initializemaze(){
		//resets the list of boxes
        collisionboxes = new ArrayList<Box>();

        //resets the array for the boxes and adds everybox to the collisionboxes array
		boxes = new Box[16][16];
		for (int i = 0; i < 16 ; i++){
			for (int j = 0 ; j < 16 ; j++){
				boxes[i][j] = new Box(i*64,j*64,32,0,0,0,0);
				collisionboxes.add(boxes[i][j]);
			}
		}

        //resets the maze and list of edges
		maze = new Maze(16*16);
		edgelist = (Queue<Edge>) maze.getEdges();

		for(Edge e : edgelist){
			int a = e.either(); //vertex 1
			int b = e.other(a); //vertex 2
            //find the first vertex in the box list
			Box abox = boxes[a%16][a/16];

            //if a connects to b to the side
			if (side(a,b)){
				//add a box to the positive x
				Box t = new Box(abox);
				t.x += 32;
                //add a box to the right side of a and add it to the list
				collisionboxes.add(t);
			}
			else{ //then a connects to b up
				//add a box to the positive y
				Box t = new Box(abox);
				t.y += 32;
                //add a box above a and add it to the list
				collisionboxes.add(t);
			}
		}
	}

    /**
     * Tests to see if b is the next positive integer to a
     * @param a the lower integer
     * @param b the higher integer
     * @return returns true if b is the next positive integer to a
     */
	private boolean side(int a, int b){
		 if (b-a == 1){
			return true;
		 }
		 return false;
	}

	@Override
    /**
     *  The dispose function is called when the Application is destroyed.
     *  This is a good place for save the application state if needed or to
     *  free resources.
     */
	public void dispose() {}

	@Override
    /**
     * The pause function is called when the Application is paused.
     *  An Application is paused before it is destroyed, when a user
     *  pressed the Home button on Android or an incoming call happens.
     *  On the desktop this will only be called immediately before dispose()
     *  is called.
     */
	public void pause() {}

	@Override
    /**
     * Called when the Application should render itself. This function is
     * constantly being called. Drawing and state updates should be called from
     * this function.
     *
     * Routes the process to the appropriate function depending on the game state
     */
	public void render() {
		switch (this.state) {
		case MENU: 		this.menu();
						break;

		case PLAYING: 	this.play();
						break;

		case HELP:  	this.help();
						break;

		case WON:		this.congratulations();
						break;

		}
	}

    /**
     * "Plays" the game
     */
	private void play(){
		this.display();
		this.update();
	}

    /**
     * Prints the menu on the screen
     */
	private void menu(){

		// Clear the screen.
		Gdx.gl11.glClearColor(0.3f, 0f, 0f, 1);
		Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		//Just to be sure this is the viewport we write into.
		Gdx.gl11.glViewport(mainPort.left, mainPort.bottom, mainPort.width, mainPort.height);

		// Draw the Menu text on the screen
		this.spriteBatch.begin();
		font.setColor(1, 1, 1, 1f);
		font.draw(this.spriteBatch, String.format("WELCOME TO THE MAZE !"), 240, 500);
		font.draw(this.spriteBatch, String.format("FIND THE PINK BUNNY"), 240, 450);
		font.draw(this.spriteBatch, String.format("Use the Arrow Keys to navigate the maze."), 130, 350);
		font.draw(this.spriteBatch, String.format("Press 'H' to see the help dialog in game."), 130, 300);
		font.draw(this.spriteBatch, String.format("Press 'N' to start a new game."), 130, 250);
		this.spriteBatch.end();

		// changes the state of the game and initializes a new game
        if(Gdx.input.isKeyPressed(Input.Keys.N)){
			this.state = PLAYING;
			this.initialize();
		}
	}

    /**
     * Prints the help on the screen
     */
	private void help() {
		// Clear the screen.
		Gdx.gl11.glClearColor(0f, 0.3f, 0f, 1);
		Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		//Just to be sure this is the viewport we write into.
		Gdx.gl11.glViewport(mainPort.left, mainPort.bottom, mainPort.width, mainPort.height);

		// Draw the help text on the screen
		this.spriteBatch.begin();
		font.setColor(1, 1, 1, 1f);
		font.draw(this.spriteBatch, String.format("Use the Arrow Keys to navigate the maze."), 100, 550);
		font.draw(this.spriteBatch, String.format("Press 'esc' or 'S' to continue game."), 100, 500);
		font.draw(this.spriteBatch, String.format("Press 'M' to toggle the minimap."), 100, 450);
		font.draw(this.spriteBatch, String.format("Press 'Q' to quit to menu."), 100, 400);
		this.spriteBatch.end();

		//changes the state of the game
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyPressed(Input.Keys.S)){
			this.state = PLAYING;
		}

        //changes the state of the game
		if(Gdx.input.isKeyPressed(Input.Keys.Q)){
			this.state = MENU;
		}
	}

    /**
     * Prints the congratulations screen
     */
	private void congratulations(){
		// Clear the screen.
		Gdx.gl11.glClearColor(0.7f, 0.3f, 0f, 1);
		Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		//Just to be sure this is the viewport we write into.
		Gdx.gl11.glViewport(mainPort.left, mainPort.bottom, mainPort.width, mainPort.height);

		// Draw the congratulations text on the screen
		this.spriteBatch.begin();
		font.setColor(1, 1, 1, 1f);
		font.draw(this.spriteBatch, String.format("CONGRATULATIONS!"), 250, 550);
		font.draw(this.spriteBatch, String.format("You caught the pink bunny in %.0f seconds :D", winningtime), 250, 500);
		font.draw(this.spriteBatch, String.format("Press Q to go to the menu and start a new game."), 250, 450);
		this.spriteBatch.end();

        //changes the state of the game
		if(Gdx.input.isKeyPressed(Input.Keys.Q)){
			this.state = MENU;
			this.initialize();
		}
	}

    /**
     * Checks if a box is outside of the maze
     * @param a the box to be tested
     * @return returns true if it is outside, false otherwise
     */
	private boolean outside(Box a){
        //counts the corners of the box that are inside the maze
		int cornsers = 0;

        //For every box in the maze
		for (Box b : collisionboxes){
            //If the lower left corner is inside this box b
			if (inside(a.x, a.y, b)){
				cornsers++;
				if (cornsers >= 4){
					return false;
				}
			}
            //If the lower right corner is inside this box b
			if (inside(a.x+a.width, a.y, b)){
				cornsers++;
				if (cornsers >= 4){
					return false;
				}
			}
            //If the upper left corner is inside this box b
			if (inside(a.x, a.y+a.width, b)){
				cornsers++;
				if (cornsers >= 4){
					return false;
				}
			}
            //If the upper right corner is inside this box b
			if (inside(a.x+a.width, a.y+a.width, b)){
				cornsers++;
				if (cornsers >= 4){
					return false;
				}
			}
		}
		return true;
	}

    /**
     * Checks if the coordinates provided are inside the given box
     * @param x the x coordinate
     * @param y the y coordinate
     * @param b the box
     * @return returns true if the coordinates are within the box
     */
	private boolean inside(int x, int y, Box b){
		if (x >= b.x && x < b.x+b.width){
			if (y >= b.y && y < b.y+b.width){
				return true;
			}
		}
		return false;
	}

    /**
     * Moves the bunny
     */
	private void updatebunny(){
		bunny.x += bunny.speed*bunny.heading_x; // move on the x axis
		if (outside(bunny)){
			bunny.heading_x *= -1; //change the heading
			bunny.x += bunny.speed*bunny.heading_x; //reverse the move;
		}
		bunny.y += bunny.speed*bunny.heading_y; // move on the y axis
		if (outside(bunny)){
			bunny.heading_y *= -1; //change the heading
			bunny.y += bunny.speed*bunny.heading_y; //reverse the move;
		}
	}

    /**
     * Moves the player
     */
	private void updateplayer(){
		if(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			player.x += player.speed; //move it on the x axis
			if (outside(player)){
				player.x -= player.speed; //rollback the keypress
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			player.x -= player.speed;
			if (outside(player)){
				player.x += player.speed; //rollback the keypress
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)){
			player.y += player.speed;
			if (outside(player)){
				player.y -= player.speed; //rollback the keypress
			}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)|| Gdx.input.isKeyPressed(Input.Keys.DOWN)){
			player.y -= player.speed;
			if (outside(player)){
				player.y += player.speed; //rollback the keypress
			}
		}
	}

    /**
     * Checks if the player has caught the bunny
     * @return returns true if the player has caught the bunny
     */
	private boolean victory(){
		if (player.x + player.width > bunny.x &&  player.x < bunny.x + bunny.width){ //if on the x axis, player is inside bunny
			if (player.y + player.width > bunny.y &&  player.y < bunny.y + bunny.width){ // if on the y axis, player is inside bunny
				winningtime = watch.elapsedTime(); //save the time it took.
				return true; //some point of player is inside bunny and therefore we have a collision between them.
			}
		}
		return false; //no collision
	}

    /**
     * Updated the player and bunny
     */
	private void update(){
		updateplayer();
		updatebunny();

        //changes the state of the game
		if (victory()){
			this.state = WON;
		}

        //changes the state of the game
		if (Gdx.input.isKeyPressed(Input.Keys.H)){
			this.state = HELP;
		}

        //changes the state of the game
		if (Gdx.input.isKeyPressed(Input.Keys.Q)){
			this.state = MENU;
		}
	}

    /**
     * Prints the "game" onto the screen
     */
	private void display(){
		//set up our projection Matrix
		Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
		Gdx.gl11.glLoadIdentity();

        //set the camera
		Gdx.gl11.glViewport(mainPort.left, mainPort.bottom, mainPort.width, mainPort.height);
		Gdx.glu.gluOrtho2D(Gdx.gl10, player.x-100, player.x+100, player.y-100, player.y+100);

        //clear the screen
		Gdx.gl11.glClearColor(0f, 0f, 0.5f, 1.0f); //background color
		Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT); //clear background

        drawScene();

		//show the minimap if that is requested.
		if (this.mtoggle){
            //set up out projection matrix
			Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
			Gdx.gl11.glLoadIdentity();

            //set the camera
			Gdx.gl11.glViewport(miniPort.left, miniPort.bottom, miniPort.width, miniPort.height);
			Gdx.glu.gluOrtho2D(Gdx.gl10, miniWindow.left, miniWindow.right, miniWindow.bottom, miniWindow.top);

			drawminimapframe();
			drawScene();
		}
	}

    /**
     * Draws everything needed on the screen
     */
	private void drawScene(){
		drawmaze();
		displaybox(player,boxBuffer);
		displaybox(bunny,boxBuffer);
	}

    /**
     * Draws the maze
     */
	private void drawmaze(){
		for (Box b : collisionboxes){
			displaybox(b, mazeBoxBuffer);
		}
	}

    /**
     * Displays a single box
     * @param b the box
     * @param buffer the FloatBuffer used to draw the box
     */
	private void displaybox(Box b, FloatBuffer buffer){
		Gdx.gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, buffer);

		Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);
		Gdx.gl11.glLoadIdentity();
		Gdx.gl11.glColor4f(b.red, b.green, b.blue, 1f);

		Gdx.gl11.glPushMatrix();
		Gdx.gl11.glTranslatef(b.x, b.y, 0);
		Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		Gdx.gl11.glPopMatrix();
	}

    /**
     * Draws the background for the minimap
     */
	private void drawminimapframe(){
		//draw the background of the map
		Gdx.gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, this.mapBuffer);

		Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);
		Gdx.gl11.glLoadIdentity();
		Gdx.gl11.glColor4f(0f, 0f, 0.7f, 1f); //red

		Gdx.gl11.glPushMatrix();
		Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		Gdx.gl11.glPopMatrix();
	}

	@Override
    /**
     *  Called when the Application is resized. This can happen at any point
     *  during a non-paused state but will never happen before a call to create().
     *
     *  Resets all the appropriate variables to be able to resize the views as they happen
     */
	public void resize(int width, int height) {
        env_height = height;
        env_width = width;
        mainPort.height = height;
        mainPort.width = width;
        miniPort.height = height/4;
        miniPort.width = width/4;

	}

	@Override
	public void resume() {}

    /**
     * Toggles the minimap on or off
     */
	public void ToggleMinimap()
	{
		if(this.state == PLAYING) //only works when playing the game.
		{
			if (this.mtoggle) this.mtoggle = false;
			else this.mtoggle = true;
		}
 	}

}
