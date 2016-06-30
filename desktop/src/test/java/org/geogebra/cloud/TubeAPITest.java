package org.geogebra.cloud;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.desktop.move.ggtapi.models.GeoGebraTubeAPID;
import org.junit.Assert;
import org.junit.Test;

public class TubeAPITest extends Assert {
	@Test
	public void testSearch() {
		ClientInfo client = new ClientInfo();
		// client.setModel((AuthenticationModel) this.model);
		client.setType("desktop");
		client.setId("APITEST");
		client.setWidth(1024);
		client.setWidth(768);
		client.setLanguage("en");
		GeoGebraTubeAPID api = new GeoGebraTubeAPID(true, client);
		final ArrayList<String> titles = new ArrayList<String>();
		api.search("pythagoras", new MaterialCallbackI() {

			public void onLoaded(List<Material> result,
					ArrayList<Chapter> meta) {
				for (Material m : result) {
					titles.add(m.getTitle());
				}

			}

			public void onError(Throwable exception) {
				exception.printStackTrace();

			}
		});
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("Wrong number of search results", titles.size(), 30);
	}

}
