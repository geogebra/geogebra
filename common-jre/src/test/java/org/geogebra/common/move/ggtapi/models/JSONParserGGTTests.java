package org.geogebra.common.move.ggtapi.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.geogebra.common.move.ggtapi.models.json.JSONArray;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;
import org.junit.Test;

public class JSONParserGGTTests {

	@Test
	public void testToMaterialTubeAPI() throws Exception {
		String json = getContentsOf("tube-3d.json");
		assertNotNull(json);
		JSONObject root = new JSONObject(json);
		JSONObject responses = root.getJSONObject("responses");
		JSONObject response = responses.getJSONObject("response");
		JSONObject item = response.getJSONObject("item");
		Material material = JSONParserGGT.prototype.toMaterial(item);
		assertEquals("PB9Npbe7", material.getSharingKeyOrId());
		assertEquals("3D Coordinate Systems", material.getTitle());
		assertEquals("https://ggbm.at/PB9Npbe7", material.getURL());
		assertFalse(material.hasCas());
		assertTrue(material.has3d());
	}

	@Test
	public void testToMaterialGeoAPI() throws Exception {
		String json = getContentsOf("geoapi-3d.json");
		assertNotNull(json);
		JSONObject root = new JSONObject(json);
		JSONArray elements = root.getJSONArray("elements");
		JSONObject item = elements.getJSONObject(1);
		Material material = JSONParserGGT.prototype.toMaterial(item);
		assertEquals("2463659", material.getSharingKeyOrId());
		assertEquals("3D Coordinate Systems", material.getTitle());
		assertEquals("https://www.geogebra.org/resource/Xsjejd9Q/Sse8BEEfloHR17hz/material-Xsjejd9Q.ggb", material.getURL());
		assertFalse(material.hasCas());
		assertTrue(material.has3d());
	}

	private String getContentsOf(String testResourcesFileName) throws Exception {
		URL url = getClass().getClassLoader().getResource(testResourcesFileName);
		byte[] bytes = Files.readAllBytes(Paths.get(url.toURI()));
		return new String(bytes, Charset.defaultCharset());
	}
}
