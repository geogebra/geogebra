package org.geogebra.desktop.gui.layout.panels;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Dock panel for the secondary euclidian view.
 */
public class Euclidian2DockPanel extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;
	private AppD app;
	private int idx;

	/**
	 * @param app
	 */
	public Euclidian2DockPanel(AppD app, String toolbar, int idx) {
		super(App.VIEW_EUCLIDIAN2, // view id
				"DrawingPad2", // view title phrase
				toolbar, // toolbar string
				true, // style bar?
				5, // menu order
				'2');
		this.idx = idx;
		this.app = app;
	}

	@Override
	protected JComponent loadStyleBar() {
		return (JComponent) app.getEuclidianView2(1).getStyleBar();
	}

	@Override
	public EuclidianView getEuclidianView() {
		return app.getEuclidianView2(this.idx);
	}

	@Override
	public ImageIcon getIcon() {
		return app.getMenuIcon(GuiResourcesD.MENU_VIEW_GRAPHICS2);
	}

}
