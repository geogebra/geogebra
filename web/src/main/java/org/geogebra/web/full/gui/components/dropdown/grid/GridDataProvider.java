package org.geogebra.web.full.gui.components.dropdown.grid;

import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.images.PropertiesResources;
import org.gwtproject.resources.client.ImageResource;

public class GridDataProvider {
	/**
	 * @param rulingType - grid type
	 * @return name on type
	 */
	public static String getTransKeyForRulingType(BackgroundType rulingType) {
		switch (rulingType) {
		case RULER:
			return "Ruled";
		case SQUARE_SMALL:
			return "Squared5";
		case SQUARE_BIG:
			return "Squared1";
		case ELEMENTARY12:
			return "Elementary12";
		case ELEMENTARY12_COLORED:
			return "Elementary12Colored";
		case ELEMENTARY12_HOUSE:
			return "Elementary12WithHouse";
		case ELEMENTARY34:
			return "Elementary34";
		case MUSIC:
			return "Music";
		case ISOMETRIC:
			return "Isometric";
		case POLAR:
			return "Polar";
		default:
			return "NoRuling";
		}
	}

	/**
	 * @param rulingType - grid type
	 * @return background image for the type
	 */
	public static ImageResource getResourceForBackgroundType(BackgroundType rulingType) {
		switch (rulingType) {
		case RULER:
			return PropertiesResources.INSTANCE.linedRuling();
		case SQUARE_SMALL:
			return PropertiesResources.INSTANCE.squared5Ruling();
		case SQUARE_BIG:
			return PropertiesResources.INSTANCE.squared1Ruling();
		case ELEMENTARY12_COLORED:
			return PropertiesResources.INSTANCE.coloredRuling();
		case ELEMENTARY12:
			return PropertiesResources.INSTANCE.elementary12Ruling();
		case ELEMENTARY12_HOUSE:
			return PropertiesResources.INSTANCE.houseRuling();
		case ELEMENTARY34:
			return PropertiesResources.INSTANCE.elementary34Ruling();
		case MUSIC:
			return PropertiesResources.INSTANCE.musicRuling();
		case ISOMETRIC:
			return PropertiesResources.INSTANCE.isometricRuling();
		case POLAR:
			return PropertiesResources.INSTANCE.polarRuling();
		default:
			return AppResources.INSTANCE.empty();
		}
	}
}
