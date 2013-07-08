package geogebra.common.move.ggtapi.models;

import geogebra.common.util.StringUtil;

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
}
