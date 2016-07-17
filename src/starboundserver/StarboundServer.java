/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package starboundserver;

/**
 *
 * @author Razorbreak
 */
public class StarboundServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        if(args.length>0){
            ServerConfig server = new ServerConfig();
            int rateUpdates=10;
            try{
                rateUpdates = Integer.parseInt(args[0]);
                System.out.println("Refresh rate set every "+args[0]+"s.");
                
                if(args.length==2){
                    String outputDir = args[1];
                    server.setOutputDirectory(outputDir);
                }
            }catch(Exception e){
                System.out.println("Refresh rate error. Default set every "+rateUpdates+"s.");
            }
            System.out.println("[START]");
            do{
                server.analyzeOutput();
                server.saveJS();
                try {
                    Thread.sleep(rateUpdates*1000);
                } catch (InterruptedException ex) {}
            }while(!server.exit);
            System.out.println("[END]");
        }else{
            System.out.println(" Program usage:\n   java -jar starboundserver.jar <refresh> [<path>]\n\n Where <refresh> must be an Integer > 0\n and <path> the output directory.");
        }
    }
    
}
