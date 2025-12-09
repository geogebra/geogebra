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

package org.geogebra.web.full.gui.view.algebra;

import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.safehtml.shared.SafeUri;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.TreeItem;

/**
 * Toggle button connected to tree item state
 *
 */
public class OpenButton extends SimplePanel {

    private SafeUri showUrl;
    private SafeUri hiddenUrl;
    private Image img;
    private String className;

    /**
	 * @param showUrl
	 *            image for open button
	 * @param hiddenUrl
	 *            image for close button
	 * @param ti
	 *            parent item
	 * @param className
	 *            CSS class
	 */
    public OpenButton(SafeUri showUrl, SafeUri hiddenUrl,
                      final TreeItem ti, String className) {
        this.showUrl = showUrl;
        this.hiddenUrl = hiddenUrl;
        this.className = className;

        addDomHandler(event -> {
            boolean open = ti.getState();
            ti.setState(!open);
            setChecked(!open);
        }, ClickEvent.getType());
        setChecked(true);
    }

    /**
     * set background-images via HTML
     *
     * @param url
     *            image url
     */
    public void setImage(String url) {
        if (img == null) {
            img = new Image(url);
            this.add(img);
        } else {
            img.setUrl(url);
        }
    }

    /**
     * @param value
     *            whether it's open
     */
    public void setChecked(boolean value) {
        if (value) {
            setImage(showUrl.asString());
            this.setStyleName("arrowBottom");
        }
        else {
            setImage(hiddenUrl.asString());
            this.setStyleName("arrowLeft");
        }
        this.getElement().addClassName(className);
    }
}