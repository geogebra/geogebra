package org.geogebra.common.euclidian.draw.dropdown;

/**
 * Interface for DrawOptions.
 * TODO use class directly?
 */
public interface OptionsInterface {
	void scrollDown();

	void scrollUp();

	void setHoverIndex(int idx);

	int getItemCount();

	Object indexOf(OptionItem item);
}
