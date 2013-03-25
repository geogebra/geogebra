package geogebra.touch;

import geogebra.touch.gui.GuiResources;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.TubeSearchGUI;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TouchEntryPoint implements EntryPoint
{
  static DeckPanel appWidget = new DeckPanel();
	static TabletGUI tabletGUI = new TabletGUI();
	static TubeSearchGUI tubeSearchGUI = new TubeSearchGUI();

	@Override
	public void onModuleLoad()
	{
		loadMobileAsync();

		// insert mathquill css
		String mathquillcss = GuiResources.INSTANCE.mathquillCss().getText();
		StyleInjector.inject(mathquillcss);
	}

	private static void loadMobileAsync()
	{
		GWT.runAsync(new RunAsyncCallback()
		{
			@Override
			public void onSuccess()
			{
				RootPanel.get().add(TouchEntryPoint.appWidget);
				
				TouchApp app = new TouchApp(TouchEntryPoint.tabletGUI);				

				app.start();
				tubeSearchGUI.loadFeatured();
				TouchEntryPoint.showTabletGUI();
			}

			@Override
			public void onFailure(Throwable reason)
			{
				// App.debug(reason);
				reason.printStackTrace();
			}
		});
	}

	public static void showTabletGUI()
  {		
			TouchEntryPoint.appWidget.remove(TouchEntryPoint.tubeSearchGUI);
			TouchEntryPoint.appWidget.add(TouchEntryPoint.tabletGUI);
			TouchEntryPoint.appWidget.showWidget(0);
  }
	
	public static void showTubeSearchUI()
	{
		TouchEntryPoint.appWidget.remove(TouchEntryPoint.tabletGUI);
		TouchEntryPoint.appWidget.add(TouchEntryPoint.tubeSearchGUI);
		TouchEntryPoint.appWidget.showWidget(0);
	}
}