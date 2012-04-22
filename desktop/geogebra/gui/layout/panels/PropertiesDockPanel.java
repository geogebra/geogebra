package geogebra.gui.layout.panels;

import geogebra.common.main.AbstractApplication;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.view.properties.PropertiesView;
import geogebra.main.Application;

import javax.swing.ImageIcon;
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
			AbstractApplication.VIEW_PROPERTIES,	// view id 
			"Properties", 			// view title phrase
			null,						// toolbar string
			false,						// style bar?
			7, 							// menu order
			'E'							// menu shortcut
		);
		
		this.app = app;
		this.setOpenInFrame(true);
	}

	@Override
	protected JComponent loadComponent() {
		
		PropertiesView view = app.getGuiManager().getPropertiesView();
		
		if (isOpenInFrame())
			view.windowPanel();
		else
			view.unwindowPanel();
		
		return view;
	}
	
	@Override
	protected void windowPanel() {
		super.windowPanel();
		app.getGuiManager().getPropertiesView().windowPanel();		
	}
	
	@Override
	protected void unwindowPanel() {
		super.unwindowPanel();
		app.getGuiManager().getPropertiesView().unwindowPanel();
	}

	@Override
	public ImageIcon getIcon() { 
			return app.getImageIcon("view-properties24.png");
	}
	
}
