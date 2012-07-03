package geogebra.common.euclidian.event;

import geogebra.common.euclidian.DrawList;
import geogebra.common.main.App;

public class ActionListener {
	
	private Object listenerClass;
	

	protected void wrapActionPerformed(ActionEvent event) {
		
		
		if (listenerClass instanceof DrawList.ActionListener){
			((DrawList.ActionListener) listenerClass).actionPerformed(event);
		}
		else{
			App.debug("other type: "+listenerClass.getClass());
		}
	}
	
	public Object getListenerClass() {
		return listenerClass;
	}

	public void setListenerClass(Object listenerClass) {
		this.listenerClass = listenerClass;
	}
}
