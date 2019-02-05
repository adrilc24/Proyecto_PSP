package serpis.psp;

import java.net.*;
import java.io.*;

public class HiloServidorChat extends Thread{
	DataInputStream fentrada;
	Socket socket=null;
	ComunHilos comun;
	
	public HiloServidorChat(Socket s, ComunHilos comun) {
		this.socket=s;
		this.comun=comun;
		try {
			//CREO FLUJO DE entrada para leer los mensajes
			fentrada=new DataInputStream(socket.getInputStream());
		}catch(IOException e) {
			System.out.println("ERROR DE E/S");
			e.printStackTrace();
		}
	}
	public void run() {
		System.out.println("NÚMERO DE CONEXIONES ACTUALES: "+ comun.getACTUALES());
		//NADA MÁS CONECTARSE LE ENVÍO TODOS LOS MENSAJES
		String texto=comun.getMensajes();
		EnviarMensajesaTodos(texto);
		while(true) {
			String cadena="";
			try {
				cadena=fentrada.readUTF();
				if(cadena.trim().equals("*")) { //CLIENTE DESCONECTA
					comun.setACTUALES(comun.getACTUALES() - 1);
					System.out.println("NÚMERO DE CONEXIONES ACTUALES: " + comun.getACTUALES());
					break;
				}
				comun.setMensajes(comun.getMensajes() + cadena + "\n");
				EnviarMensajesaTodos(comun.getMensajes());
			}catch(Exception e) {
				e.printStackTrace();
				break;
			}
		}
		//SE CIERRA EL SOCKET DEL CLIENTE
		try {
			socket.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	//ENVIA LOS MENSAJES DEL CHAT A LOS CLIENTES
	private void EnviarMensajesaTodos(String texto) {
		int i;
		//recorremos tabla de sockets para enviar mensajes
		for(i=0;i<comun.getCONEXIONES();i++) {
			Socket s1=comun.getElementoTabla(i);
			if(!s1.isClosed()) {
				try {
					DataOutputStream fsalida2=new DataOutputStream(s1.getOutputStream());
					fsalida2.writeUTF(texto);
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
