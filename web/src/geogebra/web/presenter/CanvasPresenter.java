package geogebra.web.presenter;

import geogebra.web.eventbus.MyEventBus;

public class CanvasPresenter extends BasePresenter {
	
	public CanvasPresenter(MyEventBus eventBus) {
	    this.eventBus = eventBus;
    }
	
	public void onPageLoad() {
		getEventBus().createApplicationAndAddTo(getView().getContainer());
	}
	
	// Reverse MVP
	public void syncCanvasSizeWithApplication() {
		getEventBus().syncCanvasSizeWithApplication();
	}
	
}
