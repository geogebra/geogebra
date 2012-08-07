package geogebra.web.main;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface PropertiesResource extends ClientBundle {

	PropertiesResource INSTANCE = GWT.create(PropertiesResource.class);
	
	@Source("geogebra/resources/properties/command.properties")
	TextResource commandProperties();
}
