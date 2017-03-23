package hw1_v2;

import java.io.*;
import java.net.*;

/**
 * 
 * @author Bisrat
 *
 */
public class SERVER_THREAD extends Thread{

	private Socket client_socket;
	private BufferedInputStream in;
	private BufferedOutputStream out;
	private byte[] array;
	private byte[] word;
	private final int SIZE = 32;
	private int read_bytes;
	private String selected_word;
	private StringBuilder dash_word;
	private int score_counter = 0;
	private int flag = 2; 
	private SERVER_MAIN server_main;
	private DASH_MANAGEMENT dash_management;

	/**
	 * Constructor 
	 * @param client_socket
	 * @param selected_word
	 */
	public SERVER_THREAD(SERVER_MAIN server_main, Socket client_socket, String selected_word){
		this.server_main = server_main;
		this.client_socket = client_socket;
		this.selected_word = selected_word;
		dash_management = new DASH_MANAGEMENT();
		dash_word = dash_management.createDashes(selected_word.length());
		dash_management.set_attempts(selected_word.length());
		array = new byte[SIZE];
		setupIO();
	}

	private void setupIO(){
		try {
			in = new BufferedInputStream(client_socket.getInputStream());
			out = new BufferedOutputStream(client_socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Couldn't create input or output streams. \n"+
						e.getMessage());
		}
	}
	
	private void waitForStartMessage(){
		while(true){
			try {
				read_bytes = in.read(array, 0, SIZE);
				word = new byte[read_bytes];
				for(int i = 0; i < read_bytes; i++){
					word[i] = array[i];
				}		
				if(new String(word).equals("Start Game")) break; else continue;			
			} catch (IOException e1) {
				System.out.println("Couldn't read from socket. "+e1.getMessage());
			}
		}
	}
	
	
	/**
	 * Receive Start Game message and then begin game process
	 * Write dash string, flag and either score counter or failed attempts counter
	 * receive guessed letter/word
	 * check the word and update the dash string accordingly to the right word letters
	 */
	public void run(){
		waitForStartMessage();
		String guessed_string;
		int last_byte;
		while(true) {
			try {
				out.write(dash_word.toString().getBytes());
								
				//win
				if(dash_management.countDashes(dash_word.toString().toCharArray()) == 0 && 
						dash_management.get_attempts() > 0){
					flag = 1;
					last_byte = ++score_counter;
				}
				
				//lose
				else if(dash_management.get_attempts() == 0){
					flag = 0;
					last_byte = (score_counter > 0) ? --score_counter : score_counter;
				}
				else{
					flag = 2;
					last_byte = dash_management.get_attempts();
				}
				out.write(flag);
				out.write(last_byte);
				out.flush();
		
				read_bytes = in.read(array, 0, SIZE);
				word = new byte[read_bytes];		
				for(int i = 0; i < read_bytes; i++){
					word[i] = array[i];
				}
				guessed_string = new String(word);
				if(guessed_string.equals("End Game"))
					break;
				else if(guessed_string.equals("New Game")){
					selected_word = server_main.getNewWord();
					dash_word = dash_management.createDashes(selected_word.length());
					dash_management.set_attempts(selected_word.length());
				}
				else{
					dash_word = dash_management.checkGuess(guessed_string, dash_word, selected_word);
				}
			} catch (SocketException e){
				System.out.println("Client suddendly disappeared \n"+e.getMessage());
				break;
			} catch (IOException e) {
				System.out.println("Something wrong happened with I/O \n"+e.getMessage());
				break;
			}
		}
		System.out.println("Server thread exited!");
	}
}
