package geogebra.html5.gui.util;

import geogebra.common.main.App;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListItem extends ComplexPanel{
	
	public ListItem(){
		Element el = Document.get().createLIElement();
		setElement(el);
		el.setTabIndex(0);		
	}
	
	@Override
	public void add(Widget w) {
		Element el = getElement();
	    add(w, el);
	}
	
	public void setFocus(boolean focused) {
		if(focused){
			getElement().focus();
		} else {
			getElement().blur();
		}
		
        
    }

	public void setTabIndex(int index) {
		getElement().setTabIndex(index);
    }
}