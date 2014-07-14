package geogebra.phone.gui.views;

import geogebra.html5.awt.GGraphics2DW;
import geogebra.html5.gui.ResizeListener;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * 
 */
public class EuclidianViewPanel extends FlowPanel implements ResizeListener {
	private EuclidianViewW euclidianView;

	public EuclidianViewPanel(AppW app) {
		this.euclidianView = app.createEuclidianView();

		this.euclidianView.g2p = new GGraphics2DW(Canvas.createIfSupported());

		// TODO replace with actual height (of the headerpanel)
		this.euclidianView.setCoordinateSpaceSize(Window.getClientWidth(),
		        Window.getClientHeight() - 43);

		this.euclidianView.updateFonts();
		this.euclidianView.attachView();
		this.euclidianView.doRepaint();

		this.add(this.euclidianView.getCanvas());

		this.euclidianView.getEuclidianController().calculateEnvironment();

		this.addDomHandler(this.euclidianView.getEuclidianController(),
		        MouseDownEvent.getType());
		this.addDomHandler(this.euclidianView.getEuclidianController(),
		        MouseUpEvent.getType());
		this.addDomHandler(this.euclidianView.getEuclidianController(),
		        MouseMoveEvent.getType());

		this.addDomHandler(this.euclidianView.getEuclidianController(),
		        TouchStartEvent.getType());
		this.addDomHandler(this.euclidianView.getEuclidianController(),
		        TouchEndEvent.getType());
		this.addDomHandler(this.euclidianView.getEuclidianController(),
		        TouchMoveEvent.getType());
	}

	@Override
	public void onResize() {
		this.setPixelSize(Window.getClientWidth(),
		        Window.getClientHeight() - 43);
	}

	// public void removeGBoxes() {
	// for (Widget w: this.boxes) {
	// remove(w);
	// }
	// this.boxes.clear();
	// }
	// private ArrayList<Widget> boxes = new ArrayList<Widget>();
	// public void removeBox(Widget impl) {
	// remove(impl);
	// this.boxes.remove(impl);
	//
	// }
	//
	// public void addBox(Widget impl, int x, int y) {
	// add(impl, x, y);
	// this.boxes.add(impl);
	//
	// }
}
