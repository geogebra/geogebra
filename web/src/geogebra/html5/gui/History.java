package geogebra.html5.gui;

import java.util.Stack;

public class History {
	
	public class HistoryItem {
		
		public void open() {
		    // TODO Auto-generated method stub
		    
	    }

	}
	private Stack<HistoryItem> items = new Stack<HistoryItem>();
	public void goBack(){
		HistoryItem item = items.pop();
		if(item != null){
			item.open();
		}
	}
	
	public void addItem(){
		items.push(new HistoryItem());
		//TODO
	}
}
