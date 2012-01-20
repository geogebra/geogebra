package geogebra.web.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface CssResource extends ClientBundle {
	
	public static final CssResource INSTANCE =  GWT.create(CssResource.class);
	
	/**
	 * @return the css Resource (stylesheet) used to get the main styles<br/>
	 * more css files can be added as the application grows.
	 */
	@Source("geogebra/resources/css/web.css")
	public CssResource GeoGebraWebCss();
	
	/**
	 * @return imageResource of splashimage
	 */
	@Source("geogebra/resources/images/ggb4-splash-h120.png")
	public ImageResource GeoGebraWebSplash();
	
	/**
	 * @return iamgeRersource of spinner
	 */
	@Source("geogebra/resources/images/spinner.gif")
	public ImageResource GeoGebraWebSpinner();
	

}
