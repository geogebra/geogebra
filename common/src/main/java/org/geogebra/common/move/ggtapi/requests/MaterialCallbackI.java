package org.geogebra.common.move.ggtapi.requests;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;

public interface MaterialCallbackI {

	void onLoaded(List<Material> result, ArrayList<Chapter> meta);

	void onError(Throwable exception);

}
