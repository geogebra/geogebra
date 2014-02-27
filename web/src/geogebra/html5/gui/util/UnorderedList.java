package geogebra.html5.gui.util;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class UnorderedList extends ComplexPanel {
	
	//Vector<ListItem> items = new Vector<ListItem>();

	public UnorderedList(){
		//Element ul;
		//ul = DOM.createElement("ul");
		//setElement(ul);
		setElement(Document.get().createULElement());
	}
	
	@Override
	  public void add(Widget w) {
	    add(w, getElement());
	  }
	
	//public ListItem get(int i){
	//	return items.get(i);
	//}
	
//	public int getSize(){
//		//return items.size();
//		getElement().
//	}
}

//public class UnorderedList extends FlowPanel{
//
//	Vector<ListItem> items = new Vector<ListItem>();
//	
//	public UnorderedList(){
//		super();
//	}
//	
//	public void add(Widget w) {
//		super.add(w);
//		if (w instanceof ListItem)
//			items.add((ListItem) w);
//	}
//
//	public ListItem get(int i) {
//		return items.get(i);
//	}
//
//	public int getSize() {
//		return items.size();
//	}
//	
//}