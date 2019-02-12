package serpis.psp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import sun.security.util.Length;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClienteChat extends JFrame implements ActionListener, Runnable {

	private static final long serialVersionUID = 1L;
	Socket socket = null;

//streams
	DataInputStream fentrada; // Para leer los mensajes
	DataOutputStream fsalida; // Para escribir los mensajes

	String nombre;
	static JTextField mensaje = new JTextField();

	private JScrollPane scrollpane1;
	static JTextArea textarea1;

	JButton botonEnviar = new JButton("Enviar");
	JButton botonSalir = new JButton("Salir");
	boolean repetir = true;

//constructor
	public ClienteChat(Socket s, String nombre) {
		super("CONEXION DEL CLIENTE CHAT: " + nombre);
		setLayout(null);

		mensaje.setBounds(10, 10, 400, 30);
		add(mensaje);

		textarea1 = new JTextArea();
		scrollpane1 = new JScrollPane(textarea1);
		scrollpane1.setBounds(10, 50, 400, 300);
		add(scrollpane1);

		botonEnviar.setBounds(420, 10, 100, 30);
		add(botonEnviar);
		botonSalir.setBounds(420, 50, 100, 30);
		add(botonSalir);

		textarea1.setEditable(false);
		botonEnviar.addActionListener(this);
		
		mensaje.addKeyListener(new KeyListener() {
			
			public void keyPressed(KeyEvent e) {
		         if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		        	 if (mensaje.getText().trim().length() == 0) {
		 				return;
		 			}
		 			
		 			String texto = nombre + ">" + mensaje.getText();
		 			try {
		 				mensaje.setText("");
		 				fsalida.writeUTF(texto);

		 			} catch (IOException e1) {
		 				e1.printStackTrace();
		 			}
		 		}
			}
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});

		
		botonSalir.addActionListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		socket = s;
		this.nombre = nombre;
		try {
			fentrada = new DataInputStream(socket.getInputStream());
			fsalida = new DataOutputStream(socket.getOutputStream());
			String texto = ">Entra en el chat...." + nombre;
			fsalida.writeUTF(texto);
		} catch (IOException e) {
			System.out.println("Error de E/S");
			e.printStackTrace();
			System.exit(0);
			;
		}

	}// Fin constructor

//accion cuando pulsamos botones
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == botonEnviar) { // Se pulsa ENVIAR
			if (mensaje.getText().trim().length() == 0) {
				return;
			}
			String texto = nombre + ">" + mensaje.getText();
			try {
				mensaje.setText("");
				fsalida.writeUTF(texto);

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		if (e.getSource() == botonSalir) {
			String texto = " >Abandona el chat..." + nombre;

			try {
				fsalida.writeUTF(texto);
				fsalida.writeUTF("*");
				repetir = false;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	} // FIN ACCIÓN BOTONES

	public void run() {
		String texto = "";

		while (repetir) {
			try {
				texto = fentrada.readUTF();
				textarea1.setText(texto);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "IMPOSIBLE CONECTAR CON EL SERVIDOR\n" + e.getMessage(),
						"<<MENSAJE DE ERROR:2>>", JOptionPane.ERROR_MESSAGE);
				repetir = false;
			}
		}

		try {
			socket.close(); // cerrar socket
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		int puerto = 44444;
		Socket s = null;

		String nombre = JOptionPane.showInputDialog("Introduce tu nombre aquí: ");

		if (nombre.trim().length() == 0) {
			System.out.println("El nombre está vacío....");
			return;
		}

		try {
			s = new Socket("192.168.26.122", puerto);

///////

//167		
			
			ClienteChat cliente = new ClienteChat(s, nombre);
			cliente.setBounds(0, 0, 540, 400);
			cliente.setVisible(true);
			
			new Thread(cliente).start(); // Lanzar hilo cliente

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "IMPOSIBLE CONECTAR CON EL SERVIDOR\n" + e.getMessage(),
					"<<MENSAJE DE ERROR:1>>", JOptionPane.ERROR_MESSAGE);
		}
	}// main
}// Cliente Chat
