// -*- mode:C++ ; compile-command: "g++-3.4 -DHAVE_CONFIG_H -I. -I.. -DIN_GIAC -g -c moyal.cc" -*-
#include "giacPCH.h"
/*
 *  Copyright (C) 2000,2007 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
    int s=args._VECTptr->size();
    if (s!=4) return gensizeerr(gettext("moyal.cc/_moyal"));
    return moyal( (*(args._VECTptr))[0],(*(args._VECTptr))[1],(*(args._VECTptr))[2],(*(args._VECTptr))[3]);
  }
  static const char _moyal_s  []="moyal";
  static string texprintasmoyal(const gen & g,const char * s,GIAC_CONTEXT){
    return texprintsommetasoperator(g,"#",contextptr);
  }
  static define_unary_function_eval4 (__moyal,&_moyal,_moyal_s,0,&texprintasmoyal);
  define_unary_function_ptr5( at_moyal ,alias_at_moyal,&__moyal,0,true);

  gen lower_incomplete_gamma(double s,double z,bool regularize){ // regularize=true by default
    // should be fixed if z is large using upper_incomplete_gamma asymptotics
    if (z>0 && -z+s*std::log(z)-lngamma(s+1)<-37)
      return regularize?1:std::exp(lngamma(s));
    // gamma(s,z) = int(t^s*e^(-t),t=0..z)
    // Continued fraction expansion: a1/(b1+a2/(b2+...)))
    // here a1=1, a2=-s*z, a3=z, then a_{2m}=a_{2m-2}-z and a_{2m+1}=a_{2m-1}+z
    // b1=s, b_{n}=s+n-1
    // P0=0, P1=a1, Q0=1, Q1=b1
    // j>=2: Pj=bj*Pj-1+aj*Pj-2, Qj=bj*Qj-1+aj*Qj-2
    // Here bm=1, am=em, etc.
    long_double Pm2=0,Pm1=1,Pm,Qm2=1,Qm1=s,Qm,a2m=-(s-1)*z,a2m1=0,bm=s;
    for (long_double m=1;m<100;++m){
      // even term
      a2m -= z;
      bm++;
      Pm=bm*Pm1+a2m*Pm2;
      Qm=bm*Qm1+a2m*Qm2;
      Pm2=Pm1; Pm1=Pm;
      Qm2=Qm1; Qm1=Qm;
      // odd term
      a2m1 +=z;
      bm++;
      Pm=bm*Pm1+a2m1*Pm2;
      Qm=bm*Qm1+a2m1*Qm2;
      // cerr << Pm/Qm << " " << Pm2/Qm2 << endl;
      if (std::abs(Pm/Qm-Pm2/Qm2)<1e-16){
	double res=Pm/Qm;
	if (regularize)
	  res *= std::exp(-z+s*std::log(z)-lngamma(s));
	else
	  res *= std::exp(-z+s*std::log(z));
	return res;
      }	
      Pm2=Pm1; Pm1=Pm;
      Qm2=Qm1; Qm1=Qm;
      // normalize
      Pm=1/std::sqrt(Pm1*Pm1+Qm1*Qm1);
      Pm2 *= Pm; Qm2 *= Pm; Pm1 *= Pm; Qm1 *= Pm;
    }
    return undef; //error
  }

  gen upper_incomplete_gamma(double s,double z,bool regularize){ 
    gen l=lower_incomplete_gamma(s,z,true);
    l=1-l;
    if (regularize)
      return l;
    return l*Gamma(s,context0);
  }

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
      if (std::abs(Pm/Qm-Pm2/Qm2)<1e-16){
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
    }
    return undef; //error
  }

  gen Beta(const gen & a,const gen& b,GIAC_CONTEXT){
    if (a.type==_DOUBLE_ || b.type==_DOUBLE_ ||
	a.type==_FLOAT_ || b.type==_FLOAT_ ||
	a.type==_CPLX || b.type==_CPLX ){
      gen A=evalf_double(a,1,contextptr);
      gen B=evalf_double(b,1,contextptr);
      return exp(lngamma(A,contextptr)+lngamma(B,contextptr)-lngamma(A+B,contextptr),contextptr);
    }
    return Gamma(a,contextptr)*Gamma(b,contextptr)/Gamma(a+b,contextptr);
  }
  gen _Beta(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_Beta,args);
    vecteur v=*args._VECTptr;
    int s=v.size();
    if (s>2 && (v[0].type==_DOUBLE_ || v[1].type==_DOUBLE_ || v[2].type==_DOUBLE_)){
      gen tmp=evalf_double(v,1,contextptr);
      if (tmp.type==_VECT)
	v=*tmp._VECTptr;
      s=v.size();
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

  gen _lower_incomplete_gamma(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_lower_incomplete_gamma,args);
    vecteur v=*args._VECTptr;
    int s=v.size();
    if ( (s==2 || s==3) && v[0].type==_DOUBLE_ && v[1].type==_DOUBLE_ )
      return lower_incomplete_gamma(v[0]._DOUBLE_val,v[1]._DOUBLE_val,s==3?!is_zero(v[2]):false);
    if (s<2 || s>3)
      return gendimerr(contextptr);
    if (s==3){
      if (is_zero(v[2]))
	return symbolic(at_lower_incomplete_gamma,makesequence(v[0],v[1]));
      return symbolic(at_lower_incomplete_gamma,makesequence(v[0],v[1]))/Gamma(v[0],contextptr);
    }
    return symbolic(at_lower_incomplete_gamma,args);
  }
  static const char _lower_incomplete_gamma_s []="igamma"; // "lower_incomplete_gamma"
  static define_unary_function_eval (__lower_incomplete_gamma,&_lower_incomplete_gamma,_lower_incomplete_gamma_s);
  define_unary_function_ptr5( at_lower_incomplete_gamma ,alias_at_lower_incomplete_gamma,&__lower_incomplete_gamma,0,true);

  gen _upper_incomplete_gamma(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_upper_incomplete_gamma,args);
    vecteur v=*args._VECTptr;
    int s=v.size();
    if ( (s==2 || s==3) && v[0].type==_DOUBLE_ && v[1].type==_DOUBLE_ )
      return upper_incomplete_gamma(v[0]._DOUBLE_val,v[1]._DOUBLE_val,s==3?!is_zero(v[2]):false);
    if (s<2 || s>3)
      return gendimerr(contextptr);
    if (s==3){
      if (is_zero(v[2]))
	return Gamma(v[0],contextptr)-symbolic(at_lower_incomplete_gamma,makesequence(v[0],v[1]));
      return 1-symbolic(at_lower_incomplete_gamma,makesequence(v[0],v[1]))/Gamma(v[0],contextptr);
    }
    return Gamma(v[0],contextptr)-symbolic(at_upper_incomplete_gamma,args);
  }
  static const char _upper_incomplete_gamma_s []="upper_incomplete_gamma";
  static define_unary_function_eval (__upper_incomplete_gamma,&_upper_incomplete_gamma,_upper_incomplete_gamma_s);
  define_unary_function_ptr5( at_upper_incomplete_gamma ,alias_at_upper_incomplete_gamma,&__upper_incomplete_gamma,0,true);

  gen _polygamma(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT)
      return symbolic(at_polygamma,args);
    vecteur v=*args._VECTptr;
    int s=v.size();
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
    int s=v.size();
    if (s!=3 || is_zero(v[1]))
      return gensizeerr(contextptr);
    return erfc((v[2]-v[0])/sqrt(2*v[1],contextptr),contextptr)/2;
  }
  static const char _UTPN_s []="UTPN";
  static define_unary_function_eval (__UTPN,&_UTPN,_UTPN_s);
  define_unary_function_ptr5( at_UTPN ,alias_at_UTPN,&__UTPN,0,true);

  gen randNorm(){
    /*
    double d=rand()/(RAND_MAX+1.0);
    d=2*d-1;
    identificateur x(" x");
    return newton(erf(x)-d,x,d);
    */
    double u=rand()/(RAND_MAX+1.0);
    double d=rand()/(RAND_MAX+1.0);
    return std::sqrt(-2*std::log(u))*std::cos(2*M_PI*d);
  }
  gen _randNorm(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    if (args.type!=_VECT || args._VECTptr->size()!=2)
      return gensizeerr(contextptr);
    vecteur & v=*args._VECTptr;
    return evalf(v[0]+v[1]*randNorm(),1,contextptr);
  }
  static const char _randNorm_s []="randNorm";
  static define_unary_function_eval (__randNorm,&_randNorm,_randNorm_s);
  define_unary_function_ptr5( at_randNorm ,alias_at_randNorm,&__randNorm,0,true);

  static gen normald(const gen & m,const gen & s,const gen & x,GIAC_CONTEXT){
    gen v(s*s);
    return inv(sqrt(2*cst_pi*v,contextptr),contextptr)*exp(-pow(x-m,2)/(2*v),contextptr);
  }
  gen _normald(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return normald(0,1,g,contextptr);
    vecteur & v=*g._VECTptr;
    int s=v.size();
    if (s==3)
      return normald(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _normald_s []="normald";
  static define_unary_function_eval (__normald,&_normald,_normald_s);
  define_unary_function_ptr5( at_normald ,alias_at_normald,&__normald,0,true);

  gen _randexp(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    double u=rand()/(RAND_MAX+1.0);
    return -gen(std::log(1-u))/args;
  }
  static const char _randexp_s []="randexp";
  static define_unary_function_eval (__randexp,&_randexp,_randexp_s);
  define_unary_function_ptr5( at_randexp ,alias_at_randexp,&__randexp,0,true);

  // Normal cumulative distribution function
  // proba that X<x for X following a normal distrib of mean mean and dev dev
  // arg = vector [mean,dev,x] or x alone (mean=0, dev=1)
  static gen normal_cdf(const gen & g,GIAC_CONTEXT){
    return rdiv(erf(ratnormal(plus_sqrt2_2*g),contextptr)+plus_one,2,contextptr);
  }
  gen _normal_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return normal_cdf(g,contextptr);
    vecteur & v=*g._VECTptr;
    int s=v.size();
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
    return newton(erf(x/std::sqrt(2.0),contextptr)+2*y-1,x,utpn_initial_guess(y),NEWTON_DEFAULT_ITERATION,1e-5,1e-12,contextptr);
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

  gen binomial(const gen & n,const gen & k,const gen & p,GIAC_CONTEXT){
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
    return comb(n,k,contextptr)*pow(p,k,contextptr)*pow(1-p,n-k,contextptr);
  }
  gen _binomial(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=v.size();
    if (s==2)
      return comb(v[0],v[1],contextptr);
    if (s==3)
      return binomial(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _binomial_s []="binomial";
  static define_unary_function_eval (__binomial,&_binomial,_binomial_s);
  define_unary_function_ptr5( at_binomial ,alias_at_binomial,&__binomial,0,true);

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
    int s=v.size();
    if (s==3)
      return binomial_cdf(v[0],v[1],0,v[2],contextptr);
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
    if (n.type!=_INT_ || p.type!=_DOUBLE_ || x.type!=_DOUBLE_ || x._DOUBLE_val<0 || x._DOUBLE_val>1 )
      return symbolic(at_binomial_icdf,makesequence(n,p,x));
    gen last=pow(1-p,n,contextptr);
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
    for (;k<=n.val;++k){
      if (!ck_is_strictly_greater(x,b,contextptr))
	return k-1;
      b=b+binomial(n,k,p,contextptr);
    }
    return n;
  }
  gen _binomial_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=v.size();
    if (s==3)
      return binomial_icdf(v[0],v[1],v[2],contextptr);
    if (s==4)
      return binomial_icdf(v[0],v[1],v[3],contextptr)-binomial_icdf(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _binomial_icdf_s []="binomial_icdf";
  static define_unary_function_eval (__binomial_icdf,&_binomial_icdf,_binomial_icdf_s);
  define_unary_function_ptr5( at_binomial_icdf ,alias_at_binomial_icdf,&__binomial_icdf,0,true);

  gen poisson(const gen & m,const gen & k,GIAC_CONTEXT){
    return exp(-m,contextptr)*pow(m,k,contextptr)/_factorial(k,contextptr);
  }
  gen _poisson(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=v.size();
    if (s==2)
      return poisson(v[0],v[1],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _poisson_s []="poisson";
  static define_unary_function_eval (__poisson,&_poisson,_poisson_s);
  define_unary_function_ptr5( at_poisson ,alias_at_poisson,&__poisson,0,true);

  gen poisson_cdf(const gen & n,const gen & x,GIAC_CONTEXT){
    gen fx=_floor(x,contextptr);
    if (fx.type!=_INT_ || fx.val<0)
      return gensizeerr(contextptr);
    gen res=0;
    for (int i=0;i<=fx.val;++i){
      res +=poisson(n,i,contextptr);
    }
    return res;
    //identificateur k(" k");
    //return sum(poisson(n,k,contextptr),k,0,_floor(x,contextptr),contextptr);
  }
  gen _poisson_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=v.size();
    if (s==2)
      return poisson_cdf(v[0],v[1],contextptr);
    if (s==3)
      return poisson_cdf(v[0],v[2],contextptr)-poisson_cdf(v[0],v[1],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _poisson_cdf_s []="poisson_cdf";
  static define_unary_function_eval (__poisson_cdf,&_poisson_cdf,_poisson_cdf_s);
  define_unary_function_ptr5( at_poisson_cdf ,alias_at_poisson_cdf,&__poisson_cdf,0,true);

  gen poisson_icdf(const gen & m_orig,const gen & t_orig,GIAC_CONTEXT){
    gen t=evalf_double(t_orig,1,contextptr);
    gen m=evalf_double(m_orig,1,contextptr);
    if (t.type!=_DOUBLE_ || t._DOUBLE_val<0 || t._DOUBLE_val>1
	|| m.type!=_DOUBLE_ )
      return symbolic(at_poisson_icdf,makesequence(m,t));
    if (t._DOUBLE_val==0)
      return zero;
    if (t._DOUBLE_val==1)
      return plus_inf;
    if (m._DOUBLE_val>100)
      return gensizeerr(gettext("Overflow"));
    int k=0;
    gen b;
    for (;;++k){
      b=b+poisson(m,k,contextptr);
      if (!ck_is_strictly_greater(t,b,contextptr))
	return k;
    }
    return t;
  }
  gen _poisson_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=v.size();
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
    gen n(n0);
    if (!is_integral(n) || n.val<1)
      return gensizeerr(contextptr);
    return Gamma(rdiv(n+1,2,contextptr),contextptr)/Gamma(rdiv(n,2,contextptr),contextptr)/sqrt(n*cst_pi,contextptr)*pow((1+pow(x,2)/n),-rdiv(n+1,2,contextptr),contextptr);
  }
  gen _student(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur v=*g._VECTptr;
    int s=v.size();
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
    if (dof>=100){
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
    int s=v.size();
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
    int s=v.size();
    if (s==2)
      return student_cdf(v[0],minus_inf,v[1],contextptr);
    if (s==3)
      return student_cdf(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _student_cdf_s []="student_cdf";
  static define_unary_function_eval (__student_cdf,&_student_cdf,_student_cdf_s);
  define_unary_function_ptr5( at_student_cdf ,alias_at_student_cdf,&__student_cdf,0,true);

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
    return newton(_student_cdf(makesequence(m,x),contextptr)-y,x,x0,NEWTON_DEFAULT_ITERATION,1e-5,1e-12,contextptr);
  }
  gen _student_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=v.size();
    if (s==2)
      return student_icdf(v[0],v[1],contextptr);
    if (s==3)
      return student_icdf(v[0],v[2],contextptr)-student_icdf(v[0],v[1],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _student_icdf_s []="student_icdf";
  static define_unary_function_eval (__student_icdf,&_student_icdf,_student_icdf_s);
  define_unary_function_ptr5( at_student_icdf ,alias_at_student_icdf,&__student_icdf,0,true);

  gen chisquare(const gen & n,const gen & x,GIAC_CONTEXT){
    gen n2=n/2;
    return rdiv(pow(x,n2-1,contextptr)*exp(-x/2,contextptr),Gamma(n2,contextptr)*pow(2,n2,contextptr),contextptr);
  }
  gen _chisquare(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=v.size();
    if (s==2)
      return chisquare(v[0],v[1],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _chisquare_s []="chisquare";
  static define_unary_function_eval (__chisquare,&_chisquare,_chisquare_s);
  define_unary_function_ptr5( at_chisquare ,alias_at_chisquare,&__chisquare,0,true);

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
    int s=v.size();
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
    int s=v.size();
    if (s==2)
      return chisquare_cdf(v[0],0,v[1],contextptr);
    if (s==3)
      return chisquare_cdf(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _chisquare_cdf_s []="chisquare_cdf";
  static define_unary_function_eval (__chisquare_cdf,&_chisquare_cdf,_chisquare_cdf_s);
  define_unary_function_ptr5( at_chisquare_cdf ,alias_at_chisquare_cdf,&__chisquare_cdf,0,true);

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
    return newton(1-UTPC(m,x,contextptr)-t,x,x0,NEWTON_DEFAULT_ITERATION,1e-5,1e-12,contextptr);   
  }
  gen _chisquare_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=v.size();
    if (s==2)
      return chisquare_icdf(v[0],v[1],contextptr);
    if (s==3)
      return chisquare_icdf(v[0],v[2],contextptr)-chisquare_icdf(v[0],v[1],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _chisquare_icdf_s []="chisquare_icdf";
  static define_unary_function_eval (__chisquare_icdf,&_chisquare_icdf,_chisquare_icdf_s);
  define_unary_function_ptr5( at_chisquare_icdf ,alias_at_chisquare_icdf,&__chisquare_icdf,0,true);

  gen snedecor(const gen & a,const gen & b,const gen & x,GIAC_CONTEXT){
    if (is_positive(-x,contextptr))
      return zero;
    return pow(a/b,a/2,contextptr)/Beta(a/2,b/2,contextptr) * pow(x,a/2-1,contextptr) * pow(1+a/b*x,-(a+b)/2,contextptr);
  }
  gen _snedecor(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=v.size();
    if (s==3)
      return snedecor(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _snedecor_s []="snedecor";
  static define_unary_function_eval (__snedecor,&_snedecor,_snedecor_s);
  define_unary_function_ptr5( at_snedecor ,alias_at_snedecor,&__snedecor,0,true);

  static const char _fisher_s []="fisher";
  static define_unary_function_eval (__fisher,&_snedecor,_fisher_s);
  define_unary_function_ptr5( at_fisher ,alias_at_fisher,&__fisher,0,true);

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
    int s=v.size();
    if (s!=3)
      return gensizeerr(contextptr);
    return UTPF(v[0],v[1],v[2],contextptr);
  }
  static const char _UTPF_s []="UTPF";
  static define_unary_function_eval (__UTPF,&_UTPF,_UTPF_s);
  define_unary_function_ptr5( at_UTPF ,alias_at_UTPF,&__UTPF,0,true);

  gen snedecor_cdf(const gen & ndof,const gen & ddof,const gen & x,GIAC_CONTEXT){
    gen gndf(ndof),gddf(ddof),gx(x);
    if (!is_integral(gndf) || !is_integral(gddf) || gx.type!=_DOUBLE_)
      return symbolic(at_snedecor_cdf,makesequence(ndof,ddof,x));
    return 1-UTPF(ndof,ddof,x,contextptr);
  }
  gen _snedecor_cdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=v.size();
    if (s==3)
      return snedecor_cdf(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _snedecor_cdf_s []="snedecor_cdf";
  static define_unary_function_eval (__snedecor_cdf,&_snedecor_cdf,_snedecor_cdf_s);
  define_unary_function_ptr5( at_snedecor_cdf ,alias_at_snedecor_cdf,&__snedecor_cdf,0,true);
  static const char _fisher_cdf_s []="fisher_cdf";
  static define_unary_function_eval (__fisher_cdf,&_snedecor_cdf,_fisher_cdf_s);
  define_unary_function_ptr5( at_fisher_cdf ,alias_at_fisher_cdf,&__fisher_cdf,0,true);

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
    return newton(1-UTPF(num,den,x,contextptr)-t,x,x0,NEWTON_DEFAULT_ITERATION,1e-5,1e-12,contextptr);   
  }
  gen _snedecor_icdf(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT)
      return gensizeerr(contextptr);
    vecteur & v=*g._VECTptr;
    int s=v.size();
    if (s==3)
      return snedecor_icdf(v[0],v[1],v[2],contextptr);
    if (s==4)
      return snedecor_icdf(v[0],v[1],v[3],contextptr)-snedecor_icdf(v[0],v[1],v[2],contextptr);
    return gensizeerr(contextptr);
  }
  static const char _snedecor_icdf_s []="snedecor_icdf";
  static define_unary_function_eval (__snedecor_icdf,&_snedecor_icdf,_snedecor_icdf_s);
  define_unary_function_ptr5( at_snedecor_icdf ,alias_at_snedecor_icdf,&__snedecor_icdf,0,true);

  static const char _fisher_icdf_s []="fisher_icdf";
  static define_unary_function_eval (__fisher_icdf,&_snedecor_icdf,_fisher_icdf_s);
  define_unary_function_ptr5( at_fisher_icdf ,alias_at_fisher_icdf,&__fisher_icdf,0,true);

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
    for (unsigned k=2;k<=n.val;++k){
      res += 1/pow(gen(int(k)),e,contextptr);
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
