import java.util.Arrays;

public class CommandInterpreter{
    /* Load items for all methods to share*/
    private static KeyboardReader reader = new KeyboardReader();
    private static StateManager stateManager = new StateManager();
    //private static ActivityLogger log = new ActivityLogger();
    private static DataStore storage = new DataStore();
    private static String raidaStatus = "uuuuuuuuuuuuuuuuuuuuuuuuu";//All RAIDAs status is 'u' for unknown.
   
    /* CHEST: Load variables for Chest Mode to use*/
    public static String chestTopFileName;
    public static String chestBottomFileName;
    public static int[][] topChestRegister;
    public static int[][] bottomChestRegister;
    public static String topChestTag;
    public static String bottomChestTag;
    
    /* INCOME: Load variables for Income Mode to use*/
    public static int incomeTotalMovedToBank = 0;
    public static int incomeTotalMovedToFracked  = 0;
    public static int incomeTotalMovedToCounterfeit  = 0;
    public static int incomeTotalValueMoved = 0;
    public static String tagWhenMoving;
    
    public static void main(String[] args) {

        printWelcome();

        //Load up from files
        StateManager stateManager = new StateManager();

        //Start the Program. 
        run();

        System.out.println("Thank you for using CloudCoin Crafter. Goodbye.");
    }//End main

    /**
     * Print out the opening message for the player. 
     */
    private static void printWelcome() {

        System.out.println("Welcome to CloudCoin Crafter Opensource and free. No gaurantees made.");
  
    }

    public static void run() {
        boolean restart = false;
        System.out.println( stateManager.currentState.getLongDescription() );
        while( ! restart )
        {
            String[] commandsAvailable = stateManager.currentState.getCommands();
            System.out.print( "Commands available: ");
            for ( String command : commandsAvailable)
            {
                System.out.print( "\n" + command  );
            }
            //reader.setPrompt(Arrays.toString( commandsAvailable ) + "> ");
            // System.out.print( Arrays.toString( commandsAvailable ) );
            System.out.print( "\n prompt>" );
            //System.out.println( world1.currentRoom.getLongDescription() );

            String commandRecieved = reader.readString( commandsAvailable );          

            switch( commandRecieved.toLowerCase() )
            {

                case "click bank-tab":              
                case "escape":
                case "click start":
                                executeChangeStateToBank();
                                break;
                                
                case "quit":
                                System.out.println("Goodbye!");
                                System.exit(0);
                                break;
                                
                case "doubleclick import-icon":
                                executeChangeStateToImport();
                                break;
                
                case "doubleclick chest-icon[i]":
                                executeChangeStateToChest();
                                break;
                                
                case "click fracked-tab":
                                executeChangeStateToFracked();
                                break;
                                
                case "drag":
    
                break;
                default:
                System.out.println("Command failed. Try again.");
                break;
            }
        }
    }


    
    public static void executeChangeStateToBank(  )
    {
        // Try to leave current room.
        State nextState = stateManager.currentState.getExit( "bank" );

        stateManager.currentState = nextState;
        System.out.println( "──────────────────────────");
        System.out.println( stateManager.currentState.getLongDescription());
        System.out.println( stateManager.currentState.getCommands() );
    }
    
        public static void executeChangeStateToImport(  )
    {
        // Try to leave current room.
        State nextState = stateManager.currentState.getExit( "import" );

        stateManager.currentState = nextState;
        System.out.println( "──────────────────────────");
        System.out.println( stateManager.currentState.getLongDescription());
        System.out.println( stateManager.currentState.getCommands() );
    }
    
    public static void executeChangeStateToChest(  )
    {
        // Try to leave current room.
        State nextState = stateManager.currentState.getExit( "chest" );

        stateManager.currentState = nextState;
        System.out.println( "──────────────────────────");
        System.out.println( stateManager.currentState.getLongDescription());
        System.out.println( stateManager.currentState.getCommands() );
    }

        public static void executeChangeStateToFracked(  )
    {
        // Try to leave current room.
        State nextState = stateManager.currentState.getExit( "fracked" );

        stateManager.currentState = nextState;
        System.out.println( "──────────────────────────");
        System.out.println( stateManager.currentState.getLongDescription());
        System.out.println( stateManager.currentState.getCommands() );
    }
    
    private static int countBank(){
        int total = 0;
        return total;
    }//end count bank
    
    private static int countChest( String chestPath){
         int total = 0;
        return total;
    }//end count bank
    
    /**
     * This method wraps long text in the console so that it is easier to read
     */
    private static String wrap(String longString) {
        int MAX_WIDTH = 80;
        String[] splittedString = longString.split(" ");
        String resultString = "";
        String lineString = "";

        for (int i = 0; i < splittedString.length; i++) {
            if (lineString.isEmpty()) {
                lineString += splittedString[i];
            } else if (lineString.length() + splittedString[i].length() < MAX_WIDTH) {
                lineString += splittedString[i] + " ";
            } else {
                resultString += lineString + "\n";
                lineString = " ";
            }
        }

        if(!lineString.isEmpty()){
            resultString += lineString + "\n";
        }

        return resultString;
    }

}//EndMain
