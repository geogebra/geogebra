package geogebra.web.presenter;

import geogebra.web.eventbus.MyEventBus;
import geogebra.web.html5.View;

public abstract class BasePresenter {
	
	protected MyEventBus eventBus;
	private View view;

	public MyEventBus getEventBus() {
	    return eventBus;
    }

	public void setEventBus(MyEventBus eventBus) {
	    this.eventBus = eventBus;
    }

	public View getView() {
	    return view;
    }

	public void setView(View view) {
	    this.view = view;
    }
	
	

}
