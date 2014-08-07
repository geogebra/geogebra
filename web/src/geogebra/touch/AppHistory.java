package geogebra.touch;

import geogebra.html5.gui.History;

import java.util.Stack;

public class AppHistory extends History {
	
	public class HistoryItem {

		public void open() {
		    // TODO Auto-generated method stub
		    
	    }

	}
	private Stack<HistoryItem> items;
	public void goBack(){
		HistoryItem item = items.pop();
		if(item!=null){
			item.open();
		}
	}
}
