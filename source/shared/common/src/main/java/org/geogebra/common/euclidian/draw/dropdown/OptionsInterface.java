package org.geogebra.common.euclidian.draw.dropdown;

public interface OptionsInterface {
	void scrollDown();

	void scrollUp();

	void setHoverIndex(int idx);

	int getItemCount();

	Object indexOf(OptionItem item);
}
