/**
 * Write a description of class Civilian here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import greenfoot.*;

public class Civilian extends Person
{
    // instance variables - replace the example below with your own

    private GreenfootImage image = getImage();
    /**
     * Constructor for objects of class Civilian
     */
    public Civilian(int age, double speed, double initial_probability, boolean masked, boolean isPathogen, boolean vaccinated){
        super(age, speed, initial_probability, masked, isPathogen, vaccinated);
    }
    
    public void infectMe(){
        // get the current version of the virus from the main world and set this virus to the current virus on me
        MyWorld w = (MyWorld) getWorld();
        Virus v = w.getVirus(this);
        this.virus = v;  
        getWorld().addObject(v, 0, 0);
        // if patient zero set image to red
        if (!pathogen){
            setImage("civilian A.png");
        }
        else{
            setImage("civilian I.png");
        }
        // if not patient zero set a incubation period
        if (!pathogen){
            incubation_period = getRandom(600, 1800);
        }
    }
    
    public void turnRed(){
        setImage("civilian I.png"); 
        infected = true;
        isPositive = true;
    }
    
    public void turnGreen(){
        setImage("civilian H.png");
        infected = false;
        isPositive = false;
    }
    
    public void act(){
        basicChecks(); 
        // if wondering outside
        if (wondering){
            // if at the target location or does not have a target
            if (Math.abs(getX() - targetX) <= 5 && Math.abs(getY() - targetY) <= 5 || targetY == -1 && targetX == -1){
                getPoint();
            }
            // go to target location
            goToDestination();
            // 1/2000 chance of going to a bench every act
            if (Greenfoot.getRandomNumber(2000) == 0){
                Block a = new Block(-1, -1, 0);
                Block b = getOpenBench(); // get an empty bench
                // if there is empty bench set that bench to occupied
                if (!b.equals(a)){
                    for (int i=0; i<8; i++){
                        if (benches[i].equals(b)){
                            benches[i].setOccupied(true);
                            break;
                        }
                    }
                    // set new target location to the bench's location
                    targetX = b.getX();
                    targetY = b.getY();
                    goPark = true;
                    wondering = false;
                }
            }
            // 1/4000 chance every act to go to a restaurant
            else if (Greenfoot.getRandomNumber(4000) == 0 && !isRestaurantFull()){
                goRestaurant = true;
                wondering = false;
            }
        }
        // if going to a restaurant
        if (goRestaurant){
            wondering = false; // not wondering
            // go to the front door of restaurant
            // values are hard coded 
            if (targetX != 869 && targetY != 405 && targetX != 870 && targetY != 454){
                targetX = 869;
                targetY = 405;
            }
            if (isAtPoint(869, 405) && targetX != 870 && targetY != 454){
                targetX = 870;
                targetY = 454;
                isAlongX = false;
                isAlongY = true;
            }
            // if arrived restaurant
            else if (isAtPoint(870, 454)){
                goRestaurant = false;
                // if restaurant is full go wondering
                if (isRestaurantFull()){
                    wondering = true;
                    targetX = -1;
                    targetY = -1;
                    isAtRestaurant = false;
                }
                // if there is an empty seat then go to that seat
                else{
                    getDiningTable();
                    masked = false;
                    isAtRestaurant = true;
                    eatDuration = getRandom(600, 1800);
                }
            }
            // go to target location
            goToDestination();
        }
        // if at the restaurant
        if (isAtRestaurant){
            wondering = false; // not wonrdering
            goToDestination();
            // if at my seat and still eating
            if (isAtPoint(targetX, targetY) && eatDuration > 0){
                // set rotation to sit properly
                if (findDiningTable() != -1){
                    setRotation(dining_tables[findDiningTable()].getRotation());
                }
                // if eating
                if (eatDuration > 0){
                    eatDuration--;
                    // if done eating
                    if (eatDuration == 0){
                        // set current table to not occupied
                        if (findDiningTable((double)getX(), (double)getY()) != -1){
                           dining_tables[findDiningTable((double)getX(), (double)getY())].setOccupied(false);
                        }
                        if (findDiningTable() != -1){
                            dining_tables[findDiningTable()].setOccupied(false);
                        }
                        // set target to door
                        targetX = 869;
                        targetY = 429;
                        isAlongX = true;
                        isAlongY = false;
                    }
                }
            }
            else{
                if (isAtPoint(869, 429)){
                    isAtRestaurant = false;
                    wondering = true;
                }
            }
        }
        // if going to park call the go park function
        if (goPark){        
            goToPark();
        }
        // if going to hospital 
        if (goHospital){
            wondering = false;
            // if is at restaurant door set target location to hospital
            if (isAtPoint(869, 397)){
                targetX = 1010;
                targetY = 300;
            }
            // if not inside restaurant
            if (!isAtRestaurant(getX(), getY())){
                // call go to hospital function
                goToHospital();
                // if inside hospital
                if (isAtPoint(1010, 200)){
                    // if hospital is full go out 
                    if (isHospitalFull()){
                        targetX = 1010;
                        targetY = 300;
                        isAlongX = false;
                        isAlongY = true;
                        wondering = true;
                        goHospital = false;
                        goHospitalDuration = 600;
                    }
                    // if hospital not full
                    else{
                        getWaitingArea(); // get a waiting area for testing
                        // set some variables
                        justArrivedHospital = true;
                        isAtHospital = true;
                        goHospital = false;
                        isAlongX = false;
                        isAlongY = false; 
                    }
                }
            }
            // if inside restaurant go out first
            else{
                if (targetX != 869 && targetY != 397){
                    targetX = 869;
                    targetY = 397;
                    isAlongX = true;
                    isAlongY = false;
                }
                goToDestination();
            }
        }
        // if is at hospital
        if (isAtHospital){
            wondering = false; // not wondering
            // just arrived and go to testing location
            if (justArrivedHospital){
                goToDestination();
                if (isAtPoint(targetX, targetY)){
                    if (findWaitingArea(targetX, targetY) != -1){
                        setRotation(waitingAreas[findWaitingArea(targetX, targetY)].getRotation());
                        waitingAreas[findWaitingArea(targetX, targetY)].setOccupied(true);
                    }
                    justArrivedHospital = false;
                    death_timer += 300;
                }
            }
            // go out hospital when there are no rooms left
            if (gooutHospital){
                if (targetX != 1008 && targetY != 283){
                    waitingAreas[findWaitingArea((int)targetX, (int)targetY)].setOccupied(false);
                    testingAreas[findWaitingArea((int)targetX, (int)targetY)].setOccupied(false);
                    targetX = 1008;
                    targetY = 283;
                    isAlongX = false;
                    isAlongY = false;
                }
                goToDestination();
                if (isAtPoint(1008, 283)){
                    wondering = true;
                    isAtHospital = false;
                }
            }
            // if recovered go out hospital
            if (cured){
                if (currentRoom <= 2){
                    goOutFromLeft();
                }
                else{
                    goOutFromRight();
                }
                if (isAtPoint(1008, 287)){
                    wondering = true;
                    cured = false;
                    isAtHospital = false;
                }
            }
        }
    }
}
