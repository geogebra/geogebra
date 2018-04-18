package org.geogebra.cloud;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.geogebra.common.main.Feature;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.ggtapi.models.AuthenticationModel;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.ClientInfo;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.move.ggtapi.models.SyncEvent;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.move.ggtapi.requests.SyncCallback;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.main.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.move.ggtapi.models.AuthenticationModelD;
import org.geogebra.desktop.move.ggtapi.models.GeoGebraTubeAPID;
import org.geogebra.desktop.util.LoggerD;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TubeAPITest extends Assert {
	public static final String circleBase64 = "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";

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

		assertEquals("Wrong number of upload results", 1, titles.size());
		assertFalse("Wrong upload result: " + titles.get(0),
				titles.get(0).contains("FAIL"));
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

		assertEquals("Wrong number of upload results", 2, titles.size());
		assertFalse("Wrong upload result: " + titles.get(0),
				titles.get(0).contains("FAIL"));
	}

	private void uploadMaterial(GeoGebraTubeAPID api,
			final ArrayList<String> titles, int id, final IdCallback callback) {

		api.uploadMaterial(id, "O", "testfile" + new Date() + Math.random(),
				circleBase64, new MaterialCallbackI() {

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
						Assert.assertNull(exception.getMessage());

					}
				}, MaterialType.ggb);

	}

	@Test
	public void copyMaterial() {
		final GeoGebraTubeAPID api = getAuthAPI();
		for (String mid : new String[] {}) {
			api.getItem(mid, new MaterialCallbackI() {

			public void onLoaded(List<Material> result,
					ArrayList<Chapter> meta) {
				api.uploadMaterial(0, "O",
							result.get(0).getTitle(),
						result.get(0).getBase64(),
						new MaterialCallbackI() {

							@Override
							public void onLoaded(List<Material> result,
									ArrayList<Chapter> meta) {
								Log.debug("Wheee!");
							}

							@Override
							public void onError(Throwable exception) {
								exception.printStackTrace();
								Assert.assertNull(exception.getMessage());

							}
						}, MaterialType.ggb, result.get(0));

			}

			public void onError(Throwable exception) {
				System.err.println(exception);

			}
		});
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		;


	}
	/**
	 * Upload one material and delete all materials in account.
	 */
	@Test
	public void testDelete() {
		testUpload();// ensure we have st to delete
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
		});

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
		final TestMaterialsManager man = new TestMaterialsManager(
				app);
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

	private static ClientInfo getClient() {
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

		Assert.assertNotNull("Token must be stored as materials.token",
				System.getProperty("materials.token"));
		GeoGebraTubeUser user = new GeoGebraTubeUser(
				System.getProperty("materials.token").trim());

		user.setUserId(6162121);
		auth.onEvent(new LoginEvent(user, true, true, "{}"));
		client.setModel(auth);
		return client;
	}

	String[] ord = { "VxJyMV9m", "PHTsMPDY", "J4f6V5NE", "kj5DcRvn", "AhaRCfWA",
			"JHhFr4MZ", "FkcAqPyk", "Vq2xgxdM", "cZvr3Hbt", "Q9ebERR6",
			"wAyrpfgX", "XpEZQfqp", "Xh4Pb4Cc", "zPHYaa2F", "pxu4G7CK",
			"uG4wj6fW", "hgU5BKzj", "XFx3bD7h", "xkZmC8j8", "ycJSQpXT",
			"ycJSQpXT", "ycJSQpXT", "ycJSQpXT", "ycJSQpXT", "ycJSQpXT",
			"ycJSQpXT", "ycJSQpXT", "BhBXHJwV", "XsnFAdq7", "zxRa9YuY",
			"TynXHZv3", "zHN6hAGH", "kxzmdssa", "TAPZhcqg", "bGdTdPJR",
			"KHN97EWB", "JAThv7n5", "H4FrjARN", "fPeQGtej", "UseSMZSp",
			"aVkbzxQ7", "RkwpHbqZ", "tbmsMsJZ", "z3hY3qBZ", "QAhNg5Su",
			"M4JKMQgs", "G5vPFZMv", "f6YyUjVc", "qh5jDKmX", "vpJpvqhP",
			"mhvvQU5Y", "Y6dfR3GW", "FRh5M5eb", "ju3eMb9f", "FsKb2SXD",
			"mrcZsHxp", "PpsP3Knm", "AEJZpFYq", "NSQmZf7q", "edxPr72G",
			"wYYvZH7A", "RUtdpQmN", "nKQmSnDW", "fSaq5Xc5", "n9AM2ZZp",
			"jqvTEgsj", "VVZzNwgg", "FnsBW2Rq", "thAERnHY", "ZgZrzwkN",
			"Ccv3FucS", "YF2EDCTt", "N7uYVnXD", "Knr939et", "dUedEh7r",
			"ZxqRvGWK", "f8GC2nKQ", "f6v5BREZ", "QWj2ptWS", "dFYbT8He",
			"jjRtyeJX", "nQh9SVzk", "b25xp66d", "U7wJUpeD", "Auz5nxg9",
			"SRzKTywa", "VZ4fhUTJ", "vA4jWCZ6", "Pa3zXzkG", "fngAUjVn",
			"f94RSWfW", "EpztQZ6y", "EpztQZ6y", "nApKCj3Q", "wJWDB3pw",
			"fK74qEqW", "FXEZD466", "FXEZD466", "WfP5JSmE", "WfP5JSmE",
			"D2vyZGba", "B4pcFNPC", "nfdBbPR3", "ftGcrgJ5", "P7pydzZB",
			"DvdeB7ZP", "MktzTda5", "mPJXHBBA", "dJRKsNSJ", "NAtdGb97",
			"WtaGDhSN", "Vxv48Gtz", "uK8n8GmN", "bz7v4QgM", "beVbuPFP",
			"SdP7kx2v", "PUEGS43F", "fhm2Tkk9", "yrZKQ9r8", "PHQxsCFp",
			"f3P3Qs8h", "RKQc269k", "ZGMRnGbq", "SKRXxYVQ", "dgERV46M",
			"DsB2VFYv", "DYbruk8N", "NCUhVHyQ", "fRVfZ7An", "HkyGsPVW",
			"NyKKeMqQ", "Cz3NRgmT", "w4byKZfT", "PKDtR4vP", "dGeTZDSv",
			"j8B9vZKV", "Fgeza7B8", "Fgeza7B8", "Fgeza7B8", "PjUFDcvs",
			"dx4X8ReQ", "RQ58fzCE", "cBbfA3nM", "Szb8xcvK", "hDfcuwNb",
			"hk2bGvEs", "qK3cg6yG", "S63h3qsk", "ud5nTe4s", "TdyBxNRW",
			"DY6HFe4P", "M4668aNC", "vNFKQhe6", "xkZmC8j8", "FRh5M5eb",
			"cBbfA3nM", "Szb8xcvK", "Ccv3FucS", "Vxv48Gtz", "fhm2Tkk9",
			"NAtdGb97", "SHCGnsTR", "fnNgHFdb", "ud5nTe4s", "nfdBbPR3",
			"gfHegqEK", "TVkP9mHe", "F4X5JYm7", "hJ7vaXpa", "qZCCs6Vc",
			"h6qq8j3d", "BhBXHJwV", "AmBY9MWn", "Q8wrcAWc", "CCnxTNsQ",
			"cdCejVUW", "C65Aq6NC", "csdsSV2n", "cp3xmE5v", "jZgqAAgd",
			"F5uXTbhN", "Gv5SZba6", "EE58262N", "PzNWqA29", "Z6Bh9TNV",
			"qZFUSEuA", "s474FaxY", "rXW6mvdC", "WCm4Y6dK", "pbczUxkY",
			"ZpexxzFy", "hRw74pss", "GS4E4rjc", "CR6zNyZS", "d7wPqs9B",
			"sJqu2Cmd", "aSUmJFCu", "MMKbum8v", "n2mcbeKd", "XF2dwtWK",
			"U4U26bKX", "fMmjSjQm", "nzYQGDxb", "WPf5a2QJ", "cPGTf6pd",
			"aWqR27Uy", "V7bWnzSb", "esEdKmyB", "fJPk7QNu", "TWWhGXmq",
			"G7vMh5Up", "TxETsBWF", "f4gwAUQz", "BBHP2m9M", "gDkZ6Yjy",
			"nRcKbSWn", "R3HT5raN", "n6pZR8hr", "bkDEMsex", "BuJcagUr",
			"nJQsTnWe", "Cqw7AKcp", "xbEUGnx8", "eFeE2Veu", "QckPMg3z",
			"vNFKQhe6", "TdYVZANp", "F4X5JYm7", "htbRbpHE", "yA3xMKvH",
			"aCRt6bAF", "A659cMp3", "KdNG7gTg", "AfFhY7BP", "reYPNn3V",
			"reYPNn3V", "reYPNn3V", "reYPNn3V", "reYPNn3V", "reYPNn3V",
			"reYPNn3V", "wh5qyme4", "rdMgwh9G", "n2mcbeKd", "cTyZbngu",
			"yNJ6guSn", "STkyu8GK", "STkyu8GK", "gk2jjUxj", "gk2jjUxj",
			"KQvWdEGJ", "rs8nUEhw", "um8Ybrwj", "hgU5BKzj", "XFx3bD7h",
			"VxJyMV9m", "PHTsMPDY", "J4f6V5NE", "pxu4G7CK", "uG4wj6fW",
			"wAyrpfgX", "XpEZQfqp", "Xh4Pb4Cc", "zPHYaa2F", "kj5DcRvn",
			"AhaRCfWA", "JHhFr4MZ", "FkcAqPyk", "Vq2xgxdM", "cZvr3Hbt",
			"Q9ebERR6", "xkZmC8j8", "ycJSQpXT", "ycJSQpXT", "ycJSQpXT",
			"ycJSQpXT", "ycJSQpXT", "ycJSQpXT", "ycJSQpXT", "ycJSQpXT",
			"BhBXHJwV", "wJWDB3pw", "A659cMp3", "fK74qEqW", "EpztQZ6y",
			"EpztQZ6y", "nApKCj3Q", "FXEZD466", "WfP5JSmE", "FXEZD466",
			"WfP5JSmE", "D2vyZGba", "B4pcFNPC", "nfdBbPR3", "ftGcrgJ5",
			"P7pydzZB", "DvdeB7ZP", "aCRt6bAF", "mPJXHBBA", "dJRKsNSJ",
			"MktzTda5", "csdsSV2n", "cp3xmE5v", "wh5qyme4", "jZgqAAgd",
			"Gv5SZba6", "EE58262N", "PzNWqA29", "Z6Bh9TNV", "qZFUSEuA",
			"s474FaxY", "rXW6mvdC", "WCm4Y6dK", "F5uXTbhN", "f4gwAUQz",
			"BBHP2m9M", "TxETsBWF", "XsnFAdq7", "zxRa9YuY", "TynXHZv3",
			"zHN6hAGH", "kxzmdssa", "TAPZhcqg", "bGdTdPJR", "KHN97EWB",
			"JAThv7n5", "H4FrjARN", "fPeQGtej", "UseSMZSp", "aVkbzxQ7",
			"KdNG7gTg", "yA3xMKvH", "z3hY3qBZ", "f6YyUjVc", "QAhNg5Su",
			"G5vPFZMv", "M4JKMQgs", "qh5jDKmX", "vpJpvqhP", "RkwpHbqZ",
			"tbmsMsJZ", "mrcZsHxp", "PpsP3Knm", "QckPMg3z", "Y6dfR3GW",
			"FRh5M5eb", "ju3eMb9f", "FsKb2SXD", "NAtdGb97", "j8B9vZKV",
			"WtaGDhSN", "Vxv48Gtz", "yNJ6guSn", "TCwmTXuu", "G7vMh5Up",
			"fJPk7QNu", "cTyZbngu", "uK8n8GmN", "bz7v4QgM", "reYPNn3V",
			"reYPNn3V", "reYPNn3V", "reYPNn3V", "reYPNn3V", "reYPNn3V",
			"reYPNn3V", "DYbruk8N", "NCUhVHyQ", "fRVfZ7An", "HkyGsPVW",
			"NyKKeMqQ", "Cz3NRgmT", "w4byKZfT", "PKDtR4vP", "dGeTZDSv",
			"Fgeza7B8", "Fgeza7B8", "Fgeza7B8", "fhm2Tkk9", "yrZKQ9r8",
			"PHQxsCFp", "f3P3Qs8h", "RKQc269k", "ZGMRnGbq", "SKRXxYVQ",
			"dgERV46M", "DsB2VFYv", "beVbuPFP", "SdP7kx2v", "PUEGS43F",
			"pbczUxkY", "ZpexxzFy", "hRw74pss", "GS4E4rjc", "CR6zNyZS",
			"d7wPqs9B", "MMKbum8v", "sJqu2Cmd", "aSUmJFCu", "mhvvQU5Y",
			"gDkZ6Yjy", "dUedEh7r", "AEJZpFYq", "NSQmZf7q", "edxPr72G",
			"wYYvZH7A", "RUtdpQmN", "nKQmSnDW", "Cqw7AKcp", "xbEUGnx8",
			"eFeE2Veu", "fSaq5Xc5", "n9AM2ZZp", "jqvTEgsj", "N7uYVnXD",
			"Knr939et", "VVZzNwgg", "FnsBW2Rq", "thAERnHY", "ZgZrzwkN",
			"YF2EDCTt", "Ccv3FucS", "rdMgwh9G", "ZxqRvGWK", "f8GC2nKQ",
			"f6v5BREZ", "nQh9SVzk", "QWj2ptWS", "dFYbT8He", "jjRtyeJX",
			"b25xp66d", "U7wJUpeD", "Auz5nxg9", "SRzKTywa", "VZ4fhUTJ",
			"vA4jWCZ6", "Pa3zXzkG", "fngAUjVn", "f94RSWfW", "AfFhY7BP",
			"PjUFDcvs", "dx4X8ReQ", "RQ58fzCE", "cBbfA3nM", "Szb8xcvK",
			"hDfcuwNb", "qK3cg6yG", "hk2bGvEs", "TdyBxNRW", "M4668aNC",
			"S63h3qsk", "ud5nTe4s", "DY6HFe4P", "WPf5a2QJ", "cPGTf6pd",
			"aWqR27Uy", "V7bWnzSb", "esEdKmyB", "XF2dwtWK", "U4U26bKX",
			"fMmjSjQm", "nzYQGDxb", "n2mcbeKd", "nRcKbSWn", "R3HT5raN",
			"n6pZR8hr", "bkDEMsex", "BuJcagUr", "nJQsTnWe", "n2mcbeKd",
			"qwAeDr23", "STkyu8GK", "STkyu8GK", "gk2jjUxj", "gk2jjUxj",
			"KQvWdEGJ", "rs8nUEhw", "um8Ybrwj", "cQmmGt54", "u5nkDXDy",
			"Rp9u74RP", "cQmmGt54", "u5nkDXDy" };

	@Test
	public void map() {
		HashMap<String, String> m = new HashMap<>();
		p(m, "ZYAWgqfd", "A659cMp3");
		p(m, "Np8D5HW6", "A659cMp3");
		p(m, "gu8WkMr3", "aCRt6bAF");
		p(m, "N4XHv9re", "aCRt6bAF");
		p(m, "pPyBPMXB", "AEJZpFYq");
		p(m, "sRgwvBDB", "AEJZpFYq");
		p(m, "Pf9j89xX", "AfFhY7BP");
		p(m, "RxCtzZBF", "AfFhY7BP");
		p(m, "BzjqygMN", "AhaRCfWA");
		p(m, "WFxjrwyb", "AhaRCfWA");
		p(m, "zfTfnppM", "AhaRCfWA");
		p(m, "fPw9TupT", "AmBY9MWn");
		p(m, "rMHEK24A", "aSUmJFCu");
		p(m, "YaA37Hmf", "aSUmJFCu");
		p(m, "mHDqd9mc", "Auz5nxg9");
		p(m, "Hm8NqF69", "Auz5nxg9");
		p(m, "pGXp7BXp", "aVkbzxQ7");
		p(m, "y3ZDtxTj", "aVkbzxQ7");
		p(m, "sSWErMvV", "aWqR27Uy");
		p(m, "xAAd2hsK", "aWqR27Uy");
		p(m, "SnDq6NNy", "b25xp66d");
		p(m, "hXRJcAyP", "b25xp66d");
		p(m, "AvWazFdT", "B4pcFNPC");
		p(m, "ZYE6XyRa", "B4pcFNPC");
		p(m, "HbKacSAb", "B4pcFNPC");
		p(m, "QZcMMUK4", "BBHP2m9M");
		p(m, "GfSZeJqt", "BBHP2m9M");
		p(m, "dYYq69fK", "BBHP2m9M");
		p(m, "v3cATej6", "beVbuPFP");
		p(m, "BSpN9aqX", "bGdTdPJR");
		p(m, "uHjNpebz", "BhBXHJwV");
		p(m, "F2eZje9T", "bkDEMsex");
		p(m, "Az9sSQMT", "BuJcagUr");
		p(m, "rp3yp8tc", "bz7v4QgM");
		p(m, "ehyb9yVd", "cBbfA3nM");
		p(m, "WKjaJKKu", "CCnxTNsQ");
		p(m, "StjpbRYS", "Ccv3FucS");
		p(m, "RvBG3sXy", "cdCejVUW");
		p(m, "gcaaPtYq", "cp3xmE5v");
		p(m, "JbTstqCm", "cPGTf6pd");
		p(m, "hw9aCCq5", "cQmmGt54");
		p(m, "tzjrxT4a", "Cqw7AKcp");
		p(m, "cdTzCBNE", "CR6zNyZS");
		p(m, "e5kmKP57", "csdsSV2n");
		p(m, "Jft8Bmkd", "cTyZbngu");
		p(m, "HW7CFVe2", "Cz3NRgmT");
		p(m, "CkJPNren", "Cz3NRgmT");
		p(m, "wbK2QjH6", "cZvr3Hbt");
		p(m, "p28c8kaR", "D2vyZGba");
		p(m, "zdV5rsC3", "d7wPqs9B");
		p(m, "mCxk7BDZ", "dFYbT8He");
		p(m, "yaA3FBwF", "dgERV46M");
		p(m, "AskePDuj", "dGeTZDSv");
		p(m, "q2K7hbfk", "dJRKsNSJ");
		p(m, "eXBquMsW", "DsB2VFYv");
		p(m, "j2mdPQKy", "dUedEh7r");
		p(m, "EJuGdBg3", "DvdeB7ZP");
		p(m, "PGWgdehp", "dx4X8ReQ");
		p(m, "hGpHHHH4", "dx4X8ReQ");
		p(m, "hGpHHHH4", "dx4X8ReQ");
		p(m, "J3cBSeWZ", "dx4X8ReQ");
		p(m, "J3cBSeWZ", "dx4X8ReQ");
		p(m, "x7JyptDy", "DY6HFe4P");
		p(m, "Cbjr63hD", "DYbruk8N");
		p(m, "nKTGPnHJ", "edxPr72G");
		p(m, "NMvRCyYv", "EE58262N");
		p(m, "XKaDJ2vS", "eFeE2Veu");
		p(m, "BcCSurVc", "EpztQZ6y");
		p(m, "U9mrhYED", "esEdKmyB");
		p(m, "b37vvej2", "f3P3Qs8h");
		p(m, "zSUfZVY6", "f4gwAUQz");
		p(m, "r9WJ8F9D", "f4gwAUQz");
		p(m, "eaqBq9SS", "F4X5JYm7");
		p(m, "vZuUVcbH", "F5uXTbhN");
		p(m, "eYCK7tzc", "f6v5BREZ");
		p(m, "uTCUb8jP", "f6YyUjVc");
		p(m, "sc95YNey", "f8GC2nKQ");
		p(m, "mcAEyEnk", "f94RSWfW");
		p(m, "dRRNAmp3", "Fgeza7B8");
		p(m, "vCQcGKEE", "fhm2Tkk9");
		p(m, "UJuy3X8q", "fJPk7QNu");
		p(m, "bmG3uSet", "fK74qEqW");
		p(m, "DreUGeSW", "FkcAqPyk");
		p(m, "d8p9sHdh", "FkcAqPyk");
		p(m, "JmBxuKYV", "fMmjSjQm");
		p(m, "DwSeD2ru", "fMmjSjQm");
		p(m, "pKXy2mDx", "fngAUjVn");
		p(m, "peeWAs3j", "fnNgHFdb");
		p(m, "M6TbxPJZ", "FnsBW2Rq");
		p(m, "zDJDxuKK", "fPeQGtej");
		p(m, "XcwUJYMn", "FRh5M5eb");
		p(m, "NQX9QytB", "fRVfZ7An");
		p(m, "s5qeetUt", "fSaq5Xc5");
		p(m, "E8fhJTkB", "FsKb2SXD");
		p(m, "EG8qr7FQ", "ftGcrgJ5");
		p(m, "zqxRkhMh", "FXEZD466");
		p(m, "qPY9jmQn", "G5vPFZMv");
		p(m, "Tqd2rcUw", "G7vMh5Up");
		p(m, "SNPhkZ9x", "gDkZ6Yjy");
		p(m, "z3f75Pzw", "gfHegqEK");
		p(m, "PFw5WcsN", "gk2jjUxj");
		p(m, "eedN59pG", "GS4E4rjc");
		p(m, "MnPWG2sE", "Gv5SZba6");
		p(m, "xSGj8CCJ", "H4FrjARN");
		p(m, "SfvpeDsZ", "h6qq8j3d");
		p(m, "S9Q8s8mK", "hDfcuwNb");
		p(m, "gjQKfSMK", "hgU5BKzj");
		p(m, "BJ7ry7Xy", "hJ7vaXpa");
		p(m, "B2EQUvUK", "hk2bGvEs");
		p(m, "mDQugQjF", "HkyGsPVW");
		p(m, "jC68B5xH", "hRw74pss");
		p(m, "xeh85W7p", "htbRbpHE");
		p(m, "kkgYTrZA", "j8B9vZKV");
		p(m, "nRmZDZ4P", "JAThv7n5");
		p(m, "utXdduYc", "JHhFr4MZ");
		p(m, "HRjw7StA", "JHhFr4MZ");
		p(m, "byhhUpVt", "jjRtyeJX");
		p(m, "yUv6fy2G", "jqvTEgsj");
		p(m, "dp2dAEXK", "ju3eMb9f");
		p(m, "dhUWGyEr", "ju3eMb9f");
		p(m, "AzUXKUxh", "jZgqAAgd");
		p(m, "fBtSpNB5", "KdNG7gTg");
		p(m, "egvYG3EH", "KHN97EWB");
		p(m, "z4dgqdh7", "kj5DcRvn");
		p(m, "PKTXj8KY", "kj5DcRvn");
		p(m, "S3JVU5DA", "Knr939et");
		p(m, "fQuVMQaB", "KQvWdEGJ");
		p(m, "P6cZBtWA", "kxzmdssa");
		p(m, "vj6VwPNm", "M4668aNC");
		p(m, "dNDcFHKk", "M4JKMQgs");
		p(m, "a8PUyq7h", "mhvvQU5Y");
		p(m, "jE2YMbzn", "MktzTda5");
		p(m, "DPSSR5Ct", "MMKbum8v");
		p(m, "nRhhMNyq", "mPJXHBBA");
		p(m, "V3tunPXS", "mrcZsHxp");
		p(m, "ZPYgJzXS", "n2mcbeKd");
		p(m, "tg47gdmB", "n6pZR8hr");
		p(m, "wEb9T25E", "N7uYVnXD");
		p(m, "CEwR7jQ6", "n9AM2ZZp");
		p(m, "H9esqDgS", "nApKCj3Q");
		p(m, "vRZPFWRF", "NAtdGb97");
		p(m, "yymxwyXY", "NCUhVHyQ");
		p(m, "V3quhr5j", "nfdBbPR3");
		p(m, "fXyXQuzC", "nJQsTnWe");
		p(m, "Xc3HPer6", "nKQmSnDW");
		p(m, "d5zBHpQQ", "nQh9SVzk");
		p(m, "nPX6JnVB", "nRcKbSWn");
		p(m, "SDp9E8ju", "NSQmZf7q");
		p(m, "eJscxddu", "NyKKeMqQ");
		p(m, "VMdmxAHY", "NyKKeMqQ");
		p(m, "SXGSG2aG", "nzYQGDxb");
		p(m, "p6jFu64r", "nzYQGDxb");
		p(m, "Cmp8mz4r", "P7pydzZB");
		p(m, "UQuERu6W", "Pa3zXzkG");
		p(m, "gNRbAbXr", "pbczUxkY");
		p(m, "kTS43jS3", "pbczUxkY");
		p(m, "A2mu9Qxw", "PHQxsCFp");
		p(m, "CXWkJGQ7", "PHTsMPDY");
		p(m, "seSvADnd", "PHTsMPDY");
		p(m, "AMYzD3p6", "PjUFDcvs");
		p(m, "pmz2YVw7", "PKDtR4vP");
		p(m, "TXFBp4vy", "PpsP3Knm");
		p(m, "udz9xEmw", "PUEGS43F");
		p(m, "a6hEf5dn", "pxu4G7CK");
		p(m, "Jk2MsPMC", "pxu4G7CK");
		p(m, "ukYGBvyP", "PzNWqA29");
		p(m, "u7h5DZta", "Q8wrcAWc");
		p(m, "X8MHxKdD", "Q9ebERR6");
		p(m, "rtuMM3U3", "QAhNg5Su");
		p(m, "NcdzY5EY", "QckPMg3z");
		p(m, "zvz6aT7j", "qh5jDKmX");
		p(m, "TBdsH6rd", "qK3cg6yG");
		p(m, "nvHTHDG9", "qwAeDr23");
		p(m, "HnMhxxHN", "QWj2ptWS");
		p(m, "vzKc68nA", "qZFUSEuA");
		p(m, "ThbT68Z6", "R3HT5raN");
		p(m, "hRvkvXnZ", "rdMgwh9G");
		p(m, "U85HM3Dj", "reYPNn3V");
		p(m, "xQZa7YFw", "RKQc269k");
		p(m, "JXZvTbrs", "RkwpHbqZ");
		p(m, "zPnTaRpC", "Rp9u74RP");
		p(m, "gJhtYMG8", "RQ58fzCE");
		p(m, "CRsJsTJC", "rs8nUEhw");
		p(m, "CEwAQqyB", "RUtdpQmN");
		p(m, "J3jxejjs", "rXW6mvdC");
		p(m, "qCP8VRJh", "s474FaxY");
		p(m, "PnjNTKdm", "S63h3qsk");
		p(m, "WYP5y4J2", "S63h3qsk");
		p(m, "b3ugfRaB", "SdP7kx2v");
		p(m, "TzytcWvc", "SHCGnsTR");
		p(m, "rzBmfdrt", "sJqu2Cmd");
		p(m, "WnU8HQFY", "SKRXxYVQ");
		p(m, "kSDenM5T", "SKRXxYVQ");
		p(m, "tAKUJf5x", "SRzKTywa");
		p(m, "eJbe54Cy", "STkyu8GK");
		p(m, "kWHRQy6R", "Szb8xcvK");
		p(m, "Hu7RPBuj", "TAPZhcqg");
		p(m, "W8h7q6PX", "tbmsMsJZ");
		p(m, "ZV2zqqAq", "TCwmTXuu");
		p(m, "vGdEJXEM", "TdyBxNRW");
		p(m, "b3ugfRaB", "TdYVZANp");
		p(m, "rRAyyM7s", "thAERnHY");
		p(m, "AXP59f3t", "TVkP9mHe");
		p(m, "v88Nbjfz", "TWWhGXmq");
		p(m, "GUq4waVQ", "TxETsBWF");
		p(m, "kWTTG8Pm", "TynXHZv3");
		p(m, "TUfk2E6m", "U4U26bKX");
		p(m, "RwJxd6YH", "u5nkDXDy");
		p(m, "fZ5VV2tQ", "U7wJUpeD");
		p(m, "xEYJ73Gc", "ud5nTe4s");
		p(m, "dwmdKmRw", "uG4wj6fW");
		p(m, "ygM4x3Hr", "uK8n8GmN");
		p(m, "Adjw4Jet", "um8Ybrwj");
		p(m, "rWT48xkX", "UseSMZSp");
		p(m, "wcBmnrYd", "V7bWnzSb");
		p(m, "NTesB8Vu", "vA4jWCZ6");
		p(m, "FPurKUwD", "vNFKQhe6");
		p(m, "ffGRZP6v", "vpJpvqhP");
		p(m, "HdJ5B7Rq", "Vq2xgxdM");
		p(m, "AQDuZ9ks", "VVZzNwgg");
		p(m, "VTbwtXXP", "VxJyMV9m");
		p(m, "yvCJPSDf", "VxJyMV9m");
		p(m, "gytYfJfg", "Vxv48Gtz");
		p(m, "tD7JhEm4", "VZ4fhUTJ");
		p(m, "dVZK5YPG", "w4byKZfT");
		p(m, "aDTN2mNe", "wAyrpfgX");
		p(m, "NzwgSy8w", "WCm4Y6dK");
		p(m, "BsyWefcw", "WfP5JSmE");
		p(m, "wAGQuQbp", "wh5qyme4");
		p(m, "ZEbMPt7k", "wJWDB3pw");
		p(m, "rVpxk6zu", "wJWDB3pw");
		p(m, "xyr9pZ5t", "wJWDB3pw");
		p(m, "jr7ENmhT", "wJWDB3pw");
		p(m, "wUuhZZJF", "WPf5a2QJ");
		p(m, "A8u75hsF", "WtaGDhSN");
		p(m, "vyGR4keW", "wYYvZH7A");
		p(m, "xXqj7kgh", "xbEUGnx8");
		p(m, "BEebbVfd", "XF2dwtWK");
		p(m, "ZH8SrUag", "XFx3bD7h");
		p(m, "ZUAurPsA", "Xh4Pb4Cc");
		p(m, "nCm99hnm", "xkZmC8j8");
		p(m, "Wen8mQ3t", "XpEZQfqp");
		p(m, "pxzXQkq7", "XsnFAdq7");
		p(m, "BmAYNy7U", "Y6dfR3GW");
		p(m, "Vr7EB74G", "yA3xMKvH");
		p(m, "gvxVk8Tk", "ycJSQpXT");
		p(m, "D2kNrZtN", "YF2EDCTt");
		p(m, "XZZzQwjB", "yNJ6guSn");
		p(m, "J7TGfuf7", "yrZKQ9r8");
		p(m, "mrw6CyJ5", "z3hY3qBZ");
		p(m, "rcyPBeJn", "Z6Bh9TNV");
		p(m, "QfTu4N4N", "ZGMRnGbq");
		p(m, "nASKAEs7", "ZgZrzwkN");
		p(m, "SEFDbVhy", "zHN6hAGH");
		p(m, "Th3f7Bta", "ZpexxzFy");
		p(m, "MJgSS6yZ", "zPHYaa2F");
		p(m, "pMkH9ek9", "zPHYaa2F");
		p(m, "MthraHB2", "ZxqRvGWK");
		p(m, "B6eKWNAF", "zxRa9YuY");
		for (String s : ord) {
			System.out.println(m.get(s) + "\thttps://ggbm.at/" + m.get(s));
		}
	}

	private void p(HashMap<String, String> m, String string, String string2) {
		m.put(string2, string);

	}

}
