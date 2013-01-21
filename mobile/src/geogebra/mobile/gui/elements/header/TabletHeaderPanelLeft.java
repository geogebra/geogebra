package geogebra.mobile.gui.elements.header;

import geogebra.common.kernel.Kernel;
import geogebra.mobile.MobileApp;
import geogebra.mobile.gui.TabletGUI;
import geogebra.mobile.gui.elements.header.OpenSaveDialog;
import geogebra.mobile.gui.elements.header.OpenSaveDialog.OpenCallback;
import geogebra.mobile.gui.elements.header.OpenSaveDialog.SaveCallback;
import geogebra.mobile.model.GuiModel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;

/**
 * ButtonBar for the buttons on the left side of the HeaderPanel.
 * 
 * @author Thomas Krismayer
 * 
 */
public class TabletHeaderPanelLeft extends HorizontalPanel
{
	MobileApp app;
	TabletGUI tabletGUI;
	OpenSaveDialog saveDialog;
	OpenSaveDialog openDialog;

	// private GgbAPI ggbAPI;//no need for xml string

	/**
	 * Generates the {@link HeaderButton buttons} for the left HeaderPanel.
	 */
	public TabletHeaderPanelLeft(TabletGUI tabletGUI, final Kernel kernel, final GuiModel guiModel)
	{

		this.app = (MobileApp) kernel.getApplication();
		this.tabletGUI = tabletGUI;

		// this.ggbAPI = new GgbAPI(this.app);//no need for xml string

		this.addStyleName("leftHeader");

		HeaderButton[] left = new HeaderButton[3];

		left[0] = new HeaderButton();
		left[0].setText("new");

		left[1] = new HeaderButton();
		left[1].setText("open");

		left[2] = new HeaderButton();
		left[2].setText("save");

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
	private void initNewButton(final Kernel kernel, final GuiModel guiModel, HeaderButton[] left)
	{
		left[0].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				guiModel.closeOptions();
				kernel.clearConstruction(true);
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
	private void initOpenButton(HeaderButton[] left)
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
							if (fileName != getFileName())
							{
								changeTitle(fileName);
							}
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
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
	private void initSaveButton(HeaderButton[] left)
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

						if (TabletHeaderPanelLeft.this.saveDialog.getText() != getFileName())
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
		this.tabletGUI.getTabletHeaderPanel().changeTitle(title);
	}
}