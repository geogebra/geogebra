package org.geogebra.common.euclidian.draw.dropdown;

import org.geogebra.common.annotation.MissingDoc;

/**
 * Interface for DrawOptions.
 * TODO use class directly?
 */
public interface OptionsInterface {
	@MissingDoc
	void scrollDown();

	@MissingDoc
	void scrollUp();

	/**
	 * @param idx hover index
	 */
	void setHoverIndex(int idx);

	@MissingDoc
	int getItemCount();

	/**
	 * @param item item
	 * @return index of given item
	 */
	int indexOf(OptionItem item);
}
