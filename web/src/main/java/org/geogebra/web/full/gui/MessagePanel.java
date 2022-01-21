package org.geogebra.web.full.gui;

import org.geogebra.web.html5.gui.util.NoDragImage;
import org.gwtproject.resources.client.ResourcePrototype;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Panel for displaying a message to the user with a title and an image.
 */
public class MessagePanel extends FlowPanel {

    private static final String CAPTION_STYLE_NAME = "caption";
    private static final String MESSAGE_STYLE_NAME = "info";
    private static final String PANEL_STYLE_NAME = "messagePanel";
    private static final int IMAGE_SIZE = 112;

    private NoDragImage infoImage;
    private Label titleLabel;
    private Label messageLabel;

    /**
     * Create a new information panel
     */
    public MessagePanel() {
        createPanel();
        setStyleName(PANEL_STYLE_NAME);
    }

    private void createPanel() {
        infoImage = new NoDragImage("");
        infoImage.setWidth(IMAGE_SIZE);
        infoImage.setHeight(IMAGE_SIZE);
        add(infoImage);

        titleLabel = new Label();
        titleLabel.setStyleName(CAPTION_STYLE_NAME);
        add(titleLabel);

        messageLabel = new Label();
        messageLabel.setStyleName(MESSAGE_STYLE_NAME);
        add(messageLabel);
    }

    /**
     * Set the image of the panel. The image is displayed
     * in a 112x112 pixels format.
     *
     * @param uri uri to the image.
     */
    public void setImageUri(ResourcePrototype uri) {
        infoImage.setResource(uri);
    }

    /**
     * Set the title of the panel.
     *
     * @param title title of the panel
     */
    public void setPanelTitle(String title) {
        titleLabel.setText(title);
    }

    /**
     * Set the message of the panel.
     *
     * @param message message of the panel
     */
    public void setPanelMessage(String message) {
        messageLabel.setText(message);
    }
}
