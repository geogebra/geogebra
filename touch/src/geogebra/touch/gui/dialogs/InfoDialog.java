package geogebra.touch.gui.dialogs;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.touch.TouchApp;
import geogebra.touch.gui.CommonResources;
import geogebra.touch.gui.elements.StandardImageButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InfoDialog extends PopupPanel
{
	private StandardImageButton cancelButton = new StandardImageButton(CommonResources.INSTANCE.dialog_cancel());
	private StandardImageButton okButton = new StandardImageButton(CommonResources.INSTANCE.dialog_ok());
	//OpenFileDialog openDialog;
	private VerticalPanel dialogPanel;
	private HorizontalPanel buttonContainer;
	private Label title;
	String consTitle;
	String xml;
	private Localization loc;
	Storage stockStore;
	Runnable callback = null;

	public InfoDialog(Localization loc,Storage stockStore)
	{
		super(true, true);
		this.loc = loc;
		this.stockStore = stockStore;
		this.setGlassEnabled(true);
		this.dialogPanel = new VerticalPanel();
		this.title = new Label();

		addLabel();
		addButtons();

		this.add(this.dialogPanel);
	}

	private void addLabel()
	{
		this.title.setText(this.loc.getMenu("DoYouWantToSaveYourChanges"));
		this.dialogPanel.add(this.title);
	}

	private void addButtons()
	{
		initCancelButton();
		initOKButton();

		this.buttonContainer = new HorizontalPanel();
		this.buttonContainer.setWidth("100%");
		this.buttonContainer.add(this.okButton);
		this.buttonContainer.add(this.cancelButton);

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
				if(InfoDialog.this.callback != null){
					InfoDialog.this.callback.run();
				}else{
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
				InfoDialog.this.stockStore.setItem(InfoDialog.this.consTitle, InfoDialog.this.xml);
				InfoDialog.this.hide();
				if(InfoDialog.this.callback!=null){
					InfoDialog.this.callback.run();
				}else{
					App.debug("no callback");
				}
			}
		}, ClickEvent.getType());
	}

	public void showIfNeeded(TouchApp app)
	{
		String constructionXML = app.getXML();
		if(!constructionXML.equals(this.stockStore.getItem(app.getConstructionTitle()))){
			this.consTitle = app.getConstructionTitle();
			this.xml = constructionXML;
			super.show();
			super.center();
		}else{
			if(this.callback != null){
				this.callback.run();
			}
		}
	}

	public void setLabels()
	{
		this.title.setText(this.loc.getMenu("DoYouWantToSaveYourChanges"));
	}

	public void setCallback(Runnable callback) {
		this.callback = callback;
	}
}