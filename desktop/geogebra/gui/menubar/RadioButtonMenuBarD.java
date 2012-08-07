package geogebra.gui.menubar;

import geogebra.common.gui.menubar.MyActionListener;
import geogebra.common.gui.menubar.RadioButtonMenuBar;
import geogebra.common.main.App;
import geogebra.main.AppD;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

public class RadioButtonMenuBarD extends JMenu implements RadioButtonMenuBar {

	private static final long serialVersionUID = 1L;

	private AppD app;

	public RadioButtonMenuBarD(App application) {
		super();
		app = (AppD) application;
	}

	public void addRadioButtonMenuItems(final MyActionListener alistener,
			String[] items, String[] actionCommands, int selectedPos,
			boolean changeText) {

		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		// String label;

		for (int i = 0; i < items.length; i++) {
			if (items[i] == "---") {
				addSeparator();
			} else {
				String text = (changeText) ? app.getMenu(items[i]) : items[i];
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
						public void actionPerformed(ActionEvent e) {
							alistener.actionPerformed(e.getActionCommand());
						}

					});
				}

				bg.add(mi);
				add(mi);
			}
		}
	}

	public void setSelected(int pos) {
		Component item = getMenuComponent(pos);
		if (item instanceof JRadioButtonMenuItem) {
			((JRadioButtonMenuItem) item).setSelected(true);
		} else {
			App.debug("Bad construction of radiobutton menu. All item must be an instance of JRadioButtonMenuItem.");
		}
	}

}
