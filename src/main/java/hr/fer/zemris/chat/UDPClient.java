package hr.fer.zemris.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.SwingUtilities;
import hr.fer.zemris.chat.gui.ChatListModel;
import hr.fer.zemris.chat.gui.ClientWindow;
import hr.fer.zemris.chat.utils.BytesToMessage;
import hr.fer.zemris.chat.utils.Message;
import hr.fer.zemris.chat.utils.messageImpl.AckMessage;
import hr.fer.zemris.chat.utils.messageImpl.HelloMessage;
import hr.fer.zemris.chat.utils.messageImpl.InMsgMessage;


public class UDPClient{
	
	// funkcija salje hello poruku i vraca UID ako je uspjelo, inace vraca -1
    // radi retransmisiju 10 puta
	private static long sendHello(DatagramSocket socket, String name, InetSocketAddress serverAddress,
			List<Message> received) {
		
        HelloMessage hello = new HelloMessage(0, name, new Random().nextLong());
        byte[] bytes = hello.toBytes();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
        packet.setSocketAddress(serverAddress);
        
        
        try {
            // postavljanje timeouta na 5 sekundi
			socket.setSoTimeout(5000);
		} catch (SocketException e1) {}

        int retranmissions = 0;
        byte[] buffer;
        DatagramPacket receivedPacket;
        Message message;
        while (retranmissions < 10) {
            retranmissions++;
            buffer = new byte[1024];
            receivedPacket = new DatagramPacket(buffer, buffer.length);

            // posalji
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            

            // primi
            try {
                socket.receive(receivedPacket);
            }
            catch (IOException e) {
                //e.printStackTrace();
                continue;
            }

            try {
                message = BytesToMessage.convertToMessage(receivedPacket.getData());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            // ako je to potvrda na hello poruku vrati UID, inace dodaj u listu primljenih poruka
            if (message instanceof AckMessage) {
                AckMessage ack = (AckMessage) message;
                if (ack.getNumber() == hello.getNumber()) {
                    try {
                        socket.setSoTimeout(0);
                    } catch (SocketException e1) {}
                    return ack.getUID();
                }
            }
            else{
                received.add(message);
            } 
        }
        
        // vracanje timeouta na 0
        try {
			socket.setSoTimeout(0);
		} catch (SocketException e1) {}

        // ako nismo dobili potvrdu nakon 10 retransmisija vrati -1
        return -1;
    }

    public static void main(String[] args) throws SocketException {

        if(args.length!=3) {
			System.out.println("Ocekivao sam host port ime");
			return;
		}
		
		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
        String name = args[2];
        
        InetSocketAddress serverAddress = new InetSocketAddress(hostname, port); 
        DatagramSocket socket = new DatagramSocket();
        //socket.bind(new InetSocketAddress(58652));
        
        
        List<Message> received = new ArrayList<>();
        List<Message> toSend = new ArrayList<>();


        // probamo dobiti UID
        long UID = sendHello(socket, name, serverAddress, received);
        if (UID == -1) {
            System.out.println("Nije uspjelo slanje hello poruke");
            return;
        }
        
        ClientWindow frame = new ClientWindow(name, toSend, received, UID, socket, serverAddress);
        ChatListModel chatListModel = frame.getListModel();
        
        SwingUtilities.invokeLater(() -> {
			frame.setVisible(true);
		});
        

        // u petlji primamo poruke
        // gledamo je li to poruka ili potvrda
        // ako se socket zatvori prekidamo petlju
        while(true){
            byte[] buffer = new byte[1024];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            
            try{
                socket.receive(receivedPacket);
            } catch (Exception e) {
                if(socket.isClosed()) break;
            }
            
            Message message;
            try {
                message = BytesToMessage.convertToMessage(receivedPacket.getData());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            
            if (message instanceof InMsgMessage) {
                InMsgMessage inMsg = (InMsgMessage) message;
                chatListModel.addMessage(inMsg);
                AckMessage ack = new AckMessage(inMsg.getNumber(), UID);
                toSend.add(ack);
            }
            else{
                received.add(message);
            }
        }    
    }
}
