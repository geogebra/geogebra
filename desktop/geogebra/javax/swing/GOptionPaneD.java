package geogebra.javax.swing;

import geogebra.common.javax.swing.GOptionPane;
import geogebra.common.main.App;

import java.awt.Component;

import javax.swing.JOptionPane;

public class GOptionPaneD implements GOptionPane{

	public int showConfirmDialog(Object parentComponent, String message,
			String title, int optionType, int messageType) {
		
		if (parentComponent instanceof Component){
			return JOptionPane.showConfirmDialog((Component)parentComponent, message,
					title, optionType, messageType);
		}
		App.debug("First parameter of GOptionPaneD.showConfirmDialog(...) must be a Component, but currently is a "
				+ parentComponent.getClass().toString());
		return -1;
	}

}
