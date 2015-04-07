package org.geogebra.web.web.move.ggtapi.models;

import java.util.ArrayList;

import org.geogebra.common.move.ggtapi.models.SyncEvent;

public interface SyncCallback {
	public void onSync(ArrayList<SyncEvent> events);
}
