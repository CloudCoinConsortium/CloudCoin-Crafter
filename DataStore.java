import java.nio.charset.Charset;
import java.io.IOException;
import java.io.File;
import java.util.Collection;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * All Data tracks the entire contents of the file system used to by CloudCoin Crafter
 * 
 * The folder structure is as follows: <a href="https://www.cloudcoin.co/appdesign.html">CloudCoin Crafter</a>.
 * 
 * @author Sean H. Worthington
 * @version 11/28/2016
 */
public class DataStore
{
    /*All modes */
   // public String cursorMode;//Could be "drag", "click", "doubleclick", "hover", "rightclick", "arrow"

    /*Bank Mode */
    public String[] bankChestFileNames;
   // public int[]  bankChestCoinCounts;//Based on the file name
   // public int[][] bankChestCoinRegister;//RaggedArray
    
   // public String[] bankJpegFileNames;
   // public int[] bankJpegRegister;//there are five indexes 0=1s, 1= 5s, 2=25s, 3=100s 4=250s
   // public int bankJpegsTotal;


    /*Import  Mode */
    public String[] importChestFileNames;
   // public int[] importChestCoinCounts;
   // public int[][] importChestRegister;//RaggedArray

    //public String[] importJpegFileNames;
    //public int[] importJpegRegister;//there are five indexes 0=1s, 1= 5s, 2=25s, 3=100s 4=250s


    public String highSecurityChestName;
   // public String[] simpleSecurityFileName;
   // public String[] passphraseSecurityFileName;
   // public String passphrase;


    //Fracked Mode
    public String frackedChestName;//there are five indexes 0=1s, 1= 5s, 2=25s, 3=100s 4=250s
    //public int[] frackedRegister;//there are five indexes 0=1s, 1= 5s, 2=25s, 3=100s 4=250s
    //public int[] cloudCoinSerialNumbersPerDenomination;//there are five indexes 0=1s, 1= 5s, 2=25s, 3=100s 4=250s
    public String frackedChestBeingFixedName;// The one file that is being scanned.

    //Chest Mode
 
    //Export Mode
    //Just methods?

    //Vault Mode
    //Phase II
    //Lost mode
    //Phase II

    public DataStore(){
        //This will go out and check all the folders and load files into them accordingly. 
       // cursorMode = "arrow";
        bankChestFileNames = selectAllFileNamesInFolder("Bank/Chests/");
       // bankChestCoinCounts = selectCoinTotalsInChests( bankChestFileNames );
        //bankJpegFileNames = fileNames("Bank/Jpegs/");
        //bankJpegRegister =  countChestCoinsArray(bankJpegFileNames);//there are five indexes 0=1s, 1= 5s, 2=25s, 3=100s 4=250s
        //bankJpegsTotal = addUpRegistry( bankJpegRegister );
       // bankChestCoinRegister = register( "Bank/Chests/");
        //bankTotalValue =  totalInAllChests + bankJpegsTotal;

        //Import Mode
        importChestFileNames = selectAllFileNamesInFolder("Bank/Income/Chests/");
       // importChestCoinCounts = selectCoinTotalsInChests( importChestFileNames );

        //importJpegFileNames = fileNames("Bank/Income/Jpegs/");;
        //importJpegRegister = calcRegistry( importJpegFileNames );//there are five indexes 0=1s, 1= 5s, 2=25s, 3=100s 4=250s

        //totalValueMovedToBank = 0;
        //totalValueMovedToFracked = 0;
        //totalValueMovedToCounterfeit =0;
        //totalValueMoved = 0;

        highSecurityChestName = selectFirstFileNameInFolder("Bank/Income/Authenticating/HighSecurity/Chests/");
       // simpleSecurityFileName = fileNames("Bank/Income/Chests/");
       // passphraseSecurityFileName = fileNames("Bank/Income/Chests/");
        
 
        //Fracked Mode
        frackedChestName= selectFirstFileNameInFolder("Bank/Fracked/");//there are five indexes 0=1s, 1= 5s, 2=25s, 3=100s 4=250s
        frackedChestBeingFixedName= selectFirstFileNameInFolder("Bank/Fracked/BeingFixed");//there are five indexes 0=1s, 1= 5s, 2=25s, 3=100s 4=250s
        //frackedRegister = calcRegistry( frackedCoinFileNames );//there are five indexes 0=1s, 1= 5s, 2=25s, 3=100s 4=250s
        //cloudCoinSerialNumbersPerDenomination;//there are five indexes 0=1s, 1= 5s, 2=25s, 3=100s 4=250s
       // try{
      //  frackedBeingFixedFileName = readFile("Bank/Fracked/BeingFixed/fixed.txt" );// The one file that is being scanned.
      //     }catch(IOException ioe){
       //      System.out.println(ioe);
       // }
        //

    }//end constructor
    
    public void refreshFileList(){
     bankChestFileNames = selectAllFileNamesInFolder("Bank/Chests/");
     importChestFileNames = selectAllFileNamesInFolder("Bank/Income/Chests/");
     frackedChestName= selectFirstFileNameInFolder("Bank/Fracked/");
    }//end refresh 
    
    
    
    public int[][] selectRegister( String chestPath ){
        int n = 5;//Need to open files and look in them using JSON
     int[][] raggedArray = new int[n][];
     for (int i=0; i<raggedArray.length; i++) {
         raggedArray[i] = new int[i+1];
        }//end for
        return raggedArray;
    }//end register
    

    public String[] selectAllFileNamesInFolder(String directoryPath) {
        File dir = new File(directoryPath);
        Collection<String> files  =new ArrayList<String>();
        if(dir.isDirectory()){
            File[] listFiles = dir.listFiles();

            for(File file : listFiles){
                if(file.isFile()) {
                    files.add(file.getName());
                }
            }
        }

        return files.toArray(new String[]{});
    }

        public String selectFirstFileNameInFolder(String directoryPath) {
        File dir = new File(directoryPath);
        Collection<String> files  =new ArrayList<String>();
        if(dir.isDirectory()){
            File[] listFiles = dir.listFiles();

            for(File file : listFiles){
                if(file.isFile()) {
                    files.add(file.getName());
                }
            }
        }

        String[] allFineNames = files.toArray(new String[]{});
        return allFineNames[0];
    }

    public int selectCoinTotalInChest( String filename){
        int iend = filename.indexOf(".");
        String subString= filename.substring(0 , iend);
          int  coinsInChest = Integer.parseInt( subString ); 
        return coinsInChest;
    }//end get Coin Count From File Name
    
    
    public int[] selectCoinTotalsInChests(String[] bankChestFileNames){
        String subString ="";
        int[ ] coinsInChest = new int[ bankChestFileNames.length ];
        for(int i = 0; i < bankChestFileNames.length; i++ ){  
            int iend = bankChestFileNames[i].indexOf(".");
            if (iend != -1) 
              subString= bankChestFileNames[i].substring(0 , iend);
            coinsInChest[i] = Integer.parseInt( subString ); 
        }//end for
        return coinsInChest ;
    }//end count chest coins

 public String fileToString(String pathname) throws IOException {

    File file = new File(pathname);
    StringBuilder fileContents = new StringBuilder((int)file.length());
    Scanner scanner = new Scanner(file);
    String lineSeparator = System.getProperty("line.separator");

    try {
        while(scanner.hasNextLine()) {
            fileContents.append(scanner.nextLine() + lineSeparator);
            
        }
      
    } finally {
        scanner.close();
    }
    return fileContents.toString();
}

}
