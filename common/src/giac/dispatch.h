/*  All these constants are #define to be used in switch () case :
 *  Copyright (C) 2000 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

#ifndef _GIAC_DISPATCH_H
#define _GIAC_DISPATCH_H
#include "global.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

#if !defined DOUBLEVAL && (defined __amd64 || defined __x86_64) && !defined SMARTPTR64 
#define DOUBLEVAL 1
#endif

  enum {
    _DECALAGE=8, // 2^8=256
    _DISPATCHBASE=256,
    _QUOTE_ARGUMENTS=1
  } ;

  enum gen_unary_types {
    // immediate type (without mem allocation) should be < _ZINT
    _INT_= 0, // int val
    _DOUBLE_= 1, // double _DOUBLE_val
    // all type below or equal to _DOUBLE_ must be non pointers
    _ZINT= 2, // mpz_t * _ZINTptr
    _REAL= 3, // mpf_t * _REALptr
    // all type strictly below _CPLX must be real types
    _CPLX= 4, // gen * _CPLXptr
    _POLY= 5, // polynome * _POLYptr
    _IDNT= 6, // identificateur * _IDNTptr
    _VECT= 7, // vecteur * _VECTptr
    _SYMB= 8, // symbolic * _SYMBptr
    _SPOL1= 9, // sparse_poly1 * _SPOL1ptr
    _FRAC= 10, // fraction * _FRACptr
    _EXT= 11, // gen * _EXTptr
    _STRNG= 12, // string * _STRNGptr
    _FUNC= 13, // unary_fonction_ptr * _FUNCptr
    _ROOT= 14, // real_complex_rootof *_ROOTptr
    _MOD= 15, // gen * _MODptr
    _USER= 16, // gen_user * _USERptr
    _MAP=17, // map<gen.gen> * _MAPptr
    _EQW=18, // eqwdata * _EQWptr
    _GROB=19, // grob * _GROBptr
    _POINTER_=20, // void * _POINTER_val
    _FLOAT_=21 // immediate, _FLOAT_val
  } ;

  enum gen_binary_types {
    _INT___INT_ = _INT_*_DISPATCHBASE+_INT_,
    _INT___ZINT = _INT_*_DISPATCHBASE+_ZINT,
    _INT___CPLX = _INT_*_DISPATCHBASE+_CPLX,
    _INT___POLY = _INT_*_DISPATCHBASE+_POLY,
    _INT___IDNT = _INT_*_DISPATCHBASE+_IDNT,
    _INT___SYMB = _INT_*_DISPATCHBASE+_SYMB,
    _INT___DOUBLE_ = _INT_*_DISPATCHBASE+_DOUBLE_,
    _INT___FLOAT_ = _INT_*_DISPATCHBASE+_FLOAT_,
    _INT___VECT = _INT_*_DISPATCHBASE+_VECT,
    _ZINT__INT_ = _ZINT*_DISPATCHBASE+_INT_,
    _ZINT__ZINT =_ZINT*_DISPATCHBASE+_ZINT,
    _ZINT__CPLX = _ZINT*_DISPATCHBASE+_CPLX,
    _ZINT__POLY =_ZINT*_DISPATCHBASE+_POLY,
    _ZINT__IDNT = _ZINT*_DISPATCHBASE+_IDNT,
    _ZINT__SYMB = _ZINT*_DISPATCHBASE+_SYMB,
    _ZINT__DOUBLE_ = _ZINT*_DISPATCHBASE+_DOUBLE_,
    _ZINT__FLOAT_ = _ZINT*_DISPATCHBASE+_FLOAT_,
    _ZINT__VECT = _ZINT*_DISPATCHBASE+_VECT,
    _CPLX__INT_ = _CPLX*_DISPATCHBASE+_INT_,
    _CPLX__ZINT = _CPLX*_DISPATCHBASE+_ZINT,
    _CPLX__CPLX = _CPLX*_DISPATCHBASE+_CPLX,
    _CPLX__POLY = _CPLX*_DISPATCHBASE+_POLY,
    _CPLX__IDNT = _CPLX*_DISPATCHBASE+_IDNT,
    _CPLX__SYMB = _CPLX*_DISPATCHBASE+_SYMB,
    _CPLX__DOUBLE_ = _CPLX*_DISPATCHBASE+_DOUBLE_,
    _CPLX__FLOAT_ = _CPLX*_DISPATCHBASE+_FLOAT_,
    _CPLX__REAL = _CPLX*_DISPATCHBASE+_REAL,
    _CPLX__VECT = _CPLX*_DISPATCHBASE+_VECT,
    _POLY__INT_ = _POLY*_DISPATCHBASE+_INT_,
    _POLY__ZINT = _POLY*_DISPATCHBASE+_ZINT,
    _POLY__CPLX = _POLY*_DISPATCHBASE+_CPLX,
    _POLY__POLY = _POLY*_DISPATCHBASE+_POLY,
    _POLY__IDNT = _POLY*_DISPATCHBASE+_IDNT,
    _POLY__SYMB = _POLY*_DISPATCHBASE+_SYMB,
    _POLY__DOUBLE_ = _POLY*_DISPATCHBASE+_DOUBLE_,
    _POLY__FLOAT_ = _POLY*_DISPATCHBASE+_FLOAT_,
    _POLY__VECT = _POLY*_DISPATCHBASE+_VECT,
    _POLY__USER = _POLY*_DISPATCHBASE+_USER,
    _POLY__REAL = _POLY*_DISPATCHBASE+_REAL,
    _POLY__EXT = _POLY*_DISPATCHBASE+_EXT,
    _IDNT__INT_=  _IDNT*_DISPATCHBASE+_INT_,
    _IDNT__ZINT = _IDNT*_DISPATCHBASE+_ZINT,
    _IDNT__CPLX = _IDNT*_DISPATCHBASE+_CPLX,
    _IDNT__POLY = _IDNT*_DISPATCHBASE+_POLY,
    _IDNT__IDNT = _IDNT*_DISPATCHBASE+_IDNT,
    _IDNT__SYMB = _IDNT*_DISPATCHBASE+_SYMB,
    _IDNT__DOUBLE_ = _IDNT*_DISPATCHBASE+_DOUBLE_,
    _IDNT__FLOAT_ = _IDNT*_DISPATCHBASE+_FLOAT_,
    _IDNT__VECT = _IDNT*_DISPATCHBASE+_VECT,
    _SYMB__INT_=  _SYMB*_DISPATCHBASE+_INT_,
    _SYMB__ZINT = _SYMB*_DISPATCHBASE+_ZINT,
    _SYMB__CPLX = _SYMB*_DISPATCHBASE+_CPLX,
    _SYMB__POLY = _SYMB*_DISPATCHBASE+_POLY,
    _SYMB__IDNT = _SYMB*_DISPATCHBASE+_IDNT,
    _SYMB__SYMB = _SYMB*_DISPATCHBASE+_SYMB,
    _SYMB__DOUBLE_ = _SYMB*_DISPATCHBASE+_DOUBLE_,
    _SYMB__FLOAT_ = _SYMB*_DISPATCHBASE+_FLOAT_,
    _SYMB__VECT = _SYMB*_DISPATCHBASE+_VECT,
    _DOUBLE___INT_=  _DOUBLE_*_DISPATCHBASE+_INT_,
    _DOUBLE___ZINT = _DOUBLE_*_DISPATCHBASE+_ZINT,
    _DOUBLE___CPLX = _DOUBLE_*_DISPATCHBASE+_CPLX,
    _DOUBLE___POLY = _DOUBLE_*_DISPATCHBASE+_POLY,
    _DOUBLE___IDNT = _DOUBLE_*_DISPATCHBASE+_IDNT,
    _DOUBLE___SYMB = _DOUBLE_*_DISPATCHBASE+_SYMB,
    _DOUBLE___DOUBLE_ = _DOUBLE_*_DISPATCHBASE+_DOUBLE_,
    _DOUBLE___FLOAT_ = _DOUBLE_*_DISPATCHBASE+_FLOAT_,
    _DOUBLE___VECT = _DOUBLE_*_DISPATCHBASE+_VECT,
    _DOUBLE___REAL = _DOUBLE_*_DISPATCHBASE+_REAL,
    _DOUBLE___FRAC = _DOUBLE_*_DISPATCHBASE+_FRAC,
    _FLOAT___INT_=  _FLOAT_*_DISPATCHBASE+_INT_,
    _FLOAT___ZINT = _FLOAT_*_DISPATCHBASE+_ZINT,
    _FLOAT___CPLX = _FLOAT_*_DISPATCHBASE+_CPLX,
    _FLOAT___POLY = _FLOAT_*_DISPATCHBASE+_POLY,
    _FLOAT___IDNT = _FLOAT_*_DISPATCHBASE+_IDNT,
    _FLOAT___SYMB = _FLOAT_*_DISPATCHBASE+_SYMB,
    _FLOAT___FLOAT_ = _FLOAT_*_DISPATCHBASE+_FLOAT_,
    _FLOAT___DOUBLE_ = _FLOAT_*_DISPATCHBASE+_DOUBLE_,
    _FLOAT___VECT = _FLOAT_*_DISPATCHBASE+_VECT,
    _FLOAT___REAL = _FLOAT_*_DISPATCHBASE+_REAL,
    _FLOAT___FRAC = _FLOAT_*_DISPATCHBASE+_FRAC,
    _VECT__INT_=  _VECT*_DISPATCHBASE+_INT_,
    _VECT__DOUBLE_ = _VECT*_DISPATCHBASE+_DOUBLE_,
    _VECT__FLOAT_ = _VECT*_DISPATCHBASE+_FLOAT_,
    _VECT__ZINT = _VECT*_DISPATCHBASE+_ZINT,
    _VECT__CPLX = _VECT*_DISPATCHBASE+_CPLX,
    _VECT__POLY = _VECT*_DISPATCHBASE+_POLY,
    _VECT__IDNT = _VECT*_DISPATCHBASE+_IDNT,
    _VECT__SYMB = _VECT*_DISPATCHBASE+_SYMB,
    _VECT__VECT = _VECT*_DISPATCHBASE+_VECT,
    _VECT__EXT = _VECT*_DISPATCHBASE+_EXT,
    _VECT__FRAC = _VECT*_DISPATCHBASE+_FRAC,
    _VECT__REAL = _VECT*_DISPATCHBASE+_REAL,
    _FRAC__VECT = _FRAC*_DISPATCHBASE+_VECT,
    _FRAC__FRAC = _FRAC*_DISPATCHBASE+_FRAC,
    _FRAC__FLOAT_ = _FRAC*_DISPATCHBASE+_FLOAT_,
    _FRAC__DOUBLE_ = _FRAC*_DISPATCHBASE+_DOUBLE_,
    _FRAC__INT_=  _FRAC*_DISPATCHBASE+_INT_,
    _SPOL1__SPOL1 = _SPOL1*_DISPATCHBASE+_SPOL1,
    _EXT__EXT = _EXT*_DISPATCHBASE+_EXT,
    _EXT__VECT = _EXT*_DISPATCHBASE+_VECT,
    _EXT__INT_=  _EXT*_DISPATCHBASE+_INT_,
    _EXT__POLY=  _EXT*_DISPATCHBASE+_POLY,
    _STRNG__STRNG = _STRNG*_DISPATCHBASE+_STRNG,
    _FUNC__FUNC = _FUNC*_DISPATCHBASE+_FUNC,
    _MOD__MOD = _MOD*_DISPATCHBASE+_MOD,
    _ZINT__MOD = _ZINT*_DISPATCHBASE+_MOD,
    _MOD__ZINT = _MOD*_DISPATCHBASE+_ZINT,
    _MOD__VECT = _MOD*_DISPATCHBASE+_VECT,
    _VECT__MOD = _VECT*_DISPATCHBASE+_MOD,
    _MOD__POLY = _MOD*_DISPATCHBASE+_POLY,
    _POLY__MOD = _POLY*_DISPATCHBASE+_MOD,
    _INT___MOD=  _INT_*_DISPATCHBASE+_MOD,
    _MOD__INT_=  _MOD*_DISPATCHBASE+_INT_,
    _REAL__CPLX = _REAL*_DISPATCHBASE+_CPLX,
    _REAL__REAL = _REAL*_DISPATCHBASE+_REAL,
    _REAL__INT_ = _REAL*_DISPATCHBASE+_INT_,
    _REAL__DOUBLE_ = _REAL*_DISPATCHBASE+_DOUBLE_,
    _REAL__FLOAT_ = _REAL*_DISPATCHBASE+_FLOAT_,
    _REAL__POLY = _REAL*_DISPATCHBASE+_POLY,
    _REAL__VECT = _REAL*_DISPATCHBASE+_VECT,
    _USER__USER=  _USER*_DISPATCHBASE+_USER,
    _USER__INT_=  _USER*_DISPATCHBASE+_INT_,
    _USER__POLY=  _USER*_DISPATCHBASE+_POLY
  } ;

  enum comp_subtypes {
    _SEQ__VECT=1,
    _SET__VECT=2,
    _RPN_FUNC__VECT=3,
    _RPN_STACK__VECT=4,
    _GROUP__VECT=5,
    _LINE__VECT=6,
    _VECTOR__VECT=7,
    _PNT__VECT=8,
    _CURVE__VECT=8,
    _HALFLINE__VECT=9,
    _POLY1__VECT=10,
    _MATRIX__VECT=11,
    _RUNFILE__VECT=12,
    _ASSUME__VECT=13,
    _SPREAD__VECT=14,
    _CELL__VECT=15,
    _EQW__VECT=16,
    _HIST__VECT=17,
    _TILIST__VECT=0,
    _FOLDER__VECT=18,
    _SORTED__VECT=19,
    _POINT__VECT=20,
    _POLYEDRE__VECT=21,
    _RGBA__VECT=22,
    _LIST__VECT=23,
    _LOGO__VECT=24,
    _GGB__VECT=25,
  } ;

  enum symb_subtypes {
    _GLOBAL__EVAL =-1,
    _SPREAD__SYMB =123 // do not use this value elsewhere
  } ;

  enum point_styles {
    _STYLE_DOT=0,
    _STYLE_BOX=1,
    _STYLE_CROSS=2,
    _STYLE_PLUS=3
  };

  enum line_styles {
    _STYLE_FULL=0,
    _STYLE_DOTTED=1,
    _STYLE_DASHED=2
  };

  enum plot_options {
    _ADAPTIVE=0,
    _AXES=1,
    _COLOR=2,
    _FILLED=3,
    _FONT=4,
    _LABELS=5,
    _LEGEND=6,
    _LINESTYLE=7,
    _RESOLUTION=12,
    _SAMPLE=9,
    _SCALING=10,
    _STYLE=11,
    _SYMBOL=8, // same as _SYMB!
    _SYMBOLSIZE=13,
    _THICKNESS=14,
    _TITLE=15,
    _TITLEFONT=16,
    _VIEW=17,
    _AXESFONT=18,
    _COORDS=19,
    _LABELFONT=20,
    _LABELDIRECTIONS=21,
    _NUMPOINTS=22,
    _TICKMARKS=23,
    _XTICKMARKS=24,
    _XSTEP=25,
    _YSTEP=26,
    _ZSTEP=27,
    _TSTEP=28,
    _USTEP=29,
    _VSTEP=30,
    _NSTEP=31,
    _FRAMES=32,
    _GL_LIGHT0=33,
    _GL_LIGHT1=34,
    _GL_LIGHT2=35,
    _GL_LIGHT3=36,
    _GL_LIGHT4=37,
    _GL_LIGHT5=38,
    _GL_LIGHT6=39,
    _GL_LIGHT7=40,    
    _GL_AMBIENT=50,
    _GL_SPECULAR=51,
    _GL_DIFFUSE=52,
    _GL_POSITION=53,
    _GL_SPOT_DIRECTION=54,
    _GL_SPOT_EXPONENT=55,
    _GL_SPOT_CUTOFF=56,
    _GL_CONSTANT_ATTENUATION=57,
    _GL_LINEAR_ATTENUATION=58,
    _GL_QUADRATIC_ATTENUATION=59,
    _GL_LIGHT_MODEL_AMBIENT=60, 
    _GL_LIGHT_MODEL_LOCAL_VIEWER=61,
    _GL_LIGHT_MODEL_TWO_SIDE=62,
    _GL_LIGHT_MODEL_COLOR_CONTROL=72,
    _GL_OPTION=63,
    _GL_SMOOTH=64,
    _GL_FLAT=65,
    _GL_SHININESS=66,
    _GL_FRONT=67,
    _GL_BACK=68,
    _GL_FRONT_AND_BACK=69,
    _GL_AMBIENT_AND_DIFFUSE=70,
    _GL_EMISSION=71,
    _GL_SEPARATE_SPECULAR_COLOR=73,
    _GL_SINGLE_COLOR=74,
    _GL_BLEND=75,
    _GL_SRC_ALPHA=76,
    _GL_ONE_MINUS_SRC_ALPHA=77,
    _GL_MATERIAL=78,
    _GL_COLOR_INDEXES=79,
    _GL_LIGHT=80,
    _GL_PERSPECTIVE=81,
    _GL_ORTHO=82,
    _GL_QUATERNION=83,
    _GL_X=84,
    _GL_Y=85,
    _GL_Z=86,
    _GL_XTICK=87,
    _GL_YTICK=88,
    _GL_ZTICK=89,
    _GL_ANIMATE=90,
    _GL_SHOWAXES=91,
    _GL_SHOWNAMES=92,
    _GL_X_AXIS_NAME=93,
    _GL_Y_AXIS_NAME=94,
    _GL_Z_AXIS_NAME=95,
    _GL_X_AXIS_UNIT=96,
    _GL_Y_AXIS_UNIT=97,
    _GL_Z_AXIS_UNIT=98,
    _GL_TEXTURE=99,
    _GL_ROTATION_AXIS=100,
    _GL_X_AXIS_COLOR=101,
    _GL_Y_AXIS_COLOR=102,
    _GL_Z_AXIS_COLOR=103,
    _GL_LOGX=104,
    _GL_LOGY=105,
    _GL_LOGZ=106,
  };

  enum solver_methods {
    _BISECTION_SOLVER=0,
    _FALSEPOS_SOLVER=1,
    _BRENT_SOLVER=2,
    _NEWTON_SOLVER=3,
    _SECANT_SOLVER=4,
    _STEFFENSON_SOLVER=5,
    _HYBRIDSJ_SOLVER=6,
    _HYBRIDJ_SOLVER=7,
    _NEWTONJ_SOLVER=8,
    _HYBRIDS_SOLVER=9,
    _HYBRID_SOLVER=10,
    _DNEWTON_SOLVER=11,
    _GOLUB_REINSCH_DECOMP=12,
    _GOLUB_REINSCH_MOD_DECOMP=13,
    _JACOBI_DECOMP=14,
    _MINOR_DET=15,
    _HESSENBERG_PCAR=16,
    _RATIONAL_DET=17,
    _KEEP_PIVOT=18,
    _TRAPEZE=19,
    _RECTANGLE_DROIT=20,
    _RECTANGLE_GAUCHE=21,
    _POINT_MILIEU=22,
    _SIMPSON=23,
    _UNFACTORED=24,
    _FADEEV=25,
    _BAREISS=26,
    _ROMBERGT=27,
    _ROMBERGM=28
  };

  enum groebner_switches {
    _WITH_COCOA=0,
    _WITH_F5=1,
    _TDEG_ORDER=2,
    _PLEX_ORDER=3,
    _REVLEX_ORDER=4
  };

  enum int_subtypes {
    _INT_INT=0,
    _INT_TYPE=1,
    _INT_CHAR=2,
    _INT_ORDER=3,
    _INT_SOLVER=4,
    _INT_COLOR=5,
    _INT_BOOLEAN=6,
    _INT_PLOT=7,
    _INT_VECT=8,
    _INT_MAPLELIB=9,
    _INT_MAPLECONVERSION=10,
    _INT_MUPADOPERATOR=11,
    _INT_FD=12,
    _INT_COMP_SUBTYPE=13,
    _INT_GROEBNER=14
  };

  enum pointer_subtypes {
    _FILE_POINTER=0,
    _FL_WIDGET_POINTER=1,
    _FL_IMAGE_POINTER=2,
    _CONTEXT_POINTER=3,
    _THREAD_POINTER=4,
    _VARFUNCDEF_POINTER=5,
    _APPLET_POINTER=6
  };

  enum color_values {
#ifdef GIAC_HAS_STO_38
    _WHITE=0,
#else
    _BLACK=0,
#endif
    _RED=1,
    _GREEN=2,
    _YELLOW=3,
    _BLUE=4,
    _MAGENTA=5,
    _CYAN=6,
#ifdef GIAC_HAS_STO_38
    _BLACK=7,
#else
    _WHITE=7,
#endif
    _POINT_LOSANGE= 1 << 25,
    _POINT_PLUS = 1 << 26,
    _POINT_INVISIBLE = 1 << 27,
    _POINT_CARRE = 100663296,
    _POINT_TRIANGLE = 167772160,
    _POINT_ETOILE = 201326592, 
    _POINT_POINT = 234881024,
    _FILL_POLYGON = 1 << 30,
    _QUADRANT1 = 0,
    _QUADRANT2 = 1 << 28,
    _QUADRANT3 = 1 << 29,
    _QUADRANT4 = 805306368,
    _DASH_LINE = 1 << 22,
    _DOT_LINE = 2 << 22,
    _DASHDOT_LINE = 3 << 22,
    _DASHDOTDOT_LINE = 4 << 22,
    _CAP_FLAT_LINE = 5 << 22,
    _CAP_ROUND_LINE = 6 << 22,
    _CAP_SQUARE_LINE = 7 << 22,
    _LINE_WIDTH_1 = 0,
    _LINE_WIDTH_2 = 1 << 16,
    _LINE_WIDTH_3 = 2 << 16,
    _LINE_WIDTH_4 = 3 << 16,
    _LINE_WIDTH_5 = 4 << 16,
    _LINE_WIDTH_6 = 5 << 16,
    _LINE_WIDTH_7 = 6 << 16,
    _LINE_WIDTH_8 = 7 << 16,
    _POINT_WIDTH_1 = 0,
    _POINT_WIDTH_2 = 1 << 19,
    _POINT_WIDTH_3 = 2 << 19,
    _POINT_WIDTH_4 = 3 << 19,
    _POINT_WIDTH_5 = 4 << 19,
    _POINT_WIDTH_6 = 5 << 19,
    _POINT_WIDTH_7 = 6 << 19,
    _POINT_WIDTH_8 = 7 << 19,
    _HIDDEN_NAME = 1 << 31
  };

  enum maple_libs {
    _LINALG=0,
    _NUMTHEORY=1,
    _GROEBNER=2
  };

  enum maple_conversion {
    _TRIG=100,
    _EXPLN=101,
    _PARFRAC=102,
    _FULLPARFRAC=103,
    _BASE=104,
    _CONFRAC=105,
    _MAPLE_LIST=256,
    _POSINT=1*256+2,
    _NEGINT=2*256+2,
    _NONPOSINT=3*256+2,
    _NONNEGINT=4*256+2,
  };

  enum mupad_operator {
    _DELETE_OPERATOR=0,
    _PREFIX_OPERATOR=1,
    _POSTFIX_OPERATOR=2,
    _BINARY_OPERATOR=3,
    _NARY_OPERATOR=4
  };

  enum is_num_mask {
    num_mask_withint=1,
    num_mask_withfrac=2,
  };


#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

#endif // _GIAC_DISPATCH_H
