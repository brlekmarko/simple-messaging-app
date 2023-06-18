package hr.fer.zemris.chat.utils.messageImpl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Objects;

import hr.fer.zemris.chat.utils.Message;

public class InMsgMessage extends Message{

	static byte type = 5;
	String name;
	String text;
	
	public InMsgMessage(long number, String name, String text) {
		super(number);
		this.name = name;
		this.text = text;
	}

	public byte getType() {
		return type;
	}

	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
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
			dos.writeUTF(name);
			dos.writeUTF(text);
			dos.close();
		}catch(Exception e) {
			return null;
		}
		byte[] buf = bos.toByteArray();
		
		return buf;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(name, text);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		InMsgMessage other = (InMsgMessage) obj;
		return Objects.equals(name, other.name) && Objects.equals(text, other.text);
	}
	
}
