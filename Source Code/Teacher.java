/**
 * Write a description of class Teacher here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import greenfoot.*;
import java.util.ArrayList;

public class Teacher extends Civilian
{
    private boolean schoolTime; // school time or not
    private boolean isAtSchool; 
    private Block[] paths; // path locations inside school
    private int sitX, sitY; // front teaching location
    private boolean touring; // is walking around or not
    private int currentPath, pathSize, currentSize; // path variables
    /**
     * Constructor for objects of class Teacher
     */
    public Teacher(int age, double speed, double initial_probability, boolean masked, boolean isPathogen, boolean vaccinated)
    {
        super(age, speed, initial_probability, masked, isPathogen, vaccinated);
        // starts at school at inital teaching location
        schoolTime = true;
        isAtSchool = true;
        touring = false;
        currentPath = -1;
        pathSize = 0;
        currentSize = 0;
        sitX = 415;
        sitY = 546;
        // set up the paths
        paths = new Block[9];
        for (int i=0; i<9; i++){
            paths[i] = new Block(30 + 111 * (i % 3), 450 + 100 * (i / 3), -1);
        }
    }

    public void infectMe(){
        // get the current version of the virus from the main world and set this virus to the current virus on me
        MyWorld w = (MyWorld) getWorld();
        Virus v = w.getVirus(this);
        this.virus = v;  
        getWorld().addObject(v, 0, 0);
        if (!pathogen){
            setImage("teacher A.png");
        }
        else{
            setImage("teacher I.png");
        }
        if (!pathogen){
            incubation_period = getRandom(600, 1800);
        }
    }
    
    public void turnRed(){
        setImage("teacher I.png");
        infected = true;
        isPositive = true;
    }
    
    public void turnGreen(){
        setImage("teacher H.png");
        infected = false;
        isPositive = false;
    }
    
    public void getNextPath(){
        ArrayList<Integer> arr = new ArrayList<Integer>(); // array list of integers representing path indexs
        // go through each point in paths array
        for (int i=0; i<9; i++){
            // select new destination
            if (i != currentPath){
                double x = (double)paths[i].getX();
                double y = (double)paths[i].getY();
                // if destination within 200 pixels
                if (Math.sqrt((x - (double)getX()) * (x - (double)getX()) + (y - (double)getY()) * (y - (double)getY())) <= 200){
                    arr.add(i);
                }
            }
        }
        // randomly generate my next destination
        int nextPath = arr.get(getRandom(0, arr.size() - 1));
        targetX = paths[nextPath].getX();
        targetY = paths[nextPath].getY();
        currentPath = nextPath;
    }
    
    public void setSchoolTime(boolean b){
        schoolTime = b;
    }
    
    public boolean isInHospital(){
        return isAtHospital;
    }
    
    public boolean toHospital(){
        return goHospital;    
    }
    
    public void act(){
        // if is school time
        if (schoolTime){
            basicChecks();
            // infected go to hospital immediately
            if (infected && !goHospital){
                goHospital = true;
            }
            // going to hospital set some variables
            if (goHospital){
                schoolTime = false;
                targetX = 1010;
                targetY = 300;
                isAtSchool = false;
                wondering = false;
                isAtRestaurant = false;
                goRestaurant = false;
                return;
            }
            // not at restaurant
            if (!isAtRestaurant){
                // at school
                if (isAtSchool){
                    // not touring / currently teaching
                    if (!touring){
                        // go to my teaching location if not already at the point
                        if (!isAtPoint(sitX, sitY)){
                            if (targetX != sitX && targetY != sitY){
                                targetX = sitX;
                                targetY = sitY;
                                isAlongX = true;
                                isAlongY = false;
                            }
                            goToDestination();
                        }
                        // at my teaching location 
                        else{
                            setRotation(180);
                            // 1/1000 chance of touring every act
                            if (getRandom(0, 1000) == 0){
                                currentPath = 5;
                                touring = true;
                                pathSize = getRandom(4, 8);
                                currentSize = 0;
                            }
                        }
                    }
                    // currently walking around
                    else{
                        // set target destination
                        if (targetX != paths[currentPath].getX() && targetY != paths[currentPath].getY()){
                            targetX = paths[currentPath].getX();
                            targetY = paths[currentPath].getY();
                        }
                        // if not at destination point then go there
                        if (!isAtPoint(targetX, targetY)){
                            goToDestination();
                        }
                        // currently at destination point
                        if (isAtPoint(targetX, targetY)){
                            // 1/60 chance every act to move to next point
                            if (getRandom(0, 60) == 0){
                                currentSize++;
                                // if keep walking get next destination point
                                if (currentSize < pathSize){
                                    getNextPath();
                                }
                                // go back to teaching point
                                else{
                                    targetX = sitX;
                                    targetY = sitY;
                                    isAlongX = true;
                                    isAlongY = false;
                                    touring = false;
                                }
                            }
                        }
                    }
                }
                // not at school then go to front door and enter
                else{
                    if (targetX != 304 && targetY != 405){
                        targetX = 304;
                        targetY = 405;
                    }
                    goToDestination();
                    if (isAtPoint(304, 405)){
                        isAtSchool = true;
                    }
                }
            }
            // inside restaurant go to front door
            else{
                if (targetX != 869 && targetY != 429){
                    targetX = 869; 
                    targetY = 429;
                    isAlongX = true;
                    isAlongY = false;
                }
                if (isAtPoint(869, 429)){
                    isAtRestaurant = false;
                }
                else{
                    goToDestination();
                }
            }
        }
        // not school time
        else{
            touring = false; // stop walking inside school
            // inside school then go out
            if (isAtSchool){
                if (targetX != 304 && targetY != 400){
                    targetX = 304;
                    targetY = 400;
                    isAlongX = false;
                    isAlongY = false;
                }
                if (!isAtPoint(targetX, targetY)){
                    goToDestination();
                }
                else{
                    isAtSchool = false;
                    wondering = true;
                    targetX = -1;
                    targetY = -1;
                }
            }
            // outside of school adapt civilian act method
            else{
                super.act();
            }
        }
    }
}
