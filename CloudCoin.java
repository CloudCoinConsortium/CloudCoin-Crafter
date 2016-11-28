import java.security.SecureRandom;
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
    public String ownerStrings; //key:value pairs seperated by <c>Key:Value</c>
    

    /**
     * Constructor for objects of class CloudCoin
     */
    public CloudCoin(int nn, int sn, String[] ans, String[] pans, String ed, int[] lhs, String aoid, String ownerStrings )
    {
        // initialise instance variables
        this.nn = nn;
        this.sn = sn;
        this.ans = ans;
        this.pans = new String[25];
        for(int i = 0; i < 25; i++ ){
          pans[i] = generatePan();
        }
        this.ed = ed;
        this.lhs = lhs;
        this.aoid = aoid;
        this.ownerStrings = ownerStrings;
    }

    /**
     * Returns the denomination of the money based on the serial number
     * 
     * @param  sn Serial Numbers 
     * @return  1, 5, 25, 100, 250
     */
    public int getDenomination(int sn)
    {
        int nom = 0;
        if (sn < 2097153) { 
            nom = 1; } 
            else if (sn < 4194305) { nom = 5; } 
            else if (sn < 6291457) { nom = 25; } 
            else if (sn < 14680065) { nom = 100; } 
            else if (sn < 16777217) { nom = 250; } 
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
}
