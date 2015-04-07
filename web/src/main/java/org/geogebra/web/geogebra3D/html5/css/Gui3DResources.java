package org.geogebra.web.geogebra3D.html5.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Gui3DResources extends ClientBundle {

	Gui3DResources INSTANCE = GWT.create(Gui3DResources.class);

	@Source("stylingbar_properties_graphics_view3d.png")
	ImageResource properties_graphics3d();
}
