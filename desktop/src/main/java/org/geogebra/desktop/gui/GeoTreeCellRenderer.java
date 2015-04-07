/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.main.AppD;

/**
 * ListCellRenderer for GeoElements
 * 
 * @author Markus Hohenwarter
 */
public class GeoTreeCellRenderer extends DefaultTreeCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AppD app;
	private ImageIcon iconShown, iconHidden;

	public GeoTreeCellRenderer(AppD app) {
		this.app = app;
		setOpaque(true);

		iconShown = app.getScaledIcon("shown.gif");
		iconHidden = app.getScaledIcon("hidden.gif");
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		TreeNode root = node.getRoot();

		if (root != node && root != node.getParent()) {
			// GeoElement
			GeoElement geo = (GeoElement) node.getUserObject();
			if (geo == null)
				return this;

			// ICONS
			if (geo.isEuclidianVisible()) {
				setIcon(iconShown);
			} else {
				setIcon(iconHidden);
			}

			setForeground(org.geogebra.desktop.awt.GColorD.getAwtColor(geo.getLabelColor()));
			setText(geo.getLabelTextOrHTML());
			setFont(app.getFontCanDisplayAwt(getText(), Font.BOLD));

			if (geo.doHighlighting())
				setBackground(AppD.COLOR_SELECTION);
			else
				setBackground(getBackgroundNonSelectionColor());
		} else {
			// type node
			setForeground(Color.black);

			if (selected)
				setBackground(AppD.COLOR_SELECTION);
			else
				setBackground(getBackgroundNonSelectionColor());

			setBorder(null);
			setText(value.toString());
			setIcon(null);
			setFont(app.getFontCanDisplayAwt(getText()));

		}

		return this;
	}
}