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
import org.geogebra.common.move.ggtapi.models.SyncEvent;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.move.ggtapi.requests.SyncCallback;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.move.ggtapi.models.AuthenticationModelD;
import org.geogebra.desktop.move.ggtapi.models.GeoGebraTubeAPID;
import org.geogebra.desktop.util.LoggerD;
import org.geogebra.desktop.util.UtilD;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

public class TubeAPITest extends Assert {
	public static final String circleBase64 = Base64.encodeToString(
			UtilD.loadFileIntoByteArray("src/test/resources/circles.ggb"),
			false);

	@BeforeClass()
	public static void startLogging() {
		Log.setLogger(new LoggerD());
	}

	@Test
	public void testSearch() {

		GeoGebraTubeAPID api = new GeoGebraTubeAPID(
				new AppDNoGui(new LocalizationD(3), false)
						.has(Feature.TUBE_BETA),
				getClient());
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
		for (int i = 0; i < 20 && titles.size() < 30; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		assertEquals("Wrong number of search results", 30, titles.size());
	}

	@Test
	/**
	 * Upload a simple file as new file
	 */
	public void testUpload() {

		GeoGebraTubeAPID api = getAuthAPI();
		final ArrayList<String> titles = new ArrayList<>();

		uploadMaterial(api, titles, 0, null);

		for (int i = 0; i < 20 && titles.size() < 1; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		checkNoFail(titles, 1);
	}

	@Test
	public void testReupload() {
		final GeoGebraTubeAPID api = getAuthAPI();
		final ArrayList<String> titles = new ArrayList<>();

		uploadMaterial(api, titles, 0, new IdCallback() {

			@Override
			public void handle(int id) {
				uploadMaterial(api, titles, id, null);

			}
		});

		for (int i = 0; i < 20 && titles.size() < 2; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		checkNoFail(titles, 2);
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
							titles.add("FAIL " + result.size());
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
		for (int i = 0; i < 20 && titles.size() == 0; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		checkNoFail(titles, 1);
	}

	private static void checkNoFail(ArrayList<String> titles, int len) {
		for (String title : titles) {
			assertFalse("Wrong upload result: " + title,
					title.contains("FAIL"));
		}
		assertEquals("Wrong number of upload results", len, titles.size());
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

				System.out.println(result.size());
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

		for (int i = 0; i < 20 && titles.size() < 1; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		assertTrue("Wrong number of deleted results", titles.size() > 0);
		assertFalse("Wrong upload result: " + titles.get(0),
				titles.get(0).contains("FAIL"));
	}

	private GeoGebraTubeAPID getAuthAPI() {
		return new GeoGebraTubeAPID(new AppDNoGui(new LocalizationD(3), false)
				.has(Feature.TUBE_BETA), getAuthClient(null));
	}

	@Test
	public void testSync() {
		AppDNoGui app = new AppDNoGui(new LocalizationD(3), false);
		final ClientInfo client = getAuthClient(app.getLoginOperation());
		app.getLoginOperation().getGeoGebraTubeAPI().setClient(client);
		final TestMaterialsManager man = new TestMaterialsManager(app);
		Material mat = new Material(0, MaterialType.ggb);
		mat.setTitle("test-sync-" + new Date() + Math.random());
		mat.setBase64(circleBase64);
		mat.setLanguage("en");
		man.insertFile(mat);
		app.getLoginOperation().getGeoGebraTubeAPI().sync(0,
				new SyncCallback() {

					@Override
					public void onSync(ArrayList<SyncEvent> events) {
						man.uploadUsersMaterials(events);

					}
				});
		for (int i = 0; i < 20; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	private static ClientInfo getAuthClient(LogInOperation op) {
		ClientInfo client = getClient();
		AuthenticationModel auth = op != null ? op.getModel()
				: new AuthenticationModelD();

		Assume.assumeNotNull(System.getProperty("materials.token"));
		GeoGebraTubeUser user = new GeoGebraTubeUser(
				System.getProperty("materials.token").trim());

		user.setUserId(4951854);
		auth.onEvent(new LoginEvent(user, true, true, "{}"));
		client.setModel(auth);
		return client;
	}

}
