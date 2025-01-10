package org.geogebra.desktop.gui.util;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.SystemColor;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class LayoutUtil {

	private static final int DEFAULT_HGAP = 2;
	private static final int DEFAULT_VGAP = 3;

	/**
	 * @param comps components
	 * @return flow panel
	 */
	public static JPanel flowPanel(Component... comps) {
		JPanel p = new JPanel(
				new FlowLayout(FlowLayout.LEFT, DEFAULT_HGAP, DEFAULT_VGAP));
		for (Component comp : comps) {
			p.add(comp);
		}
		return p;
	}

	/**
	 * @param tab left padding
	 * @param comps components
	 * @return flow panel
	 */
	public static JPanel flowPanel(int tab, Component... comps) {
		JPanel p = new JPanel(
				new FlowLayout(FlowLayout.LEFT, DEFAULT_HGAP, DEFAULT_VGAP));
		p.add(Box.createHorizontalStrut(tab));
		for (Component comp : comps) {
			p.add(comp);
		}
		return p;
	}

	/**
	 * @param hgap horizontal gap
	 * @param vgap vertical gap
	 * @param tab left padding
	 * @param comps components
	 * @return left-aligned flow panel
	 */
	public static JPanel flowPanel(int hgap, int vgap, int tab,
			Component... comps) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
		p.add(Box.createHorizontalStrut(tab));
		for (Component comp : comps) {
			p.add(comp);
		}
		return p;
	}

	/**
	 * @param hgap horizontal gap
	 * @param vgap vertical gap
	 * @param tab left padding
	 * @param comps components
	 * @return centered flow panel
	 */
	public static JPanel flowPanelCenter(int hgap, int vgap, int tab,
			Component... comps) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, hgap, vgap));
		p.add(Box.createHorizontalStrut(tab));
		for (Component comp : comps) {
			p.add(comp);
		}
		return p;
	}

	/**
	 * @param hgap horizontal gap
	 * @param vgap vertical gap
	 * @param tab left padding
	 * @param comps components
	 * @return right-aligned flow panel
	 */
	public static JPanel flowPanelRight(int hgap, int vgap, int tab,
			Component... comps) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, hgap, vgap));
		p.add(Box.createHorizontalStrut(tab));
		for (Component comp : comps) {
			p.add(comp);
		}
		return p;
	}

	/**
	 * @param title title
	 * @return titled border
	 */
	public static Border titleBorder(String title) {
		Border lineBorder = BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0,
						SystemColor.controlLtHighlight),
				BorderFactory.createMatteBorder(0, 0, 1, 0,
						SystemColor.controlShadow));

		Border outsideBorder = BorderFactory.createTitledBorder(lineBorder,
				title, TitledBorder.LEADING, TitledBorder.TOP);
		Border insideBorder = BorderFactory.createEmptyBorder(0, 40, 0, 0);
		return BorderFactory.createCompoundBorder(outsideBorder, insideBorder);
	}
}
