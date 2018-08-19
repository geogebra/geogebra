// -*- mode:C++ ; compile-command: "g++ -I.. -g -c ti89.cc" -*-
/*
 *  Copyright (C) 2000,2014 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
#ifndef _GIAC_TI9X_H
#define _GIAC_TI9X_H
#include "first.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  typedef unsigned char octet;
  class gen;
  extern const unary_function_ptr * const  at_entry;
  extern const unary_function_ptr * const  at_ans;
  gen _seq(const gen & g,GIAC_CONTEXT);
  gen _logb(const gen & g,GIAC_CONTEXT);
  gen _getType(const gen & g,GIAC_CONTEXT);
  gen _Define(const gen & g,GIAC_CONTEXT);
  gen _isprime(const gen & args,GIAC_CONTEXT);
  gen _Input(const gen & args,GIAC_CONTEXT);
  gen _InputStr(const gen & g,GIAC_CONTEXT);
  gen _Prompt(const gen & g,GIAC_CONTEXT);
  gen _cSolve(const gen & g,GIAC_CONTEXT);
  gen _cFactor(const gen & g,GIAC_CONTEXT);
  gen _cpartfrac(const gen & g,GIAC_CONTEXT);
  gen _nSolve(const gen & g,GIAC_CONTEXT);
  gen _zeros(const gen & g,GIAC_CONTEXT);
  gen _cZeros(const gen & g,GIAC_CONTEXT);
  gen _getDenom(const gen & g,GIAC_CONTEXT);
  gen _denom(const gen & g,GIAC_CONTEXT);
  gen _getNum(const gen & g,GIAC_CONTEXT);
  gen _numer(const gen & g,GIAC_CONTEXT);
  gen _tExpand(const gen & g,GIAC_CONTEXT);
  gen _comDenom(const gen & g,GIAC_CONTEXT);
  gen _randPoly(const gen & g,GIAC_CONTEXT);
  gen _nInt(const gen & g,GIAC_CONTEXT);
  gen _nDeriv(const gen & g,GIAC_CONTEXT);
  gen _avgRC(const gen & g,GIAC_CONTEXT);
  gen _fMin(const gen & g,GIAC_CONTEXT);
  gen _fMax(const gen & g,GIAC_CONTEXT);
  gen _taylor(const gen & g,GIAC_CONTEXT);
  gen _arcLen(const gen & g,GIAC_CONTEXT);
  gen _dim(const gen & g,GIAC_CONTEXT);
  gen _format(const gen & g,GIAC_CONTEXT);
  gen _inString(const gen & g,GIAC_CONTEXT);
  gen _left(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_left;
  gen _right(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_right;
  gen _mid(const gen & g,GIAC_CONTEXT);
  gen _ord(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_rotate;
  gen _rotate(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_shift;
  gen _shift(const gen & g,GIAC_CONTEXT);
  gen _augment(const gen & g,GIAC_CONTEXT);
  gen _semi_augment(const gen & g,GIAC_CONTEXT);
  gen _cumSum(const gen & g,GIAC_CONTEXT);
  gen _exp2list(const gen & g,GIAC_CONTEXT);
  gen _list2exp(const gen & g,GIAC_CONTEXT);
  gen _list2mat(const gen & g,GIAC_CONTEXT);
  gen _deltalist(const gen & g,GIAC_CONTEXT);
  gen _mat2list(const gen & g,GIAC_CONTEXT);
  gen _newList(const gen & g,GIAC_CONTEXT);
  gen _polyEval(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_product;
  gen _product(const gen & g,GIAC_CONTEXT);
  gen sortad(const vecteur & v,bool ascend,GIAC_CONTEXT);
  gen _SortA(const gen & g,GIAC_CONTEXT);
  gen _sorta(const gen & g,GIAC_CONTEXT);
  gen _SortD(const gen & g,GIAC_CONTEXT);
  bool complex_sort(const gen & a,const gen & b,GIAC_CONTEXT);
  gen _int(const gen & g,GIAC_CONTEXT);
  gen _iPart(const gen & g,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_iPart;
  gen _Fill(const gen & g,GIAC_CONTEXT);
  gen _mRow(const gen & g,GIAC_CONTEXT);
  gen _mRowAdd(const gen & g,GIAC_CONTEXT);
  gen _rowAdd(const gen & g,GIAC_CONTEXT);
  gen _rowSwap(const gen & g,GIAC_CONTEXT);
  gen _LU(const gen & g,GIAC_CONTEXT);
  gen _QR(const gen & g,GIAC_CONTEXT);
  gen _newMat(const gen & g,GIAC_CONTEXT);
  gen _ref(const gen & a,GIAC_CONTEXT) ;
  vecteur gen2vecteur(const gen & g,int exclude);
  gen _subMat(const gen & g,GIAC_CONTEXT) ;
  gen _unitV(const gen & g,GIAC_CONTEXT) ;
  gen _rowNorm(const gen & g,GIAC_CONTEXT) ;
  gen _colNorm(const gen & g,GIAC_CONTEXT) ;
  gen _Archive(const gen & g,GIAC_CONTEXT);
  gen _Unarchiv(const gen & g,GIAC_CONTEXT);
  gen _ClrIO(const gen & g,GIAC_CONTEXT);
  gen _Output(const gen & g,GIAC_CONTEXT);
  gen _getKey(const gen & g,GIAC_CONTEXT);
  gen _DelFold(const gen & g,GIAC_CONTEXT);
  gen _DispG(const gen & g,GIAC_CONTEXT);
  gen _DispHome(const gen & g,GIAC_CONTEXT);
  gen _Exec(const gen & g,GIAC_CONTEXT);
  gen _NewFold(const gen & g,GIAC_CONTEXT);
  gen _GetFold(const gen & g,GIAC_CONTEXT);
  gen _StoPic(const gen & g,GIAC_CONTEXT);
  gen _RclPic(const gen & g,GIAC_CONTEXT);
  gen _RplcPic(const gen & g,GIAC_CONTEXT);
  gen _ClrGraph(const gen & g,GIAC_CONTEXT);
  gen _PtOn(const gen & g,GIAC_CONTEXT);
  gen _PtOff(const gen & g,GIAC_CONTEXT);
  gen _Line(const gen & g,GIAC_CONTEXT);
  gen _LineHorz(const gen & g,GIAC_CONTEXT);
  gen _LineVert(const gen & g,GIAC_CONTEXT);
  gen _DrawSlp(const gen & g,GIAC_CONTEXT);
  gen _Circle(const gen & g,GIAC_CONTEXT);
  gen _PtText(const gen & g,GIAC_CONTEXT);
  gen _NewPic(const gen & g,GIAC_CONTEXT);
  gen _ZoomSto(const gen & g,GIAC_CONTEXT);
  gen _ZoomRcl(const gen & g,GIAC_CONTEXT);
  gen _deSolve(const gen & g,GIAC_CONTEXT);
  gen _LineTan(const gen & g,GIAC_CONTEXT);
  gen _CyclePic(const gen & g,GIAC_CONTEXT);
  gen _RandSeed(const gen & g,GIAC_CONTEXT);
  gen _Store(const gen & g,const context * contextptr);
  gen exact_double(double d,double eps);
  gen exact(const gen & g,GIAC_CONTEXT);
  gen fPart(const gen & g,GIAC_CONTEXT);
  gen simult(const gen & g,GIAC_CONTEXT);
  gen make_symbol(const std::string & s,GIAC_CONTEXT);
  gen _unarchive_ti(const gen & g,GIAC_CONTEXT);
  gen _sialorssinon(const gen & g,GIAC_CONTEXT);
  gen _pour(const gen & g,GIAC_CONTEXT);

#ifdef NSPIRE
  extern const unary_function_ptr * const  at_system;
#endif
  extern const unary_function_ptr * const  at_int;
  extern const unary_function_ptr * const  at_frac;
  extern const unary_function_ptr * const  at_exact ;
  extern const unary_function_ptr * const  at_Input;
  extern const unary_function_ptr * const  at_InputStr;
  extern const unary_function_ptr * const  at_Prompt;
  extern const unary_function_ptr * const  at_rowswap;
#ifndef RTOS_THREADX
#ifndef BESTA_OS
  extern unary_function_eval __getKey;
#endif
#endif
  extern const unary_function_ptr * const  at_getKey;
  extern const unary_function_ptr * const  at_numer;
  extern const unary_function_ptr * const  at_ClrIO;
  extern const unary_function_ptr * const  at_Output;
  extern const unary_function_ptr * const  at_DispG;
  extern const unary_function_ptr * const  at_real;
  extern const unary_function_ptr * const  at_et;
  extern const unary_function_ptr * const  at_oufr;
  extern const unary_function_ptr * const  at_DispHome;
  extern const unary_function_ptr * const  at_RclPic;
  extern const unary_function_ptr * const  at_RplcPic;

#ifndef RTOS_THREADX

  gen ti_decode_tag(octet * & ptr,GIAC_CONTEXT);
  gen ti2gen(octet * ptr,GIAC_CONTEXT);

  gen _unarchive_ti(const gen & args,GIAC_CONTEXT);
  extern const unary_function_ptr * const  at_unarchive_ti;

  // from TIGCC estack.h

  // typedef unsigned short HANDLE;

  enum Data_Tags {
    NONNEGATIVE_INTEGER_TAG=31,
    NEGATIVE_INTEGER_TAG=32,
    POSITIVE_FRACTION_TAG=33,
    NEGATIVE_FRACTION_TAG=34,
    FLOAT_TAG=35, // =BCD_TAG
    STR_DATA_TAG=45, // =STR_TAG
    LIST_TAG=217,
    USER_DEF_TAG=220, // 0xDC
    DATA_VAR_TAG=221,
    GDB_VAR_TAG=222,
    PIC_VAR_TAG=223,
    TEXT_VAR_TAG=224, //0xE0
    COMMAND_TAG=228,
    END_TAG=229, // 0xE5
    END_OF_SEGMENT=233, // 0xE9
    ASM_PRGM_TAG=243,
    GEN_DATA_TEG=248
  };
  enum Tags{
    VAR_NAME_TAG=0x00,
    _VAR_Q_TAG=0x01,
    VAR_R_TAG=0x02,
    VAR_S_TAG=0x03,
    VAR_T_TAG=0x04,
    VAR_U_TAG=0x05,
    VAR_V_TAG=0x06,
    VAR_W_TAG=0x07,
    VAR_X_TAG=0x08,
    VAR_Y_TAG=0x09,
    VAR_Z_TAG=0x0A,
    VAR_A_TAG=0x0B,
    VAR_B_TAG=0x0C,
    VAR_C_TAG=0x0D,
    VAR_D_TAG=0x0E,
    VAR_E_TAG=0x0F,
    VAR_F_TAG=0x10,
    VAR_G_TAG=0x11,
    VAR_H_TAG=0x12,
    VAR_I_TAG=0x13,
    VAR_J_TAG=0x14,
    VAR_K_TAG=0x15,
    VAR_L_TAG=0x16,
    VAR_M_TAG=0x17,
    VAR_N_TAG=0x18,
    VAR_O_TAG=0x19,
    VAR_P_TAG=0x1A,
    VAR_Q_TAG=0x1B,
    EXT_SYSTEM_TAG=0x1C, // followed by a Sysvar_Tag see enum below
    ARB_REAL_TAG=0x1D,
    ARB_INT_TAG=0x1E,
    POSINT_TAG=0x1F,
    NEGINT_TAG=0x20,
    POSFRAC_TAG=0x21,
    NEGFRAC_TAG=0x22,
    // FLOAT_TAG=0x23,
    BCD_TAG=0x23,
    PI_TAG=0x24,
    EXP_TAG=0x25,
    IM_TAG=0x26,
    NEGINFINITY_TAG=0x27,
    INFINITY_TAG=0x28,
    PN_INFINITY_TAG=0x29,
    UNDEF_TAG=0x2A,
    FALSE_TAG=0x2B,
    TRUE_TAG=0x2C,
    STR_TAG=0x2D,
    NOTHING_TAG=0x2E,
    ACOSH_TAG=0x2F,
    ASINH_TAG=0x30,
    ATANH_TAG=0x31,
    COSH_TAG=0x35,
    SINH_TAG=0x36,
    TANH_TAG=0x37,
    ACOS_TAG=0x3B,
    ASIN_TAG=0x3C,
    ATAN_TAG=0x3D,
    RACOS_TAG=0x41,
    RASIN_TAG=0x42,
    RATAN_TAG=0x43,
    COS_TAG=0x44,
    SIN_TAG=0x45,
    TAN_TAG=0x46,
    ITAN_TAG=0x4A,
    ABS_TAG=0x4B,
    ANGLE_TAG=0x4C,
    CEILING_TAG=0x4D,
    FLOOR_TAG=0x4E,
    INT_TAG=0x4F,
    SIGN_TAG=0x50,
    SQRT_TAG=0x51,
    EXPF_TAG=0x52,
    LN_TAG=0x53,
    LOG_TAG=0x54,
    FPART_TAG=0x55,
    IPART_TAG=0x56,
    CONJ_TAG=0x57,
    IMAG_TAG=0x58,
    REAL_TAG=0x59,
    APPROX_TAG=0x5A,
    TEXPAND_TAG=0x5B,
    TCOLLECT_TAG=0x5C,
    GETDENOM_TAG=0x5D,
    GETNUM_TAG=0x5E,
    CUMSUM_TAG=0x60,
    DET_TAG=0x61,
    COLNORM_TAG=0x62,
    ROWNORM_TAG=0x63,
    NORM_TAG=0x64,
    MEAN_TAG=0x65,
    MEDIAN_TAG=0x66,
    PRODUCT_TAG=0x67,
    STDDEV_TAG=0x68,
    SUM_TAG=0x69,
    VARIANCE_TAG=0x6A,
    UNITV_TAG=0x6B,
    DIM_TAG=0x6C,
    MAT2LIST_TAG=0x6D,
    NEWLIST_TAG=0x6E,
    RREF_TAG=0x6F,
    REF_TAG=0x70,
    IDENTITY_TAG=0x71,
    DIAG_TAG=0x72,
    COLDIM_TAG=0x73,
    ROWDIM_TAG=0x74,
    TRANSPOSE_TAG=0x75,
    FACTORIAL_TAG=0x76,
    PERCENT_TAG=0x77,
    RADIANS_TAG=0x78,
    NOT_TAG=0x79,
    MINUS_TAG=0x7A,
    VEC_POLAR_TAG=0x7B,
    VEC_CYLIND_TAG=0x7C,
    VEC_SPHERE_TAG=0x7D,
    START_TAG=0x7E,
    ISTORE_TAG=0x7F,
    STORE_TAG=0x80,
    WITH_TAG=0x81,
    XOR_TAG=0x82,
    OR_TAG=0x83,
    AND_TAG=0x84,
    LT_TAG=0x85,
    LE_TAG=0x86,
    EQ_TAG=0x87,
    GE_TAG=0x88,
    GT_TAG=0x89,
    NE_TAG=0x8A,
    ADD_TAG=0x8B,
    ADDELT_TAG=0x8C,
    SUB_TAG=0x8D,
    SUBELT_TAG=0x8E,
    MUL_TAG=0x8F,
    MULELT_TAG=0x90,
    DIV_TAG=0x91,
    DIVELT_TAG=0x92,
    POW_TAG=0x93,
    POWELT_TAG=0x94,
    SINCOS_TAG=0x95,
    SOLVE_TAG=0x96,
    CSOLVE_TAG=0x97,
    NSOLVE_TAG=0x98,
    ZEROS_TAG=0x99,
    CZEROS_TAG=0x9A,
    FMIN_TAG=0x9B,
    FMAX_TAG=0x9C,
    COMPLEX_TAG=0x9D,
    POLYEVAL_TAG=0x9E,
    RANDPOLY_TAG=0x9F,
    CROSSP_TAG=0xA0,
    DOTP_TAG=0xA1,
    GCD_TAG=0xA2,
    LCM_TAG=0xA3,
    MOD_TAG=0xA4,
    INTDIV_TAG=0xA5,
    REMAIN_TAG=0xA6,
    NCR_TAG=0xA7,
    NPR_TAG=0xA8,
    P2RX_TAG=0xA9,
    P2RY_TAG=0xAA,
    P2PTHETA_TAG=0xAB,
    P2PR_TAG=0xAC,
    AUGMENT_TAG=0xAD,
    NEWMAT_TAG=0xAE,
    RANDMAT_TAG=0xAF,
    SIMULT_TAG=0xB0,
    PART_TAG=0xB1,
    EXP2LIST_TAG=0xB2,
    RANDNORM_TAG=0xB3,
    MROW_TAG=0xB4,
    ROWADD_TAG=0xB5,
    ROWSWAP_TAG=0xB6,
    ARCLEN_TAG=0xB7,
    NINT_TAG=0xB8,
    PI_PRODUCT_TAG=0xB9,
    SIGMA_SUM_TAG=0xBA,
    MROWADD_TAG=0xBB,
    ANS_TAG=0xBC,
    ENTRY_TAG=0xBD,
    EXACT_TAG=0xBE,
    LOGB_TAG=0xBF,
    COMDENOM_TAG=0xC0,
    EXPAND_TAG=0xC1,
    FACTOR_TAG=0xC2,
    CFACTOR_TAG=0xC3,
    INTEGRATE_TAG=0xC4,
    DIFFERENTIATE_TAG=0xC5,
    AVGRC_TAG=0xC6,
    NDERIV_TAG=0xC7,
    TAYLOR_TAG=0xC8,
    LIMIT_TAG=0xC9,
    PROPFRAC_TAG=0xCA,
    WHEN_TAG=0xCB,
    ROUND_TAG=0xCC,
    DMS_TAG=0xCD,
    LEFT_TAG=0xCE,
    RIGHT_TAG=0xCF,
    MID_TAG=0xD0,
    SHIFT_TAG=0xD1,
    SEQ_TAG=0xD2,
    LIST2MAT_TAG=0xD3,
    SUBMAT_TAG=0xD4,
    SUBSCRIPT_TAG=0xD5,
    RAND_TAG=0xD6,
    MIN_TAG=0xD7,
    MAX_TAG=0xD8,
    // LIST_TAG=0xD9,
    USERFUNC_TAG=0xDA,
    MATRIX_TAG=0xDB,
    FUNC_TAG=0xDC,
    DATA_TAG=0xDD,
    GDB_TAG=0xDE,
    PIC_TAG=0xDF,
    TEXT_TAG=0xE0,
    FIG_TAG=0xE1,
    MAC_TAG=0xE2,
    EXT_TAG=0xE3, // see Ext_Tags enumeration below
    EXT_INSTR_TAG=0xE4, // see Instruction_Tags enumeration below
    // END_TAG=0xE5,
    COMMENT_TAG=0xE6,
    NEXTEXPR_TAG=0xE7,
    NEWLINE_TAG=0xE8,
    ENDSTACK_TAG=0xE9,
    PN1_TAG=0xEA,
    PN2_TAG=0xEB,
    ERROR_MSG_TAG=0xEC,
    EIGVC_TAG=0xED,
    EIGVL_TAG=0xEE,
    DASH_TAG=0xEF,
    LOCALVAR_TAG=0xF0,
    DESOLVE_TAG=0xF1,
    FDASH_TAG=0xF2,
    ASM_TAG=0xF3,
    ISPRIME_TAG=0xF4,
    OTH_TAG=0xF8,
    ROTATE_TAG=0xF9
  };

  // following a SYSVAR_TAG (0x1C)
  enum SysvarTags{
    X_BAR_TAG=1,
    Y_BAR_TAG=2,
    SIGMA_X_TAG=3,
    SIGMA_X2_TAG=4,
    SIGMA_Y_TAG=5,
    SIGMA_Y2_TAG=6,
    SIGMA_XY_TAG=7,
    SX_TAG=8,
    SY_TAG=9,
    SMLSIGMA_X_TAG=0xA,
    SMLSIGMA_Y_TAG=0xB,
    NSTAT_TAG=0xC,
    MINX_TAG=0xD,
    MINY_TAG=0xE,
    Q1_TAG=0xF,
    MEDSTAT_TAG=0x10,
    Q3_TAG=0x11,
    MAXX_TAG=0x12,
    MAXY_TAG=0x13,
    CORR_TAG=0x14,
    R2_TAG=0x15,
    MEDX1_TAG=0x16,
    MEDX2_TAG=0x17,
    MEDX3_TAG=0x18,
    MEDY1_TAG=0x19,
    MEDY2_TAG=0x1A,
    MEDY3_TAG=0x1B,
    XC_TAG=0x1C,
    YC_TAG=0x1D,
    ZC_TAG=0x1E,
    TC_TAG=0x1F,
    RC_TAG=0x20,
    THETA_C_TAG=0x21,
    NC_TAG=0x22,
    XFACT_TAG=0x23,
    YFACT_TAG=0x24,
    ZFACT_TAG=0x25,
    XMIN_TAG=0x26,
    XMAX_TAG=0x27,
    XSCL_TAG=0x28,
    YMIN_TAG=0x29,
    YMAX_TAG=0x2A,
    YSCL_TAG=0x2B,
    DELTA_X_TAG=0x2C,
    DELTA_Y_TAG=0x2D,
    XRES_TAG=0x2E,
    XGRID_TAG=0x2F,
    YGRID_TAG=0x30,
    ZMIN_TAG=0x31,
    ZMAX_TAG=0x32,
    ZSCL_TAG=0x33,
    EYE_THETA_TAG=0x34,
    EYE_PHI_TAG=0x35,
    THETA_MIN_TAG=0x36,
    THETA_MAX_TAG=0x37,
    THETA_STEP_TAG=0x38,
    TMIN_TAG=0x39,
    TMAX_TAG=0x3A,
    TSTEP_TAG=0x3B,
    NMIN_TAG=0x3C,
    NMAX_TAG=0x3D,
    PLOTSTRT_TAG=0x3E,
    PLOTSTEP_TAG=0x3F,
    ZXMIN_TAG=0x40,
    ZXMAX_TAG=0x41,
    ZXSCL_TAG=0x42,
    ZYMIN_TAG=0x43,
    ZYMAX_TAG=0x44,
    ZYSCL_TAG=0x45,
    ZXRES_TAG=0x46,
    Z_THETA_MIN_TAG=0x47,
    Z_THETA_MAX_TAG=0x48,
    Z_THETA_STEP_TAG=0x49,
    ZTMIN_TAG=0x4A,
    ZTMAX_TAG=0x4B,
    ZTSTEP_TAG=0x4C,
    ZXGRID_TAG=0x4D,
    ZYGRID_TAG=0x4E,
    ZZMIN_TAG=0x4F,
    ZZMAX_TAG=0x50,
    ZZSCL_TAG=0x51,
    ZEYE_THETA_TAG=0x52,
    ZEYE_PHI_TAG=0x53,
    ZNMIN_TAG=0x54,
    ZNMAX_TAG=0x55,
    ZPLTSTEP_TAG=0x56,
    ZPLTSTRT_TAG=0x57,
    SEED1_TAG=0x58,
    SEED2_TAG=0x59,
    OK_TAG=0x5A,
    ERRORNUM_TAG=0x5B,
    SYSMATH_TAG=0x5C,
    SYSDATA_TAG=0x5D,
    REGEQ_TAG=0x5E,
    REGCOEF_TAG=0x5F,
    TBLINPUT_TAG=0x60,
    TBLSTART_TAG=0x61,
    DELTA_TBL_TAG=0x62,
    FLDPIC_TAG=0x63,
    EYE_PSI_TAG=0x64,
    TPLOT_TAG=0x65,
    DIFTOL_TAG=0x66,
    ZEYE_PSI_TAG=0x67,
    T0_TAG=0x68,
    DTIME_TAG=0x69,
    NCURVES_TAG=0x6A,
    FLDRES_TAG=0x6B,
    ESTEP_TAG=0x6C,
    ZT0DE_TAG=0x6D,
    ZTMAXDE_TAG=0x6E,
    ZTSTEPDE_TAG=0x6F,
    ZTPLOTDE_TAG=0x70,
    NCONTOUR_TAG=0x71
  };

  // second byte, following EXT_TAG (0xE3)
  enum ExtTags {
    INDIR_TAG=0x01,
    GETKEY_TAG=0x02,
    GETFOLD_TAG=0x03,
    SWITCH_TAG=0x04,
    UNITCONV_TAG=0x05,
    ORD_TAG=0x06,
    EXPR_TAG=0x07,
    CHAR_TAG=0x08,
    STRING_TAG=0x09,
    GETTYPE_TAG=0x0A,
    GETMODE_TAG=0x0B,
    SETFOLD_TAG=0x0C,
    PTTEST_TAG=0x0D,
    PXLTEST_TAG=0x0E,
    SETGRAPH_TAG=0x0F,
    SETTABLE_TAG=0x10,
    SETMODE_TAG=0x11,
    FORMAT_TAG=0x12,
    INSTRING_TAG=0x13,
    APPEND_TAG=0x14,
    DD_TAG=0x15,
    EXPR2DMS_TAG=0x16,
    VEC2RECT_TAG=0x17,
    VEC2POLAR_TAG=0x18,
    VEC2CYLIND_TAG=0x19,
    VEC2SPHERE_TAG=0x1A,
    PARENTH_START_TAG=0x1B,
    PARENTH_END_TAG=0x1C,
    MAT_START_TAG=0x1D,
    MAT_END_TAG=0x1E,
    LIST_START_TAG=0x1F,
    LIST_END_TAG=0x20,
    COMMA_TAG=0x21,
    SEMICOLON_TAG=0x22,
    COMPLEX_ANGLE_TAG=0x23,
    SINGLE_QUOTE_TAG=0x24,
    QUOTE_TAG=0x25,
    POLCPLX_TAG=0x26,
    TMPCNV_TAG=0x27,
    DELTA_TMPCNV_TAG=0x28,
    GETUNITS_TAG=0x29,
    SETUNITS_TAG=0x2A,
    BIN_TAG=0x2B,
    HEX_TAG=0x2C,
    INT2BIN_TAG=0x2D,
    INT2DEC_TAG=0x2E,
    INT2HEX_TAG=0x2F,
    DET_TOL_TAG=0x30,
    REF_TOL_TAG=0x31,
    RREF_TOL_TAG=0x32,
    SIMULT_TOL_TAG=0x33,
    GETCONFG_TAG=0x34,
    V_AUGMENT_TAG=0x35,
    VARIANCE_TWOARG_TAG=0x3A
  };

  // following INST_TAG (0xE4)=COMMAND_TAG
  enum InstructionTags {
    CLRDRAW_ITAG=1,
    CLRGRAPH_ITAG=2,
    CLRHOME_ITAG=3,
    CLRIO_ITAG=4,
    CLRTABLE_ITAG=5,
    CUSTOM_ITAG=6,
    CYCLE_ITAG=7,
    DIALOG_ITAG=8,
    DISPG_ITAG=9,
    DISPTBL_ITAG=0xA,
    ELSE_ITAG=0xB,
    ENDCUSTM_ITAG=0xC,
    ENDDLOG_ITAG=0xD,
    ENDFOR_ITAG=0xE,
    ENDFUNC_ITAG=0xF,
    ENDIF_ITAG=0x10,
    ENDLOOP_ITAG=0x11,
    ENDPRGM_ITAG=0x12,
    ENDTBAR_ITAG=0x13,
    ENDTRY_ITAG=0x14,
    ENDWHILE_ITAG=0x15,
    EXIT_ITAG=0x16,
    FUNC_ITAG=0x17,
    LOOP_ITAG=0x18,
    PRGM_ITAG=0x19,
    SHOWSTAT_ITAG=0x1A,
    STOP_ITAG=0x1B,
    THEN_ITAG=0x1C,
    TOOLBAR_ITAG=0x1D,
    TRACE_ITAG=0x1E,
    TRY_ITAG=0x1F,
    ZOOMBOX_ITAG=0x20,
    ZOOMDATA_ITAG=0x21,
    ZOOMDEC_ITAG=0x22,
    ZOOMFIT_ITAG=0x23,
    ZOOMIN_ITAG=0x24,
    ZOOMINT_ITAG=0x25,
    ZOOMOUT_ITAG=0x26,
    ZOOMPREV_ITAG=0x27,
    ZOOMRCL_ITAG=0x28,
    ZOOMSQR_ITAG=0x29,
    ZOOMSTD_ITAG=0x2A,
    ZOOMSTO_ITAG=0x2B,
    ZOOMTRIG_ITAG=0x2C,
    DRAWFUNC_ITAG=0x2D,
    DRAWINV_ITAG=0x2E,
    GOTO_ITAG=0x2F,
    LBL_ITAG=0x30,
    GET_ITAG=0x31,
    SEND_ITAG=0x32,
    GETCALC_ITAG=0x33,
    SENDCALC_ITAG=0x34,
    NEWFOLD_ITAG=0x35,
    PRINTOBJ_ITAG=0x36,
    RCLGDB_ITAG=0x37,
    STOGDB_ITAG=0x38,
    ELSEIF_ITAG=0x39,
    IF_ITAG=0x3A,
    IFTHEN_ITAG=0x3B,
    RANDSEED_ITAG=0x3C,
    WHILE_ITAG=0x3D,
    LINETAN_ITAG=0x3E,
    COPYVAR_ITAG=0x3F,
    RENAME_ITAG=0x40,
    STYLE_ITAG=0x41,
    FILL_ITAG=0x42,
    REQUEST_ITAG=0x43,
    POPUP_ITAG=0x44,
    PTCHG_ITAG=0x45,
    PTOFF_ITAG=0x46,
    PTON_ITAG=0x47,
    PXLCHG_ITAG=0x48,
    PXLOFF_ITAG=0x49,
    PXLON_ITAG=0x4A,
    MOVEVAR_ITAG=0x4B,
    DROPDOWN_ITAG=0x4C,
    OUTPUT_ITAG=0x4D,
    PTTEXT_ITAG=0x4E,
    PXLTEXT_ITAG=0x4F,
    DRAWSLP_ITAG=0x50,
    PAUSE_ITAG=0x51,
    RETURN_ITAG=0x52,
    INPUT_ITAG=0x53,
    PLOTSOFF_ITAG=0x54,
    PLOTSON_ITAG=0x55,
    TITLE_ITAG=0x56,
    ITEM_ITAG=0x57,
    INPUTSTR_ITAG=0x58,
    LINEHORZ_ITAG=0x59,
    LINEVERT_ITAG=0x5A,
    PXLHORZ_ITAG=0x5B,
    PXLVERT_ITAG=0x5C,
    ANDPIC_ITAG=0x5D,
    RCLPIC_ITAG=0x5E,
    RPLCPIC_ITAG=0x5F,
    XORPIC_ITAG=0x60,
    DRAWPOL_ITAG=0x61,
    TEXT_ITAG=0x62,
    ONEVAR_ITAG=0x63,
    STOPIC_ITAG=0x64,
    GRAPH_ITAG=0x65,
    TABLE_ITAG=0x66,
    NEWPIC_ITAG=0x67,
    DRAWPARM_ITAG=0x68,
    CYCLEPIC_ITAG=0x69,
    CUBICREG_ITAG=0x6A,
    EXPREG_ITAG=0x6B,
    LINREG_ITAG=0x6C,
    LNREG_ITAG=0x6D,
    MEDMED_ITAG=0x6E,
    POWERREG_ITAG=0x6F,
    QUADREG_ITAG=0x70,
    QUARTREG_ITAG=0x71,
    TWOVAR_ITAG=0x72,
    SHADE_ITAG=0x73,
    FOR_ITAG=0x74,
    CIRCLE_ITAG=0x75,
    PXLCRCL_ITAG=0x76,
    NEWPLOT_ITAG=0x77,
    LINE_ITAG=0x78,
    PXLLINE_ITAG=0x79,
    DISP_ITAG=0x7A,
    FNOFF_ITAG=0x7B,
    FNON_ITAG=0x7C,
    LOCAL_ITAG=0x7D,
    DELFOLD_ITAG=0x7E,
    DELVAR_ITAG=0x7F,
    LOCK_ITAG=0x80,
    PROMPT_ITAG=0x81,
    SORTA_ITAG=0x82,
    SORTD_ITAG=0x83,
    UNLOCK_ITAG=0x84,
    NEWDATA_ITAG=0x85,
    DEFINE_ITAG=0x86,
    ELSE_TRY_ITAG=0x87,
    CLRERR_ITAG=0x88,
    PASSERR_ITAG=0x89,
    DISPHOME_ITAG=0x8A,
    EXEC_ITAG=0x8B,
    ARCHIVE_ITAG=0x8C,
    UNARCHIV_ITAG=0x8D,
    LU_ITAG=0x8E,
    QR_ITAG=0x8F,
    BLDDATA_ITAG=0x90,
    DRWCTOUR_ITAG=0x91,
    NEWPROB_ITAG=0x92,
    SINREG_ITAG=0x93,
    LOGISTIC_ITAG=0x94,
    CUSTMON_ITAG=0x95,
    CUSTMOFF_ITAG=0x96,
    SENDCHAT_ITAG=0x97
  };

#endif //RTOS_THREADX

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC


#endif // _GIAC_TI9X_H
