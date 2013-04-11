/* -*- mode:C++ ; compile-command: "g++-3.4 -I.. -g -c pari.cc" -*- */
#include "giacPCH.h"

/*  PARI interface
 *  Copyright (C) 2001,7 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#include <stdexcept>
#include "gen.h" 
#include "identificateur.h"
#include "sym2poly.h"
#include "plot.h"
#include "prog.h"
#include "usual.h"
#include "input_lexer.h"
#include "modpoly.h"
#include "giacintl.h"
#ifdef USE_GMP_REPLACEMENTS
#undef HAVE_LIBPARI
#endif

#ifdef HAVE_LIBPARI
#ifdef HAVE_PTHREAD_H
#include <pthread.h>
#endif

static long int abs(long int & l){
  if (l<0)
    return -l;
  else
    return l;
}
#include "pari.h"
extern "C" {
#include <pari/pari.h>
#include <pari/paripriv.h>
  extern void *PARI_stack_limit;
  extern entree functions_basic[];
}
#if PARI_VERSION_CODE<PARI_VERSION(2,4,0) // 132096
#define PARI23
#else
jmp_buf env; 
static void
gp_err_recover(long numerr)
{
  longjmp(env, numerr);
}

#endif
#include <cstdlib>

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

#ifdef HAVE_LIBPTHREAD
  static pthread_mutex_t * pari_mutex_ptr = 0;
#endif

  static map<string,entree *> pari_function_table;
  static void do_giac_pari_init(long maxprime){
    long pari_mem_size=10000000;
    if (getenv("PARI_SIZE")){
      string pari_size_s(getenv("PARI_SIZE"));
      pari_mem_size= atoi(pari_size_s.c_str());
    }
    // do not initialize INIT_JMP so that PARI error do not exit
    pari_init_opts(pari_mem_size,maxprime,INIT_SIGm | INIT_DFTm);
    entree * ptr=functions_basic;
    for (;ptr->name;++ptr){
      pari_function_table[ptr->name]=ptr;
    }
  }

  struct giac_pari_init {
    giac_pari_init(long maxprime) { 
      do_giac_pari_init(maxprime);
    }
  };
  static long pari_maxprime=100000;
  static giac_pari_init bidon(pari_maxprime);

  static gen GEN2gen(const GEN & G,const vecteur & vars);

  static gen pow2sizeof_long(pow(256,sizeof(long)));
  // Conversion of a GEN integer to a gen, using Horner method
  static gen t_INT2gen(const GEN & G){
    long Gs=signe(G);
    if (!Gs)
      return 0;
    //setsigne(G,1);
    long Gpl=lgefint(G)-2;
    // use one of the following code depending how pari codes long integers
    // Code 1
#if !defined(__APPLE__) && !defined(WIN32)
    ref_mpz_t * mz = new ref_mpz_t;
    mpz_realloc2(mz->z,32*Gpl);
    mpz_import(mz->z,Gpl,-1,sizeof(GEN),0,0,&G[2]);
    //setsigne(G,Gs);
    if (Gs>0) 
      return mz;
    else
      return -gen(mz);
#else
    // Code 2
    --Gpl;
    long * Gp=int_MSW(G);
    gen res;
    for (int i=0;i<=Gpl;++i){
#ifdef INT128
      res=res*pow2sizeof_long+int128_t((ulonglong)*Gp);
#else
      res=res*pow2sizeof_long+longlong(unsigned(*Gp));
#endif
      Gp=int_precW(Gp);
    }
    return Gs<0?-res:res;
#endif
  }

  static gen t_REAL2gen(const GEN & G){
    long Gs=signe(G);
    if (!Gs)
      return 0.0;
    long n=lg(G);
    gen res;
    for (int i=2;i<n;++i){
#ifdef __x86_64__ // FIXME make a gen constructor from ulonglong
      unsigned long u=G[i];
      res=res*pow2sizeof_long;
      if (u%2)
	res += 1;
      res += 2*gen(longlong(u>>1));
#else
      res=res*pow2sizeof_long+longlong(unsigned(G[i]));
#endif
    }
    res=res*evalf(pow(plus_two,int(expo(G)+1-bit_accuracy(n))),1,0);
    return Gs<0?-res:res;
  }

  static gen t_POL2gen(const GEN & G,const vecteur & vars){
    if (!signe(G))
      return 0;
    long n=lg(G);
    vecteur res;
    for (long i=2;i<n;++i){
      res.push_back(GEN2gen((GEN)G[i],vars));
    }
    reverse(res.begin(),res.end());
    long vn=varn(G);
    gen x;
    if (vn<long(vars.size())){
      x=vars[vn];
      return symb_horner(res,x);
    }
    if (!vars.empty() && vn==long(vars.size())){
      x=vars[vn-1];
      return symb_horner(res,x);
    }
    else
      return gen(res,_POLY1__VECT);
  }

  static gen t_VEC2gen(const GEN & G,const vecteur & vars,long debut,long fin){
    vecteur res;
    for (long i=debut;i<fin;++i){
      res.push_back(GEN2gen((GEN)G[i],vars));
    }
    return gen(res,typ(G)==t_VEC?0:99);
  }

  static gen t_VECSMALL2gen(const GEN & G){
    long fin=lg(G);
    vecteur res;
    for (long i=1;i<fin;++i){
      res.push_back((int)G[i]);
    }
    return gen(res,98);
  }

  static gen t_MOD2gen(const GEN & G,const vecteur & vars){
    return makemod(GEN2gen((GEN) G[2],vars),GEN2gen((GEN) G[1],vars));
  }

  static gen t_POLMOD2gen(const GEN & G,const vecteur & vars){
    gen tmp;
    find_or_make_symbol("Mod",tmp,0,false,context0);
    return symbolic(at_of,makesequence(tmp,gen(makevecteur(GEN2gen((GEN) G[2],vars),GEN2gen((GEN) G[1],vars)),_SEQ__VECT)));
  }

  static gen t_COMPLEX2gen(const GEN & G,const vecteur & vars){
    return gen(GEN2gen((GEN) G[1],vars),GEN2gen((GEN) G[2],vars));
  }

  static gen t_FRAC2gen(const GEN & G,const vecteur & vars){
    return fraction(GEN2gen((GEN)G[1],vars),GEN2gen((GEN)G[2],vars));
  }

  static gen t_QUAD2gen(const GEN & G,const vecteur & vars){
    // use w__IDNT_e like pari for all quadratics
    return GEN2gen((GEN) G[1],vars)+w__IDNT_e*GEN2gen((GEN)G[2],vars);
  }

  static gen t_PADIC2gen(const GEN & G,const vecteur & vars){
    gen O;
    find_or_make_symbol("O",O,0,false,context0);
    gen p(GEN2gen((GEN) G[2],vars)),val(longlong(valp(G)));
    return pow(p,val,context0)*(GEN2gen((GEN) G[4],vars)+symbolic(at_of,makesequence(O,symb_pow(p,longlong(precp(G)))))); // removed symb_quote for p-adic
  }

  // WARNING: If g is a matrix this print the transpose of the matrix
  static string GEN2string(const GEN & g){
    // cerr << typ(g) << " " << t_MAT << endl;
    char * ch;
    string s;
    if ((typ(g)==t_MAT) || (typ(g)==t_COL)){
      int taille=lg(g);
      s +="[";
      for (int i=1;i<taille;++i){
	s += GEN2string((long *)g[i]);
	if (i==taille-1)
	  s+="]";
	else
	  s+=",";
      }
      return s;
    }
    ch=GENtostr(g);
    s=ch;
#ifndef HAVE_LIBGC
    free(ch);
#endif
    return s;
  }

  static gen default2gen(const GEN &G){
    string s=GEN2string(G);
    gen g;
    try {
      g=gen(s,context0);
    } catch(...){
      return string2gen(s,false);
    }
    return g;
  }

  static gen GEN2gen(const GEN & G,const vecteur & vars){
    switch (typ(G)){
    case t_INT:
      return t_INT2gen(G);
    case t_INTMOD: 
      return t_MOD2gen(G,vars);
    case t_POLMOD:
      return t_POLMOD2gen(G,vars);
    case t_FRAC: case t_RFRAC:
      return t_FRAC2gen(G,vars);
    case t_COMPLEX:
      return t_COMPLEX2gen(G,vars);
    case t_REAL:
      return t_REAL2gen(G);
    case t_POL:
      return t_POL2gen(G,vars);
    case t_VEC: case t_COL:
      return t_VEC2gen(G,vars,1,lg(G));
    case t_VECSMALL: 
      return t_VECSMALL2gen(G);
    case t_MAT:
      return _tran(t_VEC2gen(G,vars,1,lg(G)),context0);
    case t_LIST:
#ifdef PARI23
      return t_VEC2gen(G,vars,2,lgeflist(G));
#else
      return t_VEC2gen(G,vars,2,list_nmax(G));
#endif
    case t_STR:
      return string2gen(GSTR(G),false);
    case t_QUAD:
      return t_QUAD2gen(G,vars);
    case t_PADIC:
      return t_PADIC2gen(G,vars);
    default:
      return default2gen(G);
    }
  }

  static std::string pariprint(const gen & e,int varnum,GIAC_CONTEXT);

  static string pariprint_VECT(const vecteur & v,int varnum,int subtype,GIAC_CONTEXT){
    string s;
    const_iterateur it=v.begin(),itend=v.end();
    if (subtype==_POLY1__VECT){
      if (v.empty())
	return "0";
      string tmp=")*x"+print_INT_(varnum)+"+";
      ++varnum;
      s=pariprint(*it,varnum,contextptr);
      for (++it;it!=itend;++it){
	s="("+s+tmp+pariprint(*it,varnum,contextptr);
      }
      --varnum;
      return s;
    }
    if (subtype!=_SEQ__VECT)
      s="[";
    for (;it!=itend;++it){
      s += pariprint(*it,varnum,contextptr);
      if (it+1!=itend)
	s += ",";      
    }
    if (subtype!=_SEQ__VECT)
      s+="]";
    return s;
  }

  static string pariprintmatrice(const gen & e,int varnum,GIAC_CONTEXT){
    string res = "[";
    const_iterateur it=e._VECTptr->begin(),itend=e._VECTptr->end();
    for (;it!=itend;++it){
      res += pariprint_VECT(*it->_VECTptr,varnum,_SEQ__VECT,contextptr);
      if (it+1!=itend)
	res += ";";
      else
	res +="]";
    }
    return res;
  }

  static string pariprint(const gen & e,int varnum,GIAC_CONTEXT){
    int save_maple_mode=xcas_mode(contextptr);
    xcas_mode(contextptr)=1;
    string res;
    switch (e.type){
    case _INT_:
      res=print_INT_(e.val);
      break;
    case _ZINT:
      res=e.print(contextptr);
      break;
    case _CPLX:
      res=e._CPLXptr->print(contextptr)+"+I*"+(e._CPLXptr+1)->print(contextptr);
      break;
    case _VECT:
      if (ckmatrix(e))
	res=pariprintmatrice(e,varnum,contextptr);
      else
	res=pariprint_VECT(*e._VECTptr,varnum,e.subtype,contextptr);
      break;
    case _SYMB:
      if (e._SYMBptr->sommet.ptr()->printsommet) // FIXME
	res=e.print(contextptr);
      else
	res=string(e._SYMBptr->sommet.ptr()->s)+"("+pariprint(e._SYMBptr->feuille,varnum,contextptr)+")";
      break;
    case _MOD:
      res= "Mod("+pariprint(*e._MODptr,varnum,contextptr)+","+pariprint(*(e._MODptr+1),varnum,contextptr)+")";
      break;
    case _FRAC:
      res= pariprint(*e._FRACptr,varnum,contextptr)+"/("+pariprint(*(e._FRACptr+1),varnum,contextptr)+")";
      break;
    default: // _SYMB with printsommetasoperator, _DOUBLE_, _REAL, _IDNT
      res=e.print(contextptr);
    }
    xcas_mode(contextptr)=save_maple_mode;
    return res;
  }
  
  static GEN zint2GEN(const gen & g){
    mpz_t * zz=g._ZINTptr;
    int sgn=mpz_sgn(*zz);
    if (!sgn)
      return utoi(0);
    int count=mpz_sizeinbase(*zz,2);
    if (count % (8*sizeof(GEN))) 
      count=count/(8*sizeof(GEN))+3;
    else
      count=count/(8*sizeof(GEN))+2;
    GEN G=cgetg(count,t_INT);
    //size_t countp;
    // mpz_export(&G[2],&countp,-1,sizeof(GEN),0,0,*zz);
    setlgefint(G,count);
    setsigne(G,sgn);
    // return G;
    gen q(abs(g)),tmp,r;
    GEN Gstep(int2n(16));
    vector<int> v;
    for (;!is_zero(q);){
      r=irem(q,gen(1<<16),tmp);
      v.push_back(r.val);
      q=tmp;
    }
    int s=v.size();
    GEN res(utoi(0));
    for (int i=s-1;i>=0;--i){
      res=gmul(res,Gstep);
      res=gadd(res,utoi(v[i]));
    }
    return res;
  }

  static GEN ingen2GEN(const gen & e,const vecteur & vars,GIAC_CONTEXT);

  static GEN vect2GEN(const gen & g,const vecteur & vars,GIAC_CONTEXT){
    vecteur v (*g._VECTptr);
    int n=v.size(),decal=1;
    GEN res;
    if (g.subtype==_POLY1__VECT){
      decal=2;
      res=cgetg(n+decal,t_POL);
      reverse(v.begin(),v.end());
    }
    else {
      res=cgetg(n+decal,g.subtype==99?t_COL:(g.subtype==98?t_VECSMALL:t_VEC));
    }
    for (int i=0;i<n;++i)
      gel(res,i+decal)=ingen2GEN(v[i],vars,contextptr);
    if (decal==2){
      setsigne(res,1);
      setvarn(res,0);
    }
    return res;
  }

  static GEN mat2GEN(const gen & g,const vecteur & vars,GIAC_CONTEXT){
    matrice M = mtran(*g._VECTptr);
    int n=M.size(),m=M[0]._VECTptr->size();
    GEN res=cgetg(n+1,t_MAT);
    for (int i=1;i<=n;++i){
      GEN resi=gel(res,i)=cgetg(m+1,t_COL);
      vecteur & v = *M[i-1]._VECTptr;
      for (int j=1;j<=m;++j){
	gel(resi,j)=ingen2GEN(v[j-1],vars,contextptr);
      }
    }
    return res;
  }

  static GEN cplx2GEN(const gen & g,const vecteur & vars,GIAC_CONTEXT){
    GEN res=cgetg(3,t_COMPLEX);
    gel(res,1)=ingen2GEN(*g._CPLXptr,vars,contextptr);
    gel(res,2)=ingen2GEN(*(g._CPLXptr+1),vars,contextptr);
    return res;
  }

  static GEN frac2GEN(const gen & g,const vecteur & vars,GIAC_CONTEXT){
    GEN res=cgetg(3,t_FRAC);
    gel(res,1)=ingen2GEN(g._FRACptr->num,vars,contextptr);
    gel(res,2)=ingen2GEN(g._FRACptr->den,vars,contextptr);
    return res;
  }

  static GEN ingen2GEN(const gen & e,const vecteur & vars,GIAC_CONTEXT){
    switch (e.type){
    case _INT_:
      return stoi(e.val);
    case _ZINT:
      return zint2GEN(e);
    case _CPLX:
      return cplx2GEN(e,vars,contextptr);
    case _FRAC:
      return frac2GEN(e,vars,contextptr);
    case _VECT:
      if (ckmatrix(e))
	return mat2GEN(e,vars,contextptr);
      else
	return vect2GEN(e,vars,contextptr);
    }
    // add vars to e
    string s=pariprint(e,0,contextptr);
    if (!vars.empty())
      s="["+s+","+(vars.size()==1?vars.front().print():print_VECT(vars,_SEQ__VECT,contextptr))+"]";
    GEN res= flisexpr((char *) s.c_str());
    return vars.empty()?res:gel(res,1);
  }
  static GEN gen2GEN(const gen & e,const vecteur & vars,GIAC_CONTEXT){
#ifdef PARI23
    if (setjmp(GP_DATA->env)){
      setsizeerr(gettext("Error in PARI subsystem"));
    }
#else
    cb_pari_err_recover=gp_err_recover;
    if (setjmp(env)){ 
      setsizeerr(gettext("Error in PARI subsystem"));
    }
#endif
    return ingen2GEN(e,vars,contextptr);
  }

  static void pari_cleanup(void * arg) {
    if (arg)
      pthread_mutex_unlock((pthread_mutex_t *)arg);
  }

  int check_pari_mutex(){
    if (!pari_mutex_ptr){
      pthread_mutex_t tmp=PTHREAD_MUTEX_INITIALIZER;
      pari_mutex_ptr=new pthread_mutex_t(tmp);
    }
    return pthread_mutex_trylock(pari_mutex_ptr);
  }

  void abort_if_locked(){
    if (check_pari_mutex())
      setsizeerr(gettext("PARI locked by another thread. Try again later.\nIf PARI is locked by a cancelled thread, you can unlock it with pari_unlock()"));
  }

  gen pari_isprime(const gen & e,int certif){
    gen tmp;
    abort_if_locked();
    pthread_cleanup_push(pari_cleanup, (void *) pari_mutex_ptr);
    long av=avma;
    tmp=GEN2gen(gisprime(gen2GEN(e,vecteur(0),0),certif),vecteur(0));
    avma=av;
    if (pari_mutex_ptr) pthread_mutex_unlock(pari_mutex_ptr);    
    pthread_cleanup_pop(0);
    return tmp;
  }

  string pari_ifactor(const gen & e){
    abort_if_locked();
    string s;
    pthread_cleanup_push(pari_cleanup, (void *) pari_mutex_ptr);
    long av=avma;
    GEN g=gen2GEN(e,vecteur(0),0);
    GEN gf=factorint(g,0);
    s=GEN2string(gf);
    avma=av;
    if (pari_mutex_ptr) pthread_mutex_unlock(pari_mutex_ptr);    
    pthread_cleanup_pop(0);
    return s;
  }

  gen pari_gamma(const gen & e){
    abort_if_locked();
    gen res;
    pthread_cleanup_push(pari_cleanup, (void *) pari_mutex_ptr);
    long av=avma;
    GEN g=gen2GEN(e,vecteur(0),0);
    GEN gf=ggamma(g,precision(g));
    res=GEN2gen(gf,vecteur(0));
    avma=av;
    if (pari_mutex_ptr) pthread_mutex_unlock(pari_mutex_ptr);    
    pthread_cleanup_pop(0);
    return res;
  }

  gen pari_zeta(const gen & e){
    abort_if_locked();
    gen res;
    pthread_cleanup_push(pari_cleanup, (void *) pari_mutex_ptr);
    long av=avma;
    GEN g=gen2GEN(e,vecteur(0),0);
    GEN gf=gzeta(g,precision(g));
    res=GEN2gen(gf,vecteur(0));
    avma=av;
    if (pari_mutex_ptr) pthread_mutex_unlock(pari_mutex_ptr);    
    pthread_cleanup_pop(0);
    return res;
  }

  gen pari_psi(const gen & e){
    abort_if_locked();
    gen res;
    pthread_cleanup_push(pari_cleanup, (void *) pari_mutex_ptr);
    long av=avma;
    GEN g=gen2GEN(e,vecteur(0),0);
    GEN gf=gpsi(g,precision(g));
    res=GEN2gen(gf,vecteur(0));
    avma=av;
    if (pari_mutex_ptr) pthread_mutex_unlock(pari_mutex_ptr);    
    pthread_cleanup_pop(0);
    return res;
  }

  gen pari_ffinit(const gen & p,int n){
    gen tmp;
    abort_if_locked();
    pthread_cleanup_push(pari_cleanup, (void *) pari_mutex_ptr);
    long av=avma;
    tmp=GEN2gen(ffinit(gen2GEN(p,vecteur(0),0),n,0),vecteur(0));
    avma=av;
    if (pari_mutex_ptr) pthread_mutex_unlock(pari_mutex_ptr);    
    pthread_cleanup_pop(0);
    return tmp;
  }

  // for factorization over Z when many modular factors arise
  // This is a call to PARI combine_factors
  // WARNING: You must remove static from the declaration of combine_factors
  // in pari/src/basemath/polarith2.c
  // GEN combine_factors(GEN a, GEN famod, GEN p, long klim, long hint);
  bool pari_lift_combine(const vecteur & a,const vector<vecteur> & factmod,gen & modulo,vector<vecteur> & res){
#ifdef PARI23
    long av=avma;
    GEN pari_a=gen2GEN(r2e(a,x__IDNT_e,context0),vecteur(0),0);
    string s("[");
    vector<vecteur>::const_iterator it=factmod.begin(),itend=factmod.end();
    for (;it!=itend;){
      s += r2e(*it,x__IDNT_e,context0).print();
      ++it;
      if (it==itend)
	break;
      s+=",";
    }
    s+="]";
    // cerr << s << endl;
    GEN pari_factmod=flisexpr((char *) s.c_str());
    GEN pari_modulo=gen2GEN(modulo,vecteur(0),0);
    GEN pari_res=combine_factors(pari_a,pari_factmod,pari_modulo,0,1);
    // back conversion
    string res_s=GENtostr(pari_res);
    gen res_v(res_s.substr(0,res_s.size()-1),context0);
    if (res_v.type!=_VECT)
      setsizeerr();
    const_iterateur jt=res_v._VECTptr->begin(),jtend=res_v._VECTptr->end();
    for (;jt!=jtend;++jt){
      res.push_back(*e2r(*jt,x__IDNT_e,context0)._VECTptr);
    }
    avma=av;
    return true;
#else
    return false;
#endif
  }

  static gen pari_exec(const string & s,GIAC_CONTEXT){
    long av=avma;
    void * save_pari_stack_limit = PARI_stack_limit;
    PARI_stack_limit=0; // required since the stack changed
#ifdef PARI23
    if (setjmp(GP_DATA->env))
#else
    cb_pari_err_recover=gp_err_recover;
    if (setjmp(env))
#endif
      { 
	if (pari_mutex_ptr) pthread_mutex_unlock(pari_mutex_ptr);    
	avma = av;
	*logptr(contextptr) << gettext("Error in PARI subsystem") << endl;
	PARI_stack_limit = save_pari_stack_limit ;
	// setsizeerr();
	return undef;
      } 
    GEN gres= flisexpr((char *) s.c_str());
    gen res=GEN2gen(gres,vecteur(0));
    avma=av;
    PARI_stack_limit = save_pari_stack_limit ;
    return res;
  }

#include "input_parser.h"
#define _ARGS_ argvec[0], argvec[1], argvec[2], argvec[3],\
               argvec[4], argvec[5], argvec[6], argvec[7], argvec[8]
  // args=pari_function_name, arg1, ...
  // or pari_function_name quoted to define a function

  enum {
    RET_GEN=0,
    RET_VOID=1,
    RET_INT=2,
    RET_LONG=3
  };
  typedef GEN (*PFGEN)(ANYARG);

  extern const unary_function_ptr * const  at_pari;
  static gen in_pari(const gen & args,GIAC_CONTEXT){
    if (args.type<_IDNT)
      return args;
    vecteur v(gen2vecteur(args));
    int vs=v.size();
    if (!vs){ // export all pari functions
      entree * ptr=functions_basic;
      gen tmp; int lextype;
      string redef;
      for (;ptr->name;++ptr){
	pari_function_table[ptr->name]=ptr;
	lextype=find_or_make_symbol(ptr->name,tmp,0,false,contextptr);
	if (lextype==T_SYMBOL)
	  sto(symbolic(at_pari,string2gen(ptr->name,false)),tmp,contextptr);
	else
	  redef += string(ptr->name) + " ";
	find_or_make_symbol(string("pari_")+ptr->name,tmp,0,false,contextptr);
	sto(symbolic(at_pari,string2gen(ptr->name,false)),tmp,contextptr);
      }
      return string2gen("All PARI functions are now defined with the pari_ prefix.\nPARI functions are also defined without prefix except:\n"+redef+"\nWhen working with p-adic numbers use them in a pari() call\nType ?pari for short help\nInside xcas, try Help->Manuals->PARI for HTML help",false);
    }
    if (v[0].is_symb_of_sommet(at_quote)){
      if (vs==1)
	return symbolic(at_pari,args);
      v[0]=v[0]._SYMBptr->feuille;
    }
    for (int i=1;i<vs;i++)
      v[i]=v[i].eval(eval_level(contextptr),contextptr);
    vecteur vars(1,identificateur("O"));
    lidnt(v,vars);
    vars.erase(vars.begin());
    bool parse_all=false;
    long av=avma;
#ifdef PARI23
    if (setjmp(GP_DATA->env)){ 
      avma = av;
      parse_all=true;
      if (setjmp(GP_DATA->env)){ // if (setjmp(GP_DATA->env)){
	if (pari_mutex_ptr) pthread_mutex_unlock(pari_mutex_ptr);    
	avma = av;
	*logptr(contextptr) << gettext("Error in PARI subsystem") << endl;
	// setsizeerr();
	return undef;
      } 
    }
#else
    cb_pari_err_recover=gp_err_recover;
    if (setjmp(env)){ 
      avma = av;
      parse_all=true;
      if (setjmp(env)){ // if (setjmp(GP_DATA->env)){
	if (pari_mutex_ptr) pthread_mutex_unlock(pari_mutex_ptr);    
	avma = av;
	*logptr(contextptr) << gettext("Error in PARI subsystem") << endl;
	// setsizeerr();
	return undef;
      } 
    }
#endif
    if (!parse_all && v[0].type==_STRNG) {
      string vstr=*v[0]._STRNGptr;
      if (vstr!="") {
	void * save_pari_stack_limit = PARI_stack_limit;
	PARI_stack_limit=0; // required since the stack changed
	if (vs==1)
	  return symbolic(at_pari,args);
	map<string,entree *>::const_iterator i = pari_function_table.find(vstr);
	// look at function prototype for return value
	// and call code, from anal.c line around 1990
	unsigned int ret;
	if (i!=pari_function_table.end()){
	  const char * s =i->second->code;
	  if      (*s <  'a')   ret = RET_GEN;
	  else if (*s == 'v') { ret = RET_VOID; s++; }
	  else if (*s == 'i') { ret = RET_INT;  s++; }
	  else if (*s == 'l') { ret = RET_LONG; s++; }
	  else                  ret = RET_GEN;
	  void * call= i->second->value;
	  // translate gen to GEN's 
	  GEN argvec[9]={0,0,0,0,0,0,0,0,0},res; long m;
	  int k=0;
	  for (int j=1;k<9 && *s && *s!='\n';++s){
	    switch(*s){
	    case 'L': // long
	      if (j==vs) setsizeerr();
	      argvec[k]= (GEN) v[j].val;
	      ++j; ++k;
	      break;
	    case 'P': // default precision
	      argvec[k] = (GEN) precdl; k++; break;
	    case 'D': //default param
	      {
		++s;
		switch(*s){
		case 'G': case '&': case 'I': case 'V': 
		  if (j<vs)
		    argvec[k]=ingen2GEN(v[j],vars,contextptr);
#ifdef PARI23
		  else
		    argvec[k]=utoi(0);
#endif
		  ++j; ++k; 
		  break;
		case 'n':
		  if (j<vs){
		    int pos=equalposcomp(vars,v[j]);
		    if (pos)
		      argvec[k]=(long int*)(pos -1);
		  }
		  else
		    argvec[k]=0;
		  ++j; ++k; 
		  break;
		default:
		  if (j<vs)
		    argvec[k]=(long int*) v[j].val;
		  else
		    argvec[k]=0;
		  ++k; ++j;
		  while (*s!= ',') s++;
		  s++;
		  while (*s!= ',') s++;
		}
		break;
	      }
	    case 'p':
	      argvec[k]=(GEN) precreal; 
	      ++k; 
	      break;
	    default:
	      if (j==vs) setsizeerr();
	      argvec[k]=ingen2GEN(v[j],vars,contextptr);
	      ++j; ++k;
	      break;
	    } 
	  }
	  switch (ret)
	    {
	    case RET_GEN:
	      res = ((PFGEN)call)(_ARGS_);
	      break;
	      
	    case RET_INT:
	      m = (long)((int (*)(ANYARG))call)(_ARGS_);
	      res = stoi(m); break;
	      
	    case RET_LONG:
	      m = ((long (*)(ANYARG))call)(_ARGS_);
	      res = stoi(m); break;
	      
	    case RET_VOID:
	      ((void (*)(ANYARG))call)(_ARGS_);
	      res = gnil; break;
	    }	  
	  // cerr << GEN2string(res) << endl;
	  gen resg(GEN2gen(res,vars));
	  PARI_stack_limit = save_pari_stack_limit ;
	  avma=av;
	  return resg;
	} // end if (i!=pari_function_table.end())
      } // end if vstr!=""
      if (vstr=="" && vs==2){
	long av=avma;
	gen res= GEN2gen(gen2GEN(v[1],vars,contextptr),vars);
	avma=av;
	return res;
      }
    } // end if (!parse_all ...)
    string s;
    if (v[0].type==_FUNC)
      s=v[0]._FUNCptr->ptr()->s;
    else
      s=gen2string(v[0]);
    if (vs>1){
      s+="(";
      for (int i=1;i<vs;){
	s += pariprint(v[i],0,contextptr);
	++i;
	if (i==vs)
	  break;
	s += ",";
      }
      s +=")";
    }
    return pari_exec(s,contextptr);
  }
  gen _pari(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    abort_if_locked();
    gen res;
    pthread_cleanup_push(pari_cleanup, (void *) pari_mutex_ptr);
    res=in_pari(args,contextptr);
    if (pari_mutex_ptr) pthread_mutex_unlock(pari_mutex_ptr);    
    pthread_cleanup_pop(0);
    return res;
  }
  static const char _pari_s []="pari";
  static define_unary_function_eval (__pari,&giac::_pari,_pari_s);
  define_unary_function_ptr5( at_pari ,alias_at_pari,&__pari,_QUOTE_ARGUMENTS,true);

  gen _pari_unlock(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    int locked=check_pari_mutex();
    if (!locked)
      return 0;
    delete pari_mutex_ptr;
    pari_mutex_ptr = 0;
    return 1;
  }
  static const char _pari_unlock_s []="pari_unlock";
  static define_unary_function_eval (__pari_unlock,&giac::_pari_unlock,_pari_unlock_s);
  define_unary_function_ptr5( at_pari_unlock ,alias_at_pari_unlock,&__pari_unlock,_QUOTE_ARGUMENTS,true);

  static std::string cutstring(const std::string & s,int ncol){
    string res;
    if (ncol<20)
      ncol=20;
    int left=s.size(),pos=0,j;
    for (;left>ncol;pos+=j,left-=j){
      for (j=ncol;j>ncol/2;--j){
	if (s[pos+j]==' ')
	  break;
      }
      res=res+s.substr(pos,j)+'\n';
    }
    return res+s.substr(pos,pos+left);
  }

  // Help for g calling PARI online help
  std::string pari_help(const gen & g){
    if (is_zero(g))
      return "Run pari() to export PARI functions.\n?pari(1) to ?pari(11) lists PARI functions by section\n?pari_functionname shows a short help on a function\nInside Xcas, Help->Manual->PARI-GP shows HTML help";
    string res;
    if (g.type==_INT_){
      int section=g.val;
      entree * ptr=functions_basic;
      for (;ptr->name;++ptr){
	if (ptr->menu==section){
	  res += ptr->name;
	  res += " ";
	}
      }
      return cutstring(res,70);
    }
    string gs;
    if (g.type==_FUNC)
      gs=g._FUNCptr->ptr()->s;
    else
      gs=gen2string(g);
    if (gs.size()>5 && gs.substr(0,5)=="pari_")
      gs=gs.substr(5,gs.size()-5);
    entree * ptr=functions_basic;
    for (;ptr->name;++ptr){
      if (ptr->name==gs){
	res = ptr->help;
	return cutstring(res,70);
      }
    }
    return "PARI function not found\nHelp syntax: ?pari(1),...,?pari(12) or ?pari_functionname";
  }

#ifndef NO_NAMESPACE_GIAC
}
#endif // ndef NO_NAMESPACE_GIAC

#else
// ! HAVE_LIBPARI
#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  static gen pari_error(){
    return undeferr(gettext("Not implemented, please recompile giac with PARI"));
  }

  gen pari_isprime(const gen & e,int certif){
    return string2gen("please recompile giac with PARI",false);
  }
  std::string pari_ifactor(const gen & e){
    return "please recompile giac with PARI";
  }
  
  gen pari_ffinit(const gen & p,int n){
    return string2gen("please recompile giac with PARI",false);
  }

  gen pari_gamma(const gen & e){
    return pari_error();
  }
  gen pari_zeta(const gen & e){
    return pari_error();
  }
  gen pari_psi(const gen & e){
    return pari_error();
  }
  
  bool pari_lift_combine(const vecteur& a, const std::vector<vecteur>& factmod,
			 gen& modulo, std::vector<vecteur>& res){
    vecteur tmp(1,pari_error());
    res=std::vector<vecteur>(1,tmp);
    return false;
  }

  gen _pari(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    return pari_error();
  }
  
  std::string pari_help(const gen & g){
    return "please recompile giac with PARI";
  }

  static const char _pari_s []="pari";
  static define_unary_function_eval (__pari,&giac::_pari,_pari_s);
  define_unary_function_ptr5( at_pari ,alias_at_pari,&__pari,_QUOTE_ARGUMENTS,true);

  static const char _pari_unlock_s []="pari_unlock";
  static define_unary_function_eval (__pari_unlock,&giac::_pari,_pari_unlock_s);
  define_unary_function_ptr5( at_pari_unlock ,alias_at_pari_unlock,&__pari_unlock,_QUOTE_ARGUMENTS,true);

#ifndef NO_NAMESPACE_GIAC
}
#endif // ndef NO_NAMESPACE_GIAC

#endif // HAVE_LIBPARI
