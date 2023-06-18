package hr.fer.zemris.chat.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import hr.fer.zemris.chat.utils.messageImpl.AckMessage;
import hr.fer.zemris.chat.utils.messageImpl.ByeMessage;
import hr.fer.zemris.chat.utils.messageImpl.HelloMessage;
import hr.fer.zemris.chat.utils.messageImpl.InMsgMessage;
import hr.fer.zemris.chat.utils.messageImpl.OutMsgMessage;

public class BytesToMessage {

	// funkcija koja pretvara niz bajtova u poruku
	public static Message convertToMessage(byte[] bytes) throws InvalidMessageException {


		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bais);

		byte type = 0;

		try {
			type = dis.readByte();
		} catch (Exception e) {
			throw new InvalidMessageException("Tried to read type, but failed.");
		}
		
		switch(type) {
			case 1:
				//HelloMessage

				try {
					long number = dis.readLong();
					String name = dis.readUTF();
					long randkey = dis.readLong();
					return new HelloMessage(number, name, randkey);
				} catch (Exception e) {
					throw new InvalidMessageException("Tried to convert to HelloMessage, but failed.");
				}
				

			case 2:
				//AckMessage

				try {
					long number = dis.readLong();
					long UID = dis.readLong();
					return new AckMessage(number, UID);
				} catch (Exception e) {
					throw new InvalidMessageException("Tried to convert to AckMessage, but failed.");
				}

			case 3:
				//ByeMessage
				
				try {
					long number = dis.readLong();
					long UID = dis.readLong();
					return new ByeMessage(number, UID);
				} catch (Exception e) {
					throw new InvalidMessageException("Tried to convert to ByeMessage, but failed.");
				}

			case 4:
				//OutMsgMessage

				try {
					long number = dis.readLong();
					long UID = dis.readLong();
					String text = dis.readUTF();
					return new OutMsgMessage(number, UID, text);
				} catch (Exception e) {
					throw new InvalidMessageException("Tried to convert to OutMsgMessage, but failed.");
				}

			case 5:
				//InMsgMessage
				
				try {
					long number = dis.readLong();
					String name = dis.readUTF();
					String text = dis.readUTF();
					return new InMsgMessage(number, name, text);
				} catch (Exception e) {
					throw new InvalidMessageException("Tried to convert to InMsgMessage, but failed.");
				}
			default:
				throw new InvalidMessageException();
		}
	}
}
