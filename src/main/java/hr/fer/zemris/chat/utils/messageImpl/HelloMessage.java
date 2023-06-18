package hr.fer.zemris.chat.utils.messageImpl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import hr.fer.zemris.chat.utils.Message;

public class HelloMessage extends Message{

	static byte type = 1;
	String name;
	long randkey;
	
	public HelloMessage(long number, String name, long randkey) {
		super(number);
		this.name = name;
		this.randkey = randkey;
	}

	public byte getType() {
		return type;
	}

	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	public long getRandkey() {
		return randkey;
	}
	
	public void setRandey(long randkey) {
		this.randkey = randkey;
	}


	public byte[] toBytes() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeByte(type);
			dos.writeLong(number);
			dos.writeUTF(name);
			dos.writeLong(randkey);
			dos.close();
		}catch(Exception e) {
			return null;
		}
		byte[] buf = bos.toByteArray();
		
		return buf;
	}
	
}
