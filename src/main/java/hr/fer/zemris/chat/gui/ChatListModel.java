package hr.fer.zemris.chat.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import hr.fer.zemris.chat.utils.messageImpl.InMsgMessage;


// ovo je model liste koja se koristi u JListi
// svaki put kad se doda nova poruka, ona se dodaje i u ovu listu
public class ChatListModel implements ListModel<String>{

	private List<String> lista;
	private List<InMsgMessage> poruke;
	private List<ListDataListener> promatraci;
	
	
	public ChatListModel() {
		super();
		this.lista = new ArrayList<>();
		this.poruke = new ArrayList<>();
		this.promatraci = new ArrayList<>();
	}
	
	@Override
	public int getSize() {
		return this.lista.size();
	}

	@Override
	public String getElementAt(int index) {
		return this.lista.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		promatraci.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		promatraci.remove(l);
	}
	
	
	public void addMessage(InMsgMessage msg) {
		
		if(poruke.contains(msg)) return;
		
		String name = msg.getName();
		String text = msg.getText();
		
		String toShow = name + ": " + text;
		
		lista.add(toShow);
		poruke.add(msg);
		
		ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, lista.size()-1, lista.size()-1);
		for(ListDataListener l : promatraci) {
			l.contentsChanged(event);
		}
	}

}
