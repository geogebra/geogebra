package geogebra.gui.layout.panels;

import geogebra.gui.layout.DockPanel;
import geogebra.gui.view.properties.PropertiesView;
import geogebra.main.Application;

import javax.swing.JComponent;

/**
 * Dock panel for the algebra view.
 */
public class PropertiesDockPanel extends DockPanel {
	private static final long serialVersionUID = 1L;
	private Application app;
	
	/**
	 * @param app
	 */
	public PropertiesDockPanel(Application app) {
		super(
			Application.VIEW_PROPERTIES,	// view id 
			"Properties", 			// view title phrase
			null,						// toolbar string
			false,						// style bar?
			7, 							// menu order
			'P'							// menu shortcut
		);
		
		this.app = app;
	}


	
	protected JComponent loadComponent() {
		
		PropertiesView view = app.getGuiManager().getPropertiesView();
		
		if (isOpenInFrame())
			view.windowPanel();
		else
			view.unwindowPanel();
		
		return view;
	}
	
	protected void windowPanel() {
		super.windowPanel();
		app.getGuiManager().getPropertiesView().windowPanel();
		
	}
	
	protected void unwindowPanel() {
		super.unwindowPanel();
		app.getGuiManager().getPropertiesView().unwindowPanel();
		
	}

}
