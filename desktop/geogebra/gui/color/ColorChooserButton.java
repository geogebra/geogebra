/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.gui.color;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Button with popup component for choosing colors. A mouse click on the left
 * side of the button sets the selected color. A mouse click on the
 * right side triggers a popup with a swatch panel to choose a color. When the
 * popup is done the newly selected color is set. An actionPerformed() method
 * can retrieve the color by calling getSelectedColor().
 * 
 * @author G. Sturr 2010-7-10
 * 
 */
public class ColorChooserButton extends JButton{

	private static final long serialVersionUID = 1L;

	/**
	 * Generic mode. Preview icon shows a square filled with the current color.
	 */
	public static int MODE_GENERIC = 1;
	
	/**
	 * Spreadsheet mode. Preview icon shows a table with four cells, the upper row
	 * will have the current color as background.
	 */
	public static int MODE_SPREADSHEET = 2;
	
	/**
	 * Current setting for the mode.
	 */
	private int mode;
	
	private ColorChooserPopup myPopup;
	private Color selectedColor; 
	
	
	/** Button constructor */
	public ColorChooserButton(int mode){
		super(); 
		this.mode = mode;
		
		myPopup = new ColorChooserPopup();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Point locButton = getLocation();
				
				// trigger popup if the mouse is over the right side of the button
				if(e.getX() >= 20 &&  e.getX() <=38) { 
					myPopup.show(getParent(), locButton.x,locButton.y + getHeight());
				}
			}
		});
		
		selectedColor = Color.WHITE;
		setIcon(createIcon(selectedColor));
	}

	public Color getSelectedColor(){
		return selectedColor;
	}
	
	public void handlePopupEvent(){
		setIcon(createIcon(selectedColor));
		repaint();
		this.fireActionPerformed(new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED,getActionCommand())); 
	}


	/** 
	 * Draw an icon for the button. On the left side there's either a square filled
	 * with the color for MODE_GENERIC or a grid for MODE_SPREADSHEET.
	 * Right side is a downward triangle for the drop down popup.
	 */
	private ImageIcon createIcon( Color selectedColor) {
		
		// Create image 
		BufferedImage image = new BufferedImage(32, 18, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();

		// left hand side:
		// (a click here just sends back the selected color): 
		
		if(mode == MODE_SPREADSHEET) {
			// a grid filled with our selected color
			int s = 7;
			int d = 1;
			g2.setColor(selectedColor);
			g2.fillRect(d, d, s, s);
			g2.setColor(Color.BLACK);
			g2.drawRect(d, d, s, s);
	
			g2.setColor(selectedColor);
			g2.fillRect(d+s, d, s, s);
			g2.setColor(Color.BLACK);
			g2.drawRect(d+s, d, s, s);
	
			g2.setColor(this.getBackground());
			g2.fillRect(d, d+s, s, s);
			g2.setColor(Color.BLACK);
			g2.drawRect(d, d+s, s, s);
	
			g2.setColor(this.getBackground());
			g2.fillRect(d+s, d+s, s, s);
			g2.setColor(Color.BLACK);
			g2.drawRect(d+s, d+s, s, s);
		}
		else 
		{
			g2.setColor(selectedColor);
			g2.fillRect(1, 1, 16, 16);
			g2.setColor(Color.BLACK);
			g2.drawRect(1, 1, 16, 16);
		}
		
		// right hand side: a downward triangle
		// a click here triggers the popup
		g2.setColor(Color.BLACK);
		int x = 23;
		int y = 7;
		g2.drawLine(x, y, x+6, y);
		g2.drawLine(x+1, y+1, x+5, y+1);
		g2.drawLine(x+2, y+2, x+4, y+2);
		g2.drawLine(x+3, y+3, x+3, y+3);
		
		return new ImageIcon(image);
	}
	
	/************************************************************* 
	 *             Swatch Panel Popup
	 ************************************************************/
	
	public class ColorChooserPopup extends JPopupMenu {

		private static final long serialVersionUID = 1L;
		
		private SwatchPanel swatchPanel;

		public ColorChooserPopup() {
			super();
			
			setLayout(new BorderLayout());

			swatchPanel = new SwatchPanel();
			add(swatchPanel, BorderLayout.CENTER);
			
			/*
			 * The following code adds a color picker to this popup menu,
			 * it still has to be decided if this should be used though.
			 * 
			 * Some class attributes are missing, so they have to be added
			 * in case the code comments are removed.
			 */
			
			/*metaPanel = new JPanel(new BorderLayout());
			
			pickButton = new JButton("�");
			pickButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pickButton.setVisible(false);
					pickerPanel.setVisible(true);
					pack();
				}
			});
			metaPanel.add(pickButton, app.borderWest());
			
			pickerPanel = new JPanel(new BorderLayout());
			pickerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			
			colorPicker = new ColorPickerPanel();
			colorPicker.setPreferredSize(new Dimension(120, 120));
			colorPicker.setHSB(0.0f, 0.0f, 1.0f);
			colorPicker.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					selectedColor = colorPicker.getColor(); 
					handlePopupEvent();
					slider.repaint();
				}
			});
			pickerPanel.add(colorPicker, BorderLayout.CENTER);
			
			slider = new JSlider(VERTICAL, 0, 100, 0);
			slider.setPreferredSize(new Dimension(20, 120));
			slider.setValue(100);
			slider.setUI(new ColorPickerSliderUI(slider, colorPicker));
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					colorPicker.setModeParam(slider.getValue() / 100.0f);
				}
			});
			pickerPanel.add(slider, app.borderEast());
			pickerPanel.setVisible(false);
			metaPanel.add(pickerPanel, BorderLayout.CENTER);
			
			add(metaPanel, app.borderEast());*/
		}

		/** 
		 * Draw a swatch panel and handle mouse events in the panel. 
		*/
		class SwatchPanel extends JPanel implements ActionListener {			

			private static final long serialVersionUID = 1L;
			
			private Color[] colors;

			public SwatchPanel() {
				initColors();
				setLayout(new FlowLayout(FlowLayout.LEFT));
				setPreferredSize(new Dimension(150, 120));
				
				// color buttons
				SwatchButton sb;
				for(int i = 0; i < colors.length; ++i) {
					sb = new SwatchButton(colors[i]);
					sb.addActionListener(this);
					add(sb);
				}
			}
			
			public void actionPerformed(ActionEvent e) {
				selectedColor = ((SwatchButton)e.getSource()).getColor();
				handlePopupEvent();
				myPopup.setVisible(false);
			}
			
			/**
			 * List with colors.
			 */
			private void initColors() {
				colors = new Color[] {		
					new Color(255,255,255),	//White
					new Color(255,153,204),	//Rose
					new Color(255, 204, 153), // Tan
					new Color(255, 255, 153), // Light Yellow
					new Color(204, 255, 255), // Light Turquoise
					new Color(204, 255, 204), // Light Green
					new Color(192, 192, 192), // Silver
					new Color(255, 0, 255), // Magenta (fuchsia)
					new Color(255, 102, 0), // Orange
					new Color(255, 255, 0), // Yellow
					new Color(153, 204, 255), // Pale Blue
					new Color(0, 255, 0), // Green (lime)
					new Color(128, 128, 128), // Gray / Grey
					new Color(204, 153, 255), // Lavender
					new Color(255, 0, 0), // Red
					new Color(255, 215, 0), // Gold
					new Color(0, 0, 255), // Blue
					new Color(153, 204, 0), // Yellow Green
					new Color(0, 0, 0), // Black
					new Color(153, 51, 102), // Plum
					new Color(153, 51, 0), // Brown
					new Color(255, 153, 0), // Light Orange
					new Color(0, 128, 128), // Teal
					new Color(51, 153, 102), // Sea Green
					new Color(64, 64, 64), // Dark Gray
					new Color(0, 128, 0), // Dark Green
					new Color(0, 255, 255), // Cyan (aqua)
					new Color(51, 204, 204), // Aqua
					new Color(51, 102, 255), // Light Blue
					new Color(0, 204, 255), // Sky Blue
					new Color(51, 51, 153), // Indigo
					// new Color(128,128,0), //Olive
					// new Color(51,51,0), //Dark Olive
					// new Color(128,0,0), //Maroon
					// new Color(102,102,153), //Blue Gray
					// new Color(0,0,128), //Navy
					// new Color(0,51,0), //Dark Green
					// new Color(0,51,102), //Dark Teal
					new Color(128, 0, 128) // Purple
				};
			}
			
			/**
			 * A simple button for the color swatch, just stores the color and
			 * tells the button to use SwatchButtonUI.
			 * 
			 * @author Florian Sonner
			 */
			class SwatchButton extends JButton {

				private static final long serialVersionUID = 1L;
				
				private Color color;
				
				public SwatchButton(Color color) {					
					this.color = color;
					
					setCursor(new Cursor(Cursor.HAND_CURSOR));
					setUI(new SwatchButtonUI());
				}
				
				public Color getColor() {
					return color;
				}
			}
			
			/**
			 * The UI for the color swatch button. Draws a 16x16 square filled with the
			 * color of this button and a bevel border.
			 * 
			 * @author Florian Sonner
			 */
			class SwatchButtonUI extends BasicButtonUI 
			{				
				@Override
				public Dimension getPreferredSize(JComponent c) {
					return new Dimension(16, 16);
				}
				
				@Override
				public void paint(Graphics g, JComponent c) {
					SwatchButton b = (SwatchButton)c;
					
					// highlight border
					if(b.getModel().isPressed()) {
						g.setColor(Color.darkGray);
					} else {
						g.setColor(Color.gray);
					}
					g.drawLine(0, 0, 0, 15);
					g.drawLine(0, 0, 15, 0);
					
					// filling
					g.setColor(b.getColor());
					g.fillRect(1, 1, 14, 14);
					
					// shadow border
					g.setColor(Color.darkGray);
					g.drawLine(0, 15, 15, 15);
					g.drawLine(15, 0, 15, 15);
				}
			}
		}
	}
}

