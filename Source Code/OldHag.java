/**
 * Write a description of class OldHag here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import greenfoot.*;
public class OldHag extends Civilian
{
    /**
     * Constructor for objects of class OldHag
     */
    
    public OldHag(int age, double speed, double initial_probability, boolean masked, boolean isPathogen, boolean vaccinated){
        super(age, speed, initial_probability, masked, isPathogen, vaccinated);
    }

    public void turnGreen(){
        setImage("oldHag H.png");
        infected = false;
        isPositive = false;
    }
        
    public void turnRed(){
        setImage("oldHag I.png");
        infected = true;
        isPositive = true;
    }
    
    public void infectMe(){
        // get the current version of the virus from the main world and set this virus to the current virus on me
        MyWorld w = (MyWorld) getWorld();
        Virus v = w.getVirus(this);
        this.virus = v;  
        getWorld().addObject(v, 0, 0);
        if (!pathogen){
            setImage("oldHag A.png");
        }
        else{
            setImage("oldHag I.png");
        }
        if (!pathogen){
            incubation_period = getRandom(600, 1800);
        }
    }
    
    public void act(){
        // Oldhag is just civilian 
        super.act();
    }
}
