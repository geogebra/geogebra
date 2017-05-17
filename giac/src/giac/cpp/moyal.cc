// -*- mode:C++ ; compile-command: "g++ -DHAVE_CONFIG_H -m32 -I. -I.. -DIN_GIAC -DGIAC_GENERIC_CONSTANTS  -g -c -fno-strict-aliasing moyal.cc" -*-
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
using namespace std;
#include <stdexcept>
#include <cmath>
#include <math.h>
#include <cstdlib>
#include "sym2poly.h"
#include "usual.h"
#include "moyal.h"
#include "solve.h"
#include "intg.h"
#include "permu.h"
#include "giacintl.h"
#ifdef HAVE_LIBGSL
#include <gsl/gsl_sf_airy.h>
#include <gsl/gsl_sf_erf.h>
#endif

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

#ifdef HAVE_SSCL
  gen moyal(const gen & a,const gen & b, const gen &vars,const gen & order){
    return symb_moyal(a,b,vars,order);
  }

#else // HAVE_SSCL
  gen moyal(const gen & a,const gen & b, const gen &vars,const gen & order){
    return symb_moyal(a,b,vars,order);
  }

#endif // HAVE_SSCL

  // "unary" version
  gen _moyal(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    int s=int(args._VECTptr->size());
    if (s!=4) return gensizeerr(gettext("moyal.cc/_moyal"));
    return moyal( (*(args._VECTptr))[0],(*(args._VECTptr))[1],(*(args._VECTptr))[2],(*(args._VECTptr))[3]);
  }
  static const char _moyal_s  []="moyal";
  static string texprintasmoyal(const gen & g,const char * s,GIAC_CONTEXT){
    return texprintsommetasoperator(g,"#",contextptr);
  }
  static define_unary_function_eval4 (__moyal,&_moyal,_moyal_s,0,&texprintasmoyal);
  define_unary_function_ptr5( at_moyal ,alias_at_moyal,&__moyal,0,true);

  gen incomplete_beta(double a,double b,double p,bool regularize){ // regularize=true by default
    // I_p(a,b)=1/B(a,b)*int(t^(a-1)*(1-t)^(b-1),t=0..p)
    // =p^a*(1-p)^(b-1)/B(a,b)*continued fraction expansion
    // 1/(1+e2/(1+e3/(1+...)))
    // e_2m=-(a+m-1)*(b-m)/(a+2*m-2)/(a+2*m-1)*(x/(1-x))
    // e_2m+1= m*(a+b-1+m)/(a+2*m-1)/(a+2*m)*(x/(1-x))
    // assumes p in [0,1]
    if (p<=0)
      return 0;
    if (a<=0 || b<=0)
      return 1;
    // add here test for returning 1 if b>>a
    if (p>a/double(a+b)){
      gen tmp=incomplete_beta(b,a,1-p,true);
      if (regularize)
	return 1-tmp;
      return Beta(a,b,context0)*(1-tmp);
    }
    // Continued fraction expansion: a1/(b1+a2/(b2+...)))
    // P0=1, P1=a1, Q0=1, Q1=b1
    // j>=2: Pj=bj*Pj-1+aj*Pj-2, Qj=bj*Qj-1+aj*Qj-2
    // Here bm=1, am=em, etc.
    long_double Pm2=0,Pm1=1,Pm,Qm2=1,Qm1=1,Qm,am,x=p/(1-p);
    long_double deux=9007199254740992.,invdeux=1/deux;
    for (long_double m=1;m<100;++m){
      // odd term
      am=-(a+m-1)*(b-m)/(a+2*m-2)/(a+2*m-1)*x;
      Pm=Pm1+am*Pm2;
      Qm=Qm1+am*Qm2;
      Pm2=Pm1; Pm1=Pm;
      Qm2=Qm1; Qm1=Qm;
      // even term
      am=m*(a+b-1+m)/(a+2*m-1)/(a+2*m)*x;
      Pm=Pm1+am*Pm2;
      Qm=Qm1+am*Qm2;
      // cerr << Pm/Qm << " " << Pm2/Qm2 << endl;
      if (std::abs(Pm/Qm-Pm2/Qm2)<1e-16*std::abs(Pm/Qm)){
	double res=Pm/Qm;
#if 0 // def VISUALC // no lgamma available
	gen r=res/a*std::pow(p,a)*std::pow(1-p,b-1);
	if (regularize)
	  r=r*Gamma(a+b,context0)/Gamma(a,context0)/Gamma(b,context0);
	return r;
#else
	if (regularize)
	  return res/a*std::exp(a*std::log(p)+(b-1)*std::log(1-p)+lngamma(a+b)-lngamma(a)-lngamma(b));
	return res/a*std::exp(a*std::log(p)+(b-1)*std::log(1-p));
#endif
      }	
      Pm2=Pm1; Pm1=Pm;
      Qm2=Qm1; Qm1=Qm;
      if (std::abs(Pm)>deux){
	Pm2 *= invdeux; Qm2 *= invdeux; Pm1 *= invdeux; Qm1 *= invdeux;
      }
      if (std::abs(Pm)<invdeux){
	Pm2 *= deux; Qm2 *= deux; Pm1 *= deux; Qm1 *= deux;
      }
    }
    return undef; //error
  }

  static void beta_mult(gen &res,gen & a,GIAC_CONTEXT){
    for (;;){
      gen a1=a-1;
      if (!is_positive(a1,contextptr))
	return;
      res=a1*res;
      a=a1;
    }
  }
  gen Beta(const gen & a,const gen& b,GIAC_CONTEXT){
    if (a.type==_DOUBLE_ || b.type==_DOUBLE_ ||
	a.type==_FLOAT_ || b.type==_FLOAT_ ||
	a.type==_CPLX || b.type==_CPLX ){
      gen A=evalf_double(a,1,contextptr);
      gen B=evalf_double(b,1,contextptr);
      gen C=lngamma(A+B,contextptr);
      A=lngamma(A,contextptr);
      B=lngamma(B,contextptr);
      C=A+B-C;
      C=exp(C,contextptr);
      return C;
    }
    gen n;
    if (a.type==_FRAC && b.type==_FRAC && is_positive(a,contextptr) && is_positive(b,contextptr) && is_integer( (n=a+b) )){
      gen res=1,a_(a),b_(b);
      beta_mult(res,a_,contextptr);
      beta_mult(res,b_,contextptr);
      if (a_+b_==1){
	return ratnormal(res*cst_pi/sin(cst_pi*a_,contextptr)/Gamma(n,contextptr),contextptr);
      }
    }
    return Gamma(a,contextptr)*Gamma(b,contextptr)/Gamma(a+b,contextptr);
  }
  gen _Beta(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_Beta,args);
    vecteur v=*args._VECTptr;
    int s=int(v.size());
    if (s>2 && (v[0].type==_DOUBLE_ || v[1].type==_DOUBLE_ || v[2].type==_DOUBLE_ || v[0].type==_REAL || v[1].type==_REAL || v[2].type==_REAL)){
      gen tmp=evalf_double(v,1,contextptr);
      if (tmp.type==_VECT)
	v=*tmp._VECTptr;
      s=int(v.size());
    }
    if ( (s==3 || s==4) && v[0].type==_DOUBLE_ && v[1].type==_DOUBLE_ && v[2].type==_DOUBLE_ ){
      return incomplete_beta(v[0]._DOUBLE_val,v[1]._DOUBLE_val,v[2]._DOUBLE_val, s==4 && !is_zero(v[3]) );
    }
    if (s<2 || s>4)
      return gendimerr(contextptr);
    if (s==4){
      if (is_zero(v[3]))
	return symbolic(at_Beta,makesequence(v[0],v[1],v[2]));
      return symbolic(at_Beta,makesequence(v[0],v[1],v[2]))/Beta(v[0],v[1],contextptr);
    }
    if (s!=2)
      return symbolic(at_Beta,args);
    return Beta(v[0],v[1],contextptr);
  }
  static const char _Beta_s []="Beta";
  static define_unary_function_eval (__Beta,&_Beta,_Beta_s);
  define_unary_function_ptr5( at_Beta ,alias_at_Beta,&__Beta,0,true);

  gen _upper_incomplete_gamma(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_upper_incomplete_gamma,args);
    vecteur v=*args._VECTptr;
    int s=int(v.size());
    if (s>=2 && (v[0].type==_DOUBLE_ || v[1].type==_DOUBLE_)){
      v[0]=evalf_double(v[0],1,contextptr);
      v[1]=evalf_double(v[1],1,contextptr);
    }
    if ( (s==2 || s==3) && v[0].type==_DOUBLE_ && v[1].type==_DOUBLE_ ){
      double res=upper_incomplete_gammad(v[0]._DOUBLE_val,v[1]._DOUBLE_val,s==3?!is_zero(v[2]):false);
      if (res==-1){
	if (s==3 && !is_zero(v[2]))
	  return 1-lower_incomplete_gamma(v[0]._DOUBLE_val,v[1]._DOUBLE_val,true,contextptr); 
	return Gamma(v[0]._DOUBLE_val,contextptr)-lower_incomplete_gamma(v[0]._DOUBLE_val,v[1]._DOUBLE_val,false,contextptr); 
	// return gensizeerr(contextptr);
      }
      return res;
    }
    if (s<2 || s>3)
      return gendimerr(contextptr);
    if (abs_calc_mode(contextptr)!=38) // check may be removed if ugamma declared
      return symbolic(at_upper_incomplete_gamma,args);
    if (s==3){
      if (is_zero(v[2]))
	return Gamma(v[0],contextptr)-symbolic(at_lower_incomplete_gamma,makesequence(v[0],v[1]));
      return symbolic(at_Gamma,makesequence(v[0],v[1],1));
    }
    return symbolic(at_upper_incomplete_gamma,args);
  }
  static const char _upper_incomplete_gamma_s []="ugamma";
  static define_unary_function_eval (__upper_incomplete_gamma,&_upper_incomplete_gamma,_upper_incomplete_gamma_s);
  define_unary_function_ptr5( at_upper_incomplete_gamma ,alias_at_upper_incomplete_gamma,&__upper_incomplete_gamma,0,true);

  gen _polygamma(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_polygamma,args);
    vecteur v=*args._VECTptr;
    int s=int(v.size());
    if (args.subtype==_SEQ__VECT && s==2)
      return _Psi(makesequence(v[1],v[0]),contextptr);
    return symbolic(at_polygamma,args);
  }
  static const char _polygamma_s []="polygamma";
  static define_unary_function_eval (__polygamma,&_polygamma,_polygamma_s);
  define_unary_function_ptr5( at_polygamma ,alias_at_polygamma,&__polygamma,0,true);

  gen Airy_Ai(const gen & x,GIAC_CONTEXT){
    gen e=x.evalf(1,contextptr);
#ifdef HAVE_LIBGSL
    if (e.type==_DOUBLE_)
      return gsl_sf_airy_Ai(e._DOUBLE_val,GSL_PREC_DOUBLE);
#endif
    return symbolic(at_Airy_Ai,x);
  }
  gen _Airy_Ai(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return apply(args,Airy_Ai,contextptr);
  }
  static const char _Airy_Ai_s []="Airy_Ai";
  static define_unary_function_eval (__Airy_Ai,&_Airy_Ai,_Airy_Ai_s);
  define_unary_function_ptr5( at_Airy_Ai ,alias_at_Airy_Ai,&__Airy_Ai,0,true);

  gen Airy_Bi(const gen & x,GIAC_CONTEXT){
    gen e=x.evalf(1,contextptr);
#ifdef HAVE_LIBGSL
    if (e.type==_DOUBLE_)
      return gsl_sf_airy_Bi(e._DOUBLE_val,GSL_PREC_DOUBLE);
#endif
    return symbolic(at_Airy_Bi,x);
  }
  gen _Airy_Bi(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return apply(args,Airy_Bi,contextptr);
  }
  static const char _Airy_Bi_s []="Airy_Bi";
  static define_unary_function_eval (__Airy_Bi,&_Airy_Bi,_Airy_Bi_s);
  define_unary_function_ptr5( at_Airy_Bi ,alias_at_Airy_Bi,&__Airy_Bi,0,true);

  gen _UTPN(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return erfc(args/plus_sqrt2,contextptr)/2;
    vecteur & v=*args._VECTptr;
    int s=int(v.size());
    if (s!=3 || is_zero(v[1]))
      return gensizeerr(contextptr);
    return erfc((v[2]-v[0])/sqrt(2*v[1],contextptr),contextptr)/2;
  }
  static const char _UTPN_s []="UTPN";
  static define_unary_function_eval (__UTPN,&_UTPN,_UTPN_s);
  define_unary_function_ptr5( at_UTPN ,alias_at_UTPN,&__UTPN,0,true);

  double randNorm(GIAC_CONTEXT){
    /*
    double d=rand()/(rand_max2+1.0);
    d=2*d-1;
    identificateur x(" x");
    return newton(erf(x)-d,x,d);
    */
    double u=giac_rand(contextptr)/(rand_max2+1.0);
    double d=giac_rand(contextptr)/(rand_max2+1.0);
    return std::sqrt(-2*std::log(u))*std::cos(2*M_PI*d);
  }
  void randnorm2(double & r1,double & r2,GIAC_CONTEXT){
    /*
    double d=rand()/(rand_max2+1.0);
    d=2*d-1;
    identificateur x(" x");
    return newton(erf(x)-d,x,d);
    */
    for (;;){
      double u=giac_rand(contextptr)/(rand_max2+1.0);
      double v=giac_rand(contextptr)/(rand_max2+1.0);
      double w=u*u+v*v;
      if (w>0 && w<=1){
	w=std::sqrt(-2*std::log(w)/w);
	r1=u*w;
	r2=v*w;
	return;
      }
    }
  }

  gen _randNorm(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type==_VECT && args._VECTptr->empty())
      return randNorm(contextptr);
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    if (v[1].type==_VECT){
      if (!is_squarematrix(v[1]))
	return gendimerr(contextptr);
      int n=int(v[1]._VECTptr->size());
      vecteur w(n);
      for (int i=0;i<n;++i)
	w[i]=randNorm(contextptr);
      return evalf(v[0]+v[1]*w,1,contextptr);
    }
    return evalf(v[0]+v[1]*randNorm(contextptr),1,contextptr);
  }
  static const char _randNorm_s []="randNorm";
  static define_unary_function_eval (__randNorm,&_randNorm,_randNorm_s);
  define_unary_function_ptr5( at_randNorm ,alias_at_randNorm,&__randNorm,0,true);

  static const char _randnormald_s []="randnormald";
  static define_unary_function_eval (__randnormald,&_randNorm,_randnormald_s);
  define_unary_function_ptr5( at_randnormald ,alias_at_randnormald,&__randnormald,0,true);

  double randchisquare(int k,GIAC_CONTEXT){
    double res=0.0;
    for (int i=0;i<k;++i){
      double u=giac_rand(contextptr)/(rand_max2+1.0);
      double d=giac_rand(contextptr)/(rand_max2+1.0);
      u=-2*std::log(u);
      d=std::cos(2*M_PI*d);
      d=d*d*u;
      res += d;
    }
    return res;
  }
  gen _randchisquare(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen g(args);
    if (!is_integral(g) || g.type!=_INT_ || g.val<=0 || g.val>1000)
      return gensizeerr(contextptr);
    return randchisquare(g.val,contextptr);
  }
  static const char _randchisquare_s []="randchisquare";
  static define_unary_function_eval (__randchisquare,&_randchisquare,_randchisquare_s);
  define_unary_function_ptr5( at_randchisquare ,alias_at_randchisquare,&__randchisquare,0,true);

  static const char _randchisquared_s []="randchisquared";
  static define_unary_function_eval (__randchisquared,&_randchisquare,_randchisquared_s);
  define_unary_function_ptr5( at_randchisquared ,alias_at_randchisquared,&__randchisquared,0,true);

  double randstudent(int k,GIAC_CONTEXT){
    return randNorm(contextptr)/std::sqrt(randchisquare(k,contextptr)/k);
  }
  gen _randstudent(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen g(args);
    if (!is_integral(g) || g.type!=_INT_ || g.val<=0 || g.val>1000)
      return gensizeerr(contextptr);
    return randstudent(g.val,contextptr);
  }
  static const char _randstudent_s []="randstudent";
  static define_unary_function_eval (__randstudent,&_randstudent,_randstudent_s);
  define_unary_function_ptr5( at_randstudent ,alias_at_randstudent,&__randstudent,0,true);
  static const char _randstudentd_s []="randstudentd";
  static define_unary_function_eval (__randstudentd,&_randstudent,_randstudentd_s);
  define_unary_function_ptr5( at_randstudentd ,alias_at_randstudentd,&__randstudentd,0,true);

  double randfisher(int k1,int k2,GIAC_CONTEXT){
    return randchisquare(k1,contextptr)/k1/(randchisquare(k2,contextptr)/k2);
  }
  gen _randfisher(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    gen g1(args._VECTptr->front()),g2(args._VECTptr->back());
    if (!is_integral(g1) || g1.type!=_INT_ || g1.val<=0 || g1.val>1000 ||
	!is_integral(g2) || g2.type!=_INT_ || g2.val<=0 || g2.val>1000
	)
      return gensizeerr(contextptr);
    return randfisher(g1.val,g2.val,contextptr);
  }
  static const char _randfisher_s []="randfisher";
  static define_unary_function_eval (__randfisher,&_randfisher,_randfisher_s);
  define_unary_function_ptr5( at_randfisher ,alias_at_randfisher,&__randfisher,0,true);
  static const char _randfisherd_s []="randfisherd";
  static define_unary_function_eval (__randfisherd,&_randfisher,_randfisherd_s);
  define_unary_function_ptr5( at_randfisherd ,alias_at_randfisherd,&__randfisherd,0,true);

  static gen normald(const gen & m,const gen & s,const gen & x,GIAC_CONTEXT){
    gen v(s*s);
    return inv(sqrt(2*cst_pi*v,contextptr),contextptr)*exp(-pow(x-m,2)/(2*v),contextptr);
  }
  gen _normald(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return normald(0,1,g,contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return symbolic(at_normald,g);
    if (s==3)
      return normald(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _normald_s []="normald";
  static define_unary_function_eval (__normald,&_normald,_normald_s);
  define_unary_function_ptr5( at_normald ,alias_at_normald,&__normald,0,true);

  static const char _NORMALD_s []="NORMALD";
  static define_unary_function_eval (__NORMALD,&_normald,_NORMALD_s);
  define_unary_function_ptr5( at_NORMALD ,alias_at_NORMALD,&__NORMALD,0,true);

  gen _randexp(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    double u=giac_rand(contextptr)/(rand_max2+1.0);
    return -gen(std::log(1-u))/args;
  }
  static const char _randexp_s []="randexp";
  static define_unary_function_eval (__randexp,&_randexp,_randexp_s);
  define_unary_function_ptr5( at_randexp ,alias_at_randexp,&__randexp,0,true);

  // Normal cumulative distribution function
  // proba that X<x for X following a normal distrib of mean mean and dev dev
  // arg = vector [mean,dev,x] or x alone (mean=0, dev=1)
  static gen normal_cdf(const gen & g,GIAC_CONTEXT){
    return rdiv(erf(ratnormal(plus_sqrt2_2*g,contextptr),contextptr)+plus_one,2,contextptr);
  }
  gen _normal_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return normal_cdf(g,contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return normal_cdf(v[1],contextptr)-normal_cdf(v[0],contextptr); 
    if (s==3)
      return normal_cdf((v[2]-v[0])/v[1],contextptr);
    if (s==4)
      return normal_cdf((v[3]-v[0])/v[1],contextptr)-normal_cdf((v[2]-v[0])/v[1],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _normal_cdf_s []="normal_cdf";
  static define_unary_function_eval (__normal_cdf,&_normal_cdf,_normal_cdf_s);
  define_unary_function_ptr5( at_normal_cdf ,alias_at_normal_cdf,&__normal_cdf,0,true);

  static const char _normald_cdf_s []="normald_cdf";
  static define_unary_function_eval (__normald_cdf,&_normal_cdf,_normald_cdf_s);
  define_unary_function_ptr5( at_normald_cdf ,alias_at_normald_cdf,&__normald_cdf,0,true);

  // returns x s.t. UTPN(x) ~ y
  // ref Abramowitz & Stegun equation 26.2.22
  static double utpn_initial_guess(double y){
    double t=std::sqrt(-2*std::log(y));
    t=t-(2.30753+.27061*t)/(1+0.99229*t+0.04481*t*t);
    return t;
  }
  static gen utpn_inverse(double y,GIAC_CONTEXT){
    identificateur x(" x");
    return newton(erf(x/std::sqrt(2.0),contextptr)+2*y-1,x,utpn_initial_guess(y),NEWTON_DEFAULT_ITERATION,1e-5,1e-12,true,1,0,1,0,.5,contextptr);
  }
  static gen normal_icdf(const gen & g_orig,GIAC_CONTEXT){
    gen g=evalf_double(g_orig,1,contextptr);
    if (g.type!=_DOUBLE_ || g._DOUBLE_val<0 || g._DOUBLE_val>1)
      return gensizeerr(contextptr);
    if (g._DOUBLE_val==0)
      return minus_inf;
    if (g._DOUBLE_val==1)
      return plus_inf;
    return utpn_inverse(1-g._DOUBLE_val,contextptr);
    // identificateur x(" x");
    // plus_sqrt2*newton(erf(x)+1-2*g,x,0);
  }
  gen _normal_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return normal_icdf(g,contextptr);
    vecteur & v=*g._VECTptr;
    if (v.size()!=3)
      return gensizeerr(contextptr);
    return v[0]+v[1]*normal_icdf(v[2],contextptr);
  }
  static const char _normal_icdf_s []="normal_icdf";
  static define_unary_function_eval (__normal_icdf,&_normal_icdf,_normal_icdf_s);
  define_unary_function_ptr5( at_normal_icdf ,alias_at_normal_icdf,&__normal_icdf,0,true);

  static const char _normald_icdf_s []="normald_icdf";
  static define_unary_function_eval (__normald_icdf,&_normal_icdf,_normald_icdf_s);
  define_unary_function_ptr5( at_normald_icdf ,alias_at_normald_icdf,&__normald_icdf,0,true);

  gen apply3rd(const gen & e1, const gen & e2,const gen & e3,const context * contextptr, gen (* f) (const gen &, const gen &,const gen &,const context *) ){
    if (e3.type!=_VECT)
      return f(e1,e2,e3,contextptr);
    const_iterateur it=e3._VECTptr->begin(),itend=e3._VECTptr->end();
    vecteur v;
    v.reserve(itend-it);
    for (;it!=itend;++it){
      gen tmp=f(e1,e2,*it,contextptr);
      if (is_undef(tmp))
	return gen2vecteur(tmp);
      v.push_back(tmp);
    }
    return gen(v,e3.subtype);
  }

  gen binomial(const gen & N,const gen & K,const gen & P,GIAC_CONTEXT){
    gen n(N),k(K),p(P);
    is_integral(n); is_integral(k); is_integral(p);
    if (p.type==_VECT)
      return apply3rd(n,k,p,contextptr,binomial);
    if ( (is_zero(p) && is_zero(k)) || (is_one(p) && n==k))
      return 1;
    if (is_strictly_positive(-n,contextptr))
      return _negbinomial(makesequence(-n,k,p),contextptr);
    if (is_strictly_positive(k,contextptr) && is_strictly_greater(1,k,contextptr)){
      if (is_strictly_positive(p,contextptr) && is_strictly_greater(1,p,contextptr))
	return gensizeerr(contextptr);
      return binomial(n,p,k,contextptr);	
    }
    if (k.type==_DOUBLE_ || k.type==_FLOAT_ || k.type==_FRAC){
      if (p.type==_DOUBLE_ || p.type==_FLOAT_ || p.type==_FRAC)
	return gensizeerr(contextptr);
      return binomial(n,p,k,contextptr);
    }
    if (p.type==_DOUBLE_ || p.type==_FLOAT_){
#if 0 //def VISUALC
      gen nd=evalf2bcd(n,1,contextptr);
      gen kd=evalf2bcd(k,1,contextptr);
      gen pd=evalf2bcd(p,1,contextptr);
      if (nd.type==_FLOAT_ && kd.type==_FLOAT_ && pd.type==_FLOAT_){
	return Gamma(nd+1,contextptr)/Gamma(kd+1,contextptr)/Gamma(nd-kd+1,contextptr)*pow(pd,kd,contextptr)*pow(1-pd,nd-kd,contextptr);
      }
#else
      gen nd=evalf_double(n,1,contextptr);
      gen kd=evalf_double(k,1,contextptr);
      gen pd=evalf_double(p,1,contextptr);
      if (nd.type==_DOUBLE_ && kd.type==_DOUBLE_ && pd.type==_DOUBLE_){
	double ndd=nd._DOUBLE_val,kdd=kd._DOUBLE_val,pdd=pd._DOUBLE_val,nk=ndd-kdd;
	return std::exp(lngamma(ndd+1)-lngamma(kdd+1)-lngamma(nk+1)+kdd*std::log(pdd)+nk*std::log(1-pdd));
      }
#endif
    }
    if (!is_positive(p,contextptr) || !is_greater(1,p,contextptr)){
      if (abs_calc_mode(contextptr)==38)
	return gensizeerr(contextptr);
      if (calc_mode(contextptr)!=1)
	*logptr(contextptr) << "Assuming probability=" << p << endl; 
    }
    return comb(n,k,contextptr)*pow(p,k,contextptr)*pow(1-p,n-k,contextptr);
  }
  gen _binomial(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2){
      if (is_strictly_positive(v[1],contextptr) && is_strictly_greater(1,v[1],contextptr))
	return symbolic(at_binomial,g); // inert form for binomial pseudo-random generation
      gen v0=evalf_double(v[0],1,contextptr),v1=evalf_double(v[1],1,contextptr);
      if (v0.type!=_DOUBLE_ || v1.type!=_DOUBLE_)
	return _factorial(v[0],contextptr)/(_factorial(v[1],contextptr)*_factorial(v[0]-v[1],contextptr));
      return comb(v[0],v[1],contextptr);
    }
    if (s==3){
      if (0 && calc_mode(contextptr)==1)
	return binomial(v[0],v[2],v[1],contextptr);	
      return binomial(v[0],v[1],v[2],contextptr);
    }
    return gensizeerr(contextptr);
  }
  static const char _binomial_s []="binomial";
  static define_unary_function_eval (__binomial,&_binomial,_binomial_s);
  define_unary_function_ptr5( at_binomial ,alias_at_binomial,&__binomial,0,true);

  static const char _BINOMIAL_s []="BINOMIAL";
  static define_unary_function_eval (__BINOMIAL,&_binomial,_BINOMIAL_s);
  define_unary_function_ptr5( at_BINOMIAL ,alias_at_BINOMIAL,&__BINOMIAL,0,true);

  gen _multinomial(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3){
      gen n=v[0],K=v[1],P=v[2];
      if (!is_zero(1-_plus(P,contextptr),contextptr))
	swapgen(K,P);
      if (_plus(K,contextptr)!=n || K.type!=_VECT || P.type!=_VECT || K._VECTptr->size()!=P._VECTptr->size())
	return gensizeerr(contextptr);
      vecteur k=*K._VECTptr,p=*P._VECTptr;
      unsigned s=unsigned(k.size());
      gen res=_factorial(n,contextptr);
      for (unsigned i=0;i<s;++i){
	res=res/_factorial(k[i],contextptr);
      }
      for (unsigned i=0;i<s;++i){
	res=res*pow(p[i],k[i],contextptr);
      }
      return res;
    }
    return gensizeerr(contextptr);
  }
  static const char _multinomial_s []="multinomial";
  static define_unary_function_eval (__multinomial,&_multinomial,_multinomial_s);
  define_unary_function_ptr5( at_multinomial ,alias_at_multinomial,&__multinomial,0,true);

  gen _randmultinomial(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->empty())
      return gensizeerr(contextptr);
    gen g1(args._VECTptr->front());
    if (args._VECTptr->size()==2 && g1.type==_VECT){
      gen g2(args._VECTptr->back());
      if (g2.type!=_VECT || g2._VECTptr->size()!=g1._VECTptr->size() || !is_zero(1-_sum(g1,contextptr)) )
	return gensizeerr(contextptr);
      double u=giac_rand(contextptr)/(rand_max2+1.0);
      gen somme=0;
      for (unsigned i=0;i<g1._VECTptr->size();++i){
	somme += g1[i];
	if (is_greater(somme,u,contextptr))
	  return g2[i];
      }
      return undef;
    }
    if (!is_zero(1-_sum(args,contextptr)))
      return gensizeerr(contextptr);
    double u=giac_rand(contextptr)/(rand_max2+1.0);
    gen somme=0;
    for (unsigned i=0;i<args._VECTptr->size();++i){
      somme += (*args._VECTptr)[i];
      if (is_greater(somme,u,contextptr))
	return int(i);
    }
    return undef;
  }
  static const char _randmultinomial_s []="randmultinomial";
  static define_unary_function_eval (__randmultinomial,&_randmultinomial,_randmultinomial_s);
  define_unary_function_ptr5( at_randmultinomial ,alias_at_randmultinomial,&__randmultinomial,0,true);

  gen _negbinomial(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2) return symbolic(at_negbinomial,g); // for random
    if (s==3){
      gen r=v[0],p=v[1],k=v[2];
      gen tmp=evalf_double(k,1,contextptr);
      if (tmp.type==_DOUBLE_ && tmp._DOUBLE_val<1 && tmp._DOUBLE_val>0)
	swapgen(k,p);
      if (is_integral(p))
	swapgen(k,p);
      if (is_zero(k))
	return pow(p,r,contextptr);
      return binomial(r+k-1,r,p,contextptr)*(1-p)*r/k;
    }
    return gensizeerr(contextptr);
  }
  static const char _negbinomial_s []="negbinomial";
  static define_unary_function_eval (__negbinomial,&_negbinomial,_negbinomial_s);
  define_unary_function_ptr5( at_negbinomial ,alias_at_negbinomial,&__negbinomial,0,true);

  gen _negbinomial_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3){
      gen n=v[0],p=v[1],k=v[2];
      return _Beta(makesequence(n,k+1,p,1),contextptr);
    }
    if (s==4){
      gen n=v[0],p=v[1],k1=v[2],k2=v[3];
      return _Beta(makesequence(n,k2+1,p,1),contextptr)-_Beta(makesequence(n,k1+1,p,1),contextptr);
    }
    return gensizeerr(contextptr);
  }
  static const char _negbinomial_cdf_s []="negbinomial_cdf";
  static define_unary_function_eval (__negbinomial_cdf,&_negbinomial_cdf,_negbinomial_cdf_s);
  define_unary_function_ptr5( at_negbinomial_cdf ,alias_at_negbinomial_cdf,&__negbinomial_cdf,0,true);

  gen _negbinomial_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3){
      gen R=v[0],P=evalf_double(v[1],1,contextptr),T=v[2];
      if (!is_integral(R) || R.val<=0 || P._DOUBLE_val<=0 || P._DOUBLE_val>=1)
	return gensizeerr(contextptr);
      int r=R.val;
      long_double p=P._DOUBLE_val,t=T._DOUBLE_val;
      if (t<=0)
	return 0;
      if (t>=1)
	return 1;
      long_double cumul=std::pow(p,r),current=cumul;
      if (cumul==0){
	*logptr(contextptr) << gettext("Underflow") <<endl;
	return undef;
      }
      // negbinomial(r,p,k+1)/negbinomial(r,p,k)))=(1-p)*(k+r)/(k+1)
      for (int k=0;;){
	if (cumul>=t)
	  return k;
	current=current*(k+r)*(1-p)/(k+1);
	if (cumul==cumul+current)
	  return k;
	++k;
	cumul += current;
      }
    }
    return gensizeerr(contextptr);
  }
  static const char _negbinomial_icdf_s []="negbinomial_icdf";
  static define_unary_function_eval (__negbinomial_icdf,&_negbinomial_icdf,_negbinomial_icdf_s);
  define_unary_function_ptr5( at_negbinomial_icdf ,alias_at_negbinomial_icdf,&__negbinomial_icdf,0,true);

  gen binomial_cdf(const gen & n,const gen &p,const gen & x0,const gen & x,GIAC_CONTEXT){
    gen fx=_floor(x,contextptr),fx0=_ceil(x0,contextptr);
    if (fx.type==_FLOAT_)
      fx=get_int(fx._FLOAT_val);
    if (fx0.type==_FLOAT_)
      fx0=get_int(fx0._FLOAT_val);
    if (fx.type!=_INT_ || fx.val<0 || fx0.type!=_INT_ || fx0.val<0)
      return gensizeerr(contextptr);
    if (fx0.val>fx.val)
      return 0;
    gen pd=p;
    if (pd.type==_FLOAT_) pd=evalf_double(p,1,contextptr);
    if (n.type==_INT_ && pd.type==_DOUBLE_ && (fx.val-fx0.val)>100){
      // improve if n large using Abramowitz-Stegun p. 944-945
      // [Not very useful, perhaps for much larger values?]
      // sum(binomial(n,p,k),k=a..n)=Ip(a,n-a+1)
      // hence binomial_cdf(n,p,fx0,fx)=Ip(fx0,n-fx0+1)-Ip(fx+1,n-(fx+1)+1)
      // If more than 100 terms to compute use
      // I_p(a,b)=p^a*(1-p)^b/a/B(a,b)*[1+sum(B(a+1,n+1)/B(a+b,n+1)*p^(n+1))]
      // B(a,b)=Gamma(a)*Gamma(b)/Gamma(a+b)
      double p=pd._DOUBLE_val;
      gen res;
      if (fx0.val<=0){
	if (fx.val>=n.val)
	  return 1;
	res=1-incomplete_beta(fx.val+1,n.val-fx.val,p);
      }
      else {
	if (fx.val>=n.val)
	  res=incomplete_beta(fx0.val,n.val-fx0.val+1,p);
	else
	  res=incomplete_beta(fx0.val,n.val-fx0.val+1,p)-incomplete_beta(fx.val+1,n.val-fx.val,p);
      }
      if (!is_undef(res))
	return res;
      // Other formula
      // n=a+b-1
      // I_{1-p}(b,a)=sum(i=0,a-1,comb(a+b-1,i)*p^i*(1-p)^(a+b-1-i)=binomial_cdf(a+b-1,p,0,a-1)
      // I_p(a,b)=1-I_{1-p}(b,a)
      // I_{1-p}(b,a)= Gamma(b,y)/Gamma(b)-1/24/N^2*y^b*exp(-y)/(b-2)!*(b+1+y) +
      //   +1/5760/N^4*y^b*exp(-y)/(b-2)!*[(b-3)*(b-2)*(5*b+7)*(b+1+y)-(5b-7)*(b+3+y)*y^2]
      // N=a+b/2-1/2, y=-N*ln(p), a>>b
      // Gamma(b,y) = y^(b-1)*exp(-y)*(1+(b-1)/y+(b-1)*(b-2)/y^2+...)
    }
    gen last=binomial(n,fx0.val,pd,contextptr);
    if (last.type==_FLOAT_)
      last=evalf_double(last,1,contextptr);
    gen res=last;
    if (n.type==_INT_ && pd.type==_DOUBLE_ && last.type==_DOUBLE_ && last._DOUBLE_val!=0){
      double tmp=pd._DOUBLE_val/(1-pd._DOUBLE_val);
      double lastd=last._DOUBLE_val,resd=res._DOUBLE_val;
      for (int i=fx0.val+1;i<=fx.val;++i){
	if (i%25 == 0)
	  lastd=evalf_double(binomial(n,i,pd,contextptr),1,contextptr)._DOUBLE_val; // avoid loss of precision
	else
	  lastd *= (n.val-i+1)*tmp/i;
	resd += lastd;
      }
      return resd;
    }
    if (n.type==_INT_ && pd.type==_FRAC && last.type==_FRAC){
      gen tmp=pd/(1-pd);
      for (int i=fx0.val+1;i<=fx.val;++i){
	last = last*gen((n.val-i+1)*tmp/i);
	res += last;
      }
    }
    else {
      for (int i=fx0.val+1;i<=fx.val;++i){
	res += binomial(n,i,pd,contextptr);
      }
    }
    return res;
  }
  gen _binomial_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3){
      if (v[2].type==_IDNT)
	return symbolic(at_binomial_cdf,makesequence(v[0],v[1],v[2]));
      return binomial_cdf(v[0],v[1],0,v[2],contextptr);
    }
    if (s==4)
      return binomial_cdf(v[0],v[1],v[2],v[3],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _binomial_cdf_s []="binomial_cdf";
  static define_unary_function_eval (__binomial_cdf,&_binomial_cdf,_binomial_cdf_s);
  define_unary_function_ptr5( at_binomial_cdf ,alias_at_binomial_cdf,&__binomial_cdf,0,true);

  gen binomial_icdf(const gen & n0,const gen &p0,const gen & x_orig,GIAC_CONTEXT){
    gen x=evalf_double(x_orig,1,contextptr);
    gen p=evalf_double(p0,1,contextptr);
    gen n=_floor(n0,contextptr);
    if (n.type==_FLOAT_)
      n=get_int(n._FLOAT_val);
    if (!is_zero(n-n0))
      return gensizeerr(contextptr);
    if (x._DOUBLE_val==0)
      return zero;
    if (x._DOUBLE_val==1)
      return n;
    if (is_strictly_greater(p,1,contextptr) || is_strictly_greater(0,p,contextptr))
      return gensizeerr(contextptr);
    if (n.type!=_INT_ || p.type!=_DOUBLE_ || x.type!=_DOUBLE_ || x._DOUBLE_val<0 || x._DOUBLE_val>1 )
      return symbolic(at_binomial_icdf,makesequence(n,p,x));
    int N=n.val;
    long_double P=p._DOUBLE_val;
    if (N*P>=30 && N*(1-P)>=30){
      // use approximation by normal law as a starting point
      gen g=_floor(_normal_icdf(makesequence(n*p,sqrt(n*p*(1-p),contextptr),x),contextptr),contextptr);
      int G=g.val;
      gen cdf=evalf_double(_binomial_cdf(makesequence(n,p,g),contextptr),1,contextptr);
      long_double CDF=cdf._DOUBLE_val;
      long_double T=x._DOUBLE_val;
      long_double current=std::exp(lngamma(N+1)-lngamma(G+1)-lngamma(N-G+1)+G*std::log(P)+(N-G)*std::log(1-P));
      long_double P1=P/(1-P);
      // increase G until CDF>=T
      for (;T>CDF;){
	++G;
	current *= (N-G+1)*P1/G;
	CDF += current;
      }
      if (G!=g.val)
	return G;
      // decrease G until CDF<T
      for (;T<=CDF;){
	CDF -= current;
	current /= (N-G+1)*P1/G;
	--G;
      }
      return G+1;
    }
    gen last=pow(1-p0,n,contextptr);
    gen b=last;
    int k=1;
    if (last.type==_FLOAT_)
      last=evalf_double(last,1,contextptr);
    if (last.type==_DOUBLE_){
      double tmp=(p/(1-p))._DOUBLE_val,lastd=last._DOUBLE_val,bd=lastd;
      for (;k<=n.val;++k){
	if (x._DOUBLE_val<=bd)
	  return k-1;
	if (k%25 == 0)
	  lastd=evalf_double(binomial(n,k,p,contextptr),1,contextptr)._DOUBLE_val; // avoid loss of precision
	else
	  lastd *= (n.val-k+1)*tmp/k;
	bd += lastd;
      }
      return n;
    }
#if 1
    gen tmp=p0/(1-p0);
    for (;k<=n.val;++k){
      if (!ck_is_strictly_greater(x_orig,b,contextptr))
	return k-1;
      last = last * ((n.val-k+1)*tmp)/k;
      b += last;
    }
#else
    for (;k<=n.val;++k){
      if (!ck_is_strictly_greater(x,b,contextptr))
	return k-1;
      b=b+binomial(n,k,p,contextptr);
    }
#endif
    return n;
  }
  gen _binomial_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3)
      return binomial_icdf(v[0],v[1],v[2],contextptr);
    if (s==4)
      return binomial_icdf(v[0],v[1],v[3],contextptr)-binomial_icdf(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _binomial_icdf_s []="binomial_icdf";
  static define_unary_function_eval (__binomial_icdf,&_binomial_icdf,_binomial_icdf_s);
  define_unary_function_ptr5( at_binomial_icdf ,alias_at_binomial_icdf,&__binomial_icdf,0,true);

  gen randbinomial(int n,double P,GIAC_CONTEXT){
    if (P<=0)
      return 0;
    if (P>=1)
      return n;
    if (n>1000)
      return binomial_icdf(n,P,double(giac_rand(contextptr))/rand_max2,contextptr);
    int ok=0;
    P*=rand_max2;
    for (int i=0;i<n;++i){
      if (giac_rand(contextptr)<=P)
	ok++;
    }
    return ok;
  }
  // randbinomial(n,p) returns k in [0..n] with proba binomial(n,k,p)
  gen _randbinomial(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s<2 
	|| s>2 // 3
	)
      return gensizeerr(contextptr);
    gen n=v[0];
    gen p=v[1];
    int k=1;
    if (s==3){
      gen K=v[2];
      if (!is_integral(K) || K.type!=_INT_)
	return gensizeerr(contextptr);
      k=K.val;
    }
    if (!is_integral(n) || n.type!=_INT_ || n.val<=0 || ck_is_strictly_greater(0,p,contextptr) || ck_is_strictly_greater(p,1,contextptr))
      return gensizeerr(contextptr);
    p=evalf_double(p,1,contextptr);
    return randbinomial(n.val,p._DOUBLE_val,contextptr);
  }
  static const char _randbinomial_s []="randbinomial";
  static define_unary_function_eval (__randbinomial,&_randbinomial,_randbinomial_s);
  define_unary_function_ptr5( at_randbinomial ,alias_at_randbinomial,&__randbinomial,0,true);

  gen poisson(const gen & m,const gen & k,GIAC_CONTEXT){
    if (k.type==_VECT)
      return apply2nd(m,k,contextptr,poisson);
    gen M=evalf_double(m,1,contextptr);
    if (M.type==_DOUBLE_){
      gen K=evalf_double(k,1,contextptr);
      if (K.type==_DOUBLE_)
	return std::exp(-M._DOUBLE_val + K._DOUBLE_val*std::log(M._DOUBLE_val)-lngamma(K._DOUBLE_val+1));
    }
    return exp(-m,contextptr)*pow(m,k,contextptr)/_factorial(k,contextptr);
  }
  gen _poisson(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return symbolic(at_poisson,g);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return poisson(v[0],v[1],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _poisson_s []="poisson";
  static define_unary_function_eval (__poisson,&_poisson,_poisson_s);
  define_unary_function_ptr5( at_poisson ,alias_at_poisson,&__poisson,0,true);

  static const char _POISSON_s []="POISSON";
  static define_unary_function_eval (__POISSON,&_poisson,_POISSON_s);
  define_unary_function_ptr5( at_POISSON ,alias_at_POISSON,&__POISSON,0,true);

  // exp(-lambda)*sum(lambda^k/k!,k=0..x)
  // or 1-exp(-lambda)*sum(lambda^k/k!,k=x+1..inf)
  double poisson_cdf(double lambda,double x){
    long_double N=lambda;
    long_double res=0,prod=1;
    int fx=int(std::floor(x));
    if (fx>=lambda){
      for (int i=fx+1;prod>1e-17;){
	res += prod;
	prod *= N;
	++i;
	prod /= long_double(i);
      }
      res *= std::exp(-N+(fx+1)*std::log(N)-lngamma(fx+2.));
      return 1-res; 
    }
#if 1
    for (int i=fx;i>=0 && prod>1e-17;--i){
      res += prod;
      prod /= N;
      prod *= long_double(i);
    }
    res *= std::exp(-N+fx*std::log(N)-lngamma(fx+1.));    
    return res;
#else
    for (int i=0;i<=fx;){
      res += prod;
      prod *= N;
      ++i;
      prod /= long_double(i);
    }
    res *= std::exp(-N);
    return res;
#endif
  }
  gen poisson_cdf(const gen & lambda_,const gen & x,GIAC_CONTEXT){
    gen fx=_floor(x,contextptr);
    gen lambda=evalf_double(lambda_,1,contextptr);
    if (fx.type==_INT_ && fx.val>=0 && lambda.type==_DOUBLE_)
      return poisson_cdf(lambda._DOUBLE_val,fx.val);
    if (is_zero(fx-x))
      return _upper_incomplete_gamma(makesequence(x+1,lambda,1),contextptr);
    else
      return _upper_incomplete_gamma(makesequence(evalf(fx,1,contextptr),lambda,1),contextptr);
#if 0
    gen res=0;
    for (int i=0;i<=fx.val;++i){
      res +=poisson(lambda,i,contextptr);
    }
    return res;
#endif
    //identificateur k(" k");
    //return sum(poisson(n,k,contextptr),k,0,_floor(x,contextptr),contextptr);
  }
  gen _poisson_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return poisson_cdf(v[0],v[1],contextptr);
    if (s==3)
      return poisson_cdf(v[0],v[2],contextptr)-poisson_cdf(v[0],v[1]-1,contextptr);
    return gensizeerr(contextptr);
  }
  static const char _poisson_cdf_s []="poisson_cdf";
  static define_unary_function_eval (__poisson_cdf,&_poisson_cdf,_poisson_cdf_s);
  define_unary_function_ptr5( at_poisson_cdf ,alias_at_poisson_cdf,&__poisson_cdf,0,true);

  // randpoisson(lambda) returns k>0 with proba poisson(lambda,k)
  gen randpoisson(double lambda,GIAC_CONTEXT){
    if (lambda>700)
      return poisson_icdf(lambda,double(giac_rand(contextptr))/rand_max2,contextptr);
    int k=0;
    if (lambda<200){
      double seuil=std::exp(-lambda);
      double res=1.0;
      for (;;++k){
	res *= giac_rand(contextptr)/(rand_max2+1.0);
	if (res<seuil)
	  return k;
      }
    }
    double res=0.0;
    for (;;++k){
      double u = giac_rand(contextptr)/(rand_max2+1.0);
      res += -std::log(1-u)/lambda;
      if (res>=1.0)
	return k;
    }
  }
  gen _randpoisson(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen G=evalf_double(g,1,contextptr);
    if (G.type!=_DOUBLE_)
      return gensizeerr(contextptr);
    double lambda=G._DOUBLE_val;
    if (lambda<=0)
      return gensizeerr(contextptr);
    return randpoisson(lambda,contextptr);
  }
  static const char _randpoisson_s []="randpoisson";
  static define_unary_function_eval (__randpoisson,&_randpoisson,_randpoisson_s);
  define_unary_function_ptr5( at_randpoisson ,alias_at_randpoisson,&__randpoisson,0,true);

  gen poisson_icdf(double m,double t,GIAC_CONTEXT){
    if (t==0)
      return zero;
    if (t==1)
      return plus_inf;
#if 1
    if (m>90){ 
      // 170.! =7e306 we must insure that the naive definition does not return >170
      // hence the test since poisson_cdf(90.,170.)=1.0 to double precision
      // approximation using normal_icdf
      gen g=_ceil(_normal_icdf(makesequence(m,sqrt(m,contextptr),t),contextptr),contextptr);
      if (is_undef(g))
	return gensizeerr("Underflow");
      int G=g.val;
      // check that poisson_cdf(m,g)>=t, if not increase g
      gen pg=evalf_double(_poisson_cdf(makesequence(m,g),contextptr),1,contextptr);
      long_double CDF=pg._DOUBLE_val;
      long_double M=m;
      long_double current= std::exp(-M+G*std::log(M)-lngamma(G+1));
      long_double T=t;
      for (;T>CDF;){
	++G;
	current *= M/G;
	CDF += current; // std::exp(-m._DOUBLE_val+G*std::log(m._DOUBLE_val)-lngamma(G+1));
      }
      if (G!=g.val)
	return G;
      // decrease G until cdf<t
      for (;T<=CDF;){
	CDF -= current; // pg -= std::exp(-m._DOUBLE_val+G*std::log(m._DOUBLE_val)-lngamma(G+1));
	current *= G/M;
	--G;
      }
      return G+1;
    }
    long_double M=m;
    int k=0;
    long_double T=t*std::exp(M),B=0,prod=1;
    for (;;){
      B += prod;
      if (B>=T)
	return k;
      ++k;
      prod *= M;
      prod /= k;
    }
#else
    if (m>300)
      return gensizeerr(gettext("Overflow"));
    gen b;
    for (;;++k){
      b=b+poisson(m,k,contextptr);
      if (!ck_is_strictly_greater(t,b,contextptr))
	return k;
    }
    return t;
#endif
  }
  gen poisson_icdf(const gen & m_orig,const gen & t_orig,GIAC_CONTEXT){
    gen t=evalf_double(t_orig,1,contextptr);
    gen m=evalf_double(m_orig,1,contextptr);
    if (t.type!=_DOUBLE_ || t._DOUBLE_val<0 || t._DOUBLE_val>1)
      return gensizeerr(contextptr);
    if (m.type!=_DOUBLE_ )
      return symbolic(at_poisson_icdf,makesequence(m,t));
    return poisson_icdf(m._DOUBLE_val,t._DOUBLE_val,contextptr);
  }

  gen _poisson_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return poisson_icdf(v[0],v[1],contextptr);
    if (s==3)
      return poisson_icdf(v[0],v[2],contextptr)-poisson_icdf(v[0],v[1],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _poisson_icdf_s []="poisson_icdf";
  static define_unary_function_eval (__poisson_icdf,&_poisson_icdf,_poisson_icdf_s);
  define_unary_function_ptr5( at_poisson_icdf ,alias_at_poisson_icdf,&__poisson_icdf,0,true);

  gen student(const gen & n0,const gen & x,GIAC_CONTEXT){
    if (x.type==_VECT)
      return apply2nd(n0,x,contextptr,student);
    gen n(n0);
    if (!is_integral(n) || n.val<1)
      return gensizeerr(contextptr);
    return Gamma(rdiv(n+1,2,contextptr),contextptr)/Gamma(rdiv(n,2,contextptr),contextptr)/sqrt(n*cst_pi,contextptr)*pow((1+pow(x,2)/n),-rdiv(n+1,2,contextptr),contextptr);
  }
  gen _student(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT){
      if (abs_calc_mode(contextptr)==38)
	return symbolic(at_student,g);
      return symbolic(at_studentd,g);
    }
    vecteur v=*g._VECTptr;
    int s=int(v.size());
    if (s==2){
      if (v[1].type==_DOUBLE_ || v[1].type==_FLOAT_)
	return evalf(student(v[0],v[1],contextptr),1,contextptr);
      return student(v[0],v[1],contextptr);
    }
    return gensizeerr(contextptr);
  }
  static const char _student_s []="student";
  static define_unary_function_eval (__student,&_student,_student_s);
  define_unary_function_ptr5( at_student ,alias_at_student,&__student,0,true);
  static const char _studentd_s []="studentd";
  static define_unary_function_eval (__studentd,&_student,_studentd_s);
  define_unary_function_ptr5( at_studentd ,alias_at_studentd,&__studentd,0,true);

  /* statically handled by derive.cc
  static gen d2_UTPT(const gen & e,GIAC_CONTEXT ){
    return -_student(e,contextptr);
  }
  static const partial_derivative_onearg D_at_UTPT(d2_UTPT);
  static define_unary_function_eval (d2_UTPT_eval,&d2_UTPT,"");
  define_unary_function_ptr( D2_UTPT,alias_D2_UTPT,&d2_UTPT_eval);
  static unary_function_ptr d_UTPT(int i){
    if (i==1)
      return at_zero;
    if (i==2)
      return D2_UTPT;
    return gensizeerr(contextptr);
    return 0;
  }
  static const partial_derivative_multiargs D_UTPT(&d_UTPT);
  */
  static double FTS(int ndf,double cs2,double term, int j,double sum){
    for(;;){
      term=term*cs2*(ndf+j)/(j+2);
      double oldsum=sum;
      sum=sum+term;
      if (oldsum==sum)
	return sum;
      j +=2;
    }
  }
  static double FCS(double fac,double term,int j,double res){
    for (;;){
      j -= 2;
      if (j<=1)
	return res;
      res = ((j-1)*fac*res)/j + term;
    }
  }

  static double FSS(double ofs,double term,double fac,int j,double sum){
    for (;;){
      j -= 2;
      if (j<=1)
	return sum;
      sum=(ofs+j-2)/j*fac*sum+term;
    }
  }
  static double FSS2(double ofs,double term,double fac,int j,double sum){
    for (;;){
      j -= 2;
      if (j<=0)
	return sum;
      sum=(ofs+j)/j*fac*sum+term;
    }
  }
  static double TTS(int dof,int j,double term,double res,double cs2){
    for (;;){
      j +=2;
      term= (term*cs2*(j-1))/j;
      double ores= res;
      res= res + term/(dof+j);
      if (ores==res) return res;
    }
  }
  gen UTPT(const gen & n_orig,const gen & x0,GIAC_CONTEXT){
    gen n=n_orig;
    if (!is_integral(n))
      return gensizeerr(contextptr);
    if (x0==plus_inf)
      return 0;
    if (x0==minus_inf)
      return 1;
    gen x1=evalf_double(x0,1,contextptr);
    if (n.type!=_INT_ || x1.type!=_DOUBLE_)
      return symbolic(at_UTPT,makesequence(n,x0));
    int dof=n.val;
    if (dof<=0)
      return gendimerr(contextptr);
    double x=x1._DOUBLE_val,x2=x*x,y2= x2/dof;
    if (0 && dof>=100){
      double y=std::log(y2)+1, a=dof-0.5, b=48*a*a;
      y=a*y;
      double res = (((((-.4*y - 3.3)*y -24)*y - 85.5)/(.8*y*y + 100 + b)+ y + 3)/b + 1)*std::sqrt(y);
      if (x<0)
	res=-res;
      return _UTPN(res,contextptr);
    }
    double y=std::sqrt(y2),b= 1+y2,cs2=1/b;
    if (x2<25){
      double res;
      if (dof==1)
	res=0;
      else
	res=FCS(cs2,y,dof,y);
      if (dof %2)
	res=2/M_PI*(std::atan(y)+cs2*res);
      else
	res=res*std::sqrt(cs2);
      if (x>0)
	return (1-res)/2;
      else
	return (1+res)/2;
    }
    else {
      double res= TTS(dof,0,dof,1,cs2);
      res= FCS(cs2,0,dof+2,res);
      if (dof %2)
	res= 2/M_PI*std::sqrt(cs2)*res;
      res /=2;
      if (x<0)
	res=1-res;
      return res;
    }
  }
  gen _UTPT(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    int s=int(v.size());
    if (s!=2)
      return gensizeerr(contextptr);
    return UTPT(v[0],v[1],contextptr);
  }
  static const char _UTPT_s []="UTPT";
  static define_unary_function_eval (__UTPT,&_UTPT,_UTPT_s);
  define_unary_function_ptr5( at_UTPT ,alias_at_UTPT,&__UTPT,0,true);

  // 26.7.5 in Abramowitz & Stegun
  static double utpt_initial_guess(int n,double y,GIAC_CONTEXT){
    // double xp=utpn_initial_guess(y);
    double xp=utpn_inverse(y,contextptr)._DOUBLE_val;
    double xp2=xp*xp;
    double g1xp=xp*(xp2+1)/4;
    double g2xp=((5*xp2+16)*xp2+3)*xp/96;
    xp=xp+g1xp/n+g2xp/(n*n);
    return xp;
  }

  // dof=degree of freedom
  gen student_cdf(const gen & dof0,const gen & x1,const gen & x2,GIAC_CONTEXT){
    gen X2=evalf_double(x2,1,contextptr);
    gen X1=evalf_double(x1,1,contextptr);
    gen dof(dof0);
    if (!is_integral(dof) || dof.val<1 || X1.type!=_DOUBLE_ || X2.type!=_DOUBLE_){
      if (!is_inf(X1) && !is_inf(X2))
	return symbolic(at_student_cdf,gen(makevecteur(dof0,x1,x2),_SEQ__VECT));
    }
    return UTPT(dof,X1,contextptr)-UTPT(dof,X2,contextptr);
  }
  gen _student_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return student_cdf(v[0],minus_inf,v[1],contextptr);
    if (s==3)
      return student_cdf(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _student_cdf_s []="student_cdf";
  static define_unary_function_eval (__student_cdf,&_student_cdf,_student_cdf_s);
  define_unary_function_ptr5( at_student_cdf ,alias_at_student_cdf,&__student_cdf,0,true);

  static const char _studentd_cdf_s []="studentd_cdf";
  static define_unary_function_eval (__studentd_cdf,&_student_cdf,_studentd_cdf_s);
  define_unary_function_ptr5( at_studentd_cdf ,alias_at_studentd_cdf,&__studentd_cdf,0,true);

  gen student_icdf(const gen & m0,const gen & t_orig,GIAC_CONTEXT){
    gen t=evalf_double(t_orig,1,contextptr);
    gen m(m0);
    if (!is_integral(m) || m.val<1 || t.type!=_DOUBLE_ || t._DOUBLE_val<0 || t._DOUBLE_val>1)
      return symbolic(at_student_icdf,makesequence(m,t));
    if (t._DOUBLE_val==0)
      return zero;
    if (t._DOUBLE_val==1)
      return plus_inf;
    double y=t._DOUBLE_val;
    double x0=utpt_initial_guess(m.val,1-y,contextptr);
    // return x0;
    // FIXME: use an iterative method to improve the initial guess
    identificateur x(" x");
    gen res=newton(_student_cdf(makesequence(m,x),contextptr)-y,x,x0,NEWTON_DEFAULT_ITERATION,1e-5,1e-12,true,1,0,1,0,.5,contextptr);
    if (!is_undef(res))
      return res;
    // for example student_icdf(100,0.95)
    *logptr(contextptr) << "Low accuracy" << endl;
    return x0;
  }
  gen _student_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return student_icdf(v[0],v[1],contextptr);
    if (s==3)
      return student_icdf(v[0],v[2],contextptr)-student_icdf(v[0],v[1],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _student_icdf_s []="student_icdf";
  static define_unary_function_eval (__student_icdf,&_student_icdf,_student_icdf_s);
  define_unary_function_ptr5( at_student_icdf ,alias_at_student_icdf,&__student_icdf,0,true);

  static const char _studentd_icdf_s []="studentd_icdf";
  static define_unary_function_eval (__studentd_icdf,&_student_icdf,_studentd_icdf_s);
  define_unary_function_ptr5( at_studentd_icdf ,alias_at_studentd_icdf,&__studentd_icdf,0,true);

  gen chisquare(const gen & n,const gen & x,GIAC_CONTEXT){
    if (x.type==_VECT)
      return apply2nd(n,x,contextptr,chisquare);
    gen n2=n/2;
    return rdiv(pow(x,n2-1,contextptr)*exp(-x/2,contextptr),Gamma(n2,contextptr)*pow(2,n2,contextptr),contextptr);
  }
  gen _chisquare(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT){
      if (abs_calc_mode(contextptr)==38)
	return symbolic(at_chisquare,g);
      return symbolic(at_chisquared,g);
    }
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return chisquare(v[0],v[1],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _chisquare_s []="chisquare";
  static define_unary_function_eval (__chisquare,&_chisquare,_chisquare_s);
  define_unary_function_ptr5( at_chisquare ,alias_at_chisquare,&__chisquare,0,true);

  static const char _chisquared_s []="chisquared";
  static define_unary_function_eval (__chisquared,&_chisquare,_chisquared_s);
  define_unary_function_ptr5( at_chisquared ,alias_at_chisquared,&__chisquared,0,true);

  /* statically handled by derive.cc
  static gen d2_UTPC(const gen & e,GIAC_CONTEXT){
    return -_chisquare(e,contextptr);
  }
  static const partial_derivative_onearg D_at_UTPC(d2_UTPC);
  static define_unary_function_eval (d2_UTPC_eval,&d2_UTPC,"");
  define_unary_function_ptr( D2_UTPC,alias_D2_UTPC,&d2_UTPC_eval);
  static unary_function_ptr d_UTPC(int i){
    if (i==1)
      return at_zero;
    if (i==2)
      return D2_UTPC;
    return gensizeerr(contextptr);
  }
  static const partial_derivative_multiargs D_UTPC(&d_UTPC);
  */
  gen UTPC(const gen & n_orig,const gen & x0,GIAC_CONTEXT){
    gen dof=n_orig;
    if (x0==plus_inf)
      return 0;
    if (is_zero(x0))
      return 1;
    gen x1=evalf_double(x0,1,contextptr);
    if (!is_integral(dof) || x1.type!=_DOUBLE_)
      return symbolic(at_UTPC,gen(makevecteur(dof,x0),_SEQ__VECT)); // gensizeerr(contextptr);
    int n=dof.val;
    double x=x1._DOUBLE_val;
    if (x<0)
      return 1;
    if (x>10000)
      return 0.0;
    if (n<1)
      return gensizeerr(contextptr);
    if (n==1)
      return 2*_UTPN(sqrt(x,contextptr),contextptr);
    if (n>100){
    }
    double res=1;
    if (x>2){
      int r=n%2+2;
      res=std::exp(-x/2);
      double term = res;
      for (;r<n;r += 2){
	term = term*x/r;
	res += term;
      }
    }
    else {
      int r=n-2;
      for (;r>1;r-=2){
	res = res*x/r+1;
      }
      res *= std::exp(-x/2);
    }
    if (n%2)
      return std::sqrt(2*x/M_PI)*res+2*_UTPN(sqrt(x,contextptr),contextptr);
    else
      return res;
  }
  gen _UTPC(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    int s=int(v.size());
    if (s!=2)
      return gensizeerr(contextptr);
    return UTPC(v[0],v[1],contextptr);
  }
  static const char _UTPC_s []="UTPC";
  static define_unary_function_eval (__UTPC,&_UTPC,_UTPC_s);
  define_unary_function_ptr5( at_UTPC ,alias_at_UTPC,&__UTPC,0,true);

  gen chisquare_cdf(const gen & dof,const gen & x1,const gen & x2,GIAC_CONTEXT){
    return UTPC(dof,x1,contextptr)-UTPC(dof,x2,contextptr);
  }
  gen _chisquare_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return chisquare_cdf(v[0],0,v[1],contextptr);
    if (s==3)
      return chisquare_cdf(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _chisquare_cdf_s []="chisquare_cdf";
  static define_unary_function_eval (__chisquare_cdf,&_chisquare_cdf,_chisquare_cdf_s);
  define_unary_function_ptr5( at_chisquare_cdf ,alias_at_chisquare_cdf,&__chisquare_cdf,0,true);

  static const char _chisquared_cdf_s []="chisquared_cdf";
  static define_unary_function_eval (__chisquared_cdf,&_chisquare_cdf,_chisquared_cdf_s);
  define_unary_function_ptr5( at_chisquared_cdf ,alias_at_chisquared_cdf,&__chisquared_cdf,0,true);

  // Abramowitz & Stegun 26.4.17
  static double utpc_initial_guess(int n,double y,GIAC_CONTEXT){
    // double xp=utpn_initial_guess(y);
    if (n==2)
      return -2*std::log(y);
    if (n==1)
      y=y/2;
    double xp=utpn_inverse(y,contextptr)._DOUBLE_val;
    if (n==1)
      return xp*xp;
    double d=2/(9.0*n);
    d=1+xp*std::sqrt(d)-d;
    return n*d*d*d;
  }

  gen chisquare_icdf(const gen & m0,const gen & t_orig,GIAC_CONTEXT){
    gen t=evalf_double(t_orig,1,contextptr);
    gen m(m0);
    if (!is_integral(m) || t.type!=_DOUBLE_ || t._DOUBLE_val<0 || t._DOUBLE_val>1)
      return gensizeerr(contextptr);
    if (t._DOUBLE_val==0)
      return zero;
    if (t._DOUBLE_val==1)
      return plus_inf;
    // return utpc_initial_guess(m.val,1-t._DOUBLE_val);
    double x0=utpc_initial_guess(m.val,1-t._DOUBLE_val,contextptr);
    // FIXME
    identificateur x(" z");
    return newton(1-UTPC(m,x,contextptr)-t,x,x0,NEWTON_DEFAULT_ITERATION,1e-5,1e-12,true,1,0,1,0,.5,contextptr);   
  }
  gen _chisquare_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return chisquare_icdf(v[0],v[1],contextptr);
    if (s==3)
      return chisquare_icdf(v[0],v[2],contextptr)-chisquare_icdf(v[0],v[1],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _chisquare_icdf_s []="chisquare_icdf";
  static define_unary_function_eval (__chisquare_icdf,&_chisquare_icdf,_chisquare_icdf_s);
  define_unary_function_ptr5( at_chisquare_icdf ,alias_at_chisquare_icdf,&__chisquare_icdf,0,true);

  static const char _chisquared_icdf_s []="chisquared_icdf";
  static define_unary_function_eval (__chisquared_icdf,&_chisquare_icdf,_chisquared_icdf_s);
  define_unary_function_ptr5( at_chisquared_icdf ,alias_at_chisquared_icdf,&__chisquared_icdf,0,true);

  gen snedecor(const gen & a,const gen & b,const gen & x,GIAC_CONTEXT){
    if (x.type==_VECT)
      return apply3rd(a,b,x,contextptr,snedecor);
    if (is_positive(-x,contextptr))
      return zero;
    return pow(a/b,a/2,contextptr)/Beta(a/2,b/2,contextptr) * pow(x,a/2-1,contextptr) * pow(1+a/b*x,-(a+b)/2,contextptr);
  }
  gen _snedecor(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2){
      if (abs_calc_mode(contextptr)==38)
	return symbolic(at_fisher,g);
      return symbolic(at_fisherd,g);
    }
    if (s==3)
      return snedecor(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _snedecor_s []="snedecor";
  static define_unary_function_eval (__snedecor,&_snedecor,_snedecor_s);
  define_unary_function_ptr5( at_snedecor ,alias_at_snedecor,&__snedecor,0,true);

  static const char _snedecord_s []="snedecord";
  static define_unary_function_eval (__snedecord,&_snedecor,_snedecord_s);
  define_unary_function_ptr5( at_snedecord ,alias_at_snedecord,&__snedecord,0,true);

  static const char _fisher_s []="fisher";
  static define_unary_function_eval (__fisher,&_snedecor,_fisher_s);
  define_unary_function_ptr5( at_fisher ,alias_at_fisher,&__fisher,0,true);

  static const char _fisherd_s []="fisherd";
  static define_unary_function_eval (__fisherd,&_snedecor,_fisherd_s);
  define_unary_function_ptr5( at_fisherd ,alias_at_fisherd,&__fisherd,0,true);

  /* statically handled in derive.cc
  static gen d2_UTPF(const gen & e,GIAC_CONTEXT){
    return -_snedecor(e,contextptr);
  }
  static const partial_derivative_onearg D_at_UTPF(d2_UTPF);
  static define_unary_function_eval (d2_UTPF_eval,&d2_UTPF,"");
  define_unary_function_ptr( D2_UTPF,alias_D2_UTPF,&d2_UTPF_eval);
  static unary_function_ptr d_UTPF(int i){
    if (i<3)
      return at_zero;
    if (i==3)
      return D2_UTPF;
    return gensizeerr(contextptr);
  }
  static const partial_derivative_multiargs D_UTPF(&d_UTPF);
  */
  gen UTPF(const gen & num,const gen & den,const gen & x0,GIAC_CONTEXT){
    gen gndf=num,gddf=den,gx=evalf_double(x0,1,contextptr);
    if (!is_integral(gndf) || !is_integral(gddf) || gx.type!=_DOUBLE_)
      return symbolic(at_UTPF,gen(makevecteur(num,den,x0),_SEQ__VECT)); // gensizeerr(contextptr);
    if (gx._DOUBLE_val<=0)
      return plus_one;
    int ndf=gndf.val,ddf=gddf.val;
    double x=gx._DOUBLE_val;
    if (ndf<1 || ddf <1 || ndf>300 || ddf>300)
      return gendimerr(contextptr);
    if (ndf==1)
      return 2*UTPT(ddf,std::sqrt(x),contextptr);
    double y= (x*ndf)/ddf, sn2= y/(1+y), cs2= 1/(1+y),sum;
    y=std::sqrt(y);
    if (ndf%2){ 
      if (ddf%2){ // ndf && ddf odd
	if (y<1){
	  if (ddf==1) sum=0; else sum=1;
	  sum=y*cs2*FCS(cs2,1,ddf,sum)+std::atan(y);
	  sum=1-2/M_PI*sum;
	  double sumB=ddf*FSS(ddf,1,sn2,ndf,1);
	  sumB=FCS(cs2,0,ddf+2,sumB);
	  return sum+2/M_PI*cs2*y*sumB;
	}
	else { // y>=1
	  sum= FTS(ndf,cs2,1,ddf,1);
	  sum= FSS2(ddf,0,sn2,ndf,sum);
	  sum= FCS(cs2,0,ddf+2,sum);
	  return 2/M_PI*y*cs2*sum;
	}
      } // end ndf odd , ddf odd
      else { // ndf odd, ddf even
	if (y<1)
	  return 1-FSS(ndf,1,cs2,ddf,1)*std::pow(sn2,ndf/2.0);
	else {
	  sum= FTS(ndf,cs2,1,ddf,1);
	  sum= FSS(ndf,0,cs2,ddf+2,sum);
	  return sum*std::pow(sn2,ndf/2.0);
	}
      }
    } // end ndf odd
    else { // ndf even
      return FSS(ddf,1,sn2,ndf,1)*std::pow(cs2,ddf/2.0);
    }
  }
  gen _UTPF(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    int s=int(v.size());
    if (s!=3)
      return gensizeerr(contextptr);
    return UTPF(v[0],v[1],v[2],contextptr);
  }
  static const char _UTPF_s []="UTPF";
  static define_unary_function_eval (__UTPF,&_UTPF,_UTPF_s);
  define_unary_function_ptr5( at_UTPF ,alias_at_UTPF,&__UTPF,0,true);

  gen snedecor_cdf(const gen & ndof,const gen & ddof,const gen & x,GIAC_CONTEXT){
    gen gndf(ndof),gddf(ddof),gx(x);
    if (!is_integral(gndf) || !is_integral(gddf))
      return gentypeerr(contextptr);
    if (gx.type!=_DOUBLE_){
      if (1) {// (calc_mode(contextptr)==1)
	if (is_inf(x))
	  return symbolic(at_Beta,makesequence(ndof/2,ddof/2,1,1));
	return symbolic(at_Beta,makesequence(ndof/2,ddof/2,ndof*x/(ndof*x+ddof),1));
      }
      else
	return symbolic(at_snedecor_cdf,makesequence(ndof,ddof,x));
    }
    return 1-UTPF(ndof,ddof,x,contextptr);
  }
  gen _snedecor_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3)
      return snedecor_cdf(v[0],v[1],v[2],contextptr);
    if (s==4)
      return snedecor_cdf(v[0],v[1],v[3],contextptr)-snedecor_cdf(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _snedecor_cdf_s []="snedecor_cdf";
  static define_unary_function_eval (__snedecor_cdf,&_snedecor_cdf,_snedecor_cdf_s);
  define_unary_function_ptr5( at_snedecor_cdf ,alias_at_snedecor_cdf,&__snedecor_cdf,0,true);
  static const char _snedecord_cdf_s []="snedecord_cdf";
  static define_unary_function_eval (__snedecord_cdf,&_snedecor_cdf,_snedecord_cdf_s);
  define_unary_function_ptr5( at_snedecord_cdf ,alias_at_snedecord_cdf,&__snedecord_cdf,0,true);
  static const char _fisher_cdf_s []="fisher_cdf";
  static define_unary_function_eval (__fisher_cdf,&_snedecor_cdf,_fisher_cdf_s);
  define_unary_function_ptr5( at_fisher_cdf ,alias_at_fisher_cdf,&__fisher_cdf,0,true);
  static const char _fisherd_cdf_s []="fisherd_cdf";
  static define_unary_function_eval (__fisherd_cdf,&_snedecor_cdf,_fisherd_cdf_s);
  define_unary_function_ptr5( at_fisherd_cdf ,alias_at_fisherd_cdf,&__fisherd_cdf,0,true);

  // Abramowitz & Stegun 26.6.16
  static double utpf_initial_guess(int num,int den,double y,GIAC_CONTEXT){
    if (num==1){
      double xp=utpt_initial_guess(den,y/2,contextptr);
      return xp*xp;
    }
    if (den==1){
      return y-0.5;
    }
    double xp=utpn_inverse(y,contextptr)._DOUBLE_val;
    double lambda=(xp*xp-3)/6;
    double h=2/fabs(1.0/(num-1)+1.0/(den-1)); // harmonic
    double w=xp*std::sqrt(h+lambda)/h-(lambda+5.0/6.0-2/(3*h))*fabs(1.0/(num-1)-1.0/(den-1));
    return std::exp(2*w);
  }

  gen snedecor_icdf(const gen & num0,const gen & den0,const gen & t_orig,GIAC_CONTEXT){
    gen t=evalf_double(t_orig,1,contextptr);
    gen num(num0),den(den0);
    if (!is_integral(num) || !is_integral(den) || num.val<0 || den.val<0 || t.type!=_DOUBLE_ || t._DOUBLE_val<0 || t._DOUBLE_val>1)
      return gensizeerr(contextptr);
    if (t._DOUBLE_val==0)
      return zero;
    if (t._DOUBLE_val==1)
      return plus_inf;
    // return utpf_initial_guess(num.val,den.val,1-t._DOUBLE_val);
    double x0=utpf_initial_guess(num.val,den.val,1-t._DOUBLE_val,contextptr);
    // FIXME
    identificateur x(" z");
    return newton(1-UTPF(num,den,x,contextptr)-t,x,x0,NEWTON_DEFAULT_ITERATION,1e-5,1e-12,true,0,1.79769313486e+308,1,0,.5,contextptr);   
  }
  gen _snedecor_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3)
      return snedecor_icdf(v[0],v[1],v[2],contextptr);
    if (s==4)
      return snedecor_icdf(v[0],v[1],v[3],contextptr)-snedecor_icdf(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _snedecor_icdf_s []="snedecor_icdf";
  static define_unary_function_eval (__snedecor_icdf,&_snedecor_icdf,_snedecor_icdf_s);
  define_unary_function_ptr5( at_snedecor_icdf ,alias_at_snedecor_icdf,&__snedecor_icdf,0,true);
  static const char _snedecord_icdf_s []="snedecord_icdf";
  static define_unary_function_eval (__snedecord_icdf,&_snedecor_icdf,_snedecord_icdf_s);
  define_unary_function_ptr5( at_snedecord_icdf ,alias_at_snedecord_icdf,&__snedecord_icdf,0,true);

  static const char _fisher_icdf_s []="fisher_icdf";
  static define_unary_function_eval (__fisher_icdf,&_snedecor_icdf,_fisher_icdf_s);
  define_unary_function_ptr5( at_fisher_icdf ,alias_at_fisher_icdf,&__fisher_icdf,0,true);
  static const char _fisherd_icdf_s []="fisherd_icdf";
  static define_unary_function_eval (__fisherd_icdf,&_snedecor_icdf,_fisherd_icdf_s);
  define_unary_function_ptr5( at_fisherd_icdf ,alias_at_fisherd_icdf,&__fisherd_icdf,0,true);

  gen cauchy(const gen & x0,const gen & a,const gen & x,GIAC_CONTEXT){
    if (x.type==_VECT)
      return apply3rd(x0,a,x,contextptr,cauchy);
    return 1/cst_pi*a/(pow(x-x0,2,contextptr)+pow(a,2,contextptr));
  }
  gen _cauchy(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return cauchy(0,1,g,contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return symbolic(at_cauchyd,g);
    if (s==3)
      return cauchy(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _cauchy_s []="cauchy";
  static define_unary_function_eval (__cauchy,&_cauchy,_cauchy_s);
  define_unary_function_ptr5( at_cauchy ,alias_at_cauchy,&__cauchy,0,true);

  static const char _cauchyd_s []="cauchyd";
  static define_unary_function_eval (__cauchyd,&_cauchy,_cauchyd_s);
  define_unary_function_ptr5( at_cauchyd ,alias_at_cauchyd,&__cauchyd,0,true);

  gen cauchy_cdf(const gen &x0,const gen &a,const gen & x,GIAC_CONTEXT){
    return plus_one_half+atan((x-x0)/a,contextptr)/cst_pi;
  }
  gen _cauchy_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return cauchy_cdf(0,1,g,contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3)
      return cauchy_cdf(v[0],v[1],v[2],contextptr);
    if (s==4)
      return cauchy_cdf(v[0],v[1],v[3],contextptr)-cauchy_cdf(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _cauchy_cdf_s []="cauchy_cdf";
  static define_unary_function_eval (__cauchy_cdf,&_cauchy_cdf,_cauchy_cdf_s);
  define_unary_function_ptr5( at_cauchy_cdf ,alias_at_cauchy_cdf,&__cauchy_cdf,0,true);

  static const char _cauchyd_cdf_s []="cauchyd_cdf";
  static define_unary_function_eval (__cauchyd_cdf,&_cauchy_cdf,_cauchyd_cdf_s);
  define_unary_function_ptr5( at_cauchyd_cdf ,alias_at_cauchyd_cdf,&__cauchyd_cdf,0,true);

  gen cauchy_icdf(const gen &x0,const gen &a,const gen & x,GIAC_CONTEXT){
    return tan(cst_pi*(x-plus_one_half),contextptr)*a+x0;
  }
  gen _cauchy_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return cauchy_icdf(0,1,g,contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3)
      return cauchy_icdf(v[0],v[1],v[2],contextptr);
    if (s==4)
      return cauchy_icdf(v[0],v[1],v[3],contextptr)-cauchy_icdf(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _cauchy_icdf_s []="cauchy_icdf";
  static define_unary_function_eval (__cauchy_icdf,&_cauchy_icdf,_cauchy_icdf_s);
  define_unary_function_ptr5( at_cauchy_icdf ,alias_at_cauchy_icdf,&__cauchy_icdf,0,true);

  static const char _cauchyd_icdf_s []="cauchyd_icdf";
  static define_unary_function_eval (__cauchyd_icdf,&_cauchy_icdf,_cauchyd_icdf_s);
  define_unary_function_ptr5( at_cauchyd_icdf ,alias_at_cauchyd_icdf,&__cauchyd_icdf,0,true);

  gen weibull(const gen & k,const gen & lambda,const gen & theta,const gen & x,GIAC_CONTEXT){
    gen tmp=(x-theta)/lambda;
    return k/lambda*pow(tmp,k-1,contextptr)*exp(-pow(tmp,k,contextptr),contextptr);
  }
  gen _weibull(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return symbolic(at_weibulld,g);
    if (s==3)
      return weibull(v[0],v[1],0,v[2],contextptr);
    if (s==4)
      return weibull(v[0],v[1],v[2],v[3],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _weibull_s []="weibull";
  static define_unary_function_eval (__weibull,&_weibull,_weibull_s);
  define_unary_function_ptr5( at_weibull ,alias_at_weibull,&__weibull,0,true);

  static const char _weibulld_s []="weibulld";
  static define_unary_function_eval (__weibulld,&_weibull,_weibulld_s);
  define_unary_function_ptr5( at_weibulld ,alias_at_weibulld,&__weibulld,0,true);

  gen weibull_cdf(const gen & k,const gen & lambda,const gen & theta,const gen & x,GIAC_CONTEXT){
    gen tmp=(x-theta)/lambda;
    return 1-exp(-pow(tmp,k,contextptr),contextptr);
  }
  gen _weibull_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3)
      return weibull_cdf(v[0],v[1],0,v[2],contextptr);
    if (s==4)
      return weibull_cdf(v[0],v[1],v[2],v[3],contextptr);
    if (s==5)
      return weibull_cdf(v[0],v[1],v[2],v[4],contextptr)-weibull_cdf(v[0],v[1],v[2],v[3],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _weibull_cdf_s []="weibull_cdf";
  static define_unary_function_eval (__weibull_cdf,&_weibull_cdf,_weibull_cdf_s);
  define_unary_function_ptr5( at_weibull_cdf ,alias_at_weibull_cdf,&__weibull_cdf,0,true);

  static const char _weibulld_cdf_s []="weibulld_cdf";
  static define_unary_function_eval (__weibulld_cdf,&_weibull_cdf,_weibulld_cdf_s);
  define_unary_function_ptr5( at_weibulld_cdf ,alias_at_weibulld_cdf,&__weibulld_cdf,0,true);

  gen weibull_icdf(const gen & k,const gen & lambda,const gen & theta,const gen & y,GIAC_CONTEXT){
    // solve(1-exp(-((x-theta)/lambda)^k)=y,x)
    // x=lambda*(-ln(-(y-1)))^(1/k)+theta
    return lambda*pow(-ln(1-y,contextptr),1/k,contextptr)+theta;
  }
  gen _weibull_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3)
      return weibull_icdf(v[0],v[1],0,v[2],contextptr);
    if (s==4)
      return weibull_icdf(v[0],v[1],v[2],v[3],contextptr);
    if (s==5)
      return weibull_icdf(v[0],v[1],v[2],v[4],contextptr)-weibull_icdf(v[0],v[1],v[2],v[3],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _weibull_icdf_s []="weibull_icdf";
  static define_unary_function_eval (__weibull_icdf,&_weibull_icdf,_weibull_icdf_s);
  define_unary_function_ptr5( at_weibull_icdf ,alias_at_weibull_icdf,&__weibull_icdf,0,true);

  static const char _weibulld_icdf_s []="weibulld_icdf";
  static define_unary_function_eval (__weibulld_icdf,&_weibull_icdf,_weibulld_icdf_s);
  define_unary_function_ptr5( at_weibulld_icdf ,alias_at_weibulld_icdf,&__weibulld_icdf,0,true);

  gen betad(const gen &alpha,const gen & beta,const gen & x,GIAC_CONTEXT){
    if ( (x==0 && alpha==1) || (x==1 && beta==1))
      return 1/Beta(alpha,beta,contextptr);
    return pow(x,alpha-1,contextptr)*pow(1-x,beta-1,contextptr)/Beta(alpha,beta,contextptr);
  }
  gen _betad(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return betad(0,1,g,contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return symbolic(at_betad,g);
    if (s==3)
      return betad(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _betad_s []="betad";
  static define_unary_function_eval (__betad,&_betad,_betad_s);
  define_unary_function_ptr5( at_betad ,alias_at_betad,&__betad,0,true);

  // beta_cdf=Beta regularized
  gen _betad_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3)
      return _Beta(makesequence(v[0],v[1],v[2],1),contextptr);
    if (s==4)
      return _Beta(makesequence(v[0],v[1],v[3],1),contextptr)-_Beta(makesequence(v[0],v[1],v[2],1),contextptr);
    return gensizeerr(contextptr);
  }
  static const char _betad_cdf_s []="betad_cdf";
  static define_unary_function_eval (__betad_cdf,&_betad_cdf,_betad_cdf_s);
  define_unary_function_ptr5( at_betad_cdf ,alias_at_betad_cdf,&__betad_cdf,0,true);

  gen betad_icdf(const gen &alpha_orig,const gen & beta_orig,const gen & t_orig,GIAC_CONTEXT){
    if (is_zero(t_orig)|| is_one(t_orig))
      return t_orig;
    gen t=evalf_double(t_orig,1,contextptr);
    gen alpha=evalf_double(alpha_orig,1,contextptr);
    gen beta=evalf_double(beta_orig,1,contextptr);
    if (alpha.type!=_DOUBLE_ || beta.type!=_DOUBLE_ || t.type!=_DOUBLE_ || alpha._DOUBLE_val<=0 || beta._DOUBLE_val<=0 || t._DOUBLE_val<0 || t._DOUBLE_val>1)
      return gensizeerr(contextptr); // symbolic(at_betad_icdf,makesequence(alpha_orig,beta_orig,t_orig));
    double y=t._DOUBLE_val;
    if (y<=1e-13){
      *logptr(contextptr) << "Underflow to 0" << endl;
      return 0;
    }
    if (y>=1-1e-13){
      *logptr(contextptr) << "Overflow to 1" << endl;
      return 1;
    }
    // Initial guess
    double x0=.5;
    double prefactor=.5;
    if (alpha._DOUBLE_val>1){
      if (beta._DOUBLE_val>1){
	x0=(alpha._DOUBLE_val-1)/(alpha._DOUBLE_val+beta._DOUBLE_val-2);
	prefactor=1.;
      }
      else
	return 1-betad_icdf(beta,alpha,1-y,contextptr);
    }
    else {
      gen tmp;
      if (beta._DOUBLE_val<1 && y>.5)
	return 1-betad_icdf(beta,alpha,1-y,contextptr);
      double Y=y*Beta(alpha,beta,contextptr)._DOUBLE_val;
      tmp=exp(ln(alpha*Y,contextptr)/alpha,contextptr);
      tmp=tmp*(1+tmp*(beta._DOUBLE_val-1)/(alpha._DOUBLE_val+1));
      if (tmp.type==_DOUBLE_ && tmp._DOUBLE_val>0)
	x0=tmp._DOUBLE_val;
      if (x0<1e-4)
	return x0;
    }
    identificateur x(" x");
    return newton(symbolic(at_Beta,makesequence(alpha,beta,x,1))-y,x,x0,NEWTON_DEFAULT_ITERATION,1e-5,1e-12,true,1,0,1,0,prefactor,contextptr);
  }
  gen _betad_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3)
      return betad_icdf(v[0],v[1],v[2],contextptr);
    if (s==4)
      return betad_icdf(v[0],v[1],v[3],contextptr)-betad_icdf(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _betad_icdf_s []="betad_icdf";
  static define_unary_function_eval (__betad_icdf,&_betad_icdf,_betad_icdf_s);
  define_unary_function_ptr5( at_betad_icdf ,alias_at_betad_icdf,&__betad_icdf,0,true);

  gen gammad(const gen &alpha,const gen & beta,const gen & x,GIAC_CONTEXT){
    if (is_zero(x) && alpha==1)
      return beta;
    if (x==plus_inf)
      return 0;
    return pow(x,alpha-1,contextptr)*exp(-beta*x,contextptr)*pow(beta,alpha,contextptr)/Gamma(alpha,contextptr);
  }
  gen _gammad(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gammad(0,1,g,contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return symbolic(at_gammad,g);
    if (s==3)
      return gammad(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _gammad_s []="gammad";
  static define_unary_function_eval (__gammad,&_gammad,_gammad_s);
  define_unary_function_ptr5( at_gammad ,alias_at_gammad,&__gammad,0,true);

  // beta_cdf=Gamma regularized
  gen _gammad_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3)
      return _lower_incomplete_gamma(makesequence(v[0],v[1]*v[2],1),contextptr);
    if (s==4)
      return _lower_incomplete_gamma(makesequence(v[0],v[1]*v[3],1),contextptr)-_lower_incomplete_gamma(makesequence(v[0],v[1]*v[2],1),contextptr);
    return gensizeerr(contextptr);
  }
  static const char _gammad_cdf_s []="gammad_cdf";
  static define_unary_function_eval (__gammad_cdf,&_gammad_cdf,_gammad_cdf_s);
  define_unary_function_ptr5( at_gammad_cdf ,alias_at_gammad_cdf,&__gammad_cdf,0,true);

  gen gammad_icdf(const gen &alpha_orig,const gen & beta_orig,const gen & t_orig,GIAC_CONTEXT){
    if (is_zero(t_orig)|| is_one(t_orig))
      return t_orig;
    gen t=evalf_double(t_orig,1,contextptr);
    gen alpha=evalf_double(alpha_orig,1,contextptr);
    gen beta=evalf_double(beta_orig,1,contextptr);
    if (alpha.type!=_DOUBLE_ || beta.type!=_DOUBLE_ || t.type!=_DOUBLE_ || alpha._DOUBLE_val<=0 || beta._DOUBLE_val<=0 || t._DOUBLE_val<0 || t._DOUBLE_val>1)
      return gensizeerr(contextptr); // symbolic(at_gammad_icdf,makesequence(alpha_orig,beta_orig,t_orig));
    double y=t._DOUBLE_val;
    if (y<=1e-13){
      *logptr(contextptr) << "Underflow" << endl;
      return 0;
    }
    if (y>=1-1e-13){
      *logptr(contextptr) << "Overflow" << endl;
      return plus_inf;
    }
    identificateur x(" x");
    double x0=.5; // FIXME improve for y near boundaries!
    double prefactor=.5;
    if (alpha._DOUBLE_val>1){
      x0=alpha._DOUBLE_val-1;
      prefactor=1.;
    }
    else {
      gen tmp=exp(ln(alpha*y*Gamma(alpha,contextptr),contextptr)/alpha,contextptr);
      tmp=tmp*(1-tmp/(alpha._DOUBLE_val+1));
      if (tmp.type==_DOUBLE_ && tmp._DOUBLE_val>0)
	x0=tmp._DOUBLE_val;
      if (x0<1e-4)
	return x0;
    }
    return newton(symbolic(at_lower_incomplete_gamma,makesequence(alpha,x))-y*Gamma(alpha,contextptr),x,x0,NEWTON_DEFAULT_ITERATION,1e-5,1e-12,true,1,0,1,0,prefactor,contextptr)/beta; 
  }
  gen _gammad_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3)
      return gammad_icdf(v[0],v[1],v[2],contextptr);
    if (s==4)
      return gammad_icdf(v[0],v[1],v[3],contextptr)-gammad_icdf(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _gammad_icdf_s []="gammad_icdf";
  static define_unary_function_eval (__gammad_icdf,&_gammad_icdf,_gammad_icdf_s);
  define_unary_function_ptr5( at_gammad_icdf ,alias_at_gammad_icdf,&__gammad_icdf,0,true);

  gen _uniform(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return 1;
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==0)
      return symbolic(at_uniformd,makesequence(0,1));
    if (s==2)
      return symbolic(at_uniformd,makesequence(v[0],v[1]));
    if (s==3)
      return inv(v[1]-v[0],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _uniform_s []="uniform";
  static define_unary_function_eval (__uniform,&_uniform,_uniform_s);
  define_unary_function_ptr5( at_uniform ,alias_at_uniform,&__uniform,0,true);
  static const char _uniformd_s []="uniformd";
  static define_unary_function_eval (__uniformd,&_uniform,_uniformd_s);
  define_unary_function_ptr5( at_uniformd ,alias_at_uniformd,&__uniformd,0,true);

  gen _uniform_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return g;
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3)
      return (v[2]-v[0])/(v[1]-v[0]);
    if (s==4)
      return (v[3]-v[2])/(v[1]-v[0]);
    return gensizeerr(contextptr);
  }
  static const char _uniform_cdf_s []="uniform_cdf";
  static define_unary_function_eval (__uniform_cdf,&_uniform_cdf,_uniform_cdf_s);
  define_unary_function_ptr5( at_uniform_cdf ,alias_at_uniform_cdf,&__uniform_cdf,0,true);
  static const char _uniformd_cdf_s []="uniformd_cdf";
  static define_unary_function_eval (__uniformd_cdf,&_uniform_cdf,_uniformd_cdf_s);
  define_unary_function_ptr5( at_uniformd_cdf ,alias_at_uniformd_cdf,&__uniformd_cdf,0,true);

  gen _uniform_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return g;
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==3)
      return v[0]+v[2]*(v[1]-v[0]);
    if (s==4)
      return (v[3]-v[2])*(v[1]-v[0]);
    return gensizeerr(contextptr);
  }
  static const char _uniform_icdf_s []="uniform_icdf";
  static define_unary_function_eval (__uniform_icdf,&_uniform_icdf,_uniform_icdf_s);
  define_unary_function_ptr5( at_uniform_icdf ,alias_at_uniform_icdf,&__uniform_icdf,0,true);
  static const char _uniformd_icdf_s []="uniformd_icdf";
  static define_unary_function_eval (__uniformd_icdf,&_uniform_icdf,_uniformd_icdf_s);
  define_unary_function_ptr5( at_uniformd_icdf ,alias_at_uniformd_icdf,&__uniformd_icdf,0,true);

  gen _exponential(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return symbolic(at_exponentiald,g);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return v[0]*exp(-v[0]*v[1],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _exponential_s []="exponential";
  static define_unary_function_eval (__exponential,&_exponential,_exponential_s);
  define_unary_function_ptr5( at_exponential ,alias_at_exponential,&__exponential,0,true);
  static const char _exponentiald_s []="exponentiald";
  static define_unary_function_eval (__exponentiald,&_exponential,_exponentiald_s);
  define_unary_function_ptr5( at_exponentiald ,alias_at_exponentiald,&__exponentiald,0,true);

  gen _exponential_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return 1-exp(-v[0]*v[1],contextptr);
    if (s==3)
      return exp(-v[0]*v[1],contextptr)-exp(-v[0]*v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _exponential_cdf_s []="exponential_cdf";
  static define_unary_function_eval (__exponential_cdf,&_exponential_cdf,_exponential_cdf_s);
  define_unary_function_ptr5( at_exponential_cdf ,alias_at_exponential_cdf,&__exponential_cdf,0,true);
  static const char _exponentiald_cdf_s []="exponentiald_cdf";
  static define_unary_function_eval (__exponentiald_cdf,&_exponential_cdf,_exponentiald_cdf_s);
  define_unary_function_ptr5( at_exponentiald_cdf ,alias_at_exponentiald_cdf,&__exponentiald_cdf,0,true);

  // 1 - exp(-l*x)=p => exp(-l*x)=1-p => x=-1/l*ln(1-p)
  gen _exponential_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return -ln(1-v[1],contextptr)/v[0];
    if (s==3)
      return (ln(1-v[1],contextptr)-ln(1-v[2],contextptr))/v[0];
    return gensizeerr(contextptr);
  }
  static const char _exponential_icdf_s []="exponential_icdf";
  static define_unary_function_eval (__exponential_icdf,&_exponential_icdf,_exponential_icdf_s);
  define_unary_function_ptr5( at_exponential_icdf ,alias_at_exponential_icdf,&__exponential_icdf,0,true);
  static const char _exponentiald_icdf_s []="exponentiald_icdf";
  static define_unary_function_eval (__exponentiald_icdf,&_exponential_icdf,_exponentiald_icdf_s);
  define_unary_function_ptr5( at_exponentiald_icdf ,alias_at_exponentiald_icdf,&__exponentiald_icdf,0,true);

  // geometric(p,k)=(1-p)^(k-1)*p
  gen geometric(const gen & p,const gen & k,GIAC_CONTEXT){
    gen K(k);
    if (is_positive(-k,contextptr))
      return gensizeerr(contextptr);
    return pow(1-p,k-1,contextptr)*p;
  }
  gen _geometric(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return symbolic(at_geometric,g);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return geometric(v[0],v[1],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _geometric_s []="geometric";
  static define_unary_function_eval (__geometric,&_geometric,_geometric_s);
  define_unary_function_ptr5( at_geometric ,alias_at_geometric,&__geometric,0,true);

  // geometric_cdf(p,k)=1-(1-p)^k
  gen geometric_cdf(const gen & p,const gen & k,GIAC_CONTEXT){
    gen K(k);
    if (is_strictly_positive(-k,contextptr))
      return gensizeerr(contextptr);
    return 1-pow(1-p,k,contextptr);
  }
  gen _geometric_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return symbolic(at_geometric_cdf,g);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return geometric_cdf(v[0],v[1],contextptr);
    if (s==3)
      return geometric_cdf(v[0],v[2],contextptr)-geometric_cdf(v[0],v[1]-1,contextptr);
    return gensizeerr(contextptr);
  }
  static const char _geometric_cdf_s []="geometric_cdf";
  static define_unary_function_eval (__geometric_cdf,&_geometric_cdf,_geometric_cdf_s);
  define_unary_function_ptr5( at_geometric_cdf ,alias_at_geometric_cdf,&__geometric_cdf,0,true);

  // k=geometric_icdf(p,P) if 1-(1-p)^k>=P hence 
  gen geometric_icdf(const gen & p,const gen & P,GIAC_CONTEXT){
    return _ceil(ln(1-P,contextptr)/ln(1-p,contextptr),contextptr);
  }
  gen _geometric_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return symbolic(at_geometric_icdf,g);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s==2)
      return geometric_icdf(v[0],v[1],contextptr);
    if (s==3)
      return geometric_icdf(v[0],v[2],contextptr)-geometric_icdf(v[0],v[1],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _geometric_icdf_s []="geometric_icdf";
  static define_unary_function_eval (__geometric_icdf,&_geometric_icdf,_geometric_icdf_s);
  define_unary_function_ptr5( at_geometric_icdf ,alias_at_geometric_icdf,&__geometric_icdf,0,true);

  gen _randgeometric(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return _ceil(std::log(1-giac_rand(contextptr)/(rand_max2+1.0))/ln(1-g,contextptr),contextptr);
  }
  static const char _randgeometric_s []="randgeometric";
  static define_unary_function_eval (__randgeometric,&_randgeometric,_randgeometric_s);
  define_unary_function_ptr5( at_randgeometric ,alias_at_randgeometric,&__randgeometric,0,true);

  double kolmogorovd(double c2){
    c2=c2*c2;
    // 2*sum((-1)^(r-1)*exp(-2*r^2*c^2),r,1,inf)
    long_double cumul=0;
    for (int r=1;;++r){
      long_double current=std::exp(-2*r*r*c2);
      if (cumul==cumul+current)
	return 1-double(2*cumul);
      if (r%2)
	cumul += current;
      else
	cumul -= current;
    }
  }

  gen _kolmogorovd(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT)
      return apply(g,_kolmogorovd,contextptr);
    gen tmp=evalf_double(g,1,contextptr);
    if (tmp.type!=_DOUBLE_)
      return symbolic(at_kolmogorovd,g);
    if (is_positive(-g,contextptr))
      return undef;
    double c2=tmp._DOUBLE_val;
    return kolmogorovd(c2);
  }
  static const char _kolmogorovd_s []="kolmogorovd";
  static define_unary_function_eval (__kolmogorovd,&_kolmogorovd,_kolmogorovd_s);
  define_unary_function_ptr5( at_kolmogorovd ,alias_at_kolmogorovd,&__kolmogorovd,0,true);

  /*
  gen _kolmogorov_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT)
      return apply(g,_kolmogorov_cdf,contextptr);
    gen tmp=evalf_double(g,1,contextptr);
    if (tmp.type!=_DOUBLE_)
      return symbolic(at_kolmogorov_cdf,g);
    if (is_positive(-g,contextptr))
      return undef;
    double c=tmp._DOUBLE_val,sqrt2=std::sqrt(2.0);
    // 1-1/ln(2)*sum((-1)^(r-1)/r*erfc(sqrt(2)*r*c),r,1,inf)
    double cumul=0;
    for (int r=1;;++r){
      double current=_erfc(sqrt2*r*c,contextptr)._DOUBLE_val/r;
      if (cumul==cumul+current)
	return 1-cumul/std::log(2.0);
      if (r%2)
	cumul += current;
      else
	cumul -= current;
    }
  }
  static const char _kolmogorov_cdf_s []="kolmogorov_cdf";
  static define_unary_function_eval (__kolmogorov_cdf,&_kolmogorov_cdf,_kolmogorov_cdf_s);
  define_unary_function_ptr5( at_kolmogorov_cdf ,alias_at_kolmogorov_cdf,&__kolmogorov_cdf,0,true);
  */
  bool is_discrete_distribution(int nd){
    return nd==2 || nd==3 || nd==4 || nd==12;
  }

  gen _kolmogorovt(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()<2)
      return gensizeerr(contextptr);
    vecteur & v = *g._VECTptr;
    gen x=v[0],y=v[1];
    if (y==at_exp)
      y=at_exponential;
    if (v.size()==3)
      return _kolmogorovt(makesequence(x,y(v[2],contextptr)),contextptr);
    if (v.size()>2)
      return _kolmogorovt(makesequence(x,y(vecteur(v.begin()+2,v.end()),contextptr)),contextptr);
    if (is_distribution(x)){
      if (is_distribution(y))
	return gensizeerr(contextptr);
      swapgen(x,y);
    }
    int nd=is_distribution(y);
    if (nd){
      if (y==at_normald || y==at_normal || y==at_cauchyd || y==at_cauchy)
	y=symbolic(*y._FUNCptr,makesequence(0,1));
      if (y.type!=_SYMB)
	return gensizeerr(contextptr);
    }
    vector<giac_double> X;
    x=evalf_double(x,1,contextptr);
    if (x.type!=_VECT || !convert(*x._VECTptr,X,true))
      return gensizeerr(contextptr);
    sort(X.begin(),X.end());
    int n=int(X.size());
    double cumulx=0,d=0,dcur,invn=1./n,ks;
    if (nd){
      gen f=cdf(nd);
      if (f.type!=_FUNC)
	return gensizeerr(contextptr);
      // add parameters from y
      vecteur yf=gen2vecteur(y._SYMBptr->feuille);
      if (int(yf.size())!=distrib_nargs(nd))
	return gensizeerr(contextptr);
      yf.push_back(0);
      f=symbolic(*f._FUNCptr,gen(yf,_SEQ__VECT));
      gen & fback=f._SYMBptr->feuille._VECTptr->back();
      if (is_discrete_distribution(nd)){
	for (vector<giac_double>::const_iterator it=X.begin();it!=X.end();){
	  fback=double(*it)-1;
	  gen prevtmp=evalf_double(f,1,contextptr);
	  dcur=std::abs(prevtmp._DOUBLE_val-cumulx);
	  if (dcur>d)
	    d=dcur;
	  fback=double(*it);
	  gen tmp=evalf_double(f,1,contextptr);
	  int i=1;
	  for (++it;it!=X.end() && double(*it)==fback;++it)
	    ++i;
	  cumulx += i*invn;
	  dcur=std::abs(tmp._DOUBLE_val-cumulx);
	  if (dcur>d)
	    d=dcur;
	}
	ks=d*std::sqrt(double(n));
	return makevecteur(string2gen("D=",false),d,string2gen("K=",false),ks,string2gen("1-kolmogorovd(K)=",false),1-kolmogorovd(ks));
      }
      for (int i=0;i<n;++i){
	fback=double(X[i]);
	gen tmp=evalf_double(f,1,contextptr);
	if (tmp.type!=_DOUBLE_)
	  return gensizeerr(contextptr);
	dcur=std::abs(tmp._DOUBLE_val-cumulx);
	if (dcur>d)
	  d=dcur;
	cumulx += invn;
	dcur=std::abs(tmp._DOUBLE_val-cumulx);
	if (dcur>d)
	  d=dcur;
      }
      ks=d*std::sqrt(double(n));
      return makevecteur(string2gen("D=",false),d,string2gen("K=",false),ks,string2gen("1-kolmogorovd(K)=",false),1-kolmogorovd(ks));
    }
    // 2 lists
    vector<giac_double> Y;
    y=evalf_double(y,1,contextptr);
    if (y.type!=_VECT || !convert(*y._VECTptr,Y,true))
      return gensizeerr(contextptr);
    sort(Y.begin(),Y.end());
    int m=int(Y.size());
    double cumuly=0,invm=1./m;
    int i=0,j=0;
    while (i<n && j<m){
      if (X[i]==Y[j]){
	cumulx += invn;
	cumuly += invm;
	++i; ++j;
      }
      else {
	if (X[i]>Y[j]){
	  cumuly += invm;
	  ++j;
	}
	else {
	  cumulx += invn;
	  ++i;
	}
      }
      dcur=std::abs(cumulx-cumuly);
      if (dcur>d)
	d=dcur;
    }
    ks=d*std::sqrt((n*m)/double(n+m));
    return makevecteur(string2gen("D=",false),d,string2gen("K=",false),ks,string2gen("1-kolmogorovd(K)=",false),1-kolmogorovd(ks));
  }
  static const char _kolmogorovt_s []="kolmogorovt";
  static define_unary_function_eval (__kolmogorovt,&_kolmogorovt,_kolmogorovt_s);
  define_unary_function_ptr5( at_kolmogorovt ,alias_at_kolmogorovt,&__kolmogorovt,0,true);
  

  // computes wilcoxon test value for sample x and median m_ or samples x and m_
  gen wilcoxons(const vecteur & x,const gen & m_,GIAC_CONTEXT){
    if (m_.type==_VECT){
      vecteur & y=*m_._VECTptr;
      int n=int(y.size());
      int m=int(x.size());
      vecteur xm;
      for (int i=0;i<n;++i){
	xm.push_back(makevecteur(y[i],i));
      }
      for (int i=0;i<m;++i){
	xm.push_back(makevecteur(x[i],n+i));
      }
      gen_sort_f(xm.begin(),xm.end(),first_ascend_sort);
      vector<double> stat(xm.size());
      for (unsigned i=1;i<=xm.size();++i){
	unsigned j=1; // number of ties
	for (;i<xm.size() && xm[i]._VECTptr->front()==xm[i-1]._VECTptr->front();){
	  ++j; ++i;
	}
	// xm[i]!=xm[i-1] (or i==xm.size())
	// xm[i-1]==...=xm[i-j]
	double value=(i-1+i-j)/2.+1;
	for (unsigned k=1;k<=j;++k)
	  stat[i-k]=value;
      }
      gen res=0;
      for (unsigned i=0;i<xm.size();++i){
	if (is_strictly_greater(n,xm[i]._VECTptr->back().val,contextptr))
	  res += stat[i]; // int(i+1);
      }
      return res;
    }
    gen m=evalf_double(m_,1,contextptr);
    if (m.type!=_DOUBLE_)
      return gensizeerr(gettext("Invalid median"));
    vecteur xm(x);
    for (unsigned i=0;i<xm.size();++i){
      xm[i]=makevecteur(abs(xm[i]-m),int(i));
    }
    gen_sort_f(xm.begin(),xm.end(),first_ascend_sort);
    gen res=0;
    for (unsigned i=0;i<xm.size();++i){
      if (is_greater(x[xm[i]._VECTptr->back().val],m,contextptr))
	res += int(i+1);
    }
    return res;
  }

  gen _wilcoxons(const gen & args,GIAC_CONTEXT){
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    gen x=args._VECTptr->front(),m=args._VECTptr->back();
    if (x.type!=_VECT || x._VECTptr->empty())
      return gendimerr(contextptr);
    return wilcoxons(*x._VECTptr,m,contextptr);
  }
  static const char _wilcoxons_s []="wilcoxons";
  static define_unary_function_eval (__wilcoxons,&_wilcoxons,_wilcoxons_s);
  define_unary_function_ptr5( at_wilcoxons ,alias_at_wilcoxons,&__wilcoxons,0,true);

  // returns product_i=1^n (1+t^i)
  gen wilcoxonp(int n){
    if (n<=0)
      return vecteur(0);
    gen res(gen(vecteur(1,1),_POLY1__VECT));
    for (int i=1;i<=n;++i){
      vecteur tmp(i+1);
      tmp[i]=tmp[0]=1;
      res=res*gen(tmp,_POLY1__VECT);
    }
    return res;
  }
  gen wilcoxonp(int m,int n,GIAC_CONTEXT){
    // sum_k Pmn(k)*x^k = 1/comb(m+n,n)*prod_{i=n+1}^{m+n}(1-x^i)/prod_{j=1}^m(1-x^j)
    if (n<=0 || m<=0)
      return vecteur(0);
    gen num(gen(vecteur(1,1),_POLY1__VECT));
    for (int i=n+1;i<=m+n;++i){
      vecteur tmp(i+1);
      tmp[i]=-1; tmp[0]=1;
      num=num*gen(tmp,_POLY1__VECT);
    }
    gen den(gen(vecteur(1,1),_POLY1__VECT));
    for (int i=1;i<=m;++i){
      vecteur tmp(i+1);
      tmp[i]=-1; tmp[0]=1;
      den=den*gen(tmp,_POLY1__VECT);
    }
    gen q=_quo(makesequence(num,den),contextptr);
    return q;
  }
  gen _wilcoxonp(const gen & args,GIAC_CONTEXT){
    gen n(args);
    if (n.type==_VECT && n._VECTptr->size()==2){
      gen M(n._VECTptr->front()),N(n._VECTptr->back());
      if (!is_integral(M) || M.type!=_INT_ || M.val < 1 ||
	  !is_integral(N) || N.type!=_INT_ || N.val < 1 || M.val+N.val > 400
	  )
	return gendimerr(contextptr);
      return wilcoxonp(M.val,N.val,contextptr)/comb(M.val+N.val,N.val);
    }
    if (!is_integral(n) || n.type!=_INT_ || n.val<1 || n.val>1000)
      return gendimerr(contextptr);
    return wilcoxonp(n.val)/pow(plus_two,n,contextptr);
  }
  static const char _wilcoxonp_s []="wilcoxonp";
  static define_unary_function_eval (__wilcoxonp,&_wilcoxonp,_wilcoxonp_s);
  define_unary_function_ptr5( at_wilcoxonp ,alias_at_wilcoxonp,&__wilcoxonp,0,true);

  // a faire wilcoxont(echantillon,mediane,seuil alpha) renvoyant true (accepte
  // test 2-sided) si W est dans l'intervalle symetrique de borne inferieure
  // le plus grand k tel que wilcoxonp[0]+...+wilcoxonp[k-1]<alpha/2
  gen _wilcoxont(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()<2 || g._VECTptr->size()>4)
      return gensizeerr(contextptr);
    vecteur & v = *g._VECTptr;
    gen x=v[0],m=v[1],alpha=0.05;
    int typetest=0; // 1 >, -1 <
    if (x.type!=_VECT || x._VECTptr->empty())
      return gensizeerr(gettext("Invalid sample"));
    if (v.size()>=3 && v[2].type!=_FUNC)
      alpha=evalf_double(v[2],1,contextptr);
    if (v.size()>=4 && v[3].type!=_FUNC)
      alpha=evalf_double(v[3],1,contextptr);
    if (v.size()>=3 && v[2].type==_FUNC){
      if (v[2]==at_superieur_strict || v[2]==at_superieur_egal)
	typetest=1;
      if (v[2]==at_inferieur_strict || v[2]==at_inferieur_egal)
	typetest=-1;
    }
    if (v.size()>=4 && v[3].type==_FUNC){
      if (v[3]==at_superieur_strict || v[3]==at_superieur_egal)
	typetest=1;
      if (v[3]==at_inferieur_strict || v[3]==at_inferieur_egal)
	typetest=-1;
    }
    if (alpha.type!=_DOUBLE_ || alpha._DOUBLE_val<=0 || alpha._DOUBLE_val>=1)
      return gensizeerr(gettext("Invalid confidence level"));
    int n=int(x._VECTptr->size());
    if (m.type==_VECT){
      // if (typetest!=0) return gensizeerr(gettext("H1 must be <> for Mann-Whitney test"));
      gen w=wilcoxons(*m._VECTptr,x,contextptr);
      if (w.type!=_DOUBLE_)
	return gensizeerr(contextptr);
      int N=int(m._VECTptr->size()),M=int(x._VECTptr->size());
      gen combMN=comb(M+N,N);
      *logptr(contextptr) << "Mann-Whitney 2-sample test, H0 same Median, H1 ";
      if (typetest==0) *logptr(contextptr) << "<>";
      if (typetest==1) *logptr(contextptr) << ">";
      if (typetest==-1) *logptr(contextptr) << "<";
      *logptr(contextptr) << "\nranksum "<< w << ", shifted ranksum " << w-M*(M+1.)/2 << endl;
      double W=M*N+M*(M+1.)/2-w._DOUBLE_val;
      if (typetest==0){
	*logptr(contextptr) << "u1=" << W << " ,u2=" << M*N-W ; 
	if (W>M*(N/2.))
	  W=M*N-W;
	*logptr(contextptr) << ", u=min(u1,u2)=" << W << endl;
      }
      gen p=wilcoxonp(M,N,contextptr);
      if (p.type!=_VECT || p._VECTptr->size()<double(M)*N) return gensizeerr(contextptr);
      vecteur & v = *p._VECTptr;
      gen P=0,Q=0;
      for (double i=0;i<=W;++i){
	P += v[int(i)];
      }
      P=P/combMN;
      if (typetest==0){
	gen seuil=combMN*alpha/2;
	int um(-1);
	for (double i=0;i<M*N;++i){
	  Q += v[int(i)];
	  if (um==-1 && typetest==0 && is_greater(Q,seuil,contextptr))
	    um=int(i)-1;
	}
	*logptr(contextptr) << "Limit value to reject H0 " << um << endl;
	P=2*P;
      }
      if (typetest==1)
	P=1-P;
      *logptr(contextptr) << "P-value " << P << " (" << evalf_double(P,1,contextptr) << "), alpha=" << alpha;
      bool ok=is_greater(P,alpha,contextptr);
      *logptr(contextptr) << (ok?" H0 not rejected":" H0 rejected") << endl;
      return ok?1:0;
    }
    gen w=wilcoxons(*x._VECTptr,m,contextptr);
    if (w.type!=_INT_)
      return gensizeerr(contextptr);
    *logptr(contextptr) << "Wilcoxon 1-sample test, H0 Median=" << m << ", H1 M";
    if (typetest==0) *logptr(contextptr) << "<>";
    if (typetest==1) *logptr(contextptr) << ">";
    if (typetest==-1) *logptr(contextptr) << "<";
    *logptr(contextptr) << m << endl;
    gen p=wilcoxonp(n);
    if (p.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & pv=*p._VECTptr;
    gen total=0;
    unsigned k=0,kmax=-1;
    if (typetest==0){
      int wbar=(n*(n+1))/2-w.val;
      kmax=giacmin(w.val,wbar);
    }
    if (typetest==1){
      k=w.val;
      kmax=(n*(n+1))/2;
    }
    if (typetest==-1){
      k=0;
      kmax=w.val;
    }
    for (;k<=kmax;k++){
      total += pv[k];
    }
    total=evalf_double(total/pow(plus_two,n,contextptr),1,contextptr);
    if (typetest==0)
      total=2*total;
    *logptr(contextptr) << gettext("Wilcoxon statistic: ") << w << gettext(", p-value: ") << total << gettext(", confidence level: ") << alpha << endl;
    if (is_greater(total,alpha,contextptr))
      return 1;
    return 0;
  }
  static const char _wilcoxont_s []="wilcoxont";
  static define_unary_function_eval (__wilcoxont,&_wilcoxont,_wilcoxont_s);
  define_unary_function_ptr5( at_wilcoxont ,alias_at_wilcoxont,&__wilcoxont,0,true);
  
  int giacmin(const std::vector<int> & X){
    vector<int>::const_iterator it=X.begin(),itend=X.end();
    int r=RAND_MAX;
    for (;it!=itend;++it){
      if (*it<r)
	r=*it;
    }
    return r;
  }

  int giacmax(const std::vector<int> & X){
    vector<int>::const_iterator it=X.begin(),itend=X.end();
    int r=-RAND_MAX;
    for (;it!=itend;++it){
      if (*it>r)
	r=*it;
    }
    return r;
  }

  void effectif(const std::vector<int> & x,std::vector<int> & eff,int m){
    vector<int>::const_iterator it=x.begin(),itend=x.end();
    for (;it!=itend;++it){
      ++eff[*it-m];
    }
  }

  void somme(const vector<int> & x,const vector<int> &y,vector<int> & z){
    if (&x==&z){
      vector<int>::const_iterator jt=y.begin(),jtend=y.end();
      vector<int>::iterator it=z.begin(),itend=z.end();
      for (;it!=itend&& jt!=jtend;++it,++jt)
	*it+=*jt;
      for (;jt!=jtend;++jt)
	z.push_back(*jt);
      return;
    }
    if (&y==&z){
      somme(y,x,z);
      return;
    }
    z.clear();
    z.reserve(giacmax(int(x.size()),int(y.size())));
    vector<int>::const_iterator it=x.begin(),itend=x.end(),jt=y.begin(),jtend=y.end();
    for (;it!=itend&& jt!=jtend;++it,++jt)
      z.push_back(*it+*jt);
    for (;it!=itend;++it)
      z.push_back(*it);
    for (;jt!=jtend;++jt)
      z.push_back(*jt);
  }

  int somme(const vector<int> & x){
    int s=0;
    vector<int>::const_iterator it=x.begin(),itend=x.end();
    for (;it!=itend;++it){
      s+=*it;
    }
    return s;
  }

  // Chi2 test: valid inputs
  // Arg2: A list of probabilities (multinomial adequation test) or a distribution
  //       or a list of integer values (two samples test)
  // Arg1: A list of integer values that is either effectifs or data
  // or a list of double values (adequation to density only)
  // or a matrix of classes/data (like obtained by classes)
  gen _chisquaret(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()<2)
      return gensizeerr(contextptr);
    vecteur v = *g._VECTptr;
    if (g.subtype!=_SEQ__VECT && is_integer_vecteur(v)){
      vector<int> X=vecteur_2_vector_int(v);
      int m=giacmin(X),M=giacmax(X);
      // guess if data=effectifs or data=values
      if (X.size()>=50 && int(X.size())>5*(M-m)){
	*logptr(contextptr) << gettext("Guessing data is a list of values, adequation to uniform discret distribution between ")<<m << gettext(" and ") <<  M << endl;
	return _chisquaret(makesequence(g,vecteur(M-m+1,1./(M-m))),contextptr);
      }
      *logptr(contextptr) << gettext("Guessing data is the list of number of elements in each class, adequation to uniform distribution")<<endl;
      return _chisquaret(makesequence(g,vecteur(X.size(),1./X.size())),contextptr);     
    }
    // parse arguments for keyword classes
    double classmin=class_minimum,classsize=class_size;
    bool classdef=false;
    for (unsigned i=0;i<v.size();++i){
      if (v[i]==at_classes){
	if (i+1<v.size()){
	  gen tmp=evalf_double(v[i+1],1,contextptr);
	  if (tmp.type!=_DOUBLE_)
	    return gensizeerr(contextptr);
	  classmin=tmp._DOUBLE_val;
	  if (i+2<v.size()){
	    gen tmp=evalf_double(v[i+2],1,contextptr);
	    if (tmp.type!=_DOUBLE_)
	      return gensizeerr(contextptr);
	    classsize=tmp._DOUBLE_val;
	    classdef=true;
	  }
	}
	v.erase(v.begin()+i,v.end());
	break;
      }
    }
    gen x=v[0],y=v[1];
    if (x.type!=_VECT)
      return gensizeerr(contextptr);
    gen ytotal=_plus(y,contextptr);
    bool yproba=is_zero(1-ytotal,contextptr);
    if (!yproba && y.type==_VECT){
      // >= 2 samples, same law?
      // x and y are either classes/effectifs or effectifs (integer values)
      if (is_integer_vecteur(*x._VECTptr)){
	if (!is_integer_vecteur(*y._VECTptr))
	  return gensizeerr(contextptr);  
	vector< vector<int> > M(2);
	M[0]=vecteur_2_vector_int(*x._VECTptr);
	M[1]=vecteur_2_vector_int(*y._VECTptr);
	unsigned s=unsigned(v.size());
	for (int j=2;j<int(s);++j){
	  if (v[j].type!=_VECT || !is_integer_vecteur(*v[j]._VECTptr))
	    return gensizeerr(contextptr);
	  M.push_back(vecteur_2_vector_int(*v[j]._VECTptr));
	}
	vector< vector<int> > effX(s);
	int k=int(M[0].size());
	if (k!=int(M[1].size())){
	  // build effectifs for x and y
	  int m=giacmin(M[0]),ma=giacmax(M[0]);
	  for (unsigned j=1;j<s;++j){
	    m=giacmin(m,giacmin(M[j]));
	    ma=giacmax(ma,giacmax(M[j]));
	  }
	  k=ma-m+1;
	  for (unsigned j=0;j<s;++j){
	    effX[j]=vector<int>(k);
	    effectif(M[j],effX[j],m);	  
	  }
	}
	else 
	  effX=M;
	vector<int> eff(k);
	for (unsigned j=0;j<s;++j){
	  somme(eff,effX[j],eff);	  
	}
	double N=somme(eff);
	vector<double> NX(s);
	for (unsigned j=0;j<s;++j){
	  NX[j]=somme(effX[j]);	  
	}
	double res=0;
	for (unsigned i=0;i<eff.size();++i){
	  // theoric proba of class i is eff[i]/N
	  double p_i=eff[i]/N;
	  for (unsigned j=0;j<s;++j){
	    double tmp1=NX[j]*p_i;
	    double tmp2=effX[j][i]-tmp1;
	    res += tmp2*tmp2/tmp1;
	  }
	}
	*logptr(contextptr) << s << gettext(" samples Chi2 test result ") << res << gettext(",\nreject adequation if superior to chisquare_icdf(") << k-1 << ",0.95)=" <<  chisquare_icdf(k-1,0.95,contextptr) << " or chisquare_icdf(" << k-1 <<",1-alpha) if alpha!=5%" << endl;	
	return res;
      }
    }
    if (yproba && x.type==_VECT && !x._VECTptr->empty() && is_integer_vecteur(*x._VECTptr)){
      // if x is a vector of N integers in 1..J and y a vector of J probabilities
      // multinomial adequation test: returns sum( (effectif[x==j]-N*y[j])^2/(N*y[j]),j=1..J)
      // check y
      if (y.type!=_VECT || y._VECTptr->size()<2)
	return gensizeerr(contextptr);
      int J=int(y._VECTptr->size());
      vector<int> X=vecteur_2_vector_int(*x._VECTptr);
      int N=int(X.size());
      gen res=0;
      if (N==J){ // X is directly the effectifs
	N=0;
	for (int i=0;i<J;++i){
	  if (X[i]<0)
	    return gensizeerr(contextptr);
	  N+=X[i];
	}
	for (int j=0;j<J;++j){
	  gen tmp= N*y[j];
	  res += (X[j]-tmp)*(X[j]-tmp)/tmp;
	}
      }
      else {
	vector<int> eff(J+1);
	int shift=1;
	if (equalposcomp(X,0))
	  shift=0;
	for (int i=0;i<N;++i){
	  if (X[i]<shift || X[i]>=J+shift)
	    return gensizeerr(gettext("Data should be a class number between 0 and ")+print_INT_(J));
	  ++eff[X[i]];
	}
	for (int j=shift;j<J+shift;++j){
	  gen tmp= N*y[j-shift];
	  res += (eff[j]-tmp)*(eff[j]-tmp)/tmp;
	}
      }
      *logptr(contextptr) << gettext("Sample adequation to a finite discrete probability distribution\nChi2 test result ") << res << gettext(",\nreject adequation if superior to chisquare_icdf(") << J-1 << ",0.95)=" <<  chisquare_icdf(J-1,0.95,contextptr) << " or chisquare_icdf(" << J-1 <<",1-alpha) if alpha!=5%" << endl;
      return res;
    }
    int nd;
    if ((nd=is_distribution(y))){
      // adequation to a distribution, parameters are estimated or given, depends on the
      // number of arguments
      gen xorig=x,moyenne=undef,ecart;
      vector<int> X,eff;
      int minX,maxX,Xclasses;
      double efftotal;
      if (is_integer_vecteur(*xorig._VECTptr)){
	X=vecteur_2_vector_int(*xorig._VECTptr);
	minX=giacmin(X),maxX=giacmax(X);
	Xclasses=maxX-minX+1;
	if (X.size()>=50 && int(X.size())>=5*Xclasses){
	  eff.resize(Xclasses);
	  effectif(X,eff,minX);
	  efftotal=somme(eff);
	}
	else {
	  minX=0;
	  eff=X;
	  efftotal=somme(eff);
	  Xclasses=int(X.size());
	  gen m1=0,m2=0;
	  for (unsigned i=1;i<X.size();++i){
	    m1 += double(i)*X[i];
	    m2 += (i*double(i))*X[i];
	  }
	  moyenne=m1/efftotal;
	  ecart=std::sqrt(efftotal/(efftotal-1))*sqrt(m2/efftotal-moyenne*moyenne,contextptr);
	}	
      }
      if (is_undef(moyenne)){
	x=evalf_double(x,1,contextptr);
	moyenne=_mean(x,contextptr);
	ecart=_stdDev(x,contextptr);
      }
      if (x.type!=_VECT)
	return gensizeerr(contextptr);
      int nargs=distrib_nargs(nd);
      vecteur w(v.begin()+2,v.end());
      if (y.type==_SYMB)
	w=gen2vecteur(y._SYMBptr->feuille);
      int s=int(w.size());
      if (s>nargs || s<nargs-2)
	return gendimerr(contextptr);
      int dof = nargs-s; // adjust number of degree of freedom
      if (s==nargs-2){ // 2 params estimation
	switch (nd){
	case 1:
	  w.push_back(moyenne);
	  w.push_back(ecart);
	  *logptr(contextptr) << gettext("Normal density, estimating mean and stddev from data ") << moyenne << " " << ecart << endl;
	  break;
	case 2:{
	  // moyenne=n*p, ecart^2=n*p*(1-p)
	  gen p=1-ecart*ecart/moyenne;
	  if (is_greater(0,p,contextptr) || is_greater(p,1,contextptr))
	    return gensizeerr(contextptr);
	  gen n=_round(moyenne/p,contextptr);
	  p=moyenne/n;
	  if (is_greater(0,p,contextptr) || is_greater(p,1,contextptr))
	    return gensizeerr(contextptr);
	  *logptr(contextptr) << gettext("Binomial: estimating n and p from data ") << n << " " << p << endl;
	  w.push_back(n);
	  w.push_back(p);
	  break;
	}
	case 3:{
	  gen p=moyenne/(ecart*ecart);
	  if (is_greater(0,p,contextptr) || is_greater(p,1,contextptr))
	    return gensizeerr(contextptr);
	  gen n=_round(moyenne*p/(1-p),contextptr);
	  *logptr(contextptr) << gettext("Negative binomial: estimating n and p from data ") << n << " " << p << endl;
	  w.push_back(n);
	  w.push_back(p);
	  break;
	}
	case 8:
	  // weibull: 2 parameters to estimate k, lambda, theta assumed to be 0
	  // lambda*Gamma(1+1/k)=moyenne, lambda^2*Gamma(1+2/k)=ecart^2-moyenne^2
	  return gensizeerr(contextptr);
	  break;
	case 13:{ // moyenne +/- sqrt(3)*ecart
	  gen a=moyenne-std::sqrt(3.0)*ecart;
	  gen b=moyenne+std::sqrt(3.0)*ecart;
	  w.push_back(a);
	  w.push_back(b);
	  *logptr(contextptr) << gettext("Uniform density, estimating interval boundaries from data ") << a << " " << b << endl;
	  break;
	}
	default:
	  return gendimerr(contextptr);
	} // end switch
	s=int(w.size());
      } // end 2 paramters to estimate
      if (s==nargs-1){ // 1 params estimation
	switch (nd){
	case 1: {// normald with 1 parameter known, w[0] default is stddev
	  if (w.empty())
	    return gendimerr(contextptr);
	  if (w[0].is_symb_of_sommet(at_equal)){
	    if (w[0][1]==at_mean){
	      *logptr(contextptr) << gettext("Normal density, estimating std deviation from data ") << ecart << endl;
	      w.push_back(ecart);
	    }
	    w[0]=w[0][2];
	  }
	  if (w.size()==1){
	    *logptr(contextptr) << gettext("Normal density, estimating mean from data ") << moyenne << endl;
	    w.insert(w.begin(),moyenne);
	  }
	  break;
	}
	case 4:
	  w.push_back(moyenne);
	  *logptr(contextptr) << gettext("Poisson distribution, estimating parameter from data mean ") << moyenne << endl;
	  break;
	default:
	  return gensizeerr(contextptr);
	}
	s=int(w.size());
      }
      w.push_back(0); // last argument of the distribution
      gen loi;
      bool discret=is_discrete_distribution(nd);
      if (discret)
	loi=symbolic(y.type==_FUNC?*y._FUNCptr:y._SYMBptr->sommet,gen(w,_SEQ__VECT));
      else {
	loi=cdf(nd);
	if (loi.type!=_FUNC)
	  return gensizeerr(contextptr);
	loi=symbolic(*loi._FUNCptr,gen(w,_SEQ__VECT));
      }
      // now we have the law, do the test!
      // if classdef=false, for discrete distributions use classsize=1, classmin=0
      // for densities guess from data?
      if (is_integer_vecteur(*xorig._VECTptr)){
	if (classdef){
	  // not yet supported...
	  *logptr(contextptr) << "Warning, using default class minimum=0 and class_size=1 for discrete distribution" << endl;
	}
	dof=Xclasses-1-dof;
	gen res=0;
	for (unsigned i=0;i<eff.size();++i){
	  // theoric proba of class i from law
	  loi._SYMBptr->feuille._VECTptr->back()=minX+int(i)+(discret?0:-.5);
	  gen tmp=evalf_double(loi,1,contextptr);
	  if (!discret){ // could be improved, we compute twice the cdf
	    loi._SYMBptr->feuille._VECTptr->back()=minX+int(i+1)-.5;
	    tmp=evalf_double(loi,1,contextptr)-tmp;
	  }
	  double p_i=tmp._DOUBLE_val;
	  double tmp1=efftotal*p_i;
	  double tmp2=eff[i]-tmp1;
	  res += tmp2*tmp2/tmp1;
	}
	loi._SYMBptr->feuille._VECTptr->back()=identificateur(".");
	*logptr(contextptr) << gettext("Sample adequation to ")<< loi <<gettext(", Chi2 test result ") << res << gettext(",\nreject adequation if superior to chisquare_icdf(") << dof << ",0.95)=" <<  chisquare_icdf(dof,0.95,contextptr) << gettext(" or chisquare_icdf(") << dof <<gettext(",1-alpha) if alpha!=5%") << endl;	
	return res;
      } // end if is_integer_vecteur(x)
      if (discret)
	return gensizeerr(gettext("Non integer values for adequation to a discrete distribution"));
      // compute classes and recall itself
      matrice m;
      if (ckmatrix(x))
	m=*x._VECTptr;
      else
	m=effectifs(*x._VECTptr,classmin,classsize,contextptr);
      dof=int(m.size())-dof;
      if (dof<2)
	return gensizeerr(gettext("Not enough degree of freedom with default values. Add classes,classmin,classsize parameters"));
      efftotal=0.0;
      for (unsigned i=0;i<m.size();++i){
	gen effi=m[i][1];
	if (effi.type!=_INT_)
	  return gensizeerr(contextptr);
	efftotal += effi.val;
      }
      gen res=0;
      for (unsigned i=0;i<m.size();++i){
	gen tmp=m[i][0];
	if (!tmp.is_symb_of_sommet(at_interval))
	  return gensizeerr(contextptr);
	gen tmp0=evalf_double(tmp._SYMBptr->feuille[0],1,contextptr),
	  tmp1=evalf_double(tmp._SYMBptr->feuille[1],1,contextptr),
	  tmp2=m[i][1];
	if (tmp0.type!=_DOUBLE_ || tmp1.type!=_DOUBLE_ || tmp1._DOUBLE_val<=tmp0._DOUBLE_val)
	  return gensizeerr(contextptr);
	loi._SYMBptr->feuille._VECTptr->back()=tmp1;
	gen theoric=evalf_double(loi,1,contextptr);
	loi._SYMBptr->feuille._VECTptr->back()=tmp0;
	theoric=theoric-evalf_double(loi,1,contextptr);
	theoric=efftotal*theoric;
	res += (tmp2-theoric)*(tmp2-theoric)/theoric;
      }
      loi._SYMBptr->feuille._VECTptr->back()=identificateur(".");
      *logptr(contextptr) << gettext("Sample adequation to ") << loi <<gettext(", Chi2 test result ") << res << gettext(",\nreject adequation if superior to chisquare_icdf(") << dof << ",0.95)=" <<  chisquare_icdf(dof,0.95,contextptr) << " or chisquare_icdf(" << dof <<",1-alpha) if alpha!=5%" << endl;	
      return res;
    }
    return undef;
  }
  static const char _chisquaret_s []="chisquaret";
  static define_unary_function_eval (__chisquaret,&_chisquaret,_chisquaret_s);
  define_unary_function_ptr5( at_chisquaret ,alias_at_chisquaret,&__chisquaret,0,true);

  // normalt arguments: 
  // arg1 = 1/ [x,n] number of success, number of trials
  //      = 2/ [mu1,n] mean, sample size
  //      = 3/ data (list of values)
  // arg2 = 1/ p proportion
  //      = 2, 3/ mu population mean, or data
  // arg3 optionnal for 2, 3/: sigma. If not given and 
  //        arg1=[int,int] and arg2=p in ]0,1[, sigma is derived from p
  // nextarg < > or != alternative hypothesis 
  // nextarg optional alpha level of confidence, default value 0.05
  gen zttest(const gen & g,bool ztest,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()<3 || g._VECTptr->front().type!=_VECT)
      return gensizeerr(contextptr);
    vecteur v =*g._VECTptr;
    vecteur v0=*v[0]._VECTptr;
    gen MU1=evalf_double(v[1],1,contextptr),test=undef;
    double mu0,mu1,alpha=0.05,sigma=1;
    double n0,n1=0; // sample size for data0 and data1, 0 for data1 if not a sample
    int dof=0;
    if (v0.size()<2)
      return gensizeerr(contextptr);
    bool proportion=false,data0=false,data1=MU1.type==_VECT;
    if (data1){
      n1=double(MU1._VECTptr->size());
      dof = int(n1)-1; // mean estimated from data
      gen tmp=_mean(MU1,contextptr);
      if (tmp.type!=_DOUBLE_)
	return gensizeerr(contextptr);
      mu1=tmp._DOUBLE_val;
      *logptr(contextptr) << gettext("Estimated mean from sample(s)") << mu1 << endl;
    }
    else {
      if (MU1.type!=_DOUBLE_)
	return gensizeerr(contextptr);
      mu1=MU1._DOUBLE_val;
    }
    if ( (n0=double(v0.size()))==2 && is_integer(v0[1])){
      gen v00=evalf_double(v0[0],1,contextptr);
      if (v00.type!=_DOUBLE_)
	return gensizeerr(contextptr);
      // arg1 =[mu1,n] or [x,n]
      n0=evalf_double(v0[1],1,contextptr)._DOUBLE_val;
      dof += int(n0);
      proportion=ztest && is_integer(v0[0]) && MU1.type==_DOUBLE_ && MU1._DOUBLE_val>0 && MU1._DOUBLE_val<1 && v[2].type==_FUNC;
      if (proportion)
	mu0=v00._DOUBLE_val/n0;
      else 
	mu0=v00._DOUBLE_val;
    }
    else {
      dof += int(n0);
      data0=true;
      gen tmp=evalf_double(_mean(v0,contextptr),1,contextptr);
      if (tmp.type!=_DOUBLE_)
	return gensizeerr(contextptr);
      mu0=tmp._DOUBLE_val;
    }
    if (v[2].type==_FUNC){
      // estimate sigma value from data
      --dof;
      gen S=0;
      if (data0)
	S=n0*_variance(v[0],contextptr);
      if (data1)
	S += n1*_variance(v[1],contextptr);
      S=evalf_double(S/dof,1,contextptr);
      if (S.type!=_DOUBLE_ || S._DOUBLE_val<=0)
	return gensizeerr(gettext("Unable to guess sigma using data"));
      sigma=std::sqrt(S._DOUBLE_val);
      *logptr(contextptr) << gettext("Estimated sigma from sample(s)") << sigma << endl;
      if (v.size()>4)
	return gendimerr(contextptr);
      if (v.size()==4){
	gen tmp=evalf_double(v[3],1,contextptr);
	if (tmp.type!=_DOUBLE_ || tmp._DOUBLE_val<=0 || tmp._DOUBLE_val>=1)
	  return gensizeerr(contextptr);
	alpha=tmp._DOUBLE_val;
      }
      test=v[2];
    }
    else {
      if (v.size()<4)
	return gendimerr(contextptr);
      gen tmp=evalf_double(v[2],1,contextptr);
      if (tmp.type!=_DOUBLE_ || tmp._DOUBLE_val<=0)
	return gensizeerr(contextptr);
      sigma=tmp._DOUBLE_val;
      test=v[3];
      if (v.size()==5){
	tmp=evalf_double(v[4],1,contextptr);
	if (tmp.type!=_DOUBLE_ || tmp._DOUBLE_val<=0 || tmp._DOUBLE_val>=1)
	  return gensizeerr(contextptr);
	alpha=tmp._DOUBLE_val;
      }
    }
    if (test==at_inferieur_strict)
      test=-1;
    if (test==at_superieur_strict)
      test=1;
    if (test==at_different)
      test=0;
    if (test!=0 && test!=-1 && test!=1)
      return gensizeerr(gettext("Bad alternative hypothesis, should be '>', '<' or '!='"));
    // Ready to test: compare mu0 to mu1-f(alpha)*sigma
    gen Falpha;
    if (test==0)
      Falpha=ztest?_normal_icdf(makesequence(0,1,1-alpha/2),contextptr):_student_icdf(makesequence(dof,1-alpha),contextptr);
    else
      Falpha=ztest?_normal_icdf(makesequence(0,1,1-alpha),contextptr):_student_icdf(makesequence(dof,1-alpha),contextptr);
    double falpha=evalf_double(Falpha,1,contextptr)._DOUBLE_val;
    bool ok=true;
    double sqrtn0=std::sqrt(n0);
    if (test==-1){
      if (mu0<mu1-sigma*falpha/sqrtn0)
	ok=false;
    }
    if (test==0){
      // mu0 should be < mu1 for test to fail
      if (mu0<mu1-sigma*falpha/sqrtn0 || mu0>mu1+sigma*falpha/sqrtn0)
	ok=false;
    }
    if (test==1){
      if (mu0>mu1+sigma*falpha/sqrtn0)
	ok=false;
    }
    *logptr(contextptr) << gettext("*** TEST RESULT ") << (ok?"1 ***":"0 ***") << endl << "Summary " << (ztest?"Z-Test":"T-Test") << " null hypothesis H0 " << (proportion?"p1=p2":"mu1=mu2") << ", alt. hyp. H1 mu1" << (test==-1? "<":(test==0?"!=":">")) <<  "mu2." << endl;
    *logptr(contextptr) << gettext("Test returns 0 if probability to observe data is less than ") << alpha << endl;
    *logptr(contextptr) << gettext("(null hyp. mu1=mu2 rejected with less than alpha probability error)") << endl;
    *logptr(contextptr) << gettext("Test returns 1 otherwise (can not reject null hypothesis)") << endl;
    *logptr(contextptr) << gettext("Data mean mu1=") << mu0 << gettext(", population mean mu2=") << mu1 ;
    if (!ztest)
      *logptr(contextptr) << gettext(", degrees of freedom ") << dof;
    *logptr(contextptr) << endl << "alpha level " << alpha << ", multiplier*stddev/sqrt(sample size)= " << falpha << "*" << sigma << "/" << sqrtn0 << endl;
    return (ok?1:0);
  }
  gen _normalt(const gen & g,GIAC_CONTEXT){
    return zttest(g,true,contextptr);
  }
  static const char _normalt_s []="normalt";
  static define_unary_function_eval (__normalt,&_normalt,_normalt_s);
  define_unary_function_ptr5( at_normalt ,alias_at_normalt,&__normalt,0,true);

  gen _studentt(const gen & g,GIAC_CONTEXT){
    return zttest(g,false,contextptr);
  }
  static const char _studentt_s []="studentt";
  static define_unary_function_eval (__studentt,&_studentt,_studentt_s);
  define_unary_function_ptr5( at_studentt ,alias_at_studentt,&__studentt,0,true);

  // return 0 if not distrib
  // 1 normal, 2 binomial, 3 negbinomial, 4 poisson, 5 student, 
  // 6 fisher, 7 cauchy, 8 weibull, 9 betad, 10 gammad, 11 chisquare
  // 12 geometric, 13 uniformd, 14 exponentiald
  int is_distribution(const gen & args){
    if (args.type==_SYMB && args._SYMBptr->sommet!=at_exp){
      int res=is_distribution(args._SYMBptr->sommet) ;
      if (!res)
	return res;
      int s=distrib_nargs(res);
      if (s!=int(gen2vecteur(args._SYMBptr->feuille).size()))
	return 0;
      return res;
    }
    if (args.type==_FUNC){
      if (args==at_normald || args==at_NORMALD)
	return 1;
      if (args==at_binomial || args==at_BINOMIAL)
	return 2;
      if (args==at_negbinomial)
	return 3;
      if (args==at_poisson || args==at_POISSON)
	return 4;
      if (args==at_student || args==at_studentd)
	return 5;
      if (args==at_fisher || args==at_fisherd || args==at_snedecor)
	return 6;
      if (args==at_cauchy || args==at_cauchyd)
	return 7;
      if (args==at_weibull || args==at_weibulld)
	return 8;
      if (args==at_betad)
	return 9;
      if (args==at_gammad)
	return 10;
      if (args==at_chisquared || args==at_chisquare)
	return 11;
      if (args==at_geometric)
	return 12;
      if (args==at_uniform || args==at_uniformd)
	return 13;
      if (args==at_exp || args==at_exponential || args==at_exponentiald)
	return 14;
    }
    return 0;
  }

  gen distribution(int nd){
    static vecteur d_static(makevecteur(at_normald,at_binomial,at_negbinomial,at_poisson,at_studentd,at_fisherd,at_cauchyd,at_weibulld,at_betad,at_gammad,at_chisquared,at_geometric,at_uniformd,at_exponentiald));
    if (nd<=0 || nd>int(d_static.size()))
      return undef;
    return d_static[nd-1];
  }

  int distrib_nargs(int nd){
    switch (nd){
    case 4: case 5: case 11: case 12: case 14:
      return 1;
    case 8:
      return 3;
    default:
      return 2;
    }
  }

  gen _mgf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_SYMB){
      vecteur v(gen2vecteur(g._SYMBptr->feuille));
      v.insert(v.begin(),g._SYMBptr->sommet);
      return _mgf(v,contextptr);
    }
    int nd;
    if (g.type!=_VECT || g._VECTptr->empty() || !(nd=is_distribution(g._VECTptr->front())) )
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=int(v.size());
    if (s!=distrib_nargs(nd)+1)
      return gensizeerr(contextptr);
    gen t(identificateur("t"));
    if (nd==1)
      return exp(v[1]*t+plus_one_half*pow(v[2],2,contextptr)*pow(t,2,contextptr),contextptr);
    if (nd==2)
      return pow((1-v[2])+v[2]*exp(t,contextptr),v[1],contextptr);
    if (nd==3)
      return pow(v[2]/(1-(1-v[2])*exp(t,contextptr)),v[1],contextptr);
    if (nd==4)
      return exp(v[1]*(exp(t,contextptr)-1),contextptr);
    if (nd==10)
      return pow(1-t/v[2],-v[1],contextptr);
    if (nd==11)
      return pow(1-2*t,-v[1]/2,contextptr);
    if (nd==12)
      return v[1]*exp(t,contextptr)/(1-(1-v[1])*exp(t,contextptr));
    if (nd==13)
      return (exp(t*v[2],contextptr)-exp(t*v[1],contextptr))/(t*(v[2]-v[1]));
    if (nd==14)
      return inv(1-t/v[1],contextptr);
    return undef;
  }
  static const char _mgf_s []="mgf";
  static define_unary_function_eval (__mgf,&_mgf,_mgf_s);
  define_unary_function_ptr5( at_mgf ,alias_at_mgf,&__mgf,0,true);

  gen icdf(int n){
    static vecteur icdf_static(makevecteur(at_normald_icdf,at_binomial_icdf,undef,at_poisson_icdf,at_studentd_icdf,at_fisherd_icdf,at_cauchyd_icdf,at_weibulld_icdf,at_betad_icdf,at_gammad_icdf,at_chisquared_icdf,at_geometric_icdf,at_uniformd_icdf,at_exponentiald_icdf));
    if (n<=0 || n>int(icdf_static.size()))
      return undef;
    return icdf_static[n-1];
  }

  gen cdf(int n){
    static vecteur cdf_static(makevecteur(at_normald_cdf,at_binomial_cdf,undef,at_poisson_cdf,at_studentd_cdf,at_fisherd_cdf,at_cauchyd_cdf,at_weibulld_cdf,at_betad_cdf,at_gammad_cdf,at_chisquared_cdf,at_geometric_cdf,at_uniformd_cdf,at_exponentiald_cdf));
    if (n<=0 || n>int(cdf_static.size()))
      return undef;
    return cdf_static[n-1];
  }

  // set a and b to the boundaries of the support of distrib number nd
  // truncate infinities to 100 if truncate is true
  bool distrib_support(int nd,gen & a,gen &b,bool truncate){
    a=truncate?gnuplot_xmin:minus_inf;
    b=truncate?gnuplot_xmax:plus_inf;
    if (nd==2 || nd==3 || nd==4 || nd==9 || nd==10 || nd==11 || nd==14)
      a=0;
    if (nd==9)
      b=1;
    if (nd==12){
      a=1;
      b=10;
    }
    if (nd==2 || nd==3 || nd==4 || nd==12)
      return false;
    return true;
  }

  gen _cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    int nd;
    if (g.type!=_VECT || g._VECTptr->empty())
      return gensizeerr(contextptr);
    vecteur w=*g._VECTptr;
    int s=int(w.size());
    if (!(nd=is_distribution(w.front()))){
      if (g.subtype!=_SEQ__VECT){
	w=gen2vecteur(_sort(g,contextptr));
	s=int(w.size());
	vecteur res;
	for (int i=0;i<s;++i){
	  res.push_back(makevecteur(w[i],gen(i)/gen(s)));
	}
	return res;
      }
      vector<giac_double> D;
      gen w0=evalf_double(w[0],1,contextptr);
      if (s==2 && ckmatrix(w0)){ // list of classes/effectifs
	matrice m=*w0._VECTptr;
	if (m.empty()) return gensizeerr(contextptr);
	if (m.front()._VECTptr->size()!=2)
	  m=mtran(m);
	if (m.front()._VECTptr->size()!=2)
	  return gensizeerr(contextptr);
	gen_sort_f(m.begin(),m.end(),first_ascend_sort);
	s=int(m.size());
	double tot=0,cur=0;
	for (int i=0;i<s;++i){
	  if (m[i][1].type!=_DOUBLE_)
	    return gensizeerr(contextptr);
	  tot += m[i][1]._DOUBLE_val;
	}
	if (w[1]!=at_plot){
	  w[1]=evalf_double(w[1],1,contextptr);
	  if (w[1].type!=_DOUBLE_ || w[1]._DOUBLE_val<=0 || w[1]._DOUBLE_val>=1)
	    return gensizeerr(contextptr);
	  tot *= w[1]._DOUBLE_val;
	  for (int i=0;i<s;++i){
	    cur += m[i][1]._DOUBLE_val;
	    if (cur>=tot)
	      return m[i][0];
	  }
	}
	// plot cdf for frequencies
	vecteur res; res.reserve(2*s);
	for (int i=0;i<s;++i){
	  gen mi0=m[i][0];
	  if (mi0.is_symb_of_sommet(at_interval) && mi0._SYMBptr->feuille.type==_VECT && mi0._SYMBptr->feuille._VECTptr->size()==2)
	    mi0=(mi0._SYMBptr->feuille._VECTptr->front()+mi0._SYMBptr->feuille._VECTptr->back())/2;
	  res.push_back(mi0+(cur/tot)*cst_i);
	  cur += m[i][1]._DOUBLE_val;
	  res.push_back(mi0+(cur/tot)*cst_i);
	}
	return _polygone_ouvert(gen(res,_SEQ__VECT),contextptr);	  
      }
      if (s!=2 || w0.type!=_VECT || !convert(*w0._VECTptr,D,false))
	return gensizeerr(contextptr);
      sort(D.begin(),D.end());
      s=int(D.size());
      if (w[1]!=at_plot){
	gen tmp=evalf_double(w[1],1,contextptr);
	if (tmp.type!=_DOUBLE_)
	  return gensizeerr(contextptr);
	return gen(dichotomy(D,tmp._DOUBLE_val))/gen(s);
      }
      vecteur res;
      for (int i=0;i<s;++i){
	res.push_back(double(D[i])+i/double(s)*cst_i);
	res.push_back(double(D[i])+(i+1)/double(s)*cst_i);	
      }
      return _polygone_ouvert(gen(res,_SEQ__VECT),contextptr);
    }
    if (s && w.front().type==_SYMB){
      vecteur v(gen2vecteur(w.front()._SYMBptr->feuille));
      v.insert(v.begin(),w.front()._SYMBptr->sommet);
      for (unsigned j=1;j<w.size();++j)
	v.push_back(w[j]);
      return _cdf(v,contextptr);
    }
    bool plot=false;
    if (w.back()==at_plot || w.back()==at_plotfunc){
      if (nd==13){
	gen a=w[1],b=w[2];
	return _segment(makesequence(a,b+cst_i),contextptr);
      }
      w.back()=vx_var;
      plot=true;
    }
    if (s==distrib_nargs(nd)+1){
      w.push_back(vx_var);
      ++s;
    }
    if (s!=distrib_nargs(nd)+2)
      return gensizeerr(contextptr);
    gen res=gen(vecteur(w.begin()+1,w.end()),_SEQ__VECT);
    res=cdf(nd)(res,contextptr);
    if (plot){
      gen a,b;
      if (distrib_support(nd,a,b,true)) // true if density
	return _plotfunc(makesequence(res,symb_equal(vx_var,symb_interval(a,b))),contextptr);
      if (nd==2) // binomial
	b=w[1];
      if (a.type!=_INT_ || !is_integral(b) || b.type!=_INT_ || b.val<=0)
	return gensizeerr(contextptr);
      int A=a.val,B=b.val;
      vecteur v;
      for (int i=A;i<B;++i){
	gen y=subst(res,vx_var,i,false,contextptr);
	v.push_back(i+cst_i*y);
	v.push_back(i+1+cst_i*y);
      }
      gen y=subst(res,vx_var,B,false,contextptr);
      v.push_back(B+cst_i*y);
      return _polygone_ouvert(gen(v,_SEQ__VECT),contextptr);
    }
    return res;
  }
  static const char _cdf_s []="cdf";
  static define_unary_function_eval (__cdf,&_cdf,_cdf_s);
  define_unary_function_ptr5( at_cdf ,alias_at_cdf,&__cdf,0,true);

  gen _plotcdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    vecteur v(makevecteur(g,at_plot));
    if (g.type==_VECT && g.subtype==_SEQ__VECT){
      v=*g._VECTptr;
      v.push_back(at_plot);
    }
    return _cdf(gen(v,_SEQ__VECT),contextptr);
  }
  static const char _plotcdf_s []="plotcdf";
  static define_unary_function_eval (__plotcdf,&_plotcdf,_plotcdf_s);
  define_unary_function_ptr5( at_plotcdf ,alias_at_plotcdf,&__plotcdf,0,true);

  static const char _cdfplot_s []="cdfplot";
  static define_unary_function_eval (__cdfplot,&_plotcdf,_cdfplot_s);
  define_unary_function_ptr5( at_cdfplot ,alias_at_cdfplot,&__cdfplot,0,true);

  gen _icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    int nd;
    if (g.type!=_VECT || g._VECTptr->empty())
      return gensizeerr(contextptr);
    vecteur w=*g._VECTptr;
    int s=int(w.size());
    if (!(nd=is_distribution(w.front()))){
      if (g.subtype!=_SEQ__VECT || s!=2)
	return gensizeerr(contextptr);
      if (w[1]==at_plot)
	return _symetrie(makesequence(_droite(makesequence(0,1+cst_i),contextptr),_cdf(g,contextptr)),contextptr);
      return _quantile(g,contextptr);
    }
    if (s && w.front().type==_SYMB){
      vecteur v(gen2vecteur(w.front()._SYMBptr->feuille));
      v.insert(v.begin(),w.front()._SYMBptr->sommet);
      for (unsigned j=1;j<w.size();++j)
	v.push_back(w[j]);
      return _icdf(v,contextptr);
    }
    bool plot=false;
    if (w.back()==at_plot || w.back()==at_plotfunc){
      if (nd==13){
	gen a=w[1],b=w[2];
	return _segment(makesequence(cst_i*a,1+cst_i*b),contextptr);
      }
      w.back()=vx_var;
      plot=true;
    }
    if (s==distrib_nargs(nd)+1){
      w.push_back(vx_var);
      ++s;
    }
    if (s!=distrib_nargs(nd)+2)
      return gensizeerr(contextptr);
    gen res=gen(vecteur(w.begin()+1,w.end()),_SEQ__VECT);
    res=icdf(nd)(res,contextptr);
    if (plot){
      gen a,b;
      if (distrib_support(nd,a,b,true)) // true if density
	return _plotfunc(makesequence(res,symb_equal(vx_var,symb_interval(0,1))),contextptr);
      return _symetrie(makesequence(_droite(makesequence(0,1+cst_i),contextptr),_cdf(g,contextptr)),contextptr);
    }
    return res;
  }
  static const char _icdf_s []="icdf";
  static define_unary_function_eval (__icdf,&_icdf,_icdf_s);
  define_unary_function_ptr5( at_icdf ,alias_at_icdf,&__icdf,0,true);


  // kind=0: BesselI, =1 BesselJ, =2 BesselK, =3 BesselY
  gen Bessel(const gen & g,int kind,GIAC_CONTEXT){
#ifdef BESTA_OS
    return gensizeerr(gettext("Bessel not implemented"));
#endif
    int n;
    gen a,x;
    if (!find_n_x(g,n,x,a))
      return gensizeerr(contextptr);
    if (has_evalf(x,a,1,contextptr) && a.type==_DOUBLE_){
      double d=a._DOUBLE_val;
      switch (kind){
      case 1:
	if (n==0) return j0(d);
	if (n==1) return j1(d);
	return jn(n,d);
      case 3:
	if (n==0) return y0(d);
	if (n==1) return y1(d);
	return yn(n,d);
      }
    }
    gen gn=gen(makevecteur(n,x),_SEQ__VECT);
    switch (kind){
    case 0:
      return symbolic(at_BesselI,gn);
    case 1:
      return symbolic(at_BesselJ,gn);
    case 2:
      return symbolic(at_BesselK,gn);
    case 3:
      return symbolic(at_BesselY,gn);
    }
    return gensizeerr(gettext("Bessel"));
  }
  gen bessel(const gen & g,int kind,GIAC_CONTEXT){
    if (g.type==_VECT && g._VECTptr->size()>=2)
      return Bessel(makesequence(g[1],g[0]),kind,contextptr);
    return gensizeerr(contextptr);
  }

  // numerical eval BesselJ(n,x) and BesselY(n,x) are implemented for x.type==_DOUBLE_
  gen _BesselI(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return Bessel(g,0,contextptr);
  }
  static const char _BesselI_s []="BesselI";
  static define_unary_function_eval (__BesselI,&_BesselI,_BesselI_s);
  define_unary_function_ptr5( at_BesselI ,alias_at_BesselI,&__BesselI,0,true);

  gen _BesselJ(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return Bessel(g,1,contextptr);
  }
  static const char _BesselJ_s []="BesselJ";
  static define_unary_function_eval (__BesselJ,&_BesselJ,_BesselJ_s);
  define_unary_function_ptr5( at_BesselJ ,alias_at_BesselJ,&__BesselJ,0,true);

  gen _BesselK(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return Bessel(g,2,contextptr);
  }
  static const char _BesselK_s []="BesselK";
  static define_unary_function_eval (__BesselK,&_BesselK,_BesselK_s);
  define_unary_function_ptr5( at_BesselK ,alias_at_BesselK,&__BesselK,0,true);

  gen _BesselY(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return Bessel(g,3,contextptr);
  }
  static const char _BesselY_s []="BesselY";
  static define_unary_function_eval (__BesselY,&_BesselY,_BesselY_s);
  define_unary_function_ptr5( at_BesselY ,alias_at_BesselY,&__BesselY,0,true);

  gen _besselI(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return bessel(g,0,contextptr);
  }
  static const char _besselI_s []="besselI";
  static define_unary_function_eval (__besselI,&_besselI,_besselI_s);
  define_unary_function_ptr5( at_besselI ,alias_at_besselI,&__besselI,0,true);

  gen _besselJ(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return bessel(g,1,contextptr);
  }
  static const char _besselJ_s []="besselJ";
  static define_unary_function_eval (__besselJ,&_besselJ,_besselJ_s);
  define_unary_function_ptr5( at_besselJ ,alias_at_besselJ,&__besselJ,0,true);

  gen _besselK(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return bessel(g,2,contextptr);
  }
  static const char _besselK_s []="besselK";
  static define_unary_function_eval (__besselK,&_besselK,_besselK_s);
  define_unary_function_ptr5( at_besselK ,alias_at_besselK,&__besselK,0,true);

  gen _besselY(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    return bessel(g,3,contextptr);
  }
  static const char _besselY_s []="besselY";
  static define_unary_function_eval (__besselY,&_besselY,_besselY_s);
  define_unary_function_ptr5( at_besselY ,alias_at_besselY,&__besselY,0,true);

  // sum_{k=1}^n 1/k^e
  gen _harmonic(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    gen e=1;
    gen n=g;
    if (g.type==_VECT && g.subtype==_SEQ__VECT && g._VECTptr->size()==2){
      e=g._VECTptr->front();
      n=g._VECTptr->back();
    }
    if (n==plus_inf)
      return Zeta(e,contextptr);
    if (!is_integral(n))
      return symbolic(at_harmonic,g);
    if (is_greater(0,n,contextptr) || is_greater(n,1e7,contextptr))
      return gendimerr(contextptr);
    gen res=1;
    for (int k=2;k<=n.val;++k){
      res += 1/pow(gen(k),e,contextptr);
    }
    return res;
  }
  static const char _harmonic_s []="harmonic";
  static define_unary_function_eval (__harmonic,&_harmonic,_harmonic_s);
  define_unary_function_ptr5( at_harmonic ,alias_at_harmonic,&__harmonic,0,true);

#include "input_parser.h"

  gen _constants_catalog(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_STRNG)
      return undef;
    const string & gs=*g._STRNGptr;
    gen G;
    if (gs=="black"){
      G.val=_BLACK;
      G.subtype=_INT_COLOR;
      return G;
    }
    if (gs=="white"){
      G.val=_WHITE;
      G.subtype=_INT_COLOR;
      return G;
    }
    if (gs=="red"){
      G.val=_RED;
      G.subtype=_INT_COLOR;
      return G;
    }
    if (gs=="green"){
      G.val=_GREEN;
      G.subtype=_INT_COLOR;
      return G;
    }
    if (gs=="blue"){
      G.val=_BLUE;
      G.subtype=_INT_COLOR;
      return G;
    }
    if (gs=="yellow"){
      G.val=_YELLOW;
      G.subtype=_INT_COLOR;
      return G;
    }
    if (gs=="magenta"){
      G.val=_MAGENTA;
      G.subtype=_INT_COLOR;
      return G;
    }
    if (gs=="cyan"){
      G.val=_CYAN;
      G.subtype=_INT_COLOR;
      return G;
    }
    if (gs=="filled"){
      G.val=_FILL_POLYGON;
      G.subtype=_INT_COLOR;
      return G;
    }
    if (gs=="hidden_name"){
      G.val=_HIDDEN_NAME;
      G.subtype=_INT_COLOR;
      return G;
    }
    return -1;
  }
  static const char _black_s []="black";
  static define_unary_function_eval (__black,&_constants_catalog,_black_s);
  define_unary_function_ptr5( at_black ,alias_at_black,&__black,0,T_NUMBER);

  static const char _white_s []="white";
  static define_unary_function_eval (__white,&_constants_catalog,_white_s);
  define_unary_function_ptr5( at_white ,alias_at_white,&__white,0,T_NUMBER);

  static const char _red_s []="red";
  static define_unary_function_eval (__red,&_constants_catalog,_red_s);
  define_unary_function_ptr5( at_red ,alias_at_red,&__red,0,T_NUMBER);

  static const char _green_s []="green";
  static define_unary_function_eval (__green,&_constants_catalog,_green_s);
  define_unary_function_ptr5( at_green ,alias_at_green,&__green,0,T_NUMBER);

  static const char _blue_s []="blue";
  static define_unary_function_eval (__blue,&_constants_catalog,_blue_s);
  define_unary_function_ptr5( at_blue ,alias_at_blue,&__blue,0,T_NUMBER);

  static const char _cyan_s []="cyan";
  static define_unary_function_eval (__cyan,&_constants_catalog,_cyan_s);
  define_unary_function_ptr5( at_cyan ,alias_at_cyan,&__cyan,0,T_NUMBER);

  static const char _yellow_s []="yellow";
  static define_unary_function_eval (__yellow,&_constants_catalog,_yellow_s);
  define_unary_function_ptr5( at_yellow ,alias_at_yellow,&__yellow,0,T_NUMBER);

  static const char _magenta_s []="magenta";
  static define_unary_function_eval (__magenta,&_constants_catalog,_magenta_s);
  define_unary_function_ptr5( at_magenta ,alias_at_magenta,&__magenta,0,T_NUMBER);

  static const char _filled_s []="filled";
  static define_unary_function_eval (__filled,&_constants_catalog,_filled_s);
  define_unary_function_ptr5( at_filled ,alias_at_filled,&__filled,0,T_NUMBER);

  static const char _hidden_name_s []="hidden_name";
  static define_unary_function_eval (__hidden_name,&_constants_catalog,_hidden_name_s);
  define_unary_function_ptr5( at_hidden_name ,alias_at_hidden_name,&__hidden_name,0,T_NUMBER);


#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
