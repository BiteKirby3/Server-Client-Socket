package clientPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * La classe Client est utilis¨¦e pour ¨¦tablir la connexion au serveur et ¨¦changer les messages par la suite
 * La connexion au serveur et la diffusion des messages se font dans le thread principal.
 * La r¨¦ception et l'affichage des messages se font dans le thread ReadLineThread. 
 */
public class Client {
	//Les informations constante concernant le serveur
	public static final String SERVER_ADDRESS = "127.0.0.1";
	public static final int SERVER_PORT = 8000;
	//Le socket et son flux de sortie 
	private Socket socket;
	private PrintWriter out;
	//Les composants de GUI
	private JFrame frame = new JFrame("Client"); //La fen¨ºtre globale
	private JTextField dataField = new JTextField(40); //La zone de saisie
	private JTextArea messageArea = new JTextArea(8, 60); //La zone d'affichage

	
	public Client() {
		// Layout GUI
		messageArea.setEditable(false);
		frame.getContentPane().add(dataField, "North");
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");

		// Ajouter ActionListener pour r¨¦agir aux ¨¦v¨¦nements d'entr¨¦e de client
		dataField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println(dataField.getText());
				dataField.setText(null);
			}
		});
	}
	
	/**
	 * Se connecter au serveur, lancer ReadLineThread et pr¨¦parer les streams
	 * @throws IOException
	 */
	public void connectToServer() throws IOException {
		socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
		out = new PrintWriter(socket.getOutputStream(), true);
		new ReadLineThread(socket).start();
		out.println("I connected to chatroom.");
	}

	public static void main(String[] args) {
		Client c = new Client();
		//Ouvrir l'interface de client
		c.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		c.frame.pack();
		c.frame.setVisible(true);
		try {
			c.connectToServer();
			//tant que la socket est connect¨¦e, le programme continue de fonctionner 
			while (true) {
				if (c.getSocket().isClosed())
					break;
			}
			// lib¨¦rer la socket et les flux 
			c.getOut().close();
			c.getSocket().close();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * getter pour Socket
	 * @return Socket
	 */
	public Socket getSocket() {
		return socket;
	}
	
	/**
	 * getter pour PrintWriter out
	 * @return PrintWriter
	 */
	public PrintWriter getOut() {
		return out;
	}

	/**
	 * setter pour PrintWriter out
	 * @param out
	 */
	public void setOut(PrintWriter out) {
		this.out = out;
	}

	/**
	 * Classe imbriqu¨¦e est utilis¨¦ pour d¨¦marrer un nouveau thread
	 * qui s'occupe de la r¨¦ception des messages.
	 */
	private class ReadLineThread extends Thread {
		private BufferedReader in;

		/**
		 * Constructeur pour initialiser le flux d'entr¨¦e de type BufferedReader associ¨¦ au socket
		 * @param socket
		 */
		public ReadLineThread(Socket socket) {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (Exception e) {
			}
		}

		
		/**
		 * La lecture des messages se fait dans une boucle infinie jusqu'¨¤ la r¨¦ception de message 'byeClient' ou la d¨¦tection d'exception 
		 *@Override
		 */
		public void run() {
			try {
				//r¨¦cup¨¦rer les messages et les afficher tant que le message != 'byeClient'
				while (true) {
					String result = in.readLine();
					if (result.equals("byeClient")) {
						break;
					} else {
						messageArea.append(result + "\n");
					}
				}
				messageArea.append("byeClient!\n");
				// lib¨¦rer la socket et les flux, fermer la fen¨ºtre
				in.close();
				out.close();
				socket.close();
				System.exit(0);
			} catch (Exception e) {
				try {
					//fermer le flux lorsqu'une exception est intercept¨¦e
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
