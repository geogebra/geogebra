package geogebra.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.common.main.settings.ConstructionProtocolSettings;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

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
	private ConstructionProtocolNavigation consProtNav;
	
	/**
	 * @param app
	 */
	public EuclidianDockPanel(AppD app, String toolbar) {
		super(
			App.VIEW_EUCLIDIAN,	// view id 
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
		return (JComponent) app.getEuclidianView1().getStyleBar();
	}

	@Override
	protected JComponent loadComponent() {
		if(panel == null) {
			panel = new JPanel(new BorderLayout());
			
			panel.add(app.getEuclidianView1().getJPanel(), BorderLayout.CENTER);
			
			consProtNav = app.getConstructionProtocolNavigation();

			ConstructionProtocolSettings cps = app.getSettings().getConstructionProtocol();
			consProtNav.settingsChanged(cps);
			cps.addListener(consProtNav);
				
			if (app.getShowCPNavNeedsUpdate()){
				app.setShowConstructionProtocolNavigation(app.showConsProtNavigation());
			}
			consProtNav.getImpl().setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray));
			consProtNav.getImpl().setVisible(app.showConsProtNavigation());
			
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
	public EuclidianViewND getEuclidianView() {
		return app.getEuclidianView1();
	}
	
	@Override
	public ImageIcon getIcon() { 
			return app.getImageIcon("view-graphics24.png");
	}
}
