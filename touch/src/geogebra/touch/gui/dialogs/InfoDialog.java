package geogebra.touch.gui.dialogs;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.laf.DefaultResources;
import geogebra.touch.model.GuiModel;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InfoDialog extends PopupPanel {
  public enum InfoType {
    SaveChanges, Override;
  }

  private static DefaultResources getLafIcons() {
    return TouchEntryPoint.getLookAndFeel().getIcons();
  }

  InfoType type;
  private final Button cancelButton = new Button();
  private final Button saveButton = new Button();

  private final Button dontSaveButton = new Button();
  private final SVGResource iconQuestion = getLafIcons().icon_question();
  private final VerticalPanel dialogPanel;
  private HorizontalPanel buttonContainer;
  private final HorizontalPanel textPanel;
  private final Label title;
  private final Label infoText;
  String consTitle;
  private final Localization loc;
  TouchApp app;
  Runnable callback = null;
  private final GuiModel guiModel;

  TabletGUI tabletGUI;

  public InfoDialog(App app, GuiModel guiModel, InfoType type, TabletGUI tabletGUI) {
    super(true, true);
    this.tabletGUI = tabletGUI;
    this.app = (TouchApp) app;
    this.loc = app.getLocalization();
    this.setGlassEnabled(true);
    this.type = type;
    this.dialogPanel = new VerticalPanel();
    this.title = new Label();
    this.infoText = new Label();
    this.guiModel = guiModel;
    this.textPanel = new HorizontalPanel();

    this.addLabel();
    this.addText();
    this.addButtons();

    this.add(this.dialogPanel);
    // FIXME the glass pane has z-index 20, we must go higher
    // this.getElement().getStyle().setZIndex(42);

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
    if (this.type == InfoType.SaveChanges) {
      this.title.setText(this.loc.getMenu("CloseFile"));
    } else {
      this.title.setText(this.loc.getMenu("Rename"));
    }
    this.dialogPanel.add(this.title);
    this.title.setStyleName("title");
  }

  private void addText() {
    final Panel iconPanel = new LayoutPanel();
    final String html = "<img src=\"" + this.iconQuestion.getSafeUri().asString() + "\" />";
    iconPanel.getElement().setInnerHTML(html);
    iconPanel.setStyleName("iconPanel");
    this.textPanel.add(iconPanel);

    if (this.type == InfoType.SaveChanges) {
      this.infoText.setText(this.loc.getMenu("DoYouWantToSaveYourChanges"));
    } else {
      this.infoText.setText(this.loc.getPlain("OverwriteFile"));
    }
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
      public void onClick(ClickEvent event) {
	InfoDialog.this.hide();
      }
    }, ClickEvent.getType());
  }

  private void initDontSaveButton() {
    this.dontSaveButton.addDomHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
	if (InfoDialog.this.type == InfoType.Override) {
	  InfoDialog.this.tabletGUI.editTitle();
	}
	InfoDialog.this.app.setSaved();
	InfoDialog.this.hide();

	if (InfoDialog.this.callback != null) {
	  InfoDialog.this.callback.run();
	} else {
	  App.debug("no callback");
	}
      }
    }, ClickEvent.getType());
  }

  private void initSaveButton() {
    this.saveButton.addDomHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
	if (InfoDialog.this.consTitle != null) {
	  InfoDialog.this.app.setConstructionTitle(InfoDialog.this.consTitle);
	}
	InfoDialog.this.app.getFileManager().saveFile(InfoDialog.this.app);
	InfoDialog.this.app.setSaved();
	InfoDialog.this.hide();
	if (InfoDialog.this.callback != null) {
	  InfoDialog.this.callback.run();
	} else {
	  App.debug("no callback");
	}
	TouchEntryPoint.getLookAndFeel().updateUndoSaveButtons();
      }
    }, ClickEvent.getType());
  }

  public void setCallback(Runnable callback) {
    this.callback = callback;
  }

  public void setConsTitle(String title) {
    this.consTitle = title;
  }

  public void setLabels() {
    if (this.type == InfoType.SaveChanges) {
      this.title.setText(this.loc.getMenu("CloseFile"));
      this.infoText.setText(this.loc.getMenu("DoYouWantToSaveYourChanges"));
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
    this.guiModel.setActiveDialog(this);
  }

  public void showIfNeeded(TouchApp touchApp) {
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