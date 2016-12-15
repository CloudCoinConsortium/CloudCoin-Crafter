import java.security.SecureRandom;
import java.io.*;
import java.util.Scanner;
import java.io.File;
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
    public String[] pastStatus;//fail, pass, error, unknown (could not connect to raida)
    public String ed; //Expiration Date
    public int hp;//HitPoints (1-25, One point for each server passed)
    public String aoid;//Account or Owner ID
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
            this.pastStatus = new String[25];
            for(int i = 0; i < 25; i++ ){
                pans[i] = generatePan();
                pastStatus[i]= "unknown";
            }
            break;
            case "keep"://keep the current ans the same
            this.pans = new String[25];
            this.pastStatus = new String[25];
            for(int i = 0; i < 25; i++ ){
                pans[i] = ans[i];
                pastStatus[i]= "unknown";
            }
            break;
        }

        this.ed = ed;
        this.hp = 0;
        this.aoid = aoid;

        this.fileName = getDenomination() +".CloudCoin." + this.nn +"."+ this.sn + ".";
    }

    public CloudCoin( String fileName){
        System.out.println("Loading file ./Bank/" + fileName);
        try{
            String fileContents = fileToString( "./Bank/"+fileName );
            System.out.println(fileContents);

            String[] parts = fileContents.split("<>");
            System.out.println("Length of parts " + parts.length);

            this.nn =  Integer.parseInt(parts[0]);
            this.sn =  Integer.parseInt(parts[1]);
            ans = new String[25];
            for(int i = 0; i < 25; i++){
                this.ans[i] =  parts[i+2];
                // System.out.println("Part " + (i+2) + ": " + parts[i+2] );
            }//end for each an
            pans = new String[25];
            for(int j = 0; j< 25; j++){
                this.pans[j] =  parts[j+2+25];
                //  System.out.println("Part " + (j+2+25) + ": " + parts[j+2+25] );
            }//end for each an
            this.ed =  parts[52];
            this.hp =  Integer.parseInt(parts[53]);
            this.pastStatus = new String[25];
            for(int k = 0; k < 25; k++){
                this.pastStatus[k] =  parts[k+54];
                // System.out.println("Part " + (i+2) + ": " + parts[i+2] );
            }//end for each an
            this.aoid =  parts[79];
            this.fileName = getDenomination() +".CloudCoin." + this.nn +"."+ this.sn + ".";
        }catch(IOException e){
            System.out.println(e);
        }

    }//end new cc based on file content

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
        String coinText = nn +"<>";
        coinText += sn +"<>";
        for(int ii = 0; ii< 25; ii++){
            coinText += ans[ii] +"<>";
        }//end for each an

        for(int iii = 0; iii< 25; iii++){
            coinText += pans[iii] +"<>";
        }//end for each an
        coinText += ed +"<>";
        coinText += hp +"<>";
        for(int i = 0; i< 25; i++){
            coinText += pastStatus[i] +"<>";
        }//end for each an
        coinText += aoid +"<>";

        BufferedWriter writer = null;
        try
        {
            writer = new BufferedWriter( new FileWriter( "./Bank/" + this.fileName + extension ));
            System.out.println("\nSaving Coin file to Bank/" + this.fileName + extension );
            writer.write( coinText );
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

    public boolean deleteCoin( String extension ){
        boolean deleted = false;
        File f  = new File( "./Bank/" + this.fileName + extension);
        try {
            deleted = f.delete();
            if(deleted){
                System.out.println(f.getName() + " is deleted!");
            }else{
                System.out.println("Delete operation is failed.");
            }//end else
        }catch(Exception e){

            e.printStackTrace();

        }
        return deleted;
    }//end delete file

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

    public void calculateHP(){
         this.hp = 0;
        for( int i = 0; i< 25; i++){
            if( this.pastStatus[i].equalsIgnoreCase("pass")   )
            { 
                this.hp++;
            }
        }

    }//End calculate hp
    
     public void reportStatus(){
System.out.println("NN:"+this.nn+", SN: " + this.sn );
        for( int i = 0; i< 25; i++){
                System.out.println( i +"'s status is "+ this.pastStatus[i] );
        }

    }//End report status
}
