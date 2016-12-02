import java.security.SecureRandom;
import java.io.*;
import java.util.Scanner;
/**
 * Creats a CloudCoin
 * 
 * @author Sean H. Worthington
 * @version 11/24/2016
 */
public class CloudCoin
{
    // instance variables - replace the example below with your own
    public int nn;//Network Numbers
    public int sn;//Serial Number
    public String[] ans ;//Authenticity Numbers
    public String[] pans;//Proposed Authenticty Numbers
    public String ed; //Expiration Date
    public int lhs[];//Last Known Health Status (one for each server)
    public String aoid;//Account or Owner ID
    public String coinText;
    public String fileName;

    /**
     * Constructor for objects of class CloudCoin
     */
    public CloudCoin(int nn, int sn, String[] ans, String ed, String aoid, String security )
    {
        // initialise instance variables
        this.nn = nn;
        this.sn = sn;     
        this.ans = ans;
        switch(security){
            case "change"://change pans
            this.pans = new String[25];
            for(int i = 0; i < 25; i++ ){
                pans[i] = generatePan();
            }
            break;
            case "keep"://keep the current ans the same
            this.pans = new String[25];
            for(int i = 0; i < 25; i++ ){
                pans[i] = ans[i];
            }
            break;
        }

        this.ed = ed;
        this.lhs = lhs;
        this.aoid = aoid;

        coinText = nn +"<>";
        coinText += sn +"<>";
        for(int ii = 0; ii< 25; ii++){
            coinText += ans[ii] +"<>";
        }//end for each an

        for(int iii = 0; iii< 25; iii++){
            coinText += pans[iii] +"<>";
        }//end for each an
        coinText += ed +"<>";
        coinText += lhs +"<>";
        coinText += aoid;

        this.fileName = getDenomination() +".CloudCoin." + this.nn +"."+ this.sn + ".";
    }
    /**
     * Returns the denomination of the money based on the serial number
     * 
     * @param  sn Serial Numbers 
     * @return  1, 5, 25, 100, 250
     */
    public int getDenomination() 
    {
        int nom = 0;
        if(this.sn < 1 ){  nom = 0;}
        else if(this.sn < 2097153) {  nom = 1; } 
        else if (this.sn < 4194305) { nom = 5; } 
        else if (this.sn < 6291457) { nom = 25; } 
        else if (this.sn < 14680065) { nom = 100; } 
        else if (this.sn < 16777217) { nom = 250; } 
        else { nom = '0'; }
        return nom;
    }

    private String generatePan()
    {
        String AB = "0123456789ABCDEF";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder( 25 );
        for( int i=0 ; i<32 ; i++ ) 
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    public boolean saveCoin( String extension ){
        boolean goodSave = false;
        BufferedWriter writer = null;
        try
        {
            writer = new BufferedWriter( new FileWriter( "./Bank/" + this.fileName + extension ));
            System.out.println("Saving Coin file to Bank/" + this.fileName + extension );
            writer.write( this.coinText );
            goodSave = true;

        }
        catch ( IOException e)
        {
        }
        finally
        {
            try
            {
                if ( writer != null)
                    writer.close( );
            }
            catch ( IOException e)
            {
            }
        }
        return goodSave;
    }

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
