package geogebra.web.cas.view;

import geogebra.common.cas.view.CASInputHandler;
import geogebra.common.cas.view.CASView;
import geogebra.common.main.App;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.ui.Widget;



public class CASViewW extends CASView {

	private CASComponentW component;
	private AppW app;
	private CASTableW consoleTable;

	public CASViewW(AppW app){
		component = new CASComponentW();
		kernel = app.getKernel();
		this.app = app;
		
		getCAS();	

		// init commands subtable for cas-commands in inputbar-help
		kernel.getAlgebraProcessor().enableCAS();
		
		// CAS input/output cells
		CASTableControllerW ml = new CASTableControllerW(this,app);
		consoleTable = new CASTableW(app,ml);	
		component.add(consoleTable);
		//SelectionHandler.disableTextSelectInternal(component.getElement(), true);
				// input handler
		casInputHandler = new CASInputHandler(this);

		// addFocusListener(this);
		
		component.addDomHandler(ml, MouseDownEvent.getType());
		component.addDomHandler(ml, MouseUpEvent.getType());
		component.addDomHandler(ml, MouseMoveEvent.getType());
		component.addDomHandler(ml, ClickEvent.getType());
		component.addDomHandler(ml, DoubleClickEvent.getType());
		
		
		getCAS().initCurrentCAS();
		getCAS().getMPReduce().reset();
				
	}
	public void repaintView() {
	    // TODO Auto-generated method stub
	    
    }

	public boolean hasFocus() {
	    // TODO Auto-generated method stub
	    return false;
    }

	public boolean isShowing() {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public CASTableW getConsoleTable() {
	    return consoleTable;
    }

	@Override
    public App getApp() {
	    return app;
    }

	@Override
    public void showSubstituteDialog(String prefix, String evalText,
            String postfix, int selRow) {
	    // TODO Auto-generated method stub
	    
    }
	public Widget getComponent() {
	    return component;
    }
	public CASStylebarW getCASStyleBar() {
	    // TODO Auto-generated method stub
	    return null;
    }

}
