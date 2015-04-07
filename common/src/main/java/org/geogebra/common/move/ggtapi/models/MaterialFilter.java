package org.geogebra.common.move.ggtapi.models;

import org.geogebra.common.util.StringUtil;

public abstract class MaterialFilter {
	public abstract boolean check(Material m);
	
	public static MaterialFilter getUniversalFilter(){
		return new MaterialFilter(){
			@Override
			public boolean check(Material m){
				return true;
			}
		};
	}
	
	/**
	 * searches for "query" in: title and description of material
	 * @param query String
	 * @return {@link MaterialFilter}
	 */
	public static MaterialFilter getSearchFilter(final String query){
		return new MaterialFilter(){
			@Override
			public boolean check(Material m){
				boolean ret = false;
				if(m.getTitle() != null){
					ret |= StringUtil.toLowerCase(m.getTitle()).contains(StringUtil.toLowerCase(query));
				}
				if(m.getDescription() != null){
					ret |= StringUtil.toLowerCase(m.getDescription()).contains(StringUtil.toLowerCase(query));
				}
				return ret;
			}
		};
	}
	
	/**
	 * Filter materials from specific user
	 * @param author String
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
