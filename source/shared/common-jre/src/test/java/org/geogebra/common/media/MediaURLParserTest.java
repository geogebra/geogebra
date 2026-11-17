/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/** Tests URL recognition and Mebis normalization used by the video insertion dialog. */
class MediaURLParserTest {

	@ParameterizedTest
	@ValueSource(
			strings = {
				"https://www.youtube.com/watch?v=E4uvbaTR7mw",
				"https://www.youtube.com/watch?feature=share&v=E4uvbaTR7mw&t=60",
				"https://www.youtube.com/v/E4uvbaTR7mw",
				"https://www.youtube.com/embed/E4uvbaTR7mw?start=60",
				"https://youtu.be/E4uvbaTR7mw?si=share",
				"https://youtu.be/E4uvbaTR7mw&feature=share",
				"https://www.youtube.com/embed/E4uvbaTR7mw\" frameborder=\"0\""
			})
	void youtubeFormsShouldRetainSourceAndExtractIdWithoutParameters(@NonNull String input) {
		VideoURL result = MediaURLParser.checkVideo(input);

		assertTrue(result.isValid());
		assertEquals(MediaFormat.VIDEO_YOUTUBE, result.getFormat());
		assertEquals(input, result.getUrl());
		assertEquals("E4uvbaTR7mw", MediaURLParser.getYouTubeId(input));
	}

	@ParameterizedTest
	@ValueSource(
			strings = {
				"https://example.com/video.mp4",
				"https://example.com/video.mp4?token=abc",
				"https://example.com/video.m4v"
			})
	void html5VideoShouldRetainSource(@NonNull String input) {
		VideoURL result = MediaURLParser.checkVideo(input);

		assertTrue(result.isValid());
		assertEquals(MediaFormat.VIDEO_HTML5, result.getFormat());
		assertEquals(input, result.getUrl());
		assertNull(MediaURLParser.getYouTubeId(input));
	}

	@ParameterizedTest
	@ValueSource(
			strings = {
				"",
				"https://example.com",
				"https://example.com/sound.mp3",
				"https://www.youtube.com/watch?v=",
				"https://youtu.be/",
				"https://www.youtube.com/channel/UC123"
			})
	void unsupportedOrMissingVideoSourceShouldBeInvalid(@NonNull String input) {
		VideoURL result = MediaURLParser.checkVideo(input);

		assertFalse(result.isValid());
		assertEquals(MediaFormat.NONE, result.getFormat());
		assertEquals(input, result.getUrl());
	}

	@ParameterizedTest
	@CsvSource(
			delimiter = '|',
			value = {
				"doc=record&identifier=BY-1 | BY-1",
				"doc=provideVideo&identifier=BY-2&type=video | BY-2",
				"doc=embeddedObject&id=BY-3&type=video | BY-3"
			})
	void mebisFormsShouldNormalizeToPlayableVideoUrl(
			@NonNull String query, @NonNull String identifier) {
		String input = MediaURLParser.MEBIS_BASE_URL + "?" + query;
		MebisURL result = assertInstanceOf(MebisURL.class, MediaURLParser.checkVideo(input));

		assertTrue(result.isValid());
		assertEquals(MediaFormat.VIDEO_MEBIS, result.getFormat());
		assertEquals(MebisError.NONE, result.getError());
		assertEquals(
				MediaURLParser.MEBIS_BASE_URL + "?doc=provideVideo&identifier=" + identifier
						+ "&type=video",
				result.getUrl());
		assertEquals(identifier, MediaURLParser.getMebisId(input));
	}

	@ParameterizedTest
	@CsvSource(
			delimiter = '|',
			value = {
				"doc=unknown&identifier=BY-1&type=video | DOC",
				"identifier=BY-1&type=video | DOC",
				"doc=provideVideo&identifier=BY-1 | TYPE",
				"doc=provideVideo&identifier=BY-1&type=audio | TYPE",
				"doc=embeddedObject&id=BY-1 | TYPE",
				"doc=provideVideo&type=video | ID",
				"doc=embeddedObject&type=video | ID",
				"doc=record | ID"
			})
	void malformedMebisUrlShouldRetainSpecificValidationError(
			@NonNull String query, @NonNull MebisError expectedError) {
		MebisURL result = assertInstanceOf(
				MebisURL.class, MediaURLParser.checkVideo(MediaURLParser.MEBIS_BASE_URL + "?" + query));

		assertFalse(result.isValid());
		assertEquals(MediaFormat.VIDEO_MEBIS, result.getFormat());
		assertEquals(expectedError, result.getError());
		assertNull(result.getUrl());
	}

	@ParameterizedTest
	@CsvSource(
			delimiter = '|',
			value = {
				"#t=60 | #t=60", "#t=60,120 | #t=60,120",
				"#t=60,120&ignored=1 | #t=60,120", "#t=invalid | ''"
			})
	void mebisNormalizationShouldKeepOnlyValidTimeFragments(
			@NonNull String fragment, @NonNull String expectedFragment) {
		String canonical =
				MediaURLParser.MEBIS_BASE_URL + "?doc=provideVideo&identifier=BY-1&type=video";
		VideoURL result = MediaURLParser.checkVideo(canonical + fragment);

		assertTrue(result.isValid());
		assertEquals(canonical + expectedFragment, result.getUrl());
	}

	@ParameterizedTest
	@ValueSource(
			strings = {
				"https://www.youtube.com/watch?v=E4uvbaTR7mw&origin=https://example.com?x=1",
				"https://www.youtube.com/embed/E4uvbaTR7mw\" title=\"What?\"",
				"https://www.youtube.com/embed/E4uvbaTR7mw\" title=\"A&B\""
			})
	@Issue("APPS-7919")
	void youtubeIdShouldEndAtEarliestDelimiter(@NonNull String input) {
		assertEquals("E4uvbaTR7mw", MediaURLParser.getYouTubeId(input));
		VideoURL result = MediaURLParser.checkVideo(input);
		assertTrue(result.isValid());
		assertEquals(MediaFormat.VIDEO_YOUTUBE, result.getFormat());
		assertEquals(input, result.getUrl());
	}

	@ParameterizedTest
	@ValueSource(
			strings = {
				"doc=record&identifier=",
				"doc=provideVideo&identifier=&type=video",
				"doc=embeddedObject&id=&type=video"
			})
	@Issue("APPS-7919")
	void emptyMebisIdentifierShouldBeInvalid(@NonNull String query) {
		MebisURL result = assertInstanceOf(
				MebisURL.class, MediaURLParser.checkVideo(MediaURLParser.MEBIS_BASE_URL + "?" + query));

		assertFalse(result.isValid());
		assertEquals(MebisError.ID, result.getError());
		assertNull(result.getUrl());
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"https://example.com/video.mp4"})
	void packingNonMebisInputShouldReturnBaseMismatch(@Nullable String input) {
		MebisURL result = MediaURLParser.packUrl(input);

		assertFalse(result.isValid());
		assertEquals(MebisError.BASE_MISMATCH, result.getError());
		assertNull(result.getUrl());
	}
}
