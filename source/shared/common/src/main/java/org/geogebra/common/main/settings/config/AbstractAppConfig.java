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

package org.geogebra.common.main.settings.config;

import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.equationforms.DefaultEquationBehaviour;
import org.geogebra.common.main.settings.updater.SettingsUpdater;

abstract class AbstractAppConfig implements AppConfig {

    private String appCode;
    private SuiteSubApp subAppCode;
    protected transient CommandFilter commandFilter;
    protected transient EquationBehaviour equationBehaviour;
    private transient ExpressionFilter expressionFilter;

    AbstractAppConfig(String appCode) {
        this(appCode, null);
    }

    AbstractAppConfig(String appCode, String subAppCode) {
        this.appCode = appCode;
        this.subAppCode = SuiteSubApp.forCode(subAppCode);
        initializeEquationBehaviour();
    }

    @Override
    public String getAppCode() {
        return appCode;
    }

	@Override
	public @CheckForNull String getSubAppCode() {
        return subAppCode == null ? null : subAppCode.appCode;
    }

	@Override
	public @CheckForNull SuiteSubApp getSubApp() {
        return subAppCode;
    }

	@Override
	public final @Nonnull EquationBehaviour getEquationBehaviour() {
        if (equationBehaviour == null) {
            initializeEquationBehaviour();
        }
        return equationBehaviour;
    }

    @Override
    public void initializeEquationBehaviour() {
        equationBehaviour = new DefaultEquationBehaviour();
    }

    @Override
    public SettingsUpdater createSettingsUpdater() {
        return new SettingsUpdater();
    }

    @Override
    public String getAppTransKey() {
        if (getSubAppCode() != null) {
            return  GeoGebraConstants.Version.SUITE.getTransKey();
        }
        return getVersion().getTransKey();
    }

    @Override
    public int getMainGraphicsViewId() {
        return App.VIEW_EUCLIDIAN;
    }

	@Override
	public boolean hasOneVarStatistics() {
		return true;
	}

    @Override
    public CommandFilter getCommandFilter() {
        if (commandFilter == null) {
            commandFilter = createCommandFilter();
        }
        return commandFilter;
    }

    @Override
    public boolean hasDataImport() {
        return true;
    }

    @Override
    public void applyRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions,
            @Nonnull ExamType examType) {
        if (featureRestrictions.contains(ExamFeatureRestriction.RESTRICT_CHANGING_EQUATION_FORM)) {
            equationBehaviour.allowChangingEquationFormsByUser(false);
        }
    }

    @Override
    public void removeRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions,
            @Nonnull ExamType examType) {
        initializeEquationBehaviour();
    }

    @Override
    public ExpressionFilter getExpressionFilter() {
        if (expressionFilter == null) {
            expressionFilter = createExpressionFilter();
        }
        return expressionFilter;
    }
}
