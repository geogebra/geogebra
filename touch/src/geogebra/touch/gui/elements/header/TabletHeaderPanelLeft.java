package geogebra.touch.gui.elements.header;

import geogebra.common.kernel.Kernel;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.CommonResources;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.dialogs.FileDialog;
import geogebra.touch.gui.dialogs.InputDialog;
import geogebra.touch.gui.dialogs.InputDialog.DialogType;
import geogebra.touch.gui.dialogs.OpenFileDialog;
import geogebra.touch.gui.dialogs.SaveFileDialog;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.model.GuiModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * ButtonBar for the buttons on the left side of the HeaderPanel.
 * 
 * @author Thomas Krismayer
 * 
 */
public class TabletHeaderPanelLeft extends HorizontalPanel
{
	Kernel kernel;
	TouchApp app;
	GuiModel guiModel;
	TabletGUI tabletGUI;

	InputDialog dialog;
	FileDialog openDialog, saveDialog;

	private StandardImageButton newButton = new StandardImageButton(CommonResources.INSTANCE.document_new());
	private StandardImageButton openButton = new StandardImageButton(CommonResources.INSTANCE.document_open());
	private StandardImageButton saveButton = new StandardImageButton(CommonResources.INSTANCE.document_save());
	private StandardImageButton ggtButton = new StandardImageButton(CommonResources.INSTANCE.geogebra_tube());

	/**
	 * Generates the Buttons for the left HeaderPanel.
	 */
	public TabletHeaderPanelLeft(TabletGUI tabletGUI, TouchApp app, final GuiModel guiModel)
	{
		this.app = app;
		this.kernel = app.getKernel();

		this.tabletGUI = tabletGUI;
		this.guiModel = guiModel;

		this.dialog = new InputDialog(DialogType.Title);

		this.openDialog = new OpenFileDialog(this.app);
		this.saveDialog = new SaveFileDialog(this.app);

		initNewButton();
		initOpenButton();
		initSaveButton();
		initGGTButton();

		this.add(this.newButton);
		this.add(this.openButton);
		this.add(this.saveButton);
		this.add(this.ggtButton);
	}

	private void initNewButton()
	{
		this.newButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				TabletHeaderPanelLeft.this.dialog.show();
			}
		}, ClickEvent.getType());

		this.dialog.addCloseHandler(new CloseHandler<PopupPanel>()
		{

			@Override
			public void onClose(CloseEvent<PopupPanel> event)
			{
				String result = TabletHeaderPanelLeft.this.dialog.getInput();

				if (!result.isEmpty())
				{
					TabletHeaderPanelLeft.this.guiModel.closeOptions();
					TabletHeaderPanelLeft.this.kernel.getApplication().getGgbApi().newConstruction();
					TabletHeaderPanelLeft.this.app.setConstructionTitle(result);
				}
			}
		});
	}

	private void initOpenButton()
	{
		this.openButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				TabletHeaderPanelLeft.this.openDialog.show();
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
				event.preventDefault();
				TabletHeaderPanelLeft.this.saveDialog.show();
			}
		}, ClickEvent.getType());
	}

	private void initGGTButton()
	{
		this.ggtButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				TabletHeaderPanelLeft.this.guiModel.closeOptions();

				TouchEntryPoint.showTubeSearchUI();
			}
		}, ClickEvent.getType());
	}

	/**
	 * Sets the title in the {@link TabletHeaderPanel tabletHeader}
	 * 
	 * @param title
	 */
	@Override
	public void setTitle(String title)
	{
		// FIXME ugly, implement observer pattern!
		this.tabletGUI.getLAF().setTitle(title);
	}
}