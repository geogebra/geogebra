package geogebra.gui.dialog.options;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class OptionsUtil {

	public static JPanel flowPanel(Component... comps){
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,2,5));
		for(Component comp: comps){
			p.add(comp);
		}
		return p;
	}
	
	public static Border titleBorder(String title){
		Border outsideBorder = BorderFactory.createTitledBorder(title);
		Border insideBorder = BorderFactory.createEmptyBorder(0, 20, 0, 0);
		return BorderFactory.createCompoundBorder(outsideBorder, insideBorder);
	}
	
}
