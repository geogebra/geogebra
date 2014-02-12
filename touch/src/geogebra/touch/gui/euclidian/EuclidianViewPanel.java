package geogebra.touch.gui.euclidian;

import geogebra.html5.gui.ResizeListener;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.TabletGUI;

import java.util.ArrayList;

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
		} else {
			this.setPixelSize(Window.getClientWidth()
					- TabletGUI.computeAlgebraWidth(), TouchEntryPoint
					.getLookAndFeel().getContentWidgetHeight());
		}
		adjustRightWidget();
	}
	
	public void adjustRightWidget(){
		Widget rightWidget = !this.gui.isRTL()? this.gui.getAlgebraViewButtonPanel() : this.gui.getStylebar();
		int widgetWidth =  rightWidget.getOffsetWidth()-6;
		if(widgetWidth <= 0){
			widgetWidth = !this.gui.isRTL()? TabletGUI.ALGEBRA_BUTTON_WIDTH : TabletGUI.STYLEBAR_WIDTH;
		}
		if (!this.gui.isAlgebraShowing()) {
			this.setPixelSize(Window.getClientWidth(),
					TouchEntryPoint.getLookAndFeel().getContentWidgetHeight());
			this.setWidgetPosition(
					rightWidget,
					Window.getClientWidth() - widgetWidth, 0);
		} else {
			this.setPixelSize(Window.getClientWidth()
					- TabletGUI.computeAlgebraWidth(), TouchEntryPoint
					.getLookAndFeel().getContentWidgetHeight());
			this.setWidgetPosition(
					rightWidget,
					Window.getClientWidth() - TabletGUI.computeAlgebraWidth()
							- widgetWidth, 0);
		}
	}

	public void removeGBoxes() {
	    for (Widget w: this.boxes) {
	    	remove(w);
	    }
		this.boxes.clear();
	}
	private ArrayList<Widget> boxes = new ArrayList<Widget>();
	public void removeBox(Widget impl) {
		remove(impl);
		this.boxes.remove(impl);
		
	}
	
	public void addBox(Widget impl, int x, int y) {
		add(impl, x, y);
		this.boxes.add(impl);
		
	}
}
