package org.geogebra.web.phone.gui.view.euclidian;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.phone.PhoneLookAndFeel;
import org.geogebra.web.phone.gui.view.AbstractViewPanel;

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

	/**
	 * @param app
	 *            {@link AppW}
	 * @param euclidianView
	 *            {@link EuclidianViewW}
	 */
	public EuclidianViewPanel(AppW app, EuclidianViewW euclidianView) {
		super(app);
		this.euclidianView = euclidianView;

		euclidianView.g2p = new GGraphics2DW(Canvas.createIfSupported());
		if (app.has(Feature.RETINA)) {
			euclidianView.g2p.devicePixelRatio = Browser
					.getPixelRatio();
		} else {
			App.debug("Retina not enabled");
		}
		euclidianView.g2p.setView(euclidianView);

		// TODO replace with actual height (of the headerpanel)
		euclidianView.setCoordinateSpaceSize(Window.getClientWidth(),
		                Window.getClientHeight()
		                        - PhoneLookAndFeel.PHONE_HEADER_HEIGHT);

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
		// TODO replace with actual height (of the headerpanel)
		if (this.euclidianView != null) {
			euclidianView.setCoordinateSpaceSize(Window.getClientWidth(),
			        Window.getClientHeight()
			                - PhoneLookAndFeel.PHONE_HEADER_HEIGHT);

			euclidianView.updateFonts();
			euclidianView.attachView();
			euclidianView.doRepaint();
		}

	}
}
