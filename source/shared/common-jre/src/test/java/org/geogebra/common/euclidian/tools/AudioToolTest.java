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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_AUDIO;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SELECT_MOW;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawAudio;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.gui.GuiManager;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.DialogManagerNoGui;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoAudio;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.AppConfigDefault;
import org.geogebra.common.media.MediaFactory;
import org.geogebra.common.media.MediaFormat;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.sound.SoundManager;
import org.geogebra.test.LocalizationCommonUTF;
import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/** Tests audio tool activation, shared insertion, playback controls and persistence. */
class AudioToolTest extends BaseToolTest {
	private SoundManager soundManager;
	private boolean soundManagerAvailable = true;

	@Override
	protected void setupClassicApp() {
		soundManager = Mockito.mock(SoundManager.class);
		AppCommon3D app =
				new AppCommon3D(
						new LocalizationCommonUTF(3), new AwtFactoryCommon(), new AppConfigDefault()) {
					@Override
					public @Nullable SoundManager getSoundManager() {
						return soundManagerAvailable ? soundManager : null;
					}
				};
		// All geos and the factory must use the same platform sound manager.
		try (MockedStatic<AppCommonFactory> factory = Mockito.mockStatic(AppCommonFactory.class)) {
			factory
					.when(() -> AppCommonFactory.create3D(Mockito.any(AppConfig.class)))
					.thenReturn(app);
			super.setupClassicApp();
		}
	}

	@Test
	void toolbarShouldOpenAudioDialogBeforeClicksAndAllowReopening() {
		getApp().setGuiManager(Mockito.mock(GuiManager.class, Mockito.RETURNS_DEEP_STUBS));
		GeoPoint point = add("A = (1, -2)");
		Set<GeoElement> before = getConstructionSnapshot();
		try (MockedConstruction<DialogManagerNoGui> dialogs =
				Mockito.mockConstruction(DialogManagerNoGui.class)) {
			getApp().initDialogManager(false);
			DialogManagerNoGui dialog = dialogs.constructed().get(0);
			Mockito.verifyNoInteractions(dialog);

			getApp().getActiveEuclidianView().setMode(MODE_AUDIO, ModeSetter.TOOLBAR);

			Mockito.verify(dialog).showAudioInputDialog();
			assertEquals(MODE_AUDIO, getApp().getMode());
			assertEquals(before, getConstructionSnapshot());
			moveMouseRW(1, -2);
			clickRW(1, -2);
			clickRW(3, -3);
			Mockito.verifyNoMoreInteractions(dialog);
			assertEquals(before, getConstructionSnapshot());
			assertEquals(1, point.getInhomX(), 1E-8);
			assertEquals(-2, point.getInhomY(), 1E-8);

			getApp().getActiveEuclidianView().setMode(MODE_MOVE, ModeSetter.DOCK_PANEL);
			getApp().getActiveEuclidianView().setMode(MODE_AUDIO, ModeSetter.TOOLBAR);
			Mockito.verify(dialog, Mockito.times(2)).showAudioInputDialog();
			assertEquals(before, getConstructionSnapshot());
		}
	}

	@ParameterizedTest
	@EnumSource(value = ModeSetter.class, names = "TOOLBAR", mode = EnumSource.Mode.EXCLUDE)
	void programmaticModeChangesShouldNotOpenAudioDialog(@NonNull ModeSetter modeSetter) {
		getApp().setGuiManager(Mockito.mock(GuiManager.class, Mockito.RETURNS_DEEP_STUBS));
		Set<GeoElement> before = getConstructionSnapshot();
		try (MockedConstruction<DialogManagerNoGui> dialogs =
				Mockito.mockConstruction(DialogManagerNoGui.class)) {
			getApp().initDialogManager(false);
			getApp().getActiveEuclidianView().setMode(MODE_AUDIO, modeSetter);

			Mockito.verifyNoInteractions(dialogs.constructed().get(0));
			assertEquals(MODE_AUDIO, getApp().getMode());
			assertEquals(before, getConstructionSnapshot());
		}
	}

	@Test
	void toolbarWithoutGuiShouldNotOpenDialogOrCreateObjects() {
		Set<GeoElement> before = getConstructionSnapshot();
		try (MockedConstruction<DialogManagerNoGui> dialogs =
				Mockito.mockConstruction(DialogManagerNoGui.class)) {
			getApp().initDialogManager(false);
			getApp().getActiveEuclidianView().setMode(MODE_AUDIO, ModeSetter.TOOLBAR);
			clickRW(1, -2);

			Mockito.verifyNoInteractions(dialogs.constructed().get(0));
			assertEquals(before, getConstructionSnapshot());
		}
	}

	@ParameterizedTest
	@ValueSource(
			strings = {
				"https://example.com/music.mp3", "https://example.com/music.ogg",
				"https://example.com/music.wav?token=one&part=two", "data:audio/mpeg;base64,AAAA"
			})
	void factoryShouldLoadOneCenteredPlayerAndRecordUndoWithoutSelectingIt(@NonNull String url) {
		AppCommon mediaApp = prepareMediaFactory();
		getApp().getActiveEuclidianView().setMode(MODE_AUDIO, ModeSetter.DOCK_PANEL);
		GeoPoint previous = add("A = (1, -2)");
		getApp().getSelectionManager().addSelectedGeo(previous);
		Set<GeoElement> before = getConstructionSnapshot();

		GeoAudio audio = assertInstanceOf(GeoAudio.class, new MediaFactory(mediaApp).addAudio(url));

		assertEquals(List.of(audio), getNewConstructionElements(before, geo -> true));
		assertSame(audio, lookup(audio.getLabelSimple()));
		assertEquals(url, audio.getSrc());
		assertEquals(MediaFormat.AUDIO_HTML5, audio.getFormat());
		assertEquals(GeoClass.AUDIO, audio.getGeoClassType());
		assertTrue(audio.isGeoAudio());
		assertTrue(audio.isDefined());
		assertTrue(audio.isIndependent());
		assertFalse(audio.showInAlgebraView());
		assertEquals(300, audio.getWidth(), 1E-8);
		assertEquals(48, audio.getHeight(), 1E-8);
		assertEquals(300, audio.getMinWidth(), 1E-8);
		assertEquals(48, audio.getMinHeight(), 1E-8);
		assertEquals(0.5, audio.getAnimationStep(), 1E-8);
		assertCentered(audio);
		assertEquals(List.of(previous), getApp().getSelectionManager().getSelectedGeos());
		assertEquals(MODE_AUDIO, getApp().getMode());
		Mockito.verify(soundManager).loadGeoAudio(audio);
		Mockito.verify(mediaApp).storeUndoInfo();
		Mockito.verify(soundManager, Mockito.never()).play(Mockito.any());
	}

	@Test
	void repeatedInsertionsShouldHaveDistinctLabelsAndIndependentSources() {
		AppCommon mediaApp = prepareMediaFactory();
		MediaFactory factory = new MediaFactory(mediaApp);
		Set<GeoElement> before = getConstructionSnapshot();
		GeoAudio first =
				assertInstanceOf(GeoAudio.class, factory.addAudio("https://example.com/one.mp3"));
		assertEquals(List.of(first), getNewConstructionElements(before, geo -> true));
		GeoAudio second =
				assertInstanceOf(GeoAudio.class, factory.addAudio("https://example.com/two.mp3"));

		assertEquals(List.of(first, second), getNewConstructionElements(before, geo -> true));
		assertNotSame(first, second);
		assertSame(first, lookup(first.getLabelSimple()));
		assertSame(second, lookup(second.getLabelSimple()));
		assertCentered(first);
		assertCentered(second);
		first.setSrc("https://example.com/three.mp3");
		assertEquals("https://example.com/two.mp3", second.getSrc());
		Mockito.verify(soundManager, Mockito.times(2)).loadGeoAudio(first);
		Mockito.verify(soundManager).loadGeoAudio(second);
		Mockito.verify(mediaApp, Mockito.times(2)).storeUndoInfo();
	}

	@Test
	void sourceChangeShouldLoadAfterSourceAndPlayerDefaultsAreInitialized() {
		List<String> loadedSources = new ArrayList<>();
		List<List<Double>> loadedDefaults = new ArrayList<>();
		Mockito.doAnswer(invocation -> {
					GeoAudio audio = invocation.getArgument(0);
					loadedSources.add(audio.getSrc());
					loadedDefaults.add(
							List.of(audio.getWidth(), audio.getHeight(), audio.getAnimationStep()));
					return null;
				})
				.when(soundManager)
				.loadGeoAudio(Mockito.any());

		GeoAudio audio = new GeoAudio(getConstruction(), "https://example.com/one.mp3");
		audio.setSrc("https://example.com/two.mp3");

		assertEquals(
				List.of("https://example.com/one.mp3", "https://example.com/two.mp3"), loadedSources);
		assertEquals(List.of(List.of(300.0, 48.0, 0.5), List.of(300.0, 48.0, 0.5)), loadedDefaults);
	}

	@Test
	void copyingAudioShouldCreateDistinctPlayerAndLoadItsSource() {
		GeoAudio source = new GeoAudio(getConstruction(), "https://example.com/one.mp3");
		Mockito.clearInvocations(soundManager);

		GeoAudio copy = assertInstanceOf(GeoAudio.class, source.copy());

		assertNotSame(source, copy);
		assertEquals(source.getSrc(), copy.getSrc());
		assertEquals(MediaFormat.AUDIO_HTML5, copy.getFormat());
		Mockito.verify(soundManager).loadGeoAudio(copy);
		copy.setSrc("https://example.com/two.mp3");
		assertEquals("https://example.com/one.mp3", source.getSrc());
	}

	@Test
	@Issue("APPS-7919")
	void assigningAnotherAudioShouldReloadTargetPlayer() {
		GeoAudio target = new GeoAudio(getConstruction(), "https://example.com/old.mp3");
		List<String> loadedSources = new ArrayList<>();
		Mockito.doAnswer(invocation -> {
					GeoAudio loaded = invocation.getArgument(0);
					loadedSources.add(loaded.getSrc());
					return null;
				})
				.when(soundManager)
				.loadGeoAudio(target);
		GeoAudio source = new GeoAudio(getConstruction(), "https://example.com/new.mp3");
		Mockito.clearInvocations(soundManager);

		target.set(source);

		assertEquals(List.of("https://example.com/new.mp3"), loadedSources);
		assertEquals("https://example.com/new.mp3", source.getSrc());
		assertEquals(source.getSrc(), target.getSrc());
		Mockito.verify(soundManager).loadGeoAudio(target);
		Mockito.verify(soundManager, Mockito.never()).loadGeoAudio(source);
	}

	@Test
	void assigningNonAudioShouldLeaveSourceAndPlayerUnchanged() {
		GeoAudio audio = new GeoAudio(getConstruction(), "https://example.com/music.mp3");
		GeoPoint point = add("A = (1, -2)");
		Mockito.clearInvocations(soundManager);

		audio.set(point);

		assertEquals("https://example.com/music.mp3", audio.getSrc());
		Mockito.verifyNoInteractions(soundManager);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void playbackControlsShouldDelegateWithoutChangingTimeValues(boolean playing) {
		GeoAudio audio = new GeoAudio(getConstruction(), "https://example.com/music.mp3");
		Mockito.when(soundManager.isPlaying(audio)).thenReturn(playing);
		Mockito.when(soundManager.getDuration(audio)).thenReturn(123456);
		Mockito.when(soundManager.getCurrentTime(audio)).thenReturn(12345);
		Mockito.clearInvocations(soundManager);

		audio.play();
		assertEquals(playing, audio.isPlaying());
		assertEquals(123456, audio.getDuration());
		assertEquals(12345, audio.getCurrentTime());
		audio.setCurrentTime(45678);
		audio.pause();

		Mockito.verify(soundManager).play(audio);
		Mockito.verify(soundManager).setCurrentTime(audio, 45678);
		Mockito.verify(soundManager).pause(audio);
	}

	@Test
	void missingSoundManagerShouldUseFallbacksWithoutThrowing() {
		soundManagerAvailable = false;
		GeoAudio audio = new GeoAudio(getConstruction(), "https://example.com/music.mp3");

		audio.play();
		audio.pause();
		audio.setCurrentTime(12345);
		assertFalse(audio.isPlaying());
		assertEquals(-1, audio.getDuration());
		assertEquals(-1, audio.getCurrentTime());
		audio.setLabel("audio");
		audio.remove();
		assertNull(lookup("audio"));
		Mockito.verifyNoInteractions(soundManager);
	}

	@Test
	void removingAudioShouldPauseItBeforeRemovingFromConstruction() {
		AppCommon mediaApp = prepareMediaFactory();
		GeoAudio audio = assertInstanceOf(
				GeoAudio.class, new MediaFactory(mediaApp).addAudio("https://example.com/music.mp3"));
		String label = audio.getLabelSimple();
		List<GeoElement> registeredWhilePausing = new ArrayList<>();
		Mockito.doAnswer(invocation -> {
					registeredWhilePausing.add(lookup(label));
					return null;
				})
				.when(soundManager)
				.pause(audio);

		audio.remove();

		assertEquals(List.of(audio), registeredWhilePausing);
		assertNull(lookup(label));
		Mockito.verify(soundManager).pause(audio);
	}

	@ParameterizedTest
	@ValueSource(strings = {"https://example.com/music.mp3?x=1&y=2", "data:audio/mpeg;base64,AAAA"})
	void insertionShouldSupportUndoRedoWithSourcePositionAndSizePreserved(@NonNull String url) {
		getApp().setUndoActive(true);
		add("A = (1, -2)");
		getApp().storeUndoInfo();
		AppCommon mediaApp = prepareMediaFactory();
		Set<String> before = getObjectNames();
		GeoAudio audio = assertInstanceOf(GeoAudio.class, new MediaFactory(mediaApp).addAudio(url));
		assertSame(audio, getOnlyNewObject(before, GeoAudio.class));
		String label = audio.getLabelSimple();
		double[] bounds = {
			audio.getLocation().getX(), audio.getLocation().getY(), audio.getWidth(), audio.getHeight()
		};

		getKernel().undo();
		assertEquals(before, getObjectNames());
		getKernel().redo();

		GeoAudio restored = getOnlyNewObject(before, GeoAudio.class);
		assertArrayEquals(
				bounds,
				new double[] {
					restored.getLocation().getX(),
					restored.getLocation().getY(),
					restored.getWidth(),
					restored.getHeight()
				},
				1E-8);
		assertEquals(label, restored.getLabelSimple());
		assertEquals(url, restored.getSrc());
		assertEquals(MediaFormat.AUDIO_HTML5, restored.getFormat());
		Mockito.verify(soundManager).loadGeoAudio(restored);
	}

	@Test
	void emptyAudioShouldSerializeWithoutSourceTag() {
		GeoAudio audio = new GeoAudio(getConstruction());
		audio.setLabel("audio");

		assertNull(audio.getSrc());
		assertFalse(audio.getXML().contains("<audio"));
		Mockito.verify(soundManager, Mockito.never()).loadGeoAudio(audio);
	}

	@Test
	void playButtonShouldTogglePlaybackThroughControllerWithoutCreatingObjects() {
		GeoAudio audio = insertSelectableAudio();
		EuclidianView view = getApp().getActiveEuclidianView();
		int x = view.toScreenCoordX(audio.getLocation().getX()) + 20;
		int y = view.toScreenCoordY(audio.getLocation().getY()) + 24;
		AtomicBoolean playing = new AtomicBoolean();
		Mockito.when(soundManager.isPlaying(audio)).thenAnswer(invocation -> playing.get());
		Mockito.doAnswer(invocation -> {
					playing.set(true);
					return null;
				})
				.when(soundManager)
				.play(audio);
		Mockito.doAnswer(invocation -> {
					playing.set(false);
					return null;
				})
				.when(soundManager)
				.pause(audio);
		Set<GeoElement> before = getConstructionSnapshot();
		Mockito.clearInvocations(soundManager);

		click(x, y);
		Mockito.verify(soundManager).play(audio);
		assertTrue(audio.isPlaying());
		assertEquals(before, getConstructionSnapshot());
		audio.updateRepaint();
		resetMouseLocation();
		click(x, y);

		Mockito.verify(soundManager).pause(audio);
		assertFalse(audio.isPlaying());
		assertEquals(before, getConstructionSnapshot());
	}

	@ParameterizedTest
	@CsvSource({"0.25, 30000", "0.75, 90000"})
	void clickingAudioSliderShouldSeekWithinDuration(double fraction, int expectedTime) {
		Mockito.when(soundManager.getDuration(Mockito.any())).thenReturn(120000);
		GeoAudio audio = insertSelectableAudio();
		DrawAudio drawable =
				assertInstanceOf(DrawAudio.class, getApp().getActiveEuclidianView().getDrawableFor(audio));
		int left =
				getApp().getActiveEuclidianView().toScreenCoordX(audio.getLocation().getX());
		int top =
				getApp().getActiveEuclidianView().toScreenCoordY(audio.getLocation().getY());
		Mockito.clearInvocations(soundManager);

		click(
				left + (int) Math.round(drawable.getSliderLeft() + fraction * drawable.getSliderWidth()),
				top + 24);

		ArgumentCaptor<Integer> time = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(soundManager, Mockito.atLeastOnce())
				.setCurrentTime(Mockito.same(audio), time.capture());
		assertEquals(expectedTime, time.getValue(), 120000 / drawable.getSliderWidth());
		Mockito.verify(soundManager, Mockito.never()).play(audio);
		Mockito.verify(soundManager, Mockito.never()).pause(audio);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	@Issue("APPS-7919")
	void draggingSliderBeyondPlayerShouldClampToStartOrDuration(boolean pastEnd) {
		Mockito.when(soundManager.getDuration(Mockito.any())).thenReturn(120000);
		GeoAudio audio = insertSelectableAudio();
		DrawAudio drawable =
				assertInstanceOf(DrawAudio.class, getApp().getActiveEuclidianView().getDrawableFor(audio));
		int left =
				getApp().getActiveEuclidianView().toScreenCoordX(audio.getLocation().getX())
						+ drawable.getSliderLeft();
		int y = getApp().getActiveEuclidianView().toScreenCoordY(audio.getLocation().getY()) + 24;
		Mockito.clearInvocations(soundManager);

		dragStart(left + (int) (drawable.getSliderWidth() / 2), y);
		dragEnd(pastEnd ? left + (int) drawable.getSliderWidth() + 60 : left - 60, y);

		ArgumentCaptor<Integer> time = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(soundManager, Mockito.atLeastOnce())
				.setCurrentTime(Mockito.same(audio), time.capture());
		assertEquals(pastEnd ? 120000 : 0, time.getValue());
		assertTrue(time.getAllValues().stream().allMatch(value -> value >= 0 && value <= 120000));
	}

	@ParameterizedTest
	@ValueSource(ints = {-1, 0})
	@Issue("APPS-7919")
	void sliderShouldNotSeekUntilDurationIsPositive(int duration) {
		Mockito.when(soundManager.getDuration(Mockito.any())).thenReturn(duration);
		GeoAudio audio = insertSelectableAudio();
		DrawAudio drawable =
				assertInstanceOf(DrawAudio.class, getApp().getActiveEuclidianView().getDrawableFor(audio));
		int left =
				getApp().getActiveEuclidianView().toScreenCoordX(audio.getLocation().getX());
		int top =
				getApp().getActiveEuclidianView().toScreenCoordY(audio.getLocation().getY());
		Mockito.clearInvocations(soundManager);

		click(left + (int) (drawable.getSliderLeft() + drawable.getSliderWidth() / 2), top + 24);

		Mockito.verify(soundManager, Mockito.never())
				.setCurrentTime(Mockito.same(audio), Mockito.anyInt());
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	@Issue("APPS-7919")
	void unchangedSourceAssignmentShouldNotReloadPlayer(boolean selfAssignment) {
		GeoAudio target = new GeoAudio(getConstruction(), "https://example.com/music.mp3");
		GeoAudio source = selfAssignment
				? target
				: new GeoAudio(getConstruction(), new StringBuilder(target.getSrc()).toString());
		Mockito.when(soundManager.isPlaying(target)).thenReturn(true);
		Mockito.clearInvocations(soundManager);

		target.set(source);

		Mockito.verifyNoInteractions(soundManager);
		assertEquals("https://example.com/music.mp3", target.getSrc());
		assertTrue(target.isPlaying());
	}

	@Test
	@Issue("APPS-7919")
	void assigningEmptyAudioToEmptyAudioShouldNotLoadMissingSource() {
		GeoAudio source = new GeoAudio(getConstruction());
		GeoAudio target = new GeoAudio(getConstruction());
		Mockito.clearInvocations(soundManager);

		target.set(source);

		assertNull(target.getSrc());
		Mockito.verifyNoInteractions(soundManager);
	}

	@Test
	void removingAudioDuringSliderDragShouldNotSeekOrRecreateIt() {
		Mockito.when(soundManager.getDuration(Mockito.any())).thenReturn(120000);
		GeoAudio audio = insertSelectableAudio();
		DrawAudio drawable =
				assertInstanceOf(DrawAudio.class, getApp().getActiveEuclidianView().getDrawableFor(audio));
		int left =
				getApp().getActiveEuclidianView().toScreenCoordX(audio.getLocation().getX())
						+ drawable.getSliderLeft();
		int y = getApp().getActiveEuclidianView().toScreenCoordY(audio.getLocation().getY()) + 24;
		dragStart(left + (int) (drawable.getSliderWidth() / 2), y);
		audio.remove();
		Set<GeoElement> afterRemoval = getConstructionSnapshot();
		Mockito.clearInvocations(soundManager);

		dragEnd(left + (int) drawable.getSliderWidth(), y);

		Mockito.verify(soundManager, Mockito.never())
				.setCurrentTime(Mockito.same(audio), Mockito.anyInt());
		assertNull(lookup(audio.getLabelSimple()));
		assertEquals(afterRemoval, getConstructionSnapshot());
	}

	@Test
	void sliderShouldStopSeekingIfPlayerBecomesTooNarrowDuringDrag() {
		Mockito.when(soundManager.getDuration(Mockito.any())).thenReturn(120000);
		GeoAudio audio = insertSelectableAudio();
		DrawAudio drawable =
				assertInstanceOf(DrawAudio.class, getApp().getActiveEuclidianView().getDrawableFor(audio));
		int left =
				getApp().getActiveEuclidianView().toScreenCoordX(audio.getLocation().getX())
						+ drawable.getSliderLeft();
		int y = getApp().getActiveEuclidianView().toScreenCoordY(audio.getLocation().getY()) + 24;
		dragStart(left + (int) (drawable.getSliderWidth() / 2), y);
		audio.setSize(100, 48);
		audio.updateRepaint();
		assertTrue(drawable.getSliderWidth() <= 0);
		Mockito.clearInvocations(soundManager);

		dragEnd(left + 20, y);

		Mockito.verify(soundManager, Mockito.never())
				.setCurrentTime(Mockito.same(audio), Mockito.anyInt());
	}

	@ParameterizedTest
	@CsvSource({"30, 0.25", "-30, 0.75", "75, 0.4"})
	@Issue("APPS-7919")
	void rotatedSliderShouldPreserveFractionalPositionWhenSeeking(double angle, double fraction) {
		Mockito.when(soundManager.getDuration(Mockito.any())).thenReturn(120000);
		GeoAudio audio = insertSelectableAudio();
		audio.setAngle(Math.toRadians(angle));
		audio.updateRepaint();
		DrawAudio drawable =
				assertInstanceOf(DrawAudio.class, getApp().getActiveEuclidianView().getDrawableFor(audio));
		GPoint2D start = drawable
				.getTransform()
				.transform(new GPoint2D(drawable.getSliderLeft(), 24), new GPoint2D());
		GPoint2D end = drawable
				.getTransform()
				.transform(
						new GPoint2D(drawable.getSliderLeft() + drawable.getSliderWidth(), 24), new GPoint2D());
		int x = (int) Math.round(start.getX() + fraction * (end.getX() - start.getX()));
		int y = (int) Math.round(start.getY() + fraction * (end.getY() - start.getY()));
		Mockito.clearInvocations(soundManager);

		click(x, y);

		ArgumentCaptor<Integer> time = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(soundManager, Mockito.atLeastOnce())
				.setCurrentTime(Mockito.same(audio), time.capture());
		// Project the actual screen cursor onto the rotated slider axis.
		double dx = end.getX() - start.getX();
		double dy = end.getY() - start.getY();
		double progress = ((x - start.getX()) * dx + (y - start.getY()) * dy) / (dx * dx + dy * dy);
		assertEquals(120000 * progress, time.getValue(), 1);
		Mockito.verify(soundManager, Mockito.never()).play(audio);
		Mockito.verify(soundManager, Mockito.never()).pause(audio);
	}

	private @NonNull AppCommon prepareMediaFactory() {
		getApp().getActiveEuclidianView().setCoordSystem(250, 200, 40, 60);
		return Mockito.spy(getApp());
	}

	private @NonNull GeoAudio insertSelectableAudio() {
		GeoAudio audio = assertInstanceOf(
				GeoAudio.class,
				new MediaFactory(prepareMediaFactory()).addAudio("https://example.com/music.mp3"));
		ec.selectAndShowSelectionUI(audio);
		assertEquals(MODE_SELECT_MOW, getApp().getMode());
		return audio;
	}

	private void assertCentered(@NonNull GeoAudio audio) {
		EuclidianView view = getApp().getActiveEuclidianView();
		assertEquals(
				view.getWidth() / 2.0,
				view.toScreenCoordXd(audio.getLocation().getX()) + audio.getWidth() / 2,
				1E-8);
		assertEquals(
				view.getHeight() / 2.0,
				view.toScreenCoordYd(audio.getLocation().getY()) + audio.getHeight() / 2,
				1E-8);
	}
}
