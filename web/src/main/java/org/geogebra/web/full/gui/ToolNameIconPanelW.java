package org.geogebra.web.full.gui;

import org.geogebra.common.kernel.Macro;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.geogebra.web.full.gui.dialog.image.UploadImageDialog;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.ImageManagerW;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Panel of Tool Creation Dialog. Contains tool name, command name, help and
 * icon for the tool. It also allows user to add/remove the tool from toolbar.
 */
public class ToolNameIconPanelW extends VerticalPanel implements BlurHandler,
        KeyUpHandler {

	/** With of tool icon in pixels **/
	public static final int ICON_WIDTH = 32;
	/** Height of tool icon in pixels **/
	public static final int ICON_HEIGHT = 32;

	private TextBox tfCmdName;
	private TextBox tfToolHelp;
	private TextBox tfToolName;
	private CheckBox showTool;
	private VerticalPanel mainWidget;
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
	 * 
	 * @param app
	 *            application
	 */
	public ToolNameIconPanelW(final App app) {
		super();

		this.app = (AppW) app;

		listener = null;
		macro = null;

		mainWidget = new VerticalPanel();
		Localization loc = app.getLocalization();
		Label labelCmdName = new Label(loc.getMenu("CommandName"));
		int n = app.getKernel().getMacroNumber() + 1;

		Label labelToolName = new Label(loc.getMenu("ToolName"));
		tfToolName = new GTextBox();
		tfToolName.setText(loc.getMenu("Tool") + n);
		FlowPanel pToolName = new FlowPanel();
		pToolName.add(labelToolName);
		pToolName.add(tfToolName);

		tfCmdName = new GTextBox();
		tfCmdName.setText(tfToolName.getText());
		FlowPanel pCmdName = new FlowPanel();
		pCmdName.add(labelCmdName);
		pCmdName.add(tfCmdName);

		Label labelToolHelp = new Label(loc.getMenu("ToolHelp"));
		tfToolHelp = new GTextBox();
		FlowPanel pToolHelp = new FlowPanel();
		pToolHelp.add(labelToolHelp);
		pToolHelp.add(tfToolHelp);

		tfCmdName.addBlurHandler(this);
		tfCmdName.addKeyUpHandler(this);

		tfToolName.addBlurHandler(this);
		tfToolName.addKeyUpHandler(this);

		tfToolHelp.addBlurHandler(this);
		tfToolHelp.addKeyUpHandler(this);

		mainWidget.add(pToolName);
		mainWidget.add(pCmdName);
		mainWidget.add(pToolHelp);

		VerticalPanel iconPanel = new VerticalPanel();
		icon = new NoDragImage(ImgResourceHelper
				.safeURI(ToolbarSvgResourcesSync.INSTANCE.mode_tool_32()),
				32);
		Button labelIcon = new Button(loc.getMenu("Icon") + " ...");
		labelIcon.addClickHandler(event -> {
			UploadImageDialog imageDialog = new UploadImageDialog((AppW) app,
					ICON_WIDTH, ICON_HEIGHT);
			imageDialog.center();
			imageDialog.setOnPositiveAction(() -> {
				setIconFile(imageDialog.getUploadImgPanel().getFileName(),
						imageDialog.getUploadImgPanel().getImageDataURL());
			});
		});

		iconPanel.add(icon);
		iconPanel.add(labelIcon);

		showTool = new CheckBox(loc.getMenu("ShowInToolBar"));
		showTool.setValue(true);
		showTool.addValueChangeHandler(event -> showToolChanged());

		HorizontalPanel iconSelectShowPanel = new HorizontalPanel();
		if (!app.isExam()) {
			iconSelectShowPanel.add(iconPanel);
		}
		iconSelectShowPanel.add(showTool);

		mainWidget.add(iconSelectShowPanel);

		add(mainWidget);
	}

	/**
	 * Sets icon filename and updates thumbnail.
	 * 
	 * @param fileName
	 *            Path to new icon file.
	 * @param imgDataURL the data URL of the image
	 */
	public void setIconFile(String fileName, String imgDataURL) {
		String data;
		data = ImageResizer.resizeImage(imgDataURL, ICON_WIDTH, ICON_HEIGHT);

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc\liar.gif"Mobi
		iconFileName = ImageManagerW.getMD5FileName(fileName, data);

		app.getImageManager().addExternalImage(iconFileName, data);
		icon.setUrl(app.getImageManager().getExternalImageSrc(iconFileName));
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
		if (fileName == null) {
			return;
		}

		String imageURL = app.getImageManager().getExternalImageSrc(fileName);
		if (imageURL != null) {
			String dImageURL = ImageResizer.resizeImage(imageURL, ICON_WIDTH,
			        ICON_HEIGHT);
			if (!imageURL.equals(dImageURL)) {
				app.getImageManager().addExternalImage(fileName, dImageURL);
			}
			iconFileName = fileName;
			icon.setUrl(app.getImageManager().getExternalImageSrc(iconFileName));
		} else {
			icon.setUrl(ImgResourceHelper.safeURI(
					ToolbarSvgResourcesSync.INSTANCE.mode_tool_32()));
			iconFileName = null;
		}

		updateMacro();
		macroChanged();
	}

	/**
	 * Uses the textfields in this dialog to set the currently shown macro.
	 */
	private void updateMacro() {
		if (macro == null) {
			return;
		}
		//
		macro.setToolName(getToolName());
		macro.setToolHelp(getToolHelp());
		macro.setShowInToolBar(getShowTool());
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

		// TODO
		// if (managerDialog != null)
		// managerDialog.repaint();

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
		return showTool.getValue();
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
		tfCmdName.setText(m == null ? "" : m.getCommandName());
		tfToolName.setText(m == null ? "" : m.getToolName());
		tfToolHelp.setText(m == null ? "" : m.getToolHelp());
		showTool.setValue(m != null && m.isShowInToolBar());
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

	@Override
	public void onBlur(BlurEvent event) {
		updateCmdName(event.getSource());
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		updateCmdName(event.getSource());
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
		m.setShowInToolBar(showTool.getValue());
		if (listener != null) {
			listener.onShowToolChange(m);
		}
	}
}