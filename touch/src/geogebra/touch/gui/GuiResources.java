package geogebra.touch.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface GuiResources extends ClientBundle {
	GuiResources INSTANCE = GWT.create(GuiResources.class);

	@Source("geogebra/resources/css/mathquillggb.css")
	TextResource mathquillCss();
}