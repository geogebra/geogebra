package geogebra.common.kernel.geos;

import geogebra.common.kernel.parser.cashandlers.ParserFunctions;
import geogebra.common.util.CopyPaste;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Library class for labeling geos; label-related static methods should go there
 */
public class LabelManager {
	private static Set<String> invalidFunctionNames = new HashSet<String>(ParserFunctions.RESERVED_FUNCTION_NAMES);
	static
	{
		invalidFunctionNames.addAll(Arrays.asList("x", "y", Unicode.IMAGINARY));
	}

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
				if (invalidFunctionNames.contains(name))
						return false;
			}

			return true;
		}
}
