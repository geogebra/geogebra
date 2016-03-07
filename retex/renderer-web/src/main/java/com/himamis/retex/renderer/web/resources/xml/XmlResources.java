/**
 * This file is part of the ReTeX library - https://github.com/himamis/ReTeX
 *
 * Copyright (C) 2015 Balazs Bencze
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * Linking this library statically or dynamically with other modules 
 * is making a combined work based on this library. Thus, the terms 
 * and conditions of the GNU General Public License cover the whole 
 * combination.
 * 
 * As a special exception, the copyright holders of this library give you 
 * permission to link this library with independent modules to produce 
 * an executable, regardless of the license terms of these independent 
 * modules, and to copy and distribute the resulting executable under terms 
 * of your choice, provided that you also meet, for each linked independent 
 * module, the terms and conditions of the license of that module. 
 * An independent module is a module which is not derived from or based 
 * on this library. If you modify this library, you may extend this exception 
 * to your version of the library, but you are not obliged to do so. 
 * If you do not wish to do so, delete this exception statement from your 
 * version.
 * 
 */
package com.himamis.retex.renderer.web.resources.xml;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface XmlResources extends ClientBundle {

	public static final XmlResources INSTANCE = GWT.create(XmlResources.class);

	/*
	 * This interface was generated based on the available source xml at that
	 * time. Please regenerate this if you add/delete/rename xmls.
	 */
	@Source("cyrillic/fonts/jlm_cyrillic.map.xml")
	public TextResource fontsjlm_cyrillic_map();

	@Source("cyrillic/fonts/jlm_wnbx10.xml")
	public TextResource fontsjlm_wnbx10();

	@Source("cyrillic/fonts/jlm_wnbxti10.xml")
	public TextResource fontsjlm_wnbxti10();

	@Source("cyrillic/fonts/jlm_wnr10.xml")
	public TextResource fontsjlm_wnr10();

	@Source("cyrillic/fonts/jlm_wnss10.xml")
	public TextResource fontsjlm_wnss10();

	@Source("cyrillic/fonts/jlm_wnssbx10.xml")
	public TextResource fontsjlm_wnssbx10();

	@Source("cyrillic/fonts/jlm_wnssi10.xml")
	public TextResource fontsjlm_wnssi10();

	@Source("cyrillic/fonts/jlm_wnti10.xml")
	public TextResource fontsjlm_wnti10();

	@Source("cyrillic/fonts/jlm_wntt10.xml")
	public TextResource fontsjlm_wntt10();

	@Source("cyrillic/fonts/language_cyrillic.xml")
	public TextResource fontslanguage_cyrillic();

	@Source("cyrillic/fonts/mappings_cyrillic.xml")
	public TextResource fontsmappings_cyrillic();

	@Source("cyrillic/fonts/symbols_cyrillic.xml")
	public TextResource fontssymbols_cyrillic();

	@Source("DefaultTeXFont.xml")
	public TextResource DefaultTeXFont();

	@Source("fonts/base/jlm_amsfonts.map.xml")
	public TextResource basejlm_amsfonts_map();

	@Source("fonts/base/jlm_amssymb.map.xml")
	public TextResource basejlm_amssymb_map();

	@Source("fonts/base/jlm_base.map.xml")
	public TextResource basejlm_base_map();

	@Source("fonts/base/jlm_cmex10.xml")
	public TextResource basejlm_cmex10();

	@Source("fonts/base/jlm_cmmi10.xml")
	public TextResource basejlm_cmmi10();

	@Source("fonts/base/jlm_cmmi10_unchanged.xml")
	public TextResource basejlm_cmmi10_unchanged();

	@Source("fonts/base/jlm_cmmib10.xml")
	public TextResource basejlm_cmmib10();

	@Source("fonts/base/jlm_cmmib10_unchanged.xml")
	public TextResource basejlm_cmmib10_unchanged();

	@Source("fonts/base/jlm_moustache.xml")
	public TextResource basejlm_moustache();

	@Source("fonts/euler/jlm_eufb10.xml")
	public TextResource eulerjlm_eufb10();

	@Source("fonts/euler/jlm_eufm10.xml")
	public TextResource eulerjlm_eufm10();

	@Source("fonts/latin/jlm_cmr10.xml")
	public TextResource latinjlm_cmr10();

	@Source("fonts/latin/jlm_jlmbi10.xml")
	public TextResource latinjlm_jlmbi10();

	@Source("fonts/latin/jlm_jlmbx10.xml")
	public TextResource latinjlm_jlmbx10();

	@Source("fonts/latin/jlm_jlmi10.xml")
	public TextResource latinjlm_jlmi10();

	@Source("fonts/latin/jlm_jlmr10.xml")
	public TextResource latinjlm_jlmr10();

	@Source("fonts/latin/jlm_jlmr10_unchanged.xml")
	public TextResource latinjlm_jlmr10_unchanged();

	@Source("fonts/latin/jlm_jlmsb10.xml")
	public TextResource latinjlm_jlmsb10();

	@Source("fonts/latin/jlm_jlmsbi10.xml")
	public TextResource latinjlm_jlmsbi10();

	@Source("fonts/latin/jlm_jlmsi10.xml")
	public TextResource latinjlm_jlmsi10();

	@Source("fonts/latin/jlm_jlmss10.xml")
	public TextResource latinjlm_jlmss10();

	@Source("fonts/latin/jlm_jlmtt10.xml")
	public TextResource latinjlm_jlmtt10();

	@Source("fonts/latin/optional/jlm_cmbx10.xml")
	public TextResource optionaljlm_cmbx10();

	@Source("fonts/latin/optional/jlm_cmbxti10.xml")
	public TextResource optionaljlm_cmbxti10();

	@Source("fonts/latin/optional/jlm_cmss10.xml")
	public TextResource optionaljlm_cmss10();

	@Source("fonts/latin/optional/jlm_cmssbx10.xml")
	public TextResource optionaljlm_cmssbx10();

	@Source("fonts/latin/optional/jlm_cmssi10.xml")
	public TextResource optionaljlm_cmssi10();

	@Source("fonts/latin/optional/jlm_cmti10.xml")
	public TextResource optionaljlm_cmti10();

	@Source("fonts/latin/optional/jlm_cmti10_unchanged.xml")
	public TextResource optionaljlm_cmti10_unchanged();

	@Source("fonts/latin/optional/jlm_cmtt10.xml")
	public TextResource optionaljlm_cmtt10();

	@Source("fonts/maths/jlm_cmbsy10.xml")
	public TextResource mathsjlm_cmbsy10();

	@Source("fonts/maths/jlm_cmsy10.xml")
	public TextResource mathsjlm_cmsy10();

	@Source("fonts/maths/jlm_msam10.xml")
	public TextResource mathsjlm_msam10();

	@Source("fonts/maths/jlm_msbm10.xml")
	public TextResource mathsjlm_msbm10();

	@Source("fonts/maths/jlm_rsfs10.xml")
	public TextResource mathsjlm_rsfs10();

	@Source("fonts/maths/jlm_special.map.xml")
	public TextResource mathsjlm_special_map();

	@Source("fonts/maths/jlm_special.xml")
	public TextResource mathsjlm_special();

	@Source("fonts/maths/jlm_stmary10.xml")
	public TextResource mathsjlm_stmary10();

	@Source("fonts/maths/jlm_stmaryrd.map.xml")
	public TextResource mathsjlm_stmaryrd_map();

	@Source("fonts/maths/optional/jlm_dsrom10.xml")
	public TextResource optionaljlm_dsrom10();

	@Source("GlueSettings.xml")
	public TextResource GlueSettings();

	@Source("greek/fonts/jlm_fcmbipg.xml")
	public TextResource fontsjlm_fcmbipg();

	@Source("greek/fonts/jlm_fcmbpg.xml")
	public TextResource fontsjlm_fcmbpg();

	@Source("greek/fonts/jlm_fcmripg.xml")
	public TextResource fontsjlm_fcmripg();

	@Source("greek/fonts/jlm_fcmrpg.xml")
	public TextResource fontsjlm_fcmrpg();

	@Source("greek/fonts/jlm_fcsbpg.xml")
	public TextResource fontsjlm_fcsbpg();

	@Source("greek/fonts/jlm_fcsropg.xml")
	public TextResource fontsjlm_fcsropg();

	@Source("greek/fonts/jlm_fcsrpg.xml")
	public TextResource fontsjlm_fcsrpg();

	@Source("greek/fonts/jlm_fctrpg.xml")
	public TextResource fontsjlm_fctrpg();

	@Source("greek/fonts/jlm_greek.map.xml")
	public TextResource fontsjlm_greek_map();

	@Source("greek/fonts/language_greek.xml")
	public TextResource fontslanguage_greek();

	@Source("greek/fonts/mappings_greek.xml")
	public TextResource fontsmappings_greek();

	@Source("greek/fonts/symbols_greek.xml")
	public TextResource fontssymbols_greek();

	@Source("TeXFormulaSettings.xml")
	public TextResource TeXFormulaSettings();

	@Source("TeXSymbols.xml")
	public TextResource TeXSymbols();

}
