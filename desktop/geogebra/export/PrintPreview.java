/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.export;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.main.App;
import geogebra.gui.GuiManagerD;
import geogebra.gui.TitlePanel;
import geogebra.gui.layout.DockPanel;
import geogebra.gui.view.Gridable;
import geogebra.main.AppD;
import geogebra.main.GeoGebraPreferencesD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
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
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

public class PrintPreview extends JDialog {

	private static final long serialVersionUID = 1L;

	protected int m_wPage;
	protected int m_hPage;
	protected int m_orientation;
	protected int m_scale;
	protected Printable m_target;
	protected JComboBox m_cbScale, m_cbOrientation, m_cbView;
	// protected JCheckBox cbEVscalePanel;
	protected JScrollPane ps;
	protected PreviewContainer m_preview;
	protected AppD app;
	protected JPanel tempPanel, panelForTitleAndScaling; // used for title and
															// scaling of
															// graphics view's
															// print preview
	protected ActionListener lst;

	protected boolean kernelChanged = false;

	static Graphics tempGraphics;
	static {
		BufferedImage img = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
		tempGraphics = img.getGraphics();
	}

	public PrintPreview(AppD app, Gridable target) {
		this(app, target, PageFormat.PORTRAIT);
	}

	public PrintPreview(AppD app, Printable target) {
		this(app, target, PageFormat.PORTRAIT);
	}

	public PrintPreview(AppD app, Printable target, int orientation) {
		super(app.getFrame(), true); // modal=true: user shouldn't be able to
										// change anything before actual print
										// happened.
		this.app = app;
		initPrintPreview(target, orientation);
	}

	public PrintPreview(AppD app, Gridable target, int portrait) {
		this(app, new PrintGridable(target), portrait);
	}

	private void initPrintPreview(Printable target, int orientation) {
		m_target = target;
		m_orientation = orientation;
		m_scale = 75; // init scale to 75%

		loadPreferences();

		setTitle(app.getMenu("PrintPreview"));
		Cursor oldCursor = app.getMainComponent().getCursor();
		app.getMainComponent().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		getContentPane().setLayout(new BorderLayout());

		// print button
		JButton btnPrint = new JButton(app.getMenu("Print"),
				app.getImageIcon("document-print.png"));
		lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						try {
							PrinterJob prnJob = PrinterJob.getPrinterJob();
							prnJob.setPageable(m_preview);

							if (!prnJob.printDialog())
								return;
							setCursor(Cursor
									.getPredefinedCursor(Cursor.WAIT_CURSOR));
							prnJob.print();
							setCursor(Cursor
									.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							setVisible(false);
						} catch (PrinterException ex) {
							ex.printStackTrace();
							App.debug("Printing error: " + ex.toString());
						}
					}
				};
				runner.start();
			}
		};
		btnPrint.addActionListener(lst);
		btnPrint.setAlignmentY(0.5f);
		// btnPrint.setMargin(new Insets(2, 2, 2, 2));

		// scale comboBox
		String[] scales = { "10%", "25%", "50%", "75%", "100%", "150%", "200%" };
		m_cbScale = new JComboBox(scales);
		m_cbScale.setSelectedItem(m_scale + "%");
		lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));
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

		// ORIENTATION combo box
		String[] orients = { app.getMenu("Portrait"), app.getMenu("Landscape") };
		m_cbOrientation = new JComboBox(orients);
		m_cbOrientation
				.setSelectedIndex((m_orientation == PageFormat.PORTRAIT) ? 0
						: 1);

		lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));
						int orientation = (m_cbOrientation.getSelectedIndex() == 0) ? PageFormat.PORTRAIT
								: PageFormat.LANDSCAPE;

						setOrientation(orientation);

						PrintPreview prev = PrintPreview.this;
						int width = prev.getPreferredSize().width;
						if (width > prev.getWidth())
							setSize(width, prev.getHeight());
						setCursor(Cursor.getDefaultCursor());
					}
				};
				runner.start();
			}
		};
		m_cbOrientation.addActionListener(lst);
		m_cbOrientation.setMaximumSize(m_cbOrientation.getPreferredSize());
		m_cbOrientation.setEditable(false);

		// VIEW combo box
		m_cbView = new JComboBox(getAvailableViews());

		DockPanel focusedPanel = ((GuiManagerD)app.getGuiManager()).getLayout().getDockManager().getFocusedPanel();
		if (focusedPanel == null)
			m_cbView.setSelectedItem(app.getPlain("AllViews"));
		else
			m_cbView.setSelectedItem(app.getPlain(focusedPanel.getViewTitle()));

		ActionListener lst_view = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));
						m_preview.removeAll();

						String selItem = m_cbView.getSelectedItem().toString();

						// change view
						if (selItem.equals(app.getPlain("AlgebraWindow"))) {
							m_target = new PrintGridable(((GuiManagerD)app.getGuiManager())
									.getAlgebraView());
						} else if (selItem.equals(app.getPlain("CAS"))) {
							m_target = new ScalingPrintGridable(((GuiManagerD)app.getGuiManager()).getCasView());
						} else if (selItem.equals(app.getPlain("Spreadsheet"))) {
							m_target = new PrintGridable(((GuiManagerD)app.getGuiManager())
									.getSpreadsheetView());
						} else if (selItem.equals(app.getPlain("DrawingPad"))) {
							m_target = app.getEuclidianView1();
						} else if (selItem.equals(app.getPlain("DrawingPad2"))) {
							m_target = ((GuiManagerD)app.getGuiManager()).getEuclidianView2();
						} else if (selItem.equals(app
								.getPlain("ConstructionProtocol"))) {
							m_target = ((GuiManagerD)app.getGuiManager())
									.getConstructionProtocolView();
						} else if (selItem.equals(app.getPlain("DataAnalysis"))) {
							m_target = ((GuiManagerD)app.getGuiManager())
									.getDataAnalysisView();
						} else if (selItem.equals(app.getPlain("AllViews"))) {
							m_target = (Printable) app.getMainComponent();
						}

						// show the appropriate scale panel
						tempPanel.removeAll();
						if ((selItem == app.getPlain("DrawingPad"))
								|| (selItem == app.getPlain("DrawingPad2"))) {
							tempPanel.add(createPanelForScaling());
						}
						if ((selItem == app.getPlain("CAS")))
							tempPanel.add(createPanelForScaling2());
						panelForTitleAndScaling.revalidate();

						initPages();

						m_preview.doLayout();
						m_preview.getParent().getParent().validate();

						setCursor(Cursor.getDefaultCursor());

					}
				};
				runner.start();
			}
		};
		m_cbView.addActionListener(lst_view);
		m_cbView.setMaximumSize(m_cbView.getPreferredSize());
		m_cbView.setEditable(false);

		// BUTTON PANEL
		JPanel westPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		westPanel.add(btnPrint);
		westPanel.add(Box.createHorizontalStrut(30));
		westPanel.add(m_cbView);
		westPanel.add(Box.createHorizontalStrut(30));
		westPanel.add(m_cbScale);
		westPanel.add(m_cbOrientation);

		JPanel buttonPanel = new JPanel(new BorderLayout(2, 2));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		buttonPanel.add(westPanel, app.borderWest());

		// title
		TitlePanel titlePanel = new TitlePanel(app);
		lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				kernelChanged = true;
				Thread runner = new Thread() {
					@Override
					public void run() {
						setCursor(Cursor
								.getPredefinedCursor(Cursor.WAIT_CURSOR));
						updatePages();
						setCursor(Cursor.getDefaultCursor());
					}
				};
				runner.start();
			}
		};
		titlePanel.addActionListener(lst);

		m_preview = new PreviewContainer();
		ps = new JScrollPane(m_preview);
		JPanel centerPanel = new JPanel(new BorderLayout());
		panelForTitleAndScaling = new JPanel(new BorderLayout());

		// show scale panel for euclidian view
		EuclidianView ev = app.getEuclidianView1();
		EuclidianView ev2 = app.getEuclidianView2();
		// CASView cas = app.getca
		app.getSelectionManager().clearSelectedGeos();

		tempPanel = new JPanel(new GridLayout(0, 1));
		if (m_target == ev || m_target == ev2) {
			tempPanel.add(createPanelForScaling());
		}
		// HACK: m_target gives no information about the current view
		else if (m_target instanceof ScalingPrintGridable)
			tempPanel.add(createPanelForScaling2());
		panelForTitleAndScaling.add(tempPanel, BorderLayout.SOUTH);
		panelForTitleAndScaling.add(titlePanel, BorderLayout.CENTER);
		centerPanel.add(panelForTitleAndScaling, BorderLayout.NORTH);

		// preview in center
		centerPanel.add(ps, BorderLayout.CENTER);

		// toolbar north
		getContentPane().add(buttonPanel, BorderLayout.NORTH);
		// title and preview center
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		// setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// init the preview
		initPages();
		centerOnScreen();

		app.getMainComponent().setCursor(oldCursor);
	}

	private String[] getAvailableViews() {
		ArrayList<String> list = new ArrayList<String>();

		if (((GuiManagerD)app.getGuiManager()).showView(App.VIEW_ALGEBRA)) {
			list.add(app.getPlain("AlgebraWindow"));
		}
		if (((GuiManagerD)app.getGuiManager()).showView(App.VIEW_CAS)) {
			list.add(app.getPlain("CAS"));
		}
		if (((GuiManagerD)app.getGuiManager()).showView(App.VIEW_SPREADSHEET)) {
			list.add(app.getPlain("Spreadsheet"));
		}
		if (((GuiManagerD)app.getGuiManager()).showView(App.VIEW_EUCLIDIAN)) {
			list.add(app.getPlain("DrawingPad"));
		}
		if (((GuiManagerD)app.getGuiManager()).showView(App.VIEW_EUCLIDIAN2)) {
			list.add(app.getPlain("DrawingPad2"));
		}
		if (((GuiManagerD)app.getGuiManager()).showView(App.VIEW_CONSTRUCTION_PROTOCOL)) {
			list.add(app.getPlain("ConstructionProtocol"));
		}
		if (((GuiManagerD)app.getGuiManager()).showView(App.VIEW_DATA_ANALYSIS)) {
			list.add(app.getPlain("DataAnalysis"));
		}

		list.add(app.getPlain("AllViews"));

		String[] s = new String[list.size()];
		list.toArray(s);

		return s;
	}

	public JPanel createPanelForScaling2() {
		// scale panel to set scale of x-axis in cm
		PrintScalePanel2 scalePanel = new PrintScalePanel2(app,
				(ScalingPrintGridable) m_target);

		JPanel retPanel = new JPanel();
		retPanel.setLayout(new BoxLayout(retPanel, BoxLayout.X_AXIS));
		retPanel.setBorder(BorderFactory.createEtchedBorder());
		retPanel.add(Box.createHorizontalStrut(10));
		retPanel.add(scalePanel);
		return retPanel;
	}

	public JPanel createPanelForScaling() {
		// checkbox to turn on/off printing of scale string
		final JCheckBox cbEVscalePanel = new JCheckBox();
		cbEVscalePanel.setSelected(app.isPrintScaleString());
		cbEVscalePanel.addActionListener(lst);
		cbEVscalePanel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				app.setPrintScaleString(cbEVscalePanel.isSelected());
			}
		});

		// scale panel to set scale of x-axis in cm
		PrintScalePanel scalePanel = new PrintScalePanel(app,
				(EuclidianView) m_target);
		scalePanel.addActionListener(lst);

		JPanel retPanel = new JPanel();
		retPanel.setLayout(new BoxLayout(retPanel, BoxLayout.X_AXIS));
		retPanel.setBorder(BorderFactory.createEtchedBorder());
		retPanel.add(Box.createHorizontalStrut(10));
		retPanel.add(cbEVscalePanel);
		retPanel.add(scalePanel);

		return retPanel;
	}

	private void loadPreferences() {
		try {
			// orientation
			String strOrientation = GeoGebraPreferencesD.getPref()
					.loadPreference(GeoGebraPreferencesD.PRINT_ORIENTATION,
							"landscape");
			m_orientation = strOrientation.equals("portrait") ? PageFormat.PORTRAIT
					: PageFormat.LANDSCAPE;

			// show printing scale in cm
			app.setPrintScaleString(Boolean.valueOf(
					GeoGebraPreferencesD.getPref().loadPreference(
							GeoGebraPreferencesD.PRINT_SHOW_SCALE, "false"))
					.booleanValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void savePreferences() {
		// orientation
		String strOrientation;
		switch (m_orientation) {
		case PageFormat.LANDSCAPE:
			strOrientation = "landscape";
			break;
		default:
			strOrientation = "portrait";
		}

		GeoGebraPreferencesD pref = GeoGebraPreferencesD.getPref();
		pref.savePreference(GeoGebraPreferencesD.PRINT_ORIENTATION,
				strOrientation);

		// show printing scale in cm
		pref.savePreference(GeoGebraPreferencesD.PRINT_SHOW_SCALE,
				Boolean.toString(app.isPrintScaleString()));
	}

	@Override
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
		// center on screen
		pack();
		Dimension size = getPreferredSize();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = Math.min(size.width, dim.width);
		int h = Math.min(size.height, (int) (dim.height * 0.9));
		setLocation((dim.width - w) / 2, (dim.height - h) / 2);
		setSize(w, h);
	}

	/**
	 * Sets the orientation of preview and applies current scale.
	 */
	private void initPages() {
		PageFormat pageFormat = getDefaultPageFormat();
		pageFormat.setOrientation(m_orientation);

		if (pageFormat.getWidth() == 0 || pageFormat.getHeight() == 0) {
			App.debug("Unable to determine default page size");
			return;
		}

		int pageIndex = 0;
		while (true) {
			if (pageExists(pageIndex)) {
				PagePreview pp = new PagePreview(m_target, pageFormat,
						pageIndex);
				pp.setScale(m_scale);
				m_preview.add(pp);
			} else
				break;
			pageIndex++;
		}
	}

	private static PageFormat getDefaultPageFormat() {
		PrinterJob prnJob = PrinterJob.getPrinterJob();
		PageFormat pageFormat = prnJob.defaultPage();

		Paper paper = pageFormat.getPaper();
		double width = paper.getWidth();
		double height = paper.getHeight();
		if (width > 0 && height > 0) {
			// set margins
			paper.setImageableArea(AppD.PAGE_MARGIN_X, AppD.PAGE_MARGIN_Y,
					width - 2 * AppD.PAGE_MARGIN_X, height - 2
							* AppD.PAGE_MARGIN_Y);
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
		if (!pageExists(comps.length - 1)) {
			m_preview.remove(comps.length - 1);
			m_preview.doLayout();
			m_preview.getParent().getParent().validate();
		}
		// new page?
		else if (pageExists(comps.length)) {
			PageFormat pageFormat = getDefaultPageFormat();
			pageFormat.setOrientation(m_orientation);
			if (pageFormat.getHeight() == 0 || pageFormat.getWidth() == 0) {
				App.debug("Unable to determine default page size");
				return;
			}
			PagePreview pp = new PagePreview(m_target, pageFormat, comps.length);
			pp.setScale(m_scale);
			m_preview.add(pp);
			m_preview.doLayout();
			m_preview.getParent().getParent().validate();
		}
	}

	public boolean pageExists(int pageIndex) {
		try {
			PageFormat pageFormat = getDefaultPageFormat();
			pageFormat.setOrientation(m_orientation);
			return (m_target.print(tempGraphics, pageFormat, pageIndex) == Printable.PAGE_EXISTS);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void setOrientation(int orientation) {
		m_orientation = orientation;

		m_preview.removeAll();
		initPages();

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
	}

	class PreviewContainer extends JPanel implements Pageable {
		private static final long serialVersionUID = 1L;

		protected int H_GAP = 16;
		protected int V_GAP = 10;

		@Override
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
			return new Dimension(ww + ins.left + ins.right, hh + ins.top
					+ ins.bottom);
		}

		@Override
		public Dimension getMaximumSize() {
			return getPreferredSize();
		}

		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		@Override
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
		 * Pageable interface ***************
		 */

		public int getNumberOfPages() {
			return getComponentCount();
		}

		public PageFormat getPageFormat(int pageIndex)
				throws IndexOutOfBoundsException {
			try {
				return ((PagePreview) getComponent(pageIndex)).getPageFormat();
			} catch (Exception e) {
				throw new IndexOutOfBoundsException();
			}
		}

		public Printable getPrintable(int pageIndex)
				throws IndexOutOfBoundsException {
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
			// update();
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

		@Override
		public Dimension getPreferredSize() {
			Insets ins = getInsets();
			return new Dimension(m_w + ins.left + ins.right, m_h + ins.top
					+ ins.bottom);
		}

		@Override
		public Dimension getMaximumSize() {
			return getPreferredSize();
		}

		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		private void updateBufferedImage() {
			img = new BufferedImage(m_w, m_h, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = img.createGraphics();
			g2.setColor(getBackground());
			g2.fillRect(0, 0, m_w, m_h);
			if (scale != 1.0)
				g2.scale(scale, scale);
			try {
				target.print(g2, format, pageIndex);
			} catch (Exception e) {
			}
		}

		public void update() {
			try {
				updateBufferedImage();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
			repaint();
		}

		@Override
		public void paint(Graphics g) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			g.drawImage(img, 0, 0, this);
			paintBorder(g);
		}
	}
}
