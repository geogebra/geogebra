package org.geogebra.common.gui.view.table;

/**
 * Table values related utility method collection.
 */
public final class TableUtil {

	/**
	 * Returns html string of indexed label
	 * @param columnIndex index of column
	 * @return html string of indexed label
	 */
	public static String getHeaderHtml(TableValuesModel model, int columnIndex) {
		String content = model.getHeaderAt(columnIndex);
		if (content.contains("_")) {
			String[] labelParts = content.split("_");
			if (labelParts.length == 2) {
				String index = labelParts[1].replaceAll("\\{", "")
						.replaceAll("\\}", "");
				return labelParts[0] + "<sub>" + index + "</sub>";
			}
		}
		return content;
	}
}
