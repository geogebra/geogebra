/* -*- mode:C++ ; compile-command: "g++-3.4 -I.. -g -c pari.cc" -*- */
#ifndef _GIAC_PARI_H
#define _GIAC_PARI_H
#include "first.h"
#include <string>

typedef long * GEN;

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  class gen;
  // print e as a PARI parsable string, varnum is the # of the var for poly1
  // std::string pariprint(const gen & e,int varnum,GIAC_CONTEXT);

  // Convert a giac gen to pari GEN. This modifies pari pointers
  // GEN gen2GEN(const gen & e,const vecteur & vars,GIAC_CONTEXT);
  // Convert a pari GEN to a giac gen, pari pointer are not modified
  // gen GEN2gen(const GEN & G,const vecteur & vars);

  gen pari_isprime(const gen & e,int certif=0);
  // FIXME for pari 2.2 use 1 instead of 2, 2 is for APRCL test

  gen pari_ffinit(const gen & p,int n); // irreducible polynomial of deg n over F_p

  std::string pari_ifactor(const gen & e);
  gen pari_gamma(const gen & e);
  gen pari_zeta(const gen & e);
  gen pari_psi(const gen & e);
  bool pari_lift_combine(const vecteur & a,const std::vector<vecteur> & factmod,gen & modulo,std::vector<vecteur> & res);
  gen _pari(const gen & args,GIAC_CONTEXT);
  std::string pari_help(const gen & g);
  bool pari_polroots(const vecteur & p,vecteur & res,long l,GIAC_CONTEXT);
  gen GEN2gen(const GEN & G,const vecteur & vars);
  GEN gen2GEN(const gen & e,const vecteur & vars,GIAC_CONTEXT);
  bool pari_polresultant(const gen & p,const gen & q,const vecteur & lv,gen & res,GIAC_CONTEXT);
  bool pari_nffactor(const gen & p,const gen & pmin,const vecteur & lv,gen & res,GIAC_CONTEXT);
  bool pari_galoisconj(const gen & g,vecteur & w,GIAC_CONTEXT);
  bool pari_allocatemem(size_t mem,GIAC_CONTEXT);

#ifndef NO_NAMESPACE_GIAC
}
#endif // ndef NO_NAMESPACE_GIAC
 
#endif //_GIAC_PARI_H
