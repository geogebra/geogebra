package geogebra.common.euclidian.event;

import geogebra.common.euclidian.DrawTextField;
import geogebra.common.euclidian.event.FocusEvent;
import geogebra.common.main.AbstractApplication;

public class FocusListener {
	
	private Object listenerClass;
	
	protected void wrapFocusGained(FocusEvent event) {
		if (listenerClass instanceof DrawTextField.InputFieldListener){
			((DrawTextField.InputFieldListener) listenerClass).focusGained(event);
		}
		else{
			AbstractApplication.debug("other type");
		}
	}
	
	protected void wrapFocusGained(){
		wrapFocusGained(null);
	}
	
	protected void wrapFocusLost(FocusEvent event) {
		if (listenerClass instanceof DrawTextField.InputFieldListener){
			((DrawTextField.InputFieldListener) listenerClass).focusLost(event);
		}
		else{
			AbstractApplication.debug("other type");
		}
	}
	
	protected void wrapFocusLost(){
		wrapFocusLost(null);
	}

	public Object getListenerClass() {
		return listenerClass;
	}

	public void setListenerClass(Object listenerClass) {
		this.listenerClass = listenerClass;
	}
}
