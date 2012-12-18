package geogebra.mobile.gui.elements.header;

import geogebra.common.kernel.Kernel;
import geogebra.mobile.MobileApp;
import geogebra.mobile.gui.TabletGUI;
import geogebra.mobile.gui.elements.header.SaveDialog;
import geogebra.mobile.gui.elements.header.SaveDialog.SaveCallback;
import geogebra.mobile.gui.elements.header.OpenDialog;
import geogebra.mobile.gui.elements.header.OpenDialog.OpenCallback;
import geogebra.mobile.model.GuiModel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.mgwt.ui.client.widget.HeaderButton;

import com.google.gwt.storage.client.Storage;

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

	// to save xml-strings
	Storage stockStore = null;
	SaveDialog saveDialog;
	OpenDialog openDialog;

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

	private void initSaveButton(HeaderButton[] left)
	{
		left[2].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				TabletHeaderPanelLeft.this.stockStore = Storage.getLocalStorageIfSupported();

				TabletHeaderPanelLeft.this.saveDialog = new SaveDialog(getFileName(), new SaveCallback()
				{

					@Override
					public void onSave()
					{
						String ggbXML = TabletHeaderPanelLeft.this.app.getXML();
						TabletHeaderPanelLeft.this.stockStore = Storage.getLocalStorageIfSupported();
						if (TabletHeaderPanelLeft.this.stockStore != null && TabletHeaderPanelLeft.this.saveDialog.getText() != null)
						{
							TabletHeaderPanelLeft.this.stockStore.setItem(TabletHeaderPanelLeft.this.saveDialog.getText(), ggbXML);
						}
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

	private void initOpenButton(HeaderButton[] left)
	{
		left[1].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				TabletHeaderPanelLeft.this.stockStore = Storage.getLocalStorageIfSupported();
				TabletHeaderPanelLeft.this.openDialog = new OpenDialog(TabletHeaderPanelLeft.this.stockStore, new OpenCallback()
				{

					@Override
					public void onOpen()
					{
						String xml = TabletHeaderPanelLeft.this.openDialog.getChosenFile();
						try
						{
							TabletHeaderPanelLeft.this.app.loadXML(xml);
							if (TabletHeaderPanelLeft.this.openDialog.getFileName() != getFileName())
							{
								changeTitle(TabletHeaderPanelLeft.this.openDialog.getFileName());
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

	private void initNewButton(final Kernel kernel, final GuiModel guiModel, HeaderButton[] left)
	{
		left[0].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				guiModel.closeOptions();
				kernel.clearConstruction();
				kernel.notifyRepaint();
				changeTitle("New File");
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