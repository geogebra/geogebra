package geogebra3D.gui.layout.panels;

import geogebra.common.gui.toolbar.ToolBar;
import geogebra.common.io.layout.DockPanelData;
import geogebra.common.main.App;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.gui.layout.panels.EuclidianDockPanelAbstract;
import geogebra.main.AppD;
import geogebra3D.euclidianForPlane.EuclidianViewForPlane;
import geogebra3D.io.layout.DockPanelDataForPlane;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Dock panel for the primary euclidian view.
 */
public class EuclidianDockPanelForPlane extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;
	private EuclidianViewForPlane view;
	
	//id of the first view
	private static int viewId = App.VIEW_EUCLIDIAN_FOR_PLANE_START;
	
	
	public EuclidianDockPanelForPlane(AppD app, EuclidianViewForPlane view) {
		this(app, view, viewId);

	}
	
	/**
	 * @param app application
	 * @param view view for plane
	 */
	public EuclidianDockPanelForPlane(AppD app, EuclidianViewForPlane view, int id) {
		super(
			id,	// view id 
			"GraphicsViewForPlaneA", 				// view title
			ToolBar.getAllToolsNoMacrosForPlane(),// toolbar string
			true,						// style bar?
			-1,							// menu order
			'P'
		);
		
		
		
		this.app = app;
		this.view = view;
		view.setDockPanel(this);
		
		setEmbeddedSize(300);
		
		if (viewId<id)
			viewId=id;
		viewId++; //id of next view
	}
	
	
	
	
	/**
	 * 
	 * @return view
	 */
	public EuclidianViewForPlane getView(){
		return view;
	}
	
	@Override
	protected String getPlainTitle(){
		return app.getPlain(getViewTitle(),view.getTranslatedFromPlaneString());
	}

	@Override
	protected JComponent loadComponent() {
		return view.getJPanel();
	}
	
	@Override
	protected JComponent loadStyleBar() {
		return (JComponent) view.getStyleBar();
	}
	
	@Override
	public EuclidianViewND getEuclidianView() {
		return view;
	}
	
	
	@Override
	public boolean updateResizeWeight(){
		return true;
	}
	
	@Override
	public DockPanelData createInfo() {
		return new DockPanelDataForPlane(id, toolbarString, visible, openInFrame,
				showStyleBar, new geogebra.awt.GRectangleD(frameBounds),
				embeddedDef, embeddedSize,view.getFromPlaneString());
	}
	
	public static void resetIds(){
		viewId = App.VIEW_EUCLIDIAN_FOR_PLANE_START;
	}
	
}
