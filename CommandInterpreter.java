import java.util.Arrays;
import java.security.SecureRandom;
import java.util.concurrent.*;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONArray;
//import org.json.*;
public class CommandInterpreter{
    /* Load items for all methods to share*/
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();//For generating PANs (Proposed Authenticity Numbers)
    public static KeyboardReader reader = new KeyboardReader();
    public static StateManager stateManager = new StateManager();
    //private static ActivityLogger log = new ActivityLogger();
    public static Bank bank = new Bank();
    public static Random myRandom = new Random();//This is used for naming new chests

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
    public static RAIDA[] raidaArray = new RAIDA[25];

    public static void main(String[] args) {

        printWelcome();

        //Get JSON from RAIDA Directory
        loadRaida();
        System.out.print("Connecting to RAIDA.");
        setRaidaStatus();
        System.out.println("complete.");

        //Load up from files
        StateManager stateManager = new StateManager();

        //Start the Program. 
        run();

        System.out.println("Thank you for using CloudCoin Crafter. Goodbye.");
    }//End main

    public static void run() {
        boolean restart = false;
        //System.out.println( stateManager.currentState.getLongDescription() );
        while( ! restart )
        {

            System.out.println( "╔═════════════════════╗");
            String[] commandsAvailable = stateManager.currentState.getCommands();
            System.out.println( "║ Commands available:               ║");
            System.out.println( "╚═════════════════════╝");
            for ( String command : commandsAvailable)
            {
                System.out.print( "\n" + command  );
            }
            //reader.setPrompt(Arrays.toString( commandsAvailable ) + "> ");
            // System.out.print( Arrays.toString( commandsAvailable ) );

            System.out.print( "\nCloudCoin Crafter>>" );
            //System.out.println( world1.currentRoom.getLongDescription() );

            String commandRecieved = reader.readString( commandsAvailable );          

            switch( commandRecieved.toLowerCase() )
            {

                case "click bank-tab":              
                case "escape":
                case "start":
                executeChangeStateToBank();
                break;

                case "quit":
                System.out.println("Goodbye!");
                System.exit(0);
                break;

                case "show raida":
                for(int i = 0; i < 25;i++){System.out.println("RAIDA"+ i +": "+ raidaArray[i].status +", ms:" + raidaArray[i].ms );}//end for each raida status
                break;

                case "loadfiles change ans":
                break;

                case "import":
                int totalValueToBank = 0;
                int totalValueToCounterfeit = 0;
                int totalValueToFractured = 0;
                int totalValueLost = 0;

                System.out.println("What is the path and name of the file you want to load?");
                String loadFileName = reader.readString( );   
                String loadFilePath = "W:\\MoneyJpegs\\testchests\\";
                //load the coins into an array of coin objects
                if( !bank.ifFileExists(loadFilePath + loadFileName)){
                    System.out.println(loadFilePath + loadFileName + " not found. Please check your file name and try again."); 
                    break;
                }
                bank.loadIncome( loadFilePath + loadFileName, "keep" );//Keep means do not chage ANs, Change means use High Security
                System.out.println("Done loading into RAM. Saving as .income files");
                //save the coins to the bank file with an .income extension
                for( int ii = 0; ii< bank.newCoins.length ; ii++ ){
                    bank.newCoins[ii].saveCoin("income");
                }//end for

                reader.readString();

                //change imported file to have a .imported extention
                bank.renameFileExtension(loadFilePath + loadFileName, "imported" );
                System.out.println("Results:");

                /*COINS IMPORTED, NOW WE START TESTING COINS */
                //LOAD THE .income COINS ONE AT A TIME AND TEST THEM
                bank.loadImported("./Bank/","income");//Load Coins from hard drive into RAM

                System.out.println("Loaded " + bank.importedCoins.length + " income files");

                for(int j = 0; j < bank.importedCoins.length; j++){
                    System.out.println("Detecting SN #"+bank.importedCoins[j].sn +", Denomination: "+ bank.importedCoins[j].getDenomination() );
                    System.out.println("ans 1 " + bank.importedCoins[j].ans[1]); 
                    System.out.println("ans 2 " + bank.importedCoins[j].ans[2]); 
                    detectCoin( bank.importedCoins[j] );//Checks all 25 GUIDs in the Coin and sets the status. 
                    System.out.println("Finished detecting coin index " + j);
                    //PRINT OUT ALL COIN'S RAIDA STATUS AND SET AN TO NEW PAN
                    System.out.println("");
                    System.out.println("CloudCoin SN #"+bank.importedCoins[j].sn +", Denomination: "+ bank.importedCoins[j].getDenomination() );
                    for(int i = 0; i < 25;i++){
                        if ( i % 5 == 0 ) { System.out.println("");}//Give every five statuses a line break
                        bank.importedCoins[j].pastStatus[i]= raidaArray[i].lastDetectStatus;
                        if( raidaArray[i].lastDetectStatus == "pass"){ 
                            bank.importedCoins[j].hp++; 
                            bank.importedCoins[j].ans[i] =  bank.importedCoins[j].pans[i] ; //Set the coin to show that it has a new AN

                        }
                        System.out.print("R"+ i +": "+ raidaArray[i].lastDetectStatus +", dms:" + raidaArray[i].dms +" | " );
                    }//End for each cloud coin GUID statu

                    //SORT OUT EACH COIN INTO CATAGORIES
                    System.out.println("HP is: " + bank.importedCoins[j].hp );
                    if( bank.importedCoins[j].hp > 24 ){//No Problems Move to Bank
                        bank.importedCoins[j].saveCoin("bank");
                        bank.importedCoins[j].deleteCoin("income");
                        totalValueToBank++;
                    }
                    else if( bank.importedCoins[j].hp > 9 )
                    {//Can be fixed
                        bank.importedCoins[j].saveCoin("fracked");
                        bank.importedCoins[j].deleteCoin("income");
                        totalValueToFractured++;
                        //greater than 20, send to bank
                    } else if( bank.importedCoins[j].hp > 1) {//Lost coin
                        bank.importedCoins[j].saveCoin("lost");
                        bank.importedCoins[j].deleteCoin("income");
                        totalValueLost++;
                    }else{ //Counterfeit - send to counterfeit
                        bank.importedCoins[j].saveCoin("counterfeit");
                        bank.importedCoins[j].deleteCoin("income");
                        totalValueToCounterfeit++;
                    }

                    //NOW FIX FRACTURED IF IF NEEDED

                    //  bank.loadCloudCoins("./Bank/","fractured");
                }//end for each coin to import
                //REPORT ON DETECTION OUTCOME
                System.out.println("Results of Import:");
                System.out.println("Good and Moved in Bank: "+ totalValueToBank);
                System.out.println("Counterfeit and Moved to trash: "+totalValueToCounterfeit);
                System.out.println("Fracked and Moved to Fracked: "+ totalValueToFractured);
                System.out.println("Lost and Moved to Lost: "+ totalValueLost);

                //fix fractured. 
                //Fixed fraced later
                //rename file to counterfeit   
                break;

                case "fix fracked":
                //Load coins from file in to banks fracked array
                totalValueToBank = 0;
                totalValueToFractured = 0;
                totalValueLost = 0;
                totalValueToCounterfeit=0;
                bank.loadFracked("./Bank/","fracked");
                System.out.println("Loaded " + bank.frackedCoins.length + " fracked files");
                for(int k = 0; k < bank.frackedCoins.length; k++){

                    //bank.frackedCoins[k].reportStatus();
                    System.out.println("Unfracking SN #"+bank.frackedCoins[k].sn +", Denomination: "+ bank.frackedCoins[k].getDenomination() );
                    System.out.println("ans 1 " + bank.frackedCoins[k].ans[1]); 
                    System.out.println("ans 2 " + bank.frackedCoins[k].ans[2]); 
                    fixCoin( bank.frackedCoins[k] );//Checks all 25 GUIDs in the Coin and sets the status. 
                    //Check CloudCoin's hp. 
                    bank.frackedCoins[k].calculateHP();
                    System.out.println("Finished unfracking coin index " + k +", HP is now " + bank.frackedCoins[k].hp);
                    //write file name to bank
                    //SORT OUT EACH COIN INTO CATAGORIES
                    System.out.println("HP is: " + bank.frackedCoins[k].hp );
                    if( bank.frackedCoins[k].hp > 24 ){//No Problems Move to Bank
                        bank.frackedCoins[k].saveCoin("bank");
                        bank.frackedCoins[k].deleteCoin("income");
                        totalValueToBank++;
                    }
                    else if( bank.frackedCoins[k].hp > 9 )
                    {//Can be fixed
                        bank.frackedCoins[k].saveCoin("fracked");
                        bank.frackedCoins[k].deleteCoin("income");
                        totalValueToFractured++;
                        //greater than 20, send to bank
                    } else if( bank.importedCoins[k].hp > 1) {//Lost coin
                        bank.frackedCoins[k].saveCoin("lost");
                        bank.frackedCoins[k].deleteCoin("income");
                        totalValueLost++;
                    }else{ //Counterfeit - send to counterfeit
                        bank.frackedCoins[k].saveCoin("counterfeit");
                        bank.frackedCoins[k].deleteCoin("income");
                        totalValueToCounterfeit++;
                    }

                }//end for each fracked coin

                //REPORT ON DETECTION OUTCOME
                System.out.println("Results of Fix Fractured:");
                System.out.println("Good and Moved in Bank: "+ totalValueToBank);
                System.out.println("Counterfeit and Moved to trash: "+totalValueToCounterfeit);
                System.out.println("Still Fracked and Moved to Fracked: "+ totalValueToFractured);
                System.out.println("Lost and Moved to Lost: "+ totalValueLost);

                break;
                case "fracked":
                executeChangeStateToFracked();

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
            }//end switch
        }//end while
    }//end run method

    /**
     * Print out the opening message for the player. 
     */
    public static void printWelcome() {
        System.out.println("Welcome to CloudCoin Crafter Opensource and free. No gaurantees made.");
    }

    public static void loadFractured(){

        /*/Go through every RAIDA for the coin and check to see if it needs fixing
        for(int i = 0; i < 25;i++){

        if( raidaArray[i].lastDetectStatus != "fail")//Probably cannot fix errors. 
        { 
        if(   raidaArray[ TriadOne[0]].lastDetectStatus=="passed" && raidaArray[TriadOne[1]].lastDetectStatus=="passed" &&  raidaArray[TriadOne[2]].lastDetectStatus=="passed"   )
        {
        //Do inParllel
        raidaArray[ triadTwo[0] ].get_Ticket();
        raidaArray[ triadTwo[1] ].get_Ticket();
        raidaArray[ triadTwo[2] ].get_Ticket();
        raidaArray[ id ].fix();
        }
        }//end 
        System.out.print("R"+ i +": "+ raidaArray[i].lastDetectStatus +", dms:" + raidaArray[i].dms +" | " );
        }//End for each cloud coin GUID statu

        loadTickets(bank.newCoins[j]);
        fixFractured(bank.newCoins[j]);
         */
    }//end load fracture
    /**
     * Initializes 25 RAIDA server objects that can be used to talk to the RAIDA
     */
    public static void loadRaida() {
        String directoryJson = "blank";
        try{
            directoryJson = getHtml("http://CloudCoin.co/servers.html");
        }catch( IOException ex ){
            System.out.println( "error " + ex );
        }
        //Parse the json file
        //System.out.println("1." + directoryJson);

        JSONArray directoryJsonArray;
        try{
            JSONObject o = new JSONObject( directoryJson );
            directoryJsonArray = o.getJSONArray("server"); 

            for (int i = 0; i < directoryJsonArray.length(); i++) {  // **line 2**
                JSONObject childJSONObject = directoryJsonArray.getJSONObject(i);
                String url     = childJSONObject.getString("url");
                String bkurl     = childJSONObject.getString("bkurl");
                String name = childJSONObject.getString("name");
                String status     = childJSONObject.getString("status");
                int ms     = childJSONObject.getInt("ms");
                String ext     = childJSONObject.getString("ext");
                String location     = childJSONObject.getString("location");
                String img     = childJSONObject.getString("img");
                String protocol = childJSONObject.getString("protocol");
                int port     = childJSONObject.getInt("port");
                raidaArray[i] = new RAIDA( url, bkurl, name, status, ms, ext, location, img, protocol, port);
            }   
        }catch(JSONException e){
            System.out.println("Json array error: " + e);
        }

    }

    public static boolean getTickets( int[] triad, String[] ans, int nn, int sn, int denomination ){
        // String echo1 = raidaArray[0].echo());
        //create a callable for each method
        Callable<Void> callable0 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[triad[0]].get_ticket( ans[0],nn,sn,denomination );
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable1 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[triad[1]].get_ticket( ans[1],nn,sn, denomination );
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable2 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[triad[2]].get_ticket( ans[2],nn,sn, denomination );
                    System.out.print(".");
                    return null;
                }
            };

        //add to a list
        List<Callable<Void>> taskList = new ArrayList<Callable<Void>>();
        taskList.add(callable0);
        taskList.add(callable1);
        taskList.add(callable2);
        //create a pool executor with 3 threads
        ExecutorService executor = Executors.newFixedThreadPool(3);

        try
        {
            //start the threads
            List<Future<Void>> futureList = executor.invokeAll(taskList);

            for(Future<Void> voidFuture : futureList)
            {
                try
                {
                    //check the status of each future.  get will block until the task
                    //completes or the time expires
                    voidFuture.get(100, TimeUnit.MILLISECONDS);
                }
                catch (ExecutionException e)
                {
                    System.out.println("Error executing task " + e.getMessage());
                }
                catch (TimeoutException e)
                {
                    System.out.println("Timed out executing task" + e.getMessage());
                }

            }

        }
        catch (InterruptedException ie)
        {
            //do something if you care about interruption;
        }
        //Check that all ticket status are good
        if ( raidaArray[triad[0]].lastTicketStatus.equalsIgnoreCase("ticket") && raidaArray[triad[1]].lastTicketStatus.equalsIgnoreCase("ticket") && raidaArray[triad[2]].lastTicketStatus.equalsIgnoreCase("ticket") )
        {
            return true;
        }else{
            return false;
        }

    }//end get Ticket

    public static void detectCoin( CloudCoin newCoin ){
        // String echo1 = raidaArray[0].echo());
        //create a callable for each method
        Callable<Void> callable0 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[0].detect( newCoin );
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable1 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[1].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable2 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[2].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable3 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[3].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable4 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[4].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable5 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[5].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable6 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[6].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable7 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[7].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable8 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[8].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable9 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[9].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable10 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[10].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable11 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[11].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable12 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[12].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable13 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[13].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable14 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[14].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable15 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[15].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable16 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[16].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable17 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[17].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable18 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[18].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable19 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[19].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable20 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[20].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable21 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[21].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable22 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[22].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable23 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[23].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable24 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[24].detect(newCoin);
                    System.out.print(".");
                    return null;
                }
            };

        //add to a list
        List<Callable<Void>> taskList = new ArrayList<Callable<Void>>();
        taskList.add(callable0);
        taskList.add(callable1);
        taskList.add(callable2);
        taskList.add(callable3);
        taskList.add(callable4);
        taskList.add(callable5);
        taskList.add(callable6);
        taskList.add(callable7);
        taskList.add(callable8);
        taskList.add(callable9);
        taskList.add(callable10);
        taskList.add(callable11);
        taskList.add(callable12);
        taskList.add(callable13);
        taskList.add(callable14);
        taskList.add(callable15);
        taskList.add(callable16);
        taskList.add(callable17);
        taskList.add(callable18);
        taskList.add(callable19);
        taskList.add(callable20);
        taskList.add(callable21);
        taskList.add(callable22);
        taskList.add(callable23);
        taskList.add(callable24);

        //create a pool executor with 3 threads
        ExecutorService executor = Executors.newFixedThreadPool(25);

        try
        {
            //start the threads
            List<Future<Void>> futureList = executor.invokeAll(taskList);

            for(Future<Void> voidFuture : futureList)
            {
                try
                {
                    //check the status of each future.  get will block until the task
                    //completes or the time expires
                    voidFuture.get(100, TimeUnit.MILLISECONDS);
                }
                catch (ExecutionException e)
                {
                    System.out.println("Error executing task " + e.getMessage());
                }
                catch (TimeoutException e)
                {
                    System.out.println("Timed out executing task" + e.getMessage());
                }

            }

        }
        catch (InterruptedException ie)
        {
            //do something if you care about interruption;
        }

    }//end detect

    /***
     * This sends an echo to each RAIDA server and records the results.
     */
    public static void setRaidaStatus(){
        // String echo1 = raidaArray[0].echo());
        //create a callable for each method
        Callable<Void> callable0 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[0].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable1 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[1].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable2 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[2].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable3 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[3].echo();
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable4 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[4].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable5 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[5].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable6 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[6].echo();
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable7 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[7].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable8 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[8].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable9 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[9].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable10 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[10].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable11 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[11].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable12 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[12].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable13 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[13].echo();
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable14 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[14].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable15 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[15].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable16 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[16].echo();
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable17 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[17].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable18 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[18].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable19 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[19].echo();
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable20 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[20].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable21 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[21].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable22 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[22].echo();
                    System.out.print(".");
                    return null;
                }
            };

        Callable<Void> callable23 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[23].echo();
                    System.out.print(".");
                    return null;
                }
            };
        Callable<Void> callable24 = new Callable<Void>()
            {
                @Override
                public Void call() throws Exception
                {
                    raidaArray[24].echo();
                    System.out.print(".");
                    return null;
                }
            };

        //add to a list
        List<Callable<Void>> taskList = new ArrayList<Callable<Void>>();
        taskList.add(callable0);
        taskList.add(callable1);
        taskList.add(callable2);
        taskList.add(callable3);
        taskList.add(callable4);
        taskList.add(callable5);
        taskList.add(callable6);
        taskList.add(callable7);
        taskList.add(callable8);
        taskList.add(callable9);
        taskList.add(callable10);
        taskList.add(callable11);
        taskList.add(callable12);
        taskList.add(callable13);
        taskList.add(callable14);
        taskList.add(callable15);
        taskList.add(callable16);
        taskList.add(callable17);
        taskList.add(callable18);
        taskList.add(callable19);
        taskList.add(callable20);
        taskList.add(callable21);
        taskList.add(callable22);
        taskList.add(callable23);
        taskList.add(callable24);

        //create a pool executor with 3 threads
        ExecutorService executor = Executors.newFixedThreadPool(25);

        try
        {
            //start the threads
            List<Future<Void>> futureList = executor.invokeAll(taskList);

            for(Future<Void> voidFuture : futureList)
            {
                try
                {
                    //check the status of each future.  get will block until the task
                    //completes or the time expires
                    voidFuture.get(100, TimeUnit.MILLISECONDS);
                }
                catch (ExecutionException e)
                {
                    System.out.println("Error executing task " + e.getMessage());
                }
                catch (TimeoutException e)
                {
                    System.out.println("Timed out executing task" + e.getMessage());
                }

            }

        }
        catch (InterruptedException ie)
        {
            //do something if you care about interruption;
        }

    }

    public static void showRaidaStatus(){
        for(int i =0; i<25; i++){
            System.out.println("Raida "+ i + " status:" +  raidaArray[i].status + ". " + raidaArray[i].ms + " ms "  );
        }//end for
    }//end show raida status

    public static void loadFiles(){
    }

    public static void executeChangeStateToBank(  )
    {
        // Try to leave current room.
        State nextState = stateManager.currentState.getExit( "bank" );
        stateManager.currentState = nextState;

    }

    public static void executeChangeStateToImport(  )
    {
        // Try to leave current room.
        State nextState = stateManager.currentState.getExit( "import" );
        stateManager.currentState = nextState;
    }

    public static void executeChangeStateToChest(  )
    {
        // Try to leave current room.
        State nextState = stateManager.currentState.getExit( "chest" );
        stateManager.currentState = nextState;

    }

    public static void executeChangeStateToFracked(  )
    {
        // Try to leave current room.
        State nextState = stateManager.currentState.getExit( "fracked" );
        stateManager.currentState = nextState;
    }

    private static int countBank(){
        int total = 0;
        return total;
    }//end count bank

    public static void moveCoin( String sn, String chestPathFrom, String chestPathTo){
        int total = 0;

    }//end count bank

    public static void newChest( String chestPath){
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream( chestPath ), "utf-8"));
            writer.write( "{\"CloudCoin\":[ ]}");
        }catch(IOException ioe){
            System.out.println(ioe);

        }
    }//end count bank

    public static void downloadDirectory( ){
        int total = 0;

    }//end count bank

    public static void checkRAIDAStatus( ){
        int total = 0;

    }//end count bank

    public static String[] generatePANs( ){
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[16];
        random.nextBytes(bytes);
        String[] pans = new String[25];
        for(int i = 0; i < 25; i++){
            random.nextBytes(bytes);
            pans[i] = bytesToHex( bytes );
        }//end for 25 pans
        return pans;
    }//end count bank

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void fixCoin( CloudCoin brokeCoin ){
        //Make an array of broken coins or go throug each if broken fix
        int mode = 1;
        boolean hasTickets = false;
        String fix_result = "";

        //brokeCoin.reportStatus();

        for (int guid_id = 0; guid_id < 25; guid_id++  ){//Check every Guid in the cloudcoin to see if it is fractured
            System.out.println("Inspecting RAIDA guid " + guid_id );

            FixitHelper fixer;
            if( brokeCoin.pastStatus[guid_id].equalsIgnoreCase("fail")){//This guid has failed, get tickets
                fixer = new FixitHelper( guid_id, brokeCoin );
                //fixer.reportCoinStatus();
                mode = 1;
                hasTickets = false;
                while( ! fixer.finnished ){
                    System.out.println("Triad "+ mode + " is " + brokeCoin.pastStatus[fixer.currentTriad[0]] +", "+brokeCoin.pastStatus[fixer.currentTriad[1]]+", "+brokeCoin.pastStatus[fixer.currentTriad[2]]);
                    if( fixer.currentTriadReady ){
                        hasTickets = getTickets( fixer.currentTriad, fixer.currentAns, brokeCoin.nn, brokeCoin.sn, brokeCoin.getDenomination() ); 
                        if( hasTickets ){
                            fix_result = raidaArray[guid_id].fix( fixer.currentTriad, raidaArray[fixer.currentTriad[0]].lastTicket, raidaArray[fixer.currentTriad[1]].lastTicket, raidaArray[fixer.currentTriad[2]].lastTicket, brokeCoin.ans[guid_id]);
                            if( fix_result.equalsIgnoreCase("success")){
                                //Save pan to an, stop looping, report sucess. 
                                brokeCoin.pastStatus[guid_id] = "pass";
                                fixer.finnished = true;
                                System.out.println("GUID fixed for guid " + guid_id );
                            }else{
                                System.out.println("Fix it command failed for guid  " + guid_id );
                                mode++;//beed to try another corner
                                fixer.setCornerToCheck( mode );
                            }//end if success fixing

                        }else{//No tickets, go to next triad
                            System.out.println("Get ticket commands failed for guid " + guid_id );
                            mode++;
                            fixer.setCornerToCheck( mode );
                        }//all the tickets are good. 
                    }else{//Triad will not work change it 
                        System.out.println("Triad "+mode+" not ready " + guid_id );
                        mode++;
                        fixer.setCornerToCheck( mode );
                    }//end if traid is ready
                }//end while still trying to fix
                //Finnished fixing 
            }//end if guid is broken and needs to be fixed
        }//end for each guid
    }//end fix coin

    /**
     * This method wraps long text in the console so that it is easier to read
     */
    public static String wrap(String longString) {
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

    public static String getHtml(String url_in) throws MalformedURLException, IOException {
        int c;
        URL cloudCoinGlobal = new URL(url_in);
        URLConnection conn = cloudCoinGlobal.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        InputStream input = conn.getInputStream();

        StringBuilder sb = new StringBuilder();

        while((( c = input.read()) != -1))
        {
            sb.append((char)c); 
        }//end while   
        input.close();
        return sb.toString();
    }//end get url

}//EndMain
