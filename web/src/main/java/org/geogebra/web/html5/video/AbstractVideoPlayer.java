package org.geogebra.web.html5.video;

import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.main.App;

import com.google.gwt.user.client.ui.IsWidget;

public abstract class AbstractVideoPlayer implements IsWidget {
    /**
     * The application
     */
    protected App app;

    /**
     * Video geo to play
     */
    protected GeoVideo video;
    private String playerId;

    AbstractVideoPlayer(GeoVideo video, int id) {
        this.video = video;
        app = video.getKernel().getApplication();
        playerId = "video_player" + id;
    }

    /**
     * Selects player showing its bounding box.
     */
    void selectPlayer() {
        EuclidianView view = app.getActiveEuclidianView();
        Drawable d = ((Drawable) view.getDrawableFor(video));
        d.update();
        if (d.getBoundingBox().getRectangle() != null) {
            view.setBoundingBox(d.getBoundingBox());
            view.repaintView();
            app.getSelectionManager().addSelectedGeo(video);
        }
    }

    /**
     * @return the associated GeoVideo object.
     */
    public GeoVideo getVideo() {
        return video;
    }

    protected void stylePlayer() {
        asWidget().addStyleName("mowVideo");
        asWidget().addStyleName("mowWidget");
        asWidget().getElement().setId(playerId);
    }

    abstract void update();

    abstract void onReady();

    /**
     * @return if the player is valid.
     */
    abstract boolean isValid();

    /**
     * Play the video.
     */
    abstract void play();

    /**
     * Pause the video.
     */
    abstract void pause();

    /**
     * @param video2 other video
     * @return whether the player is compatible with the oter video
     */
    abstract boolean matches(GeoVideo video2);

    /**
     * Sends the player background.
     */
    public void sendBackground() {
        video.setBackground(true);
        update();
    }

    /**
     * @return if player is offline.
     */
    abstract boolean isOffline();
}
