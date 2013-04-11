/* -*- mode:C++ ; compile-command: "g++-3.4 -I.. -I../include -g -c threaded.cc -DHAVE_CONFIG_H -DIN_GIAC  -D_I386_" -*- */
#include "giacPCH.h"
/*  Copyright (C) 2000,2007 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#include "threaded.h"
#include "sym2poly.h"
#include "gausspol.h"
#include "usual.h"
#include "monomial.h"
#include "modpoly.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

#ifdef HAVE_GMPXX_H
mpz_class invmod(const mpz_class & a,int reduce){
  mpz_class z=(a%reduce);
  int tmp=z.get_si();
  tmp=giac::invmod(tmp,reduce);
  return tmp;
}

mpz_class smod(const mpz_class & a,int reduce){
  mpz_class z=(a%reduce);
  int tmp=z.get_si();
  tmp=giac::smod(tmp,reduce);
  return tmp;
}
#endif

  double heap_mult=20000;
  gen _heap_mult(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g=evalf_double(g0,1,contextptr);
    if (g.type!=_DOUBLE_)
      return heap_mult;
    return heap_mult=g._DOUBLE_val;
  }
  static const char _heap_mult_s []="heap_mult";
  static define_unary_function_eval (__heap_mult,&_heap_mult,_heap_mult_s);
  define_unary_function_ptr5( at_heap_mult ,alias_at_heap_mult,&__heap_mult,0,true);

  double modgcd_cachesize=6291456;
  gen _modgcd_cachesize(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g=evalf_double(g0,1,contextptr);
    if (g.type!=_DOUBLE_)
      return modgcd_cachesize;
    return modgcd_cachesize=g._DOUBLE_val;
  }
  static const char _modgcd_cachesize_s []="modgcd_cachesize";
  static define_unary_function_eval (__modgcd_cachesize,&_modgcd_cachesize,_modgcd_cachesize_s);
  define_unary_function_ptr5( at_modgcd_cachesize ,alias_at_modgcd_cachesize,&__modgcd_cachesize,0,true);

  gen _debug_infolevel(const gen & g0,GIAC_CONTEXT){
    if ( g0.type==_STRNG && g0.subtype==-1) return  g0;
    gen g=evalf_double(g0,1,contextptr);
    if (g.type!=_DOUBLE_)
      return debug_infolevel;
    return debug_infolevel=int(g._DOUBLE_val);
  }
  static const char _debug_infolevel_s []="debug_infolevel";
  static define_unary_function_eval (__debug_infolevel,&_debug_infolevel,_debug_infolevel_s);
  define_unary_function_ptr5( at_debug_infolevel ,alias_at_debug_infolevel,&__debug_infolevel,0,true);

  my_mpz operator % (const my_mpz & a,const my_mpz & b){
    my_mpz tmp;
    mpz_fdiv_r(tmp.ptr,a.ptr,b.ptr);
    return tmp;
  }

  my_mpz operator %= (my_mpz & a,const my_mpz & b){
    mpz_fdiv_r(a.ptr,a.ptr,b.ptr);
    return a;
  }

  my_mpz operator += (my_mpz & a,const my_mpz & b){
    mpz_add(a.ptr,a.ptr,b.ptr);
    return a;
  }

  my_mpz operator -= (my_mpz & a,const my_mpz & b){
    mpz_sub(a.ptr,a.ptr,b.ptr);
    return a;
  }

  my_mpz operator + (const my_mpz & a,const my_mpz & b){
    my_mpz tmp;
    mpz_add(tmp.ptr,a.ptr,b.ptr);
    return tmp;
  }

  my_mpz operator - (const my_mpz & a,const my_mpz & b){
    my_mpz tmp;
    mpz_sub(tmp.ptr,a.ptr,b.ptr);
    return tmp;
  }

  my_mpz operator * (const my_mpz & a,const my_mpz & b){
    my_mpz tmp;
    mpz_mul(tmp.ptr,a.ptr,b.ptr);
    return tmp;
  }

  my_mpz operator / (const my_mpz & a,const my_mpz & b){
    my_mpz tmp;
    mpz_fdiv_q(tmp.ptr,a.ptr,b.ptr);
    return tmp;
  }

  my_mpz invmod(const my_mpz & a,int reduce){
    my_mpz z=(a%reduce);
    int tmp=mpz_get_si(z.ptr);
    tmp=invmod(tmp,reduce);
    return tmp;
  }

  my_mpz smod(const my_mpz & a,int reduce){
    my_mpz z=(a%reduce);
    int tmp=mpz_get_si(z.ptr);
    tmp=smod(tmp,reduce);
    return tmp;
  }

  void wait_1ms(context * contextptr){
    usleep(1000);
  }

  void background_callback(const gen & g,void * newcontextptr){
    if (g.type==_VECT && g._VECTptr->size()==2){
      context * cptr=(giac::context *)newcontextptr;
      if (cptr){
#ifdef HAVE_LIBPTHREAD
	pthread_mutex_lock(cptr->globalptr->_mutex_eval_status_ptr);
	sto(g._VECTptr->back(),g._VECTptr->front(),cptr);
	pthread_mutex_unlock(cptr->globalptr->_mutex_eval_status_ptr);
#endif
      }
    }
  }

  gen _background(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
#ifdef HAVE_LIBPTHREAD
    if (g.type!=_VECT || g._VECTptr->size()<2)
      return gensizeerr(contextptr);
    vecteur v(*g._VECTptr);
    gen target=v[0];
    gen toeval=v[1];
    int s=v.size();
    double maxsize=1e9; // in bytes
    double maxtime=1e9; // in microseconds
    int level=eval_level(contextptr);
    if (s>2){
      gen tmp=evalf_double(v[2],level,contextptr);
      if (tmp.type!=_DOUBLE_)
	return gentypeerr(contextptr);
      maxsize=tmp._DOUBLE_val;
    }
    if (s>3){
      gen tmp=evalf_double(v[3],level,contextptr);
      if (tmp.type!=_DOUBLE_)
	return gentypeerr(contextptr);
      maxtime=tmp._DOUBLE_val;
    }
    if (s>4 && v[4].type==_INT_)
      level=v[4].val;
    gen tmp;
    context * newcontextptr=clone_context(contextptr);
    newcontextptr->parent=contextptr;
    tmp=gen(newcontextptr,_THREAD_POINTER);
    sto(tmp,target,contextptr);
    if (!make_thread(makevecteur(symbolic(at_quote,target),toeval),level,background_callback,(void *)newcontextptr,newcontextptr)){
      sto(undef,target,contextptr);
      return gensizeerr(gettext("Unable to make thread"));
    }
    return tmp;
#else
    return undef;
#endif
  }
  static const char _background_s []="background";
  static define_unary_function_eval_quoted (__background,&_background,_background_s);
  define_unary_function_ptr5( at_background ,alias_at_background,&__background,_QUOTE_ARGUMENTS,true);

  // check if var is a power of 2
  static int find_shift(const hashgcd_U & var){
    hashgcd_U u(var);
    int shift=-1;
    for (;u;u = u>> 1){
      ++shift;
    }
    return (int(var) == (1<<shift) )?shift:-1;
  }

  static bool find_shift(const std::vector<hashgcd_U> & vars,index_t & shift){
    shift.clear();
    std::vector<hashgcd_U>::const_iterator it=vars.begin(),itend=vars.end();
    shift.reserve(itend-it);
    for (;it!=itend;++it){
      shift.push_back(find_shift(*it));
      if (shift.back()==-1)
	return false;
    }
    return true;
  }

  static bool gcdsmallmodpoly(const vector<int> & a,const vector<int> & b,const vector<int> * pminptr,int modulo,vector<int> & d){
    gcdsmallmodpoly(a,b,modulo,d);
    return true;
  }

  static bool DivRem(const vector<int> & a,const vector<int> & b,const vector<int> * pminptr,int modulo,vector<int> & q,vector<int> & r){
    DivRem(a,b,modulo,q,r);
    return true;
  }

  static bool divrem(vector< vector<int> > & a,vector< vector<int> > & b,const vector<int> & pmin, int modulo, vector< vector<int> > * qptr,bool set_q_orig_b=true);

  static bool DivRem(const vector< vector<int> > & a,const vector< vector<int> > & b,const vector<int> * pminptr,int modulo,vector< vector<int> > & q,vector< vector<int> > & r){
    r=a;
    vector< vector<int> > b0(b);
    return divrem(r,b0,*pminptr,modulo,&q,true);
  }

  static bool gcdsmallmodpoly_ext(const vector< vector<int> > & p,const vector< vector<int> > & q,const vector<int> & pmin,int modulo,vector< vector<int> > & d);

  static bool gcdsmallmodpoly(const vector< vector<int> > & a,const vector< vector<int> > & b,const vector<int> * pminptr,int modulo,vector< vector<int> > & d){
    return gcdsmallmodpoly_ext(a,b,*pminptr,modulo,d);
  }

  bool is_zero(const vector<int> & v){
    vector<int>::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      if (*it)
	return false;
    }
    return true;
  }

  inline int make_unit(int ){
    return 1;
  }
  
  inline vector<int> make_unit(const vector<int>){
    vector<int> v(1,1);
    return v;
  }

  // extract modular content of p wrt to the last variable
  template<class T>
  static bool pp_mod(vector< T_unsigned<T,hashgcd_U> > & p,const vector<int> * pminptr,int modulo,hashgcd_U var,hashgcd_U var2,vector<T> & pcontxn){
    typename vector< T_unsigned<T,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    pcontxn.clear();
    if (it==itend) return true;
    hashgcd_U u=(p.front().u/var)*var,curu,newu;
    if (u==p.front().u){
      pcontxn.push_back(make_unit(p.front().g));
      return true;
    }
    vector<T> current,tmp,reste;
    for (;it!=itend;){
      current.clear();
      current.push_back(it->g);
      curu=it->u;
      ++it;
      for (;it!=itend;++it){
	newu=it->u;
	if ( newu < u ){
	  break;
	}
	if (curu>newu+var2)
	  current.insert(current.end(),(curu-newu)/var2-1,T(0));
	current.push_back(it->g);
	curu=newu;
      }
      if (curu>u)
	current.insert(current.end(),(curu-u)/var2,T(0));
      if (!gcdsmallmodpoly(pcontxn,current,pminptr,modulo,tmp))
	return false;
      pcontxn=tmp;
      if (tmp.size()==1)
	return true;
      u=(newu/var)*var;
    }
    vector< T_unsigned<T,hashgcd_U> > res;
    it=p.begin();
    u=(p.front().u/var)*var;
    for (;it!=itend;){
      current.clear();
      current.push_back(it->g);
      curu=it->u;
      ++it;
      for (;it!=itend;++it){
	newu=it->u;
	if ( newu < u ){
	  break;
	}
	if (curu>newu+var2)
	  current.insert(current.end(),(curu-newu)/var2-1,T(0));
	current.push_back(it->g);
	curu=newu;
      }
      if (curu>u)
	current.insert(current.end(),(curu-u)/var2,T(0));
      if (!DivRem(current,pcontxn,pminptr,modulo,tmp,reste))
	return false;
      typename vector<T>::const_iterator jt=tmp.begin(),jtend=tmp.end();
      for (int s=jtend-jt-1;jt!=jtend;++jt,--s){
	if (!is_zero(*jt))
	  res.push_back(T_unsigned<T,hashgcd_U>(*jt,u+s*var2));
      }
      u=(newu/var)*var;
    }
    p=res;
    return true;
  }

  template<class T>
  static bool operator < (const vector< T_unsigned<T,hashgcd_U> > & v1,const vector< T_unsigned<T,hashgcd_U> > & v2){
    return v1.size()<v2.size();
  }

  /*
  unsigned degree_xn(const vector< T_unsigned<int,hashgcd_U> > & p,hashgcd_U var,hashgcd_U var2){
    vector< T_unsigned<int,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    hashgcd_U degxn=0;
    hashgcd_U u;
    for (;it!=itend;++it){
      u=it->u%var;
      if (degxn<u)
	degxn=u;
    }
    return degxn/var2;
  }
  */

  template<class T>
  static unsigned degree_xn(const vector< T_unsigned<T,hashgcd_U> > & p,short int shift_var,short int shift_var2){
    typename vector< T_unsigned<T,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end(),it1;
    unsigned degxn=0;
    unsigned u,uend;
    for (;it!=itend;++it){
      uend = ((it->u >> shift_var) << shift_var);
      u = (it->u - uend) >> shift_var2 ;
      if (!u)
	continue;
      if (degxn<u)
	degxn=u;
      if ((int)u>=itend-it)
	continue;
      it1 = it+u;
      if (it1->u==uend)
	it = it1;
    }
    return degxn ;
  }

  /*
  template<class T>
  int degree(const vector< T_unsigned<T,hashgcd_U> > & p,
	      const std::vector<hashgcd_U> & vars,
	      index_t & res){
    typename vector< T_unsigned<T,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    std::vector<hashgcd_U>::const_iterator jtbeg=vars.begin(),jtend=vars.end(),jt;
    hashgcd_U u,ur;
    res=index_t(jtend-jtbeg);
    index_t::iterator ktbeg=res.begin(),ktend=res.end(),kt;
    int totaldeg=0,current;
    for (;it!=itend;++it){
      u=it->u;
      current=0;
      for (jt=jtbeg,kt=ktbeg;jt!=jtend;++jt,++kt){
	ur=u%(*jt);
	u=u/(*jt);
	if (u>*kt)
	  *kt=u;
	current += u;
	u=ur;
      }
      if (current>totaldeg)
	totaldeg=current;
    }
    return totaldeg;
  }
  */

  template<class T>
  static int degree(const vector< T_unsigned<T,hashgcd_U> > & p,
	      const index_t & shift_vars,
	      index_t & res){
    typename vector< T_unsigned<T,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    index_t::const_iterator jtbeg=shift_vars.begin(),jtend=shift_vars.end(),jt;
    int dim=jtend-jtbeg;
    if (dim==1){
      int deg=it->u >> *jtbeg;
      res.clear();
      res.push_back(deg);
      return deg;
    }
    hashgcd_U u,uq,uend,skip;
    short int lastvar=shift_vars[shift_vars.size()-2],shift_var2=shift_vars.back();
    res=index_t(jtend-jtbeg);
    index_t::iterator ktbeg=res.begin(),ktend=res.end(),kt;
    int totaldeg=0,current;
    for (;it!=itend;){
      u=it->u;
      // find degree for *it and skip monomials having x1..xn-1 in common
      uend = (u >> lastvar) << lastvar;
      skip = (u-uend) >> shift_var2;
      current=0;
      // find partial and total degree
      for (jt=jtbeg,kt=ktbeg;jt!=jtend;++jt,++kt){
	uq = u >> *jt;
	u -= uq << *jt;
	if ((int)uq>*kt)
	  *kt = uq;
	current += uq;
      }
      if (current>totaldeg)
	totaldeg=current;
      if ((int)skip<itend-it && (it+skip)->u==uend){
	it += skip;
	++it;
	continue;
      }
      for (++it;it!=itend;++it){
	if (it->u<uend)
	  break;
      }
    }
    return totaldeg;
  }

  static int mod_gcd_ext(const vector< T_unsigned<vector<int>,hashgcd_U> > & p_orig,const vector< T_unsigned<vector<int>,hashgcd_U> > & q_orig,const std::vector<hashgcd_U> & vars,const vector<int> & pmin,int modulo,
	      vector< T_unsigned<vector<int>,hashgcd_U> > & d,
	      vector< T_unsigned<vector<int>,hashgcd_U> > & pcof,vector< T_unsigned<vector<int>,hashgcd_U> > & qcof,bool compute_pcof,bool compute_qcof,
	      int nthreads);

  bool mod_gcd(const vector< T_unsigned<vector<int>,hashgcd_U> > & p_orig,const vector< T_unsigned<vector<int>,hashgcd_U> > & q_orig,const vector<int> * pminptr,int modulo,const std::vector<hashgcd_U> & vars,
	      vector< T_unsigned<vector<int>,hashgcd_U> > & d,
	      vector< T_unsigned<vector<int>,hashgcd_U> > & pcof,vector< T_unsigned<vector<int>,hashgcd_U> > & qcof,bool compute_cof,
	      int nthreads){
    return mod_gcd_ext(p_orig,q_orig,vars,*pminptr,modulo,d,pcof,qcof,compute_cof,compute_cof,nthreads)!=0;
  }

  bool mod_gcd(const vector< T_unsigned<int,hashgcd_U> > & p_orig,const vector< T_unsigned<int,hashgcd_U> > & q_orig,const vector<int> * pminptr,int modulo,const std::vector<hashgcd_U> & vars, vector< T_unsigned<int,hashgcd_U> > & d, vector< T_unsigned<int,hashgcd_U> > & pcofactor, vector< T_unsigned<int,hashgcd_U> > & qcofactor,bool compute_cofactor,int nthreads){
    return mod_gcd(p_orig,q_orig,modulo,d,pcofactor,qcofactor,vars,compute_cofactor,nthreads);
  }

  struct modred {
    int modulo;
    vector<int> pmin;
    modred(int m,const vector<int> & p):modulo(m),pmin(p) {}
    modred():modulo(0),pmin(0) {}
  };

  static bool is_zero(const modred & r){
    return r.modulo==0;
  }

  static int make_modulo(const vector<int> * pminptr,int modulo,int){
    return modulo;
  }

  static modred make_modulo(const vector<int> * pminptr,int modulo,vector<int>){
    return modred(modulo,*pminptr);
  }

  struct Modred {
    int modulo;
    vecteur pmin;
    Modred(int m,const vecteur & p):modulo(m),pmin(p) {}
    Modred():modulo(0),pmin(0) {}
  };

  static bool is_zero(const Modred & r){
    return r.modulo==0;
  }

  // extract modular content of p wrt to all but the last variable
  template<class T>
  static bool pp_mod(vector< T_unsigned<T,hashgcd_U> > & p,const vector<int> * pminptr,int modulo,const std::vector<hashgcd_U> & vars,vector< T_unsigned<T,hashgcd_U> > & pcont,int nthreads){
#if defined( RTOS_THREADX) || defined(BESTA_OS) || defined(EMCC)
    return false;
#else
    typename vector< T_unsigned<T,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    pcont.clear();
    pcont.push_back(T_unsigned<T,hashgcd_U>(make_unit(T(0)),0));
    if (it==itend || vars.empty()) return true;
    std::vector<hashgcd_U> varsn(vars);
    varsn.pop_back();
    // hashgcd_U degxnu=0;
    hashgcd_U u,u0,var=varsn.back(),var2=vars.back();
    short int shiftvar=find_shift(var),shiftvar2=find_shift(var2);
    unsigned degxn = degree_xn(p,shiftvar,shiftvar2);
    vector<int> nterms(degxn+1);
    vector<bool> nonzero(degxn+1,false); // number of non constant terms
    int pos;
    for (it=p.begin();it!=itend;++it){
      u = it->u;
      pos = (u%var) >> shiftvar2;
      nonzero[pos]=true;
      if (u>>shiftvar)
	++nterms[pos];
    }
    vector< vector< T_unsigned<T,hashgcd_U> > > vp(degxn+1);
    for (int i=degxn;i>=0;--i){
      if (!nterms[i] && nonzero[i]) // all terms would be constant in vp[i]
	return true;
      vp[i].reserve(nterms[i]+1);
    }
    for (it=p.begin();it!=itend;++it){
      u = it->u;
      u0 = (u >> shiftvar) << shiftvar;
      vp[(u-u0)>>shiftvar2].push_back(T_unsigned<T,hashgcd_U>(it->g, u0));
    }
    // sort vp by size
    sort(vp.begin(),vp.end());
    // compute gcd
    unsigned int i=0;
    for (;i<=degxn;++i){
      if (!vp[i].empty()){
	pcont=vp[i];
	break;
      }
    }
    if (pcont.empty())
      cerr << "empty" << endl;
    vector< T_unsigned<T,hashgcd_U> > res,rem,pcof,qcof;
    for (++i;i<=degxn;++i){
      if (pcont.size()==1 && pcont.front().u==0)
	return true;
      if (!mod_gcd(pcont,vp[i],pminptr,modulo,varsn,pcont,pcof,qcof,false,nthreads))
	return false;
    }
    if (pcont.size()==1 && pcont.front().u==0)
      return true;
    hashdivrem(p,pcont,res,rem,vars,make_modulo(pminptr,modulo,T(0)),0,false);
    swap(p,res);
    return true;
#endif // RTOS_THREADX
  }


  // fast check if p is primitive with respect to the main var
  // p main var is y, inner var is x
  static bool is_front_primitive(vector< vector<int> > & p,int modulo){
    vector< vector<int> >::iterator it=p.begin(),itend=p.end();
    int degy=(itend-it)-1;
    vector<int> degrees;
    int degs=0,d;
    for (it=p.begin();it!=itend;++it,--degy){
      d=it->size();
      // there is a x^d*y^degy term -> set degree of x^0 to x^d to degy at least 
      if (d>degs){
	degrees.insert(degrees.end(),d-degs,degy);
	degs=d;
      }
    }
    vector<int>::iterator jt=degrees.begin(),jtend=degrees.end();
    for (;jt!=jtend;++jt){
      if (!*jt)
	return true;
    }
    return false;
  }

  // eval p at x with respect to the last variable
  // keep only terms of degree <= maxdeg with respect to the main variable
  static bool horner(const vector< T_unsigned<int,hashgcd_U> > & p,int x,const std::vector<hashgcd_U> & vars,vector< T_unsigned<int,hashgcd_U> > & px,int modulo,int maxdeg){
    hashgcd_U var=vars[vars.size()-2];
    hashgcd_U var2=vars.back();
    vector< T_unsigned<int,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end(),it1,it2;
    vector<hashgcd_U>::const_iterator jtend=vars.end()-1;
    hashgcd_U ucur,uend;
    if (maxdeg>=0){
      uend=(maxdeg+1)*vars.front();
      // dichotomy to find start position
      int pos1=0,pos2=itend-it,pos;
      for (;pos2-pos1>1;){
	pos=(pos1+pos2)/2;
	if ((it+pos)->u<uend)
	  pos2=pos;
	else
	  pos1=pos;
      }
      it += pos1;
      if (it->u>=uend)
	++it;
    }
    if (x==0){
      px.clear();
      for (;it!=itend;){
	ucur=it->u;
	uend=(ucur/var)*var;
	if (ucur==uend){
	  register int g=smod(it->g,modulo);
	  if (g!=0)
	    px.push_back(T_unsigned<int,hashgcd_U>(g,uend));
	  ++it;
	  continue;
	}
	register int nterms = (ucur-uend)/var2;
	if (nterms<itend-it && (it+nterms)->u==uend){
	  it += nterms;
	  register int g=smod(it->g,modulo);
	  if (g!=0)
	    px.push_back(T_unsigned<int,hashgcd_U>(g,uend));
	  ++it;
	  continue;	  
	}
	for (++it;it!=itend;++it){
	  if (it->u<=uend){
	    if (it->u==uend){
	      register int g=smod(it->g,modulo);
	      if (g!=0)
		px.push_back(T_unsigned<int,hashgcd_U>(g,uend));
	      ++it;
	    }
	    break;
	  }
	}
      }
      return true;
    }
    vector< T_unsigned<int,hashgcd_U> >::iterator kt=px.begin(),ktend=px.end();
    for (;it!=itend;){
      ucur=it->u;
      uend=(ucur/var)*var;
      if (ucur==uend){
	if (kt!=ktend){
	  *kt=*it;
	  ++kt;
	}
	else
	  px.push_back(*it);
	++it;
	continue;
      }
      int g=0;
      int nterms=(ucur-uend)/var2+1;
      if (x==1 && nterms < RAND_MAX/modulo ){
	for (;it!=itend;++it){
	  if (it->u<uend){
	    g=smod(g,modulo);
	    if (g!=0){
	      if (kt!=ktend){
		*kt=T_unsigned<int,hashgcd_U>(g,uend);
		++kt;
	      }
	      else
		px.push_back(T_unsigned<int,hashgcd_U>(g,uend));
	    }
	    break;
	  }
	  else
	    g += it->g;
	}
	if (it==itend){
	  g=smod(g,modulo);
	  if (g!=0){
	    if (kt!=ktend){
	      *kt=T_unsigned<int,hashgcd_U>(g,uend);
	      ++kt;
	    }
	    else
	      px.push_back(T_unsigned<int,hashgcd_U>(g,uend));
	  }
	}
	continue;
      }
      else {
	// Check if the next group of monomials is dense wrt to xn
	it1=it+nterms;
	if (//false &&
	    nterms<itend-it && (it1-1)->u==uend
	    ){
	  if (modulo<=46340){
	    if (x>=-14 && x<=14){
	      if (x>=-8 && x<=8){
		it2=it+5*((it1-it)/5);
		for (;it!=it2;){
		  g *= x;
		  g += it->g;
		  ++it;
		  g *= x;
		  g += it->g;
		  ++it;
		  g *= x;
		  g += it->g;
		  ++it;
		  g *= x;
		  g += it->g;
		  ++it;
		  g *= x;
		  g += it->g;
		  g %= modulo;
		  ++it;
		}
	      }
	      else {
		it2=it+4*((it1-it)/4);
		for (;it!=it2;){
		  g *= x;
		  g += it->g;
		  ++it;
		  g *= x;
		  g += it->g;
		  ++it;
		  g *= x;
		  g += it->g;
		  ++it;
		  g *= x;
		  g += it->g;
		  g %= modulo;
		  ++it;
		}
	      }
	    }
	    else {
	      if (x>=-35 && x<=35){
		it2=it+3*((it1-it)/3);
		for (;it!=it2;){
		  g *= x;
		  g += it->g;
		  ++it;
		  g *= x;
		  g += it->g;
		  ++it;
		  g *= x;
		  g += it->g;
		  g %= modulo;
		  ++it;
		}
	      }
	    }
	    for (;it!=it1;++it){
	      g = (g*x+it->g)%modulo;
	    }
	  } // end if (modulo<46430)
	  else { // modulo>=46430, using longlong
	    if (x>=-84 && x<=84){
	      longlong G=g;
	      it2=it+5*((it1-it)/5);
	      for (;it!=it2;){
		G *= x;
		G += it->g;
		++it;
		G *= x;
		G += it->g;
		++it;
		G *= x;
		G += it->g;
		++it;
		G *= x;
		G += it->g;
		++it;
		G *= x;
		G += it->g;
		G %= modulo;
		++it;
	      }
	      for (;it!=it1;++it){
		G *=x;
		G += it->g;
	      }
	      g=G%modulo;
	    }
	    else {
	      for (;it!=it1;++it){
		g = (g*longlong(x)+it->g)%modulo;
	      }
	    }
	  }
	  g=smod(g,modulo);
	  if (g!=0){
	    if (kt!=ktend){
	    *kt=T_unsigned<int,hashgcd_U>(g,uend);
	    ++kt;
	    }
	    else
	      px.push_back(T_unsigned<int,hashgcd_U>(g,uend));
	  }
	  continue;
	}
	if (modulo>=46340){
	  for (;it!=itend;++it){
	    const hashgcd_U & u=it->u;
	    if (u<uend){
	      if (g!=0){
		g = smod(g*longlong(powmod(x,(ucur-uend)/var2,modulo)),modulo);
		if (g!=0){
		  if (kt!=ktend){
		    *kt=T_unsigned<int,hashgcd_U>(g,uend);
		    ++kt;
		  }
		  else
		    px.push_back(T_unsigned<int,hashgcd_U>(g,uend));
		}
	      }
	      break;
	    }
	    if (ucur-u==var2)
	      g = (g*longlong(x)+ it->g)%modulo;
	    else
	      g = (g*longlong(powmod(x,(ucur-u)/var2,modulo))+ it->g)%modulo;
	    ucur=u;
	  } // end for
	} // end if modulo>=46340
	else {
	  for (;it!=itend;++it){
	    const hashgcd_U & u=it->u;
	    if (u<uend){
	      if (g!=0){
		g = smod(g*powmod(x,(ucur-uend)/var2,modulo),modulo);
		if (g!=0){
		  if (kt!=ktend){
		    *kt=T_unsigned<int,hashgcd_U>(g,uend);
		    ++kt;
		  }
		  else
		    px.push_back(T_unsigned<int,hashgcd_U>(g,uend));
		}
	      }
	      break;
	    }
	    if (ucur-u==var2)
	      g = (g*x+ it->g)%modulo;
	    else
	      g = (g*powmod(x,(ucur-u)/var2,modulo)+ it->g)%modulo;
	    ucur=u;
	  } // end for
	}
      } // end else x=1
      if (it==itend){
	if (g!=0){
	  g = smod(g*longlong(powmod(x,(ucur-uend)/var2,modulo)),modulo);
	  if (g!=0){
	    if (kt!=ktend){
	      *kt=T_unsigned<int,hashgcd_U>(g,uend);
	      ++kt;
	    }
	    else
	      px.push_back(T_unsigned<int,hashgcd_U>(g,uend));
	  }
	}
      }
    }
    if (kt!=ktend)
      px.erase(kt,ktend);
    return true;
  }

  // v <- v*k % m
  void mulmod(vector<int> & v,int k,int m){
    if (k==1)
      return;
    vector<int>::iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      type_operator_times_reduce(*it,k,*it,m);
      // *it = ((*it)*k)%m;
    }
  }

  // v <- v*k % m
  static void mulmod(vector< vector<int> > & v,int k,int m){
    if (k==1)
      return;
    vector< vector<int> >::iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      mulmod(*it,k,m);
    }
  }

  inline void mulmod(int k,vector<int> & v,int m){
    mulmod(v,k,m);
  }

  // v <- v+w % m
  static void addmod(vector<int> & v,const vector<int> & w,int m){
    vector<int>::const_iterator jt=w.begin(),jtend=w.end();
    vector<int>::iterator it=v.begin(),itend=v.end();
    int ws=jtend-jt,vs=v.size();
    if (ws>vs){
      if ((int)v.capacity()<ws){
	vector<int> tmp(ws);
	copy(v.begin(),v.end(),tmp.begin()+ws-vs);
	swap(v,tmp);
      }
      else {
	v.insert(v.begin(),ws-vs,0);	
      }
      it=v.begin();
      itend=v.end();
    }
    for (it=itend-ws;it!=itend;++jt,++it){
      *it = (*it+*jt)%m;
    }
    // trim resulting polynomial
    for (it=v.begin();it!=itend;++it){
      if (*it)
	break;
    }
    if (it!=v.begin())
      v.erase(v.begin(),it);
  }

  // v <- v+w % m
  static void addmod(vector< vector<int> > & v,const vector< vector<int> > & w,int m){
    vector< vector<int> >::iterator it=v.begin(),itend=v.end();
    vector< vector<int> >::const_iterator jt=w.begin(),jtend=w.end();
    int addv=(jtend-jt)-(itend-it);
    if (addv>0){
      v.insert(v.begin(),addv,vector<int>(0));
      it=v.begin();
      itend=v.end();
    }
    for (it=itend-(jtend-jt);it!=itend;++jt,++it){
      addmod(*it,*jt,m);
    }
  }

  // v <- v+w % m
  static void addmod(vecteur & v,const vecteur & w,int m){
    vecteur::const_iterator jt=w.begin(),jtend=w.end();
    vecteur::iterator it=v.begin(),itend=v.end();
    int ws=jtend-jt,vs=v.size();
    if (ws>vs){
      if ((int)v.capacity()<ws){
	vecteur tmp(ws);
	copy(v.begin(),v.end(),tmp.begin()+ws-vs);
	swap(v,tmp);
      }
      else {
	v.insert(v.begin(),ws-vs,0);	
      }
      it=v.begin();
      itend=v.end();
    }
    for (it=itend-ws;it!=itend;++jt,++it){
      *it = smod(*it+*jt,m);
    }
    // trim resulting polynomial
    for (it=v.begin();it!=itend;++it){
      if (!is_zero(*it))
	break;
    }
    if (it!=v.begin())
      v.erase(v.begin(),it);
  }

  // v <- v-w % m
  static void submod(vector<int> & v,const vector<int> & w,int m){
    vector<int>::iterator it=v.begin(),itend=v.end();
    vector<int>::const_iterator jt=w.begin(),jtend=w.end();
    int addv=(jtend-jt)-(itend-it);
    if (addv>0){
      v.insert(v.begin(),addv,0);
      it=v.begin();
      itend=v.end();
    }
    for (it=itend-(jtend-jt);it!=itend;++jt,++it){
      *it = (*it-*jt)%m;
    }
    for (it=v.begin();it!=itend;++it){
      if (*it)
	break;
    }
    if (it!=v.begin())
      v.erase(v.begin(),it);
  }

  // v <- k*(v-w) % m
  static void mulsubmod(int k,vector<int> & v,const vector<int> & w,int m){
    vector<int>::iterator it=v.begin(),itend=v.end();
    vector<int>::const_iterator jt=w.begin(),jtend=w.end();
    int addv=(jtend-jt)-(itend-it);
    if (addv>0){
      v.insert(v.begin(),addv,0);
      it=v.begin();
      itend=v.end();
    }
    itend -= (jtend-jt);
    if (m<=46340){ 
      if (2*k>m)
	k -= m;
      for (;it!=itend;++it){
	*it = ((*it)*k)%m;      
      }
      for (itend=v.end();it!=itend;++jt,++it){
	*it = ((*it-*jt)*k)%m;      
      }
    }
    else {
      for (;it!=itend;++it){
	type_operator_times_reduce(*it,k,*it,m);
	// *it = ((*it)*k)%m;      
      }
      for (itend=v.end();it!=itend;++jt,++it){
	*it -= *jt;
	type_operator_times_reduce(*it,k,*it,m);
	// *it = ((*it)*k)%m;      
      }
    }
    for (it=v.begin();it!=itend;++it){
      if (*it)
	break;
    }
    if (it!=v.begin())
      v.erase(v.begin(),it);
  }

  // eval p at x with respect to all but the last variable
  template<class T>
  static bool horner(const std::vector< T_unsigned<T,hashgcd_U> > & p,const std::vector<int> & x,const std::vector<hashgcd_U> & vars,std::vector<T> & px,int modulo){
    int s=x.size();
    // int xback=x.back();
    int vs=vars.size();
    if (s+1!=vs || vs<2)
      return false; // setdimerr();
    hashgcd_U var=vars[vs-2],var2=vars.back();
    int shift_var=find_shift(var),shift_var2=find_shift(var2);
    typename vector< T_unsigned<T,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    if (is_zero(x)){
      int pos1=0,pos2=itend-it,pos;
      for (;pos2-pos1>1;){
	pos=(pos1+pos2)/2;
	if ((it+pos)->u<var)
	  pos2=pos;
	else
	  pos1=pos;
      }
      it += pos1;
      if (it->u>=var)
	++it;
      if (it==itend)
	px.clear();
      else {
	int pxdeg = it->u >> shift_var2;
	px=vector<T>(pxdeg+1);
	for (;it!=itend;++it){
	  px[pxdeg - (it->u >> shift_var2)]=it->g;
	}
      }
    }
    else {
      int pxdeg=degree_xn(p,shift_var,shift_var2);
      px=vector<T>(pxdeg+1);
      vector<hashgcd_U>::const_iterator jtbeg=vars.begin(),jtend=vars.end(),jt;
      --jtend;
      vector<int>::const_iterator ktbeg=x.begin(),kt;
      hashgcd_U u,oldu=0;
      int oldfact=0;
      int inverse=x.back()?invmod(x.back(),modulo):0;
      for (;it!=itend;){
	// group next monomials with same powers in x1..xn-1
	u=(it->u/var)*var;
	int tmpdeg=(it->u-u)/var2;
	vector<T> tmp(tmpdeg+1);
	for (;;++it){
	  if (it==itend || it->u<u){
	    int fact=1;
	    if (inverse && oldu-u==var && oldfact){
	      fact = (longlong(oldfact)*inverse)%modulo;
	      oldu=u;
	    }
	    else {
	      oldu=u;
	      for (jt=jtbeg,kt=ktbeg;jt!=jtend;++jt,++kt){
		// px=px*powmod(*kt,u / *jt,modulo);
		fact = (longlong(fact)*powmod(*kt,u / *jt,modulo))%modulo;
		u = u % *jt;
	      }
	    }
	    oldfact=fact;
	    mulmod(tmp,fact,modulo); // inlined in modpoly.cc
	    addmod(px,tmp,modulo);
	    break;
	  }
	  tmp[tmpdeg-(it->u-u)/var2]=it->g;
	}
      }
    } // end else is_zero(x)
    // trim px
    typename vector<T>::iterator lt=px.begin(),ltend=px.end();
    for (;lt!=ltend;++lt){
      if (!is_zero(*lt))
	break;
    }
    if (lt!=px.begin())
      px.erase(px.begin(),lt);
    return true;
  }

  template<class T>
  static void convert_back(const vector<T> & v,hashgcd_U var,vector< T_unsigned<T,hashgcd_U> > & p){
    p.clear();
    typename vector<T>::const_iterator it=v.begin(),itend=v.end();
    unsigned s=itend-it;
    p.reserve(s);
    hashgcd_U u=var*(s-1);
    for (;it!=itend;u-=var,++it){
      if (!is_zero(*it))
	p.push_back(T_unsigned<T,hashgcd_U>(*it,u));
    }
  }

  static void convert_back(const vector< vector<int> > & v,hashgcd_U varxn,hashgcd_U var2,vector< T_unsigned<int,hashgcd_U> > & p){
    vector< vector<int> >::const_iterator jt=v.begin(),jtend=v.end();
    vector< T_unsigned<int,hashgcd_U> >::iterator kt=p.begin(),ktend=p.end();
    for (;jt!=jtend;++jt){
      vector<int>::const_iterator it=jt->begin(),itend=jt->end();
      unsigned s=itend-it;
      hashgcd_U u=var2*(s-1)+varxn*((jtend-jt)-1);
      for (;it!=itend;u-=var2,++it){
	if (*it!=0){
	  if (kt!=ktend){
	    *kt=T_unsigned<int,hashgcd_U>(*it,u);
	    ++kt;
	  }
	  else
	    p.push_back(T_unsigned<int,hashgcd_U>(*it,u));
	}
      }
    }
    if (kt!=ktend)
      p.erase(kt,ktend);
  }

  static void convert(const vector< T_unsigned<int,hashgcd_U> > & p,hashgcd_U var,vector<int> & v,int modulo){
    v.clear();
    vector< T_unsigned<int,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    if (it==itend)
      return;
    hashgcd_U u=it->u;
    unsigned s=u/var;
    v=vector<int>(s+1);
    for (;it!=itend;++it){
      v[s-it->u/var]=it->g<0?it->g+modulo:it->g;
    }
  }

#if 0
  static void convert(const vector< T_unsigned<int,hashgcd_U> > & p,short int var,vector<int> & v,int modulo){
    vector< T_unsigned<int,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    if (it==itend){
      v.clear();
      return;
    }
    hashgcd_U u=it->u;
    unsigned s=u >> var;
    if (v.size()==s+1)
      fill(v.begin(),v.end(),0);
    else
      v=vector<int>(s+1);
    for (;it!=itend;++it){
      v[s-(it->u >> var)]=it->g<0?it->g+modulo:it->g;
    }
  }
#endif

  /*
  void convert(const vector< T_unsigned<int,hashgcd_U> > & p,hashgcd_U var,vector<int> & v){
    v.clear();
    vector< T_unsigned<int,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    if (it==itend)
      return;
    hashgcd_U u=it->u,prevu=u;
    unsigned s=u/var;
    v.reserve(s+1);
    v.push_back(it->g);
    for (++it;it!=itend;++it){
      u=it->u;
      prevu -= var;
      if (u==prevu)
	v.push_back(it->g);
      else {
	v.insert(v.end(),(prevu-u)/var,0);
	v.push_back(it->g);
	prevu=u;
      }
    }
  }
  */
  // Find non zeros coeffs of p
  template<class T>
  static int find_nonzero(const vector<T> & p,index_t & res){
    res.clear();
    typename vector<T>::const_iterator it=p.begin(),itend=p.end();
    if (it==itend)
      return 0;
    int nzeros=0;
    for (;it!=itend;++it){
      bool test=is_zero(*it);
      res.push_back(test?0:1);
      if (test)
	++nzeros;
    }
    return nzeros;
  }

  template<class T>
  static hashgcd_U lcoeff(const vector< T_unsigned<T,hashgcd_U> > & p,hashgcd_U var,hashgcd_U var2,vector<T> & lp){
    lp.clear();
    typename vector< T_unsigned<T,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    if (it==itend)
      return 0;
    hashgcd_U u=it->u;
    int deg=(u%var)/var2;
    lp=vector<T>(deg+1);
    u=(u/var)*var;
    for (;it!=itend;++it){
      if (it->u<u)
	break;
      lp[deg-(it->u%var)/var2]=it->g;
    }
    return u;
  }

  static std::vector<int> smod(const std::vector<int> & v,int modulo){
    std::vector<int> res(v);
    std::vector<int>::iterator it=res.begin(),itend=res.end();
    for (;it!=itend;++it){
      *it = smod(*it,modulo);
    }
    if (res.empty() || res.front())
      return res;
    for (it=res.begin();it!=itend;++it){
      if (*it)
	break;
    }
    return std::vector<int>(it,itend);
  }

  static int hornermod(const vector<int> & v,int alpha,int modulo,bool unsig=false){
    vector<int>::const_iterator it=v.begin(),itend=v.end(),it0;
    if (!alpha)
      return it==itend?0:v.back();
    int res=0;
    if (alpha==1 && (itend-it)<RAND_MAX/modulo){
      for (;it!=itend;++it)
	res += *it;
      return smod(res,modulo);
    }
    if (alpha==-1 && (itend-it)<RAND_MAX/modulo){
      for (;it!=itend;++it)
	res = *it - res;
      return smod(res,modulo);
    }
    if (modulo<46340){
      if (alpha>=-8 && alpha<=8){
	it0=it+5*((itend-it)/5);
	for (;it!=it0;){
	  res *= alpha;
	  res += *it;
	  ++it;
	  res *= alpha;
	  res += *it;
	  ++it;
	  res *= alpha;
	  res += *it;
	  ++it;
	  res *= alpha;
	  res += *it;
	  ++it;
	  res *= alpha;
	  res += *it;
	  res %= modulo;
	  ++it;
	}
	for (;it!=itend;++it){
	  res *= alpha;
	  res += *it;
	}
	return smod(res,modulo);
      }
      if (alpha>=-14 && alpha<=14){
	it0=it+4*((itend-it)/4);
	for (;it!=it0;){
	  res *= alpha;
	  res += *it;
	  ++it;
	  res *= alpha;
	  res += *it;
	  ++it;
	  res *= alpha;
	  res += *it;
	  ++it;
	  res *= alpha;
	  res += *it;
	  res %= modulo;
	  ++it;
	}
	for (;it!=itend;++it){
	  res *= alpha;
	  res += *it;
	}
	return smod(res,modulo);
      }
      if (alpha>=-35 && alpha<=35){
	it0=it+3*((itend-it)/3);
	for (;it!=it0;){
	  res *= alpha;
	  res += *it;
	  ++it;
	  res *= alpha;
	  res += *it;
	  ++it;
	  res *= alpha;
	  res += *it;
	  res %= modulo;
	  ++it;
	}
	for (;it!=itend;++it){
	  res *= alpha;
	  res += *it;
	}
	return smod(res,modulo);
      }
      if (alpha>=-214 && alpha<=214){
	it0=it+2*((itend-it)/2);
	for (;it!=it0;){
	  res *= alpha;
	  res += *it;
	  ++it;
	  res *= alpha;
	  res += *it;
	  res %= modulo;
	  ++it;
	}
	for (;it!=itend;++it){
	  res *= alpha;
	  res += *it;
	}
	return smod(res,modulo);
      }
    } // if (modulo<46430)
    else {
      longlong Res=res;
      if (alpha>=-8 && alpha<=8){
	it0=it+5*((itend-it)/5);
	for (;it!=it0;){
	  Res *= alpha;
	  Res += *it;
	  ++it;
	  Res *= alpha;
	  Res += *it;
	  ++it;
	  Res *= alpha;
	  Res += *it;
	  ++it;
	  Res *= alpha;
	  Res += *it;
	  ++it;
	  Res *= alpha;
	  Res += *it;
	  Res %= modulo;
	  ++it;
	}
	for (;it!=itend;++it){
	  Res *= alpha;
	  Res += *it;
	}
	return smod(Res,modulo);
      } // end if alpha>=-84 and alpha<=84
    } // end else (modulo>46430)
#if defined _I386_ && !defined __x86_64__
    if (unsig){
      if (alpha<0)
	alpha += modulo;
      // it va dans ecx, itend dans ebx, modulo dans edi, alpha dans esi
      // eax::edx produit et division, eax contient res,
      asm volatile("movl $0x0,%%eax;\n\t"
		   "cmpl %%ecx,%%ebx;\n\t"
		   "je .Lend%=\n"
		   ".Lloop%=:\t"
		   "imul %%esi;\n\t" /* res<-res*alpha */
		   "addl (%%ecx),%%eax;\n\t"
		   "adcl $0x0,%%edx; \n\t" /* res += *it */
		   "idivl %%edi; \n\t"  /* res %= modulo */
		   "movl %%edx,%%eax; \n\t"
		   "addl $4,%%ecx; \n\t" /* ++ *it */
		   "cmpl %%ecx,%%ebx;\n\t" /* it==itend */
		   "jne .Lloop%=\n"
		   ".Lend%=:\t"
		   :"=a"(res)
		   :"c"(it),"b"(itend),"D"(modulo),"S"(alpha)
		   :"%edx"
		   );
      if (res>modulo)
	return res-modulo;
      return res;
    }
#endif
    if (modulo<46340){
      if (unsig){
	unsigned Alpha=alpha<0?alpha+modulo:alpha;
	unsigned res=0;
	for (;it!=itend;++it){
	  res = (res*Alpha+unsigned(*it))%modulo;
	}
	if (res>(unsigned)(modulo/2))
	  return int(res)-modulo;
	else
	  return int(res);
      }
      for (;it!=itend;++it){
	res = (res*alpha+*it)%modulo;
      }
    }
    else {
      for (;it!=itend;++it){
	res = (res*longlong(alpha)+*it)%modulo;
      }
    }
    register int tmp=res+res;
    if (tmp>modulo)
      return res-modulo;
    if (tmp<=-modulo)
      return res+modulo;
    return res;
  }

  // distribute multiplication
  static void distmult(const vector< T_unsigned<int,hashgcd_U> > & p,const vector<int> & v,vector< T_unsigned<int,hashgcd_U> > & pv,hashgcd_U var,int modulo){
    if (&pv==&p){
      vector< T_unsigned<int,hashgcd_U> > tmp;
      distmult(p,v,tmp,var,modulo);
      swap(pv,tmp);
      return;
    }
    pv.clear();
    vector< T_unsigned<int,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    vector<int>::const_iterator jtbeg=v.begin(),jtend=v.end(),jt;
    int vs=jtend-jtbeg,j;
    pv.reserve((itend-it)*vs);
    if (modulo>=46340){
      for (;it!=itend;++it){
	for (jt=jtbeg,j=1;jt!=jtend;++j,++jt){
	  if (*jt)
	    pv.push_back(T_unsigned<int,hashgcd_U>((it->g*longlong(*jt))%modulo,it->u+(vs-j)*var));
	}
      }
    }
    else {
      for (;it!=itend;++it){
	for (jt=jtbeg,j=1;jt!=jtend;++j,++jt){
	  if (*jt)
	    pv.push_back(T_unsigned<int,hashgcd_U>((it->g*(*jt))%modulo,it->u+(vs-j)*var));
	}
      }
    }
  }

  // distribute multiplication
  static void distmult(const vector< T_unsigned<vector<int>,hashgcd_U> > & p,const vector<int> & v,vector< T_unsigned<vector<int>,hashgcd_U> > & pv,hashgcd_U var,int modulo){
    if (&pv==&p){
      vector< T_unsigned<vector<int>,hashgcd_U> > tmp;
      distmult(p,v,tmp,var,modulo);
      swap(pv,tmp);
      return;
    }
    pv.clear();
    vector< T_unsigned<vector<int>,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    vector<int> tmp;
    vector<int>::const_iterator jtbeg=v.begin(),jtend=v.end(),jt;
    int vs=jtend-jtbeg,j;
    pv.reserve((itend-it)*vs);
    for (;it!=itend;++it){
      for (jt=jtbeg,j=1;jt!=jtend;++j,++jt){
	if (*jt){
	  tmp=it->g;
	  mulmod(*jt,tmp,modulo);
	  pv.push_back( T_unsigned<vector<int>,hashgcd_U>(tmp,it->u+(vs-j)*var));
	}
      }
    }
  }

#if 0
  static void distmult(const vector<int> & p,const vector<int> & v,vector< vector<int> > & pv,int modulo){
    vector<int>::const_iterator it=p.begin(),itend=p.end(),jt=v.begin(),jtend=v.end();
    pv.clear();
    pv.insert(pv.end(),(itend-it),vector<int>(0));
    if (modulo<=46340){
      for (int i=0;it!=itend;++it,++i){
	vector<int> & w = pv[i];
	const int & fact = *it;
	if (fact){
	  w.reserve(v.size());
	  for (jt=v.begin();jt!=jtend;++jt)
	    w.push_back(fact*(*jt) % modulo);
	}
      }
    }
    else {
      for (int i=0;it!=itend;++it,++i){
	vector<int> & w = pv[i];
	const int & fact = *it;
	if (fact){
	  w.reserve(v.size());
	  for (jt=v.begin();jt!=jtend;++jt)
	    w.push_back( (longlong(fact)*(*jt)) % modulo);
	}
      }
    }
  }

  static void mulmod(const vector<int> & v,int m,vector<int> & w,int modulo){
    if (&v==&w){
      mulmod(w,m,modulo);
      return;
    }
    vector<int>::const_iterator jt=v.begin(),jtend=v.end();
    w.clear();
    w.reserve(jtend-jt);
    if (modulo>=46340){
      for (;jt!=jtend;++jt)
	w.push_back(smod(*jt * longlong(m),modulo));
    }
    else {
      for (;jt!=jtend;++jt)
	w.push_back(smod(*jt * m,modulo));
    }
  }
#endif

  // if all indices are == return -2
  // if all indices corr. to u1 are <= to u2 return 1,
  // if all are >= to u2 return 0
  // otherwise return -1
  int compare(hashgcd_U u1,hashgcd_U u2,const vector<hashgcd_U> & vars){
    if (u1==u2)
      return -2;
    std::vector<hashgcd_U>::const_iterator it=vars.begin(),itend=vars.end();
    hashgcd_U r1,r2;
    int res=-2;
    for (;it!=itend;++it){
      r1=u1%(*it);
      r2=u2%(*it);
      if (r1==r2)
	continue;
      if (res==-2){
	res=r1<r2;
	continue;
      }
      if (r1<r2){
	if (res)
	  continue;
	return -1;
      }
      else {
	if (!res)
	  continue;
	return -1;
      }
    }
    return res;
  }

  // if all indices are == return -2
  // if all indices corr. to u1 are <= to u2 return 1,
  // if all are >= to u2 return 0
  // otherwise return -1
  static int compare(const index_t & u1,const index_t & u2){
    if (u1==u2)
      return -2;
    index_t::const_iterator it=u1.begin(),itend=u1.end(),jt=u2.begin();
    int res=-2;
    for (;it!=itend;++it,++jt){
      if (*it==*jt)
	continue;
      if (res==-2){
	res=*it<*jt;
	continue;
      }
      if (*it<*jt){
	if (res)
	  continue;
	return -1;
      }
      else {
	if (!res)
	  continue;
	return -1;
      }    
    }
    return res;
  }

  inline bool is_one(const vector< T_unsigned<int,hashgcd_U> > & p){
    return p.size()==1 && p.front().g==1 && p.front().u==0;
  }

  // Note that smallmult may fail if the degree of a and b * modulo^2 
  // overflows in a longlong, so keep modulo not too large
  static void smallmult(const vector<int>::const_iterator & ita0,const vector<int>::const_iterator & ita_end,const vector<int>::const_iterator & itb0,const vector<int>::const_iterator & itb_end,vector<int> & new_coord,int modulo){
    longlong test=longlong(modulo)*std::min(ita_end-ita0,itb_end-itb0);
    bool large=test/RAND_MAX>RAND_MAX/modulo;
    new_coord.clear();
    vector<int>::const_iterator ita_begin=ita0,ita=ita0,itb=itb0;
    for ( ; ita!=ita_end; ++ita ){
      vector<int>::const_iterator ita_cur=ita,itb_cur=itb;
      if (large){
	int res=0;
	for (;itb_cur!=itb_end;--ita_cur,++itb_cur) {
	  res = (res + *ita_cur * longlong(*itb_cur))%modulo ;
	  if (ita_cur==ita_begin)
	    break;
	}
	new_coord.push_back(smod(res,modulo));
      }
      else {
	longlong res=0;
	for (;itb_cur!=itb_end;--ita_cur,++itb_cur) {
	  res += *ita_cur * longlong(*itb_cur) ;
	  if (ita_cur==ita_begin)
	    break;
	}
	new_coord.push_back(smod(res,modulo));
      }
    }
    --ita;
    ++itb;
    for ( ; itb!=itb_end;++itb){
      vector<int>::const_iterator ita_cur=ita,itb_cur=itb;
      if (large){
	int res=0;
	for (;;) {
	  res = (res + *ita_cur * longlong(*itb_cur))%modulo ;
	  if (ita_cur==ita_begin)
	    break;
	  --ita_cur;
	  ++itb_cur;
	  if (itb_cur==itb_end)
	    break;
	}
	new_coord.push_back(smod(res,modulo));
      }
      else {
	longlong res= 0;
	for (;;) {
	  res += *ita_cur * longlong(*itb_cur) ;
	  if (ita_cur==ita_begin)
	    break;
	  --ita_cur;
	  ++itb_cur;
	  if (itb_cur==itb_end)
	    break;
	}
	new_coord.push_back(smod(res,modulo));
      }    
    }
  }

#if 0
  static void smallmult1(const vector< T_unsigned<int,hashgcd_U> > & p,const vector< T_unsigned<int,hashgcd_U> > & q,vector< T_unsigned<int,hashgcd_U> > & res,hashgcd_U var,int modulo){
    vector<int> p1,q1,r1;
    convert(p,var,p1,modulo);
    convert(q,var,q1,modulo);
    r1.reserve(p1.size()+q1.size()-1);
    smallmult(p1.begin(),p1.end(),q1.begin(),q1.end(),r1,modulo);
    convert_back(r1,var,res);
  }
#endif

  static void convert(const vector< T_unsigned<int,hashgcd_U> > & p,hashgcd_U var,hashgcd_U var2,vector< vector<int> > & v,int modulo){
    if (p.empty()){
      v.clear();
      return;
    }
    int dim1=p.front().u/var,dim2;
    if (int(v.size())!=dim1+1){
      vector< vector<int> > tmp(dim1+1);
      swap(tmp,v);
    }
    vector< T_unsigned<int,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    hashgcd_U u,uend;
    int deg,prevdeg=0;
    for (;it!=itend;){
      u=it->u;
      deg=u/var;
      if (prevdeg){
	for (--prevdeg;prevdeg>deg;--prevdeg){
	  v[dim1-prevdeg].clear();
	}
      }
      prevdeg=deg;
      uend=deg*var;
      dim2=(u-uend)/var2;
      vector<int> & vi = v[dim1-deg];
      if (int(vi.size())!=dim2+1){
	vector<int> tmp(dim2+1);
	swap(vi,tmp);
      }
      else
	fill(vi.begin(),vi.end(),0);
      if (dim2<itend-it && (it+dim2)->u==uend){
	vector<int>::iterator jt=vi.begin(),jtend=vi.end();
	for (;jt!=jtend;++it,++jt){
	  *jt = it->g;
	  if (*jt>=0)
	    continue;
	  *jt += modulo;
	}
	continue;
      }
      for (;it!=itend;++it){
	u=it->u;
	if (u<uend)
	  break;
	vi[dim2-(u-uend)/var2]=it->g<0?it->g+modulo:it->g;
      }
    }
    for (--prevdeg;prevdeg>=0;--prevdeg)
      v[dim1-prevdeg].clear();
  }

  static void horner_back(const vector< vector<int> > & v,int x,vector<int> & vx,int modulo,int maxdeg=-1,bool unsig=false){
    vector< vector<int> >::const_iterator it=v.begin(),itend=v.end();
    if (maxdeg>=0 && maxdeg<itend-it)
      it = itend-(maxdeg+1);
    vector<int>::iterator jt=vx.begin(),jtend=vx.end();
    if (jtend-jt>=itend-it){
      for (;it!=itend;++it,++jt){
	*jt=hornermod(*it,x,modulo,unsig);
      }
      if (jt!=jtend)
	vx.erase(jt,jtend);
    }
    else {
      vx.clear();
      vx.reserve(itend-it);
      for (;it!=itend;++it){
	vx.push_back(hornermod(*it,x,modulo,unsig));
      }
    }
  }

  static void horner_front(const vector< vector<int> > & v,int x,vector<int> & vx,int modulo){
    if (!x && !v.empty()){
      vx=v.back();
      return;
    }
    vx.clear();
    vector< vector<int> >::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      mulmod(vx,x,modulo);
      addmod(vx,*it,modulo);
    }
    // trim(vx);
  }

  static bool is_equal_modulo(const vector<int> & v,const vector<int> & w,int modulo){
    vector<int>::const_iterator it=v.begin(),itend=v.end(),jt=w.begin(),jtend=w.end();
    if (itend-it!=jtend-jt)
      return false;
    for (;it!=itend;++jt,++it){
      if ((*it-*jt)%modulo)
	return false;
    }
    return true;
  }

  static bool is_p_a_times_b(const vector<int> & p,const vector<int> & a,const vector<int> & b,int modulo,int maxdeg){
    int as=a.size(),bs=b.size(),ps=p.size();
    if (ps!=as+bs-1 && ps!=maxdeg+1)
      return false;
    if (ps<=maxdeg){
      vector<int> r;
      smallmult(a.begin(),a.end(),b.begin(),b.end(),r,modulo);
      return is_equal_modulo(r,p,modulo);
    }
    longlong test=longlong(modulo)*std::min(maxdeg+1,std::min(as,bs));
    bool large=test/RAND_MAX>RAND_MAX/modulo;
    int j;
    vector<int>::const_iterator it,itbeg=a.begin(),jt,jtend=b.end();
    for (int i=0;i<=maxdeg;++i){
      // degree i==? p[ps-i-1]= sum_j a[as-1-j]*b[bs-1-i+j]
      // starting value for j: 0 or i+1-bs
      j=i+1-bs;
      if (j<0)
	j=0;
      it=itbeg+as-1-j;
      jt=b.begin()+bs-1-i+j;
      // end value: a.begin() or b.end()
      if (large){
	int res = - p[ps-i-1];
	for (;jt!=jtend;--it,++jt){
	  res = (res+(*it)* longlong(*jt))%modulo;
	  if (it==itbeg)
	    break;
	}
	if (res)
	  return false;
      }
      else {
	longlong res = - p[ps-i-1];
	for (;jt!=jtend;--it,++jt){
	  res += (*it)*longlong(*jt);
	  if (it==itbeg)
	    break;
	}
	if (res%modulo)
	  return false;
      }
    }
    return true;
  }

  static bool is_p_a_times_b(const vector< T_unsigned<int,hashgcd_U> > & p,const vector< T_unsigned<int,hashgcd_U> > & a,const vector< T_unsigned<int,hashgcd_U> > & b,const std::vector<hashgcd_U> & vars,int modulo,int maxtotaldeg){
    if (a.empty() || b.empty())
      return p.empty();
    if (p.empty())
      return false;
    int dim=vars.size()-1;
    if (dim<=0)
      return false; // setdimerr();
    // double as=a.size(),bs=b.size();
    double ps=p.size();
    hashgcd_U var2=vars[dim-1];
    if (dim==1){
      // ? dense vector<int> multiplication or sparse mult ?
      unsigned adeg=a.front().u/var2,bdeg=b.front().u/var2,pdeg=p.front().u/var2;
      if (pdeg!=adeg+bdeg && int(pdeg)!=maxtotaldeg)
	return false;
      // double timesparse=as*bs*std::log(ps);
      // double timedense=(adeg+1)*(bdeg+1);
      if (true
	  // timedense<timesparse
	  ){
	vector<int> p1,a1,b1,r1;
	convert(a,var2,a1,modulo);
	convert(b,var2,b1,modulo);
	convert(p,var2,p1,modulo);
	return is_p_a_times_b(p1,a1,b1,modulo,maxtotaldeg);
      }
      // FIXME take maxdeg in account
      vector< T_unsigned<int,hashgcd_U> > r;
      smallmult(a,b,r,modulo,size_t(ps+1));
      smallsub(r,p,r,modulo);
      return r.empty();
    }
    hashgcd_U var=vars[dim-2];
    int shift_var=find_shift(var),shift_var2=find_shift(var2);
    unsigned adeg=degree_xn(a,shift_var,shift_var2),bdeg=degree_xn(b,shift_var,shift_var2),pdeg=degree_xn(p,shift_var,shift_var2);
    int ntests=std::min(int(pdeg),maxtotaldeg);
    if (pdeg!=adeg+bdeg && int(pdeg)!=maxtotaldeg)
      return false;
    // multiplication requires as*bs*ln(ps) operations
    // alternative is checking pdeg+1 values of x=xn
    // time for 1 check: ps+as+bs (horner)+ dim^2*as/adeg*bs/bdeg*ln(ps)
    // double timemult=3*as*bs*std::log(ps);
    // double timeeval=(ntests+1)*(ps+as+bs+dim*dim*as/adeg*bs/bdeg*std::log(ps));
    if (false
	// timemult<timeeval
	){
      vector< T_unsigned<int,hashgcd_U> > r;
      smallmult(a,b,r,modulo,size_t(ps));
      smallsub(r,p,r,modulo);
      // FIXME take maxdeg in account
      return r.empty();
    }
    if (dim==2){
      vector< vector<int> > pv,av,bv;
      vector<int> pi,ai,bi,ri;
      convert(p,var,var2,pv,modulo);
      convert(a,var,var2,av,modulo);
      convert(b,var,var2,bv,modulo);
      for (int i=0;i<=ntests;++i){
	int alpha=i%2?-(i+1)/2:i/2;
	horner_back(pv,alpha,pi,modulo,maxtotaldeg,true);
	horner_back(av,alpha,ai,modulo,maxtotaldeg,true);
	horner_back(bv,alpha,bi,modulo,maxtotaldeg,true);
	if (!is_p_a_times_b(pi,ai,bi,modulo,maxtotaldeg))
	  return false;
	--maxtotaldeg;
      }
      return true;
    }
    vector< T_unsigned<int,hashgcd_U> > pi,ai,bi;
    std::vector<hashgcd_U> vars_truncated(vars);
    vars_truncated.pop_back();
    for (int i=0;i<=ntests;++i){
      if (!horner(p,i,vars_truncated,pi,modulo,maxtotaldeg) ||
	  !horner(a,i,vars_truncated,ai,modulo,maxtotaldeg) ||
	  !horner(b,i,vars_truncated,bi,modulo,maxtotaldeg))
	return false;
      if (!is_p_a_times_b(pi,ai,bi,vars_truncated,modulo,maxtotaldeg))
	return false;
      --maxtotaldeg;
    }
    return true;
  }

  /*
  template<class T>
  void divided_differences(const vector<int> & x,vector< vector< T_unsigned<T,hashgcd_U> > > & res,int modulo){
    int s=x.size();
    int fact;
    for (int k=1;k<s;++k){
      for (int j=s-1;j>=k;--j){
	smallsub(res[j],res[j-1],res[j],modulo);
	fact=invmod(x[j]-x[j-k],modulo);
	if (fact!=1)
	  smallmult(fact,res[j],res[j],modulo);
      }
    }
  }
  */

  template<class T>
  static void divided_differences(const vector<int> & x,vector< vector< T_unsigned<T,hashgcd_U> > > & res,int modulo){
    int s=x.size();
    int fact;
    vector< T_unsigned<T,hashgcd_U> > tmp;
    for (int k=1;k<s;++k){
      for (int j=s-1;j>=k;--j){
	smallsub(res[j],res[j-1],tmp,modulo);
	swap(tmp,res[j]);
	fact=invmod(x[j]-x[j-k],modulo);
	if (fact!=1)
	  smallmult(fact,res[j],res[j],modulo);
      }
    }
  }

// Lagrange interpolation at x/y
  template<class T>
  static void interpolate(const vector<int> & x,vector< vector< T_unsigned<T,hashgcd_U> > > & diff,vector< T_unsigned<T,hashgcd_U> > & res,hashgcd_U varx,int modulo){
    divided_differences(x,diff,modulo);
    int s=diff.size();
    vector<int> interp(1,1);
    res=diff.front();
    int alpha;
    vector< T_unsigned<T,hashgcd_U> > tmp,tmp2;
    for (int j=1;j<s;++j){
      alpha=x[j-1];
      interp.push_back(smod(-longlong(alpha)*interp[j-1],modulo));
      for (int i=j-1;i>0;--i){
	interp[i]=smod(-longlong(alpha)*interp[i-1]+interp[i],modulo);
      }
      distmult(diff[j],interp,tmp,varx,modulo);
      smalladd(res,tmp,tmp2,modulo);
      swap(tmp2,res);
    }
    /*
    res=diff[s-1];
    vector< T_unsigned<int,hashgcd_U> > res_shift,res_times;
    for (int i=s-2;i>=0;--i){
      // res = res*(x-x[i])+diff[i];
      smallshift(res,varx,res_shift);
      smallmult(-x[i],res,res_times,modulo);
      smalladd(res_times,diff[i],res_times,modulo);
      smalladd(res_shift,res_times,res,modulo);
    }
    */
  }

  static void divided_differences(const vector<int> & x,vector< vector<int> > & res,int modulo){
    int s=x.size();
    int fact;
    for (int k=1;k<s;++k){
      for (int j=s-1;j>=k;--j){
	fact=invmod(x[j]-x[j-k],modulo);
	mulsubmod(fact,res[j],res[j-1],modulo);
	/*
	submod(res[j],res[j-1],modulo);
	if (fact!=1)
	  mulmod(res[j],fact,modulo);
	*/
      }
    }
  }

  // Lagrange interpolation at x/y
  static void interpolate(const vector<int> & x,vector< vector<int> > & diff,vector< vector<int> > & res,int modulo){
    divided_differences(x,diff,modulo);
    // cerr << "end diff div " << clock() << endl;
    res.clear();
    int s=diff.size(),alpha;
    int ysize=0,cur;
    for (int i=0;i<s;++i){
      if ( (cur=diff[i].size()) >ysize )
	ysize=cur;
    }
    res.reserve(ysize);
    for (int i=ysize-1;i>=0;--i){
      res.push_back(vector<int>(0));
      vector<int> & curx = res.back();
      vector<int> & cury = diff[s-1];
      if ( (cur=cury.size()) >i)
	curx.push_back(cury[cur-1-i]);
      for (int j=s-2;;--j){
	// multiply curx by (x-x[j])
	alpha=-x[j];
	if (!curx.empty()){
	  curx.push_back( (longlong(alpha)*curx.back()) % modulo );
	  vector<int>::iterator it=curx.end()-2,itbeg=curx.begin();
	  for (;it!=itbeg;){
	    int & curxk=*it;
	    --it;
	    type_operator_plus_times_reduce(alpha,*it,curxk,modulo);
	  }
	}
	// add diff[j]
	vector<int> & cury = diff[j];
	if ( (cur=cury.size()) >i){
	  if (curx.empty())
	    curx.push_back(cury[cur-1-i]);
	  else
	    curx.back() = (curx.back() + cury[cur-1-i])%modulo;
	}
	if (!j)
	  break;
      }
    }
    /*
    vector<int> interp(1,1);
    distmult(diff[0],interp,res,modulo);
    vector< vector<int> >::iterator it=res.begin(),itend=res.end();
    for (;it!=itend;++it)
      it->reserve(s);
    vector< vector<int> > tmp;
    for (int j=1;j<s;++j){
      alpha=x[j-1];
      interp.push_back(smod(-longlong(alpha)*interp[j-1],modulo));
      for (int i=j-1;i>0;--i){
	interp[i]=smod(-longlong(alpha)*interp[i-1]+interp[i],modulo);
      }
      distmult(diff[j],interp,tmp,modulo);
      addmod(res,tmp,modulo);
    }
    */
    // unsigne res
    s=res.size();
    for (int j=0;j<s;++j){
      vector<int> & cur=res[j];
      vector<int>::iterator it=cur.begin(),itend=cur.end();
      for (;it!=itend;++it){
	if (*it<0)
	  *it += modulo;
      }
    }
  }

  static void interpolate(const vector<int> & x,vector< vector<int> > & y,vector< T_unsigned<int,hashgcd_U> > & res,int varxn,int var2,int modulo){
    vector< vector<int> > tmp;
    interpolate(x,y,tmp,modulo);
    convert_back(tmp,varxn,var2,res);
  }

  template<class T>
  struct gcd_call_param {
    vector<T> * Delta ;
    vector<T> * lcoeffp ;
    vector<T> * lcoeffq ;
    vector<int> * alphav;
    vector< vector<T> > * pv ;
    vector< vector<T> > * qv ;
    vector< vector<T> > * dim2gcdv ;
    vector< vector<T> > * dim2pcofactorv ;
    vector< vector<T> > * dim2qcofactorv ;
    vector< T_unsigned<T,hashgcd_U> > * p ;
    vector< T_unsigned<T,hashgcd_U> > * q ;
    vector< vector< T_unsigned<T,hashgcd_U> > > * gcdv ;
    vector< vector< T_unsigned<T,hashgcd_U> > > * pcofactorv ;
    vector< vector< T_unsigned<T,hashgcd_U> > > * qcofactorv ;
    index_t * pdeg ;
    index_t * qdeg ;
    const vector<hashgcd_U> * vars ;
    vector<hashgcd_U> * vars_truncated ;
    index_t * shift_vars ;
    index_t * shift_vars_truncated ;
    bool compute_cof ;
    bool compute_qcofactor ;
    bool dim2;
    const vector<int> * pminptr;
    int modulo ;
    int vpos ;
    int nthreads ;
    int ext_gcd_ok ; // used for gcd over algebraic extension of Q
  };

  static bool mod_gcd(const vector< T_unsigned<int,hashgcd_U> > & p_orig,const vector< T_unsigned<int,hashgcd_U> > & q_orig,int modulo,vector< T_unsigned<int,hashgcd_U> > & d, vector< T_unsigned<int,hashgcd_U> > & pcofactor, vector< T_unsigned<int,hashgcd_U> > & qcofactor,const std::vector<hashgcd_U> & vars, bool compute_pcofactor,bool compute_qcofactor,bool & divtest,vector< vector<int> > & pv,vector< vector<int> > & qv,int nthreads);

#if !defined( RTOS_THREADX) && !defined(BESTA_OS) && !defined(EMCC)
  static void * do_recursive_gcd_call(void * ptr_){
    if (ctrl_c || interrupted)
      return 0;
    gcd_call_param<int> * ptr = (gcd_call_param<int> *) ptr_;
    vector<int> & Delta = *ptr->Delta;
    vector<int> & lcoeffp = *ptr->lcoeffp;
    vector<int> & lcoeffq = *ptr->lcoeffq;
    vector<int> & alphav = * ptr->alphav;
    vector< vector<int> > & pv = *ptr->pv;
    vector< vector<int> > & qv = *ptr->qv;
    vector<int> dim2palpha;
    vector<int> dim2qalpha;
    vector< vector<int> > & dim2gcdv = *ptr->dim2gcdv;
    vector< vector<int> > & dim2pcofactorv = *ptr->dim2pcofactorv;
    vector< vector<int> > & dim2qcofactorv = *ptr->dim2qcofactorv;
    vector< T_unsigned<int,hashgcd_U> > & p = * ptr->p;
    vector< T_unsigned<int,hashgcd_U> > & q = * ptr->q;
    vector< vector< T_unsigned<int,hashgcd_U> > > & gcdv = * ptr->gcdv;
    vector< vector< T_unsigned<int,hashgcd_U> > > & pcofactorv = * ptr->pcofactorv;
    vector< vector< T_unsigned<int,hashgcd_U> > > & qcofactorv = * ptr->qcofactorv;
    index_t & pdeg = * ptr->pdeg;
    index_t & qdeg = * ptr->qdeg;
    vector< T_unsigned<int,hashgcd_U> > palpha,qalpha;
    index_t pdegalpha,qdegalpha;
    int vpos=ptr->vpos;
    int alpha1 = alphav[vpos];
    int modulo = ptr->modulo;
    const vector<hashgcd_U> & vars = * ptr->vars;
    vector<hashgcd_U> & vars_truncated = * ptr->vars_truncated;
    // index_t & shift_vars = *ptr->shift_vars;
    index_t & shift_vars_truncated = *ptr->shift_vars_truncated;
    bool compute_cof = ptr->compute_cof;
    bool compute_qcofactor = ptr->compute_qcofactor;
    bool dim2 = ptr->dim2;
    int nthreads = ptr->nthreads;
    // Eval p and q at xn=alpha
    if (dim2){
      horner_back(pv,alpha1,dim2palpha,modulo,-1,true);
      if ( int(dim2palpha.size())-1 != pdeg.front())
	return 0;
      // convert(dim2palpha,varxn,palpha);
      horner_back(qv,alpha1,dim2qalpha,modulo,-1,true);
      if ( int(dim2qalpha.size())-1 != qdeg.front())
	return 0;
      // convert(dim2qalpha,varxn,qalpha);
    }
    else {
      if (!horner(p,alpha1,vars,palpha,modulo,-1))
	return 0;
      degree(palpha,shift_vars_truncated,pdegalpha);
      pdegalpha.push_back(pdeg.back());
      if (pdegalpha!=pdeg)
	return 0;
      if (!horner(q,alpha1,vars,qalpha,modulo,-1))
	return 0;
      degree(qalpha,shift_vars_truncated,qdegalpha);
      qdegalpha.push_back(qdeg.back());
      if (qdegalpha!=qdeg)
	return 0;
    }
    if (dim2){
      gcdsmallmodpoly(dim2palpha,dim2qalpha,modulo,dim2gcdv[vpos],compute_cof?&dim2pcofactorv[vpos]:0,(compute_cof && compute_qcofactor)?&dim2qcofactorv[vpos]:0);
      mulmod(dim2gcdv[vpos],smod(hornermod(Delta,alpha1,modulo)*longlong(invmod(dim2gcdv[vpos].front(),modulo)),modulo),modulo);
      if (compute_cof){
	mulmod(dim2pcofactorv[vpos],smod(hornermod(lcoeffp,alpha1,modulo)*longlong(invmod(dim2pcofactorv[vpos].front(),modulo)),modulo),modulo);
	if (compute_qcofactor){
	  mulmod(dim2qcofactorv[vpos],smod(hornermod(lcoeffq,alpha1,modulo)*longlong(invmod(dim2qcofactorv[vpos].front(),modulo)),modulo),modulo);
	}
      }
    }
    else {
      vector< T_unsigned<int,hashgcd_U> > & g=gcdv[vpos];
      vector< T_unsigned<int,hashgcd_U> > & gp=pcofactorv[vpos];
      vector< T_unsigned<int,hashgcd_U> > & gq=qcofactorv[vpos];
      bool tmptestdiv;
      if (&pv && &qv){
	if (!mod_gcd(palpha,qalpha,modulo,g,gp,gq,vars_truncated,compute_cof,compute_qcofactor,tmptestdiv,pv,qv,nthreads)){
	  g.clear();
	  return 0;
	}
      }
      else {
	vector< vector<int> > pv1,qv1;
	if (!mod_gcd(palpha,qalpha,modulo,g,gp,gq,vars_truncated,compute_cof,compute_qcofactor,tmptestdiv,pv1,qv1,nthreads)){
	  g.clear();
	  return 0;
	}
      }
      smallmult(smod(hornermod(Delta,alpha1,modulo)*longlong(invmod(g.front().g,modulo)),modulo),g,g,modulo);
      if (compute_cof){
	// adjust gp lcoeff
	smallmult(smod(hornermod(lcoeffp,alpha1,modulo)*longlong(invmod(gp.front().g,modulo)),modulo),gp,gp,modulo);
	if (compute_qcofactor){
	  // adjust gq cofactor
	  smallmult(smod(hornermod(lcoeffq,alpha1,modulo)*longlong(invmod(gq.front().g,modulo)),modulo),gq,gq,modulo);
	}
      }
    }
    return ptr;
  }
#endif //RTOS_THREADX

  // Modular gcd in "internal form"
  static bool mod_gcd(const vector< T_unsigned<int,hashgcd_U> > & p_orig,const vector< T_unsigned<int,hashgcd_U> > & q_orig,int modulo,vector< T_unsigned<int,hashgcd_U> > & d, vector< T_unsigned<int,hashgcd_U> > & pcofactor, vector< T_unsigned<int,hashgcd_U> > & qcofactor,const std::vector<hashgcd_U> & vars, bool compute_pcofactor,bool compute_qcofactor,bool & divtest,vector< vector<int> > & pv,vector< vector<int> > & qv,int nthreads){
#if defined( RTOS_THREADX) || defined(BESTA_OS) || defined(EMCC)
    return false;
#else
    divtest=true;
    if (p_orig.empty() || is_one(q_orig) ){
      d=q_orig;
      if (compute_pcofactor)
	pcofactor=p_orig;
      if (compute_qcofactor){
	if (p_orig.empty()){
	  qcofactor.clear();
	  qcofactor.push_back(T_unsigned<int,hashgcd_U>(1,0));
	}
	else
	  qcofactor=q_orig;
      }
      return true;
    }
    if (q_orig.empty() || is_one(p_orig) ){
      d=p_orig;
      if (compute_qcofactor)
	qcofactor=q_orig;
      if (compute_pcofactor){
	if (q_orig.empty()){
	  pcofactor.clear();
	  pcofactor.push_back(T_unsigned<int,hashgcd_U>(1,0));
	}
	else
	  pcofactor=p_orig;
      }
      return true;
    }
    if (&p_orig==&d || &q_orig==&d){
      vector< T_unsigned<int,hashgcd_U> > res;
      bool b=mod_gcd(p_orig,q_orig,modulo,res,pcofactor,qcofactor,vars,compute_pcofactor,compute_qcofactor,divtest,qv,pv,nthreads);
      swap(res,d);
      return b;
    }
    d.clear();
    // Check dim, if 1 ->
    int dim=vars.size();
    if (dim==1){
      hashgcd_U var=vars.front();
      vector<int> pv,qv,dv,pvcof,qvcof;
      convert(p_orig,var,pv,modulo);
      convert(q_orig,var,qv,modulo);
      gcdsmallmodpoly(pv,qv,modulo,dv,compute_pcofactor?&pvcof:0,compute_qcofactor?&qvcof:0);
      convert_back(dv,var,d);
      if (compute_pcofactor){
	convert_back(pvcof,var,pcofactor);
      }
      if (compute_qcofactor){
	convert_back(qvcof,var,qcofactor);
      }
      return true;
    }
    std::vector<hashgcd_U> vars_truncated(vars);
    vars_truncated.pop_back();
    hashgcd_U varxn=vars_truncated.back(),var2=vars.back();
    index_t shift_vars,shift_vars_truncated;
    if (!find_shift(vars,shift_vars))
      return false; // setsizeerr();
    shift_vars_truncated=shift_vars;
    shift_vars_truncated.pop_back();
    short int shiftxn=shift_vars_truncated.back(),shift2=shift_vars.back();
    // Make p and q primitive as polynomials in x1,...,xn-1
    // with coeff polynomial in xn
    vector< T_unsigned<int,hashgcd_U> > p(p_orig),q(q_orig),pcont,qcont,dcont,tmp;
    vector<int> pcontxn,qcontxn,dcontxn,pcofcontxn,qcofcontxn;
    if (debug_infolevel>20-dim)
      cerr << "gcdmod threads " << nthreads << " content begin " << "dim " << dim << " " << clock() << endl;
    pp_mod(p,0,modulo,varxn,var2,pcontxn);
    pp_mod(q,0,modulo,varxn,var2,qcontxn);
    gcdsmallmodpoly(pcontxn,qcontxn,modulo,dcontxn,compute_pcofactor?&pcofcontxn:0,compute_qcofactor?&qcofcontxn:0);
    if (debug_infolevel>20-dim)
      cerr << "gcdmod content in " << "dim " << dim << " " << clock() << endl;
    // Make p and q primitive as polynomial in xn with coeff in x1...xn-1
    vector< vector<int> > dv,dpv,dim2gcdv,dim2pcofactorv,dim2qcofactorv;
    if (dim==2){
      convert(p,varxn,var2,pv,modulo);
      if (is_front_primitive(pv,modulo))
	pcont.push_back(T_unsigned<int,hashgcd_U>(1,0));
      else {
	pp_mod(p,0,modulo,vars,pcont,nthreads);
	if (!is_one(pcont))
	  convert(p,varxn,var2,pv,modulo);
      }
      convert(q,varxn,var2,qv,modulo);
      if (is_front_primitive(qv,modulo))
	qcont.push_back(T_unsigned<int,hashgcd_U>(1,0));
      else {
	pp_mod(q,0,modulo,vars,qcont,nthreads);
	if (!is_one(qcont))
	  convert(q,varxn,var2,qv,modulo);
      }
    }
    else {
      pp_mod(p,0,modulo,vars,pcont,nthreads);
      pp_mod(q,0,modulo,vars,qcont,nthreads);
    }
    mod_gcd(pcont,qcont,modulo,dcont,pcofactor,qcofactor,vars_truncated,compute_pcofactor,compute_qcofactor,nthreads); // don't use pv and qv here!
    // multiply pcofactor and qcofactor by the initial contents dep. on xn
    if (debug_infolevel>20-dim)
      cerr << "gcdmod content end " << "dim " << dim << " " << clock() << endl;
    if (compute_pcofactor){
      convert_back(pcofcontxn,vars.back(),tmp);
      smallmult(pcofactor,tmp,pcofactor,modulo,0);
    }
    if (compute_qcofactor){
      convert_back(qcofcontxn,vars.back(),tmp);
      smallmult(qcofactor,tmp,qcofactor,modulo,0);
    }
    distmult(dcont,dcontxn,dcont,var2,modulo);
    // ready for gcd computation by interpolation with respect to xn
    // first find degree of gcd with respect to xn
    int pxndeg=degree_xn(p,shiftxn,shift2),qxndeg=degree_xn(q,shiftxn,shift2),gcddeg=0;
    vector<int> pb(pxndeg+1),qb(qxndeg+1),db,b(dim-1),bnext(dim-1);
    index_t vzero; // coeff of vzero correspond to zero or non zero
    int nzero=1; // Number of zero coeffs
    for (int essai=0;essai<2;){
      if (debug_infolevel>20-dim)
	cerr << "gcdmod degree? " << essai << " dim " << dim << " " << clock() << endl;
      if (dim==2){
	horner_front(pv,b.front(),pb,modulo);
	horner_front(qv,b.front(),qb,modulo);
      }
      else {
	if (!horner(p,b,vars,pb,modulo) ||
	    !horner(q,b,vars,qb,modulo))
	  return false;
      }
      for (;;){
	for (int i=0;i<dim-1;++i)
	  bnext[i]=std::rand() % modulo;
	if (bnext!=b){ b=bnext; break; }
      }
      if (int(pb.size())!=pxndeg+1 || int(qb.size())!=qxndeg+1)
	continue;
      gcdsmallmodpoly(pb,qb,modulo,db);
      int dbdeg=db.size()-1;
      if (!dbdeg){
	d=dcont;
	if (compute_pcofactor){
	  smallmult(pcofactor,p,pcofactor,modulo,0);
	  smallmult(smod(longlong(p_orig.front().g)*invmod(pcofactor.front().g,modulo),modulo),pcofactor,pcofactor,modulo);
	}
	if (compute_qcofactor){
	  smallmult(qcofactor,q,qcofactor,modulo,0);
	  smallmult(smod(longlong(q_orig.front().g)*invmod(qcofactor.front().g,modulo),modulo),qcofactor,qcofactor,modulo);
	}
	return true;
      }
      if (!essai){ // 1st gcd test
	gcddeg=dbdeg;
	nzero=find_nonzero(db,vzero);
	++essai;
	continue;
      }
      // 2nd try
      if (dbdeg<gcddeg){ // 1st try was unlucky, restart 1st try
	gcddeg=dbdeg;
	nzero=find_nonzero(db,vzero);
	continue;
      }
      if (dbdeg!=gcddeg) 
	  continue;
      // Same gcd degree for 1st and 2nd try, keep this degree
      index_t tmp;
      nzero=find_nonzero(db,tmp);
      if (nzero){
	vzero = vzero | tmp;
	// Recompute nzero, it is the number of 0 coeff of vzero
	index_t::const_iterator it=vzero.begin(),itend=vzero.end();
	for (nzero=0;it!=itend;++it){
	  if (!*it) 
	    ++nzero;
	}
      }
      ++essai;
    } // end for (essai)
    bool pqswap=qxndeg<pxndeg; // swap p and q ?
    if (pqswap){
      swap(p,q);
      swap(pcont,qcont);
      swap(pcofactor,qcofactor);
      swap(pxndeg,qxndeg);
      swap(pv,qv);
#ifdef BESTA_OS
      bool tmpbool=compute_pcofactor;
      compute_pcofactor=compute_qcofactor;
      compute_qcofactor=tmpbool;
      // BESTA DOES NOT LIKE THE FOLLOWING LINE OF CODE
#else
      swap(compute_pcofactor,compute_qcofactor);
#endif

    }
    vector<int> lcoeffp,lcoeffq,lcoeffg,Delta;
    hashgcd_U lcoeffpu,lcoeffqu;
    if (debug_infolevel>20-dim)
      cerr << "gcdmod lcoeff begin " << "dim " << dim << " " << clock() << endl;
    lcoeffpu=lcoeff(p,varxn,var2,lcoeffp);
    lcoeffqu=lcoeff(q,varxn,var2,lcoeffq);
    gcdsmallmodpoly(lcoeffp,lcoeffq,modulo,Delta);
    if (debug_infolevel>20-dim){
      cerr << "lcoeff p, q, gcd" << lcoeffp << "," << lcoeffq << "," << Delta << endl;
      cerr << "gcdmod lcoeff end " << "dim " << dim << " " << clock() << endl;
    }
    // estimate time for full lift or division try
    // size=p.size()+q.size()
    // sumdeg=pxndeg+qxndeg
    // %age=gcddeg/min(pxndeg,qxndeg)
    // %age^dim*(1-%age)^dim*size^2 estimates the time for division try
    // gcddeg*size estimates the time for lifting to gcddeg
    // sumdeg*size estimates the time for full lifting
    // if sumdeg<(gcddeg+%age^dim*(1-%age)^dim*size) do full lifting
    int Deltadeg = Delta.size()-1,liftdeg=(compute_qcofactor?qxndeg:pxndeg)+Deltadeg;
    int gcddeg_plus_delta=gcddeg+Deltadeg;
    int liftdeg0=giacmax(liftdeg-gcddeg,gcddeg_plus_delta);
    // once liftdeg0 is reached we can replace g/gp/gq computation
    // by a check that d*dp=dxn*lcoeff(d*dp)/Delta at alpha
    // and d*dq=dxn*lcoeff(d*dq)/lcoeff(qxn) at alpha
    int sumdeg = pxndeg+qxndeg;
    double percentage = double(gcddeg)/giacmin(pxndeg,qxndeg);
    int sumsize = p.size()+q.size();
    // ? add a malus factor for division
    double gcdlift=gcddeg+std::pow(percentage,dim)*std::pow(1-percentage,dim)*sumsize;
    bool compute_cof = dim==2 || sumdeg<gcdlift;
    // we are now interpolating G=gcd(p,q)*a poly/xn
    // such that the leading coeff of G is Delta
    index_t pdeg(dim),qdeg(dim),pdegalpha(dim),qdegalpha(dim);
    if (debug_infolevel>20-dim)
      cerr << "gcdmod degree begin " << "dim " << dim << " " << clock() << " compute_cof " << compute_cof << "(" << sumdeg/gcdlift << ")" << endl;
    int ptotaldeg=degree(p,shift_vars,pdeg);
    int qtotaldeg=degree(q,shift_vars,qdeg);
    if (debug_infolevel>20-dim){
      cerr << "pdeg " << pdeg << " " << ptotaldeg << endl;
      cerr << "qdeg " << qdeg << " " << qtotaldeg << endl;
    }
    if (debug_infolevel>20-dim)
      cerr << "gcdmod degree end " << "dim " << dim << " " << clock() << endl;
    int spdeg=0,sqdeg=0;
    for (int i=0;i<dim-1;++i){
      spdeg += pdeg[i];
      sqdeg += qdeg[i];
    }
    index_t gdeg(dim-1),delta=min(pdeg,qdeg);
    delta.pop_back();
    int e=0; // number of evaluations
    int alpha,alpha1;
    if (debug_infolevel>20-dim)
      cerr << "gcdmod find alpha dim " << dim << " " << clock() << endl;
    if (debug_infolevel>25-dim)
      cerr << " p " << p << " q " << q << endl;
    vector< T_unsigned<int,hashgcd_U> > palpha,qalpha,dp,dq; // d, dp and dq are the current interpolated values of gcd and cofactors
    vector<int> alphav,dim2palpha,dim2qalpha,tmpcont;
    vector< vector< T_unsigned<int,hashgcd_U> > > gcdv,pcofactorv,qcofactorv;
    // for dim 2
    bool dim2 = dim==2 && compute_cof;
    if (dim2){
      dim2gcdv.reserve(liftdeg0+1);
      dim2pcofactorv.reserve(liftdeg0+1);
      dim2qcofactorv.reserve(liftdeg0+1);
    }
    else {
      gcdv.reserve(liftdeg0+1);
      pcofactorv.reserve(liftdeg0+1);
      qcofactorv.reserve(liftdeg0+1);
    }
    gcd_call_param<int> gcd_par;
    gcd_par.Delta=&Delta;
    gcd_par.lcoeffp=&lcoeffp;
    gcd_par.lcoeffq=&lcoeffq;
    gcd_par.alphav=&alphav;
    gcd_par.pv=&pv;
    gcd_par.qv=&qv;
    gcd_par.dim2gcdv=&dim2gcdv;
    gcd_par.dim2pcofactorv=&dim2pcofactorv;
    gcd_par.dim2qcofactorv=&dim2qcofactorv;
    gcd_par.p=&p;
    gcd_par.q=&q;
    gcd_par.gcdv=&gcdv;
    gcd_par.pcofactorv=&pcofactorv;
    gcd_par.qcofactorv=&qcofactorv;
    gcd_par.pdeg=&pdeg;
    gcd_par.qdeg=&qdeg;
    gcd_par.vars=&vars;
    gcd_par.vars_truncated=&vars_truncated;
    gcd_par.shift_vars=&shift_vars;
    gcd_par.shift_vars_truncated=&shift_vars_truncated;
    gcd_par.compute_cof=compute_cof;
    gcd_par.compute_qcofactor=compute_qcofactor;
    gcd_par.dim2=dim2;
    gcd_par.modulo=modulo;
    if (dim>3 || (dim==3 && sumsize*ptotaldeg*4 > modgcd_cachesize )){
      gcd_par.nthreads=nthreads;
      nthreads=1;
    }
    else
      gcd_par.nthreads=1;
    if (debug_infolevel>20 && nthreads>1)
      cerr << "nthreads " << nthreads << " dim " << dim << " " << sumsize << " " << sumsize*ptotaldeg << endl;
    if (nthreads>gcddeg_plus_delta)
      nthreads=gcddeg_plus_delta+1;
    if (nthreads>1){
      int todo=compute_cof?(liftdeg0+1):(gcddeg_plus_delta+1);
      double nth=todo/double(nthreads);
      // if (nthreads>=4 && (todo%nthreads==0)) nth = (todo+1)/double(nthreads); // keep one proc for a bad prime
      nth=std::ceil(nth);
      nthreads=int(std::ceil(todo/nth));
      if (debug_infolevel>20-dim)
	cerr << "Using " << nthreads << " threads " << nth << " " << todo/nth << endl;
    }
    for (alpha=-1;;){
      // First check if we are ready to interpolate
      if (!compute_cof && e>gcddeg_plus_delta){
	if (dim2)
	  interpolate(alphav,dim2gcdv,d,varxn,var2,modulo);
	else
	  interpolate(alphav,gcdv,d,var2,modulo);
	if (debug_infolevel>20-dim)
	  cerr << "gcdmod pp1mod dim " << dim << " " << clock() << " d " << d << endl;
	vector< T_unsigned<int,hashgcd_U> > pquo,qquo,tmprem,pD(d);
	pp_mod(pD,0,modulo,varxn,var2,tmpcont);
	// This removes the polynomial in xn that we multiplied by
	// (it was necessary to know the lcoeff of the interpolated poly)
	if (debug_infolevel>20-dim)
	  cerr << "gcdmod check dim " << dim << " " << clock() << endl;
	// Now, gcd divides pD for gcddeg+1 values of x1
	// degree(pD)<=degree(gcd)
	if (hashdivrem(p,pD,pquo,tmprem,vars,modulo,0,false)==1 && tmprem.empty()){
	  // If pD divides both P and Q, then the degree wrt variables
	  // x1,...,xn-1 is the right one (because it is <= since pD 
	  // divides the gcd and >= since pD(xn=one of the try) was a gcd
	  // The degree in xn is the right one because of the condition
	  // on the lcoeff
	  // Note that the division test might be much longer than the
	  // interpolation itself (e.g. if the degree of the gcd is small)
	  // but it seems unavoidable, for example if 
	  // P=Y-X+X(X-1)(X-2)(X-3)
	  // Q=Y-X+X(X-1)(X-2)(X-4)
	  // then gcd(P,Q)=1, but if we take Y=0, Y=1 or Y=2
	  // we get gcddeg=1 (probably degree 1 for the gcd)
	  // interpolation at X=0 and X=1 will lead to Y-X as candidate gcd
	  // and even adding X=2 will not change it
	  // We might remove division if we compute the cofactors of P and Q
	  // if P=pD*cofactor is true for degree(P) values of x1
	  // and same for Q, and the degrees wrt xn of pD and cofactors
	  // have sum equal to degree of P or Q + lcoeff then pD is the gcd
	  if (hashdivrem(q,pD,qquo,tmprem,vars,modulo,0,false)==1 && tmprem.empty()){
	    smallmult(pD,dcont,d,modulo,0);
	    smallmult(invmod(d.front().g,modulo),d,d,modulo);
	    if (compute_pcofactor){
	      smallmult(pcofactor,pquo,pcofactor,modulo,0);
	      smallmult(smod(longlong(p_orig.front().g)*invmod(pcofactor.front().g,modulo),modulo),pcofactor,pcofactor,modulo);
	    }
	    if (compute_qcofactor){
	      smallmult(qcofactor,qquo,qcofactor,modulo,0);
	      smallmult(smod(longlong(q_orig.front().g)*invmod(qcofactor.front().g,modulo),modulo),qcofactor,qcofactor,modulo);
	    }
	    if (debug_infolevel>20-dim)
	      cerr << "gcdmod found dim " << dim << " " << clock() << endl;
	    if (pqswap)
	      swap(pcofactor,qcofactor);
	    return true;
	  } // end if hashdivrem(q,...)	      
	} // end if hashdivrem(p,...)
	if (debug_infolevel>20-dim)
	  cerr << "Gcdmod bad guess " << endl;
	// restart
	gcdv.clear(); alphav.clear(); dim2gcdv.clear();
	pcofactorv.clear(); qcofactorv.clear(); 
	dim2pcofactorv.clear(); dim2qcofactorv.clear(); 
	e=0;
      } // end if (e>gcddeg+delta)
      if (compute_cof && e>liftdeg0 ){ 
	// interpolate d and dp
	if (dim2){
	  interpolate(alphav,dim2gcdv,dv,modulo);
	  if (debug_infolevel>20-dim)
	    cerr << "end interpolate gcd " << clock() << endl;
	  convert_back(dv,varxn,var2,d);
	  interpolate(alphav,dim2pcofactorv,dpv,modulo);
	  convert_back(dpv,varxn,var2,dp);
	  if (debug_infolevel>20-dim)
	    cerr << "end interpolate p cof " << clock() << endl;
	}
	else {
	  interpolate(alphav,gcdv,d,var2,modulo);
	  if (debug_infolevel>20-dim)
	    cerr << "end interpolate gcd " << clock() << endl;
	  interpolate(alphav,pcofactorv,dp,var2,modulo);
	  if (debug_infolevel>20-dim)
	    cerr << "end interpolate p cof " << clock() << endl;
	}
	// check that d(alpha)*dp(alpha)=palpha with lcoeff adjusted
	// for e<=liftdeg
	if (dim2){
	  vector<int> dv1,dpv1;
	  for (++alpha;e<=liftdeg;++e,++alpha){
	    alpha1=alpha%2?-(alpha+1)/2:alpha/2;
	    while (hornermod(lcoeffp,alpha1,modulo)==0){
	      ++alpha;
	      alpha1=alpha%2?-(alpha+1)/2:alpha/2;
	    }
	    if (alpha>=modulo)
	      return false;
	    int maxtotaldeg=ptotaldeg+1-e;
	    horner_back(pv,alpha1,dim2palpha,modulo,maxtotaldeg,true);
	    horner_back(dv,alpha1,dv1,modulo,maxtotaldeg,true);
	    horner_back(dpv,alpha1,dpv1,modulo,maxtotaldeg,true);
	    if (debug_infolevel>20){
	      cerr << "palpha " << dim2palpha << endl;
	      cerr << "gcd alpha " << dv1 << endl;
	      cerr << "p-cof alpha " << dpv1 << endl;
	    }
	    mulmod(dpv1,smod(longlong(hornermod(pv.front(),alpha1,modulo))*invmod((hornermod(dv.front(),alpha1,modulo)*longlong(hornermod(dpv.front(),alpha1,modulo)))%modulo,modulo),modulo),modulo);
	    if (!is_p_a_times_b(dim2palpha,dpv1,dv1,modulo,maxtotaldeg)){
	      e=liftdeg0+1;
	      break;
	    }
	  }
	}
	else {
	  vector< T_unsigned<int,hashgcd_U> > g,gp;
	  for (++alpha;e<=liftdeg;++e,++alpha){
	    alpha1=alpha%2?-(alpha+1)/2:alpha/2;
	    while (hornermod(lcoeffp,alpha1,modulo)==0){
	      ++alpha;
	      alpha1=alpha%2?-(alpha+1)/2:alpha/2;
	    }
	    if (alpha>=modulo)
	      return false;
	    int maxtotaldeg=ptotaldeg+1-e;
	    if (!horner(p,alpha1,vars,palpha,modulo,maxtotaldeg))
	      return false;
	    if (debug_infolevel>20-dim)
	      cerr << "gcdmod horner d " << alpha << " dim " << dim << " " << clock() << endl;
	    if (!horner(d,alpha1,vars,g,modulo,maxtotaldeg))
	      return false;
	    if (debug_infolevel>20-dim)
	      cerr << "gcdmod horner dp " << alpha << " dim " << dim << " " << clock() << endl;
	    if (!horner(dp,alpha1,vars,gp,modulo,maxtotaldeg))
	      return false;
	    smallmult(smod(longlong(palpha.back().g)*invmod((gp.back().g*longlong(g.back().g))%modulo,modulo),modulo),gp,gp,modulo);
	    if (!is_p_a_times_b(palpha,gp,g,vars,modulo,maxtotaldeg)){
	      // Bad guess, go find some new gcd and interpolate
	      e=liftdeg0+1;
	      break;
	    }
	  } // end for ( e loop )
	}
	if (e>liftdeg){ 
	  // enough evaluation point
	  // divide d,dp,dq by their content in xn
	  pp_mod(d,0,modulo,varxn,var2,tmpcont);
	  pp_mod(dp,0,modulo,varxn,var2,tmpcont);
	  // check xn degrees of d+dp=degree(pxn), d+dq=degree(qxn)
	  int dxndeg=degree_xn(d,shiftxn,shift2),dpxndeg=degree_xn(dp,shiftxn,shift2);
	  // int dqxndeg=degree_xn(dq,shiftxn,shift2);
	  if ( dxndeg+dpxndeg==pdeg.back() ){
	    smallmult(d,dcont,d,modulo,0);
	    if (compute_pcofactor){
	      smallmult(dp,pcofactor,pcofactor,modulo,0);
	      smallmult(smod(longlong(p_orig.front().g)*invmod(pcofactor.front().g,modulo),modulo),pcofactor,pcofactor,modulo);
	    }
	    if (compute_qcofactor){
	      if (dim2)
		interpolate(alphav,dim2qcofactorv,dq,varxn,var2,modulo);
	      else 
		interpolate(alphav,qcofactorv,dq,var2,modulo);
	      pp_mod(dq,0,modulo,varxn,var2,tmpcont);
	      smallmult(dq,qcofactor,qcofactor,modulo,0);
	      smallmult(smod(longlong(q_orig.front().g)*invmod(qcofactor.front().g,modulo),modulo),qcofactor,qcofactor,modulo);
	    }
	    if (debug_infolevel>20-dim)
	      cerr << "gcdmod end dim " << dim << " " << clock() << endl;
	    if (pqswap)
	      swap(pcofactor,qcofactor);
	    divtest=false;
	    return true;
	  }
	  // failure, restart
	  gcdv.clear(); alphav.clear(); dim2gcdv.clear();
	  pcofactorv.clear(); qcofactorv.clear(); 
	  dim2pcofactorv.clear(); dim2qcofactorv.clear(); 
	  e=0;
	} // end if (e>lifdeg)
      } // end if (compute_cof && e in liftdeg0..liftdeg)

      // *************************************************** //
      // Not ready to interpolate, try to eval new points
      // *************************************************** //

      for (int thread=0;thread<nthreads;++thread){
	// find a good alpha with respect to leading coeff
	for (;;){
	  ++alpha;
	  if (alpha==modulo){
	    cerr << "Modgcd: no suitable evaluation point" << endl;
	    return false;
	  }
	  alpha1=alpha%2?-(alpha+1)/2:alpha/2;
	  if (hornermod(lcoeffp,alpha1,modulo)==0 || hornermod(lcoeffq,alpha1,modulo)==0)
	    continue;
	  if (debug_infolevel>20-dim)
	    cerr << "gcdmod eval alpha1=" << alpha1 << " dim " << dim << " " << clock() << endl;
	  break;
	} // end for (;;)
	// alpha is probably admissible 
	// (we will test later if degree of palpha/qalpha wrt x1...xn-1 is max)
	// prepare room for gcd and cofactors
	if (debug_infolevel>25-dim)
	  cerr << "dim " << dim << " palpha " << palpha << " qalpha " << qalpha << endl ;
	alphav.push_back(alpha1);
	if (dim2){
	  dim2gcdv.push_back(vector<int>(0));
	  dim2pcofactorv.push_back(vector<int>(0));
	  dim2qcofactorv.push_back(vector<int>(0));
	}
	else {
	  gcdv.push_back(vector< T_unsigned<int,hashgcd_U> >(0));
	  pcofactorv.push_back(vector< T_unsigned<int,hashgcd_U> >(0));
	  qcofactorv.push_back(vector< T_unsigned<int,hashgcd_U> >(0));
	}
      } // end for (int thread=0;thread<nthreads;++thread)
      vector<gcd_call_param<int> > gcd_call_param_v(nthreads,gcd_par);
#ifdef HAVE_PTHREAD_H
      pthread_t tab[nthreads-1];
#endif
      for (int thread=0;thread<nthreads;++thread){
	int vpos=alphav.size()-(nthreads-thread);
	gcd_call_param_v[thread].vpos=vpos;
	if (thread!=nthreads-1 && !dim2){
	  gcd_call_param_v[thread].pv=0;
	  gcd_call_param_v[thread].qv=0;
	}
#ifdef HAVE_PTHREAD_H
	if (thread==nthreads-1)
	  do_recursive_gcd_call((void *)&gcd_call_param_v[thread]);
	else { // launch gcd computation in a separate thread
	  bool res=pthread_create(&tab[thread],(pthread_attr_t *) NULL,do_recursive_gcd_call,(void *) &gcd_call_param_v[thread]);
	  if (res)
	    do_recursive_gcd_call((void *)&gcd_call_param_v[thread]);
	}
#else
	do_recursive_gcd_call((void *)&gcd_call_param_v[thread]);
#endif
      }
      if (ctrl_c || interrupted)
	return false;
#ifdef HAVE_PTHREAD_H
      for (int thread=0;thread<nthreads-1;++thread){
	int vpos=alphav.size()-(nthreads-thread);
	// wait for thread to finish
	void * ptr;
	pthread_join(tab[thread],&ptr);
	if (!ptr){
	  if (dim2) 
	    dim2gcdv[vpos].clear();
	  else
	    gcdv[vpos].clear();
	}
      }
#endif
      for (int thread=0;thread<nthreads;++thread){
	int vpos=alphav.size()-(nthreads-thread);
	// Compare gcd degree in x1..xn-1, 0 means trash this value of alpha1
	int comp=0;
	if ( dim2 ? (!dim2gcdv[vpos].empty()) : (!gcdv[vpos].empty()) ){
	  if (dim2)
	    gdeg[0]=dim2gcdv[vpos].size()-1;
	  else
	    degree(gcdv[vpos],shift_vars_truncated,gdeg);
	  comp=compare(gdeg,delta); 
	}
	if (comp==-2){
	  // same degrees, add to interpolation
	  // Try spmod first
	  if (!compute_cof && nzero){
	    // Add alpha,g 
	    if (dim>2 && gcddeg-nzero==e){ 
	      // We have enough evaluations, let's try SPMOD
	      // Build the matrix, each line has coeffs / vzero
	      vector< vector<int> > m,minverse;
	      for (int j=0;j<=e;++j){
		index_t::reverse_iterator it=vzero.rbegin(),itend=vzero.rend();
		vector<int> line;
		for (int p=alphav[j],pp=1;it!=itend;++it,pp=smod(p*pp,modulo)){
		  if (*it)
		    line.push_back(pp);
		}
		reverse(line.begin(),line.end());
		m.push_back(line);
	      }
	      // assume gcd is the vector of non zero coeffs of the gcd in x^n
	      // we have the relation
	      // m*gcd=gcdv
	      // invert m (if invertible)
	      longlong det_mod_p;
	      if (smallmodinv(m,minverse,modulo,det_mod_p) && det_mod_p){
		// hence gcd=minverse*gcdv, where the i-th component of gcd
		// must be "multiplied" by xn^degree_corresponding_vzero[i]
		vector< vector< T_unsigned<int,hashgcd_U> > > minversegcd(e+1);
		for (int j=0;j<=e;++j){
		  for (int k=0;k<=e;++k){
		    vector< T_unsigned<int,hashgcd_U> > tmp(gcdv[k]);
		    smallmult(minverse[j][k],tmp,tmp,modulo);
		    smalladd(minversegcd[j],tmp,minversegcd[j],modulo);
		    // cerr << minversegcd[j] << endl;
		  }
		}
		vector< T_unsigned<int,hashgcd_U> > trygcd,pquo,qquo,tmprem;
		index_t::const_iterator it=vzero.begin(),itend=vzero.end();
		int deg=itend-it-1;
		for (int j=0;it!=itend;++it,--deg){
		  if (!*it)
		    continue;
		  smallshift(minversegcd[j],deg*var2,minversegcd[j]);
		  smalladd(trygcd,minversegcd[j],trygcd,modulo);
		  ++j;
		}
		// Check if trygcd is the gcd!
		pp_mod(trygcd,0,modulo,varxn,var2,tmpcont);
		if (hashdivrem(p,trygcd,pquo,tmprem,vars,modulo,0,false)==1 && tmprem.empty()){
		  if (hashdivrem(q,trygcd,qquo,tmprem,vars,modulo,0,false)==1 && tmprem.empty()){
		    smallmult(trygcd,dcont,d,modulo,0);
		    smallmult(invmod(d.front().g,modulo),d,d,modulo);
		    if (compute_pcofactor){
		      smallmult(pcofactor,pquo,pcofactor,modulo,0);
		    smallmult(smod(longlong(p_orig.front().g)*invmod(pcofactor.front().g,modulo),modulo),pcofactor,pcofactor);
		    }
		    if (compute_qcofactor){
		      smallmult(qcofactor,qquo,qcofactor,modulo,0);
		      smallmult(smod(longlong(q_orig.front().g)*invmod(qcofactor.front().g,modulo),modulo),qcofactor,qcofactor);
		    }
		    if (debug_infolevel>20-dim)
		      cerr << "gcdmod found dim " << dim << " " << clock() << endl;
		    if (pqswap)
		      swap(pcofactor,qcofactor);
		    return true;
		  } // end q divisible by trygcd
		} // end p divisible by trygcd
	      } // end m invertible
	    } // end if (dim>2 && )
	  }
	  if (debug_infolevel>20-dim)
	    cerr << "gcdmod interp dim " << dim << " " << clock() << endl;
	  ++e;
	  continue;
	} // end gdeg==delta
	if (comp==0 || comp==-1){ 
	  if (debug_infolevel>20-dim)
	    cerr << "Bad reduction " << alphav[vpos] << endl;
	  // bad reduction: all indices of gdeg are >= to delta and gdeg!=delta
	  alphav.erase(alphav.begin()+vpos);
	  if (dim2){
	    dim2gcdv.erase(dim2gcdv.begin()+vpos);
	    dim2pcofactorv.erase(dim2pcofactorv.begin()+vpos);
	    dim2qcofactorv.erase(dim2qcofactorv.begin()+vpos);
	  }
	  else {
	    gcdv.erase(gcdv.begin()+vpos);
	    pcofactorv.erase(pcofactorv.begin()+vpos);
	    qcofactorv.erase(qcofactorv.begin()+vpos);
	  }
	  if (comp==0)
	    continue;
	}
	// previous alpha where bad reduction
	if (debug_infolevel>20-dim && vpos)
	  cerr << "Bads reductions " << alphav[vpos-1] << endl;
	alphav.erase(alphav.begin(),alphav.begin()+vpos);
	if (dim2){
	  dim2gcdv.erase(dim2gcdv.begin(),dim2gcdv.begin()+vpos); 
	  dim2pcofactorv.erase(dim2pcofactorv.begin(),dim2pcofactorv.begin()+vpos);
	  dim2qcofactorv.erase(dim2qcofactorv.begin(),dim2qcofactorv.begin()+vpos);       
	}
	else {
	  gcdv.erase(gcdv.begin(),gcdv.begin()+vpos); 
	  pcofactorv.erase(pcofactorv.begin(),pcofactorv.begin()+vpos);
	  qcofactorv.erase(qcofactorv.begin(),qcofactorv.begin()+vpos);       
	}
	if (comp==-1){ 
	  e=0;
	  continue;
	}
	// restart everything with this value of alpha
	// this will happen (almost all the time) at first iteration
	delta=gdeg;
	e=1;
	continue;
      } // end for (int thread=0;thread<nthreads;++thread)
    } // end for (alpha=-1;;)
#endif // RTOS_THREADX
  }

  bool mod_gcd(const std::vector< T_unsigned<int,hashgcd_U> > & p_orig,const std::vector< T_unsigned<int,hashgcd_U> > & q_orig,int modulo,std::vector< T_unsigned<int,hashgcd_U> > & d, std::vector< T_unsigned<int,hashgcd_U> > & pcofactor, std::vector< T_unsigned<int,hashgcd_U> > & qcofactor,const std::vector<hashgcd_U> & vars, bool compute_pcofactor,bool compute_qcofactor,int nthreads){
    bool divtest;
    vector< vector<int> > pv,qv;
    return mod_gcd(p_orig,q_orig,modulo,d,pcofactor,qcofactor,vars,compute_pcofactor,compute_qcofactor,divtest,pv,qv,nthreads);
  }

  bool mod_gcd(const std::vector< T_unsigned<int,hashgcd_U> > & p_orig,const std::vector< T_unsigned<int,hashgcd_U> > & q_orig,int modulo,std::vector< T_unsigned<int,hashgcd_U> > & d, std::vector< T_unsigned<int,hashgcd_U> > & pcofactor, std::vector< T_unsigned<int,hashgcd_U> > & qcofactor,const std::vector<hashgcd_U> & vars, bool compute_cofactors,int nthreads){
    return mod_gcd(p_orig,q_orig,modulo,d,pcofactor,qcofactor,vars,compute_cofactors,compute_cofactors,nthreads);
  }

#if 0
  static void smod(vector< T_unsigned<int,hashgcd_U> > & p_orig,int modulo){
    vector< T_unsigned<int,hashgcd_U> >::iterator it=p_orig.begin(),itend=p_orig.end();
    for (;it!=itend;++it){
      it->g=smod(it->g,modulo);
    }
  }
#endif

  static void smod(const vector< T_unsigned<gen,hashgcd_U> > & p_orig,int modulo,vector< T_unsigned<int,hashgcd_U> > & p){
    p.clear();
    vector< T_unsigned<gen,hashgcd_U> >::const_iterator it=p_orig.begin(),itend=p_orig.end();
    p.reserve(itend-it);
    int x;
    for (;it!=itend;++it){
      x=smod(it->g,modulo).val;
      if (x)
	p.push_back(T_unsigned<int,hashgcd_U>(x,it->u));
    }
  }

  static void unmod(const vector< T_unsigned<int,hashgcd_U> > & p_orig,vector< T_unsigned<gen,hashgcd_U> > & p,int modulo){
    p.clear();
    vector< T_unsigned<int,hashgcd_U> >::const_iterator it=p_orig.begin(),itend=p_orig.end();
    for (;it!=itend;++it){
      p.push_back(T_unsigned<gen,hashgcd_U>(smod(it->g,modulo),it->u));
    }
  }

  static void complex_unmod(const vector< T_unsigned<int,hashgcd_U> > & p1,const vector< T_unsigned<int,hashgcd_U> > & p2,vector< T_unsigned<gen,hashgcd_U> > & p,int i,int modulo){
    p.clear();
    vector< T_unsigned<int,hashgcd_U> >::const_iterator it=p1.begin(),itend=p1.end();
    vector< T_unsigned<int,hashgcd_U> >::const_iterator jt=p2.begin(),jtend=p2.end();
    // real part is halfsum of p1,p2, imaginary part halfdiff of p1-p2 divided by i (mod modulo)
    longlong inv2=invmod(2,modulo), inv2i=invmod(2*i,modulo);
    for (;it!=itend && jt!=jtend;){
      if (it->u==jt->u){
	p.push_back(T_unsigned<gen,hashgcd_U>(gen(smod(inv2*(it->g+jt->g),modulo),smod(inv2i*(it->g-jt->g),modulo)),it->u));
	++it;
	++jt;
	continue;
      }
      if (it->u>jt->u){
	p.push_back(T_unsigned<gen,hashgcd_U>(gen(smod(inv2*(it->g),modulo),smod(inv2i*(it->g),modulo)),it->u));
	++it;
      }
      else {
	p.push_back(T_unsigned<gen,hashgcd_U>(gen(smod(inv2*(jt->g),modulo),smod(inv2i*(-jt->g),modulo)),it->u));
	++jt;
      }
    }
    for (;it!=itend;++it){
      p.push_back(T_unsigned<gen,hashgcd_U>(gen(smod(inv2*(it->g),modulo),smod(inv2i*(it->g),modulo)),it->u));
    }
    for (;jt!=jtend;++jt){
      p.push_back(T_unsigned<gen,hashgcd_U>(gen(smod(inv2*(jt->g),modulo),smod(inv2i*(-jt->g),modulo)),it->u));
    }    
  }

  static void complex_unmod_ext(const vector< T_unsigned<vecteur,hashgcd_U> > & p1,const vector< T_unsigned<vecteur,hashgcd_U> > & p2,vector< T_unsigned<gen,hashgcd_U> > & p,int i,int modulo){
    p.clear();
    vector< T_unsigned<vecteur,hashgcd_U> >::const_iterator it=p1.begin(),itend=p1.end();
    vector< T_unsigned<vecteur,hashgcd_U> >::const_iterator jt=p2.begin(),jtend=p2.end();
    // real part is halfsum of p1,p2, imaginary part halfdiff of p1-p2 divided by i (mod modulo)
    gen inv2=invmod(2,modulo), inv2i=invmod(2*i,modulo);
    for (;it!=itend && jt!=jtend;){
      if (it->u==jt->u){
	p.push_back(T_unsigned<gen,hashgcd_U>( smod(inv2*(it->g+jt->g),modulo)+cst_i*smod(inv2i*(it->g-jt->g),modulo) ,it->u));
	++it;
	++jt;
	continue;
      }
      if (it->u>jt->u){
	p.push_back(T_unsigned<gen,hashgcd_U>( smod(inv2*(it->g),modulo)+cst_i*smod(inv2i*(it->g),modulo) ,it->u));
	++it;
      }
      else {
	p.push_back(T_unsigned<gen,hashgcd_U>( smod(inv2*(jt->g),modulo)+cst_i*smod(inv2i*(-jt->g),modulo),it->u));
	++jt;
      }
    }
    for (;it!=itend;++it){
      p.push_back(T_unsigned<gen,hashgcd_U>(smod(inv2*(it->g),modulo)+cst_i*smod(inv2i*(it->g),modulo),it->u));
    }
    for (;jt!=jtend;++jt){
      p.push_back(T_unsigned<gen,hashgcd_U>(smod(inv2*(jt->g),modulo)+cst_i*smod(inv2i*(-jt->g),modulo),it->u));
    }
  }

  static void ichinrem(const vector< T_unsigned<int,hashgcd_U> > & p_orig,const gen & modulo,vector< T_unsigned<gen,hashgcd_U> > & p,const gen & pimod){
    vector< T_unsigned<gen,hashgcd_U> > q;
    vector< T_unsigned<int,hashgcd_U> >::const_iterator it=p_orig.begin(),itend=p_orig.end();
    vector< T_unsigned<gen,hashgcd_U> >::const_iterator jt=p.begin(),jtend=p.end();
    q.reserve(jtend-jt);
    for (;it!=itend && jt!=jtend;){
      if (it->u==jt->u){
	q.push_back(T_unsigned<gen,hashgcd_U>(ichinrem(it->g,jt->g,modulo,pimod),it->u));
	++it; ++jt;
	continue;
      }
      if (it->u<jt->u){
	q.push_back(T_unsigned<gen,hashgcd_U>(ichinrem(0,jt->g,modulo,pimod),jt->u));
	++jt;
      }
      else {
	q.push_back(T_unsigned<gen,hashgcd_U>(ichinrem(it->g,zero,modulo,pimod),it->u));
	++it;
      }
    }
    for (;it!=itend;++it)
      q.push_back(T_unsigned<gen,hashgcd_U>(ichinrem(it->g,zero,modulo,pimod),it->u));      
    for (;jt!=jtend;++jt)
      q.push_back(T_unsigned<gen,hashgcd_U>(ichinrem(0,jt->g,modulo,pimod),jt->u));
    swap(p,q);
  }

  static void ichinrem(const vector< T_unsigned<gen,hashgcd_U> > & p_orig,const gen & modulo,vector< T_unsigned<gen,hashgcd_U> > & p,const gen & pimod){
    vector< T_unsigned<gen,hashgcd_U> > q;
    vector< T_unsigned<gen,hashgcd_U> >::const_iterator it=p_orig.begin(),itend=p_orig.end();
    vector< T_unsigned<gen,hashgcd_U> >::const_iterator jt=p.begin(),jtend=p.end();
    q.reserve(jtend-jt);
    for (;it!=itend && jt!=jtend;){
      if (it->u==jt->u){
	q.push_back(T_unsigned<gen,hashgcd_U>(ichinrem(it->g,jt->g,modulo,pimod),it->u));
	++it; ++jt;
	continue;
      }
      if (it->u<jt->u){
	q.push_back(T_unsigned<gen,hashgcd_U>(ichinrem(0,jt->g,modulo,pimod),jt->u));
	++jt;
      }
      else {
	q.push_back(T_unsigned<gen,hashgcd_U>(ichinrem(it->g,zero,modulo,pimod),it->u));
	++it;
      }
    }
    for (;it!=itend;++it)
      q.push_back(T_unsigned<gen,hashgcd_U>(ichinrem(it->g,zero,modulo,pimod),it->u));      
    for (;jt!=jtend;++jt)
      q.push_back(T_unsigned<gen,hashgcd_U>(ichinrem(0,jt->g,modulo,pimod),jt->u));
    swap(p,q);
  }

  static void complex_ichinrem(const vector< T_unsigned<int,hashgcd_U> > & p1,const vector< T_unsigned<int,hashgcd_U> > & p2,int i,int modulo,vector< T_unsigned<gen,hashgcd_U> > & p,const gen & pimod){
    vector< T_unsigned<gen,hashgcd_U> > p_orig,q;
    complex_unmod(p1,p2,p_orig,i,modulo);
    ichinrem(p_orig,modulo,p,pimod);
  }

  static void complex_ichinrem_ext(const vector< T_unsigned<vecteur,hashgcd_U> > & p1,const vector< T_unsigned<vecteur,hashgcd_U> > & p2,int i,int modulo,vector< T_unsigned<gen,hashgcd_U> > & p,const gen & pimod){
    vector< T_unsigned<gen,hashgcd_U> > p_orig;
    complex_unmod_ext(p1,p2,p_orig,i,modulo);
    ichinrem(p_orig,modulo,p,pimod);
  }

  static gen max(const vector< T_unsigned<gen,hashgcd_U> > & p,GIAC_CONTEXT){
    vector< T_unsigned<gen,hashgcd_U> >::const_iterator jt=p.begin(),jtend=p.end();
    gen g,res;
    for (;jt!=jtend;++jt){
      g=abs(jt->g,contextptr);
      if (is_strictly_greater(g,res,contextptr))
	res=g;
    }
    return res;
  }

  // if res==0 set res to gcd of coeffs of p
  // if divide, divide p by res
  static gen ppz(vector< T_unsigned<gen,hashgcd_U> > & p,const gen & pgcd,bool divide){
    vector< T_unsigned<gen,hashgcd_U> >::iterator jt=p.begin(),jtend=p.end();
    gen res=pgcd;
    if (res==0){
      for (;jt!=jtend;++jt){
	if (jt->g.type==_VECT){
	  const_iterateur it=jt->g._VECTptr->begin(),itend=jt->g._VECTptr->end();
	  for (;it!=itend;++it){
	    res=gcd(res,*it);
	  }
	}
	else
	  res=gcd(res,jt->g);
	if (is_one(res))
	  return res;
      }
    }
    if (!divide)
      return res;
    for (jt=p.begin();jt!=jtend;++jt){
      jt->g = jt->g/res;
    }
    return res;
  }

  static gen lcmdeno(vector< T_unsigned<gen,hashgcd_U> > & p){
    vector< T_unsigned<gen,hashgcd_U> >::iterator jt=p.begin(),jtend=p.end();
    gen res=1;
    for (;jt!=jtend;++jt){
      if (jt->g.type==_FRAC){
	res=lcm(res,jt->g._FRACptr->den);
	continue;
      }
      if (jt->g.type==_VECT){
	const_iterateur it=jt->g._VECTptr->begin(),itend=jt->g._VECTptr->end();
	for (;it!=itend;++it){
	  if (it->type==_FRAC)
	    res=lcm(res,it->_FRACptr->den);
	  if (it->type==_POLY){
	    vector< monomial<gen> >::const_iterator kt=it->_POLYptr->coord.begin(),ktend=it->_POLYptr->coord.end();
	    for (;kt!=ktend;++kt){
	      if (kt->value.type==_FRAC)
		res=lcm(res,kt->value._FRACptr->den);
	    }
	  }
	}
	continue;
      }
      if (jt->g.type==_POLY){
	vector< monomial<gen> >::const_iterator it=jt->g._POLYptr->coord.begin(),itend=jt->g._POLYptr->coord.end();
	for (;it!=itend;++it){
	  if (it->value.type==_FRAC)
	    res=lcm(res,it->value._FRACptr->den);
	}
	continue;
      }
    }
    if (is_one(res))
      return res;
    for (jt=p.begin();jt!=jtend;++jt){
      jt->g = jt->g * res;
    }
    return res;
  }

  // 1: integer, 2: gaussian integer, 3: ext, 4: ext with gaussint coeff, 0: other
  static int is_integer(const vector< T_unsigned<gen,hashgcd_U> > & p,gen & coefft){
    vector< T_unsigned<gen,hashgcd_U> >::const_iterator jt=p.begin(),jtend=p.end();
    int t=1;
    for (;jt!=jtend;++jt){
      if (jt->g.is_integer())
	continue;
      if (jt->g.type==_EXT){
	if (t<3)
	  t=t==1?3:4;
	if (coefft.type==_EXT){ 
	  if (*(coefft._EXTptr+1)!=*(jt->g._EXTptr+1))
	    return 0;
	}
	else {
	  coefft=jt->g;
	}
	if (t==3 && !is_zero(im(*jt->g._EXTptr,context0)))
	  t=4;
	continue;
      }
      if (jt->g.type==_POLY){
	vector< monomial<gen> >::const_iterator it=jt->g._POLYptr->coord.begin(),itend=jt->g._POLYptr->coord.end();
	for (;it!=itend;++it){
	  if (it->value.is_integer())
	    continue;
	  if (!it->value.is_cinteger())
	    return 0;
	  if (t!=3)
	    t=2;
	  else
	    t=4;
	}
	continue;
      }
      if (!jt->g.is_cinteger())
	return 0;
      if (t!=3 && t!=4)
	t=2;
      else
	t=4;
    }
    return t;
  }

  // reduce g mod modulo using i as square root of -1
  static int complex_smod(const gen & g,int i,int modulo){
    gen tmp=smod(re(g,context0)+i*im(g,context0),modulo);
#ifndef NO_STDEXCEPT
    if (tmp.type!=_INT_)
      setsizeerr(gettext("complex_smod"));
#endif
    return tmp.val;
  }

  static gen complex_smod_ext(const gen & g,int i,int modulo){
    return smod(re(g,context0)+i*im(g,context0),modulo);
  }

  static void complex_smod(const vector< T_unsigned<gen,hashgcd_U> > & p_orig,int i,int modulo,vector< T_unsigned<int,hashgcd_U> > & p){
    vector< T_unsigned<gen,hashgcd_U> >::const_iterator it=p_orig.begin(),itend=p_orig.end();
    p.clear();
    p.reserve(itend-it);
    for (;it!=itend;++it){
      p.push_back(T_unsigned<int,hashgcd_U>(complex_smod(it->g,i,modulo),it->u));
    }
  }

  static bool complex_smod_ext(const vector< T_unsigned<gen,hashgcd_U> > & p_orig,int i,int modulo,vector< T_unsigned<vecteur,hashgcd_U> > & p){
    vector< T_unsigned<gen,hashgcd_U> >::const_iterator it=p_orig.begin(),itend=p_orig.end();
    p.clear();
    p.reserve(itend-it);
    vecteur x;
    gen tmp,itg;
    const_iterateur vt,vtend;
    for (;it!=itend;++it){
      x.clear();
      if (it->g.type!=_EXT){
	tmp=complex_smod_ext(it->g,i,modulo);
	if (!is_zero(tmp))
	  x.push_back(tmp);
      }
      else {
	tmp=*it->g._EXTptr;
	if (tmp.type!=_VECT || tmp._VECTptr->empty())
	  return false;
	vt=tmp._VECTptr->begin(),vtend=tmp._VECTptr->end();
	x.reserve(vtend-vt);
	for (;vt!=vtend;++vt){
	  itg=complex_smod_ext(*vt,i,modulo);
	  if (is_zero(itg) && x.empty())
	    continue;
	  x.push_back(itg.val);
	}
      }
      if (!x.empty())
	p.push_back( T_unsigned<vecteur,hashgcd_U> (x,it->u) );
    }
    return true;
  }

  static int modsqrtminus1(int modulo){
    int i;
    for (int j=2;j<modulo;++j){
      i=powmod(j,(modulo-1)/4,modulo);
      if ((longlong(i)*i)%modulo==modulo-1)
	return i;
    }
    return 0;
  }

  bool gcd(const vector< T_unsigned<gen,hashgcd_U> > & p_orig,const vector< T_unsigned<gen,hashgcd_U> > & q_orig,vector< T_unsigned<gen,hashgcd_U> > & d, vector< T_unsigned<gen,hashgcd_U> > & pcofactor, vector< T_unsigned<gen,hashgcd_U> > & qcofactor,const std::vector<hashgcd_U> & vars, bool compute_cofactors,int nthreads){
#if defined( RTOS_THREADX) || defined(BESTA_OS) || defined(EMCC)
    return false;
#else
    index_t shift_vars;
    gen coefft;
    int tp=is_integer(p_orig,coefft),tq=is_integer(q_orig,coefft);
    if (
	// tp!=1 || tq!=1
	tp==0 || tq==0 
	|| !find_shift(vars,shift_vars)
	)
      return false;
    bool is_complex=tp==2 || tq==2;
    compute_cofactors=true; // FIXME
    index_t pdeg,qdeg,pdegmod,qdegmod,gdegmod,gdegmod2,gdeg;
    degree(p_orig,shift_vars,pdeg);
    degree(q_orig,shift_vars,qdeg);
    gdeg=min(pdeg,qdeg);
    // gen m=30000,pimod=1;
    gen m=536871000,pimod=1;
    gen lcoeffp=p_orig.front().g,lcoeffq=q_orig.front().g,gcdlcoeff=gcd(lcoeffp,lcoeffq);
    d.clear();
    pcofactor.clear();
    qcofactor.clear();
    bool divtest;
    vector< vector<int> > pv,qv;
    if (is_complex){
      vector< T_unsigned<int,hashgcd_U> > p1,q1,g1,pcof1,qcof1,p2,q2,g2,pcof2,qcof2;
      for (;;){
	m=nextprime(m+1);
	if (m.type!=_INT_)
	  return false;
	int modulo=m.val;
	// computing gcd in Z[i]: use a modulo =1[4], find gcd for both roots of -1 mod modulo
	if (modulo%4==3) 
	  continue;
	// find square root of -1
	int i=modsqrtminus1(modulo);
	int lg1=complex_smod(gcdlcoeff,i,modulo),lg2=complex_smod(gcdlcoeff,-i,modulo);
	int lp1=complex_smod(lcoeffp,i,modulo),lp2=complex_smod(lcoeffp,-i,modulo);
	int lq1=complex_smod(lcoeffq,i,modulo),lq2=complex_smod(lcoeffq,-i,modulo);
	if (!lg1 || !lp1 || !lq1 || !lg2 || !lp2 || !lq2)
	  continue;
	complex_smod(p_orig,i,modulo,p1);
	degree(p1,shift_vars,pdegmod);
	if (pdegmod!=pdeg)
	  continue;
	complex_smod(q_orig,i,modulo,q1);
	degree(q1,shift_vars,qdegmod);
	if (qdegmod!=qdeg)
	  continue;
	complex_smod(p_orig,-i,modulo,p2);
	degree(p2,shift_vars,pdegmod);
	if (pdegmod!=pdeg)
	  continue;
	complex_smod(q_orig,-i,modulo,q2);
	degree(q2,shift_vars,qdegmod);
	if (qdegmod!=qdeg)
	  continue;
	if (!mod_gcd(p1,q1,modulo,g1,pcof1,qcof1,vars,compute_cofactors,compute_cofactors,divtest,pv,qv,nthreads))
	  continue;
	// normalize g, pcof, qcof
	smallmult(smod(longlong(lg1)*invmod(g1.front().g,modulo),modulo),g1,g1,modulo);
	smallmult(smod(longlong(lp1)*invmod(pcof1.front().g,modulo),modulo),pcof1,pcof1,modulo);
	smallmult(smod(longlong(lq1)*invmod(qcof1.front().g,modulo),modulo),qcof1,qcof1,modulo);
	degree(g1,shift_vars,gdegmod);
	if (!mod_gcd(p2,q2,modulo,g2,pcof2,qcof2,vars,compute_cofactors,compute_cofactors,divtest,pv,qv,nthreads))
	  continue;
	smallmult(smod(longlong(lg2)*invmod(g2.front().g,modulo),modulo),g2,g2,modulo);
	smallmult(smod(longlong(lp2)*invmod(pcof2.front().g,modulo),modulo),pcof2,pcof2,modulo);
	smallmult(smod(longlong(lq2)*invmod(qcof2.front().g,modulo),modulo),qcof2,qcof2,modulo);
	degree(g2,shift_vars,gdegmod2);
	if (gdegmod2!=gdegmod)
	  continue;
	int cmp=compare(gdegmod,gdeg);
	if (cmp==0){ // bad reduction
	  continue;
	}
	if (cmp==-1){
	  // restart
	  d.clear();
	  pcofactor.clear();
	  qcofactor.clear();
	  pimod=1;
	  continue;
	}
	if (cmp==-2){ // same deg, chinese remainder
	  // same degrees, chinese remainder
	  complex_ichinrem(g1,g2,i,modulo,d,pimod);
	  if (compute_cofactors){
	    complex_ichinrem(pcof1,pcof2,i,modulo,pcofactor,pimod);
	    complex_ichinrem(qcof1,qcof2,i,modulo,qcofactor,pimod);
	  }
	  pimod = modulo * pimod;
	}
	else { // restart with this gcd
	  complex_unmod(g1,g2,d,i,modulo);
	  if (compute_cofactors){
	    complex_unmod(pcof1,pcof2,pcofactor,i,modulo);
	    complex_unmod(qcof1,qcof2,qcofactor,i,modulo);
	  }
	  pimod=modulo;
	  gdeg=gdegmod;
	}
	// finished??
	if (compute_cofactors){
	  // d*pcofactor=p_orig*gcdlcoeff mod pimod
	  // if |gcdlcoeff*p_orig|+|g|*|pcof|*min(size(g),size(pcof))< pimod we are done
	  int mgp=std::min(d.size(),pcofactor.size());
	  int mgq=std::min(d.size(),qcofactor.size());
	  gen maxg=max(d,context0),maxp=max(p_orig,context0),maxq=max(q_orig,context0),maxpcof=max(pcofactor,context0),maxqcof=max(qcofactor,context0);
	  gen dz=ppz(d,0,false),pz=ppz(pcofactor,0,false),qz=ppz(qcofactor,0,false);
	  maxg = maxg/abs(dz,context0);
	  maxpcof = maxpcof/abs(pz,context0);
	  maxqcof = maxqcof/abs(qz,context0);
	  if (is_strictly_greater(pimod,abs(gcdlcoeff,context0)*maxp+mgp*maxg*maxpcof,context0) && is_strictly_greater(pimod,abs(gcdlcoeff,context0)*maxq+mgq*maxg*maxqcof,context0) ){
	    // divide g,pcofactor,qcofactor by their integer content
	    ppz(d,dz,true);
	    if (compute_cofactors){
	      ppz(pcofactor,pz,true);
	      ppz(qcofactor,qz,true);
	    }
	    return true;
	  }
	}
	if (divtest) {
	  // division test
	  vector< T_unsigned<gen,hashgcd_U> > dtest(d),pquo,qquo,rem;
	  ppz(d,0,true);
	  if (hashdivrem(p_orig,dtest,pquo,rem,vars,0 /* reduce */,0/*qmax*/,false)==1 && rem.empty()){
	    if (hashdivrem(q_orig,dtest,qquo,rem,vars,0 /* reduce */,0/*qmax*/,false)==1 && rem.empty()){
	      pcofactor=pquo;
	      qcofactor=qquo;
	      return true;
	    }
	  }
	  d=dtest;
	}
      } // end for (;;)
      return false;
    } // end if (is_complex)
    // gcd in Z[x1,..,xn]
    vector< T_unsigned<int,hashgcd_U> > p,q,g,pcof,qcof;
    for (;;){
      m=nextprime(m+1);
      if (m.type!=_INT_)
	return false;
      int modulo=m.val;
      int lg=smod(gcdlcoeff,modulo).val,lp=smod(lcoeffp,modulo).val,lq=smod(lcoeffq,modulo).val;
      if (!lg || !lp || !lq)
	continue;
      smod(p_orig,modulo,p);
      degree(p,shift_vars,pdegmod);
      if (pdegmod!=pdeg)
	continue;
      smod(q_orig,modulo,q);
      degree(q,shift_vars,qdegmod);
      if (qdegmod!=qdeg)
	continue;
      if (!mod_gcd(p,q,modulo,g,pcof,qcof,vars,compute_cofactors,compute_cofactors,divtest,pv,qv,nthreads))
	continue;
      degree(g,shift_vars,gdegmod);
      // normalize g, pcof, qcof
      smallmult(smod(longlong(lg)*invmod(g.front().g,modulo),modulo),g,g,modulo);
      smallmult(smod(longlong(lp)*invmod(pcof.front().g,modulo),modulo),pcof,pcof,modulo);
      smallmult(smod(longlong(lq)*invmod(qcof.front().g,modulo),modulo),qcof,qcof,modulo);
      // cerr << " g " << g << " pcof " << pcof << "qcof " << qcof << endl;
      int cmp=compare(gdegmod,gdeg);
      if (cmp==0){ // bad reduction
	continue;
      }
      if (cmp==-1){
	// restart
	d.clear();
	pcofactor.clear();
	qcofactor.clear();
	pimod=1;
	continue;
      }
      if (cmp==-2){
	// same degrees, chinese remainder
	ichinrem(g,modulo,d,pimod);
	if (compute_cofactors){
	  ichinrem(pcof,modulo,pcofactor,pimod);
	  ichinrem(qcof,modulo,qcofactor,pimod);
	}
	pimod = modulo * pimod;
      }
      else {
	// restart with this modular gcd
	unmod(g,d,modulo);
	if (compute_cofactors){
	  unmod(pcof,pcofactor,modulo);
	  unmod(qcof,qcofactor,modulo);
	}
	pimod=modulo;
	gdeg=gdegmod;
      }
      // are we finished?
      if (compute_cofactors){
	// d*pcofactor=p_orig*gcdlcoeff mod pimod
	// if |gcdlcoeff*p_orig|+|g|*|pcof|*min(size(g),size(pcof))< pimod we are done
	int mgp=std::min(d.size(),pcofactor.size());
	int mgq=std::min(d.size(),qcofactor.size());
	gen maxg=max(d,context0),maxp=max(p_orig,context0),maxq=max(q_orig,context0),maxpcof=max(pcofactor,context0),maxqcof=max(qcofactor,context0);
	gen dz=ppz(d,0,false),pz=ppz(pcofactor,0,false),qz=ppz(qcofactor,0,false);
	maxg = maxg/ dz;
	maxpcof = maxpcof/pz;
	maxqcof = maxqcof/qz;
	if (is_strictly_greater(pimod,abs(gcdlcoeff,context0)*maxp+mgp*maxg*maxpcof,context0) && is_strictly_greater(pimod,abs(gcdlcoeff,context0)*maxq+mgq*maxg*maxqcof,context0) ){
	  // divide g,pcofactor,qcofactor by their integer content
	  ppz(d,dz,true);
	  if (compute_cofactors){
	    ppz(pcofactor,pz,true);
	    ppz(qcofactor,qz,true);
	  }
	  return true;
	}
      }
      if (divtest) {
	// division test
	vector< T_unsigned<gen,hashgcd_U> > dtest(d),pquo,qquo,rem;
	ppz(d,0,true);
	if (hashdivrem(p_orig,dtest,pquo,rem,vars,0 /* reduce */,0/*qmax*/,false)==1 && rem.empty()){
	  if (hashdivrem(q_orig,dtest,qquo,rem,vars,0 /* reduce */,0/*qmax*/,false)==1 && rem.empty()){
	    pcofactor=pquo;
	    qcofactor=qquo;
	    return true;
	  }
	}
	d=dtest;
      }
    }
#endif // RTOS_THREADX
  }

  /* ************************************************* 
           MODULAR ALGEBRAIC EXTENSIONS GCD
     ************************************************* */
  static void mulsmall(const vector<int>::const_iterator & ita0,const vector<int>::const_iterator & ita_end,const vector<int>::const_iterator & itb0,const vector<int>::const_iterator & itb_end,int modulo,vector<int> & new_coord){  
    new_coord.clear();
    if (ita0==ita_end || itb0==itb_end)
      return;
    vector<int>::const_iterator ita_begin=ita0,ita=ita0,itb=itb0;
    for ( ; ita!=ita_end; ++ita ){
      vector<int>::const_iterator ita_cur=ita,itb_cur=itb;
      longlong res=0;
      for (;itb_cur!=itb_end;--ita_cur,++itb_cur) {
	res += longlong(*ita_cur) * *itb_cur ;
	if (ita_cur==ita_begin)
	  break;
      }
      new_coord.push_back(smod(res,modulo));
    }
    --ita;
    ++itb;
    for ( ; itb!=itb_end;++itb){
      longlong res= 0;
      vector<int>::const_iterator ita_cur=ita,itb_cur=itb;
      for (;;) {
	res += longlong(*ita_cur) * *itb_cur ;
	if (ita_cur==ita_begin)
	  break;
	--ita_cur;
	++itb_cur;
	if (itb_cur==itb_end)
	  break;
      }
      new_coord.push_back(smod(res,modulo));
    }
  }

  // res=a*b mod pmin,modulo
  static void mulext(const vector<int> & a,const vector<int> & b,const vector<int> & pmin,int modulo,vector<int> & res){
    if (b.empty()){
      res.clear();
      return;
    }
    if (b.size()==1 && b.front()==1){
      res=a;
      return;
    }
    vector<int> q,tmp;
    mulsmall(a.begin(),a.end(),b.begin(),b.end(),modulo,tmp);
    DivRem(tmp,pmin,modulo,q,res);
  }

  static gen mulextaux2(const gen & a,const gen & b,int modulo){
    gen res= a*b;
    if (res.type==_FRAC)
      return smod(res,modulo);
    else
      return res;
  }

  static void mulextaux(const modpoly & a,const modpoly &b,int modulo,modpoly & new_coord){
    if (ctrl_c) { 
      interrupted = true; ctrl_c=false;
      new_coord=vecteur(1, gensizeerr(gettext("Stopped by user interruption."))); 
      return;
    }
    modpoly::const_iterator ita=a.begin(),ita_end=a.end(),itb=b.begin(),itb_end=b.end();
    new_coord.clear();
    ita=a.begin(); itb=b.begin();
    if (ita==ita_end || itb==itb_end)
      return;
    modpoly::const_iterator ita_begin=ita;
    for ( ; ita!=ita_end; ++ita ){
      modpoly::const_iterator ita_cur=ita,itb_cur=itb;
      gen res;
      for (;;) {
	res += mulextaux2(*ita_cur,*itb_cur,modulo); // res = res + (*ita_cur) * (*itb_cur);
	if (ita_cur==ita_begin)
	  break;
	--ita_cur;
	++itb_cur;
	if (itb_cur==itb_end)
	  break;
      }
      new_coord.push_back(res);	
    }
    --ita;
    ++itb;
    for ( ; itb!=itb_end;++itb){
      modpoly::const_iterator ita_cur=ita,itb_cur=itb;
      gen res;
      for (;;) {
	res += mulextaux2(*ita_cur,*itb_cur,modulo); // res = res + (*ita_cur) * (*itb_cur);
	if (ita_cur==ita_begin)
	  break;
	--ita_cur;
	++itb_cur;
	if (itb_cur==itb_end)
	  break;
      }
      new_coord.push_back(res);	
    }
  }

  static void mulext(const vecteur & a,const vecteur & b,const vecteur & pmin,int modulo,vecteur & res){
    if (b.empty()){
      res.clear();
      return;
    }
    if (b.size()==1 && b.front()==1){
      res=a;
      return;
    }
    vecteur q,tmp;
    environment env;
    env.modulo=modulo;
    env.moduloon=true;
    mulextaux(a,b,modulo,tmp);
    // operator_times(a,b,0,tmp); // bug if a and b contains fraction
    DivRem(tmp,pmin,&env,q,res);
  }

  // a *=b mod pmin, modulo
  static void mulext(vector<int> & a,const vector<int> & b,const vector<int> & pmin,int modulo){
    if (b.empty()){
      a.clear();
      return;
    }
    if (b.size()==1 && b.front()==1)
      return;
    vector<int> q,tmp;
    mulsmall(a.begin(),a.end(),b.begin(),b.end(),modulo,tmp);
    DivRem(tmp,pmin,modulo,q,a);
  }

  static void mulext(vector< vector<int> > & a,const vector<int> & b,const vector<int> & pmin,int modulo){
    if (b.empty()){
      a.clear();
      return;
    }
    if (b.size()==1 && b.front()==1)
      return;
    vector<int> q,tmp;
    vector< vector<int> >::iterator it=a.begin(),itend=a.end();
    for (;it!=itend;++it){
      mulsmall(it->begin(),it->end(),b.begin(),b.end(),modulo,tmp);
      DivRem(tmp,pmin,modulo,q,*it);
    }
  }

  // inverse of rootof(a:pmin) modulo 
  // pmin=1*pmin+0*a
  // a_=0*a+1*b
  // ...
  // 1=?*a+ainv*b
  static bool invmodext(const vector<int> & a_,const vector<int> & pmin,int modulo,vector<int> & u0){
    if (a_.empty())
      return false;
    vector<int> a(pmin),b(a_),q,r,u1,u2; // u0=ainv
    vector<int>::iterator it,itend;
    u0.clear();
    u1.push_back(1);
    for (;!b.empty();){
      if (gcd(modulo,b.front())!=1)
	return false;
      DivRem(a,b,modulo,q,r);
      swap(a,b); // a,b,r -> b,a,r
      swap(b,r); // -> b,r,a
      // u2=u0-q*u1 modulo
      mulext(q,u1,pmin,modulo,u2); // u2=q*u1
      submod(u2,u0,modulo); // u2=q*u1-u0
      for (it=u2.begin(),itend=u2.end();it!=itend;++it)
	*it = -*it; // done
      swap(u0,u1);
      swap(u1,u2);
    }
    if (gcd(modulo,a.front())!=1)
      return false;
    mulmod(u0,invmod(a.front(),modulo),modulo);
    return true;
  }

  static vector<int> invmod(const vector<int> & a,const modred & R){
    vector<int> ainv;
    if (!invmodext(a,R.pmin,R.modulo,ainv)){
#ifndef NO_STDEXCEPT
      setsizeerr(gettext("invmodext"));
#endif
      ainv.clear();
    }
    return ainv;
  }

  // c = a*b % reduce
  static void type_operator_reduce(const vector<int> & a,const vector<int> & b,vector<int> & c,const modred & reduce){
    mulext(a,b,reduce.pmin,reduce.modulo,c);
  }

  // c += a*b % reduce
  static void type_operator_plus_times_reduce(const vector<int> & a,const vector<int> & b,vector<int> & c,const modred & reduce){
    vector<int> tmp;
    mulext(a,b,reduce.pmin,reduce.modulo,tmp);
    addmod(c,tmp,reduce.modulo);
  }

  static vecteur invmod(const vecteur & a,const Modred & R){
    if (a.size()==1)
      return vecteur(1,invmod(a.front(),R.modulo));
    // should not occur if division test is done with lcoeff=1
    vecteur ainv,v,d;
    environment env;
    env.modulo=R.modulo;
    env.moduloon=true;
    egcd(a,R.pmin,&env,ainv,v,d);
    if (d.size()!=1){
#ifndef NO_STDEXCEPT
      setsizeerr(gettext("invmodext"));
#endif
      return vecteur(1,gensizeerr(gettext("invmodext")));
    }
    return ainv;
  }

  // c = a*b % reduce
  static void type_operator_reduce(const vecteur & a,const vecteur & b,vecteur & c,const Modred & reduce){
    mulext(a,b,reduce.pmin,reduce.modulo,c);
  }

  static void type_operator_times(const vecteur & a,const vecteur &b,vecteur & c){
    mulmodpoly(a,b,c);
  }

  static void type_operator_plus_times(vecteur & a,const vecteur & b,const vecteur & c){
    vecteur tmp;
    mulmodpoly(b,c,tmp);
    addmodpoly(a,tmp,a);
  }

  // c += a*b % reduce
  static void type_operator_plus_times_reduce(const vecteur & a,const vecteur & b,vecteur & c,const Modred & reduce){
    vecteur tmp;
    mulext(a,b,reduce.pmin,reduce.modulo,tmp);
    addmod(c,tmp,reduce.modulo);
  }

  static void smallmult(int g,const std::vector< T_unsigned<vector<int>,hashgcd_U> > & v1,std::vector< T_unsigned<vector<int>,hashgcd_U> > & v,int reduce){
    if (!g){
      v.clear();
      return;
    }
    std::vector< T_unsigned<vector<int>,hashgcd_U> >::const_iterator it1=v1.begin(),it1end=v1.end();
    if (&v1==&v){
      std::vector< T_unsigned<vector<int>,hashgcd_U> >::iterator it1=v.begin(),it1end=v.end();
      for (;it1!=it1end;++it1){
	mulmod(g,it1->g,reduce);
	// it1->g = (g*it1->g) % reduce;
      }
    }
    else {
      v.clear();
      v.reserve(it1end-it1); // worst case
      vector<int> res;
      for (;it1!=it1end;++it1){
	res=it1->g;
	mulmod(g,res,reduce);
	v.push_back(T_unsigned<vector<int>,hashgcd_U>(res,it1->u));
      }
    }
  }

  // a=b*q+r, modifies b (multiplication by inverse of lcoeff)
  // if you want q corresponding to original b, set q_orig_b to true
  static bool divrem(vector< vector<int> > & a,vector< vector<int> > & b,const vector<int> & pmin, int modulo, vector< vector<int> > * qptr,bool set_q_orig_b){
    int as=a.size(),bs=b.size();
    if (!bs)
      return false;
    vector<int> b0,b0inv;
    if (set_q_orig_b)
      b0=b.front();
    if (!invmodext(b.front(),pmin,modulo,b0inv))
      return false;
    if (qptr)
      qptr->clear();
    if (as<bs)
      return true;
    if (bs==1){
      if (qptr){
	swap(*qptr,a);
	if (set_q_orig_b)
	  mulext(*qptr,b0inv,pmin,modulo);
      }
      a.clear();
      return true;
    }
    mulext(b,b0inv,pmin,modulo);
    vector<int> tmp;
    vector< vector<int> >::iterator it,jt,jtend=b.end();
    int nstep=as-bs+1,pos=0;
    for (;pos<nstep;++pos){
      vector<int> & q = a[pos];
      if (qptr){
	if (set_q_orig_b){
	  mulext(q,b0inv,pmin,modulo,tmp);
	  qptr->push_back(tmp);
	}
	else
	  qptr->push_back(q);
      }
      if (!q.empty()){
	for (it=a.begin()+pos+1,jt=b.begin()+1;jt!=jtend;++it,++jt){
	  mulext(*jt,q,pmin,modulo,tmp);
	  submod(*it,tmp,modulo);
	}
      }
    }
    while (pos<as && a[pos].empty())
      ++pos;
    a.erase(a.begin(),a.begin()+pos);
    if (set_q_orig_b)
      mulext(b,b0,pmin,modulo);
    return true;
  }

  // find gcd of p and q modulo pmin,modulo
  // answer in d
  // return false if one of the lcoeff if not invertible during Euclide's algorithm
  static bool gcdsmallmodpoly_ext(const vector< vector<int> > & p,const vector< vector<int> > & q,const vector<int> & pmin,int modulo,vector< vector<int> > & d){
    // lcoeffs of p/q should be invertible mod modulo
    // for q this is checked at the first divrem
    if (p.empty()){
      d=q;
      return true;
    }
    if (q.empty()){
      d=p;
      return true;
    }
    vector<int> p0inv;
    if (!invmodext(p.front(),pmin,modulo,p0inv))
      return false;
    d=p;
    vector< vector<int> > b(q);
    for (;!b.empty();){
      if (!divrem(d,b,pmin,modulo,0,false))
	return false;
      swap(d,b);
    }
    if (!invmodext(d.front(),pmin,modulo,p0inv))
      return false;
    mulext(d,p0inv,pmin,modulo);
    return true;
  }

  static bool smod_ext(const vector< T_unsigned<gen,hashgcd_U> > & p_orig,int modulo,vector< T_unsigned<vecteur,hashgcd_U> > & p){
    p.clear();
    vector< T_unsigned<gen,hashgcd_U> >::const_iterator it=p_orig.begin(),itend=p_orig.end();
    p.reserve(itend-it);
    vecteur x;
    gen tmp,itg;
    const_iterateur vt,vtend;
    for (;it!=itend;++it){
      x.clear();
      if (it->g.type!=_EXT){
	tmp=smod(it->g,modulo);
	if (!is_zero(tmp))
	  x.push_back(tmp);
      }
      else {
	tmp=*it->g._EXTptr;
	if (tmp.type!=_VECT || tmp._VECTptr->empty())
	  return false;
	vt=tmp._VECTptr->begin(),vtend=tmp._VECTptr->end();
	x.reserve(vtend-vt);
	for (;vt!=vtend;++vt){
	  itg=smod(*vt,modulo);
	  if (is_zero(itg) && x.empty())
	    continue;
	  x.push_back(itg);
	}
      }
      if (!x.empty())
	p.push_back( T_unsigned<vecteur,hashgcd_U> (x,it->u) );
    }
    return true;
  }

  static vector<int> hornermod_ext(const vector< vector<int> > & v,int alpha,int modulo){
    vector< vector<int> >::const_iterator it=v.begin(),itend=v.end();
    if (!alpha)
      return it==itend?vector<int>(0):v.back();
    vector<int> res;
    for (;it!=itend;++it){
      mulmod(res,alpha,modulo);
      addmod(res,*it,modulo);
    }
    return res;
  }
  
  // convert one dimensional polynomial with ext coeff to full dense representation
  // back conversion is convert_back (template coefficients)
  static void convert(const vector< T_unsigned<vector<int>,hashgcd_U> > & p,hashgcd_U var,vector< vector<int> > & v){
    v.clear();
    vector< T_unsigned<vector<int>,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    if (it==itend)
      return;
    hashgcd_U u=it->u;
    unsigned s=u/var;
    v=vector< vector<int> >(s+1);
    for (;it!=itend;++it){
      v[s-it->u/var]=it->g;
    }
  }

  // find n and d such that n/d=a mod m, modulo
  // and deg(n) <= deg(m)/2, deg(d) <= deg(m)-deg(a)
  // n=m*u+a*d, 
  // Bezout for m and a, m*u_k + a*v_k = r_k, stop when deg(r_k)<=deg(m)/2
  // n=r_k and d=v_k, then check that d is prime with m and n
  static bool polyfracmod(const vector<int> & a,const vector<int> &m,int modulo,vector<int> & n, vector<int> & d){
    vector<int> r0(m),r1(a),r2,v0,v1(1,1),v2,q,tmp;
    // m*u0+a*v0=m=r0, m*u1+a*v1=a=r1, u0, u1 and u2 are not computed (not used)
    int N=(m.size()-1)/2;
    for (;(int)r1.size()>N+1;){
      DivRem(r0,r1,modulo,q,r2);
      smallmult(q.begin(),q.end(),v1.begin(),v1.end(),tmp,modulo);
      swap(v0,v2); // v2=v0; but v0 is not used anymore
      submod(v2,tmp,modulo);
      // move r1 to r0 and r2 to r1, same with v
      // A B C
      swap(r0,r1); // B A C
      swap(r1,r2); // B C A
      // unused B C 
      swap(v0,v1); // B unused C
      swap(v1,v2); // B C unused
    }
    // deg(r1)<=N, store answer
    swap(r1,n);
    swap(v1,d);
    // checks
    gcdsmallmodpoly(d,m,modulo,r2);
    if (r2.size()>1)
      return false;
    gcdsmallmodpoly(n,d,modulo,r2);
    if (r2.size()>1)
      return false;
    return true;
  }

  static bool polyfracmod(const gen & g,const vector<int> & m,int modulo,gen & res){
    if (g.type==_POLY){
      polynome p(g._POLYptr->dim);
      vector<monomial<gen> >::const_iterator it=g._POLYptr->coord.begin(),itend=g._POLYptr->coord.end();
      for (;it!=itend;++it){
	const gen tmp=it->value;
	gen x;
	if (tmp.type!=_VECT)
	  p.coord.push_back(monomial<gen>(tmp,it->index));
	else {
	  polyfracmod(*tmp._VECTptr,m,modulo,x);
	  p.coord.push_back(monomial<gen>(x,it->index));
	}
      }
      res=p;
      return true;
    }
    if (g.type!=_VECT){
      res=g;
      return true;
    }
    if (g._VECTptr->size()-1<=(m.size()-1)/2){
      res=g;
      return true;
    }
    // FIXME coeffs might be polynomials instead of integers
    vector<int> a,n,d;
    vecteur N,D;
    vecteur2vector_int(*g._VECTptr,modulo,a);
    if (!polyfracmod(a,m,modulo,n,d))
      return false;
    vector_int2vecteur(n,N);
    vector_int2vecteur(d,D);
    res=fraction(N,D);
    return true;
  }

  static bool polyfracmod(vecteur & res,const vector<int> & m,int modulo){
    iterateur it=res.begin(),itend=res.end();
    for (;it!=itend;++it){
      if (!polyfracmod(*it,m,modulo,*it))
	return false;
    }
    return true;
  }

  // replace p by lcm of p and g
  static bool vlcm(modpoly & p,const gen & g,int modulo){
    if (g.type!=_VECT)
      return true;
    const modpoly & q=*g._VECTptr;
    environment env;
    env.modulo=modulo;
    env.moduloon=true;
    modpoly quo,rem,pgcd;
    // first check if g divides p
    if (!DivRem(p,q,&env,quo,rem,false))
      return false; // setsizeerr();
    if (rem.empty())
      return true;
    // nope, compute gcd and divide p*q by gcd
    gcdmodpoly(p,q,&env,pgcd);
    if (is_undef(pgcd))
      return false;
    DivRem(p,pgcd,&env,quo,rem);
    mulmodpoly(q,quo,&env,p);
    return true;
  }

  // rational recon and clear fraction, return size of lcm of denom
  static int polyfracmod(const vector< T_unsigned<vecteur,hashgcd_U> > & p0,const vector<int> & m,int modulo,vector< T_unsigned<vecteur,hashgcd_U> > & p,int maxdeg){
    p=p0;
    vector< T_unsigned<vecteur,hashgcd_U> >::iterator kt=p.begin(),ktend=p.end();
    for (;kt!=ktend;++kt){
      if (!polyfracmod(kt->g,m,modulo))
	return 0;
    }
    // find lcm of deno
    vecteur ppcm(1,1);
    for (kt=p.begin();kt!=ktend;++kt){
      if ((int)ppcm.size()>maxdeg+1)
	return 0;
      iterateur it=kt->g.begin(),itend=kt->g.end();
      for (;it!=itend;++it){
	if (it->type==_FRAC){
	  if (!vlcm(ppcm,it->_FRACptr->den,modulo))
	    return 0;
	  if ((int)ppcm.size()>maxdeg+1)
	    return 0;
	}
	else {
	  if (it->type==_POLY){
	    vector< monomial<gen> >::const_iterator jt=it->_POLYptr->coord.begin(),jtend=it->_POLYptr->coord.end();
	    for (;jt!=jtend;++jt){
	      if (jt->value.type==_FRAC){
		if (!vlcm(ppcm,jt->value._FRACptr->den,modulo))
		  return 0;
		if ((int)ppcm.size()>maxdeg+1)
		  return 0;
	      }
	    }
	  }
	}
      }
    }
    // clear deno
    gen den(ppcm,_POLY1__VECT);
    environment env;
    env.modulo=modulo;
    env.moduloon=true;
    if (ppcm.size()>1){
      gen adjust=invmod(ppcm.front(),modulo);
      if (is_undef(adjust))
	return 0;
      for (kt=p.begin();kt!=ktend;++kt){
	iterateur it=kt->g.begin(),itend=kt->g.end();
	for (;it!=itend;++it){
	  if (it->type==_FRAC){
	    if (it->_FRACptr->den.type!=_VECT)
	      return 0; // setsizeerr();
	    modpoly quot,rest;
	    if (!DivRem(ppcm,*it->_FRACptr->den._VECTptr,&env,quot,rest,false) || !rest.empty())
	      return 0; // setsizeerr();
	    *it = smod(adjust*(it->_FRACptr->num * gen(quot,_POLY1__VECT)),modulo);
	  }
	  else {
	    if (it->type==_POLY){
	      vector< monomial<gen> >::iterator jt=it->_POLYptr->coord.begin(),jtend=it->_POLYptr->coord.end();
	      for (;jt!=jtend;++jt){
		if (jt->value.type==_FRAC){
		  modpoly quot,rest;
		  if (!DivRem(ppcm,*jt->value._FRACptr->den._VECTptr,&env,quot,rest,false) || !rest.empty())
		    return 0; // setsizeerr();
		  jt->value = smod(adjust*(jt->value._FRACptr->num * gen(quot,_POLY1__VECT)),modulo);
		}
		else
		  jt->value = smod(adjust*(den * jt->value),modulo) ;
	      }
	    }
	    else
	      *it = smod(adjust*den * (*it),modulo);
	  }
	}
      }
    }
    return ppcm.size();
  }

  static bool gentoint(const vector< T_unsigned<vecteur,hashgcd_U> > & p0,vector< T_unsigned<vector<int>,hashgcd_U> > & p){
    vector< T_unsigned<vecteur,hashgcd_U> >::const_iterator it=p0.begin(),itend=p0.end();
    p.clear();
    p.reserve(itend-it);
    for (;it!=itend;++it){
      vecteur::const_iterator jt=it->g.begin(),jtend=it->g.end();
      p.push_back(T_unsigned<vector<int>,hashgcd_U>(vector<int>(0),it->u));
      vector<int> & res=p.back().g;
      for (;jt!=jtend;++jt){
	if (jt->type!=_INT_)
	  return false;
	res.push_back(jt->val);
      }
    }
    return true;
  }

  static void inttogen(const vector< T_unsigned<vector<int>,hashgcd_U> > & p0,vector< T_unsigned<vecteur,hashgcd_U> > & p){
    vector< T_unsigned<vector<int>,hashgcd_U> >::const_iterator it=p0.begin(),itend=p0.end();
    p.clear();
    p.reserve(itend-it);
    for (;it!=itend;++it){
      vector<int>::const_iterator jt=it->g.begin(),jtend=it->g.end();
      p.push_back(T_unsigned<vecteur,hashgcd_U>(vecteur(0),it->u));
      vecteur & res=p.back().g;
      for (;jt!=jtend;++jt){
	res.push_back(*jt);
      }
    }
  }

  // eval last variable of polynomials in g at x modulo modulo, answer in res
  // polynomial coeff must be integers
  static bool horner_back_ext(const gen & g,int x,int modulo,gen & res){
    if (g.type!=_POLY){
      if (g.type==_FRAC){
	gen r1,r2;
	if (!horner_back_ext(g._FRACptr->num,x,modulo,r1) ||
	    !horner_back_ext(g._FRACptr->den,x,modulo,r2))
	  return false;
	res=r1/r2;
	return true;
      }
      if (g.type==_EXT){
	gen r1,r2;
	if (!horner_back_ext(*g._EXTptr,x,modulo,r1) ||
	    !horner_back_ext(*(g._EXTptr+1),x,modulo,r2))
	  return false;
	res=algebraic_EXTension(r1,r2);
	return true;
      }
      if (g.type!=_VECT){
	res=g;
	return true;
      }
      res=gen(*g._VECTptr,g.subtype);
      if (res.type!=_VECT)
	return true;
      gen tmp;
      iterateur it=res._VECTptr->begin(),itend=res._VECTptr->end();
      for (;it!=itend;++it){
	if (!horner_back_ext(*it,x,modulo,tmp))
	  return false;
	*it=tmp;
      }
      return true;
    }
    const polynome & p = *g._POLYptr;
    int dim=p.dim;
    polynome r(dim-1);
    vector< monomial<gen> >::const_iterator it=p.coord.begin(),itend=p.coord.end();
    if (it==itend){
      res=0;
      return true;
    }
    index_t current(it->index.begin(),it->index.begin()+dim-1),nouveau(dim-1);
    int expo=it->index.back(),newexpo;
    if (it->value.type!=_INT_)
      return false; // setsizeerr();
    int val=it->value.val;
    for (++it;it!=itend;++it){
      // IMPROVE: for dense poly: skip to it+expo check if same outer exponents
      if (it->value.type!=_INT_)
	return false; // setsizeerr();
      index_t::iterator nit=nouveau.begin();
      index_t::const_iterator jt=it->index.begin(),jtend=it->index.begin()+dim-1;
      for (;jt!=jtend;++nit,++jt){
	*nit=*jt;
      }
      if (nouveau==current){
	val=(powmod(x,expo-(newexpo=it->index.back()),modulo)*longlong(val)+it->value.val)%modulo;
	expo=newexpo;
	continue;
      }
      if (expo)
	val=(powmod(x,expo,modulo)*longlong(val))%modulo;
      r.coord.push_back(monomial<gen>(val,current));
      swap(current,nouveau);
      val=it->value.val;
      expo=it->index.back();
    }
    if (expo)
      val=(powmod(x,expo,modulo)*longlong(val))%modulo;
    r.coord.push_back(monomial<gen>(val,current));
    res=r;
    return true;
  }

  // return false if the lcoeff is 0
  static bool horner_back_ext2(const vector< T_unsigned<vecteur,hashgcd_U> > & g,int x,int modulo,vector< T_unsigned<vecteur,hashgcd_U> > & res){
    gen tmp;
    res.clear();
    vector< T_unsigned<vecteur,hashgcd_U> >::const_iterator it=g.begin(),itend=g.end();
    bool b=true;
    for (;it!=itend;++it){
      if (!horner_back_ext(it->g,x,modulo,tmp))
	return false;
      if (is_zero(tmp)){
	if (it==res.begin())
	  b=false;
	continue;
      }
      if (tmp.type==_VECT)
	res.push_back(T_unsigned<vecteur,hashgcd_U>(*tmp._VECTptr,it->u));
      else
	res.push_back(T_unsigned<vecteur,hashgcd_U>(vecteur(1,tmp),it->u));
    }
    return b;
  }

  struct ext_gcd_t {
    gen pi_t_minus_tk;
    vector< T_unsigned<vecteur,hashgcd_U> > lagrange;
    // the vecteur represents dependency wrt the extension variable (z)
    // each coeff depends on t1..tk-1 variable as a _POLY
    // the depency wrt tk is represented by coeffs=vecteur of type poly1[]
  };

  static gen hornermod(const gen & g,int tk,int modulo){
    if (g.type==_POLY){
      polynome res(g._POLYptr->dim);
      vector< monomial<gen> >::const_iterator it=g._POLYptr->coord.begin(),itend=g._POLYptr->coord.end();
      for (;it!=itend;++it){
	gen tmp=hornermod(it->value,tk,modulo);
	if (!is_zero(tmp))
	  res.coord.push_back(monomial<gen>(tmp,it->index));
      }
      return res;
    }
    if (g.type!=_VECT)
      return g;
    int res=0;
    const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
    for (;it!=itend;++it){
      res = ( res*longlong(tk)+it->val)%modulo;
    }
    return res;
  }

  static vecteur interp_tk(const vecteur & interp_ancien,const gen & ancienpi,const vecteur & interp_tk,int tk,int modulo){
    int as=interp_ancien.size(),bs=interp_tk.size(),cs=giacmax(as,bs);
    vecteur res(cs);
    gen pi(hornermod(ancienpi,tk,modulo));
    if (pi.type!=_INT_)
      return vecteur(1,gensizeerr(gettext("interp_tk")));
    int invpi=invmod(pi.val,modulo);
    for (int i=0;i<cs;++i){
      gen old=0,nouv=0;
      if (i+as>=cs) old=hornermod(interp_ancien[i+as-cs],tk,modulo);
      if (i+bs>=cs) nouv=interp_tk[i+bs-cs];
      gen alpha=smod(invpi*(nouv-old),modulo);
      // multiply by ancienpi and add to interp_ancien
      if (alpha.type==_POLY)
	alpha=ancienpi * (*alpha._POLYptr);
      else
	alpha=ancienpi*alpha;
      if (i+as>=cs){
	gen g =interp_ancien[i+as-cs];
	if (g.type==_POLY && alpha.type!=_POLY)
	  res[i]=smod(addpoly(*g._POLYptr,alpha),modulo);
	else {
	  if (g.type!=_POLY && alpha.type==_POLY)
	    res[i]=smod(addpoly(*alpha._POLYptr,g),modulo);
	  else
	    res[i]=smod(g+alpha,modulo);
	}
      }
      else
	res[i]=smod(alpha,modulo);
    }
    return res;
  }

  static void untrunc(gen & g,int innerdim){
    if (g.type==_VECT){
      polynome p(innerdim);
      index_t i(innerdim);
      const_iterateur jt=g._VECTptr->begin(),jtend=g._VECTptr->end();
      if (jtend==jt){
	g=0;
	return;
      }
      i.back()=(jtend-jt)-1;
      for (;jt!=jtend;--i.back(),++jt){
	if (!is_zero(*jt))
	  p.coord.push_back(monomial<gen>(*jt,i));
      }
      g=p;
      return;
    }
    if (g.type!=_POLY)
      return;
    polynome p(innerdim);
    vector< monomial<gen> >::const_iterator it=g._POLYptr->coord.begin(),itend=g._POLYptr->coord.end();
    for (;it!=itend;++it){
      index_t i(it->index.iref());
      i.push_back(0);
      if (it->value.type!=_VECT)
	p.coord.push_back(monomial<gen>(it->value,i));
      else {
	const_iterateur jt=it->value._VECTptr->begin(),jtend=it->value._VECTptr->end();
	if (jtend==jt)
	  continue;
	i.back()=(jtend-jt)-1;
	for (;jt!=jtend;--i.back(),++jt){
	  if (!is_zero(*jt))
	    p.coord.push_back(monomial<gen>(*jt,i));	
	}
      }
    }
    g=p;
  }

  static void untrunc(vector< T_unsigned<vecteur,hashgcd_U> > & p,int dim){
    vector< T_unsigned<vecteur,hashgcd_U> >::iterator it=p.begin(),itend=p.end();
    for (;it!=itend;++it){
      iterateur jt=it->g.begin(),jtend=it->g.end();
      for (;jt!=jtend;++jt)
	untrunc(*jt,dim);
    }
  }

  static int gcd_ext(const vector< T_unsigned<vecteur,hashgcd_U> > & p0,const vector< T_unsigned<vecteur,hashgcd_U> > & q0,const std::vector<hashgcd_U> & vars,const vecteur & Pmin,int modulo,
	       vector< T_unsigned<vecteur,hashgcd_U> > & D,
	       vector< T_unsigned<vecteur,hashgcd_U> > & Pcof,vector< T_unsigned<vecteur,hashgcd_U> > & Qcof,bool compute_pcof,bool compute_qcof,
	       int nthreads){
#if defined( RTOS_THREADX) || defined(BESTA_OS) || defined(EMCC)
    return 0;
#else
    // FIXME cofactors are not yet implemented
    gen extension;
    const_iterateur it=Pmin.begin(),itend=Pmin.end();
    for (;it!=itend;++it){
      if (it->type==_POLY)
	extension=*it;
      else {
	if (!it->is_integer())
	  return 0;
      }
    }
    if (extension.type==_POLY){
      // return -1; // avoid inf loop while not implemented
      Modred pminmodulo(modulo,Pmin);
      // otherwise give ext variables values, subroutine P van Hoeij-Monagan
      // Algo. for Polynomial GCD computation over Algebraic Function Fields
      int d=1,n=1,innerdim=extension._POLYptr->dim;
      if (innerdim<1)
	return 0; // setsizeerr();
      map<pair<hashgcd_U,index_t>,ext_gcd_t> m; 
      // index is obtained by taking lcoeff with respect to outer var 
      // (hashgcd_U) and inner var (index_t)
      vector< T_unsigned<vecteur,hashgcd_U> > p0tk,q0tk,dtk,pcoftk,qcoftk,nouveau,test,prevtest,tmprem;
      for (int tcount=0;;++tcount){
	int tk=giac_rand(context0) % modulo;
	gen pmintk;
	if (!horner_back_ext(Pmin,tk,modulo,pmintk))
	  return 0;
	if (pmintk.type!=_VECT)
	  continue;
	vecteur & Pmintk = *pmintk._VECTptr;
	if (is_zero(Pmintk.front()))
	  continue;
	if (!horner_back_ext2(p0,tk,modulo,p0tk))
	  continue;
	if (!horner_back_ext2(q0,tk,modulo,q0tk))
	  continue;
	int res=gcd_ext(p0tk,q0tk,vars,Pmintk,modulo,dtk,pcoftk,qcoftk,false,false,nthreads);
	if (res==-1)
	  return -1;
	if (res!=1){
	  ++d;
	  if (d>n)
	    return 0;
	  else
	    continue;
	}
	// dtk lcoeff should not depend on extension variable
	if (dtk.empty())
	  return 0; // setsizeerr();
	vecteur & lcoeffdtk = dtk.front().g;
	if (lcoeffdtk.size()!=1)
	  return 0; // setsizeerr();
	index_t innerlcoeff(innerdim-1);
	if (lcoeffdtk.front().type==_POLY && !lcoeffdtk.front()._POLYptr->coord.empty())
	  innerlcoeff=lcoeffdtk.front()._POLYptr->coord.front().index.iref();
	// adjust m[pair(dtk.front().u,innerlcoeff)]
	pair<hashgcd_U,index_t> pa(dtk.front().u,innerlcoeff);
	map<pair<hashgcd_U,index_t>,ext_gcd_t>::iterator jt=m.find(pa),jtend=m.end();
	if (jt==jtend){
	  m[pa].pi_t_minus_tk=gen(makevecteur(1,-tk),_POLY1__VECT);
	  m[pa].lagrange=dtk;
	}
	else {
	  gen ancienpi=m[pa].pi_t_minus_tk;
	  vector< T_unsigned<vecteur,hashgcd_U> >::iterator jt=m[pa].lagrange.begin(),jtend=m[pa].lagrange.end(),kt=dtk.begin(),ktend=dtk.end();
	  nouveau.clear();
	  for (;;){
	    if (jt==jtend && kt==ktend)
	      break;
	    if (jt==jtend || jt->u>kt->u){
	      nouveau.push_back(T_unsigned<vecteur,hashgcd_U>(interp_tk(vecteur(0),ancienpi,kt->g,tk,modulo),kt->u));
	      ++kt;
	      continue;
	    }
	    if (kt==ktend || jt->u<kt->u){
	      nouveau.push_back(T_unsigned<vecteur,hashgcd_U>(interp_tk(jt->g,ancienpi,vecteur(0),tk,modulo),jt->u));
	      ++jt;
	      continue;
	    }
	    nouveau.push_back(T_unsigned<vecteur,hashgcd_U>(interp_tk(jt->g,ancienpi,kt->g,tk,modulo),kt->u));
	    ++jt;
	    ++kt;
	  }
	  swap(m[pa].lagrange,nouveau);
	  m[pa].pi_t_minus_tk=smod(ancienpi*gen(makevecteur(1,-tk),_POLY1__VECT),modulo);
	}
	// rational reconstruction for m[pa].lagrange modulo m[pa].pi_tk_minus_t
	// must be done on each component of the vecteur
	// which should be either a poly1[] or a _POLY with poly1[] coeffs
	// Clear fractions dependency wrt tk variable
	vector<int> mo;
	vecteur2vector_int(*m[pa].pi_t_minus_tk._VECTptr,modulo,mo);
	if (!polyfracmod(m[pa].lagrange,mo,modulo,test,tcount))
	  continue;
	// now replace inner tk dependency as a poly1[] to an usual polynome
	untrunc(test,innerdim);
	// ?CHECK? clean lcoeff so that it is 1 wrt extension variable z
	// trial division, if success return 1 else continue
	// ?FIXME? use pseudo-division test 
	if (debug_infolevel)
	  cerr << clock() << " algmodgcd hashdivrem " << test.size() <<endl;// << " " << test << endl;
	if (test==prevtest && hashdivrem(p0,test,Pcof,tmprem,vars,pminmodulo,0,true)==1 && tmprem.empty()){
	  if (hashdivrem(q0,test,Qcof,tmprem,vars,pminmodulo,0,true)==1 && tmprem.empty()){
	    if (debug_infolevel)
	      cerr << clock() << " algmodgcd hashdivrem sucess" << endl;
	    D=test;
	    return 1;
	  }
	}
	prevtest=test;
	if (debug_infolevel)
	  cerr << clock() << " algmodgcd hashdivrem failure" << endl;
      }
    } // end extension.type==_POLY
    // int dim=vars.size();
    vector< T_unsigned<vector<int>,hashgcd_U> > p_orig,q_orig,d,pcof,qcof;
    if (!gentoint(p0,p_orig) || !gentoint(q0,q_orig))
      return 0; 
    // ok, ext of dim 0
    vector<int> pmin;
    vecteur2vector_int(Pmin,modulo,pmin);
    int res=mod_gcd_ext(p_orig,q_orig,vars,pmin,modulo,d,pcof,qcof,compute_pcof,compute_qcof,nthreads);
    // convert back 
    inttogen(d,D);
    return res;
#endif // RTOS_THREADX
  }

  vector<int> operator / (const vector<int> & v,const vector<int> & b){
#ifndef NO_STDEXCEPT
    setsizeerr(gettext("vector<int> operator /"));
#endif
    return v;
  }

  vector<int> operator % (const vector<int> & v,const vector<int> & b){
#ifndef NO_STDEXCEPT
    setsizeerr(gettext("vector<int> operator %"));
#endif
    return v;
  }

  bool operator > (const vector<int> & v,double q){
#ifndef NO_STDEXCEPT
    setsizeerr(gettext("vector<int> operator >"));
#endif
    return false;
  }

  static std::vector<int> trim(const std::vector<int> & res){
    // trim res
    std::vector<int>::const_iterator ita=res.begin(),itaend=res.end();
    for (;ita!=itaend;++ita){
      if (*ita)
	break;
    }
    return vector<int>(ita,itaend);
  }

  std::vector<int> operator + (const std::vector<int> & a, const std::vector<int> & b){
    std::vector<int>::const_iterator ita=a.begin(),itaend=a.end(),itb=b.begin(),itbend=b.end();
    unsigned s=itaend-ita,t=itbend-itb;
    if (s>=t){
      std::vector<int> res(a);
      std::vector<int>::iterator itres=res.begin()+(s-t);  
      for (;itb!=itbend;++itb,++itres)
	*itres += (*itb);
      if (res.empty() || res.front())
	return res;
      return trim(res);
    }
    std::vector<int> res(b);
    std::vector<int>::iterator itres=res.begin()+(t-s);  
    for (;ita!=itaend;++ita,++itres)
      *itres += (*ita);
    return res;    
  }

  std::vector<int> operator - (const std::vector<int> & a, const std::vector<int> & b){
    std::vector<int>::const_iterator ita=a.begin(),itaend=a.end(),itb=b.begin(),itbend=b.end();
    unsigned s=itaend-ita,t=itbend-itb;
    if (s>=t){
      std::vector<int> res(a);
      std::vector<int>::iterator itres=res.begin()+(s-t);  
      for (;itb!=itbend;++itb,++itres)
	*itres -= (*itb);
      if (res.empty() || res.front())
	return res;
      // trim res
      return trim(res);
    }
    std::vector<int> res(b);
    std::vector<int>::iterator itres=res.begin();
    for (;t>s;--t,++itres)
      *itres = -*itres;
    for (;ita!=itaend;++ita,++itres)
      *itres = *ita - *itres;
    return res;    
  }

  std::vector<int> operator - (const std::vector<int> & a){
    std::vector<int> res(a);
    std::vector<int>::iterator ita=res.begin(),itaend=res.end();
    for (;ita!=itaend;++ita)
      *ita = -*ita;
    return res;
  }

  std::vector<int> operator % (const std::vector<int> & a,int modulo){
    std::vector<int> res(a);
    std::vector<int>::iterator ita=res.begin(),itaend=res.end();
    for (;ita!=itaend;++ita)
      *ita %= modulo;
    if (res.empty() || res.front())
      return res;
    // trim res
    return trim(res);
  }

  static void distmult_ext(const vector< T_unsigned<vector<int>,hashgcd_U> > & p,const vector< vector<int> > & v,vector< T_unsigned<vector<int>,hashgcd_U> > & pv,hashgcd_U var,const vector<int> & pmin,int modulo){
    if (&pv==&p){
      vector< T_unsigned<vector<int>,hashgcd_U> > tmp;
      distmult_ext(p,v,tmp,var,pmin,modulo);
      swap(pv,tmp);
      return;
    }
    pv.clear();
    vector< T_unsigned<vector<int>,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end();
    vector< vector<int> >::const_iterator jtbeg=v.begin(),jtend=v.end(),jt;
    int vs=jtend-jtbeg,j;
    pv.reserve((itend-it)*vs);
    for (;it!=itend;++it){
      for (jt=jtbeg,j=1;jt!=jtend;++j,++jt){
	if (!is_zero(*jt)){
	  vector<int> tmp;
	  mulext(it->g,*jt,pmin,modulo,tmp);
	  pv.push_back( T_unsigned< vector<int>,hashgcd_U >(tmp,it->u+(vs-j)*var));
        }
      }
    }
  }

  static bool horner(const vector< T_unsigned<vector<int>,hashgcd_U> > & p,int x,const std::vector<hashgcd_U> & vars,vector< T_unsigned<vector<int>,hashgcd_U> > & px,int modulo,int maxdeg){
    px.clear();
    hashgcd_U var=vars[vars.size()-2];
    hashgcd_U var2=vars.back();
    vector< T_unsigned<vector<int>,hashgcd_U> >::const_iterator it=p.begin(),itend=p.end(),it1;
    vector<hashgcd_U>::const_iterator jtend=vars.end()-1;
    hashgcd_U ucur,uend;
    if (maxdeg>=0){
      uend=(maxdeg+1)*vars.front();
      // dichotomy to find start position
      int pos1=0,pos2=itend-it,pos;
      for (;pos2-pos1>1;){
	pos=(pos1+pos2)/2;
	if ((it+pos)->u<uend)
	  pos2=pos;
	else
	  pos1=pos;
      }
      it += pos1;
      if (it->u>=uend)
	++it;
    }
    if (x==0){
      for (;it!=itend;){
	ucur=it->u;
	uend=(ucur/var)*var;
	if (ucur==uend){
	  const vector<int> & g=smod(it->g,modulo);
	  if (!is_zero(g))
	    px.push_back(T_unsigned<vector<int>,hashgcd_U>(g,uend));
	  ++it;
	  continue;
	}
	register int nterms = (ucur-uend)/var2;
	if (nterms<itend-it && (it+nterms)->u==uend){
	  it += nterms;
	  const vector<int> & g=smod(it->g,modulo);
	  if (!is_zero(g))
	    px.push_back(T_unsigned<vector<int>,hashgcd_U>(g,uend));
	  ++it;
	  continue;	  
	}
	for (++it;it!=itend;++it){
	  if (it->u<=uend){
	    if (it->u==uend){
	      const vector<int> & g=smod(it->g,modulo);
	      if (!is_zero(g))
		px.push_back(T_unsigned<vector<int>,hashgcd_U>(g,uend));
	      ++it;
	    }
	    break;
	  }
	}
      }
      return true;
    }
    for (;it!=itend;){
      ucur=it->u;
      uend=(ucur/var)*var;
      if (ucur==uend){
	px.push_back(*it);
	++it;
	continue;
      }
      vector<int> g(0);
      int nterms=(ucur-uend)/var2+1;
      // Check if the next group of monomials is dense wrt to xn
      it1=it+nterms;
      if (//false &&
	  nterms<itend-it && (it1-1)->u==uend
	  ){
	for (;it!=it1;++it){
	  // g = (g*x+it->g)%modulo;
	  mulmod(g,x,modulo);
	  addmod(g,it->g,modulo);
	} 
	g=smod(g,modulo);
	if (!is_zero(g))
	  px.push_back(T_unsigned<vector<int>,hashgcd_U>(g,uend));
	continue;
      }
      for (;it!=itend;++it){
	const hashgcd_U & u=it->u;
	if (u<uend){
	  if (!is_zero(g)){
	    mulmod(g,powmod(x,(ucur-uend)/var2,modulo),modulo);
	    g = smod(g,modulo);
	    if (!is_zero(g))
	      px.push_back(T_unsigned<vector<int>,hashgcd_U>(g,uend));
	  }
	  break;
	}
	mulmod(g,powmod(x,(ucur-u)/var2,modulo),modulo);
	addmod(g,it->g,modulo);
	ucur=u;
      } // end for
      if (it==itend){
	if (!is_zero(g)){
	  mulmod(g,powmod(x,(ucur-uend)/var2,modulo),modulo);
	  g=smod(g,modulo);
	  if (!is_zero(g))
	    px.push_back(T_unsigned<vector<int>,hashgcd_U>(g,uend));
	}
      }
    }
    return true;
  }

#if !defined( RTOS_THREADX) && !defined(BESTA_OS) && !defined(EMCC)
  static void * do_recursive_gcd_ext_call(void * ptr_){
    if (ctrl_c || interrupted)
      return 0;
    gcd_call_param< vector<int> > * ptr = (gcd_call_param< vector<int> > *) ptr_;
    vector< vector<int> > & Delta = *ptr->Delta;
    vector< vector<int> > & lcoeffp = *ptr->lcoeffp;
    vector< vector<int> > & lcoeffq = *ptr->lcoeffq;
    vector<int> & alphav = * ptr->alphav;
    // vector< vector< vector<int> > > & pv = *ptr->pv;
    // vector< vector< vector<int> > > & qv = *ptr->qv;
    vector< vector<int> > dim2palpha;
    vector< vector<int> > dim2qalpha;
    // vector< vector< vector<int> > > & dim2gcdv = *ptr->dim2gcdv;
    // vector< vector< vector<int> > > & dim2pcofactorv = *ptr->dim2pcofactorv;
    // vector< vector< vector<int> > > & dim2qcofactorv = *ptr->dim2qcofactorv;
    vector< T_unsigned< vector<int> ,hashgcd_U> > & p = * ptr->p;
    vector< T_unsigned< vector<int> ,hashgcd_U> > & q = * ptr->q;
    vector< vector< T_unsigned< vector<int> ,hashgcd_U> > > & gcdv = * ptr->gcdv;
    vector< vector< T_unsigned< vector<int> ,hashgcd_U> > > & pcofactorv = * ptr->pcofactorv;
    vector< vector< T_unsigned< vector<int> ,hashgcd_U> > > & qcofactorv = * ptr->qcofactorv;
    index_t & pdeg = * ptr->pdeg;
    index_t & qdeg = * ptr->qdeg;
    vector< T_unsigned< vector<int> ,hashgcd_U> > palpha,qalpha;
    index_t pdegalpha,qdegalpha;
    int vpos=ptr->vpos;
    int alpha1 = alphav[vpos];
    int modulo = ptr->modulo;
    const vector<int> & pmin = *ptr->pminptr;
    const vector<hashgcd_U> & vars = * ptr->vars;
    vector<hashgcd_U> & vars_truncated = * ptr->vars_truncated;
    // index_t & shift_vars = *ptr->shift_vars;
    index_t & shift_vars_truncated = *ptr->shift_vars_truncated;
    bool compute_cof = ptr->compute_cof;
    bool compute_qcofactor = ptr->compute_qcofactor;
    // bool dim2 = ptr->dim2;
    int nthreads = ptr->nthreads;
    // Eval p and q at xn=alpha
    if (!horner(p,alpha1,vars,palpha,modulo,-1))
      return 0;
    degree(palpha,shift_vars_truncated,pdegalpha);
    pdegalpha.push_back(pdeg.back());
    if (pdegalpha!=pdeg)
      return 0;
    if (!horner(q,alpha1,vars,qalpha,modulo,-1))
      return 0;
    degree(qalpha,shift_vars_truncated,qdegalpha);
    qdegalpha.push_back(qdeg.back());
    if (qdegalpha!=qdeg)
      return 0;
    vector< T_unsigned< vector<int> ,hashgcd_U> > & g=gcdv[vpos];
    vector< T_unsigned< vector<int> ,hashgcd_U> > & gp=pcofactorv[vpos];
    vector< T_unsigned< vector<int> ,hashgcd_U> > & gq=qcofactorv[vpos];
    if ( (ptr->ext_gcd_ok=mod_gcd_ext(palpha,qalpha,vars_truncated,pmin,modulo,g,gp,gq,compute_cof,compute_qcofactor,nthreads))!=1){
      g.clear();
      return ptr;
    }
    vector<int> tmp;
    // adjust lcoeff of gcd to be the same as Delta
    // smallmult(smod(hornermod(Delta,alpha1,modulo)*longlong(invmod(g.front().g,modulo)),modulo),g,g,modulo);
    if (!invmodext(g.front().g,pmin,modulo,tmp)){
      ptr->ext_gcd_ok=-1;
      return ptr;
    }
    mulext(tmp,hornermod_ext(Delta,alpha1,modulo),pmin,modulo);
    smallmult(tmp,g,g,modred(modulo,pmin));
    if (compute_cof){
      // adjust gp lcoeff
      // smallmult(smod(hornermod(lcoeffp,alpha1,modulo)*longlong(invmod(gp.front().g,modulo)),modulo),gp,gp,modulo);
      if (!invmodext(gp.front().g,pmin,modulo,tmp)){
	ptr->ext_gcd_ok=-1;
	return ptr;
      }
      mulext(tmp,hornermod_ext(lcoeffp,alpha1,modulo),pmin,modulo);
      smallmult(tmp,gp,gp,modred(modulo,pmin));
      if (compute_qcofactor){
	// adjust gq cofactor
	// smallmult(smod(hornermod(lcoeffq,alpha1,modulo)*longlong(invmod(gq.front().g,modulo)),modulo),gq,gq,modulo);
	if (!invmodext(gq.front().g,pmin,modulo,tmp)){
	  ptr->ext_gcd_ok=-1;
	  return ptr;
	}
	mulext(tmp,hornermod_ext(lcoeffq,alpha1,modulo),pmin,modulo);
	smallmult(tmp,gq,gq,modred(modulo,pmin));
      }
    }
    return ptr;
  }
#endif //RTOS_THREADX

  static int mod_gcd_ext(const vector< T_unsigned<vector<int>,hashgcd_U> > & p_orig,const vector< T_unsigned<vector<int>,hashgcd_U> > & q_orig,const std::vector<hashgcd_U> & vars,const vector<int> & pmin,int modulo,
	      vector< T_unsigned<vector<int>,hashgcd_U> > & d,
	      vector< T_unsigned<vector<int>,hashgcd_U> > & pcof,vector< T_unsigned<vector<int>,hashgcd_U> > & qcof,bool compute_pcofactor,bool compute_qcofactor,
	      int nthreads){
#if defined( RTOS_THREADX) || defined(BESTA_OS) || defined(EMCC)
    return 0;
#else
    //nthreads=1; // FIXME, !=1 segfaults on compaq mini
    hashgcd_U var=vars.front();
    int dim=vars.size();
    if (dim==1){
      vector< vector<int> > P,Q,D;
      convert(p_orig,var,P);
      convert(q_orig,var,Q);
      if (!gcdsmallmodpoly_ext(P,Q,pmin,modulo,D))
	return 0;
      convert_back(D,var,d);
      return 1;
    }
    modred pminmodulo(modulo,pmin);
    // multi-dim gcd where pmin does not depend on vars -> Brown's recursive P algorithm
    // normalization: leading coeff in Z = 1
    std::vector<hashgcd_U> vars_truncated(vars);
    vars_truncated.pop_back();
    hashgcd_U varxn=vars_truncated.back(),var2=vars.back();
    index_t shift_vars,shift_vars_truncated;
    if (!find_shift(vars,shift_vars))
      return 0; // setsizeerr();
    shift_vars_truncated=shift_vars;
    shift_vars_truncated.pop_back();
    short int shiftxn=shift_vars_truncated.back(),shift2=shift_vars.back();
    // Make p and q primitive as polynomials in x1,...,xn-1
    // with coeff polynomial in xn
    vector< T_unsigned<vector<int>,hashgcd_U> > p(p_orig),q(q_orig),pcont,qcont,dcont,tmp,pcofactor,qcofactor;
    vector< vector<int> > pcontxn,qcontxn,dcontxn,pcofcontxn,qcofcontxn;
    if (debug_infolevel>20-dim)
      cerr << "gcdmod_ext threads " << nthreads << " content begin " << "dim " << dim << " " << clock() << endl;
    if (!pp_mod(p,&pmin,modulo,varxn,var2,pcontxn))
      return 0;
    if (!pp_mod(q,&pmin,modulo,varxn,var2,qcontxn))
      return 0;
    if (!gcdsmallmodpoly_ext(pcontxn,qcontxn,pmin,modulo,dcontxn))
      return 0;
    if (debug_infolevel>20-dim)
      cerr << "gcdmod content in " << "dim " << dim << " " << clock() << endl;
    // Make p and q primitive as polynomial in xn with coeff in x1...xn-1
    if (!pp_mod(p,&pmin,modulo,vars,pcont,nthreads))
      return 0;
    if (!pp_mod(q,&pmin,modulo,vars,qcont,nthreads))
      return 0;
    mod_gcd_ext(pcont,qcont,vars_truncated,pmin,modulo,dcont,pcofactor,qcofactor,false,false,nthreads); // don't use pv and qv here!
    // multiply pcofactor and qcofactor by the initial contents dep. on xn
    if (debug_infolevel>20-dim)
      cerr << "gcdmod content end " << "dim " << dim << " " << clock() << endl;
    distmult_ext(dcont,dcontxn,dcont,var2,pmin,modulo);
    // ready for gcd computation by interpolation with respect to xn
    // first find degree of gcd with respect to xn
    int pxndeg=degree_xn(p,shiftxn,shift2),qxndeg=degree_xn(q,shiftxn,shift2),gcddeg=0;
    vector< vector<int> > pb(pxndeg+1),qb(qxndeg+1),db;
    vector<int> b(dim-1),bnext(dim-1);
    index_t vzero; // coeff of vzero correspond to zero or non zero
    int nzero=1; // Number of zero coeffs
    for (int essai=0;essai<2;){
      if (debug_infolevel>20-dim)
	cerr << "gcdmod degree? " << essai << " dim " << dim << " " << clock() << endl;
      if (!horner(p,b,vars,pb,modulo) ||
	  !horner(q,b,vars,qb,modulo))
	return false;
      for (;;){
	for (int i=0;i<dim-1;++i)
	  bnext[i]=std::rand() % modulo;
	if (bnext!=b){ b=bnext; break; }
      }
      if (int(pb.size())!=pxndeg+1 || int(qb.size())!=qxndeg+1)
	continue;
      if (!gcdsmallmodpoly_ext(pb,qb,pmin,modulo,db))
	continue;
      int dbdeg=db.size()-1;
      if (!dbdeg){
	d=dcont;
	if (compute_pcofactor){
	  smallmult(p,pcofactor,pcofactor,pminmodulo,0);
	  vector<int> tmp(invmod(pcofactor.front().g,pminmodulo));
	  mulext(tmp,p_orig.front().g,pmin,modulo);
	  smallmult(tmp,pcofactor,pcofactor,pminmodulo);
	}
	if (compute_qcofactor){
	  smallmult(q,qcofactor,qcofactor,pminmodulo,0);
	  vector<int> tmp(invmod(qcofactor.front().g,pminmodulo));
	  mulext(tmp,q_orig.front().g,pmin,modulo);
	  smallmult(tmp,qcofactor,qcofactor,pminmodulo);
	}

	return true;
      }
      if (!essai){ // 1st gcd test
	gcddeg=dbdeg;
	nzero=find_nonzero(db,vzero);
	++essai;
	continue;
      }
      // 2nd try
      if (dbdeg<gcddeg){ // 1st try was unlucky, restart 1st try
	gcddeg=dbdeg;
	nzero=find_nonzero(db,vzero);
	continue;
      }
      if (dbdeg!=gcddeg) 
	  continue;
      // Same gcd degree for 1st and 2nd try, keep this degree
      index_t tmp;
      nzero=find_nonzero(db,tmp);
      if (nzero){
	vzero = vzero | tmp;
	// Recompute nzero, it is the number of 0 coeff of vzero
	index_t::const_iterator it=vzero.begin(),itend=vzero.end();
	for (nzero=0;it!=itend;++it){
	  if (!*it) 
	    ++nzero;
	}
      }
      ++essai;
    } // end for (essai)
    bool pqswap=qxndeg<pxndeg; // swap p and q ?
    if (pqswap){
      swap(p,q);
      swap(pcont,qcont);
      swap(pcofactor,qcofactor);
      swap(pxndeg,qxndeg);
      // swap(pv,qv);
#ifdef BESTA_OS
      bool tmpbool=compute_pcofactor;
      compute_pcofactor=compute_qcofactor;
      compute_qcofactor=tmpbool;
      // BESTA DOES NOT LIKE THE FOLLOWING LINE OF CODE
#else
      swap(compute_pcofactor,compute_qcofactor);
#endif
    }
    vector< vector<int> > lcoeffp,lcoeffq,lcoeffg,Delta,tmpcont;
    hashgcd_U lcoeffpu,lcoeffqu;
    if (debug_infolevel>20-dim)
      cerr << "gcdmod lcoeff begin " << "dim " << dim << " " << clock() << endl;
    lcoeffpu=lcoeff(p,varxn,var2,lcoeffp);
    lcoeffqu=lcoeff(q,varxn,var2,lcoeffq);
    if (!gcdsmallmodpoly_ext(lcoeffp,lcoeffq,pmin,modulo,Delta))
      return 0;
    if (debug_infolevel>20-dim){
      cerr << "lcoeff p, q, gcd" << lcoeffp << "," << lcoeffq << "," << Delta << endl;
      cerr << "gcdmod lcoeff end " << "dim " << dim << " " << clock() << endl;
    }
    // estimate time for full lift or division try
    // size=p.size()+q.size()
    // sumdeg=pxndeg+qxndeg
    // %age=gcddeg/min(pxndeg,qxndeg)
    // %age^dim*(1-%age)^dim*size^2 estimates the time for division try
    // gcddeg*size estimates the time for lifting to gcddeg
    // sumdeg*size estimates the time for full lifting
    // if sumdeg<(gcddeg+%age^dim*(1-%age)^dim*size) do full lifting
    int Deltadeg = Delta.size()-1,liftdeg=(compute_qcofactor?qxndeg:pxndeg)+Deltadeg;
    int gcddeg_plus_delta=gcddeg+Deltadeg;
    int liftdeg0=giacmax(liftdeg-gcddeg,gcddeg_plus_delta);
    // once liftdeg0 is reached we can replace g/gp/gq computation
    // by a check that d*dp=dxn*lcoeff(d*dp)/Delta at alpha
    // and d*dq=dxn*lcoeff(d*dq)/lcoeff(qxn) at alpha
    int sumdeg = pxndeg+qxndeg;
    double percentage = double(gcddeg)/giacmin(pxndeg,qxndeg);
    int sumsize = p.size()+q.size();
    // ? add a malus factor for division
    double gcdlift=gcddeg+std::pow(percentage,dim)*std::pow(1-percentage,dim)*sumsize;
    bool compute_cof = false; // dim==2 || sumdeg<gcdlift;
    // we are now interpolating G=gcd(p,q)*a poly/xn
    // such that the leading coeff of G is Delta
    index_t pdeg(dim),qdeg(dim),pdegalpha(dim),qdegalpha(dim);
    if (debug_infolevel>20-dim)
      cerr << "gcdmod ext degree begin " << "dim " << dim << " " << clock() << " compute_cof " << compute_cof << "(" << sumdeg/gcdlift << ")" << endl;
    int ptotaldeg=degree(p,shift_vars,pdeg);
    int qtotaldeg=degree(q,shift_vars,qdeg);
    if (debug_infolevel>20-dim){
      cerr << "pdeg " << pdeg << " " << ptotaldeg << endl;
      cerr << "qdeg " << qdeg << " " << qtotaldeg << endl;
    }
    if (debug_infolevel>20-dim)
      cerr << "gcdmod degree end " << "dim " << dim << " " << clock() << endl;
    int spdeg=0,sqdeg=0;
    for (int i=0;i<dim-1;++i){
      spdeg += pdeg[i];
      sqdeg += qdeg[i];
    }
    index_t gdeg(dim-1),delta=min(pdeg,qdeg);
    delta.pop_back();
    int e=0; // number of evaluations
    int alpha,alpha1;
    if (debug_infolevel>20-dim)
      cerr << "gcdmod find alpha dim " << dim << " " << clock() << endl;
    if (debug_infolevel>20-dim)
      cerr << " p " << p << " q " << q << endl;
    vector< T_unsigned<vector<int>,hashgcd_U> > palpha,qalpha,dp,dq; // d, dp and dq are the current interpolated values of gcd and cofactors
    vector<int> alphav;
    vector< vector< T_unsigned<vector<int>,hashgcd_U> > > gcdv,pcofactorv,qcofactorv;
    gcd_call_param< vector<int> > gcd_par;
    gcd_par.Delta=&Delta;
    gcd_par.lcoeffp=&lcoeffp;
    gcd_par.lcoeffq=&lcoeffq;
    gcd_par.alphav=&alphav;
    gcd_par.pv=0;
    gcd_par.qv=0;
    gcd_par.dim2gcdv=0;
    gcd_par.dim2pcofactorv=0;
    gcd_par.dim2qcofactorv=0;
    gcd_par.p=&p;
    gcd_par.q=&q;
    gcd_par.gcdv=&gcdv;
    gcd_par.pcofactorv=&pcofactorv;
    gcd_par.qcofactorv=&qcofactorv;
    gcd_par.pdeg=&pdeg;
    gcd_par.qdeg=&qdeg;
    gcd_par.vars=&vars;
    gcd_par.vars_truncated=&vars_truncated;
    gcd_par.shift_vars=&shift_vars;
    gcd_par.shift_vars_truncated=&shift_vars_truncated;
    gcd_par.compute_cof=compute_cof;
    gcd_par.compute_qcofactor=compute_qcofactor;
    gcd_par.dim2=false;
    gcd_par.pminptr=&pmin;
    gcd_par.modulo=modulo;
    // Warning: leaving nthreads > 1 is a bad idea if too many allocations
    // with lock happen
    if (dim>3 && sumsize > modgcd_cachesize ){
      gcd_par.nthreads=nthreads;
      nthreads=1;
    }
    else
      gcd_par.nthreads=1;      
    if (nthreads>gcddeg_plus_delta)
      nthreads=gcddeg_plus_delta+1;
    if (nthreads>1){
      int todo=compute_cof?(liftdeg0+1):(gcddeg_plus_delta+1);
      double nth=todo/double(nthreads);
      // if (nthreads>=4 && (todo%nthreads==0)) nth = (todo+1)/double(nthreads); // keep one proc for a bad prime
      nth=std::ceil(nth);
      nthreads=int(std::ceil(todo/nth));
      if (debug_infolevel>20-dim)
	cerr << "Using " << nthreads << " threads " << nth << " " << todo/nth << endl;
    }
    // return -1; // avoid inf loop while not implemented
    /* ******************************* 
       BEGIN LOOP
       ******************************* */
    int gcd_ext_ok=1;
    for (alpha=-1;;){
      if (gcd_ext_ok!=1)
	return -1;
      // First check if we are ready to interpolate
      if (!compute_cof && e>gcddeg_plus_delta){
	if (debug_infolevel>20-dim){
	  cerr << "gcdmod before interp " << dim << " clock= " << clock() << gcdv << endl;
	}
	interpolate(alphav,gcdv,d,var2,modulo);
	vector< T_unsigned<vector<int>,hashgcd_U> > pquo,qquo,tmprem,pD(d);
	pp_mod(pD,&pmin,modulo,varxn,var2,tmpcont);
	if (debug_infolevel>20-dim){
	  cerr << "gcdmod pp1mod dim " << dim << " clock= " << clock() << " d " << d << endl;
	  cerr << "gcdmod alphav " << alphav << endl << "gcdv " << gcdv << endl
	       << "gcdmod content " << tmpcont << endl;
	}
	// This removes the polynomial in xn that we multiplied by
	// (it was necessary to know the lcoeff of the interpolated poly)
	if (debug_infolevel>20-dim)
	  cerr << "gcdmod check dim " << dim << " " << clock() << endl;
	// Now, gcd divides pD for gcddeg+1 values of x1
	// degree(pD)<=degree(gcd)
	// ?CHECK? should we pseudo-divide?
	if (hashdivrem(p,pD,pquo,tmprem,vars,pminmodulo,0,true)==1 && tmprem.empty()){
	  if (hashdivrem(q,pD,qquo,tmprem,vars,pminmodulo,0,true)==1 && tmprem.empty()){
	    smallmult(pD,dcont,d,pminmodulo,0);
	    smallmult(invmod(d.front().g,pminmodulo),d,d,pminmodulo);
	    if (compute_pcofactor){
	      smallmult(pcofactor,pquo,pcofactor,pminmodulo,0);
	      vector<int> tmp(invmod(pcofactor.front().g,pminmodulo));
	      mulext(tmp,p_orig.front().g,pmin,modulo);
	      smallmult(tmp,pcofactor,pcofactor,pminmodulo);
	    }
	    if (compute_qcofactor){
	      smallmult(qcofactor,qquo,qcofactor,pminmodulo,0);
	      vector<int> tmp(invmod(qcofactor.front().g,pminmodulo));
	      mulext(tmp,q_orig.front().g,pmin,modulo);
	      smallmult(tmp,qcofactor,qcofactor,pminmodulo);
	    }
	    if (debug_infolevel>20-dim)
	      cerr << "gcdmod found dim " << dim << " " << clock() << endl;
	    if (pqswap)
	      swap(pcofactor,qcofactor);
	    return true;
	  } // end if hashdivrem(q,...)
	} // end if hashdivrem(p,...)
	if (debug_infolevel>20-dim)
	  cerr << "Gcdmod bad guess " << endl;
	// restart
	gcdv.clear(); alphav.clear(); 
	pcofactorv.clear(); qcofactorv.clear(); 
	e=0;
      } // end if (e>gcddeg+delta)
      
      if (compute_cof && e>liftdeg0 ){ 
	// interpolate d and dp
	interpolate(alphav,gcdv,d,var2,modulo);
	if (debug_infolevel>20-dim)
	  cerr << "end interpolate gcd " << clock() << endl;
	interpolate(alphav,pcofactorv,dp,var2,modulo);
	if (debug_infolevel>20-dim)
	  cerr << "end interpolate p cof " << clock() << endl;
	// check that d(alpha)*dp(alpha)=palpha with lcoeff adjusted
	// for e<=liftdeg
	vector< T_unsigned<vector<int>,hashgcd_U> > g,gp;
	for (++alpha;e<=liftdeg;++e,++alpha){
	  alpha1=alpha%2?-(alpha+1)/2:alpha/2;
	  while (is_zero(hornermod_ext(lcoeffp,alpha1,modulo))){
	    ++alpha;
	    alpha1=alpha%2?-(alpha+1)/2:alpha/2;
	  }
	  if (alpha>=modulo)
	    return false;
	  int maxtotaldeg=ptotaldeg+1-e;
	  if (!horner(p,alpha1,vars,palpha,modulo,maxtotaldeg))
	    return false;
	  if (debug_infolevel>20-dim)
	    cerr << "gcdmod horner d " << alpha << " dim " << dim << " " << clock() << endl;
	  if (!horner(d,alpha1,vars,g,modulo,maxtotaldeg))
	    return false;
	  if (debug_infolevel>20-dim)
	    cerr << "gcdmod horner dp " << alpha << " dim " << dim << " " << clock() << endl;
	  if (!horner(dp,alpha1,vars,gp,modulo,maxtotaldeg))
	    return false;
	  vector<int> tmp;
	  mulext(gp.back().g,g.back().g,pmin,modulo,tmp);
	  invmod(tmp,pminmodulo);
	  mulext(tmp,palpha.back().g,pmin,modulo);
	  smallmult(tmp,gp,gp,pminmodulo);
	  // FIXME is_p_a_times_b
	  if (true 
	      // !is_p_a_times_b(palpha,gp,g,vars,modulo,maxtotaldeg)
	      ){
	    // Bad guess, go find some new gcd and interpolate
	    e=liftdeg0+1;
	    break;
	  }
	} // end for ( e loop )
	if (e>liftdeg){ 
	  // enough evaluation point
	  // divide d,dp,dq by their content in xn
	  pp_mod(d,&pmin,modulo,varxn,var2,tmpcont);
	  pp_mod(dp,&pmin,modulo,varxn,var2,tmpcont);
	  // check xn degrees of d+dp=degree(pxn), d+dq=degree(qxn)
	  int dxndeg=degree_xn(d,shiftxn,shift2),dpxndeg=degree_xn(dp,shiftxn,shift2);
	  // int dqxndeg=degree_xn(dq,shiftxn,shift2);
	  if ( dxndeg+dpxndeg==pdeg.back() ){
	    smallmult(d,dcont,d,pminmodulo,0);
	    if (compute_pcofactor){
	      smallmult(dp,pcofactor,pcofactor,pminmodulo,0);
	      vector<int> tmp(invmod(pcofactor.front().g,pminmodulo));
	      mulext(tmp,p_orig.front().g,pmin,modulo);
	      smallmult(tmp,pcofactor,pcofactor,pminmodulo);
	    }
	    if (compute_qcofactor){
	      interpolate(alphav,qcofactorv,dq,var2,modulo);
	      pp_mod(dq,&pmin,modulo,varxn,var2,tmpcont);
	      smallmult(dq,qcofactor,qcofactor,pminmodulo,0);
	      vector<int> tmp(invmod(qcofactor.front().g,pminmodulo));
	      mulext(tmp,q_orig.front().g,pmin,modulo);
	      smallmult(tmp,qcofactor,qcofactor,pminmodulo);
	    }
	    if (debug_infolevel>20-dim)
	      cerr << "gcdmod end dim " << dim << " " << clock() << endl;
	    if (pqswap)
	      swap(pcofactor,qcofactor);
	    // divtest=false;
	    return true;
	  }
	  // failure, restart
	  gcdv.clear(); alphav.clear(); 
	  pcofactorv.clear(); qcofactorv.clear(); 
	  e=0;
	} // end if (e>lifdeg)
      } // end if (compute_cof && e in liftdeg0..liftdeg)

      // *************************************************** //
      // Not ready to interpolate, try to eval new points
      // *************************************************** //

      for (int thread=0;thread<nthreads;++thread){
	// find a good alpha with respect to leading coeff
	for (;;){
	  ++alpha;
	  if (alpha==modulo){
	    cerr << "Modgcd: no suitable evaluation point" << endl;
	    return false;
	  }
	  alpha1=alpha%2?-(alpha+1)/2:alpha/2;
	  if (is_zero(hornermod_ext(lcoeffp,alpha1,modulo)) || is_zero(hornermod_ext(lcoeffq,alpha1,modulo)))
	    continue;
	  if (debug_infolevel>20-dim)
	    cerr << "gcdmod eval alpha1=" << alpha1 << " dim " << dim << " " << clock() << endl;
	  break;
	} // end for (;;)
	// alpha is probably admissible 
	// (we will test later if degree of palpha/qalpha wrt x1...xn-1 is max)
	// prepare room for gcd and cofactors
	if (debug_infolevel>20-dim)
	  cerr << "dim " << dim << " palpha " << palpha << " qalpha " << qalpha << endl ;
	alphav.push_back(alpha1);
	gcdv.push_back(vector< T_unsigned< vector<int>,hashgcd_U> >(0));
	pcofactorv.push_back(vector< T_unsigned< vector<int>,hashgcd_U> >(0));
	qcofactorv.push_back(vector< T_unsigned< vector<int>,hashgcd_U> >(0));
      } // end for (int thread=0;thread<nthreads;++thread)
      vector<gcd_call_param<vector<int> > > gcd_call_param_v(nthreads,gcd_par);
#ifdef HAVE_PTHREAD_H
      pthread_t tab[nthreads-1];
#endif
      for (int thread=0;thread<nthreads;++thread){
	int vpos=alphav.size()-(nthreads-thread);
	gcd_call_param_v[thread].vpos=vpos;
	if (thread!=nthreads-1){
	  gcd_call_param_v[thread].pv=0;
	  gcd_call_param_v[thread].qv=0;
	}
#ifdef HAVE_PTHREAD_H
	if (thread==nthreads-1)
	  do_recursive_gcd_ext_call((void *)&gcd_call_param_v[thread]);
	else { // launch gcd computation in a separate thread
	  bool res=pthread_create(&tab[thread],(pthread_attr_t *) NULL,do_recursive_gcd_ext_call,(void *) &gcd_call_param_v[thread]);
	  if (res)
	    do_recursive_gcd_ext_call((void *)&gcd_call_param_v[thread]);
	}
#else
	do_recursive_gcd_ext_call((void *)&gcd_call_param_v[thread]);
#endif
      }
      // wait for all threads before doing modifications in gcdv and co
      for (int thread=0;thread<nthreads-1;++thread){
	if (ctrl_c || interrupted)
	  return 0;
	int vpos=alphav.size()-(nthreads-thread);
	// wait for thread to finish
#ifdef HAVE_PTHREAD_H
	if (thread!=nthreads-1){
	  void * ptr;
	  pthread_join(tab[thread],&ptr);
	  if (!ptr){
	    gcdv[vpos].clear();
	  }
	  if (gcd_ext_ok==1)
	    gcd_ext_ok=((gcd_call_param<vector<int> > *) ptr)->ext_gcd_ok;
	}
	else {
	  if (gcd_ext_ok==1)
	    gcd_ext_ok=gcd_call_param_v[thread].ext_gcd_ok;
	}
#else
	if (gcd_ext_ok==1)
	  gcd_ext_ok=gcd_call_param_v[thread].ext_gcd_ok;
#endif
	if (gcd_ext_ok!=1)
	  continue; // improve: kill other threads
      }
      if (gcd_ext_ok!=1)
	continue; 
      for (int thread=0;thread<nthreads;++thread){
	int vpos=alphav.size()-(nthreads-thread);
	// Compare gcd degree in x1..xn-1, 0 means trash this value of alpha1
	int comp=0;
	if (!gcdv[vpos].empty()){
	  degree(gcdv[vpos],shift_vars_truncated,gdeg);
	  comp=compare(gdeg,delta); 
	}
	if (comp==-2){
	  // same degrees, add to interpolation
	  /* FIXME SPMOD
	  // Try spmod first
	  if (!compute_cof && nzero){
	    // Add alpha,g 
	    if (dim>2 && gcddeg-nzero==e){ 
	      // We have enough evaluations, let's try SPMOD
	      // Build the matrix, each line has coeffs / vzero
	      vector< vector<int> > m,minverse;
	      for (int j=0;j<=e;++j){
		index_t::const_reverse_iterator it=vzero.rbegin(),itend=vzero.rend();
		vector<int> line;
		for (int p=alphav[j],pp=1;it!=itend;++it,pp=smod(p*pp,modulo)){
		  if (*it)
		    line.push_back(pp);
		}
		reverse(line.begin(),line.end());
		m.push_back(line);
	      }
	      // assume gcd is the vector of non zero coeffs of the gcd in x^n
	      // we have the relation
	      // m*gcd=gcdv
	      // invert m (if invertible)
	      longlong det_mod_p;
	      if (smallmodinv(m,minverse,modulo,det_mod_p) && det_mod_p){
		// hence gcd=minverse*gcdv, where the i-th component of gcd
		// must be "multiplied" by xn^degree_corresponding_vzero[i]
		vector< vector< T_unsigned<int,hashgcd_U> > > minversegcd(e+1);
		for (int j=0;j<=e;++j){
		  for (int k=0;k<=e;++k){
		    vector< T_unsigned<int,hashgcd_U> > tmp(gcdv[k]);
		    smallmult(minverse[j][k],tmp,tmp,modulo);
		    smalladd(minversegcd[j],tmp,minversegcd[j],modulo);
		    // cerr << minversegcd[j] << endl;
		  }
		}
		vector< T_unsigned<int,hashgcd_U> > trygcd,pquo,qquo,tmprem;
		index_t::const_iterator it=vzero.begin(),itend=vzero.end();
		int deg=itend-it-1;
		for (int j=0;it!=itend;++it,--deg){
		  if (!*it)
		    continue;
		  smallshift(minversegcd[j],deg*var2,minversegcd[j]);
		  smalladd(trygcd,minversegcd[j],trygcd,modulo);
		  ++j;
		}
		// Check if trygcd is the gcd!
		pp_mod(trygcd,0,modulo,varxn,var2,tmpcont);
		if (hashdivrem(p,trygcd,pquo,tmprem,vars,modulo,0,false)==1 && tmprem.empty()){
		  if (hashdivrem(q,trygcd,qquo,tmprem,vars,modulo,0,false)==1 && tmprem.empty()){
		    smallmult(trygcd,dcont,d,modulo,0);
		    smallmult(invmod(d.front().g,modulo),d,d,modulo);
		    if (compute_pcofactor){
		      smallmult(pcofactor,pquo,pcofactor,modulo,0);
		    smallmult(smod(longlong(p_orig.front().g)*invmod(pcofactor.front().g,modulo),modulo),pcofactor,pcofactor);
		    }
		    if (compute_qcofactor){
		      smallmult(qcofactor,qquo,qcofactor,modulo,0);
		      smallmult(smod(longlong(q_orig.front().g)*invmod(qcofactor.front().g,modulo),modulo),qcofactor,qcofactor);
		    }
		    if (debug_infolevel>20-dim)
		      cerr << "gcdmod found dim " << dim << " " << clock() << endl;
		    if (pqswap)
		      swap(pcofactor,qcofactor);
		    return true;
		  } // end q divisible by trygcd
		} // end p divisible by trygcd
	      } // end m invertible
	    } // end if (dim>2 && )
	  } // end SPMOD
	  */
	  if (debug_infolevel>20-dim)
	    cerr << "gcdmod interp dim " << dim << " " << clock() << endl;
	  ++e;
	  continue;
	} // end gdeg==delta
	if (comp==0 || comp==-1){ 
	  if (debug_infolevel>20-dim)
	    cerr << "Bad reduction " << alphav[vpos] << endl;
	  // bad reduction: all indices of gdeg are >= to delta and gdeg!=delta
	  alphav.erase(alphav.begin()+vpos);
	  gcdv.erase(gcdv.begin()+vpos);
	  pcofactorv.erase(pcofactorv.begin()+vpos);
	  qcofactorv.erase(qcofactorv.begin()+vpos);
	  if (comp==0)
	    continue;
	}
	// previous alpha where bad reduction
	if (debug_infolevel>20-dim && vpos)
	  cerr << "Bads reductions " << alphav[vpos-1] << endl;
	alphav.erase(alphav.begin(),alphav.begin()+vpos);
	gcdv.erase(gcdv.begin(),gcdv.begin()+vpos); 
	pcofactorv.erase(pcofactorv.begin(),pcofactorv.begin()+vpos);
	qcofactorv.erase(qcofactorv.begin(),qcofactorv.begin()+vpos);       
	if (comp==-1){ 
	  e=0;
	  continue;
	}
	// restart everything with this value of alpha
	// this will happen (almost all the time) at first iteration
	delta=gdeg;
	e=1;
	continue;
      } // end for (int thread=0;thread<nthreads;++thread)
    } // end for (alpha=-1;;)
#endif // RTOS_THREADX
  }

  static gen ichinrem_ext(const vecteur & v,const gen & g,const gen & modulo,const gen & pimod){
    if (g.type!=_VECT){
      vecteur w(v);
      iterateur it=w.begin(),itend=w.end(),it_;
      it_=itend-1;
      for (;it!=itend;++it){
	*it=ichinrem(*it,(it==it_?g:0),modulo,pimod);
      }
      return w;
    }
    vecteur & w = *g._VECTptr;
    vecteur::const_reverse_iterator it=v.rbegin(),itend=v.rend();
    vecteur::reverse_iterator jt=w.rbegin(),jtend=w.rend();
    vecteur res;
    for (;it!=itend && jt!=jtend;++it,++jt){
      res.push_back(ichinrem(*it,*jt,modulo,pimod));
    }
    for (;it!=itend;++it)
      res.push_back(ichinrem(*it,0,modulo,pimod));
    for (;jt!=jtend;++jt)
      res.push_back(ichinrem(0,*jt,modulo,pimod));
    reverse(res.begin(),res.end());
    return res;
  }

  static void ichinrem_ext(const vector< T_unsigned<vecteur,hashgcd_U> > & p_orig,const gen & modulo,vector< T_unsigned<gen,hashgcd_U> > & p,const gen & pimod){
    vector< T_unsigned<gen,hashgcd_U> > q;
    vector< T_unsigned< vecteur,hashgcd_U> >::const_iterator it=p_orig.begin(),itend=p_orig.end();
    vector< T_unsigned<gen,hashgcd_U> >::const_iterator jt=p.begin(),jtend=p.end();
    q.reserve(jtend-jt);
    for (;it!=itend && jt!=jtend;){
      if (it->u==jt->u){
	const vecteur & tmp=it->g;
	q.push_back(T_unsigned<gen,hashgcd_U>(ichinrem_ext(tmp,jt->g,modulo,pimod),it->u));
	++it; ++jt;
	continue;
      }
      if (it->u<jt->u){
	q.push_back(T_unsigned<gen,hashgcd_U>(ichinrem_ext(vecteur(0),jt->g,modulo,pimod),jt->u));
	++jt;
      }
      else {
	const vecteur & tmp=it->g;
	q.push_back(T_unsigned<gen,hashgcd_U>(ichinrem_ext(tmp,0,modulo,pimod),it->u));
	++it;
      }
    }
    for (;it!=itend;++it){
      const vecteur & tmp=it->g;
      q.push_back(T_unsigned<gen,hashgcd_U>(ichinrem_ext(tmp,0,modulo,pimod),it->u));
    }
    for (;jt!=jtend;++jt)
      q.push_back(T_unsigned<gen,hashgcd_U>(ichinrem_ext(vecteur(0),jt->g,modulo,pimod),jt->u));
    swap(p,q);
  }

  // add pmin extension to all vecteurs inside p
  static void make_ext(vector< T_unsigned<gen,hashgcd_U> > &p,const gen & pmin){
    vector< T_unsigned<gen,hashgcd_U> >::iterator jt=p.begin(),jtend=p.end();
    for (;jt!=jtend;++jt){
      if (jt->g.type==_VECT)
	jt->g=algebraic_EXTension(jt->g,pmin);
    }
  }

  static bool fracmod(const vector< T_unsigned<gen,hashgcd_U> > &p,const gen & modulo,vector< T_unsigned<gen,hashgcd_U> > & q){
    q=p;
    gen tmp;
    vector< T_unsigned<gen,hashgcd_U> >::iterator jt=q.begin(),jtend=q.end();
    for (;jt!=jtend;++jt){
      if (!fracmod(jt->g,modulo,tmp))
	return false;
      jt->g=tmp;
    }
    return true;
  }
  
  // should be called from gausspol.cc in gcdheu after monomial packing like in modpoly.cc gcd_modular
  bool gcd_ext(const vector< T_unsigned<gen,hashgcd_U> > & p_orig,const vector< T_unsigned<gen,hashgcd_U> > & q_orig,vector< T_unsigned<gen,hashgcd_U> > & d, vector< T_unsigned<gen,hashgcd_U> > & pcofactor, vector< T_unsigned<gen,hashgcd_U> > & qcofactor,const std::vector<hashgcd_U> & vars, bool compute_cofactors,int nthreads){
#if defined( RTOS_THREADX) || defined(BESTA_OS) || defined(EMCC)
    return false;
#else
    // find extension
    compute_cofactors=false;
    gen coefft;
    int tp=is_integer(p_orig,coefft),tq=is_integer(q_orig,coefft);
    if (!tp || !tq)
      return false;
    if (tp<3 && tq<3)
      return false;
    if (tp==3 && tq==2)
      tp=4;
    if (tq==3 && tp==2)
      tp=4;
    if (tq>tp)
      tp=tq;
    index_t shift_vars;
    if (!find_shift(vars,shift_vars))
      return false;
    coefft=*(coefft._EXTptr+1);
    if (coefft.type!=_VECT || coefft._VECTptr->empty())
      return false;
    vecteur & pminv=*coefft._VECTptr;
    gen pmin0=pminv.front();
    gen m=536871000,pimod=1;
    gen lcoeffp=p_orig.front().g,lcoeffq=q_orig.front().g;
    d.clear();
    pcofactor.clear();
    qcofactor.clear();
    vector< T_unsigned<vecteur,hashgcd_U> > p,q,g,pcof,qcof;
    index_t pdeg,qdeg,pdegmod,qdegmod,gdegmod,gdegmod2,gdeg;
    degree(p_orig,shift_vars,pdeg);
    degree(q_orig,shift_vars,qdeg);
    gdeg=min(pdeg,qdeg);
    if (tp==4){
      // return false;
      vector< T_unsigned<vecteur,hashgcd_U> > p1,q1,g1,pcof1,qcof1,p2,q2,g2,pcof2,qcof2;
      // tp==4, use primes == 1 % 4, find gcds replacing i by both
      // sqrts of -1 modulo the primes, ichinrem 
      for (int n=0;;n++){
	m=nextprime(m+1);
	if (m.type!=_INT_)
	  return false;
	int modulo=m.val;
	// computing gcd in Z[i]: use a modulo =1[4], find gcd for both roots of -1 mod modulo
	if (modulo%4==3) 
	  continue;
	// min poly mod modulo, coeffs should not contain i==sqrt(-1)
	if (is_zero(smod(pmin0,modulo)))
	  continue;
	if (debug_infolevel>(int)shift_vars.size())
	  cerr << "Modular algebraic extension gcd " << modulo << endl;
	vecteur pmin;
	pmin.reserve(pminv.size());
	const_iterateur it=pminv.begin(),itend=pminv.end();
	for (;it!=itend;++it){
	  pmin.push_back(smod(*it,modulo));
	}
	// find square root of -1
	int i=modsqrtminus1(modulo);
	gen lp1=complex_smod_ext(lcoeffp,i,modulo),lp2=complex_smod_ext(lcoeffp,-i,modulo);
	gen lq1=complex_smod_ext(lcoeffq,i,modulo),lq2=complex_smod_ext(lcoeffq,-i,modulo);
	if (is_zero(lp1) || is_zero(lq1) || is_zero(lp2) || is_zero(lq2))
	  continue;
	if (!complex_smod_ext(p_orig,i,modulo,p1)) 
	  return false; // setsizeerr();
	degree(p1,shift_vars,pdegmod);
	if (pdegmod!=pdeg)
	  continue;
	if (!complex_smod_ext(q_orig,i,modulo,q1)) 
	  return false; // setsizeerr();
	degree(q1,shift_vars,qdegmod);
	if (qdegmod!=qdeg)
	  continue;
	if (!complex_smod_ext(p_orig,-i,modulo,p2)) 
	  return false; // setsizeerr();
	degree(p2,shift_vars,pdegmod);
	if (pdegmod!=pdeg)
	  continue;
	if (!complex_smod_ext(q_orig,-i,modulo,q2)) 
	  return false; // setsizeerr();
	degree(q2,shift_vars,qdegmod);
	if (qdegmod!=qdeg)
	  continue;
	int res=gcd_ext(p1,q1,vars,pmin,modulo,g1,pcof1,qcof1,compute_cofactors,compute_cofactors,nthreads);
	// ?FIXME? check that g1, pcof1, qcof1 are normalized
	if (res==-1)
	  return false;
	if (!res)
	  continue;
	res=gcd_ext(p2,q2,vars,pmin,modulo,g2,pcof2,qcof2,compute_cofactors,compute_cofactors,nthreads);
	if (res==-1)
	  return false;
	if (!res)
	  continue;
	// cerr << p1 << " " << q1 << " " << g1 << endl << p2 << " " << q2 << " " << g2 << endl ;
	// IMPROVE? use a map for modulo -> g1,g2,etc. 
	// and do chinese remaindering for poly having the same lcoeff
	degree(g1,shift_vars,gdegmod);
	degree(g2,shift_vars,gdegmod2);
	if (gdegmod2!=gdegmod)
	  continue;
	if (gdegmod.front()>gdeg.front()) // unlucky prime
	  continue;
	int cmp=compare(gdegmod,gdeg);
	if (cmp==-1){
	  // full restart
	  d.clear();
	  pcofactor.clear();
	  qcofactor.clear();
	  pimod=1;
	  continue;
	}
	if (cmp!=-2){
	  // restart with this gcd
	  gdeg=gdegmod;
	  d.clear();
	  pcofactor.clear();
	  qcofactor.clear();
	  pimod=1;
	}
	complex_ichinrem_ext(g1,g2,i,modulo,d,pimod);
	if (compute_cofactors){
	  complex_ichinrem_ext(pcof1,pcof2,i,modulo,pcofactor,pimod);
	  complex_ichinrem_ext(qcof1,qcof2,i,modulo,qcofactor,pimod);
	}
	pimod = modulo * pimod;
	// rational reconstruction and division test
	vector< T_unsigned<gen,hashgcd_U> > dtest,pcofactortest,qcofactortest,pquo,qquo,rem;
	fracmod(d,pimod,dtest);
	lcmdeno(dtest);
	gen tmp;
	ppz(dtest,tmp,true);
	make_ext(dtest,coefft); // make extensions
	if (compute_cofactors){
	  fracmod(pcofactor,pimod,pcofactortest);
	  fracmod(qcofactor,pimod,qcofactortest);
	  lcmdeno(pcofactortest);
	  ppz(pcofactortest,tmp,true);
	  lcmdeno(qcofactortest);
	  ppz(qcofactortest,tmp,true);
	  // lcoeff(p)*dtest*pcofactortest =? p*lcoeff(dtest)*lcoeff(pcofactortest)
	  // this is true mod pimod, check sizes
	  // If AxB=C mod m,Pmin(theta)
	  // then A*B-C=mD mod Pmin(theta)
	  // The size of coeffs of A*B-C is <= 
	  // <= |A|*|B|* min(size(A),size(B)) * (degpmin+1) +|C|
	  // but division by Pmin(theta) may enlarge the remainder
	  // by a multiplication by ((degpmin+1)*|Pmin|)^(degpmin-1)
	  // remove denominators and integer content
	  gen lcoeffdpcof=dtest.front().g*pcofactortest.front().g;
	  simplify(lcoeffp,lcoeffdpcof);
	  gen lcoeffdqcof=dtest.front().g*qcofactortest.front().g;
	  simplify(lcoeffq,lcoeffdqcof);
	  gen maxp=max(p_orig,context0),maxq=max(q_orig,context0),maxpcof=max(pcofactortest,context0),maxqcof=max(qcofactortest,context0),maxd=max(dtest,context0);
	  gen maxpmin=_max(coefft,context0),degpmin=int(pminv.size()-1);
	  if (is_undef(maxpmin)) return false;
	  gen multpmin=pow((degpmin+1)*maxpmin,degpmin,context0);
	  gen test1=lcoeffdpcof*maxp+lcoeffp*maxd*maxpcof*gen(std::min(dtest.size(),pcofactortest.size()))*(degpmin+1)*multpmin; 
	  gen test2=lcoeffdqcof*maxq+lcoeffq*maxd*maxqcof*gen(std::min(dtest.size(),qcofactortest.size()))*(degpmin+1)*multpmin; 
	  if (is_strictly_greater(pimod,test1,context0) && is_strictly_greater(pimod,test2,context0)){
	    // leave d unchanged but adjust pcofactor and qcofactor
	    swap(d,dtest);
	    smallmult(lcoeffp/lcoeffdpcof,pcofactortest,pcofactor);
	    smallmult(lcoeffq/lcoeffdqcof,qcofactortest,qcofactor);
	    return true;
	  }
	}
	if (hashdivrem(p_orig,dtest,pquo,rem,vars,0 /* reduce */,0/*qmax*/,true)==1 && rem.empty()){
	  if (hashdivrem(q_orig,dtest,qquo,rem,vars,0 /* reduce */,0/*qmax*/,true)==1 && rem.empty()){
	    // FIXME normalize d
	    swap(pcofactor,pquo);
	    swap(qcofactor,qquo);
	    swap(d,dtest);
	    return true;
	  }
	}
      }
    }
    // begin loop on modulo
    vector< T_unsigned<gen,hashgcd_U> > dtestold;
    for (int n=0;;n++){
      m=nextprime(m+1);
      if (m.type!=_INT_)
	return false;
      int modulo=m.val;
      if (debug_infolevel>(int)shift_vars.size())
	cerr << "Modular algebraic extension gcd " << modulo << endl;
      // min poly mod modulo
      if (is_zero(smod(pmin0,modulo)))
	continue;
      vecteur pmin;
      pmin.reserve(pminv.size());
      const_iterateur it=pminv.begin(),itend=pminv.end();
      for (;it!=itend;++it){
	pmin.push_back(smod(*it,modulo));
      }
      // reduce mod m
      if (!smod_ext(p_orig,m.val,p) || !smod_ext(q_orig,m.val,q))
	return false;
      // check degrees of p and q mod modulo
      degree(p,shift_vars,pdegmod);
      if (pdegmod!=pdeg)
	continue;
      degree(q,shift_vars,qdegmod);
      if (qdegmod!=qdeg)
	continue;
      // if one lcoeff of p/q is not invertible mod modulo
      // then it won't be when variables of p or q are evaluated
      // hence checking in dim 1 is sufficient
      int res=gcd_ext(p,q,vars,pmin,modulo,g,pcof,qcof,compute_cofactors,compute_cofactors,nthreads);
      if (res==-1)
	return false;
      if (!res)
	continue;
      // ?FIXME? check that g is normalized to have lcoeff==1
      // IMPROVE? use a map for modulo -> lcoeff -> g, pcof, qcof, pimodulo 
      // and chinese remainder with modulos having the same lcoeff
      degree(g,shift_vars,gdegmod);
      if (gdegmod.front()>gdeg.front()) // unlucky prime
	continue;
      // Check with respect to other variables
      // N.B.: it could be faster to store all g,pcof,qcof
      // to avoid a full restart each time an unlucky content is reached
      // Here we must have sufficiently successive primes
      // that do not have unlucky content
      int cmp=compare(gdegmod,gdeg);
      if (cmp==-1){
	// full restart
	d.clear();
	pcofactor.clear();
	qcofactor.clear();
	pimod=1;
	continue;
      }
      if (cmp!=-2){
	// restart with this gcd
	gdeg=gdegmod;
	d.clear();
	pcofactor.clear();
	qcofactor.clear();
	pimod=1;
      }
      // Same degrees
      // chinese remainder of g and d
      ichinrem_ext(g,modulo,d,pimod);
      if (compute_cofactors){
	ichinrem_ext(pcof,modulo,pcofactor,pimod);
	ichinrem_ext(qcof,modulo,qcofactor,pimod);
      }
      pimod = modulo * pimod;
      // rational reconstruction and division test
      vector< T_unsigned<gen,hashgcd_U> > dtest,pcofactortest,qcofactortest,pquo,qquo,rem;
      fracmod(d,pimod,dtest);
      lcmdeno(dtest);
      gen tmp;
      ppz(dtest,tmp,true);
      make_ext(dtest,coefft); // make extensions
      if (compute_cofactors){
	fracmod(pcofactor,pimod,pcofactortest);
	fracmod(qcofactor,pimod,qcofactortest);
	lcmdeno(pcofactortest);
	ppz(pcofactortest,tmp,true);
	lcmdeno(qcofactortest);
	ppz(qcofactortest,tmp,true);
	// lcoeff(p)*dtest*pcofactortest =? p*lcoeff(dtest)*lcoeff(pcofactortest)
	// this is true mod pimod, check sizes
	// If AxB=C mod m,Pmin(theta)
	// then A*B-C=mD mod Pmin(theta)
	// The size of coeffs of A*B-C is <= 
	// <= |A|*|B|* min(size(A),size(B)) * (degpmin+1) +|C|
	// but division by Pmin(theta) may enlarge the remainder
	// by a multiplication by ((degpmin+1)*|Pmin|)^(degpmin-1)
	// remove denominators and integer content
	gen lcoeffdpcof=dtest.front().g*pcofactortest.front().g;
	simplify(lcoeffp,lcoeffdpcof);
	gen lcoeffdqcof=dtest.front().g*qcofactortest.front().g;
	simplify(lcoeffq,lcoeffdqcof);
	gen maxp=max(p_orig,context0),maxq=max(q_orig,context0),maxpcof=max(pcofactortest,context0),maxqcof=max(qcofactortest,context0),maxd=max(dtest,context0);
	gen maxpmin=_max(coefft,context0),degpmin=int(pminv.size()-1);
	if (is_undef(maxpmin)) return false;
	gen multpmin=pow((degpmin+1)*maxpmin,degpmin,context0);
	gen test1=lcoeffdpcof*maxp+lcoeffp*maxd*maxpcof*gen(std::min(dtest.size(),pcofactortest.size()))*(degpmin+1)*multpmin; 
	gen test2=lcoeffdqcof*maxq+lcoeffq*maxd*maxqcof*gen(std::min(dtest.size(),qcofactortest.size()))*(degpmin+1)*multpmin; 
	if (is_strictly_greater(pimod,test1,context0) && is_strictly_greater(pimod,test2,context0)){
	  // leave d unchanged but adjust pcofactor and qcofactor
	  swap(d,dtest);
	  smallmult(lcoeffp/lcoeffdpcof,pcofactortest,pcofactor);
	  smallmult(lcoeffq/lcoeffdqcof,qcofactortest,qcofactor);
	  return true;
	}
      }
      // This division might take a very long time if not successfull
      // Make it only when we are sure (might be improved)
      if (dtest==dtestold && hashdivrem(p_orig,dtest,pquo,rem,vars,0 /* reduce */,0/*qmax*/,true)==1 && rem.empty()){
	if (hashdivrem(q_orig,dtest,qquo,rem,vars,0 /* reduce */,0/*qmax*/,true)==1 && rem.empty()){
	  swap(pcofactor,pquo);
	  swap(qcofactor,qquo);
	  swap(d,dtest);
	  return true;
	}
      }
      dtestold=dtest;
    }
#endif // RTOS_THREADX
  }
  
#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC

