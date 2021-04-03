package serverPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Enumeration;


// La classe de serveur
public class Server {
	// Le numéro du port doit être pareil que celui du client.
	private static final int SERVER_PORT = 8000;
	private ServerSocket listener;
	private static Hashtable<String, ServerThread> clientThread = new Hashtable<String, ServerThread>();
	// HashTable est pour stocker le nom d'utilisateur et son thread correspondant.

	// constructeur de la classe, qui fait une connetion de Socket.
	public Server() throws IOException {
		this.listener = new ServerSocket(SERVER_PORT);
		System.out.println("Server lanched.");
	}

	// fonction main qui comprend la boucle principale
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

	// La classe qui hérite Thread
	private static class ServerThread extends Thread {
		private String clientName; // nom du client en string
		private Socket client; // le socket de chaque client
		private PrintWriter out;
		private BufferedReader in;

		public ServerThread(Socket socket) {
			this.client = socket;
		}

		@Override
		public void run() {
			try {
				// pour écrire avant la boucle
				out = new PrintWriter(client.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				out.println("Bonjour, la connexion va s'arreter jusqu'au l'envoie d'un message 'exit'.");
				out.println("Entrer votre pseudo : ");
				in.readLine();

				int flag = 0;
				String line = in.readLine();
				while (!line.equals("exit")) { // si le client n'entre pas 'exit'
					if (flag == 0) { // pour son premier message, cad son pseudo
						while (clientThread.containsKey(line)) { // lorsque son nom existe dans le hashtable
							out.println("Nom existant. Veuillez reentrer votre pseudo : ");
							line = in.readLine();
						}
						this.setClientName(line);
						clientThread.put(this.getClientName(), this);
						// imformer les autres client son entrée
						System.out.println(this.getClientName() + " a rejoint la conversation.");
						send(this.getClientName() + " a rejoint la conversation.");
					} else { // a partir de son 2e message, on ajoute un préfixe 'xxx a dit:'
						System.out.println(this.getClientName() + " a dit : " + line);
						send(this.getClientName() + " a dit : " + line);
					}
					flag++;
					line = in.readLine();
					if (line.equals("exit")) { // le client a entrée 'exit', cad il veut quitter le chat
						send(this.getClientName() + " a quitt la conversation.");
						System.out.println(this.getClientName() + " a quitt la conversation.");
					}
				}
				out.println("byeClient");
			} catch (Exception e) { // pour ce qui quitte le chat forcément
				System.out.println(this.getClientName() + " a quitt la conversation.");
				send(this.getClientName() + " a quitt la conversation.");
			} finally {
				try {
					in.close();
					out.close();
					client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				clientThread.remove(this.getClientName()); // supprimer le client qui quitte
			}
		}

		public void send(String s) { // pour envoyer un message à chaque client
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
