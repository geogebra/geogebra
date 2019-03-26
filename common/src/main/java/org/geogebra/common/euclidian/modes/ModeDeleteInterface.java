package org.geogebra.common.euclidian.modes;

import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.euclidian.event.PointerEventType;

public interface ModeDeleteInterface {

	void handleMouseDraggedForDelete(AbstractEvent event, int deleteToolSize,
			boolean b);

	void mousePressed(PointerEventType type);

	boolean process(Hits topHits, boolean isControlDown,
			boolean selectionPreview);

}
