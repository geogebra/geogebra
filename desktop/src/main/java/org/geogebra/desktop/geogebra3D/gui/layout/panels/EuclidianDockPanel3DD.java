package org.geogebra.desktop.geogebra3D.gui.layout.panels;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.main.App;
import org.geogebra.desktop.geogebra3D.App3D;
import org.geogebra.desktop.geogebra3D.euclidian3D.EuclidianView3DD;
import org.geogebra.desktop.gui.layout.DockManagerD;
import org.geogebra.desktop.gui.layout.DockPanel;
import org.geogebra.desktop.gui.layout.panels.EuclidianDockPanelAbstract;
import org.geogebra.desktop.main.AppD;

/**
 * Dock panel for the primary euclidian view.
 */
public class EuclidianDockPanel3DD extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;

	/**
	 * @param app
	 *            application
	 */
	public EuclidianDockPanel3DD(AppD app) {
		super(App.VIEW_EUCLIDIAN3D, // view id
				"GraphicsView3D", // view title
				ToolBar.getAllToolsNoMacros3D(),// toolbar string
				true, // style bar?
				6, // menu order
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
		return (JComponent) ((App3D) app).getEuclidianView3D().getStyleBar();
	}

	@Override
	public EuclidianView getEuclidianView() {
		return ((App3D) app).getEuclidianView3D();
	}

	@Override
	public boolean isEuclidianDockPanel3D() {
		return true;
	}

	@Override
	public ImageIcon getIcon() {
		return app.getMenuIcon("menu_view_graphics3D.png");
	}

	/**
	 * force openGL to refresh
	 * 
	 * @param manager
	 *            dock manager
	 */
	public void refresh(DockManagerD manager) {

		if (!isVisible()) {
			return;
		}

		if (isOpenInFrame()) {
			// just put the panel in a main window and put it again in a frame
			// to force openGL to restart
			unwindowPanel();
			windowPanel();
		} else {
			// if 3D view is alone, we put algebra view in main window to avoid
			// gray panel
			boolean isAlone = manager.containsLessThanTwoPanels();
			DockPanel algebraPanel = null;
			if (isAlone) {
				algebraPanel = manager.getPanel(App.VIEW_ALGEBRA);
				if (algebraPanel.isVisible()) {
					// algebra view is visible and in a frame, so we put it in
					// the main window
					algebraPanel.unwindowPanel();
				} else {
					// makes algebraPanel = null as a flag
					algebraPanel = null;
					manager.show(App.VIEW_ALGEBRA);
				}
			}
			// just put the panel in a frame and put it again in main window to
			// force openGL to restart
			windowPanel();
			unwindowPanel();

			// we put algebra view in old state again
			if (isAlone) {
				if (algebraPanel == null) {
					// algebra panel was not visible before
					manager.hide(App.VIEW_ALGEBRA, false);
				} else {
					// algebra panel was visible in a frame before
					algebraPanel.windowPanel();
				}
			}
		}
	}

	@Override
	public boolean updateResizeWeight() {

		resumeRenderer();

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
	public void resumeRenderer() {

		if (visible) {
			((App3D) app).getEuclidianView3D().getRenderer().resumeAnimator();
		}

	}

}
