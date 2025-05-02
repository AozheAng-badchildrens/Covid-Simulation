/**
 * Write a description of class Doctor here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import greenfoot.*;
import java.util.ArrayList;

public class Doctor extends Person 
{
    private int sitX, sitY; // doctor's sitting location
    private boolean which; // left one or right one
    private boolean isFree = true, testing = false, cureing = false, leading = false;
    private int testDuration = 120;
    private int cureDuration;
    private int waitDuration = 0;
    private int dy = 25; // y offset 
    private ArrayList<Integer> patientRooms = new ArrayList<Integer>(); // all the rooms
    private Person currentPatient = null; // current patient with
    /**
     * Constructor for objects of class Doctor
     */
    
    public Doctor(int age, double speed, double initial_probability, boolean masked, boolean isPathogen, boolean vaccinated, boolean which){
        super(age, speed, initial_probability, masked, isPathogen, vaccinated);
        this.which = which;
        // set up seat location
        if (which){
            sitX = 950; 
            sitY = 48;
        }
        else{
            sitX = 1015;
            sitY = 48;
        }
        setRotation(90);
        // set testing areas
        testingAreas = new Block[6];
        for (int i=0; i<6; i++){
            Block t = new Block(940 + 60 * (i % 3), 135 + 40 * (i / 3) , -90);
            testingAreas[i] = t;
        }
    }

    public void turnRed(){
        setImage("doctor I.png");
    }
    
    public void turnGreen(){
        setImage("doctor H.png");
    }
    
    public void infectMe(){
        // get the current version of the virus from the main world and set this virus to the current virus on me
        MyWorld w = (MyWorld) getWorld();
        Virus v = w.getVirus(this);
        this.virus = v;  
        getWorld().addObject(v, 0, 0);
        setImage("doctor I.png");
        incubation_period = 0; // doctor has no incubation period
    }
    
    public boolean isAtCounter(){
        int x = getX(), y = getY();
        if (922 <= x && x <= 1040 && 30 <= y && y < 60){
            return true;
        }
        return false;
    }
    
    public boolean isAtHall(){
        int x = getX(), y = getY();
        if (883 <= x && x <= 1128 && 99 <= y && y <= 217){
            return true;
        }
        return false;    
    }
    
    public boolean isAtLeft(){
        int x = getX(), y = getY();
        if (764 <= x && x <= 882 && 27 <= y && y <= 198){
            return true;
        }
        return false;    
    }
    
    public boolean isAtRight(){
        int x = getX(), y = getY();
        if (1129 <= x && x <= 1267 && 27 <= y && y <= 198){
            return true;
        }
        return false;    
    }
    
    public void walkOutCounter(){
        if (isAtCounter()){
            targetX = 900;
            targetY = 52;
            isAlongX = false;
            isAlongY = false;
        }
        else{
            targetX = 900;
            targetY = 78;
        }
        goToDestination();
    }
    
    public boolean isPeopleWaiting(){
        // go through each waiting area 
        for (int i=0; i<6; i++){
            // if there is in a waiting area return true
            if (waitingAreas[i].isOccupied() && !testingAreas[i].isOccupied()){
                if (getWorld().getObjectsAt(waitingAreas[i].getX(), waitingAreas[i].getY(), Person.class).size() != 0){
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isEmptyRoom(){
        for (int i=0; i<6; i++){
            if (!beds[i].isOccupied()){
                return true;
            }
        }
        return false;
    }
    
    public int getNearestRoom(){
        // go through each room to find non-occupied room
        for (int i=0; i<6; i++){
            if (!beds[i].isOccupied()){
                beds[i].setOccupied(true);
                return i;
            }
        }
        return -1;
    }
    
    public void goToWaiter(){
        for (int i=0; i<6; i++){
            if (waitingAreas[i].isOccupied() && !testingAreas[i].isOccupied()){
                targetX = waitingAreas[i].getX();
                targetY = waitingAreas[i].getY() - dy;
                isAlongX = false;
                isAlongY = false;
                return;
            }
        }
    }
    
    public void goToLeft(int dx){
        // path finding
        // values are hard coded to simuluate path 
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
        // path finding
        // values are hard coded to simuluate path 
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
    
    public void goBackToCounter(){
        goToDestination();
        // if at left building go to left door
        if (isAtLeft()){
            targetX = 885;
            targetY = 60;
        }
        // if at right building go to right door
        else if (isAtRight()){
            targetX = 1126;
            targetY = 60;
        }
        // if at middle building
        else{
            // do some path finding
            if (targetY == 118){
                if (!isAtPoint(1083, 118)){
                    return;
                }
                else{
                    targetX = 900;
                    targetY = 60;
                }
            }
            if (isAtPoint(885, 60)){
                targetX = sitX;
                targetY = sitY;
                isAlongY = false;
                isAlongX = false;
            }
            else if (isAtPoint(1126, 60)){
                targetX = 1083;
                targetY = 118;
                isAlongX = true;
                isAlongY = false;
            }
            else if (isAtPoint(900, 60)){
                targetX = sitX;
                targetY = sitY;
            }
            // if at lower portion of the middle building
            else if (isAtHall()){
                if (targetX != 900 && targetY != 60){
                    isAlongX = true;
                    isAlongY = false;
                    targetX = 900;
                    targetY = 60;
                }
            }
        }
    }
    
    public ArrayList<Integer> getRooms(){
        ArrayList<Integer> tmp = new ArrayList<Integer>();
        // for left doctor to find any patients in the left room
        if (which){
            for (int i=0; i<=2; i++){
                if (beds[i].isOccupied()){
                    tmp.add(i);
                }
            }
        }
        // for right doctor to find any patients in the right room
        else{
            for (int i=3; i<6; i++){
                if (beds[i].isOccupied()){
                    tmp.add(i);
                }
            }
        }
        return tmp;
    }
    
    public void basicChecks(){
        if (diseased){
            death_timer--;
            if (death_timer < -60){
                setLocation(0, 0);
            }
            return;
        }
        if (death_timer > 0){
            death_timer--;
        }
    }
    
    public void setDeathTimer(){
        death_timer = 6000;
    }
    
    public void act(){
        basicChecks();
        speed = 2;
        // if dead go back to counter and remove me
        // if free of work
        if (isFree){
            if (death_timer == 0){
                targetX = sitX;
                targetY = sitY;
                if (isAtPoint(sitX, sitY)){
                    removeMe();
                }
                goToDestination();
                return;
            }
            // if at counter
            if (isAtCounter()){
                setRotation(90);
                // if people are waiting to get tested
                if (isPeopleWaiting()){
                    walkOutCounter(); // walk out of counter
                    // walked out of counter then go to the person to get tested
                    if (!isAtCounter()){
                        goToWaiter();
                    }
                    // testing
                    if (findWaitingArea((int)targetX, (int)targetY + dy) != -1){
                        isFree = false;
                        testing = true;
                        testDuration = 120;
                        testingAreas[findWaitingArea((int)targetX, (int)targetY + dy)].setOccupied(true);
                    }
                }
                // if no one is waiting to get tested 
                else{
                    targetX = sitX;
                    targetY = sitY;
                    goToDestination();
                    if (isAtPoint(sitX, sitY)){
                        // get which rooms have patients
                        ArrayList<Integer> rooms = getRooms();
                        // if there are any patients waiting to get cured
                        if (rooms.size() > 0){
                            patientRooms = rooms;
                            currentRoom = rooms.get(0);
                            cureDuration = 120;
                            cureing = true;
                            isFree = false;
                        }
                    }
                }
            }
            // if not at counter then go back
            else{
                goBackToCounter();
            }
        }
        // if not free doing other work
        else{
            // if currently testing
            if (testing){
                // set up test time
                if (testDuration > 0){
                    testDuration--;
                }
                Person p = null; // person to get tested
                goToDestination();
                setRotation(90);
                // if arrived testing location
                if (isAtPoint(targetX, targetY)){
                    p = (Person)getOneObjectAtOffset(0, dy, Person.class); // get the person infront of me
                    // if person does not exist / dead
                    if (p == null){
                        isFree = true;
                        testing = false;
                        if (findWaitingArea((int)targetX, (int)targetY + dy) != -1){
                            testingAreas[findWaitingArea((int)targetX, (int)targetY + dy)].setOccupied(false);
                            waitingAreas[findWaitingArea((int)targetX, (int)targetY + dy)].setOccupied(false);
                        }
                        return;
                    }
                }
                // person exists then make them turn red
                if (p != null && !p.isDead() && testDuration == 0){
                    p.turnRed();
                    p.setPositive();
                }
                // if person is dead
                if (p != null && p.isDead()){
                    testing = false;
                    isFree = true;
                    if (findWaitingArea((int)targetX, (int)targetY + dy) != -1){
                        testingAreas[findWaitingArea((int)targetX, (int)targetY + dy)].setOccupied(false);
                        waitingAreas[findWaitingArea((int)targetX, (int)targetY + dy)].setOccupied(false);
                    }
                }
                // if there is an empty room then lead them to there
                if (isEmptyRoom() && p != null && !p.isDead() && testDuration == 0){
                    testing = false;
                    leading = true;
                    currentRoom = getNearestRoom();
                    currentPatient = p;
                    currentPatient.setCurrentRoom(currentRoom);
                    if (findWaitingArea((int)targetX, (int)targetY + dy) != -1){
                        testingAreas[findWaitingArea((int)targetX, (int)targetY + dy)].setOccupied(false);
                        waitingAreas[findWaitingArea((int)targetX, (int)targetY + dy)].setOccupied(false);
                    }
                }
                // rooms are full tell patient to go out
                else{
                    if (currentPatient != null && p != null && testDuration == 0){
                        p.goOutHospital();
                        testingAreas[findWaitingArea((int)targetX, (int)targetY + dy)].setOccupied(false);
                        waitingAreas[findWaitingArea((int)targetX, (int)targetY + dy)].setOccupied(false);
                        testing = false;
                        leading = false;
                        isFree = true;
                    }
                }
            }
            // if leading a patient to a room
            if (leading){
                // if they died while on their way
                if (currentPatient.isDead()){
                    leading = false;
                    isFree = true;
                    beds[currentRoom].setOccupied(false);
                    return;
                }
                else{
                    currentPatient.addDeathTimer(1);
                }
                speed = (double)currentPatient.getSpeed(); // walk the same speed as the patient
                if (currentRoom <= 2){ // if room is at left buidling
                    goToLeft(0); // go to left room
                    currentPatient.goToLeft(0); // make patient go to left room as well
                    // if both arrived at the room then leave
                    if (isAtPoint(beds[currentRoom].getX(), beds[currentRoom].getY()) && currentPatient.isAtPoint(beds[currentRoom].getX(), beds[currentRoom].getY())){
                        leading = false;
                        isFree = true;
                        currentPatient.setRotation(beds[currentRoom].getRotation());
                        beds[currentRoom].setOccupied(true);
                        waitDuration = 0;
                    }
                    else{
                        waitDuration++;
                        if (waitDuration >= 1000){
                            leading = false;
                            isFree = true;
                            beds[currentRoom].setOccupied(false);
                            waitDuration = 0;
                        }
                    }
                }
                // if room is at right building
                else{
                    goToRight(0); // go to right room
                    currentPatient.goToRight(0); // make patient go to right room as well
                    // if both arrived at the room then leave
                    if (isAtPoint(beds[currentRoom].getX(), beds[currentRoom].getY()) && currentPatient.isAtPoint(beds[currentRoom].getX(), beds[currentRoom].getY())){
                        leading = false;
                        isFree = true;
                        currentPatient.setRotation(beds[currentRoom].getRotation());
                        beds[currentRoom].setOccupied(true);
                        waitDuration = 0;
                    }
                    else{
                        waitDuration++;
                        if (waitDuration >= 1000){
                            leading = false;
                            isFree = true;
                            beds[currentRoom].setOccupied(false);
                            waitDuration = 0;
                        }
                    }
                }
            }
            // if cureing a patient
            if (cureing){
                // if at counter then walk out
                if (isAtCounter() && targetX != 1080){
                    walkOutCounter();
                }
                else{
                    // current room to go to 
                    if (currentRoom == patientRooms.get(0)){
                        // if left doctor
                        if (which){
                            // arrived at the room
                            if (isAtPoint(beds[currentRoom].getX() + 30, beds[currentRoom].getY())){
                                setRotation(180);
                                cureDuration--;
                                // cure time 
                                if (cureDuration == 0){
                                    // get the person infront of me
                                    Person p = (Person)getOneObjectAtOffset(-30, 0, Person.class);
                                    if (p != null){
                                        // if person is dead
                                        if (p.isDead() && p != null){
                                            beds[currentRoom].setOccupied(false);
                                        }
                                        else{
                                            // cure the person 
                                            p.cureMe(currentRoom);
                                            if (!p.isInfected()){
                                                beds[currentRoom].setOccupied(false);
                                            }
                                        }
                                    }
                                    // person does not exist / already dead
                                    else{
                                        beds[currentRoom].setOccupied(false);
                                    }
                                    // go to next room if there are any
                                    if (patientRooms.size() > 1){
                                        currentRoom = patientRooms.get(1);
                                        cureDuration = 120;
                                    }
                                    else{
                                        isFree = true;
                                        cureing = false;
                                    }
                                }
                            }
                            // not arrived at room then go 
                            else{
                                goToLeft(30);
                                setRotation(180);
                            }
                        }
                        // for right doctor
                        // all below code is similar procedure
                        else{
                            if (isAtPoint(beds[currentRoom].getX() - 30, beds[currentRoom].getY())){
                                setRotation(0);
                                cureDuration--;
                                if (cureDuration == 0){
                                    Person p = (Person)getOneObjectAtOffset(30, 0, Person.class);
                                    if (p != null){
                                        if (p.isDead()){
                                            beds[currentRoom].setOccupied(false);
                                        }
                                        else{
                                            p.cureMe(currentRoom);
                                            if (!p.isInfected()){
                                                beds[currentRoom].setOccupied(false);
                                            }
                                        }
                                    }
                                    else{
                                        beds[currentRoom].setOccupied(false);
                                    }
                                    if (patientRooms.size() > 1){
                                        currentRoom = patientRooms.get(1);
                                        cureDuration = 120;
                                    }
                                    else{
                                        isFree = true;
                                        cureing = false;
                                    }
                                }
                            }
                            else{
                                goToRight(30);
                                setRotation(180);
                            }
                        }
                    }
                    else if (patientRooms.size() > 1 && currentRoom == patientRooms.get(1)){
                        targetY = beds[currentRoom].getY();
                        if (which){
                            targetX = beds[currentRoom].getX() + 30;
                            if (isAtPoint(beds[currentRoom].getX() + 30, beds[currentRoom].getY())){
                                cureDuration--;
                                setRotation(180);
                                if (cureDuration == 0){
                                    Person p = (Person)getOneObjectAtOffset(-30, 0, Person.class);
                                    if (p != null){
                                        if (p.isDead()){
                                            beds[currentRoom].setOccupied(false);
                                        }
                                        else{
                                            p.cureMe(currentRoom);
                                            if (!p.isInfected()){
                                                beds[currentRoom].setOccupied(false);
                                            }
                                        }
                                    }
                                    else{
                                        beds[currentRoom].setOccupied(false);
                                    }
                                    if (patientRooms.size() > 2){
                                        currentRoom = patientRooms.get(2);
                                        cureDuration = 120;
                                    }
                                    else{
                                        isFree = true;
                                        cureing = false;
                                    }
                                }
                            }
                        }
                        else{
                            targetX = beds[currentRoom].getX() - 30;
                            if (isAtPoint(beds[currentRoom].getX() - 30, beds[currentRoom].getY())){
                                setRotation(0);
                                cureDuration--;
                                if (cureDuration == 0){
                                    Person p = (Person)getOneObjectAtOffset(30, 0, Person.class);
                                    if (p != null){
                                        if (p.isDead()){
                                            beds[currentRoom].setOccupied(false);
                                        }
                                        else{
                                            p.cureMe(currentRoom);
                                            if (!p.isInfected()){
                                                beds[currentRoom].setOccupied(false);
                                            }
                                        }
                                    }
                                    else{
                                        beds[currentRoom].setOccupied(false);
                                    }
                                    if (patientRooms.size() > 2){
                                        currentRoom = patientRooms.get(2);
                                        cureDuration = 120;
                                    }
                                    else{
                                        isFree = true;
                                        cureing = false;
                                    }
                                }
                            }
                        }
                        goToDestination();
                    }
                    else if (patientRooms.size() > 2 && currentRoom == patientRooms.get(2)){
                        targetY = beds[currentRoom].getY();
                        if (which){
                            targetX = beds[currentRoom].getX() + 30;
                            if (isAtPoint(beds[currentRoom].getX() + 30, beds[currentRoom].getY())){
                                cureDuration--;
                                setRotation(180);
                                if (cureDuration == 0){
                                    Person p = (Person)getOneObjectAtOffset(-30, 0, Person.class);
                                    if (p != null){
                                        if (p.isDead()){
                                            beds[currentRoom].setOccupied(false);
                                        }
                                        else{
                                            p.cureMe(currentRoom);
                                            if (!p.isInfected()){
                                                beds[currentRoom].setOccupied(false);
                                            }
                                        }
                                    }
                                    else{
                                        beds[currentRoom].setOccupied(false);
                                    }
                                    if (patientRooms.size() > 3){
                                        currentRoom = patientRooms.get(2);
                                        cureDuration = 120;
                                    }
                                    else{
                                        isFree = true;
                                        cureing = false;
                                    }
                                }
                            }
                        }
                        else{
                            targetX = beds[currentRoom].getX() - 30;
                            if (isAtPoint(beds[currentRoom].getX() - 30, beds[currentRoom].getY())){
                                setRotation(0);
                                cureDuration--;
                                if (cureDuration == 0){
                                    Person p = (Person)getOneObjectAtOffset(30, 0, Person.class);
                                    if (p != null){
                                        if (p.isDead()){
                                            beds[currentRoom].setOccupied(false);
                                        }
                                        else{
                                            p.cureMe(currentRoom);
                                            if (!p.isInfected()){
                                                beds[currentRoom].setOccupied(false);
                                            }
                                        }
                                    }
                                    else{
                                        beds[currentRoom].setOccupied(false);
                                        isFree = true;
                                        cureing = false;
                                    }
                                    if (patientRooms.size() > 3){
                                        currentRoom = patientRooms.get(2);
                                        cureDuration = 120;
                                    }
                                    else{
                                        isFree = true;
                                        cureing = false;
                                    }
                                }
                            }
                        }
                        goToDestination();
                    }
                }
                
            }
        }
    }
}
