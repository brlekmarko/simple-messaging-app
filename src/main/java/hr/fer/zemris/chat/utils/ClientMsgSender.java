package hr.fer.zemris.chat.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.List;

import hr.fer.zemris.chat.utils.messageImpl.AckMessage;

// posao klijenta koji salje poruke ako ih ima
// ako salje potvrdu, salje je samo jednom, inace radi retransmisiju 10 puta
public class ClientMsgSender implements Runnable{
	
	private List<Message> toSend;
	private List<Message> received;
	private DatagramSocket socket;
	private InetSocketAddress address;
	
	
	public ClientMsgSender(List<Message> toSend, List<Message> received, DatagramSocket socket, InetSocketAddress address) {
		this.toSend = toSend;
		this.received = received;
		this.socket = socket;
		this.address = address;
	}


	@Override
	public void run() {
		
		DatagramPacket sendPacket;
        Message message;
        byte[] sendBytes;
        int retranmissions = 0;
        boolean done = false;

		while (true) {

			if(toSend.isEmpty()){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				continue;
			}

			message = toSend.remove(0);
			sendBytes = message.toBytes();
			sendPacket = new DatagramPacket(sendBytes, sendBytes.length);
			sendPacket.setSocketAddress(address);

			// ako je ACK, salji ga samo jednom
			if(message.getType() == 2){
				try {
					socket.send(sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
			// inace radi retransmisiju 10 puta
			else {
				retranmissions = 0;
				done = false;
				while(!done && retranmissions < 10){
					try {
						socket.send(sendPacket);
					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}
					for(int i = 0; i < 20; i++){
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {}

                        if(received.isEmpty()){
                            continue;
                        }


                        for(Message m : received){
                            if(m.getType() == 2){
                                AckMessage ackMessage = (AckMessage) m;
                                if(ackMessage.getNumber() == message.getNumber()){
                                    received.remove(m);
                                    System.out.println("PRIMIO ACK");
                                    done = true;
                                    break;
                                }
                            }
                        }
                        if(done) break;
                    }
                    if(done) break;
                }
				// ako smo obradili BYE poruku, zavrsi dretvu
				if(message.getType() == 3){
					// zavrsi dretvu
					break;
				}
			}
		}
		socket.close();
	}
}
