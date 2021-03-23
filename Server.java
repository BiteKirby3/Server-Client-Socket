import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Server extends ServerSocket {

    private static final int SERVER_PORT = 8000;
    private static boolean isPrint = false;
    private static List<String> user_list = new ArrayList<String>();
    private static List<ServerThread> thread_list = new ArrayList<ServerThread>();
    private static LinkedList<Message> message_list = new LinkedList<Message>();

    
    public Server() throws IOException {
        super(SERVER_PORT);
        new PrintOutThread();
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

    

    class PrintOutThread extends Thread {

        public PrintOutThread() {
            start();
        }

        @Override
        public void run() {
            while (true) {
                if (!isPrint) {
                    try {
                        Thread.sleep(500);
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                
                Message message = (Message) message_list.getFirst();
                
                for (int i = 0; i < thread_list.size(); i++) {
                    ServerThread thread = thread_list.get(i);
                    if (thread.num == 0) {
                        thread.sendMessage(message);
                    }
                    
                }
                message_list.removeFirst();
                isPrint = message_list.size() > 0 ? true : false;

            }
        }
    }

   
    class ServerThread extends Thread {
        private Socket client;

        private PrintWriter out;

        private BufferedReader in;

        private String name;

        private int num;

        public ServerThread(Socket s) throws IOException {
            client = s;
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            in.readLine();

            start();
        }

        @Override
        public void run() {
            out.println("Entrer votre pseudo: ");
            //System.out.println(getName());
            try {
                int flag = 0;
                String line = in.readLine();
                while (!"quit".equals(line)) {
                    if (flag == 0) {
                        num = 1;
                        name = line;
                        user_list.add(name);
                        thread_list.add(this);
                        //out.println(name + " a rejoint la conversation.");
                        System.out.println(name + " a rejoint la conversation.");
                        pushMessage(name, "");
                    } else {
                        num = 0;
                        pushMessage(name, line);
                    }
                    flag++;
                    line = in.readLine();
                    if ("quit".equals(line)) {
                        num = -1;
                        pushMessage(name, line);
                        System.out.println(name + " a quitte la conversation.");
                    } 
                    else System.out.println(name + ": " + line);
                }
                out.println("byeClient");
            } catch (Exception e) {
                e.printStackTrace();
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

        public void pushMessage(String name, String msg) {
            Message message = new Message(name, msg);
            message_list.addLast(message);
            isPrint = true;
        }

        public void sendMessage(Message message) {
            if (num == 1) {
                out.println(message.getName() + " a rejoint la conversation.");
                //num = 0;
            }
            else if (num == -1) {
                out.println(message.getName() + " a quitte la conversation.");
                
            }
            else out.println(message.getName() + ": " + message.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}

class Message {
    String client;

    String message;

    public Message() {
        super();
    }

    public Message(String client, String message) {
        super();
        this.client = client;
        this.message = message;
    }

    public String getName() {
        return client;
    }

    public void setName(String name) {
        this.client = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message [client=" + client + ", message=" + message + "]";
    }

}
