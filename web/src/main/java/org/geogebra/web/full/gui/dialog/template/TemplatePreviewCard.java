package org.geogebra.web.full.gui.dialog.template;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.gui.browser.MaterialCardController;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.openfileview.MaterialCardI;
import org.geogebra.web.full.gui.util.ContextMenuButtonCard;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.DoubleClickEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class TemplatePreviewCard extends FlowPanel
        implements SetLabels, MaterialCardI {
    private final MaterialCardController controller;
    private FlowPanel imgPanel;
    private final AsyncOperation<TemplatePreviewCard> callback;

    /**
     * @param app - application
     * @param material - current material
     * @param hasMoreButton - context menu button
     * @param callback - callback
     */
    public TemplatePreviewCard(final AppW app, final Material material, boolean hasMoreButton,
                               final AsyncOperation<TemplatePreviewCard> callback) {
        controller = new MaterialCardController(app);
        controller.setMaterial(material);
        this.callback = callback;
        buildGUI(app, hasMoreButton);
        // select card on click
        this.addDomHandler(event -> runCallback(), ClickEvent.getType());
        // load material on double click
        this.addDomHandler(event -> {
            app.getDialogManager().closeTemplateChooser();
            if (getController().getMaterial() == null) {
                app.fileNew();
            } else {
                getController().loadOnlineFile();
            }
        }, DoubleClickEvent.getType());
    }

    @Override
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
        addStyleName("mowPreviewCard");
        if (!(NavigatorUtil.isMobile())) {
            addStyleName("desktop");
        }
        // panel containing the preview image of material
        imgPanel = new FlowPanel();
        imgPanel.setStyleName("cardImagePanel");
        setBackgroundImgPanel(getMaterial());
        this.add(imgPanel);
        // panel containing the info regarding the material
        FlowPanel infoPanel = new FlowPanel();
        infoPanel.setStyleName("cardInfoPanel mowTitlePanel");
        String text = getMaterial() == null ? app.getLocalization().getMenu(
                "blankFile") : getMaterial().getTitle();
        Label cardTitle = BaseWidgetFactory.INSTANCE.newPrimaryText(text, "cardTitle");
        infoPanel.add(cardTitle);
        if (hasMoreButton) {
            ContextMenuButtonCard moreBtn = new ContextMenuButtonDeleteCard(app, this);
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
        if (thumb != null && !thumb.isEmpty()) {
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
    public void onDelete() {
        controller.onConfirmDelete(this);
    }

    @Override
    public String getCardTitle() {
        return getMaterial().getTitle();
    }
}
