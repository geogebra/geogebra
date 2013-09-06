package geogebra.touch.gui.euclidian;

import geogebra.touch.TouchEntryPoint;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.ResizeListener;
import geogebra.touch.gui.TabletGUI;

import java.util.Iterator;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Extends from {@link LayoutPanel}. Holds the instances of the canvas and the
 * euclidianView.
 */
public class EuclidianViewPanel extends AbsolutePanel implements ResizeListener {
	private EuclidianViewT euclidianView;
	private TabletGUI gui;

	public EuclidianViewPanel(TabletGUI tabletGUI) {
		this.gui = tabletGUI;
	}

	/**
	 * Creates the {@link EuclidianViewT euclidianView} and initializes the
	 * canvas on it.
	 * 
	 * @param ec
	 *            MobileEuclidianController
	 * @param widget
	 * @param width
	 * @param height
	 */
	public void initEuclidianView(TouchController ec, Widget widget, int width,
			int height) {
		this.gui.addResizeListener(this);
		this.euclidianView = new EuclidianViewT(this, ec, widget, width, height);
		this.add(this.euclidianView.getCanvas());
	}

	/**
	 * @return euclidianView
	 */
	public EuclidianViewT getEuclidianView() {
		return this.euclidianView;
	}

	@Override
	public void setPixelSize(int width, int height) {
		super.setPixelSize(width, height);
		if (this.euclidianView != null) {
			this.euclidianView.setPixelSize(width, height);
		}
	}

	@Override
	public void setSize(String width, String height) {
		super.setSize(width, height);
		this.euclidianView.setPixelSize(Integer.valueOf(width).intValue(),
				Integer.valueOf(height).intValue());
	}

	@Override
	public void onResize() {
		if (!this.gui.isAlgebraShowing()) {
			this.setPixelSize(Window.getClientWidth(),
					TouchEntryPoint.getLookAndFeel().getContentWidgetHeight());
			this.setWidgetPosition(
					this.gui.getAlgebraViewButtonPanel(),
					Window.getClientWidth() - TabletGUI.ALGEBRA_BUTTON_WIDTH, 0);
		} else {
			this.setPixelSize(Window.getClientWidth()
					- TabletGUI.computeAlgebraWidth(), TouchEntryPoint
					.getLookAndFeel().getContentWidgetHeight());
			this.setWidgetPosition(
					this.gui.getAlgebraViewButtonPanel(),
					Window.getClientWidth() - TabletGUI.computeAlgebraWidth()
							- TabletGUI.ALGEBRA_BUTTON_WIDTH, 0);
		}
	}

	public void removeGBoxes() {
		Iterator<Widget> it = this.iterator();
	    while (it.hasNext()) {
	    	Widget nextItem = it.next();
	    	if (!(nextItem instanceof Canvas)) it.remove();
	    }
		
	}
}
