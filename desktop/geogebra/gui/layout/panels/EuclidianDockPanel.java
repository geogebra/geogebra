package geogebra.gui.layout.panels;

import geogebra.euclidian.EuclidianViewInterface;
import geogebra.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.main.Application;
import geogebra.main.settings.ConstructionProtocolSettings;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Dock panel for the primary euclidian view.
 */
public class EuclidianDockPanel extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;
	private Application app;
	
	/**
	 * Panel to hold euclidian view and navigation bar if necessary. 
	 */
	private JPanel panel;
	
	/**
	 * Component of the construction protocol navigation bar,
	 * invisible if not needed.
	 */
	private JComponent consProtNav;
	
	/**
	 * @param app
	 */
	public EuclidianDockPanel(Application app, String toolbar) {
		super(
			Application.VIEW_EUCLIDIAN,	// view id 
			"DrawingPad", 				// view title
			toolbar,						// toolbar string
			true,						// style bar?
			5,							// menu order
			'1' // ctrl-shift-1
		);
		
		this.app = app;
	}

//	@Override
//	public ImageIcon getIcon() {
//		return app.getImageIcon("document-properties.png");
//	}

	@Override
	protected JComponent loadStyleBar() {		
		return app.getEuclidianView().getStyleBar();
	}

	@Override
	protected JComponent loadComponent() {
		if(panel == null) {
			panel = new JPanel(new BorderLayout());
			
			panel.add(app.getEuclidianView(), BorderLayout.CENTER);
			
			consProtNav = app.getGuiManager().getConstructionProtocolNavigation();

			ConstructionProtocolSettings cps = app.getSettings().getConstructionProtocol();
			((ConstructionProtocolNavigation) consProtNav).settingsChanged(cps);
			cps.addListener((ConstructionProtocolNavigation)consProtNav);
				
			if (app.getShowCPNavNeedsUpdate()){
				app.setShowConstructionProtocolNavigation(app.showConsProtNavigation());
			}
			consProtNav.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray));
			consProtNav.setVisible(app.showConsProtNavigation());
			
			panel.add(consProtNav, BorderLayout.SOUTH); // may be invisible, but made visible later
		}
		
		return panel;
	}
	
	/**
	 * As the component of this panel is not just the euclidian view 
	 * as asserted in EuclidianDockPanelAbstract we have to override
	 * this method to provide the correct euclidian view.
	 */
	@Override
	public EuclidianViewInterface getEuclidianView() {
		return app.getEuclidianView();
	}
}
