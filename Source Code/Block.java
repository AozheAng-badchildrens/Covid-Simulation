/**
 * Write a description of class Bloack here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Block  
{
    private int x, y; // location of the block
    private boolean occupied; // occupied or not
    private int rotation; // rotation to set for other person
    /**
     * Constructor for objects of class Block
     */
    public Block(int x, int y, int rotation)
    {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.occupied = false;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public boolean isOccupied(){
        return occupied;
    }
    
    public void setOccupied(boolean b){
        occupied = b;
    }
    
    public int getRotation(){
        return rotation;
    }
    
    public boolean equals(Block other){
        return other.getX() == x && other.getY() == y;
    }
}
