package geogebra.common.euclidian;

import geogebra.common.awt.Font;
import geogebra.common.awt.FontRenderContext;


/**
 * @author gabor@gegeobra.org
 *
 *
 *Abstract class for EuclidianStatic
 */
public abstract class EuclidianStatic {

	public abstract float textWidth(String str, Font font, FontRenderContext frc);
}
