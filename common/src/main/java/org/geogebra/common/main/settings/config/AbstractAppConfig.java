package org.geogebra.common.main.settings.config;

import javax.annotation.CheckForNull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppConfig;

abstract class AbstractAppConfig implements AppConfig {

    private String appCode;
    private String subAppCode;

    AbstractAppConfig(String appCode) {
        this(appCode, null);
    }

    AbstractAppConfig(String appCode, String subAppCode) {
        this.appCode = appCode;
        this.subAppCode = subAppCode;
    }

    @Override
    public String getAppCode() {
        return appCode;
    }

    @CheckForNull
    @Override
    public String getSubAppCode() {
        return subAppCode;
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
}
