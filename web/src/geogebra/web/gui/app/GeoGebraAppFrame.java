/**
 * 
 */
package geogebra.web.gui.app;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author gabor
 * 
 * Creates the App base structure.
 *
 */
public class GeoGebraAppFrame extends Composite {

	private static GeoGebraAppFrameUiBinder uiBinder = GWT
	        .create(GeoGebraAppFrameUiBinder.class);

	interface GeoGebraAppFrameUiBinder extends
	        UiBinder<Widget, GeoGebraAppFrame> {
	}
	
	public GeoGebraAppFrame() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
