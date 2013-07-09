package geogebra.touch;

import geogebra.html5.js.ResourcesInjector;
import geogebra.touch.gui.BrowseGUI;
import geogebra.touch.gui.GuiResources;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.TubeSearchGUI;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.gwtphonegap.client.PhoneGap;
import com.googlecode.gwtphonegap.client.event.BackButtonPressedEvent;
import com.googlecode.gwtphonegap.client.event.BackButtonPressedHandler;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TouchEntryPoint implements EntryPoint
{
	static DeckLayoutPanel appWidget = new DeckLayoutPanel();
	static TabletGUI tabletGUI = new TabletGUI();
	static TubeSearchGUI tubeSearchGUI;
	public static BrowseGUI browseGUI;
	public static final PhoneGap phoneGap = (PhoneGap) GWT.create(PhoneGap.class);

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
				ResourcesInjector.injectResources();

				// TouchEntryPoint.appWidget.setPixelSize(Window.getClientWidth(),
				// Window.getClientHeight());
				RootLayoutPanel.get().add(TouchEntryPoint.appWidget);

				TouchApp app = new TouchApp(TouchEntryPoint.tabletGUI);
				app.start();
				tubeSearchGUI = new TubeSearchGUI(app);
				browseGUI = new BrowseGUI(app);
				TouchEntryPoint.showTabletGUI();
				Window.addResizeHandler(new ResizeHandler()
				{

					@Override
					public void onResize(ResizeEvent event)
					{
						// TouchEntryPoint.appWidget.setPixelSize(event.getWidth(),
						// event.getHeight());
					}
				});

				tabletGUI.getContentWidget().getElement().getStyle().setOverflow(Overflow.VISIBLE);
				app.getScriptManager().ggbOnInit();
				// needed for testing
				if (RootPanel.getBodyElement().getAttribute("data-param-ggbbase64").length() > 0)
				{
					app.getGgbApi().setBase64(RootPanel.getBodyElement().getAttribute("data-param-ggbbase64"));
				}

				initPhoneGap();

				Window.enableScrolling(false);

			}

			private void initPhoneGap()
			{
//			phoneGap.addHandler(new PhoneGapAvailableHandler()
//			{
//				@Override
//				public void onPhoneGapAvailable(PhoneGapAvailableEvent event)
//				{
//					// TODO Auto-generated method stub
//					System.out.println("test onPhoneGapAvailable");
//				}
//			});
//
//			phoneGap.addHandler(new PhoneGapTimeoutHandler()
//			{
//
//				@Override
//				public void onPhoneGapTimeout(PhoneGapTimeoutEvent event)
//				{
//					// TODO Auto-generated method stub
//
//				}
//			});
				phoneGap.initializePhoneGap();
				phoneGap.getEvent().getBackButton().addBackButtonPressedHandler(new BackButtonPressedHandler()
				{

					@Override
					public void onBackButtonPressed(BackButtonPressedEvent event)
					{
						tabletGUI.getTouchModel().getGuiModel().closeOptions();
					}

				});
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
		TouchEntryPoint.appWidget.remove(TouchEntryPoint.browseGUI);
		TouchEntryPoint.appWidget.add(TouchEntryPoint.tabletGUI);
		TouchEntryPoint.appWidget.showWidget(0);
	}

	public static void showTubeSearchUI()
	{
		TouchEntryPoint.appWidget.remove(TouchEntryPoint.tabletGUI);
		TouchEntryPoint.appWidget.remove(TouchEntryPoint.browseGUI);
		TouchEntryPoint.appWidget.add(TouchEntryPoint.tubeSearchGUI);
		TouchEntryPoint.appWidget.showWidget(0);
	}

	public static void showBrowseUI()
	{
		TouchEntryPoint.appWidget.remove(TouchEntryPoint.tabletGUI);
		TouchEntryPoint.appWidget.remove(TouchEntryPoint.tubeSearchGUI);
		TouchEntryPoint.appWidget.add(TouchEntryPoint.browseGUI);
		TouchEntryPoint.appWidget.showWidget(0);
	}
}