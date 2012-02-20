package geogebra.web.main;

import java.util.Iterator;

import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;  //*
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;  //*
import com.google.gwt.user.client.ui.Widget;

public class EuclidianViewPanel extends Composite implements HasWidgets, HasAllKeyHandlers{

	private AbsolutePanel evPanel;
	private FocusPanel focusPanel;

	public EuclidianViewPanel() {
		evPanel = new AbsolutePanel();
		focusPanel = new FocusPanel();
		focusPanel.setWidget(evPanel);
		initWidget(focusPanel);
	}
	
	
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
	    return focusPanel.addKeyUpHandler(handler);
    }

	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
	    return focusPanel.addKeyDownHandler(handler);
    }

	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
	    return focusPanel.addKeyPressHandler(handler);
    }

	public void add(Widget w) {
	    evPanel.add(w);	    
    }

	public void clear() {
	    evPanel.clear();
    }

	public Iterator<Widget> iterator() {
	    return evPanel.iterator();
    }

	public boolean remove(Widget w) {
		return evPanel.remove(w);
	}


	public Widget getWidget(int i) {
	    return evPanel.getWidget(i);
    }


	public void add(HorizontalPanel panel, int x, int y) {
	    evPanel.add(panel, x, y);
	    
    }

}
