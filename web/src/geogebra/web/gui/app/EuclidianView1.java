package geogebra.web.gui.app;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
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

	private static EuclidianView1UiBinder uiBinder = GWT
	        .create(EuclidianView1UiBinder.class);

	/**
	 * @author gabor
	 * 
	 * Binds EuclidianView as UIBinder
	 *
	 */
	interface EuclidianView1UiBinder extends UiBinder<EuclidianPanel, EuclidianView1> {
	}
	
	/**
	 * EuclidianPanel for textfields
	 */
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
	    GWT.log("Resized");
    }
	
	public void onLoad() {
		//getEuclidianPanel().getElement().getStyle().setPosition(Position.RELATIVE);
	}
	
	

}
