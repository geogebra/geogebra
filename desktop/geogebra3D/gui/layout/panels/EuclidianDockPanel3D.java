package geogebra3D.gui.layout.panels;

import javax.swing.JComponent;

import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.main.AbstractApplication;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.gui.layout.panels.EuclidianDockPanelAbstract;
import geogebra.main.Application;
import geogebra3D.Application3D;

/**
 * Dock panel for the primary euclidian view.
 */
public class EuclidianDockPanel3D extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param app application
	 */
	public EuclidianDockPanel3D(Application app) {
		super(
			AbstractApplication.VIEW_EUCLIDIAN3D,	// view id 
			"GraphicsView3D", 				// view title
			ToolBar.getAllToolsNoMacros3D(),// toolbar string
			true,						// style bar?
			4,							// menu order
			'3' // ctrl-shift-3
		);
		
		this.app = app;
	}

	@Override
	protected JComponent loadComponent() {
		return ((Application3D)app).getEuclidianView3D().getJPanel();
	}
	
	@Override
	protected JComponent loadStyleBar() {
		return (JComponent)  ((Application3D)app).getEuclidianView3D().getStyleBar();
	}
	
	@Override
	public EuclidianViewND getEuclidianView() {
		return ((Application3D)app).getEuclidianView3D();
	}
}
