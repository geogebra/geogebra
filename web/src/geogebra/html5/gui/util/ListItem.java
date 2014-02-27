package geogebra.html5.gui.util;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

//public class ListItem extends Widget{
//	
//	public ListItem(){
//		Element li = DOM.createElement("li");
//		setElement(li);
//	}
//	
//	public void add(Widget w){
//		this.getElement().appendChild(w.getElement());
//	}
//}



public class ListItem extends ComplexPanel {
	
	public ListItem(){
		//Element li = DOM.createElement("li");
		//setElement(li);
		setElement(Document.get().createLIElement());
	}
	
	@Override
	  public void add(Widget w) {
	    add(w, getElement());
	  }
	
//	@Override
//	  public void insert(Widget w) {
//		//insert(Widget child, Element container, int beforeIndex,
//	     // boolean domInsert)
//	    insert(w, getElement());
//	  }
	
	
	//public void add(Widget w){
	//	this.getElement().appendChild(w.getElement());
	//}
}