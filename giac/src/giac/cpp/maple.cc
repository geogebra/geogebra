// -*- mode:C++ ; compile-command: "g++-3.4 -I.. -I../include -g -c maple.cc  -DIN_GIAC -DHAVE_CONFIG_H" -*-
#include "giacPCH.h"
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
#ifdef HAVE_CONFIG_H
#include "config.h"
#endif
using namespace std;
#include <stdexcept>
#include <cmath>
#include <cstdlib>
#include <stdio.h>
#if !defined HAVE_NO_SYS_TIMES_H && defined HAVE_SYS_TIME_H
#include <fcntl.h>
#include <sys/time.h>
#include <time.h>
#endif
// #include <sys/resource.h>
// #include <unistd.h>
#include "sym2poly.h"
#include "usual.h"
#include "moyal.h"
#include "solve.h"
#include "intg.h"
#include "prog.h"
#include "misc.h"
#include "maple.h"
#include "plot.h"
#include "ifactor.h"
#include "subst.h"
#include "input_parser.h"
#include "sym2poly.h"
#include "modpoly.h"
#include "lin.h"
#include "derive.h"
#include "ti89.h"
#include "giacintl.h"
#ifdef HAVE_LIBGSL
#include <gsl/gsl_errno.h>
#include <gsl/gsl_fft_complex.h>
#include <gsl/gsl_fft_real.h>
#endif
#ifndef HAVE_PNG_H
#undef HAVE_LIBPNG
#endif
#ifdef HAVE_LIBPNG
#include <png.h>
#endif
#ifdef HAVE_UNISTD_H
#include <unistd.h>
#endif

#ifdef EMCC
#include <emscripten.h>
#endif


#if 0 // def GIAC_HAS_STO_38
  TMillisecs PrimeGetNow();
#endif


#if defined(EMCC) && !defined(PNACL)
extern "C" double emcctime();
// definition of emcctime should be added in emscripten directory src/library.js
// search for _time definition, and return only Date.now() for _emcctime
// otherwise time() will not work
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  gen _zip(const gen & g,const context * contextptr){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    int s=-1;
    if (g.type!=_VECT || (s=g._VECTptr->size())<2)
      return symbolic(at_zip,g);
    vecteur & v=*g._VECTptr;
    gen & f=v[0];
    if (s==2){
      if (f.type!=_VECT || v[1].type!=_VECT || f._VECTptr->size()!=v[1]._VECTptr->size())
	return gendimerr(contextptr);
      return _tran(g,contextptr);
    }
    if (v[1].type!=_VECT || v[2].type!=_VECT)
      return f(gen(makevecteur(v[1],v[2]),_SEQ__VECT),contextptr);
    vecteur & w1=*v[1]._VECTptr;
    vecteur & w2=*v[2]._VECTptr;
    int s1=int(w1.size()),s2=int(w2.size());
    vecteur res;
    int ss=giacmin(s1,s2),i=0;
    res.reserve(ss);
    for (;i<ss;++i)
      res.push_back(_zip(gen(makevecteur(f,w1[i],w2[i]),_SEQ__VECT),contextptr));
    if (s==3)
      return res;
    gen & ff=v[3];
    for (;i<s1;++i)
      res.push_back(_zip(gen(makevecteur(f,w1[i],ff),_SEQ__VECT),contextptr));
    for (;i<s2;++i)
      res.push_back(_zip(gen(makevecteur(f,ff,w2[i]),_SEQ__VECT),contextptr));
    return res;
  }
  static const char _zip_s []="zip";
  static define_unary_function_eval (__zip,&_zip,_zip_s);
  define_unary_function_ptr5( at_zip ,alias_at_zip,&__zip,0,true);

  gen _accumulate_head_tail(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=3)
      return symbolic(at_accumulate_head_tail,g);
    vecteur & v=*g._VECTptr;
    if (v[0].type!=_VECT || v[1].type!=_INT_ || v[2].type!=_INT_)
      return gensizeerr();
    vecteur & w=*v[0]._VECTptr;
    int tail=v[1].val;
    int head=v[2].val;
    int s=int(w.size());
    if (tail<1 || head<1 || tail+head>s)
      return gensizeerr();
    gen tmp;
    int i=0;
    for (;i<tail;++i)
      tmp=tmp+w[i];
    vecteur res;
    res.push_back(tmp);
    int t=s-head;
    for (;i<t;++i)
      res.push_back(w[i]);
    tmp=zero;
    for (;i<s;++i)
      tmp=tmp+w[i];
    res.push_back(tmp);
    return gen(res,v[0].subtype);
  }
  static const char _accumulate_head_tail_s []="accumulate_head_tail";
  static define_unary_function_eval (__accumulate_head_tail,&_accumulate_head_tail,_accumulate_head_tail_s);
  define_unary_function_ptr5( at_accumulate_head_tail ,alias_at_accumulate_head_tail,&__accumulate_head_tail,0,true);

  gen _divide(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()<2)
      return gensizeerr();
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return _quorem(g,contextptr);
    gen q("Quo",contextptr),r("Rem",contextptr),arg;
    gen mode=v.back();
    if (s==4)
      arg=gen(makevecteur(v[0],v[1],v[2]),_SEQ__VECT);
    else
      arg=gen(makevecteur(v[0],v[1]),_SEQ__VECT);
    if (mode==q)
      return _quo(arg,contextptr);
    if (mode==r)
      return _rem(arg,contextptr);
    if (s==4)
      return _quorem(arg,contextptr);
    if (s==3 && mode.type==_IDNT)
      return _quorem(g,contextptr);
    return gensizeerr();
    // return 0;
  }
  static const char _divide_s []="divide";
  static define_unary_function_eval (__divide,&_divide,_divide_s);
  define_unary_function_ptr5( at_divide ,alias_at_divide,&__divide,0,true);

  gen _ratnormal(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return ratnormal(g,contextptr);
  }
  static const char _ratnormal_s []="ratnormal";
  static define_unary_function_eval (__ratnormal,&_ratnormal,_ratnormal_s);
  define_unary_function_ptr5( at_ratnormal ,alias_at_ratnormal,&__ratnormal,0,true);

  gen _about(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT)
      return apply(g,contextptr,_about);
    if (g.type==_IDNT)
      return g._IDNTptr->eval(1,g,contextptr);
    return g;
  }
  static const char _about_s []="about";
  static define_unary_function_eval (__about,&_about,_about_s);
  define_unary_function_ptr5( at_about ,alias_at_about,&__about,0,true);

  gen _inverse(const gen & a_orig,GIAC_CONTEXT){
    if ( a_orig.type==_STRNG && a_orig.subtype==-1) return  a_orig;
    matrice a;
    bool convert_internal,minor_det,keep_pivot;
    int algorithm,last_col;
    if (!read_reduction_options(a_orig,a,convert_internal,algorithm,minor_det,keep_pivot,last_col))
      return inv(a_orig,contextptr);
    if (keep_pivot)
      return gensizeerr(gettext("Option keep_pivot not applicable"));
    if (minor_det){ // not really minors, use Leverrier algorithm
      vecteur b;
      vecteur p(mpcar(a,b,true,contextptr));
      gen res=b.back()/p.back();
      // if (a.size()%2==0)
	res=-res;
      return res;
    }
    matrice res;
    if (!minv(a,res,convert_internal,algorithm,contextptr))
      return gendimerr(contextptr);
    return res;
  }
  static const char _inverse_s []="inverse";
  static define_unary_function_eval (__inverse,&_inverse,_inverse_s);
  define_unary_function_ptr5( at_inverse ,alias_at_inverse,&__inverse,0,true);

  gen _Inverse(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return symbolic(at_inverse,g);
  }
  static const char _Inverse_s []="Inverse";
  static define_unary_function_eval (__Inverse,&_Inverse,_Inverse_s);
  define_unary_function_ptr5( at_Inverse ,alias_at_Inverse,&__Inverse,0,true);

  gen _inverser(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return sto(inv(eval(g,1,contextptr),contextptr),g,contextptr);
  }
  static const char _inverser_s []="inverser";
  static define_unary_function_eval2 (__inverser,&_inverser,_inverser_s,&printastifunction);
  define_unary_function_ptr5( at_inverser ,alias_at_inverser,&__inverser,_QUOTE_ARGUMENTS,T_LOGO);

  static gen maple_gcdigcd(const gen & a_orig,const unary_function_ptr * u,GIAC_CONTEXT){
    if (a_orig.type!=_VECT || a_orig._VECTptr->size()<2)
      return gensizeerr();
    vecteur v=*a_orig._VECTptr;
    int s=int(v.size());
    for (int i=2;i<s;++i){
      v[i]=unmodunprod(v[i]);
    }
    if (s<4)
      return (*u)(gen(v,a_orig.subtype),contextptr);
    gen res;
    if (s==4)
      res=(*u)(makevecteur(v[0],v[1]),contextptr);
    else
      res=(*u)(makevecteur(v[0],v[1],v[2]),contextptr);
    if (res.type==_VECT && res._VECTptr->size()==3 && v[s-2].type==_IDNT && v[s-1].type==_IDNT){
      gen tmpsto=sto(res[0],v[s-2],contextptr);
      if (is_undef(tmpsto))
	return tmpsto;
      tmpsto=sto(res[1],v[s-1],contextptr);
      if (is_undef(tmpsto))
	return tmpsto;
      return res[2];
    }
    else
      return res;
    return gensizeerr();
  }
  gen _igcdex(const gen & a_orig,GIAC_CONTEXT){
    if ( a_orig.type==_STRNG && a_orig.subtype==-1) return  a_orig;
    return maple_gcdigcd(a_orig,at_iegcd,contextptr);
  }
  static const char _igcdex_s []="igcdex";
  static define_unary_function_eval (__igcdex,&_igcdex,_igcdex_s);
  define_unary_function_ptr5( at_igcdex ,alias_at_igcdex,&__igcdex,0,true);

  static const char _igcd_s []="igcd";
  static define_unary_function_eval (__igcd,&_gcd,_igcd_s);
  define_unary_function_ptr5( at_igcd ,alias_at_igcd,&__igcd,0,true);

  gen _gcdex(const gen & a_orig,GIAC_CONTEXT){
    if ( a_orig.type==_STRNG && a_orig.subtype==-1) return  a_orig;
    return maple_gcdigcd(a_orig,at_egcd,contextptr);
  }
  static const char _gcdex_s []="gcdex";
  static define_unary_function_eval (__gcdex,&_gcdex,_gcdex_s);
  define_unary_function_ptr5( at_gcdex ,alias_at_gcdex,&__gcdex,0,true);

  static const char _indets_s []="indets";
  static define_unary_function_eval (__indets,&_lname,_indets_s);
  define_unary_function_ptr5( at_indets ,alias_at_indets,&__indets,0,true);

  gen _revlist(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (a.type==_VECT){
      vecteur v=*a._VECTptr;
      reverse(v.begin(),v.end());
      return gen(v,a.subtype);
    }
    if (a.type==_STRNG){
      string s=*a._STRNGptr;
      int l=int(s.size());
      for (int i=0;i<l/2;++i){
	char c=s[i];
	s[i]=s[l-1-i];
	s[l-1-i]=c;
      }
      return string2gen(s,false);
    }
    return a;
  }
  static const char _revlist_s []="revlist";
  static define_unary_function_eval (__revlist,&_revlist,_revlist_s);
  define_unary_function_ptr5( at_revlist ,alias_at_revlist,&__revlist,0,true);

  static const char _reverse_s []="reverse";
  static define_unary_function_eval (__reverse,&_revlist,_reverse_s);
  define_unary_function_ptr5( at_reverse ,alias_at_reverse,&__reverse,0,true);

  gen _restart(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    intvar_counter=0;
    realvar_counter=0;
    if (args==at_solve) return 1;
    init_context((context *) ((void *) contextptr));
    gen res= _rm_all_vars(args,contextptr);
    *logptr(contextptr) << "============== restarted ===============" << endl;
    if (args.type==_VECT && args.subtype==_SEQ__VECT && args._VECTptr->empty())
      _srand(_time(gen(vecteur(0),_SEQ__VECT),contextptr),contextptr);
    return res;
  }
  static const char _restart_s []="restart";
  static define_unary_function_eval (__restart,&_restart,_restart_s);
  define_unary_function_ptr5( at_restart ,alias_at_restart,&__restart,0,T_RETURN);

  static const char _restart_vars_s []="restart_vars";
  static define_unary_function_eval (__restart_vars,&_rm_all_vars,_restart_vars_s);
  define_unary_function_ptr5( at_restart_vars ,alias_at_restart_vars,&__restart_vars,0,T_RETURN);

  gen _restart_modes(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (!contextptr)
      return 0;
    init_context((context *) ((void *) contextptr));
    return 1;
  }
  static const char _restart_modes_s []="restart_modes";
  static define_unary_function_eval (__restart_modes,&_restart_modes,_restart_modes_s);
  define_unary_function_ptr5( at_restart_modes ,alias_at_restart_modes,&__restart_modes,0,T_RETURN);

  gen _time(const gen & a,GIAC_CONTEXT){
    if ( a.type==_STRNG && a.subtype==-1) return  a;
    if (a.type==_VECT && a.subtype==_SEQ__VECT){
#ifdef GIAC_HAS_STO_38
      return PrimeGetNow()/1000.;
#endif
#if 0 && defined(EMCC) && !defined(GIAC_GGB)
      double res;
      res=EM_ASM_DOUBLE_V({
	  var hw=Date.now();
	  return hw;
      });
      return res*1e-6;
#endif // GIAC_GGB

#if defined(EMCC) && !defined(PNACL)
      return emcctime()/1e6;
#endif
      return total_time(contextptr);
    }
    double delta;
    int ntimes=1,i=0;
    int level=eval_level(contextptr);
#if defined NSPIRE || defined NSPIRE_NEWLIB
    unsigned NSPIRE_RTC_ADDR=0x90090000;
    unsigned t1= * (volatile unsigned *) NSPIRE_RTC_ADDR;
    // CERR << t1 << endl;
    for (unsigned i=1;i<=100;++i){
      eval(a,level,contextptr);
      unsigned t2= * (volatile unsigned *) NSPIRE_RTC_ADDR;
      if (t2>=t1+3)
	return double(t2-t1)/double(i);
    }
    return 0.0;
#endif
#if 0 && defined(EMCC) && !defined(GIAC_GGB)
    double T1=EM_ASM_DOUBLE_V({
	var hw=Date.now();
	return hw;
      });
    eval(a,level,contextptr);
    double T2=EM_ASM_DOUBLE_V({
	var hw=Date.now();
	return hw;
      });
    return (T2-T1)*1e-6;
#endif // GIAC_GGB
#if defined(EMCC) && !defined(PNACL)
    // time_t t1,t2;
    // time(&t1);
    // eval(a,level,contextptr);
    // time(&t2);
    // return difftime(t2,t1);
    double t1=emcctime();
    eval(a,level,contextptr);
    return (emcctime()-t1)/1e6;
#endif
#if defined(__APPLE__) || defined(PNACL)
    unsigned u1=CLOCK();
    struct timezone tz;
    struct timeval debut,fin;
    gettimeofday(&debut,&tz);
    eval(a,level,contextptr);
    gettimeofday(&fin,&tz);
    u1=CLOCK()-u1;
    return makevecteur(double(u1)/CLOCKS_PER_SEC,fin.tv_sec-debut.tv_sec+(fin.tv_usec-debut.tv_usec)/1e6);
#endif
#ifdef GIAC_HAS_STO_38
   int t1=PrimeGetNow(),t2;
#endif
#ifdef _RUSAGE
    struct rusage tmp1,tmp2,tmpc1,tmpc2;
    getrusage(RUSAGE_SELF,&tmp1);
    getrusage(RUSAGE_CHILDREN,&tmpc1);
#else // _RUSAGE
#ifdef HAVE_SYS_TIMES_H
    struct tms tmp1,tmp2;
    times(&tmp1);
#ifdef HAVE_LIBRT
    struct timespec real1,real2;
    clock_gettime(CLOCK_REALTIME,&real1);
#endif
#endif
#endif // _RUSAGE
    for (;;){ // do it 10 times more
      for (;i<ntimes;++i){
	eval(a,level,contextptr);
      }
#ifdef GIAC_HAS_STO_38
      t2=PrimeGetNow();
      delta=(t2-t1)/1000.;
#else // GIAC_HAS_STO_38
#ifdef _RUSAGE
      getrusage(RUSAGE_SELF,&tmp2);
      getrusage(RUSAGE_CHILDREN,&tmpc2);
      delta = tmp2.ru_utime.tv_sec+1e-6*tmp2.ru_utime.tv_usec+tmp2.ru_stime.tv_sec+1e-6*tmp2.ru_stime.tv_usec-(tmp1.ru_utime.tv_sec+1e-6*tmp1.ru_utime.tv_usec+tmp1.ru_stime.tv_sec+1e-6*tmp1.ru_stime.tv_usec);
      delta += tmpc2.ru_utime.tv_sec+1e-6*tmpc2.ru_utime.tv_usec+tmpc2.ru_stime.tv_sec+1e-6*tmpc2.ru_stime.tv_usec-(tmpc1.ru_utime.tv_sec+1e-6*tmpc1.ru_utime.tv_usec+tmpc1.ru_stime.tv_sec+1e-6*tmpc1.ru_stime.tv_usec);
#else // RUSAGE
#ifdef HAVE_SYS_TIMES_H
#ifdef HAVE_LIBRT
      clock_gettime(CLOCK_REALTIME,&real2);
#endif // HAVE_LIBRT
      times(&tmp2);
      delta=delta_tms(tmp1,tmp2);
#else // HAVE_SYS_TIMES_H
      delta=0.0;
      break;
#endif // HAVE_SYS_TIMES_H
#endif // RUSAGE
#endif // GIAC_HAS_STO_38
      if (delta>0.1)
	break;
      if (delta>0.05) // max wait time will be 2 seconds
	ntimes *= 2;
      else {
	if (delta>0.02) 
	  ntimes *= 5;
	else
	  ntimes *= 10;
      }
    }
#ifdef GIAC_HAS_STO_38
    return (delta/ntimes);
#endif    
#ifdef HAVE_SYS_TIME_H
#ifdef HAVE_LIBRT
    return makevecteur(delta/ntimes,(real2.tv_sec-real1.tv_sec+(real2.tv_nsec-real1.tv_nsec)/1e9)/ntimes);
#else
    return (delta/ntimes);
#endif // HAVE_LIBRT
#else // HAVE_SYS_TIME_H
    return (delta/ntimes);
#endif
  }
  static const char _time_s []="time";
  static define_unary_function_eval_quoted (__time,&_time,_time_s);
  define_unary_function_ptr5( at_time ,alias_at_time,&__time,_QUOTE_ARGUMENTS,true);

  static const char _Phi_s []="Phi";
  static define_unary_function_eval (__Phi,&_euler,_Phi_s);
  define_unary_function_ptr5( at_Phi ,alias_at_Phi,&__Phi,0,true);

  static const char _powermod_s []="powermod";
  static define_unary_function_eval (__powermod,&_powmod,_powermod_s);
  define_unary_function_ptr5( at_powermod ,alias_at_powermod,&__powermod,0,true);

  static const char _length_s []="length";
  static define_unary_function_eval (__length,&_size,_length_s);
  define_unary_function_ptr5( at_length ,alias_at_length,&__length,0,true);

  static const char _len_s []="len";
  static define_unary_function_eval (__len,&_size,_len_s);
  define_unary_function_ptr5( at_len ,alias_at_len,&__len,0,true);

  static const char _nops_s []="nops";
  static define_unary_function_eval (__nops,&_size,_nops_s);
  define_unary_function_ptr5( at_nops ,alias_at_nops,&__nops,0,true);

  gen _cat(const gen & a_orig,GIAC_CONTEXT){
    if ( a_orig.type==_STRNG && a_orig.subtype==-1) return  a_orig;
    vecteur v(gen2vecteur(a_orig));
    string s;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (it->type==_STRNG)
	s = s + *it->_STRNGptr;
      else
	s = s+it->print(contextptr);
    }
    return string2gen(s,false);
  }
  static const char _cat_s []="cat";
  static define_unary_function_eval (__cat,&_cat,_cat_s);
  define_unary_function_ptr5( at_cat ,alias_at_cat,&__cat,0,true);

  gen _pivot(const gen & a_orig,GIAC_CONTEXT){
    if ( a_orig.type==_STRNG && a_orig.subtype==-1) return  a_orig;
    vecteur v(gen2vecteur(a_orig));
    int s=int(v.size());
    if (s!=3 && s!=4)
      return gensizeerr();
    if (!ckmatrix(v.front()) || v[1].type!=_INT_ || v[2].type!=_INT_)
      return gentypeerr();
    matrice m=*v.front()._VECTptr;
    int ml,mc;
    mdims(m,ml,mc);
    for (int i=0;i<ml;++i)
      m[i]=*m[i]._VECTptr; // create a copy of the matrix
    int l=v[1].val,c=v[2].val;
    int shift = array_start(contextptr); // xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38;
    l -= shift ;
    c -= shift ;
    if (l<0 || l>=ml || c<0 || c>=mc)
      return gensizeerr();
    gen p=m[l][c];
    int l1=0,l2=ml-1;
    if (s==4 && v[3].type==_INT_){
      int lmin=v[3].val;
      if (lmin<0){
	l1 = -lmin;
	l1 -= shift;
      }
      else {
	lmin -= shift;
	l2=giacmax(0,giacmin(lmin,ml-1));
	l1=l2;
      }
    }
    for (;l1<=l2;++l1){
      if (l1!=l && !is_zero(m[l1][c]))
	linear_combination(p,*m[l1]._VECTptr,-m[l1][c],*m[l]._VECTptr,plus_one,1,*m[l1]._VECTptr,epsilon(contextptr));
    }
    return m;
  }
  static const char _pivot_s []="pivot";
  static define_unary_function_eval (__pivot,&_pivot,_pivot_s);
  define_unary_function_ptr5( at_pivot ,alias_at_pivot,&__pivot,0,true);

  static gen rowcolspace(const gen & g,bool transpose,GIAC_CONTEXT){
    if (g.type==_VECT && g._VECTptr->size()==2 && g._VECTptr->back().type!=_VECT){
      gen tmp=rowcolspace(g._VECTptr->front(),transpose,contextptr),tmpsto;
      if (transpose)
	tmpsto=sto(int(tmp._VECTptr->front()._VECTptr->size()),g._VECTptr->back(),contextptr);
      else
	tmpsto=sto(int(tmp._VECTptr->size()),g._VECTptr->back(),contextptr);
      if (is_undef(tmpsto))
	return tmpsto;
      return tmp;
    }
    if (!ckmatrix(g))
      return gensizeerr(contextptr);
    vecteur v=*g._VECTptr;
    if (transpose)
      v=mtran(v);
    v=mrref(v,contextptr);
    vecteur newv;
    int s=int(v.size());
    vecteur cmp(v.front()._VECTptr->size());
    for (int i=0;i<s;++i){
      if (v[i]!=cmp)
	newv.push_back(v[i]);
    }
    if (transpose)
      newv=mtran(newv);
    return newv; // gen(newv,_SET__VECT);
  }
  gen _rowspace(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return rowcolspace(g,false,contextptr);
  }
  static const char _rowspace_s []="rowspace";
  static define_unary_function_eval (__rowspace,&_rowspace,_rowspace_s);
  define_unary_function_ptr5( at_rowspace ,alias_at_rowspace,&__rowspace,0,true);

  gen _colspace(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return rowcolspace(g,true,contextptr);
  }
  static const char _colspace_s []="colspace";
  static define_unary_function_eval (__colspace,&_colspace,_colspace_s);
  define_unary_function_ptr5( at_colspace ,alias_at_colspace,&__colspace,0,true);

  static const char _nullspace_s []="nullspace";
  static define_unary_function_eval (__nullspace,&_ker,_nullspace_s);
  define_unary_function_ptr5( at_nullspace ,alias_at_nullspace,&__nullspace,0,true);

  gen _copy(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT){
      vecteur v(*g._VECTptr);
      iterateur it=v.begin(),itend=v.end();
      for (;it!=itend;++it)
	*it=_copy(*it,contextptr);
      return gen(v,g.subtype);
    }
    if (g.type==_MAP)
      return gen(*g._MAPptr);
    return g;
  }
  static const char _copy_s []="copy";
  static define_unary_function_eval (___copy,&_copy,_copy_s);
  define_unary_function_ptr5( at_copy ,alias_at_copy,&___copy,0,true);

  gen _row(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gensizeerr();
    int shift = array_start(contextptr); //xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38;
    gen indice=g._VECTptr->back();
    if (indice.is_symb_of_sommet(at_interval) && indice._SYMBptr->feuille.type==_VECT)
      indice=symbolic(at_interval,indice._SYMBptr->feuille-gen(shift)*vecteur(indice._SYMBptr->feuille._VECTptr->size(),1));
    else
      indice -= shift;
    gen res=g._VECTptr->front().operator_at(indice,contextptr);
    if (ckmatrix(res))
      return gen(*res._VECTptr,_SEQ__VECT);
    else
      return res;
  }
  static const char _row_s []="row";
  static define_unary_function_eval (__row,&_row,_row_s);
  define_unary_function_ptr5( at_row ,alias_at_row,&__row,0,true);

  gen _col(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=2)
      return gensizeerr();
    int shift = array_start(contextptr); //xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38;
    gen indice=g._VECTptr->back();
    if (indice.is_symb_of_sommet(at_interval) && indice._SYMBptr->feuille.type==_VECT)
      indice=symbolic(at_interval,indice._SYMBptr->feuille-gen(shift)*vecteur(indice._SYMBptr->feuille._VECTptr->size(),1));
    else
      indice -= shift;
    gen res=_tran(g._VECTptr->front(),contextptr)[indice];
    if (ckmatrix(res))
      return gen(*res._VECTptr,_SEQ__VECT);
    else
      return res;
  }
  static const char _col_s []="col";
  static define_unary_function_eval (__col,&_col,_col_s);
  define_unary_function_ptr5( at_col ,alias_at_col,&__col,0,true);

  static gen count(const gen & f,const gen & v,const context * contextptr,const gen & param){
    if (v.type!=_VECT)
      return f(v,contextptr);
    const_iterateur it=v._VECTptr->begin(),itend=v._VECTptr->end();
    if (param==at_row){
      vecteur res;
      for (;it!=itend;++it){
	res.push_back(count(f,*it,contextptr,0));
      }
      return res;
    }
    if (param==at_col){
      if (!ckmatrix(v))
	return gentypeerr();
      return count(f,mtran(*v._VECTptr),contextptr,at_row);
    }
    gen res;
    for (;it!=itend;++it){
      res=res+count(f,*it,contextptr,0);
    }
    return res;
  }
  gen _count(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2))
      return gensizeerr(contextptr);
    if (args.subtype!=_SEQ__VECT) {
      if (!is_integer_vecteur(*args._VECTptr))
	return gensizeerr(contextptr);
      // count elements in list of integers
      vector<int> x=vecteur_2_vector_int(*args._VECTptr);
      int m=giacmin(x),M=giacmax(x),s=int(x.size());
      if (M-m<3*s){
	vector<int> eff(M-m+1);
	effectif(x,eff,m);
	matrice res;
	for (int i=m;i<=M;++i){
	  int e=eff[i-m];
	  if (e>0)
	    res.push_back(makevecteur(i,e));
	}
	return res;
      }
      sort(x.begin(),x.end());
      int old=M,eff=0,cur;
      matrice res;
      for (int pos=0;pos<s;++pos){
	cur=x[pos];
	if (cur==old){
	  ++eff;
	  continue;
	}
	if (eff)
	  res.push_back(makevecteur(old,eff));
	old=cur;
	eff=1;
      }
      if (eff)
	res.push_back(makevecteur(old,eff));
      return res;
    }
    gen v((*args._VECTptr)[1]);
    gen f(args._VECTptr->front());
    if (f.type==_STRNG && v.type==_STRNG){
      // (Python-like) count occurences of v in f
      int count=0,pos=-1,s=f._STRNGptr->size();
      for (;pos<s;++count){
	pos=f._STRNGptr->find(*v._STRNGptr,pos+1);
	if (pos<0 || pos>=s)
	  break;
      }
      return count;
    }
    gen param;
    if (args._VECTptr->size()>2)
      param=(*args._VECTptr)[2];
    else {
      if (v.type!=_VECT) 
	return _count_eq(makesequence(v,f),contextptr);
    }
    return count(f,v,contextptr,param);
  }
  static const char _count_s []="count";
  static define_unary_function_eval (___count,&_count,_count_s);
  define_unary_function_ptr5( at_count ,alias_at_count,&___count,0,true);

  static longlong count_eq0(const gen & f,const gen & v,GIAC_CONTEXT){
    if (v.type!=_VECT)
      return v==f;
    const_iterateur it=v._VECTptr->begin(),itend=v._VECTptr->end();
    longlong res=0;
    for (;it!=itend;++it){
      if (it->type!=_VECT){
	if (*it==f)
	  ++res;
      }
      else
	res += count_eq0(f,*it,contextptr);
    }
    return res;
  }

  static gen count_eq1(const gen & f,const gen & v,GIAC_CONTEXT){
    if (v.type!=_VECT)
      return is_strictly_greater(v,f,contextptr);
    const_iterateur it=v._VECTptr->begin(),itend=v._VECTptr->end();
    gen res;
    for (;it!=itend;++it){
      res=res+count_eq1(f,*it,contextptr);
    }
    return res;
  }

  static gen count_eq2(const gen & f,const gen & v,GIAC_CONTEXT){
    if (v.type!=_VECT)
      return is_strictly_greater(f,v,contextptr);
    const_iterateur it=v._VECTptr->begin(),itend=v._VECTptr->end();
    gen res;
    for (;it!=itend;++it){
      res=res+count_eq2(f,*it,contextptr);
    }
    return res;
  }

  static gen count_eq(const gen & f,const gen & v,GIAC_CONTEXT,int type,const gen & rowcol){
    if (rowcol==at_row || rowcol==at_col){
      if (!ckmatrix(v))
	return gentypeerr();
      if (rowcol==at_row){
	const_iterateur it=v._VECTptr->begin(),itend=v._VECTptr->end();
	vecteur res;
	for (;it!=itend;++it){
	  res.push_back(count_eq(f,*it,contextptr,type,0));
	}
	return res;
      }
      if (rowcol==at_col)
	return count_eq(f,mtran(*v._VECTptr),contextptr,type,at_row);
    }
    if (type==0)
      return count_eq0(f,v,contextptr);
    if (type==1)
      return count_eq1(f,v,contextptr);
    if (type==2)
      return count_eq2(f,v,contextptr);
    return gentypeerr();
#if 0
    if (v.type!=_VECT){
      switch (type){
      case 0:
	return v==f;
      case 1:
	return is_strictly_greater(v,f,contextptr);
      case 2:
	return is_strictly_greater(f,v,contextptr);
      default:
	return gentypeerr();
      }
    }
    const_iterateur it=v._VECTptr->begin(),itend=v._VECTptr->end();
    gen res;
    for (;it!=itend;++it){
      res=res+count_eq(f,*it,contextptr,type,0);
    }
    return res;
#endif
  }

  gen _count_eq(const gen & args,const context * contextptr){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type!=_VECT || args.subtype!=_SEQ__VECT || args._VECTptr->size()<2)
      return gensizeerr(contextptr);
    gen v((*args._VECTptr)[1]);
    gen f(args._VECTptr->front());
    gen param;
    if (args._VECTptr->size()>2)
      param=(*args._VECTptr)[2];
    return count_eq(f,v,contextptr,0,param);
  }
  static const char _count_eq_s []="count_eq";
  static define_unary_function_eval (___count_eq,&_count_eq,_count_eq_s);
  define_unary_function_ptr5( at_count_eq ,alias_at_count_eq,&___count_eq,0,true);

  gen _count_sup(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type!=_VECT || args.subtype!=_SEQ__VECT || args._VECTptr->size()<2)
      return gensizeerr(contextptr);
    gen v((*args._VECTptr)[1]);
    gen f(args._VECTptr->front());
    gen param;
    if (args._VECTptr->size()>2)
      param=(*args._VECTptr)[2];
    return count_eq(f,v,contextptr,1,param);
  }
  static const char _count_sup_s []="count_sup";
  static define_unary_function_eval (___count_sup,&_count_sup,_count_sup_s);
  define_unary_function_ptr5( at_count_sup ,alias_at_count_sup,&___count_sup,0,true);

  gen _count_inf(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if ( args.type!=_VECT || args.subtype!=_SEQ__VECT || args._VECTptr->size()<2)
      return gensizeerr(contextptr);
    gen v((*args._VECTptr)[1]);
    gen f(args._VECTptr->front());
    gen param;
    if (args._VECTptr->size()>2)
      param=(*args._VECTptr)[2];
    return count_eq(f,v,contextptr,2,param);
  }
  static const char _count_inf_s []="count_inf";
  static define_unary_function_eval (___count_inf,&_count_inf,_count_inf_s);
  define_unary_function_ptr5( at_count_inf ,alias_at_count_inf,&___count_inf,0,true);

  gen _trunc(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (is_equal(args))
      return apply_to_equal(args,_trunc,contextptr);
    if (args.is_symb_of_sommet(at_unit))
      return apply_unit(args,_trunc,contextptr);
    if (args.type==_VECT) {
      if (args.subtype==_SEQ__VECT && args._VECTptr->size()==2 && args._VECTptr->back().type==_INT_){
#ifdef BCD
	if (args._VECTptr->front().type==_FLOAT_)
	  return ftrunc(args._VECTptr->front()._FLOAT_val,args._VECTptr->back().val);
#endif
	gen b=args._VECTptr->back();
	if (b.val<0){
	  gen gf=_floor(log10(abs(args._VECTptr->front(),contextptr),contextptr),contextptr); 
	  if (gf.type!=_INT_ && gf.type!=_FLOAT_)
	    return gensizeerr(contextptr);
	  b=-1-b-gf;
	}
#ifdef _SOFTMATH_H
	double d=std::giac_gnuwince_pow(10.0,double(b.val));
#else
	double d=std::pow(10.0,double(b.val));
#endif
	return _floor(d*args._VECTptr->front(),contextptr)/d;
      }
      if (args.subtype==_SEQ__VECT && args._VECTptr->size()==2 && args._VECTptr->front().type==_INT_){
	if (args._VECTptr->front().val)
	  return zero;
	return _trunc(args._VECTptr->back(),contextptr);
      }
      return apply(args,contextptr,_trunc);
    }
    if (is_strictly_positive(-args,contextptr))
      return -_floor(-args,contextptr);
    return _floor(args,contextptr);
  }
  static const char _trunc_s []="trunc";
  static define_unary_function_eval (___trunc,&_trunc,_trunc_s);
  define_unary_function_ptr5( at_trunc ,alias_at_trunc,&___trunc,0,true);

  static const char _giac_s []="giac";
  static define_unary_function_eval (__giac,&_version,_giac_s);
  define_unary_function_ptr5( at_giac ,alias_at_giac,&__giac,0,true);

  static const char _div_s []="div";
  static define_unary_function_eval4 (__div,&_iquo,_div_s,&printsommetasoperator,&texprintsommetasoperator);
  define_unary_function_ptr5( at_div ,alias_at_div,&__div,0,T_MOD);

  gen _evalc(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT)
      return apply(g,_evalc,contextptr);
    gen tmp(_exp2pow(_lin(recursive_normal(g,contextptr),contextptr),contextptr));
    vecteur l(lop(tmp,at_arg));
    if (!l.empty()){
      vecteur lp=*apply(l,gen_feuille)._VECTptr;
      lp=*apply(lp,contextptr,arg_CPLX)._VECTptr;
      tmp=subst(tmp,l,lp,false,contextptr);
    }
    tmp=recursive_normal(tmp,contextptr);
    vecteur vtmp(lvar(tmp));
    if (vtmp.size()==1 && vtmp[0].is_symb_of_sommet(at_exp)){
      tmp=ratnormal(_halftan(_exp2trig(tmp,contextptr),contextptr),contextptr);
    }
    gen r,i;
    reim(tmp,r,i,contextptr);
    gen tmp2=_lin(inv(tmp,contextptr),contextptr);
    gen re2,im2;
    reim(tmp2,re2,im2,contextptr);
    if (lvar(makevecteur(re2,im2)).size()<lvar(makevecteur(r,i)).size())
      reim(inv(ratnormal(re2,contextptr)+cst_i*ratnormal(im2,contextptr),contextptr),r,i,contextptr);
    // tmp=simplify(i,contextptr);
    if (is_zero(i))
      return r;
    if (is_zero(r))
      return cst_i*i;
    return symbolic(at_plus,gen(makevecteur(r,cst_i*i),_SEQ__VECT));
  }
  static const char _evalc_s []="evalc";
  static define_unary_function_eval (__evalc,&_evalc,_evalc_s);
  define_unary_function_ptr5( at_evalc ,alias_at_evalc,&__evalc,0,true);

  static const char _evala_s []="evala";
  static define_unary_function_eval (__evala,(const gen_op_context)recursive_normal,_evala_s);
  define_unary_function_ptr5( at_evala ,alias_at_evala,&__evala,0,true);

#ifndef S_IRUSR
#define S_IRUSR 00400
#endif
#ifndef S_IWUSR
#define S_IWUSR 00200
#endif

  // open a file, returns a FD
  gen _open(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
#if defined(VISUALC) || defined(__MINGW_H) || defined (FIR) || defined(FXCG) || defined(NSPIRE) || defined(__ANDROID__) || defined(NSPIRE_NEWLIB) || defined(EMCC) || defined(GIAC_GGB)
    return gensizeerr(gettext("not implemented"));
#else
    gen tmp=check_secure();
    if (is_undef(tmp)) return tmp;
    gen filename(g);
    if (filename.type!=_STRNG)
      return gensizeerr();
    int access=O_RDWR | O_CREAT | O_TRUNC | O_APPEND ;
    int res=open(filename._STRNGptr->c_str(),access, S_IRUSR|S_IWUSR);
    if (res==-1)
      return gensizeerr(gettext("Could not open file"));
    gen r(res);
    r.subtype=_INT_FD;
    return r;
#endif
  }
  static const char _open_s []="open";
  static define_unary_function_eval (__open,&_open,_open_s);
  define_unary_function_ptr5( at_open ,alias_at_open,&__open,0,true);

  // open a file, returns a FILE *
  gen _fopen(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen tmp=check_secure();
    if (is_undef(tmp)) return tmp;
    gen filename(g);
    string mode="w+";
    if (g.type==_VECT && g.subtype==_SEQ__VECT && g._VECTptr->size()==2 && g._VECTptr->back().type==_STRNG){
      filename=g._VECTptr->front();
      mode=*g._VECTptr->back()._STRNGptr;
    }
    if (filename.type!=_STRNG || mode.size()>2)
      return gensizeerr();
    FILE * f=fopen(filename._STRNGptr->c_str(),mode.c_str());
    return gen((void *) f,_FILE_POINTER);
  }
  static const char _fopen_s []="fopen";
  static define_unary_function_eval (__fopen,&_fopen,_fopen_s);
  define_unary_function_ptr5( at_fopen ,alias_at_fopen,&__fopen,0,true);

  // fprint first arg=FD, rest is printed
  gen _fprint(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen tmp=check_secure();
    if (is_undef(tmp)) return tmp;
    if (g.type!=_VECT || g._VECTptr->size()<1)
      return gensizeerr(gettext("1st arg=open result, then other args"));
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    FILE * f=0;
#if !defined(BESTA_OS) && !defined(NSPIRE) && !defined(FXCG)
    if (v[0].type==_INT_ && v[0].subtype==_INT_FD)
      f= fdopen(v[0].val,"a");
#endif    
    if (v[0].type==_POINTER_ && v[0].subtype==_FILE_POINTER)
      f=(FILE *) v[0]._POINTER_val;
    if (f){
      if (s>1 && v[1]==gen("Unquoted",contextptr)){
	for (int i=2;i<s;++i)
	  fprintf(f,"%s",v[i].type==_STRNG?v[i]._STRNGptr->c_str():unquote(v[i].print(contextptr)).c_str());
      }
      else {
	for (int i=1;i<s;++i)
	  fprintf(f,"%s",v[i].print(contextptr).c_str());
      }
      // fclose(f);
      return plus_one;
    }
    return zero;
  }
  static const char _fprint_s []="fprint";
  static define_unary_function_eval (__fprint,&_fprint,_fprint_s);
  define_unary_function_ptr5( at_fprint ,alias_at_fprint,&__fprint,0,true);

  gen _close(const gen & g0,GIAC_CONTEXT){
    gen g=eval(g0,1,contextptr);
    if ( g.type==_STRNG && g.subtype==-1) return  g;
#if !defined(VISUALC) && !defined(BESTA_OS) && !defined(__MINGW_H) && !defined(NSPIRE) && !defined(FXCG)
    if (g.type==_INT_ && g.subtype==_INT_FD){
      purgenoassume(g0,contextptr);
      close(g.val);
      return plus_one;
    }
#endif
    if (g.type==_POINTER_){
      purgenoassume(g0,contextptr);
      fclose((FILE *)g._POINTER_val);
      return plus_one;
    }
    return zero;
  }
  static const char _close_s []="close";
  static define_unary_function_eval (__close,&_close,_close_s);
  define_unary_function_ptr5( at_close ,alias_at_close,&__close,_QUOTE_ARGUMENTS,true);

  static const char _fclose_s []="fclose";
  static define_unary_function_eval (__fclose,&_close,_fclose_s);
  define_unary_function_ptr5( at_fclose ,alias_at_fclose,&__fclose,_QUOTE_ARGUMENTS,true);

  gen _blockmatrix(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=3)
      return gentypeerr();
    vecteur & v = *g._VECTptr;
    if (v[0].type!=_INT_ || v[1].type!=_INT_ || v[2].type!=_VECT)
      return gensizeerr();
    int n=v[0].val,m=v[1].val;
    vecteur l=*v[2]._VECTptr;
    int s=int(l.size());
    if (n<=0 || m<=0 || n*m!=s)
      return gendimerr();
    for (int k=0;k<s;++k){
      if (!ckmatrix(l[k])){
	vecteur vtmp(vecteur(1,l[k]));
	if (ckmatrix(vtmp))
	  l[k]=mtran(vtmp);
	if (!ckmatrix(l[k]))
	  return gentypeerr();
      }
    }
    matrice res;
    int pos=0;
    unsigned final=0;
    for (int i=0;i<n;++i){
      // glue m matrices from l vertically, these matrices must have same #rows
      pos=i*m;
      int nrows=int(l[pos]._VECTptr->size());
      for (int j=1;j<m;++j){
	if (l[pos+j]._VECTptr->size()!=unsigned(nrows))
	  return gendimerr();
      }
      for (int k=0;k<nrows;++k){
	// glue row k of l[pos+j] for j=0..m
	vecteur tmp;
	for (int j=0;j<m;++j){
	  gen cur_row = (*l[pos+j]._VECTptr)[k];
	  const_iterateur it=cur_row._VECTptr->begin(),itend=cur_row._VECTptr->end();
	  for (;it!=itend;++it)
	    tmp.push_back(*it);
	}
	if (final && tmp.size()!=final)
	  return gendimerr();
	else
	  final=unsigned(tmp.size());
	res.push_back(tmp);
      }
    }
    return res;
  }
  static const char _blockmatrix_s []="blockmatrix";
  static define_unary_function_eval (__blockmatrix,&_blockmatrix,_blockmatrix_s);
  define_unary_function_ptr5( at_blockmatrix ,alias_at_blockmatrix,&__blockmatrix,0,true);

  static gen delrowscols(const gen & g,bool isrow,GIAC_CONTEXT){
    gen gm,interval,f,fa,fb;
    if (g.type!=_VECT || g._VECTptr->size()!=2 )
      return gentypeerr();
    gm=g._VECTptr->front();
    if (is_Ans(gm))
      gm=eval(gm,1,contextptr);
    if (gm.type==_IDNT){
      return sto(delrowscols(eval(g,eval_level(contextptr),contextptr),isrow,contextptr),gm,contextptr);
    }
    interval=g._VECTptr->back();
    // if (interval.type==_IDNT || interval.type==_SYMB)
      interval=eval(interval,1,contextptr);
    if (!interval.is_symb_of_sommet(at_interval))
      interval=symb_interval(interval,interval);
    if (!ckmatrix(gm) || !interval.is_symb_of_sommet(at_interval) || (f=interval._SYMBptr->feuille).type!=_VECT || f._VECTptr->size()!=2 || !is_integral(fa=f._VECTptr->front()) || !is_integral(fb=f._VECTptr->back()) )
      return gentypeerr();
    int shift = array_start(contextptr); //xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38;
    int a=fa.val-shift,b=fb.val-shift,s;
    matrice m=*gm._VECTptr;
    if (!isrow)
      m=mtran(m);
    s=int(m.size());
    if (a>=s || b>=s || a<0 || b<0 || a>b)
      return gendimerr();
    m.erase(m.begin()+a,m.begin()+b+1);
    if (!isrow)
      m=mtran(m);
    return m;
  }

  gen _delrows(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return delrowscols(g,true,contextptr);
  }
  static const char _delrows_s []="delrows";
  static define_unary_function_eval (__delrows,&_delrows,_delrows_s);
  define_unary_function_ptr5( at_delrows ,alias_at_delrows,&__delrows,0,true);

  gen _delcols(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return delrowscols(g,false,contextptr);
  }
  static const char _delcols_s []="delcols";
  static define_unary_function_eval (__delcols,&_delcols,_delcols_s);
  define_unary_function_ptr5( at_delcols ,alias_at_delcols,&__delcols,0,true);

  static const char _gaussjord_s []="gaussjord";
  static define_unary_function_eval (__gaussjord,&_rref,_gaussjord_s);
  define_unary_function_ptr5( at_gaussjord ,alias_at_gaussjord,&__gaussjord,0,true);

  gen _JordanBlock(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    int s;
    if (g.type!=_VECT || g._VECTptr->size()!=2 || g._VECTptr->back().type!=_INT_ )
      return gentypeerr();
    s=g._VECTptr->back().val;
    if (s<=0 || double(s)*s>LIST_SIZE_LIMIT)
      return gendimerr();
    --s;
    gen x=g._VECTptr->front();
    matrice m;
    m.reserve(s+1);
    for (int i=0;i<=s;++i){
#ifdef TIMEOUT
      control_c();
#endif
      if (ctrl_c || interrupted)
	return gensizeerr(gettext("Stopped by user interruption."));
      vecteur v(s+1);
      v[i]=x;
      if (i<s)
	v[i+1]=plus_one;
      m.push_back(v);
    }
    return m;
  }
  static const char _JordanBlock_s []="JordanBlock";
  static define_unary_function_eval (__JordanBlock,&_JordanBlock,_JordanBlock_s);
  define_unary_function_ptr5( at_JordanBlock ,alias_at_JordanBlock,&__JordanBlock,0,true);

  gen _companion(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v;
    if (g.type!=_VECT)
      return _companion(makesequence(g,vx_var),contextptr); // gentypeerr();
    if (g.subtype==_SEQ__VECT && g._VECTptr->size()==2){
      gen P=g._VECTptr->front();
      gen x=g._VECTptr->back();
      gen Px=_e2r(makevecteur(P,x),contextptr);
      if (Px.type==_FRAC)
	Px=inv(Px._FRACptr->den,contextptr)*Px._FRACptr->num;
      if (Px.type!=_VECT)
	return gensizeerr();
      v=*Px._VECTptr;
    }
    else
      v=*g._VECTptr;
    return companion(v);
  }
  static const char _companion_s []="companion";
  static define_unary_function_eval (__companion,&_companion,_companion_s);
  define_unary_function_ptr5( at_companion ,alias_at_companion,&__companion,0,true);

  gen _border(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=2 || !ckmatrix(g._VECTptr->front()) || g._VECTptr->back().type!=_VECT)
      return gensizeerr();
    matrice m=*g._VECTptr->front()._VECTptr,v=*g._VECTptr->back()._VECTptr;
    if (m.size()!=v.size())
      return gendimerr();
    m=mtran(m);
    if (ckmatrix(v))
      m=mergevecteur(m,mtran(v));
    else
      m.push_back(v);
    return mtran(m);
  }
  static const char _border_s []="border";
  static define_unary_function_eval (__border,&_border,_border_s);
  define_unary_function_ptr5( at_border ,alias_at_border,&__border,0,true);

  // pade(f(x),x,n,p) or pade(f(x),x,N(x),p)
  // Find P/Q such that P/Q=f(x) mod N(x) [1st case N(x)=x^n], deg(P)<p
  // Assume deg(f)<n
  gen _pade(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=4)
      return gensizeerr();
    vecteur v =*g._VECTptr;
    if (v[1].type!=_IDNT || v[3].type!=_INT_)
      return gensizeerr();
    int n,p=v[3].val,ns;
    gen f=v[0],x=v[1],N=v[2];
    if (v[2].type==_INT_){
      n=v[2].val+1;
      N=pow(v[1],n);
      ns=n;
    }
    else {
      n=p;
      ns=_degree(makevecteur(N,x),contextptr).val;
    }
    if (p<=0 || n<=0)
      return gensizeerr();
    f=_series(makevecteur(f,x,zero,ns-1),contextptr);
    vecteur l1(lop(f,at_order_size));
    vecteur l2(l1.size(),zero);
    f=subst(f,l1,l2,false,contextptr);
    // Convert f and N to poly1
    vecteur l(1,x);
    lvar(f,l);
    lvar(N,l);
    int ls=int(l.size());
    gen ff(sym2r(f,l,contextptr));
    gen fn,fd;
    fxnd(ff,fn,fd);
    gen Nf(sym2r(N,l,contextptr));
    gen Nn,Nd;
    fxnd(Nf,Nn,Nd); // Note nd has no meaning
    vecteur fp;
    if (fn.type==_POLY)
      fp=polynome2poly1(*fn._POLYptr,1);
    else
      return gensizeerr();
    vecteur Np;
    if (Nn.type==_POLY)
      Np=polynome2poly1(*Nn._POLYptr,1);
    else
      return gensizeerr();
    n=int(Np.size())-1;
    // Check that fp is a poly of degree less than n
    if (n<1 || p>n || signed(fp.size())>n)
      return gendimerr();
    vecteur a,b;
    if (!egcd_pade(Np,fp,p,a,b,0))
      *logptr(contextptr) << gettext("Solution may be wrong since a and b are not prime together: ")+gen(a).print(contextptr)+","+gen(b).print(contextptr) << endl;
    gen res=poly12polynome(a,1,ls);
    res=res/(fd*gen(poly12polynome(b,1,ls)));
    res=r2sym(res,l,contextptr);
    return res;
  }
  static const char _pade_s []="pade";
  static define_unary_function_eval (__pade,&_pade,_pade_s);
  define_unary_function_ptr5( at_pade ,alias_at_pade,&__pade,0,true);

  static const char _interp_s []="interp";
  static define_unary_function_eval (__interp,&_lagrange,_interp_s);
  define_unary_function_ptr5( at_interp ,alias_at_interp,&__interp,0,true);

  static gen lhsrhs(const gen & g,int i){
    if (!is_equal(g) && !g.is_symb_of_sommet(at_interval))
      return gensizeerr();
    gen & f=g._SYMBptr->feuille;
    if (f.type!=_VECT || f._VECTptr->size()!=2)
      return gensizeerr();
    return (*f._VECTptr)[i];
  }
  gen _lhs(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return lhsrhs(g,0);
  }
  static const char _lhs_s []="lhs";
  static define_unary_function_eval (__lhs,&_lhs,_lhs_s);
  define_unary_function_ptr5( at_lhs ,alias_at_lhs,&__lhs,0,true);

  gen _rhs(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return lhsrhs(g,1);
  }
  static const char _rhs_s []="rhs";
  static define_unary_function_eval (__rhs,&_rhs,_rhs_s);
  define_unary_function_ptr5( at_rhs ,alias_at_rhs,&__rhs,0,true);

  // Given [v_0 ... v_(2n-1)] (begin of the recurrence sequence) 
  // return [b_n...b_0] such that b_n*v_{n+k}+...+b_0*v_k=0
  // Example [1,-1,3,3] -> [1,-3,-6]
  // -> the recurrence relation is v_{n+2}=3v_{n+1}+6v_n
  gen _reverse_rsolve(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr();
    vecteur v=reverse_rsolve(*g._VECTptr);
    return v/v.front();
  }
  static const char _reverse_rsolve_s []="reverse_rsolve";
  static define_unary_function_eval (__reverse_rsolve,&_reverse_rsolve,_reverse_rsolve_s);
  define_unary_function_ptr5( at_reverse_rsolve ,alias_at_reverse_rsolve,&__reverse_rsolve,0,true);

  // Approx fft or exact if args=poly1,omega,n
  gen fft(const gen & g_orig,int direct,GIAC_CONTEXT){
    if (g_orig.type==_VECT && g_orig.subtype==_SEQ__VECT && g_orig._VECTptr->size()==3 && g_orig._VECTptr->front().type==_VECT){
      vecteur & v =*g_orig._VECTptr->front()._VECTptr;
      int n=int(v.size());
      if (n<2)
	return gendimerr();
      gen omega=(*g_orig._VECTptr)[1];
      gen modulo=(*g_orig._VECTptr)[2];
      if (direct==1)
	omega=invmod(omega,modulo);
      if (omega.type==_INT_ && modulo.type==_INT_ && n==(1<<(sizeinbase2(n)-1))){
	if (debug_infolevel)
	  CERR << CLOCK()*1e-6 << " fft start" << endl;
	vector<int> A; A.reserve(n);
	for (int i=0;i<n;++i){
	  if (v[i].type==_INT_)
	    A.push_back(v[i].val);
	  else
	    A.push_back(smod(v[i],modulo).val);
	}
	int p=modulo.val;
	fft2(&A.front(),n,omega.val,p);
	gen r=vecteur(0);
	vecteur & res=*r._VECTptr;
	res.reserve(n);
	if (direct){
	  for (int i=0;i<n;++i)
	    res.push_back(A[i]);
	}
	else {
	  longlong ninv=invmod(n,p);
	  if (ninv<0) ninv += p;
	  for (int i=0;i<n;++i)
	    res.push_back((ninv*A[i])%p);
	}
	if (debug_infolevel)
	  CERR << CLOCK()*1e-6 << "fft end" << endl;
	return r;
      }
      vecteur w(n),res;
      iterateur it=w.begin(),itend=w.end();
      if (omega.type==_INT_ && modulo.type==_INT_){
	int Omega=omega.val,m=modulo.val;
	longlong omegan=1;
	for (;it!=itend;++it,omegan=(Omega*omegan) %m){
	  *it=int(omegan);
	}
      }
      else {
	gen omegan=1;
	for (;it!=itend;++it,omegan=smod(omega*omegan,modulo)){
	  *it=omegan;
	}
      }
      environment * env = new environment;
      env->moduloon=true;
      env->modulo=modulo;
      fft(v,w,res,env);
      delete env;
      if (direct==1)
	return res;
      else
	return smod(invmod(n,modulo)*res,modulo);
    }
    gen g=g_orig;
    if (g.type!=_VECT)
      return gensizeerr();
    int n=int(g._VECTptr->size());
    if (n<2)
      return gendimerr();
    vector< complex<double> > vd;
    if (convert(*g._VECTptr,vd,true)){
      if (debug_infolevel)
	CERR << CLOCK()*1e-6 << " fft start" << endl;
      double theta=2.0*M_PI/n;
      if (direct) theta=-theta;
      bool done=false;
      if (n==(1<<(sizeinbase2(n)-1))){
	fft2(&vd.front(),n,theta);
	done=true;
      }
#if 1 //ndef HAVE_LIBGSL
      if (!done){
	complex<double> w(std::cos(theta),std::sin(theta));
	vector< complex<double> > W(n),res(n);
	for (int i=0;i<n;++i){
	  if ( i % 64==0)
	    W[i]=complex<double>(std::cos(i*theta),std::sin(i*theta));
	  else
	    W[i]=W[i-1]*w;
	}
	fft(&vd[0],n,&W[0],n,&res[0]);
	done=true;
      }
#endif
      if (done){
	gen r=vecteur(0);
	vecteur & v=*r._VECTptr;
	v.reserve(n);
	if (direct){
	  for (int i=0;i<n;++i){
	    v.push_back(vd[i]);
	  }
	}
	else {
	  double invn=1.0/n;
	  for (int i=0;i<n;++i){
	    v.push_back(gen(invn*vd[i].real(),invn*vd[i].imag()));
	  }
	}
	if (debug_infolevel)
	  CERR << CLOCK()*1e-6 << " fft end" << endl;
	return r;
      }
    }
    g=evalf_double(g_orig,1,contextptr);
    if (debug_infolevel)
      CERR << CLOCK()*1e-6 << " fft start" << endl;
    vecteur v =*g._VECTptr;
#ifdef HAVE_LIBGSL
    if (direct && is_zero(im(v,contextptr))){
      double * data=new double[n];
      for (int i=0;i<n;++i){
	if (v[i].type!=_DOUBLE_){
	  delete [] data;
	  return gensizeerr(contextptr);
	}
	data[i]=v[i]._DOUBLE_val;
      }
      if (n==(1<<(sizeinbase2(n)-1))){
	gsl_fft_real_radix2_transform(data,1,n);
	v[0]=data[0];
	v[n/2]=data[n/2];
	for (int i=1;i<n/2;++i){
	  v[i]=gen(data[i],data[n-i]);
	  v[n-i]=conj(v[i],contextptr);
	}
      }
      else {
	gsl_fft_real_wavetable * wavetable = gsl_fft_real_wavetable_alloc (n);
	gsl_fft_real_workspace * workspace = gsl_fft_real_workspace_alloc (n);
	gsl_fft_real_transform (data, 1, n,wavetable,workspace);
	gsl_fft_real_wavetable_free (wavetable);
	gsl_fft_real_workspace_free (workspace);
	v[0]=data[0];
	int n2;
	if (n%2==0){
	  v[n/2]=data[n-1];
	  n2=n/2;
	}
	else 
	  n2=n/2+1;
	for (int i=1;i<n2;++i){
	  v[i]=gen(data[2*i-1],data[2*i]);
	  v[n-i]=conj(v[i],contextptr);
	}
      }
      delete [] data;
      if (debug_infolevel)
	CERR << CLOCK()*1e-6 << " fft end" << endl;
      return v;
    }
    // Could be improved by keeping the wavetable
    double * data=new double[2*n];
    gen gr,gi;
    for (int i=0;i<n;++i){
      reim(v[i],gr,gi,contextptr);
      gi=evalf_double(gi,1,contextptr);
      if (gr.type!=_DOUBLE_ || gi.type!=_DOUBLE_){
	delete [] data;
	return gensizeerr(contextptr);
      }
      data[2*i]=gr._DOUBLE_val;
      data[2*i+1]=gi._DOUBLE_val;
    }
    if (n==(1<<(sizeinbase2(n)-1))){
      if (direct)
	gsl_fft_complex_radix2_forward(data,1,n);
      else
	gsl_fft_complex_radix2_backward(data,1,n);
    }
    else {
      gsl_fft_complex_wavetable * wavetable = gsl_fft_complex_wavetable_alloc (n);
      gsl_fft_complex_workspace * workspace=gsl_fft_complex_workspace_alloc (n);
      if (direct)
	gsl_fft_complex_forward (data, 1, n,wavetable,workspace);
      else
	gsl_fft_complex_backward (data, 1, n,wavetable,workspace);
      gsl_fft_complex_wavetable_free (wavetable);
      gsl_fft_complex_workspace_free (workspace);
    }
    if (direct){
      for (int i=0;i<n;++i){
	v[i]=gen(data[2*i],data[2*i+1]);
      }
    }
    else {
      for (int i=0;i<n;++i){
	v[i]=gen(data[2*i],data[2*i+1])/n;
      }
    }
    delete [] data;
    if (debug_infolevel)
      CERR << CLOCK()*1e-6 << " fft end" << endl;
    return v;
#endif
    /* 
       unsigned m=gen(n).bindigits()-1;
       if (n!=1<<m)
       return gensizeerr(gettext("Size is not a power of 2 ")+print_INT_(n));
    */
    vecteur res;
    double theta;
    vecteur w(n);
    iterateur it=w.begin(),itend=w.end();
    for (int i=0;it!=itend;++it,++i){
      theta = (2*M_PI*i)/n;
      if (direct==1)
	*it = gen(std::cos(theta),-std::sin(theta));
      else
	*it = gen(std::cos(theta),std::sin(theta));
    }
    fft(v,w,res,0);
    if (direct==1)
      return res;
    else
      return gen(res)/n;
  }

  gen _fft(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return fft(g,1,contextptr);
  }
  static const char _fft_s []="fft";
  static define_unary_function_eval (__fft,&_fft,_fft_s);
  define_unary_function_ptr5( at_fft ,alias_at_fft,&__fft,0,true);

  gen _ifft(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return fft(g,0,contextptr);
  }
  static const char _ifft_s []="ifft";
  static define_unary_function_eval (__ifft,&_ifft,_ifft_s);
  define_unary_function_ptr5( at_ifft ,alias_at_ifft,&__ifft,0,true);

  gen _Resultant(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return symbolic(at_resultant,g);
  }
  static const char _Resultant_s []="Resultant";
  static define_unary_function_eval (__Resultant,&_Resultant,_Resultant_s);
  define_unary_function_ptr5( at_Resultant ,alias_at_Resultant,&__Resultant,0,true);

  gen _Nullspace(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return symbolic(at_nullspace,g);
  }
  static const char _Nullspace_s []="Nullspace";
  static define_unary_function_eval (__Nullspace,&_Nullspace,_Nullspace_s);
  define_unary_function_ptr5( at_Nullspace ,alias_at_Nullspace,&__Nullspace,0,true);

  gen _assign(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT && g.subtype==_SEQ__VECT && g._VECTptr->size()==2){
      return sto(g._VECTptr->back(),g._VECTptr->front(),contextptr);
    }
    if (is_equal(g)){
      gen & f=g._SYMBptr->feuille;
      if (f.type==_VECT && f._VECTptr->size()==2)
	return sto(f._VECTptr->back(),f._VECTptr->front(),contextptr);
    }
    if (g.type!=_VECT)
      return gensizeerr();
    return apply(g,_assign,contextptr);
  }
  static const char _assign_s []="assign";
  static define_unary_function_eval (__assign,&_assign,_assign_s);
  define_unary_function_ptr5( at_assign ,alias_at_assign,&__assign,0,true);


  gen _implicitplot3d(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return _plotimplicit(g,contextptr);
  }
  static const char _implicitplot3d_s []="implicitplot3d";
  static define_unary_function_eval (__implicitplot3d,&_implicitplot3d,_implicitplot3d_s);
  define_unary_function_ptr5( at_implicitplot3d ,alias_at_implicitplot3d,&__implicitplot3d,0,true);

  void convert_double_int(vecteur & v){
    for (unsigned i=0;i<v.size();++i){
      if (v[i].type==_DOUBLE_)
	v[i]=int(v[i]._DOUBLE_val+.5);
      if (v[i].type==_VECT){
	vecteur w=*v[i]._VECTptr;
	convert_double_int(w);
	v[i]=w;
      }
    }
  }

  bool read_audio(vecteur & v,int & channels,int & sample_rate,int & bits_per_sample,unsigned int & data_size){
    convert_double_int(v);
    if (v.size()>1 && v[1].type!=_VECT)
      v=makevecteur(1,v);
    gen g=v[0];
    if (ckmatrix(g)){
      if (v.size()==2 && v[1].type==_INT_){
	sample_rate=v[1].val;
	v=*g._VECTptr;
	v.insert(v.begin(),makevecteur(1,16,sample_rate));
	g=v[0];
      }
      else {
	if (v.size()==3 && v[1].type==_INT_ && v[2].type==_INT_){
	  sample_rate=v[1].val;
	  bits_per_sample=v[2].val;
	  v=*g._VECTptr;
	  v.insert(v.begin(),makevecteur(1,bits_per_sample,sample_rate));
	  g=v[0];
	}
      }
    }
    // g=channels,bits_per_sample,sample_rate,data_size,
    if (g.type==_INT_)
      g=makevecteur(g,16);
    if (g.type!=_VECT || g._VECTptr->empty())
      return false;
    vecteur w=*g._VECTptr;
    if (w.size()==1)
      w.push_back(16);
    int ws=int(w.size());
    if (w[0].type!=_INT_ || w[1].type!=_INT_)
      return false;
    channels=w[0].val;
    if (channels<=0 || channels>int(v.size())){
      channels=int(v.size());
      if (v.front().type!=_VECT || !is_integer_vecteur(*v.front()._VECTptr))
	return false;
      data_size=unsigned(v.front()._VECTptr->size());
      for (int i=1;i<channels;++i){
	if (v[i].type!=_VECT || !is_integer_vecteur(*v[i]._VECTptr))
	  return false;
	if (data_size>v[i]._VECTptr->size())
	  data_size=unsigned(v[i]._VECTptr->size());
      }
      w=makevecteur(channels,16,44100); ws=3;
      g=w;
      v.insert(v.begin(),g);
    }
    if (channels<=0 || channels>4 || int(v.size())<=channels)
      return false;
    bits_per_sample=(w[1].val/8)*8;
    if (bits_per_sample<=0)
      return false;
    if (ws>=3)
      sample_rate=w[2].val;
    else
      sample_rate=44100;
    if (sample_rate<1)
      return false;
    if (ws>=4 && w[3].type==_INT_)
      data_size=w[3].val;
    else
      data_size=RAND_MAX;
    for (int i=1;i<=channels;++i){
      if (v[i].type!=_VECT || !is_integer_vecteur(*v[i]._VECTptr))
	return false;
      if (data_size>v[i]._VECTptr->size())
	data_size=unsigned(v[i]._VECTptr->size());
    }
    return true;
  }

#ifdef HAVE_LIBAO
#include <ao/ao.h>
  typedef unsigned short aou16;
  typedef unsigned int aou32;
  gen _playsnd(const gen & args,GIAC_CONTEXT){
    if (args.type==_STRNG){
      if (args.subtype==-1) return  args;
      return _playsnd(_readwav(args,contextptr),contextptr);
    }
    ao_device *device=0;
    ao_sample_format format;
    int default_driver;
    ao_initialize();
    default_driver = ao_default_driver_id();
    memset(&format, 0, sizeof(format));
    format.bits = 16;
    format.channels = 2;
    format.rate = 44100;
    format.byte_format = AO_FMT_LITTLE;
    unsigned int data_size=0;
    vecteur v;
    if (args.type==_VECT && !args._VECTptr->empty()){
      // set format
      v=*args._VECTptr;
      if (!read_audio(v,format.channels,format.rate,format.bits,data_size))
	return gensizeerr(gettext("Invalid sound data"));
    }
    if (data_size){
      *logptr(contextptr) << gettext("Using sound parameters: channels, rate, bits, records ") << format.channels << "," << format.rate << "," << format.bits << "," << data_size << endl;
      device = ao_open_live(default_driver, &format, NULL /* no options */);
      if (device == NULL) 
	return gensizeerr(gettext("Error opening audio device."));
      unsigned n=data_size*format.channels*format.bits/8;
      char * buffer=(char *)malloc(n*sizeof(char));
      // aou16 * bufshort=(aou16 *) buffer;
      aou32 * bufint=(aou32 *) buffer;
      if (buffer){
	// copy data from v into buffer and play it
	unsigned c=format.channels,b=format.bits/8;
	for (unsigned i=0;i<data_size;++i){
	  for (unsigned j=0;j<c;++j){
	    unsigned u=(*v[j+1]._VECTptr)[i].val;
	    if (b==1)
	      buffer[i*c+j]=u;
	    if (b==2){
	      buffer[2*(i*c+j)]=u & 0xff;
	      buffer[2*(i*c+j)+1]=(u>>8) & 0xff;
	    }
	    if (b==4)
	      bufint[i*c+j]=u;
	  }
	}
	ao_play(device, buffer, n);
      }
    }
    ao_close(device);
    ao_shutdown();
    return 1;
  }
  static const char _playsnd_s []="playsnd";
  static define_unary_function_eval (__playsnd,&_playsnd,_playsnd_s);
  define_unary_function_ptr5( at_playsnd ,alias_at_playsnd,&__playsnd,0,true);
#else
#if !defined GIAC_GGB && defined EMCC // must have EM_ASM code javascript inlined (emscripten 1.30.4 at least?)
#include <emscripten.h>
  gen _playsnd(const gen & args,GIAC_CONTEXT){
    if (args.type==_STRNG){
      if (args.subtype==-1) return  args;
      return _playsnd(_readwav(args,contextptr),contextptr);
    }
	   
    int nbits = 16;
    int nchannels = 2;
    int nrate = 44100;
    unsigned int data_size=0;
    vecteur v;
    if (args.type==_VECT && !args._VECTptr->empty()){
      // set format
      v=*args._VECTptr;
      if (!read_audio(v,nchannels,nrate,nbits,data_size))
	return gensizeerr(gettext("Invalid sound data"));
    }
    if (data_size){
      *logptr(contextptr) << gettext("Using sound parameters: channels, rate, bits, records ") << nchannels << "," << nrate << "," << data_size << endl;
      unsigned nDataBytes=data_size*nchannels*sizeof(float);
      // copy data from v into buffer and play it
      unsigned b=nbits/8;
      float * ptr = (float *) malloc(nDataBytes);
      for (unsigned j=0;j<nchannels;++j){
	vecteur & w=(*v[j+1]._VECTptr);
	COUT << "channel " << j << endl;
	for (unsigned i=0;i<data_size;++i){
	  unsigned u=w[i].val;
	  double ud=0;
	  if (b==1)
	    ud=u/128.0-1;
	  if (b==2)
	    ud=u/32768.0-1;
	  if (b==4)
	    ud=u/2147483648.0-1;
	  ptr[j*data_size+i]=ud;
	}
      }
      COUT << "playing" << endl;
      EM_ASM_ARGS({
	  var nchannels;
	  var nDataBytes;
	  var nrate;
	  var ptr;
	  var data_size;
	  nchannels=$0;nDataBytes=$1;nrate=$2;ptr=$3;
	  data_size=nDataBytes/4/nchannels;
	  var audioCtx = new (window.AudioContext || window.webkitAudioContext)();
	  var SoundArrayBuffer = audioCtx.createBuffer(nchannels, nDataBytes, audioCtx.sampleRate);
	  var dataHeap = new Uint8Array(Module.HEAPU8.buffer, ptr, nDataBytes);
	  var result = new Float32Array(dataHeap.buffer, dataHeap.byteOffset, nDataBytes/4);
	  var j;
	  var i;
	  for (j=0;j<nchannels;j++){
	    var v=SoundArrayBuffer.getChannelData(j);
	    for (i=0;i<data_size;++i)
	      v[i]=result[j*data_size+i];
	  }
	  var source = audioCtx.createBufferSource();
	  // set the buffer in the AudioBufferSourceNode
	  source.buffer = SoundArrayBuffer;
	  // connect the AudioBufferSourceNode to the
	  // destination so we can hear the sound
	  source.connect(audioCtx.destination);
	  // start the source playing
	  source.start();
	},nchannels,nDataBytes,nrate,ptr);
      free(ptr);
    }
    return 1;
  }
#else
  gen _playsnd(const gen & args,GIAC_CONTEXT){
    return gensizeerr("Sorry! libao is not present on system");
  }
#endif
  static const char _playsnd_s []="playsnd";
  static define_unary_function_eval (__playsnd,&_playsnd,_playsnd_s);
  define_unary_function_ptr5( at_playsnd ,alias_at_playsnd,&__playsnd,0,true);
#endif

  gen _soundsec(const gen & args,GIAC_CONTEXT){
    // nseconds [,rate]
    gen n,rate=44100;
    if (args.type==_VECT && args._VECTptr->size()==2){
      n=args._VECTptr->front();
      rate=args._VECTptr->back();
    }
    else
      n=args;
    n=evalf_double(n,1,contextptr);
    if (n.type!=_DOUBLE_ || n._DOUBLE_val<=0 || rate.type!=_INT_ || rate.val < 1 )
      return gensizeerr(gettext("Invalid sound parameters"));
    double r=evalf_double(rate,1,contextptr)._DOUBLE_val;
    double nr=r*n._DOUBLE_val;
    if (nr>LIST_SIZE_LIMIT)
      return gensizeerr("Too many records");
    vecteur v;
    v.reserve(int(nr));
    for (int i=0;i<nr;++i){
      v.push_back(double(i)/r);
    }
    return v;
  }
  static const char _soundsec_s []="soundsec";
  static define_unary_function_eval (__soundsec,&_soundsec,_soundsec_s);
  define_unary_function_ptr5( at_soundsec ,alias_at_soundsec,&__soundsec,0,true);

  /*
  gen _beep(const gen & args,GIAC_CONTEXT){
  }
  static const char _beep_s []="beep";
  static define_unary_function_eval (__beep,&_beep,_beep_s);
  define_unary_function_ptr5( at_beep ,alias_at_beep,&__beep,0,true);
  */

#ifdef RTOS_THREADX
  gen _readwav(const gen & args,GIAC_CONTEXT){
    return undef;
  }
  static const char _readwav_s []="readwav";
  static define_unary_function_eval (__readwav,&_readwav,_readwav_s);
  define_unary_function_ptr5( at_readwav ,alias_at_readwav,&__readwav,0,true);

  static const char _writewav_s []="writewav";
  static define_unary_function_eval (__writewav,&_readwav,_writewav_s);
  define_unary_function_ptr5( at_writewav ,alias_at_writewav,&__writewav,0,true);

#else // RTOS_THREADX

  // http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
  static bool in_readwav(FILE * f,gen & g){
    unsigned char c,channels;
    unsigned int u,s,sample_rate,byte_rate,block_align=0,bits_per_sample=0,data_size=0;
    // Header
    if (fread(&u,4,1,f)!=1 || u!=0x46464952) // "RIFF"
      return false;
    if (fread(&s,4,1,f)!=1)
      return false;
    if (fread(&u,4,1,f)!=1 || u!=0x45564157) // "WAVE"
      return false;
    if (fread(&u,4,1,f)!=1 || u!=0x20746d66) // "fmt "
      return false;
    if (fread(&u,4,1,f)!=1 || u!=0x10) // 16 for PCM
      return false;
    c=fgetc(f);
    if (c!=1)
      return false;
    c=fgetc(f);
    if (c!=0)
      return false;
    channels=fgetc(f);
    c=fgetc(f);
    if (c!=0)
      return false;
    if (fread(&sample_rate,4,1,f)!=1)
      return false;
    if (fread(&byte_rate,4,1,f)!=1)
      return false;
    block_align=fgetc(f);
    block_align=block_align+(fgetc(f)<<8);
    bits_per_sample=fgetc(f); // 8 or 16
    bits_per_sample=bits_per_sample+(fgetc(f)<<8);
    bits_per_sample /= 8;
    if (fread(&u,4,1,f)!=1 || u!=0x61746164) // "data"
      return false;
    if (fread(&data_size,4,1,f)!=1)
      return false;
    int n=data_size;
    // data_size=bits_per_sample/8 * num_samples * channels
    vecteur v(channels+1);
    v[0]=makevecteur(int(channels),int(bits_per_sample*8),int(sample_rate),int(data_size));
    g=v;
    vecteur & w=*g._VECTptr;
    for (int i=1;i<=channels;++i){
      w[i]=vecteur(0);
      w[i]._VECTptr->reserve(n/(channels*bits_per_sample));
    }
    while (n>0 && !feof(f)){
      for (int i=1;i<=channels;++i){
	u=0;
	if (fread(&u,bits_per_sample,1,f)!=1)
	  return false;
	// for (int j=0;j<bits_per_sample;++j)
	//  u += fgetc(f) << (j*8) ;
	n -= bits_per_sample;
	if (feof(f))
	  break;
	w[i]._VECTptr->push_back(int(u));
	if (n<=0)
	  break;
      }
    }
    return true;
  }

  static bool readwav(const string & s,gen & g){
    FILE * f = fopen(s.c_str(),"r");
    if (!f)
      return false;
    bool res=in_readwav(f,g);
    fclose(f);
    return res;
  }

  gen _readwav(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_STRNG)
      return gensizeerr();
    gen res;
    bool ok= readwav(*g._STRNGptr,res);
    if (!ok)
      return gensizeerr(gettext("File not found or unrecognized wav file format"));
    return res;
  }
  static const char _readwav_s []="readwav";
  static define_unary_function_eval (__readwav,&_readwav,_readwav_s);
  define_unary_function_ptr5( at_readwav ,alias_at_readwav,&__readwav,0,true);

  static bool in_writewav(FILE * f,const vecteur & v_){
    if (v_.empty())
      return false;
    vecteur v(v_);
    int channels,sample_rate=44100,bits_per_sample=0;
    unsigned int u,byte_rate,block_align=0,data_size=1U<<31;
    if (!read_audio(v,channels,sample_rate,bits_per_sample,data_size))
      return false;
    u=0x46464952;
    if (fwrite(&u,4,1,f)!=1)
      return false;
    u=36+data_size*bits_per_sample/8*channels;
    if (fwrite(&u,4,1,f)!=1)
      return false;    
    u=0x45564157;
    if (fwrite(&u,4,1,f)!=1)
      return false;
    u=0x20746d66;
    if (fwrite(&u,4,1,f)!=1)
      return false;
    u=0x10;
    if (fwrite(&u,4,1,f)!=1)
      return false;
    fputc(1,f);
    fputc(0,f);
    fputc(channels,f);
    fputc(0,f);
    if (fwrite(&sample_rate,4,1,f)!=1)
      return false;
    byte_rate = sample_rate * channels * bits_per_sample/8;
    if (fwrite(&byte_rate,4,1,f)!=1)
      return false;
    block_align=channels * bits_per_sample/8;
    if (fwrite(&block_align,2,1,f)!=1)
      return false;
    if (fwrite(&bits_per_sample,2,1,f)!=1)
      return false;
    u=0x61746164; // "data"
    if (fwrite(&u,4,1,f)!=1)
      return false;
    u= bits_per_sample/8* data_size*channels; // should be data_size
    if (fwrite(&u,4,1,f)!=1)
      return false;
    // write data
    u /= channels;
    bits_per_sample /= 8;
    unsigned n = u/ bits_per_sample;
    for (unsigned i=0;i<n;++i){
      for (int j=1;j<=channels;++j){
	u=(*v[j]._VECTptr)[i].val;
	if (fwrite(&u,bits_per_sample,1,f)!=1)
	  return false;
      }
    }
    return true;
  }

  static bool writewav(const string & s,const vecteur & v){
    FILE * f = fopen(s.c_str(),"w");
    if (!f)
      return false;
    bool res=in_writewav(f,v);
    fclose(f);
    return res;
  }

  gen _writewav(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()!=2 || g._VECTptr->front().type!=_STRNG || g._VECTptr->back().type!=_VECT)
      return gensizeerr();
    bool ok= writewav(*g._VECTptr->front()._STRNGptr,*g._VECTptr->back()._VECTptr);
    if (!ok)
      return gensizeerr(gettext("Unable to open file or unable to code data"));
    return 1;
  }
  static const char _writewav_s []="writewav";
  static define_unary_function_eval (__writewav,&_writewav,_writewav_s);
  define_unary_function_ptr5( at_writewav ,alias_at_writewav,&__writewav,0,true);

#endif // RTOS_THREADX

  static gen animate2d3d(const gen & g,bool dim3,GIAC_CONTEXT){
    int s=0,frames=10;
    if (g.type!=_VECT || (s=int(g._VECTptr->size()))<3)
      return gensizeerr();
    vecteur v = *g._VECTptr;
    gen t;
    double tmin,tmax;
    if (!readrange(v[2],gnuplot_tmin,gnuplot_tmax,t,tmin,tmax,contextptr))
      return gensizeerr();
    // find a frame argument, otherwise use 10
    for (int i=3;i<s;++i){
      if (is_equal(v[i])){
	gen & f = v[i]._SYMBptr->feuille;
	if (f.type==_VECT && f._VECTptr->size()==2 && f._VECTptr->front().type==_INT_ && f._VECTptr->front().val==_FRAMES && f._VECTptr->back().type==_INT_)
	  frames=f._VECTptr->back().val;
      }
    }
    if (frames==0)
      return gensizeerr();
    v.erase(v.begin()+2);
    gen h=symbolic(dim3?at_plotfunc:at_plot,gen(v,g.subtype));
    v=makevecteur(h,t,tmin,tmax,(tmax-tmin)/frames);
    gen tmpseq=seqprod(v,0,contextptr);
    if (is_undef(tmpseq)) return tmpseq;
    return symbolic(at_animation,tmpseq);
  }

  gen _animate(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return animate2d3d(g,false,contextptr);
  }
  static const char _animate_s []="animate";
  static define_unary_function_eval (__animate,&_animate,_animate_s);
  define_unary_function_ptr5( at_animate ,alias_at_animate,&__animate,0,true);

  gen _animate3d(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return animate2d3d(g,true,contextptr);
  }
  static const char _animate3d_s []="animate3d";
  static define_unary_function_eval (__animate3d,&_animate3d,_animate3d_s);
  define_unary_function_ptr5( at_animate3d ,alias_at_animate3d,&__animate3d,0,true);

  gen _even(const gen & g_,GIAC_CONTEXT){
    gen g(g_);
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (!is_integral(g)) return gentypeerr(contextptr);
    return is_zero(smod(g,2));
  }
  static const char _even_s []="even";
  static define_unary_function_eval (__even,&_even,_even_s);
  define_unary_function_ptr5( at_even ,alias_at_even,&__even,0,true);

  gen _odd(const gen & g_,GIAC_CONTEXT){
    gen g(g_);
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (!is_integral(g)) return gentypeerr(contextptr);
    return !is_zero(smod(g,2));
  }
  static const char _odd_s []="odd";
  static define_unary_function_eval (__odd,&_odd,_odd_s);
  define_unary_function_ptr5( at_odd ,alias_at_odd,&__odd,0,true);

#ifdef HAVE_LIBPNG
  int write_png(const char *file_name, void *rows_, int w, int h, int colortype, int bitdepth){
    png_bytep * rows=(png_bytep *) rows_;
    png_structp png_ptr;
    png_infop info_ptr;
    FILE *fp = fopen(file_name, "wb");
    const char *doing = "open for writing";
    if (!(fp = fopen(file_name, "wb"))) goto fail;
    doing = "create png write struct";
    if (!(png_ptr = png_create_write_struct(PNG_LIBPNG_VER_STRING, NULL, 
					    NULL, NULL))) goto fail;
    doing = "create png info struct";
    if (!(info_ptr = png_create_info_struct(png_ptr))) goto fail;
    if (setjmp(png_jmpbuf(png_ptr))) goto fail;
    doing = "init IO";
    png_init_io(png_ptr, fp);
    doing = "write header";
    png_set_IHDR(png_ptr, info_ptr, w, h, bitdepth, colortype, 
		 PNG_INTERLACE_NONE,
		 PNG_COMPRESSION_TYPE_BASE, PNG_FILTER_TYPE_BASE);
    doing = "write info";
    png_write_info(png_ptr, info_ptr);
    doing = "write image";
    png_write_image(png_ptr, rows);
    doing = "write end";
    png_write_end(png_ptr, NULL);
    fclose(fp);
    return 0;
  fail:   
    printf("Write_png: could not %s\n", doing);
    return -1;
  }

  static bool writergb(const string & filename,const vecteur & v){
    if (v.size()==2 && ckmatrix(v[1])){
      int w,h;
      mdims(*v[1]._VECTptr,h,w);
      unsigned i=0,taille=w*h;    
      unsigned char * screenbuf=new unsigned char[w*h];
      unsigned rowbytes = w;
      // fill screenbuf with data from v[] order 1,2,4,3
      for (;i<taille;++i){
	vecteur & wnv =*v[1]._VECTptr;
	int nr=i/rowbytes;
	vecteur & row =*wnv[nr]._VECTptr;
	screenbuf[i]=row[i%w].val;
      }
      unsigned char *rows[h];
      for (i = 0; int(i) < h; i++) {
	rows[i] = &screenbuf[i*w]; // &screenbuf[(h - i - 1)*4*w];
      }
      int res=write_png(filename.c_str(), rows, w, h, PNG_COLOR_TYPE_GRAY, 8);
      delete screenbuf;
      return res+1;
    }
    if (v.size()!=5 || !ckmatrix(v[1]) || !ckmatrix(v[2]) 
	|| !ckmatrix(v[3]) || !ckmatrix(v[4]))
      return false;
    int w,h,w2,w3,w4,h2,h3,h4;
    mdims(*v[1]._VECTptr,h,w);
    mdims(*v[2]._VECTptr,h2,w2);
    mdims(*v[3]._VECTptr,h3,w3);
    mdims(*v[4]._VECTptr,h4,w4);
    if (w!=w2 || w!=w3 || w!=w4 || h!=h2 || h!=h3 || h!=h4)
      return false;
    unsigned i=0,taille=w*h*4;    
    unsigned char * screenbuf=new unsigned char[w*h*4];
    unsigned rowbytes = w*4;
    // fill screenbuf with data from v[] order 1,2,4,3
    for (;i<taille;++i){
      int nv,nr;
      switch (i%4){
      case 0: nv=1; break;
      case 1: nv=2; break;
      case 2: nv=4; break;
      case 3: nv=3; break;
      }
      vecteur & wnv =*v[nv]._VECTptr;
      nr=i/rowbytes;
      vecteur & row =*wnv[nr]._VECTptr;
      screenbuf[i]=row[(i/4)%w].val;
    }
    unsigned char *rows[h];
    for (i = 0; int(i) < h; i++) {
      rows[i] = &screenbuf[i*4*w]; // &screenbuf[(h - i - 1)*4*w];
    }
    int res=write_png(filename.c_str(), rows, w, h, PNG_COLOR_TYPE_RGBA, 8);
    delete screenbuf;
    return res+1;
  }
#else // LIBPNG
  int write_png(const char *file_name, void *rows, int w, int h, int colortype, int bitdepth){
    return -1;
  }

  static bool writergb(const string & s,const vecteur & w){
    return false;
  }
#endif // LIBPNG

  gen _writergb(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (ckmatrix(v[1])){
      int l,c;
      mdims(*v[1]._VECTptr,l,c);
      vecteur w(1,makevecteur(v.size()==2?1:4,c,l));
      for (unsigned i=1;i<v.size();++i){
	if (i==3 && v.size()==4)
	  w.push_back(vecteur(l,vecteur(c,255)));
	w.push_back(v[i]);
      }
      v=makevecteur(v[0],w);
    }
    if (v.size()!=2 || v[0].type!=_STRNG || v[1].type!=_VECT)
      return gensizeerr();
    vecteur w=*v[1]._VECTptr;
    // w[0]==[d,w,h], w[1..4]=data
    bool ok= writergb(*v[0]._STRNGptr,w);
    if (!ok)
      return gensizeerr(gettext("File not found or unrecognized image file format"));
    gen tmp(_GL_TEXTURE);
    tmp.subtype=_INT_PLOT;
    vecteur arg=makevecteur(w[0][2],0,w[0][1]/w[0][2],
			    symb_equal(tmp,makevecteur(v[0],undef)));
    tmp=gen(arg,_SEQ__VECT);
    return _rectangle(tmp,contextptr);
  }
  static const char _writergb_s []="writergb";
  static define_unary_function_eval (__writergb,&_writergb,_writergb_s);
  define_unary_function_ptr5( at_writergb ,alias_at_writergb,&__writergb,0,true);

  bool (* readrgb_ptr)(const std::string & s,int W,int H,gen & res)=0;

  gen _readrgb(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(gen2vecteur(g));
    if (v.empty() || v[0].type!=_STRNG)
      return gensizeerr();
    int s=int(v.size());
    gen res;
    bool ok= false;
    if (readrgb_ptr)
      ok = readrgb_ptr(*v[0]._STRNGptr,
		       ( (s>2 && v[1].type==_INT_) ?v[1].val:0),
		       ( (s>2 && v[2].type==_INT_) ?v[2].val:0),
		       res);
    if (!ok)
      return gensizeerr(gettext("File not found or unrecognized image file format"));
    return res;
  }
  static const char _readrgb_s []="readrgb";
  static define_unary_function_eval (__readrgb,&_readrgb,_readrgb_s);
  define_unary_function_ptr5( at_readrgb ,alias_at_readrgb,&__readrgb,0,true);

  gen linear_apply(const gen & e,const gen & x,const gen & l,gen & remains, GIAC_CONTEXT, gen (* f)(const gen &,const gen &,const gen &,gen &,const context *)){
    if (is_constant_wrt(e,x,contextptr) || (e==x) )
      return f(e,x,l,remains,contextptr);
    // e must be of type _SYMB
    if (e.type!=_SYMB) return gensizeerr(gettext("in linear_apply"));
    unary_function_ptr u(e._SYMBptr->sommet);
    gen arg(e._SYMBptr->feuille);
    gen res;
    if (u==at_neg){
      res=-linear_apply(arg,x,l,remains,contextptr,f);
      remains=-remains;
      return res;
    } // end at_neg
    if (u==at_plus){
      if (arg.type!=_VECT)
	return linear_apply(arg,x,l,remains,contextptr,f);
      const_iterateur it=arg._VECTptr->begin(),itend=arg._VECTptr->end();
      for (gen tmp;it!=itend;++it){
	res = res + linear_apply(*it,x,l,tmp,contextptr,f);
	remains =remains + tmp;
      }
      return res;
    } // end at_plus
    if (u==at_prod){
      if (arg.type!=_VECT)
	return linear_apply(arg,x,l,remains,contextptr,f);
      // find all constant terms in the product
      vecteur non_constant;
      gen prod_constant;
      decompose_prod(*arg._VECTptr,x,non_constant,prod_constant,true,contextptr);
      if (non_constant.empty()) return gensizeerr(gettext("in linear_apply 2")); // otherwise the product would be constant
      if (non_constant.size()==1)
	res = linear_apply(non_constant.front(),x,l,remains,contextptr,f);
      else
	res = f(symbolic(at_prod,non_constant),x,l,remains,contextptr);
      remains = prod_constant * remains;
      return prod_constant * res;
    } // end at_prod
    return f(e,x,l,remains,contextptr);
  }

  // discrete product primitive of a polynomial P
  gen product(const polynome & P,const vecteur & v,const gen & n,gen & remains,GIAC_CONTEXT){
    // factor P, for a linear factor a*n+b, multiply res by a^n*gamma(n+b/a)
    // for other factors multiply remains by the factor
    polynome Pcont;
    factorization f;
    gen divan=1,res,extra_div=1;
    if (!factor(P,Pcont,f,/* is_primitive*/false,/* with_sqrt*/true,/* complex */true,divan,extra_div) || extra_div!=1){
      remains=r2e(P,v,contextptr);
      return 1;
    }
    res = pow(divan,-n,contextptr);
    factorization::const_iterator it=f.begin(),itend=f.end();
    for (;it!=itend;++it){
      gen tmp=r2e(it->fact,v,contextptr);
      if (it->fact.lexsorted_degree()!=1){
	remains = remains * pow(tmp,it->mult);
      }
      else {
	gen a=derive(tmp,n,contextptr);
	if (is_undef(a))
	  return a;
	gen b=normal(tmp-a*n,contextptr);
	res  = res * pow(a,it->mult*n,contextptr) * pow(symbolic(at_factorial,n+b/a-1),it->mult,contextptr);
      }
    }
    return res*pow(r2e(Pcont,v,contextptr),n,contextptr);
  }

  // product(P,n=a..b) where the first variable in v is n
  gen product(const polynome & P,const vecteur & v,const gen & n,const gen & a,const gen & b,GIAC_CONTEXT){
    gen remains(1),res=product(P,v,n,remains,contextptr);
    res=subst(res,n,b+1,false,contextptr)/subst(res,n,a,false,contextptr);
    if (is_one(remains))
      return res;
    else
      return res*symbolic(at_product,gen(makevecteur(remains,n,a,b),_SEQ__VECT));
  }

  // solve vnext*sol[n+1]+v*sol[n]+vcst=0
  // example: u_{n+1}=u_n/n+1 -> n*u_{n+1}-u_n-n=0 
  // vnext=[1,0], v=[1], vcst=[-1,0] -> n solution
  static bool rsolve_particular(const modpoly & vnext,const modpoly & v,const modpoly & vcst,modpoly & sol,GIAC_CONTEXT){
    // find majoration of degree for solution
    int vnextdeg=int(vnext.size())-1,vdeg=int(v.size())-1,vcstdeg=int(vcst.size())-1,soldeg;
    soldeg=vcstdeg-giacmax(vnextdeg,vdeg);
    if (vnextdeg==vdeg && is_zero(vnext.front()+v.front()))
      soldeg++;
    if (soldeg<0)
      return false;
    modpoly vars(soldeg+1);
    for (int i=0;i<=soldeg;++i){
      vars[i]=identificateur("rsolve_x"+print_INT_(i));
    }
    modpoly equations=operator_plus(operator_times(vnext,taylor(vars,1,0),0),
				    operator_times(v,vars,0),0);
    equations=operator_plus(equations,vcst,0);
    sol=linsolve(equations,vars,contextptr);
    return !sol.empty() && !is_undef(sol);
  }

  // solve a recurrence relation x_{n+1}=l0*x_n+e
  static gen rsolve(const gen & e,const gen & n,const gen & l0,gen & remains,GIAC_CONTEXT){
    if (is_zero(e)){
      remains=0;
      return e;
    }
    vecteur v;
    polynome P,Q,R;
    if (!is_hypergeometric(e,*n._IDNTptr,v,P,Q,R,contextptr)){
      *logptr(contextptr) << gettext("Cst part must be hypergeometric") << endl;
      remains=e;
      return 0;
    }
    if (Q.lexsorted_degree() || R.lexsorted_degree()){
      *logptr(contextptr) << gettext("Cst part must be of type a^n*P(n)") << endl;
      remains=e;
      return 0;
    }
    gen a=r2e(Q,v,contextptr)/r2e(R,v,contextptr);
    gen P0=r2e(P,v,contextptr);
    if (is_zero(P0))
      return 0;
    for (int i=0;;i++){
      gen tmp=normal(subst(e/P0,n,i,false,contextptr),contextptr);
      if (!is_undef(tmp)){
	P0=tmp*P0/pow(a,i);
	break;
      }
    }
    v.clear();
    v.push_back(n);
    lvar(P0,v);
    lvar(l0,v);
    lvar(a,v);
    vecteur v1(v.begin()+1,v.end());
    gen l=e2r(l0,v1,contextptr);
    P0=e2r(P0,v,contextptr);
    gen P0n,P0d;
    fxnd(P0,P0n,P0d);
    if (P0n.type!=_POLY)
      P=polynome(P0n,int(v.size()));
    else
      P=*P0n._POLYptr;
    a=e2r(a,v1,contextptr);
    remains=0;
    // solve u(n+1)=l*u(n)+a^n*P(n)
    if (a==l){ // let v(n)=u(n)/l^n, then v(n+1)=v(n)+P(n)/l
      return pow(l0,n,contextptr)*sum(r2e(P,v,contextptr),n,remains,contextptr)/r2e(l,v1,contextptr)/r2e(P0d,v,contextptr);
    }
    // search u(n)=a^n*Q(n) with Q a polynomial of same degree than P
    // we have u(n+1)=a^(n+1)*Q(n+1)=l*a^n*Q(n)+a^n*P(n)
    // hence a*Q(n+1)-l*Q(n)=P(n)
    vecteur p=polynome2poly1(P,1);
    int pdeg=int(p.size())-1;
    vecteur q(pdeg+1);
    reverse(p.begin(),p.end());
    for (int j=pdeg;j>=0;j--){
      gen tmp;
      for (int k=j+1;k<=pdeg;++k){
	tmp = tmp + comb(k,j)*q[k];
      }
      q[j]=(p[j]-a*tmp)/(a-l);
    }
    reverse(q.begin(),q.end());
    gen res=r2e(q,v1,contextptr);
    if (res.type!=_VECT)
      return gentypeerr();
    res=symb_horner(*res._VECTptr,n);
    res=res*pow(r2e(a,v1,contextptr),n,contextptr)/r2e(P0d,v,contextptr);
    return res;
  }

  /*
  void gen2fracpoly1(const gen & g,vecteur & num,vecteur & den){
    if (g.type==_FRAC){
      num=gen2vecteur(g._FRACptr->num);
      den=gen2vecteur(g._FRACptr->den);	  
    }
    else {
      num=gen2vecteur(g);
      den=vecteur(1,1);
    }
  }
  */

  // solveseq(expression,[var,value])
  // var is a vector of dim the number of terms in the recurrence
  gen _seqsolve(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen f,x=vx_var,uzero=zero,n;
    int dim=1,udim=1;
    if (args.type==_VECT){
      vecteur & v=*args._VECTptr;
      int s=int(v.size());
      if (!s)
	return gentoofewargs("");
      f=v[0];
      if (s>1)
	x=v[1];
      if (s>2)
	uzero=eval(v[2],eval_level(contextptr),contextptr);
      if (x.type==_VECT){
	dim=int(x._VECTptr->size());
	if (uzero.type==_VECT)
	  udim=int(uzero._VECTptr->size());
      }
    }
    else
      f=args;
    x=gen2vecteur(x);
    vecteur fv=gen2vecteur(f);
    fv=quote_eval(fv,*x._VECTptr,contextptr);
    bool fv1=false;
    if (fv.size()==1 && fv.front().type==_VECT){
      fv1=true;
      vecteur tmp=*fv.front()._VECTptr;
      fv=tmp;
    }
    if (dim==udim+1){
      n=x._VECTptr->back();
      if (n.type!=_IDNT){
	identificateur nn(" rsolve_n");
	x._VECTptr->back()=nn;
	f=quotesubst(f,n,nn,contextptr);
	gen res=_seqsolve(gen(makevecteur(f,x,uzero),_SEQ__VECT),contextptr);
	return quotesubst(res,nn,n,contextptr);
      }
      x=vecteur(x._VECTptr->begin(),x._VECTptr->end()-1);
      --dim;
    }
    else {
      if (dim!=udim)
	return gendimerr();
      n=gen(identificateur("rsolve_n"));
    }
    int fs=int(fv.size());
    if (dim<fs) return gensizeerr(contextptr);
    int add=dim-fs;
    if (f.type!=_VECT && !fv1){
      f=vecteur(x._VECTptr->end()-add,x._VECTptr->end());
      f._VECTptr->push_back(fv[0]);
    }
    else {
      for (int i=0;i<add;++i)
	fv.push_back(x[i]);
      f=fv;
    }
    // check linear recurrence + admissible cst term wrt n
    gen difff=derive(f,x,contextptr);
    if (is_undef(difff) || difff.type!=_VECT)
      return difff;
    matrice m=mtran(*difff._VECTptr);
    if (!is_zero(derive(m,x,contextptr))){
      if (f._VECTptr->size()==1 && x._VECTptr->size()==1 && is_zero(derive(f,n,contextptr))){
	// homographic?
	gen tmp=ratnormal(f._VECTptr->front(),contextptr),var(x._VECTptr->front());
	gen tmpn=_getNum(tmp,contextptr),tmpd=_getDenom(tmp,contextptr),a,b,c,d;
	if (is_linear_wrt(tmpn,var,a,b,contextptr) && is_linear_wrt(tmpd,var,c,d,contextptr)&& !is_zero(c)){
	  // u_{n+1}=(a*u_n+b)/(c*u_n+d)
	  // find fixed points
	  gen fixed=solve(a*n+b-(c*n+d)*n,n,1,contextptr);
	  if (fixed.type==_VECT && fixed._VECTptr->size()==2){
	    gen r1=fixed._VECTptr->front(),r2=fixed._VECTptr->back();
	    // (u_n-r1)/(u_n-r2) is geometric, compute ratio
	    gen un1=(a*n+b)/(c*n+d);
	    gen r=normal((un1-r1)*(n-r2)/((un1-r2)*(n-r1)),contextptr);
	    if (is_zero(derive(r,n,contextptr))){
	      un1=pow(r,n,contextptr)*(var-r1)/(var-r2);
	      return (r1-un1*r2)/(1-un1);
	    }
	  }
	  if (fixed.type==_VECT && fixed._VECTptr->size()==1){
	    gen r1=fixed._VECTptr->front();
	    // 1/(u_n-r1) is arithmetic
	    gen un1=(a*n+b)/(c*n+d);
	    gen r=normal(inv(un1-r1,contextptr)-inv(n-r1,contextptr),contextptr);
	    if (is_zero(derive(r,n,contextptr))){
	      un1=r*n+inv(var-r1,contextptr);
	      return inv(un1,contextptr)+r1;
	    }
	  }
	}
      }
      return symbolic(at_seqsolve,args);
    }
    vecteur cst=*normal(subvecteur(*f._VECTptr,multmatvecteur(m,*x._VECTptr)),contextptr)._VECTptr;
    if (m.size()==1 && cst.size()==1){
      gen l(m[0][0]),c(cst[0]),remains;
      gen u0=gen2vecteur(uzero)[0];
      if (n.type!=_IDNT)
	return gentypeerr();
      if (!is_zero(derive(l,n,contextptr))){
	vecteur v;
	polynome P,Q,R;
	if (!is_hypergeometric(l,*n._IDNTptr,v,P,Q,R,contextptr)){
	  *logptr(contextptr) << gettext("Cst part must be hypergeometric") << endl;
	  return symbolic(at_seqsolve,args);
	}
	// l(n+1)/l(n) = P(n+1)/P(n)*Q(n)/R(n+1)
	std::vector< monomial<gen> >::const_iterator it=Q.coord.begin();
	polynome q0=Tnextcoeff<gen>(it,Q.coord.end()).untrunc1();
	it=R.coord.begin();
	polynome r0=Tnextcoeff<gen>(it,R.coord.end()).untrunc1();
	if (!is_zero(r0*Q-q0*R)){
	  *logptr(contextptr) << gettext("Unable to handle coeff of homogeneous part") << endl;
	  return symbolic(at_seqsolve,args);
	}
	// -> l(n)=l(0)*P(n)/P(0) -> product(l(n))
	gen pn=r2e(P,v,contextptr)/r2e(Q,v,contextptr);
	gen q0r0=r2e(q0,v,contextptr)/r2e(r0,v,contextptr);
	gen res=pow(subst(normal(l/pn,contextptr),n,0,false,contextptr),n,contextptr)*simplify(product(P,v,n,0,n-1,contextptr)/product(Q,v,n,0,n-1,contextptr),contextptr)*pow(q0r0,n*(n-1)/2,contextptr);
	// then we might search for a polynomial particular solution to e
	if (is_zero(c))
	  return u0/subst(res,n,0,false,contextptr)*res;
	if (!is_one(q0r0))
	  return gensizeerr("Unable to find particular solution, general solution is "+res.print(contextptr));
	// u_{n+1}=l*u_{n}+c
	gen tmp=l*x[0]+c,tmpnum,tmpden;
	vecteur tmpv(1,n);
	lvar(tmp,tmpv);
	tmp=e2r(tmp,tmpv,contextptr);
	fxnd(tmp,tmpnum,tmpden);
	tmpnum=r2e(tmpnum,tmpv,contextptr);
	tmpden=r2e(tmpden,tmpv,contextptr);
	tmpnum=_e2r(gen(makevecteur(tmpnum,n),_SEQ__VECT),contextptr);
	tmpden=_e2r(gen(makevecteur(tmpden,n),_SEQ__VECT),contextptr);
	if (is_zero(derive(tmpnum,n,contextptr)) && 
	    is_zero(derive(tmpnum,n,contextptr)) &&
	    tmpnum.type==_VECT && tmpden.type==_VECT){
	  vecteur tmpn,tmpd,ln,cn;
	  tmpn=*tmpnum._VECTptr;
	  tmpd=*tmpden._VECTptr;
	  gen difftmpn=derive(tmpn,x[0],contextptr);
	  if (is_undef(difftmpn) || difftmpn.type!=_VECT)
	    return gensizeerr(contextptr);
	  ln=trim(*difftmpn._VECTptr,0);
	  cn=trim(subst(tmpn,x[0],0,false,contextptr),0);
	  if (rsolve_particular(-tmpd,ln,cn,tmpn,contextptr)){
	    gen sol=_r2e(gen(makevecteur(tmpn,n),_SEQ__VECT),contextptr);
	    // general solution is sol+C*res, sol(0)+C*res(0)=u0
	    gen C=subst((u0-sol)/res,n,0,false,contextptr);
	    return sol+C*res;
	  }
	}
	*logptr(contextptr) << gettext("Unable to find a particular solution for inhomogeneous part") << endl;
	return symbolic(at_seqsolve,args);
      }
      gen d=linear_apply(c,n,l,remains,contextptr,rsolve);
      if (!is_zero(remains))
	*logptr(contextptr) << gettext("Unable to solve recurrence") << endl;
      // d is a particular solution of u(n+1)=l*u(n)+c(n)
      // add a general solution d(n)+C*l^n
      // such that at n=0 we get u0 -> C+d(0)=u0
      gen C=normal(u0-quotesubst(d,n,0,contextptr),contextptr);
      return d+C*pow(l,n,contextptr);
    }
    // u(n+1)=M*u(n)+cst
    // Let M=PDP^-1, v=P^-1*u, v satisfies v(n+1)=Dv(n)+P^-1*cst
    // solve for v then for u
    if (!is_zero(derive(m,n,contextptr)))
      return symbolic(at_seqsolve,args);
    if (has_num_coeff(m))
      m=*evalf(m,1,contextptr)._VECTptr;
    matrice P,Pinv,D;
    bool b=complex_mode(contextptr);
    complex_mode(true,contextptr);
    egv(m,P,D,contextptr,true,false,false);
    complex_mode(b,contextptr);
    Pinv=minv(P,contextptr);
    if (is_undef(Pinv))
      return Pinv;
    // if (fs==1) uzero=_revlist(uzero);
    vecteur vzero=multmatvecteur(Pinv,*uzero._VECTptr);
    vecteur vcst=multmatvecteur(Pinv,cst);
    int taille=int(m.size());
    vecteur res(taille);
    for (int i=taille-1;i>=0;--i){
      // find cst coefficient
      gen c=vcst[i],l=D[i][i];
      for (int j=i+1;j<taille;j++){
	c += D[i][j]*res[j];
      }
      c=normal(c,contextptr);
      gen remains,d=linear_apply(c,n,l,remains,contextptr,rsolve);
      if (!is_zero(remains))
	*logptr(contextptr) << gettext("Unable to solve recurrence") << endl;
      gen C=normal(vzero[i]-quotesubst(d,n,0,contextptr),contextptr);
      if (is_zero(l))
	res[i]=d+C*symbolic(at_same,gen(makevecteur(n,0),_SEQ__VECT));
      else
	res[i]=d+C*pow(l,n,contextptr);
    }
    // u=P*v
    res=multmatvecteur(P,res);
    if (fs==1)
      //return normal(subst(res[0],n,n-add,false,contextptr),contextptr);// normal(res[0]); 
      return ratnormal(res[0],contextptr);
    else
      return ratnormal(res,contextptr);
  }
  static const char _seqsolve_s []="seqsolve";
  static define_unary_function_eval_quoted (__seqsolve,&_seqsolve,_seqsolve_s);
  define_unary_function_ptr5( at_seqsolve ,alias_at_seqsolve,&__seqsolve,_QUOTE_ARGUMENTS,true);

  static vecteur rsolve_initcond(const vecteur & initcond,const gen & un,const gen & u,const gen & n,const vecteur & uinit,GIAC_CONTEXT){
    if (initcond.empty())
      return makevecteur(un);
    gen initc=subst(initcond,u,_unapply(gen(makevecteur(un,n),_SEQ__VECT),contextptr),false,contextptr);
    initc=initc.eval(1,contextptr);
    if (initc.type!=_VECT) 
      return vecteur(1,gensizeerr());
    vecteur valv=gsolve(*initc._VECTptr,uinit,/* complex mode */ true,0,contextptr);
    if (is_undef(valv))
      return valv;
    vecteur resv(valv.size());
    for (unsigned int i=0;i<valv.size();++i){
      resv[i]=normal(quotesubst(un,uinit,valv[i],contextptr),contextptr);
    }
    return resv;
  }

  // example f : u(n+1)=2*u(n)+n, initcond [u(0)=1]
  static gen crsolve(const gen & f0,const gen &u,const gen & n,vecteur & initcond0,GIAC_CONTEXT){
    if (f0.type==_VECT){
      if (u.type!=_VECT || f0._VECTptr->size()!=u._VECTptr->size())
	return gendimerr();
    }
    else {
      if (u.type==_VECT)
	return gendimerr();
    }
    vecteur uv(gen2vecteur(u));
    int uvs=int(uv.size());
    vecteur initcond;
    aplatir(*apply(initcond0,equal2diff)._VECTptr,initcond);
    gen f=apply(f0,equal2diff);
    if (n.type!=_IDNT){
      identificateur N(" rsolve_N");
      gen F=quotesubst(f,n,N,contextptr);
      gen tmp=crsolve(F,u,N,initcond0,contextptr);
      return quotesubst(tmp,N,n,contextptr);
    }
    vecteur vof0(lop(f,at_of));
    // keep only those with u
    vecteur vofa,vofb,vopos,vofun;
    bool all_one=true,all_bzero=true;
    for (const_iterateur it=vof0.begin();it!=vof0.end();++it){
      gen & itf=it->_SYMBptr->feuille;
      int pos;
      if (itf.type==_VECT && itf._VECTptr->size()==2 && (pos=equalposcomp(uv,itf._VECTptr->front())) ){
	gen tmp=it->_SYMBptr->feuille._VECTptr->back(),a,b;
	if (is_linear_wrt(tmp,n,a,b,contextptr) && !is_zero(a)){
	  if (!is_one(a))
	    all_one=false;
	  if (!is_zero(b))
	    all_bzero=false;
	  vofa.push_back(a);
	  vofb.push_back(b);
	  vopos.push_back(pos-1);
	  vofun.push_back(*it);
	}
	else
	  return gensizeerr(gettext("Unable to handle this kind of recurrence"));
      }
    }
    if (vofa.empty())
      return undef;
    if (all_one){
      gen bmin=vofb.front(),bmax=vofb.front();
      int nmax=0;
      for (const_iterateur it=vofb.begin();it!=vofb.end();++it){
	const gen & tmp = *it;
	if (is_strictly_greater(tmp,bmax,contextptr)){
	  nmax=int(it-vofb.begin());
	  bmax=tmp;
	}
	if (is_strictly_greater(bmin,tmp,contextptr))
	  bmin=tmp;
      }
      if (bmin.type!=_INT_ || bmax.type!=_INT_)
	return gensizeerr(gettext("Bad indexes in recurrence"));
      int m=bmin.val,M=bmax.val;
      if (uvs>1){
	if (M!=m+1)
	  return gendimerr(gettext("Multi-recurrences implemented only for 1 step"));
	// generate identifiers x0,...,x_{uvs-1} and y0,...,y_{uvs-1}
	vecteur idx,idX;
	for (int i=0;i<uvs;++i){
	  idx.push_back(gen(identificateur("rsolve_x"+print_INT_(i))));
	  idX.push_back(gen(identificateur("rsolve_X"+print_INT_(i))));
	}
	vecteur vofid;
	for (const_iterateur it=vofb.begin();it!=vofb.end();++it){
	  if (it->type!=_INT_)
	    return gensizeerr();
	  bool isx=it->val==m;
	  int pos=vopos[it-vofb.begin()].val;
	  vofid.push_back(isx?idx[pos]:idX[pos]);
	}
	gen F=quotesubst(f,vofun,vofid,contextptr);
	if (F.type!=_VECT)
	  return gensizeerr();
	// solves for idX in terms of idx
	vecteur Fv=gsolve(*F._VECTptr,idX,1/* complex */,0/* approx mode=no*/,contextptr),res;
	if (is_undef(Fv))
	  return Fv;
	for (const_iterateur it=Fv.begin();it!=Fv.end();++it){
	  vecteur idxn(idx);
	  idxn.push_back(n);
	  gen vn=_seqsolve(makevecteur(*it,idxn,idx),contextptr);
	  gen un=quotesubst(vn,n,n+1-M,contextptr);
	  if (un.type!=_VECT) 
	    return gensizeerr("Unable to solve this recurrence");
	  if (int(un._VECTptr->size())!=uvs)
	    return gendimerr(contextptr);
	  if (initcond.empty())
	    return vecteur(1,un);
	  // solve initial conditions
	  vecteur vout;
	  for (int i=0;i<uvs;++i){
	    vout.push_back(_unapply(gen(makevecteur(un[i],n),_SEQ__VECT),contextptr));
	  }	  
	  gen initc=subst(initcond,uv,vout,false,contextptr);
	  initc=initc.eval(1,contextptr);
	  if (initc.type!=_VECT) 
	    return gensizeerr();
	  vecteur valv=gsolve(*initc._VECTptr,idx,/* complex mode */ true,/* approx=no */ 0,contextptr);
	  if (is_undef(valv))
	    return valv;
	  for (unsigned int i=0;i<valv.size();++i){
	    res.push_back(normal(quotesubst(un,idx,valv[i],contextptr),contextptr));
	  }
	}
	return res;
      }
      // generate identifiers xm,...,xM
      vecteur ids;
      for (int i=m;i<=M;++i){
	ids.push_back(gen(identificateur("rsolve_x"+print_INT_(i-m))));
      }
      // replace vofun
      vecteur vofid;
      for (const_iterateur it=vofb.begin();it!=vofb.end();++it){
	if (it->type!=_INT_)
	  return gensizeerr();
	vofid.push_back(ids[it->val-m]);
      }
      gen F=quotesubst(f,vofun,vofid,contextptr),a,b;
      if (!is_linear_wrt(F,ids.back(),a,b,contextptr))
	return gensizeerr();
      F=-b/a; // xM in terms of xm to xM-1
      ids.back()=n;
      vecteur uinit(ids);
      uinit.pop_back();
      // let v_n=u_{n+m} -> u_n=v_{n-m}
      // old code
      // gen vn=_seqsolve(makevecteur(F,ids,uinit),contextptr);
      // gen un=quotesubst(vn,n,n-m,contextptr);
      // new code that solves rsolve(u(n)=(n)*u(n-1),u(n),u(1)=3);
      gen Fm=quotesubst(F,n,n-m,contextptr);
      gen un=_seqsolve(makevecteur(Fm,ids,uinit),contextptr);
      // end new code
      return rsolve_initcond(initcond,un,u,n,uinit,contextptr);
    }
    // divide and conquier?
    if (uvs==1 && vofa.size()==2 && all_bzero){
      gen idy=identificateur("rsolve_y"),x(identificateur("rsolve_x"));
      vecteur ids(makevecteur(x,idy));
      // replace vofun
      vecteur vofid;
      gen b;
      if (is_one(vofa[0])){
	vofid=ids;
	b=vofa[1];
      }
      if (is_one(vofa[1])){
	vofid=makevecteur(ids[1],ids[0]);
	b=vofa[0];
      }
      if (vofid.empty() || ck_is_greater(1,b,contextptr))
	return gensizeerr();
      vofun.push_back(n);
      vofid.push_back(pow(b,n,contextptr));
      gen F=quotesubst(f,vofun,vofid,contextptr);
      // F(x,y,n)=0 where y=t(b*n) x=t(n)
      // auxiliary sequence u(n)=t(b^n), verifies F(u(n+1),u(n),b^n)=0
      gen A,B;
      if (is_linear_wrt(F,idy,A,B,contextptr)){
	F=-B/A; // u(n+1) in terms of u(n)=x
	gen vn=_seqsolve(makevecteur(F,makevecteur(x,n),x),contextptr);
	gen un=quotesubst(vn,n,ln(n,contextptr)/ln(b,contextptr),contextptr);
	// solve initial cond
	return rsolve_initcond(initcond,un,u,n,makevecteur(x),contextptr);
      }
    }
    return gensizeerr(gettext("Not yet implemented"));
  }
  static gen rsolve(const gen & f0,const gen &u,const gen & n,vecteur & initcond0,int st,GIAC_CONTEXT){
    bool b=complex_mode(contextptr);
    complex_mode(true,contextptr);
    gen res=crsolve(f0,u,n,initcond0,contextptr);
    if (!b){
      complex_mode(b,contextptr);
      // FIXME take real part for res
    }
    return res;
  }
  // example args=u(n+1)=2*u(n)+n,u(n),u(0)=1
  gen _rsolve(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    vecteur varg=gen2vecteur(args);
    if (debug_infolevel>20)
      varg.dbgprint();
    int s=int(varg.size());
    if (!s)
      return gendimerr();
    gen f,u,n;
    vecteur initcond;
    if (s>1){
      gen un=varg[1];
      if (un.is_symb_of_sommet(at_of)){
	gen & unf=un._SYMBptr->feuille;
	if (unf.type==_VECT && unf._VECTptr->size()==2){
	  u=unf._VECTptr->front();
	  n=unf._VECTptr->back();
	}
      }
      if (un.type==_VECT){
	vecteur & unv=*un._VECTptr;
	vecteur uv;
	for (const_iterateur it=unv.begin();it!=unv.end();++it){
	  const gen & itg=*it;
	  if (itg.is_symb_of_sommet(at_of)){
	    gen & unf=itg._SYMBptr->feuille;
	    if (unf.type==_VECT && unf._VECTptr->size()==2){
	      uv.push_back(unf._VECTptr->front());
	      if (is_zero(n))
		n=unf._VECTptr->back();
	      else
		if (n!=unf._VECTptr->back())
		  return gentypeerr();
	    }
	  }
	}
	u=uv;
      }
      if (is_zero(n))
	return gentypeerr();
    }
    else {
      u=gen(identificateur("rsolve_u"));
      n=gen(identificateur("rsolve_n"));
    }
    vecteur quoted=gen2vecteur(u);
    quoted.push_back(n);
    varg=quote_eval(varg,quoted,contextptr);
    f=varg[0];
    if (s>2){
      initcond=vecteur(varg.begin()+2,varg.end());
      if (initcond.size()==1 && initcond.front().type==_VECT){
	// use a temporary vector,
	// otherwise the source is destroyed when copying the first element of the vector
	// another way to do that could be to have a gen with a copy of initcond.front
	vecteur tmp=*initcond.front()._VECTptr;
	initcond=tmp;
      }
    }
    else {
      if (f.type==_VECT && !f._VECTptr->empty()){
	if (u.type!=_VECT || f._VECTptr->size()!=u._VECTptr->size()){
	  initcond=*f._VECTptr;
	  f=initcond.front();
	  initcond.erase(initcond.begin());
	}
      }
    }
    int st=step_infolevel(contextptr);
    step_infolevel(0,contextptr);
    gen res=rsolve(f,u,n,initcond,st,contextptr);
    step_infolevel(st,contextptr);
    return res;
  }
  static const char _rsolve_s []="rsolve";
  static define_unary_function_eval_quoted (__rsolve,&_rsolve,_rsolve_s);
  define_unary_function_ptr5( at_rsolve ,alias_at_rsolve,&__rsolve,_QUOTE_ARGUMENTS,true);

  static bool islessthanf(const gen & a,const gen & b){
    if (a.type!=_VECT || b.type!=_VECT)
      return is_strictly_greater(b,a,context0);
    vecteur & av =*a._VECTptr;
    vecteur & bv =*b._VECTptr;
    int avs=int(av.size()),bvs=int(bv.size());
    if (avs!=bvs)
      return avs<bvs;
    for (int i=0;i<avs;++i){
      if (av[i]==bv[i])
	continue;
      return islessthanf(av[i],bv[i]);
    }
    return false;
  }
  
  gen _array(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    // create a table with specified index, may be initialized
    vecteur args;
    if (g.type==_VECT && g.subtype==_SEQ__VECT){
      args=*g._VECTptr;
      if (!args.empty()){
	gen f=args.back();
	if (f.is_symb_of_sommet(at_equal) && f._SYMBptr->feuille[0]==at_dtype){
	  args.pop_back();
	  return _convert(makesequence(_array(args.size()==1?args.front():gen(args,_SEQ__VECT),contextptr),f._SYMBptr->feuille[1]),contextptr);
	}
      }
    }
    else
      args=gen2vecteur(g);
#if 1 // def NSPIRE
    gen_map m;
#else
    gen_map m(ptr_fun(islessthanf));
#endif
    int s=int(args.size());
    vector<int> indexbegin,indexsize;
    int nindexes=1;
    gen initv(vecteur(0));
    vecteur & init = *initv._VECTptr;
    int shift = array_start(contextptr); //xcas_mode(contextptr)!=0 || abs_calc_mode(contextptr)==38;
    for (int i=0;i<s;++i){
      if (args[i].is_symb_of_sommet(at_interval)){
	gen & f =args[i]._SYMBptr->feuille;
	if (f.type!=_VECT || f._VECTptr->size()!=2)
	  return gendimerr(args[i].print(contextptr));
	vecteur & fv= *f._VECTptr;
	if (fv[0].type!=_INT_ || fv[1].type!=_INT_ || fv[1].val<fv[0].val)
	  return gendimerr(args[i].print(contextptr));
	indexbegin.push_back(fv.front().val-shift);
	indexsize.push_back(fv.back().val-fv.front().val+1);
	nindexes *= indexsize.back();
      }
      if (args[i].type==_VECT)
	init=mergevecteur(init,*args[i]._VECTptr);
    }
    if (nindexes>>24)
      return gendimerr(gettext("Array too large")+print_INT_(nindexes));
    if (nindexes==1)
      return g;
    int is=int(indexsize.size());
    for (int i=0;i<nindexes;++i){
      // generate index by writing nindexes in bases indexsize
      vecteur curidx(is);
      int pos=i,posj;
      gen initval=initv;
      bool initialize=true;
      for (int j=0;j<is;++j){
	posj = (pos % indexsize[j]);
	if (initialize && initval.type==_VECT && (int)initval._VECTptr->size()>posj)
	  initval=(*initval._VECTptr)[posj];
	else
	  initialize=false;
	curidx[j] = posj + indexbegin[j];
	pos /= indexsize[j];
      }
      // initialize m[curidx]
      if (initialize)
	m[curidx]=initval;
      else
	m[curidx]=0;
    }
    gen res=m;
    res.subtype=1;
    return res;
  }
  static const char _array_s []="array";
  static define_unary_function_eval (__array,&_array,_array_s);
  define_unary_function_ptr5( at_array ,alias_at_array,&__array,0,true);

  static const char _whattype_s []="whattype";
  static define_unary_function_eval (__whattype,&_type,_whattype_s);
  define_unary_function_ptr5( at_whattype ,alias_at_whattype,&__whattype,0,true);

  static const char _modp_s []="modp";
  static define_unary_function_eval (__modp,&_irem,_modp_s);
  define_unary_function_ptr5( at_modp ,alias_at_modp,&__modp,0,true);

  gen _makemod(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gentypeerr();
    gen a=args._VECTptr->front(),b=args._VECTptr->back();
    if (is_zero(b))
      return unmod(a);
    if (!is_integer(a) || !is_integer(b))
      return gentypeerr();
    return makemod(a,b);
  }
  static const char _makemod_s []="makemod";
  static define_unary_function_eval (__makemod,&_makemod,_makemod_s);
  define_unary_function_ptr5( at_makemod ,alias_at_makemod,&__makemod,0,true);

  gen cpp_convert_0(const gen &g,GIAC_CONTEXT){
    return g;
  }

  longlong cpp_convert_2(const gen & g,GIAC_CONTEXT){
    gen h=g;
    if (!is_integral(h)){
      gensizeerr(contextptr);
      return 0;
    }
    if (h.type==_INT_)
      return h.val;
#ifdef USE_GMP_REPLACEMENTS
    gensizeerr(contextptr);
    return 0;
#else 
    if (mpz_sizeinbase(*h._ZINTptr,2)>62){
      gensizeerr(contextptr);      
      return 0;
    }
    if (is_greater(0,g,context0))
      return -cpp_convert_2(-g,contextptr);
    unsigned int lo, hi;
    mpz_t tmp;
    mpz_init( tmp );
    mpz_mod_2exp( tmp, *h._ZINTptr, 64 );   /* tmp = (lower 64 bits of n) */    
    lo = mpz_get_ui( tmp );       /* lo = tmp & 0xffffffff */ 
    mpz_div_2exp( tmp, tmp, 32 ); /* tmp >>= 32 */
    hi = mpz_get_ui( tmp );       /* hi = tmp & 0xffffffff */
    mpz_clear( tmp );
    longlong i=(((unsigned long long)hi) << 32) + lo;
    return i;
#endif
  }

  double cpp_convert_1(const gen & g,GIAC_CONTEXT){
    gen h=evalf_double(g,1,context0);
    if (h.type!=_DOUBLE_){
      gensizeerr(contextptr);
      return 0;
    }
    return h._DOUBLE_val;
  }

  complex<double> cpp_convert_4(const gen & g,GIAC_CONTEXT){
    gen h=evalf_double(g,1,context0);
    if (h.type==_DOUBLE_)
      return h._DOUBLE_val;
    if (h.type!=_CPLX || h.subtype!=3){
      gensizeerr(contextptr);
      return 0;
    }
    return complex<double>(h._CPLXptr->_DOUBLE_val,(h._CPLXptr+1)->_DOUBLE_val);
  }

  vecteur cpp_convert_7(const gen & g,GIAC_CONTEXT){
    if (g.type!=_VECT){
      gensizeerr(contextptr);
      return 0;
    }
    return *g._VECTptr;
  }

  std::string cpp_convert_12(const gen & g,GIAC_CONTEXT){
    if (g.type!=_STRNG){
      gensizeerr(contextptr);
      return "";
    }
    return *g._STRNGptr;
  }

  int is_int_or_double(const gen & g){
    if (g.type==_INT_)
      return 2;
    if (g.type==_DOUBLE_)
      return _DOUBLE_;
    if (g.type==_IDNT){
      const char * ch =g._IDNTptr->id_name;
      int s=int(strlen(ch));
      if (s>=3 && ch[s-2]=='_') {
	switch (ch[s-1]){
	case 'i': case 'l':
	  return 2;
	case 'd':
	  return _DOUBLE_;
	}
      }
      return 0;
    }
    if (g.type==_SYMB){
      if (g._SYMBptr->sommet==at_floor || g._SYMBptr->sommet==at_irem || g._SYMBptr->sommet==at_iquo || g._SYMBptr->sommet==at_size)
	return 2;
      vecteur v=lvar(g._SYMBptr->feuille);
      return is_int_or_double(v); // this assumes that the function has the same type as the arguments
    }
    if (g.type!=_VECT)
      return 0;
    const vecteur & v=*g._VECTptr;
    for (int i=0;i<int(v.size());++i){
      if (!is_int_or_double(v[i]))
	return 0;
    }
    return 3;
  }

  int cpp_vartype(const gen & g){
    switch (g.type){
    case _INT_: case _ZINT:
      return 2;
    case _DOUBLE_:
      return _DOUBLE_;
    case _CPLX:
      return _CPLX;
    case _VECT:
      return _VECT;
    case _STRNG:
      return _STRNG;
    }
    if (g.type==_SYMB){
      if (g._SYMBptr->sommet==at_floor || g._SYMBptr->sommet==at_irem || g._SYMBptr->sommet==at_iquo)
	return 2;
      if (g._SYMBptr->sommet==at_size)
	return 2;
      if (g._SYMBptr->sommet==at_abs && cpp_vartype(g._SYMBptr->feuille)==3)
	return 2;
      vecteur v=lvar(g._SYMBptr->feuille);
      const_iterateur it=v.begin(),itend=v.end();
      int cur=-1;
      for (;it!=itend;++it){
	int i=cpp_vartype(*it);
	if (i==0)
	  return 0;
	if (cur==2 && i==_DOUBLE_)
	  cur=_DOUBLE_;
	if (cur==i || (cur==_DOUBLE_ && i==2))
	  continue;
	if (cur==-1)
	  cur=i;
	else 
	  return 0;
      }
      return cur;
    }
    if (g.type!=_IDNT)
      return 0;
    const char * ch=g._IDNTptr->id_name;
    int cl=int(strlen(ch));
    if (cl>=3 && ch[cl-2]=='_'){
      switch (ch[cl-1]){
      case 'i':case 'l':
	return 2;
      case 'd':
	return _DOUBLE_;
      case 'c':
	return _CPLX;
      case 'v':
	return _VECT;
      case 's':
	return _STRNG;
      }
    }
    return 0;
  }

  std::string cprintvars(const gen & f0,bool cst,bool ctx0,const string & sep,int * typeptr,GIAC_CONTEXT){
    vecteur fv=gen2vecteur(f0);
    if (fv.size()==2 && fv[0].type==_VECT)
      fv=*fv[0]._VECTptr;
    string res;
    for (int i=0;;){
      gen fvi=fv[i];
      bool eq=fvi.is_symb_of_sommet(at_equal);
      bool eqsto=fvi.is_symb_of_sommet(at_sto);
      if (eq)
	fvi=fvi[1];
      if (eqsto)
	fvi=fvi[2];
      if (fvi.type!=_IDNT)
	return "Invalid parameter "+fv[i].print(contextptr);
      const char * ch=fvi._IDNTptr->id_name;
      int cl=int(strlen(ch));
      string vtype=cst?"const giac::gen & ":"giac::gen ";
      if (typeptr) *typeptr=0;
      if (cl>=3 && ch[cl-2]=='_'){
	switch (ch[cl-1]){
	case 'i': case 'l':
	  vtype="long ";
	  if (typeptr) *typeptr=2;
	  break;
	case 'd':
	  vtype="double ";
	  if (typeptr) *typeptr=_DOUBLE_;
	  break;
	case 'c':
	  vtype="complex<double> ";
	  if (typeptr) *typeptr=_CPLX;
	  break;
	case 'v':
	  vtype="giac::vecteur ";
	  if (typeptr) *typeptr=_VECT;
	  break;
	case 's':
	  vtype="std::string ";
	  if (typeptr) *typeptr=_STRNG;
	  break;
	}
      }
      if (typeptr) ++typeptr;
      vtype = vtype+ch;
      if (eq || eqsto){ 
	gen g=(fv[i])[1];
	if (eq)
	  g=(fv[i])[2];
#if 1
	int i=cpp_vartype(fvi);
	int gt=cpp_vartype(g);
	if (!i || i==gt || (i==2 && gt==_DOUBLE_) || (i==_DOUBLE_ && gt==2))
	  vtype = vtype + " = " +cprint(g,0,contextptr);
	else 
	  vtype = vtype + " = cpp_convert_"+print_INT_(i)+"("+cprint(g,0,contextptr)+(ctx0?",giac::context0)":",contextptr)");
#else
	vtype = vtype+'=';
	int i=is_int_or_double(fvi);
	if (!i || is_int_or_double(g))
	  vtype = vtype +g.print(contextptr);
	else {
	  vtype = vtype + "("+g.print(contextptr);
	  vtype += (i==_DOUBLE_)?")._DOUBLE_val":").val";
	}
#endif
      }
      ++i;
      if (i==int(fv.size()))
	return res+vtype;
      res = res + vtype + sep;
    }
  }
  
  std::string cpp_stoprint(const gen & g,GIAC_CONTEXT){
    if (g.is_symb_of_sommet(at_at) && g._SYMBptr->feuille.type==_VECT && g._SYMBptr->feuille._VECTptr->size()==2){
      gen f=g._SYMBptr->feuille._VECTptr->front();
      gen i=g._SYMBptr->feuille._VECTptr->back();
      int t=cpp_vartype(i);
      if (t!=2)
	return cprint(f,0,contextptr)+"[cpp_convert_2("+cprint(i,0,contextptr)+",contextptr)]";
    }
    return cprint(g,0,contextptr);
  }

  // name is the program name if args==program(..) or 1 for vectors of instructions or 0 otherwise or -1 for vector to put in a makesequence
  std::string cprint(const gen & args,const gen & name,GIAC_CONTEXT){
    if (args.type==_INT_ && args.subtype==_INT_BOOLEAN)
      return args.val?"true":"false";
    if (args.type==_IDNT){
      if (args==cst_pi)
	return "cst_pi";
    }
    if (args.type==_VECT){
      string sep=name==1?";\n":",";
      string s;
      bool ret=!args._VECTptr->empty() && args._VECTptr->back().is_symb_of_sommet(at_return);
      if (name==0 && args.subtype!=_SEQ__VECT && !ret){
	s="giac::makevecteur(";
      }
      if (name==-1)
	s="giac::makesequence(";
      vecteur::const_iterator it=args._VECTptr->begin(), itend=args._VECTptr->end();
      if (it==itend)
	return "gen(vecteur(0),"+print_INT_(args.subtype)+")";
      for(int i=0;;++i){
	s += cprint(*it,(name.type==_VECT && i<name._VECTptr->size())?name[i]:zero,contextptr);
	++it;
	if (it==itend){
	  break;
	}
	s += sep;
      }
      if (name==0 && args.subtype!=_SEQ__VECT && !ret) s += ")";
      if (name==-1) s += ")";
      if (name==1 || ret) s += ";";
      return s;
    }
    if (args.type==_CPLX)
      return "gen("+args._CPLXptr->print(contextptr)+","+(args._CPLXptr+1)->print(contextptr)+")";
    if (args.type==_FUNC){
      if (args==at_break)
	return "break";
      if (args==at_continue)
	return "continue";
      string name=args.print(contextptr);
      if (name.size()>2 && name[0]=='\'' && name[name.size()-1]=='\'')
	name=name.substr(1,name.size()-2);
      return "at_"+name;
    }
    if (args.type!=_SYMB)
      return args.print(contextptr);
    unary_function_ptr & u=args._SYMBptr->sommet;
    if (u==at_break)
      return "break";
    if (u==at_continue)
      return "continue";
    gen f=args._SYMBptr->feuille;
    if ( (u==at_sto || u==at_array_sto) && f.type==_VECT && f._VECTptr->size()==2){
#if 1
      if (f[1].type==_VECT){
	string res="{\n";
	const vecteur & v=*f[1]._VECTptr;
	int vs=v.size();
	res += "giac::vecteur __tmp_=giac::makevecteur("+cprint(f[0],0,contextptr)+");\n";
	for (int j=0;j<vs;++j){
	  gen var=f[1][j];
	  int i=cpp_vartype(var);
	  res += cpp_stoprint(var,contextptr)+" = cpp_convert_"+print_INT_(i)+"("+"__tmp_["+print_INT_(j)+"],contextptr);";
	}
	return res+"\n}";
      }
      int i=cpp_vartype(f[1]);
      int f0t=cpp_vartype(f[0]);
      if (!i || i==f0t || (i==_DOUBLE_ && f0t==2) || (i==2 && f0t==_DOUBLE_))
	return cpp_stoprint(f[1],contextptr)+'='+cprint(f[0],0,contextptr);;
      string res= cpp_stoprint(f[1],contextptr)+" = cpp_convert_"+print_INT_(i)+"("+cprint(f[0],0,contextptr)+",contextptr)";
      return res;
#else
      int i=is_int_or_double(f[1]);
      if (!i || is_int_or_double(f[0]))
	return f[1].print(contextptr)+'='+cprint(f[0],0,contextptr);;
      string res= f[1].print(contextptr)+"=("+cprint(f[0],0,contextptr);
      res += (i==_DOUBLE_)?")._DOUBLE_val":").val";
      return res;
#endif
    }
    if (u==at_equal && f.type==_VECT && f._VECTptr->size()==2){
#if 1
      int i=cpp_vartype(f[0]);
      if (!i || i==cpp_vartype(f[1]))
	return cpp_stoprint(f[0],contextptr)+'='+cprint(f[1],0,contextptr);
      string res= cpp_stoprint(f[0],contextptr)+" = cpp_convert_"+print_INT_(i)+"("+cprint(f[1],0,contextptr)+",contextptr)";
      return res;
#else
      int i=is_int_or_double(f[0]);
      if (!i || is_int_or_double(f[1]))
	return f[0].print(contextptr)+'='+cprint(f[1],0,contextptr);;
      string res= f[0].print(contextptr)+"=("+cprint(f[1],0,contextptr);
      res += (i==_DOUBLE_)?")._DOUBLE_val":").val";
      return res;
#endif
    }
    if (u==at_size){
      int i=cpp_vartype(f);
      if (i==_VECT)
	return cprint(f,0,contextptr)+".size()";
      return "cpp_convert_7("+cprint(f,0,contextptr)+",contextptr).size()";
    }
    if (u==at_at && f.type==_VECT && f._VECTptr->size()==2){
      int i=cpp_vartype(f._VECTptr->front());
      int j=cpp_vartype(f._VECTptr->back());
      if (i==0 || j==2)
	return f._VECTptr->front().print(contextptr)+"["+cprint(f._VECTptr->back(),0,contextptr)+"]";
      return f._VECTptr->front().print(contextptr)+"[cpp_convert_2("+cprint(f._VECTptr->back(),0,contextptr)+",contextptr)]";
    }
    if (u==at_program && f.type==_VECT && f._VECTptr->size()==3){
      // header
      bool head=name.type==_IDNT;
      string heads;
      int convert=0;
      if (head){
	// IMPROVE if name=="main" make it argv,argc->int
	string rtype="giac::gen ";
	convert=cpp_vartype(name);
	if (convert){
	  switch (convert){
	  case _DOUBLE_:
	    rtype="double ";
	    break;
	  case 2:
	    rtype="long ";
	    break;
	  case _VECT:
	    rtype="giac::vecteur ";
	    break;
	  case _STRNG:
	    rtype="std::string ";
	    break;
	  }
	}
	heads=rtype+name._IDNTptr->id_name+"(";
      }
      else
	heads="giac::gen f(";
      vector<int> argtype(gen2vecteur(f[0]).size());
      heads += cprintvars(f[0],false,true,",",&argtype.front(),contextptr)+",const giac::context * contextptr=0)";
      // core
      string core=cprint(f[2],0,contextptr);
      string res= heads+"{\n"+core+"\n}\n";
      heads="f";
      if (head)
	heads=name._IDNTptr->id_name;
      if (f[0].type!=_VECT || f[0]._VECTptr->size()<=1){
	if (f[0].type==_VECT && f[0]._VECTptr->empty())
	  core ="return "+heads+"(contextptr);";
	else {
#if 1
	  core = "return "+heads+"(cpp_convert_"+print_INT_(cpp_vartype(f[0]._VECTptr->front()))+"(g,contextptr),contextptr);";
#else
	  switch (convert){
	  case 0:
	    core="return "+heads+"(g,contextptr);";
	    break;
	  case 2:
	    core="if (g.type!=_INT_) return gensizeerr(contextptr); else return "+heads+"(g.val,contextptr);";
	    break;
	  case _DOUBLE_:
	    core="if (g.type!=_DOUBLE_) return gensizeerr(contextptr); else return "+heads+"(g._DOUBLE_val,contextptr);";
	    break;
	  }
#endif
	}
      }
      else {
	int nargs=int(f[0]._VECTptr->size());
	core = "if (g.type!=_VECT || g.subtype!=_SEQ__VECT || g._VECTptr->size()!="+print_INT_(nargs)+") return gendimerr(contextptr);\n";
	core += "vecteur v = *g._VECTptr;";
	for (int i=0;i<nargs;++i){
	  string vi="v["+print_INT_(i)+"]";
	  switch(argtype[i]){
	  case _DOUBLE_:
	    core += vi+"=evalf_double("+vi+",1,contextptr);\n";
	    core += "if ("+vi+".type!=_DOUBLE_) return gensizeerr(contextptr);\n";
	    break;
	  case 2:
	    core += "if (!is_integral("+vi+")) return gensizeerr(contextptr);\n";
	    break;
	  case _VECT:
	    core += "if ("+vi+".type!=_VECT) return gensizeerr(contextptr);\n";
	    break;
	  case _STRNG:
	    core += "if ("+vi+".type!=_STRNG) return gensizeerr(contextptr);\n";
	    break;
	  }
	}
	core += "return "+heads+"(" ;
	for (int i=0;;){
	  string vi="v["+print_INT_(i)+"]";
	  switch (argtype[i]){
	  case _DOUBLE_:
	    core += vi+"._DOUBLE_val";
	    break;
	  case 2:
	    core += vi+".val";
	    break;
	  case _VECT:
	    core += "*"+vi+"._VECTptr";
	    break;
	  case _STRNG:
	    core += "*"+vi+"._STRNGptr";
	    break;
	  default:
	    core += vi;
	  }
	  ++i;
	  if (i==nargs) break;
	  core += ",";
	}
	core += ",contextptr);";
      }
      heads="giac::gen _"+heads+"(const giac::gen & g,const giac::context * contextptr)";
      res += heads+"{\n"+core+"\n}\n";
      return res;
    }
    if (u==at_irem && f.type==_VECT && f._VECTptr->size()==2){
      int f0t=cpp_vartype(f[0]);
      int f1t=cpp_vartype(f[1]);
      if ( (f0t==2 || f0t==_DOUBLE_ ) && (f1t==2 || f1t==_DOUBLE_))
	return "long("+cprint(f[0],0,contextptr)+") % long("+ cprint(f[1],0,contextptr)+")" ;
    }
    if (u==at_floor){
      int ft=cpp_vartype(f);
      if (ft==2 || ft==_DOUBLE_)
	return "long("+cprint(f,0,contextptr)+")";
    }
    if (u==at_local){
      gen f0=f[0];
      string s(cprintvars(f0,false,false,";\n",0,contextptr)+";\n");
      return s+cprint(f[1],1,contextptr);
    }
    if ( (u==at_for || u==at_pour) && f.type==_VECT && f._VECTptr->size()==4){
      string res="for(";
      res = res +cprint(f[0],0,contextptr);
      res = res +';';
      res = res +cprint(f[1],0,contextptr);
      res = res +';';
      res = res +cprint(f[2],0,contextptr);
      res = res +"){\n";
      res = res +cprint(f[3],0,contextptr);
      res = res +";\n}\n"; 
      return res;
    }
    if (u==at_bloc)
      return cprint(f,1,contextptr);
    if ( (u==at_ifte || u==at_si) && f.type==_VECT && f._VECTptr->size()==3){
      string res="if(";
      res = res +cprint(f[0],0,contextptr);
      res = res +"){\n";
      res = res +cprint(f[1],0,contextptr);
      res = res +";\n}";
      if (!is_zero(f[2])){
	res = res + " else {\n";
	res = res + cprint(f[2],0,contextptr) + ";\n}";
      }
      return res;
    }
    if (u==at_when){
      string res("giac::_when(giac::makesequence(");
      res += cprint(f,0,contextptr);
      res += "),contextptr)";
      return res;
    }
    if (u==at_try_catch && f.type==_VECT && f._VECTptr->size()>=3){
      string res="try {\n";
      res = res +cprint(f[0],0,contextptr);
      res = res +";\n} catch(";
      res = res +cprint(f[1],0,contextptr);
      res = res +"){\n";
      res = res +cprint(f[2],0,contextptr);
      res = res +";\n}";
      return res;
    }    
    if (u==at_return){
      if (f.type==_VECT)
	return "return giac::makevecteur("+cprint(f,0,contextptr)+")";
      else
	return "return "+cprint(f,0,contextptr);
    }
    if (u.ptr()->cprint)
      return u.ptr()->cprint(args._SYMBptr->feuille,u.ptr()->s,contextptr);
    // operators: if all vars are double or int args.print()
    // otherwise or not an operator giac::opname(,contextptr)
    string res=u.ptr()->print(contextptr);
    int idf0=is_int_or_double(f);
    bool idf= idf0 || cpp_vartype(f)==_CPLX;
    if (u==at_sin || u==at_cos || u==at_tan || u==at_asin || u==at_acos || u==at_atan || u==at_exp || u==at_ln || u==at_abs){
      if (idf)
	return "std::"+args.print(contextptr);
    }
    else
      res = '_'+res;
    if (u.ptr()->printsommet==printsommetasoperator || u==at_division || 
	(idf0 && (u==at_inferieur_strict || u==at_inferieur_egal || u==at_superieur_strict || u==at_superieur_egal))){
      int rs=int(res.size());
      if ( (idf && (rs!=2 || res[1]!='^')) || (rs==2 && (res[1]=='+' || res[1]=='*' || res[1]=='>' || res[1]=='<')) || (rs==3 && (res[1]=='>' || res[1]=='<') && res[2]=='=') ){
	res=u.ptr()->print(contextptr);
	string sres;
	vecteur v=gen2vecteur(f);
	for (int i=0;;){
	  bool b=need_parenthesis(v[i]);
	  if (b) sres += '(';
	  sres += cprint(v[i],0,contextptr);
	  if (b) sres += ')';
	  ++i;
	  if (i>=v.size())
	    break;
	  sres += res;
	}
	return sres;
      }
      if (rs==2){
	switch (res[1]){
	case '+':
	  res="_plus";
	  break;
	case '*':
	  res="_prod";
	  break;
	case '/':
	  res="_division";
	  break;
	case '<':
	  res="_inferieur_strict";
	  break;
	case '>':
	  res="_superieur_strict";
	  break;
	case '=':
	  res="_equal";
	  break;
	case '^':
	  res="_pow";
	  break;
	}
      }
    }
    if (u==at_increment || u==at_decrement){
      if (f.type!=_VECT &&  is_int_or_double(f)){
	return cprint(f,0,contextptr)+(u==at_increment?"++":"--");
      }
      if (f.type==_VECT && f._VECTptr->size()==2 && is_int_or_double(f._VECTptr->front()) && is_int_or_double(f._VECTptr->back()) ){
	return cprint(f._VECTptr->front(),0,contextptr)+(u==at_increment?"+=":"-=")+cprint(f._VECTptr->back(),0,contextptr);
      }
    }
    if (u==at_same || u==at_different || u==at_and || u==at_ou ){
      return cprint(args._SYMBptr->feuille[0],-1,contextptr)+" "+u.ptr()->s+" "+cprint(args._SYMBptr->feuille[1],-1,contextptr);
    }
    if (u==at_neg || u==at_not)
      return u.ptr()->s+cprint(args._SYMBptr->feuille,-1,contextptr);
    if (u==at_inferieur_egal)
      return "giac::is_greater("+cprint(args._SYMBptr->feuille[1],-1,contextptr)+","+cprint(args._SYMBptr->feuille[0],0,contextptr)+",contextptr)";
    if (u==at_superieur_egal)
      return "giac::is_greater("+cprint(args._SYMBptr->feuille[0],-1,contextptr)+","+cprint(args._SYMBptr->feuille[1],0,contextptr)+",contextptr)";
    if (u==at_inferieur_strict)
      return "!giac::is_greater("+cprint(args._SYMBptr->feuille[0],-1,contextptr)+","+cprint(args._SYMBptr->feuille[1],0,contextptr)+",contextptr)";
    if (u==at_superieur_strict)
      return "!giac::is_greater("+cprint(args._SYMBptr->feuille[1],-1,contextptr)+","+cprint(args._SYMBptr->feuille[0],0,contextptr)+",contextptr)";
    res = "giac::"+res + '(';
    res = res + cprint(args._SYMBptr->feuille,-1,contextptr);
    res = res +",contextptr)";
    return res;
    // ...
  }

  std::string cprint(const gen & args,GIAC_CONTEXT){
    return cprint(args,0,contextptr);
  }

  gen _cprint(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    int x=xcas_mode(contextptr),l=language(contextptr);
    xcas_mode(0,contextptr);
    language(2,contextptr);
    gen args_evaled=eval(args,1,contextptr);
    string s=cprint(args_evaled,args,contextptr);
    xcas_mode(x,contextptr);
    language(l,contextptr);
    return string2gen(s,false);
  }
  static const char _cprint_s []="cprint";
  static define_unary_function_eval (__cprint,&_cprint,_cprint_s);
  define_unary_function_ptr5( at_cprint ,alias_at_cprint,&__cprint,_QUOTE_ARGUMENTS,true);

#if !defined GIAC_HAS_STO_38 && !defined NSPIRE && !defined FXCG
  int cpp_write_compile(const string & filename,const string & funcname,const string &s,GIAC_CONTEXT){
    ofstream of(filename.c_str());
#ifdef __APPLE__
    of << "// -*- mode:c++; compile-command:\" c++ -I/Applications/usr/include -I.. -I. -fPIC -DPIC -g -O2 -dynamiclib giac_" << funcname << ".cpp -o libgiac_" << funcname << ".dylib -L/Applications/usr/lib -L/Applications/usr/64/local/lib -lgiac \" -*-" << endl;
#else
    of << "// -*- mode:c++; compile-command:\" c++ -I.. -I. -fPIC -DPIC -g -O2 -c giac_" << funcname << ".cpp -o giac_" << funcname << ".lo && cc -shared giac_"<<funcname<<".lo -lgiac -lc -Wl,-soname -Wl,libgiac_"<<funcname<<"so.0 -o libgiac_"<<funcname<<".so.0.0.0 && ln -sf libgiac_"<<funcname<<".so.0.0.0 libgiac_"<<funcname<<".so\" -*-" << endl;
#endif
    of << "#include <giac/config.h>" << endl;
    of << "#include <giac/giac.h>" << endl;
    of << "using namespace std;" << endl;
    of << "namespace giac {" << endl;
    of << "inline gen _sqrt(const gen & a,GIAC_CONTEXT){return sqrt(a,contextptr);}" << endl;
    of << "inline gen _arg(const gen & a,GIAC_CONTEXT){return arg(a,contextptr);}" << endl;
    of << "inline gen _complex(const gen & a,GIAC_CONTEXT){return _build_complex(a,contextptr);}" << endl;
    of << "inline gen operator *(const gen & a,double b){return b*a;}" << endl;
    of << "inline bool operator <= (const gen & a,const gen &b){ return is_greater(b,a,giac::context0); }\ninline bool operator >= (const gen & a,const gen &b){ return is_greater(a,b,giac::context0); }" <<endl;
    of << s << endl;
    of << "const string _"+funcname+"_s(\""+funcname+"\");" << endl;
    of << "unary_function_eval __"+funcname+"(0,&_"+funcname+",_"+funcname+"_s);" << endl;
    of << "unary_function_ptr at_"+funcname+"(&__"+funcname+",0,true);" << endl;
    of << "}" << endl;
    of.close();
    *logptr(contextptr) << "File " << filename << " created." << endl;
    string cmd="indent -br -brf -l256 giac_"+funcname+".cpp"; int not_ok;
#ifndef __APPLE__
    *logptr(contextptr) << "Running " << cmd << endl;
    not_ok=system_no_deprecation(cmd.c_str());
    if (not_ok)
      *logptr(contextptr) << "Warning, indent not found, please install for nice output" << endl;
#endif
#ifdef __APPLE__
    cmd="g++ -dynamiclib -I/Applications/usr/include -I.. -I. -fPIC -DPIC -g -O2 -dy giac_"+funcname+".cpp -o libgiac_"+funcname+".dylib -L/Applications/usr/lib -L/Applications/usr/64/local/lib -lgiac";
#else
    cmd="c++ -I.. -I. -fPIC -DPIC -g -O2 -c giac_"+funcname+".cpp -o giac_"+funcname+".lo";
#endif
    *logptr(contextptr) << "Running\n" << cmd << endl;
    not_ok=system_no_deprecation(cmd.c_str());
    if (not_ok){
      *logptr(contextptr) << "Unable to compile, please fix cpp file" << endl;
      return -1;
    }
    //cmd="ln -sf giac_"+funcname+".lo giac_"+funcname+".o";
    //*logptr(contextptr) << "Running " << cmd << endl;
    //not_ok=system_no_deprecation(cmd.c_str());
#ifndef __APPLE__
    cmd="cc -shared giac_"+funcname+".lo -lgiac -lc -Wl,-soname -Wl,libgiac_"+funcname+".so.0 -o libgiac_"+funcname+".so.0.0.0";
    *logptr(contextptr) << cmd << endl;
    not_ok=system_no_deprecation(cmd.c_str());
    if (not_ok){
      *logptr(contextptr) << "Unable to create shared library, perhaps missing libraries?" << endl;
      return -2;
    }
    //cmd="ln -sf libgiac_"+funcname+".so.0.0.0 libgiac_"+funcname+".so.0";
    //system_no_deprecation(cmd.c_str());
    cmd="ln -sf libgiac_"+funcname+".so.0.0.0 libgiac_"+funcname+".so";
    *logptr(contextptr) << cmd << endl;
    not_ok=system_no_deprecation(cmd.c_str());
#endif
    *logptr(contextptr) << "You can now run insmod(\"" << funcname << "\")" << endl;
    return 1;
  }

  // make a cpp translation file
  gen _cpp(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen tmp=check_secure();
    if (is_undef(tmp)) return tmp;
    int x=xcas_mode(contextptr),l=language(contextptr),l0=language(context0),p=python_compat(contextptr);
    if (args.type==_IDNT){
      xcas_mode(0,contextptr);
      language(2,contextptr);
      language(2,context0);
      python_compat(0,contextptr);
      gen args_evaled=eval(args,1,contextptr);
      string s=cprint(args_evaled,args,contextptr);
      python_compat(p,contextptr);
      language(l,contextptr);
      language(l0,context0);
      xcas_mode(x,contextptr);
      string funcname=args.print(contextptr);
      string filename="giac_"+funcname+".cpp";
      return cpp_write_compile(filename,funcname,s,contextptr);
    }
    if (args.type!=_VECT || args.subtype!=_SEQ__VECT || args._VECTptr->size()<2)
      return gensizeerr(contextptr);
    xcas_mode(0,contextptr);
    language(2,contextptr);
    language(2,context0);
    python_compat(0,contextptr);
    gen arg=args._VECTptr->front();
    gen args_evaled=eval(arg,1,contextptr);
    string s=cprint(args_evaled,arg,contextptr);
    for (int i=1;i<args._VECTptr->size();++i){
      arg=(*args._VECTptr)[i];
      args_evaled=eval(arg,1,contextptr);
      s += cprint(args_evaled,arg,contextptr);
    }
    python_compat(p,contextptr);
    language(l,contextptr);
    language(l0,context0);
    xcas_mode(x,contextptr);
    string funcname=arg.print(contextptr);
    string filename="giac_"+funcname+".cpp";
    return cpp_write_compile(filename,funcname,s,contextptr);
    return 1;
  }
  static const char _cpp_s []="cpp";
  static define_unary_function_eval (__cpp,&_cpp,_cpp_s);
  define_unary_function_ptr5( at_cpp ,alias_at_cpp,&__cpp,_QUOTE_ARGUMENTS,true);
#endif

  gen _hexprint(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type == _INT_)
      return string2gen(hexa_print_INT_(g.val), false);  
    if (g.type == _ZINT)
      return string2gen(hexa_print_ZINT(*g._ZINTptr), false);
    return gentypeerr();
  }
  static const char _hexprint_s []="hexprint";
  static define_unary_function_eval (__hexprint,&_hexprint,_hexprint_s);
  define_unary_function_ptr5( at_hexprint ,alias_at_hexprint,&__hexprint,0,true);
  static const char _giac_hex_s []="hex";
  static define_unary_function_eval (__giac_hex,&_hexprint,_giac_hex_s);
  define_unary_function_ptr5( at_giac_hex ,alias_at_giac_hex,&__giac_hex,0,true)  
  gen _octprint(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type == _INT_)
      return string2gen(octal_print_INT_(g.val), false);	
    if (g.type == _ZINT)
      return string2gen(octal_print_ZINT(*g._ZINTptr), false);
    return gentypeerr();
  }
  static const char _octprint_s []="octprint";
  static define_unary_function_eval (__octprint,&_octprint,_octprint_s);
  define_unary_function_ptr5( at_octprint ,alias_at_octprint,&__octprint,0,true);
  
  gen _binprint(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type == _INT_)
      return string2gen(binary_print_INT_(g.val), false);
    if (g.type == _ZINT)
      return string2gen(binary_print_ZINT(*g._ZINTptr), false);
    return gentypeerr();
  }
  static const char _binprint_s[]="binprint";
  static define_unary_function_eval(__binprint,&_binprint,_binprint_s);
  define_unary_function_ptr5(at_binprint,alias_at_binprint,&__binprint,0,true);
  // const unary_function_ptr at_binprint (&__binprint,0,true);

  static const char _giac_bin_s []="bin";
  static define_unary_function_eval (__giac_bin,&_binprint,_giac_bin_s);
  define_unary_function_ptr5( at_giac_bin ,alias_at_giac_bin,&__giac_bin,0,true);

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
