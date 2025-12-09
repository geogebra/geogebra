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

package org.geogebra.desktop.gui.menubar;

import java.awt.Component;
import java.awt.Font;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import org.geogebra.common.gui.menubar.MenuInterface;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppD;

public class RadioButtonMenuBarD extends JMenu implements MenuInterface {

	private static final long serialVersionUID = 1L;

	private AppD app;

	private ButtonGroup buttonGroup;

	/**
	 * @param application application
	 */
	public RadioButtonMenuBarD(App application) {
		super();
		app = (AppD) application;
	}

	/**
	 * @param listener listener
	 * @param items items
	 * @param selectedPos selected position
	 * @param changeText whether to translate the options
	 */
	public void addRadioButtonMenuItems(final Consumer<Integer> listener,
			String[] items, int selectedPos,
			boolean changeText) {

		JRadioButtonMenuItem mi;
		buttonGroup = new ButtonGroup();
		// String label;

		for (int i = 0; i < items.length; i++) {
			if ("---".equals(items[i])) {
				addSeparator();
			} else {
				String text = changeText
						? app.getLocalization().getMenu(items[i]) : items[i];
				mi = new JRadioButtonMenuItem(text);
				mi.setFont(app.getFontCanDisplayAwt(text, false, Font.PLAIN,
						app.getGUIFontSize()));
				if (i == selectedPos) {
					mi.setSelected(true);
				}
				final int index = i;
				mi.addActionListener(evt -> listener.accept(index));

				buttonGroup.add(mi);
				add(mi);
			}
		}
	}

	/**
	 * @param pos selected position
	 */
	public void setSelected(int pos) {

		if (pos == -1) { // unselect all
			buttonGroup.clearSelection();
		} else {
			Component item = getMenuComponent(pos);
			if (item instanceof JRadioButtonMenuItem) {
				((JRadioButtonMenuItem) item).setSelected(true);
			} else {
				Log.debug("Bad construction of radiobutton menu. "
						+ "All item must be an instance of JRadioButtonMenuItem.");
			}
		}
	}

}
