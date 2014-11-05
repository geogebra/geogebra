package geogebra.web.gui;

import geogebra.common.kernel.Macro;
import geogebra.common.main.App;
import geogebra.common.util.MD5EncrypterGWTImpl;
import geogebra.html5.main.AppW;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.dialog.image.UploadImageDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
public class ToolNameIconPanel extends VerticalPanel {

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

	/**
	 * 
	 * @param app
	 */
	public ToolNameIconPanel(final App app) {
		super();

		this.app = (AppW) app;

		mainWidget = new VerticalPanel();

		Label labelCmdName = new Label(app.getMenu("CommandName"));
		tfCmdName = new TextBox();
		FlowPanel pCmdName = new FlowPanel();
		pCmdName.add(labelCmdName);
		pCmdName.add(tfCmdName);

		Label labelToolHelp = new Label(app.getMenu("ToolHelp"));
		tfToolHelp = new TextBox();
		FlowPanel pToolHelp = new FlowPanel();
		pToolHelp.add(labelToolHelp);
		pToolHelp.add(tfToolHelp);

		Label labelToolName = new Label(app.getMenu("ToolName"));
		tfToolName = new TextBox();
		FlowPanel pToolName = new FlowPanel();
		pToolName.add(labelToolName);
		pToolName.add(tfToolName);

		mainWidget.add(pCmdName);
		mainWidget.add(pToolHelp);
		mainWidget.add(pToolName);

		VerticalPanel iconPanel = new VerticalPanel();
		icon = new NoDragImage(GGWToolBar.safeURI(GGWToolBar.getMyIconResourceBundle().mode_tool_32()
		        ));
		Button labelIcon = new Button(app.getMenu("Icon") + " ...");
		labelIcon.addClickHandler(new ClickHandler() {

			@SuppressWarnings("unused")
            public void onClick(ClickEvent event) {
				(new UploadImageDialog(
				        (AppW) app, ICON_WIDTH + "px", ICON_HEIGHT + "px") {

					public void onClick(ClickEvent ev) {
						Object source = ev.getSource();
						if (source == insertBtn) {
							setIconFile(uploadImagePanel.getFileName(),
							        uploadImagePanel.getImageDataURL());
							hide();
						} else if (source == cancelBtn) {
							hide();
						}
					}
				}).center();
			}
		});

		iconPanel.add(icon);
		iconPanel.add(labelIcon);

		showTool = new CheckBox(app.getMenu("ShowInToolBar"));
		showTool.setValue(true);

		HorizontalPanel iconSelectShowPanel = new HorizontalPanel();
		iconSelectShowPanel.add(iconPanel);
		iconSelectShowPanel.add(showTool);

		mainWidget.add(iconSelectShowPanel);

		add(mainWidget);
	}

	public void setIconFile(String fileName, String imgDataURL) {
		String data;
		data = ImageResizer.resizeImage(imgDataURL, ICON_WIDTH, ICON_HEIGHT);

		MD5EncrypterGWTImpl md5e = new MD5EncrypterGWTImpl();
		String zip_directory = md5e.encrypt(data);

		String fn = fileName;
		int index = fileName.lastIndexOf('/');
		if (index != -1) {
			fn = fn.substring(index + 1, fn.length()); // filename
			                                           // without
		}
		fn = geogebra.common.util.Util.processFilename(fn);

		// filename will be of form
		// "a04c62e6a065b47476607ac815d022cc\liar.gif"
		iconFileName = zip_directory + '/' + fn;

		app.getImageManager().addExternalImage(iconFileName, data);
		icon.setUrl(app.getImageManager().getExternalImageSrc(iconFileName));

		updateMacro();
	}

	/**
	 * Sets icon filename and updates thumbnail.
	 * 
	 * @param fileName
	 *            Path to new icon file.
	 */
	public void setIconFileName(String fileName) {
		String imageURL = app.getImageManager().getExternalImageSrc(fileName);
		if (imageURL != null) {
			String dImageURL = ImageResizer.resizeImage(imageURL, ICON_WIDTH,
			        ICON_HEIGHT);
			if (!imageURL.equals(dImageURL)) {
				app.addExternalImage(fileName, dImageURL);
			}
			iconFileName = fileName;
			icon.setUrl(app.getImageManager().getExternalImageSrc(iconFileName));
		} else {
			icon.setUrl(GGWToolBar.safeURI(GGWToolBar.getMyIconResourceBundle().mode_tool_32()));
			iconFileName = null;
		}
		
		updateMacro();
	}

	/**
	 * Uses the textfields in this dialog to set the currently shown macro.
	 * 
	 * @see #init()
	 * 
	 */
	private void updateMacro() {
		if (macro == null)
			return;
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

	public String getCommandName() {
		return tfCmdName.getText();
	}

	public String getToolName() {
		return tfToolName.getText();
	}

	public String getToolHelp() {
		return tfToolHelp.getText();
	}

	public boolean getShowTool() {
		return showTool.getValue();
	}

	public String getIconFileName() {
		return iconFileName;
	}

}
