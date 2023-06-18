package hr.fer.zemris.chat.utils;

import java.util.Objects;

// klasa koja predstavlja poruku, treba je naslijediti
public abstract class Message {
	
	protected long number;

	public abstract byte getType();
	public abstract byte[] toBytes();
	
	public Message(long number) {
		this.number = number;
	}
	
	public long getNumber() {
		return this.number;
	}
	
	public void setNumber(long number) {
		this.number = number;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(number);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		return number == other.number;
	}
}
