package geogebra.html5.gui.util;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListItem extends ComplexPanel {
	
	public ListItem(){
		setElement(Document.get().createLIElement());
	}
	
	@Override
	  public void add(Widget w) {
		Element el = getElement();
	    add(w, el);
	  }
}