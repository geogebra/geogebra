package org.geogebra.common.move.ggtapi.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.junit.Test;

public class JSONParserGGTTests {

	// Sample response: POST https://www.geogebra.org/api/json.php (post body?)
	@Test
	public void testToMaterial_TubeAPI_ggb() throws Exception {
		String json = getContentsOf("tube-3d.json");
		assertNotNull(json);
		JSONObject root = new JSONObject(json);
		JSONObject responses = root.getJSONObject("responses");
		JSONObject response = responses.getJSONObject("response");
		JSONObject item = response.getJSONObject("item");
		Material material = JSONParserGGT.prototype.toMaterial(item);
		assertSame(Material.MaterialType.ggb, material.getType());
		assertEquals("PB9Npbe7", material.getSharingKeySafe());
		assertEquals("3D Coordinate Systems", material.getTitle());
		assertEquals("Dr. Doug Davis, 3D", material.getAuthor());
		assertNotNull(material.getDescription());
		assertEquals(Material.MaterialType.ggb, material.getType());
		assertEquals("O", material.getVisibility());
		assertEquals("https://ggbm.at/PB9Npbe7", material.getURL());
		assertEquals("https://cdn.geogebra.org/resource/Xsjejd9Q/Sse8BEEfloHR17hz/material-Xsjejd9Q.png", material.getPreviewURL());
		// for Tube API, this is the base64-encoded thumbnail image
		assertNotNull(material.getThumbnail());
		assertTrue(material.thumbnailIsBase64());
		assertFalse(material.hasCas());
		assertTrue(material.has3d());
	}

	// Sample response: GET https://api.geogebra.org/v1.0/materials/PB9Npbe7
	@Test
	public void testToMaterial_GeoAPI_ws() throws Exception {
		String json = getContentsOf("geoapi-3d.json");
		assertNotNull(json);
		JSONObject root = new JSONObject(json);
		Material material = JSONParserGGT.prototype.toMaterial(root);
		assertEquals("PB9Npbe7", material.getSharingKeySafe());
		assertSame(Material.MaterialType.ws, material.getType());
		assertTrue(material.isDeleted());
		assertEquals("3D Coordinate Systems", material.getTitle());
		assertEquals("O", material.getVisibility());
		assertEquals("https://www.geogebra.org/resource/Xsjejd9Q/Sse8BEEfloHR17hz/material-Xsjejd9Q-thumb.png", material.getThumbnail());
	}

	// Sample response: https://api.geogebra.org/v1.0/materials/gfnbcfxx
	@Test
	public void testToMaterial_GeoAPI_ws_NoThumbUrl() throws Exception {
		String json = getContentsOf("geoapi-nothumb.json");
		assertNotNull(json);
		JSONObject root = new JSONObject(json);
		Material material = JSONParserGGT.prototype.toMaterial(root);
		assertEquals("gfnbcfxx", material.getSharingKeySafe());
		assertSame(Material.MaterialType.ws, material.getType());
		assertEquals("Empty", material.getTitle());
		assertEquals("S", material.getVisibility());
		assertEquals("", material.getThumbnail());
	}

	// Sample response: GET https://api.geogebra.org/v1.0/materials/PB9Npbe7
	// extract an element where "type": "G"
	@Test
	public void testToMaterial_GeoAPI_G() throws Exception {
		String json = getContentsOf("geoapi-3d.json");
		assertNotNull(json);
		JSONObject root = new JSONObject(json);
		Material parent = JSONParserGGT.prototype.toMaterial(root);
		JSONArray elements = root.getJSONArray("elements");
		JSONObject item = elements.getJSONObject(1);
		Material material = JSONParserGGT.prototype.worksheetToMaterial(parent, item);
		assertEquals("PB9Npbe7", parent.getSharingKeySafe());
		assertSame(Material.MaterialType.ggb, material.getType());
		assertEquals("3D Coordinate Systems", material.getTitle());
		assertEquals("https://www.geogebra.org/resource/Xsjejd9Q/Sse8BEEfloHR17hz/material-Xsjejd9Q.ggb", material.getURL());
		assertEquals("https://www.geogebra.org/resource/Xsjejd9Q/Sse8BEEfloHR17hz/material-Xsjejd9Q-thumb$1.png", material.getThumbnail());
		assertTrue(material.getUndoRedo());
		assertFalse(material.hasCas());
		assertTrue(material.has3d());
	}

	// Sample response: GET http://tafel.dlb-dev01.alp-dlg.net/api/users/18/materials?format=page&type=all&limit=50&offset=0&embed=creator&order=-modified
	// For more info, see https://git.geogebra.org/doc/general/-/wikis/MOW
	@Test
	public void testToMaterial_MowAPI() throws Exception {
		String json = getContentsOf("mow.json");
		assertNotNull(json);
		JSONObject root = new JSONObject(json);
		Material material = JSONParserGGT.prototype.toMaterial(root);
		assertEquals("kgmqpmpf", material.getSharingKeySafe());
		assertSame(Material.MaterialType.ggs, material.getType());
		assertTrue(material.isMultiuser());
		assertTrue(material.isSharedWithGroup());
		assertEquals("multipage", material.getTitle());
		assertEquals("", material.getURL());
		assertEquals("http://tafel.dlb-dev01.alp-dlg.net/files/k/kg/kgmqpmpf/4k1XSuRpjVWnqCSu/kgmqpmpf.ggs", material.getFileName());
		assertEquals("http://tafel.dlb-dev01.alp-dlg.net/files/k/kg/kgmqpmpf/4k1XSuRpjVWnqCSu/kgmqpmpf-thumb.png", material.getThumbnail());
	}

	private String getContentsOf(String testResourcesFileName) throws Exception {
		URL url = getClass().getClassLoader().getResource(testResourcesFileName);
		byte[] bytes = Files.readAllBytes(Paths.get(url.toURI()));
		return new String(bytes, Charset.defaultCharset());
	}
}
