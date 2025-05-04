package org.geogebra.common.euclidian.draw.dropdown;

/**
 * Interface for DrawOptions.
 * TODO use class directly?
 */
public interface OptionsInterface {
	void scrollDown();

	void scrollUp();

	/**
	 * @param idx hover index
	 */
	void setHoverIndex(int idx);

	int getItemCount();

	/**
	 * @param item item
	 * @return index of given item
	 */
	int indexOf(OptionItem item);
}
