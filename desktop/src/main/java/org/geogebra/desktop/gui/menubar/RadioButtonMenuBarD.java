package org.geogebra.desktop.gui.menubar;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import org.geogebra.common.gui.menubar.MyActionListener;
import org.geogebra.common.gui.menubar.RadioButtonMenuBar;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppD;

public class RadioButtonMenuBarD extends JMenu implements RadioButtonMenuBar {

	private static final long serialVersionUID = 1L;

	private AppD app;

	private ButtonGroup buttonGroup;

	public RadioButtonMenuBarD(App application) {
		super();
		app = (AppD) application;
	}

	@Override
	public void addRadioButtonMenuItems(final MyActionListener alistener,
			String[] items, String[] actionCommands, int selectedPos,
			boolean changeText) {

		JRadioButtonMenuItem mi;
		buttonGroup = new ButtonGroup();
		// String label;

		for (int i = 0; i < items.length; i++) {
			if ("---".equals(items[i])) {
				addSeparator();
			} else {
				String text = (changeText)
						? app.getLocalization().getMenu(items[i]) : items[i];
				mi = new JRadioButtonMenuItem(text);
				mi.setFont(app.getFontCanDisplayAwt(text, false, Font.PLAIN,
						app.getGUIFontSize()));
				if (i == selectedPos) {
					mi.setSelected(true);
				}
				mi.setActionCommand(actionCommands[i]);
				if (alistener instanceof ActionListener) {
					mi.addActionListener((ActionListener) alistener);
				} else {
					mi.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							alistener.actionPerformed(e.getActionCommand());
						}

					});
				}

				buttonGroup.add(mi);
				add(mi);
			}
		}
	}

	@Override
	public void setSelected(int pos) {

		if (pos == -1) { // unselect all
			buttonGroup.clearSelection();
		} else {
			Component item = getMenuComponent(pos);
			if (item instanceof JRadioButtonMenuItem) {
				((JRadioButtonMenuItem) item).setSelected(true);
			} else {
				Log.debug(
						"Bad construction of radiobutton menu. All item must be an instance of JRadioButtonMenuItem.");
			}
		}
	}

}
