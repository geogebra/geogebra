package geogebra.touch.gui.dialogs;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.touch.FileManagerM;
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

public class InfoDialog extends PopupPanel
{
	public enum InfoType
	{
		SaveChanges, Override;
	}
	
	InfoType type;
	
	private Button cancelButton = new Button();
	private Button saveButton = new Button();
	private Button dontSaveButton = new Button();
	
	private SVGResource iconQuestion = getLafIcons().icon_question();
	private VerticalPanel dialogPanel;
	private HorizontalPanel buttonContainer;
	private HorizontalPanel textPanel;
	private Label title;
	private Label infoText;
	String consTitle;
	private Localization loc;
	App app;
	FileManagerM fm;
	Runnable callback = null;
	private GuiModel guiModel;
	TabletGUI tabletGUI;

	public InfoDialog(App app, FileManagerM fm, GuiModel guiModel, InfoType type, TabletGUI tabletGUI)
	{
		super(true, true);
		this.tabletGUI = tabletGUI;
		this.app = app;
		this.loc = app.getLocalization();
		this.fm = fm;
		this.setGlassEnabled(true);
		this.type = type;
		this.dialogPanel = new VerticalPanel();
		this.title = new Label();
		this.infoText = new Label();
		this.guiModel = guiModel;
		this.textPanel = new HorizontalPanel();

		addLabel();
		addText();
		addButtons();

		this.add(this.dialogPanel);
		// FIXME the glass pane has z-index 20, we must go higher
		//this.getElement().getStyle().setZIndex(42);
		
		this.setStyleName("infoDialog");
	}

	private static DefaultResources getLafIcons()
	{
		return TouchEntryPoint.getLookAndFeel().getIcons();
	}
	
	private void addLabel()
	{
		if (this.type == InfoType.SaveChanges)
		{
			this.title.setText(this.loc.getMenu("CloseFile"));
		}
		else
		{
			this.title.setText(this.loc.getMenu("Rename"));
		}
		this.dialogPanel.add(this.title);
		this.title.setStyleName("title");
	}
	
	private void addText() {
		Panel iconPanel = new LayoutPanel();
		String html = "<img src=\"" + this.iconQuestion.getSafeUri().asString() + "\" />";
		iconPanel.getElement().setInnerHTML(html);
		iconPanel.setStyleName("iconPanel");
		this.textPanel.add(iconPanel);
		
		if (this.type == InfoType.SaveChanges)
		{
			this.infoText.setText(this.loc.getMenu("DoYouWantToSaveYourChanges"));
		}
		else
		{
			this.infoText.setText(this.loc.getPlain("OverwriteFile"));
		}
		this.textPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.textPanel.add(this.infoText);
		
		this.textPanel.setStyleName("textPanel");
		
		this.dialogPanel.add(this.textPanel);
	}

	private void addButtons()
	{
		initCancelButton();
		initSaveButton();
		initDontSaveButton();

		this.buttonContainer = new HorizontalPanel();
		this.buttonContainer.setStyleName("buttonPanel");
		this.buttonContainer.add(this.cancelButton);
		this.buttonContainer.add(this.dontSaveButton);
		this.buttonContainer.add(this.saveButton);
		
		// Last Button has no border-right
		this.saveButton.addStyleName("last");
		
		this.dialogPanel.add(this.buttonContainer);
	}

	private void initDontSaveButton()
  {
		this.dontSaveButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				if (InfoDialog.this.type == InfoType.Override)
				{
					InfoDialog.this.tabletGUI.editTitle();
				}
				InfoDialog.this.app.setSaved();
				InfoDialog.this.hide();
				
				if (InfoDialog.this.callback != null)
				{
					InfoDialog.this.callback.run();
				}
				else
				{
					App.debug("no callback");
				}
			}
		}, ClickEvent.getType());
  }

	private void initSaveButton()
	{
		this.saveButton.addDomHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				if (InfoDialog.this.consTitle != null)
					{
					
						((TouchApp) InfoDialog.this.app).setConstructionTitle(InfoDialog.this.consTitle);
					}
				InfoDialog.this.fm.saveFile(InfoDialog.this.app);
				InfoDialog.this.app.setSaved();
				InfoDialog.this.hide();
				if (InfoDialog.this.callback != null)
				{
					InfoDialog.this.callback.run();
				}
				else
				{
					App.debug("no callback");
				}
				TouchEntryPoint.getLookAndFeel().updateUndoSaveButtons();
			}
		}, ClickEvent.getType());
	}

	private void initCancelButton()
	{
		this.cancelButton.addDomHandler(new ClickHandler()
		{
			
			@Override
			public void onClick(ClickEvent event)
			{
				InfoDialog.this.hide();
			}
		}, ClickEvent.getType());
	}

	public void showIfNeeded(TouchApp touchApp)
	{
		if (this.type == InfoType.SaveChanges)
		{
			if (!touchApp.isSaved())
			{
				this.consTitle = touchApp.getConstructionTitle();
				show();
				super.center();
			}
			else
			{
				if (this.callback != null)
				{
					this.callback.run();
				}
			}
		}
	}

	public void setLabels()
	{
		if (this.type == InfoType.SaveChanges)
		{
			this.title.setText(this.loc.getMenu("CloseFile"));
			this.infoText.setText(this.loc.getMenu("DoYouWantToSaveYourChanges"));
			this.cancelButton.setText(this.loc.getMenu("Cancel"));
			this.saveButton.setText(this.loc.getMenu("Save"));
			this.dontSaveButton.setText(this.loc.getMenu("DontSave"));
		}
		else
		{
			this.title.setText(this.loc.getMenu("Rename"));
			this.infoText.setText(this.loc.getPlain("OverwriteFile"));
			this.cancelButton.setText(this.loc.getMenu("Cancel"));
			this.saveButton.setText(this.loc.getMenu("Overwrite"));
			this.dontSaveButton.setText(this.loc.getMenu("DontOverwrite"));
		}
	}

	public void setCallback(Runnable callback)
	{
		this.callback = callback;
	}
	
	@Override
	public void show()
	{
		super.show();
		super.center();
		this.guiModel.setActiveDialog(this);
	}
	
	@Override
  public void hide()
	{
		super.hide();
		this.guiModel.setActiveDialog(null);
	}
	
	public void setConsTitle(String title)
	{
		this.consTitle = title;
	}
}