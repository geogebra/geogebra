package geogebra.mobile;

import geogebra.mobile.gui.GuiResources;
import geogebra.mobile.gui.TabletGUI;
import geogebra.mobile.gui.TubeSearchUI;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MobileEntryPoint implements EntryPoint
{
  static DeckPanel appWidget = new DeckPanel();
	static TabletGUI tabletGUI = new TabletGUI();
	static TubeSearchUI tubeSearchUI = new TubeSearchUI();

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
				RootPanel.get().add(MobileEntryPoint.appWidget);
				
				MobileApp app = new MobileApp(MobileEntryPoint.tabletGUI);

				app.start();
				MobileEntryPoint.showTabletGUI();
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
			MobileEntryPoint.appWidget.remove(MobileEntryPoint.tubeSearchUI);
			MobileEntryPoint.appWidget.add(MobileEntryPoint.tabletGUI);
			MobileEntryPoint.appWidget.showWidget(0);
  }
	
	public static void showTubeSearchUI()
	{
		MobileEntryPoint.appWidget.remove(MobileEntryPoint.tabletGUI);
		MobileEntryPoint.appWidget.add(MobileEntryPoint.tubeSearchUI);
		MobileEntryPoint.appWidget.showWidget(0);
	}
}