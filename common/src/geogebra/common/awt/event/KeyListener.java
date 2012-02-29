package geogebra.common.awt.event;

import geogebra.common.euclidian.DrawTextField;
import geogebra.common.main.AbstractApplication;

public class KeyListener {

	private Object listenerClass;
	
	public Object getListenerClass() {
		return listenerClass;
	}

	public void setListenerClass(Object listenerClass) {
		this.listenerClass = listenerClass;
	}
	
	public void wrapKeyReleased(KeyEvent event){
		if (listenerClass instanceof DrawTextField.InputFieldKeyListener){
			((DrawTextField.InputFieldKeyListener) listenerClass).keyReleased(event);
		}
		else{
			AbstractApplication.debug("other type");
		}		
	}
	
}
