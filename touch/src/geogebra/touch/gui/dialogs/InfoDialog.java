package geogebra.touch.gui.dialogs;

import org.vectomatic.dom.svg.ui.SVGResource;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.laf.DefaultIcons;
import geogebra.touch.model.GuiModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InfoDialog extends PopupPanel
{
	private StandardImageButton cancelButton = new StandardImageButton(getLafIcons().dialog_cancel());
	private StandardImageButton okButton = new StandardImageButton(getLafIcons().dialog_ok());
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

	public InfoDialog(App app, FileManagerM fm, GuiModel guiModel)
	{
		super(true, true);
		this.app = app;
		this.loc = app.getLocalization();
		this.fm = fm;
		this.setGlassEnabled(true);
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

	private static DefaultIcons getLafIcons()
	{
		return TouchEntryPoint.getLookAndFeel().getIcons();
	}

	@Override
	public void show()
	{
		super.show();
		this.guiModel.setActiveDialog(this);
	}

//	@Override
//  public void hide()
//	{
//		super.hide();
//		this.guiModel.closeActiveDialog();
//	}
	
	private void addLabel()
	{
		this.title.setText(this.loc.getMenu("CloseFile"));
		this.dialogPanel.add(this.title);
		this.title.setStyleName("title");
	}
	
	private void addText() {
		Panel iconPanel = new LayoutPanel();
		String html = "<img src=\"" + this.iconQuestion.getSafeUri().asString() + "\" style=\"margin-right: 10px;\" />";
		iconPanel.getElement().setInnerHTML(html);
		this.textPanel.add(iconPanel);
		
		this.infoText.setText(this.loc.getMenu("DoYouWantToSaveYourChanges"));
		this.textPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.textPanel.add(this.infoText);
		
		this.textPanel.setStyleName("textPanel");
		
		this.dialogPanel.add(this.textPanel);
	}

	private void addButtons()
	{
		initCancelButton();
		initOKButton();

		this.buttonContainer = new HorizontalPanel();
		this.buttonContainer.setWidth("100%");
		this.buttonContainer.add(this.cancelButton);
		this.buttonContainer.add(this.okButton);
		
		// Last Button has no border-right
		this.okButton.addStyleName("last");
		
		this.dialogPanel.add(this.buttonContainer);
	}

	private void initCancelButton()
	{
		this.cancelButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
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

	private void initOKButton()
	{
		this.okButton.addDomHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				// just save in stockStore - no changes of construction title
				InfoDialog.this.fm.saveFile(InfoDialog.this.app);
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

	public void showIfNeeded(TouchApp touchApp)
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

	public void setLabels()
	{
		this.title.setText(this.loc.getMenu("CloseFile"));
		this.infoText.setText(this.loc.getMenu("DoYouWantToSaveYourChanges"));
	}

	public void setCallback(Runnable callback)
	{
		this.callback = callback;
	}
	
	@Override
  public void hide()
	{
		super.hide();
		this.guiModel.setActiveDialog(null);
	}
}