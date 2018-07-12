package org.geogebra.cloud;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.junit.Assert;

public class TestMaterialCallback implements MaterialCallbackI {
	private final ArrayList<String> titles = new ArrayList<>();
	private final ArrayList<String> errors = new ArrayList<>();
	private int expectedCount = 1;

	@Override
	public final void onLoaded(List<Material> result, ArrayList<Chapter> meta) {
		for (int i = 0; i < result.size(); i++) {
			String title = result.get(i).getTitle();
			if (handleMaterial(result.get(i))) {
				titles.add(title);
			}
		}
	}

	protected boolean handleMaterial(Material material) {
		return true;
		// overridden in subclasses
	}

	@Override
	public final void onError(Throwable exception) {
		errors.add("API error:" + exception.getMessage());
	}

	public void verify(String title) {
		Assert.assertEquals("", StringUtil.join(",", errors));
		Assert.assertEquals(title, StringUtil.join(",", titles));
	}

	public void await(int time) {
		for (int i = 0; i < time; i++) {
			if (titles.size() >= expectedCount || !errors.isEmpty()) {
				return;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Log.warn("cannot sleep");
			}
		}

	}

	public void setExpectedCount(int size) {
		expectedCount = size;
	}

}
