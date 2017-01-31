package org.geogebra.cloud;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	public static final String circleBase64 = "UEsDBBQACAgIAPRtOkoAAAAAAAAAAAAAAAAWAAAAZ2VvZ2VicmFfamF2YXNjcmlwdC5qc0srzUsuyczPU0hPT/LP88zLLNHQVKiuBQBQSwcI1je9uRkAAAAXAAAAUEsDBBQACAgIAPRtOkoAAAAAAAAAAAAAAAAXAAAAZ2VvZ2VicmFfZGVmYXVsdHMyZC54bWztWlFz4jYQfu79Co2f2oeAZTCQTMhN7mY6zUwu1ymZm74KezFqZMm15MPk158s2caEQIFwCUnDA9bKK1n6vtVqvfL5xzxm6Dukkgo+dHDLdRDwQISUR0MnU5OTgfPx4sN5BCKCcUrQRKQxUUPHLzTrdlpqdfBpUYdySc+4uCExyIQEMAqmEJNrERBlVKdKJWft9mw2a1WdtkQataNItXIZOkgPiMuhUxbOdHdLjWYdo+65Lm7//eXadn9CuVSEB+AgPdgQJiRjSuoiMIiBK6TmCQydScaDYhQ330jqIEbGwIYOzxhzUNlm6PR85+LDL+dyKmZIjP+BQNepNINa3wjtQkff/iyYSFE6dDy36yANmUZgbP4JS6ZEl1p932ozMocUfSesuG1qSKZEYDowtRPCJFS6+lFfRAj2TrfU5zQ2KCKpINFsOUgmAKEp2RnqByf6QYa9Rn9UjqYkgQfPOW+X8KwAxahU9YSvjVADhDvuKkK2z80QuQYg7D6E6KSwuiOFSD+HchipOQOkpjS44yC1cXqNzorCHzQMoVg/tk0iKFcjel/25jdrTVem+gRvJiERbB4JXqP6ZyXXVPQtE/sMcVf7xn7H0OfjFQPXjJof7p66GPewd6xkboa6QHEJa1uxABs/G9juGlfy4stkE4LauBvwaQn9OkkBfmu6jkc8x45weL7/QoDg3fxGIEQaSpQPnRty46B5eb231x3chLsH8CEkwLUFqiX08V7o9wYG/uIytpe3CX/nUPB/bbpo7Q33creeb3fL4vpu9lvhfsX/gogubZC4847+M6G/bPXdd0ffwNyoWHRl8T90AhEnDPIDUsJEkMlFzG6lmpDB03feowpE1tNBspwyStL5ASL5TS9IjWDxejlQ9P43WG9h+vuEy/AvX1osVK8VGlC1mRIJUSHVKI8qeUHMfgHQOzEVMSJTrBjDFVeQSjC5FLny8DuA5FY3/spvU8JlkTGyOhW86zncJkHj7benr99bvFeYoKmAWgvS0zfgV2Lqh/TqKZlvch/7ZQZfIaav1X18132KheP4Voo1g49lLt8Z3IHBRwJZkiqQlPDNzCjIFzvzrREaWaFjpWUsBcsUjIIUgFdHGKhA1TWY/kdIHghOg3oSn61UT7t7rLM+AhetY0AaAbfrWWrE3fJsau7auaL7qibHZc0clzX3ZcH0o6eS0hxdVu0uK/VLryp0qkK3KvgNQPcLRg31iV4bjf3kgTPq7heNvoVE/Bs2iWfY5TQX0cJKrqzUyPJavzKheoCcxLpBSQX/RIK7KBUZD1eGcxgvdMQHeevh5FkMacNP31RyDalvEdUdZlUupproNp55zbJcj4hkNNTwxVTb/ol+U4lJXmSF0OP7UcXXjIZqWmQfjMFOaF6AZW9NRUrvBVe1faGC70tmTt+bFO+zLB8ypSf85NMkwiO2MMFLKy34sDl1o/QwQfY4TU1E3RLQXssbdPDA77h93D/1B70tAcaDJwB8sEPzn+jtdzKCypjToHGS4q6zDHfQ93q9bs/zT0/7uNftP9lSxkIwIIv34E+V3DiBWVm+6xzf9oT8xPgrmEJwNxb5koXtlgz4va5YfNbyMtFmw7W8ZGTR2TYf0G58O9Suvk+6+AFQSwcIRFSh68IEAAAmJQAAUEsDBBQACAgIAPRtOkoAAAAAAAAAAAAAAAAXAAAAZ2VvZ2VicmFfZGVmYXVsdHMzZC54bWztV81OGzEQPpensHwna+9PICgLiuihlQC14tKrsztJ3G7sZe38LK/Wd+gz1eu1w0IIFVGoqooc8OfxeOx833iGDC/W8wItoVJcihTTHsEIRCZzLqYpXujJ8Sm+OD8aTkFOYVwxNJHVnOkUJ43nZp+Z9SI6aGxorfiZkDdsDqpkGdxmM5izK5kxbV1nWpdnQbBarXo+aE9W02A61b21yjEyFxIqxQ6cmXCPNq0i6x4SQoNv11dt+GMulGYiA4zMZXOYsEWhlYFQwByERrouIcVlwQRE5oyCjaFI8Rc7/4iR25HiyMTF50cfhmomV0iOv0NmrLpawGaTnQSNj1m+lIWsUJXiwQAjw1hIzTh2IyvKGUsx6SWtf8FqqNCSmSCktbCFlpkNYa0TVijwvuawa5lDuxK3Vq5uZ6yELe9MyipXaN0cZjSo3XjvxlU7WtcJa7R1t+hRdxoXcKvrApCe8eyHAKWaOzraHPjE8xyaJGn2DANH7RbJmRQ865D8WWiTJoZHIz/KFtUSunzTZD++wySxhNPwxBJOOnRT0n5oPCCU9ml4KPqZ4HObxkhpKBsmkCoBcos2XJksq+3z6cTbJdyzxIcvEf9hCHwKYmmIkpUyz424Z1sTr7q3rKnPB+os9w7YOOarVHyNRn7fyLuPQg8iD2IPkk4qwZ1o766avynm87LgGdcv5wdbc9VJj1EzffQEKYn2SgliE4JspQP5z+R/wnrJKlNrjZSZObvFYNj49fMPz7R5iRmrNCjOREeQy2bhqSL9d0VeXwlLWdQzyCspHjpOx/TAb+Sazj7p8FpNaBJZVRK6JUvs6mYy6JO4Hx+sax1apd2M3y1Ybh+Co+Crn3e5pvs1HBLvyOaTN2/vb9UlyO4u0S75RlB7cB8eqIGgUd+DEw9OPRhsisRuqdWimph/L5+rYG7pserxP6v6XypidL8iJkBvOLppcJfU5L1s7Ve2gs5vlMD/Djr/DVBLBwgBfmsO3gIAAI4NAABQSwMEFAAICAgA9G06SgAAAAAAAAAAAAAAAAwAAABnZW9nZWJyYS54bWzdGF1v2zbwuf0VBz3HNkmJkl3YLdy+bEBbFMs2DHujJcbmIkuaSMd20B+/O1Ky5aTd1qTYw9KoJE/H+/5S5m8O2xLudGtNXS0iPmYR6CqvC1OtF9HO3Yym0ZvXL+drXa/1qlVwU7db5RaRJMzTPTyNYz4jmCnwmKf6Jo+nIyayZJTM+GqkEiVH0yQreC5kJrMiAjhY86qqP6qtto3K9XW+0Vv1vs6V80Q3zjWvJpP9fj/u2Y/rdj1Zr1fjg0UCKHplF1G3eYXkLi7tY48uGOOT3z68D+RHprJOVbmOgNTamdcvX8z3pirqPexN4TaLaMpQjY026w3qmdJhQkgNKtvo3Jk7bfHq4Oh1dtsm8miqovcvwg7KkzoRFObOFLpdRGwsZAR1a3Tlure84zLp78/vjN4HQrTzPBI2y9DqxppVqRfRjSot6mGqmxZtiCK0Ozxadyz1SrX9+SwBv/L/EMXca6KGqgXV8R1jV/Rk+EjZ6TxgLbmIwNV16Skz+AwcJMMH+AyuIM0QIoBLSBAyRUgGMcEkTyAGQuExJAmuCYF5Su8k3pcMOEcwCAZCgOAgYjxKCTIFmdFFgbjpzBNj+BA2ioNPTLA4xsfD4gQfQTskJAMZFELGqd9Jwkb6UpD4HhhPIZkhIwLIjEOMMuA5Y4AUYyLPvRIJA/rlkBB5kYGYAtJDvYkyE3/jlO589koHeOCW3inyS07BILxK2GOnJJcuQQ8w1O2KFh4WEjdNwysWYCwOiwhLEhYZcJJwPQmoQVuWBJwkfq6avZLxtyg5HSjJSQl0CknvlxhIbu7lpyXpjmk4+lBjnHXQKf03owPaJJ36zTN1ip+kEx9wDVn6LUxPsZJO/z1L8RyWJy3Fl7QU8itaPtO4PVMuB0yRl//1zyOW8Tel4qMC+QSOafKcivwEhhn7LxjOJ33/mXfZB3ZDuF3sOL21VHTiGWQxpFj6+bkppFS2u86QCcgkZOmgP1xRh0jluUlQi5heNAk5HXQKbBMpATPPAXlSnQ9dQyR947jqWsfnR60DK31yLvYoIJHiANiZfBHoqz5KIU51X0gq/QLLBLYcASmVna+0gAia2pqTfTe6bE6O8aY0VbNzF+bLt0W/dTViq9KPNx1+Uee3bx8YXCvr+j0i4Zxwnj/C3HAxnryYl2qlS5zirikWAO5Uie6KPP2bunLQx0EaeXJ+EprrXV6awqjqV3R+P4R83G1XugW/rUlFT4SuQz8y+UrUj0yJiANKXtdtcX20GCtw+F23eFlwPzgewymm0/AHbWlzVfo+6PEGp+EPTwIHfXetnUMtLaiDtr2B1i0lSucCOvxo39blGdTUpnLvVON2rZ9zsby1JPuyWpfaG8x7EgfG/HZVH65D6UsDrZ+PjT6ZcrV+V5d1C5hqQuJMt+7WVVg9Dkl2wmIeh3mMjgYRPb3nM+Ex/LoKq8dCXwbROk15rybruRgL4XwROD4OaPrcVca97w/O5LdnRQk/eLk34SVJ/p1IzicPAmx+q9tKlyFYKnTkrt7ZEKvBVV6OndWflNssq+InvcY0+6So2DkkHVDPEhc6N1u8GOCd5RR59RcUNUALvW51r2HIu2DXLkXANq1Whd1o7U7WDaE8RPPq9OLPbd6ahgIRVlhsb/U51gpjFZbqYqAR6WpR6JxqBtrNkc0iUDu3qVv/oaAcQSghS73FjwRwPuh83J7sv/TfG2RoqFd/YOo/8M/ZLPj6iwHoQ1WVzUbRN0mnaqmOlO2DeuHpfagLfQFVFZraa4AJ3gSnN1qHcAny4qZBcj7HBv409nqjGv3Iz97IFg6LaMTHAivKkb5IMe3uw5dp+AwjG1BGhutyCH3kn858/2DIt/9rQ868HePxNPkuhszr7VZVBVR+3nhn2rzU0bnLKUaBCYqTWYPNdq5/kQdiHYlHXsG0MPnJ6vmzvcKe7pOvWfZchd0G6x1+qls/WruuKfjND6YodHXqlNgWdXWHCtRYsODAuj+zHFmQC+57yAGNNgp9j3egez7wGEZJaw6w7PGXPdZShJRhsZydfzCBlnHHY5kQKuXUUnrctJPtzyqoY0PlphHC3Jj8odcnw5Ll54XuLzKv/wJQSwcIDHgjwiEGAABCEgAAUEsBAhQAFAAICAgA9G06StY3vbkZAAAAFwAAABYAAAAAAAAAAAAAAAAAAAAAAGdlb2dlYnJhX2phdmFzY3JpcHQuanNQSwECFAAUAAgICAD0bTpKRFSh68IEAAAmJQAAFwAAAAAAAAAAAAAAAABdAAAAZ2VvZ2VicmFfZGVmYXVsdHMyZC54bWxQSwECFAAUAAgICAD0bTpKAX5rDt4CAACODQAAFwAAAAAAAAAAAAAAAABkBQAAZ2VvZ2VicmFfZGVmYXVsdHMzZC54bWxQSwECFAAUAAgICAD0bTpKDHgjwiEGAABCEgAADAAAAAAAAAAAAAAAAACHCAAAZ2VvZ2VicmEueG1sUEsFBgAAAAAEAAQACAEAAOIOAAAAAA==";

	@BeforeClass()
	public static void startLogging() {
		Log.setLogger(new LoggerD());
	}
	@Test
	public void testSearch() {

		GeoGebraTubeAPID api = new GeoGebraTubeAPID(true, getClient());
		final ArrayList<String> titles = new ArrayList<String>();
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

		GeoGebraTubeAPID api = new GeoGebraTubeAPID(true, getAuthClient(null));
		final ArrayList<String> titles = new ArrayList<String>();

		api.uploadMaterial(0, "O", "testfile" + new Date() + Math.random(),
					circleBase64,
				new MaterialCallbackI() {

					public void onLoaded(List<Material> result,
							ArrayList<Chapter> meta) {
						if (result.size() > 0) {
							for (Material m : result) {
								titles.add(m.getTitle());
							}
						} else {
							titles.add("FAIL " + result.size());
						}

					}

					public void onError(Throwable exception) {
						exception.printStackTrace();
						Assert.assertNull(exception.getMessage());

					}
				}, MaterialType.ggb);

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

	/**
	 * Upload one material and delete all materials in account.
	 */
	@Test
	public void testDelete() {
		testUpload();// ensure we have st to delete
		final GeoGebraTubeAPID api = new GeoGebraTubeAPID(true,
				getAuthClient(null));
		final ArrayList<String> titles = new ArrayList<String>();

		api.getUsersOwnMaterials(new MaterialCallbackI() {

			public void onLoaded(List<Material> result,
					ArrayList<Chapter> meta) {

				System.out.println(result.size());
				for (Material m : result) {
					final Material mFinal = m;
					api.deleteMaterial(m, new MaterialCallbackI() {

						public void onLoaded(List<Material> result1,
								ArrayList<Chapter> meta1) {

							titles.add(mFinal.getTitle());

						}

						public void onError(Throwable exception) {
							// TODO Auto-generated method stub

						}
					});
					// titles.add(m.getTitle());
				}


			}

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
				System.getProperty("materials.token"));

		user.setUserId(4951854);
		auth.onEvent(new LoginEvent(user, true, true, "{}"));
		client.setModel(auth);
		return client;
	}


}
