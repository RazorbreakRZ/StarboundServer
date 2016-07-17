/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my_ip;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author Razorbreak
 */
public class My_ip {
    private String publicIP = "0.0.0.0";
    /**
     * @param args the command line arguments
     */
    /*public static void main(String[] args) {
        My_ip cls = new My_ip();
        System.out.print(cls.get_public_ip());
    }*/
    public My_ip(){}
    
    public String get_public_ip(){
        try {
            URL tempURL = new URL("http://myip.xname.org/");
            HttpURLConnection tempConn = (HttpURLConnection)tempURL.openConnection();
            InputStream tempInStream = tempConn.getInputStream();
            InputStreamReader tempIsr = new InputStreamReader(tempInStream);
            BufferedReader tempBr = new BufferedReader(tempIsr);        
                        
            publicIP = tempBr.readLine();

            tempBr.close();
            tempInStream.close(); 
        } catch (Exception ex) {
                publicIP = "127.0.0.1";   
        }
        return publicIP;
    }
}
