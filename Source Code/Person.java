/**
 * Write a description of class Civilian here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import greenfoot.*;
import java.util.ArrayList;

public abstract class Person extends SuperSmoothMover
{
    // individual traits
    protected int age;
    protected double speed;
    protected double targetX = -1, targetY = -1; // target location x and y coordinate
    protected boolean masked;
    protected boolean vaccinated;
    protected boolean infected;
    protected boolean isPositive = false;
    // timers
    protected int immunePeriod = -1;
    protected int incubation_period = 0;
    protected int death_timer = -1;
    protected int remove_timer = 50;
    protected double initial_probability;
    protected double current_probability;
    protected boolean pathogen = false; // patient zero
    protected boolean diseased = false; // dead
    protected boolean isAlongX, isAlongY; // is walking along x-axis or y-axis
    protected boolean isAtRestaurant = false;
    protected boolean wondering = true, goPark = false, goSchool = false, goHospital = false, goRestaurant = false; // go place variables
    // hospital variables
    protected boolean isAtHospital = false, justArrivedHospital = false, waitingForTest = false, cured = false, following = false, gooutHospital = false;
    protected int goHospitalDuration = 0;
    protected int chillDuration = 0, eatDuration = 0; // Duration of their seat and eating
    protected GreenfootImage image;
    protected Virus virus = null; // current virus on me
    protected int currentRoom = -1; // current bed I am in
    // Sounds
    protected GreenfootSound death = new GreenfootSound("deathSFX.wav");
    protected GreenfootSound cough = new GreenfootSound("coughSFX.wav");
    // Array of blocks to represent benches
    protected static Block[] benches = {new Block(170, 192, -45), new Block(201, 231, -45), new Block(379, 198, -135), new Block(345, 233, -135),
        new Block(148, 65, 45), new Block(189, 27, 45), new Block(356, 35, 135), new Block(400, 79, 135)
    };
    
    // using blocks to easily code with sqaure like areas
    protected static Block[] waitingAreas; // hospital waiting areas
    protected static Block[] beds; // hospital rooms 
    protected static Block[] testingAreas; // hospital testing areas (same as waiting areas)
    protected static Block[] school_tables = {new Block(245, 481, 180), new Block(165, 510, 0), new Block(99, 534, 270), new Block(66 ,457, 90), new Block(248, 585, 180), 
        new Block(168, 622, 0), new Block(102, 642, 270), new Block(66, 566, 90)}; // school tables inside school
    protected static Block[] dining_tables = {new Block(932, 506, 0), new Block(979, 506, 180), new Block(1025, 479, 90), new Block(1054, 506, 180),
    new Block(935,623,0), new Block(988, 623, 180), new Block(960, 597, 90), new Block(1038, 623, 0), new Block(1094, 623, 180), new Block(1065, 596, 90)}; // testing tables
    
    /**
     * Constructor for objects of class Civilian
     */
    
    public Person(int age, double speed, double initial_probability, boolean masked, boolean isPathogen, boolean vaccinated){
        this.age = age;
        this.speed = speed;
        this.initial_probability = initial_probability;
        this.masked = masked;
        this.vaccinated = vaccinated;
        this.pathogen = isPathogen;
        this.infected = false;
        this.incubation_period = 0;
        // initialize these public spaces 
        waitingAreas = new Block[6];
        beds = new Block[6];
        for (int i=0; i<benches.length; i++){
            benches[i].setOccupied(false);
        }
        for (int i=0; i<dining_tables.length; i++){
            dining_tables[i].setOccupied(false);
        }
        for (int i=0; i<6; i++){
            Block t = new Block(940 + 60 * (i % 3), 135 + 40 * (i / 3) , -90);
            waitingAreas[i] = t;
        }
        for (int i=0; i<3; i++){
            Block t = new Block(795, 50 + 45 * i, 0);
            beds[i] = t;
        }
        for (int i=3; i<6; i++){
            Block t = new Block(1228, 53 + 45 * (i - 3), -180);
            beds[i] = t;
        }
        death_timer = -1;
        incubation_period = -1;
    }

    public abstract void infectMe();
    
    public abstract void turnRed();
    
    public abstract void turnGreen();
    
    public void stopped(){
        death.stop();
        cough.stop();
    }
    
    public void removeMe(){
        death.play();
        if (virus != null){
            virus.setRemoved(true);
        }
        diseased = true;
        MyWorld w = (MyWorld) getWorld();
        // Increases total world deaths
        w.addDeaths(1);
        getImage().setTransparency(0); 
        double x = (double)getX(), y = (double)getY();
        checkAllBlocks(x, y);
    }
    
    public void setDeathTimer(int version){
        death_timer = 2400 - 2 * age - (int)initial_probability - (4 - version) * 100; 
        MyWorld w = (MyWorld)getWorld();
        if (w.getVirusVersion() == 4){
            death_timer = 1000;
        }
        incubation_period = -1; 
    }
    
    public void checkAllBlocks(double x, double y){
        // find a bench I occupy and make it not occupied
        if (findBench(x, y) != -1){
            benches[findBench(x, y)].setOccupied(false);
        }
        if (findBench(targetX, targetY) != -1){ 
            benches[findBench(targetX, targetY)].setOccupied(false);
        }
        // find a waiting area I occupy and make it not occupied
        if (findWaitingArea(x, y) != -1){
            waitingAreas[findWaitingArea(x,y)].setOccupied(false);
            testingAreas[findWaitingArea(x,y)].setOccupied(false);
        }
        if (findWaitingArea(targetX, targetY) != -1){
            waitingAreas[findWaitingArea(targetX, targetY)].setOccupied(false);
            testingAreas[findWaitingArea(targetX, targetY)].setOccupied(false);
        }
        // find a bed I occupy and make it not occupied
        if (findBed(x, y) != -1){
            beds[findBed(x,y)].setOccupied(false);
        }
        if (findBed(targetX, targetY) != -1){
            beds[findBed(targetX, targetY)].setOccupied(false);
        }
        // find a dining table I occupy and make it not occupied
        if (findDiningTable(x, y) != -1){
            dining_tables[findDiningTable(x, y)].setOccupied(false);
        }
        if (findDiningTable() != -1){
            dining_tables[findDiningTable()].setOccupied(false);
        }
    }
    
    public void basicChecks(){
        // if dead setLocation to (0,0) after 60 acts
        if (diseased){
            death_timer--;
            if (death_timer < -60){
                setLocation(0, 0);
            }
            return;
        }
        // if is patient zero and not infected
        if (pathogen && !infected){
            infectMe();
            infected = true;
        }
        if (incubation_period > 0){
            incubation_period--;
        }
        // if dead call removeMe function
        if (death_timer == 0){
            removeMe();
        }
        // to keep people not flow the hospital
        if (goHospitalDuration > 0){
            goHospitalDuration--;
        }
        // if infected and not dead 
        if (death_timer > 0){
            death_timer -= 1;
            // try go to the hospital
            if (!goHospital && !isHospitalFull() && !isAtHospital && !isPositive){
                if (getRandom(Math.max(30 + (2400 - death_timer) / 30, 50)) && goHospitalDuration == 0){
                    goHospital = true;
                    wondering = false;
                    goPark = false;
                    goRestaurant = false;
                    isAtRestaurant = false;
                    checkAllBlocks((double)getX(), (double)getY());
                    getPoint(1010, 300);
                }
            }
        }
        // Immune period
        if (immunePeriod > 0){
            immunePeriod--;
            if (immunePeriod == 0){
                immunePeriod = -1;
            }
        }
        // play cough sound while they are infected
        if (infected && getRandom(0, 5000) == 0){
            cough.play();
        }
    }
    
    public void goToLeft(int dx){
        death_timer++;
        if (targetX != 901 && targetY != 60 && targetX != beds[currentRoom].getX() + dx && targetY != beds[currentRoom].getY()){
            targetX = 901;
            targetY = 60;
            isAlongX = true;
            isAlongY = false;
        }
        else if (isAtPoint(901, 60)){
            targetX = beds[currentRoom].getX() + dx;
            targetY = beds[currentRoom].getY();
            isAlongX = true;
            isAlongY = false;
        }
        goToDestination();
    }
    
    public void goToRight(int dx){
        death_timer++;
        if (targetX != 1080 && targetY != 60 && targetX != beds[currentRoom].getX() - dx && targetY != beds[currentRoom].getY()){
            targetX = 1080.0;
            targetY = 60.0;
            isAlongX = true;
            isAlongY = false;
        }
        if (isAtPoint(1080, 60)){
            targetX = beds[currentRoom].getX() - dx;
            targetY = beds[currentRoom].getY();
            isAlongX = true;
            isAlongY = false;
        }
        goToDestination();
    }
    
    public void cureMe(int room){
        incubation_period = -1;
        death_timer = -1;
        isPositive = false;
        infected = false;
        turnGreen();
        if (virus != null){
            virus.setRemoved(true);
        }
        // immuned for 800 acts
        immunePeriod = 800;
        cured = true;
        currentRoom = room;
        MyWorld w = (MyWorld) getWorld();
        // subtract active cases by 1
        w.addRecovered();
    }
    
    public boolean getRandom(double p){
        if (Greenfoot.getRandomNumber(1000) <= p){
            return true;
        }
        return false;
    }
    
    public int getRandom(int min, int max){
        return min + Greenfoot.getRandomNumber(max - min + 1);    
    }
    
    public boolean tryInfecting(int r, boolean isOtherMasked){
        double p = current_probability + (int)10/(r + 1);
        if (isOtherMasked) p /= 2;
        if (getRandom(p)){
            return true;
        }
        return false;
    }
    
    public boolean checkDiagnal(){
        double x = (double)getX(), y = (double)getY();
        double slope = (y - targetY) / (x - targetX);
        // simulate a line shooting from current position to target position
        // if the line hits a area inside the school, hospital or restaurant then return false
        if (x > targetX){
            for (int i=(int)x; i>=targetX; i--){
                y -= slope;
                if (isAtSchool(i, y) || isAtHospital(i, y) || isAtRestaurant(i, y)){
                    return false;
                }
            }
        }
        else{
            for (int i=(int)x; i<=targetX; i++){
                y += slope;
                if (isAtSchool(i, y) || isAtHospital(i, y) || isAtRestaurant(i, y)){
                    return false;
                }
            }
        }
        return true;
    }
    
    public void getPoint(){
        // generate a random point on road
        if (getRandom(0,1) == 0){
            targetY = getRandom(300, 400);
            targetX = getRandom(0, 1250);
        }
        else{
            targetX = getRandom(550, 700);
            targetY = getRandom(0, 680);
        }
        isAlongX = false;
        isAlongY = false;
        // if can walk diagonally
        if (checkDiagnal()){
            turnTowards((int)targetX, (int)targetY);
            isAlongX = false;
            isAlongY = false;
        }
        else{
            // check if go along x-axis or y-axis first without running into buildings
            if (isAtSchool(getX(), targetY) || isAtHospital(getX(), targetY) || isAtRestaurant(getX(), targetY)){
                isAlongX = true;
                isAlongY = false;
            }
            else{
                isAlongY = true;
                isAlongX = false;
            }
        }
    }
    
    public void getPoint(int x, int y){
        // same as above but set to a specific target instead of random location
        if (targetX != x && targetY != y){
            targetX = x;
            targetY = y;
            isAlongX = false;
            isAlongY = false;
        }
        if (checkDiagnal()){
            turnTowards((int)targetX, (int)targetY);
            isAlongX = false;
            isAlongY = false;
        }
        else{
            if (isAtSchool(getX(), targetY) || isAtHospital(getX(), targetY) || isAtRestaurant(getX(), targetY)){
                isAlongX = true;
                isAlongY = false;
            }
            else{
                isAlongY = true;
                isAlongX = false;
            }
        }
    }
    
    public void goToPark(){
        goToDestination(); // go to the bench
        // if is at the bench then set up sit duration
        if (Math.abs(getX() - targetX) <= 1 && Math.abs(getY() - targetY) <= 1 && chillDuration == 0){
            chillDuration = getRandom(500, 1500);
            // rotate to properly sit
            if (findBench((int)targetX, (int)targetY) != -1){
                setRotation(benches[findBench((int)targetX, (int)targetY)].getRotation());
            }
        }
        // if sit duration is over go wondering
        if (chillDuration > 0){
            chillDuration--;
            if (chillDuration == 0){
                wondering = true;
                goPark = false;
                if (findBench((int)targetX, (int)targetY) != -1){
                    benches[findBench((int)targetX, (int)targetY)].setOccupied(false);
                }
            }
        }
    }
    
    public void goToHospital(){ 
        goToDestination();
        if (isAtPoint(1010, 300)){
            targetY = 200;
            targetX = 1010;
            isAlongX = false;
            isAlongY = true;
        }
    }
    
    public void goOutFromLeft(){
        // hard coded values to simlutate path
        if (targetX != 858 && targetY != 54 && targetX != 904 && targetY != 108 && targetX != 1008 && targetY != 287){
            targetX = 858;
            targetY = 54;
            isAlongX = true;
            isAlongY = false;
        }
        else if (isAtPoint(858, 54) && targetX != 904 && targetY != 108){
            targetX = 904;
            targetY = 108;
            isAlongX = true;
            isAlongY = false;
        }
        else if (isAtPoint(904, 108)){
            targetX = 1008;
            targetY = 287;
            isAlongX = false;
            isAlongY = false;
        }
        goToDestination();
    }
    
    public void goOutFromRight(){
        // hard coded values to simlutate path
        if (targetX != 1092 && targetY != 67 && targetX != 1008 && targetY != 287){
            targetX = 1092;
            targetY = 67;
            isAlongX = true;
            isAlongY = false;
        }
        else if (isAtPoint(1092, 67)){
            targetX = 1008;
            targetY = 287;
            isAlongX = false;
            isAlongY = false;
        }
        goToDestination();
    }
    
    public Block getOpenBench(){
        for (int i=0; i<8; i++){
            if (!benches[i].isOccupied()){
                return benches[i];
            }
        }
        return new Block(-1, -1, 0);
    }
    
    public int findBench(double x, double y){
        for (int i=0; i<8; i++){
            if (Math.abs(benches[i].getX() - x) <= 1 && Math.abs(benches[i].getY() - y) <= 1){
                return i;
            }
        }
        return -1;
    }
    
    public int findBed(double x, double y){
        for (int i=0; i<6; i++){
            if (Math.abs((double)beds[i].getX() - x) <= 2 && Math.abs((double)beds[i].getY() - y) <= 2){
                return i;
            }
        }
        return -1;
    }
    
    public void getWaitingArea(){
        // go through all waiting areas and if there is free space set that as the target location
        for (int i=0; i<6; i++){
            if (!waitingAreas[i].isOccupied()){
                targetX = waitingAreas[i].getX();
                targetY = waitingAreas[i].getY();
                waitingAreas[i].setOccupied(true);
                return;
            }
        }
    }
    
    public int findWaitingArea(double x, double y){
        for (int i=0; i<6; i++){
            if (Math.abs((double)waitingAreas[i].getX() - x) <= 1 && Math.abs((double)waitingAreas[i].getY() - y) <= 1){
                return i;
            }
        }
        return -1;
    }
    
    public boolean isHospitalFull(){
        // go through each waiting area to find if there is any empty ones
        for (int i=0; i<6; i++){
            if (!waitingAreas[i].isOccupied()){
                return false;
            }
        }
        return true;
    }
    
    public boolean isRestaurantFull(){
        for (int i=0; i<dining_tables.length; i++){
            if (!dining_tables[i].isOccupied()){
                return false;
            }
        }
        return true;
    }
    
    public void getDiningTable(){
        // get a non-occupied dining table by looping through every table
        for (int i=0; i<dining_tables.length; i++){
            if (!dining_tables[i].isOccupied()){
                targetX = dining_tables[i].getX();
                targetY = dining_tables[i].getY();
                dining_tables[i].setOccupied(true);
                return;
            }
        }
    }
    
    public int findDiningTable(){
        for (int i=0; i<dining_tables.length; i++){
            if (Math.abs((double)dining_tables[i].getX() - targetX) <= 1 && Math.abs((double)dining_tables[i].getY() - targetY) <= 1){
                return i;
            }
        }
        return -1;
    }
    
    public int findDiningTable(double x, double y){
        for (int i=0; i<dining_tables.length; i++){
            if (Math.abs((double)dining_tables[i].getX() - x) <= 2 && Math.abs((double)dining_tables[i].getY() - x) <= 2){
                return i;
            }
        }
        return -1;
    }
    
    public boolean isAtPoint(double x, double y){
        if (Math.abs((double)getX() - x) <= 1 && Math.abs((double)getY() - y) <= 1){
            return true;
        }
        return false;
    }
    
    
    public boolean isAtSchool(double x, double y){
        if (0 <= x && x <= 550 && 400 <= y && y <= 700){
            return true;
        }
        return false;
    }
    
    public boolean isAtHospital(double x, double y){
        if (700 <= x && x <= 1280 && 0 <= y && y <= 300){
            return true;
        }
        return false;
    }
    
    public boolean isAtRestaurant(double x, double y){
        if (700 <= x && x <= 1280 && 400 <= y && y <= 700){
            return true;
        }
        return false;
    }
    
    public void goToDestination(){
        // if at the target point do nothing
        if (isAtPoint(targetX, targetY)){
            return;
        }
        // if does not have a target set target to current location
        if (targetX == -1 && targetY == -1){
            targetX = getX();
            targetY = getY();
        }
        // if walking diagonally turn towards target location and move
        if (!isAlongX && !isAlongY){
            turnTowards((int)targetX, (int)targetY);
            move(speed);
        }
        // if walking along x-axis
        if (isAlongX){
            // if targetX is to my right go right
            if (getX() < targetX){
                setLocation(getX() + speed, getY());
                setRotation(0);
            }
            // if targetX is to my left go left
            else{
                setLocation(getX() - speed, getY());
                setRotation(180);
            }
            // if at the targetX point then walk in the Y direction
            if (Math.abs(getX() - targetX) <= 2){
                isAlongX = false;
                isAlongY = true;
            }
        }
        // if walking in the y-axis direction
        else if (isAlongY){
            // if targetY is below me go down
            if (getY() < targetY){
                setRotation(90);
                setLocation((double)getX(), (double)getY() + speed);
            }
            // if targetY is above me go up
            else{
                setRotation(-90);
                setLocation((double)getX(), (double)getY() - speed);
            }
            // if at targetY then move along x-axis
            if (Math.abs(getY() - targetY) <= 2){
                isAlongY = false;
                isAlongX = true;
            }
        }
    }
    
    public void updateProbability(){
        current_probability = initial_probability + age / 3;
        // if wearing a mask reduce probabiliy by half
        if (masked){
            current_probability /= 2;
        }
        // if vaccinated reduce probabiliy by 4 times
        if (vaccinated){
            current_probability /= 4;   
        }
        // if at restaurant higher probability of getting infected
        if (isAtRestaurant){
            current_probability *= 1.5;
        }
    }
    
    public boolean isDead(){
        return diseased;
    }
    
    public void setInfected(boolean b){
        infected = b;
    }
    
    public void setPositive(){
        isPositive = true;
    }
    
    public double getSpeed(){
        return speed;
    }
    
    public void addDeathTimer(int t){
        death_timer += t;
    }
    
    public void setVirus(Virus v){
        this.virus = v;
    }
    
    public void setCurrentRoom(int room){
        currentRoom = room;
    }
    
    public void setMasked(boolean b){
        masked = b;
    }
    
    public boolean isMasked(){
        return masked;
    }
    
    public boolean isInfected(){
        return infected;    
    }
    
    public double getCurrent(){
        return current_probability;
    }
    
    public void goOutHospital(){
        gooutHospital = true;
    }
    
    public boolean isImmuned(){
        return immunePeriod != -1;
    }
    
    public double getIncubationPeriod(){
        return incubation_period;    
    }
    
    public void act(){
        
    }
}
