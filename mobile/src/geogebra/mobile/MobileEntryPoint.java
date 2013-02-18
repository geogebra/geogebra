package geogebra.mobile;

import geogebra.mobile.gui.GuiResources;
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
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MobileEntryPoint implements EntryPoint
{
	static Place defaultPlace = new TabletGuiPlace("TabletGui");
	static SimplePanel appWidget = new SimplePanel();

	@Override
	public void onModuleLoad()
	{
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

	private static void loadMobileAsync()
	{
		GWT.runAsync(new RunAsyncCallback()
		{

			@Override
			public void onSuccess()
			{	
				EventBus eventBus = ClientFactory.getEventBus();
				PlaceController placeController = ClientFactory.getPlaceController();

				// Start ActivityManager for the main widget with our ActivityMapper
				ActivityMapper activityMapper = new AppActivityMapper();
				ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
				activityManager.setDisplay(MobileEntryPoint.appWidget);

				// Start PlaceHistoryHandler with our PlaceHistoryMapper
				AppPlaceHistoryMapper historyMapper= GWT.create(AppPlaceHistoryMapper.class);
				PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
				historyHandler.register(placeController, eventBus, MobileEntryPoint.defaultPlace);

				RootPanel.get().add(MobileEntryPoint.appWidget);
				// Goes to place represented on URL or default place
				historyHandler.handleCurrentHistory();
				
				MobileApp app = new MobileApp(ClientFactory.getTabletGui());
				app.start();
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