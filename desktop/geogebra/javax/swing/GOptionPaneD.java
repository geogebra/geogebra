package geogebra.javax.swing;

import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.main.App;
import geogebra.main.AppD;

import javax.swing.JOptionPane;

public class GOptionPaneD implements GOptionPane {

	public int showConfirmDialog(App app, String message, String title,
			int optionType, int messageType) {

		return JOptionPane.showConfirmDialog(((AppD) app).getMainComponent(),
				message, title, optionType, messageType);

	}

	public String getReturnValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getReturnOption() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void showInputDialog(App app, String message,
			String initialSelectionValue, Object closeHandler) {
		// TODO Auto-generated method stub

	}

	public void showOptionDialog(App app, String message, String title,
			int optionType, int messageType, String[] optionNames,
			Object closeHandler) {
		// TODO Auto-generated method stub

	}

}
