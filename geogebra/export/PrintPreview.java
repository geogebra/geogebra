/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.export;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.TitlePanel;
import geogebra.gui.view.Gridable;
import geogebra.main.Application;
import geogebra.main.GeoGebraPreferences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.border.MatteBorder;

public class PrintPreview extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	protected int m_wPage;
	protected int m_hPage;
	protected int m_orientation;
	protected int m_scale;
	protected Printable m_target;
	protected JComboBox m_cbScale, m_cbOrientation;
	protected JCheckBox cbEVscalePanel;
	protected JScrollPane ps;
	protected PreviewContainer m_preview;
	protected Application app;
	
	protected boolean kernelChanged = false;
	
	static Graphics tempGraphics;
	static {
		BufferedImage img = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
		tempGraphics = img.getGraphics(); 
	}		
	
	public PrintPreview(Application app,Gridable target){
		this(app,target,PageFormat.PORTRAIT);
	}

	public PrintPreview(Application app, Printable target) {
		this(app, target, PageFormat.PORTRAIT);		
	}

	public PrintPreview(Application app, Printable target, int orientation) {
		super(app.getFrame(), true); //modal=true: user shouldn't be able to change anything before actual print happened.
		this.app = app;
		initPrintPreview(target, orientation);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
		
	public PrintPreview(Application app, Gridable target, int portrait) {
		this(app,new PrintGridable(target),portrait);
	}

	private void initPrintPreview(Printable target, int orientation) {
		m_target = target;
		m_orientation = orientation;
		m_scale = 75; // init scale to 75%		
		
		loadPreferences();
		
		setTitle(app.getMenu("PrintPreview"));
		Cursor oldCursor = app.getMainComponent().getCursor();
		app.getMainComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));			
		getContentPane().setLayout(new BorderLayout());
			
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		JButton bt = new JButton(app.getMenu("Print"), 
												app.getImageIcon("document-print.png"));
		ActionListener lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {				
						try {
							PrinterJob prnJob = PrinterJob.getPrinterJob();
							prnJob.setPageable(m_preview);							
		
							if (!prnJob.printDialog())
								return;
							setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							prnJob.print();
							setCursor(
								Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							setVisible(false);
						} catch (PrinterException ex) {
							ex.printStackTrace();
							Application.debug("Printing error: " + ex.toString());
						}
					}
				};
				runner.start();
			}
		};
		bt.addActionListener(lst);
		bt.setAlignmentY(0.5f);
		bt.setMargin(new Insets(4, 6, 4, 6));
		tb.add(bt);
		tb.addSeparator();

		bt = new JButton(app.getMenu("Close"));
		lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};
		bt.addActionListener(lst);
		bt.setAlignmentY(0.5f);
		bt.setMargin(new Insets(2, 6, 2, 6));
		tb.add(bt);

		String[] scales = { "10%", "25%", "50%", "75%", "100%", "150%", "200%" };
		m_cbScale = new JComboBox(scales);
		m_cbScale.setSelectedItem(m_scale + "%");
		lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						String str = m_cbScale.getSelectedItem().toString();
						if (str.endsWith("%"))
							str = str.substring(0, str.length() - 1);
						str = str.trim();
						int scale = 0;
						try {
							scale = Integer.parseInt(str);
						} catch (NumberFormatException ex) {
							return;
						}
						setScale(scale);
						setCursor(Cursor.getDefaultCursor());
					}
				};
				runner.start();
			}
		};
		m_cbScale.addActionListener(lst);
		m_cbScale.setMaximumSize(m_cbScale.getPreferredSize());
		m_cbScale.setEditable(false); // can be set true
		tb.addSeparator();
		tb.add(m_cbScale);
		
		// ORIENTATION combo box
		String[] orients = { 	app.getMenu("Portrait"), 
										app.getMenu("Landscape") };
		m_cbOrientation = new JComboBox(orients);
		m_cbOrientation.setSelectedIndex( 
			(m_orientation == PageFormat.PORTRAIT) ? 0 : 1);	
			
		lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						int orientation = (m_cbOrientation.getSelectedIndex() == 0) ? 
								PageFormat.PORTRAIT : PageFormat.LANDSCAPE;								
													
						setOrientation(orientation);
						
						PrintPreview prev = PrintPreview.this; 
						int width = prev.getPreferredSize().width;
						if (width > prev.getWidth()) setSize(width, prev.getHeight());																										
						setCursor(Cursor.getDefaultCursor());				
					}
				};
				runner.start();
			}
		};					
		m_cbOrientation.addActionListener(lst);
		m_cbOrientation.setMaximumSize(m_cbOrientation.getPreferredSize());
		m_cbOrientation.setEditable(false);
		tb.addSeparator();
		tb.add(m_cbOrientation);
		
		TitlePanel titlePanel = new TitlePanel(app);
		lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				kernelChanged = true;
				Thread runner = new Thread() {
					public void run() {
						setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));					
						updatePages();													
						setCursor(Cursor.getDefaultCursor());				
					}
				};
				runner.start();
			};
		};
		titlePanel.addActionListener(lst);
			
		m_preview = new PreviewContainer();		
		ps = new JScrollPane(m_preview);
		JPanel centerPanel = new JPanel(new BorderLayout());
		
		// show scale panel for euclidian view
		EuclidianView ev = app.getEuclidianView();
		EuclidianView ev2 = app.getEuclidianView2();
		app.clearSelectedGeos();
		if (m_target == ev || m_target == ev2) {		
			// checkbox to turn on/off printing of scale string
			cbEVscalePanel = new JCheckBox();
			cbEVscalePanel.setSelected(app.isPrintScaleString());
			cbEVscalePanel.addActionListener(lst);
			cbEVscalePanel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					app.setPrintScaleString(cbEVscalePanel.isSelected());
				}				
			});
						
			// scale panel to set scale of x-axis in cm
			PrintScalePanel scalePanel = new PrintScalePanel(app, (EuclidianView) m_target);				
			scalePanel.addActionListener(lst);									
			
			JPanel tempPanel = new JPanel();
			tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.X_AXIS));
			tempPanel.setBorder(BorderFactory.createEtchedBorder());		
			tempPanel.add(Box.createHorizontalStrut(10));
			tempPanel.add(cbEVscalePanel);			
			tempPanel.add(scalePanel);
						
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(titlePanel, BorderLayout.CENTER);
			panel.add(tempPanel, BorderLayout.SOUTH);
			centerPanel.add(panel, BorderLayout.NORTH);
		} else {
			centerPanel.add(titlePanel, BorderLayout.NORTH);
		}
	
		// preview in center
		centerPanel.add(ps, BorderLayout.CENTER);
				
		// toolbar north
		getContentPane().add(tb, BorderLayout.NORTH); 
		// title and preview center
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		//setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		// init the preview
		initPages();	
		centerOnScreen();			   	      
	   						
		setVisible(true);		
		app.getMainComponent().setCursor(oldCursor);
	}
	
	private void loadPreferences() {
		try {
			// orientation			
			String strOrientation = GeoGebraPreferences.getPref().
				loadPreference(GeoGebraPreferences.PRINT_ORIENTATION, "landscape");
			m_orientation = strOrientation.equals("portrait") ? PageFormat.PORTRAIT : PageFormat.LANDSCAPE;						
	    						
			
			// show printing scale in cm
			app.setPrintScaleString( Boolean.valueOf(
					GeoGebraPreferences.getPref().
						loadPreference(GeoGebraPreferences.PRINT_SHOW_SCALE, "false")).booleanValue() );	    							
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void savePreferences() {    		    	
    	// orientation
    	String strOrientation;
    	switch (m_orientation) {
    		case PageFormat.LANDSCAPE: strOrientation = "landscape"; break;    		
    		default: strOrientation = "portrait";
    	}    	
    	
    	GeoGebraPreferences pref =  GeoGebraPreferences.getPref();
    	pref.savePreference(GeoGebraPreferences.PRINT_ORIENTATION, strOrientation);
    	
    	// show printing scale in cm
    	pref.savePreference(GeoGebraPreferences.PRINT_SHOW_SCALE, Boolean.toString(app.isPrintScaleString()));  
    }
	
	
	public void setVisible(boolean flag) {
		if (flag) {
			// note: preferences loaded in initPreview			
			super.setVisible(true);			
		} else {
			// store undo info
			if (kernelChanged) {
				app.storeUndoInfo();				
			}
			
			// save preferences
			savePreferences();
			super.setVisible(false);
		}				
	}
	
	private void centerOnScreen() {
		//	center on screen
		pack();
		Dimension size = getPreferredSize();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();  	
		int w = Math.min(size.width, dim.width);
		int h = Math.min(size.height, (int)(dim.height*0.9));
		setLocation((dim.width - w) / 2, (dim.height - h) / 2);                
		setSize(w, h);	
	}
	
	/**
	 *  Sets the orientation of preview and applies current scale.
	 */
	private void initPages() {	
		PageFormat pageFormat = getDefaultPageFormat();
		pageFormat.setOrientation(m_orientation);
	
		if (pageFormat.getWidth() == 0 ||  pageFormat.getHeight() == 0) {
			Application.debug("Unable to determine default page size");
			return;
		}
		
		int pageIndex = 0;			
		while (true) {
			if (pageExists(pageIndex)) {
				PagePreview pp = new PagePreview(m_target, pageFormat, pageIndex);
				pp.setScale(m_scale);			
				m_preview.add(pp);
			} 
			else break;			
			pageIndex++;
		}								
	}
	
	private PageFormat getDefaultPageFormat() {
		PrinterJob prnJob = PrinterJob.getPrinterJob();	
		PageFormat pageFormat = prnJob.defaultPage();
		
		Paper paper = pageFormat.getPaper();				
		double width = paper.getWidth();
		double height = paper.getHeight();			
		if (width > 0 &&  height > 0) {
			//	set margins				
			paper.setImageableArea(
					Application.PAGE_MARGIN_X, 
					Application.PAGE_MARGIN_Y,
					width  - 2 * Application.PAGE_MARGIN_X, 
					height - 2 * Application.PAGE_MARGIN_Y);
			pageFormat.setPaper(paper);
		}
				
		return pageFormat;
	}
	
	// update Pages, add or remove last page if necessary
	private void updatePages() {							
		// update existing pages
		Component[] comps = m_preview.getComponents();
		for (int k = 0; k < comps.length; k++) {
			if (!(comps[k] instanceof PagePreview))
				continue;
			PagePreview pp = (PagePreview) comps[k];
			pp.update();			 			
		}
		
		// add or remove last page if necessary
		// last page gone?
		if (!pageExists(comps.length-1)) {
			m_preview.remove(comps.length-1);
			m_preview.doLayout();
			m_preview.getParent().getParent().validate(); 
		}
		// new page?
		else if (pageExists(comps.length)) {			
			PageFormat pageFormat = getDefaultPageFormat();
			pageFormat.setOrientation(m_orientation);
			if (pageFormat.getHeight() == 0 || pageFormat.getWidth() == 0) {
				Application.debug("Unable to determine default page size");
				return;
			}		
			PagePreview pp = new PagePreview(m_target, pageFormat, comps.length);
			pp.setScale(m_scale);
			m_preview.add(pp);
			m_preview.doLayout();
			m_preview.getParent().getParent().validate(); 
		}	
		System.gc();
	}
	
	public boolean pageExists(int pageIndex) {
		try {							
			PageFormat pageFormat = getDefaultPageFormat();
			return (m_target.print(tempGraphics, pageFormat, pageIndex) ==
										Printable.PAGE_EXISTS);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}			
	}
	
	private void setOrientation(int orientation) {
		m_orientation = orientation;	
				
		PageFormat pageFormat = getDefaultPageFormat();
		pageFormat.setOrientation(m_orientation);
	
		Component[] comps = m_preview.getComponents();
		for (int k = 0; k < comps.length; k++) {
			if (!(comps[k] instanceof PagePreview))
				continue;
			PagePreview pp = (PagePreview) comps[k];
			pp.setPageFormat(pageFormat);			
		}
		m_preview.doLayout();
		m_preview.getParent().getParent().validate();
		System.gc();
	}
	
	private void setScale(int scale) {
		m_scale = scale;
	
		Component[] comps = m_preview.getComponents();
		for (int k = 0; k < comps.length; k++) {
			if (!(comps[k] instanceof PagePreview))
				continue;
			PagePreview pp = (PagePreview) comps[k];
			pp.setScale(scale);
		}
		m_preview.doLayout();
		m_preview.getParent().getParent().validate();	
		System.gc();	
	}

	class PreviewContainer extends JPanel implements Pageable {
		private static final long serialVersionUID = 1L;
		
		protected int H_GAP = 16;
		protected int V_GAP = 10;

		public Dimension getPreferredSize() {
			int n = getComponentCount();
			if (n == 0)
				return new Dimension(H_GAP, V_GAP);
			Component comp = getComponent(0);
			Dimension dc = comp.getPreferredSize();
			int w = dc.width;
			int h = dc.height;

			Dimension dp = getParent().getSize();
			int nCol = Math.max((dp.width - H_GAP) / (w + H_GAP), 1);
			int nRow = n / nCol;
			if (nRow * nCol < n)
				nRow++;

			int ww = nCol * (w + H_GAP) + H_GAP;
			int hh = nRow * (h + V_GAP) + V_GAP;
			Insets ins = getInsets();
			return new Dimension(
				ww + ins.left + ins.right,
				hh + ins.top + ins.bottom);
		}

		public Dimension getMaximumSize() {
			return getPreferredSize();
		}

		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		public void doLayout() {
			Insets ins = getInsets();
			int x = ins.left + H_GAP;
			int y = ins.top + V_GAP;

			int n = getComponentCount();
			if (n == 0)
				return;
			Component comp = getComponent(0);
			Dimension dc = comp.getPreferredSize();
			int w = dc.width;
			int h = dc.height;

			Dimension dp = getParent().getSize();
			int nCol = Math.max((dp.width - H_GAP) / (w + H_GAP), 1);
			int nRow = n / nCol;
			if (nRow * nCol < n)
				nRow++;

			int index = 0;
			for (int k = 0; k < nRow; k++) {
				for (int m = 0; m < nCol; m++) {
					if (index >= n)
						return;
					comp = getComponent(index++);
					comp.setBounds(x, y, w, h);
					x += w + H_GAP;
				}
				y += h + V_GAP;
				x = ins.left + H_GAP;
			}
		}
		
		
		/* ****************
		 * Pageable interface		 
		 * ****************/
		 
		public int getNumberOfPages() {			
			return getComponentCount();
		}

		public PageFormat getPageFormat(int pageIndex) 
		throws IndexOutOfBoundsException {			 			
			try {
				return ((PagePreview)getComponent(pageIndex)).getPageFormat();
			} catch (Exception e) {
				throw new IndexOutOfBoundsException();
			}			
		}

		public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
			return m_target;
		}
	}	

	class PagePreview extends JPanel {
		
		private static final long serialVersionUID = 1L;

		protected int m_w;
		protected int m_h;
		protected Printable target;
		protected PageFormat format;
		protected int pageIndex;
		protected double scale = 1.0;
		protected BufferedImage img;

		public PagePreview(Printable target, PageFormat format, int pageIndex) {
			this.target = target;
			this.format = format;						
			this.pageIndex = pageIndex;
		
			m_w = (int) format.getWidth();
			m_h = (int) format.getHeight();					
								
			setBackground(Color.white);
			setBorder(new MatteBorder(1, 1, 2, 2, Color.black));
//			update();
		}		
	
		public void setPageFormat(PageFormat format) {
			this.format = format;	
			m_w = (int) (format.getWidth() * scale);
			m_h = (int) (format.getHeight() * scale);
			update();
		}
		
		public PageFormat getPageFormat() {
			return format;
		}

		public void setScale(int scale) {
			double newScale = scale / 100.0;
			if (newScale != this.scale) {
				this.scale = newScale;	
				m_w = (int) (format.getWidth() * this.scale);
				m_h = (int) (format.getHeight() * this.scale);
				update();
			}				
		}

		public Dimension getPreferredSize() {
			Insets ins = getInsets();
			return new Dimension(
				m_w + ins.left + ins.right,
				m_h + ins.top + ins.bottom);
		}

		public Dimension getMaximumSize() {
			return getPreferredSize();
		}

		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		private void updateBufferedImage() {
			img = new BufferedImage(m_w, m_h, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = img.createGraphics();
			g2.setColor(getBackground());
			g2.fillRect(0, 0, m_w, m_h);				
			if (scale != 1.0) g2.scale(scale, scale);		
			try {
				target.print(g2, format, pageIndex);
			} catch (Exception e) {}									
		}
		
		public void update() {
			try {
				updateBufferedImage();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e){
				e.printStackTrace();
			}
			repaint();			
		}

		public void paint(Graphics g) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			g.drawImage(img, 0, 0, this);
			paintBorder(g);
		}		
	}
}

