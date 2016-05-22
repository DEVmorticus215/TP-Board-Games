package es.ucm.fdi.tp.assignment6.gameserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {

	private Socket s;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public Connection (Socket s) throws IOException{
		this.s = s;
		this.out = new ObjectOutputStream(s.getOutputStream());
		this.in = new ObjectInputStream(s.getInputStream());
	}
	
	public void sendObject(Object o) throws IOException{
		out.writeObject(o);
		out.flush();
		out.reset();
	}
	
	public Object getObject() throws ClassNotFoundException, IOException{
		return in.readObject();
	}
	
	public void stop() throws IOException{
		s.close();
	}
}
