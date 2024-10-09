package org.geogebra.web.full.gui.dialog.tools;

import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.geogebra.web.full.gui.ImageResizer;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.dialog.image.UploadImagePanel;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ImageManagerW;
import org.gwtproject.user.client.ui.FileUpload;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;

/**
 * Panel of Tool Creation Dialog. Contains tool name, command name, help and
 * icon for the tool. It also allows user to add/remove the tool from toolbar.
 */
public class ToolNameIconPanelW extends FlowPanel {

	/** With of tool icon in pixels **/
	public static final int ICON_WIDTH = 32;
	/** Height of tool icon in pixels **/
	public static final int ICON_HEIGHT = 32;

	private ComponentInputField tfCmdName;
	private ComponentInputField tfToolHelp;
	private ComponentInputField tfToolName;
	private ComponentCheckbox showTool;
	private Image icon;
	private String iconFileName;

	private AppW app;
	private Macro macro;
	private MacroChangeListener listener;
	
	/**
	 * Change listener
	 */
	public interface MacroChangeListener {
		/**
		 * @param macro
		 *            changed macro
		 */
		void onMacroChange(Macro macro);

		/**
		 * @param macro
		 *            macro shown in toolbar
		 */
		void onShowToolChange(Macro macro);
	}

	/**
	 * @param app
	 *            application
	 * @param parent parent dialog
	 */
	public ToolNameIconPanelW(final App app, GPopupPanel parent) {
		this.app = (AppW) app;

		listener = null;
		macro = null;

		Localization loc = app.getLocalization();
		int n = app.getKernel().getMacroNumber() + 1;

		tfToolName = new ComponentInputField((AppW) app,
				null, loc.getMenu("ToolName"), null, "", 28);
		tfToolName.setInputText(loc.getMenu("Tool") + n);
		addHandlers(tfToolName);

		tfCmdName = new ComponentInputField((AppW) app,
				null, loc.getMenu("CommandName"), null, "", 28);
		tfCmdName.setInputText(tfToolName.getText());
		addHandlers(tfCmdName);

		tfToolHelp =  new ComponentInputField((AppW) app,
				null, loc.getMenu("ToolHelp"), null, "", 28);
		addHandlers(tfToolHelp);

		FlowPanel iconPanel = new FlowPanel();
		iconPanel.addStyleName("iconPanel");
		icon = new NoDragImage(NoDragImage.safeURI(ToolbarSvgResourcesSync.INSTANCE.mode_tool_32()),
				32);
		StandardButton labelIcon = new StandardButton(loc.getMenu("Icon") + " ...");
		labelIcon.addFastClickHandler(event -> {
			FileUpload uploadButton =
					UploadImagePanel.getUploadButton((AppW) app, this::resizeAndUpdateIcon);
			// make sure synthetic click on file upload is not canceled
			parent.addAutoHidePartner(uploadButton.getElement());
			uploadButton.click();
		});

		if (!this.app.enableFileFeatures()) {
			labelIcon.setEnabled(false);
		}

		iconPanel.add(icon);
		iconPanel.add(labelIcon);

		showTool = new ComponentCheckbox(app.getLocalization(), false,
				"ShowInToolBar", selected -> showToolChanged());
		showTool.setSelected(true);

		FlowPanel iconSelectShowPanel = new FlowPanel();
		iconSelectShowPanel.addStyleName("iconSelectShowPanel");
		if (GlobalScope.examController.isIdle()) {
			iconSelectShowPanel.add(iconPanel);
		}
		iconSelectShowPanel.add(showTool);

		add(iconSelectShowPanel);
	}

	private void resizeAndUpdateIcon(String fn, String data) {
		setIconFile(fn, data);
	}

	private void addHandlers(ComponentInputField tf) {
		add(tf);
		tf.getTextField().getTextComponent().addBlurHandler(e -> updateCmdName(tf));
		tf.getTextField().getTextComponent().addKeyUpHandler(e -> onKeyUp(tf));
	}

	/**
	 * Sets icon filename and updates thumbnail.
	 * 
	 * @param fileName
	 *            Path to new icon file.
	 * @param imgDataURL the data URL of the image
	 */
	public void setIconFile(String fileName, String imgDataURL) {
		ImageResizer.resizeImage(imgDataURL, ICON_WIDTH, ICON_HEIGHT, resizedData -> {
			// filename will be of form
			// "a04c62e6a065b47476607ac815d022cc\liar.gif"Mobi
			iconFileName = ImageManagerW.getMD5FileName(fileName, resizedData);
			app.getImageManager().addExternalImage(iconFileName, resizedData);
			updateWithIcon(app.getImageManager().getExternalImageSrc(iconFileName));
		});
	}

	private void updateWithIcon(String url) {
		icon.setUrl(url);
		updateMacro();
		macroChanged();
	}

	/**
	 * Sets icon filename and updates thumbnail.
	 * 
	 * @param fileName
	 *            Path to new icon file.
	 */
	public void setIconFileName(String fileName) {
		if (fileName == null || "".equals(fileName)) {
			return;
		}

		String imageURL = app.getImageManager().getExternalImageSrc(fileName);
		if (imageURL != null) {
			ImageResizer.resizeImage(imageURL, ICON_WIDTH,
			        ICON_HEIGHT, dImageURL -> {
						if (!imageURL.equals(dImageURL)) {
							app.getImageManager().addExternalImage(fileName, dImageURL);
						}
						iconFileName = fileName;
						updateWithIcon(app.getImageManager().getExternalImageSrc(iconFileName));
					});
		} else {
			iconFileName = null;
			updateWithIcon(NoDragImage.safeURI(ToolbarSvgResourcesSync.INSTANCE.mode_tool_32()));
		}
	}

	/**
	 * Uses the textfields in this dialog to set the currently shown macro.
	 */
	private void updateMacro() {
		if (macro == null) {
			return;
		}

		macro.setToolName(getToolName());
		macro.setToolHelp(getToolHelp());
		macro.setShowInToolBar(getShowTool());
		macro.setIconFileName(getIconFileName());

		// be careful when changing the command name of a macro
		// as this is the internally used name
		String cmdName = getCommandName();
		if (!macro.getCommandName().equals(cmdName)) {
			// try to change
			if (!app.getKernel().setMacroCommandName(macro, cmdName)) {
				// name used by macro: undo textfield change
				tfCmdName.setInputText(macro.getCommandName());
			}
		}
	}

	/**
	 * @return command name
	 */
	public String getCommandName() {
		return tfCmdName.getText();
	}

	/**
	 * @return tool name
	 */
	public String getToolName() {
		return tfToolName.getText();
	}

	/**
	 * @return tool help
	 */
	public String getToolHelp() {
		return tfToolHelp.getText();
	}

	/**
	 * @return whether tool is shown in toolbar
	 */
	public boolean getShowTool() {
		return showTool.isSelected();
	}

	/**
	 * @return icon filename
	 */
	public String getIconFileName() {
		return iconFileName;
	}

	/**
	 * Update panel for given macro.
	 * 
	 * @param m
	 *            macro
	 */
	public void setMacro(Macro m) {
		this.macro = m;
		tfCmdName.setInputText(m == null ? "" : m.getCommandName());
		tfToolName.setInputText(m == null ? "" : m.getToolName());
		tfToolHelp.setInputText(m == null ? "" : m.getToolHelp());
		showTool.setSelected(m != null && m.isShowInToolBar());
		setIconFileName(m == null ? null : m.getIconFileName());
	}

	private Macro getMacro() {
		Macro m = new Macro(app.getKernel(), tfCmdName.getText());
		m.setToolName(getToolName());
		m.setToolHelp(getToolHelp());
		m.setShowInToolBar(getShowTool());
		m.setIconFileName(getIconFileName());
		return m;
	}

	/**
	 * @param listener
	 *            macro listener
	 */
	public void setMacroChangeListener(MacroChangeListener listener) {
		this.listener = listener;
	}

	private void onKeyUp(ComponentInputField source) {
		updateCmdName(source);
		showToolChanged();
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
				tfCmdName.setInputText(parsed);
			}
		} catch (Error | Exception err) {
			tfCmdName.setInputText(defaultToolName());
		}
		updateMacro();
	}

	private String defaultToolName() {
		int n = app.getKernel().getMacroNumber() + 1;
		return app.getLocalization().getMenu("Tool") + n;
	}

	private void macroChanged() {
		Macro m = getMacro();
		if (listener != null) {
			listener.onMacroChange(m);
		}
	}

	/**
	 * Callback for "show tool" checkbox
	 */
	void showToolChanged() {
		Macro m = getMacro();
		if (listener != null) {
			listener.onShowToolChange(m);
		}
	}
}