package geogebra.mobile.gui.elements.header;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.dialog.Dialog;
import com.googlecode.mgwt.ui.client.dialog.DialogPanel;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.theme.base.DialogCss;
import com.googlecode.mgwt.ui.client.widget.base.ButtonBase;

public class OpenDialog implements Dialog
{
	/**
	 * The callback used when buttons are taped.
	 */
	public interface OpenCallback
	{
		/**
		 * Called if save button is clicked.
		 */
		public void onOpen();

		/**
		 * Called if cancel button is clicked.
		 */
		public void onCancel();
	}
	
	
	/**
	 * The Save-Button of the InputDialog.
	 * @see com.googlecode.mgwt.ui.client.widget.base.ButtonBase ButtonBase
	 */
  private static class OpenButton extends ButtonBase {
    public OpenButton(DialogCss css, String text) {
      super(css);
      setText(text);
      addStyleName(css.okbutton());
    }
  }
	
  /**
   * The Cancel-Button of the InputDialog.
   * @see com.googlecode.mgwt.ui.client.widget.base.ButtonBase ButtonBase
   */
  private static class CancelButton extends ButtonBase {
    public CancelButton(DialogCss css, String text) {
      super(css);
      setText(text);
      addStyleName(css.cancelbutton());
    }
  }
  
	PopinDialog popinDialog;
	private DialogPanel dialogPanel;
	private DialogCss css;
	private FlowPanel buttonContainer;
	OpenCallback callback;
	private Storage stockStore;
	private ListBox list;

  
	
	public OpenDialog(Storage stockStore, OpenCallback callback)
  {
	  super();
	  this.callback = callback;
		this.css =  MGWTStyle.getTheme().getMGWTClientBundle().getDialogCss();
		this.popinDialog = new PopinDialog(this.css);
		this.stockStore = stockStore;
		setCloseOnBackgroundClick();
		
		initDialogPanel();
		setTitleText("Open");
		addFileChooser();
		addButtonContainer();
  }

	/**
	 * generates a ListBox with the saved files
	 */
	private void addFileChooser()
  {

		try
    {
	    if (this.stockStore != null){
	    	this.list = new ListBox();
	      for (int i = 0; i < this.stockStore.getLength(); i++){
	        this.list.addItem(this.stockStore.key(i));
	      }
	      this.list.addStyleName("listBoxToOpen");
		    this.dialogPanel.getContent().add(this.list);
	    }
	    else {
	    	System.out.println("keine daten da");
	    	this.dialogPanel.getContent().add(new Label("No files found"));
	    }
    }
    catch (Exception e)
    {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }

  }


	/**
	 * Initializes the dialogPanel and adds it to the popinDialog.
	 */
	private void initDialogPanel()
	{
		this.dialogPanel = new DialogPanel();
		this.dialogPanel.addStyleName("opendialog");
		this.popinDialog.add(this.dialogPanel);
	}
	
	/**
	 * @see com.googlecode.mgwt.ui.client.dialog.HasTitleText#setTitleText(java.lang.String)
	 */
	public void setTitleText(String title)
	{
		this.dialogPanel.getDialogTitle().setHTML(title);
	}
	
	/**
	 * Adds all the buttons to the dialog. Horizontal alignment.
	 */
	private void addButtonContainer()
	{
    this.buttonContainer = new FlowPanel();
    this.buttonContainer.addStyleName(this.css.footer());

    addOpenButton();
    addCancelButton();
    
    this.dialogPanel.getContent().add(this.buttonContainer);
	}

	
	/**
	 * Adds the Open-button to the dialog, adds a TapHandler.
	 */
	private void addOpenButton()
	{
    OpenButton openButton = new OpenButton(this.css, "Open");
    openButton.addDomHandler(new ClickHandler()
		{

			@Override
      public void onClick(ClickEvent event)
      {
	      OpenDialog.this.popinDialog.hide();
	      if (OpenDialog.this.callback != null)
	      {
	      	OpenDialog.this.callback.onOpen();
	      }
      }
		}, ClickEvent.getType());

		this.dialogPanel.showOkButton(false); //don't show the default buttons from Daniel Kurka
    this.buttonContainer.add(openButton);
	}
	
	/**
	 * Adds the CANCEL-button to the dialog, adds a TapHandler.
	 */
	private void addCancelButton()
  {
	  CancelButton cancelButton = new CancelButton(this.css, "Cancel");
    cancelButton.addDomHandler(new ClickHandler()
		{
			@Override
      public void onClick(ClickEvent event)
      {
				OpenDialog.this.popinDialog.hide();
				if (OpenDialog.this.callback != null)
					OpenDialog.this.callback.onCancel();
      }
			
		}, ClickEvent.getType());
		this.dialogPanel.showCancelButton(false);
    this.buttonContainer.add(cancelButton);
  }
	
	/**
	 * Shows the dialog.
	 * @see com.googlecode.mgwt.ui.client.dialog.Dialog#show()
	 */
	@Override
  public void show()
	{
		this.popinDialog.center();
	}

	/**
	 * Closes the dialog
	 * @see com.googlecode.mgwt.ui.client.dialog.AnimatableDialogBase#hide()
	 */
	public void close()
	{
		this.popinDialog.hide();
	}

	/**
	 * @see com.googlecode.mgwt.ui.client.dialog.AnimatableDialogBase#setHideOnBackgroundClick(boolean)
	 */
	private void setCloseOnBackgroundClick()
	{
		this.popinDialog.setHideOnBackgroundClick(true);
	}
	
	/**
	 * Returns the chosen file as String.
	 */
	public String getChosenFile()
	{
		String key = this.list.getValue(this.list.getSelectedIndex());
		return this.stockStore.getItem(key);
	}
}
