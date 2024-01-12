package org.geogebra.common.move.ggtapi.models;

import org.geogebra.common.util.StringUtil;

public abstract class MaterialFilter {
	public abstract boolean check(Material m);

	/**
	 * @param appName app code for filtering
	 * @return filter that filters for app name
	 */
	public static MaterialFilter getAppNameFilter(final String appName) {
		if ("".equals(appName)) {
			return getUniversalFilter();
		}

		return new MaterialFilter() {
			@Override
			public boolean check(Material m) {
				return m.getAppName().equals(appName);
			}
		};
	}

	/**
	 * @return filter that keeps everything
	 */
	public static MaterialFilter getUniversalFilter() {
		return new MaterialFilter() {
			@Override
			public boolean check(Material m) {
				return true;
			}
		};
	}

	/**
	 * searches for "query" in: title and description of material
	 * 
	 * @param query
	 *            String
	 * @return {@link MaterialFilter}
	 */
	public static MaterialFilter getSearchFilter(final String query) {
		return new MaterialFilter() {
			@Override
			public boolean check(Material m) {
				boolean ret = false;
				if (m.getTitle() != null) {
					ret |= StringUtil.toLowerCaseUS(m.getTitle())
							.contains(StringUtil.toLowerCaseUS(query));
				}
				if (m.getDescription() != null) {
					ret |= StringUtil.toLowerCaseUS(m.getDescription())
							.contains(StringUtil.toLowerCaseUS(query));
				}
				return ret;
			}
		};
	}

	/**
	 * Filter materials from specific user
	 * 
	 * @param author
	 *            String
	 * @return {@link MaterialFilter}
	 */
	/*
	 * public static MaterialFilter getAuthorFilter(final String author) {
	 * return new MaterialFilter() {
	 * 
	 * @Override public boolean check(Material m) { return
	 * m.getAuthor().equals(author); } }; }
	 */
}
