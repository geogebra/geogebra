package org.geogebra.common.contextmenu;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.main.Localization;

/**
 * Items contained in the {@link ContextMenu} as selectable options
 */
public interface ContextMenuItem {
	/**
	 * Title/label of an item which may contain a number of special values
	 * that represents a subscript and it should be parsed before displaying in the UI
	 * <p>
	 *     Special subscript value format: "_{&lt;subscript&gt;}"
	 * </p>
	 *
	 * <table>
	 *     <tr>
	 *         <th>String value</th>
	 *         <th>Displayed value</th>
	 *     </tr>
	 *     <tr>
	 *			<td>"y_{1}"</td>
	 *			<td>y<sub>1</sub></td>
	 *     </tr>
	 *     <tr>
	 *         <td>"x_{15}"</td>
	 *         <td>x<sub>15</sub></td>
	 *     </tr>
	 *     <tr>
	 *         <td>"value1_{subscript1}, value2_{subscript2}"</td>
	 *         <td>value1<sub>subscript1</sub>, value2<sub>subscript2</sub></td>
	 *     </tr>
	 * </table>
	 *
	 * @param localization Used for translating the title
	 * @return The title of the item to be displayed in the UI
	 */
	@Nonnull
	String getLocalizedTitle(@Nonnull Localization localization);

	/**
	 * @return One of the possible values identifying the icon to be displayed in the UI
	 * in front of the item title, or null if the context menu item does not contain an icon
	 */
	@CheckForNull
	default Icon getIcon() {
		return null;
	}

	/**
	 * An identifier for the possible icon values
	 */
	enum Icon {
		Expression, Text, Help, Delete
	}
}
