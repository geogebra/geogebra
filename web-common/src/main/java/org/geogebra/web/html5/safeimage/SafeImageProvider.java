package org.geogebra.web.html5.safeimage;

import org.geogebra.web.html5.util.ArchiveEntry;

public interface SafeImageProvider {
	void onReady(ArchiveEntry imageFile);
}
