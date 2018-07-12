package org.geogebra.cloud;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.AuthenticationModel;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.MarvlAPI;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.MaterialRequest.Order;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.desktop.main.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.move.ggtapi.models.AuthenticationModelD;
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
		doUpload(api, "Test material", new TestMaterialCallback());
	}

	private static void doUpload(MarvlAPI api, String title,
			TestMaterialCallback testCallback) {
		api.uploadMaterial("", "S", title,
				Base64.encodeToString(UtilD.loadFileIntoByteArray(
						"src/test/resources/slides.ggs"), false),
				testCallback,
				MaterialType.ggs);
		testCallback.await(5);
		testCallback.verify(title);
	}

	@Test
	public void testOpen(){
		if (System.getProperty("marvl.auth.basic") == null) {
			return;
		}
		final MarvlAPI api = authAPI();
		final AppDNoGui appd = new AppDNoGui(new LocalizationD(3), false);
		api.setClient(getClient(appd));
		deleteAll(api);

		final String title = "OpenTest" + System.currentTimeMillis();
		doUpload(api, title, new TestMaterialCallback());
		TestMaterialCallback getCallback = new TestMaterialCallback() {

			@Override
			public boolean handleMaterial(Material mat) {
				if (title.equals(mat.getTitle())) {
					try {
						appd.getGgbApi().openFile(mat.getFileName());
						return true;
					} catch (Exception e) {
						onError(e);
					}
				}
				return false;
			}
		};
		api.getUsersOwnMaterials(getCallback, Order.title);
		getCallback.await(5);
		getCallback.verify(title);
	}

	private static void deleteAll(final MarvlAPI api) {
		final TestMaterialCallback deleteCallback = new TestMaterialCallback();
		api.getUsersOwnMaterials(new MaterialCallbackI() {

			@Override
			public void onLoaded(List<Material> result,
					ArrayList<Chapter> meta) {
				deleteCallback.setExpectedCount(result.size());
				for (int i = 0; i < result.size(); i++) {
					api.deleteMaterial(result.get(i), deleteCallback);
				}
			}

			@Override
			public void onError(Throwable exception) {
				//
			}
		}, Order.title);
		deleteCallback.await(10);
	}

	private ClientInfo getClient(AppDNoGui appd) {

		ClientInfo client = TubeAPITest.getClient();
		AuthenticationModel auth = appd.getLoginOperation() != null
				? appd.getLoginOperation().getModel()
				: new AuthenticationModelD();

		GeoGebraTubeUser user = new GeoGebraTubeUser("");
		user.setUserId(5);
		auth.onEvent(new LoginEvent(user, true, true, "{}"));
		client.setModel(auth);
		return client;
	}

	@Test
	public void testCopy() {
		if (System.getProperty("marvl.auth.basic") == null) {
			return;
		}
		allowMethods("PATCH");
		final MarvlAPI api = authAPI();
		final LocalizationD loc = new LocalizationD(3);
		final TestMaterialCallback copyCallback = new TestMaterialCallback();
		TestMaterialCallback uploadCallback = new TestMaterialCallback() {

			@Override
			public boolean handleMaterial(Material mat) {
				api.copy(mat, MarvlAPI.getCopyTitle(loc, mat.getTitle()),
						copyCallback);
				return true;
			}
		};
		doUpload(api, "Test material", uploadCallback);
		uploadCallback.verify("Test material");
		copyCallback.verify("Copy of Test material");
	}

	@Test
	public void testDelete() {
		if (System.getProperty("marvl.auth.basic") == null) {
			return;
		}
		allowMethods("PATCH");
		final MarvlAPI api = authAPI();

		final AppDNoGui appd = new AppDNoGui(new LocalizationD(3), false);
		api.setClient(getClient(appd));
		// clear all
		deleteAll(api);
		// upload one material
		doUpload(api, "Test Material", new TestMaterialCallback());
		// load list of materials, delete the first one
		final TestMaterialCallback deleteCallback = new TestMaterialCallback();
		api.getUsersOwnMaterials(new TestMaterialCallback() {

			@Override
			public boolean handleMaterial(Material mat) {
				api.deleteMaterial(mat, deleteCallback);
				return true;
			}
		}, Order.title);
		deleteCallback.await(5);
		deleteCallback.verify("Test Material");

		materialCountShouldBe(api, 0);
	}

	private static void materialCountShouldBe(MarvlAPI api, int i) {
		final StringBuilder count = new StringBuilder();

		// check that no materials are on server
		TestMaterialCallback getCallback = new TestMaterialCallback() {
			@Override
			public boolean handleMaterial(Material mat) {
				count.append("*");
				return false;
			}
		};
		api.getUsersOwnMaterials(getCallback, Order.title);
		getCallback.await(5);
		getCallback.verify("");
		Assert.assertEquals(i, count.length());

	}

	// Hack Java to understand PATCH method:
	// https://stackoverflow.com/a/46323891
	private static void allowMethods(String... methods) {
		try {
			Field methodsField = HttpURLConnection.class
					.getDeclaredField("methods");

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(methodsField,
					methodsField.getModifiers() & ~Modifier.FINAL);

			methodsField.setAccessible(true);

			String[] oldMethods = (String[]) methodsField.get(null);
			Set<String> methodsSet = new LinkedHashSet<>(
					Arrays.asList(oldMethods));
			methodsSet.addAll(Arrays.asList(methods));
			String[] newMethods = methodsSet.toArray(new String[0]);

			methodsField.set(null/* static field */, newMethods);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	@Test
	public void copyTitles(){
		LocalizationD loc = new LocalizationD(3);
		Assert.assertEquals("Copy of A", MarvlAPI.getCopyTitle(loc,
				"A"));
		Assert.assertEquals("Copy of A (2)",
				MarvlAPI.getCopyTitle(loc, "Copy of A"));
		Assert.assertEquals("Copy of A (3)",
				MarvlAPI.getCopyTitle(loc, "Copy of A (2)"));
	}

	@Test
	public void testRename() {
		if (System.getProperty("marvl.auth.basic") == null) {
			return;
		}
		final MarvlAPI api = authAPI();
		final TestMaterialCallback renameCallback = new TestMaterialCallback();
		TestMaterialCallback uploadCallback = new TestMaterialCallback() {

			@Override
			public boolean handleMaterial(Material mat) {
				mat.setTitle("Renamed material");
				api.uploadRenameMaterial(mat, renameCallback);
				return true;
			}
		};
		doUpload(api, "Test material", uploadCallback);
		renameCallback.await(5);
		renameCallback.verify("Renamed material");
	}

}
