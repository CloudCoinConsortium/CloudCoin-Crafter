import java.util.Arrays;
import java.security.SecureRandom;
import java.util.concurrent.*;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;
import java.io.*;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONArray;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * All Data tracks the entire contents of the file system used to by CloudCoin Crafter
 * 
 * The folder structure is as follows: <a href="https://www.cloudcoin.co/appdesign.html">CloudCoin Crafter</a>.
 * 
 * @author Sean H. Worthington
 * @version 11/30/2016
 */
public class FileUtils
{
    // instance variables - replace the example below with your own
    public KeyboardReader reader = new KeyboardReader();
    public CloudCoin[] newCoins; 
    /**
     * Constructor for objects of class FileMover
     */
    public FileUtils()
    {
        // initialise instance variables
        
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  fileName   The name of the file to import into the import mode
     * @return    income money
     */
    public void loadIncome( String loadFilePath, String security )
    {  // put your code here
       System.out.println("Trying to load: " + loadFilePath );
       String incomeJson = ""; 
       // String new fileName = coinCount +".CloudCoin.New"+ rand.nextInt(5000) + "";
        try{
            incomeJson = loadJSON( loadFilePath );
        }catch( IOException ex ){
            System.out.println( "error " + ex );
        }
        String ans[] = new String[25];
        JSONArray incomeJsonArray;
        
        try{
          JSONObject o = new JSONObject( incomeJson );
          incomeJsonArray = o.getJSONArray("CloudCoin");
          newCoins = new CloudCoin[incomeJsonArray.length()];
          for (int i = 0; i < incomeJsonArray.length(); i++) {  // **line 2**
             JSONObject childJSONObject = incomeJsonArray.getJSONObject(i);
                int nn     = childJSONObject.getInt("nn");
                int sn     = childJSONObject.getInt("sn");
                JSONArray an = childJSONObject.getJSONArray("an");
                // take the elements of the json array
             for(int ii = 0; ii < 25; ii++ ){
                //System.out.println("AN 1:" + an.getString(ii)  );
                ans[ii] = an.getString(ii);
                }//end for ans  
                String ed     = childJSONObject.getString("ed");
                String aoid     = childJSONObject.getString("aoid");
                newCoins[i] = new CloudCoin( nn, sn, ans, ed, aoid, security );//security should be change or keep for pans.
                System.out.println( "Loading Coin: nn " + nn + ", sn " + sn + ", ed " + ed + ", aoid " + aoid );
            }//end for each coin
 
        }catch( JSONException ex)
        {
           System.out.println("Error: " + ex);
           
        }//try 

    }//end load income
    

    public String loadJSON( String jsonfile) throws FileNotFoundException {
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
        return jsonData;
    }//end json test
    
    public static boolean renameFileExtension
  (String source, String newExtension)
  {
    String target;
    String currentExtension = getFileExtension(source);

    if (currentExtension.equals("")){
      target = source + "." + newExtension;
    }
    else {
      target = source.replaceFirst(Pattern.quote("." +
          currentExtension) + "$", Matcher.quoteReplacement("." + newExtension));

    }
    return new File(source).renameTo(new File(target));
  }

  public static String getFileExtension(String f) {
    String ext = "";
    int i = f.lastIndexOf('.');
    if (i > 0 &&  i < f.length() - 1) {
      ext = f.substring(i + 1);
    }
    return ext;
  }

    
}
