package geogebra.javax.swing;

import javax.swing.JOptionPane;

import java.awt.Component;
import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.main.App;

public class GOptionPaneD implements GOptionPane{

	public int showConfirmDialog(Object parentComponent, String message,
			String title, int optionType, int messageType) {
		
		if (parentComponent instanceof Component){
			return JOptionPane.showConfirmDialog((Component)parentComponent, message,
					title, optionType, messageType);
		}
		App.debug("First parameter of GOptionPaneD.showConfirmDialog(...) must be a Component.");
		return -1;
	}

}
