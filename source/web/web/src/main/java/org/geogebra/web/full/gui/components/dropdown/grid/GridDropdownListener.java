package org.geogebra.web.full.gui.components.dropdown.grid;

/**
 * Listens for changes in GridCardPanel.
 */
public interface GridDropdownListener {

	/**
	 * Called when an item has been selected in the {@link GridCardPanel}.
	 * @param index index of the item
	 */
	void itemSelected(int index);
}
