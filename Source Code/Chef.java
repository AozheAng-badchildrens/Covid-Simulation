/**
 * Write a description of class Chef here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import greenfoot.*;

public class Chef extends Civilian
{

    /**
     * Constructor for objects of class Chef
     */
    
    private static Block[] stands = {new Block(1095, 490, 90), new Block(1145, 490, 90), new Block(1195, 490, 90)};
    public Chef(int age, double speed, double initial_probability, boolean masked, boolean isPathogen, boolean vaccinated){
        super(age, speed, initial_probability, masked, isPathogen, vaccinated);
        isAtRestaurant = true;
        targetX = 1095;
        targetY = 490;
    }
    
    public void infectMe(){
        // get the current version of the virus from the main world and set this virus to the current virus on me
        MyWorld w = (MyWorld) getWorld();
        Virus v = w.getVirus(this);
        this.virus = v;  
        getWorld().addObject(v, 0, 0);
        if (!pathogen){
            setImage("chef A.png");
        }
        else{
            setImage("chef I.png");
        }
        if (!pathogen){
            incubation_period = getRandom(600, 1800);
        }
    }
    
    public void turnRed(){
        setImage("chef I.png");
    }
    
    public void turnGreen(){
        setImage("chef H.png");
    }
    
    public void goToStove(){
        Block b = stands[getRandom(0, 2)];
        targetX = b.getX();
        targetY = b.getY();
    }
    
    public void act(){
        if (isAtRestaurant){
            basicChecks();
            // if go hospital then leave restaurant
            if (goHospital){
                isAtRestaurant = false;
                wondering = false;
                return;
            }
            // if is at one of the stoves selected another one and go
            if (isAtPoint(targetX, targetY)){
                setRotation(270);
                if (getRandom(0, 500) == 0){
                    goToStove();
                }
            }
            // go to the destination stove
            else{
                goToDestination();
            }
        }
        else{
            // if not go to hospital and not at hospital go back to restaurant
            if (!goHospital && !isAtHospital){
                if (targetX != 869 && targetY != 427){
                    targetX = 869;
                    targetY = 427;
                }
                if (!isAtPoint(targetX, targetY)){
                    goToDestination();
                }
                else{
                    targetX = 1095;
                    targetY = 490;
                    isAtRestaurant = true;
                }
            }
            // if not tested positive adapt civilian act method
            else if (!isPositive){
                super.act();
            }
            else if (isPositive){
                basicChecks();
            }
        }
    }
}
