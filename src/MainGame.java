import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
		
		//This is set to true when the player is in the progress of playing a game
		private boolean ongoinggame = false;

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
            
            player = new Box(0,0,12,);

            // Initialize the position of the box.
            player.x = player.y = 10;

            
            // Enable vertex arrays.
            Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

            //Specify the color (RGB, Alpha) when the color buffers are cleared.
            Gdx.gl11.glClearColor(0.2f, 0.2f, 0.2f, 1);

            // Create vertex buffer for a box.
            this.vertexBuffer = BufferUtils.newFloatBuffer(8);
            this.vertexBuffer.put(new float[] {0,0, 0,boxWidth, boxWidth,0, boxWidth,boxWidth});
            this.vertexBuffer.rewind();

            // Specify the location of data in the vertex buffer that we will draw when
            // we call the glDrawArrays function.
            Gdx.gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, this.vertexBuffer);
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
        	
        	case PLAYING: 	this.update();
        					this.display();
        					break;
        	
        	case HELP:  	this.help();
        					break;
        	
        	case WON:		break;
        	
        	}
        }
        
        private void menu(){
        	// Clear the screen.
            Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            // Draw the Menu text on the screen
            this.spriteBatch.begin();
            font.setColor(1, 1, 1, 1f);
            font.draw(this.spriteBatch, String.format("WELCOME TO THE MAZE !"), 240, 500);
            font.draw(this.spriteBatch, String.format("FIND THE PINK BUNNY"), 240, 450);
            font.draw(this.spriteBatch, String.format("Use the Arrow Keys to navigate the maze."), 130, 350);
            font.draw(this.spriteBatch, String.format("Press 'S' to start a game."), 130, 300);
            font.draw(this.spriteBatch, String.format("Press 'N' to start a new game."), 130, 250);
            this.spriteBatch.end();

            if(Gdx.input.isKeyPressed(Input.Keys.S)){
            	this.state = PLAYING;
            	if (this.ongoinggame == false){
            		this.ongoinggame = true;
            		this.initialize();
            	}
            }
            
            if(Gdx.input.isKeyPressed(Input.Keys.N)){
            	this.state = PLAYING;
            	this.ongoinggame = true;
            	this.initialize();
            }
            
            
        }
        
        private void initialize(){
        	//TODO
        }
        
        private void help() {
        	// Clear the screen.
            Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT);

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

        private void update(){
            // Keyboard handling.
            if(Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                player.x -=(this.moveSpeed *  Gdx.graphics.getDeltaTime());
            }

            if(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                player.x +=(this.moveSpeed *  Gdx.graphics.getDeltaTime());;
            }

            if(Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)){
                player.y += (this.moveSpeed *  Gdx.graphics.getDeltaTime());
            }

            if(Gdx.input.isKeyPressed(Input.Keys.S)|| Gdx.input.isKeyPressed(Input.Keys.DOWN)){
                player.y -= (this.moveSpeed *  Gdx.graphics.getDeltaTime());
            }
            
            if (Gdx.input.isKeyPressed(Input.Keys.H)){
            	this.state = HELP;
            }
            
            if (Gdx.input.isKeyPressed(Input.Keys.Q)){
            	this.state = MENU;
            }

        }

        private void display(){
            // Clear the screen.
            Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            // Change into ModelView matrix mode. All commands hereafter will operate on
            // the ModelView matrix.
            Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);

            // Replace the ModelView matrix with the identity matrix
            Gdx.gl11.glLoadIdentity();

            // Draw some text on the screen
            this.spriteBatch.begin();
            font.setColor(1,1,1,1f);
            font.draw(this.spriteBatch, String.format("H: Help"),10,60);
            font.draw(this.spriteBatch, String.format("M: Minimap"),10,40);
            font.draw(this.spriteBatch, String.format("Q: Quit"),10,20);
            this.spriteBatch.end();

            Gdx.gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, this.vertexBuffer);

            // The color that we want to draw with (changed in update.)
            Gdx.gl11.glColor4f(this.r_color, this.b_color, this.b_color, 1f);

            // Apply translation to the modelview matrix with respect to the
            // values in x and y.
            Gdx.gl11.glTranslatef(this.x, this.y, 0);

            // Draw the box (that was defines in our vertex array in create)
            Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
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