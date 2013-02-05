package geogebra.mobile;

import geogebra.mobile.gui.GuiResources;
import geogebra.mobile.gui.TabletGUI;
import geogebra.mobile.mvp.AppActivityMapper;
import geogebra.mobile.mvp.AppPlaceHistoryMapper;
import geogebra.mobile.place.TabletGuiPlace;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MobileEntryPoint implements EntryPoint
{
	MobileApp app;
	private Place defaultPlace = new TabletGuiPlace("TabletGui");

	@Override
	public void onModuleLoad()
	{
		this.app = new MobileApp(new TabletGUI());

		// this.app = new MobileApp(new IconTestGUI());
		loadMobileAsync();

		// insert mathquill css
		String mathquillcss = GuiResources.INSTANCE.mathquillCss().getText();
	
		//FIXME does this do anything? the resulting string is ignored.
		mathquillcss.replace("url(mobile/font/Symbola",
				"url(" + GWT.getModuleBaseURL() + "font/Symbola");
		mathquillcss.replace("url(web/font/Symbola",
				"url(" + GWT.getModuleBaseURL() + "font/Symbola");
		StyleInjector.inject(mathquillcss);
		
		
	}

	private void loadMobileAsync()
	{
		GWT.runAsync(new RunAsyncCallback()
		{

			@Override
			public void onSuccess()
			{
				MobileEntryPoint.this.app.start();
				
				
				TabletGUI gui = (TabletGUI)app.getMobileGui(); 
				
				
				// Create ClientFactory using deferred binding so we can replace with different
				// impls in gwt.xml
//				ClientFactory clientFactory = GWT.create(ClientFactory.class);
				
				ClientFactory clientFactory = new ClientFactoryImpl();
				EventBus eventBus = clientFactory.getEventBus();
				PlaceController placeController = clientFactory.getPlaceController();

				// Start ActivityManager for the main widget with our ActivityMapper
				ActivityMapper activityMapper = new AppActivityMapper(clientFactory);
				ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
				activityManager.setDisplay(gui);

				// Start PlaceHistoryHandler with our PlaceHistoryMapper
				AppPlaceHistoryMapper historyMapper= GWT.create(AppPlaceHistoryMapper.class);
				PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
				historyHandler.register(placeController, eventBus, defaultPlace);

				RootPanel.get().add(gui);
				// Goes to place represented on URL or default place
				historyHandler.handleCurrentHistory();
				
				
				
			}

			@Override
			public void onFailure(Throwable reason)
			{
				// App.debug(reason);
				reason.printStackTrace();
			}
		});
	}
}