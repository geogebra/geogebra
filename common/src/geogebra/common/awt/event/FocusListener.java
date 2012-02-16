package geogebra.common.awt.event;

import geogebra.common.awt.event.FocusEvent;
import geogebra.common.euclidian.DrawTextField;
import geogebra.common.main.AbstractApplication;

public class FocusListener {
	
	private Object listenerClass;
	
	protected void wrapFocusGained(FocusEvent event) {
		// TODO Auto-generated method stub
		AbstractApplication.debug("focus gained");
		if (listenerClass instanceof DrawTextField.InputFieldListener){
			((DrawTextField.InputFieldListener) listenerClass).focusGained(event);
		}
		else{
			AbstractApplication.debug("other type");
		}
	}
	
	protected void wrapFocusLost(FocusEvent event) {
		AbstractApplication.debug("focus lost");
		if (listenerClass instanceof DrawTextField.InputFieldListener){
			((DrawTextField.InputFieldListener) listenerClass).focusLost(event);
		}
		else{
			AbstractApplication.debug("other type");
		}
	}

	public Object getListenerClass() {
		return listenerClass;
	}

	public void setListenerClass(Object listenerClass) {
		this.listenerClass = listenerClass;
	}
}
