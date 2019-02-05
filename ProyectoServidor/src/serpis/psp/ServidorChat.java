package serpis.psp;

import java.net.*;
import java.io.*;

public class ServidorChat {
	static final int MAXIMO=10; //MÁXIMO DE CONEXIONES PERMITIDAS
	public static void main(String[] args) throws IOException {
		int PUERTO=44444;
		ServerSocket servidor =new ServerSocket(PUERTO);
		System.out.println("Servidor iniciado...");
		
		Socket tabla[]=new Socket[MAXIMO];
		ComunHilos comun=new ComunHilos(MAXIMO,0,0,tabla);
		
		while(comun.getCONEXIONES() <MAXIMO) {
			Socket socket =new Socket();
			socket=servidor.accept();//ESPERANDO CLIENTE
			
			comun.addTabla(socket,comun.getCONEXIONES());
			comun.setACTUALES(comun.getACTUALES() + 1);
			comun.setCONEXIONES(comun.getCONEXIONES() + 1);
			
			HiloServidorChat hilo=new HiloServidorChat(socket,comun);
			hilo.start();
		}
		servidor.close();

	}

}
