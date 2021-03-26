package serverPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Enumeration;

public class Server {

	private static final int SERVER_PORT = 8000;
	private ServerSocket listener;
	private static Hashtable<String, ServerThread> clientThread = new Hashtable<String, ServerThread>();

	public Server() throws IOException {
		this.listener = new ServerSocket(SERVER_PORT);
		System.out.println("Server lanched.");
	}

	public static void main(String[] args) throws IOException {
		Server sc = new Server();
		try {
			while (true) {
				Socket socket = sc.getListener().accept();
				new ServerThread(socket).start();
			}
		} catch (Exception e) {
		} finally {
			sc.getListener().close();
		}
	}

	public ServerSocket getListener() {
		return listener;
	}

	private static class ServerThread extends Thread {
		private String clientName;
		private Socket client;
		private PrintWriter out;
		private BufferedReader in;

		public ServerThread(Socket socket) {
			this.client = socket;
		}

		@Override
		public void run() {
			try {
				out = new PrintWriter(client.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				out.println("Bonjour, la connexion va s'arreter jusqu'au l'envoie d'un message 'exit'.");
				out.println("Entrer votre pseudo : ");
				in.readLine();

				int flag = 0;
				String line = in.readLine();
				while (!line.equals("exit")) {
					if (flag == 0) {
						while (clientThread.containsKey(line)) {
							out.println("Nom existant. Veuillez reentrer votre pseudo : ");
							line = in.readLine();
						}
						this.setClientName(line);
						clientThread.put(this.getClientName(), this);
						System.out.println(this.getClientName() + " a rejoint la conversation.");
						send(this.getClientName() + " a rejoint la conversation.");
					} else {
						System.out.println(this.getClientName() + " a dit : " + line);
						send(this.getClientName() + " a dit : " + line);
					}
					flag++;
					line = in.readLine();
					if (line.equals("exit")) {
						send(this.getClientName() + " a quitt¨¦ la conversation.");
						System.out.println(this.getClientName() + " a quitt¨¦ la conversation.");
					}
				}
				out.println("byeClient");
			} catch (Exception e) {
				System.out.println(this.getClientName() + " a quitt¨¦ la conversation.");
				send(this.getClientName() + " a quitt¨¦ la conversation.");
			} finally {
				try {
					in.close();
					out.close();
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				clientThread.remove(this.getClientName());
			}
		}

		public void send(String s) {
			Enumeration<String> e = clientThread.keys();
			while (e.hasMoreElements()) {
				String key = e.nextElement();
				clientThread.get(key).out.println(s);
				clientThread.get(key).out.flush();
			}
		}

		public String getClientName() {
			return clientName;
		}

		public void setClientName(String clientName) {
			this.clientName = clientName;
		}

	}

}
