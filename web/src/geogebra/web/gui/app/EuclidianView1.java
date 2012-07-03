package geogebra.web.gui.app;

import geogebra.common.main.App;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author gabor
 * 
 * The markup for EuclidianView in UIBinder
 *
 */
public class EuclidianView1 extends ResizeComposite {

	App application = null;

	private static EuclidianView1UiBinder uiBinder = GWT
	        .create(EuclidianView1UiBinder.class);

	/**
	 * @author gabor
	 * 
	 * Binds EuclidianView as UIBinder
	 *
	 */
	interface EuclidianView1UiBinder extends UiBinder<VerticalPanel, EuclidianView1> {
	}

	/**
	 * EuclidianPanel for textfields
	 */
	@UiField VerticalPanelSmart ancestor;
	@UiField EuclidianStyleBarPanel espanel;
	@UiField EuclidianPanel euclidianpanel;

	public EuclidianView1() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	/**
	 * @return Canvas corresponding eview1
	 */
	public Canvas getCanvas() {
		return euclidianpanel.getCanvas();
	}
	
	/**
	 * @return euclidianPanel (needed for wrap for textfields)
	 */
	public AbsolutePanel getEuclidianPanel() {
		return euclidianpanel.getAbsolutePanel();
	}

	public void onResize() {
		App.debug("Resized");
    }
	
	public void onLoad() {
		//getEuclidianPanel().getElement().getStyle().setPosition(Position.RELATIVE);
	}

	public void attachApp(App app) {
	   this.application = app;
	   espanel.attachApp(app);
	}

}
