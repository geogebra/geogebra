package geogebra.touch;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.Browser;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.js.ResourcesInjector;
import geogebra.touch.gui.BrowseGUIT;
import geogebra.touch.gui.GuiResources;
import geogebra.touch.gui.PhoneGUI;
import geogebra.touch.gui.TouchGUI;
import geogebra.touch.gui.WorksheetGUI;
import geogebra.touch.gui.laf.AndroidLAF;
import geogebra.touch.gui.laf.AppleLAF;
import geogebra.touch.gui.laf.DefaultLAF;
import geogebra.touch.gui.laf.LookAndFeel;
import geogebra.touch.gui.laf.PhoneLAF;
import geogebra.touch.gui.laf.WinLAF;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.gwtphonegap.client.PhoneGap;
import com.googlecode.gwtphonegap.client.event.BackButtonPressedEvent;
import com.googlecode.gwtphonegap.client.event.BackButtonPressedHandler;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TouchEntryPoint implements EntryPoint {
	static TabletDeckLayoutPanel appWidget;
	static TouchGUI touchGUI;
	private static BrowseGUIT browseGUI;
	private static WorksheetGUI worksheetGUI;
	static PhoneGap phoneGap = (PhoneGap) GWT.create(PhoneGap.class);
	static TouchApp app;
	private static LookAndFeel laf;
	static boolean isTablet;

	public static void allowEditing(final boolean b) {
		touchGUI.allowEditing(b);
	}

	public static LookAndFeel getLookAndFeel() {
		return TouchEntryPoint.laf;
	}

	public static void goBack() {
		// is Dialog open? -> close dialog
		if (touchGUI.getTouchModel().getGuiModel().isDialogShown()) {
			touchGUI.getTouchModel().getGuiModel().closeActiveDialog();
		} else {
			// else go to last view in history
			if (!appWidget.goBack()) {
				// if history is empty -> close app
				phoneGap.exitApp();
			}
		}

		laf.updateUndoSaveButtons();
		touchGUI.updateViewSizes();
	}

	static void loadMobileAsync() {
		GWT.runAsync(new RunAsyncCallback() {
			private void initPhoneGap() {
				phoneGap.initializePhoneGap();
				phoneGap.getEvent()
						.getBackButton()
						.addBackButtonPressedHandler(
								new BackButtonPressedHandler() {

									@Override
									public void onBackButtonPressed(
											final BackButtonPressedEvent event) {
										goBack();
									}
								});
			}

			@Override
			public void onFailure(final Throwable reason) {
				// App.debug(reason);
				reason.printStackTrace();
			}

			@Override
			public void onSuccess() {
				
//				if(Window.getClientWidth() < 500) {
//					System.out.println("PHONE GUI");
					touchGUI = new PhoneGUI();
					isTablet = false;
//				} else {
//					System.out.println("TABLET GUI");
//					touchGUI = new TabletGUI();
//					isTablet = true;
//				}
				app = new TouchApp(TouchEntryPoint.touchGUI);
				
				Browser.checkFloat64();
				ResourcesInjector.injectResources();
				setLookAndFeel();
				final FileManagerT fm = new FileManagerT();
				appWidget = new TabletDeckLayoutPanel(app);
				app.setFileManager(fm);
				app.registerSavedStateListener(TouchEntryPoint.getLookAndFeel());

				TouchEntryPoint.appWidget.add(TouchEntryPoint.touchGUI);

				RootLayoutPanel.get().add(TouchEntryPoint.appWidget);

				app.start();

				TouchEntryPoint.showTabletGUI();

//				tabletGUI.getContentWidget().getElement().getStyle()
//						.setOverflow(Overflow.VISIBLE);
				app.getScriptManager().ggbOnInit();
				// needed for testing
				if (RootPanel.getBodyElement()
						.getAttribute("data-param-ggbbase64").length() > 0) {
					app.getGgbApi().setBase64(
							RootPanel.getBodyElement().getAttribute(
									"data-param-ggbbase64"));
				}

				this.initPhoneGap();
				Window.enableScrolling(false);
				ToolTipManagerW.setEnabled(false);
			}
		});
	}

	public native static void abc(String s) /*-{
	console.log(s);
}-*/;
	
	static void setLookAndFeel() {
		final String param = RootPanel.getBodyElement().getAttribute(
				"data-param-laf");

		if (!isTablet()) {
			laf = new PhoneLAF(app);
		} else if ("android".equals(param)) {
			laf = new AndroidLAF(TouchEntryPoint.app);
		} else if ("apple".equals(param)) {
			laf = new AppleLAF(TouchEntryPoint.app);
		} else if ("win".equals(param)) {
			laf = new WinLAF(TouchEntryPoint.app);
		} else {
			laf = new DefaultLAF(TouchEntryPoint.app);
		}
	}

	public static void showBrowseGUI() {
		TouchEntryPoint.appWidget.showWidget(TouchEntryPoint.getBrowseGUI());
		TouchEntryPoint.getBrowseGUI().loadFeatured();
		TouchEntryPoint.appWidget.forceLayout();
	}

	public static void showTabletGUI() {
		TouchEntryPoint.appWidget.clearHistory();
		TouchEntryPoint.appWidget.showWidget(TouchEntryPoint.touchGUI);
		touchGUI.updateViewSizes();
		laf.updateUndoSaveButtons();
	}

	public static void showWorksheetGUI(final Material material) {
		System.out.println("show worksheet gui");
		TouchEntryPoint.appWidget.showWidget(TouchEntryPoint.getWorksheetGUI());
		TouchEntryPoint.getWorksheetGUI().loadWorksheet(material);
	}

	@Override
	public void onModuleLoad() {
		AnimationScheduler.get().requestAnimationFrame(new AnimationCallback() {

			@Override
			public void execute(final double timestamp) {
				loadMobileAsync();

				// insert mathquill css
				final String mathquillcss = GuiResources.INSTANCE.mathquillCss().getText();
				StyleInjector.inject(mathquillcss);
			}
		});
	}

	public static WorksheetGUI getWorksheetGUI() {
		if (!hasWorksheetGUI()) {
			worksheetGUI = new WorksheetGUI(app);
			TouchEntryPoint.appWidget.add(TouchEntryPoint.worksheetGUI);
		}
		return worksheetGUI;
	}

	public static BrowseGUIT getBrowseGUI() {
		if (!hasBrowseGUI()) {
			browseGUI = new BrowseGUIT(app);
			TouchEntryPoint.appWidget.add(TouchEntryPoint.browseGUI);
		}
		return browseGUI;
	}

	static boolean hasWorksheetGUI() {
		return TouchEntryPoint.worksheetGUI != null;
	}

	static boolean hasBrowseGUI() {
		return TouchEntryPoint.browseGUI != null;
	}

	public static PhoneGap getPhoneGap() {
		return TouchEntryPoint.phoneGap;
	}

	public static TouchGUI getTouchGUI() {
		return touchGUI;
	}
	
	public static boolean isTablet() {
		return isTablet;
	}
}