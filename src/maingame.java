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


public class maingame implements ApplicationListener {
        // Java random util for random colors of the box.
        private Random random = new Random();

        // Class member variables for the box position.
        private float x,y;

        // Member variables for the colors of the box.
        private float r_color, g_color, b_color;

        // Member variable for the movement speed of the box.
        private float moveSpeed = 250;

        // The width/height of the box.
        private float boxWidth = 50;

        private SpriteBatch spriteBatch;
        private BitmapFont font;
        private FloatBuffer vertexBuffer;


        @Override
        //The create function is called when the Application is first created.
        // This is a good place for application initialization.
        public void create() {

            this.spriteBatch = new SpriteBatch();
            this.font = new BitmapFont();

            // Initialize the position of the box.
            this.x = this.y = 10f;

            // Get random colors for the RGB. value of the box.
            this.r_color = this.random.nextFloat();
            this.g_color    = this.random.nextFloat();
            this.b_color = this.random.nextFloat();

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
            this.display();
            this.update();
        }

        private void update(){
            // Check if the mouse has been pressed.
            if(Gdx.input.justTouched()) {
                // Update x,y coordinates of the box with respect to the location of the mouse.
                this.x = Gdx.input.getX()- (this.boxWidth/2.0f);
                this.y = (Gdx.graphics.getHeight() - Gdx.input.getY()) - (this.boxWidth/2.0f);
            }
            // Keyboard handling.
            if(Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                this.x -=(this.moveSpeed *  Gdx.graphics.getDeltaTime());
            }

            if(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                this.x +=(this.moveSpeed *  Gdx.graphics.getDeltaTime());;
            }

            if(Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)){
                this.y += (this.moveSpeed *  Gdx.graphics.getDeltaTime());
            }

            if(Gdx.input.isKeyPressed(Input.Keys.S)|| Gdx.input.isKeyPressed(Input.Keys.DOWN)){
                this.y -= (this.moveSpeed *  Gdx.graphics.getDeltaTime());
            }

            // Update the color of the box.
            this.r_color = this.r_color <= 1.0 ? this.r_color + 0.008f : 0f;
            this.b_color = this.b_color <= 1.0 ? this.b_color + 0.008f : 0f;
            this.g_color = this.g_color <= 1.0 ? this.g_color + 0.008f : 0f;
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
            font.setColor(this.r_color, this.b_color, this.b_color, 1f);
            font.draw(this.spriteBatch, String.format("Box position %.2f %.2f",this.x, this.y), 10, 20);
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