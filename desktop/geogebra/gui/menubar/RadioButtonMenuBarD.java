package geogebra.gui.menubar;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import geogebra.common.gui.menubar.MenuInterface;
import geogebra.common.gui.menubar.RadioButtonMenuBar;
import geogebra.common.main.App;

public class RadioButtonMenuBarD extends JMenu implements RadioButtonMenuBar{

	public void addRadioButtonMenuItems(MenuInterface alistener,
			String[] items, String[] actionCommands, int selectedPos) {
		// TODO Auto-generated method stub
		
	}

	public void setSelected(int pos) {
		Component item = getMenuComponent(pos);
		if (item instanceof JRadioButtonMenuItem)
			((JRadioButtonMenuItem)item).setSelected(true);
		else 
			App.debug("Bad construction of radiobutton menu. All item must be an instance of JRadioButtonMenuItem.");
		
	}

}
