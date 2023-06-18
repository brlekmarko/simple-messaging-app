package hr.fer.zemris.chat.utils;

import java.net.SocketAddress;
import java.util.Objects;


// ključ za mapiranje klijenata da provjerimo da li je klijent već prijavljen
public class ClientAddress {

	private SocketAddress address;
	private Long randKey;
	
	
	public ClientAddress(SocketAddress address, Long randKey) {
		super();
		this.address = address;
		this.randKey = randKey;
	}
	
	public SocketAddress getAddress() {
		return address;
	}
	public void setAddress(SocketAddress address) {
		this.address = address;
	}
	public Long getRandKey() {
		return randKey;
	}
	public void setRandKey(Long randKey) {
		this.randKey = randKey;
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, randKey);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClientAddress other = (ClientAddress) obj;
		return Objects.equals(address, other.address) && Objects.equals(randKey, other.randKey);
	}

	
}
