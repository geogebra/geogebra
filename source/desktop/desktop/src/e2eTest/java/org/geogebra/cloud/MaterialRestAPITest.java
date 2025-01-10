package org.geogebra.cloud;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.move.ggtapi.GroupIdentifier;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.AuthenticationModel;
import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.MarvlService;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.MaterialRestAPI;
import org.geogebra.common.move.ggtapi.models.Pagination;
import org.geogebra.common.move.ggtapi.models.ResourceOrdering;
import org.geogebra.common.move.ggtapi.models.UserPublic;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.desktop.factories.UtilFactoryD;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.move.ggtapi.models.AuthenticationModelD;
import org.geogebra.desktop.move.ggtapi.models.LoginOperationD;
import org.geogebra.desktop.util.UtilD;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

public class MaterialRestAPITest {

	private static final String BASE_URL = "http://tafel.dlb-dev01.alp-dlg.net/api";

	@Test
	public void testAuth() {
		needsAuth();
		GeoGebraTubeUser usr = new GeoGebraTubeUser("");
		LoginOperationD loginOp = buildLoginOperation();
		authorise(usr, loginOp);
		Assert.assertEquals("GGBTest-Student", usr.getUserName());
		Assert.assertEquals("GGBTest-Student", 0, usr.getGroups().size());
	}

	private LoginOperationD buildLoginOperation() {
		return new LoginOperationD();
	}

	private static void authorise(GeoGebraTubeUser usr,
			LoginOperationD loginOp) {
		MaterialRestAPI api = authAPI();
		final TestAsyncOperation<Boolean> callback = new TestAsyncOperation<>();
		loginOp.getView().add(event -> {
			if (event instanceof LoginEvent) {
				callback.callback(true);
			}
		});
		api.authorizeUser(usr, loginOp, true);
		callback.await(5);
	}

	private static void pause() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static MaterialRestAPI authAPI() {
		return new MaterialRestAPI(BASE_URL, new MarvlService());
	}

	@Test
	public void testUpload() {
		needsAuth();
		MaterialRestAPI api = authAPI();
		doUpload(api, "Test material", new TestMaterialCallback());
	}

	@Test
	public void testUploadLoggout() {
		needsAuth();
		MaterialRestAPI api = new MaterialRestAPI(BASE_URL, new MarvlService());
		UtilFactory.setPrototypeIfNull(new UtilFactoryD());
		TestMaterialCallback t = new TestMaterialCallback();
		api.uploadMaterial("", "S", "This should fail",
				Base64.encodeToString(UtilD.loadFileIntoByteArray(
						"src/test/resources/slides.ggs"), false),
				t, MaterialType.ggs, false);
		t.await(5);
		t.verifyError(".*401.*");
	}

	/**
	 * TODO groups are shibboleth feature and can't be tested with simple login
	 */
	@Test
	@Ignore
	public void testmaterialGroup() {
		needsAuth();
		final MaterialRestAPI api = authAPI();
		final String[] success = new String[1];
		final TestAsyncOperation<List<GroupIdentifier>> groupCallback =
				new TestAsyncOperation<List<GroupIdentifier>>() {

			@Override
			public void callback(List<GroupIdentifier> obj) {
				success[0] = obj == null ? "FAIL" : obj.size() + "";

			}
		};
		doUpload(api, "Test material", new TestMaterialCallback() {
			@Override
			public boolean handleMaterial(Material mat) {

				api.getGroups(mat.getSharingKey(), null, groupCallback);
				return true;
			}
		});
		groupCallback.await(5);
		Assert.assertEquals("0", success[0]);
	}

	private static void doUpload(MaterialRestAPI api, String title,
			TestMaterialCallback testCallback) {
		api.uploadMaterial("", "S", title,
				Base64.encodeToString(UtilD.loadFileIntoByteArray(
						"src/test/resources/slides.ggs"), false),
				testCallback, MaterialType.ggs, false);
		testCallback.await(5);
		testCallback.verify(title);
	}

	@Test
	public void testOpen() {
		needsAuth();
		final MaterialRestAPI api = authAPI();
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
		api.getUsersOwnMaterials(getCallback, ResourceOrdering.title);
		getCallback.await(5);
		getCallback.verify(title);
	}

	private static void deleteAll(final MaterialRestAPI api) {
		final TestMaterialCallback deleteCallback = new TestMaterialCallback();
		api.getUsersOwnMaterials(new MaterialCallbackI() {

			@Override
			public void onLoaded(List<Material> result,
					Pagination meta) {
				deleteCallback.setExpectedCount(result.size());
				for (Material material : result) {
					api.deleteMaterial(material, deleteCallback);
				}
			}

			@Override
			public void onError(Throwable exception) {
				//
			}
		}, ResourceOrdering.title);
		deleteCallback.await(10);
	}

	private ClientInfo getClient(AppDNoGui appd) {

		ClientInfo client = TubeAPITest.getClient();
		AuthenticationModel auth = appd.getLoginOperation() != null
				? appd.getLoginOperation().getModel()
				: new AuthenticationModelD();

		GeoGebraTubeUser user = new GeoGebraTubeUser("");
		user.setUserId(5);
		auth.onLogin(new LoginEvent(user, true, true, "{}"));
		client.setModel(auth);
		return client;
	}

	@Test
	public void testCopy() {
		needsAuth();
		allowMethods("PATCH");
		final MaterialRestAPI api = authAPI();
		final LocalizationD loc = new LocalizationD(3);
		final TestMaterialCallback copyCallback = new TestMaterialCallback();
		TestMaterialCallback uploadCallback = new TestMaterialCallback() {

			@Override
			public boolean handleMaterial(Material mat) {
				api.copy(mat, MaterialRestAPI.getCopyTitle(loc, mat.getTitle()),
						copyCallback);
				return true;
			}
		};
		doUpload(api, "Test material", uploadCallback);
		copyCallback.await(5);
		copyCallback.verify("Copy of Test material");
	}

	@Test
	public void testDelete() {
		needsAuth();
		allowMethods("PATCH");
		final MaterialRestAPI api = authAPI();

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
		}, ResourceOrdering.title);
		deleteCallback.await(5);
		deleteCallback.verify("Test Material");

		materialCountShouldBe(api, 0);
	}

	private static void materialCountShouldBe(MaterialRestAPI api, int i) {
		final StringBuilder count = new StringBuilder();

		// check that no materials are on server
		TestMaterialCallback getCallback = new TestMaterialCallback() {
			@Override
			public boolean handleMaterial(Material mat) {
				count.append("*");
				return false;
			}
		};
		getCallback.setExpectedCount(i);
		api.getUsersOwnMaterials(getCallback, ResourceOrdering.title);
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
	public void copyTitles() {
		LocalizationD loc = new LocalizationD(3);
		Assert.assertEquals("Copy of A", MaterialRestAPI.getCopyTitle(loc, "A"));
		Assert.assertEquals("Copy of A (2)",
				MaterialRestAPI.getCopyTitle(loc, "Copy of A"));
		Assert.assertEquals("Copy of A (3)",
				MaterialRestAPI.getCopyTitle(loc, "Copy of A (2)"));
	}

	@Test
	public void testRename() {
		needsAuth();
		final MaterialRestAPI api = authAPI();
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

	@Test
	public void testReupload() {
		needsAuth();
		final AppDNoGui appd = new AppDNoGui(new LocalizationD(3), false);
		final MaterialRestAPI api = authAPI();
		api.setClient(getClient(appd));
		deleteAll(api);
		final String[] filenames = new String[2];
		final TestMaterialCallback reuploadCallback = new TestMaterialCallback() {
			@Override
			public boolean handleMaterial(Material mat) {
				filenames[1] = mat.getFileName();
				return true;
			}
		};
		TestMaterialCallback uploadCallback = new TestMaterialCallback() {

			@Override
			public boolean handleMaterial(Material mat) {
				filenames[0] = mat.getFileName();
				pause();
				api.uploadMaterial(mat.getSharingKey(), "S",
						"Test material",
						Base64.encodeToString(
								UtilD.loadFileIntoByteArray(
										"src/test/resources/slides.ggs"),
								false),
						reuploadCallback, MaterialType.ggs, false);
				return true;
			}
		};
		doUpload(api, "Test material", uploadCallback);
		reuploadCallback.await(5);
		reuploadCallback.verify("Test material");
		materialCountShouldBe(api, 1);
		Assert.assertNotEquals(filenames[0], filenames[1]);
	}

	@Test
	public void writePermissionsTest() {
		needsAuth();

		Material mat = new Material(MaterialType.ggs);
		mat.setSharingKey("k89JtCqY");
		GeoGebraTubeUser usr = new GeoGebraTubeUser("");
		LoginOperationD loginOp = buildLoginOperation();

		Assert.assertTrue("Should overwrite anonymous materials",
				loginOp.owns(mat));
		authorise(usr, loginOp);
		mat.setCreator(new UserPublic(42, "Bart"));
		Assert.assertFalse("Should not overwrite foreign materials",
				loginOp.owns(mat));
		Assert.assertTrue("User ID should be set", usr.getUserId() > 0);
		mat.setCreator(new UserPublic(loginOp.getModel().getUserId(),
				loginOp.getUserName()));
		Assert.assertTrue("Should overwrite own materials",
				loginOp.owns(mat));

	}

	private static void needsAuth() {
		Assume.assumeNotNull(System.getProperty("marvl.auth.basic"));
	}

}
