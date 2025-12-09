/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.gui.toolbar;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.geogebra.desktop.main.AppD;

/**
 * Renderer for tools tree
 *
 */
public class ModeCellRenderer extends DefaultTreeCellRenderer
		implements ListCellRenderer<Integer> {

	private static final long serialVersionUID = 1L;

	private AppD app;

	/**
	 * Creates new cell renderer
	 * 
	 * @param app
	 *            application
	 */
	public ModeCellRenderer(AppD app) {
		setOpaque(true);
		setBackgroundNonSelectionColor(Color.white);
		this.app = app;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean componentSelected, boolean expanded, boolean leaf, int row,
			boolean componentHasFocus) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

		// mode number
		if (leaf) {
			Object ob = node.getUserObject();
			if (ob instanceof Integer) {
				// mode node
				int mode = ((Integer) ob).intValue();
				handleModeNode(mode);
			} else {
				// root node
				handleRootNode(node);
			}
		}
		// folder
		else {
			if (row == 0) {
				handleRootNode(node);
			} else {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node
						.getFirstChild();
				Object ob = childNode.getUserObject();
				handleModeNode(((Integer) ob).intValue());
			}
		}

		handleSelection(componentSelected);

		return this;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Integer value,
			int index, boolean isSelected, boolean cellHasFocus) {
		handleModeNode(value.intValue());
		handleSelection(isSelected);
		return this;
	}

	private void handleRootNode(DefaultMutableTreeNode node) {
		setIcon(null);
		Object ob = node.getUserObject();
		if (ob != null) {
			setText(ob.toString());
		} else {
			setText(app.getLocalization().getMenu("Toolbar"));
		}
	}

	private void handleModeNode(int mode) {
		if (mode == -1) {
			setText("\u2500\u2500\u2500 "
					+ app.getLocalization().getMenu("Separator"));
			setIcon(null);
		} else {
			setText(app.getToolName(mode));
			setIcon(app.getModeIcon(mode));
		}
	}

	private void handleSelection(boolean select) {
		if (select) {
			setBackground(AppD.COLOR_SELECTION);
		} else {
			setBackground(getBackgroundNonSelectionColor());
		}
	}

}