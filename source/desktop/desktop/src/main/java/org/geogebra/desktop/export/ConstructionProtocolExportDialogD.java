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
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GBufferedImageD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.TitlePanel;
import org.geogebra.desktop.gui.dialog.Dialog;
import org.geogebra.desktop.gui.view.consprotocol.ConstructionProtocolViewD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.plugin.GgbAPID;
import org.geogebra.desktop.util.UtilD;

public class ConstructionProtocolExportDialogD extends Dialog
		implements KeyListener {

	private static final long serialVersionUID = -2626950140196416416L;

	private JCheckBox cbDrawingPadPicture;
	private JCheckBox cbScreenshotPicture;
	private JCheckBox cbColor;
	private GraphicSizePanel sizePanel;
	boolean kernelChanged = false;
	private final ConstructionProtocolViewD prot;
	private final AppD app;

	/**
	 * @param prot construction protocol
	 */
	public ConstructionProtocolExportDialogD(ConstructionProtocolViewD prot) {
		super(prot.getApplication().getFrame(), true);
		this.prot = prot;
		app = prot.getApplication();

		initGUI();
	}

	private void initGUI() {
		setResizable(true);
		final Localization loc = app.getLocalization();
		setTitle(loc.getMenu("Export") + ": "
				+ loc.getMenu("ConstructionProtocol") + " ("
				+ FileExtensions.HTML + ")");

		JPanel cp = new JPanel(new BorderLayout(5, 5));
		cp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().add(cp);

		TitlePanel tp = new TitlePanel(app);
		cp.add(tp, BorderLayout.NORTH);
		tp.addActionListener(e -> kernelChanged = true);

		// checkbox: insert picture of drawing pad
		JPanel picPanel = new JPanel(new BorderLayout(20, 5));
		cbDrawingPadPicture = new JCheckBox(
				loc.getMenu("InsertPictureOfConstruction"));
		cbDrawingPadPicture.setSelected(true);
		cbScreenshotPicture = new JCheckBox(
				loc.getMenu("InsertPictureOfAllOpenViews"));
		cbScreenshotPicture.setSelected(false);

		picPanel.add(cbDrawingPadPicture, app.getLocalization().borderWest());
		if (((GuiManagerD) app.getGuiManager()).showView(App.VIEW_ALGEBRA)) {
			picPanel.add(cbScreenshotPicture, BorderLayout.SOUTH);
		}

		// panel with fields to enter width and height of picture
		EuclidianView ev = app.getEuclidianView1();
		// int height = (int) Math.ceil(DEFAULT_GRAPHICS_WIDTH *
		// (double) ev.getHeight() / ev.getWidth());
		// sizePanel = new GraphicSizePanel(app, DEFAULT_GRAPHICS_WIDTH,
		// height);
		sizePanel = new GraphicSizePanel(app, ev.getWidth(), ev.getHeight());
		picPanel.add(sizePanel, BorderLayout.CENTER);
		picPanel.setBorder(BorderFactory.createEtchedBorder());
		cp.add(picPanel, BorderLayout.CENTER);

		cbColor = new JCheckBox(loc.getMenu("ColorfulConstructionProtocol"));
		cbColor.setSelected(false);

		// disable width and height field when checkbox is deselected
		cbDrawingPadPicture.addActionListener(e -> {
			boolean flag = cbDrawingPadPicture.isSelected();
			sizePanel.setEnabled(flag);
			if (flag) {
				cbScreenshotPicture.setSelected(false);
			}
		});
		cbScreenshotPicture.addActionListener(e -> {
			boolean flag = cbScreenshotPicture.isSelected();
			sizePanel.setEnabled(false);
			if (flag) {
				cbDrawingPadPicture.setSelected(false);
			}
		});
		cbColor.addActionListener(e -> prot.setUseColors(cbColor.isSelected()));

		// Cancel and Export Button
		JButton cancelButton = new JButton(loc.getMenu("Cancel"));
		cancelButton.addActionListener(e -> dispose());
		JButton exportButton = new JButton(loc.getMenu("Export"));
		exportButton.addActionListener(e -> {
			Thread runner = new Thread() {
				@Override
				public void run() {
					dispose();
					if (kernelChanged) {
						app.storeUndoInfo();
					}
					exportHTML(cbDrawingPadPicture.isSelected(),
							cbScreenshotPicture.isSelected(),
							cbColor.isSelected());
				}
			};
			runner.start();
		});

		JButton clipboardButton = new JButton(loc.getMenu("Clipboard"));
		clipboardButton.addActionListener(e -> {
			Thread runner = new Thread(() -> {
				dispose();
				if (kernelChanged) {
					app.storeUndoInfo();
				}
				try {
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Clipboard clipboard = toolkit.getSystemClipboard();

					StringSelection stringSelection = new StringSelection(
							ConstructionProtocolView.getHTML(null,
									app.getLocalization(),
									app.getKernel(), prot.getColumns(),
									prot.getUseColors()));
					clipboard.setContents(stringSelection, null);
				} catch (Exception ex) {
					ex.printStackTrace();
					app.showError(Errors.SaveFileFailed);
					Log.debug(ex.toString());
				}
			});
			runner.start();
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(exportButton);
		buttonPanel.add(clipboardButton);
		buttonPanel.add(cancelButton);

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(cbColor, BorderLayout.NORTH);
		southPanel.add(buttonPanel, BorderLayout.SOUTH);
		cp.add(southPanel, BorderLayout.SOUTH);

		UtilD.addKeyListenerToAll(this, this);
		centerOnScreen();
	}

	private void centerOnScreen() {
		// center on screen
		pack();
		setLocationRelativeTo(app.getMainComponent());
	}

	/*
	 * Keylistener implementation of ConstructionProtocol
	 */

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_ESCAPE) {
			dispose();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//
	}

	/*
	 * ******************* HTML export *******
	 */

	/**
	 * Exports construction protocol as html
	 *
	 * @param includePicture
	 *            : states whether a picture of the drawing pad should be
	 *            exported with the html output file
	 * @param includeAlgebraPicture
	 *            : states whether a picture of the algebraWindow should be
	 *            exported with the html output file
	 */
	private void exportHTML(boolean includePicture,
			boolean includeAlgebraPicture, boolean useColors) {
		prot.setUseColors(useColors);
		final File file = app.getGuiManager().showSaveDialog(
				FileExtensions.HTML, null,
				app.getLocalization().getMenu("HTML"), true, false);

		BufferedImage img = null;

		if (includePicture) {
			// picture of drawing pad
			img = GBufferedImageD.getAwtBufferedImage(
					app.getEuclidianView1().getExportImage(1d));
		} else if (includeAlgebraPicture) {
			// picture of drawing pad
			img = getCenterPanelImage();
		}

		String imgBase64 = GgbAPID.base64encode(img, 72);
		String export = ConstructionProtocolView.getHTML(imgBase64,
				app.getLocalization(), app.getKernel(), prot.getColumns(),
				useColors);

		Log.debug(export);

		try (BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), StandardCharsets.UTF_8))) {
			fw.write(export);
		} catch (IOException ex) {
			app.showError(Errors.SaveFileFailed);
			Log.debug(ex.toString());
			return;
		}

		Thread runner = new Thread(() -> {
			try {
				// open html file in browser
				((GuiManagerD) app.getGuiManager())
						.showURLinBrowser(file.toURI().toURL());
			} catch (Exception ex) {
				app.showError(Errors.SaveFileFailed);
				Log.debug(ex.toString());
			}
		});
		runner.start();
	}

	private BufferedImage getCenterPanelImage() {
		JPanel centPanel = app.getCenterPanel();
		BufferedImage img = new BufferedImage(centPanel.getWidth(),
				centPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		centPanel.paint(g);
		g.dispose();
		img.flush();
		return img;
	}

}
