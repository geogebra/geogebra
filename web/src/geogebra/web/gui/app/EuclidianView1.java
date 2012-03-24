package geogebra.web.gui.app;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author gabor
 * 
 * The markup for EuclidianView in UIBinder
 *
 */
public class EuclidianView1 extends Composite {

	private static EuclidianView1UiBinder uiBinder = GWT
	        .create(EuclidianView1UiBinder.class);

	/**
	 * @author gabor
	 * 
	 * Binds EuclidianView as UIBinder
	 *
	 */
	interface EuclidianView1UiBinder extends UiBinder<Widget, EuclidianView1> {
	}
	
	/**
	 * EuclidianPanel for textfields
	 */
	@UiField AbsolutePanel euclidianpanel;
	Canvas eview1 = null;

	public EuclidianView1() {
		initWidget(uiBinder.createAndBindUi(this));
		eview1 = Canvas.createIfSupported();
		euclidianpanel.add(eview1);
	}
	
	/**
	 * @return Canvas corresponding eview1
	 */
	public Canvas getCanvas() {
		return eview1;
	}
	
	/**
	 * @return euclidianPanel (needed for wrap for textfields)
	 */
	public AbsolutePanel getEuclidianPanel() {
		return euclidianpanel;
	}
	
	

}
