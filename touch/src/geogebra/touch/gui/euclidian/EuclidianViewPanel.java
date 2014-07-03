package geogebra.touch.gui.euclidian;

import geogebra.html5.gui.ResizeListener;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.controller.TouchController;
import geogebra.touch.gui.TabletGUI;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Extends from {@link AbsolutPanel}. Holds the instances of the canvas and the
 * euclidianView.
 */
public class EuclidianViewPanel extends AbsolutePanel implements ResizeListener {
	private EuclidianViewT euclidianView;

	public EuclidianViewPanel() {
	
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
	public void initEuclidianView(TouchController ec) {
		
		this.euclidianView = new EuclidianViewT(this, ec);
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
		if (!TouchEntryPoint.getLookAndFeel().getGUI().isAlgebraShowing()) {
			this.setPixelSize(Window.getClientWidth(),
						TouchEntryPoint.getLookAndFeel().getCanvasHeight());
		} else {
			this.setPixelSize(Window.getClientWidth()
						- TabletGUI.computeAlgebraWidth(), TouchEntryPoint.getLookAndFeel().getCanvasHeight());
		}
		if (TouchEntryPoint.isTablet()) {
			adjustRightWidget();
		}
		
	}
	
	public void adjustRightWidget(){
		Widget rightWidget = (Widget) (!TouchEntryPoint.getLookAndFeel().getGUI().isRTL()? TouchEntryPoint.getLookAndFeel().getGUI().getAlgebraViewButtonPanel() : TouchEntryPoint.getLookAndFeel().getGUI().getStylebar());
		int widgetWidth =  rightWidget.getOffsetWidth()-6;
		if(widgetWidth <= 0){
			widgetWidth = !TouchEntryPoint.getLookAndFeel().getGUI().isRTL()? TabletGUI.ALGEBRA_BUTTON_WIDTH : TabletGUI.STYLEBAR_WIDTH;
		}
		//TODO use new LAF
//		if (!TouchEntryPoint.getLookAndFeel().getGUI().isAlgebraShowing()) {
//			this.setPixelSize(Window.getClientWidth(),
//					TouchEntryPoint.getLookAndFeel().getContentWidgetHeight());
//			this.setWidgetPosition(
//					rightWidget,
//					Window.getClientWidth() - widgetWidth, 0);
//		} else {
//			this.setPixelSize(Window.getClientWidth()
//					- TabletGUI.computeAlgebraWidth(), TouchEntryPoint
//					.getLookAndFeel().getContentWidgetHeight());
//			this.setWidgetPosition(
//					rightWidget,
//					Window.getClientWidth() - TabletGUI.computeAlgebraWidth()
//							- widgetWidth, 0);
//		}
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
