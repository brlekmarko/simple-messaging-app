package hr.fer.zemris.chat.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import hr.fer.zemris.chat.utils.ClientMsgSender;
import hr.fer.zemris.chat.utils.Message;
import hr.fer.zemris.chat.utils.messageImpl.ByeMessage;
import hr.fer.zemris.chat.utils.messageImpl.OutMsgMessage;


// klijentov GUI
// ima listu poruka i polje za unos (poruka se salje pritiskom na enter)
public class ClientWindow extends JFrame{

	private static final long serialVersionUID = 1L;
	private ChatListModel chatListModel;
	private List<Message> toSend;
	private List<Message> received;
	private long number;
	private long UID;
	
	public ClientWindow(String name, List<Message> toSend, List<Message> received, long UID, DatagramSocket socket, InetSocketAddress serverAddress) {
		this.toSend = toSend;
		this.received = received;
		this.UID = UID;
		this.number = 1L;
		setLocation(100,100);
		setSize(1000, 800);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				closeWindow();
			}
		});
		setTitle("Chat Client: " + name);
		
		// pokreni dretvu koja salje poruke
		new Thread(new ClientMsgSender(this.toSend, this.received, socket, serverAddress)).start();
		
		initGUI();
	}
	
	
	// klikom na x zatvori prozor i posalji poruku serveru da se klijent odjavljuje
	public void closeWindow() {
		toSend.add(new ByeMessage(number, UID));
		number++;
		dispose();
	}
	
	
	private void initGUI() {

		this.setLayout(new BorderLayout());
		
		// u scrollPane stavljamo listu poruka
		this.chatListModel = new ChatListModel();
		JList<String> chatList = new JList<>(chatListModel);
		JScrollPane scrollPane = new JScrollPane(chatList);
		
		this.add(scrollPane, BorderLayout.CENTER);
		
		// polje za unos poruke
		// sa keyListenerom koji salje poruku pritiskom na enter
		JTextField enterText = new JTextField();
		enterText.setFont(new Font("Serif", Font.BOLD, 20));
		enterText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				
				if(key == KeyEvent.VK_ENTER) {
					toSend.add(new OutMsgMessage(number, UID, enterText.getText()));
					enterText.setText("");
				}
				
			}

			@Override
			public void keyReleased(KeyEvent e) {}
			
		});
		this.add(enterText, BorderLayout.SOUTH);
	}
	
	
	public ChatListModel getListModel() {
		return this.chatListModel;
	}

}
