package org.geogebra.web.full.gui.images;

import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.resources.client.Resource;

/**
 * Resources for the properties view.
 */
@Resource
public interface PropertiesResources extends ClientBundle {

	/** Singleton instance */
	PropertiesResources INSTANCE = new PropertiesResourcesImpl();

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
