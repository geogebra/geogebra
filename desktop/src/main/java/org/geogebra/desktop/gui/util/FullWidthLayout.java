package org.geogebra.desktop.gui.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * Layout manager which stretches all component to the full possible width, but
 * unlike GridLayout does not enforce any height. Unncessary space is left empty
 * at the bottom of the container.
 */
public class FullWidthLayout implements LayoutManager {
	private int vgap;
	private int minWidth = 0, minHeight = 0;
	private int preferredWidth = 0, preferredHeight = 0;
	private boolean sizeUnknown = true;

	/**
	 * Creates a new instance of the layout manager with a vertical gap of 5px.
	 */
	public FullWidthLayout() {
		this(5);
	}

	/**
	 * Creates a new instance of the layout manager.
	 * 
	 * @param vgap
	 *            gap between two components in px
	 */
	public FullWidthLayout(int vgap) {
		this.vgap = vgap;
	}

	/**
	 * Calculate the minimum and preferred size of the passed container.
	 * 
	 * @param parent
	 */
	private void calculateSizes(Container parent) {
		preferredWidth = 0;
		preferredHeight = 0;
		minWidth = 0;
		minHeight = 0;

		Dimension pref = null, min = null;
		for (int i = 0; i < parent.getComponentCount(); i++) {
			Component c = parent.getComponent(i);

			if (c.isVisible()) {
				pref = c.getPreferredSize();
				min = c.getMinimumSize();

				preferredHeight += (i > 0 ? vgap : 0) + pref.height;
				preferredWidth = Math.max(pref.width, preferredWidth);

				minWidth = Math.max(min.width, minWidth);
				minHeight += (i > 0 ? vgap : 0) + min.height;
			}
		}
	}

	/**
	 * @return The preferred size of the container.
	 */
	@Override
	public Dimension preferredLayoutSize(Container parent) {
		calculateSizes(parent);
		sizeUnknown = false;

		Insets insets = parent.getInsets();
		return new Dimension(preferredWidth + insets.left + insets.right,
				preferredHeight + insets.top + insets.bottom);
	}

	/**
	 * @return The minimum size of the container.
	 */
	@Override
	public Dimension minimumLayoutSize(Container parent) {
		if (sizeUnknown) {
			calculateSizes(parent);
			sizeUnknown = false;
		}

		Insets insets = parent.getInsets();
		return new Dimension(minWidth + insets.left + insets.right,
				minHeight + insets.top + insets.bottom);
	}

	/**
	 * Layout components.
	 */
	@Override
	public void layoutContainer(Container parent) {
		Insets insets = parent.getInsets();

		int maxWidth = parent.getWidth() - (insets.left + insets.right);
		int y = insets.top;

		if (sizeUnknown) {
			calculateSizes(parent);
		}

		for (int i = 0; i < parent.getComponentCount(); i++) {
			Component c = parent.getComponent(i);

			if (c.isVisible()) {
				Dimension pref = c.getPreferredSize();

				if (i > 0) {
					y += vgap;
				}

				c.setBounds(insets.left, y, maxWidth, pref.height);

				y += pref.height;
			}
		}
	}

	/**
	 * Not implemented.
	 */
	@Override
	public void addLayoutComponent(String name, Component comp) {
		// not needed
	}

	/**
	 * Not implemented.
	 */
	@Override
	public void removeLayoutComponent(Component comp) {
		// not needed
	}

	/**
	 * The class name and vertical gap as string.
	 */
	@Override
	public String toString() {
		return getClass().getName() + "[vgap=" + vgap + "]";
	}
}
