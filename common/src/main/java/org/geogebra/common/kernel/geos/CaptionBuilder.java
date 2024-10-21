package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;

public class CaptionBuilder {

	/**
	 * @param caption
	 *            caption pattern
	 * @param geo
	 *            element
	 * @param tpl
	 *            template
	 * @return caption with substitutions
	 */
	public static String getCaption(String caption, GeoElement geo,
			StringTemplate tpl) {
		StringBuilder captionSB = new StringBuilder();
		Kernel kernel = geo.getKernel();
		// replace %v with value and %n with name
		for (int i = 0; i < caption.length(); i++) {
			char ch = caption.charAt(i);
			if ((ch == '%') && (i < (caption.length() - 1))) {
				// get number after %
				i++;
				ch = caption.charAt(i);
				switch (ch) {
				case 'c':
					// (text value) of next cell to the right
					String cText = "";
					String label = geo.getLabelSimple();
					if (label != null) {
						SpreadsheetCoords p = GeoElementSpreadsheet
								.spreadsheetIndices(label);
						if (p.column > -1 && p.row > -1) {
							String labelR1 = GeoElementSpreadsheet
									.getSpreadsheetCellName(p.column + 1, p.row);
							GeoElement geoR1 = kernel.lookupLabel(labelR1);
							if (geoR1 != null) {
								cText = geoR1.toValueString(tpl);
							}
						}
					}
					captionSB.append(cText);
					break;
				case 'f':
					captionSB.append(geo.getDefinition(tpl));
					break;
				case 'd':
					captionSB.append(geo.getDefinitionDescription(tpl));
					break;
				case 'v':
					captionSB.append(geo.toValueString(tpl));
					break;
				case 'n':
					captionSB.append(geo.getLabel(tpl));
					break;
				case 'x':
					if (geo.isGeoPoint()) {
						captionSB.append(kernel.format(
								((GeoPointND) geo).getInhomCoords().getX(),
								tpl));
					} else if (geo.isGeoVector()) {
						captionSB.append(kernel.format(
								((GeoVectorND) geo).getInhomCoords()[0], tpl));
					} else if (geo.isGeoLine()) {
						captionSB.append(
								kernel.format(((GeoLine) geo).getX(), tpl));
					} else {
						captionSB.append("%x");
					}

					break;
				case 'y':
					if (geo.isGeoPoint()) {
						captionSB.append(kernel.format(
								((GeoPointND) geo).getInhomCoords().getY(),
								tpl));
					} else if (geo.isGeoVector()) {
						captionSB.append(kernel.format(
								((GeoVectorND) geo).getInhomCoords()[1], tpl));
					} else if (geo.isGeoLine()) {
						captionSB.append(
								kernel.format(((GeoLine) geo).getY(), tpl));
					} else {
						captionSB.append("%y");
					}
					break;
				case 'z':
					if (geo.isGeoPoint()) {
						captionSB.append(kernel.format(
								((GeoPointND) geo).getInhomCoords().getZ(),
								tpl));
					} else if (geo.isGeoVector()) {
						captionSB.append(
								((GeoVectorND) geo).getInhomCoords().length < 3
										? "0"
										: kernel.format(
												((GeoVectorND) geo)
														.getInhomCoords()[2],
												tpl));
					} else if (geo.isGeoLine()) {
						captionSB.append(
								kernel.format(((GeoLine) geo).getZ(), tpl));
					} else {
						captionSB.append("%z");
					}
					break;

				default:
					captionSB.append('%');
					captionSB.append(ch);
				}
			} else {
				captionSB.append(ch);
			}
		}

		if (captionSB.length() == 0) {
			// can't return empty string
			// eg if %c used when not a spreadsheet cell
			return geo.getLabel(tpl);
		}

		return captionSB.toString();
	}

}
