package geogebra.touch.gui.elements.header;

import geogebra.common.kernel.Kernel;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.CommonResources;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.dialogs.OpenSaveDialog;
import geogebra.touch.gui.dialogs.OpenSaveDialog.OpenCallback;
import geogebra.touch.gui.dialogs.OpenSaveDialog.SaveCallback;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.model.GuiModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * ButtonBar for the buttons on the left side of the HeaderPanel.
 * 
 * @author Thomas Krismayer
 * 
 */
public class TabletHeaderPanelLeft extends HorizontalPanel
{
	TouchApp app;
	TabletGUI tabletGUI;
	OpenSaveDialog saveDialog;
	OpenSaveDialog openDialog;

	/**
	 * Generates the {@link HeaderButton buttons} for the left HeaderPanel.
	 */
	public TabletHeaderPanelLeft(TabletGUI tabletGUI, final Kernel kernel, final GuiModel guiModel)
	{
		this.app = (TouchApp) kernel.getApplication();
		this.tabletGUI = tabletGUI;

		StandardImageButton[] left = new StandardImageButton[4];

		left[0] = new StandardImageButton(CommonResources.INSTANCE.document_new());
		left[1] = new StandardImageButton(CommonResources.INSTANCE.document_open());
		left[2] = new StandardImageButton(CommonResources.INSTANCE.document_save());
		
		left[3] = new StandardImageButton(CommonResources.INSTANCE.geogebra_tube());
		left[3].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				guiModel.closeOptions();
				
				TouchEntryPoint.showTubeSearchUI();
			}
		}, ClickEvent.getType());

		for (int i = 0; i < left.length; i++)
		{
			this.add(left[i]);
		}

		initNewButton(kernel, guiModel, left);
		initOpenButton(left);
		initSaveButton(left);
	}

	/**
	 * Opens a new file and sets the title to "New File".
	 * 
	 * @param kernel
	 * @param guiModel
	 * @param left
	 *          the buttons on the left side of the headerPanel
	 */
	private void initNewButton(final Kernel kernel, final GuiModel guiModel, StandardImageButton[] left)
	{
		left[0].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				guiModel.closeOptions();
				kernel.clearConstruction(true);
				kernel.initUndoInfo();
				kernel.notifyRepaint();
				changeTitle("New File");
			}
		}, ClickEvent.getType());
	}

	/**
	 * By clicking the {@link HeaderButton OPEN-button}, a dialog opens. OPEN - it
	 * opens the selected file and changes the title of the headerPanel. CANCEL -
	 * nothing happens.
	 * 
	 * @param left
	 *          the buttons on the left side of the headerPanel
	 */
	private void initOpenButton(StandardImageButton[] left)
	{
		left[1].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				TabletHeaderPanelLeft.this.openDialog = new OpenSaveDialog(getFileName(), new OpenCallback()
				{
					@Override
					public void onOpen()
					{
						String xml = TabletHeaderPanelLeft.this.openDialog.getChosenFile();
						String fileName = TabletHeaderPanelLeft.this.openDialog.getFileName();

						try
						{
							TabletHeaderPanelLeft.this.app.loadXML(xml);
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (!fileName.equals(getFileName()))
						{
							changeTitle(fileName);
						}
					}

					@Override
					public void onCancel()
					{
						TabletHeaderPanelLeft.this.openDialog.close();
					}
				});

				TabletHeaderPanelLeft.this.openDialog.show();
			}
		}, ClickEvent.getType());
	}

	/**
	 * By clicking the {@link HeaderButton SAVE-button}, a dialog opens. SAVE - it
	 * saves the construction and changes the title of the headerPanel. CANCEL -
	 * nothing happens.
	 * 
	 * @param left
	 *          the buttons on the left side of the headerPanel
	 */
	private void initSaveButton(StandardImageButton[] left)
	{
		left[2].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				TabletHeaderPanelLeft.this.saveDialog = new OpenSaveDialog(getFileName(), new SaveCallback()
				{
					@Override
					public void onSave()
					{
						TabletHeaderPanelLeft.this.saveDialog.save(TabletHeaderPanelLeft.this.app.getXML());

						if (!TabletHeaderPanelLeft.this.saveDialog.getText().equals(getFileName()))
						{
							changeTitle(TabletHeaderPanelLeft.this.saveDialog.getText());
						}
					}

					@Override
					public void onCancel()
					{
						TabletHeaderPanelLeft.this.saveDialog.close();
					}
				});

				TabletHeaderPanelLeft.this.saveDialog.show();
			}
		}, ClickEvent.getType());
	}

	/**
	 * 
	 * @return fileName the title defined in the {@link TabletHeaderPanel
	 *         tabletHeader}
	 */
	String getFileName()
	{
		return this.tabletGUI.getTabletHeaderPanel().getTitle();
	}

	/**
	 * Sets the title in the {@link TabletHeaderPanel tabletHeader}
	 * 
	 * @param title
	 */
	void changeTitle(String title)
	{
		this.tabletGUI.getTabletHeaderPanel().setTitle(title);
	}
}