import java.util.ArrayList;
import java.util.HashMap;
/**
 * Creates the state with commands, links to other states and datastores. 
 * 
 * @author Sean Worthington
 * @version 11/25/2016
 */
public class StateManager
{
    // instance variables - replace the example below with your own
    private static ArrayList<State> states; 
    private ArrayList<Door> doors;
    public State currentState;
    
    //bank mode variables
    private boolean toolTipChest = false;
    private boolean toolTipNameTag = false;

    //Chest Mode Variables
    public String topChestTag;
    public String bottomChestTag;
    public int topChestTotal;
    public int bottomChestTotal;
   
    /**
     * Constructor for objects of class StateManager. 
     * Not much goin on here. 
     */
    public StateManager( )
    {
        //Make rooms
        states = new ArrayList<State>();
        
            State splash_mode = new State("Splash mode");
            State bank_mode = new State("Bank mode");
            State import_mode = new State("Import mode");
            State  fracked_mode = new State("Fracked mode");
            State chest_mode = new State("Chest mode");
            
        currentState = splash_mode;

        //Connect rooms via doors and specifiy if they are locked. 
        splash_mode.setDoor("bank", bank_mode );
        bank_mode.setDoor("import", import_mode );
        bank_mode.setDoor("fracked", fracked_mode );
        bank_mode.setDoor("chest", chest_mode );
        fracked_mode.setDoor("bank", bank_mode);
        import_mode.setDoor("bank", bank_mode );
        chest_mode.setDoor("bank", bank_mode );

        
        /*Splash mode*/  
        splash_mode.setCommand("click start");
        splash_mode.setCommand("quit");
        
       /*bank mode*/  
        bank_mode.setCommand("loadfiles keep ans");
        bank_mode.setCommand("loadfiles change ans");
        bank_mode.setCommand("fracked");
        bank_mode.setCommand("export");
        bank_mode.setCommand("show raida");
        bank_mode.setCommand("quit");

        /*Import mode*/   
        import_mode.setCommand("list coins");
        import_mode.setCommand("loadfiles");
        import_mode.setCommand("click tag-chest");
        import_mode.setCommand("click bank-tab");
        import_mode.setCommand("escape");
        import_mode.setCommand("quit");
        
        /*Fracked mode*/  
        fracked_mode.setCommand("click bank-tab");
        //fracked_mode.setCommand("click vault-tab");
        //fracked_mode.setCommand("click lost-tab");
        fracked_mode.setCommand("click up-arrow[i]");
        fracked_mode.setCommand("click down-arrow[i]");
        
        fracked_mode.setCommand("doubleclick coin[i][i]");
        fracked_mode.setCommand("drag coin[i][i]-to-repair");
        fracked_mode.setCommand("quit");
        
        
        chest_mode.setCommand("click top-chest-arrow[i]");
        chest_mode.setCommand("click bottom-chest-arrow[i]");
        chest_mode.setCommand("click top-chest-tag");
        chest_mode.setCommand("click bottom-chest-tag");
        chest_mode.setCommand("escape");
        chest_mode.setCommand("quit");
        
        
    }//End constructor

        //Methdods
        
     /**
     * @return the States
     */
    public static ArrayList<State> getStates() {
        return states;
    }//end get rooms

    public State getCurrentState()
    {
     return currentState;
    }//end get current room

}//end class
    
 
