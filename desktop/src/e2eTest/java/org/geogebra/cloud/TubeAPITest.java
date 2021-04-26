package org.geogebra.cloud;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.geogebra.common.jre.util.Base64;
import org.geogebra.common.main.Feature;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.AuthenticationModel;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.MaterialRequest.Order;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.move.ggtapi.models.AuthenticationModelD;
import org.geogebra.desktop.move.ggtapi.models.GeoGebraTubeAPID;
import org.geogebra.desktop.util.LoggerD;
import org.geogebra.desktop.util.UtilD;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TubeAPITest extends Assert {
	public static final String circleBase64 = Base64.encodeToString(
			UtilD.loadFileIntoByteArray("src/e2eTest/resources/circles.ggb"),
			false);
	private AppDNoGui app;

	@Before
	public void setup() {
		app = new AppDNoGui(new LocalizationD(3), false);
	}

	@BeforeClass()
	public static void startLogging() {
		Log.setLogger(new LoggerD());
	}

	@Test
	public void testSearch() {

		GeoGebraTubeAPID api = new GeoGebraTubeAPID(app
						.has(Feature.TUBE_BETA),
				getClient());
		updateUrls(api);
		final ArrayList<String> titles = new ArrayList<>();
		api.search("pythagoras", new MaterialCallbackI() {

			@Override
			public void onLoaded(List<Material> result,
					ArrayList<Chapter> meta) {
				for (Material m : result) {
					titles.add(m.getTitle());
				}
			}

			@Override
			public void onError(Throwable exception) {
				exception.printStackTrace();

			}
		});
		awaitValidTitlesExact("search", titles, 30);
	}

	private void updateUrls(GeoGebraTubeAPID api) {
		if ("e2e".equals(System.getProperty("ggb.env"))) {
			api.setURL("https://e2e.geogebra.org/api/json.php");
			api.setLoginURL("https://e2e-accounts.geogebra.org/api/index.php");
		}
	}

	/**
	 * Upload a simple file as new file
	 */
	@Test
	public void testUpload() {

		GeoGebraTubeAPID api = getAuthAPI();
		final ArrayList<String> titles = new ArrayList<>();

		uploadMaterial(api, titles, 0, null);

		awaitValidTitlesExact("upload", titles, 1);
	}

	@Test
	public void testReupload() {
		final GeoGebraTubeAPID api = getAuthAPI();
		final ArrayList<String> titles = new ArrayList<>();

		uploadMaterial(api, titles, 0, id -> uploadMaterial(api, titles, id, null));

		awaitValidTitlesExact("upload", titles, 2);
	}

	private void uploadMaterial(GeoGebraTubeAPID api,
			final ArrayList<String> titles, int id, final IdCallback callback) {

		api.uploadMaterial(id + "", "O",
				"testfile" + new Date() + Math.random(), circleBase64,
				new MaterialCallbackI() {

					@Override
					public void onLoaded(List<Material> result,
							ArrayList<Chapter> meta) {
						if (result.size() > 0) {
							for (Material m : result) {
								titles.add(m.getTitle());
								if (callback != null) {
									callback.handle(m.getId());
								}
							}
						} else {
							titles.add("FAIL nothing uploaded");
						}

					}

					@Override
					public void onError(Throwable exception) {
						exception.printStackTrace();
						titles.add("FAIL " + exception.getMessage());

					}

				}, MaterialType.ggb);
	}

	@Test
	public void copyMaterial() {
		final GeoGebraTubeAPID api = getAuthAPI();
		final ArrayList<String> titles = new ArrayList<>();
		final MaterialCallbackI copyCallback = new MaterialCallbackI() {

			@Override
			public void onLoaded(List<Material> result,
					ArrayList<Chapter> meta) {
				if (result.size() == 1) {
					titles.add(result.get(0).getTitle());
				} else {
					titles.add("FAIL " + result.size());
				}
			}

			@Override
			public void onError(Throwable exception) {
				exception.printStackTrace();
				titles.add("FAIL " + exception.getMessage());

			}
		};
		api.copy(new Material(144, MaterialType.ggb), "Copy of Mobile Example",
				copyCallback);
		awaitValidTitlesExact("upload", titles, 1);
	}

	@Test
	public void getSingleMaterialShouldWorkWithEmptyToken() {
		final GeoGebraTubeAPID api = getAuthAPI("");
		final ArrayList<String> titles = new ArrayList<>();
		final MaterialCallbackI getCallback = new MaterialCallbackI() {

			@Override
			public void onLoaded(List<Material> result,
					ArrayList<Chapter> meta) {
				if (result.size() == 1) {
					titles.add(result.get(0).getTitle());
				} else {
					titles.add("FAIL " + result.size());
				}
			}

			@Override
			public void onError(Throwable exception) {
				titles.add("FAIL " + exception.getMessage());

			}
		};
		api.getItem("144", getCallback);
		awaitValidTitlesExact("fetch", titles, 1);
	}

	private static void awaitValidTitlesExact(String description,
			ArrayList<String> titles, int len) {
		awaitValidTitles(description, titles, len);
		assertEquals("Wrong number of " + description + " results", len,
				titles.size());
	}

	private static void awaitValidTitles(String description, ArrayList<String> titles, int len) {
		for (int i = 0; i < 20 && titles.size() < len; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (String title : titles) {
			assertFalse("Wrong " + description + " result: " + title,
					title.contains("FAIL"));
		}
	}

	/**
	 * Upload one material and delete all materials in account.
	 */
	@Test
	public void testDelete() {
		testUpload(); // ensure we have st to delete
		final GeoGebraTubeAPID api = getAuthAPI();
		final ArrayList<String> titles = new ArrayList<>();

		api.getUsersOwnMaterials(new MaterialCallbackI() {

			@Override
			public void onLoaded(List<Material> result,
					ArrayList<Chapter> meta) {

				for (Material m : result) {
					final Material mFinal = m;
					api.deleteMaterial(m, new MaterialCallbackI() {

						@Override
						public void onLoaded(List<Material> result1,
								ArrayList<Chapter> meta1) {

							titles.add(mFinal.getTitle());

						}

						@Override
						public void onError(Throwable exception) {
							// TODO Auto-generated method stub

						}
					});
					// titles.add(m.getTitle());
				}
			}

			@Override
			public void onError(Throwable exception) {
				exception.printStackTrace();
				Assert.assertNull(exception.getMessage());
			}

		}, Order.description);
		awaitValidTitles("delete",  titles, 1);
	}


	private GeoGebraTubeAPID getAuthAPI() {
		return getAuthAPI(getToken());
	}

	private static String getToken() {
		Assume.assumeNotNull(System.getProperty("materials.token"));
		return System.getProperty("materials.token").trim();
	}

	private GeoGebraTubeAPID getAuthAPI(String token) {
		GeoGebraTubeAPID geoGebraTubeAPID = new GeoGebraTubeAPID(app
				.has(Feature.TUBE_BETA), getAuthClient(null, token));
		updateUrls(geoGebraTubeAPID);
		return geoGebraTubeAPID;
	}

	protected static ClientInfo getClient() {
		ClientInfo client = new ClientInfo();
		// client.setModel((AuthenticationModel) this.model);
		client.setType("desktop");
		client.setId("APITEST");
		client.setWidth(1024);
		client.setWidth(768);
		client.setLanguage("en");
		return client;
	}

	private static ClientInfo getAuthClient(LogInOperation op,
			String token) {
		ClientInfo client = getClient();
		AuthenticationModel auth = op != null ? op.getModel()
				: new AuthenticationModelD();
		GeoGebraTubeUser user = new GeoGebraTubeUser(token);

		user.setUserId(4951854);
		auth.onLogin(new LoginEvent(user, true, true, "{}"));
		client.setModel(auth);
		return client;
	}

}
