package org.geogebra.common.main.settings.config;

import javax.annotation.CheckForNull;

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
}
