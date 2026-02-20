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

package org.geogebra.web.html5.gui.accessibility;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.AltTextTimer;
import org.geogebra.common.gui.FocusableComponent;
import org.geogebra.common.gui.GeoTabber;
import org.geogebra.common.gui.compositefocus.FocusableComposite;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.web.html5.main.AppW;

/**
 * Web implementation of AccessibilityManager.
 *
 */
public class AccessibilityManagerW implements AccessibilityManagerInterface {
	private final GeoTabber geoTabber;
	private final AltGeoTabber altGeoTabber;
	private final AppW app;
	private final SelectionManager selection;
	private final ViewAltTexts altTexts;
	private FocusableComponent anchor;
	private SideBarAccessibilityAdapter menuContainer;

	private final AltTextTimer timer;

	private final Comparator<FocusableComponent> componentComparator = (o1, o2) -> {
		int viewDiff = o1.getAccessibilityGroup().ordinal()
				- o2.getAccessibilityGroup().ordinal();
		if (viewDiff != 0) {
			return viewDiff;
		}
		if (o1.getViewControlId() != null && o2.getViewControlId() != null) {
			return o1.getViewControlId().ordinal() - o2.getViewControlId().ordinal();
		}
		return 0;
	};

	private final TreeSet<FocusableComponent> components = new TreeSet<>(componentComparator);
	private final Set<FocusableComposite> compositeFocusOwners = new HashSet<>();
	private FocusableComposite activeCompositeFocus;

	/**
	 * Constructor.
	 *
	 * @param app
	 *            The application.
	 */
	public AccessibilityManagerW(AppW app) {
		this.app = app;
		selection = app.getSelectionManager();
		this.geoTabber = new GeoTabber(app);
		altTexts = new ViewAltTexts(app);
		timer = new AltTextTimer(app.getActiveEuclidianView().getScreenReader(),
				app.getLocalization());
		altGeoTabber = new AltGeoTabber(app, altTexts);
		components.add(altGeoTabber);
		components.add(geoTabber);
		components.add(new PlayButtonTabber(app.getActiveEuclidianView()));
		components.add(new ResetButtonTabber(app.getActiveEuclidianView()));
		List<String> externalControlSelectors  = Arrays.stream(
				app.getAppletParameters().getParamExternalControls().split(","))
				.filter(s -> ! s.isEmpty())
				.collect(Collectors.toList());
		if (!externalControlSelectors.isEmpty()) {
			components.add(new ExternalControl(externalControlSelectors, this,
					app.getGlobalHandlers()));
		}
	}

	@Override
	public boolean focusNext() {
		removeFocusFromInternals();
		for (FocusableComponent entry: components) {
			if (entry.hasFocus()) {
				if (!entry.focusNext()) {
					focusFirstVisible(findNext(entry));
				}
				return true;
			}
		}
		return focusFirstVisible(components.first());
	}

	private boolean focusFirstVisible(@Nonnull FocusableComponent entry) {
		FocusableComponent nextEntry = entry;
		do {
			if (nextEntry.focusIfVisible(false)) {
				return true;
			}
			nextEntry = findNext(nextEntry);
		} while (nextEntry != entry);

		return false;
	}

	private boolean focusLastVisible(@Nonnull FocusableComponent entry) {
		FocusableComponent nextEntry = entry;
		do {
			if (nextEntry.focusIfVisible(true)) {
				return true;
			}
			nextEntry = findPrevious(nextEntry);
		} while (nextEntry != entry);

		return false;
	}

	private FocusableComponent findNext(FocusableComponent entry) {
		FocusableComponent nextEntry = components.higher(entry);
		if (nextEntry == null) {
			return components.first();
		}
		return nextEntry;
	}

	private FocusableComponent findPrevious(FocusableComponent entry) {
		FocusableComponent nextEntry = components.lower(entry);
		if (nextEntry == null) {
			return components.last();
		}
		return nextEntry;
	}

	@Override
	public boolean focusPrevious() {
		removeFocusFromInternals();
		for (FocusableComponent entry: components) {
			if (entry.hasFocus()) {
				if (!entry.focusPrevious()) {
					return focusLastVisible(findPrevious(entry));
				}
				return true;
			}
		}

		return focusLastVisible(components.last());
	}

	private void removeFocusFromInternals() {
		if (activeCompositeFocus != null) {
			activeCompositeFocus.blur();
		}
		activeCompositeFocus = null;
	}

	@Override
	public void register(FocusableComponent focusable) {
		components.removeIf(c -> componentComparator.compare(focusable, c) == 0);
		components.add(focusable);
	}

	@Override
	public void unregister(FocusableComponent focusable) {
		components.removeIf(c -> componentComparator.compare(focusable, c) == 0);
	}

	@Override
	public void setTabOverGeos() {
		geoTabber.setFocused(true);
		app.getSelectionManager().resetKeyboardSelection();
	}

	@Override
	public void focusFirstElement() {
		components.first().focusIfVisible(false);
	}

	@Override
	public boolean focusInput(boolean force, boolean forceFade) {
		if (menuContainer != null) {
			return menuContainer.focusInput(force, forceFade);
		}
		return false;
	}

	@Override
	public void focusGeo(GeoElement geo) {
		if (geo != null) {
			selection.addSelectedGeoForEV(geo);
			if (!geo.isGeoInputBox()) {
				app.getActiveEuclidianView().requestFocus();
			}
		} else {
			if (menuContainer != null) {
				menuContainer.focusMenu();
			}
		}
	}

	@Override
	public void setAnchor(FocusableComponent anchor) {
		this.anchor = anchor;
	}

	@Override
	public FocusableComponent getAnchor() {
		return anchor;
	}

	@Override
	public void focusAnchor() {
		if (anchor == null) {
			return;
		}
		anchor.focusIfVisible(false);
		cancelAnchor();
	}

	@Override
	public void focusAnchorOrMenu() {
		if (anchor == null) {
			focusFirstElement();
		} else {
			focusAnchor();
		}
	}

	@Override
	public void cancelAnchor() {
		anchor = null;
	}

	/**
	 * @param toolbarPanel side bar adapter
	 */
	public void setMenuContainer(SideBarAccessibilityAdapter toolbarPanel) {
		this.menuContainer = toolbarPanel;
	}

	@Override
	public void appendAltText(GeoText altText) {
		if (altTexts.isValid(altText)) {
			timer.feed(altText);
		}
	}

	@Override
	public void cancelReadCollectedAltTexts() {
		timer.cancel();
	}

	@Override
	public void preloadAltText(GeoText geoText) {
		timer.preload(geoText);
	}

	@Override
	public void registerCompositeFocusContainer(FocusableComposite compositeFocus) {
		compositeFocusOwners.add(compositeFocus);
	}

	@Override
	public void unregisterCompositeFocusContainer(FocusableComposite compositeFocus) {
		compositeFocusOwners.remove(compositeFocus);
		activeCompositeFocus = null;
	}

	@Override
	public boolean hasFocusInComposite() {
		findActiveCompositeFocus();
		return activeCompositeFocus.isFocused();
	}

	@Override
	public boolean focusNextInComposite() {
		findActiveCompositeFocus();
		if (activeCompositeFocus == null) {
			return false;
		}
		return activeCompositeFocus.hasFocus() ? activeCompositeFocus.focusNext()
				: activeCompositeFocus.focusFirst();
	}

	@Override
	public boolean focusPreviousInComposite() {
		findActiveCompositeFocus();
		if (activeCompositeFocus == null) {
			return false;
		}
		return activeCompositeFocus.hasFocus() ? activeCompositeFocus.focusPrevious()
				: activeCompositeFocus.focusLast();
	}

	@Override
	public void blurCompositeFocus() {
		findActiveCompositeFocus();
		if (activeCompositeFocus != null) {
			activeCompositeFocus.blur();
			activeCompositeFocus = null;
		}
	}

	@Override
	public boolean handlesEnterInComposite() {
		return activeCompositeFocus != null
				&& activeCompositeFocus.handlesEnterKeyForSelectedPart();
	}

	private void findActiveCompositeFocus() {
		if (activeCompositeFocus != null && !activeCompositeFocus.isFocused()) {
			activeCompositeFocus = null;
		}

		if (activeCompositeFocus == null) {
			Optional<FocusableComposite> compositeFocus =
					compositeFocusOwners.stream().filter(FocusableComposite::isFocused).findFirst();
			compositeFocus.ifPresent(focusableComposite
					-> activeCompositeFocus = focusableComposite);
		}
	}

	@Override
	public void readSliderUpdate(GeoNumeric geo) {
		if (!app.getKernel().getConstruction().isFileLoading()
				&& (!app.getAppletParameters().preventFocus()
				|| !geo.isAnimating() || !app.getKernel().isAnimationRunning())) {
			timer.feed(geo);
		}
	}
}
