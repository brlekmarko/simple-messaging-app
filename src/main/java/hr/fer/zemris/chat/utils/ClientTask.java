package hr.fer.zemris.chat.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import hr.fer.zemris.chat.utils.messageImpl.AckMessage;

// posao klijenta na serveru koji salje poruke ako ih ima
// ako salje potvrdu, salje je samo jednom, inace radi retransmisiju 10 puta
public class ClientTask implements Runnable{

    private DatagramSocket serverSocket;
    private ClientAddress clientAddress;
    private Client client;
    
    public ClientTask(DatagramSocket serverSocket, ClientAddress clientAddress, Client client) {
        this.serverSocket = serverSocket;
        this.clientAddress = clientAddress;
        this.client = client;
    }
    
    @Override
    public void run() {
        DatagramPacket sendPacket;
        Message message;
        byte[] sendBytes;
        int retranmissions = 0;
        boolean done = false;
        while (true) {

            if(client.getToSend().isEmpty()){
                try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
                continue;
            }
            //System.out.println("SENDING");
            message = client.getToSend().remove(0);
            
            sendBytes = message.toBytes();
            sendPacket = new DatagramPacket(sendBytes, sendBytes.length);
            sendPacket.setSocketAddress(clientAddress.getAddress());

            // ako je primio bye, zavrsi dretvu
            if(message.getType() == 3){
                // zavrsi dretvu
                // primio je bye
                break;
            }
            
            // ako je ACK, salji ga samo jednom
            if(message.getType() == 2) {
                try {
                    System.out.println("SALJEM ACK");
                    serverSocket.send(sendPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            // ako je poruka, salji je 10 puta
            else {
                retranmissions = 0;
                done = false;
                while (!done && retranmissions < 10) {
                    retranmissions++;

                    try {
                        System.out.println("SALJEM PAKET TIPA " + message.getType());
                        serverSocket.send(sendPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }

                    for(int i = 0; i < 20; i++){
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {}

                        if(client.getReceived().isEmpty()){
                            continue;
                        }


                        for(Message m : client.getReceived()){
                            if(m.getType() == 2){
                                AckMessage ackMessage = (AckMessage) m;
                                if(ackMessage.getNumber() == message.getNumber()){
                                    client.getReceived().remove(m);
                                    System.out.println("PRIMIO ACK");
                                    done = true;
                                    break;
                                }
                            }
                        }
                        if(done) break;
                    }
                }
            }
        }
        System.out.println("GASIM DRETVU");
    }

}
