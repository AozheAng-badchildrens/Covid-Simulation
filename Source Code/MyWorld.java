import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.ArrayList;
/**
 * Write a description of class MyWorld here.
 * 
 * @author (your name) 
 * @virusVersion (a virusVersion number or a date)
 */
public class MyWorld extends World
{
    // Iniil counters
    private int counterN = 0;
    private int counterP = 0;
    private int population = 20;
    private int patientZeros;
    
    // Percentage of people who are masked and vaccinted(out of 1000)
    private int maskedPercentage = 666;
    private int vaccinatedPercentage = 666;
    
    private int virusVersion = 1;
    private int deaths = 0;
    private int active_cases = 0;
    private int recovered = 0;
    private boolean virusVisibility = false;
    private boolean statsVisibility = false;
    private MouseInfo m;
    
    private boolean isSchoolTime = true;
    private int mutateSchoolDuration = 1500;
    
    // for testing purposes
    private int Covid19R = 35, Covid19I = 100;
    private int DeltaR = 25, DeltaI = 250;
    private int OmiR = 40, OmiI = 120;
    private int Covid23R = 2000, Covid23I = 300;
    
    // Music
    private GreenfootSound bell;
    private GreenfootSound backgroundSound;
    
    public MyWorld(int population, int patientZeros, int maskedPercentage, int vaccinatedPercentage)
    {    
        // Create a new world with 1280x700 cells with a cell size of 1x1 pixels.
        super(1280, 700, 1);
        this.population = population;
        this.patientZeros = patientZeros;
        this.maskedPercentage = maskedPercentage;
        this.vaccinatedPercentage = vaccinatedPercentage;
        this.population = population + patientZeros + 12;
        bell = new GreenfootSound("bellSFX.wav");
        backgroundSound = new GreenfootSound("background noise.wav");
    }
    
    public void started(){
        backgroundSound.playLoop();
    }
    
    public void stopped(){
        bell.stop();
        backgroundSound.stop();
    }
    
    public int getRandom(int min, int max){
        return min + Greenfoot.getRandomNumber(max - min + 1);    
    }
    
    public boolean getRandom(double p){
        if (getRandom(1, 1000) <= p){
            return true;
        }
        return false;
    }
    
    public void addDeaths(int a){
        deaths += a;
    }
    
    public void addRecovered(){
        recovered++;
    }
    
    public int getPopulation(){
        return population;
    }
    
    public int getDeaths(){
        return deaths;
    }
    
    public int getActiveCases(){
        return active_cases;
    }
    
    public int getVirusVersion(){
        return virusVersion;
    }
    
    public Virus getVirus(Person p){
        Virus v = null;
        if (virusVersion == 1){
            v = new Covid19(1, p, Covid19R, Covid19I, virusVisibility); 
        }
        else if (virusVersion == 2){
            v = new Delta(2, p, DeltaR, DeltaI, virusVisibility);
        }
        else if (virusVersion == 3){
            v = new Omicron(3, p, OmiR, OmiI, virusVisibility);
        }
        else{
            v = new Covid23(4, p, Covid23R, Covid23I, virusVisibility);
        }
        return v;
    }
    
    public void mutateSchool(){
        if (isSchoolTime){
            // Go through every student and teacher and checks if not dead or in hospital, then they go to school
            ArrayList<Student> students = (ArrayList<Student>) getObjects(Student.class);
            for (Student s : students){
                if (!s.isDead() && !(s.isInHospital() || s.toHospital())){
                    s.setSchoolTime(false);
                }
            }
            ArrayList<Teacher> teacher = (ArrayList<Teacher>) getObjects(Teacher.class);
            for (Teacher t : teacher){
                if (!t.isDead() && !(t.isInHospital() || t.toHospital())){
                    t.setSchoolTime(false);
                }
            }
            isSchoolTime = false;
        }
        else{
            ArrayList<Student> students = (ArrayList<Student>) getObjects(Student.class);
            for (Student s : students){
                if (!s.isDead() && !(s.isInHospital() || s.toHospital())){
                    s.setSchoolTime(true);
                }
            }
            ArrayList<Teacher> teacher = (ArrayList<Teacher>) getObjects(Teacher.class);
            for (Teacher t : teacher){
                if (!t.isDead() && !(t.isInHospital() || t.toHospital())){
                    t.setSchoolTime(true);
                }
            }
            isSchoolTime = true;
        }
        bell.play();
    }
    
    public void mutateVirus(){
        virusVersion++;
        ArrayList<Virus> viruses = (ArrayList<Virus>) getObjects(Virus.class);
        for (Virus v : viruses){
            if (!v.isRemoved()){
                v.setRemoved(true);
            }
        }
        ArrayList<Person> person = (ArrayList<Person>) getObjects(Person.class);
        Virus v = null;
        for (Person p : person){
            if (!p.isDead() && p.isInfected()){
                if (virusVersion == 2){
                    v = new Delta(2, p, DeltaR, DeltaI, virusVisibility);
                }
                else if (virusVersion == 3){
                    v = new Omicron(3, p, OmiR, OmiI, virusVisibility);
                }
                else if (virusVersion == 4){
                    v = new Covid23(4, p, Covid23R, Covid23I, virusVisibility);
                }
                addObject(v, 0, 0);
                p.setVirus(v);
            }
        }
    }
    
    public boolean doIMutate(){
        if (virusVersion == 1){
            if (active_cases + deaths >= population / 4){
                return true;
            }
        }
        else if (virusVersion == 2){
            if (active_cases + deaths >= population / 2){
                return true;
            }
        }
        else if (virusVersion == 3){
            if (active_cases + deaths >= population * 3 / 4){
                return true;
            }
        }
        return false;
    }
    
    public void spawnCivilian(boolean isPathogen){
        int age = getRandom(20, 55); // get age
        int X = 0, Y = 0; // random location
        double speed = (double)getRandom(8, 18) / 10.0; // get speed
        int initial_probability = getRandom(50, 100); // get initial probability of being infected
        boolean masked = getRandom(maskedPercentage) ? true : false; 
        boolean vaccinated = getRandom(vaccinatedPercentage) ? true : false;
        // generate random location on the roads
        if (getRandom(0,1) == 0){
            Y = getRandom(300, 400);
            X = getRandom(0, 1250);
        }
        else{
            X = getRandom(550, 700);
            Y = getRandom(0, 680);
        }
        addObject(new Civilian(age, speed, initial_probability, masked, isPathogen, vaccinated), X, Y);
    }
    
    public void spawnOldHag(){
        int age = getRandom(60, 100);
        int X = 0, Y = 0;
        double speed = 0.8;
        int initial_probability = getRandom(100, 200);
        boolean masked = getRandom(maskedPercentage) ? true : false;
        boolean vaccinated = getRandom(vaccinatedPercentage) ? true : false;
        if (getRandom(0,1) == 0){
            Y = getRandom(300, 400);
            X = getRandom(0, 1250);
        }
        else{
            X = getRandom(550, 700);
            Y = getRandom(0, 680);
        }
        addObject(new OldHag(age, speed, initial_probability, masked, false, vaccinated), X, Y);
    }
    
    public void spawnChef(){
        int age = getRandom(20, 55);
        double speed = 1;
        int initial_probability = getRandom(10, 30);
        addObject(new Chef(age, speed, initial_probability, true, false, true), 1095, 490);
    }
    
    public void spawnStudent(){
        for (int i=0; i<8; i++){
            int age = getRandom(6, 15);
            double speed = (double)getRandom(8, 14) / 10.0;
            int initial_probability = getRandom(20, 60);
            boolean masked = getRandom(maskedPercentage) ? true : false;
            boolean vaccinated = getRandom(vaccinatedPercentage) ? true : false;
            addObject(new Student(age, speed, initial_probability, masked, false, vaccinated, i), 1, 1);
        }
    }
    
    public void spawnDoctor(int X, int Y, boolean which){
        int age = getRandom(20, 55);
        double speed = 2;
        int initial_probability = getRandom(5, 15);
        addObject(new Doctor(age, speed, initial_probability, true, false, true, which), X, Y);
    }
    
    public void spawnTeacher(){
        int age = getRandom(25, 50);
        double speed = 1;
        int initial_probability = getRandom(20, 60);
        boolean masked = getRandom(maskedPercentage) ? true : false;
        boolean vaccinated = getRandom(vaccinatedPercentage) ? true : false;
        addObject(new Teacher(age, speed, initial_probability, masked, false, vaccinated), 415, 546);
    }
    
    public void act(){
        active_cases = getObjects(Virus.class).size(); // get number of active cases
        if (statsVisibility){
            showText("Active Cases: " + active_cases + " Recovered Cases: " + recovered + " Death Cases: " + deaths, 640, 20);
        }
        m = Greenfoot.getMouseInfo();
        String key = Greenfoot.getKey();
        if (key != null){
            // if v key is pressed toggle virus visibility
            if (key.equals("v")){
                virusVisibility = !virusVisibility;
                ArrayList<Virus> viruses = (ArrayList<Virus>) getObjects(Virus.class);
                for (Virus v : viruses){
                    if (v != null && !v.isRemoved() && !v.getPerson().isDead()){
                        v.setvisible(virusVisibility);
                    }
                }
            }
            // is s key is pressed toggle stats visibility
            else if (key.equals("s")){
                statsVisibility = !statsVisibility;
                showText((statsVisibility ? "Active Cases: " + active_cases + " Recovered Cases: " + recovered + " Death Cases: " + deaths : null), 640, 20);
            }
        }
        // Check if the virus should mutate
        if (doIMutate()){
            mutateVirus();
        }
        // Adds civilian
        if (counterP < patientZeros){
            spawnCivilian(true);
            counterP++;
        }
        if (counterN < population - 12 - patientZeros){
            // add doctors, students, teacher, chef
            if (counterN == 1){
                spawnDoctor(947, 48, true);
                spawnDoctor(1015, 48, false);
                spawnStudent();
                spawnTeacher();
                spawnChef();
            }
            
            // Adds old hag and normal civilian
            if (getRandom(0, 3) == 0){
                spawnOldHag();
            }
            else{
                spawnCivilian(false);
            }
            counterN++;
        }
        // school starts and ends after a period of time
        if (mutateSchoolDuration > 0){
            mutateSchoolDuration--;
            if (mutateSchoolDuration == 0){
                mutateSchool();
                mutateSchoolDuration = 1500;
            }
        }
        backgroundSound.playLoop();
        if (active_cases == patientZeros && deaths == population - patientZeros){
            setBackground("endbackground.png");
            showText(null, 640, 20);
            removeObjects(getObjects(Virus.class));
            removeObjects(getObjects(Person.class));
            Greenfoot.stop();
        }
    }
}
