package geogebra.gui.dialog.options;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class OptionsUtil {

	private static int defaultHgap = 2;
	private static int defaultVgap = 3;

	public static JPanel flowPanel(Component... comps) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, defaultHgap,
				defaultVgap));
		for (Component comp : comps) {
			p.add(comp);
		}
		return p;
	}

	public static JPanel flowPanel(int tab, Component... comps) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, defaultHgap,
				defaultVgap));
		p.add(Box.createHorizontalStrut(tab));
		for (Component comp : comps) {
			p.add(comp);
		}
		return p;
	}

	public static JPanel flowPanel(int hgap, int vgap, int tab,
			Component... comps) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
		p.add(Box.createHorizontalStrut(tab));
		for (Component comp : comps) {
			p.add(comp);
		}
		return p;
	}

	public static Border titleBorder(String title) {
		Border outsideBorder = BorderFactory.createTitledBorder(title);
		Border insideBorder = BorderFactory.createEmptyBorder(0, 20, 0, 0);
		return BorderFactory.createCompoundBorder(outsideBorder, insideBorder);
	}

	public static class TitlePanel extends JPanel {

		public TitlePanel() {
			this("");
		}
		
		public TitlePanel(String title) {
			setLayout((new BoxLayout(this, BoxLayout.Y_AXIS)));
			setBorder(titleBorder(title));
		}

		public void setTitle(String title) {
			setBorder(titleBorder(title));
		}
	}

}
