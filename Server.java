import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server extends ServerSocket {

    private static final int SERVER_PORT = 8000;
    private static List<String> user_list = new ArrayList<String>();
    private static List<ServerThread> thread_list = new ArrayList<ServerThread>();
    
    public Server() throws IOException {
        super(SERVER_PORT);
        System.out.println("Server lanched.");
        try {
            while (true) {
                Socket socket = accept();
                new ServerThread(socket);
            }
        } catch (Exception e) {
        } finally {
            close();
        }
    }

   
    class ServerThread extends Thread {

        private PrintWriter out;

        private BufferedReader in;

        private String name;

        private Socket client;

        public ServerThread(Socket socket) throws IOException {
            client = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            in.readLine();

            start();
        }

        @Override
        public void run() {
            out.println("Entrer votre pseudo: ");

            try {
                int flag = 0;
                String line = in.readLine();
                while (!"quit".equals(line)) {
                    if (flag == 0) {
                        
                        while (existName(line) == true) {
                            out.println("Nom existant. Veuillez reentrer votre pseudo: ");
                            line = in.readLine();
                        }
                        name = line;
                        user_list.add(name);
                        thread_list.add(this);
                        System.out.println(name + " a rejoint la conversation.");
                        send(name + " a rejoint la conversation.");
                    } else {
                        System.out.println(name + ": " + line);
                        send(name + " : " + line);
                    }
                    flag++;
                    line = in.readLine();
                    if ("quit".equals(line)) {
                        send(name + " a quitte la la conversation.");
                        System.out.println(name + " a quitte la conversation.");
                    }
                }
                out.println("byeClient");
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println(name + " a quitte la conversation.");
                send(name + " a quitte la conversation.");
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                thread_list.remove(this);
                user_list.remove(name);
            }
        }

        public void send(String s) {
            for (ServerThread thread : thread_list) {
                thread.out.println(s);
                thread.out.flush();
            }
        }


        public boolean existName(String name) {
            for (String user : user_list) {
                if (name.equals(user)) {
                    return true;
                }
            }
            return false;
        }

    }

    public static void main(String[] args) throws IOException {
        Server sc = new Server();
    }
}

