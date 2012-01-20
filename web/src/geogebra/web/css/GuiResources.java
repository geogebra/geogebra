package geogebra.web.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface GuiResources extends ClientBundle {
	
	GuiResources INSTANCE = GWT.create(GuiResources.class);
	
	@Source("geogebra/resources/css/web.css")
	CssWeb getCssWeb();
	
	@Source("geogebra/resources/images/ggb4-splash-h120.png")
	ImageResource getGeoGebraWebSplash();
	
	@Source("geogebra/resources/images/spinner.gif")
	ImageResource getGeoGebraWebSpinner();

}
