package geogebra.touch;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.Browser;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.js.ResourcesInjector;
import geogebra.touch.gui.BrowseGUI;
import geogebra.touch.gui.GuiResources;
import geogebra.touch.gui.TabletGUI;
import geogebra.touch.gui.WorksheetGUI;
import geogebra.touch.gui.laf.AndroidLAF;
import geogebra.touch.gui.laf.AppleLAF;
import geogebra.touch.gui.laf.DefaultLAF;
import geogebra.touch.gui.laf.LookAndFeel;
import geogebra.touch.gui.laf.WinLAF;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Overflow;
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
	static TabletGUI tabletGUI = new TabletGUI();
	private static BrowseGUI browseGUI;
	private static WorksheetGUI worksheetGUI;
	static PhoneGap phoneGap = (PhoneGap) GWT.create(PhoneGap.class);
	static TouchApp app = new TouchApp(TouchEntryPoint.tabletGUI);
	private static LookAndFeel laf;

	public static void allowEditing(final boolean b) {
		tabletGUI.allowEditing(b);
	}

	public static LookAndFeel getLookAndFeel() {
		return TouchEntryPoint.laf;
	}

	public static void goBack() {
		// is Dialog open? -> close dialog
		if (tabletGUI.getTouchModel().getGuiModel().isDialogShown()) {
			tabletGUI.getTouchModel().getGuiModel().closeActiveDialog();
		} else {
			// else go to last view in history
			if (!appWidget.goBack()) {
				// if history is empty -> close app
				phoneGap.exitApp();
			}
		}

		laf.updateUndoSaveButtons();
		tabletGUI.updateViewSizes();
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
				Browser.checkFloat64();
				ResourcesInjector.injectResources();
				setLookAndFeel();

				final FileManagerT fm = new FileManagerT();
				appWidget = new TabletDeckLayoutPanel(app);
				app.setFileManager(fm);
				app.registerSavedStateListener(TouchEntryPoint.getLookAndFeel());

				TouchEntryPoint.appWidget.add(TouchEntryPoint.tabletGUI);

				RootLayoutPanel.get().add(TouchEntryPoint.appWidget);

				app.start();

				TouchEntryPoint.showTabletGUI();

				tabletGUI.getContentWidget().getElement().getStyle()
						.setOverflow(Overflow.VISIBLE);
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

	static void reloadLocalFiles(String changedName) {
		TouchEntryPoint.getBrowseGUI().reloadLocalFiles(changedName);
	}

	static void setLookAndFeel() {
		final String param = RootPanel.getBodyElement().getAttribute(
				"data-param-laf");

		if ("android".equals(param)) {
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
		TouchEntryPoint.appWidget.showWidget(TouchEntryPoint.tabletGUI);
		tabletGUI.updateViewSizes();
		laf.updateUndoSaveButtons();
	}

	public static void showWorksheetGUI(final Material material) {
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
				final String mathquillcss = GuiResources.INSTANCE
						.mathquillCss().getText();
				StyleInjector.inject(mathquillcss);
			}
		});
	}

	public static WorksheetGUI getWorksheetGUI() {
		if (worksheetGUI == null) {
			worksheetGUI = new WorksheetGUI(app);
			TouchEntryPoint.appWidget.add(TouchEntryPoint.worksheetGUI);
		}
		return worksheetGUI;
	}

	public static BrowseGUI getBrowseGUI() {
		if (browseGUI == null) {
			browseGUI = new BrowseGUI(app);
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
}