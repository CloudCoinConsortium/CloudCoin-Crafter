import java.util.Arrays;
import java.security.SecureRandom;
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileNotFoundException;
//import java.io.BufferedWriter;
//import java.io.BufferedReader;
//import java.io.Writer;
//import java.io.OutputStreamWriter;
//import java.io.FileOutputStream;
//import java.io.IOException;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;
//import java.io.BufferedReader;
//import java.io.IOException;
import java.io.*;
//import java.io.InputStreamReader;
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
    public static DataStore storage = new DataStore();
    public static String raidaStatus = "uuuuuuuuuuuuuuuuuuuuuuuuu";//All RAIDAs status is 'u' for unknown.
    
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
      System.out.println( raidaArray[0].echo());
/*
        //Create an array of RAIDA with the Directory information
        org.json.JSONObject[] obj = new org.json.JSONObject[25];
        for(int i =0; i< 25; i++){
            try{
                obj[i] = new org.json.JSONObject( jsonServers[i] );
                }catch(JSONException ex){
                System.out.println("error reading JSON:" + ex);
            }
        }//end for each json obje
        
        
        

        for(int i = 0; i <25; i++){
         try{
            raidaArray[i] = new RAIDA( i, obj[i].getString("url"), obj[i].getString("bkurl"), obj[i].getString("name"), obj[i].getString("status"), obj[i].getString("ms"), obj[i].getString("ext"), obj[i].getString("location"), obj[i].getString("img"), obj[i].getString("protocol"), obj[i].getString("port"));
            }catch(JSONException ex){
              System.out.println("error reading JSON:" + ex);
            }  
                
        }//end for
        

        System.out.println("From echo: " + raidaArray[0].echo() );
*/

        //Load up from files
        StateManager stateManager = new StateManager();

        //Start the Program. 
        run();

        System.out.println("Thank you for using CloudCoin Crafter. Goodbye.");
    }//End main

    /**
     * Print out the opening message for the player. 
     */
    public static void printWelcome() {
        System.out.println("Welcome to CloudCoin Crafter Opensource and free. No gaurantees made.");
    }

    
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
                     String protocol     = childJSONObject.getString("protocol");
                     int port     = childJSONObject.getInt("port");
                     
                     raidaArray[i] = new RAIDA( i, url, bkurl, name, status, ms, ext, location, img, protocol, port);
      
                     //System.out.println("Record " + i + " is "+ url );
                }   
           }catch(JSONException e){
           System.out.println("Json array error: " + e);
          }
    }
    
    public static void run() {
        boolean restart = false;
        System.out.println( stateManager.currentState.getLongDescription() );
        while( ! restart )
        {
            System.out.println( "----------------------------------------------------------");
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
                
               case "list chests":
                        showChests();
                break;
                

                case "click loadfiles":
                    System.out.println("What is the path and name of the file you want to load?");
                    String loadFileName = reader.readString( );   
                    String loadFilePath = "W:\\MoneyJpegs\\testchests\\";
                    //int coinCount = getTotal();
                   // String new fileName = coinCount +".CloudCoin.New"+ rand.nextInt(5000) + "";
                   try{
                       System.out.println("Trying to load: " + loadFilePath + loadFileName );
                          loadJSON( loadFilePath + loadFileName );
                   }catch( FileNotFoundException | JSONException ex)
                   {
                          System.out.println("Error: " + ex);
                    }
                    //loadJSON( loadFilePath + loadFileName, "W:\\Code\\Java\\CloudCoin Crafter\\Bank\\Income\\Chests\\" + loadFileName + ".Chest");
                    //storage.refreshFileList();
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

    public static void showChests(){
      
        //To show chests, you must be in either Bank mode or Import mode
        String[] chestFileNames;
        if( stateManager.currentState.description == "bank mode"  ){
           chestFileNames = storage.bankChestFileNames;
        }else{
            chestFileNames = storage.importChestFileNames;
        }//end if else
        
        if( chestFileNames.length == 0){
        System.out.println("There are no chests in this mode.");
        }else
        {
            for(int i = 0; i< chestFileNames.length; i++){
             int[] chestTotals = storage.selectCoinTotalsInChests( chestFileNames );   
             System.out.println( "Chest " + i + " has " + chestTotals[i] + " CloudCoins in it.");
            }//end for every file name
            System.out.println("");
        
        }//end if
    }
    private static int countBank(){
        int total = 0;
        return total;
    }//end count bank

    public static int countChest( String chestPath){
        int total = 0;
        return total;
    }//end count bank

    public static void moveChest( String chestPathFrom, String chestPathTo ){
        try{

           File afile =new File( chestPathFrom );

           if(afile.renameTo(new File( chestPathTo + afile.getName()))){
            System.out.println("Chest moved successful!");
           }else{
            System.out.println("Chest failed to move!");
           }

        }catch(Exception e){
            e.printStackTrace();
        }//end try

    }//end count bank

    public static void moveCoin( String sn, String chestPathFrom, String chestPathTo){
        int total = 0;

    }//end count bank

    public static void deleteChest( String sn, String chestPathFrom, String chestPathTo){
         try{
           File afile =new File( chestPathFrom );
           afile.delete();
        }catch(Exception e){
            e.printStackTrace();
        }//end try

    }//end count bank

    public static void tagChest( String chestPath, String tag){
        File oldfile =new File(chestPath);
        String[] yourArray = chestPath.split(".");
        String newFileName = yourArray[0] + "." + yourArray[1] + "." + yourArray[2] + "." + yourArray[3]+ "." + tag + "." + yourArray[5];
        File newfile =new File( newFileName );

        if(oldfile.renameTo(newfile)){
            System.out.println("Rename succesful");
        }else{
            System.out.println("Rename failed");
        }

    }//end count bank

    public static void newChest( String chestPath){
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream( chestPath ), "utf-8"));
         writer.write( "{\"CloudCoin\":[ ]}");
        }catch(IOException ioe){
            System.out.println(ioe);
        
        }
    }//end count bank

    public static void dequeueChest( String chestPath){
        int total = 0;
        
    }//end count bank

    public static void enqueueChest( CloudCoin newCoin){
        int total = 0;

    }//end count bank

    public static void downloadDirectory( ){
        int total = 0;

    }//end count bank

    public static void checkRAIDAStatus( ){
        int total = 0;

    }//end count bank
    
    public static void loadJSON( String jsonfile) throws FileNotFoundException, JSONException{
    String jsonData = "";
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new FileReader( jsonfile ));
            while ((line = br.readLine()) != null) {
                jsonData += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //System.out.println("File Content: \n" + jsonData);
        //System.out.println( "[{ at: " + jsonData.indexOf("[{")  );
        //System.out.println( "}] at: " + jsonData.indexOf("}]")  );
        jsonData = jsonData.substring(  jsonData.indexOf("[{")+1 , jsonData.indexOf("}]")+1   ) ;
        jsonData = jsonData.replace("},", "}<>" );//Put diamond so we can split on it
        String[] jsonCoins = jsonData.split("<>");
        
        for(int i = 0; i < jsonCoins.length; i++ ){
            //save to bank file .import 
            //Change source named to .movedToBank
            //Roll through each one and detect
            //If fracked try to fix.
            //Change file name to .bank or .fracked or .counterfeit
            
          }//end for
        
        //org.json.JSONObject obj = new org.json.JSONObject(jsonData);
        //System.out.println("nn: " + obj.getString("nn"));
        //System.out.println("sn: " + obj.getString("sn"));
        
        //System.out.println("an: " + obj.getString("an"));
        //System.out.println("ed: " + obj.getString("ed"));
        //System.out.println("aoid: " + obj.getString("aoid"));
        //System.out.println("ed: " + obj.getJSONObject("CloudCoin"));
    }//end json test
    
    
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
    
    public static void authenticateCoin( String kjkj ){
        int total = 0;

    }//end count bank

    public static void evaluateResults( String[] CoinResults ){
        int total = 0;

    }//end count bank

    public static void fixCoin( String kjkj ){
        int total = 0;

    }//end count bank

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
