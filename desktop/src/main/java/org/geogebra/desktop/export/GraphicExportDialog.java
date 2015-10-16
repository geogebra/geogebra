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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.AbstractVectorGraphicsIO;
import org.freehep.graphicsio.FontConstants;
import org.freehep.graphicsio.emf.EMFGraphics2D;
import org.freehep.graphicsio.emf.EMFPlusGraphics2D;
import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.freehep.graphicsio.svg.SVGGraphics2D;
import org.freehep.util.UserProperties;
import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.Unicode;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.euclidian.EuclidianViewD;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceDesktop;
import org.geogebra.desktop.export.epsgraphics.ColorMode;
import org.geogebra.desktop.gui.util.FileTransferable;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.GeoGebraPreferencesD;
import org.geogebra.desktop.util.DownloadManager;
import org.geogebra.desktop.util.Util;


/**
 * @author Markus Hohenwarter
 */
public class GraphicExportDialog extends JDialog implements KeyListener {

	// #4979
	private static final int EXTRA_MARGIN = 4;

	private static final long serialVersionUID = 1L;

	private final AppD app;
	@SuppressWarnings("rawtypes")
	private JComboBox cbFormat;

	/**
	 * Combobox for DPI
	 */
	@SuppressWarnings("rawtypes")
	JComboBox cbDPI;
	private JLabel sizeLabel;
	private JButton cancelButton;

	private double exportScale;
	private int pixelWidth, pixelHeight;
	private double cmWidth, cmHeight;
	private final NumberFormat sizeLabelFormat;

	/** convert text to shapes (eps, pdf, svg) */
	boolean textAsShapes = true;
	/** true for transparent images (png) */
	boolean transparent = true;
	/** whether EMF+ is used or EMF */
	boolean EMFPlus = true;

	private enum Format {
		PNG, PDF, EPS, SVG, EMF
	}

	private EuclidianViewD specifiedEuclidianView;

	/** print scale or pixel size settings */
	PrintScalePanel psp;

	/**
	 * Creates a dialog for exporting an image of the active EuclidianView
	 * 
	 * @param app
	 *            application
	 */
	public GraphicExportDialog(AppD app) {
		this(app, null);

	}

	/**
	 * Creates a dialog for exporting an image of the EuclidianView given as a
	 * parameter.
	 * 
	 * @param app
	 *            application
	 * @param specifiedEuclidianView
	 *            EV
	 */
	public GraphicExportDialog(AppD app, EuclidianViewD specifiedEuclidianView) {
		super(app.getFrame(), false);
		this.app = app;
		this.specifiedEuclidianView = specifiedEuclidianView;

		sizeLabelFormat = NumberFormat.getInstance(Locale.ENGLISH);
		sizeLabelFormat.setGroupingUsed(false);
		sizeLabelFormat.setMaximumFractionDigits(2);

		initGUI();
	}

	private EuclidianViewInterfaceDesktop getEuclidianView() {
		if (specifiedEuclidianView != null) {
			return specifiedEuclidianView;
		}
		return (EuclidianViewInterfaceDesktop) app.getActiveEuclidianView();
	}

	@Override
	public void setVisible(boolean flag) {
		if (flag) {
			loadPreferences();
			super.setVisible(true);
		} else {
			savePreferences();
			super.setVisible(false);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initGUI() {
		setResizable(false);
		setTitle(app.getPlain("ExportAsPicture"));
		JPanel cp = new JPanel(new BorderLayout(5, 5));
		cp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().add(cp);

		// format list
		JPanel formatPanel = new JPanel(new FlowLayout(5));
		String[] formats;
		
		if (((EuclidianView) getEuclidianView()).isEuclidianView3D()){
			formats = new String[] {
					app.getPlain("png") + " (" + FileExtensions.PNG + ")" };
		}else{
			formats = new String[] {
					app.getPlain("png") + " (" + FileExtensions.PNG + ")",
					app.getPlain("pdf") + " (" + FileExtensions.PDF + ")",
					app.getPlain("eps") + " (" + FileExtensions.EPS + ")",
					app.getPlain("svg") + " (" + FileExtensions.SVG + ")",
					app.getPlain("emf") + " (" + FileExtensions.EMF + ")" };
		}

		cbFormat = new JComboBox(formats);
		formatPanel.add(new JLabel(app.getPlain("Format") + ":"));
		formatPanel.add(cbFormat);
		cp.add(formatPanel, BorderLayout.NORTH);

		// panel with fields to enter
		// scale of image, dpi and
		// width and height of picture
		final JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		// scale
		EuclidianView ev = (EuclidianView) getEuclidianView();

		psp = new PrintScalePanel(app, ev);
		psp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateSizeLabel();
			}
		});
		p.add(psp);

		// dpi combo box
		final JPanel dpiPanel = new JPanel(new FlowLayout(5));

		String[] dpiStr = { "72", "96", "150", "300", "600" };
		cbDPI = new JComboBox(dpiStr);
		cbDPI.setSelectedItem("300");
		final JLabel resolutionInDPILabel = new JLabel(
				app.getPlain("ResolutionInDPI") + ":");
		final JCheckBox cbTransparent = new JCheckBox(
				app.getMenu("Transparent"), transparent);
		final JCheckBox cbEMFPlus = new JCheckBox(app.getMenu("EMFPlus"),
				EMFPlus);

		final JCheckBox textAsShapesCB = new JCheckBox(
				app.getPlain("ExportTextAsShapes"), textAsShapes);

		// make sure panel is wide enough
		if (selectedFormat() == Format.PNG) {
			dpiPanel.add(resolutionInDPILabel);
			dpiPanel.add(cbDPI);
			dpiPanel.add(cbTransparent);
		} else if (selectedFormat() == Format.SVG) {
			dpiPanel.add(cbTransparent);
		} else if (selectedFormat() == Format.EMF) {
			dpiPanel.add(cbEMFPlus);
		} else {
			dpiPanel.add(textAsShapesCB);
		}

		p.add(dpiPanel);
		cbDPI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				updateSizeLabel();
			}
		});

		cbTransparent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				transparent = cbTransparent.isSelected();
			}
		});

		cbEMFPlus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EMFPlus = cbEMFPlus.isSelected();
			}
		});

		textAsShapesCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textAsShapes = textAsShapesCB.isSelected();
			}
		});

		cbFormat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textAsShapesCB.setEnabled(true);
				switch (selectedFormat()) {
				case SVG:
					dpiPanel.remove(resolutionInDPILabel);
					dpiPanel.remove(cbDPI);
					dpiPanel.remove(cbEMFPlus);
					dpiPanel.add(cbTransparent);
					dpiPanel.add(textAsShapesCB);
					psp.enableAbsoluteSize(true);
					break;
				case EPS:
					textAsShapesCB.setEnabled(false);
					// fall through
				case PDF:
					dpiPanel.remove(resolutionInDPILabel);
					dpiPanel.remove(cbDPI);
					dpiPanel.remove(cbEMFPlus);
					dpiPanel.remove(cbTransparent);
					dpiPanel.add(textAsShapesCB);
					textAsShapesCB.setSelected(true);
					psp.enableAbsoluteSize(false);
					break;
				case EMF:
					dpiPanel.add(cbEMFPlus);
					dpiPanel.remove(resolutionInDPILabel);
					dpiPanel.remove(cbDPI);
					dpiPanel.remove(cbTransparent);
					dpiPanel.remove(textAsShapesCB);
					psp.enableAbsoluteSize(false);
					break;
				default: // PNG
					dpiPanel.add(resolutionInDPILabel);
					dpiPanel.add(cbDPI);
					dpiPanel.remove(cbEMFPlus);
					dpiPanel.add(cbTransparent);
					dpiPanel.remove(textAsShapesCB);
					cbDPI.setSelectedItem("300");
					cbDPI.setEnabled(true);
					psp.enableAbsoluteSize(true);
					break;
				}
				updateSizeLabel();
				SwingUtilities.updateComponentTreeUI(p);
			}
		});

		// width and height of picture
		JPanel sizePanel = new JPanel(new FlowLayout(5));
		sizePanel.add(new JLabel(app.getPlain("Size") + ":"));
		sizeLabel = new JLabel();
		sizePanel.add(sizeLabel);
		p.add(sizePanel);
		cp.add(p, BorderLayout.CENTER);

		// Cancel and Export Button
		cancelButton = new JButton(app.getPlain("Cancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		JButton exportButton = new JButton(app.getMenu("Save"));
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						setVisible(false);

						doExport(false);
					}
				};
				runner.start();
			}
		});

		JButton exportClipboardButton = new JButton(app.getMenu("Clipboard"));
		exportClipboardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						setVisible(false);

						doExport(true);
					}
				};
				runner.start();
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(exportButton);
		/*
		 * This is done via copying a file to the clipboard. This is not
		 * supported on Mac.
		 * Toolkit.getDefaultToolkit().getSystemClipboard().setContents
		 * (geogebra.gui.util.ImageSelection, null) would be OK (via
		 * AppD.simpleExportToClipboard(EuclidianView)), but this code here
		 * wants to use
		 * Toolkit.getDefaultToolkit().getSystemClipboard().setContents
		 * (FileTransferable, null) via sendToClipboard(File). So we simply not
		 * show the "Clipboard" button here on Mac.
		 */
		if (!app.isMacOS()) {
			buttonPanel.add(exportClipboardButton);
		}
		buttonPanel.add(cancelButton);
		cp.add(buttonPanel, BorderLayout.SOUTH);

		Util.addKeyListenerToAll(this, this);

		updateSizeLabel();
		setPreferredSize(new Dimension(getPreferredSize().width
				+ app.getFontSize() * EXTRA_MARGIN, getPreferredSize().height
				+ app.getFontSize() * EXTRA_MARGIN));
		centerOnScreen();

	}

	/**
	 * Creates the picture of the correct format
	 * 
	 * @param toClipboard
	 *            whether output is a file or clipboard
	 */
	void doExport(boolean toClipboard) {
		Format index = selectedFormat();
		switch (index) {
		case PNG: // PNG
			exportPNG(toClipboard);
			break;

		case EPS: // EPS
			exportEPS(toClipboard);
			break;

		case EMF: // EMF
			exportEMF(toClipboard, EMFPlus);
			break;

		case PDF: // PDF
			exportPDF(toClipboard);
			break;

		case SVG: // SVG
			exportSVG(toClipboard);
			break;

		}
	}

	private int getDPI() {
		if (selectedFormat() == Format.EPS || selectedFormat() == Format.SVG) {
			return 72;
		}

		return Integer.parseInt((String) cbDPI.getSelectedItem());
	}

	/**
	 * Select a dpi item in the
	 * 
	 * @param dpi
	 *            must be one of 72,96,150,300,600
	 */
	public void setDPI(String dpi) {
		cbDPI.setSelectedItem(dpi);
	}

	private void loadPreferences() {
		try {
			// format
			Format formatID = Format.PNG;
			String format = GeoGebraPreferencesD.getPref().loadPreference(
					GeoGebraPreferencesD.EXPORT_PIC_FORMAT, "png");
			if (format.equals("eps")) {
				formatID = Format.EPS;
			} else if (format.equals("svg")) {
				formatID = Format.SVG;
			}
			cbFormat.setSelectedIndex(formatID.ordinal());

			// dpi
			if (cbDPI.isEnabled()) {
				String strDPI = GeoGebraPreferencesD.getPref().loadPreference(
						GeoGebraPreferencesD.EXPORT_PIC_DPI, "300");
				for (int i = 0; i < cbDPI.getItemCount(); i++) {
					String dpi = cbDPI.getItemAt(i).toString();
					if (dpi.equals(strDPI)) {
						cbDPI.setSelectedIndex(i);
					}
				}
			}

			/*
			 * // scale in cm double scale =
			 * Double.parseDouble(GeoGebraPreferences.loadPreference(
			 * GeoGebraPreferences.EXPORT_PIC_SCALE, "1"));
			 * app.getEuclidianView().setPrintingScale(scale);
			 */

			updateSizeLabel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void savePreferences() {
		// dpi
		GeoGebraPreferencesD.getPref().savePreference(
				GeoGebraPreferencesD.EXPORT_PIC_DPI,
				cbDPI.getSelectedItem().toString());

		// format
		String format;
		switch (selectedFormat()) {
		case EPS:
			format = "eps";
			break;
		case SVG:
			format = "svg";
			break;
		default:
			format = "png";
		}
		GeoGebraPreferencesD.getPref().savePreference(
				GeoGebraPreferencesD.EXPORT_PIC_FORMAT, format);

		/*
		 * // scale in cm
		 * GeoGebraPreferences.savePreference(GeoGebraPreferences.
		 * EXPORT_PIC_SCALE,
		 * Double.toString(app.getEuclidianView().getPrintingScale()));
		 */
	}

	/**
	 * Update the pixel size
	 */
	void updateSizeLabel() {
		EuclidianView ev = (EuclidianView) getEuclidianView();
		double printingScale = ev.getPrintingScale();
		// takes dpi into account (note: eps has 72dpi)

		StringBuilder sb = new StringBuilder();
		switch (psp.getMode()) {
		case SIZEINPX:
			pixelWidth = psp.getPixelWidth();
			pixelHeight = psp.getPixelHeight();
			cmWidth = (pixelWidth * 2.54) / getDPI();
			cmHeight = (pixelHeight * 2.54) / getDPI();
			exportScale = pixelWidth / ((double) ev.getExportWidth());
			break;

		case FIXED_SIZE:

			// what the user typed in the "100 screen pixels = x cm" textfield
			double screenPixels = Kernel.checkDecimalFraction(100
					* ev.getPrintingScale() / ev.getXscale());

			double screenPixelsY = 100 * ev.getPrintingScale() / ev.getYscale();

			cmWidth = ev.getExportWidth() / 100.0 * screenPixels;
			// not screenPixelsY
			// eg http://forum.geogebra.org/viewtopic.php?f=8&t=38682
			cmHeight = ev.getExportHeight() / 100.0 * screenPixels;

			pixelWidth = (int) (cmWidth / 2.54 * getDPI());
			pixelHeight = (int) (cmHeight / 2.54 * getDPI());

			exportScale = pixelWidth / ((double) ev.getExportWidth());

			break;

		case SIZEINCM:
			exportScale = (printingScale * getDPI()) / 2.54 / ev.getXscale();
			// cm size
			cmWidth = printingScale
					* (ev.getExportWidth() / ev.getXscale());

			// getXscale() is not a typo, see #2894
			// #4185 changed back to getYscale()
			// * ev.getYscale() / ev.getXscale() added for when x:y ratio is not
			// 1:1
			// http://forum.geogebra.org/viewtopic.php?f=8&t=38682
			cmHeight = printingScale * (ev.getExportHeight() / ev.getYscale())
					* ev.getYscale() / ev.getXscale();

			pixelWidth = (int) Math.floor(ev.getExportWidth()
					* exportScale);
			pixelHeight = (int) Math.floor(ev.getExportHeight()
					* exportScale);
			break;
		}

		sb.append(sizeLabelFormat.format(cmWidth));
		sb.append(" cm ");
		sb.append(Unicode.MULTIPLY);
		sb.append(' ');
		sb.append(sizeLabelFormat.format(cmHeight));
		sb.append(" cm");

		Format index = selectedFormat();
		if (index == Format.PNG || index == Format.SVG) {
			// pixel size
			sb.append(", ");
			sb.append(pixelWidth);
			sb.append(' ');
			sb.append(Unicode.MULTIPLY);
			sb.append(' ');
			sb.append(pixelHeight);
			sb.append(" pixels");
			sb.append(Unicode.Superscript_2);
		}

		sizeLabel.setText(sb.toString());
	}

	/**
	 * @return curently selected format
	 */
	Format selectedFormat() {
		return Format.values()[cbFormat.getSelectedIndex()];
	}

	private void centerOnScreen() {
		// center on screen
		pack();
		setLocationRelativeTo(app.getMainComponent());
	}

	/**
	 * Shows save dialog and exports drawing as eps.
	 */
	final private boolean exportEPS(final boolean exportToClipboard) {

		final EuclidianView ev = (EuclidianView) getEuclidianView();

		File file;
		if (exportToClipboard) {
			final String tempDir = DownloadManager.getTempDir();
			// os = new ByteArrayOutputStream();
			// use file to get the correct filetype (so eg pasting into Word
			// works)
			// NB pasting into WordPad *won't* work with this method
			file = new File(tempDir + "geogebra.eps");
		} else {
			file = app.getGuiManager().showSaveDialog(FileExtensions.EPS, null,
					app.getPlain("eps") + " " + app.getMenu("Files"), true,
					false);

			if (file == null) {
				return false;
			}
		}
		try {

			exportEPS(app, (EuclidianViewD) ev, file, exportToClipboard, pixelWidth,
					pixelHeight, exportScale);

			if (exportToClipboard) {
				sendToClipboard(file);
			}

			return true;
		} catch (final Exception ex) {
			app.showError("SaveFileFailed");
			App.debug(ex.toString());
			return false;
		}
	}

	/**
	 * Exports drawing as emf
	 */
	final private boolean exportEMF(boolean exportToClipboard,
			boolean useEMFplus) {

		// Michael Borcherds 2008-03-02 BEGIN
		File file;
		String tempDir = DownloadManager.getTempDir();
		if (exportToClipboard) {
			file = new File(tempDir + "geogebra.emf");
		} else {
			file = app.getGuiManager().showSaveDialog(FileExtensions.EMF, null,
					app.getPlain("emf") + " " + app.getMenu("Files"), true,
					false);
			// Michael Borcherds 2008-03-02 END
		}

		if (file == null) {
			return false;
		}
		try {
			exportEMF(app, (EuclidianViewD) getEuclidianView(), file, useEMFplus, pixelWidth,
					pixelHeight, exportScale);

			if (exportToClipboard) {
				sendToClipboard(file); // Michael Borcherds 2008-03-02 END
			}

			return true;
		} catch (Exception ex) {
			app.showError("SaveFileFailed");
			App.debug(ex.toString());
			return false;
		} catch (Error ex) {
			app.showError("SaveFileFailed");
			App.debug(ex.toString());
			return false;
		}
	}

	/**
	 * Exports drawing as pdf
	 */
	final private boolean exportPDF(boolean exportToClipboard) {
		// Michael Borcherds 2008-03-02 BEGIN
		File file;
		String tempDir = DownloadManager.getTempDir();
		if (exportToClipboard) {
			file = new File(tempDir + "geogebra.pdf");
		} else {
			// Michael Borcherds 2008-03-02 END
			file = app.getGuiManager().showSaveDialog(FileExtensions.PDF, null,
					app.getPlain("pdf") + " " + app.getMenu("Files"), true,
					false);
		}

		if (file == null) {
			return false;
		}
		try {

			exportPDF(app, (EuclidianViewD) getEuclidianView(), file, textAsShapes, pixelWidth,
					pixelHeight, exportScale);

			if (exportToClipboard) {
				sendToClipboard(file); // Michael Borcherds 2008-03-02 END
			}

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			app.showError("SaveFileFailed");

			return false;
		} catch (Error ex) {
			ex.printStackTrace();
			app.showError("SaveFileFailed");

			return false;
		}
	}

	/**
	 * Exports drawing as SVG
	 */
	final private boolean exportSVG(boolean exportToClipboard) {

		EuclidianView ev = (EuclidianView) getEuclidianView();

		File file;
		String tempDir = DownloadManager.getTempDir();
		if (exportToClipboard) {
			file = new File(tempDir + "geogebra.svg");
		} else {
			file = app.getGuiManager().showSaveDialog(FileExtensions.SVG, null,
					app.getPlain("svg") + " " + app.getMenu("Files"), true,
					false);
		}

		if (file == null) {
			return false;
		}
		ev.setTemporaryCoordSystemForExport(); // allow clipping with Export_1
												// and 2 Points
		try {
			exportSVG(app, (EuclidianViewD) ev, file, textAsShapes, pixelWidth,
					pixelHeight, cmWidth, cmHeight,
					exportScale, transparent);

			if (exportToClipboard) {
				// note this *doesn't* copy as text
				// so ctrl-V in notepad won't show anything
				sendToClipboard(file);
			}

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			app.showError("SaveFileFailed");
			App.debug(ex.toString());
			return false;
		} catch (Error ex) {
			app.showError("SaveFileFailed");
			App.debug(ex.toString());
			return false;
		} finally {
			ev.restoreOldCoordSystem();
		}
	}

	/**
	 * Exports drawing as png with given resolution in dpi
	 * 
	 * @param exportToClipboard
	 *            whether to use clipboard or not
	 * @return whether succesful
	 */
	final public boolean exportPNG(boolean exportToClipboard) {
		File file;
		String tempDir = DownloadManager.getTempDir();
		if (exportToClipboard) {
			file = new File(tempDir + "geogebra.png");
		} else {
			file = app.getGuiManager().showSaveDialog(FileExtensions.PNG, null,
					app.getPlain("png") + " " + app.getMenu("Files"), true,
					false);
		}

		if (file == null) {
			return false;
		}

		try {
			// draw graphics view into image
			EuclidianViewInterfaceDesktop ev = getEuclidianView();

			exportPNG(ev, file, transparent, getDPI(), exportScale, exportToClipboard);


			return true;
		} catch (Exception ex) {
			app.showError("SaveFileFailed");
			App.debug(ex.toString());
			return false;
		} catch (Error ex) {
			app.showError("SaveFileFailed");
			App.debug(ex.toString());
			return false;
		}
	}

	/*
	 * Keylistener implementation of PropertiesDialog
	 */

	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_ESCAPE) {
			setVisible(false);
		}
	}

	public void keyReleased(KeyEvent e) {
		//
	}

	public void keyTyped(KeyEvent e) {
		//
	}

	public static void sendToClipboard(File file) {
		FileTransferable ft = new FileTransferable(file);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, null);
	}

	/**
	 * 
	 * @param app
	 *            application
	 * @param ev
	 *            view
	 * @param file
	 *            target file
	 * @param textAsShapes
	 *            whether to convert text to curves
	 * @param pixelWidth
	 *            width in pixels
	 * @param pixelHeight
	 *            height in pixels
	 * @param cmWidth
	 *            width in cm
	 * @param cmHeight
	 *            height in cm
	 * @param exportScale
	 *            scale units / cm
	 * @param transparent0
	 *            transparent?
	 */
	public static void exportSVG(AppD app, EuclidianViewD ev, File file,
			boolean textAsShapes, int pixelWidth, int pixelHeight,
			double cmWidth, double cmHeight,
			double exportScale, boolean transparent0) {
		UserProperties props = (UserProperties) SVGGraphics2D
				.getDefaultProperties();
		props.setProperty(SVGGraphics2D.EMBED_FONTS, !textAsShapes);
		props.setProperty(AbstractVectorGraphicsIO.TEXT_AS_SHAPES, textAsShapes);
		SVGGraphics2D.setDefaultProperties(props);

		// added SVGExtensions to support grouped objects in layers
		SVGExtensions g;
		try {
			g = new SVGExtensions(file, new Dimension(
					(int) (pixelWidth / exportScale),
					(int) (pixelHeight / exportScale)), cmWidth, cmHeight);
			// make sure LaTeX exported at hi res
			app.setExporting(ExportType.SVG);

			g.startExport();
			// export scale = 1 (change for SVG export in cm)
			// and avoid lots of these in the file
			// <g
			// transform="matrix(.56693, 0.0000, 0.0000, .56693, 0.0000, 0.0000)">
			ev.exportPaintPre(new GGraphics2DD(g), 1, transparent0);

			g.startGroup("misc");
			ev.drawActionObjects(new GGraphics2DD(g));
			g.endGroup("misc");

			for (int layer = 0; layer <= app.getMaxLayerUsed(); layer++) // draw
			// only
			// layers
			// we
			// need
			{
				g.startGroup("layer" + layer);

				// ev.drawLayers[layer].drawAll(new GGraphics2DD(g));
				g.drawAll(ev.drawLayers[layer], new GGraphics2DD(g));

				g.endGroup("layer" + layer);
			}

			g.endExport();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			app.setExporting(ExportType.NONE);
		}
	}

	/**
	 * 
	 * @param app
	 *            application
	 * @param ev
	 *            view
	 * @param file
	 *            target file
	 * @param useEMFplus
	 *            whether to use EMF+
	 * @param pixelWidth
	 *            width in pixels
	 * @param pixelHeight
	 *            height in pixels
	 * @param exportScale
	 *            scale units / cm
	 */
	public static void exportEMF(AppD app, EuclidianViewD ev, File file,
			boolean useEMFplus, int pixelWidth, int pixelHeight,
			double exportScale) {

		VectorGraphics g;
		try {
			if (useEMFplus) {
				g = new EMFPlusGraphics2D(file, new Dimension(pixelWidth,
						pixelHeight));

			} else {
				g = new EMFGraphics2D(file, new Dimension(pixelWidth,
						pixelHeight));
			}

			// fix problem with aspect ratio in eg MS Word
			// https://community.oracle.com/thread/1264130
			g.setDeviceIndependent(true);

			g.startExport();
			ev.exportPaint(g, exportScale, ExportType.EMF);

			g.endExport();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param app
	 *            application
	 * @param ev
	 *            view
	 * @param file
	 *            target file
	 * @param textAsShapes
	 *            whether to convert text to curves
	 * @param pixelWidth
	 *            width in pixels
	 * @param pixelHeight
	 *            height in pixels
	 * @param exportScale
	 *            scale units / cm
	 */
	public static void exportPDF(AppD app, EuclidianViewD ev, File file,
			boolean textAsShapes, int pixelWidth, int pixelHeight,
			double exportScale) {

		ImageIO.scanForPlugins();
		// export text as shapes or plaintext
		// shapes: better representation
		// text: smaller file size, but some unicode symbols don't export eg
		// Upsilon
		UserProperties props = (UserProperties) PDFGraphics2D
				.getDefaultProperties();

		// #TRAC-5292
		props.setProperty(PDFGraphics2D.EMBED_FONTS, !textAsShapes);
		props.setProperty(PDFGraphics2D.EMBED_FONTS_AS,
				FontConstants.EMBED_FONTS_TYPE1);
		props.setProperty(AbstractVectorGraphicsIO.TEXT_AS_SHAPES, textAsShapes);
		PDFGraphics2D.setDefaultProperties(props);

		VectorGraphics g;
		try {

			double printingScale = ev.getPrintingScale();

			// TODO: why do we need this to make correct size in cm?
			double factor = ev.getXscale() * 2.54 / 72;

			// Dimension size = new Dimension(
			// (int)(ev.getExportWidth() * printingScale / factor),
			// (int)(ev.getExportHeight() * printingScale / factor));

			Dimension size = new Dimension((int) (ev.getExportWidth()
					* printingScale / factor), (int) (ev.getExportHeight()
					* printingScale / factor));

			g = new PDFGraphics2D(file, size);

			// g.
			//
			// PDFRedundanceTracker tracker = new PDFRedundanceTracker(pdf);
			// Font font = new Font("Impact", Font.PLAIN, 1000);
			// FontRenderContext context = new FontRenderContext(null, true,
			// true);
			// FontEmbedder fe = PDFFontEmbedderType1.create(context, pdf,
			// "MyFont", tracker);
			// fe.includeFont(font, Lookup.getInstance().getTable("PDFLatin"),
			// "F1");

			((PDFGraphics2D) g).setPageSize(size);

			g.startExport();
			ev.exportPaint(g, printingScale / factor, textAsShapes
					? ExportType.PDF_TEXTASSHAPES : ExportType.PDF_EMBEDFONTS);
			g.endExport();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param app
	 *            application
	 * @param ev
	 *            view
	 * @param file
	 *            target file
	 * @param textAsShapes
	 *            whether to convert text to curves
	 * @param pixelWidth
	 *            width in pixels
	 * @param pixelHeight
	 *            height in pixels
	 * @param exportScale
	 *            scale units / cm
	 */
	public static void exportEPS(AppD app, EuclidianViewD ev, File file,
			boolean textAsShapes, int pixelWidth, int pixelHeight,
			double exportScale) {
		org.geogebra.desktop.export.epsgraphics.EpsGraphics g;
		try {
			g = new org.geogebra.desktop.export.epsgraphics.EpsGraphics(
					GeoGebraConstants.APPLICATION_NAME + ", "
							+ GeoGebraConstants.GEOGEBRA_WEBSITE,
					new FileOutputStream(file), 0, 0, pixelWidth, pixelHeight,
					ColorMode.COLOR_RGB);
			// draw to epsGraphics2D
			ev.exportPaint(g, exportScale, ExportType.EPS);
			g.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param ev
	 *            view
	 * @param file
	 *            target file
	 * @param transparent
	 *            whether result should be transparent
	 * @param dpi
	 *            dpi
	 * @param exportScale
	 *            scale units / cm
	 * @param exportToClipboard says if exports to clipboard
	 * 	
	 */
	public static void exportPNG(EuclidianViewInterfaceDesktop ev, File file,
			boolean transparent, int dpi, double exportScale, boolean exportToClipboard) {
		
		ev.exportImagePNG(exportScale, transparent, dpi, file, exportToClipboard);

	}

	public static void export(String extension,
			EuclidianViewInterfaceDesktop ev, File file,
			boolean transparent, int dpi, double exportScale,
			boolean textAsShapes, boolean useEMFplus, int pixelWidth,
			int pixelHeight, AppD app) {
		if (extension.equals("png")) {
			// can be 3D View
			GraphicExportDialog.exportPNG(ev, file, transparent, dpi,
					exportScale, false);

		} else if (extension.equals("eps")) {
			GraphicExportDialog.exportEPS(app, (EuclidianViewD) ev, file,
					textAsShapes,
					pixelWidth, pixelHeight, exportScale);

		} else if (extension.equals("pdf")) {
			GraphicExportDialog.exportPDF(app, (EuclidianViewD) ev, file,
					textAsShapes,
					pixelWidth, pixelHeight, exportScale);

		} else if (extension.equals("emf")) {
			GraphicExportDialog.exportEMF(app, (EuclidianViewD) ev, file,
					useEMFplus,
					pixelWidth, pixelHeight, exportScale);

		} else if (extension.equals("svg")) {
			GraphicExportDialog.exportSVG(app, (EuclidianViewD) ev, file,
					textAsShapes,
					pixelWidth, pixelHeight, -1, -1, exportScale, transparent);

		}

	}

}
