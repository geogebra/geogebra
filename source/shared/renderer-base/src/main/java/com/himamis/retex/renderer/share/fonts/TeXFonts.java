/* TeXFonts.java
 * =========================================================================
 * This file is part of the JLaTeXMath Library - http://forge.scilab.org/jlatexmath
 *
 * Copyright (C) 2018 DENIZET Calixte
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

package com.himamis.retex.renderer.share.fonts;

import com.himamis.retex.renderer.share.FontInfo;

public final class TeXFonts {

	public final FontInfo cmbsy10 = new CMBSY10("fonts/maths/jlm_cmbsy10");
	public final FontInfo cmbx10 = new CMBX10(
			"fonts/latin/optional/jlm_cmbx10");
	public final FontInfo cmbxti10 = new CMBXTI10(
			"fonts/latin/optional/jlm_cmbxti10");
	public final FontInfo cmex10 = new CMEX10("fonts/base/jlm_cmex10");
	public final FontInfo cmmi10 = new CMMI10("fonts/base/jlm_cmmi10");
	public final FontInfo cmmi10_unchanged = new CMMI10_UNCHANGED(
			"fonts/base/jlm_cmmi10");
	public final FontInfo cmmib10 = new CMMIB10("fonts/base/jlm_cmmib10");
	public final FontInfo cmmib10_unchanged = new CMMIB10_UNCHANGED(
			"fonts/base/jlm_cmmib10");
	public final FontInfo cmr10 = new CMR10("fonts/latin/jlm_cmr10");
	public final FontInfo cmss10 = new CMSS10(
			"fonts/latin/optional/jlm_cmss10");
	public final FontInfo cmssbx10 = new CMSSBX10(
			"fonts/latin/optional/jlm_cmssbx10");
	public final FontInfo cmssi10 = new CMSSI10(
			"fonts/latin/optional/jlm_cmssi10");
	public final FontInfo cmsy10 = new CMSY10("fonts/maths/jlm_cmsy10");
	public final FontInfo cmti10 = new CMTI10(
			"fonts/latin/optional/jlm_cmti10");
	public final FontInfo cmti10_unchanged = new CMTI10_UNCHANGED(
			"fonts/latin/optional/jlm_cmti10");
	public final FontInfo cmtt10 = new CMTT10(
			"fonts/latin/optional/jlm_cmtt10");
	public final FontInfo dsrom10 = new DSROM10(
			"fonts/maths/optional/jlm_dsrom10");
	public final FontInfo eufb10 = new EUFB10("fonts/euler/jlm_eufb10");
	public final FontInfo eufm10 = new EUFM10("fonts/euler/jlm_eufm10");
	public final FontInfo fcmbipg = new FCMBIPG("fonts/greek/jlm_fcmbipg");
	public final FontInfo fcmbpg = new FCMBPG("fonts/greek/jlm_fcmbpg");
	public final FontInfo fcmripg = new FCMRIPG("fonts/greek/jlm_fcmripg");
	public final FontInfo fcmrpg = new FCMRPG("fonts/greek/jlm_fcmrpg");
	public final FontInfo fcsbpg = new FCSBPG("fonts/greek/jlm_fcsbpg");
	public final FontInfo fcsropg = new FCSROPG("fonts/greek/jlm_fcsropg");
	public final FontInfo fcsrpg = new FCSRPG("fonts/greek/jlm_fcsrpg");
	public final FontInfo fctrpg = new FCTRPG("fonts/greek/jlm_fctrpg");
	public final FontInfo jlmbi10 = new JLMBI10("fonts/latin/jlm_jlmbi10");
	public final FontInfo jlmbx10 = new JLMBX10("fonts/latin/jlm_jlmbx10");
	public final FontInfo jlmi10 = new JLMI10("fonts/latin/jlm_jlmi10");
	public final FontInfo jlmr10 = new JLMR10("fonts/latin/jlm_jlmr10");
	public final FontInfo jlmr10_unchanged = new JLMR10_UNCHANGED(
			"fonts/latin/jlm_jlmr10");
	public final FontInfo jlmsb10 = new JLMSB10("fonts/latin/jlm_jlmsb10");
	public final FontInfo jlmsbi10 = new JLMSBI10("fonts/latin/jlm_jlmsbi10");
	public final FontInfo jlmsi10 = new JLMSI10("fonts/latin/jlm_jlmsi10");
	public final FontInfo jlmss10 = new JLMSS10("fonts/latin/jlm_jlmss10");
	public final FontInfo jlmtt10 = new JLMTT10("fonts/latin/jlm_jlmtt10");
	public final FontInfo moustache = new MOUSTACHE("fonts/base/jlm_cmex10");
	public final FontInfo msam10 = new MSAM10("fonts/maths/jlm_msam10");
	public final FontInfo msbm10 = new MSBM10("fonts/maths/jlm_msbm10");
	public final FontInfo rsfs10 = new RSFS10("fonts/maths/jlm_rsfs10");
	public final FontInfo special = new SPECIAL("fonts/maths/jlm_special");
	public final FontInfo stmary10 = new STMARY10("fonts/maths/jlm_stmary10");
	public final FontInfo wnbx10 = new WNBX10("fonts/cyrillic/jlm_wnbx10");
	public final FontInfo wnbxti10 = new WNBXTI10(
			"fonts/cyrillic/jlm_wnbxti10");
	public final FontInfo wnr10 = new WNR10("fonts/cyrillic/jlm_wnr10");
	public final FontInfo wnss10 = new WNSS10("fonts/cyrillic/jlm_wnss10");
	public final FontInfo wnssbx10 = new WNSSBX10(
			"fonts/cyrillic/jlm_wnssbx10");
	public final FontInfo wnssi10 = new WNSSI10("fonts/cyrillic/jlm_wnssi10");
	public final FontInfo wnti10 = new WNTI10("fonts/cyrillic/jlm_wnti10");
	public final FontInfo wntt10 = new WNTT10("fonts/cyrillic/jlm_wntt10");

	public TeXFonts() {
		cmbsy10.setDependencies(null, null, null, null, null);
		cmbx10.setDependencies(null, null, cmssbx10, cmtt10, cmbxti10);
		cmbxti10.setDependencies(null, cmbx10, cmssbx10, cmtt10, null);
		cmex10.setDependencies(null, null, null, null, null);
		cmmi10.setDependencies(cmmib10, cmr10, cmss10, cmtt10, cmti10);
		cmmi10_unchanged.setDependencies(cmmib10_unchanged, null, null, null,
				null);
		cmmib10.setDependencies(null, cmbx10, cmssbx10, cmtt10, cmbxti10);
		cmmib10_unchanged.setDependencies(null, null, null, null, null);
		cmr10.setDependencies(cmbx10, null, cmss10, cmtt10, cmti10);
		cmss10.setDependencies(cmssbx10, cmr10, null, cmtt10, cmssi10);
		cmssbx10.setDependencies(null, cmbx10, null, cmtt10, cmbxti10);
		cmssi10.setDependencies(cmssbx10, cmti10, null, cmtt10, null);
		cmsy10.setDependencies(cmbsy10, null, null, null, null);
		cmti10.setDependencies(cmbxti10, cmr10, cmss10, cmtt10, null);
		cmti10_unchanged.setDependencies(cmbxti10, null, null, null, null);
		cmtt10.setDependencies(null, null, null, null, null);
		dsrom10.setDependencies(null, null, null, null, null);
		eufb10.setDependencies(null, null, null, null, null);
		eufm10.setDependencies(eufb10, null, null, null, null);
		fcmbipg.setDependencies(null, null, fcsbpg, fctrpg, null);
		fcmbpg.setDependencies(null, null, fcsbpg, fctrpg, fcmbipg);
		fcmripg.setDependencies(fcmbipg, fcmrpg, fcsropg, fctrpg, null);
		fcmrpg.setDependencies(fcmbpg, null, fcsrpg, fctrpg, fcmripg);
		fcsbpg.setDependencies(null, fcmbpg, null, fctrpg, fcsropg);
		fcsropg.setDependencies(fcsbpg, fcmripg, null, fctrpg, null);
		fcsrpg.setDependencies(fcsbpg, null, null, fctrpg, fcsropg);
		fctrpg.setDependencies(null, null, null, null, null);
		jlmbi10.setDependencies(null, jlmr10, jlmsbi10, jlmtt10, null);
		jlmbx10.setDependencies(null, jlmr10, jlmsb10, jlmtt10, jlmbi10);
		jlmi10.setDependencies(jlmbi10, jlmr10, jlmsi10, jlmtt10, null);
		jlmr10.setDependencies(jlmbx10, null, jlmss10, jlmtt10, jlmi10);
		jlmr10_unchanged.setDependencies(null, null, null, null, null);
		jlmsb10.setDependencies(null, jlmbx10, null, jlmtt10, jlmsbi10);
		jlmsbi10.setDependencies(null, jlmss10, null, jlmtt10, null);
		jlmsi10.setDependencies(jlmsbi10, jlmss10, null, jlmtt10, null);
		jlmss10.setDependencies(jlmsb10, jlmr10, null, jlmtt10, jlmsi10);
		jlmtt10.setDependencies(null, null, null, null, null);
		moustache.setDependencies(null, null, null, null, null);
		msam10.setDependencies(null, null, null, null, null);
		msbm10.setDependencies(null, null, null, null, null);
		rsfs10.setDependencies(null, null, null, null, null);
		special.setDependencies(null, null, null, null, null);
		stmary10.setDependencies(null, null, null, null, null);
		wnbx10.setDependencies(null, null, wnssbx10, wntt10, wnbxti10);
		wnbxti10.setDependencies(null, wnbx10, wnssbx10, wntt10, null);
		wnr10.setDependencies(wnbx10, null, wnss10, wntt10, wnti10);
		wnss10.setDependencies(wnssbx10, wnr10, null, wntt10, wnssi10);
		wnssbx10.setDependencies(null, wnbx10, null, wntt10, null);
		wnssi10.setDependencies(wnssbx10, wnti10, null, wntt10, null);
		wnti10.setDependencies(wnbxti10, wnr10, wnssi10, wntt10, null);
		wntt10.setDependencies(null, wnr10, wnss10, null, null);
	}
}
