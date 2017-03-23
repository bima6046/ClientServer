package hw1_v2;

import java.awt.*;	//GUI RELATED LIBRARIES
import java.awt.event.*; //ACTIONLISTENER
import java.util.LinkedList;

import javax.swing.*; //MAIN GUI LIBRARY
import javax.swing.border.MatteBorder;

/**
 * THIS IS THE GUI CODE
 * Read connection related input from GUI/Console
 * send those to thread which will handle all connection related stuff
 * @author Bisrat
 **/
public class CLIENT_MAIN extends JFrame implements ActionListener{

	/* Communication related variables */
	private String ip_address = "localhost";
	private int port;
	private CLIENT_THREAD thread;



	private LinkedList<String> guessedStuff = new LinkedList<String>();

	/* ------------------GUI related variables------------------- */

	/* Connection panel variables */
	private JPanel connectPanel;
	private JTextField ip_field, port_field;

	/* Main frame variables */
	private JPanel background;
	private final int width = Toolkit.getDefaultToolkit().getScreenSize().width;
	private final int height = Toolkit.getDefaultToolkit().getScreenSize().height;
	private JButton submit_button;
	private JTextField guess_field;
	private Font f;
	private JLabel dashes_label;
	private JLabel attempt_label;
	private JLabel score_label;
	private JTextArea guessed_log;

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args){
		new CLIENT_MAIN();	//Create the constructor
	}

	/**
	 * CONSTRUCTOR
	 * Create connection panel
	 * Check input from that panel
	 * Connect to server
	 * Create GUI window
	 */
	public CLIENT_MAIN(){
		while(true){
			createConnectPanel();
			if(checkInput()){			
				connect();
				setupGUI();
				break;
			}
		}

	}

	private void createConnectPanel(){
		connectPanel = new JPanel();
		connectPanel.setLayout(new GridLayout(2,2));
		JLabel ip_label = new JLabel("Enter IP-address");
		JLabel port_label = new JLabel("Enter port number");
		ip_field = new JTextField(20);
		port_field = new JTextField(5);
		connectPanel.add(ip_label);
		connectPanel.add(ip_field);
		connectPanel.add(port_label);
		connectPanel.add(port_field);
		Object[] answer = {"Start game"};
		int val = JOptionPane.showOptionDialog(null, connectPanel, "HANGMAN", JOptionPane.PLAIN_MESSAGE, 
				JOptionPane.DEFAULT_OPTION, null, 
				answer, answer[0]);
		if(val == -1){
			System.exit(1);
		}
	}

	/**
	 * Method to check the input of the connect JOptionPane panel
	 */
	private boolean checkInput(){
		if(ip_field.getText() == null || ip_field.getText().equals("")){
			ip_address = "localhost";
		}
		else {
			ip_address = ip_field.getText();
		}
		if(port_field.getText() == null || port_field.getText().equals("")) {
			port = 4444;
		}
		else
			try{
				port = Integer.parseInt(port_field.getText());
			} catch (NumberFormatException e){
				JOptionPane.showMessageDialog(null, "Wrong input format on port field "+port);
				return false;
			}
		return true;
	}

	/**
	 * create new communication thread and send this GUI object to it as parameter
	 */
	private void connect(){
		thread = new CLIENT_THREAD(this);
		thread.start();
		thread.addToQueue("Start Game");
	}

	/**
	 * Method to setup GUI
	 */
	private void setupGUI(){
		f = new Font("Arial", Font.BOLD, 30);
		setBounds(0, 0, width, height-40); 
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent windowEvent){
				if(JOptionPane.showConfirmDialog(null, "Are you sure you want to quit game?", 
						"Quit option", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
					System.exit(1);
				}
			}
		});

		background = new JPanel();		
		background.setLayout(new BorderLayout());
		guess_field = new JTextField(30);
		guess_field.setFont(f);
		guess_field.addActionListener(this);
		submit_button = new JButton("Submit");
		submit_button.setFont(f);
		submit_button.addActionListener(this);
		background.add(createLeftPanel(), BorderLayout.WEST);
		//background.add(createBottomPanel(), BorderLayout.SOUTH);
		background.add(createCenterPanel(), BorderLayout.CENTER);
		//background.add(createTopPanel(), BorderLayout.NORTH);
		
		//background.add(createRightPanel(), BorderLayout.EAST);
		add(background);
		setVisible(true);
	}

	private JPanel createBottomPanel(){
		JPanel bottom_p = new JPanel();
		JPanel field_p = new JPanel();
		JPanel button_p = new JPanel();
		JLabel guess_label = new JLabel("Guess letter or word: ");
		guess_label.setFont(f);
		field_p.add(guess_label);
		field_p.add(guess_field);
		button_p.add(submit_button);
		bottom_p.setLayout(new GridLayout(2,1));
		bottom_p.add(field_p);
		bottom_p.add(button_p);
		return bottom_p;
	}

	private JPanel createCenterPanel(){
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(3,1));
		JPanel p = new JPanel();
		dashes_label = new JLabel();
		dashes_label.setFont(new Font("Arial", Font.BOLD,100));		
		p.add(dashes_label);
		centerPanel.add(createTopPanel());
		centerPanel.add(p);
		centerPanel.add(createBottomPanel());
		return centerPanel;
	}

	private JPanel createTopPanel(){
		JPanel topPanel = new JPanel();
		attempt_label = new JLabel("Attempts left: ");
		attempt_label.setFont(f);
		score_label = new JLabel("Score: 0");
		score_label.setFont(f);
		topPanel.add(attempt_label);
		topPanel.add(score_label);
		return topPanel;
	}

	private JPanel createLeftPanel(){
		JPanel leftPanel = new JPanel();
		guessed_log = new JTextArea(17,10);
		guessed_log.setFont(new Font("Arial", Font.BOLD, 50));
		guessed_log.setEditable(false);
		JScrollPane sp = new JScrollPane(guessed_log);
		sp.setBorder(new MatteBorder(0,0,0,10, Color.BLACK));
		leftPanel.add(sp);
		return leftPanel;
	}

	private void addToLog(String guess){
		guessed_log.append(guess+"\n");
	}


	/**
	 * Called from communication thread to update dash string label on GUI
	 * @param dashes
	 */
	void updateDashesToGUI(final String dashes){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				dashes_label.setText(dashes);
				dashes_label.setHorizontalTextPosition(SwingConstants.RIGHT);
			}
		});
	}

	/**
	 * Called from communication thread to update attempts left label on GUI
	 * @param attempts
	 */
	void updateAttemptsToGUI(final int attempts){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				attempt_label.setText("Remaning Attempts: "+attempts);
			}
		});
	}

	/**
	 * Called from communication thread to update score label on GUI
	 * @param score
	 */
	void updateScoreToGUI(final int score){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				score_label.setText("Score: "+score);
			}
		});
	}

	/**
	 * Called from communication thread to reset the log of letters/words on GUI
	 */
	void newGame(){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				guessed_log.setText("");
				guessedStuff.clear();
			}
		});
	}

	/**
	 * Returns the IP-address
	 * @return IP-address
	 */
	String getIP(){
		return ip_address;
	}

	/**
	 * Returns the port number
	 * @return port
	 */
	int getPort(){
		return port;
	}

	/**
	 * Method to be called when event such as button clicked is in action
	 * When button "submit_button" is clicked, read text field of guessed letter/word
	 * and send it to the server
	 */
	public void actionPerformed(ActionEvent e){
		Object b = e.getSource();
		if((b == submit_button || b == guess_field) && !guess_field.getText().equals("")){
			String guess = guess_field.getText();
			if(!guessedStuff.contains(guess)){
				thread.addToQueue(guess);
				addToLog(guess);
				guess_field.setText("");
				guessedStuff.add(guess);
			}
			else{
				guess_field.setText("");
			}
		}	
	}
}
