package geogebra3D.gui.layout.panels;

import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import geogebra.gui.layout.DockPanel;
import geogebra.gui.layout.panels.EuclidianDockPanelAbstract;
import geogebra.gui.toolbar.Toolbar;
import geogebra.main.Application;
import geogebra3D.Application3D;

/**
 * Dock panel for the primary euclidian view.
 */
public class EuclidianDockPanel3D extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;
	private Application app;
	
	/**
	 * @param app
	 */
	public EuclidianDockPanel3D(Application app) {
		super(
			Application.VIEW_EUCLIDIAN3D,	// view id 
			"GraphicsView3D", 				// view title
			Toolbar.getAllToolsNoMacros3D(),// toolbar string
			true,						// style bar?
			4,							// menu order
			'3' // ctrl-shift-3
		);
		
		this.app = app;
	}



	protected JComponent loadComponent() {
		return ((Application3D)app).getEuclidianView3D();
	}
	
	protected JComponent loadStyleBar() {
		return ((Application3D)app).getEuclidianView3D().getStyleBar();
	}
}
