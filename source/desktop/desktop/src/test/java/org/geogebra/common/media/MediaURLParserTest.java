package org.geogebra.common.media;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.util.AsyncOperation;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Test;

public class MediaURLParserTest {
	protected static final String MEBIS_REGEX = "https://mediathek.mebis.bayern.de/\\?doc=provideVideo&identifier=[BYWS\\-0-9]+&type=video(&)?(#t=[0-9,]+)?";
	private static AsyncOperation<VideoURL> INVALID = obj -> Assert.assertFalse(obj.isValid());

	private static AsyncOperation<VideoURL> validYT(final String id) {
		return valid(MediaFormat.VIDEO_YOUTUBE, id);
	}

	private static AsyncOperation<VideoURL> validMP4() {
		return valid(MediaFormat.VIDEO_HTML5, null);
	}

	private static AsyncOperation<VideoURL> validMebis() {
		return valid(MediaFormat.VIDEO_MEBIS, null);
	}

	private static AsyncOperation<VideoURL> valid(final MediaFormat fmt,
			final String id) {
		return obj -> {
			assertTrue(obj.isValid());
			assertEquals(fmt, obj.getFormat());
			if (fmt == MediaFormat.VIDEO_YOUTUBE) {
				assertEquals(id,
					MediaURLParser.getYouTubeId(obj.getUrl()));
			}
			if (fmt == MediaFormat.VIDEO_MEBIS) {
				assertThat(obj.getUrl(),
						new TypeSafeMatcher<String>() {

							@Override
							public void describeTo(
									Description description) {
								description.appendText("Valid Mebis URL");
							}

							@Override
							public boolean matchesSafely(String item) {
								return item.matches(MEBIS_REGEX);
							}
						});
			}
		};
	}

	@Test
	public void checkYoutubeUrls() {
		checkVideo("https://youtu.be/bdRUiXUrYIs",
				validYT("bdRUiXUrYIs"));
		checkVideo(
				"https://www.youtube.com/watch?v=bdRUiXUrYIs&feature=youtu.be",
				validYT("bdRUiXUrYIs"));
		checkVideo(
				"https://www.youtube.com/watch?spam&v=bdRUiXUrYIs&feature=youtu.be&spam",
				validYT("bdRUiXUrYIs"));
		checkVideo("https://youtu.be/", INVALID);
		checkVideo("https://youtu.be/?& &&", INVALID);
		checkVideo("https://youtube.com/bdRUiXUrYIs", INVALID);
	}

	@Test
	public void checkMp4Urls() {
		checkVideo("https://www.w3schools.com/htmL/mov_bbb.mp4",
				validMP4());
		checkVideo("file.mp4", validMP4());
		checkVideo("file.mp5", INVALID);
		checkVideo("https://example.com/file.mp4", validMP4());
		checkVideo("https://example.com/file.mp5", INVALID);
	}

	@Test
	public void checkMebisUrls() {
		checkVideo(
				"https://mediathek.mebis.bayern.de/?doc=embeddedObject&id=BWS-04985070&type=video&start=178&title=Wetter",
				validMebis());
		checkVideo(
				"https://mediathek.mebis.bayern.de/?doc=provideVideo&identifier=BWS-04985070&type=video&start=0&title=Wetter&file=default.mp4",
				validMebis());
		checkVideo(
				"https://mediathek.mebis.bayern.de/?doc=record&identifier=BWS-04985070",
				validMebis());
		checkVideo(
				"https://mediathek.mebis.bayern.de/?doc=record&identifier=BY-04985070",
				validMebis());
		checkVideo(
				"https://mediathek.mebis.bayern.de/?doc=provideVideo&identifier=BY-00072140&type=video&#t=60,120",
				validMebis());
		checkVideo(
				"https://mediathek.mebis.bayern.de/?doc=provideVideo&identifier=BY-00072140&type=video#t=60,120",
				validMebis());
		checkVideo(
				"https://mediathek.mebis.bayern.de/index.php?doc=provideVideo&identifier=BWS-04980092&type=video&start=0&title=Das Eichhornchen&file=default.mp4&restorePosted",
				validMebis());
		// missing id
		checkVideo(
				"https://mediathek.mebis.bayern.de/?doc=provideVideo&v=BY-00072140&type=video&#t=60,120",
				INVALID);
		// wrong host
		checkVideo(
				"https://mediathek.bayern.de/?doc=provideVideo&identifier=BY-00072140&type=video&#t=60,120",
				INVALID);
		checkVideo(
				"https://mediathek.mebis.bayern.de/?identifier=BY-00072140",
				INVALID);
		checkVideo("https://mediathek.mebis.bayern.de/?&&f&",
				INVALID);
	}

	@Test
	public void testEmbeddableUrls() {
		assertEquals("https://en.wikipedia.org/wiki/%2B", // this actually exists
				MediaURLParser.toEmbeddableUrl("https://en.wikipedia.org/wiki/+"));
		assertEquals("https://www.bavarikon.de/object/bav:foo?lang=de&mebisembedding=true",
				MediaURLParser.toEmbeddableUrl("https://www.bavarikon.de/object/bav:foo?lang=de"));
		assertEquals("https://www.bavarikon.de/object/bav:foo?mebisembedding=true",
				MediaURLParser.toEmbeddableUrl("https://www.bavarikon.de/object/bav:foo"));
	}

	public static void checkVideo(String url,
			AsyncOperation<VideoURL> callback) {
		callback.callback(MediaURLParser.checkVideo(url));
	}
}
