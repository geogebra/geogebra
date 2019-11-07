package org.geogebra.web.full.gui.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources for the properties view.
 */
public interface PropertiesResources extends ClientBundle {

	/** Singleton instance */
	PropertiesResources INSTANCE = GWT.create(PropertiesResources.class);

	@Source("org/geogebra/web/full/gui/images/ruling/colored.png")
	ImageResource coloredRuling();

	@Source("org/geogebra/web/full/gui/images/ruling/elementary12.png")
	ImageResource elementary12Ruling();

	@Source("org/geogebra/web/full/gui/images/ruling/elementary34.png")
	ImageResource elementary34Ruling();

	@Source("org/geogebra/web/full/gui/images/ruling/house.png")
	ImageResource houseRuling();

	@Source("org/geogebra/web/full/gui/images/ruling/music.png")
	ImageResource musicRuling();

	@Source("org/geogebra/web/full/gui/images/ruling/squared1.png")
	ImageResource squared1Ruling();

	@Source("org/geogebra/web/full/gui/images/ruling/squared5.png")
	ImageResource squared5Ruling();

	@Source("org/geogebra/web/full/gui/images/ruling/lined.png")
	ImageResource linedRuling();
}
