package hw1_v2;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * 
 * @author Bisrat
 *
 */
public class SERVER_MAIN {

	private ServerSocket server_socket;
	private int port = 4444;
	private LinkedList<String> words;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		new SERVER_MAIN();
	}
	
	/**
	 * 
	 */
	public SERVER_MAIN(){
		//System.out.println("Read word file, setup connection and listen for clients");
		readWordFile();
		setUpConnection();
		listenForClients();
	}
	
	private void readWordFile(){
		words = new LinkedList<String>();
		FileReader fr;
		BufferedReader br;
		
		
		try {
			fr = new FileReader("random.txt");
			br = new BufferedReader(fr);
			String line;
			while((line = br.readLine()) != null){
				if(line.matches("[a-zA-Z]{5,}")){
					words.add(line.toLowerCase());
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	String getNewWord(){
		Random rnd = new Random();
		
		int list_size = words.size();
		String selected_word = words.get(rnd.nextInt(list_size));
		return selected_word;
	}
	
	
	private void setUpConnection(){
		try {
			server_socket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void listenForClients(){
		Random rnd = new Random();
		
		int list_size = words.size();
		String selected_word;
		//INFINITE LOOP
		while(true){
			try {
				selected_word = words.get(rnd.nextInt(list_size));
				Socket client_socket = server_socket.accept();	//MAKE A CONNECTION WITH THE CLIENT
				(new SERVER_THREAD(this, client_socket, selected_word)).start();		//START THREAD
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
