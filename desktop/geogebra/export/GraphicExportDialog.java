/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.export;

import geogebra.common.GeoGebraConstants;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.main.App;
import geogebra.common.util.Unicode;
import geogebra.euclidian.EuclidianViewD;
import geogebra.export.epsgraphics.ColorMode;
import geogebra.gui.GuiManagerD;
import geogebra.gui.util.FileTransferable;
import geogebra.gui.util.ImageSelection;
import geogebra.io.MyImageIO;
import geogebra.main.AppD;
import geogebra.main.GeoGebraPreferencesD;
import geogebra.util.DownloadManager;
import geogebra.util.Util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
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
import org.freehep.graphicsio.emf.EMFGraphics2D;
import org.freehep.graphicsio.emf.EMFPlusGraphics2D;
import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.freehep.graphicsio.svg.SVGGraphics2D;
import org.freehep.util.UserProperties;

/**
 * @author Markus Hohenwarter
 */
public class GraphicExportDialog extends JDialog implements KeyListener {

	private static final long serialVersionUID = 1L;

	private final AppD app;
	private JComboBox cbFormat, cbDPI;
	private JLabel sizeLabel;
	private JButton cancelButton;

	private double exportScale;
	private int pixelWidth, pixelHeight;
	private final NumberFormat sizeLabelFormat;

	boolean textAsShapes = true;
	boolean transparent = true;
	boolean EMFPlus = true;

	private final int FORMAT_PNG = 0;
	private final int FORMAT_PDF = 1;
	private final int FORMAT_EPS = 2;
	private final int FORMAT_SVG = 3;
	private final int FORMAT_EMF = 4;

	private EuclidianViewD specifiedEuclidianView;
	
	
	/**
	 * Creates a dialog for exporting an image of the active EuclidianView 
	 * @param app
	 */
	public GraphicExportDialog(AppD app) {
		this(app, null);
		
	}
	
	/**
	 * Creates a dialog for exporting an image of the EuclidianView given as a
	 * parameter.
	 * 
	 * @param app
	 * @param specifiedEuclidianView
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

	
	private EuclidianViewD getEuclidianView(){
		if(specifiedEuclidianView != null){
			return specifiedEuclidianView;
		}
		return (EuclidianViewD) app.getActiveEuclidianView();
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

	private void initGUI() {
		setResizable(false);
		setTitle(app.getPlain("ExportAsPicture"));

		JPanel cp = new JPanel(new BorderLayout(5, 5));
		cp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().add(cp);

		// format list
		JPanel formatPanel = new JPanel(new FlowLayout(5));
		String[] formats = {
				app.getPlain("png") + " (" + AppD.FILE_EXT_PNG + ")",
				app.getPlain("pdf") + " (" + AppD.FILE_EXT_PDF + ")",
				app.getPlain("eps") + " (" + AppD.FILE_EXT_EPS + ")",
				app.getPlain("svg") + " (" + AppD.FILE_EXT_SVG + ")",
				app.getPlain("emf") + " (" + AppD.FILE_EXT_EMF + ")" };

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
		EuclidianView ev = getEuclidianView();

		final PrintScalePanel psp = new PrintScalePanel(app, ev);
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
		if (cbFormat.getSelectedIndex() == FORMAT_PNG) {
			dpiPanel.add(resolutionInDPILabel);
			dpiPanel.add(cbDPI);
			dpiPanel.add(cbTransparent);
		} else if (cbFormat.getSelectedIndex() == FORMAT_EMF) {
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

		// if (cbFormat.getSelectedIndex()==FORMAT_SVG ||
		// cbFormat.getSelectedIndex()==FORMAT_PDF)
		// dpiPanel.add(textAsShapesCB);

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
				switch (cbFormat.getSelectedIndex()) {
				case FORMAT_SVG:
					dpiPanel.remove(resolutionInDPILabel);
					dpiPanel.remove(cbDPI);
					dpiPanel.remove(cbEMFPlus);
					dpiPanel.remove(cbTransparent);
					dpiPanel.add(textAsShapesCB);
					break;
				case FORMAT_PDF:
				case FORMAT_EPS:
					dpiPanel.remove(resolutionInDPILabel);
					dpiPanel.remove(cbDPI);
					dpiPanel.remove(cbEMFPlus);
					dpiPanel.remove(cbTransparent);
					dpiPanel.add(textAsShapesCB);
					textAsShapesCB.setEnabled(false);
					textAsShapesCB.setSelected(true);
					break;
				case FORMAT_EMF:
					dpiPanel.add(cbEMFPlus);
					dpiPanel.remove(resolutionInDPILabel);
					dpiPanel.remove(cbDPI);
					dpiPanel.remove(cbTransparent);
					dpiPanel.remove(textAsShapesCB);
					break;
				default: // PNG
					dpiPanel.add(resolutionInDPILabel);
					dpiPanel.add(cbDPI);
					dpiPanel.remove(cbEMFPlus);
					dpiPanel.add(cbTransparent);
					dpiPanel.remove(textAsShapesCB);
					cbDPI.setSelectedItem("300");
					cbDPI.setEnabled(true);
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

						int index = cbFormat.getSelectedIndex();
						switch (index) {
						case FORMAT_PNG: // PNG
							exportPNG(false);
							break;

						case FORMAT_EPS: // EPS
							exportEPS(false);
							break;

						case FORMAT_EMF: // EMF
							exportEMF(false, EMFPlus);
							break;

						case FORMAT_PDF: // PDF
							exportPDF(false);
							break;

						case FORMAT_SVG: // SVG
							exportSVG(false);
							break;

						}
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

						int index = cbFormat.getSelectedIndex();
						switch (index) {
						case FORMAT_PNG: // PNG
							exportPNG(true);
							break;

						case FORMAT_EPS: // EPS
							exportEPS(true);
							break;

						case FORMAT_EMF: // EMF
							exportEMF(true, EMFPlus);
							break;

						case FORMAT_PDF: // PDF
							exportPDF(true);
							break;

						case FORMAT_SVG: // SVG
							exportSVG(true);
							break;

						}
					}
				};
				runner.start();
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(exportButton);
		buttonPanel.add(exportClipboardButton);
		buttonPanel.add(cancelButton);
		cp.add(buttonPanel, BorderLayout.SOUTH);

		Util.addKeyListenerToAll(this, this);

		updateSizeLabel();
		centerOnScreen();
	}

	private int getDPI() {
		return Integer.parseInt((String) cbDPI.getSelectedItem());
	}

	/*
	 * dpi must be one of 72,96,150,300,600
	 */
	public void setDPI(String dpi) {
		cbDPI.setSelectedItem(dpi);
	}

	private void loadPreferences() {
		try {
			// format
			int formatID = FORMAT_PNG;
			String format = GeoGebraPreferencesD.getPref().loadPreference(
					GeoGebraPreferencesD.EXPORT_PIC_FORMAT, "png");
			if (format.equals("eps")) {
				formatID = FORMAT_EPS;
			} else if (format.equals("svg")) {
				formatID = FORMAT_SVG;
			}
			cbFormat.setSelectedIndex(formatID);

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
		switch (cbFormat.getSelectedIndex()) {
		case FORMAT_EPS:
			format = "eps";
			break;
		case FORMAT_SVG:
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

	private void updateSizeLabel() {
		EuclidianViewD ev = getEuclidianView();
		double printingScale = ev.getPrintingScale();
		// takes dpi into account (note: eps has 72dpi)
		exportScale = (printingScale * getDPI()) / 2.54 / ev.getXscale();

		StringBuilder sb = new StringBuilder();
		// cm size
		double cmWidth = printingScale * (ev.getExportWidth() / ev.getXscale());
		double cmHeight = printingScale
				* (ev.getExportHeight() / ev.getXscale());  // getXscale is not a typo. see #2894
		sb.append(sizeLabelFormat.format(cmWidth));
		sb.append(" cm ");
		sb.append(Unicode.multiply);
		sb.append(' ');
		sb.append(sizeLabelFormat.format(cmHeight));
		sb.append(" cm");

		int index = cbFormat.getSelectedIndex();
		if (index == FORMAT_PNG) {
			// pixel size
			pixelWidth = (int) Math.floor(ev.getExportWidth() * exportScale);
			pixelHeight = (int) Math.floor(ev.getExportHeight() * exportScale);
			sb.append(", ");
			sb.append(pixelWidth);
			sb.append(' ');
			sb.append(Unicode.multiply);
			sb.append(' ');
			sb.append(pixelHeight);
			sb.append(" pixels");
			sb.append(Unicode.Superscript_2);
		}

		sizeLabel.setText(sb.toString());
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

		final EuclidianViewD ev = getEuclidianView();
		final double printingScale = ev.getPrintingScale();

		// set dpi to 72
		exportScale = (printingScale * 72) / 2.54 / ev.getXscale();
		// ... and update bounding box accordingly
		pixelWidth = (int) Math.floor(ev.getExportWidth() * exportScale);
		pixelHeight = (int) Math.floor(ev.getExportHeight() * exportScale);

		File file;
		if (exportToClipboard) {
			final String tempDir = DownloadManager.getTempDir(); 
			//os = new ByteArrayOutputStream();
			// use file to get the correct filetype (so eg pasting into Word works)
			// NB pasting into WordPad *won't* work with this method
			file = new File(tempDir + "geogebra.eps");
		} else {
			file = ((GuiManagerD)app.getGuiManager()).showSaveDialog(AppD.FILE_EXT_EPS,
					null, app.getPlain("eps") + " " + app.getMenu("Files"),
					true, false);
			
			if (file == null) { 
				return false;
			}
		}
		try {
			

			exportEPS(app, ev, file, exportToClipboard, pixelWidth, pixelHeight, exportScale);

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
			file = ((GuiManagerD)app.getGuiManager()).showSaveDialog(AppD.FILE_EXT_EMF,
					null, app.getPlain("emf") + " " + app.getMenu("Files"),
					true, false);
			// Michael Borcherds 2008-03-02 END
		}

		if (file == null) {
			return false;
		}
		try {
			exportEMF(app, getEuclidianView(), file, useEMFplus, pixelWidth, pixelHeight, exportScale);

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
			file = ((GuiManagerD)app.getGuiManager()).showSaveDialog(AppD.FILE_EXT_PDF,
					null, app.getPlain("pdf") + " " + app.getMenu("Files"),
					true, false);
		}

		if (file == null) {
			return false;
		}
		try {
			
			exportPDF(app, getEuclidianView(), file, textAsShapes, pixelWidth, pixelHeight, exportScale);

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

		EuclidianViewD ev = getEuclidianView();
		double printingScale = ev.getPrintingScale();

		// set dpi to 72
		exportScale = (printingScale * 72) / 2.54 / ev.getXscale();

		// ... and update bounding box accordingly
		pixelWidth = (int) Math.floor(ev.getExportWidth() * exportScale);
		pixelHeight = (int) Math.floor(ev.getExportHeight() * exportScale);

		// Michael Borcherds 2008-03-02 BEGIN
		File file;
		String tempDir = DownloadManager.getTempDir();
		if (exportToClipboard) {
			file = new File(tempDir + "geogebra.svg");
		} else {
			// Michael Borcherds 2008-03-02 END
			file = ((GuiManagerD)app.getGuiManager()).showSaveDialog(AppD.FILE_EXT_SVG,
					null, app.getPlain("svg") + " " + app.getMenu("Files"),
					true, false);
		}

		if (file == null) {
			return false;
		}
		ev.setTemporaryCoordSystemForExport(); // allow clipping with Export_1
												// and 2 Points
		try {
			exportSVG(app, ev, file, exportToClipboard, pixelWidth, pixelHeight, exportScale);

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
		} finally {
			ev.restoreOldCoordSystem();
		}
	}

	final public boolean exportPNG(boolean exportToClipboard) {
		return exportPNG(exportToClipboard, true);
	}

	/**
	 * Exports drawing as png with given resolution in dpi
	 */
	final public boolean exportPNG(boolean exportToClipboard, boolean showError) {
		// Michael Borcherds 2008-03-02 BEGIN
		File file;
		String tempDir = DownloadManager.getTempDir();
		if (exportToClipboard) {
			file = new File(tempDir + "geogebra.png");
		} else {
			file = ((GuiManagerD)app.getGuiManager()).showSaveDialog(AppD.FILE_EXT_PNG,
					null, app.getPlain("png") + " " + app.getMenu("Files"),
					true, false);
		}

		if (file == null) {
			return false;
		}

		try {
			// draw graphics view into image
			EuclidianViewD ev = getEuclidianView();
			
			exportPNG(ev, file, transparent, getDPI(), exportScale);
			
			if (exportToClipboard) {
				sendToClipboard(file);
			}

			return true;
		} catch (Exception ex) {
			if (showError) {
				app.showError("SaveFileFailed");
			}
			App.debug(ex.toString());
			return false;
		} catch (Error ex) {
			if (showError) {
				app.showError("SaveFileFailed");
			}
			App.debug(ex.toString());
			return false;
		}
	}

	/**
	 * Exports drawing as jpg.
	 * 
	 * final private boolean exportJPG() { File file = app.showSaveDialog(
	 * Application.FILE_EXT_JPG, app.getPlain("jpg") + " " +
	 * app.getMenu("Files")); if (file == null) return false; try {
	 * BufferedImage img = app.getEuclidianView().getExportImage(
	 * sizePanel.getSelectedWidth()); ImageIO.write(img, "jpg", file); return
	 * true; } catch (IOException ex) { app.showError("SaveFileFailed");
	 * Application.debug(ex.toString()); return false; } }
	 */

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

	// Michael Borcherds 2008-03-02 BEGIN
	private static void sendToClipboard(File file) {
		FileTransferable ft = new FileTransferable(file);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, null);
	}

	private static void sendToClipboard(Image img) {
		ImageSelection imgSel = new ImageSelection(img);
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(imgSel, null);
	}
	// Michael Borcherds 2008-03-02 END

	/**
	 * 
	 * @param app
	 * @param ev
	 * @param file
	 * @param textAsShapes
	 * @param pixelWidth
	 * @param pixelHeight
	 * @param exportScale
	 */
	public static void exportSVG(AppD app, EuclidianViewD ev, File file, boolean textAsShapes, int pixelWidth, int pixelHeight, double exportScale) {
		UserProperties props = (UserProperties) SVGGraphics2D
				.getDefaultProperties();
		props.setProperty(SVGGraphics2D.EMBED_FONTS, !textAsShapes);
		props.setProperty(AbstractVectorGraphicsIO.TEXT_AS_SHAPES, textAsShapes);
		SVGGraphics2D.setDefaultProperties(props);

		// Michael Borcherds 2008-03-01
		// added SVGExtensions to support grouped objects in layers
		SVGExtensions g;
		try {
			g = new SVGExtensions(file, new Dimension(pixelWidth,
					pixelHeight));
			// make sure LaTeX exported at hi res
			app.exporting = true;

			g.startExport();
			ev.exportPaintPre(new geogebra.awt.GGraphics2DD(g), exportScale);

			g.startGroup("misc");
			ev.drawActionObjects(new geogebra.awt.GGraphics2DD(g));
			g.endGroup("misc");

			for (int layer = 0; layer <= app.getMaxLayerUsed(); layer++) // draw
				// only
				// layers
				// we
				// need
			{
				g.startGroup("layer" + layer);
				ev.drawLayers[layer].drawAll(new geogebra.awt.GGraphics2DD(g));
				g.endGroup("layer" + layer);
			}

			g.endExport();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			app.exporting = false;									
		}
	}
	
	/**
	 * 
	 * @param app
	 * @param ev
	 * @param file
	 * @param useEMFplus
	 * @param pixelWidth
	 * @param pixelHeight
	 * @param exportScale
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
			g.startExport();
			ev.exportPaint(g,exportScale);

			g.endExport();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @param app
	 * @param ev
	 * @param file
	 * @param textAsShapes
	 * @param pixelWidth
	 * @param pixelHeight
	 * @param exportScale
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
		props.setProperty(PDFGraphics2D.EMBED_FONTS, !textAsShapes);
		// props.setProperty(PDFGraphics2D.EMBED_FONTS_AS,
		// FontConstants.EMBED_FONTS_TYPE1);
		props.setProperty(AbstractVectorGraphicsIO.TEXT_AS_SHAPES, textAsShapes);
		PDFGraphics2D.setDefaultProperties(props);
		
		VectorGraphics g;
		try {
			
			double printingScale = ev.getPrintingScale();
			
			// TODO: why do we need this to make correct size in cm?
			double factor = ev.getXscale() * 2.54 / 72;

			//Dimension size = new Dimension(
			//		(int)(ev.getExportWidth() * printingScale / factor), (int)(ev.getExportHeight() * printingScale / factor));

			Dimension size = new Dimension((int)(ev.getExportWidth() * printingScale / factor), (int)(ev.getExportHeight() * printingScale / factor));

			g = new PDFGraphics2D(file, size);

			((PDFGraphics2D)g).setPageSize(size);
			
			// make sure LaTeX exported at hi res
			app.exporting = true;

			g.startExport();
			ev.exportPaint(g,printingScale / factor);
			g.endExport();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			app.exporting = false;									
		}
	}

	/**
	 * 
	 * @param app
	 * @param ev
	 * @param file
	 * @param textAsShapes
	 * @param pixelWidth
	 * @param pixelHeight
	 * @param exportScale
	 */
	public static void exportEPS(AppD app, EuclidianViewD ev, File file,
			boolean textAsShapes, int pixelWidth, int pixelHeight,
			double exportScale) {
		geogebra.export.epsgraphics.EpsGraphics g;
		try {
			g = new geogebra.export.epsgraphics.EpsGraphics(
					app.getPlain("ApplicationName") + ", "
							+ GeoGebraConstants.GEOGEBRA_WEBSITE, new FileOutputStream(file), 0, 0, pixelWidth, pixelHeight,
							ColorMode.COLOR_RGB);
			// draw to epsGraphics2D
			ev.exportPaint(g, exportScale);
			g.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * 
	 * @param ev
	 * @param file
	 * @param transparent
	 * @param dpi
	 * @param exportScale
	 */
	public static void exportPNG(EuclidianViewD ev, File file,
			boolean transparent, int dpi,
			double exportScale) {
		// write image to file
		try {
			BufferedImage img = ev.getExportImage(exportScale, transparent);
			MyImageIO.write(img, "png", dpi, file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
