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

import java.util.HashMap;

import com.google.gwt.resources.client.TextResource;
import com.himamis.retex.renderer.share.cyrillic.CyrillicRegistration;
import com.himamis.retex.renderer.share.exception.ResourceParseException;
import com.himamis.retex.renderer.share.greek.GreekRegistration;
import com.himamis.retex.renderer.share.platform.resources.ResourceLoader;
import com.himamis.retex.renderer.web.resources.xml.XmlResources;

public class ResourceLoaderW implements ResourceLoader {

	private static final XmlResources XML_RESOURCES = XmlResources.INSTANCE;
	private static HashMap<String, TextResource> map = new HashMap<String, TextResource>();
	static {
		initResources();
	}
	@Override
	public Object loadResource(Object base, String path)
			throws ResourceParseException {
		// base object is either a class or null
		String fullPath = getPath((Class<?>) base) + path;
		if (!map.containsKey(fullPath)) {
			throw new ResourceParseException(
					"Resource not found, please regenerate XmlResource file and ResourceLoader.getResource() methods"
							+ fullPath);
		}
		return map.get(fullPath).getText();

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

	public static void addResource(String path, TextResource res) {
		map.put(path, res);
	}

	/*
	 * This method was generated based on the available source xml at that time.
	 * Please regenerate this if you add/delete/rename xmls.
	 */
	private static final void initResources() {
		addResource("cyrillic/fonts/jlm_cyrillic.map.xml",
				XML_RESOURCES.fontsjlm_cyrillic_map());

		addResource("cyrillic/fonts/jlm_wnbx10.xml",
				XML_RESOURCES.fontsjlm_wnbx10());

		addResource("cyrillic/fonts/jlm_wnbxti10.xml",
				XML_RESOURCES.fontsjlm_wnbxti10());

		addResource("cyrillic/fonts/jlm_wnr10.xml",
				XML_RESOURCES.fontsjlm_wnr10());

		addResource("cyrillic/fonts/jlm_wnss10.xml",
				XML_RESOURCES.fontsjlm_wnss10());

		addResource("cyrillic/fonts/jlm_wnssbx10.xml",
				XML_RESOURCES.fontsjlm_wnssbx10());

		addResource("cyrillic/fonts/jlm_wnssi10.xml",
				XML_RESOURCES.fontsjlm_wnssi10());

		addResource("cyrillic/fonts/jlm_wnti10.xml",
				XML_RESOURCES.fontsjlm_wnti10());

		addResource("cyrillic/fonts/jlm_wntt10.xml",
				XML_RESOURCES.fontsjlm_wntt10());

		addResource("cyrillic/fonts/language_cyrillic.xml",
				XML_RESOURCES.fontslanguage_cyrillic());

		addResource("cyrillic/fonts/mappings_cyrillic.xml",
				XML_RESOURCES.fontsmappings_cyrillic());

		addResource("cyrillic/fonts/symbols_cyrillic.xml",
				XML_RESOURCES.fontssymbols_cyrillic());

		addResource("DefaultTeXFont.xml", XML_RESOURCES.DefaultTeXFont());

		addResource("fonts/base/jlm_amsfonts.map.xml",
				XML_RESOURCES.basejlm_amsfonts_map());

		addResource("fonts/base/jlm_amssymb.map.xml",
				XML_RESOURCES.basejlm_amssymb_map());

		addResource("fonts/base/jlm_base.map.xml",
				XML_RESOURCES.basejlm_base_map());

		addResource("fonts/base/jlm_cmex10.xml",
				XML_RESOURCES.basejlm_cmex10());

		addResource("fonts/base/jlm_cmmi10.xml",
				XML_RESOURCES.basejlm_cmmi10());

		addResource("fonts/base/jlm_cmmi10_unchanged.xml",
				XML_RESOURCES.basejlm_cmmi10_unchanged());

		addResource("fonts/base/jlm_cmmib10.xml",
				XML_RESOURCES.basejlm_cmmib10());

		addResource("fonts/base/jlm_cmmib10_unchanged.xml",
				XML_RESOURCES.basejlm_cmmib10_unchanged());

		addResource("fonts/base/jlm_moustache.xml",
				XML_RESOURCES.basejlm_moustache());

		addResource("fonts/euler/jlm_eufb10.xml",
				XML_RESOURCES.eulerjlm_eufb10());

		addResource("fonts/euler/jlm_eufm10.xml",
				XML_RESOURCES.eulerjlm_eufm10());

		addResource("fonts/latin/jlm_cmr10.xml",
				XML_RESOURCES.latinjlm_cmr10());

		addResource("fonts/latin/jlm_jlmbi10.xml",
				XML_RESOURCES.latinjlm_jlmbi10());

		addResource("fonts/latin/jlm_jlmbx10.xml",
				XML_RESOURCES.latinjlm_jlmbx10());

		addResource("fonts/latin/jlm_jlmi10.xml",
				XML_RESOURCES.latinjlm_jlmi10());

		addResource("fonts/latin/jlm_jlmr10.xml",
				XML_RESOURCES.latinjlm_jlmr10());

		addResource("fonts/latin/jlm_jlmr10_unchanged.xml",
				XML_RESOURCES.latinjlm_jlmr10_unchanged());

		addResource("fonts/latin/jlm_jlmsb10.xml",
				XML_RESOURCES.latinjlm_jlmsb10());

		addResource("fonts/latin/jlm_jlmsbi10.xml",
				XML_RESOURCES.latinjlm_jlmsbi10());

		addResource("fonts/latin/jlm_jlmsi10.xml",
				XML_RESOURCES.latinjlm_jlmsi10());

		addResource("fonts/latin/jlm_jlmss10.xml",
				XML_RESOURCES.latinjlm_jlmss10());

		addResource("fonts/latin/jlm_jlmtt10.xml",
				XML_RESOURCES.latinjlm_jlmtt10());

		addResource("fonts/latin/optional/jlm_cmbx10.xml",
				XML_RESOURCES.optionaljlm_cmbx10());

		addResource("fonts/latin/optional/jlm_cmbxti10.xml",
				XML_RESOURCES.optionaljlm_cmbxti10());

		addResource("fonts/latin/optional/jlm_cmss10.xml",
				XML_RESOURCES.optionaljlm_cmss10());

		addResource("fonts/latin/optional/jlm_cmssbx10.xml",
				XML_RESOURCES.optionaljlm_cmssbx10());

		addResource("fonts/latin/optional/jlm_cmssi10.xml",
				XML_RESOURCES.optionaljlm_cmssi10());

		addResource("fonts/latin/optional/jlm_cmti10.xml",
				XML_RESOURCES.optionaljlm_cmti10());

		addResource("fonts/latin/optional/jlm_cmti10_unchanged.xml",
				XML_RESOURCES.optionaljlm_cmti10_unchanged());

		addResource("fonts/latin/optional/jlm_cmtt10.xml",
				XML_RESOURCES.optionaljlm_cmtt10());

		addResource("fonts/maths/jlm_cmbsy10.xml",
				XML_RESOURCES.mathsjlm_cmbsy10());

		addResource("fonts/maths/jlm_cmsy10.xml",
				XML_RESOURCES.mathsjlm_cmsy10());

		addResource("fonts/maths/jlm_msam10.xml",
				XML_RESOURCES.mathsjlm_msam10());

		addResource("fonts/maths/jlm_msbm10.xml",
				XML_RESOURCES.mathsjlm_msbm10());

		addResource("fonts/maths/jlm_rsfs10.xml",
				XML_RESOURCES.mathsjlm_rsfs10());

		addResource("fonts/maths/jlm_special.map.xml",
				XML_RESOURCES.mathsjlm_special_map());

		addResource("fonts/maths/jlm_special.xml",
				XML_RESOURCES.mathsjlm_special());

		addResource("fonts/maths/jlm_stmary10.xml",
				XML_RESOURCES.mathsjlm_stmary10());

		addResource("fonts/maths/jlm_stmaryrd.map.xml",
				XML_RESOURCES.mathsjlm_stmaryrd_map());

		addResource("fonts/maths/optional/jlm_dsrom10.xml",
				XML_RESOURCES.optionaljlm_dsrom10());

		addResource("GlueSettings.xml", XML_RESOURCES.GlueSettings());

		addResource("greek/fonts/jlm_fcmbipg.xml",
				XML_RESOURCES.fontsjlm_fcmbipg());

		addResource("greek/fonts/jlm_fcmbpg.xml",
				XML_RESOURCES.fontsjlm_fcmbpg());

		addResource("greek/fonts/jlm_fcmripg.xml",
				XML_RESOURCES.fontsjlm_fcmripg());

		addResource("greek/fonts/jlm_fcmrpg.xml",
				XML_RESOURCES.fontsjlm_fcmrpg());

		addResource("greek/fonts/jlm_fcsbpg.xml",
				XML_RESOURCES.fontsjlm_fcsbpg());

		addResource("greek/fonts/jlm_fcsropg.xml",
				XML_RESOURCES.fontsjlm_fcsropg());

		addResource("greek/fonts/jlm_fcsrpg.xml",
				XML_RESOURCES.fontsjlm_fcsrpg());

		addResource("greek/fonts/jlm_fctrpg.xml",
				XML_RESOURCES.fontsjlm_fctrpg());

		addResource("greek/fonts/jlm_greek.map.xml",
				XML_RESOURCES.fontsjlm_greek_map());

		addResource("greek/fonts/language_greek.xml",
				XML_RESOURCES.fontslanguage_greek());

		addResource("greek/fonts/mappings_greek.xml",
				XML_RESOURCES.fontsmappings_greek());

		addResource("greek/fonts/symbols_greek.xml",
				XML_RESOURCES.fontssymbols_greek());

		addResource("TeXFormulaSettings.xml",
				XML_RESOURCES.TeXFormulaSettings());

		addResource("TeXSymbols.xml", XML_RESOURCES.TeXSymbols());


	}
}
