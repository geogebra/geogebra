package geogebra.javax.swing;

import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.main.App;
import geogebra.common.util.AsyncOperation;
import geogebra.main.AppD;

import javax.swing.JOptionPane;

public class GOptionPaneD implements GOptionPane {

	
	public int showConfirmDialog(App app, String message, String title,
			int optionType, int messageType, Object icon) {
		
		return JOptionPane.showConfirmDialog(((AppD) app).getMainComponent(),
				message, title, optionType, messageType);
		
	}

	public void showInputDialog(App app, String message,
			String initialSelectionValue, Object icon, AsyncOperation handler) {
		// TODO Auto-generated method stub
		
	}

	public void showOptionDialog(App app, String message, String title,
			int optionType, int messageType, Object icon, String[] optionNames,
			AsyncOperation handler) {
		// TODO Auto-generated method stub
		
	}

	public void setGlassEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		
	}

	
}
