package org.geogebra.desktop.geogebra3D.gui.layout.panels;

import javax.swing.JComponent;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.io.layout.DockPanelDataForPlane;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.main.App;
import org.geogebra.desktop.geogebra3D.euclidianForPlane.EuclidianViewForPlaneD;
import org.geogebra.desktop.gui.layout.panels.EuclidianDockPanelAbstract;
import org.geogebra.desktop.main.AppD;

/**
 * Dock panel for the primary euclidian view.
 */
public class EuclidianDockPanelForPlaneD extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;
	private EuclidianViewForPlaneD view;

	// id of the first view
	private static int viewId = App.VIEW_EUCLIDIAN_FOR_PLANE_START;

	/**
	 * @param app
	 *            application
	 * @param view
	 *            view for plane
	 */
	public EuclidianDockPanelForPlaneD(AppD app, EuclidianViewForPlaneD view) {
		super(viewId, // view id
				"GraphicsViewForPlaneA", // view title
				ToolBar.getAllToolsNoMacrosForPlane(),// toolbar string
				true, // style bar?
				-1, // menu order
				'P');

		setApp(app);
		this.view = view;
		view.getCompanion().setDockPanel(this);

		setEmbeddedSize(300);

		viewId++; // id of next view
	}

	@Override
	public boolean canCustomizeToolbar() {
		return false;
	}

	/**
	 * 
	 * @return view
	 */
	public EuclidianViewForPlaneD getView() {
		return view;
	}

	@Override
	protected String getPlainTitle() {
		return app.getLocalization().getPlain(getViewTitle(),
				view.getTranslatedFromPlaneString());
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
	public EuclidianView getEuclidianView() {
		return view;
	}

	@Override
	public boolean updateResizeWeight() {
		return true;
	}

	@Override
	public DockPanelData createInfo() {
		return new DockPanelDataForPlane(id, toolbarString, visible,
				openInFrame, showStyleBar, new org.geogebra.desktop.awt.GRectangleD(
						frameBounds), embeddedDef, embeddedSize,
				view.getFromPlaneString());
	}

	public static void resetIds() {
		viewId = App.VIEW_EUCLIDIAN_FOR_PLANE_START;
	}

}
