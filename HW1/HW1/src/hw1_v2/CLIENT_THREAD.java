package hw1_v2;

import java.io.*;	//I/O READERS/WRITERS
import java.net.*;	//SOCKET 
import java.util.concurrent.LinkedBlockingQueue;	//QUEUE BETWEEN THREADS

import javax.swing.JOptionPane;


/**
 * 
 * @author Bisrat
 *
 */
public class CLIENT_THREAD extends Thread{

	private String ip_address;
	private int port;
	private Socket client_socket;
	private BufferedInputStream in;
	private BufferedOutputStream out;
	private byte[] received_message_array;
	private byte[] received_guess;
	private final int RECEIVING_SIZE = 32;
	private int read_bytes;
	private String received_string;
	private CLIENT_MAIN gui_thread;
	private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

	
	/**
	 * Constructor to setup necessary class variables
	 * @param gui_thread
	 */
        
               
	public CLIENT_THREAD(CLIENT_MAIN gui_thread){
		this.gui_thread = gui_thread;
		ip_address = gui_thread.getIP();
		port = gui_thread.getPort();
		received_message_array = new byte[RECEIVING_SIZE];
	}
	
	/**
	 * Thread run method
	 * Setup the connection and then run the main loop
	 */
	public void run(){
		setupConnection();
		communicateWithServer();
	}
	
	/**
	 * Add message to blocking queue
	 * @param message
	 */
	void addToQueue(String message){
		queue.add(message);
	}
	
	private void setupConnection(){
            
           
		try {
			client_socket = new Socket(ip_address, port);
			in = new BufferedInputStream(client_socket.getInputStream());
			out = new BufferedOutputStream(client_socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int showMessage(String message){
		Object[] opt = {"Yes", "No"};
		return JOptionPane.showOptionDialog(null, message,
				"Attention!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
				null, opt, opt[0]);
	}
	
	private void checkFlag(int flag, int last_byte) throws IOException, InterruptedException{
		if(flag == 2) continueRound(last_byte);
		else finishRound(flag, last_byte);
	}
	
	private void continueRound(int last_byte) throws IOException, InterruptedException{
		gui_thread.updateAttemptsToGUI(last_byte);
		String wordFromQueue = queue.take();
		received_guess = wordFromQueue.getBytes();
		out.write(received_guess);
		out.flush();
	}
	
	private void finishRound(int flag, int last_byte) throws IOException{
		gui_thread.updateScoreToGUI(last_byte);
		int answer = (flag == 0) ? 
				showMessage("Game over! Do you want to play again?") : 
					showMessage("Congratulations! Do you want to play again?");
		if(answer == 1){
			out.write(new String("End Game").getBytes());
			out.flush();
			System.exit(1);
		}
		out.write(new String("New Game").getBytes());
		out.flush();
		gui_thread.newGame();
	}
	
	private void communicateWithServer(){
		String start_game_message;
		try {
			start_game_message = queue.take();
			out.write(start_game_message.getBytes());
			out.flush();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int flag_byte, last_byte;
		while(true){
			try {
				read_bytes = in.read(received_message_array, 0, RECEIVING_SIZE);
				received_guess = new byte[read_bytes-2];
				flag_byte = received_message_array[read_bytes-2];
				last_byte = received_message_array[read_bytes-1];

				for(int i = 0; i < read_bytes-2; i++){
					received_guess[i] = received_message_array[i];
				}
				received_string = new String(received_guess);
				gui_thread.updateDashesToGUI(received_string);
				
				checkFlag(flag_byte, last_byte);

						
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Connection lost!");
				System.exit(1);			
			} catch (InterruptedException e) {				
				e.printStackTrace();
				break;
			}		
		}
		try {
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
