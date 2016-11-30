import java.io.BufferedReader;
import java.io.IOException;
import java.io.*;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Write a description of class RAIDA here.
 * 
 * @author Sean Worthington
 * @version (a version number or a date)
 */
public class RAIDA
{
    // instance variables
    public int RAIDANumber;
    public int[] trustedServers = new int[4];
    public String url;
    public String bkurl; //backup url
    public String name; 
    public String status; 
    public int ms; //milliseconds
    public String ext;  //file extension php or aspx
    public String location; //country
    public String img; //img url
    public String protocol; //http or https
    public int port; //80 or 443
    public String lastMessage;
    public String lastTicket;
    public String fullUrl;

    /**
     * Constructor for objects of class RAIDA
     */
    public RAIDA(int RAIDANumber, String url, String bkurl, String name, String status, int ms, String ext, String location, String img, String protocol, int port )
    {
        // initialise instance variables
        this.RAIDANumber = RAIDANumber;
        this.trustedServers[0]= RAIDANumber - 5;
        this.trustedServers[1]= RAIDANumber - 1;
        this.trustedServers[2]= RAIDANumber + 1;
        this.trustedServers[3]= RAIDANumber + 5;
        //Calculate the Trusted Servers
        if(  this.trustedServers[0] < 0 ){ trustedServers[0] += 25;  }//end if 
        if(  this.trustedServers[1] < 0 ){ trustedServers[1] += 25; }//end if 
        if(  this.trustedServers[2] > 24 ){ trustedServers[2] -= 25;  }//end if 
        if(  this.trustedServers[3] > 24 ){ trustedServers[3] -= 25;  }//end if 
        
        this.url = url;
        this.bkurl = bkurl;
        this.name = name;
        this.status = status;
        this.ms = ms;
        this.ext = ext;
        this.location = location;
        this. img = img;
        this.protocol =  protocol;
        this.port = port;
        this.lastMessage ="No Message";
        this.lastTicket = "None";
        
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
        try{
             html = getHtml(url);
           }catch( IOException ex ){
              status = "error " + ex;
              System.out.println( status );
            }
        System.out.println( html );
        return html;
    }//end echo

    public String test(){
       String status = "unknown";
        String url = fullUrl + "test." + this.ext;
        System.out.println( url );
       return status;
    }//end echo
    
    public String detect(String nn, String sn, String an, String pan, String denomination){
     String status = "unkown";
        String url = fullUrl + "detect" + this.ext + "?nn=" + nn + "&sn=" + sn + "&an=" + an + "&pan=" + pan + "&denomination=" + denomination;
        System.out.println( url );
     return status;
    }//end echo
    
    public String get_ticket(String nn, String sn, String an, String pan, int denomination){
      String status = "unkown";
      String url = fullUrl + "get_ticket" + this.ext +"?nn=" + nn + "&sn=" + sn + "&an=" + an + "&pan=" + pan + "&denomination=" + denomination;
        System.out.println( url );
      return status;
    }//end echo
    
    public String fix( int s1, String m1, int s2, String m2, String pan  ){
      String status = "unkown";
      String url = fullUrl + "fix" + this.ext + "?fromserver1="+ s1 +"&message1=" + m1 + "&fromserver2="+s2+"&message2=" + m2 + "&pan=" + pan ;
        System.out.println( url );
      return status;
    }//end echo
    
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
