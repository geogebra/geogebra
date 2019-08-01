package org.geogebra.web.codesize;

import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.geogebra.web.util.file.FileIO;
import org.junit.Test;

import com.google.gwt.dev.util.collect.HashMap;

public class PackageSize {

	private static HashMap<String, JSONObject> packages = new HashMap<>();
	private static final boolean addUnattributed = true;

	@Test
	public void testPackages() {
		try {
			checkModule("webSimple");
			checkModule("web3d");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void checkModule(String string) throws JSONException {
		packages.clear();
		String html = FileIO.load(
				"build/gwt/extra/" + string
						+ "/soycReport/compile-report/initial-0-packageBreakdown.html");
		if (html == null) {
			return;
		}
		String[] table = html
				.substring(html.indexOf("<tr"), html.lastIndexOf("/tr>"))
				.split("<tr");
		int total = 0;
		for (String row : table) {
			String[] cells = row.split("<td");
			if (cells.length > 3) {
				try {
					addPackage(
							cells[1].substring(cells[1].indexOf(">", 7) + 1,
									cells[1].indexOf("</a")),
							cells[3].replaceAll("[^0-9]", ""));
					total += Integer
							.parseInt(cells[3].replaceAll("[^0-9]", ""));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		updateSelfSizes(packages.get(""));

		if (addUnattributed) {
			html = FileIO.load(
					"build/gwt/extra/" + string
							+ "/soycReport/compile-report/initial-0-codeTypeBreakdown.html");
			String bytes = html.split("<p class=\"soyc-breakdown-strings\">")[2]
					.replaceAll("[^0-9]", "");
			packages.get("").getJSONArray("children").put(new JSONObject()
					.put("name", "Dark matter").put("bytes", bytes));
			total += Integer.parseInt(bytes);
		}
		packages.get("").put("total", total);
		System.out.println(packages.get("").toString());

	}

	private static void updateSelfSizes(JSONObject pkg) throws JSONException {
		if (pkg.has("children")) {
			JSONArray children = pkg.getJSONArray("children");
			for (int i = 0; i < children.length(); i++) {
				updateSelfSizes(children.getJSONObject(i));
			}
			if (pkg.has("bytes")) {
				JSONObject root = new JSONObject();
				root.put("bytes", pkg.get("bytes"));
				root.put("name", pkg.get("name") + "*");
				pkg.remove("bytes");
				pkg.getJSONArray("children").put(root);
			}
		}

	}

	private void addPackage(String name, String size) throws JSONException {
		if (!packages.containsKey(name)) {
			JSONObject self = new JSONObject();
			addParents(name, self);
			packages.put(name, self);
		}
		packages.get(name).put("bytes", size);
	}

	private void addParents(String name, JSONObject self)
			throws JSONException {
		self.put("name", name);
		String parent = name.contains(".")
				? name.substring(0, name.lastIndexOf(".")) : "";
		if (packages.get(parent) == null) {
			JSONObject parentPackage = new JSONObject();
			packages.put(parent, parentPackage);
			packages.get(parent).put("name", parent);
			if (parent.length() > 0) {
				addParents(parent, parentPackage);
			}			
		}
		if (!packages.get(parent).has("children")) {
			packages.get(parent).put("children", new JSONArray());
		}
		packages.get(parent).getJSONArray("children").put(self);
	}
}
