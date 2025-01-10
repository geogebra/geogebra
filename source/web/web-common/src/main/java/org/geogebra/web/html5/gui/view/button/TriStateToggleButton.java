package org.geogebra.web.html5.gui.view.button;

import org.gwtproject.resources.client.ResourcePrototype;

public class TriStateToggleButton extends StandardButton {

	private ResourcePrototype[] icons = new ResourcePrototype[3];
	private int index = 0;

	/**
	 * Default constructor
	 */
	public TriStateToggleButton() {
		super(24);
	}

	/**
	 * Updates the icons for this button
	 * @param first First icon
	 * @param second Second icon
	 * @param third Third icon
	 */
	public void updateIcons(ResourcePrototype first, ResourcePrototype second,
			ResourcePrototype third) {
		icons = new ResourcePrototype[]{first, second, third};
	}

	/**
	 * Selects on the three different button states and updates the icon
	 * @param index Index
	 */
	public void select(int index) {
		this.index = index;
		setIcon(icons[index]);
	}

	/**
	 * Increments the index and sets the next icon
	 */
	public void selectNext() {
		index++;
		if (index > 2) {
			index = 0;
		}
		setIcon(icons[index]);
	}

	/**
	 * @return The current index of the Button
	 */
	public int getIndex() {
		return index;
	}

}
