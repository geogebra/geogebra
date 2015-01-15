package geogebra.web.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.SyncEvent;

import java.util.ArrayList;

public interface SyncCallback {
	public void onSync(ArrayList<SyncEvent> events);
}
