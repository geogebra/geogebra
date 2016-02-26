package org.geogebra.common.move.ggtapi.requests;

import java.util.ArrayList;

import org.geogebra.common.move.ggtapi.models.SyncEvent;

public interface SyncCallback {
	public void onSync(ArrayList<SyncEvent> events);
}
