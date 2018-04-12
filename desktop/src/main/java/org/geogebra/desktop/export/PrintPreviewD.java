/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.export;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.euclidian.EuclidianViewD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.TitlePanel;
import org.geogebra.desktop.gui.layout.DockManagerD;
import org.geogebra.desktop.gui.layout.DockPanelD;
import org.geogebra.desktop.gui.view.Gridable;
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolViewD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.GeoGebraPreferencesD;
import org.geogebra.desktop.util.GuiResourcesD;

public class PrintPreviewD extends JDialog {

	private static final long serialVersionUID = 1L;

	protected int m_wPage;
	protected int m_hPage;
	protected int m_orientation;
	protected int m_scale;
	protected List<Printable> m_target;
	@SuppressWarnings("rawtypes")
	protected JComboBox m_cbScale, m_cbOrientation, m_cbView;
	// protected JCheckBox cbEVscalePanel;
	protected JScrollPane ps;
	protected PreviewContainer m_preview;
	protected final AppD app;
	protected JPanel tempPanel, panelForTitleAndScaling; // used for title and
															// scaling of
															// graphics view's
															// print preview
	protected transient ActionListener lst;

	protected boolean kernelChanged = false;
	private boolean justPreview = true;
	private int[] targetPages;

	private Book book;

	static Graphics tempGraphics;
	static {
		BufferedImage img = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
		tempGraphics = img.getGraphics();
	}

	public static PrintPreviewD get(AppD app, int viewID, int orientation) {

		PrintPreviewD ret = new PrintPreviewD(app);
		ret.m_target = getPrintables(viewID, app);
		ret.m_orientation = orientation;
		ret.initPrintPreview();
		return ret;
	}

	static List<Printable> getPrintables(int viewID, AppD app) {
		GuiManagerD gui = (GuiManagerD) app.getGuiManager();
		if (viewID == App.VIEW_CAS) {

			return wrap(gui.getCasView());
		} else if (viewID == App.VIEW_CONSTRUCTION_PROTOCOL) {
			return (wrap((ConstructionProtocolViewD) app.getGuiManager()
					.getConstructionProtocolView()));
		} else if (viewID == App.VIEW_SPREADSHEET) {
			return wrap(gui.getSpreadsheetView());
		} else if (viewID == App.VIEW_EUCLIDIAN2) {
			return wrap(app.getEuclidianView2(1));
		} else if (viewID == App.VIEW_ALGEBRA) {
			return wrap(gui.getAlgebraView());
		} else if (viewID == App.VIEW_DATA_ANALYSIS) {
			return wrap(gui.getDataAnalysisView());
		}
		// if there is no view in focus (e.g. just closed the
		// focused view),
		// it prints the GeoGebra main window
		else {
			return wrap(app.getEuclidianView1());
		}

	}

	public PrintPreviewD(AppD app) {
		// modal=true: user shouldn't be able to
		// change anything before actual print
		// happened.
		super(app.getFrame(), true);
		this.app = app;
		app.setPrintPreview(this);

	}

	private static List<Printable> wrap(Gridable target) {
		List<Printable> list = new ArrayList<>();
		list.add(new PrintGridable(target));
		return list;
	}

	private static List<Printable> wrap(Printable mainComponent) {
		ArrayList<Printable> list = new ArrayList<>();
		list.add(mainComponent);
		return list;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initPrintPreview() {
		final Localization loc = app.getLocalization();
		m_scale = 75; // init scale to 75%

		loadPreferences();

		setTitle(loc.getMenu("PrintPreview"));
		Cursor oldCursor = app.getMainComponent().getCursor();
		app.getMainComponent()
				.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		getContentPane().setLayout(new BorderLayout());

		// print button
		JButton btnPrint = new JButton(loc.getMenu("Print"),
				app.getScaledIcon(GuiResourcesD.DOCUMENT_PRINT));
		lst = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						try {
							PrinterJob prnJob = PrinterJob.getPrinterJob();
							prnJob.setPageable(book);

							if (!prnJob.printDialog()) {
								return;
							}
							setCursor(Cursor
									.getPredefinedCursor(Cursor.WAIT_CURSOR));
							justPreview = false;
							prnJob.print();
							justPreview = true;
							setCursor(Cursor.getPredefinedCursor(
									Cursor.DEFAULT_CURSOR));
							setVisible(false);
						} catch (PrinterException ex) {
							ex.printStackTrace();
							Log.debug("Printing error: " + ex.toString());
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
		String[] scales = { "10%", "25%", "50%", "75%", "100%", "150%",
				"200%" };
		m_cbScale = new JComboBox(scales);
		m_cbScale.setSelectedItem(m_scale + "%");
		lst = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						setCursor(
								Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						String str = m_cbScale.getSelectedItem().toString();
						if (str.endsWith("%")) {
							str = str.substring(0, str.length() - 1);
						}
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
		String[] orients = { loc.getMenu("Portrait"),
				loc.getMenu("Landscape") };
		m_cbOrientation = new JComboBox(orients);
		m_cbOrientation.setSelectedIndex(
				(m_orientation == PageFormat.PORTRAIT) ? 0 : 1);

		lst = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						setCursor(
								Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						int pageOrientation = (m_cbOrientation
								.getSelectedIndex() == 0) ? PageFormat.PORTRAIT
										: PageFormat.LANDSCAPE;

						setOrientation(pageOrientation);

						PrintPreviewD prev = PrintPreviewD.this;
						int width = prev.getPreferredSize().width;
						if (width > prev.getWidth()) {
							setSize(width, prev.getHeight());
						}
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

		DockPanelD focusedPanel = ((GuiManagerD) app.getGuiManager())
				.getLayout().getDockManager().getFocusedPanel();
		if (focusedPanel == null) {
			m_cbView.setSelectedItem(loc.getMenu("AllViews"));
		} else {
			m_cbView.setSelectedItem(loc.getMenu(focusedPanel.getViewTitle()));
		}

		ActionListener lst_view = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						setCursor(
								Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						m_preview.removeAll();

						final String selItem = m_cbView.getSelectedItem()
								.toString();
						// change view
						if (selItem.equals(loc.getMenu("AllViews"))) {
							final List<Printable> l = new ArrayList<>();
							app.forEachView(new App.ViewCallback() {

								@Override
								public void run(int viewID, String viewName) {

									l.addAll(getPrintables(viewID, app));// TODO

								}
							});

							m_target = l;
						} else {
							m_target = new ArrayList<>();
							app.forEachView(new App.ViewCallback() {

								@Override
								public void run(int viewID, String viewName) {

									if (selItem.equals(loc.getMenu(viewName))) {
										m_target.addAll(
												getPrintables(viewID, app));
									}

								}
							});
						}
						tempPanel.removeAll();
						if (selItem.equals(loc.getMenu("DrawingPad"))
								|| selItem.equals(loc.getMenu("AllViews"))) {
							tempPanel.add(createPanelForScaling(
									app.getEuclidianView1()));
						}
						if (selItem.equals(loc.getMenu("DrawingPad2"))
								|| (selItem.equals(loc.getMenu("AllViews"))
										&& app.hasEuclidianView2(1))) {
							tempPanel.add(createPanelForScaling(
									app.getEuclidianView2(1)));
						}
						panelForTitleAndScaling.revalidate();

						initPages();
						updateFormat();

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
		buttonPanel.add(westPanel, app.getLocalization().borderWest());

		// title
		TitlePanel titlePanel = new TitlePanel(app);
		lst = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				kernelChanged = true;
				Thread runner = new Thread() {
					@Override
					public void run() {
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								setCursor(Cursor.getPredefinedCursor(
										Cursor.WAIT_CURSOR));
								updatePages();
								setCursor(Cursor.getDefaultCursor());

							}
						});

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
		EuclidianViewD ev = app.getEuclidianView1();
		EuclidianViewD ev2 = app.getEuclidianView2(1);
		// CASView cas = app.getca
		app.getSelectionManager().clearSelectedGeos();

		tempPanel = new JPanel(new GridLayout(0, 1));
		if (m_target.contains(ev)) {
			tempPanel.add(createPanelForScaling(ev));
		}
		if (m_target.contains(ev2)) {
			tempPanel.add(createPanelForScaling(ev2));
		}
		// HACK: m_target gives no information about the current view
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
		updateFormat();
		centerOnScreen();

		app.getMainComponent().setCursor(oldCursor);
	}

	private String[] getAvailableViews() {
		final ArrayList<String> list = new ArrayList<>();
		final Localization loc = app.getLocalization();
		app.forEachView(new App.ViewCallback() {

			@Override
			public void run(int viewID, String viewName) {
				list.add(loc.getMenu(viewName));

			}
		});
		list.add(loc.getMenu("AllViews"));

		String[] s = new String[list.size()];
		list.toArray(s);

		return s;
	}

	public JPanel createPanelForScaling(final EuclidianViewD view) {
		// checkbox to turn on/off printing of scale string
		final JCheckBox cbEVscalePanel = new JCheckBox();
		cbEVscalePanel.setSelected(view.isPrintScaleString());
		cbEVscalePanel.addActionListener(lst);
		cbEVscalePanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				view.setPrintScaleString(cbEVscalePanel.isSelected());
			}
		});

		// scale panel to set scale of x-axis in cm
		PrintScalePanel scalePanel = new PrintScalePanel(app, view);
		scalePanel.enableAbsoluteSize(false);
		scalePanel.addActionListener(lst);

		JPanel retPanel = new JPanel();
		DockPanelD dock;

		dock = ((DockManagerD) app.getGuiManager().getLayout().getDockManager())
				.getPanel(view.getViewID());
		retPanel.add(Box.createHorizontalStrut(10));
		retPanel.add(new JLabel(dock.getIcon()));
		// retPanel.add(new JLabel(loc.getMenu(dock.getViewTitle())));
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
			m_orientation = "portrait".equals(strOrientation)
					? PageFormat.PORTRAIT : PageFormat.LANDSCAPE;

			// show printing scale in cm
			app.getEuclidianView1().setPrintScaleString(Boolean
					.valueOf(GeoGebraPreferencesD.getPref().loadPreference(
							GeoGebraPreferencesD.PRINT_SHOW_SCALE, "false"))
					.booleanValue());
			if (app.hasEuclidianView2EitherShowingOrNot(1)) {
				app.getEuclidianView2(1)
						.setPrintScaleString(Boolean
								.valueOf(GeoGebraPreferencesD.getPref()
										.loadPreference(
												GeoGebraPreferencesD.PRINT_SHOW_SCALE2,
												"false"))
								.booleanValue());
			}
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
				Boolean.toString(app.getEuclidianView1().isPrintScaleString()));
		if (app.hasEuclidianView2EitherShowingOrNot(1)) {
			pref.savePreference(GeoGebraPreferencesD.PRINT_SHOW_SCALE2, Boolean
					.toString(app.getEuclidianView2(1).isPrintScaleString()));
		}
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
	void initPages() {
		PageFormat pageFormat = getDefaultPageFormat();
		pageFormat.setOrientation(m_orientation);

		if (pageFormat.getWidth() == 0 || pageFormat.getHeight() == 0) {
			Log.debug("Unable to determine default page size");
			return;
		}

		int pageIndex = 0;
		int targetIndex = 0;
		book = new Book();
		targetPages = new int[m_target.size()];
		while (true) {
			if (pageExists(targetIndex, pageIndex)) {
				PagePreview pp = new PagePreview(m_target.get(targetIndex),
						pageFormat, pageIndex, targetIndex, app);
				pp.setScale(m_scale);
				m_preview.add(pp);
				// book.append(m_target.get(targetIndex), pageFormat);
			} else {
				book.append(m_target.get(targetIndex), pageFormat, pageIndex);
				targetPages[targetIndex] = pageIndex;
				targetIndex++;
				pageIndex = -1;
			}
			if (targetIndex >= m_target.size()) {
				break;
			}
			pageIndex++;
		}
	}

	public int computePageIndex(int pageIndex0) {
		int pageIndex = pageIndex0;
		for (int i = 0; i < targetPages.length
				&& targetPages[i] <= pageIndex; i++) {
			pageIndex -= targetPages[i];
		}
		return pageIndex;
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
					width - 2 * AppD.PAGE_MARGIN_X,
					height - 2 * AppD.PAGE_MARGIN_Y);
			pageFormat.setPaper(paper);
		}

		return pageFormat;
	}

	// update Pages, add or remove last page if necessary
	void updatePages() {
		// update existing pages
		Component[] comps = m_preview.getComponents();
		int[] lengths = new int[m_target.size()];
		for (int i = 0; i < lengths.length; i++) {
			lengths[i] = 0;
		}

		for (int k = 0; k < comps.length; k++) {
			if (!(comps[k] instanceof PagePreview)) {
				continue;
			}
			PagePreview pp = (PagePreview) comps[k];
			lengths[pp.getTarget()]++;
			pp.update();
		}

		for (int i = 0; i < m_target.size(); i++) {
			// add or remove last page if necessary
			// last page gone?
			if (!pageExists(i, lengths[i] - 1)) {
				m_preview.remove(lengths[i] - 1);
				m_preview.doLayout();
				m_preview.getParent().getParent().validate();
			}
			// new page?
			else if (pageExists(i, lengths[i])) {
				PageFormat pageFormat = getDefaultPageFormat();
				pageFormat.setOrientation(m_orientation);
				if (pageFormat.getHeight() == 0 || pageFormat.getWidth() == 0) {
					Log.debug("Unable to determine default page size");
					return;
				}
				PagePreview pp = new PagePreview(m_target.get(i), pageFormat,
						lengths[i], i, app);
				pp.setScale(m_scale);
				m_preview.add(pp);
				m_preview.doLayout();
				m_preview.getParent().getParent().validate();
			}
		}
	}

	public boolean pageExists(int targetIndex, int pageIndex) {
		try {
			PageFormat pageFormat = getDefaultPageFormat();
			pageFormat.setOrientation(m_orientation);
			return (m_target.get(targetIndex).print(tempGraphics, pageFormat,
					pageIndex) == Printable.PAGE_EXISTS);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	void setOrientation(int orientation) {
		m_orientation = orientation;

		m_preview.removeAll();
		initPages();

		updateFormat();
		m_preview.doLayout();
		m_preview.getParent().getParent().validate();
	}

	void updateFormat() {
		PageFormat pageFormat = getDefaultPageFormat();
		pageFormat.setOrientation(m_orientation);

		Component[] comps = m_preview.getComponents();
		for (int k = 0; k < comps.length; k++) {
			if (!(comps[k] instanceof PagePreview)) {
				continue;
			}
			PagePreview pp = (PagePreview) comps[k];
			pp.setPageFormat(pageFormat);
		}

	}

	void setScale(int scale) {
		m_scale = scale;

		Component[] comps = m_preview.getComponents();
		for (int k = 0; k < comps.length; k++) {
			if (!(comps[k] instanceof PagePreview)) {
				continue;
			}
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
			if (n == 0) {
				return new Dimension(H_GAP, V_GAP);
			}
			Component comp = getComponent(0);
			Dimension dc = comp.getPreferredSize();
			int w = dc.width;
			int h = dc.height;

			Dimension dp = getParent().getSize();
			int nCol = Math.max((dp.width - H_GAP) / (w + H_GAP), 1);
			int nRow = n / nCol;
			if (nRow * nCol < n) {
				nRow++;
			}

			int ww = nCol * (w + H_GAP) + H_GAP;
			int hh = nRow * (h + V_GAP) + V_GAP;
			Insets ins = getInsets();
			return new Dimension(ww + ins.left + ins.right,
					hh + ins.top + ins.bottom);
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
			if (n == 0) {
				return;
			}
			Component comp = getComponent(0);
			Dimension dc = comp.getPreferredSize();
			int w = dc.width;
			int h = dc.height;

			Dimension dp = getParent().getSize();
			int nCol = Math.max((dp.width - H_GAP) / (w + H_GAP), 1);
			int nRow = n / nCol;
			if (nRow * nCol < n) {
				nRow++;
			}

			int index = 0;
			for (int k = 0; k < nRow; k++) {
				for (int m = 0; m < nCol; m++) {
					if (index >= n) {
						return;
					}
					comp = getComponent(index++);
					comp.setBounds(x, y, w, h);
					x += w + H_GAP;
				}
				y += h + V_GAP;
				x = ins.left + H_GAP;
			}
		}

		/*
		 * **************** Pageable interface ***************
		 */

		@Override
		public int getNumberOfPages() {
			return getComponentCount();
		}

		@Override
		public PageFormat getPageFormat(int pageIndex)
				throws IndexOutOfBoundsException {
			try {
				return ((PagePreview) getComponent(pageIndex)).getPageFormat();
			} catch (Exception e) {
				throw new IndexOutOfBoundsException();
			}
		}

		@Override
		public Printable getPrintable(int pageIndex)
				throws IndexOutOfBoundsException {
			return m_target.get(0);
		}
	}

	public int adjustIndex(int pageIndex0) {
		if (!justPreview) {
			return computePageIndex(pageIndex0);
		}
		return pageIndex0;
	}
}
