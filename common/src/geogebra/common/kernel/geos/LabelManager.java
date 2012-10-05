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

		public static boolean validVar(String var) {
			// check for invalid assignment variables like $, $$, $1, $2, ...,
			// $1$, $2$, ... which are dynamic references
				if (var.charAt(0) == GeoCasCell.ROW_REFERENCE_DYNAMIC) {
					boolean validVar = false;
					// if var.length() == 1 we have "$" and the for-loop won't be
					// entered
					for (int i = 1; i < var.length(); i++) {
						if (!Character.isDigit(var.charAt(i))) {
							if (i == 1 && var.charAt(1) == GeoCasCell.ROW_REFERENCE_DYNAMIC) {
								// "$$" so far, so it can be valid (if var.length >
								// 2) or invalid if "$$" is the whole var
							} else if (i == var.length() - 1
									&& var.charAt(var.length() - 1) == GeoCasCell.ROW_REFERENCE_DYNAMIC) {
								// "$dd...dd$" where all d are digits -> invalid
							} else {
								// "$xx..xx" where not all x are numbers and the
								// first x is not a '$' (there can only be one x)
								validVar = true;
								break;
							}
						}
					}
					return validVar;
			}
			return true;
		}
}
