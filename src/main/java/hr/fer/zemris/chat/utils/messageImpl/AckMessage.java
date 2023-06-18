package hr.fer.zemris.chat.utils.messageImpl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import hr.fer.zemris.chat.utils.Message;

public class AckMessage extends Message{

	static byte type = 2;
	long UID;
	
	public AckMessage(long number, long UID) {
		super(number);
		this.UID = UID;
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


	public byte[] toBytes() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeByte(type);
			dos.writeLong(number);
			dos.writeLong(UID);
			dos.close();
		}catch(Exception e) {
			return null;
		}
		byte[] buf = bos.toByteArray();
		
		return buf;
	}
	
}
