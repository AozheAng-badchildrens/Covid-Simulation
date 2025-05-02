/**
 * Write a description of class Student here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import greenfoot.*;
public class Student extends Civilian
{

    /**
     * Constructor for objects of class Student
     */
    private boolean schoolTime; // is school time or not
    private boolean isAtSchool; 
    private int seat, sitX, sitY, rotation; // seat information
    
    public Student(int age, double speed, double initial_probability, boolean masked, boolean isPathogen, boolean vaccinated, int seat){
        super(age, speed, initial_probability, masked, isPathogen, vaccinated);
        this.seat = seat;
        // student spawns inside school
        schoolTime = true;
        isAtSchool = true;
        sitX = 0;
        sitY = 0;
    }
    
    public void infectMe(){
        // get the current version of the virus from the main world and set this virus to the current virus on me
        MyWorld w = (MyWorld) getWorld();
        Virus v = w.getVirus(this);
        this.virus = v;  
        getWorld().addObject(v, 0, 0);
        if (!pathogen){
            setImage("child A.png");
        }
        else{
            setImage("child I.png");
        }
        if (!pathogen){
            incubation_period = getRandom(600, 1800);
        }
    }
    
    public void getMySeat(){
        sitX = school_tables[seat].getX();
        sitY = school_tables[seat].getY();
        rotation = school_tables[seat].getRotation();
    }
    
    public void turnRed(){
        setImage("child I.png");
        infected = true;
        isPositive = true;
    }
    
    public void turnGreen(){
        setImage("child H.png");
        infected = false;
        isPositive = false;
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
        // initialize seat location
        if (sitX == 0 && sitY == 0){
            getMySeat();
            setLocation(sitX, sitY);
        }
        // if school time
        if (schoolTime){
            basicChecks();
            // if infected immediately go to hospital
            if (infected && !goHospital){
                goHospital = true;
            }
            // if going to hospital set some variables
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
            // if not at restaurant
            if (!isAtRestaurant){
                // if at school
                if (isAtSchool){
                    // not at my seat then go to my seat
                    if (!isAtPoint(sitX, sitY) && targetX != sitX && targetY != sitY){
                        targetX = sitX;
                        targetY = sitY;
                        isAlongX = false;
                        isAlongY = false;
                    }
                    else if (isAtPoint(sitX, sitY)){
                        setRotation(rotation);
                    }
                    goToDestination();
                }
                // not at school
                else{
                    // go to the front door of school and enter
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
            // at restaurant
            else{
                // go to front door of restaurant
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
            // if inside school then leave
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
            // not inside school adapt the civilian act method
            else{
                super.act();
            }
        }
    }
}
