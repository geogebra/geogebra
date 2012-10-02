package geogebra.common.plugin;

import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;

import java.util.ArrayList;

/**
 * @author arno
 *
 */
public class EventDispatcher implements View {
	
	private App app;
	private ArrayList<EventListener> listeners = new ArrayList<EventListener>();
	
	public EventDispatcher(App app) {
		this.app = app;
	}
	
	public void addEventListener(EventListener listener) {
		listeners.add(listener);
	}
	
	public void dispatchEvent(Event evt) {
		for (EventListener listener : listeners) {
			listener.sendEvent(evt);
		}
	}
	
	public void dispatchEvent(EventType evtType, GeoElement geo, String arg) {
		dispatchEvent(new Event(evtType, geo, arg));
	}
	
	public void dispatchEvent(EventType evtType, GeoElement geo) {
		dispatchEvent(new Event(evtType, geo));
	}
	
	/*
	 * Implementation of View	
	 */
	
	public void add(GeoElement geo) {
		dispatchEvent(EventType.ADD, geo);
	}

	public void remove(GeoElement geo) {
		dispatchEvent(EventType.REMOVE, geo);
	}

	public void rename(GeoElement geo) {
		dispatchEvent(EventType.RENAME, geo);
	}

	public void update(GeoElement geo) {
		dispatchEvent(EventType.UPDATE, geo);
	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}

	public void repaintView() {
		// TODO Auto-generated method stub
		
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public void clearView() {
		// TODO Auto-generated method stub
		
	}

	public void setMode(int mode) {
		// TODO Auto-generated method stub
		
	}

	public int getViewID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasFocus() {
		return false;
	}

	public void repaint() {
		// TODO Auto-generated method stub
		
	}

	public boolean isShowing() {
		return false;
	}
	
	
}
