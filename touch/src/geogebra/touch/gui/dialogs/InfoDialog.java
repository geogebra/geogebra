package geogebra.touch.gui.dialogs;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.laf.DefaultResources;
import geogebra.touch.model.GuiModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InfoDialog extends DialogT {
	public enum InfoType {
		SaveChanges, Override;
	}

	private static DefaultResources getLafIcons() {
		return TouchEntryPoint.getLookAndFeel().getIcons();
	}

	private final InfoType type;
	private final Button cancelButton = new Button();
	private final Button saveButton = new Button();
	private final Button dontSaveButton = new Button();
	private final ImageResource iconQuestion = getLafIcons().icon_question();
	private final VerticalPanel dialogPanel;
	private HorizontalPanel buttonContainer;
	private final HorizontalPanel textPanel;
	private final FlowPanel titlePanel = new FlowPanel();
	private final Label title;
	private final Label infoText;
	private String consTitle;
	private final Localization loc;
	private final TouchApp app;
	private Runnable callback = null;
	private final GuiModel guiModel;
	private final TabletGUI tabletGUI;

	public InfoDialog(final App app, final InfoType type) {
		super(true, true);
		this.app = (TouchApp) app;
		this.tabletGUI = (TabletGUI) this.app.getTouchGui();
		this.guiModel = this.tabletGUI.getTouchModel().getGuiModel();
		this.loc = app.getLocalization();
		this.setGlassEnabled(true);
		this.type = type;
		this.dialogPanel = new VerticalPanel();
		this.title = new Label();
		this.infoText = new Label();
		this.textPanel = new HorizontalPanel();

		this.addLabel();
		this.addText();
		this.addButtons();

		this.add(this.dialogPanel);

		this.setStyleName("infoDialog");
	}

	private void addButtons() {
		this.initCancelButton();
		this.initSaveButton();
		this.initDontSaveButton();

		this.buttonContainer = new HorizontalPanel();
		this.buttonContainer.setStyleName("buttonPanel");
		this.buttonContainer.add(this.cancelButton);
		this.buttonContainer.add(this.dontSaveButton);
		this.buttonContainer.add(this.saveButton);

		// Last Button has no border-right
		this.saveButton.addStyleName("last");

		this.dialogPanel.add(this.buttonContainer);
	}

	private void addLabel() {
		this.title.setStyleName("title");
		this.titlePanel.add(this.title);
		this.titlePanel.setStyleName("titlePanel");
		this.dialogPanel.add(this.titlePanel);
	}

	private void addText() {
		final Panel iconPanel = new LayoutPanel();
		final String html = "<img src=\""
				+ this.iconQuestion.getSafeUri().asString() + "\" />";
		iconPanel.getElement().setInnerHTML(html);
		iconPanel.setStyleName("iconPanel");
		this.textPanel.add(iconPanel);
		this.textPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.textPanel.add(this.infoText);
		this.textPanel.setStyleName("textPanel");
		this.dialogPanel.add(this.textPanel);
	}

	@Override
	public void hide() {
		super.hide();
		this.guiModel.setActiveDialog(null);
	}

	private void initCancelButton() {
		this.cancelButton.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				InfoDialog.this.hide();
			}
		}, ClickEvent.getType());
	}

	private void initDontSaveButton() {
		this.dontSaveButton.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				onDontSave();
			}
		}, ClickEvent.getType());
	}

	void onDontSave() {
		if (this.type == InfoType.Override) {
			this.tabletGUI.editTitle();
		}
		this.app.setSaved();
		this.hide();

		if (this.callback != null) {
			this.callback.run();
		} else {
			App.debug("no callback");
		}
	}

	private void initSaveButton() {

		this.saveButton.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				onSave();
			}
		}, ClickEvent.getType());
	}

	protected void onSave() {
		if (this.consTitle != null) {
			this.app.setConstructionTitle(this.consTitle);
		}
		this.app.getFileManager().saveFile(this.app);
		this.app.setSaved();
		this.hide();
		if (this.callback != null) {
			this.callback.run();
		} else {
			App.debug("no callback");
		}
		TouchEntryPoint.getLookAndFeel().updateUndoSaveButtons();
	}

	public void setCallback(final Runnable callback) {
		this.callback = callback;
	}

	public void setConsTitle(final String title) {
		this.consTitle = title;
	}

	public void setLabels() {
		if (this.type == InfoType.SaveChanges) {
			this.title.setText(this.loc.getMenu("CloseFile"));
			this.infoText.setText(this.loc
					.getMenu("DoYouWantToSaveYourChanges"));
			this.cancelButton.setText(this.loc.getMenu("Cancel"));
			this.saveButton.setText(this.loc.getMenu("Save"));
			this.dontSaveButton.setText(this.loc.getMenu("DontSave"));
		} else {
			this.title.setText(this.loc.getMenu("Rename"));
			this.infoText.setText(this.loc.getPlain("OverwriteFile"));
			this.cancelButton.setText(this.loc.getMenu("Cancel"));
			this.saveButton.setText(this.loc.getMenu("Overwrite"));
			this.dontSaveButton.setText(this.loc.getMenu("DontOverwrite"));
		}
	}

	@Override
	public void show() {
		super.show();
		super.center();
		this.setLabels();
		this.guiModel.setActiveDialog(this);
	}

	public void showIfNeeded(final TouchApp touchApp) {
		if (this.type == InfoType.SaveChanges) {
			if (!touchApp.isSaved()) {
				this.consTitle = touchApp.getConstructionTitle();
				this.show();
				super.center();
			} else {
				if (this.callback != null) {
					this.callback.run();
				}
			}
		}
	}
}