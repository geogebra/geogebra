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
import geogebra.gui.TitlePanel;
import geogebra.gui.view.consprotocol.ConstructionProtocolView;
import geogebra.main.AppD;
import geogebra.util.Util;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class ConstructionProtocolExportDialog extends JDialog implements
		KeyListener {

	private static final long serialVersionUID = -2626950140196416416L;

	private JCheckBox cbDrawingPadPicture, cbScreenshotPicture;
	private JCheckBox cbColor;
	private JCheckBox cbIcons;
	private GraphicSizePanel sizePanel;
	boolean kernelChanged = false;
	private ConstructionProtocolView prot;
	private AppD app;

	public ConstructionProtocolExportDialog(ConstructionProtocolView prot) {
		super(prot.getApplication().getFrame(), true);
		this.prot = prot;
		app = prot.getApplication();

		initGUI();
	}

	private void initGUI() {
		setResizable(true);
		setTitle(app.getMenu("Export") + ": "
				+ app.getPlain("ConstructionProtocol") + " ("
				+ AppD.FILE_EXT_HTML + ")");

		JPanel cp = new JPanel(new BorderLayout(5, 5));
		cp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().add(cp);

		TitlePanel tp = new TitlePanel(app);
		cp.add(tp, BorderLayout.NORTH);
		tp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				kernelChanged = true;
			}
		});

		// checkbox: insert picture of drawing pad
		JPanel picPanel = new JPanel(new BorderLayout(20, 5));
		cbDrawingPadPicture = new JCheckBox(
				app.getPlain("InsertPictureOfConstruction"));
		cbDrawingPadPicture.setSelected(true);
		cbScreenshotPicture = new JCheckBox(
				app.getPlain("InsertPictureOfAllOpenViews"));
		cbScreenshotPicture.setSelected(false);

		picPanel.add(cbDrawingPadPicture, BorderLayout.WEST);
		if (app.getGuiManager().showView(App.VIEW_ALGEBRA)) {
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

		cbColor = new JCheckBox(app.getPlain("ColorfulConstructionProtocol"));
		cbColor.setSelected(false);

		cbIcons = new JCheckBox(
				app.getPlain("ToolbarIconsConstructionProtocolExport"));
		cbIcons.setSelected(prot.getAddIcons());

		// disable width and height field when checkbox is deselected
		cbDrawingPadPicture.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean flag = cbDrawingPadPicture.isSelected();
				sizePanel.setEnabled(flag);
				if (flag) {
					cbScreenshotPicture.setSelected(false);
				}
			}
		});
		cbScreenshotPicture.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean flag = cbScreenshotPicture.isSelected();
				sizePanel.setEnabled(false);
				if (flag) {
					cbDrawingPadPicture.setSelected(false);
				}
			}
		});
		cbColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prot.setUseColors(cbColor.isSelected());
			}
		});
		cbIcons.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prot.setAddIcons(cbIcons.isSelected());
			}
		});

		// Cancel and Export Button
		JButton cancelButton = new JButton(app.getPlain("Cancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		JButton exportButton = new JButton(app.getMenu("Export"));
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						dispose();
						if (kernelChanged)
							app.storeUndoInfo();
						exportHTML(cbDrawingPadPicture.isSelected(),
								cbScreenshotPicture.isSelected(),
								cbColor.isSelected(), cbIcons.isSelected());
					}
				};
				runner.start();
			}
		});
		
		JButton clipboardButton = new JButton(app.getMenu("Clipboard"));
		clipboardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					@Override
					public void run() {
						dispose();
						if (kernelChanged)
							app.storeUndoInfo();
						try {
							Toolkit toolkit = Toolkit.getDefaultToolkit();
							Clipboard clipboard = toolkit.getSystemClipboard();
							StringSelection stringSelection = new StringSelection(prot.getHTML(null, null));
							clipboard.setContents(stringSelection, null);
						} catch (Exception ex) {
							app.showError("SaveFileFailed");
							App.debug(ex.toString());
						}	
					}
				};
				runner.start();
			}
		});
		
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(exportButton);
		buttonPanel.add(clipboardButton);
		buttonPanel.add(cancelButton);

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(cbColor, BorderLayout.NORTH);
		southPanel.add(cbIcons, BorderLayout.CENTER);
		southPanel.add(buttonPanel, BorderLayout.SOUTH);
		cp.add(southPanel, BorderLayout.SOUTH);

		Util.addKeyListenerToAll(this, this);
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

	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_ESCAPE) {
			dispose();
		}
	}

	public void keyReleased(KeyEvent e) {
		//
	}

	public void keyTyped(KeyEvent e) {
		//
	}

	/* *******************
	 * HTML export *******
	 */

	/**
	 * Exports construction protocol as html
	 * 
	 * @param addIcons
	 * @param includePicture
	 *            : states whether a picture of the drawing pad should be
	 *            exported with the html output file
	 * @param includeAlgebraPicture
	 *            : states whether a picture of the algebraWindow should be
	 *            exported with the html output file
	 */
	private void exportHTML(boolean includePicture,
			boolean includeAlgebraPicture, boolean useColors, boolean addIcons) {
	File file, pngFile = null;
		File dir = null;
		prot.setUseColors(useColors);
		dir = app.getGuiManager().showSaveDialog("", null,
				app.getPlain("Directories"), false, true);
		if (dir == null)
			return;

		try {
			String thisPath = dir.getPath();
			file = new File(thisPath, "index.html");

			BufferedImage img = null;

			if (includePicture) {
				// picture of drawing pad
				img = app.getEuclidianView1().getExportImage(1d);
			} else if (includeAlgebraPicture) {
				// picture of drawing pad
				img = getCenterPanelImage();
			}

			// save image to PNG file
			if (img != null) {
				pngFile = new File(thisPath, "image.png");
				pngFile.mkdirs();
				ImageIO.write(img, "png", pngFile);
			}

			FileWriter fw = new FileWriter(file);
			fw.write(prot.getHTML(pngFile, thisPath));
			fw.close();
			
			// This code is mostly copy-pasted from geogebra/export/WorksheetExportDialog.java.
	        // open browser
			final File HTMLfile = file;
	        Thread runner = new Thread() {
	        @Override
			public void run() {    
	                try {
	                        // open html file in browser
	                        app.getGuiManager().showURLinBrowser(HTMLfile.toURI().toURL());
	                } catch (Exception ex) {                        
	                        app.showError("SaveFileFailed");
	                        App.debug(ex.toString());
	                } 
	        }
	        };
	        runner.start();
			
		} catch (IOException ex) {
			app.showError("SaveFileFailed");
			App.debug(ex.toString());
		}
						
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
