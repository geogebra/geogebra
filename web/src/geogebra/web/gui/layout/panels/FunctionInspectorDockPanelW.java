package geogebra.web.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.layout.DockPanelW;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author gabor
 * 
 * FunctionInspector dockpanel for Web
 *
 */
public class FunctionInspectorDockPanelW extends DockPanelW {
	
	/**
	 * default width of this panel
	 */
	public static final int DEFAULT_WIDTH = 480;
	private App app;

	/**
	 * @param app App
	 * Creates panel
	 */
	public FunctionInspectorDockPanelW(App app) {
		super(App.VIEW_FUNCTION_INSPECTOR, // view id
				"FunctionInspector", // view title phrase
				null, // toolbar string
				true, // style bar?
				-1, // menu order
				'F' // menu shortcut
		);

		this.app = app;
		this.setOpenInFrame(true);
		this.setEmbeddedSize(DEFAULT_WIDTH);
		
	    this.app = app;
    }

	@Override
	protected Widget loadComponent() {
		return ((DialogManagerW) app.getDialogManager()).getFunctionInspector().getWrappedPanel();
	}

	@Override
	public void showView(boolean b) {
		App.debug("FunctionInspector showView " + b);
	}
	
	@Override
	protected void closePanel(boolean isPermanent) {
		App.debug("FunctionInspector closePanel");
		((DialogManagerW) app.getDialogManager()).getFunctionInspector().setInspectorVisible(false);
		super.closePanel(isPermanent);
	}
	
	@Override
	protected Widget loadStyleBar() {
		return null;
	}
	
	@Override
	public boolean isStyleBarEmpty(){
		return true;
	}

}
