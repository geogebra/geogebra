package org.geogebra.desktop.gui.layout.panels;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.ConstructionProtocolSettings;
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolNavigationD;
import org.geogebra.desktop.main.AppD;

/**
 * Dock panel for the primary euclidian view.
 */
public class EuclidianDockPanel extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;
	private AppD app;
	
	/**
	 * Panel to hold euclidian view and navigation bar if necessary. 
	 */
	private JPanel panel;
	
	/**
	 * Component of the construction protocol navigation bar,
	 * invisible if not needed.
	 */
	private ConstructionProtocolNavigationD consProtNav;
	
	/**
	 * @param app
	 */
	public EuclidianDockPanel(AppD app, String toolbar) {
		super(
			App.VIEW_EUCLIDIAN,	// view id 
			"DrawingPad", 				// view title
			toolbar,						// toolbar string
			true,						// style bar?
			4,							// menu order
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
		return (JComponent) app.getEuclidianView1().getStyleBar();
	}

	@Override
	protected JComponent loadComponent() {
		if(panel == null) {
			panel = new JPanel(new BorderLayout());
			
			panel.add(app.getEuclidianView1().getJPanel(), BorderLayout.CENTER);
			
			consProtNav = (ConstructionProtocolNavigationD) app.getGuiManager().getConstructionProtocolNavigation();

			ConstructionProtocolSettings cps = app.getSettings().getConstructionProtocol();
			consProtNav.settingsChanged(cps);
			cps.addListener(consProtNav);
				
			if (app.getShowCPNavNeedsUpdate(App.VIEW_EUCLIDIAN)) {
				app.setShowConstructionProtocolNavigation(
						app.showConsProtNavigation(App.VIEW_EUCLIDIAN),
						App.VIEW_EUCLIDIAN);
			}
			consProtNav.getImpl().setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray));
			consProtNav.getImpl().setVisible(
					app.showConsProtNavigation(App.VIEW_EUCLIDIAN));
			
			panel.add(consProtNav.getImpl(), BorderLayout.SOUTH); // may be invisible, but made visible later
		}
		
		return panel;
	}
	
	/**
	 * As the component of this panel is not just the euclidian view 
	 * as asserted in EuclidianDockPanelAbstract we have to override
	 * this method to provide the correct euclidian view.
	 */
	@Override
	public EuclidianView getEuclidianView() {
		return app.getEuclidianView1();
	}
	
	@Override
	public ImageIcon getIcon() { 
		return app.getMenuIcon("menu_view_graphics.png");
	}
}
