package geogebra.javax.swing;

import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.main.App;
import geogebra.main.AppD;

import javax.swing.JOptionPane;

public class GOptionPaneD implements GOptionPane{

	public int showConfirmDialog(App app, String message,
			String title, int optionType, int messageType) {
		
		
			return JOptionPane.showConfirmDialog(((AppD)app).getMainComponent(), message,
					title, optionType, messageType);
		
	}

}
