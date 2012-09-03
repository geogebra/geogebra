package geogebra.common.euclidian.event;

import geogebra.common.euclidian.draw.DrawTextField;
import geogebra.common.main.App;

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
			App.debug("other type");
		}		
	}
	
}
