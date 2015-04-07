package org.geogebra.desktop.gui.view.algebra;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComboBox;

public abstract class MyComboBoxListener extends MouseAdapter implements ActionListener {
		
	@Override
	public void mousePressed(MouseEvent e) {
		Object src = e.getSource();

		doActionPerformed(src);	
		if (src instanceof JComboBox) {
			JComboBox cb = (JComboBox) src;
			cb.setPopupVisible(false);
		}		
	}		
	
	@Override
	public void mouseReleased(MouseEvent e) {
		Object src = e.getSource();
		if (src instanceof JComboBox) {
			JComboBox cb = (JComboBox) src;
			cb.setPopupVisible(false);
		}
	}	
		
	public void actionPerformed(ActionEvent e) {
		doActionPerformed(e.getSource());
	}		
	
	public abstract void doActionPerformed(Object source);
}