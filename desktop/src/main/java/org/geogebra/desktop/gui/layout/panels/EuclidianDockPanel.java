package org.geogebra.desktop.gui.layout.panels;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Dock panel for the primary euclidian view.
 */
public class EuclidianDockPanel extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;
	private AppD app;

	/**
	 * Panel to hold euclidian view and navigation bar if necessary.
	 */

	/**
	 * @param app
	 */
	public EuclidianDockPanel(AppD app, String toolbar) {
		super(App.VIEW_EUCLIDIAN, // view id
				"DrawingPad", // view title
				toolbar, // toolbar string
				true, // style bar?
				4, // menu order
				'1' // ctrl-shift-1
		);

		this.app = app;
	}

	@Override
	protected JComponent loadStyleBar() {
		return (JComponent) app.getEuclidianView1().getStyleBar();
	}

	/**
	 * As the component of this panel is not just the euclidian view as asserted
	 * in EuclidianDockPanelAbstract we have to override this method to provide
	 * the correct euclidian view.
	 */
	@Override
	public EuclidianView getEuclidianView() {
		return app.getEuclidianView1();
	}

	@Override
	public ImageIcon getIcon() {
		return app.getMenuIcon(GuiResourcesD.MENU_VIEW_GRAPHICS);
	}
}
