package geogebra.mobile.gui.elements.header;

import geogebra.common.kernel.Kernel;
import geogebra.mobile.MobileApp;
import geogebra.mobile.gui.elements.header.SaveDialog;
import geogebra.mobile.gui.elements.header.SaveDialog.SaveCallback;
import geogebra.mobile.gui.elements.header.OpenDialog;
import geogebra.mobile.gui.elements.header.OpenDialog.OpenCallback;
import geogebra.mobile.model.GuiModel;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
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
	
	//to save xml-strings
	Storage stockStore = null;
	SaveDialog saveDialog;
	OpenDialog openDialog;

	
	//private GgbAPI ggbAPI;//no need for xml string

	/**
	 * Generates the {@link HeaderButton buttons} for the left HeaderPanel.
	 */
	public TabletHeaderPanelLeft(final Kernel kernel, final GuiModel guiModel)
	{
		
		this.app = (MobileApp) kernel.getApplication();
		//this.ggbAPI = new GgbAPI(this.app);//no need for xml string

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
		
		//new - button
		left[0].addTapHandler(new TapHandler() {
			
			@Override
			public void onTap(TapEvent event) {
				//TODO 
				guiModel.closeOptions(); 
				kernel.clearConstruction(); 
				kernel.notifyRepaint(); 				
			}
		});
		
		//save - button
		left[2].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				TabletHeaderPanelLeft.this.stockStore = Storage.getLocalStorageIfSupported();

				TabletHeaderPanelLeft.this.saveDialog = new SaveDialog(new SaveCallback() {

					@Override
          public void onSave()
          {
						String ggbXML = TabletHeaderPanelLeft.this.app.getXML();
						TabletHeaderPanelLeft.this.stockStore = Storage.getLocalStorageIfSupported();
						if (TabletHeaderPanelLeft.this.stockStore != null && TabletHeaderPanelLeft.this.saveDialog.getText() != null) {
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
		
		
		//open
		left[1].addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				event.preventDefault();
				TabletHeaderPanelLeft.this.stockStore = Storage.getLocalStorageIfSupported();
				TabletHeaderPanelLeft.this.openDialog = new OpenDialog(TabletHeaderPanelLeft.this.stockStore, new OpenCallback() {

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
		
		
		
//		left[1].addTapHandler(new TapHandler() {
//
//			@Override
//      public void onTap(TapEvent event)
//      {
//				TabletHeaderPanelLeft.this.stockStore = Storage.getLocalStorageIfSupported();
//				if (TabletHeaderPanelLeft.this.stockStore != null){
//				  for (int i = 0; i < TabletHeaderPanelLeft.this.stockStore.getLength(); i++){
//				    String key = TabletHeaderPanelLeft.this.stockStore.key(i);
//				    System.out.println("opened from local storage: " + TabletHeaderPanelLeft.this.stockStore.getItem(key));
//				  }
//				}
//				//openFile();
//				//MobileApp app = (MobileApp) kernel.getApplication();
//				//app.getGuiManager().openURL();
//      }
//			
//		});
		
//		//save - button
//		left[2].addTapHandler(new TapHandler() {
//
//			@Override
//      public void onTap(TapEvent event)
//      {
//				//System.out.println("vor callback");
//				//JavaScriptObject callback = MobileGoogleApis.getPutFileCallback("fileName.getText()", "description.getText()");
//				//System.out.println("nach callback");
//				//((geogebra.mobile.gui.elements.header.GgbAPI)TabletHeaderPanelLeft.this.app.getGgbApi()).getBase64(callback);
//				//callback(dataURI.substr(dataURI.indexOf(',')+1));
//				//System.out.println("nach get base");
//				//final String ggbXML = ((geogebra.mobile.gui.elements.header.GgbAPI)TabletHeaderPanelLeft.this.app.getGgbApi()).getArchiveContent();
//				String ggbXML = TabletHeaderPanelLeft.this.app.getXML();
//				System.out.println(ggbXML);
//				TabletHeaderPanelLeft.this.stockStore = Storage.getLocalStorageIfSupported();
//				if (TabletHeaderPanelLeft.this.stockStore != null) {
//					TabletHeaderPanelLeft.this.stockStore.setItem("testOne", ggbXML);
//					System.out.println("saved");
//				}
//      }
//			
//		});
	}
}