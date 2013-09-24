/**
 * A class to hold the information needed for a box.
 */
public class Box {
	public int x; //x position on screen
	public int y; //y position on screen
	public int width; //the width of the box
	public float red, green, blue; //colors of the box
	public int speed, heading_x, heading_y;;

    /**
     * Generic constructor
     * @param x x coordinate
     * @param y y coordinate
     * @param width width of the box
     * @param red R in RGB
     * @param green G in RGB
     * @param blue B in RGB
     * @param speed speed of the box
     */
	public Box(int x, int y, int width, float red, float green, float blue, int speed) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.speed = speed;
		this.heading_x = 1;
		this.heading_y = 1;
	}

    /**
     * Copy constructor
     * @param b the box to be replicated
     */
    public Box(Box b){
        this.x = b.x;
        this.y = b.y;
        this.width = b.width;
        this.red = b.red;
        this.green = b.green;
        this.blue = b.blue;
        this.speed = b.speed;
        this.heading_x = 1;
        this.heading_y = 1;
    }
}

	