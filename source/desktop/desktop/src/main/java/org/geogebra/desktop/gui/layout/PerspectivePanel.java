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

package org.geogebra.desktop.gui.layout;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;

import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageResourceD;

/**
 * JPopupMenu to offer Perspective choices
 * 
 * @author G.Sturr
 * 
 */
public class PerspectivePanel extends JPopupMenu {

	private static final long serialVersionUID = 1L;

	private final AppD app;

	/* Layout manager */
	protected LayoutD layout;

	private final DockBar dockBar;

	private AbstractAction changePerspectiveAction;

	/**
	 * Constructs a PerspectivePanel
	 * 
	 * @param app application
	 * @param dockBar dockbar
	 */
	public PerspectivePanel(AppD app, DockBar dockBar) {
		this.app = app;
		this.layout = (LayoutD) app.getGuiManager().getLayout();
		this.dockBar = dockBar;

		initActions();
		initItems();
		Border b = this.getBorder();
		Border empty = BorderFactory.createEmptyBorder(0, 0, 10, 0);
		this.setBorder(BorderFactory.createCompoundBorder(b, empty));
	}

	@Override
	public void setVisible(boolean b) {
		// prevent call from javax.swing.JPopupMenu.menuSelectionChanged()
		if (!dockBar.sideBarHasMouse()) {
			superSetVisible(b);
		}
	}

	/**
	 * call super.setVisible()
	 * 
	 * @param b
	 *            flag
	 */
	public void superSetVisible(boolean b) {
		super.setVisible(b);
		dockBar.setSidebarTriangle(b);
	}

	/**
	 * Initialize the menu items.
	 */
	private void initItems() {

		this.removeAll();

		JMenuItem title = new JMenuItem("<html><font color = black>"
				+ app.getLocalization().getMenu("CreateYourOwn")
				+ "</font></html>");
		title.setIcon(GeoGebraIconD.createEmptyIcon(32, 32));
		title.setFont(app.getBoldFont());
		title.setEnabled(false);

		add(Box.createVerticalStrut(5));
		add(title);
		add(Box.createVerticalStrut(5));

		addPerspective(0, GuiResourcesD.MENU_VIEW_ALGEBRA);
		addPerspective(3, GuiResourcesD.MENU_VIEW_CAS);
		addPerspective(1, GuiResourcesD.PERSPECTIVES_GEOMETRY);
		addPerspective(4, GuiResourcesD.PERSPECTIVES_GEOMETRY3D);
		addPerspective(2, GuiResourcesD.MENU_VIEW_SPREADSHEET);
		addPerspective(5, GuiResourcesD.MENU_VIEW_PROBABILITY);
		add(Box.createVerticalStrut(20));
	}

	private void addPerspective(int i, ImageResourceD icon) {
		if (layout.getDefaultPerspectives(i) == null) {
			return;
		}
		JMenuItem tmpItem = new JMenuItem(changePerspectiveAction);
		tmpItem.setText(app.getLocalization()
				.getMenu(layout.getDefaultPerspectives(i).getId()));
		tmpItem.setActionCommand("d" + i);

		Icon ic;
		if (icon != null) {
			ic = app.getScaledIcon(icon);
			// GeoGebraIcon.ensureIconSize((ImageIcon) ic, new
			// Dimension(40,40));
		} else {
			ic = app.getEmptyIcon();
		}
		tmpItem.setIcon(ic);

		Dimension d = tmpItem.getMaximumSize();
		d.height = tmpItem.getPreferredSize().height;
		tmpItem.setMaximumSize(d);

		add(tmpItem);

	}

	/**
	 * Initialize the actions.
	 */
	private void initActions() {
		changePerspectiveAction = new AbstractAction() {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// default perspectives start with a "d"
				int index = Integer.parseInt(e.getActionCommand().substring(1));
				boolean changed = layout.applyPerspective(
							layout.getDefaultPerspectives(index));
				if (changed) {
					app.storeUndoInfo();
				}
			}
		};

	}

}
