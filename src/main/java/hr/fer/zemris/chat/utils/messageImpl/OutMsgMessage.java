package hr.fer.zemris.chat.utils.messageImpl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import hr.fer.zemris.chat.utils.Message;

public class OutMsgMessage extends Message{

	static byte type = 4;
	long UID;
	String text;
	
	public OutMsgMessage(long number, long UID, String text) {
		super(number);
		this.UID = UID;
		this.text = text;
	}

	public byte getType() {
		return type;
	}

	public long getUID() {
		return UID;
	}
	
	public void setUID(long UID) {
		this.UID = UID;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}


	public byte[] toBytes() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeByte(type);
			dos.writeLong(number);
			dos.writeLong(UID);
			dos.writeUTF(text);
			dos.close();
		}catch(Exception e) {
			return null;
		}
		byte[] buf = bos.toByteArray();
		
		return buf;
	}
	
}
