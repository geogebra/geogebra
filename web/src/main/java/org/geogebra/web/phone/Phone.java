package org.geogebra.web.phone;

import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.phone.gui.GeoGebraAppFrameP;
import org.geogebra.web.phone.gui.PhoneUI;
import org.geogebra.web.phone.gui.view.AbstractView;
import org.geogebra.web.phone.gui.view.algebra.AlgebraView;
import org.geogebra.web.phone.gui.view.euclidian.EuclidianView;
import org.geogebra.web.phone.gui.view.menu.MenuView;
import org.geogebra.web.touch.PhoneGapManager;
import org.geogebra.web.web.gui.app.GeoGebraAppFrame;
import org.geogebra.web.web.gui.applet.AppletFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.googlecode.gwtphonegap.client.event.BackButtonPressedEvent;
import com.googlecode.gwtphonegap.client.event.BackButtonPressedHandler;

/**
 * @author geogebra
 *
 */
public class Phone implements EntryPoint {

	private static PhoneUI phoneGui;
	private static AbstractView euclidianView;
	private static AbstractView browseView;
	static GeoGebraAppFrame appFrame;

	public void onModuleLoad() {
		appFrame = new GeoGebraAppFrameP(new PhoneLookAndFeel(),
		        new PhoneDevice(),
		        (AppletFactory) GWT.create(AppletFactory.class));
		PhoneGapManager.initializePhoneGap(null);
		PhoneGapManager.getPhoneGap().getEvent().getBackButton()
		        .addBackButtonPressedHandler(new BackButtonPressedHandler() {

			        @Override
			        public void onBackButtonPressed(
			                final BackButtonPressedEvent event) {
				        goBack();
			        }
		        });
		ResourcesInjector.injectResources();
	}

	private static void addViews() {
		browseView = (AbstractView) appFrame.app.getGuiManager()
				.getBrowseView();
		phoneGui.addView(new AlgebraView(appFrame.app));
		phoneGui.addView(euclidianView = new EuclidianView(appFrame.app));
		phoneGui.addView(browseView);
		phoneGui.addView(new MenuView(appFrame.app));
		phoneGui.showView(browseView);
	}

	public static void showEuclidianView() {
		phoneGui.showView(euclidianView);
	}

	public static void showBrowseView() {
		phoneGui.showView(browseView);
	}

	public static PhoneUI getGUI() {
		return phoneGui;
	}

	public static void goBack() {
		// TODO implement
	}

	public static void initGUI() {
		phoneGui = new PhoneUI(appFrame.app);
		addViews();
		RootLayoutPanel.get().clear();
		RootLayoutPanel.get().add(phoneGui);
	}
}