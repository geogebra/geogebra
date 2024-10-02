package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.util.CopyPaste;
import org.geogebra.common.util.StringUtil;

/**
 * Library class for labeling geos; label-related static methods should go there
 */
public class LabelManager {

	/** Prefix for labels that are not shown in AV */
	public static final String HIDDEN_PREFIX = "\u00A5\u00A6\u00A7\u00A8\u00A9\u00AA";
	
	private char[] angleLabels;
	private final Construction cons;
	private String multiuserSuffix = "";

	/**
	 * @param construction
	 *            construction
	 */
	public LabelManager(Construction construction) {
		this.cons = construction;
	}

	/**
	 * Checks whether name can be used as label Parser.parseLabel takes care of
	 * checking unicode ranges and indices; this only checks for reserved names
	 * and CAS labels
	 * 
	 * @param geo
	 *            geo to be checked
	 * @param nameToCheck
	 *            potential label
	 * @return true for valid labels
	 */
	public static boolean checkName(GeoElementND geo, String nameToCheck) {
		String name = nameToCheck;
		if (name == null) {
			return true;
		}

		if (name.isEmpty() || name.startsWith(CopyPaste.labelPrefix)) {
			return false;
		}

		name = StringUtil.toLowerCaseUS(name);
		if (geo != null && geo.isGeoFunction()) {
			if (geo.getKernel().getApplication().getParserFunctions()
					.isReserved(name)) {
				return false;
			}
		}

		// $1 is a valid label for CAS cells, not other geos
		return name.charAt(0) != '$' || (geo != null && geo.isGeoCasCell());
	}

	/**
	 * @param label
	 *            label
	 * @param kernel
	 *            kernel
	 * @param geo
	 *            element
	 * @return whether label can be parsed, is not reserved name and does not
	 *         start with $
	 */
	public static boolean isValidLabel(String label, Kernel kernel,
			GeoElement geo) {

		if (!checkName(geo, label)) {
			return false;
		}

		try {
			// parseLabel for "A B" returns "A", check equality
			return label.trim()
					.equals(kernel.getAlgebraProcessor().parseLabel(label));
		} catch (Exception e) {
			// eg ParseException
			return false;
		} catch (Error e) {
			// eg TokenMgrError, BracketsError
			return false;
		}
	}

	/**
	 * @param var
	 *            variable name (for CAS cell)
	 * @return whether position(s) od $ are valid in this name
	 */
	public static boolean validVar(String var) {
		// check for invalid assignment variables like $, $$, $1, $2, ...,
		// $1$, $2$, ... which are dynamic references
		if (var.charAt(0) == GeoCasCell.ROW_REFERENCE_DYNAMIC) {
			boolean validVar = false;
			// if var.length() == 1 we have "$" and the for-loop won't be
			// entered
			for (int i = 1; i < var.length(); i++) {
				if (!Character.isDigit(var.charAt(i))) {
					if (i == 1 && var
							.charAt(1) == GeoCasCell.ROW_REFERENCE_DYNAMIC) {
						// "$$" so far, so it can be valid (if var.length >
						// 2) or invalid if "$$" is the whole var
					} else if (i == var.length() - 1 && var.charAt(var.length()
							- 1) == GeoCasCell.ROW_REFERENCE_DYNAMIC) {
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

	/**
	 * set labels for array of GeoElements with given label prefix. e.g.
	 * labelPrefix = "F", geos.length = 2 sets geo[0].setLabel("F_1") and
	 * geo[1].setLabel("F_2") all members in geos are assumed to be initialized.
	 * 
	 * @param labelPrefix
	 *            prefix
	 * @param geos
	 *            array of geos to be labeled
	 */
	public static void setLabels(final String labelPrefix,
			final GeoElementND[] geos) {
		if (geos == null) {
			return;
		}

		int visible = 0;
		int firstVisible = 0;
		for (int i = geos.length - 1; i >= 0; i--) {
			if (geos[i].isVisible()) {
				firstVisible = i;
				visible++;
			}
		}

		switch (visible) {
		case 0: // no visible geos: they all get the labelPrefix as suggestion
			for (int i = 0; i < geos.length; i++) {
				geos[i].setLabel(labelPrefix);
			}
			break;

		case 1: // if there is only one visible geo, don't use indices
			geos[firstVisible].setLabel(labelPrefix);
			break;

		default:
			// is this a spreadsheet label?
			final SpreadsheetCoords p = GeoElementSpreadsheet
					.spreadsheetIndices(labelPrefix);
			if ((p.column >= 0) && (p.row >= 0)) {
				// more than one visible geo and it's a spreadsheet cell
				// use D1, E1, F1, etc as names
				final int col = p.column;
				final int row = p.row;
				for (int i = 0; i < geos.length; i++) {
					geos[i].setLabel(geos[i].getFreeLabel(GeoElementSpreadsheet
							.getSpreadsheetCellName(col + i, row)));
				}
			} else { // more than one visible geo: use indices if we got a
						// prefix
				for (int i = 0; i < geos.length; i++) {
					geos[i].setLabel(geos[i].getIndexLabel(labelPrefix));
				}
			}
		}
	}

	/**
	 * Sets labels for given geos
	 * 
	 * @param labels
	 *            labels
	 * @param geos
	 *            geos
	 */
	public static void setLabels(final String[] labels, final GeoElementND[] geos) {
		geos[0].getKernel().batchAddStarted();
		final int labelLen = (labels == null) ? 0 : labels.length;

		if ((labelLen == 1) && (labels[0] != null) && !labels[0].equals("")) {
			setLabels(labels[0], geos);
			geos[0].getKernel().batchAddComplete();
			return;
		}

		String label;
		for (int i = 0; i < geos.length; i++) {
			if (i < labelLen) {
				label = labels[i];
			} else {
				label = null;
			}

			geos[i].setLabel(label);
		}
		geos[0].getKernel().batchAddComplete();
	}

	/**
	 * search through labels to find a free one, eg
	 * 
	 * A, B, C, ...
	 * 
	 * A_1, B_1, C_1, ...
	 * 
	 * A_2, B_2, C_2, ...
	 * 
	 * ...
	 * 
	 * A_{10}, B_{10}, c_{10}, ...
	 * 
	 * ...
	 * 
	 * @param chars
	 *            single character names for this type
	 * @return next label
	 */
	public String getNextIndexedLabel(char[] chars) {
		int counter = 0, q, r;
		String labelToUse = "";
		boolean repeat = true;

		while (repeat) {
			q = counter / chars.length; // quotient
			r = counter % chars.length; // remainder

			String labelBase;

			// this arabic letter is two Unicode chars
			if (chars[r] == '\u0647') {
				labelBase = "\u0647\u0640" + getMultiuserSuffix();
			} else {
				labelBase = chars[r] + getMultiuserSuffix();
			}

			String index1;
			String index2;

			if (q == 0) {
				index1 = "";
				index2 = "";
				labelToUse = labelBase;
			} else if (q < 10) {
				index1 = "_" + q;
				index2 = "_{" + q + "}";
				labelToUse = labelBase + index1;

			} else {
				index1 = "_" + q;
				index2 = "_{" + q + "}";
				labelToUse = labelBase + index2;
			}

			counter++;

			// is label reserved
			// check both forms ie a_{1} and a_1
			repeat = !cons.isFreeLabel(labelBase + index1, true, true)
					|| !cons.isFreeLabel(labelBase + index2, true, true);

		}

		return labelToUse;
	}

	/**
	 * Sets the characters which will be used as the labels of the angles.
	 * 
	 * @param greek
	 *            whether the characters for the angle should be greek.
	 */
	public void setAngleLabels(boolean greek) {
		this.angleLabels = greek ? LabelType.greekLowerCaseLabels
				: LabelType.lowerCaseLabels;
	}

	/**
	 * Returns the characters which are used as the labels of the angles.
	 * @return the character array that includes the characters for the angles.
	 */
	public char[] getAngleLabels() {
		return angleLabels;
	}

	/**
	 * @return next label for an integer slider
	 */
	public String getNextIntegerLabel() {
		return getNextIndexedLabel(LabelType.integerLabels);
	}

	/**
	 * @param trans localized prefix
	 * @return first free label of {prefix1, prefix2, ...}
	 */
	public String getNextNumberedLabel(String trans) {
		int counter = 0;
		String str;
		do {
			counter++;
			str = trans + cons.getKernel().internationalizeDigits(counter + "",
					StringTemplate.defaultTemplate);
		} while (!cons.isFreeLabel(str));
		return str;
	}

	/**
	 * Sets a suffix that is used for labeling newly created objects within multiuser
	 * @param multiuserSuffix User Suffix
	 */
	public void setMultiuserSuffix(String multiuserSuffix) {
		this.multiuserSuffix = multiuserSuffix;
	}

	/**
	 * @return The user suffix used to label objects in multiuser
	 */
	public String getMultiuserSuffix() {
		return multiuserSuffix;
	}
}
