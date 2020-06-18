package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.ColorProperty;
import org.geogebra.common.properties.impl.AbstractProperty;

/**
 * Color property
 */
public class ElementColorProperty extends AbstractProperty implements ColorProperty {

	private final GeoElement element;
	private GColor[] colors;

	/***/
	public ElementColorProperty(Localization localization, GeoElement element) {
		super(localization, "stylebar.Color");
		this.element = element;
	}

	@Override
	public GColor getColor() {
		return element.getObjectColor();
	}

	@Override
	public void setColor(GColor color) {
		App app = element.getApp();
		EuclidianStyleBarStatic.applyColor(
				color, element.getAlphaValue(), app, app.getSelectionManager().getSelectedGeos());
	}

	@Override
	public GColor[] getColors() {
		if (colors == null) {
			colors = createColorValues();
		}
		return colors;
	}

	@Override
	public boolean isEnabled() {
		return element.isEuclidianVisible();
	}

	private GColor[] createColorValues() {
		GColor[] primColor = GeoGebraColorConstants.getPrimarySwatchColors();
		GColor[] scolors = GeoGebraColorConstants.getMainColorSwatchColors();
		return new GColor[]{primColor[0], primColor[2], primColor[4],
				primColor[8], primColor[10], primColor[12], GColor.BLACK,
				GeoGebraColorConstants.GEOGEBRA_OBJECT_RED,
				GeoGebraColorConstants.GEOGEBRA_OBJECT_ORANGE, scolors[19],
				GeoGebraColorConstants.GEOGEBRA_OBJECT_GREEN, scolors[43],
				GeoGebraColorConstants.GEOGEBRA_OBJECT_BLUE,
				GeoGebraColorConstants.GEOGEBRA_OBJECT_PURPLE, scolors[0],
				scolors[8], scolors[16], scolors[32], scolors[40], scolors[48],
				scolors[56], scolors[1], scolors[9], scolors[17], scolors[24],
				scolors[41], scolors[49], scolors[57], scolors[3], scolors[11],
				primColor[5], scolors[33], primColor[11], scolors[51],
				scolors[59], scolors[4], scolors[12], scolors[20], scolors[36],
				scolors[44], scolors[52], scolors[60], scolors[6], scolors[14],
				scolors[22], scolors[38], scolors[46], scolors[54], scolors[62],
				scolors[7], scolors[15], scolors[23], scolors[39], scolors[47],
				scolors[55], scolors[63]};
	}
}
