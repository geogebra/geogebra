package org.geogebra.web.full.gui.view.algebra;

import org.gwtproject.safehtml.shared.SafeUri;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TreeItem;

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