package org.geogebra.web.full.main.video;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.draw.DrawVideo;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.main.App;
import org.geogebra.common.media.MediaURLParser;
import org.geogebra.common.media.VideoManager;
import org.geogebra.common.media.VideoURL;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.main.MyImageW;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.dom.client.Element;

import elemental2.dom.HTMLImageElement;

/**
 * Class for managing audio content.
 *
 * @author laszlo
 *
 */
public class VideoManagerW implements VideoManager {

	private final static int DEFAULT_WIDTH = 420;
	private final static int DEFAULT_HEIGHT = 365;

	private AppWFull app;

	private Map<DrawVideo, AbstractVideoPlayer> players = new HashMap<>();
	private ArrayList<AbstractVideoPlayer> cache = new ArrayList<>();

	public VideoManagerW(AppWFull app) {
		this.app = app;
	}

	@Override
	public void loadGeoVideo(DrawVideo geo) {
		addPlayer(geo);
		updatePlayer(geo);
	}

	@Override
	public void play(DrawVideo video) {
		if (video == null) {
			return;
		}

		playerOf(video).setBackground(false);
		app.getMaskWidgets().masksToForeground();
	}

	@Override
	public void createPreview(final GeoVideo geo) {
		HTMLImageElement img = Dom.createImage();
		img.setAttribute("crossorigin", "anonymous");
		img.src = geo.getPreviewUrl();
		img.addEventListener("load", (event) -> {
				final MyImage prev = new MyImageW(img, false);
				geo.setPreview(prev);
				app.getActiveEuclidianView().updateAllDrawablesForView(true);
		});

		img.addEventListener("error", (event) -> {
				img.src = GuiResourcesSimple.INSTANCE.mow_video_player()
						.getSafeUri().asString();
				app.getActiveEuclidianView().updateAllDrawablesForView(true);
		});
	}

	@Override
	public void addPlayer(final DrawVideo video) {
		// use int instead of iterator to prevent concurrent access
		for (int i = 0; i < cache.size(); i++) {
			AbstractVideoPlayer other = cache.get(i);
			if (other.matches(video.getVideo())) {
				players.put(video, other);
				other.video = video;
				other.asWidget().setVisible(true);
				cache.remove(other);
				return;
			}
		}

		final AbstractVideoPlayer player = !isOnline()
				? createPlayerOffline(video, players.size())
				: createPlayer(video, players.size()) ;

		addPlayerToFrame(video, player);
	}

	private void addPlayerToFrame(DrawVideo video, AbstractVideoPlayer player) {
		if (player == null) {
			return;
		}

		players.put(video, player);

		DockPanelW panel = app.getGuiManager().getLayout().getDockManager()
				.getPanel(App.VIEW_EUCLIDIAN);
		((EuclidianDockPanelW) panel).getEuclidianPanel().add(player);
	}

	private AbstractVideoPlayer createPlayerOffline(DrawVideo video, int id) {
		return new VideoOffline(video, id);
	}

	private AbstractVideoPlayer createPlayer(DrawVideo video, int id) {
		switch (video.getVideo().getFormat()) {
		case VIDEO_YOUTUBE:
			return new YouTubePlayer(video, id);
		case VIDEO_HTML5:
			return new HTML5Player(video, id);
		case VIDEO_MEBIS:
			return new MebisPlayer(video, id);
		case AUDIO_HTML5:
		case NONE:
		default:
			return null;
		}
	}

	@Override
	public void removePlayer(final DrawVideo video) {
		if (!hasPlayer(video)) {
			return;
		}
		playerOf(video).asWidget().removeFromParent();
		players.remove(video);
	}

	@Override
	public boolean hasPlayer(DrawVideo video) {
		return players.containsKey(video);
	}

	private AbstractVideoPlayer playerOf(DrawVideo video) {
		return players.get(video);
	}

	@Override
	public void updatePlayer(DrawVideo video) {
		if (!hasPlayer(video)) {
			loadGeoVideo(video);
		} else {
			playerOf(video).update();
		}
	}

	@Override
	public void removePlayers() {
		for (AbstractVideoPlayer player : players.values()) {
			player.asWidget().removeFromParent();
		}
		for (AbstractVideoPlayer player : cache) {
			player.asWidget().removeFromParent();
		}

		players.clear();
		cache.clear();
		app.getActiveEuclidianView().getEuclidianController().clearSelectionAndRectangle();
	}

	@Override
	public void storeVideos() {
		for (AbstractVideoPlayer player : players.values()) {
			player.asWidget().setVisible(false);
			cache.add(player);
		}
		players.clear();
	}

	public boolean isOnline() {
		return app.getNetworkOperation().isOnline();
	}

	@Override
	public void backgroundAll() {
		for (AbstractVideoPlayer player : players.values()) {
			player.setBackground(true);
		}
	}

	@Override
	public GeoVideo createVideo(Construction c, VideoURL videoURL) {
		return new GeoVideo(c, videoURL.getUrl(), videoURL.getFormat());
	}

	@Override
	public String getYouTubeId(String url) {
		return MediaURLParser.getYouTubeId(url);
	}

	@Override
	public String getMebisId(String url) {
		return MediaURLParser.getMebisId(url);
	}

	@Override
	public void onError(DrawVideo video) {
		setDefaultSize(video);
		removePlayer(video);
		AbstractVideoPlayer offlinePlayer = createPlayerOffline(video, players.size() + 1);
		addPlayerToFrame(video, offlinePlayer);
	}

	private void setDefaultSize(DrawVideo video) {
		if (video.getVideo().hasSize()) {
			return;
		}
		video.getVideo().setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	@Override
	public boolean isPlayerOffline(DrawVideo video) {
		return playerOf(video).isOffline();
	}

	public Element getElement(DrawVideo drawable) {
		return playerOf(drawable).asWidget().getElement();
	}
}
