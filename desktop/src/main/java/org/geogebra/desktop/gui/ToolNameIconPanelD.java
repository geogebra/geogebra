/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.Util;
import org.geogebra.desktop.gui.dialog.ToolManagerDialogD;
import org.geogebra.desktop.gui.inputfield.MyTextFieldD;
import org.geogebra.desktop.main.AppD;

/**
 * Panel of Tool Creation Dialog. Contains tool name, command name, help and
 * icon for the tool. It also allows user to add/remove the tool from toolbar.
 *
 * @version 2010-06-14
 */
public class ToolNameIconPanelD extends JPanel {
	private static final long serialVersionUID = 1L;

	/** With of tool icon in pixels **/
	public static final int ICON_WIDTH = 32;
	/** Height of tool icon in pixels **/
	public static final int ICON_HEIGHT = 32;

	private JTextField tfCmdName;
	private JTextField tfToolHelp;
	private JTextField tfToolName;
	private JCheckBox cbShowInToolBar;
	private JLabel labelIcon;
	private String iconFileName;
	private JButton btIconFile;

	private AppD app;
	private boolean editHappens = false;

	// tool manager updating
	private ToolManagerDialogD managerDialog;
	private Macro macro;

	/**
	 * Creates new ToolNameIconPanel in a Tool Creation Dialog
	 * 
	 * @param app
	 *            Application to which the Tool Creation Dialog belongs
	 */
	public ToolNameIconPanelD(final AppD app, boolean edithappens) {
		this.app = app;
		Localization loc = app.getLocalization();
		this.editHappens = edithappens;
		GridBagLayout namePanelLayout = new GridBagLayout();
		namePanelLayout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1, 0.0 };
		namePanelLayout.rowHeights = new int[] { 7, 7, 7, 20, 7 };
		namePanelLayout.columnWidths = new int[] { 7, 7, 7 };
		namePanelLayout.columnWeights = new double[] { 0.1, 0.9, 0.1 };
		namePanelLayout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1, 0.1 };
		namePanelLayout.rowHeights = new int[] { 7, 7, 7, 20, 20 };
		namePanelLayout.columnWidths = new int[] { 7, 7, 7 };
		namePanelLayout.columnWeights = new double[] { 0.1, 0.9, 0.1 };
		namePanelLayout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1 };
		namePanelLayout.rowHeights = new int[] { 7, 7, 7, 20 };
		namePanelLayout.columnWeights = new double[] { 0.1, 0.9, 0.1 };
		namePanelLayout.columnWidths = new int[] { 7, 7, 7 };
		setLayout(namePanelLayout);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		{
			JLabel labelToolName = new JLabel();
			add(labelToolName,
					new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
							GridBagConstraints.EAST, GridBagConstraints.NONE,
							new Insets(0, 0, 0, 10), 0, 0));
			labelToolName.setText(loc.getMenu("ToolName"));
		}
		{
			JLabel labelCmdName = new JLabel();
			add(labelCmdName,
					new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
							GridBagConstraints.EAST, GridBagConstraints.NONE,
							new Insets(0, 0, 0, 10), 0, 0));
			labelCmdName.setText(loc.getMenu("CommandName"));
		}
		{
			JLabel labelToolHelp = new JLabel();
			add(labelToolHelp,
					new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
							GridBagConstraints.EAST, GridBagConstraints.NONE,
							new Insets(0, 0, 0, 10), 0, 0));
			labelToolHelp.setText(loc.getMenu("ToolHelp"));
		}
		KeyListener kl = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				// only handles release
			}

			@Override
			public void keyReleased(KeyEvent e) {
				updateCmdName(e.getSource());
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// only handles release
			}
		};
		{
			tfToolName = new MyTextFieldD(app);
			int n = app.getKernel().getMacroNumber() + 1;
			tfToolName.setText(loc.getMenu("Tool") + n);
			add(tfToolName, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 0), 0, 0));
			tfToolName.addKeyListener(kl);
		}
		{
			tfCmdName = new MyTextFieldD(app);
			tfCmdName.setText(tfToolName.getText());
			add(tfCmdName, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 0), 0, 0));
			FocusListener fl = new FocusListener() {
				@Override
				public void focusGained(FocusEvent arg0) {
					// only handles blur
				}

				@Override
				public void focusLost(FocusEvent e) {
					updateCmdName(e.getSource());
				}
			};
			tfCmdName.addFocusListener(fl);
		}
		{
			tfToolHelp = new MyTextFieldD(app);
			add(tfToolHelp, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
					new Insets(0, 0, 0, 0), 0, 0));
		}
		{
			labelIcon = new JLabel();
			labelIcon.setIcon(app.getToolIcon(null));
			add(labelIcon,
					new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.NONE,
							new Insets(0, 0, 0, 0), 0, 0));
		}

		btIconFile = new JButton();
		add(btIconFile,
				new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		btIconFile.setText(loc.getMenu("Icon") + " ...");
		ActionListener ac = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String fileName = ((GuiManagerD) app.getGuiManager())
						.getImageFromFile();
				if (fileName != null) {
					setIconFileName(fileName);
				}
			}
		};

		btIconFile.addActionListener(ac);

		cbShowInToolBar = new JCheckBox();
		add(cbShowInToolBar,
				new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0), 0, 0));
		cbShowInToolBar.setText(loc.getMenu("ShowInToolBar"));
		cbShowInToolBar.setSelected(true);
		ActionListener ac2 = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean active = cbShowInToolBar.isSelected();
				labelIcon.setEnabled(active);
				btIconFile.setEnabled(active);
				updateMacro();

				if (editHappens) {
					int macroId = (macro.getKernel().getMacroID(macro)
							+ EuclidianConstants.MACRO_MODE_ID_OFFSET);
					if (active) {
						((GuiManagerD) app.getGuiManager())
								.refreshCustomToolsInToolBar();
					} else {
						((GuiManagerD) app.getGuiManager())
								.removeFromToolbarDefinition(macroId);
					}
					app.updateToolBar();
					app.updateMenubar();
				}
			}
		};
		cbShowInToolBar.addActionListener(ac2);
	}

	/**
	 * Uses the textfields in this dialog to set the currently shown macro.
	 * 
	 * @see #init()
	 * 
	 */
	private void updateMacro() {
		if (macro == null) {
			return;
		}

		macro.setToolName(getToolName());
		macro.setToolHelp(getToolHelp());
		macro.setShowInToolBar(showInToolBar());
		macro.setIconFileName(getIconFileName());

		// be careful when changing the command name of a macro
		// as this is the internally used name
		String cmdName = getCommandName();
		if (!macro.getCommandName().equals(cmdName)) {
			// try to change
			boolean cmdNameChanged = app.getKernel().setMacroCommandName(macro,
					cmdName);
			if (!cmdNameChanged) {
				// name used by macro: undo textfield change
				tfCmdName.setText(macro.getCommandName());
			}
		}

		if (managerDialog != null) {
			managerDialog.repaint();
		}
	}

	/**
	 * Sets all fields using properties of a macro
	 * 
	 * @param macro
	 *            macro whose parameters are used
	 */
	public void setFromMacro(Macro macro) {
		tfToolName.setText(macro.getToolName());
		tfCmdName.setText(macro.getCommandName());
		tfToolHelp.setText(macro.getToolHelp());
		cbShowInToolBar.setSelected(macro.isShowInToolBar());
		setIconFileName(macro.getIconFileName());
	}

	/**
	 * Inits the textfields in this dialog using the properties of the given
	 * macro. The ToolManagerDialog is registered as a listener to be updated
	 * whenever the macro properties are changed.
	 * 
	 * @param macro1
	 *            Macro into which new name, help, etc. are stored
	 * @param managerDialog1
	 *            Manager dialog this tab belongs to
	 */
	public void init(ToolManagerDialogD managerDialog1, Macro macro1) {
		updateMacro(); // update last macro if we already had one

		this.managerDialog = managerDialog1;
		this.macro = macro1;

		boolean enabled = macro1 != null;
		if (!enabled) {
			tfToolName.setText("");
			tfCmdName.setText("");
			tfToolHelp.setText("");
			cbShowInToolBar.setSelected(false);
			iconFileName = "";
			labelIcon.setIcon(null);
		} else {
			setFromMacro(macro1);
		}

		tfToolName.setEnabled(enabled);
		tfCmdName.setEnabled(enabled);
		tfToolHelp.setEnabled(enabled);
		cbShowInToolBar.setEnabled(enabled);
		labelIcon.setEnabled(enabled);
		btIconFile.setEnabled(enabled);
	}

	@Override
	public void requestFocus() {
		super.requestFocus();

		tfToolName.requestFocusInWindow();
		tfToolName.setSelectionStart(0);
		tfToolName.setSelectionEnd(tfToolName.getText().length());
	}

	/**
	 * Returns command name
	 * 
	 * @return command name
	 */
	public String getCommandName() {
		return tfCmdName.getText();
	}

	/**
	 * Sets command name
	 * 
	 * @param commandName
	 *            command name
	 */
	public void setCommandName(String commandName) {
		tfCmdName.setText(commandName);
	}

	/**
	 * Gets tool name
	 * 
	 * @return tool name
	 */
	public String getToolName() {
		return tfToolName.getText();
	}

	/**
	 * Sets tool name
	 * 
	 * @param toolName
	 */
	public void setToolName(String toolName) {
		tfToolName.setText(toolName);
	}

	/**
	 * Gets tool help
	 * 
	 * @return tool help
	 */
	public String getToolHelp() {
		return tfToolHelp.getText();
	}

	/**
	 * Sets tool help (e.g. for tooltip)
	 * 
	 * @param toolHelp
	 *            tool help
	 */
	public void setToolHelp(String toolHelp) {
		tfToolHelp.setText(toolHelp);
	}

	/**
	 * Returns true if the macro shoud be dispalayed in toolbar
	 * 
	 * @return true if "Show in toolbar" checkbox is selected
	 */
	public boolean showInToolBar() {
		return cbShowInToolBar.isSelected();
	}

	/**
	 * Sets icon filename and updates thumbnail.
	 * 
	 * @param fileName
	 *            Path to new icon file.
	 * @version 2010-06-14 Last change: separated from #actionPerformed (Zbynek
	 *          Konecny)
	 */
	public void setIconFileName(String fileName) {
		MyImageD image = app.getExternalImage(fileName);
		if (image != null) {
			if (image.getWidth() != ICON_WIDTH
					|| image.getHeight() != ICON_HEIGHT) {
				BufferedImage bi = ImageResizer.resizeImage(
						(BufferedImage) image.getImage(), ICON_WIDTH,
						ICON_HEIGHT);
				image = new MyImageD(bi);
				app.addExternalImage(fileName, image);
			}
			iconFileName = Util.checkImageExtension(fileName);
			labelIcon.setIcon(new ImageIcon(image.getImage()));
		} else {
			labelIcon.setIcon(app.getToolIcon(null));
			iconFileName = null;
		}
		updateMacro();
	}

	/**
	 * Returns filename of icon.
	 * 
	 * @return filename of icon
	 */
	public String getIconFileName() {
		return iconFileName;
	}

	private void updateCmdName(Object source) {
		String cmdName = source == tfToolName ? tfToolName.getText()
				: tfCmdName.getText();

		// remove spaces
		cmdName = cmdName.replaceAll(" ", "");
		try {
			String parsed = app.getKernel().getAlgebraProcessor()
					.parseLabel(cmdName);
			if (!parsed.equals(tfCmdName.getText())) {
				tfCmdName.setText(parsed);
			}
		} catch (Error err) {
			tfCmdName.setText(defaultToolName());
		} catch (Exception ex) {
			tfCmdName.setText(defaultToolName());
		}
		updateMacro();
	}

	private String defaultToolName() {
		int n = app.getKernel().getMacroNumber() + 1;
		return app.getLocalization().getMenu("Tool") + n;
	}

}