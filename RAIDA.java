import java.io.BufferedReader;
import java.io.IOException;
import java.io.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.*;

/**
 * Write a description of class RAIDA here.
 * 
 * @author Sean Worthington
 * @version (a version number or a date)
 */
public class RAIDA
{
    // instance variables
    public int[] trustedServes = new int[8];
    public int[] trustedTriad1;
    public int[] trustedTriad2;
    public int[] trustedTriad3;
    public int[] trustedTriad4;

    public int RAIDANumber;
    public String url;
    public String bkurl; //backup url
    public String name; 
    public String status; //Unknown, slow or ready
    public long ms; //milliseconds
    public long dms = 0; //ms to detect
    public String ext;  //file extension php or aspx
    public String location; //country
    public String img; //img url
    public String protocol; //http or https
    public int port; //80 or 443
    public String lastJsonRaFromServer = null;
    public String lastTicket = null;
    public String fullUrl;
    public String lastDetectStatus = "notdetected";//error, notdetected, pass, fail
    //public String lastDetectSn = null;//error, unknown, pass, fail
    public String lastTicketStatus = "empty";//ticket, fail, error
    public String lastFixStatus = "empty";//ticket, fail, error

    /**
     * Constructor for objects of class RAIDA
     */
    public RAIDA(String url, String bkurl, String name, String status, int ms, String ext, String location, String img, String protocol, int port )
    {
        // initialise instance variables
        String raidaNumberString = name.replace("RAIDA","");
        RAIDANumber = Integer.parseInt( raidaNumberString );
        //Calculate the Trusted Servers
        // Calculate the 8 trusted servers that are directly attached to broken RAIDA
        trustedServes[0] = (RAIDANumber - 6) % 25;//Trusted server 1 is the id of your servers minus 6 mod 25.
        trustedServes[1] = (RAIDANumber - 5) % 25;
        trustedServes[2] = (RAIDANumber - 4) % 25;
        trustedServes[3] = (RAIDANumber - 1) % 25;
        trustedServes[4] = (RAIDANumber + 1) % 25;
        trustedServes[5] = (RAIDANumber + 4) % 25;
        trustedServes[6] = (RAIDANumber + 5) % 25;
        trustedServes[7] = (RAIDANumber + 6) % 25;

        trustedTriad1 = new int[]{trustedServes[0] , trustedServes[1] , trustedServes[3] };
        trustedTriad2 = new int[]{trustedServes[1] , trustedServes[2] , trustedServes[4] };
        trustedTriad3 = new int[]{trustedServes[3] , trustedServes[5] , trustedServes[6] };
        trustedTriad4 = new int[]{trustedServes[4] , trustedServes[6] , trustedServes[7] };

        this.url = url;
        this.bkurl = bkurl;
        this.name = name;
        this.status = status;
        this.ms = (int)ms;
        this.ext = ext;
        this.location = location;
        this. img = img;
        this.protocol =  protocol;
        this.port = port;

        if( this.port !=80 && this.port != 443){
            this.fullUrl = this.protocol +"://"+ this.url + ":"+ this.port +"/service/";
        }//if port not 80 or 443
        else{
            this.fullUrl = this.protocol +"://"+ this.url + "/service/";
        }//end if the port needs to be listed
    }//RAIDA

    //Methods
    public String echo(){
        String html ="error";
        String url = this.fullUrl + "echo." + this.ext;
        Instant before = Instant.now();
        try{
            html = getHtml(url);
        }catch( IOException ex ){
            System.out.println( status );
            this.status = "error";
            return "error";
        }
        Instant after = Instant.now();
        //System.out.println( html );
        boolean isReady = html.contains("ready");
        this.ms = Duration.between(before, after).toMillis();
        if(isReady){ 
            this.status = "ready";
            return "ready";}else{
            this.status = "error";
            return "error";
        }
    }//end echo

    public void detect(CloudCoin cc ){
        if( this.status.equals("ready")){

            String html ="error";
            String url = this.fullUrl + "detect." + this.ext + "?nn=" + cc.nn + "&sn=" + cc.sn + "&an=" + cc.ans[RAIDANumber] + "&pan=" + cc.pans[RAIDANumber]  + "&denomination=" + cc.getDenomination();
            // System.out.print( ".  Raida number " + RAIDANumber );
            Instant before = Instant.now();
            try{
                html = getHtml(url);
                //  System.out.println( html );
            }catch( IOException ex ){
                System.out.println( ex );
                lastDetectStatus = "error";
            }
            Instant after = Instant.now();
            this.lastJsonRaFromServer = html;
            this.dms = Duration.between(before, after).toMillis();
            if( html.contains("pass") ){ 
                lastDetectStatus = "pass";
            }else if( html.contains("fail") ){  lastDetectStatus = "fail"; 
            }else { lastDetectStatus = "error"; }
        }//end if status not ready. 
    }//end detect

    public void get_ticket( String an, int nn, int sn, int denomination  ){
        this.lastTicket = "none";
        String url = fullUrl + "get_ticket."+this.ext+"?nn="+nn+"&sn="+sn+"&an="+an+"&pan=" +an+ "&denomination="+denomination;
        System.out.println( url );

        String html = "";
        Instant before = Instant.now();
        try{
            html = getHtml(url);
            JSONObject o = new JSONObject( html );
            this.lastTicketStatus = o.getString("status");
            String message = o.getString("message");
            if (this.lastTicketStatus.equalsIgnoreCase("ticket") ){
                this.lastTicket = message;

            }//end if
            //  System.out.println( html );
        }catch( JSONException ex ){
            System.out.println( "Error in RAIDA get_ticket() " +ex );

        }catch( MalformedURLException ex ){
            System.out.println( "Error in RAIDA get_ticket() " +ex );
        } catch( IOException ex ){
            System.out.println( "Error in RAIDA get_ticket() " +ex );
        }
        Instant after = Instant.now();
        this.lastJsonRaFromServer = html;
        this.dms = Duration.between(before, after).toMillis();
        System.out.println(html);
    }//end get ticket

    public String fix( int[] ans, String m1,String m2, String m3, String pan ){
        this.lastFixStatus = "error"; 
        
        int f1 = ans[0];
        int f2 = ans[1];
        int f3 = ans[2];
        String url = fullUrl;
        url += "fix."+this.ext+"?fromserver1="+f1+"&message1="+m1+"&fromserver2="+f2+"&message2="+m2+"&fromserver3="+f3+"&message3="+m3+"&pan="+pan;
        System.out.println( url );

        /*try {
            System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        String html = "";
        Instant before = Instant.now();
        try{
            html = getHtml(url);
            System.out.println( html );
        }catch( MalformedURLException ex ){//quit
            
            System.out.println( "Error in RAIDA fix() " +ex );
        } catch( IOException ex ){
            System.out.println( "Error in RAIDA fix() " +ex );
        }
        Instant after = Instant.now();
        this.lastJsonRaFromServer = html;
        this.dms = Duration.between(before, after).toMillis();
        if( html.contains("success") ){ 
            this.lastFixStatus = "success"; 
        }
        this.lastJsonRaFromServer = html;
        this.dms = Duration.between(before, after).toMillis();

        return this.lastFixStatus;
    }//end fixit

    public String getHtml(String url_in) throws MalformedURLException, IOException {
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

}
