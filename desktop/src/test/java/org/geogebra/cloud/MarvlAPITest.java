package org.geogebra.cloud;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.MarvlAPI;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.main.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.move.ggtapi.models.LoginOperationD;
import org.geogebra.desktop.util.UtilD;
import org.junit.Assert;
import org.junit.Test;

public class MarvlAPITest {

	@Test
	public void testAuth() {
		if (System.getProperty("marvl.auth.basic") == null) {
			return;
		}
		GeoGebraTubeUser usr = new GeoGebraTubeUser("");
		MarvlAPI api = authAPI();
		api.authorizeUser(usr,
				new LoginOperationD(new AppDNoGui(new LocalizationD(3), false)),
				true);
		pause(5000);
		Assert.assertEquals("GGBTest-Student", usr.getRealName());
	}

	private static void pause(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static MarvlAPI authAPI() {
		MarvlAPI ret = new MarvlAPI("http://notes.dlb-dev01.alp-dlg.net/api");
		ret.setBasicAuth(Base64.encodeToString(
				System.getProperty("marvl.auth.basic").getBytes(), false));
		return ret;
	}

	@Test
	public void testUpload() {
		if (System.getProperty("marvl.auth.basic") == null) {
			return;
		}
		MarvlAPI api = authAPI();
		final ArrayList<String> titles = new ArrayList<>();
		final ArrayList<String> errors = new ArrayList<>();
		api.uploadMaterial(0, "S", "Test material",
				Base64.encodeToString(UtilD.loadFileIntoByteArray(
						"src/test/resources/slides.ggs"), false),
				new MaterialCallbackI() {

					public void onLoaded(List<Material> result,
							ArrayList<Chapter> meta) {
						titles.add(result.get(0).getTitle());

					}

					public void onError(Throwable exception) {
						errors.add(exception.getMessage());

					}
				},
				MaterialType.ggs);
		pause(5000);
		Assert.assertEquals("", StringUtil.join(",", errors));
		Assert.assertEquals("Test material", StringUtil.join(",", titles));
	}

}
