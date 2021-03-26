package clientPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Client {
	public static final String SERVER_ADDRESS = "127.0.0.1";
	public static final int SERVER_PORT = 8000;
	private PrintWriter out;
	private Socket socket;
	private JFrame frame = new JFrame("Client");
	private JTextField dataField = new JTextField(40);
	private JTextArea messageArea = new JTextArea(8, 60);

	public Client() {
		// Layout GUI
		messageArea.setEditable(false);
		frame.getContentPane().add(dataField, "North");
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");

		// Add Listeners
		dataField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println(dataField.getText());
				dataField.setText(null);
			}
		});
	}

	public void connectToServer() throws IOException {
		socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
		out = new PrintWriter(socket.getOutputStream(), true);
		new ReadLineThread(socket).start();
		out.println("I connected to chatroom.");
	}

	public static void main(String[] args) {
		Client c = new Client();
		c.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		c.frame.pack();
		c.frame.setVisible(true);
		try {
			c.connectToServer();
			while (true) {
				if (c.getSocket().isClosed())
					break;
			}
			c.getOut().close();
			c.getSocket().close();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}

	private class ReadLineThread extends Thread {
		private BufferedReader in;

		public ReadLineThread(Socket socket) {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (Exception e) {
			}
		}

		@Override
		public void run() {
			try {
				while (true) {
					String result = in.readLine();
					if (result.equals("byeClient")) {
						break;
					} else {
						messageArea.append(result + "\n");
					}
				}
				messageArea.append("byeClient!\n");
				in.close();
				out.close();
				socket.close();
				System.exit(0);
			} catch (Exception e) {
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
