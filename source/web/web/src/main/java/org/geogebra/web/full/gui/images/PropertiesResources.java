/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	@Source("org/geogebra/web/full/gui/images/ruling/polar.png")
	ImageResource polarRuling();

	@Source("org/geogebra/web/full/gui/images/ruling/isometric.png")
	ImageResource isometricRuling();

	@Source("org/geogebra/web/full/gui/images/ruling/dots.png")
	ImageResource dotsRuling();
}
