package geogebra.touch.gui.dialogs;

import org.vectomatic.dom.svg.ui.SVGResource;

import geogebra.common.main.GWTKeycodes;
import geogebra.touch.gui.CommonResources;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

public class OpenSaveDialog extends PopupPanel
{
	/**
	 * The callback used when buttons are taped.
	 */
	public interface OpenCallback
	{
		/**
		 * Called if open button is clicked.
		 * 
		 * @throws Exception
		 */
		public void onOpen();

		/**
		 * Called if cancel button is clicked.
		 */
		public void onCancel();
	}

	public interface SaveCallback
	{
		/**
		 * Called if save button is clicked.
		 */
		public void onSave();

		/**
		 * Called if cancel button is clicked.
		 */
		public void onCancel();
	}

	HorizontalPanel buttonContainer;

	OpenCallback openCallback;
	SaveCallback saveCallback;
	boolean isOpenDialog;

	Storage stockStore;
	private String fileName;
	ListBox list;
	TextBox textInput;

	public OpenSaveDialog(String fileName, OpenCallback openCallback)
	{
		this.setGlassEnabled(true);

		this.isOpenDialog = true;
		this.openCallback = openCallback;
		this.fileName = fileName;

		initDialog();
		setTitle("Open");
	}

	/**
	 * Construct a save-dialog.
	 * 
	 * @param title
	 *          - the title of the dialog
	 * @param saveCallback
	 *          - the callback used when a button of the dialog is taped
	 */
	public OpenSaveDialog(String fileName, SaveCallback saveCallback)
	{
		super();
		this.isOpenDialog = false;
		this.saveCallback = saveCallback;
		this.fileName = fileName;

		initDialog();
		setTitle("Save");
	}

	private void initDialog()
	{
		this.stockStore = Storage.getLocalStorageIfSupported();

		addProgressIndicator();
		addTextBox(this.fileName);
		addFileChooser();
		addButtonContainer();

		this.textInput.addKeyUpHandler(new KeyUpHandler()
		{
			@Override
			public void onKeyUp(KeyUpEvent event)
			{
				if (event.getNativeKeyCode() == GWTKeycodes.KEY_ENTER)
				{
					if (OpenSaveDialog.this.isOpenDialog)
					{
						try
						{
							OpenSaveDialog.this.openCallback.onOpen();
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else
					{
						OpenSaveDialog.this.saveCallback.onSave();
					}
					OpenSaveDialog.this.close();
				}
			}
		});
	}

	private void addProgressIndicator()
	{
		// TODO add progress indicator
	}

	/**
	 * Adds a textbox with the given text to the dialogPanel.
	 * 
	 * @param text
	 */
	private void addTextBox(String text)
	{
		this.textInput = new TextBox();
		this.textInput.addStyleName("savetextinput");
		setText(text);
		this.add(this.textInput);
	}

	/**
	 * generates a ListBox with the saved files
	 */
	private void addFileChooser()
	{
		this.list = new ListBox();
		this.list.getElement().setAttribute("style", "height: 35px");
		this.list.addStyleName("openSaveListBox");
		this.list.addItem("(None)");

		try
		{
			if (this.stockStore.key(0) != null)
			{
				for (int i = 0; i < this.stockStore.getLength(); i++)
				{
					this.list.addItem(this.stockStore.key(i));
				}
				this.add(this.list);

				if (isFileSaved())
				{
					this.list.setItemSelected(getIndexOfItem(), true);
				}
				else
				{
					this.list.setItemSelected(0, true);
				}
			}
			else
			{
				this.add(new Label("No files found"));
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.list.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event)
			{
				setFileName();
				setText(getFileName());
			}
		});
	}

	/**
	 * Adds all the buttons to the dialog. Horizontal alignment. Disables default
	 * buttons from Daniel Kurka.
	 */
	private void addButtonContainer()
	{

		this.buttonContainer = new HorizontalPanel();

		if (this.isOpenDialog)
		{
			// if no file is saved, just the CANCEL-Button is visible
			if (this.stockStore.key(0) != null)
			{
				addOpenButton();
				addCancelButton();
				addDeleteButton();
			}
			else
			{
				addCancelButton();
			}
		}
		else
		{
			addSaveButton();
			addCancelButton();
			// if no file is saved, the DELETE-Button is not visible
			if (this.stockStore.key(0) != null)
			{
				addDeleteButton();
			}
		}
		this.add(this.buttonContainer);
	}

	/**
	 * Adds the DELETE-Button to the dialog.
	 */
	private void addDeleteButton()
	{
		SVGResource icon = CommonResources.INSTANCE.dialog_trash();
		Button deleteButton = new Button();
		String html = "<img src=\"" + icon.getSafeUri().asString() + "\" style=\"height:32px; width:32px; margin:auto;\">";
		deleteButton.getElement().setInnerHTML(html);

		deleteButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				OpenSaveDialog.this.stockStore.removeItem(getText());
				OpenSaveDialog.this.close();
			}
		});
		this.buttonContainer.add(deleteButton);
	}

	/**
	 * Adds the OPEN-button to the dialog, adds a TapHandler.
	 */
	private void addOpenButton()
	{
		SVGResource icon = CommonResources.INSTANCE.dialog_ok();
		Button openButton = new Button();
		String html = "<img src=\"" + icon.getSafeUri().asString() + "\" style=\"height:32px; width:32px; margin:auto;\">";
		openButton.getElement().setInnerHTML(html);

		openButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				// TODO open file
			}
		});
		this.buttonContainer.add(openButton);
	}

	/**
	 * Adds the SAVE-button to the dialog, adds a TapHandler.
	 */
	private void addSaveButton()
	{
		SVGResource icon = CommonResources.INSTANCE.dialog_ok();
		Button saveButton = new Button();
		String html = "<img src=\"" + icon.getSafeUri().asString() + "\" style=\"height:32px; width:32px; margin:auto; background-color: none;\">";
		saveButton.getElement().setInnerHTML(html);
		saveButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				OpenSaveDialog.this.hide();

				if (OpenSaveDialog.this.saveCallback != null && !getText().equals("(None)"))
				{
					OpenSaveDialog.this.saveCallback.onSave();
				}
				else if (OpenSaveDialog.this.saveCallback != null)
				{
					OpenSaveDialog.this.saveCallback.onCancel();
				}
			}
		}, ClickEvent.getType());

		this.buttonContainer.add(saveButton);
	}

	/**
	 * Adds the CANCEL-button to the dialog, adds a TapHandler.
	 */
	private void addCancelButton()
	{
		SVGResource icon = CommonResources.INSTANCE.dialog_cancel();
		Button cancelButton = new Button();
		String html = "<img src=\"" + icon.getSafeUri().asString() + "\" style=\"height:32px; width:32px; margin:auto;\">";
		cancelButton.getElement().setInnerHTML(html);

		cancelButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				OpenSaveDialog.this.hide();
				if (OpenSaveDialog.this.openCallback != null)
					OpenSaveDialog.this.openCallback.onCancel();
			}

		}, ClickEvent.getType());

		this.buttonContainer.add(cancelButton);
	}

	/**
	 * Shows the dialog.
	 * 
	 * @see com.googlecode.mgwt.ui.client.dialog.Dialog#show()
	 */
	@Override
	public void show()
	{
		this.center();
	}

	/**
	 * Closes the dialog
	 * 
	 * @see com.googlecode.mgwt.ui.client.dialog.AnimatableDialogBase#hide()
	 */
	public void close()
	{
		this.hide();
	}

	private String chosenFile;

	protected void setChosenFile()
	{
		String key = this.list.getValue(this.list.getSelectedIndex());
		this.chosenFile = this.stockStore.getItem(key);
	}

	/**
	 * Returns the chosen file as (xml-)String.
	 */
	public String getChosenFile()
	{
		return this.chosenFile;
	}

	private String nameOfFile;

	protected void setFileName()
	{
		this.nameOfFile = this.list.getValue(this.list.getSelectedIndex());
	}

	/**
	 * Returns the chosen filename.
	 */
	public String getFileName()
	{
		return this.nameOfFile;
	}

	/**
	 * Returns the text of the text-input.
	 */
	public String getText()
	{
		return this.textInput.getText();
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasText#setText(java.lang.String)
	 */
	public void setText(String text)
	{
		this.textInput.setText(text);
	}

	/**
	 * 
	 * @return the index of the item in the list of the files. Returns -1 if it's
	 *         a new file.
	 */
	private int getIndexOfItem()
	{
		for (int i = 0; i < this.list.getItemCount(); i++)
		{
			if (this.list.getItemText(i).equals(getText()))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * 
	 * @return true if the file is saved in the {@link ListBox list of files}.
	 *         Otherwise false.
	 */
	private boolean isFileSaved()
	{
		if (getIndexOfItem() != -1)
		{
			return true;
		}
		return false;
	}

	/**
	 * Saves the xml String to the local storage.
	 * 
	 * @param ggbXML
	 *          xml String of the construction
	 */
	public void save(String ggbXML)
	{
		if (this.stockStore != null && getText() != null)
		{
			this.stockStore.setItem(getText(), ggbXML);
		}
	}
}
