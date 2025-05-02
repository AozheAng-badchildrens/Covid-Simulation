/**
 * Write a description of class Circle here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import greenfoot.*;
public class Circle extends Actor
{
    // instance variables - replace the example below with your own
    private int x, y; // x and y location
    private int r; // radius of the circle
    private GreenfootImage image;
    private Color color; // color of the circle
    private boolean visible; // visible or not
    /**
     * Constructor for objects of class Circle
     */
    public Circle(int x, int y, int radius, Color color, boolean visible)
    {
        this.x = x;
        this.y = y;
        this.r = radius;
        this.color = color;
        this.visible = visible;
        image = new GreenfootImage(r, r);
        setImage(image);
    }
    
    private void draw(){
        image.setColor(color);
        image.drawOval(0, 0, r, r);
        image.fillOval(0, 0, r, r);
        // if not visible set transparency to 0 otherwise to 100
        image.setTransparency((visible ? 100 : 0));
        setImage(image);
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public void setvisible(boolean b){
        visible = b;
    }
    
    public void act(){
        draw();
    }
}
