package org.geogebra.cloud;

import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.MarvlAPI;
import org.geogebra.desktop.main.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.move.ggtapi.models.LoginOperationD;
import org.junit.Assert;
import org.junit.Test;

import com.sun.org.apache.xml.internal.security.utils.Base64;

public class MarvlAPITest {

	@Test
	public void testAuth() {
		if (System.getProperty("marvl.auth.basic") == null) {
			return;
		}
		GeoGebraTubeUser usr = new GeoGebraTubeUser("");
		MarvlAPI api = new MarvlAPI("http://notes.dlb-dev01.alp-dlg.net/api");
		api.setBasicAuth(
				Base64.encode(
						System.getProperty("marvl.auth.basic").getBytes()));
		api.authorizeUser(usr,
				new LoginOperationD(new AppDNoGui(new LocalizationD(3), false)),
				true);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals("GGBTest-Student", usr.getRealName());
	}
}
