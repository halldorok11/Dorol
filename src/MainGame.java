import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
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
		private boolean mtoggle = false;

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

        private FloatBuffer boxBuffer;
        private FloatBuffer hBuffer;
        private FloatBuffer vBuffer;
        private FloatBuffer mapBuffer;
        private FloatBuffer mazeBoxBuffer;


        private int env_height;
        private int env_width;

        private Maze maze;
        private Queue<Edge> edgelist;
        private Box[][] boxes;
        private List<Box> collisionboxes;

        @Override
        //The create function is called when the Application is first created.
        // This is a good place for application initialization.
        public void create() {

            env_height = Gdx.graphics.getHeight();
            env_width = Gdx.graphics.getWidth();
            this.state = MENU;

            this.initialize();

            
            //Specify the color (RGB, Alpha) when the color buffers are cleared.
            Gdx.gl11.glClearColor(0f, 0f, 0.2f, 1);

            // Create vertex buffer for a Box.
            this.boxBuffer = BufferUtils.newFloatBuffer(8);
            this.boxBuffer.put(new float[] {0,0, 0,player.width, player.width,0, player.width,player.width});
            this.boxBuffer.rewind();

            this.mazeBoxBuffer = BufferUtils.newFloatBuffer(8);
            this.mazeBoxBuffer.put(new float[] {0,0, 0,32, 32,0, 32,32});
            this.mazeBoxBuffer.rewind();

            this.vBuffer = BufferUtils.newFloatBuffer(8);
            this.vBuffer.put(new float[] {0,0, 10,0, 0,env_height, 10,env_height});
            this.vBuffer.rewind();

            this.hBuffer = BufferUtils.newFloatBuffer(8);
            this.hBuffer.put(new float[] {0,0, env_width,0, 0,10, env_width,10});
            this.hBuffer.rewind();

            this.mapBuffer = BufferUtils.newFloatBuffer(8);
            this.mapBuffer.put(new float[] {0,0, env_width,0, 0,env_height, env_width,env_height});
            this.mapBuffer.rewind();

            // Specify the location of data in the vertex buffer that we will draw when
            // we call the glDrawArrays function.
            Gdx.gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, this.boxBuffer);
            
         // Enable vertex arrays.
            Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        }

        
        private void initialize(){
        	this.spriteBatch = new SpriteBatch();
            this.font = new BitmapFont();
            
            int width = 12;
            player = new Box(1,1,width,0,1,1,3);
            bunny = new Box(390,450,width,1,0.6f,0.6f,2);
            
            mainWindow = new WorldWindow(0, env_width, 0, env_height);
        	mainPort = new ViewPort(0, 0, env_width, env_height);
        	
        	miniWindow = new WorldWindow(0, env_width, 0, env_height); //TODO change to accommodate the maze size

        	miniPort = new ViewPort(0, 0, env_width/4, env_height/4);

            initializemaze();

        }

        private void initializemaze(){
            collisionboxes = new ArrayList<Box>();
            boxes = new Box[16][16];
            for (int i = 0; i < 16 ; i++){
                for (int j = 0 ; j < 16 ; j++){
                    boxes[i][j] = new Box(j*64,i*64,32,0,0,0,0);
                    collisionboxes.add(boxes[i][j]);
                }
            }

            maze = new Maze(16*16);
            edgelist = (Queue<Edge>) maze.getEdges();

            for(Edge e : edgelist){
                int a = e.either();
                int b = e.other(a);
                Box abox = boxes[a%16][a/16];

                if (side(a,b)){
                    //add a box to the positive x
                    Box t = new Box(abox);
                    t.x += 32;
                    collisionboxes.add(t);
                }
                else{
                    //add a box to the positive y
                    Box t = new Box(abox);
                    t.y += 32;
                    collisionboxes.add(t);
                }
            }
        }

        private boolean side(int a, int b){
             if (b-a == 1){
                return true;
             }
             return false;
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
        	
        	case PLAYING: 	this.play();
        					break;

        	case HELP:  	this.help();
        					break;
        	
        	case WON:		this.congratulations();
        					break;
        	
        	}
        }

        private void play(){
            this.display();
            this.update();
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
            font.draw(this.spriteBatch, String.format("Press 'N' to start a new game."), 130, 250);
            this.spriteBatch.end();

            if(Gdx.input.isKeyPressed(Input.Keys.N)){
            	this.state = PLAYING;
            	this.initialize();
            }
            
            
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
        
        private void congratulations(){
        	// Clear the screen.
        	Gdx.gl11.glClearColor(0f, 0.3f, 0f, 1);
            Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            //Just to be sure this is the viewport we write into.
            Gdx.gl11.glViewport(mainPort.left, mainPort.bottom, mainPort.width, mainPort.height);

            // Draw the congratulations text on the screen
            this.spriteBatch.begin();
            font.setColor(1, 1, 1, 1f);
            font.draw(this.spriteBatch, String.format("CONGRATULATIONS!"), 250, 550);
            font.draw(this.spriteBatch, String.format("You caught the pink bunny :D"), 250, 500);
            font.draw(this.spriteBatch, String.format("Press Q to go to the menu and start a new game."), 250, 450);
            this.spriteBatch.end();
            
            if(Gdx.input.isKeyPressed(Input.Keys.Q)){
            	this.state = MENU;
            	this.initialize();
            }
        }

        private boolean outside(Box a){
           /** for (Box b : collisionboxes){
                if (inside(a,b)){
                    return false;
                }
            }   */

	        if(inside(a))
		        return false;

	        System.out.println("Outside?");
            return true;
        }
	    private boolean inside(Box a)
	    {
		    boolean bottomLeft = false, topLeft = false, bottomRight = false, topRight = false;
			for(Box b : collisionboxes)
			{
				if((a.x > b.x) && (a.y < b.y))
					bottomLeft = true;

				if((a.x > b.x) && (a.y + a.width < b.y + b.width))
					topLeft = true;

				if((a.x + a.width < b.x + b.width) && (a.y < b.y))
					bottomRight = true;

				if((a.x  + a.width < b.x + b.width) && (a.y + a.width < b.y + b.width))
					topRight = true;
			}
		    if(!bottomLeft || !topLeft || !bottomRight || !topRight)
			    return false;

		    return true;

	    }
        /*private boolean inside(Box a, int b){ //if a is inside b
            if (a.x > b.x && a.x+a.width < b.x+b.width){
                if (a.y > b.y && a.y+a.width < b.y+b.width){
                    return true;
                }
            }
            return false;
        }   */

        
        private void updatebunny(){
            /*bunny.x += bunny.speed*bunny.heading_x; // move on the x axis
            if (outside(bunny)){
                bunny.heading_x *= -1; //change the heading
                bunny.x += bunny.speed*bunny.heading_x; //reverse the move;
            }
            bunny.y += bunny.speed*bunny.heading_y; // move on the y axis
            if (outside(bunny)){
                bunny.heading_y *= -1; //change the heading
                bunny.y += bunny.speed*bunny.heading_y; //reverse the move;
            }    */
        }
        
        private void updateplayer(){
        	if(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            	player.x += player.speed; //move it on the x axis
                if (outside(player)){
                    player.x -= player.speed; //rollback the keypress
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)){ //same as above
            	player.x -= player.speed;
                if (outside(player)){
                    player.x += player.speed; //rollback the keypress
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)){ //same as above
            	player.y += player.speed;
                if (outside(player)){
                    player.y -= player.speed; //rollback the keypress
                }
            }
            if(Gdx.input.isKeyPressed(Input.Keys.S)|| Gdx.input.isKeyPressed(Input.Keys.DOWN)){ //same as above
            	player.y -= player.speed;
                if (outside(player)){
                    player.y += player.speed; //rollback the keypress
                }
            }
        }
        
        private boolean victory(){
            if (player.x + player.width > bunny.x &&  player.x < bunny.x + bunny.width){ //if on the x axis, player is inside bunny, this is true
           		if (player.y + player.width > bunny.y &&  player.y < bunny.y + bunny.width){ // if on the y axis, player is inside bunny, this is true
           			return true; //some point of player is inside bunny and therefore we have a collision between them.
           		}
           	}
           	return false; //no collision      
        }
        
        private void update(){
        	updateplayer();
        	updatebunny();
        	
        	if (victory()){
        		this.state = WON;
        	}
        	
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
	        //set up our projection Matrix
	        Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
	        Gdx.gl11.glLoadIdentity();
	        Gdx.gl11.glViewport(mainPort.left, mainPort.bottom, mainPort.width, mainPort.height);
	        Gdx.glu.gluOrtho2D(Gdx.gl10, player.x-100, player.x+100, player.y-100, player.y+100);

    		Gdx.gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, this.boxBuffer);
        	Gdx.gl11.glClearColor(0f, 0f, 0.5f, 1.0f); //background color
            Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT); //clear background
            Gdx.gl11.glColor4f(1, 1, 0, 1.0f);

            Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);
            Gdx.gl11.glLoadIdentity();

            drawScene();

            
            //show the minimap if that is requested.
            if (this.mtoggle){
	            Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
	            Gdx.gl11.glLoadIdentity();
	            Gdx.gl11.glViewport(miniPort.left, miniPort.bottom, miniPort.width, miniPort.height);
	            Gdx.glu.gluOrtho2D(Gdx.gl10, miniWindow.left, miniWindow.right, miniWindow.bottom, miniWindow.top);

            	Gdx.gl11.glColor4f(0, 1, 0, 1.0f);
                Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);
                Gdx.gl11.glLoadIdentity();
                drawminimapframe();
                drawScene();
            }
        }
        
        private void drawScene(){
            drawmaze();
            displaybox(player,boxBuffer);
        	displaybox(bunny,boxBuffer);
        }

        private void drawmaze(){
            for (Box b : collisionboxes){
                displaybox(b, mazeBoxBuffer);
            }
        }
        
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