package geogebra.mobile.gui.elements.header;

import geogebra.common.kernel.Kernel;
import geogebra.mobile.MobileApp;
import geogebra.mobile.gui.elements.header.OpenDialog.OpenCallback;
import geogebra.mobile.gui.elements.header.SaveDialog.SaveCallback;
import geogebra.mobile.model.GuiModel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.storage.client.Storage;
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

	// to save xml-strings
	Storage stockStore = null;
	SaveDialog saveDialog;
	OpenDialog openDialog;

	// private GgbAPI ggbAPI;//no need for xml string

	/**
	 * Generates the {@link HeaderButton buttons} for the left HeaderPanel.
	 */
	public TabletHeaderPanelLeft(final Kernel kernel, final GuiModel guiModel)
	{

		this.app = (MobileApp) kernel.getApplication();
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

		// new - button
		left[0].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				guiModel.closeOptions();
				kernel.clearConstruction();
				kernel.notifyRepaint();
			}
		}, ClickEvent.getType());

		// open
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

		// save - button
		left[2].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				TabletHeaderPanelLeft.this.stockStore = Storage.getLocalStorageIfSupported();

				TabletHeaderPanelLeft.this.saveDialog = new SaveDialog(new SaveCallback()
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
}