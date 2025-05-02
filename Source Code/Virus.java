/**
 * Write a description of class Virus here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import greenfoot.*;
import java.util.ArrayList;

public abstract class Virus extends SuperSmoothMover
{
    protected int radius; // infecting radius
    protected int version; // current version of the virus
    protected double infectionRate; // how often to infect people
    protected boolean removed; // not active anymore
    protected Person person; // current person following
    protected int timer = 1; // remove timer 
    protected boolean visible; // visibility
    /**
     * Constructor for objects of class Virus
     */
    public Virus(int version, Person p, int radius, int infectionRate, boolean visible)
    {
        this.person = p;
        this.version = version;
        this.radius = radius;
        this.infectionRate = infectionRate;
        this.removed = false;
        this.visible = visible;
        getImage().setTransparency(visible ? 255 : 0);
    }
    
    public void setRemoved(boolean b){
        removed = b;
    }
    
    public boolean isRemoved(){
        return removed;
    }
    
    public Person getPerson(){
        return person;
    }
    
    public boolean getRandom(double p){
        if (Greenfoot.getRandomNumber(1000) <= p){
            return true;
        }
        return false;
    }
    
    public void setvisible(boolean b){
        visible = b;
        getImage().setTransparency((visible ? 254 : 0));
    }
    
    public boolean isVisible(){
        return visible;
    }
    
    public void act(){
        // if not active or person died remove me
        if (removed || person.isDead()){
            timer--;
            if (timer == 0){
                getWorld().removeObject(this);
            }
            return;   
        }
        // if person is still alive then follow the person
        if (person != null && !person.isDead()){
            setLocation(person.getX(), person.getY());
        }
        // if person incubation period is over then start his death timer
        if (person.getIncubationPeriod() == 0){
            person.setDeathTimer(version);
        }
        // get all person within my infecting radius
        ArrayList<Person> persons = (ArrayList<Person>)getObjectsInRange(radius, Person.class);
        // if person exists
        if (persons != null){
            // traverse through every person
            for (Person p : persons){
                // if person exists and not infected and not dead and not immuned
                if (p != null && !p.isInfected() && !p.isDead() && !p.isImmuned()){
                    p.updateProbability(); // update their probablility 
                    // get the distance from that person 
                    double x = p.getX(), y = p.getY(); 
                    int d = (int)Math.floor(Math.sqrt((getX() - x) * (getX() - x) + (getY() - y) * (getY() - y)));
                    // try infect other person
                    if (p.tryInfecting(d, person.isMasked()) && getRandom(infectionRate)){
                        p.setInfected(true);
                        p.infectMe();
                    }
                }
            }
        }
    }
}
