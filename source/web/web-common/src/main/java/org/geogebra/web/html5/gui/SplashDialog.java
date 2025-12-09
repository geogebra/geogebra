/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.html5.gui;

import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.laf.LoadSpinner;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Position;
import org.gwtproject.timer.client.Timer;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.SimplePanel;

import jsinterop.base.Js;

public class SplashDialog extends SimplePanel {
	public static final int SPLASH_DIALOG_DELAY = 1000;
	boolean appLoaded = false;
	boolean timerElapsed = false;
	boolean previewExists = false;
	private GeoGebraElement geoGebraElement;

	private Timer t = new Timer() {
		@Override
		public void run() {
			if (appLoaded) {
				hide();
			}
			timerElapsed = true;
		}
	};

	private final GeoGebraFrameW geogebraFrame;

	/**
	 * @param showLogo
	 *            whether to show GeoGebra logo
	 * @param geoGebraElement
	 *            configuration element
	 * @param frame
	 *            frame
	 */
	public SplashDialog(boolean showLogo, GeoGebraElement geoGebraElement,
			AppletParameters parameters, GeoGebraFrameW frame) {
		this.geoGebraElement = geoGebraElement;
		this.geogebraFrame = frame;
		previewExists = checkIfPreviewExists(geoGebraElement.getElement())
				|| AppConfigDefault
						.isUnbundledOrNotes(parameters.getDataParamAppName());

		if (!previewExists) {
			FlowPanel panel = new FlowPanel();
			Style style = panel.getElement().getStyle();
			style.setPosition(Position.ABSOLUTE);
			style.setZIndex(1000000);
			style.setBackgroundColor("white");
			if (showLogo) {
				NoDragImage logo = new NoDragImage(GuiResourcesSimple.INSTANCE
						.ggb_logo_name(), 427 , 120);
				panel.add(logo);
			}
			LoadSpinner spinner = new LoadSpinner();
			spinner.addStyleName("spinnerForLogo");
			panel.add(spinner);
			add(panel);
		}
		Timer afterConstructor = new Timer() {

			@Override
			public void run() {
				triggerImageLoaded();
			}
		};
		afterConstructor.schedule(0);

		t.schedule(SPLASH_DIALOG_DELAY);
	}

	private void triggerImageLoaded() {
		geogebraFrame.runAsyncAfterSplash();
	}

	private boolean checkIfPreviewExists(Element thisArticle) {
		if (thisArticle != null && Dom.querySelectorForElement(thisArticle,
				".ggb_preview") != null) {
			return true;
		}
		return thisArticle != null
				&& thisArticle.getParentElement() != null
				&& Dom.querySelectorForElement(thisArticle.getParentElement(),
				".ggb_preview") != null;
	}

	/**
	 * Hide the splash popup.
	 */
	protected void hide() {
		this.removeFromParent();
		removePreviewImg(geoGebraElement.getElement());
	}

	private void removePreviewImg(Element thisArticle) {
		if (Js.isTruthy(thisArticle)) {
			Element img = Dom.querySelectorForElement(thisArticle, ".ggb_preview");
			if (Js.isTruthy(img)) {
				img.getParentNode().removeChild(img);
			}
		}
	}

	/**
	 * Notify about app load; hide popup if it was shown logng enough.
	 */
	public void canNowHide() {
		appLoaded = true;
		if (timerElapsed) {
			hide();
		}
	}

	/**
	 * @return whether preview is present
	 */
	public boolean isPreviewExists() {
		return previewExists;
	}

}
