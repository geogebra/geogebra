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

package org.geogebra.common.euclidian.tools;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SELECT_MOW;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_VIDEO;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.gui.GuiManager;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.DialogManagerNoGui;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.common.media.MediaFactory;
import org.geogebra.common.media.MediaFormat;
import org.geogebra.common.media.MediaURLParser;
import org.geogebra.common.media.VideoManager;
import org.geogebra.common.media.VideoURL;
import org.geogebra.test.LocalizationCommonUTF;
import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/** Tests video tool activation and shared insertion after video dimensions become available. */
class VideoToolTest extends BaseToolTest {
	private VideoManager videoManager;

	@Override
	protected void setupClassicApp() {
		videoManager = Mockito.mock(VideoManager.class);
		Mockito.when(videoManager.getYouTubeId(Mockito.anyString()))
				.thenAnswer(invocation -> MediaURLParser.getYouTubeId(invocation.getArgument(0)));
		Mockito.when(videoManager.getMebisId(Mockito.anyString()))
				.thenAnswer(invocation -> MediaURLParser.getMebisId(invocation.getArgument(0)));
		AppCommon3D app =
				new AppCommon3D(
						new LocalizationCommonUTF(3), new AwtFactoryCommon(), new AppConfigDefault()) {
					@Override
					public @NonNull VideoManager getVideoManager() {
						return videoManager;
					}
				};
		// Give the real kernel and its geos the same platform loader as MediaFactory.
		try (MockedStatic<AppCommonFactory> factory = Mockito.mockStatic(AppCommonFactory.class)) {
			factory
					.when(() -> AppCommonFactory.create3D(Mockito.any(AppConfig.class)))
					.thenReturn(app);
			super.setupClassicApp();
		}
	}

	@Test
	void toolbarShouldOpenVideoDialogBeforeAnyCanvasClickAndAllowReopening() {
		getApp().setGuiManager(Mockito.mock(GuiManager.class, Mockito.RETURNS_DEEP_STUBS));
		GeoPoint point = add("A = (1, -2)");
		Set<GeoElement> before = getConstructionSnapshot();
		try (MockedConstruction<DialogManagerNoGui> dialogs =
				Mockito.mockConstruction(DialogManagerNoGui.class)) {
			getApp().initDialogManager(false);
			DialogManagerNoGui dialog = dialogs.constructed().get(0);
			Mockito.verifyNoInteractions(dialog);

			getApp().getActiveEuclidianView().setMode(MODE_VIDEO, ModeSetter.TOOLBAR);

			Mockito.verify(dialog).showVideoInputDialog();
			assertEquals(before, getConstructionSnapshot());
			assertEquals(MODE_VIDEO, getApp().getMode());
			moveMouseRW(1, -2);
			clickRW(1, -2);
			clickRW(3, -3);
			Mockito.verifyNoMoreInteractions(dialog);
			assertEquals(before, getConstructionSnapshot());
			assertEquals(1, point.getInhomX(), 1E-8);
			assertEquals(-2, point.getInhomY(), 1E-8);

			getApp().getActiveEuclidianView().setMode(MODE_MOVE, ModeSetter.DOCK_PANEL);
			getApp().getActiveEuclidianView().setMode(MODE_VIDEO, ModeSetter.TOOLBAR);
			Mockito.verify(dialog, Mockito.times(2)).showVideoInputDialog();
			assertEquals(before, getConstructionSnapshot());
		}
	}

	@ParameterizedTest
	@EnumSource(value = ModeSetter.class, names = "TOOLBAR", mode = EnumSource.Mode.EXCLUDE)
	void programmaticModeChangesShouldNotOpenVideoDialog(@NonNull ModeSetter modeSetter) {
		getApp().setGuiManager(Mockito.mock(GuiManager.class, Mockito.RETURNS_DEEP_STUBS));
		Set<GeoElement> before = getConstructionSnapshot();
		try (MockedConstruction<DialogManagerNoGui> dialogs =
				Mockito.mockConstruction(DialogManagerNoGui.class)) {
			getApp().initDialogManager(false);

			getApp().getActiveEuclidianView().setMode(MODE_VIDEO, modeSetter);

			Mockito.verifyNoInteractions(dialogs.constructed().get(0));
			assertEquals(MODE_VIDEO, getApp().getMode());
			assertEquals(before, getConstructionSnapshot());
		}
	}

	@Test
	void toolbarWithoutGuiShouldNotAttemptToOpenVideoDialogOrCreateObjects() {
		Set<GeoElement> before = getConstructionSnapshot();
		try (MockedConstruction<DialogManagerNoGui> dialogs =
				Mockito.mockConstruction(DialogManagerNoGui.class)) {
			getApp().initDialogManager(false);

			getApp().getActiveEuclidianView().setMode(MODE_VIDEO, ModeSetter.TOOLBAR);
			clickRW(1, -2);

			Mockito.verifyNoInteractions(dialogs.constructed().get(0));
			assertEquals(before, getConstructionSnapshot());
		}
	}

	@Test
	void youtubeWithKnownSizeShouldBeCenteredSelectedAndRecordedImmediately() {
		AppCommon mediaApp = prepareMediaFactory();
		VideoURL url = MediaURLParser.checkVideo("https://youtu.be/E4uvbaTR7mw");
		Set<GeoElement> before = getConstructionSnapshot();

		GeoVideo video = assertInstanceOf(GeoVideo.class, new MediaFactory(mediaApp).addVideo(url));

		assertEquals(List.of(video), getNewConstructionElements(before, geo -> true));
		assertSame(video, lookup(video.getLabelSimple()));
		assertEquals(url.getUrl(), video.getSrc());
		assertEquals(MediaFormat.VIDEO_YOUTUBE, video.getFormat());
		assertEquals(420, video.getWidth(), 1E-8);
		assertEquals(345, video.getHeight(), 1E-8);
		assertTrue(video.isBackground());
		assertCenteredAndSelected(video);
		Mockito.verify(mediaApp).storeUndoInfo();

		video.setSize(640, 360);
		Mockito.verify(mediaApp).storeUndoInfo();
	}

	@ParameterizedTest
	@ValueSource(
			strings = {
				"https://example.com/video.mp4",
				"https://example.com/video.m4v",
				"https://mediathek.mebis.bayern.de/?doc=record&identifier=BY-00072140"
			})
	void unknownDimensionsShouldDeferCenteringSelectionAndUndoUntilBothArePositive(
			@NonNull String input) {
		AppCommon mediaApp = prepareMediaFactory();
		GeoPoint previous = add("A = (1, -2)");
		getApp().getSelectionManager().addSelectedGeo(previous);
		Set<GeoElement> before = getConstructionSnapshot();
		VideoURL url = MediaURLParser.checkVideo(input);
		assertTrue(url.isValid());

		GeoVideo video = assertInstanceOf(GeoVideo.class, new MediaFactory(mediaApp).addVideo(url));

		assertEquals(List.of(video), getNewConstructionElements(before, geo -> true));
		assertEquals(url.getUrl(), video.getSrc());
		assertEquals(url.getFormat(), video.getFormat());
		assertEquals(-1, video.getWidth(), 1E-8);
		assertEquals(-1, video.getHeight(), 1E-8);
		for (double[] dimensions : new double[][] {{320, -1}, {0, 180}, {320, 0}}) {
			video.setSize(dimensions[0], dimensions[1]);
			Mockito.verify(mediaApp, Mockito.never()).storeUndoInfo();
			assertEquals(List.of(previous), getApp().getSelectionManager().getSelectedGeos());
		}

		video.setSize(320, 180);

		assertCenteredAndSelected(video);
		Mockito.verify(mediaApp).storeUndoInfo();
		double x = video.getLocation().getX();
		double y = video.getLocation().getY();
		video.setSize(640, 360);
		assertEquals(x, video.getLocation().getX(), 1E-8);
		assertEquals(y, video.getLocation().getY(), 1E-8);
		Mockito.verify(mediaApp).storeUndoInfo();
		assertEquals(List.of(video), getNewConstructionElements(before, geo -> true));
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void pendingVideoInsertionsShouldCompleteIndependentlyInEitherOrder(boolean firstCompletesFirst) {
		AppCommon mediaApp = prepareMediaFactory();
		MediaFactory factory = new MediaFactory(mediaApp);
		Set<GeoElement> before = getConstructionSnapshot();
		GeoVideo first = assertInstanceOf(
				GeoVideo.class,
				factory.addVideo(MediaURLParser.checkVideo("https://example.com/first.mp4")));
		assertEquals(List.of(first), getNewConstructionElements(before, geo -> true));
		GeoVideo second = assertInstanceOf(
				GeoVideo.class,
				factory.addVideo(MediaURLParser.checkVideo("https://example.com/second.mp4")));
		assertNotEquals(first.getLabelSimple(), second.getLabelSimple());
		Mockito.verify(mediaApp, Mockito.never()).storeUndoInfo();

		GeoVideo earlier = firstCompletesFirst ? first : second;
		GeoVideo later = firstCompletesFirst ? second : first;
		earlier.setSize(320, 180);
		assertCenteredAndSelected(earlier);
		assertEquals(-1, later.getWidth(), 1E-8);
		assertEquals(-1, later.getHeight(), 1E-8);
		Mockito.verify(mediaApp).storeUndoInfo();
		later.setSize(640, 360);
		assertCenteredAndSelected(later);
		Mockito.verify(mediaApp, Mockito.times(2)).storeUndoInfo();
		assertEquals(List.of(first, second), getNewConstructionElements(before, geo -> true));
		assertSame(first, lookup(first.getLabelSimple()));
		assertSame(second, lookup(second.getLabelSimple()));
	}

	@Test
	@Issue("APPS-7919")
	void sizeCompletionShouldRunOnceEvenIfCallbackResizesVideo() {
		GeoVideo video =
				new GeoVideo(getConstruction(), "https://example.com/video.mp4", MediaFormat.VIDEO_HTML5);
		AtomicInteger completions = new AtomicInteger();
		video.afterSizeSet(() -> {
			if (completions.incrementAndGet() == 1) {
				video.setSize(640, 360);
			}
		});

		video.setSize(320, 180);

		assertEquals(1, completions.get());
		assertEquals(640, video.getWidth(), 1E-8);
		assertEquals(360, video.getHeight(), 1E-8);
	}

	@ParameterizedTest
	@ValueSource(strings = {"https://youtu.be/E4uvbaTR7mw", "https://example.com/video.mp4"})
	void completedInsertionShouldBeUndoableAndRedoShouldRestoreVideo(@NonNull String input) {
		getApp().setUndoActive(true);
		add("A = (1, -2)");
		getApp().storeUndoInfo();
		AppCommon mediaApp = prepareMediaFactory();
		Set<String> before = getObjectNames();
		VideoURL url = MediaURLParser.checkVideo(input);
		GeoVideo video = assertInstanceOf(GeoVideo.class, new MediaFactory(mediaApp).addVideo(url));
		if (url.getFormat() != MediaFormat.VIDEO_YOUTUBE) {
			video.setSize(320, 180);
		}
		assertSame(video, getOnlyNewObject(before, GeoVideo.class));
		String label = video.getLabelSimple();
		double[] expectedBounds = {
			video.getLocation().getX(), video.getLocation().getY(), video.getWidth(), video.getHeight()
		};

		getKernel().undo();
		assertEquals(before, getObjectNames());
		getKernel().redo();

		GeoVideo restored = getOnlyNewObject(before, GeoVideo.class);
		assertArrayEquals(
				expectedBounds,
				new double[] {
					restored.getLocation().getX(),
					restored.getLocation().getY(),
					restored.getWidth(),
					restored.getHeight()
				},
				1E-8);
		assertEquals(label, restored.getLabelSimple());
		assertEquals(url.getFormat(), restored.getFormat());
		assertEquals(
				url.getFormat() == MediaFormat.VIDEO_YOUTUBE
						? "https://www.youtube.com/embed/E4uvbaTR7mw?wmode=transparent"
						: url.getUrl(),
				restored.getSrc());
		assertTrue(restored.isBackground());
	}

	@ParameterizedTest
	@ValueSource(
			strings = {
				"https://youtu.be/E4uvbaTR7mw",
				"https://mediathek.mebis.bayern.de/?doc=record&identifier=BY-00072140"
			})
	@Issue("APPS-7919")
	void insertionShouldRetainPreviewMetadataCreatedBySourceInitialization(@NonNull String input) {
		AppCommon mediaApp = prepareMediaFactory();
		VideoURL url = MediaURLParser.checkVideo(input);

		GeoVideo video = assertInstanceOf(GeoVideo.class, new MediaFactory(mediaApp).addVideo(url));

		String expectedPreview = url.getFormat() == MediaFormat.VIDEO_YOUTUBE
				? "/apps/crossorigin/?url=https://img.youtube.com/vi/E4uvbaTR7mw/0.jpg"
				: "/apps/crossorigin/?url=https://sodis.de/mediathek/thumbsCache_16_9/"
						+ "BY-00072140___.jpg";
		assertEquals(expectedPreview, video.getPreviewUrl());
		if (url.getFormat() == MediaFormat.VIDEO_YOUTUBE) {
			assertEquals("E4uvbaTR7mw", video.getYouTubeId());
		}
		Mockito.verify(videoManager).createPreview(video);
	}

	@Test
	@Issue("APPS-7919")
	void changingVideoProviderShouldReplaceMetadataAndDiscardPreviousPreview() {
		GeoVideo video =
				new GeoVideo(getConstruction(), "https://youtu.be/E4uvbaTR7mw", MediaFormat.VIDEO_YOUTUBE);
		video.setPreview(Mockito.mock(MyImage.class));

		video.setSrc("https://youtu.be/M7lc1UVf-VE", MediaFormat.VIDEO_YOUTUBE);

		assertEquals("M7lc1UVf-VE", video.getYouTubeId());
		assertEquals(
				"/apps/crossorigin/?url=https://img.youtube.com/vi/M7lc1UVf-VE/0.jpg",
				video.getPreviewUrl());
		assertNull(video.getPreview());
		video.setPreview(Mockito.mock(MyImage.class));
		video.setSrc(
				MediaURLParser.MEBIS_BASE_URL + "?doc=provideVideo&identifier=BY-00072140&type=video",
				MediaFormat.VIDEO_MEBIS);
		assertNull(video.getYouTubeId());
		assertEquals(
				"/apps/crossorigin/?url=https://sodis.de/mediathek/thumbsCache_16_9/"
						+ "BY-00072140___.jpg",
				video.getPreviewUrl());
		assertNull(video.getPreview());
		assertNull(video.getEmbeddedUrl());
		video.setPreview(Mockito.mock(MyImage.class));
		video.setSrc("https://example.com/video.mp4", MediaFormat.VIDEO_HTML5);
		assertNull(video.getYouTubeId());
		assertNull(video.getPreviewUrl());
		assertNull(video.getPreview());
		assertNull(video.getEmbeddedUrl());
		Mockito.verify(videoManager, Mockito.times(3)).createPreview(video);
	}

	@Test
	@Issue("APPS-7919")
	void failedSizeCallbackShouldPropagateAndNotRunAgainOnResize() {
		GeoVideo video =
				new GeoVideo(getConstruction(), "https://example.com/video.mp4", MediaFormat.VIDEO_HTML5);
		AtomicInteger completions = new AtomicInteger();
		IllegalStateException failure = new IllegalStateException("Completion failed");
		video.afterSizeSet(() -> {
			completions.incrementAndGet();
			throw failure;
		});

		assertSame(failure, assertThrows(IllegalStateException.class, () -> video.setSize(320, 180)));
		video.setSize(640, 360);

		assertEquals(1, completions.get());
	}

	@Test
	@Issue("APPS-7919")
	void sizeCallbackShouldNotDiscardAnotherCallbackRegisteredDuringCompletion() {
		GeoVideo video =
				new GeoVideo(getConstruction(), "https://example.com/video.mp4", MediaFormat.VIDEO_HTML5);
		AtomicInteger first = new AtomicInteger();
		AtomicInteger next = new AtomicInteger();
		video.afterSizeSet(() -> {
			if (first.incrementAndGet() == 1) {
				video.setSize(-1, -1);
				video.afterSizeSet(next::incrementAndGet);
			}
		});

		video.setSize(320, 180);
		assertEquals(1, first.get());
		assertEquals(0, next.get());
		video.setSize(640, 360);
		assertEquals(1, first.get());
		assertEquals(1, next.get());
		video.setSize(800, 450);
		assertEquals(1, next.get());
	}

	@Test
	void videoShouldInitializeSourceWithoutPlatformVideoManager() {
		AppCommon headless = AppCommonFactory.create();
		assertNull(headless.getVideoManager());

		GeoVideo video = new GeoVideo(
				headless.getKernel().getConstruction(),
				"https://example.com/video.mp4",
				MediaFormat.VIDEO_HTML5);

		assertEquals("https://example.com/video.mp4", video.getSrc());
		assertEquals(MediaFormat.VIDEO_HTML5, video.getFormat());
		assertNull(video.getYouTubeId());
		assertNull(video.getPreviewUrl());
		assertNull(video.getPreview());
		assertEquals(-1, video.getWidth(), 1E-8);
		assertEquals(-1, video.getHeight(), 1E-8);
	}

	private @NonNull AppCommon prepareMediaFactory() {
		// Keep the real construction, view and controller. Replace only the platform video loader.
		AppCommon mediaApp = Mockito.spy(getApp());
		Mockito.when(
						videoManager.createVideo(Mockito.same(getConstruction()), Mockito.any(VideoURL.class)))
				.thenAnswer(invocation -> {
					VideoURL url = invocation.getArgument(1);
					return new GeoVideo(getConstruction(), url.getUrl(), url.getFormat());
				});
		getApp().getActiveEuclidianView().setCoordSystem(250, 200, 40, 60);
		return mediaApp;
	}

	private void assertCenteredAndSelected(@NonNull GeoVideo video) {
		EuclidianView view = getApp().getActiveEuclidianView();
		assertEquals(
				view.getWidth() / 2.0,
				view.toScreenCoordXd(video.getLocation().getX()) + video.getWidth() / 2,
				1E-8);
		assertEquals(
				view.getHeight() / 2.0,
				view.toScreenCoordYd(video.getLocation().getY()) + video.getHeight() / 2,
				1E-8);
		assertEquals(List.of(video), getApp().getSelectionManager().getSelectedGeos());
		assertEquals(MODE_SELECT_MOW, getApp().getMode());
	}
}
