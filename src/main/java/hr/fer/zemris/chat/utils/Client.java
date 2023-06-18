package hr.fer.zemris.chat.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// klasa koja predstavlja jednog klijenta na serveru
public class Client {
	
	private String name;
	private List<Message> toSend;
	private List<Message> received;
	
	public Client(String name) {
		this.name = name;
		this.toSend = new ArrayList<Message>();
		this.received = new ArrayList<Message>();
	}

	public Client(String name, List<Message> toSend, List<Message> received) {
		this.name = name;
		this.toSend = toSend;
		this.received = received;
	}
	
	public List<Message> getToSend(){
		return this.toSend;
	}
	
	public void setToSend(List<Message> list) {
		this.toSend = list;
	}
	
	public List<Message> getReceived(){
		return this.received;
	}
	
	public void setReceived(List<Message> list) {
		this.received = list;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(name, received, toSend);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Client other = (Client) obj;
		return Objects.equals(name, other.name) && Objects.equals(received, other.received)
				&& Objects.equals(toSend, other.toSend);
	}

}
