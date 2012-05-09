package geogebra.common.euclidian.event;

import geogebra.common.euclidian.DrawComboBox;
import geogebra.common.main.AbstractApplication;

public class ActionListener {
	
	private Object listenerClass;
	

	protected void wrapActionPerformed(ActionEvent event) {
		
		
		if (listenerClass instanceof DrawComboBox.ActionListener){
			((DrawComboBox.ActionListener) listenerClass).actionPerformed(event);
		}
		else{
			AbstractApplication.debug("other type: "+listenerClass.getClass());
		}
	}
	
	public Object getListenerClass() {
		return listenerClass;
	}

	public void setListenerClass(Object listenerClass) {
		this.listenerClass = listenerClass;
	}
}
