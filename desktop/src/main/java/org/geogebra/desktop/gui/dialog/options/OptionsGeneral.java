package org.geogebra.desktop.gui.dialog.options;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicCheckBoxUI;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.desktop.main.AppD;

/**
 * General options.
 * 
 * @author Florian Sonner
 */
public class OptionsGeneral extends JPanel implements SetLabels {
	/** */
	private static final long serialVersionUID = 1L;
	
	/**
	 * An instance of the GeoGebra application.
	 */
	private AppD app;
	
	/**
	 * The tabbed pane which contains the single areas which can be
	 * edited using this panel.
	 */
	private JTabbedPane tabbedPane;
	
	/**
	 * Panel to manage application colors.
	 */
	private JPanel colorPanel;
	
	/**
	 * Panel with color swatches.
	 */
	private JPanel swatchPanel;
	
	/**
	 * Construct a panel for the general options which is divided using tabs.
	 * 
	 * @param app
	 */
	public OptionsGeneral(AppD app) {
		this.app = app;
		
		initGUI();
		updateGUI();
	}
	
	/**
	 * Initialize the GUI.
	 */
	private void initGUI() {
		initColorPanel();
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("", new JPanel());
		tabbedPane.addTab("", new JPanel());
		tabbedPane.addTab("", new JPanel());
		tabbedPane.addTab("", colorPanel);
		
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
	}
	
	/**
	 * Init the panel to manage application colors.
	 */
	private void initColorPanel() {
		colorPanel = new JPanel(new BorderLayout());
		
		JPanel swatchManagementPanel = new JPanel(new BorderLayout());
		swatchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		swatchPanel.setBackground(Color.darkGray);
		
		updateSwatchPanel();
		
		swatchManagementPanel.add(swatchPanel, BorderLayout.CENTER);
		
		JPanel swatchButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		swatchButtonPanel.add(new JButton("Remove"));
		swatchManagementPanel.add(swatchButtonPanel, BorderLayout.SOUTH);
		
		colorPanel.add(swatchManagementPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Update the GUI to take care of new settings which were applied.
	 */
	public void updateGUI() {
		// TODO Hide tabs for applets (F.S.)
	}
	
	/**
	 * Update the swatch panel.
	 */
	private void updateSwatchPanel() {
		swatchPanel.removeAll();
		
		swatchPanel.add(new SwatchCheckBox(Color.white));
		swatchPanel.add(new SwatchCheckBox(Color.red));
		swatchPanel.add(new SwatchCheckBox(Color.black));
		swatchPanel.add(new SwatchCheckBox(Color.green));
	}
	
	/**
	 * Update the labels of the current panel. Should be applied if the
	 * language was changed. Will be called after initialization automatically.
	 */
	public void setLabels() {
		tabbedPane.setTitleAt(0, app.getMenu("General"));
		tabbedPane.setTitleAt(1, app.getPlain("Display"));
		tabbedPane.setTitleAt(2, app.getMenu("Export"));
		tabbedPane.setTitleAt(3, app.getPlain("Color"));		
	}
	
	/**
	 * Save the settings of this panel.
	 */
	public void apply() {
		
	}
	
	/**
	 * Class for color swatch checkboxes.
	 * 
	 * @author Florian Sonner
	 */
	private class SwatchCheckBox extends JCheckBox
	{
		private static final long serialVersionUID = 1L;
		private Color color;
		
		public SwatchCheckBox(Color color) {
			this.color = color;
			
			setUI(new SwatchCheckBoxUI());
		}
		
		public Color getColor() {
			return color;
		}
	}
	
	private class SwatchCheckBoxUI extends BasicCheckBoxUI
	{
		@Override
		public synchronized void paint(Graphics g, JComponent c)
		{
			SwatchCheckBox b = (SwatchCheckBox)c;
			
			if(b.getModel().isSelected()) {
				g.setColor(Color.white);
			} else {
				g.setColor(Color.black);
			}
			
			g.drawLine(0, 0, 0, 15);
			g.drawLine(0, 0, 15, 0);
			
			// filling
			g.setColor(b.getColor());
			g.fillRect(1, 1, 14, 14);
			
			if(b.getModel().isSelected()) {
				g.setColor(Color.white);
			} else {
				g.setColor(Color.black);
			}
			
			g.drawLine(0, 15, 15, 15);
			g.drawLine(15, 0, 15, 15);
		}
		
		@Override
		public Dimension getPreferredSize(JComponent j) {
			return new Dimension(16, 16);
		}
	}
}
