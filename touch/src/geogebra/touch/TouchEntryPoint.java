package geogebra.touch;

import geogebra.html5.js.ResourcesInjector;
import geogebra.touch.gui.BrowseGUI;
import geogebra.touch.gui.GuiResources;
import geogebra.touch.gui.SaveGUI;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.WorksheetGUI;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
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
	static TabletDeckLayoutPanel appWidget = new TabletDeckLayoutPanel();
	static TabletGUI tabletGUI = new TabletGUI();
	static BrowseGUI browseGUI;
	static SaveGUI saveGUI;
	static WorksheetGUI worksheetGUI = new WorksheetGUI();

	static final PhoneGap phoneGap = (PhoneGap) GWT.create(PhoneGap.class);

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

				TouchApp app = new TouchApp(TouchEntryPoint.tabletGUI);
				FileManagerM fm = new FileManagerM();
				browseGUI = new BrowseGUI(app, fm);
				saveGUI = new SaveGUI(app, fm);

				TouchEntryPoint.appWidget.add(TouchEntryPoint.tabletGUI);
				TouchEntryPoint.appWidget.add(TouchEntryPoint.browseGUI);
				TouchEntryPoint.appWidget.add(TouchEntryPoint.saveGUI);
				TouchEntryPoint.appWidget.add(TouchEntryPoint.worksheetGUI);

				RootLayoutPanel.get().add(TouchEntryPoint.appWidget);

				app.start();

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
				// phoneGap.addHandler(new PhoneGapAvailableHandler()
				// {
				// @Override
				// public void onPhoneGapAvailable(PhoneGapAvailableEvent event)
				// {
				// // TODO Auto-generated method stub
				// System.out.println("test onPhoneGapAvailable");
				// }
				// });
				//
				// phoneGap.addHandler(new PhoneGapTimeoutHandler()
				// {
				//
				// @Override
				// public void onPhoneGapTimeout(PhoneGapTimeoutEvent event)
				// {
				// // TODO Auto-generated method stub
				//
				// }
				// });
				phoneGap.initializePhoneGap();
				phoneGap.getEvent().getBackButton().addBackButtonPressedHandler(new BackButtonPressedHandler()
				{

					@Override
					public void onBackButtonPressed(BackButtonPressedEvent event)
					{
						// is Dialog open? -> close dialog
						if (tabletGUI.getTouchModel().getGuiModel().isDialogShown())
						{
							tabletGUI.getTouchModel().getGuiModel().closeActiveDialog();
						}
						else
						{
							// else go to last view in history
							if (!appWidget.goBack())
							{
								// if history is empty -> close app
								phoneGap.exitApp();
							}
						}
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
		TouchEntryPoint.appWidget.showWidget(TouchEntryPoint.tabletGUI);
	}

	public static void showBrowseGUI()
	{
		TouchEntryPoint.appWidget.showWidget(TouchEntryPoint.browseGUI);
	}

	public static void showWorksheetGUI()
	{
		TouchEntryPoint.appWidget.showWidget(TouchEntryPoint.worksheetGUI);
	}

	public static void showSaveUI() {
		TouchEntryPoint.appWidget.showWidget(TouchEntryPoint.saveGUI);
	}

}