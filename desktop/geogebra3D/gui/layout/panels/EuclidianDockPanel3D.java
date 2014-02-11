package geogebra3D.gui.layout.panels;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.main.App;
import geogebra.gui.layout.panels.EuclidianDockPanelAbstract;
import geogebra.main.AppD;
import geogebra3D.App3D;
import geogebra3D.euclidian3D.EuclidianView3DD;

import javax.swing.JComponent;

/**
 * Dock panel for the primary euclidian view.
 */
public class EuclidianDockPanel3D extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param app application
	 */
	public EuclidianDockPanel3D(AppD app) {
		super(
			App.VIEW_EUCLIDIAN3D,	// view id 
			"GraphicsView3D", 				// view title
			ToolBar.getAllToolsNoMacros3D(),// toolbar string
			true,						// style bar?
			4,							// menu order
			'3' // ctrl-shift-3
		);
		
		setApp(app);
		
		setEmbeddedSize(300);
	}

	@Override
	protected JComponent loadComponent() {
		return ((EuclidianView3DD) app.getEuclidianView3D()).getJPanel();
	}
	
	@Override
	protected JComponent loadStyleBar() {
		return (JComponent)  ((App3D)app).getEuclidianView3D().getStyleBar();
	}
	
	@Override
	public EuclidianView getEuclidianView() {
		return ((App3D)app).getEuclidianView3D();
	}
	
	@Override
	public boolean updateResizeWeight(){

		resumeRenderer();
		
		return true;
	}
	
	@Override
	public boolean isEuclidianDockPanel3D() {
		return true;
	}
	
	@Override
	public void updatePanel() {
		
		super.updatePanel();
		
		resumeRenderer();
		
	}
	
	/**
	 * ensure that 3D animator is running
	 */
	private void resumeRenderer(){
		if (visible){ 
			((App3D)app).getEuclidianView3D().getRenderer().resumeAnimator();
		}

	}

}
