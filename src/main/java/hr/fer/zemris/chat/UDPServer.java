package hr.fer.zemris.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import hr.fer.zemris.chat.utils.BytesToMessage;
import hr.fer.zemris.chat.utils.Client;
import hr.fer.zemris.chat.utils.ClientAddress;
import hr.fer.zemris.chat.utils.ClientTask;
import hr.fer.zemris.chat.utils.Message;
import hr.fer.zemris.chat.utils.messageImpl.AckMessage;
import hr.fer.zemris.chat.utils.messageImpl.ByeMessage;
import hr.fer.zemris.chat.utils.messageImpl.HelloMessage;
import hr.fer.zemris.chat.utils.messageImpl.InMsgMessage;
import hr.fer.zemris.chat.utils.messageImpl.OutMsgMessage;

public class UDPServer {

	private static long startUID = new Random().nextLong();
	

	private static void obradiHello(DatagramSocket serverSocket, DatagramPacket clientPacket, Message message, 
			Map<ClientAddress, Long> inicijalizacijaKlijenata, Map<Long, Client> klijenti) throws IOException {

		HelloMessage helloMessage = (HelloMessage) message;
		ClientAddress clientAddress = new ClientAddress(clientPacket.getSocketAddress(), helloMessage.getRandkey());
		long UID;

		// ako je klijent vec inicijaliziran
		// znaci da ima svoju dretvu
		if(inicijalizacijaKlijenata.containsKey(clientAddress)) {
			System.out.println("KLIJENT VEC POSTOJI");
			UID = inicijalizacijaKlijenata.get(clientAddress);
			
			AckMessage ackMessage = new AckMessage(helloMessage.getNumber(), UID);
			klijenti.get(UID).getToSend().add(ackMessage);
			return;
		}
		// inace mu damo novi UID i kreiramo mu novu dretvu
		else{
			UID = startUID++;
			inicijalizacijaKlijenata.put(clientAddress, UID);
			klijenti.put(UID, new Client(helloMessage.getName()));
			// zapocni dretvu za klijenta
			new Thread(new ClientTask(serverSocket, clientAddress, klijenti.get(UID))).start();

			AckMessage ackMessage = new AckMessage(helloMessage.getNumber(), UID);
			klijenti.get(UID).getToSend().add(ackMessage);
			return;
		}
	}
	
	private static void obradiAck(DatagramSocket dSocket, DatagramPacket packet, Message message,
			Map<ClientAddress, Long> inicijalizacijaKlijenata, Map<Long, Client> klijenti) {
		
		AckMessage ackMessage = (AckMessage) message;

		if(klijenti.containsKey(ackMessage.getUID())) {
			klijenti.get(ackMessage.getUID()).getReceived().add(ackMessage);
		}
		else {
			System.out.println("Nepoznat UID");
		}
	}
	
	private static void obradiBye(DatagramSocket dSocket, DatagramPacket packet, Message message,
			Map<ClientAddress, Long> inicijalizacijaKlijenata, Map<Long, Client> klijenti) {
		
		ByeMessage byeMessage = (ByeMessage) message;

		if(klijenti.containsKey(byeMessage.getUID())) {
			AckMessage ackMessage = new AckMessage(byeMessage.getNumber(), byeMessage.getUID());
			klijenti.get(byeMessage.getUID()).getToSend().add(ackMessage);
			// posaljemo ACK poruku klijentu

			// posaljemo bye poruku klijentu da zna zatvoriti dretvu
			klijenti.get(byeMessage.getUID()).getToSend().add(byeMessage);

			// maknemo klijenta iz liste klijenata
			klijenti.remove(byeMessage.getUID());
			for(Entry<ClientAddress, Long> entry : inicijalizacijaKlijenata.entrySet()) {
				if(entry.getValue() == byeMessage.getUID()) {
					inicijalizacijaKlijenata.remove(entry.getKey());
					break;
				}
			}
		}
		else {
			System.out.println("Nepoznat UID");
		}
		
	}
	
	
	private static void obradiOutMsg(DatagramSocket dSocket, DatagramPacket packet, Message message,
			Map<ClientAddress, Long> inicijalizacijaKlijenata, Map<Long, Client> klijenti) {
		
		OutMsgMessage outMsgMessage = (OutMsgMessage) message;

		if(klijenti.containsKey(outMsgMessage.getUID())) {
			AckMessage ackMessage = new AckMessage(outMsgMessage.getNumber(), outMsgMessage.getUID());
			klijenti.get(outMsgMessage.getUID()).getToSend().add(ackMessage);
			// posaljemo ACK poruku klijentu
			
			// posaljemo poruku svim ostalim klijentima
			String name = klijenti.get(outMsgMessage.getUID()).getName();
			InMsgMessage inMsgMessage = new InMsgMessage(outMsgMessage.getNumber(), name, outMsgMessage.getText());
			for(Entry<Long, Client> entry : klijenti.entrySet()) {
				entry.getValue().getToSend().add(inMsgMessage);
			}
		}
		else {
			System.out.println("Nepoznat UID");
		}
	}
	
	public static void main(String[] args) throws SocketException {
		
		if(args.length!=1) {
			System.out.println("Ocekivao sam port");
			return;
		}
		
		int port = Integer.parseInt(args[0]);

		// Stvori pristupnu tocku posluzitelja:
		@SuppressWarnings("resource")
		DatagramSocket dSocket = new DatagramSocket(null);
		dSocket.bind(new InetSocketAddress((InetAddress)null, port));
		
		
		Map<ClientAddress, Long> inicijalizacijaKlijenata = new HashMap<>();
		Map<Long, Client> klijenti = new HashMap<>();
		
		
		byte[] buffer;
		DatagramPacket packet;
		Message message;
		Random rand = new Random();

		// obradujemo poruke
		// posebne funkcije za obradu svake vrste poruke
		while(true){
			buffer = new byte[1024];
			packet = new DatagramPacket(buffer, buffer.length);
			message = null;

			try {
				dSocket.receive(packet);
				System.out.println("RECIEVED");
			} catch (IOException e) {
				continue;
			}
			
			// namjerno gubimo poruke
//			if(rand.nextInt(100) < 50) {
//				System.out.println("ODBACENA PORUKA");
//				continue;
//			}

			try{
				message = BytesToMessage.convertToMessage(packet.getData());
				if(message==null) {
					continue;
				}
				if(message.getType() == 1){
					System.out.println("OBRADI HELLO");
					obradiHello(dSocket, packet, message, inicijalizacijaKlijenata, klijenti);
				}
				else if(message.getType() == 2){
					System.out.println("OBRADI ACK");
					obradiAck(dSocket, packet, message, inicijalizacijaKlijenata, klijenti);
				}
				else if(message.getType() == 3){
					System.out.println("OBRADI BYE");
					obradiBye(dSocket, packet, message, inicijalizacijaKlijenata, klijenti);
				}
				else if(message.getType() == 4){
					System.out.println("OBRADI OUTMSG");
					obradiOutMsg(dSocket, packet, message, inicijalizacijaKlijenata, klijenti);
				}
				else{
					System.out.println("Nepoznata poruka");
				}
			}
			catch(Exception e) {
				continue;
			}
		}
	}


}
