package geogebra.common.euclidian.event;

import geogebra.common.euclidian.draw.DrawTextField;
import geogebra.common.main.App;

public class FocusListener {
	
	private Object listenerClass;
	
	protected void wrapFocusGained(FocusEvent event) {
		if (listenerClass instanceof DrawTextField.InputFieldListener){
			((DrawTextField.InputFieldListener) listenerClass).focusGained(event);
		}
		else{
			App.debug("other type");
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
			App.debug("other type");
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
