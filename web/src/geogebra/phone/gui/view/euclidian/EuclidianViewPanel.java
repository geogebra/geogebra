package geogebra.phone.gui.view.euclidian;

import geogebra.html5.awt.GGraphics2DW;
import geogebra.html5.euclidian.EuclidianViewW;
import geogebra.html5.main.AppW;
import geogebra.phone.gui.view.AbstractViewPanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.user.client.Window;

/**
 * 
 */
public class EuclidianViewPanel extends AbstractViewPanel {

	private EuclidianViewW euclidianView;

	public EuclidianViewPanel(AppW app) {
		super(app);
		euclidianView = app.getEuclidianView1();

		euclidianView.g2p = new GGraphics2DW(Canvas.createIfSupported());
		euclidianView.g2p.setView(euclidianView);

		// TODO replace with actual height (of the headerpanel)
		euclidianView.setCoordinateSpaceSize(Window.getClientWidth(),
				Window.getClientHeight() - 43);

		euclidianView.updateFonts();
		euclidianView.attachView();
		euclidianView.doRepaint();

		add(this.euclidianView.getCanvas());

		euclidianView.getEuclidianController().calculateEnvironment();

		addDomHandler(this.euclidianView.getEuclidianController(),
				MouseDownEvent.getType());
		addDomHandler(this.euclidianView.getEuclidianController(),
				MouseUpEvent.getType());
		addDomHandler(this.euclidianView.getEuclidianController(),
				MouseMoveEvent.getType());
		addDomHandler(euclidianView.getEuclidianController(),
				MouseWheelEvent.getType());

		addDomHandler(this.euclidianView.getEuclidianController(),
				TouchStartEvent.getType());
		addDomHandler(this.euclidianView.getEuclidianController(),
				TouchEndEvent.getType());
		addDomHandler(this.euclidianView.getEuclidianController(),
				TouchMoveEvent.getType());
	}

	@Override
	protected String getViewPanelStyleName() {
		return "euclidianViewPanel";
	}

	@Override
	public void onResize() {
		super.onResize();
	}
}
