package geogebra.common.kernel.geos;

import geogebra.common.util.CopyPaste;
import geogebra.common.util.StringUtil;

/**
 * Library class for labeling geos; label-related static methods should go there
 */
public class LabelManager {
	

		/**
		 * Checks whether name can be used as label
		 * @param geo geo to be checked
		 * @param nameToCheck potential label
		 * @return true for valid labels
		 */
		public static boolean checkName(GeoElement geo, String nameToCheck) {
			String name = nameToCheck;
			if (name == null) return true;

			if (name.startsWith(CopyPaste.labelPrefix))
				return false;

			name = StringUtil.toLowerCase(name);
			if (geo.isGeoFunction()) {
				if (geo.getKernel().getApplication().
						getParserFunctions().isReserved(name))
						return false;
			}

			return true;
		}
}
