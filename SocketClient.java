import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
 

public class SocketClient extends Socket{
 
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8000;
    
    private Socket client;
    private PrintWriter out;
    private BufferedReader in;
    
 
    public SocketClient() throws Exception{
        super(SERVER_IP, SERVER_PORT);
        client = this;
        out = new PrintWriter(this.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(this.getInputStream()));
        new readLineThread();
        out.println("I connected to chatroom.");
        while (true) {
            in = new BufferedReader(new InputStreamReader(System.in));
            String input = in.readLine();
            out.println(input);
        }
    }
    
   
    class readLineThread extends Thread{
        
        private BufferedReader buff;
        public readLineThread(){
            try {
                buff = new BufferedReader(new InputStreamReader(client.getInputStream()));
                start();
            } catch (Exception e) {
            }
        }
        
        @Override
        public void run() {
            try {
                while(true){
                    String result = buff.readLine();
                    if("byeClient".equals(result)){
                        break;
                    } else {
                        System.out.println(result);
                    }
                }
                System.out.println("byeClient!");
                in.close();
                out.close();
                client.close();
            } catch (Exception e) {
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            new SocketClient();
        } catch (Exception e) {
        }
    }
}
