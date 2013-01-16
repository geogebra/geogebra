package geogebra.web.cas.view;

import geogebra.common.cas.view.CASInputHandler;
import geogebra.common.cas.view.CASTable;
import geogebra.common.cas.view.CASView;
import geogebra.common.cas.view.RowHeader;
import geogebra.common.main.App;

import com.google.gwt.user.client.ui.Widget;



public class CASViewW extends CASView {

	private CASComponentW component;
	private App app;
	private CASTableW consoleTable;

	public CASViewW(App app){
		component = new CASComponentW();
		kernel = app.getKernel();
		this.app = app;
		
		getCAS();	

		// init commands subtable for cas-commands in inputbar-help
		kernel.getAlgebraProcessor().enableCAS();
		
		// CAS input/output cells
		consoleTable = new CASTableW();	
		component.add(consoleTable);
				// input handler
		casInputHandler = new CASInputHandler(this);

		// addFocusListener(this);
		
		
		
		getCAS().initCurrentCAS();
				
	}
	public void repaintView() {
	    // TODO Auto-generated method stub
	    
    }

	public boolean hasFocus() {
	    // TODO Auto-generated method stub
	    return false;
    }

	public void repaint() {
	    // TODO Auto-generated method stub
	    
    }

	public boolean isShowing() {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public CASTable getConsoleTable() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public App getApp() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public RowHeader getRowHeader() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void showSubstituteDialog(String prefix, String evalText,
            String postfix, int selRow) {
	    // TODO Auto-generated method stub
	    
    }
	public Widget getComponent() {
	    return component;
    }

}
