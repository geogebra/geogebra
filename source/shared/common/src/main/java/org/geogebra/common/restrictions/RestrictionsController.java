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

package org.geogebra.common.restrictions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.contextmenu.ContextMenuItemFilter;
import org.geogebra.common.properties.PropertiesRegistryListener;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyKey;
import org.geogebra.common.restrictions.Restrictions.ContextDependencies;

import com.google.j2objc.annotations.Weak;

/**
 * A controller for coordinating restrictions on app components.
 * <p>
 * Restrictions can be applied (activated) and removed (deactivated) at runtime.
 * The only requirement for clients is that {@link #setActiveContext(ContextDependencies)} needs
 * to be called at least once before applying restrictions.
 */
public final class RestrictionsController implements PropertiesRegistryListener {

	/**
	 * The delegate (mandatory)
	 * @apiNote It is assumed that the delegate is set before attempting to apply restrictions.
	 * {@link #applyRestrictions(Restrictions)} will {@code assert} if the delegate is null.
	 */
	@Weak
	@com.google.j2objc.annotations.Property
	public @CheckForNull RestrictionsControllerDelegate delegate;

	private Restrictions restrictions;
	private ContextDependencies activeDependencies;
	private final Set<Restrictable> restrictables = new HashSet<>();

	/**
	 * Set the active context and associated dependencies.
	 * <p>
	 * When any of the dependencies (e.g., the command dispatcher or algebra processor) change,
	 * this should be communicated to the restrictions controller by calling this method.
	 * </p>
	 * This method needs to be called before {@link #applyRestrictions(Restrictions)}, and also
	 * when the active app changes while restrictions are active, so what we can remove the
	 * restrictions from the current dependencies, and apply the restrictions on the new
	 * dependencies.
	 * @param contextDependencies The set of context dependencies.
	 */
	public void setActiveContext(@Nonnull ContextDependencies contextDependencies) {
		if (activeDependencies != null) {
			activeDependencies.propertiesRegistry().removeListener(this);
			// remove restrictions for current dependencies
			if (restrictions != null) {
				removeRestrictionsFromContextDependencies(activeDependencies);
			}
		}
		activeDependencies = contextDependencies;
		activeDependencies.propertiesRegistry().addListener(this);
		// apply restrictions to new dependencies if restrictions are currently active
		if (restrictions != null) {
			applyRestrictionsToDelegate(restrictions);
			applyRestrictionsToContextDependencies(contextDependencies);
		}
	}

	/**
	 * Register an object that may need to apply additional restrictions/customization.
	 * @param restrictable An object that may need to perform additional customization.
	 * @apiNote When {@link Restrictions} are currently active, the {@link Restrictable} is asked
	 * to apply the current restrictions immediately.
	 * @apiNote Make sure to balance register and unregister calls, to properly unregister
	 * any {@code Restrictable}s when no longer needed, to prevent keeping objects alive
	 * that could otherwise be discarded.
	 */
	public void registerRestrictable(@Nonnull Restrictable restrictable) {
		restrictables.add(restrictable);
		if (restrictions != null) {
			restrictable.applyRestrictions(restrictions.getFeatureRestrictions());
		}
	}

	/**
	 * Unregister an {@link Restrictable}.
	 * @param restrictable An object that that was previously registered with
	 * {@link #registerRestrictable(Restrictable)}..
	 * @apiNote When {@link Restrictions} are currently active, the {@link Restrictable} is asked
	 * to remove the current restrictions immediately.
	 */
	public void unregisterRestrictable(@Nonnull Restrictable restrictable) {
		if (restrictions != null) {
			restrictable.removeRestrictions(restrictions.getFeatureRestrictions());
		}
		restrictables.remove(restrictable);
	}

	/**
	 * Apply restrictions.
	 * @param restrictions A set of restrictions.
	 */
	public void applyRestrictions(@Nonnull Restrictions restrictions) {
		assert delegate != null; // delegate is mandatory, not setting it is a developer error
		applyRestrictionsToDelegate(restrictions);
		// delay setting the restrictions field until after delegates have been notified
		// (see https://git.geogebra.org/ggb/geogebra/-/merge_requests/9370#note_76206)
		this.restrictions = restrictions;

		if (activeDependencies != null) {
			applyRestrictionsToContextDependencies(activeDependencies);
		}
		applyRestrictionsToRestrictables();
	}

	/**
	 * Remove the restrictions from the active context dependencies.
	 */
	public void removeRestrictions() {
		if (activeDependencies != null) {
			removeRestrictionsFromContextDependencies(activeDependencies);
		}
		removeRestrictionsFromRestrictables();
	}

	private void applyRestrictionsToRestrictables() {
		if (restrictions == null) {
			return;
		}
		Set<FeatureRestriction> featureRestrictions = restrictions.getFeatureRestrictions();
		for (Restrictable restrictable : restrictables) {
			restrictable.applyRestrictions(featureRestrictions);
		}
	}

	private void removeRestrictionsFromRestrictables() {
		if (restrictions == null) {
			return;
		}
		Set<FeatureRestriction> featureRestrictions = restrictions.getFeatureRestrictions();
		for (Restrictable restrictable : restrictables) {
			restrictable.removeRestrictions(featureRestrictions);
		}
	}

	/**
	 * Clear out any restrictions (set to {@code null}).
	 */
	public void resetRestrictions() {
		restrictions = null;
	}

	private void applyRestrictionsToDelegate(Restrictions restrictions) {
		if (delegate == null) {
			return;
		}
		SuiteSubApp currentSubApp = delegate.getCurrentSubApp();
		Set<SuiteSubApp> disabledSubApps = restrictions.getDisabledSubApps();
		if (disabledSubApps.contains(currentSubApp)) {
			delegate.switchSubApp(restrictions.getDefaultSubApp());
		}
	}

	private void applyRestrictionsToContextDependencies(ContextDependencies dependencies) {
		if (restrictions == null) {
			return;
		}
		restrictions.applyTo(dependencies);
	}

	private void removeRestrictionsFromContextDependencies(ContextDependencies dependencies) {
		if (dependencies == null || restrictions == null) {
			return;
		}
		restrictions.removeFrom(dependencies);
	}

	public @CheckForNull Restrictions getRestrictions() {
		return restrictions;
	}

	/**
	 * Get the list of disabled subapps, if any.
	 * @return The set of disabled (restricted) sub-apps, or null if there are no
	 * restrictions on sub-apps currently.
	 */
	public @CheckForNull Set<SuiteSubApp> getDisabledSubApps() {
		return restrictions != null ? restrictions.getDisabledSubApps() : null;
	}

	/**
	 * Get the list of disabled subapps, if any.
	 * @return The set of disabled (restricted) sub-app codes, or null if there are no
	 * restrictions on sub-apps currently.
	 */
	public @CheckForNull Set<String> getDisabledSubAppCodes() {
		Set<SuiteSubApp> disabledSubApps = getDisabledSubApps();
		if (disabledSubApps == null) {
			return null;
		}
		return disabledSubApps.stream().map(subApp -> subApp.appCode).collect(Collectors.toSet());
	}

	/**
	 * Check for disabled subapps.
	 * @param subApp A sub-app
	 * @return True if the sub-app corresponding to appCode is currently disabled, false otherwise.
	 */
	public boolean isDisabledSubApp(SuiteSubApp subApp) {
		Set<SuiteSubApp> disabledSubApps = getDisabledSubApps();
		if (disabledSubApps == null) {
			return false;
		}
		return disabledSubApps.contains(subApp);
	}

	/**
	 * Get the current list of context menu item filters (may be empty).
	 * @return The context menu item filters if restrictions are currently active, or an
	 * empty set otherwise.
	 */
	public @Nonnull Set<ContextMenuItemFilter> getContextMenuItemFilters() {
		return restrictions != null ? restrictions.getContextMenuItemFilters() : Set.of();
	}

	/**
	 * Check for disabled features.
	 * @param featureRestriction A feature restriction.
	 * @return True if restrictions are currently active and the feature is restricted, false
	 * otherwise.
	 */
	public boolean isFeatureRestricted(@Nonnull FeatureRestriction featureRestriction) {
		return restrictions != null
				&& restrictions.getFeatureRestrictions().contains(featureRestriction);
	}

	// -- PropertiesRegistryListener --

	/**
	 * Handles freezing properties on lazy property instantiation/registration.
	 * @param property A property that just got registered.
	 */
	@Override
	public void propertyRegistered(@Nonnull Property property) {
		if (restrictions == null) {
			return;
		}
		Map<PropertyKey, PropertyRestriction> propertyRestrictions =
				restrictions.getPropertyRestrictions();
		PropertyKey key = property.getKey();
		if (propertyRestrictions.containsKey(key)) {
			propertyRestrictions.get(key).applyTo(property);
		}
	}

	/**
	 * Handles unfreezing any frozen properties on deregistration.
	 * @param property A property that just got unregistered.
	 */
	@Override
	public void propertyUnregistered(@Nonnull Property property) {
		if (restrictions == null) {
			return;
		}
		Map<PropertyKey, PropertyRestriction> propertyRestrictions =
				restrictions.getPropertyRestrictions();
		PropertyKey key = property.getKey();
		if (propertyRestrictions.containsKey(key)) {
			propertyRestrictions.get(key).removeFrom(property);
		}
	}

}
