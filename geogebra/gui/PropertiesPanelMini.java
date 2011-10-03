package geogebra.gui;

import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.PropertiesPanelMiniListener;
import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PropertiesPanelMini extends JFrame implements ActionListener, ChangeListener {
	
	JComboBox dashCB;
	//JLabel dashLabel;
	JPanel panel, sizePanel, lineStylePanel, colorPanel;
	JSlider slider;
	JButton colorButton;
	JPopupMenu colorMenu;
	PreviewPanel pv;
	
	Application app;
	float transparency = 0.75f;
	PropertiesPanelMiniListener listener;
	
	public PropertiesPanelMini(Application app, PropertiesPanelMiniListener listener) {
		
		super();
		
		this.app = app;
		this.listener = listener;
		
		
		this.setFocusableWindowState(false);
		this.setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		try { // Java 6u10+ only
			Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
			Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
			mSetWindowOpacity.invoke(null, this, Float.valueOf(transparency));
		} catch (Exception ex) {

			// fallback for OSX Leopard pre-6u10
			this.getRootPane().putClientProperty("Window.alpha", Float.valueOf(transparency));

		} 


		initialize();

	}
	
	public void setListener(PropertiesPanelMiniListener listener) {
		this.listener = listener;
	}
	
	private void initialize() {
		//setSize(windowX, windowY);
		//setPreferredSize(new Dimension(windowX, windowY));
		populateContentPane();
	}
	
	private void populateContentPane() {

		//setLayout(null);

		// line style combobox (dashing)		
		DashListRenderer renderer = new DashListRenderer();
		renderer.setPreferredSize(
			new Dimension(130, app.getGUIFontSize() + 6));
		dashCB = new JComboBox(EuclidianView.getLineTypes());
		dashCB.setRenderer(renderer);
		dashCB.addActionListener(this);

		// line style panel
		
		
		lineStylePanel = new JPanel();
		//panel.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//dashLabel = new JLabel();
		//panel.add(dashLabel);
		lineStylePanel.add(dashCB);
		
		Container pane = getContentPane();
		
		
		slider = new JSlider(1, 13);
		slider.setMajorTickSpacing(2);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		slider.addChangeListener(this);
		slider.setValue(3);
		listener.setSize(3);
		sizePanel = new JPanel();
		sizePanel.add(slider);		
		
		colorPanel = new JPanel();
		//colorButton = new JButton();
		//colorButton.setPreferredSize(new Dimension(100,50));
		//colorPanel.add(colorButton);
		
		pv = new PreviewPanel();
		colorPanel.add(pv);
		

		//setLayout(new FlowLayout(FlowLayout.CENTER));
		//setLayout(new FlowLayout());
		//add(sizePanel);
		
		panel = new JPanel();
		panel.setLayout(new GridLayout(3,1));
		panel.add(sizePanel);
		panel.add(lineStylePanel);
		panel.add(colorPanel);
		
		pane.add(panel);




		pack();

	}



	public void actionPerformed(ActionEvent e) {
		//Application.debug(e.getSource().getClass()+"");
		
		if (e.getSource().equals(dashCB)) {
			//Application.debug(dashCB.getSelectedIndex()+"");
			listener.setLineStyle(EuclidianView.getLineTypes()[dashCB.getSelectedIndex()]);
		}
		
	}

	public void stateChanged(ChangeEvent e) {
		//Application.debug(e.getSource().getClass()+"");
		
		if (e.getSource().equals(slider)) {
			//Application.debug(dashCB.getSelectedIndex()+"");
			listener.setSize(slider.getValue());
		}
		
	}

	private class PreviewPanel extends JPanel implements MouseListener, ActionListener {
		
		JPopupMenu menu = new JPopupMenu();
		
		private Color color = Color.black;
		
	    public PreviewPanel() {
	    	addMouseListener(this);
	        setPreferredSize(new Dimension(100,app.getGUIFontSize() + 8));
	        setBorder(BorderFactory.createRaisedBevelBorder());
			 
	        
	        JMenuItem menuItem1 = new JMenuItem(app.getColor("Red"), new ColorIcon(Color.red));  
	        JMenuItem menuItem2 = new JMenuItem(app.getColor("Green"), new ColorIcon(Color.green));  
	        JMenuItem menuItem3 = new JMenuItem(app.getColor("Blue"), new ColorIcon(Color.blue));  
	        JMenuItem menuItem4 = new JMenuItem(app.getColor("Black"), new ColorIcon(Color.black));  
	        JMenuItem menuItem5 = new JMenuItem(app.getColor("White"), new ColorIcon(Color.white));  
	        JMenuItem menuItem6 = new JMenuItem(app.getColor("Yellow"), new ColorIcon(Color.yellow));  
	        JMenuItem menuItem7 = new JMenuItem(app.getColor("Cyan"), new ColorIcon(Color.cyan));  
	        JMenuItem menuItem8 = new JMenuItem(app.getColor("Magenta"), new ColorIcon(Color.magenta));  
	        JMenuItem menuItem9 = new JMenuItem(app.getColor("Orange"), new ColorIcon(Color.orange));  
	        JMenuItem menuItem10 = new JMenuItem(app.getColor("Pink"), new ColorIcon(Color.pink));  
	        JMenuItem menuItem11 = new JMenuItem(app.getColor("Gray"), new ColorIcon(Color.gray));  
	        JMenuItem menuItem12 = new JMenuItem(app.getColor("Purple"), new ColorIcon(GeoGebraColorConstants.getGeogebraColor(app, "PURPLE")));  
	        JMenuItem menuItem13 = new JMenuItem(app.getColor("Brown"), new ColorIcon(GeoGebraColorConstants.getGeogebraColor(app, "BROWN")));  
	        menuItem1.addActionListener(this);
	        menuItem2.addActionListener(this);
	        menuItem3.addActionListener(this);
	        menuItem4.addActionListener(this);
	        menuItem5.addActionListener(this);
	        menuItem6.addActionListener(this);
	        menuItem7.addActionListener(this);
	        menuItem8.addActionListener(this);
	        menuItem9.addActionListener(this);
	        menuItem10.addActionListener(this);
	        menuItem11.addActionListener(this);
	        menuItem12.addActionListener(this);
	        menuItem13.addActionListener(this);
	        
	        menu.add(menuItem1);
	        menu.add(menuItem2);
	        menu.add(menuItem3);
	        menu.add(menuItem4);
	        menu.add(menuItem5);
	        menu.add(menuItem6);
	        menu.add(menuItem7);
	        menu.add(menuItem8);
	        menu.add(menuItem9);
	        menu.add(menuItem10);
	        menu.add(menuItem11);
	        menu.add(menuItem12);
	        menu.add(menuItem13);

	        
	      }
	    
	      public void paintComponent(Graphics g) {
	        Dimension size = getSize();

	        g.setColor(color);
	        g.fillRect(0,0,size.width,size.height);
	      }
		public void mouseClicked(MouseEvent e) {
			menu.show(this, e.getX(), e.getY());
		}
		
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		public void actionPerformed(ActionEvent e) {
			
			JMenuItem item = (JMenuItem)(e.getSource());
			ColorIcon icon = (ColorIcon)(item.getIcon());
			
			color = icon.getColor();
			
			repaint(); // force new color to be shown
			
			listener.setColor(color);
			
		}
    }

    private static int HEIGHT = 14;
    private static int WIDTH = 14;

	private class ColorIcon implements Icon
	{

	    private Color color;

	    public ColorIcon(Color color)
	    {
	        this.color = color;
	    }

	    public int getIconHeight()
	    {
	        return HEIGHT;
	    }

	    public int getIconWidth()
	    {
	        return WIDTH;
	    }

	    public Color getColor()
	    {
	        return color;
	    }

	    public void paintIcon(Component c, Graphics g, int x, int y)
	    {
	        g.setColor(color);
	        g.fillRect(x, y, WIDTH - 1, HEIGHT - 1);

	        g.setColor(Color.black);
	        g.drawRect(x, y, WIDTH - 1, HEIGHT - 1);
	    }
	}
}
