/*
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
#undef max
#undef min
//#define GIAC_VECTOR

#ifndef _GIAC_FIRST_H_
#define _GIAC_FIRST_H_
#ifdef RTOS_THREADX
#define NO_STDEXCEPT 1
#endif

#ifdef __sparc__
#define DOUBLEVAL
#define GIAC_NO_OPTIMIZATIONS
#endif

#ifndef DOUBLEVAL
// #define IMMEDIATE_VECTOR 12 // 48 extra bytes = 6 gens or 4 monomials or 3 sparse
#define IMMEDIATE_VECTOR 6 // 24 extra bytes = 3 gens or 2 monomials or 1 sparse
#endif
#define GIAC_MPZ_INIT_SIZE 128 // initial number of bits for mpz

typedef double giac_double;

#ifdef __x86_64__
#define alias_type ulonglong
#else
typedef unsigned long alias_type;
#endif

#ifdef NO_UNARY_FUNCTION_COMPOSE

#define define_unary_function_eval(name,ptr,name_s) const alias_unary_function_eval name={name_s,0,taylor,0,0,0,ptr,0}
#define define_unary_function_eval2(name,ptr,name_s,printptr) const alias_unary_function_eval name={name_s,0,taylor,printptr,0,0,ptr,0}
#define define_unary_function_eval3(name,ptr,derivee,name_s) const alias_unary_function_eval name={name_s,derivee,taylor,0,0,0,ptr,0}
#define define_unary_function_eval4(name,ptr,name_s,printptr,texprintptr) const alias_unary_function_eval name={name_s,0,taylor,printptr,texprintptr,0,ptr,0}
#define define_unary_function_eval5(name,ptr,derive,name_s,printptr,texprintptr) const alias_unary_function_eval name={name_s,derive,taylor,printptr,texprintptr,0,ptr,0}
#define define_unary_function_eval_taylor(name,ptr,derive,taylors,name_s) const alias_unary_function_eval name={name_s,derive,taylors,0,0,0,ptr,0}
#define define_unary_function_eval_taylor2(name,ptr,derive,taylors,name_s,printptr,texprintptr) const alias_unary_function_eval name={name_s,derive,taylors,printptr,texprintptr,0,ptr,0}
#define define_unary_function_eval_quoted(name,ptr,name_s) const alias_unary_function_eval name={name_s,0,taylor,0,0,0,ptr,1}
#define define_unary_function_eval2_quoted(name,ptr,name_s,printptr) const alias_unary_function_eval name={name_s,0,taylor,printptr,0,0,ptr,1}
#define define_unary_function_eval3_quoted(name,ptr,derivee,name_s) const alias_unary_function_eval name={name_s,derivee,taylor,0,0,0,ptr,1}
#define define_unary_function_eval4_quoted(name,ptr,name_s,printptr,texprintptr) const alias_unary_function_eval name={name_s,0,taylor,printptr,texprintptr,0,ptr,1}
#define define_unary_function_eval5_quoted(name,ptr,derive,name_s,printptr,texprintptr) const alias_unary_function_eval name={name_s,derive,taylor,printptr,texprintptr,0,ptr,1}
#define define_unary_function_eval_taylor_quoted(name,ptr,derive,taylors,name_s) const alias_unary_function_eval name={name_s,derive,taylors,0,0,0,ptr,1}
#define define_unary_function_eval_taylor2_quoted(name,ptr,derive,taylors,name_s,printptr,texprintptr) const alias_unary_function_eval name={name_s,derive,taylors,printptr,texprintptr,0,ptr,1}

#define define_unary_function_eval_index(u,name,ptr,name_s) const alias_unary_function_eval name={name_s,0,taylor,0,0,0,ptr,u}
#define define_unary_function_eval2_index(u,name,ptr,name_s,printptr) const alias_unary_function_eval name={name_s,0,taylor,printptr,0,0,ptr,u}
#define define_unary_function_eval3_index(u,name,ptr,derivee,name_s) const alias_unary_function_eval name={name_s,derivee,taylor,0,0,0,ptr,u}
#define define_unary_function_eval4_index(u,name,ptr,name_s,printptr,texprintptr) const alias_unary_function_eval name={name_s,0,taylor,printptr,texprintptr,0,ptr,u}
#define define_unary_function_eval5_index(u,name,ptr,derive,name_s,printptr,texprintptr) const alias_unary_function_eval name={name_s,derive,taylor,printptr,texprintptr,0,ptr,u}
#define define_unary_function_eval_taylor_index(u,name,ptr,derive,taylors,name_s) const alias_unary_function_eval name={name_s,derive,taylors,0,0,0,ptr,u}
#define define_unary_function_eval_taylor2_index(u,name,ptr,derive,taylors,name_s,printptr,texprintptr) const alias_unary_function_eval name={name_s,derive,taylors,printptr,texprintptr,0,ptr,u}

#define define_partial_derivative_onearg_unary_function_ptr(name,fcn) const unsigned long name=(const unsigned long) &fcn
#define define_partial_derivative_onearg_genop(name,name_s,genop) const alias_unary_function_eval name##unary_function_eval={name_s,0,taylor,0,0,0,genop,0}; const unsigned long name##unary_function_ptr = (const unsigned long)(&name##unary_function_eval); const unsigned long name=(const unsigned long) &name##unary_function_ptr

#else //  NO_UNARY_FUNCTION_COMPOSE

#define define_unary_function_eval(name,ptr,name_s) const unary_function_eval name(0,ptr,name_s)
#define define_unary_function_eval2(name,ptr,name_s,printptr) const unary_function_eval name(0,ptr,name_s,printptr)
#define define_unary_function_eval3(name,ptr,derivee,name_s) const unary_function_eval name(0,ptr,derivee,name_s)
#define define_unary_function_eval4(name,ptr,name_s,printptr,texprintptr) const unary_function_eval name(0,ptr,name_s,printptr,texprintptr)
#define define_unary_function_eval5(name,ptr,derive,name_s,printptr,texprintptr) const unary_function_eval name(0,ptr,derive,name_s,printptr,texprintptr)
#define define_unary_function_eval_taylor(name,ptr,derive,taylors,name_s) const unary_function_eval name(0,ptr,derive,taylors,name_s)
#define define_unary_function_eval_taylor2(name,ptr,derive,taylors,name_s,printptr,texprintptr) const unary_function_eval name(0,ptr,derive,taylors,name_s,printptr,texprintptr)
#define define_unary_function_eval_quoted(name,ptr,name_s) const unary_function_eval name(1,ptr,name_s)
#define define_unary_function_eval2_quoted(name,ptr,name_s,printptr) const unary_function_eval name(1,ptr,name_s,printptr)
#define define_unary_function_eval3_quoted(name,ptr,derivee,name_s) const unary_function_eval name(1,ptr,derivee,name_s)
#define define_unary_function_eval4_quoted(name,ptr,name_s,printptr,texprintptr) const unary_function_eval name(1,ptr,name_s,printptr,texprintptr)
#define define_unary_function_eval5_quoted(name,ptr,derive,name_s,printptr,texprintptr) const unary_function_eval name(1,ptr,derive,name_s,printptr,texprintptr)
#define define_unary_function_eval_taylor_quoted(name,ptr,derive,taylors,name_s) const unary_function_eval name(1,ptr,derive,taylors,name_s)
#define define_unary_function_eval_taylor2_quoted(name,ptr,derive,taylors,name_s,printptr,texprintptr) const unary_function_eval name(1,ptr,derive,taylors,name_s,printptr,texprintptr)
#define define_unary_function_eval_index(u,name,ptr,name_s) const unary_function_eval name(u,ptr,name_s)
#define define_unary_function_eval2_index(u,name,ptr,name_s,printptr) const unary_function_eval name(u,ptr,name_s,printptr)
#define define_unary_function_eval3_index(u,name,ptr,derivee,name_s) const unary_function_eval name(u,ptr,derivee,name_s)
#define define_unary_function_eval4_index(u,name,ptr,name_s,printptr,texprintptr) const unary_function_eval name(u,ptr,name_s,printptr,texprintptr)
#define define_unary_function_eval5_index(u,name,ptr,derive,name_s,printptr,texprintptr) const unary_function_eval name(u,ptr,derive,name_s,printptr,texprintptr)
#define define_unary_function_eval_taylor_index(u,name,ptr,derive,taylors,name_s) const unary_function_eval name(u,ptr,derive,taylors,name_s)
#define define_unary_function_eval_taylor2_index(u,name,ptr,derive,taylors,name_s,printptr,texprintptr) const unary_function_eval name(u,ptr,derive,taylors,name_s,printptr,texprintptr)

#define define_partial_derivative_onearg_genop(name,name_s,genop) static const unary_function_eval name##unary_function_eval(0,genop,name_s); static const unary_function_ptr name##unary_function_ptr(&name##unary_function_eval,0,0); static const partial_derivative_onearg name##partial_derivative_onearg(name##unary_function_ptr); const partial_derivative_onearg * name = &name##partial_derivative_onearg

#endif //  NO_UNARY_FUNCTION_COMPOSE

#ifdef __x86_64__
#define define_unary_function_ptr(name,alias_name,ptr) const ulonglong alias_name = (ulonglong)(ptr); const unary_function_ptr * const name = (const unary_function_ptr *) &alias_name
#else
#define define_unary_function_ptr(name,alias_name,ptr) const unsigned long alias_name = (unsigned long)(ptr); const unary_function_ptr * const name = (const unary_function_ptr *) &alias_name
#endif

#ifdef DOUBLEVAL 
#undef STATIC_BUILTIN_LEXER_FUNCTIONS
// otherwise change the definition of charptr_gen in input_lexer.ll
#endif

#ifdef STATIC_BUILTIN_LEXER_FUNCTIONS
#ifdef __x86_64__
#define define_unary_function_ptr5(name,alias_name,ptr,quoted,token) const ulonglong alias_name = ulonglong(ptr); const unary_function_ptr * const name = (const unary_function_ptr *) &alias_name;
#else
#define define_unary_function_ptr5(name,alias_name,ptr,quoted,token) const unsigned long alias_name = (unsigned long)(ptr); const unary_function_ptr * const name = (const unary_function_ptr *) &alias_name;
#endif
#else
#ifdef __x86_64__
#define define_unary_function_ptr5(name,alias_name,ptr,quoted,token) static const unary_function_ptr alias_name##_(ptr,quoted,token); const ulonglong alias_name=(ulonglong)ptr; const unary_function_ptr * const name = &alias_name##_;
#else
#define define_unary_function_ptr5(name,alias_name,ptr,quoted,token) static const unary_function_ptr alias_name##_(ptr,quoted,token); const unsigned long alias_name=(unsigned long)ptr; const unary_function_ptr * const name = &alias_name##_;
#endif
#endif


#ifdef __VISUALC__ 
#define GIAC_GENERIC_CONSTANTS
// Visual C++ is compiling
#define VISUALC
typedef long pid_t;
typedef __int64 longlong ;
typedef unsigned __int64 ulonglong ;
#ifndef M_PI
#define M_PI       3.14159265358979323846
#endif
#define YY_NO_UNISTD_H
#ifndef _HAS_ITERATOR_DEBUGGING
#define _HAS_ITERATOR_DEBUGGING 0
#endif
#ifndef _ITERATOR_DEBUG_LEVEL
#define _ITERATOR_DEBUG_LEVEL 0
#endif
#ifndef _SECURE_SCL
#define _SECURE_SCL 0
#endif
#ifndef _CRT_SECURE_NO_WARNINGS
#define _CRT_SECURE_NO_WARNINGS 1
#endif
#ifndef _CRT_NONSTDC_NO_DEPRECATE
#define _CRT_NONSTDC_NO_DEPRECATE 1
#endif
#else // __VISUALC__
typedef long long longlong;
typedef unsigned long long ulonglong;
#ifdef __x86_64__
  typedef int int128_t __attribute__((mode(TI)));
  typedef unsigned int uint128_t __attribute__((mode(TI)));
#ifndef INT128
#define INT128 1
#endif
#endif // __x86_64__

// do not define PSEUDO_MOD if a negative unsigned longlong >> 63 is != 0xffffffffffffffff
#define PSEUDO_MOD 
#endif // __VISUALC__


#ifndef __x86_64__
#ifdef SMARTPTR64
#undef SMARTPTR64
#endif // SMARTPTR64
#ifdef _I386_
#undef _I386_
#endif // _I386_
#else // __x86_64__
#define SMARTPTR64
#endif // __x86_64__

#ifdef USE_GMP_REPLACEMENTS
//#define GIAC_TYPE_ON_8BITS
#undef HAVE_GMPXX_H
#undef HAVE_LIBMPFR
#include "gmp_replacements.h"
#else
#include "gmp.h"
#endif

#include <cassert>

class init_gmp_memory 
{
	static int refcount;
	public:
	init_gmp_memory();
	~init_gmp_memory();
};
extern init_gmp_memory init_gmp_memory_instance;

#ifdef BCD
// #define ASPEN_GEOMETRY // required also here because of name resolution in identificateur.cc
// #define CAS38_DISABLED

#include <stdlib.h>
#include "bcd_float.h" 
// abstract structure for BCD float operations
// bcd_float.h should define a type for bcd_float, 
// that fits into a gen (it can be a 64 bits integer but only 56 bits avail.)
// For accuracy, the BCD floats should have an expanded form
// of type accurate_bcd_float, that one can get using 
//   accurate_bcd_float * fExpand(bcd_float,accurate_bcd_float *)
// This accurate value is returned from a gen g using
//   gentobcd(g,accurate_bcd_float *)
// bcd_float.h should define the following operations 
//   fradd_g, frsub_g, fmul_g, fdiv_g, fchs_g, fpow_g
//   fLT_g, fGT_g (strict comparison, return bool), fEQ_g,
//   fabs_g, fsqrt_g, fZero_g (is ==0)
//   bcd_zero, bcd_set_double, bcd_get_double, bcd_set_int, bcd_get_int, 
struct giac_bcdfloat {
  bcd_float f;
  giac_bcdfloat(const bcd_float & _f){f=_f;}
  giac_bcdfloat(int i) {f=bcd_set_int(i);}
  giac_bcdfloat(mpz_t * m) {f=bcd_set_mpz(m);}
  giac_bcdfloat(const giac_bcdfloat & _f) {f=_f.f;}
  giac_bcdfloat(){f=bcd_zero;}
  giac_bcdfloat(double d){ f=bcd_set_double(d);}
};
typedef giac_bcdfloat giac_float;
inline void print_float(const giac_bcdfloat & f,char * ch,u32 bcd_decpoint,u32 bcd_mantissa,u32 bcd_flags){
  bcd_print(f.f,ch,bcd_decpoint,bcd_mantissa,bcd_flags);
}
inline giac_bcdfloat operator + (const giac_bcdfloat & f1,const giac_bcdfloat & f2){
  return fradd_g(f1.f,f2.f);
}
inline giac_bcdfloat operator += (giac_bcdfloat & f1,const giac_bcdfloat & f2){
  f1.f=fradd_g(f1.f,f2.f);
  return f1;
}
inline giac_bcdfloat operator - (const giac_bcdfloat & f1,const giac_bcdfloat & f2){
  return frsub_g(f1.f,f2.f);
}
inline giac_bcdfloat operator -= (giac_bcdfloat & f1,const giac_bcdfloat & f2){
  f1.f=frsub_g(f1.f,f2.f);
  return f1;
}
inline giac_bcdfloat operator - (const giac_bcdfloat & f1){
  return fchs_g(f1.f);
}
inline giac_bcdfloat operator * (const giac_bcdfloat & f1,const giac_bcdfloat & f2){
  return fmul_g(f1.f,f2.f);
}
inline giac_bcdfloat operator *= (giac_bcdfloat & f1,const giac_bcdfloat & f2){
  f1.f=fmul_g(f1.f,f2.f);
  return f1;
}
inline giac_bcdfloat operator / (const giac_bcdfloat & f1,const giac_bcdfloat & f2){
  return fdiv_g(f1.f,f2.f);
}
inline giac_bcdfloat operator /= (giac_bcdfloat & f1,const giac_bcdfloat & f2){
  f1.f=fdiv_g(f1.f,f2.f);
  return f1;
}
inline bool operator == (const giac_bcdfloat & f1,const giac_bcdfloat & f2){
  return fEQ_g(f1.f,f2.f);
}
inline bool operator > (const giac_bcdfloat & f1,const giac_bcdfloat & f2){ return fGT_g(f1.f,f2.f); }
inline bool operator < (const giac_bcdfloat & f1,const giac_bcdfloat & f2){ return fLT_g(f1.f,f2.f); }
inline bool operator >= (const giac_bcdfloat & f1,const giac_bcdfloat & f2){ return fGE_g(f1.f,f2.f); }
inline bool operator <= (const giac_bcdfloat & f1,const giac_bcdfloat & f2){ return fLE_g(f1.f,f2.f); }
inline int get_int(const giac_bcdfloat & f) { return bcd_get_int(f.f);}
inline giac_bcdfloat fabs (const giac_bcdfloat & f1){ return fabs_g(f1.f);}
inline int fsign (const giac_bcdfloat & f1){ return fZero_g(f1.f)?0:(fPos_g(f1.f)?1:-1); }
inline giac_bcdfloat fsqrt (const giac_bcdfloat & f1){ return fsqrt_g(f1.f); }
inline giac_bcdfloat finv (const giac_bcdfloat & f1){ return finv_g(f1.f); }
inline giac_bcdfloat fsquare (const giac_bcdfloat & f1){ return fsquare_g(f1.f); }
inline giac_bcdfloat fpow(const giac_bcdfloat & f1,const giac_bcdfloat & f2) {   return fpow_g(f1.f,f2.f); }
inline giac_bcdfloat fround(const giac_bcdfloat & f1,int digits) {   if (digits>=0) return frnd_g(f1.f,digits,false); else return frnd_g(f1.f,-digits,true); }
inline giac_bcdfloat ftrunc(const giac_bcdfloat & f1,int digits) {   return ftrunc_g(f1.f,digits,false); }
inline giac_bcdfloat ffloor(const giac_bcdfloat & f){ return ffloor_g(f.f);}
inline giac_bcdfloat fceil(const giac_bcdfloat & f){ return fceil_g(f.f);}
inline giac_bcdfloat fsin(const giac_bcdfloat & f,int angle_mode){ return fsin_g(f.f,TAngleMode(angle_mode));}
inline giac_bcdfloat fcos(const giac_bcdfloat & f,int angle_mode){ return fcos_g(f.f,TAngleMode(angle_mode));}
inline giac_bcdfloat ftan(const giac_bcdfloat & f,int angle_mode){ return ftan_g(f.f,TAngleMode(angle_mode));}
inline giac_bcdfloat fcot(const giac_bcdfloat & f,int angle_mode){ return fcot_g(f.f,TAngleMode(angle_mode));}
inline giac_bcdfloat fasin(const giac_bcdfloat & f,int angle_mode){ return fasin_g(f.f,TAngleMode(angle_mode));}
inline giac_bcdfloat facos(const giac_bcdfloat & f,int angle_mode){ return facos_g(f.f,TAngleMode(angle_mode));}
inline giac_bcdfloat fatan(const giac_bcdfloat & f,int angle_mode){ return fatan_g(f.f,TAngleMode(angle_mode));}
inline giac_bcdfloat facot(const giac_bcdfloat & f,int angle_mode){ return facot_g(f.f,TAngleMode(angle_mode));}
inline giac_bcdfloat fsinh(const giac_bcdfloat & f){ return fsinh_g(f.f);}
inline giac_bcdfloat fcosh(const giac_bcdfloat & f){ return fcosh_g(f.f);}
inline giac_bcdfloat ftanh(const giac_bcdfloat & f){ return ftanh_g(f.f);}
inline giac_bcdfloat fasinh(const giac_bcdfloat & f){ return fasinh_g(f.f);}
inline giac_bcdfloat facosh(const giac_bcdfloat & f){ return facosh_g(f.f);}
inline giac_bcdfloat fatanh(const giac_bcdfloat & f){ return fatanh_g(f.f);}
inline giac_bcdfloat flog(const giac_bcdfloat & f){ return fln_g(f.f);}
inline giac_bcdfloat fln(const giac_bcdfloat & f){ return fln_g(f.f);}
inline giac_bcdfloat flnp1(const giac_bcdfloat & f){ return flnp1_g(f.f);}
inline giac_bcdfloat fexp(const giac_bcdfloat & f){ return fexp_g(f.f);}
inline giac_bcdfloat fexpm(const giac_bcdfloat & f){ return fexpm_g(f.f);}
inline giac_bcdfloat flog10(const giac_bcdfloat & f){ return flog_g(f.f);}
inline giac_bcdfloat falog10(const giac_bcdfloat & f){ return falog_g(f.f);}
inline giac_bcdfloat fgamma(const giac_bcdfloat & f){ return fgamma_g(f.f); }
inline giac_bcdfloat fmod(const giac_bcdfloat & f1,const giac_bcdfloat & f2){ return fmod_g(f1.f,f2.f);}
inline giac_bcdfloat fnthroot(const giac_bcdfloat & f1,const giac_bcdfloat & f2){ return fnthroot_g(f1.f,f2.f);}
inline giac_bcdfloat fmax(const giac_bcdfloat & f1,const giac_bcdfloat & f2){ return fmax_g(f1.f,f2.f);}
inline giac_bcdfloat fmin(const giac_bcdfloat & f1,const giac_bcdfloat & f2){ return fmin_g(f1.f,f2.f);}
inline bool fis_exactly_zero(const giac_bcdfloat & f){ return fZero_g(f.f);}
inline bool fis_integer(const giac_bcdfloat & f){ return fInteger_g(f.f); }
inline bool fis_positive(const giac_bcdfloat & f){ return fPos_g(f.f);}
inline bool fis_nan(const giac_bcdfloat & f){ return fNaN_g(f.f); }
inline bool fis_inf(const giac_bcdfloat & f){ return finfmax_g(f.f); }
inline bool fis_inf_notmax(const giac_bcdfloat & f){ return fInf_g(f.f); }
inline giac_bcdfloat fpi(){ return fpi_g(); }
inline giac_bcdfloat atan2f(const giac_bcdfloat & re,const giac_bcdfloat & im,int anglemode=0){ return fatan2_g(re.f,im.f,anglemode); }

inline giac_bcdfloat strtobcd(const char *nptr,const char **endptr,char decpoint,char thousand,int base){ return fstrtobcd(nptr,endptr,decpoint,thousand,base); }
inline double get_double(const giac_bcdfloat & f) { 
  // FIXME
#ifdef ASPEN_GEOMETRY
  return bcd_get_float(f.f);
#else
  char ch[32]; char * endchar;
  bool pos=fis_positive(f) || fis_exactly_zero(f);
  if (fis_inf(f)){ double d=0.0; return 1./d; }
  if (fis_nan(f)){ double d=0.0; return 0./d; }
  if (!pos) ch[0]='-';
  print_float(pos?f.f:(-f).f,pos?ch:ch+1,'.'|('E'<<16)|(' '<<24),12+(15<<8),0);
  return strtod(ch,&endchar); 
#endif
}
#else // defined BCD

#include <cmath>
typedef float giac_float;
#define fis_inf_notmax fis_inf
inline bool fis_exactly_zero(float f){ return f==0;}
inline double get_double(float f) { return f;}
inline int get_int(float f) { return int(f);}
inline int fsign (float f1){return f1==0?0:(f1>0?1:-1);}
float fsqrt (float f1);
void print_float(const giac_float & f,char * ch);
inline float fpow(float f1,float f2){ return std::pow(f1,f2); }
inline float ffloor(float f1){ return std::floor(f1); }
inline float finv(float f1){ return 1/f1; }
#ifdef __APPLE__
inline float fgamma(float f1){ return tgammaf(f1); }
#else
#ifdef __MINGW_H // FIXME gamma
inline float fgamma(float f1){ return f1; }
#else
inline float fgamma(float f1){ return gammaf(f1); }
#endif
#endif
inline float atan2f(float f1,float f2,int rad){ if (rad) return atan2f(f1,f2); else return atan2f(f1,f2)*180/M_PI;}
#define fis_nan my_isnan
#define fis_inf my_isinf
#endif // BCD

#endif // _GIAC_FIRST_H_
