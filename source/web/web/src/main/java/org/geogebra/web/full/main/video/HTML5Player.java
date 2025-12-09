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

package org.geogebra.web.full.main.video;

import org.geogebra.common.euclidian.draw.DrawVideo;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.web.full.main.video.HTML5VideoWidget.VideoListener;
import org.geogebra.web.html5.util.PersistablePanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

/**
 * Frame based HTML5 player with video tag.
 *
 * @author laszlo
 *
 */
public class HTML5Player extends VideoPlayer implements VideoListener {
	private HTML5VideoWidget v;
	private PersistablePanel main;

	/**
	 * Constructor
	 *
	 * @param video
	 *            the video object.
	 * @param id
	 *            The id of the player.
	 */
	public HTML5Player(DrawVideo video, int id) {
		super(video, id);
	}

	@Override
	protected void createGUI() {
		main = new PersistablePanel();
		v = new HTML5VideoWidget(getVideo().getSrc(), this);
		main.add(v);
	}

	@Override
	public void update() {
		super.update();
		v.setControls(!video.isBackground());
	}

	@Override
	public Widget asWidget() {
		return main;
	}

	@Override
	public void onLoad(int width, int height) {
		if (!getVideo().hasSize()) {
			getVideo().setSize(width, height);
		}
		getVideo().update();
		update();
	}

	@Override
	public void onError() {
		app.getVideoManager().onError(video);
	}

	/**
	 * @return the error widget needs to be displayed.
	 */
	protected Widget getErrorWidget() {
		return new Label();
	}

	@Override
	public boolean matches(GeoVideo video2) {
		return getVideo().getSrc().equals(video2.getSrc());
	}
}
