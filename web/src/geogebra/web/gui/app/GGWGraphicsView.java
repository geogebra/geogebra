package geogebra.web.gui.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author gabor
 * 
 * Wrapper for the Two EuclidianView with UIBinder
 *
 */
public class GGWGraphicsView extends Composite {

	private static GGWGraphicsViewUiBinder uiBinder = GWT
	        .create(GGWGraphicsViewUiBinder.class);

	interface GGWGraphicsViewUiBinder extends UiBinder<Widget, GGWGraphicsView> {
	}
	
	/**
	 * 
	 */
	@UiField EuclidianView1 eview1;

	/**
	 * 	Wrapper for the two EuclidianView (one is active only)
	 */
	public GGWGraphicsView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	/**
	 * @return Euclidianview1,
	 * 
	 * The wrapper of EuclidianView in UIBinder
	 */
	public EuclidianView1 getEuclidianView1Wrapper() {
		return eview1;
	}

}
