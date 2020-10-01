package org.geogebra.web.full.gui.dialog.template;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.browser.MaterialCardController;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.openfileview.MaterialCardI;
import org.geogebra.web.full.gui.util.ContextMenuButtonCard;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class TemplatePreviewCard extends FlowPanel
        implements SetLabels, MaterialCardI {
    private MaterialCardController controller;
    private FlowPanel imgPanel;
    private AsyncOperation<TemplatePreviewCard> callback;

    /**
     * @param app       parent application
     */
    public TemplatePreviewCard(final AppW app, final Material material, boolean hasMoreButton,
                               final AsyncOperation<TemplatePreviewCard> callback) {
        controller = new MaterialCardController(app);
        controller.setMaterial(material);
        this.callback = callback;
        buildGUI(app, hasMoreButton);
        // select card on click
        this.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                runCallback();
            }
        }, ClickEvent.getType());
        // load material on double click
        this.addDomHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                app.getDialogManager().closeTemplateChooser();
                if (getController().getMaterial() == null) {
                    app.fileNew();
                } else {
                    getController().loadOnlineFile();
                }
            }
        }, DoubleClickEvent.getType());
    }

    public MaterialCardController getController() {
        return controller;
    }

    /**
     * run callback function
     */
    public void runCallback() {
        if (callback != null) {
            callback.callback(this);
        }
    }

    private void buildGUI(AppW app, boolean hasMoreButton) {
        this.addStyleName("templateCard");
        // panel containing the preview image of material
        imgPanel = new FlowPanel();
        imgPanel.setStyleName("cardImgPanel");
        setBackgroundImgPanel(getMaterial());
        this.add(imgPanel);
        // panel containing the info regarding the material
        FlowPanel infoPanel = new FlowPanel();
        infoPanel.setStyleName("cardInfoPanel");
        Label cardTitle = new Label(getMaterial() == null ? app.getLocalization().getMenu(
                "blankFile") : getMaterial().getTitle());
        cardTitle.setStyleName("cardTitle");
        infoPanel.add(cardTitle);
        if (hasMoreButton) {
            ContextMenuButtonCard moreBtn = new ContextMenuButtonTemplateCard(app, this);
            infoPanel.add(moreBtn);
        }
        this.add(infoPanel);
    }

    /**
     * @param selected true if template should be selected, false otherwise
     */
    public void setSelected(boolean selected) {
        Dom.toggleClass(this, "selected", selected);
    }

    /**
     * @return represented material
     */
    Material getMaterial() {
        return controller.getMaterial();
    }

    private void setBackgroundImgPanel(Material m) {
        final String thumb = m == null ? "" : m.getThumbnail();
        if (thumb != null && thumb.length() > 0) {
            imgPanel.getElement().getStyle().setBackgroundImage(
                    "url(" + Browser.normalizeURL(thumb) + ")");
        } else {
            imgPanel.getElement().getStyle().setBackgroundImage("url("
                    + AppResources.INSTANCE.empty().getSafeUri().asString()
                    + ")");
        }
    }

    @Override
    public void setLabels() {
        // do nothing here
    }

    @Override
    public void remove() {
        if (getParent().getElement().getChildCount() == 7) {
            // no border style if after remove we will have only 6 cards in the panel
            getParent().removeStyleName("withBorder");
        }
        removeFromParent();
    }

    @Override
    public void rename(String title) {
        // nothing to do here
    }

    @Override
    public void copy() {
        // nothing to do here
    }

    @Override
    public void onDelete() {
        // TODO handle delete here
    }

    @Override
    public String getCardTitle() {
        return null;
    }

    @Override
    public void setShare(String groupID, boolean share, AsyncOperation<Boolean> callback) {
        // nothing to do here
    }

    @Override
    public String getMaterialID() {
        return null;
    }

    @Override
    public void updateVisibility(String visibility) {
        // nothing to do here
    }
}
