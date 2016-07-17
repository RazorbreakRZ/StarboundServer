/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package starboundserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import my_ip.My_ip;


/**
 *
 * @author Razorbreak
 */
public class ServerConfig{
    //Server info
    boolean isOnline;
    String host;
    String version;
    String ipv4;
    ArrayList<Jugador> lPlayers;
    boolean exit;
    File javascript = new File("statusStarboundServer.js");
    File serverLog = new File("starbound_server.log");
    File serverLogCopy = new File("sb_server.log");
    
    public ServerConfig(){
        this.exit = false;
        this.isOnline = false;
        this.version = "Unknown";
        this.ipv4 = (new My_ip()).get_public_ip();
        this.host = System.getProperty("user.name");
        this.lPlayers = new ArrayList();
    }
    
    public void setOutputDirectory(String folder){
        javascript = new File(folder+"\\statusStarboundServer.js");
    }
        
    public void saveJS(){
        try {
            this.javascript.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.javascript));
            String codigoJS="";
            //
            if(this.isOnline){
                codigoJS+="function getSBHostName(){return \""+this.host+"\";}\n";
                codigoJS+="function getSBServerVersion(){return \""+this.version+"\";}\n";
                codigoJS+="function getSBServerIP(){return \""+this.ipv4+"\";}\n";
                codigoJS+="function getSBNumberOfPlayers(){return "+lPlayers.size()+";}\n";
                codigoJS+="function isSBOnline(){return true;}\n";
                codigoJS+="function getSBPlayersList(){var list; list=new Object();";
                for(int i=0;i<lPlayers.size();i++){
                    codigoJS+="list["+i+"]=\""+lPlayers.get(i).getName()+"\";";
                }
                codigoJS+=" return list;}\n";
            }else{
                codigoJS+="function getSBHostName(){return \"---\";}\n";
                codigoJS+="function getSBServerVersion(){return \""+this.version+"\";}\n";
                codigoJS+="function getSBServerIP(){return \"---.---.---.---\";}\n";
                codigoJS+="function getSBNumberOfPlayers(){return 0;}\n";
                codigoJS+="function isSBOnline(){return false;}\n";
                codigoJS+="function getSbPlayersList(){var list; list=new Object(); return list;}\n";
            }
            //
            bw.write(codigoJS);        
            bw.close();
        } catch (Exception ex) {
            System.err.println("Error while attempting to create .js file. Resetting directory.");
            javascript = new File("statusStarboundServer.js");
        }
    }
    
    public void analyzeOutput(){
        try {
            System.out.print("  Copying server log...");
            String command = "cmd /c copy /Y starbound_server.log sb_server.log";
            Process p = Runtime.getRuntime().exec(command);
            System.out.println("[COMPLETE]");
        } catch (IOException ex) {
            System.out.println("[ERROR]");
        }
        //
        System.out.println("  Analyzing server log...");
        if(!serverLog.exists()){
            System.out.println("[NOT FOUND]");
            return;
        }
        //
        try{
            BufferedReader br = new BufferedReader(new FileReader(serverLogCopy));
            String line,pj="",ip="";
            while((line = br.readLine())!=null){
                if(line.matches("Info: UdpServer .*")){//Start
                    this.isOnline = true;
                    System.out.println("    Server listening -> "+this.isOnline);
                    
                }else if(line.matches(".* connected")){//Connection
                    pj = line.substring(line.indexOf("'")+1,line.lastIndexOf("'"));
                    ip = line.substring(line.indexOf("(")+1,line.lastIndexOf(":"));
                    boolean exists=false;
                    for(int i=0;i<this.lPlayers.size();i++){
                        if(this.lPlayers.get(i).getName().equals(pj)){
                            exists = true;
                            break;
                        }
                    }
                    if(!exists) this.lPlayers.add(new Jugador(pj,ip));
                    System.out.println("    Player connected -> "+pj+" ("+ip+")");
                    
                }else if(line.matches(".* disconnected")){//Disconnection
                    pj = line.substring(line.indexOf("'")+1,line.lastIndexOf("'"));
                    for(int i=0;i<this.lPlayers.size();i++){
                        if(this.lPlayers.get(i).getName().equals(pj)){
                            this.lPlayers.remove(i);
                            break;
                        }
                    }
                    System.out.println("    Player disconnected -> "+pj);
                    
                }else if(line.matches("Info: Server shutdown gracefully")){//Shutdown
                    System.out.println("    Server shutdown -> "+line);
                    this.isOnline = false;
                    this.exit = true;
                    
                }else if(line.matches("Info: Server version.*")){
                    this.version = line.substring(line.indexOf("'")+1,line.lastIndexOf("'"));
                    System.out.println("    Game version -> "+this.version);
                    
                }
                //Info: Server version 'Beta v. Enraged Koala - Update 8', protocol 643
                //Info: UdpServer listening on: [ipv4]*:21025
                //Info: UniverseServer: Client 'Aerith Gainsborough' <1> (176.84.247.40:28632) connected
                //Info: UniverseServer: Client 'Madamelena' <2> (95.91.237.137:15144) disconnected
                //Info: Server shutdown gracefully
            }
            br.close();
        }catch(Exception e){
            System.out.println("  [ERROR]");
            return;
        }
        //
        System.out.println("[COMPLETE]");
    }
}
