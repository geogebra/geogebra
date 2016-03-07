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
package com.himamis.retex.renderer.web.resources;

import com.himamis.retex.renderer.share.cyrillic.CyrillicRegistration;
import com.himamis.retex.renderer.share.exception.ResourceParseException;
import com.himamis.retex.renderer.share.greek.GreekRegistration;
import com.himamis.retex.renderer.share.platform.resources.ResourceLoader;
import com.himamis.retex.renderer.web.resources.xml.XmlResources;

public class ResourceLoaderW implements ResourceLoader {

	private static final XmlResources XML_RESOURCES = XmlResources.INSTANCE;

	@Override
	public Object loadResource(Object base, String path)
			throws ResourceParseException {
		// base object is either a class or null
		String fullPath = getPath((Class<?>) base) + path;
		return getResource(fullPath);
	}

	private String getPath(Class<?> clazz) {
		if (CyrillicRegistration.class.equals(clazz)) {
			return "cyrillic/";
		}
		if (GreekRegistration.class.equals(clazz)) {
			return "greek/";
		}
		return "";
	}

	/*
	 * This method was generated based on the available source xml at that time.
	 * Please regenerate this if you add/delete/rename xmls.
	 */
	private static final String getResource(String source) {
		if (source.equals("cyrillic/fonts/jlm_cyrillic.map.xml")) {
			return XML_RESOURCES.fontsjlm_cyrillic_map().getText();
		}
		if (source.equals("cyrillic/fonts/jlm_wnbx10.xml")) {
			return XML_RESOURCES.fontsjlm_wnbx10().getText();
		}
		if (source.equals("cyrillic/fonts/jlm_wnbxti10.xml")) {
			return XML_RESOURCES.fontsjlm_wnbxti10().getText();
		}
		if (source.equals("cyrillic/fonts/jlm_wnr10.xml")) {
			return XML_RESOURCES.fontsjlm_wnr10().getText();
		}
		if (source.equals("cyrillic/fonts/jlm_wnss10.xml")) {
			return XML_RESOURCES.fontsjlm_wnss10().getText();
		}
		if (source.equals("cyrillic/fonts/jlm_wnssbx10.xml")) {
			return XML_RESOURCES.fontsjlm_wnssbx10().getText();
		}
		if (source.equals("cyrillic/fonts/jlm_wnssi10.xml")) {
			return XML_RESOURCES.fontsjlm_wnssi10().getText();
		}
		if (source.equals("cyrillic/fonts/jlm_wnti10.xml")) {
			return XML_RESOURCES.fontsjlm_wnti10().getText();
		}
		if (source.equals("cyrillic/fonts/jlm_wntt10.xml")) {
			return XML_RESOURCES.fontsjlm_wntt10().getText();
		}
		if (source.equals("cyrillic/fonts/language_cyrillic.xml")) {
			return XML_RESOURCES.fontslanguage_cyrillic().getText();
		}
		if (source.equals("cyrillic/fonts/mappings_cyrillic.xml")) {
			return XML_RESOURCES.fontsmappings_cyrillic().getText();
		}
		if (source.equals("cyrillic/fonts/symbols_cyrillic.xml")) {
			return XML_RESOURCES.fontssymbols_cyrillic().getText();
		}
		if (source.equals("DefaultTeXFont.xml")) {
			return XML_RESOURCES.DefaultTeXFont().getText();
		}
		if (source.equals("fonts/base/jlm_amsfonts.map.xml")) {
			return XML_RESOURCES.basejlm_amsfonts_map().getText();
		}
		if (source.equals("fonts/base/jlm_amssymb.map.xml")) {
			return XML_RESOURCES.basejlm_amssymb_map().getText();
		}
		if (source.equals("fonts/base/jlm_base.map.xml")) {
			return XML_RESOURCES.basejlm_base_map().getText();
		}
		if (source.equals("fonts/base/jlm_cmex10.xml")) {
			return XML_RESOURCES.basejlm_cmex10().getText();
		}
		if (source.equals("fonts/base/jlm_cmmi10.xml")) {
			return XML_RESOURCES.basejlm_cmmi10().getText();
		}
		if (source.equals("fonts/base/jlm_cmmi10_unchanged.xml")) {
			return XML_RESOURCES.basejlm_cmmi10_unchanged().getText();
		}
		if (source.equals("fonts/base/jlm_cmmib10.xml")) {
			return XML_RESOURCES.basejlm_cmmib10().getText();
		}
		if (source.equals("fonts/base/jlm_cmmib10_unchanged.xml")) {
			return XML_RESOURCES.basejlm_cmmib10_unchanged().getText();
		}
		if (source.equals("fonts/base/jlm_moustache.xml")) {
			return XML_RESOURCES.basejlm_moustache().getText();
		}
		if (source.equals("fonts/euler/jlm_eufb10.xml")) {
			return XML_RESOURCES.eulerjlm_eufb10().getText();
		}
		if (source.equals("fonts/euler/jlm_eufm10.xml")) {
			return XML_RESOURCES.eulerjlm_eufm10().getText();
		}
		if (source.equals("fonts/latin/jlm_cmr10.xml")) {
			return XML_RESOURCES.latinjlm_cmr10().getText();
		}
		if (source.equals("fonts/latin/jlm_jlmbi10.xml")) {
			return XML_RESOURCES.latinjlm_jlmbi10().getText();
		}
		if (source.equals("fonts/latin/jlm_jlmbx10.xml")) {
			return XML_RESOURCES.latinjlm_jlmbx10().getText();
		}
		if (source.equals("fonts/latin/jlm_jlmi10.xml")) {
			return XML_RESOURCES.latinjlm_jlmi10().getText();
		}
		if (source.equals("fonts/latin/jlm_jlmr10.xml")) {
			return XML_RESOURCES.latinjlm_jlmr10().getText();
		}
		if (source.equals("fonts/latin/jlm_jlmr10_unchanged.xml")) {
			return XML_RESOURCES.latinjlm_jlmr10_unchanged().getText();
		}
		if (source.equals("fonts/latin/jlm_jlmsb10.xml")) {
			return XML_RESOURCES.latinjlm_jlmsb10().getText();
		}
		if (source.equals("fonts/latin/jlm_jlmsbi10.xml")) {
			return XML_RESOURCES.latinjlm_jlmsbi10().getText();
		}
		if (source.equals("fonts/latin/jlm_jlmsi10.xml")) {
			return XML_RESOURCES.latinjlm_jlmsi10().getText();
		}
		if (source.equals("fonts/latin/jlm_jlmss10.xml")) {
			return XML_RESOURCES.latinjlm_jlmss10().getText();
		}
		if (source.equals("fonts/latin/jlm_jlmtt10.xml")) {
			return XML_RESOURCES.latinjlm_jlmtt10().getText();
		}
		if (source.equals("fonts/latin/optional/jlm_cmbx10.xml")) {
			return XML_RESOURCES.optionaljlm_cmbx10().getText();
		}
		if (source.equals("fonts/latin/optional/jlm_cmbxti10.xml")) {
			return XML_RESOURCES.optionaljlm_cmbxti10().getText();
		}
		if (source.equals("fonts/latin/optional/jlm_cmss10.xml")) {
			return XML_RESOURCES.optionaljlm_cmss10().getText();
		}
		if (source.equals("fonts/latin/optional/jlm_cmssbx10.xml")) {
			return XML_RESOURCES.optionaljlm_cmssbx10().getText();
		}
		if (source.equals("fonts/latin/optional/jlm_cmssi10.xml")) {
			return XML_RESOURCES.optionaljlm_cmssi10().getText();
		}
		if (source.equals("fonts/latin/optional/jlm_cmti10.xml")) {
			return XML_RESOURCES.optionaljlm_cmti10().getText();
		}
		if (source.equals("fonts/latin/optional/jlm_cmti10_unchanged.xml")) {
			return XML_RESOURCES.optionaljlm_cmti10_unchanged().getText();
		}
		if (source.equals("fonts/latin/optional/jlm_cmtt10.xml")) {
			return XML_RESOURCES.optionaljlm_cmtt10().getText();
		}
		if (source.equals("fonts/maths/jlm_cmbsy10.xml")) {
			return XML_RESOURCES.mathsjlm_cmbsy10().getText();
		}
		if (source.equals("fonts/maths/jlm_cmsy10.xml")) {
			return XML_RESOURCES.mathsjlm_cmsy10().getText();
		}
		if (source.equals("fonts/maths/jlm_msam10.xml")) {
			return XML_RESOURCES.mathsjlm_msam10().getText();
		}
		if (source.equals("fonts/maths/jlm_msbm10.xml")) {
			return XML_RESOURCES.mathsjlm_msbm10().getText();
		}
		if (source.equals("fonts/maths/jlm_rsfs10.xml")) {
			return XML_RESOURCES.mathsjlm_rsfs10().getText();
		}
		if (source.equals("fonts/maths/jlm_special.map.xml")) {
			return XML_RESOURCES.mathsjlm_special_map().getText();
		}
		if (source.equals("fonts/maths/jlm_special.xml")) {
			return XML_RESOURCES.mathsjlm_special().getText();
		}
		if (source.equals("fonts/maths/jlm_stmary10.xml")) {
			return XML_RESOURCES.mathsjlm_stmary10().getText();
		}
		if (source.equals("fonts/maths/jlm_stmaryrd.map.xml")) {
			return XML_RESOURCES.mathsjlm_stmaryrd_map().getText();
		}
		if (source.equals("fonts/maths/optional/jlm_dsrom10.xml")) {
			return XML_RESOURCES.optionaljlm_dsrom10().getText();
		}
		if (source.equals("GlueSettings.xml")) {
			return XML_RESOURCES.GlueSettings().getText();
		}
		if (source.equals("greek/fonts/jlm_fcmbipg.xml")) {
			return XML_RESOURCES.fontsjlm_fcmbipg().getText();
		}
		if (source.equals("greek/fonts/jlm_fcmbpg.xml")) {
			return XML_RESOURCES.fontsjlm_fcmbpg().getText();
		}
		if (source.equals("greek/fonts/jlm_fcmripg.xml")) {
			return XML_RESOURCES.fontsjlm_fcmripg().getText();
		}
		if (source.equals("greek/fonts/jlm_fcmrpg.xml")) {
			return XML_RESOURCES.fontsjlm_fcmrpg().getText();
		}
		if (source.equals("greek/fonts/jlm_fcsbpg.xml")) {
			return XML_RESOURCES.fontsjlm_fcsbpg().getText();
		}
		if (source.equals("greek/fonts/jlm_fcsropg.xml")) {
			return XML_RESOURCES.fontsjlm_fcsropg().getText();
		}
		if (source.equals("greek/fonts/jlm_fcsrpg.xml")) {
			return XML_RESOURCES.fontsjlm_fcsrpg().getText();
		}
		if (source.equals("greek/fonts/jlm_fctrpg.xml")) {
			return XML_RESOURCES.fontsjlm_fctrpg().getText();
		}
		if (source.equals("greek/fonts/jlm_greek.map.xml")) {
			return XML_RESOURCES.fontsjlm_greek_map().getText();
		}
		if (source.equals("greek/fonts/language_greek.xml")) {
			return XML_RESOURCES.fontslanguage_greek().getText();
		}
		if (source.equals("greek/fonts/mappings_greek.xml")) {
			return XML_RESOURCES.fontsmappings_greek().getText();
		}
		if (source.equals("greek/fonts/symbols_greek.xml")) {
			return XML_RESOURCES.fontssymbols_greek().getText();
		}
		if (source.equals("TeXFormulaSettings.xml")) {
			return XML_RESOURCES.TeXFormulaSettings().getText();
		}
		if (source.equals("TeXSymbols.xml")) {
			return XML_RESOURCES.TeXSymbols().getText();
		}
		throw new ResourceParseException(
				"Resource not found, please regenerate XmlResource file and ResourceLoader.getResource() methods");
	}
}
