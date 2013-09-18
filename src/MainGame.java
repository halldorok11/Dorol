import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainGame implements ApplicationListener {
		private static final int MENU = 0;
		private static final int PLAYING = 1;
		private static final int HELP = 2;
		private static final int WON = 3;
	
		//Used to see in what state the game is
		private int state;
		
		//minimap toggle
		private boolean mtoggle = true; 
		
		//This is set to true when the player is in the progress of playing a game
		private boolean playing = false;

        //Variable for the Player.
        private Box player;
        
        //Variable for the Bunny.
        private Box bunny;
        
        //Variables for the main window
        private WorldWindow mainWindow;
        private ViewPort mainPort;
        
        //Varables for the minimap
        private WorldWindow miniWindow;
        private ViewPort miniPort;

        private SpriteBatch spriteBatch;
        private BitmapFont font;
        private FloatBuffer vertexBuffer;


        @Override
        //The create function is called when the Application is first created.
        // This is a good place for application initialization.
        public void create() {
        	this.state = MENU;

            this.spriteBatch = new SpriteBatch();
            this.font = new BitmapFont();
            
            int width = 12;
            player = new Box(250,100,width,0,1,1,3);
            bunny = new Box(100,100,width,1,0.6f,0.6f,2);
            
            mainWindow = new WorldWindow(0, 800, 0, 800);
        	mainPort = new ViewPort(0, 0, 800, 800);
        	
        	miniWindow = new WorldWindow(0, 800, 0, 800); //TODO change to accomadate the maze size
        	miniPort = new ViewPort(0, 0, 200, 200);

            
            //Specify the color (RGB, Alpha) when the color buffers are cleared.
            Gdx.gl11.glClearColor(0f, 0f, 0.2f, 1);

            // Create vertex buffer for a Box.
            this.vertexBuffer = BufferUtils.newFloatBuffer(8);
            this.vertexBuffer.put(new float[] {0,0, 0,width, width,0, width,width});
            this.vertexBuffer.rewind();

            // Specify the location of data in the vertex buffer that we will draw when
            // we call the glDrawArrays function.
            Gdx.gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, this.vertexBuffer);
            
         // Enable vertex arrays.
            Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        }
        
        //The dispose function is called when the Application is destroyed.
        // This is a good place for save the application state if needed or to
        // free resources.
        @Override
        public void dispose() {}

        //The pause function is called when the Application is paused.
        //An Application is paused before it is destroyed, when a user
        //pressed the Home button on Android or an incoming call happens.
        //On the desktop this will only be called immediately before dispose()
        //is called.
        @Override
        public void pause() {}

        // Called when the Application should render itself. This function is
        // constantly being called. Drawing and state updates should be called from
        // this function.
        @Override
        public void render() {
        	switch (this.state) {
        	case MENU: 		this.menu();
        					break;
        	
        	case PLAYING: 	this.display();
        					this.update();
        					break;
        	
        	case HELP:  	this.help();
        					break;
        	
        	case WON:		break;
        	
        	}
        }
        
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
            font.draw(this.spriteBatch, String.format("Press 'S' to start a game."), 130, 250);
            font.draw(this.spriteBatch, String.format("Press 'N' to start a new game."), 130, 200);
            this.spriteBatch.end();

            if(Gdx.input.isKeyPressed(Input.Keys.S)){
            	this.state = PLAYING;
            	if (this.playing == false){
            		this.playing = true;
            		this.initialize();
            	}
            }
            
            if(Gdx.input.isKeyPressed(Input.Keys.N)){
            	this.state = PLAYING;
            	this.playing = true;
            	this.initialize();
            }
            
            
        }
        
        private void initialize(){
        	//TODO
        }
        
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

            if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyPressed(Input.Keys.S)){
            	this.state = PLAYING;
            }
            
            if(Gdx.input.isKeyPressed(Input.Keys.Q)){
            	this.state = MENU;
            }
        }

        
        private void updatebunny(){
            bunny.x += bunny.speed*bunny.heading_x; // move on the x axis
            bunny.y += bunny.speed*bunny.heading_y; // move on the y axis
        }
        
        private void updateplayer(){
        	if(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            	player.x += player.speed; //move it on the x axis
            }
            if(Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)){ //same as above
            	player.x -= player.speed;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)){ //same as above
            	player.y += player.speed;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.S)|| Gdx.input.isKeyPressed(Input.Keys.DOWN)){ //same as above
            	player.y -= player.speed;
            }
        }
        
        private void update(){
        	updateplayer();
        	updatebunny();
        	
        	if (Gdx.input.isKeyPressed(Input.Keys.H)){
            	this.state = HELP;
            }
            
            if (Gdx.input.isKeyPressed(Input.Keys.Q)){
            	this.state = MENU;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.M)){
            	if (this.mtoggle) this.mtoggle = false;
            	else this.mtoggle = true;
            }
        }
       

        private void display(){
    		Gdx.gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, this.vertexBuffer);
        	Gdx.gl11.glClearColor(0f, 0f, 0.5f, 1.0f); //background color
            Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT); //clear background
            Gdx.gl11.glColor4f(1, 1, 0, 1.0f);
            Gdx.gl11.glViewport(mainPort.left, mainPort.bottom, mainPort.width, mainPort.height);
            Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);
            Gdx.gl11.glLoadIdentity();
            Gdx.glu.gluOrtho2D(Gdx.gl10, player.x-75, player.x+75, player.y-75, player.y+75);
            drawScene();
            
            //show the minimap if that is requested.
            if (this.mtoggle){
            	Gdx.gl11.glColor4f(0, 1, 0, 1.0f);
            	Gdx.gl11.glViewport(miniPort.left, miniPort.bottom, miniPort.width, miniPort.height);
                Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);
                Gdx.gl11.glLoadIdentity();
                Gdx.glu.gluOrtho2D(Gdx.gl10, miniWindow.left, miniWindow.right, miniWindow.bottom, miniWindow.top);
                drawScene();     
            }
        }
        
        private void drawScene(){
        	displaybox(player);
        	displaybox(bunny);
        }
        
        private void displaybox(Box b){
        	Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);
            Gdx.gl11.glLoadIdentity();
            Gdx.gl11.glColor4f(b.red, b.green, b.blue, 1f);
            
            Gdx.gl11.glPushMatrix();
            Gdx.gl11.glTranslatef(b.x, b.y, 0);
            Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
            Gdx.gl11.glPopMatrix(); 
        }
        

        // Called when the Application is resized. This can happen at any point
        //during a non-paused state but will never happen before a call to create().
        @Override
        public void resize(int width, int height) {
            // Load the Project matrix. Next commands will be applied on that matrix.
            Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
            Gdx.gl11.glLoadIdentity();
            // Set up a two-dimensional orthographic viewing region.
            Gdx.glu.gluOrtho2D(Gdx.gl10, 0, width, 0, height);
            // Set up affine transformation of x and y from world coordinates to window coordinates
            Gdx.gl11.glViewport(0, 0, width, height);
            
        }

        @Override
        public void resume() {}

    }