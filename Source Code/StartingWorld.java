/**
 * Write a description of class StartingWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import greenfoot.*;
public class StartingWorld extends World
{
    // instance variables - replace the example below with your own
    private int counter = 0;
    private MouseInfo m;
    private Circle[] population, patient_zeros, masked, vaccinated;
    
    private int last_po = 0, last_pa = 0, last_ma = 0, last_va = 0; // keep track of the last circle clicked
    private int populationCount = 10, patient_zerosCount = 1, maskedCount = 0, vaccinatedCount = 0; // keep track of the values being selected
    /**
     * Constructor for objects of class StartingWorld
     */
    public StartingWorld()
    {
        super(1280, 700, 1);
        population = new Circle[4];
        patient_zeros = new Circle[4];
        masked = new Circle[4];
        vaccinated = new Circle[4];
        // Uses arrays to set up circles/options on the starting screen
        for (int i=0; i<4; i++){
            population[i] = new Circle(331 + 77 * i, 387, 62, Color.BLUE, (i == 0 ? true : false));
            masked[i] = new Circle(331 + 77 * i, 550, 62, Color.BLUE, (i == 0 ? true : false));
        }
        for (int i=0; i<4; i++){
            patient_zeros[i] = new Circle(707 + 77 * i, 390, 62, Color.BLUE, (i == 0 ? true : false));
        }
        for (int i=0; i<4; i++){
            vaccinated[i] = new Circle(710 + 77 * i, 557, 62, Color.BLUE, (i == 0 ? true : false));
        }
    }
    
    public void act(){
        m = Greenfoot.getMouseInfo();
        if (m != null){
            for (int i=0; i<4; i++){
                // if clicked on a population toggle update corresponding values
                if (Greenfoot.mouseClicked(population[i])){
                    population[last_po].setvisible(false);
                    population[i].setvisible(true);
                    last_po = i;
                    populationCount = 10 + 10 * i;
                }
                // if clicked on a patient zero toggle update corresponding values
                if (Greenfoot.mouseClicked(patient_zeros[i])){
                    patient_zeros[last_pa].setvisible(false);
                    patient_zeros[i].setvisible(true);
                    last_pa = i;
                    patient_zerosCount = 1 + i;
                }
                // if clicked on a masked toggle update corresponding values
                if (Greenfoot.mouseClicked(masked[i])){
                    masked[last_ma].setvisible(false);
                    masked[i].setvisible(true);
                    last_ma = i;
                    maskedCount = 333 * i;
                }
                // if clicked on a vaccinated toggle update corresponding values
                if (Greenfoot.mouseClicked(vaccinated[i])){
                    vaccinated[last_va].setvisible(false);
                    vaccinated[i].setvisible(true);
                    last_va = i;
                    vaccinatedCount = 333 * i;
                }
            }
            if (Greenfoot.isKeyDown("Enter")){
                Greenfoot.setWorld(new MyWorld(populationCount, patient_zerosCount, maskedCount, vaccinatedCount));
            }
        }
        // add circles to the world
        if (counter == 0){
            for (int i=0; i<4; i++){
                addObject(population[i], population[i].getX(), population[i].getY());
                addObject(masked[i], masked[i].getX(), masked[i].getY());
                addObject(patient_zeros[i], patient_zeros[i].getX(), patient_zeros[i].getY());
                addObject(vaccinated[i], vaccinated[i].getX(), vaccinated[i].getY());
            }
            counter++;
        }
    }
}
