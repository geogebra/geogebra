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

package org.geogebra.desktop.gui.view.algebra;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.main.AppD;

/**
 * Editor for AV
 */
public class MyCellEditorD extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;
	private AppD app;

	/**
	 * @param textField
	 *            text field
	 * @param app
	 *            application
	 */
	public MyCellEditorD(final JTextField textField, AppD app) {
		super(textField);
		this.app = app;
	}

	/** Implements the <code>TreeCellEditor</code> interface. */
	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row) {

		String str = null;
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object ob = node.getUserObject();
			if (ob instanceof GeoElement) {
				GeoElement geo = (GeoElement) ob;
				StringTemplate tpl = StringTemplate.defaultTemplate;
				if ((geo.isPointOnPath() || geo.isPointInRegion())
						&& geo.isChangeable()) {
					str = geo.toString(tpl);
				} else if (geo.isChangeable()) {
					str = geo.getDefinitionForInputBar();
				} else {
					str = geo.getDefinition(tpl);
				}
			}
		}

		String stringValue;
		if (str == null) {
			stringValue = (value == null) ? "" : value.toString();
		} else {
			stringValue = str;
		}
		delegate.setValue(stringValue);

		// make sure we use a font that can display the text
		editorComponent.setFont(app.getFontCanDisplayAwt(str));
		return editorComponent;
	}
}
