package org.geogebra.common.move.ggtapi.models;

import java.util.ArrayList;

import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.util.debug.Log;

public abstract class JSONParserGGT {
	public static JSONParserGGT prototype;
	public abstract ArrayList<Chapter> parseResponse(String response,
			ArrayList<Material> result);

	public Material toMaterial(JSONWrapper obj) {
		Material.MaterialType type = MaterialType.ggb;
		if (getString(obj, "type").length() > 0) {
			try {
				type = MaterialType.valueOf(getString(obj, "type"));
			} catch (Throwable t) {
				Log.error("Unknown material type:" + getString(obj, "type"));
			}
		}
		int ID = getInt(obj, "id", -1);

		Material material = new Material(ID, type);

		material.setTitle(getString(obj, "title"));
		material.setDescription(getString(obj, "description"));
		if (getString(obj, "timestamp") != "") {
			material.setTimestamp(Long.parseLong(getString(obj, "timestamp")));
		}
		if (getString(obj, "modified") != "") {
			material.setModified(Long.parseLong(getString(obj, "modified")));
		}
		if (getString(obj, "syncstamp") != "") {
			material.setSyncStamp(Long.parseLong(getString(obj, "syncstamp")));
		}
		material.setVisibility(getString(obj, "visibility"));
		material.setAuthor(getString(obj, "author"));
		material.setAuthorId(getInt(obj, "author_id", -1));
		material.setURL(getString(obj, "url"));
		material.setURLdirect(getString(obj, "url_direct"));
		material.setThumbnail(getString(obj, "thumbnail"));
		material.setLanguage(getString(obj, "language"));
		material.setFeatured(Boolean.parseBoolean(getString(obj, "featured")));
		material.setLikes(getInt(obj, "likes", -1));
		material.setHeight(getInt(obj, "height", 600));
		material.setWidth(getInt(obj, "width", 800));
		material.setInstructionsPost(getString(obj, "instructions_post"));
		material.setInstructionsPre(getString(obj, "instructions_pre"));
		material.setShowToolbar(getBoolean(obj, "toolbar", false));
		material.setShowMenu(getBoolean(obj, "menubar", false));
		material.setShowInputbar(getBoolean(obj, "inputbar", false));
		material.setFavorite(getBoolean(obj, "favorite", false));
		material.setShiftDragZoom(getBoolean(obj, "shiftdragzoom", false));
		material.setShowResetIcon(getBoolean(obj, "reseticon", false));
		material.setBase64(getString(obj, "ggbBase64"));
		material.setDeleted(getBoolean(obj, "deleted", false));
		material.setFromAnotherDevice(
				getBoolean(obj, "from_another_device", false));
		return material;
	}

	public boolean getBoolean(JSONWrapper obj, String string, boolean def) {
		if (obj.has(string) && obj.get(string) instanceof Boolean) {
			return ((Boolean) obj.get(string)).booleanValue();
		}
		if (!obj.has(string) || obj.get(string) == null
				|| obj.get(string).toString() == null
				|| "".equals(obj.get(string).toString())) {
			return def;
		}
		return Boolean.parseBoolean(obj.get(string).toString());
	}

	public String getString(JSONWrapper obj, String string) {
		if (!obj.has(string)) {
			return "";
		}
		if (obj.get(string) == null) {
			return "";
		}
		return obj.get(string).toString();
	}

	public int getInt(JSONWrapper obj, String string, int def) {
		if (!obj.has(string) || obj.get(string) == null
				|| "".equals(obj.get(string).toString())) {
			return def;
		}
		return Integer.parseInt(obj.get(string).toString());
	}

	public long getLong(JSONWrapper obj, String string, long def) {
		if (!obj.has(string) || obj.get(string) == null
				|| "".equals(obj.get(string).toString())) {
			return def;
		}
		return Long.parseLong(obj.get(string).toString());
	}

	public void addEvent(JSONWrapper object, ArrayList<SyncEvent> events) {

		SyncEvent se = new SyncEvent(
				getInt(object, "id", 0), getLong(object, "ts", 0));
		if (object.get("deleted") != null
				&& object.get("deleted") instanceof String) {
			se.setDelete(true);
		}
		if (object.get("favorite") != null
				&& getBoolean(object, "favorite", false)) {
			se.setFavorite(true);
		}
		if (object.get("unfavorited") != null
				&& object.get("unfavorited") instanceof String) {
			se.setUnfavorite(true);
		}
		events.add(se);

	}
}
