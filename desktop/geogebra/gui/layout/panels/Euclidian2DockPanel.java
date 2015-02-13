package geogebra.gui.layout.panels;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.main.App;
import geogebra.main.AppD;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

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

	// public ImageIcon getIcon() {
	// return app.getImageIcon("document-properties.png");
	// }
	//
	@Override
	protected JComponent loadStyleBar() {
		return (JComponent) app.getEuclidianView2(1).getStyleBar();
	}

	@Override
	protected JComponent loadComponent() {
		return app.getEuclidianView2(this.idx).getJPanel();
	}

	@Override
	public EuclidianView getEuclidianView() {
		return app.getEuclidianView2(this.idx);
	}

	@Override
	public ImageIcon getIcon() {
		return app.getMenuIcon("menu_view_graphics2.png");
	}

}
