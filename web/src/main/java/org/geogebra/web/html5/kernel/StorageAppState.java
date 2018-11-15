package org.geogebra.web.html5.kernel;

import org.geogebra.common.kernel.AppState;
import org.geogebra.web.html5.util.UUIDW;

import com.google.gwt.storage.client.Storage;

/**
 * App State based on web storage
 */
public class StorageAppState implements AppState {

    private static final String TEMP_STORAGE_PREFIX = "GeoGebraUndoInfo"
            + UUIDW.randomUUID();
    /** state counter */
    static long nextKeyNum = 1;

    private Storage storage;
    private String key;

    /**
     * Construct an App State based on the storage and state xml.
     *
     * @param storage the web storage
     * @param xml current xml
     */
    public StorageAppState(Storage storage, String xml) {
        this.storage = storage;
		storage.setItem(key = TEMP_STORAGE_PREFIX + nextKeyNum, xml);
		increment();
    }

	private static void increment() {
		nextKeyNum++;
	}

	@Override
    public String getXml() {
        return storage.getItem(key);
    }

    @Override
    public void delete() {
        storage.removeItem(key);
    }

    @Override
    public boolean equalsTo(AppState state) {
        return state != null && getXml().equals(state.getXml());
    }
}
