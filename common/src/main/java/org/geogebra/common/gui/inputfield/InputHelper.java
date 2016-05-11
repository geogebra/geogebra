package org.geogebra.common.gui.inputfield;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.Korean;

public class InputHelper {
	public static boolean needsAutocomplete(StringBuilder curWord,
			Kernel kernel) {
		if ("ko".equals(kernel.getLocalization().getLanguage())) {
			if (Korean.flattenKorean(curWord.toString()).length() < 2) {
				return false;
			}
		} else if (curWord.length() < 3) {
			return false;
		}
		return kernel.lookupLabel(curWord.toString()) == null;
	}

	public static void centerText(GeoElement[] geos,
			EuclidianViewInterfaceCommon ev) {
		// create texts in the middle of the visible view
		// we must check that size of geos is not 0 (ZoomIn, ZoomOut, ...)
		if (geos != null && geos.length > 0 && geos[0] != null
				&& geos[0].isGeoText()) {
			GeoText text = (GeoText) geos[0];
			if (!text.isTextCommand() && text.getStartPoint() == null) {

				Construction cons = text.getConstruction();

				boolean oldSuppressLabelsStatus = cons.isSuppressLabelsActive();
				cons.setSuppressLabelCreation(true);
				GeoPoint p = new GeoPoint(text.getConstruction(), null,
						(ev.getXmin() + ev.getXmax()) / 2,
						(ev.getYmin() + ev.getYmax()) / 2, 1.0);
				cons.setSuppressLabelCreation(oldSuppressLabelsStatus);

				try {
					text.setStartPoint(p);
					text.update();
				} catch (CircularDefinitionException e1) {
					e1.printStackTrace();
				}
			}
		}

	}
}
