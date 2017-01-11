package org.geogebra.web.phone;

import org.geogebra.web.html5.js.ResourcesInjector;
import org.geogebra.web.phone.gui.GeoGebraAppFrameP;
import org.geogebra.web.phone.gui.PhoneUI;
import org.geogebra.web.phone.gui.view.AbstractView;
import org.geogebra.web.phone.gui.view.algebra.AlgebraPhoneView;
import org.geogebra.web.phone.gui.view.euclidian.EuclidianPhoneView;
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

	private PhoneUI phoneGui;
	private AbstractView euclidianView;
	private AbstractView browseView;
	GeoGebraAppFrame appFrame;

	@Override
	public void onModuleLoad() {
		appFrame = new GeoGebraAppFrameP(new PhoneLookAndFeel(),
				new PhoneDevice(this),
				(AppletFactory) GWT.create(AppletFactory.class), this);
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

	private void addViews() {
		browseView = (AbstractView) appFrame.app.getGuiManager()
				.getBrowseView();
		phoneGui.addView(new AlgebraPhoneView(appFrame.app));
		phoneGui.addView(euclidianView = new EuclidianPhoneView(appFrame.app));
		phoneGui.addView(browseView);
		phoneGui.addView(new MenuView(appFrame.app));
		phoneGui.showView(browseView);
	}

	public void showEuclidianView() {
		phoneGui.showView(euclidianView);
	}

	public void showBrowseView() {
		phoneGui.showView(browseView);
	}

	public PhoneUI getGUI() {
		return phoneGui;
	}

	public static void goBack() {
		// TODO implement
	}

	public void initGUI() {
		phoneGui = new PhoneUI(appFrame.app);
		addViews();
		RootLayoutPanel.get().clear();
		RootLayoutPanel.get().add(phoneGui);
	}
}