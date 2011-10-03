/* 
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

package geogebra.export;

import geogebra.GeoGebra;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.GuiManager;
import geogebra.gui.TitlePanel;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.HelpAction;
import geogebra.gui.util.IconTabbedPane;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.GeoGebraPreferences;
import geogebra.util.DownloadManager;
import geogebra.util.Util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Dialog which provides for exporting into an HTML page enriched with an
 * Applet.
 * 
 * @author Markus Hohenwarter
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 */
public class WorksheetExportDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * Index of 'Upload to GeoGebraTube' tab.
	 */
	private static final int TAB_UPLOAD = 0;

	/**
	 * Index of HTML export tab.
	 */
	private static final int TAB_HTML = 1;

	private static final int BUTTON_WIDTH = 200;
	private static final int BUTTON_HEIGHT = 40;
	private static final int DEFAULT_HTML_PAGE_WIDTH = 600;
	public static final int DEFAULT_APPLET_WIDTH = 600;
	public static final int DEFAULT_APPLET_HEIGHT = 500;

	final private static int TYPE_SINGLE_FILE = 0;
	final private static int TYPE_SINGLE_FILE_TABS = 1;
	final private static int TYPE_MULTIPLE_FILES = 2;

	final public static int TYPE_HTMLFILE = 0;
	final public static int TYPE_HTMLCLIPBOARD = 1;
	final private static int TYPE_MEDIAWIKI = 2;
	final private static int TYPE_GOOGLEGADGET = 3;
	final private static int TYPE_MOODLE = 4;
	// final private static int TYPE_JSXGRAPH = 4;
	// final private static int TYPE_JAVASCRIPT = 5;

	private Application app;
	private Kernel kernel;
	private InputPanel textAbove, textBelow, textAboveUpload, textBelowUpload;
	private JCheckBox cbShowFrame, cbEnableRightClick, cbEnableLabelDrags,
			cbShowResetIcon, cbShowMenuBar, cbSavePrint, cbShowToolBar,
			cbShowToolBarHelp, cbShowInputField, cbUseBrowserForJavaScript,
			cbAllowRescaling, cbRemoveLinebreaks, cbOpenButton, cbOfflineJars,
			cbIncludeHTML5;
	private JComboBox cbFileType, cbAllWorksheets;
	private JButton exportButton, uploadButton, helpButton;
	private GraphicSizePanel sizePanel;
	private boolean kernelChanged = false;
	private JTabbedPane tabbedPane;
	private GeoGebraPreferences ggbPref;
	private GuiManager guiManager;
	private boolean removeLineBreaks;
	private MyTextField titleField;
	private TitlePanel titlePanel;

	private JTabbedPane modeSwitch;
	private JPanel modeUploadPanel;
	private JPanel modeHtmlPanel;

	public WorksheetExportDialog(Application app) {
		super(app.getFrame(), true);
		this.app = app;
		kernel = app.getKernel();

		ggbPref = GeoGebraPreferences.getPref();
		guiManager = app.getGuiManager();

		initGUI();
	}

	/**
	 * Checks if the EuclidianView has a selected rectangle. In this case we will
	 * automatically move the coord system to put the selection rectangle into the
	 * upper left corner of the euclidian view.
	 */
	private void checkEuclidianView() {
		EuclidianView ev = app.getEuclidianView();

		// 1) selection rectangle
		Rectangle rect = ev.getSelectionRectangle();
		if (rect != null) {
			double xZero = ev.getXZero() - rect.x;
			double yZero = ev.getYZero() - rect.y;
			rect.x = 0;
			rect.y = 0;
			ev.setCoordSystem(xZero, yZero, ev.getXscale(), ev.getYscale(), true);

			// update size panel
			int width = sizePanel.getSelectedWidth() - (ev.getWidth() - rect.width);
			int height = sizePanel.getSelectedHeight()
					- (ev.getHeight() - rect.height);
			sizePanel.setValues(width, height, false);
		}
	}

	private void initGUI() {

		// title, author, date
		titlePanel = new TitlePanel(app);
		ActionListener kernelChangedListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				kernelChanged = true;
			}
		};
		titlePanel.addActionListener(kernelChangedListener);
		titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tabbedPane.addTab(app.getMenu("General"), createGeneralSettingsTab());
		tabbedPane.addTab(app.getMenu("Advanced"), createAdvancedSettingsTab());

		// Cancel and Export Button
		JButton cancelButton = new JButton(app.getPlain("Cancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		helpButton = new JButton(app.getMenu("Help"));
		HelpAction helpAction = new HelpAction(app, app.getImageIcon("help.png"),
				app.getMenu("Help"), Application.WIKI_EXPORT_WORKSHEET);
		helpButton.setAction(helpAction);

		exportButton = new JButton(app.getPlain("Upload"));
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						setVisible(false);
						if (kernelChanged)
							app.storeUndoInfo();

						if(modeSwitch.getSelectedIndex() == TAB_HTML) {
							int multipleType = (cbAllWorksheets == null) ? TYPE_SINGLE_FILE
									: cbAllWorksheets.getSelectedIndex();
							int type = cbFileType.getSelectedIndex();

							switch (multipleType) {

							case TYPE_SINGLE_FILE:

								// index 0 = file + html
								if (type == TYPE_HTMLFILE)
									exportHTMLtoFile();
								else
									// index 1 = clipboard + html
									// index 2 = clipboard + mediawiki
									// index 3 = clipboard + google gadget
									try {
										exportToClipboard(type);

										if (type == TYPE_GOOGLEGADGET) {
											// open Google Gadgets Editor
											app.getGuiManager()
											.showURLinBrowser(
											"http://code.google.com/apis/gadgets/docs/tools.html#GGE");
										}
									} catch (Exception e) {
										app.showError("SaveFileFailed");
										Application.debug(e.toString());
									}
									break;

							case TYPE_SINGLE_FILE_TABS:
								try {
									exportAllOpenFilesToHTMLasTabs();
								} catch (Exception e) {
									app.showError("SaveFileFailed");
									e.printStackTrace();
									Application.debug(e.toString());
								}
								break;
							case TYPE_MULTIPLE_FILES:
								try {
									exportAllOpenFilesToHTML();
								} catch (Exception e) {
									app.showError("SaveFileFailed");
									e.printStackTrace();
									Application.debug(e.toString());
								}
								break;
							}
						} else {
							GeoGebraTubeExport ggtExport = new GeoGebraTubeExport(app);
							ggtExport.uploadWorksheet();
						}
					}
					};
					runner.start();
				}
			});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());

		buttonPanel.add(helpButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(exportButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(cancelButton);

		modeHtmlPanel = new JPanel(new BorderLayout());
		modeHtmlPanel.add(titlePanel, BorderLayout.NORTH);
		modeHtmlPanel.add(tabbedPane, BorderLayout.CENTER);

		modeUploadPanel = createUploadPanel();

		modeSwitch = new IconTabbedPane();

		modeSwitch.addTab(app.getMenu("UploadGeoGebraTube"),
				app.getImageIcon("export.png"), modeUploadPanel);
		modeSwitch.addTab(app.getPlain("ExportAsWebpage"),
				app.getImageIcon("export-html.png"), modeHtmlPanel);
		modeSwitch.setSelectedIndex(TAB_UPLOAD);
		
		modeSwitch.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int selected = modeSwitch.getSelectedIndex();

				if (selected == TAB_HTML) {
					exportButton.setText(app.getMenu("Export"));
					
					textAbove.setText(textAboveUpload.getText());
					textBelow.setText(textBelowUpload.getText());
					
					Construction cons = kernel.getConstruction();
					
					if (titleField.getText().equals(cons.getTitle()))
						return;
					cons.setTitle(titleField.getText());
					
					kernelChanged = true;
					
					titlePanel.updateData();
				} else {
					exportButton.setText(app.getPlain("Upload"));
					
					textAboveUpload.setText(textAbove.getText());
					textBelowUpload.setText(textBelow.getText());
					
					titleField.setText(kernel.getConstruction().getTitle());
				}
			}
		});
		
		// init text of text areas
		Construction cons = kernel.getConstruction();
		String text = cons.getWorksheetText(0);
		if (text.length() > 0)
			textAboveUpload.setText(text);
		text = cons.getWorksheetText(1);
		if (text.length() > 0)
			textBelowUpload.setText(text);
		
		titleField.setText(cons.getTitle());

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(modeSwitch, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		Util.registerForDisposeOnEscape(this);

		setTitle(app.getPlain("DynamicWorksheetExport"));
		setResizable(false);
		centerOnScreen();
	}

	private void loadPreferences() {
		try {

			cbEnableRightClick.setSelected(Boolean.valueOf(
					ggbPref.loadPreference(GeoGebraPreferences.EXPORT_WS_RIGHT_CLICK,
							"false")).booleanValue());
			cbEnableLabelDrags.setSelected(Boolean.valueOf(
					ggbPref.loadPreference(GeoGebraPreferences.EXPORT_WS_LABEL_DRAGS,
							"false")).booleanValue());
			cbShowResetIcon.setSelected(Boolean.valueOf(
					ggbPref.loadPreference(GeoGebraPreferences.EXPORT_WS_RESET_ICON,
							"false")).booleanValue());
			cbShowFrame.setSelected(Boolean.valueOf(
					ggbPref.loadPreference(GeoGebraPreferences.EXPORT_WS_FRAME_POSSIBLE,
							"false")).booleanValue());
			cbOpenButton.setSelected(Boolean.valueOf(
					ggbPref.loadPreference(GeoGebraPreferences.EXPORT_WS_BUTTON_TO_OPEN,
							"false")).booleanValue());

			cbShowMenuBar.setSelected(Boolean.valueOf(
					ggbPref.loadPreference(GeoGebraPreferences.EXPORT_WS_SHOW_MENUBAR,
							"false")).booleanValue());
			cbSavePrint.setSelected(Boolean.valueOf(
					ggbPref.loadPreference(GeoGebraPreferences.EXPORT_WS_SAVE_PRINT,
							"false")).booleanValue());
			cbUseBrowserForJavaScript.setSelected(Boolean
					.valueOf(
							ggbPref.loadPreference(
									GeoGebraPreferences.EXPORT_WS_USE_BROWSER_FOR_JAVASCRIPT,
									"true")).booleanValue());
			cbIncludeHTML5.setSelected(Boolean.valueOf(
					ggbPref.loadPreference(GeoGebraPreferences.EXPORT_WS_INCLUDE_HTML5,
							"false")).booleanValue());
			cbShowToolBar.setSelected(Boolean.valueOf(
					ggbPref.loadPreference(GeoGebraPreferences.EXPORT_WS_SHOW_TOOLBAR,
							"false")).booleanValue());
			cbShowToolBarHelp.setSelected(Boolean.valueOf(
					ggbPref.loadPreference(
							GeoGebraPreferences.EXPORT_WS_SHOW_TOOLBAR_HELP, "false"))
					.booleanValue());
			cbShowToolBarHelp.setEnabled(cbShowToolBar.isSelected());
			cbShowInputField.setSelected(Boolean.valueOf(
					ggbPref.loadPreference(
							GeoGebraPreferences.EXPORT_WS_SHOW_INPUT_FIELD, "false"))
					.booleanValue());
			cbAllowRescaling.setSelected(Boolean.valueOf(
					ggbPref.loadPreference(GeoGebraPreferences.EXPORT_WS_ALLOW_RESCALING,
							"true")).booleanValue());
			cbRemoveLinebreaks.setSelected(Boolean.valueOf(
					ggbPref.loadPreference(
							GeoGebraPreferences.EXPORT_WS_REMOVE_LINEBREAKS, "false"))
					.booleanValue());
			removeLineBreaks = cbRemoveLinebreaks.isSelected();
			// cbOfflineArchive.setSelected( Boolean.valueOf(ggbPref.loadPreference(
			// GeoGebraPreferences.EXPORT_WS_OFFLINE_ARCHIVE, "false")).booleanValue()
			// );
			cbOfflineJars.setSelected(Boolean.valueOf(
					ggbPref.loadPreference(GeoGebraPreferences.EXPORT_WS_OFFLINE_ARCHIVE,
							"false")).booleanValue());
			addHeight();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addHeight() {
		int height = 0;

		if (cbShowToolBar.isSelected()) {
			height += guiManager.getToolBarHeight();
		}
		if (cbShowMenuBar.isSelected()) {
			height += guiManager.getMenuBarHeight();
		}
		if (cbShowInputField.isSelected()) {
			height += guiManager.getAlgebraInputHeight();
		}

		sizePanel.setValues(sizePanel.getSelectedWidth(),
				sizePanel.getSelectedHeight() + height, false);
	}

	private void savePreferences() {
		ggbPref.savePreference(GeoGebraPreferences.EXPORT_WS_RIGHT_CLICK,
				Boolean.toString(cbEnableRightClick.isSelected()));
		ggbPref.savePreference(GeoGebraPreferences.EXPORT_WS_LABEL_DRAGS,
				Boolean.toString(cbEnableLabelDrags.isSelected()));
		ggbPref.savePreference(GeoGebraPreferences.EXPORT_WS_RESET_ICON,
				Boolean.toString(cbShowResetIcon.isSelected()));
		ggbPref.savePreference(GeoGebraPreferences.EXPORT_WS_FRAME_POSSIBLE,
				Boolean.toString(cbShowFrame.isSelected()));
		ggbPref.savePreference(GeoGebraPreferences.EXPORT_WS_SHOW_MENUBAR,
				Boolean.toString(cbShowMenuBar.isSelected()));
		ggbPref.savePreference(GeoGebraPreferences.EXPORT_WS_SHOW_TOOLBAR,
				Boolean.toString(cbShowToolBar.isSelected()));
		ggbPref.savePreference(GeoGebraPreferences.EXPORT_WS_SHOW_TOOLBAR_HELP,
				Boolean.toString(cbShowToolBarHelp.isSelected()));
		ggbPref.savePreference(GeoGebraPreferences.EXPORT_WS_SHOW_INPUT_FIELD,
				Boolean.toString(cbShowInputField.isSelected()));
		// ggbPref.savePreference(GeoGebraPreferences.EXPORT_WS_OFFLINE_ARCHIVE,
		// Boolean.toString(cbOfflineArchive.isSelected()));
		ggbPref.savePreference(GeoGebraPreferences.EXPORT_WS_SAVE_PRINT,
				Boolean.toString(cbSavePrint.isSelected()));
		ggbPref.savePreference(
				GeoGebraPreferences.EXPORT_WS_USE_BROWSER_FOR_JAVASCRIPT,
				Boolean.toString(cbUseBrowserForJavaScript.isSelected()));
		ggbPref.savePreference(GeoGebraPreferences.EXPORT_WS_INCLUDE_HTML5,
				Boolean.toString(cbIncludeHTML5.isSelected()));
		ggbPref.savePreference(GeoGebraPreferences.EXPORT_WS_ALLOW_RESCALING,
				Boolean.toString(cbAllowRescaling.isSelected()));
		ggbPref.savePreference(GeoGebraPreferences.EXPORT_WS_REMOVE_LINEBREAKS,
				Boolean.toString(cbRemoveLinebreaks.isSelected()));
		ggbPref.savePreference(GeoGebraPreferences.EXPORT_WS_BUTTON_TO_OPEN,
				Boolean.toString(cbOpenButton.isSelected()));
		ggbPref.savePreference(GeoGebraPreferences.EXPORT_WS_OFFLINE_ARCHIVE,
				Boolean.toString(cbOfflineJars.isSelected()));
	}
	
	private JPanel createUploadPanel() {
		JPanel panel = new JPanel(new BorderLayout(5,5));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// title textfield
		titleField = new MyTextField(app);
		
		titleField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Construction cons = kernel.getConstruction();
				
				if (titleField.getText().equals(cons.getTitle()))
					return;
				cons.setTitle(titleField.getText());
				
				kernelChanged = true;
			}
		});
		
		titleField.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				Construction cons = kernel.getConstruction();
				
				if (titleField.getText().equals(cons.getTitle()))
					return;
				cons.setTitle(titleField.getText());
				
				kernelChanged = true;
			}
			
			public void focusGained(FocusEvent e) {}
		});
		
		JPanel p = new JPanel(new BorderLayout(5, 5));
		p.add(new JLabel(app.getPlain("Title") + ": "), BorderLayout.WEST);
		p.add(titleField, BorderLayout.CENTER);
		panel.add(p, BorderLayout.NORTH);
		
		// text areas
		JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
		JLabel label = new JLabel(app.getPlain("TextBeforeConstruction") + ":");
		textAboveUpload = new InputPanel(null, app, 5, 40, true);
		// JScrollPane scrollPane = new JScrollPane(textAbove);

		p = new JPanel(new BorderLayout());
		p.add(label, BorderLayout.NORTH);
		p.add(textAboveUpload, BorderLayout.CENTER);
		centerPanel.add(p, BorderLayout.CENTER);

		label = new JLabel(app.getPlain("TextAfterConstruction") + ":");
		textBelowUpload = new InputPanel(null, app, 8, 40, true);
		
		p = new JPanel(new BorderLayout());
		p.add(label, BorderLayout.NORTH);
		p.add(textBelowUpload, BorderLayout.CENTER);
		centerPanel.add(p, BorderLayout.SOUTH);
		
		panel.add(centerPanel, BorderLayout.CENTER);
		
		return panel;
	}

	/**
	 * The General Settings Tab contains some of the more general settings.
	 */
	private JPanel createGeneralSettingsTab() {
		JPanel tab = new JPanel(new BorderLayout(5, 5));
		tab.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// text areas
		JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
		JLabel label = new JLabel(app.getPlain("TextBeforeConstruction") + ":");
		textAbove = new InputPanel(null, app, 5, 40, true);
		// JScrollPane scrollPane = new JScrollPane(textAbove);

		JPanel p = new JPanel(new BorderLayout());
		p.add(label, BorderLayout.NORTH);
		p.add(textAbove, BorderLayout.SOUTH);
		centerPanel.add(p, BorderLayout.NORTH);

		label = new JLabel(app.getPlain("TextAfterConstruction") + ":");
		textBelow = new InputPanel(null, app, 8, 40, true);
		// scrollPane = new JScrollPane(textBelow);
		p = new JPanel(new BorderLayout());
		p.add(label, BorderLayout.NORTH);
		p.add(textBelow, BorderLayout.SOUTH);
		centerPanel.add(p, BorderLayout.SOUTH);

		// TODO set line wrapping
		tab.add(centerPanel, BorderLayout.CENTER);

		return tab;
	}

	private JPanel createAdvancedSettingsTab() {
		JPanel tab = new JPanel();
		tab.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		// tab.setLayout(new BoxLayout(tab, BoxLayout.Y_AXIS));
		tab.setLayout(new BorderLayout(5, 5));

		// functionality panel
		JPanel funcPanel = new JPanel();
		funcPanel.setBorder(BorderFactory.createTitledBorder(app
				.getMenu("Functionality")));
		funcPanel.setLayout(new BoxLayout(funcPanel, BoxLayout.Y_AXIS));
		tab.add(funcPanel, BorderLayout.WEST);

		// enable right click
		cbEnableRightClick = new JCheckBox(app.getMenu("EnableRightClick"));
		cbEnableRightClick.setEnabled(true);
		funcPanel.add(cbEnableRightClick);

		// enable label drags
		cbEnableLabelDrags = new JCheckBox(app.getMenu("EnableLabelDrags"));
		cbEnableLabelDrags.setEnabled(true);
		funcPanel.add(cbEnableLabelDrags);

		// showResetIcon
		cbShowResetIcon = new JCheckBox(app.getMenu("ShowResetIcon"));
		funcPanel.add(cbShowResetIcon);

		// framPossible
		cbShowFrame = new JCheckBox(app.getPlain("DoubleClickToOpen"));
		funcPanel.add(cbShowFrame);

		// button to open applet
		cbOpenButton = new JCheckBox(app.getPlain("OpenButton"));
		funcPanel.add(cbOpenButton);

		funcPanel.add(Box.createVerticalGlue());

		cbUseBrowserForJavaScript = new JCheckBox(app.getMenu("UseBrowserForJS"));
		funcPanel.add(cbUseBrowserForJavaScript);

		// GUI panel
		JPanel guiPanel = new JPanel();
		guiPanel.setLayout(new BoxLayout(guiPanel, BoxLayout.Y_AXIS));
		guiPanel.setBorder(BorderFactory.createTitledBorder(app
				.getMenu("UserInterface")));
		tab.add(guiPanel, BorderLayout.EAST);

		// two columns
		JPanel guiPanelWest = new JPanel();
		guiPanelWest.setLayout(new BoxLayout(guiPanelWest, BoxLayout.Y_AXIS));
		JPanel guiPanelEast = new JPanel();
		guiPanelEast.setLayout(new BoxLayout(guiPanelEast, BoxLayout.Y_AXIS));
		JPanel twoColumns = new JPanel();
		twoColumns.setLayout(new BorderLayout());
		twoColumns.add(guiPanelEast, BorderLayout.EAST);
		twoColumns.add(guiPanelWest, BorderLayout.WEST);
		twoColumns.setAlignmentX(LEFT_ALIGNMENT);
		guiPanel.add(twoColumns);

		// left column
		// showMenuBar
		cbShowMenuBar = new JCheckBox(app.getMenu("ShowMenuBar"));
		guiPanelWest.add(cbShowMenuBar);
		// showToolBar
		cbShowToolBar = new JCheckBox(app.getMenu("ShowToolBar"));
		guiPanelWest.add(cbShowToolBar);
		// showAlgebraInput
		cbShowInputField = new JCheckBox(app.getMenu("ShowInputField"));
		guiPanelWest.add(cbShowInputField);

		// right column
		// save, print
		cbSavePrint = new JCheckBox(app.getMenu("SavePrintUndo"));
		guiPanelEast.add(cbSavePrint);

		// showToolBarHelp
		cbShowToolBarHelp = new JCheckBox(app.getMenu("ShowToolBarHelp"));
		cbShowToolBarHelp.setEnabled(cbShowToolBar.isSelected());
		guiPanelEast.add(cbShowToolBarHelp);

		// Allow Rescaling
		cbAllowRescaling = new JCheckBox(app.getMenu("AllowRescaling"));
		guiPanelEast.add(cbAllowRescaling);

		// width and height of applet, info about double clicking
		int width, height;
		JPanel appCP = app.getCenterPanel();
		if (appCP != null) {
			width = appCP.getWidth();
			height = appCP.getHeight();
		} else {
			width = DEFAULT_APPLET_WIDTH;
			height = DEFAULT_APPLET_HEIGHT;
		}
		sizePanel = new GraphicSizePanel(app, width, height, false);
		sizePanel.setAlignmentX(LEFT_ALIGNMENT);
		guiPanel.add(sizePanel);

		// Files panel
		JPanel filePanel = new JPanel();
		filePanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Files")));
		filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
		tab.add(filePanel, BorderLayout.SOUTH);

		// two columns
		JPanel filePanelWest = new JPanel();
		filePanelWest.setLayout(new BoxLayout(filePanelWest, BoxLayout.Y_AXIS));
		JPanel filePanelEast = new JPanel();

		filePanelEast.setLayout(new BorderLayout(5, 5));
		filePanelEast.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JPanel fileTwoColumns = new JPanel();
		fileTwoColumns.setLayout(new BorderLayout());
		fileTwoColumns.add(filePanelEast, BorderLayout.EAST);
		fileTwoColumns.add(filePanelWest, BorderLayout.WEST);
		fileTwoColumns.setAlignmentX(LEFT_ALIGNMENT);
		filePanel.add(fileTwoColumns);

		// left column
		// include HTML5
		cbIncludeHTML5 = new JCheckBox(app.getMenu("IncludeHTML5"));
		if (GeoGebra.IS_PRE_RELEASE) filePanelWest.add(cbIncludeHTML5);

		// download jar files
		cbOfflineJars = new JCheckBox(app.getPlain("IncludeJARFiles"));
		filePanelWest.add(cbOfflineJars);

		// remove line breaks
		cbRemoveLinebreaks = new JCheckBox(app.getMenu("RemoveLineBreaks"));
		filePanelWest.add(cbRemoveLinebreaks);

		cbRemoveLinebreaks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				removeLineBreaks = cbRemoveLinebreaks.isSelected();
			}
		});

		// right column
		// file type (file/clipboard, mediaWiki)
		String fileTypeStrings[] = { app.getPlain("File.HTML"),
				app.getPlain("Clipboard.HTML"), app.getPlain("Clipboard.MediaWiki"),
				app.getPlain("Clipboard.Google"), app.getPlain("Clipboard.Moodle") };
		cbFileType = new JComboBox(fileTypeStrings);
		cbFileType.setEnabled(true);
		filePanelEast.add(cbFileType, BorderLayout.NORTH);
		cbFileType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (exportButton != null)
					if (cbFileType.getSelectedIndex() == TYPE_HTMLFILE) {
						exportButton.setText(app.getMenu("Export"));
						if (cbAllWorksheets != null)
							cbAllWorksheets.setEnabled(GeoGebraFrame.getInstanceCount() > 1);
						cbOfflineJars.setEnabled(true);
					} else {
						exportButton.setText(app.getMenu("Clipboard"));
						if (cbAllWorksheets != null) {
							cbAllWorksheets.setSelectedIndex(TYPE_SINGLE_FILE);
							cbAllWorksheets.setEnabled(false);
						}
						cbOfflineJars.setSelected(false);
						cbOfflineJars.setEnabled(false);
					}
			}
		});

		// single or multiple constructions in applet when more than one
		// construction open
		if (GeoGebraFrame.getInstanceCount() > 1) {
			String worksheetStrings[] = { app.getMenu("SingleFile"),
					app.getMenu("SingleFileTabs"), app.getMenu("LinkedFiles") };
			cbAllWorksheets = new JComboBox(worksheetStrings);
			filePanelEast.add(cbAllWorksheets, BorderLayout.SOUTH);
		}

		/*
		 * // to load a ggb file we need a signed jar (cbSavePrint) or // offline
		 * jar files cbGgbFile.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent ae) { if (cbGgbFile.isSelected() &&
		 * !cbSavePrint.isSelected()) { cbOfflineArchive.setSelected(true); } } });
		 * 
		 * // use archive from geogebra.org? cbOfflineArchive = new JCheckBox("jar "
		 * + app.getMenu("Files")); appletPanel.add(cbOfflineArchive);
		 */

		ActionListener heightChanger = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JCheckBox src = (JCheckBox) ae.getSource();

				int heightChange = 0;
				if (src == cbShowToolBar) {
					heightChange = guiManager.getToolBarHeight();
					if (!cbShowToolBarHelp.isSelected())
						cbShowToolBarHelp.setSelected(false);
					cbShowToolBarHelp.setEnabled(cbShowToolBar.isSelected());
				} else if (src == cbShowInputField) {
					heightChange = guiManager.getAlgebraInputHeight();
				}

				if (!src.isSelected())
					heightChange = -heightChange;

				sizePanel.setValues(sizePanel.getSelectedWidth(),
						sizePanel.getSelectedHeight() + heightChange, false);
			}
		};

		cbShowToolBar.addActionListener(heightChanger);
		cbShowMenuBar.addActionListener(heightChanger);
		cbShowInputField.addActionListener(heightChanger);

		return tab;
	}

	public void setVisible(boolean flag) {
		if (flag) {
			checkEuclidianView();
			loadPreferences();
			super.setVisible(true);
		} else {
			// store the texts of the text ares in
			// the current construction
			Construction cons = kernel.getConstruction();
			
			if(modeSwitch.getSelectedIndex() == TAB_HTML) {
				cons.setWorksheetText(textAbove.getText(), 0);
				cons.setWorksheetText(textBelow.getText(), 1);
			} else {
				cons.setWorksheetText(textAboveUpload.getText(), 0);
				cons.setWorksheetText(textBelowUpload.getText(), 1);
			}

			savePreferences();
			super.setVisible(false);
		}
	}

	private void centerOnScreen() {
		pack();
		setLocationRelativeTo(app.getMainComponent());
	}

	private void exportToClipboard(int type) throws IOException {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection stringSelection = null;

		switch (type) {
		case TYPE_HTMLCLIPBOARD:
			stringSelection = new StringSelection(getHTML(app, null, null, null));
			break;

		case TYPE_MEDIAWIKI:
			stringSelection = new StringSelection(getMediaWiki());
			break;

		case TYPE_GOOGLEGADGET:
			stringSelection = new StringSelection(getGoogleGadget());
			break;

		case TYPE_MOODLE:
			int appletWidth,
			appletHeight;
			if (cbOpenButton.isSelected()) { // change width and height for open
																				// button
				appletWidth = BUTTON_WIDTH;
				appletHeight = BUTTON_HEIGHT;
			} else {
				appletWidth = sizePanel.getSelectedWidth();
				appletHeight = sizePanel.getSelectedHeight();
			}

			stringSelection = new StringSelection(getAppletTag(app, null,
					appletWidth, appletHeight, false, removeLineBreaks,
					cbIncludeHTML5.isSelected(), false));
			break;

		// case TYPE_JSXGRAPH:
		// stringSelection = new StringSelection(getJSXGraph());
		// break;

		// case TYPE_JAVASCRIPT:
		// stringSelection = new StringSelection(getJavaScript());
		// break;
		}

		clipboard.setContents(stringSelection, null);

	}

	/**
	 * Exports construction as html worksheet and returns success state
	 */
	private void exportHTMLtoFile() {
		File htmlFile = null;

		File currFile = Application.removeExtension(app.getCurrentFile());
		if (currFile != null)
			htmlFile = Application.addExtension(currFile, Application.FILE_EXT_HTML);

		htmlFile = guiManager.showSaveDialog(Application.FILE_EXT_HTML, htmlFile,
				app.getPlain("html") + " " + app.getMenu("Files"), true, false);
		if (htmlFile == null)
			return;

		try {
			// save construction file
			// as worksheet_file.ggb
			File ggbFile = null;

			/*
			 * if (cbOfflineJars.isSelected()) { String ggbFileName =
			 * Application.removeExtension(htmlFile).getName() + ".ggb"; ggbFile = new
			 * File(htmlFile.getParent(), ggbFileName);
			 * app.getXMLio().writeGeoGebraFile(ggbFile); }
			 */

			// write html string to file
			// UTF8 needed for eg writing out JavaScript
			FileOutputStream fos = new FileOutputStream(htmlFile);
			Writer fw = new OutputStreamWriter(fos, "UTF8");

			fw.write(getHTML(app, ggbFile, null, null));
			fw.close();

			final File HTMLfile = htmlFile;
			// copy files and open browser
			Thread runner = new Thread() {
				public void run() {
					try {

						// copy jar to same directory as ggbFile
						if (cbOfflineJars.isSelected()) {
							// copy all jar files
							copyJarsTo(getAppletCodebase(), HTMLfile.getParent());
						}

						// open html file in browser
						guiManager.showURLinBrowser(HTMLfile.toURL());
					} catch (Exception ex) {
						app.showError("SaveFileFailed");
						Application.debug(ex.toString());
					}
				}
			};
			runner.start();

		} catch (Exception ex) {
			app.showError("SaveFileFailed");
			Application.debug(ex.toString());
		}
	}

	/**
	 * Exports all open ggb files as one html files with Tabs to choose
	 * 
	 * @throws IOException
	 */
	private void exportAllOpenFilesToHTMLasTabs() throws IOException {

		File htmlFile = null;

		File currFile = Application.removeExtension(app.getCurrentFile());
		if (currFile != null)
			htmlFile = Application.addExtension(currFile, Application.FILE_EXT_HTML);

		htmlFile = guiManager.showSaveDialog(Application.FILE_EXT_HTML, htmlFile,
				app.getPlain("html") + " " + app.getMenu("Files"), true, false);

		if (htmlFile == null)
			return;

		StringBuilder sb = new StringBuilder();

		appendWithLineBreak(
				sb,
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
		appendWithLineBreak(sb, "<html lang=\"en\">");
		appendWithLineBreak(sb, "<head>");
		appendWithLineBreak(sb,
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">");
		appendWithLineBreak(sb, "<title>" + kernel.getConstruction().getTitle()
				+ "</title>");
		appendWithLineBreak(sb,
				"<script type=\"text/javascript\" src=\"tabber.js\"></script>");
		appendWithLineBreak(sb,
				"<link rel=\"stylesheet\" href=\"ggb.css\" TYPE=\"text/css\" MEDIA=\"screen\">");
		// appendWithLineBreak(sb,"<link rel=\"stylesheet\" href=\"example-print.css\" TYPE=\"text/css\" MEDIA=\"print\">");

		appendWithLineBreak(sb, "<script type=\"text/javascript\">");

		appendWithLineBreak(sb,
				"document.write('<style type=\"text/css\">.tabber{display:none;}<\\/style>');");
		appendWithLineBreak(sb, "</script>");

		appendWithLineBreak(sb, "</head>");
		appendWithLineBreak(sb, "<body>");

		appendWithLineBreak(sb, "<h1>Tabber Example</h1>");

		appendWithLineBreak(sb, "<div class=\"tabber\">");

		final ArrayList<GeoGebraFrame> ggbInstances = GeoGebraFrame.getInstances();

		int size = ggbInstances.size();

		for (int i = 0; i < size; i++) {
			GeoGebraFrame ggb = (GeoGebraFrame) ggbInstances.get(i);
			Application application = ggb.getApplication();

			appendWithLineBreak(sb, "<div class=\"tabbertab\">");

			// appendWithLineBreak(sb,"<h2>Tab 1</h2>");
			sb.append("<h2>");
			File file = application.getCurrentFile();

			if (file != null)
				sb.append(file.getName());
			else
				sb.append("Tab " + (i + 1));

			sb.append("</h2>");

			// appendWithLineBreak(sb,"<p>Tab 1 content.</p>");
			sb.append("<p>");

			sb.append(getAppletTag(application, null, sizePanel.getSelectedWidth(),
					sizePanel.getSelectedHeight(), false, removeLineBreaks,
					cbIncludeHTML5.isSelected(), true));
			sb.append("</p>");

			appendWithLineBreak(sb, "</div>");

		}

		appendWithLineBreak(sb, "</div>");

		appendWithLineBreak(sb, "</body>");
		appendWithLineBreak(sb, "</html>");

		FileOutputStream fos = new FileOutputStream(htmlFile);
		Writer fw = new OutputStreamWriter(fos, "UTF8");
		fw.write(sb.toString());
		fw.close();

		String text = app.loadTextFile("/geogebra/export/ggb.css");
		fos = new FileOutputStream(htmlFile.getParent() + "/ggb.css");
		fw = new OutputStreamWriter(fos, "UTF8");
		fw.write(text);
		fw.close();

		text = app.loadTextFile("/geogebra/export/tabber.js");
		fos = new FileOutputStream(htmlFile.getParent() + "/tabber.js");
		fw = new OutputStreamWriter(fos, "UTF8");
		fw.write(text);
		fw.close();

		final File HTMLfile = htmlFile;
		// open browser
		Thread runner = new Thread() {
			public void run() {
				try {

					// open html file in browser
					guiManager.showURLinBrowser(HTMLfile.toURL());
				} catch (Exception ex) {
					app.showError("SaveFileFailed");
					Application.debug(ex.toString());
				}
			}
		};
		runner.start();

	}

	/**
	 * Exports all open ggb files as separate html files linked with Next and Back
	 * buttons
	 * 
	 * @throws IOException
	 */
	private void exportAllOpenFilesToHTML() throws IOException {

		final ArrayList<GeoGebraFrame> ggbInstances = GeoGebraFrame.getInstances();

		int size = ggbInstances.size();

		File htmlFile = null;

		File currFile = Application.removeExtension(app.getCurrentFile());
		if (currFile != null)
			htmlFile = Application.addExtension(currFile, Application.FILE_EXT_HTML);

		htmlFile = guiManager.showSaveDialog(Application.FILE_EXT_HTML, htmlFile,
				app.getPlain("html") + " " + app.getMenu("Files"), false, false);

		if (htmlFile == null)
			return;

		String fileBase = Application.removeExtension(htmlFile.getName());

		StringBuilder next = new StringBuilder();
		StringBuilder prev = new StringBuilder();

		for (int i = 0; i < size; i++) {
			GeoGebraFrame ggb = (GeoGebraFrame) ggbInstances.get(i);
			Application application = ggb.getApplication();

			htmlFile = new File(fileBase + (i + 1) + ".html");
			// Application.debug("writing "+fileBase+(i+1)+".html");
			FileOutputStream fos = new FileOutputStream(htmlFile);
			Writer fw = new OutputStreamWriter(fos, "UTF8");

			next.setLength(0);
			prev.setLength(0);

			if (i > 0) {
				prev.append(fileBase);
				prev.append((i + 0) + "");
				prev.append(".html");
			}
			if (i < size - 1) {
				next.append(fileBase);
				next.append((i + 2) + "");
				next.append(".html");
			}

			fw.write(getHTML(application, null, next.toString(), prev.toString()));
			fw.close();

		}

		final File HTMLfile = new File(fileBase + "1.html");
		// open browser
		Thread runner = new Thread() {
			public void run() {
				try {

					// open html file in browser
					guiManager.showURLinBrowser(HTMLfile.toURL());
				} catch (Exception ex) {
					app.showError("SaveFileFailed");
					Application.debug(ex.toString());
				}
			}
		};
		runner.start();
	}

	/**
	 * Returns the code base for exported applet depending on whether a signed or
	 * unsigned applet is needed for the options set.
	 */
	private URL getAppletCodebase() {
		URL codebase = Application.getCodeBase();
		if (!cbSavePrint.isSelected()) {
			try {
				codebase = new URL(Application.getCodeBase(), "unsigned/");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return codebase;
	}

	/**
	 * Copies all jar files to the given destination directory.
	 * 
	 * @param codeBase
	 * @param destDir
	 * @throws Exception
	 */
	public synchronized void copyJarsTo(URL codeBase, String destDir)
			throws Exception {
		for (int i = 0; i < Application.JAR_FILES.length; i++) {
			// jar file
			URL src = new URL(codeBase, Application.JAR_FILES[i]);
			File dest = new File(destDir, Application.JAR_FILES[i]);
			DownloadManager.copyURLToFile(src, dest);

			// jar.pack.gz file
			// try {
			// src = new URL(codeBase, Application.JAR_FILES[i] + ".pack.gz");
			// dest = new File(destDir, Application.JAR_FILES[i] + ".pack.gz");
			// DownloadManager.copyURLToFile(src, dest);
			// } catch (Exception e) {
			// System.err.println("could not copy: " + Application.JAR_FILES[i] +
			// ".pack.gz");
			// }
		}
	}

	// private void packFile(File jarFile) {
	// Packer packer = Pack200.newPacker();
	//
	// // Initialize the state by setting the desired properties
	// Map p = packer.properties();
	// // take more time choosing codings for better compression
	// p.put(Packer.EFFORT, "7"); // default is "5"
	// // use largest-possible archive segments (>10% better compression).
	// p.put(Packer.SEGMENT_LIMIT, "-1");
	// // reorder files for better compression.
	// p.put(Packer.KEEP_FILE_ORDER, Packer.FALSE);
	// // smear modification times to a single value.
	// p.put(Packer.MODIFICATION_TIME, Packer.LATEST);
	// // ignore all JAR deflation requests,
	// // transmitting a single request to use "store" mode.
	// p.put(Packer.DEFLATE_HINT, Packer.FALSE);
	// // discard debug attributes
	// p.put(Packer.CODE_ATTRIBUTE_PFX+"LineNumberTable", Packer.STRIP);
	// // throw an error if an attribute is unrecognized
	// p.put(Packer.UNKNOWN_ATTRIBUTE, Packer.ERROR);
	//
	// try {
	// JarFile jarFile = new JarFile("/tmp/testref.jar");
	// FileOutputStream fos = new FileOutputStream("/tmp/test.pack");
	// // Call the packer
	// packer.pack(jarFile, fos);
	// jarFile.close();
	// fos.close();
	//
	// File f = new File("/tmp/test.pack");
	// FileOutputStream fostream = new FileOutputStream("/tmp/test.jar");
	// JarOutputStream jostream = new JarOutputStream(fostream);
	// Unpacker unpacker = Pack200.newUnpacker();
	// // Call the unpacker
	// unpacker.unpack(f, jostream);
	// // Must explicitly close the output.
	// jostream.close();
	// } catch (IOException ioe) {
	// ioe.printStackTrace();
	// }
	// }

	public static boolean appendBase64(Application app, StringBuilder sb) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			app.getXMLio().writeGeoGebraFile(baos, false);
			sb.append(geogebra.util.Base64.encode(baos.toByteArray(), 0));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * returns string like this: <ggb_applet width="585" height="470"
	 * ggbBase64="..." framePossible = "false" showResetIcon = "true"
	 * showAnimationButton = "true" enableRightClick = "false" errorDialogsActive
	 * = "true" enableLabelDrags = "false" showMenuBar = "false" showToolBar =
	 * "true" showToolBarHelp = "true" showAlgebraInput = "false" /> for insertion
	 * into MediaWiki
	 */
	private String getMediaWiki() throws IOException {
		StringBuilder sb = new StringBuilder();

		sb.append("<ggb_applet width=\"");
		sb.append(sizePanel.getSelectedWidth());
		sb.append("\" height=\"");
		sb.append(sizePanel.getSelectedHeight());
		sb.append("\" ");

		// GeoGebra version
		sb.append(" version=\"");
		sb.append(GeoGebra.SHORT_VERSION_STRING);
		sb.append("\" ");

		// base64 encoding
		sb.append("ggbBase64=\"");
		appendBase64(app, sb);
		/*
		 * ByteArrayOutputStream baos = new ByteArrayOutputStream(); try {
		 * app.getXMLio().writeGeoGebraFile(baos, false);
		 * sb.append(geogebra.util.Base64.encode(baos.toByteArray(), 0)); } catch
		 * (IOException e) { e.printStackTrace(); }
		 */
		sb.append("\"");

		appendGgbAppletParameters(sb, TYPE_MEDIAWIKI);

		sb.append(" />");

		return sb.toString();

	}

	/*
	 * private String getJavaScript() throws IOException { StringBuilder sb = new
	 * StringBuilder();
	 * 
	 * appendWithLineBreak(sb,
	 * "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
	 * appendWithLineBreak(sb,
	 * "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
	 * appendWithLineBreak(sb, "<html xmlns=\"http://www.w3.org/1999/xhtml\">");
	 * appendWithLineBreak(sb, "<head>"); appendWithLineBreak(sb, "<title>");
	 * Construction cons = kernel.getConstruction(); String title =
	 * cons.getTitle(); sb.append(Util.toHTMLString(title));
	 * appendWithLineBreak(sb, "</title>"); appendWithLineBreak(sb, "<body>");
	 * 
	 * appendWithLineBreak(sb, "<script type=\"text/javascript\">");
	 * 
	 * // base64 encoding sb.append("loadBase64Unzipped('");
	 * appendBase64Unzipped(sb); appendWithLineBreak(sb, "');");
	 * appendWithLineBreak(sb, "</script>"); appendWithLineBreak(sb, "</body>");
	 * appendWithLineBreak(sb, "</html>"); return sb.toString();
	 * 
	 * }
	 */

	private void appendWithLineBreak(StringBuilder sb, String string) {
		sb.append(string);
		if (!removeLineBreaks)
			sb.append('\n');
		else
			sb.append(' ');

	}

	/*
	 * private String getJSXGraph() throws IOException { StringBuilder sb = new
	 * StringBuilder();
	 * 
	 * appendWithLineBreak(sb,
	 * "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
	 * appendWithLineBreak(sb,
	 * "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
	 * appendWithLineBreak(sb, "<html xmlns=\"http://www.w3.org/1999/xhtml\">");
	 * appendWithLineBreak(sb, "<head>"); appendWithLineBreak(sb, "<title>");
	 * Construction cons = kernel.getConstruction(); String title =
	 * cons.getTitle(); sb.append(Util.toHTMLString(title));
	 * appendWithLineBreak(sb, "</title>");
	 * 
	 * appendWithLineBreak(sb, " <style type=\"text/css\">");
	 * appendWithLineBreak(sb, "  .jxgbox {"); appendWithLineBreak(sb,
	 * "    position:relative;"); appendWithLineBreak(sb, "    overflow:hidden;");
	 * appendWithLineBreak(sb, "    background-color:#ffffff;");
	 * appendWithLineBreak(sb, "    border-style:solid;"); appendWithLineBreak(sb,
	 * "    border-width:1px;"); appendWithLineBreak(sb,
	 * "    border-color:#356AA0;"); appendWithLineBreak(sb,
	 * "    -moz-border-radius:10px;"); appendWithLineBreak(sb,
	 * "    -webkit-border-radius:10px;"); appendWithLineBreak(sb, "  }");
	 * appendWithLineBreak(sb, "  .JXGtext {"); appendWithLineBreak(sb,
	 * "    background-color:transparent;"); appendWithLineBreak(sb,
	 * "    font-family: Arial, Helvetica, Geneva;"); appendWithLineBreak(sb,
	 * "    font-size:11px;"); appendWithLineBreak(sb, "    padding:0px;");
	 * appendWithLineBreak(sb, "    margin:0px;"); appendWithLineBreak(sb, "  }");
	 * appendWithLineBreak(sb, "  </style>");
	 * 
	 * appendWithLineBreak(sb,
	 * "  <script type=\"text/javascript\" src=\"http://jsxgraph.uni-bayreuth.de/~alfred/jsxgraph/branches/0.80/src/loadjsxgraph.js\"></script>"
	 * ); appendWithLineBreak(sb,
	 * "  <script type=\"text/javascript\" src=\"http://jsxgraph.uni-bayreuth.de/~alfred/jsxgraph/branches/0.80/src/GeogebraReader.js\"></script>"
	 * ); appendWithLineBreak(sb, "</head>"); appendWithLineBreak(sb, "<body>");
	 * sb.append("<div id=\"box\" class=\"jxgbox\" style=\"width:");
	 * sb.append(sizePanel.getSelectedWidth()+""); sb.append("px; height:");
	 * sb.append(sizePanel.getSelectedHeight()+""); appendWithLineBreak(sb,
	 * "px;\"></div>");
	 * 
	 * sb.append(getFooter(cons, true));
	 * 
	 * appendWithLineBreak(sb,
	 * "<div id=\"debug\" style=\"display:block;\"></div>");
	 * appendWithLineBreak(sb, "<script type=\"text/javascript\">");
	 * appendWithLineBreak(sb, "JXG.JSXGraph.licenseText = '';");
	 * sb.append("JXG.JSXGraph.loadBoardFromString('box','");
	 * 
	 * // append base64 encoded XML (not zipped)
	 * sb.append(geogebra.util.Base64.encode
	 * (app.getXMLio().getFullXML().getBytes(),0));
	 * 
	 * appendWithLineBreak(sb, "', 'Geogebra');"); appendWithLineBreak(sb,
	 * "</script>"); appendWithLineBreak(sb, "</body>"); appendWithLineBreak(sb,
	 * "</html>"); return sb.toString();
	 * 
	 * }
	 */

	private String getGoogleGadget() throws IOException {
		StringBuilder sb = new StringBuilder();

		appendWithLineBreak(sb, "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		appendWithLineBreak(sb, "<Module>");
		sb.append("<ModulePrefs title=\"");
		Construction cons = kernel.getConstruction();
		String title = cons.getTitle();
		if (!title.equals("")) {
			sb.append(Util.toHTMLString(title));
		} else {
			sb.append("GeoGebra Gadget");
		}
		sb.append("\" height=\"");
		sb.append(sizePanel.getSelectedHeight());
		sb.append("\" width=\"");
		sb.append(sizePanel.getSelectedWidth());
		sb.append("\" scrolling=\"false\" ");
		sb.append("author=\"");
		sb.append(Util.toHTMLString(cons.getAuthor()));
		sb.append("\" author_email=\"xxx@google.com\" ");
		appendWithLineBreak(
				sb,
				"description=\"GeoGebra applet as a Google-Site gadget\" thumbnail=\"http://www.geogebra.org/static/images/geogebra_logo67x60.png\">");
		appendWithLineBreak(sb, "</ModulePrefs>");

		appendWithLineBreak(sb, "<Content type=\"html\">");
		appendWithLineBreak(sb, "<![CDATA[");
		appendWithLineBreak(sb, "<div id='ggbapplet'>");

		sb.append(getAppletTag(app, null, sizePanel.getSelectedWidth(),
				sizePanel.getSelectedHeight(), true, removeLineBreaks,
				cbIncludeHTML5.isSelected(), true));

		appendWithLineBreak(sb, "</div>");
		appendWithLineBreak(sb, "]]>");
		appendWithLineBreak(sb, "</Content>");
		appendWithLineBreak(sb, "</Module>");

		return sb.toString();

	}

	/**
	 * Returns a html page with the applet included
	 * 
	 * @param ggbFile
	 *          construction File
	 */
	private String getHTML(Application app2, File ggbFile, String nextLink,
			String previousLink) {
		StringBuilder sb = new StringBuilder();

		// applet width
		int appletWidth, appletHeight;
		if (cbOpenButton.isSelected()) { // change width and height for open button
			appletWidth = BUTTON_WIDTH;
			appletHeight = BUTTON_HEIGHT;
		} else {
			appletWidth = sizePanel.getSelectedWidth();
			appletHeight = sizePanel.getSelectedHeight();
		}

		// width for table
		int pageWidth = Math.max(appletWidth, DEFAULT_HTML_PAGE_WIDTH);
		
		Construction cons = app2.getKernel().getConstruction();

		// xhtml header
		// Michael Borcherds 2008-05-01
		// xhtml header
		// The declaration may be optionally omitted because it declares as its
		// encoding the default encoding.
		// and casuses problems on some servers (when short php tags enabled)
		// sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

		// Gabor Ancsin 2010 05 20
		// html5 doctype
		appendWithLineBreak(sb, "<!DOCTYPE html>");
		appendWithLineBreak(sb, "<html>");
		appendWithLineBreak(sb, "<head>");
		String textBoth = textAbove.getText() + textBelow.getText();
		if (textBoth.contains("$$") || textBoth.contains("\\("))
			appendWithLineBreak(
					sb,
					"<script type=\"text/javascript\" "
							+ "src=\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML\"></script>");
		sb.append("<title>");
		Construction cons2 = app2.getKernel().getConstruction();
		String title = cons2.getTitle();
		if (!title.equals("")) {
			sb.append(Util.toHTMLString(title));
			sb.append(" - ");
		}
		sb.append(Util.toHTMLString(app2.getPlain("ApplicationName") + " "
				+ app2.getPlain("DynamicWorksheet")));
		appendWithLineBreak(sb, "</title>");
		// charset
		appendWithLineBreak(sb,
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		// sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />");

		appendWithLineBreak(sb, "<meta name=\"generator\" content=\"GeoGebra\" />");
		String css = app2.getSetting("cssDynamicWorksheet");
		if (css != null) {
			sb.append(css);
			appendWithLineBreak(sb, "");
		}
		appendWithLineBreak(sb, "</head>");

		// Guy Hed 14/03/2011
		if (app2.isRightToLeftReadingOrder())
			appendWithLineBreak(sb, "<body dir='rtl'>");
		else
			appendWithLineBreak(sb, "<body>");
		// -----------------
		appendWithLineBreak(sb, "<table border=\"0\" width=\"" + pageWidth + "\">");
		appendWithLineBreak(sb, "<tr><td>");

		// header with title
		if (!title.equals("")) {
			sb.append("<h2>");
			sb.append(Util.toHTMLString(title));
			appendWithLineBreak(sb, "</h2>");
		}

		// text before applet
		String text = app == app2 ? textAbove.getText() : cons2.getWorksheetText(0);
		if (text != null) {
			appendWithLineBreak(sb, "<p>");
			sb.append(Util.toHTMLString(text));
			appendWithLineBreak(sb, "</p>");
		}

		// include applet tag
		appendWithLineBreak(sb, "");
		sb.append(getAppletTag(app2, ggbFile, appletWidth, appletHeight, true,
				removeLineBreaks, cbIncludeHTML5.isSelected(), true));
		appendWithLineBreak(sb, "");

		// text after applet
		text = app == app2 ? textBelow.getText() : cons2.getWorksheetText(1);
		if (text != null) {
			appendWithLineBreak(sb, "<p>");
			sb.append(Util.toHTMLString(text));
			appendWithLineBreak(sb, "</p>");
		}

		sb.append(getFooter(cons2, false));

		appendWithLineBreak(sb, "</td></tr>");
		sb.append("</table>");

		if (previousLink != null && previousLink.length() > 0) {
			sb.append("<form method=\"post\" action=\"");
			sb.append(previousLink);
			appendWithLineBreak(sb, "\">");
			sb.append("<input type=\"submit\" value=\"");
			sb.append(app2.getMenu("Back"));
			appendWithLineBreak(sb, "\">");
			appendWithLineBreak(sb, "</form>");
		}

		if (nextLink != null && nextLink.length() > 0) {
			sb.append("<form method=\"post\" action=\"");
			sb.append(nextLink);
			appendWithLineBreak(sb, "\">");
			sb.append("<input type=\"submit\" value=\"");
			sb.append(app2.getMenu("Next"));
			appendWithLineBreak(sb, "\">");
			appendWithLineBreak(sb, "</form>");
		}

		appendJavaScript(sb);

		appendWithLineBreak(sb, "</body>");
		sb.append("</html>");

		return sb.toString();
	}

	private void appendJavaScript(StringBuilder sb) {
		appendWithLineBreak(sb, "<script type=\"text/javascript\">");

		appendWithLineBreak(sb, "var ggbApplet = document.ggbApplet;");
		sb.append(kernel.getLibraryJavaScript());

		Construction cons = kernel.getConstruction();
		TreeSet<GeoElement> geoSet = cons.getGeoSetConstructionOrder();

		Iterator<GeoElement> it = geoSet.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();

			String script = geo.getClickScript();
			if (!script.equals("") && geo.isClickJavaScript()) {
				// for each GeoElement with a JavaScript, create a function call
				// with the same name as the geo's label (prefixed by ggb)
				sb.append("function ggb");
				sb.append(geo.getLabel());
				appendWithLineBreak(sb, "() {");
				appendWithLineBreak(sb, "var ggbApplet = document.ggbApplet;");
				appendWithLineBreak(sb, script);
				appendWithLineBreak(sb, "}");

			}
		}

		appendWithLineBreak(sb, "");
		appendWithLineBreak(sb, "</script>");
	}

	private String getFooter(Construction cons, boolean JSXGraph) {
		StringBuilder sb = new StringBuilder();
		// footer
		// author and date information for footer
		String author = cons.getAuthor();
		String date = cons.getDate();
		String line = null;
		if (!author.equals("")) {
			line = author;
		}
		if (!date.equals("")) {
			if (line == null)
				line = date;
			else
				line = line + ", " + date;
		}

		sb.append("<p>");
		sb.append("<span style=\"font-size:small\">");
		if (line != null) {
			sb.append(Util.toHTMLString(line));
			sb.append(", ");
		}
		sb.append(guiManager.getCreatedWithHTML(JSXGraph));
		sb.append("</span>");
		appendWithLineBreak(sb, "</p>");

		return sb.toString();
	}

	public String getAppletTag(Application app, File ggbFile, int width,
			int height, boolean mayscript, boolean RemoveLineBreaks,
			boolean includeHTML5, boolean includeNoJavaMessage) {

		this.removeLineBreaks = RemoveLineBreaks;

		StringBuilder sb = new StringBuilder();

		// JavaScript version for non-Java devices eg Android, iPhone
		if (includeHTML5) {

			appendWithLineBreak(
					sb,
					"<script type=\"text/javascript\" language=\"javascript\" src=\"http://www.geogebra.org/mobile/4.0/geogebramobile/geogebramobile.nocache.js\"></script>");
			sb.append("<article class=\"geogebramobile\" style=\"width: ");
			sb.append(width);
			sb.append("px; height: ");
			sb.append(height);
			appendWithLineBreak(sb, "px; border: 1px solid black;\"");
			sb.append("data-param-ggbbase64=\"");
			appendBase64(app, sb);
			sb.append("\"");
			appendWithLineBreak(sb, "></article>");
			appendWithLineBreak(sb, "<noscript id=\"ggbappletwrapper\">");
		}

		// include applet
		sb.append("<applet name=\"ggbApplet\" code=\"geogebra.GeoGebraApplet\"");
		// archive geogebra.jar
		sb.append(" archive=\"geogebra.jar\"");

		if (cbOfflineJars.isSelected()) {
			// codebase for offline applet
			sb.append("\tcodebase=\"./\"");
		} else {
			// add codebase for online applets
			appendWithLineBreak(sb, "");
			sb.append("\tcodebase=\"");
			sb.append(GeoGebra.GEOGEBRA_ONLINE_ARCHIVE_BASE);
			if (!cbSavePrint.isSelected())
				sb.append("unsigned/");
			sb.append("\"");
		}

		// width, height
		appendWithLineBreak(sb, "");
		sb.append("\twidth=\"");
		sb.append(width);
		sb.append("\" height=\"");
		sb.append(height);
		sb.append("\"");
		if (mayscript && !cbUseBrowserForJavaScript.isSelected())
			sb.append(" mayscript=\"true\"");// add MAYSCRIPT to ensure ggbOnInit()
																				// can be called
		appendWithLineBreak(sb, ">");

		/*
		 * if (cbOfflineJars.isSelected() && ggbFile != null) { // ggb file
		 * sb.append("\t<param name=\"filename\" value=\"");
		 * sb.append(ggbFile.getName()); sb.append("\" />"); } else
		 */
		{
			// base64 encoding
			sb.append("\t<param name=\"ggbBase64\" value=\"");
			appendBase64(app, sb);
			// space before '/>' stops moodle stripping the paramater! 
			appendWithLineBreak(sb, "\" />");
		}

		// loading image for online applet
		if (!cbOfflineJars.isSelected()) {
			appendWithLineBreak(sb, "\t<param name=\"image\" value=\""
					+ GeoGebra.LOADING_GIF + "\" />");
			appendWithLineBreak(sb, "\t<param name=\"boxborder\" value=\"false\" />");
			appendWithLineBreak(sb,
					"\t<param name=\"centerimage\" value=\"true\" />");
		}

		if (!cbOpenButton.isSelected()) {
			appendAllAppletParameters(sb, TYPE_HTMLFILE);
		} else {// button type
			appendWithLineBreak(sb, "\t<param name=\"type\" value=\"button\" />");
			// white background
			appendWithLineBreak(sb, "\t<param name=\"bgcolor\" value=\"#FFFFFF\" />");
		}

		// problem with Moodle 1.9.5 mangling this
		if (includeNoJavaMessage)
			appendWithLineBreak(sb, app.getPlain("NoJavaMessage"));

		sb.append("</applet>");
		if (includeHTML5) {
			appendWithLineBreak(sb, "</noscript>");
		}
		return sb.toString();
	}

	private void appletParam(StringBuilder sb, String param, boolean value,
			int type) {
		appletParam(sb, param, value + "", type);

	}

	private void appletParam(StringBuilder sb, String param, String value,
			int type) {

		switch (type) {
		case TYPE_MEDIAWIKI:
			sb.append(' ');
			sb.append(param);
			sb.append(" = \"");
			sb.append(value);
			sb.append('\"');
			break;

		case TYPE_GOOGLEGADGET:
			sb.append(param);
			sb.append(":\"");
			sb.append(value);
			sb.append("\", ");

			break;

		default: // HTML file/clipboard
			sb.append("\t<param name=\"");
			sb.append(param);
			sb.append("\" value=\"");
			sb.append(value);
			appendWithLineBreak(sb, "\" />");
		}

	}

	private void appendGgbAppletParameters(StringBuilder sb, int type) {

		// framePossible (double click opens GeoGebra window)
		appletParam(sb, "framePossible", cbShowFrame.isSelected(), type);

		// showResetIcon
		appletParam(sb, "showResetIcon", cbShowResetIcon.isSelected(), type);

		// TODO: implement show animation controls
		appletParam(sb, "showAnimationButton", true, type);

		// enable right click
		appletParam(sb, "enableRightClick", cbEnableRightClick.isSelected(), type);

		// enable error dialogs
		appletParam(sb, "errorDialogsActive", true, type);// sb.append(cbEnableErrorDialogs.isSelected());

		// enable label drags
		appletParam(sb, "enableLabelDrags", cbEnableLabelDrags.isSelected(), type);

		// showMenuBar
		appletParam(sb, "showMenuBar", cbShowMenuBar.isSelected(), type);

		// showToolBar
		appletParam(sb, "showToolBar", cbShowToolBar.isSelected(), type);

		// showToolBarHelp
		appletParam(sb, "showToolBarHelp", cbShowToolBarHelp.isSelected(), type);

		// showAlgebraInput
		appletParam(sb, "showAlgebraInput", cbShowInputField.isSelected(), type);

		// Use Browser for JavaScript (eg Buttons)
		appletParam(sb, "useBrowserForJS", cbUseBrowserForJavaScript.isSelected(),
				type);

		// allowRescaling
		appletParam(sb, "allowRescaling", cbAllowRescaling.isSelected(), type);

	}

	private StringBuilder sb2 = new StringBuilder();

	/**
	 * Appends all selected applet parameters
	 */
	private void appendAllAppletParameters(StringBuilder sb, int type) {

		// JVM arguments, for Java 1.6.0_10 and later
		// increase heap memory for applets
		String javaArgs = "-Xmx" + GeoGebra.MAX_HEAP_SPACE
				+ "m -Djnlp.packEnabled=true";
		// TODO: include pack.gz files in offline export
		// if (cbOfflineArchive.isSelected()) {
		// // look for local pack200 files: jar.pack.gz
		// javaArgs += " -Djnlp.packEnabled=true";
		// }

		// sb.append("\t<param name=\"java_arguments\" value=\"" + javaArgs +
		// "\" />");
		appletParam(sb, "java_arguments", javaArgs, type);

		// add caching information to help JVM with faster applet loading
		// sb.append("\t<param name=\"cache_archive\" value=\"");
		sb2.setLength(0);
		for (int i = 0; i < Application.JAR_FILES.length; i++) {
			sb2.append(Application.JAR_FILES[i]);
			if (i < Application.JAR_FILES.length - 1)
				sb2.append(", ");
		}
		// sb.append("\" />");

		appletParam(sb, "cache_archive", sb2.toString(), type);

		// cache versions of jar files: if this version is already present on the
		// client
		// then the JVM does not need to connect to the server to compare jar time
		// stamps
		// sb.append("\t<param name=\"cache_version\" value=\"");
		sb2.setLength(0);
		for (int i = 0; i < Application.JAR_FILES.length; i++) {
			sb2.append(GeoGebra.VERSION_STRING);
			if (i < Application.JAR_FILES.length - 1)
				sb2.append(", ");
		}
		// sb.append("\" />");
		appletParam(sb, "cache_version", sb2.toString(), type);

		appendGgbAppletParameters(sb, type);

	}

}
