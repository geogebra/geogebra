package geogebra.mobile.gui.elements.header;

import geogebra.common.main.GWTKeycodes;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.mgwt.dom.client.event.touch.TouchCancelEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchEndEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchHandler;
import com.googlecode.mgwt.dom.client.event.touch.TouchMoveEvent;
import com.googlecode.mgwt.dom.client.event.touch.TouchStartEvent;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.dialog.Dialog;
import com.googlecode.mgwt.ui.client.dialog.DialogPanel;
import com.googlecode.mgwt.ui.client.dialog.HasTitleText;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.theme.base.DialogCss;
import com.googlecode.mgwt.ui.client.widget.base.ButtonBase;
import com.googlecode.mgwt.ui.client.widget.buttonbar.TrashButton;

public class OpenSaveDialog implements HasText, HasTitleText, Dialog
{
	/**
	 * The callback used when buttons are taped.
	 */
	public interface OpenCallback
	{
		/**
		 * Called if open button is clicked.
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

	/**
	 * The Open-Button of the InputDialog.
	 * 
	 * @see com.googlecode.mgwt.ui.client.widget.base.ButtonBase ButtonBase
	 */
	private static class OKButton extends ButtonBase
	{
		public OKButton(DialogCss css, String text)
		{
			super(css);
			setText(text);
			addStyleName(css.okbutton());
		}
	}

	/**
	 * The Cancel-Button of the InputDialog.
	 * 
	 * @see com.googlecode.mgwt.ui.client.widget.base.ButtonBase ButtonBase
	 */
	private static class CancelButton extends ButtonBase
	{
		public CancelButton(DialogCss css, String text)
		{
			super(css);
			setText(text);
			addStyleName(css.cancelbutton());
		}
	}

	PopinDialog popinDialog;
	private DialogPanel dialogPanel;
	private DialogCss css;
	private FlowPanel buttonContainer;

	OpenCallback openCallback;
	SaveCallback saveCallback;
	boolean isOpenDialog;

	Storage stockStore;
	private String fileName;
	private ListBox list;
	TextBox textInput;

	// private boolean noFiles;

	public OpenSaveDialog(String fileName, OpenCallback openCallback)
	{
		super();

		this.isOpenDialog = true;
		this.openCallback = openCallback;
		this.fileName = fileName;

		initDialog();
		setTitleText("Open");
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
		setTitleText("Save");
	}

	private void initDialog()
	{
		this.css = MGWTStyle.getTheme().getMGWTClientBundle().getDialogCss();
		this.popinDialog = new PopinDialog(this.css);
		this.stockStore = Storage.getLocalStorageIfSupported();
		setCloseOnBackgroundClick();

		initDialogPanel();

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
						OpenSaveDialog.this.openCallback.onOpen();
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
		this.dialogPanel.getContent().add(this.textInput);
	}

	/**
	 * generates a ListBox with the saved files
	 */
	private void addFileChooser()
	{
		this.list = new ListBox();
		this.list.addItem("(None)");

		try
		{
			if (this.stockStore.key(0) != null)
			{
				for (int i = 0; i < this.stockStore.getLength(); i++)
				{
					this.list.addItem(this.stockStore.key(i));
				}
				this.list.addStyleName("listBoxToOpen");
				this.dialogPanel.getContent().add(this.list);

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
				this.dialogPanel.getContent().add(new Label("No files found"));
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
				setText(getFileName());
			}
		});
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
	 * Adds all the buttons to the dialog. Horizontal alignment.
	 */
	private void addButtonContainer()
	{
		this.buttonContainer = new FlowPanel();
		this.buttonContainer.addStyleName(this.css.footer());

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
		this.dialogPanel.getContent().add(this.buttonContainer);
	}

	/**
	 * Adds the DELETE-Button to the dialog.
	 */
	private void addDeleteButton()
	{
		TrashButton deleteButton = new TrashButton();

		deleteButton.addTouchHandler(new TouchHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{
				OpenSaveDialog.this.stockStore.removeItem(getText());
			}

			@Override
			public void onTouchMove(TouchMoveEvent event)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onTouchEnd(TouchEndEvent event)
			{
				OpenSaveDialog.this.close();
			}

			@Override
			public void onTouchCanceled(TouchCancelEvent event)
			{
				// TODO Auto-generated method stub
			}
		});

		this.buttonContainer.add(deleteButton);
	}

	/**
	 * Adds the OPEN-button to the dialog, adds a TapHandler.
	 */
	private void addOpenButton()
	{
		OKButton openButton = new OKButton(this.css, "Open");
		openButton.addDomHandler(new ClickHandler()
		{

			@Override
			public void onClick(ClickEvent event)
			{
				OpenSaveDialog.this.popinDialog.hide();

				if (OpenSaveDialog.this.openCallback != null && !getText().equals("(None)"))
				{
					OpenSaveDialog.this.openCallback.onOpen();
				}
				else
				{
					OpenSaveDialog.this.openCallback.onCancel();
				}
			}
		}, ClickEvent.getType());

		this.buttonContainer.add(openButton);
	}

	/**
	 * Adds the SAVE-button to the dialog, adds a TapHandler.
	 */
	private void addSaveButton()
	{
		OKButton saveButton = new OKButton(this.css, "Save");
		saveButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				OpenSaveDialog.this.popinDialog.hide();

				if (OpenSaveDialog.this.saveCallback != null && !getText().equals("(None)"))
				{
					OpenSaveDialog.this.saveCallback.onSave();
				}
				else
				{
					OpenSaveDialog.this.saveCallback.onCancel();
				}
			}
		}, ClickEvent.getType());

		this.dialogPanel.showOkButton(false); // don't show the default buttons from
		                                      // Daniel Kurka
		this.buttonContainer.add(saveButton);
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
				OpenSaveDialog.this.popinDialog.hide();
				if (OpenSaveDialog.this.openCallback != null)
					OpenSaveDialog.this.openCallback.onCancel();
			}

		}, ClickEvent.getType());
		this.dialogPanel.showCancelButton(false); // don't show the default buttons
																							// from
		this.dialogPanel.showOkButton(false); // Daniel Kurka
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
		this.popinDialog.center();
	}

	/**
	 * Closes the dialog
	 * 
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
	 * Returns the chosen file as (xml-)String.
	 */
	public String getChosenFile()
	{
		String key = this.list.getValue(this.list.getSelectedIndex());
		return this.stockStore.getItem(key);
	}

	/**
	 * Returns the chosen filename.
	 */
	public String getFileName()
	{
		return this.list.getValue(this.list.getSelectedIndex());
	}

	@Override
	public String getTitleText()
	{
		return this.dialogPanel.getDialogTitle().getHTML();
	}

	/**
	 * Returns the text of the text-input.
	 */
	@Override
	public String getText()
	{
		return this.textInput.getText();
	}

	/**
	 * @see com.google.gwt.user.client.ui.HasText#setText(java.lang.String)
	 */
	@Override
	public void setText(String text)
	{
		this.textInput.setText(text);
	}

	/**
	 * @see com.googlecode.mgwt.ui.client.dialog.HasTitleText#setTitleText(java.lang.String)
	 */
	@Override
	public void setTitleText(String title)
	{
		this.dialogPanel.getDialogTitle().setHTML(title);
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
