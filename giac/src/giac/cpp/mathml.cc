// -*- mode:C++ ; compile-command: "g++ -I.. -g -c mathml.cc  -DIN_GIAC -DHAVE_CONFIG_H" -*-

#include "giacPCH.h"

#include "gen.h"
#include "symbolic.h"
#include "usual.h"
#include "intg.h"
#include "derive.h"
#include "series.h"
#include "plot.h"
#include "tex.h"
#include "mathml.h"
#include "giacintl.h"

//#include <fcntl.h>
#include <cstdlib>
#ifndef NSPIRE
#include <cstdio>
#if defined VISUALC13 && !defined BESTA_OS
#undef clock
#undef clock_t
#endif
#include <iomanip>
#endif
#include <math.h>

#ifdef HAVE_SSTREAM
#include <sstream>
#else
#ifndef NSPIRE
#include <strstream>
#endif
#endif
using namespace std;

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

#if defined GIAC_HAS_STO_38 || defined NSPIRE
  gen _mathml_error(const gen & g,GIAC_CONTEXT){
    return gensizeerr(gettext("No mathml support"));
  }
  static const char _mathml_s []="mathml";
  static define_unary_function_eval (__mathml,&_mathml_error,_mathml_s);
  define_unary_function_ptr5( at_mathml ,alias_at_mathml,&__mathml,0,true);

  static const char _spread2mathml_s []="spread2mathml";
  static define_unary_function_eval (__spread2mathml,&_mathml_error,_spread2mathml_s);
  define_unary_function_ptr5( at_spread2mathml ,alias_at_spread2mathml,&__spread2mathml,0,true);

#else //RTOS_THREADX

#ifndef NO_NAMESPACE_GIAC
  using namespace giac;
#endif // ndef NO_NAMESPACE_GIAC

  const char mathml_preamble[]="<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1 plus MathML 2.0//EN\"\n\"http://www.w3.org/TR/MathML2/dtd/xhtml-math11-f.dtd\" [\n<!ENTITY mathml \"http://www.w3.org/1998/Math/MathML\">\n]>\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<body>\n";
  const char mathml_end[]="\n</body>\n</html>";
  const char math_begin[]="<math mode=\"display\" xmlns=\"http://www.w3.org/1998/Math/MathML\">";
  const char math_end[]="</math>";
  const char provisoire_mbox_begin[]="<mi>";
  const char provisoire_mbox_end[]="</mi>";
  const char svg_end[]="</svg>";
  const double svg_epaisseur1=200; // thickness=(xmax-xmin)/svg_epaisseur1

  struct svg_attribut {
    double ysurx;
    int color;
    int width;
    int epaisseur_point;
    int type_line;
    int type_point;
    bool fill_polygon;
    bool hidden_name;
    bool ie; // Internet explorer flag for SVG
  };

  // 2 fonctions de service
  static void elimine_inf_sup(string &s){
    string t="";
    while (t!=s){
      t=s;
      int i=s.find("<"), j=s.find(">");
      if (i!=-1)
	s.replace(s.find("<"),1,"&lt;");
      if (j!=-1)
	s.replace(s.find(">"),1,"&gt;");
    }
  }
 
  static void delete_cr(string & s){
    for (unsigned int i=0 ; i<s.length(); i++)
      if (s[i]=='\n') 
	s[i]=' ';
  }


   string string2mathml(const string & m){
     string s=m;
#if 0
     size_t ms=m.size();
     for (size_t i=0;i<ms;++i){
       if (m[i]=='\\')
	 s+="\\\\";
       else
	 s+=m[i];
     }
#endif
     string t="";
     string mat[]={"&","<",">","\n"};
     string rep[]={"&amp;","&lt;","&gt;","</mi></mtd></mtr><mtr><mtd><mi>"};
     //start with & before adding new ones
     for(int siz=0;siz<sizeof(mat)/sizeof(string);siz++){
       int c=0,k=-1,le=s.length();
       while (c<le){
         k=s.find(mat[siz],c);
         if (k!=-1){
	   s.replace(k,1,rep[siz]);c=k+rep[siz].length()-1;le=le+rep[siz].length()-1;
         }
         else{
	   c=le;
         }
       }
     }
     return "<mtable columnalign=\"left\"><mtr><mtd><mi>"+s+"</mi></mtd></mtr></mtable>";
   }

  string spread2mathml(const matrice & m,int formule,GIAC_CONTEXT){
    int l=m.size();
    if (!l)
      return "<p>empty_spread</p>";
    int c=m.front()._VECTptr->size();
    string percent="", percent_first="";
    if (c<=50){
      percent="width=\""+print_INT_(200/(2*c+1))+"%\"";
      percent_first="width=\""+print_INT_(100/(2*c+1))+"%\""; 
    }
    string s="<table border=\"1\"><tbody>";
    string tmp;
    for (int k=0;;){ // write first line
      int i=k;
      for(int j=0;;++j){
	tmp+="<td bgcolor=\"lightGray\" "+percent+">";
	tmp+=char('A'+i%26-(j!=0));
	tmp+="</td>";
	//	tmp="<td>"+char('A'+i%26-(j!=0))+"</td>"+tmp;
	i=i/26;
	if (!i)
	  break;
      } // end for j
      ++k;
      if (k==c)
	break;
    } // end for k
    s += "<tr align=\"center\"><td bgcolor=\"lightGray\" "+percent_first+"> &nbsp; &nbsp; &nbsp;</td>"
      +tmp+"</tr>";
    for (int i=0;i<l;++i){
      s +="<tr align=\"center\"><td  bgcolor=\"lightGray\">"+ print_INT_(i)+"</td>";
      for (int j=0;j<c;++j){ 
	if (formule)
	  s += "<td>"+string(math_begin)+gen2mathml(m[i][j][formule],contextptr)+math_end+"</td>" ;
	else {
	  int save_r=printcell_current_row(contextptr),save_c=printcell_current_col(contextptr);
	  printcell_current_row(contextptr)=i,printcell_current_col(contextptr)=j;
	  string t(m[i][j][0].print(contextptr)),tt("<td>");
	  int ll=t.size();
	  for (int l=0;l<ll;++l){
	    if (t[l]=='<')
	      tt +=  "&lt;";
	    else  if (t[l]=='>')
	      tt += "&gt;";
	    else  if (t[l]=='&')
	      tt += "&amp;";
	    else
	      tt += t[l];
	  } // end for l
	  s += tt+"</td>";
	  printcell_current_row(contextptr)=save_r;printcell_current_col(contextptr)=save_c;
	} // end if (formule)
      } // end for j
      s +="</tr>";
    } // end for i
    s += "</tbody></table>";
    return s;
  }   

  string matrix2mathml(const matrice & m,GIAC_CONTEXT){
    int l=m.size();
    if (!l)
      return string("()");
    int c=m.front()._VECTptr->size();
    string s("<mrow><mo>(</mo><mtable>");
    for (int i=0;i<l;++i){
      s+="<mtr>"; 
      for (int j=0;j<c;++j){
	s += "<mtd>"+gen2mathml(m[i][j],contextptr)+"</mtd>" ;
      }
      s+= "</mtr>"; 
    }
    s += "</mtable><mo>)</mo></mrow>";
    return s;
  }

  static string _SPOL12mathml(const sparse_poly1 & p, string &svg,GIAC_CONTEXT){
    return gen2mathml(spol12gen(p,contextptr),svg,contextptr);
  }

  static string _VECT2mathml(const vecteur & v, unsigned char type,string &svg,GIAC_CONTEXT){
#if 1
    string s(begin_VECT_string(type,false,contextptr));
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;){
      s += gen2mathml(*it,svg,contextptr);  
      ++it;
      if (it!=itend)
	s += "<mo>,</mo>";
    }
    s+=end_VECT_string(type,false,contextptr);
    return s;
#else
    string s("<mfenced open=\"");
    if (type==_SEQ__VECT) s.append("(\" close=\")\">");
    else {
      if (type==_SET__VECT) s.append("{\" close=\"}\">");
      else{
	if (type==_POLY1__VECT) s.append("poly1[\" close=\"]\">");
        else s.append("[\" close=\"]\">");
      }
    }
    s.append("<mrow>");
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;){
      s += gen2mathml(*it,svg,contextptr);  
      ++it;
      if (it!=itend)
	s += "<mo>,</mo>";
    }
    s +="</mrow></mfenced>";
    return s;
#endif
  }

  static bool needs_times(gen g,GIAC_CONTEXT){
    //F.H: sqrt(2) est constant mais pas besoin de x.
    //if (eval(g,eval_level(contextptr),contextptr).is_constant())
    gen gg=eval(g,eval_level(contextptr),contextptr);
    if( (gg.type==_INT_)||(gg.type==_ZINT) || (gg.type==_REAL) || (gg.type==_DOUBLE_) )
      return true;
    /* F.H: a quoi cela sert il de mettre un x dans tous ces cas?
       else {
       
	 if (g.type==_SYMB) {
	symbolic symb = *(g._SYMBptr);
	if ( !strcmp(symb.sommet.ptr()->print(contextptr),"/") )
	  return true;
      }
      }*/
    return false;
  }

  static string prod_vect2mathml(const vecteur & v,GIAC_CONTEXT){
    bool isprecINT = false;
    if (v.empty())
      return "<mn>1</mn>";
    vecteur::const_iterator it=v.begin(),itend=v.end();
    string s;
    for (;;){
      if ( (it->type==_SYMB) && ( (it->_SYMBptr->sommet==at_plus) || (it->_SYMBptr->sommet==at_neg))) 
	s += string(" <mo>(</mo>")+gen2mathml(*it,contextptr)+string(" <mo>)</mo>");
      else if (it->type==_CPLX){
	if  (!is_zero(re(*it,contextptr)) && !is_zero(im(*it,contextptr)))
	  s += "<mo>(</mo>"+gen2mathml(*it,contextptr)+"<mo>)</mo>";
	else{

	 s += gen2mathml(*it,contextptr);
	}
      }
      else{
	if ( isprecINT && (it->type==_SYMB) &&  (it->_SYMBptr->sommet==at_pow) ) {
	    symbolic mys = *(it->_SYMBptr);
	    if(!((mys.feuille._VECTptr->back()==plus_one_half))){
	      if ((mys.feuille._VECTptr->front().type==_INT_)||(mys.feuille._VECTptr->front().type==_ZINT)){
	        if  (is_positive(mys.feuille._VECTptr->front(),contextptr))
	          s += "<mo>&times;</mo>";// F.H: 2*2^n et 22^n, 2*2^(1/7)  mais pas 2*sqrt(2). 
	      }
	    } 
	}
	s += gen2mathml(*it,contextptr);
	
      }

      if(  (it->type==_INT_)||(it->type==_ZINT) || (it->type==_REAL) || (it->type==_DOUBLE_) ){
	isprecINT=true;
      }
      else{
	isprecINT=false;
      }

      ++it;
      if (it==itend)
	return s;
      else if ((needs_times(*it,contextptr))&& isprecINT)
	s += "<mo>&times;</mo>";
    }
  }
  
  static string prod_vect2mathml_no_bra(const vecteur & v,GIAC_CONTEXT){
    if (v.empty())
      return "<mn>1</mn>";
    vecteur::const_iterator it=v.begin(),itend=v.end();
    bool isprecINT = false;
    if (v.size()==1)
      return gen2mathml(*it,contextptr);
    string s;
    for (;;){
      if ( (it->type==_SYMB) && ( (it->_SYMBptr->sommet==at_plus) || (it->_SYMBptr->sommet==at_neg)))
	s += string(" <mo>(</mo>")+gen2mathml(*it,contextptr)+string(" <mo>)</mo>");
      else if (it->type==_CPLX){
	if  (!is_zero(re(*it,contextptr)) && !is_zero(im(*it,contextptr)))
	  s += "<mo>(</mo>"+gen2mathml(*it,contextptr)+"<mo>)</mo>";
	else
	  s += gen2mathml(*it,contextptr);
      }
      else if ( isprecINT && (it->type==_SYMB) &&  (it->_SYMBptr->sommet==at_pow)  ) {
	  symbolic mys = *(it->_SYMBptr);
	   if(!((mys.feuille._VECTptr->back()==plus_one_half))){
	     if ((mys.feuille._VECTptr->front().type==_INT_)||(mys.feuille._VECTptr->front().type==_ZINT)){
	       if  (is_positive(mys.feuille._VECTptr->front(),contextptr))
		 s += "<mo>&times;</mo>";//F.H 2*2^n  ou 2*2^(1/7)  != 22^(1/3)
	     } 
	   }

	 s += gen2mathml(*it,contextptr);
      }
      else 
	s += gen2mathml(*it,contextptr);

      if(  (it->type==_INT_)||(it->type==_ZINT) || (it->type==_REAL) || (it->type==_DOUBLE_) ){
	isprecINT=true;
      }
      else{
	isprecINT=false;
      }
      ++it;
      if (it==itend)
	return s;
      else if ((needs_times(*it,contextptr)) && isprecINT)
	s +="<mo>&times;</mo>";
    }
  }


  // // --------------------------------- recopi?? de unary.cc ---------------------------------------

  static string mathml_printsommetasoperator(const gen & feuille, const string & sommetstr,GIAC_CONTEXT){
    if (feuille.type!=_VECT)
      return "<mi>"+feuille.print(contextptr)+"</mi>"; 
    vecteur::const_iterator itb=feuille._VECTptr->begin(),itend=feuille._VECTptr->end();
    if (itb==itend)
      return "";
    string s;
    if (itb->type==_FRAC)
      s=gen2mathml(*itb,contextptr); 
    else {
      if ( (itb->type==_IDNT) || ((itb->type!=_SYMB) && is_positive(*itb,contextptr)) || sommetstr=="<mo>=</mo>")
	s=gen2mathml(*itb,contextptr);
      else
	s="<mo>(</mo>"+gen2mathml(*itb,contextptr)+"<mo>)</mo>";
    }
    ++itb;
    for (;;){
      if (itb==itend)
	return s;
      if ((itb->type!=_SYMB) || sommetstr=="<mo>=</mo>")
	s += sommetstr + gen2mathml(*itb,contextptr);
      else
	s += sommetstr + "<mo>(</mo>"+gen2mathml(*itb,contextptr)+"<mo>)</mo>";
      ++itb;
    }
  }
  // ------------------------------- fin provenant de unary.cc -------------------------------------



  // // ------------------------------- provenant de usual.cc ----------------------------------
  static string mathml_printasexp(const gen & g,GIAC_CONTEXT){
    return "<msup><mi>e</mi><mrow>"+gen2mathml(g,contextptr)+"</mrow></msup>";
  }

  static string mathml_printassqrt(const gen & g,GIAC_CONTEXT){
    return   "<msqrt>"+gen2mathml(g,contextptr)+"</msqrt>";
  }

#if 0
  //-------------- inutile ? -------------------------
  static string mathml_printassqr(const gen & g,GIAC_CONTEXT){
    return "<msup><mrow>"+gen2mathml(g,contextptr)+"</mrow><mn>2</mn></msup>";
  }
#endif

  static string mathml_printasre(const gen & g,GIAC_CONTEXT){
    return "<mi>Re</mi><mrow><mo>(</mo>"+gen2mathml(g,contextptr)+"<mo>)</mo></mrow>";   
  }

  static string mathml_printasim(const gen & g,GIAC_CONTEXT){
    return "<mi>Im</mi><mrow><mo>(</mo>"+gen2mathml(g,contextptr)+"<mo>)</mo></mrow>";   
  }

  // overline en mathml ????????????
  // string texprintasconj(const gen & g,const char & s){
  //   return "\\overline{"+gen2tex []=g+"}";
  // }



  static string mathml_printassto(const gen & g,const string & sommetstr,GIAC_CONTEXT){
    if ( (g.type!=_VECT) || (g._VECTptr->size()!=2) )
      return sommetstr+"<mo>(</mo>"+gen2mathml(g,contextptr)+"<mo>)</mo>";
    string s(gen2mathml(g._VECTptr->back(),contextptr)+"<mi>:=</mi>");
    if (g._VECTptr->front().type==_SEQ__VECT)
      return s+"<mo>(</mo>"+gen2mathml(g._VECTptr->front(),contextptr)+"<mo>)</mo>";
    else
      return s+gen2mathml(g._VECTptr->front(),contextptr);
  }



  // --------------------------- fin de provenant de usual.cc ----------------------------------

  // n'existe pas avec tex :
  static string mathml_equal2arrow(gen &g,GIAC_CONTEXT){
    if (!is_equal(g))
      return gen2mathml(g,contextptr);
    vecteur v=*g._SYMBptr->feuille._VECTptr;
    return "<mrow>"+gen2mathml(v[0],contextptr)+"<mo>&rarr;</mo>"+gen2mathml(v[1],contextptr)+"</mrow>";
  
  }

  // ----------------------------- provenant de series.cc ----------------------------------
  static string mathml_printaslimit(const gen & g,GIAC_CONTEXT){
    string s("<mo>lim</mo>");
    if (g.type!=_VECT)
      return s+gen2mathml(g,contextptr);
    vecteur v(*g._VECTptr);
    int l(v.size());
    if (!l)
      return s;
    if (l==1)
      return s+gen2mathml(v[0],contextptr);
    if (l==2)
      return "<munder>"+s+mathml_equal2arrow(v[1],contextptr)+"</munder>"+gen2mathml(eval(v[0],eval_level(contextptr),contextptr),contextptr);
    // directional limit
    if (l==3){
      if (is_one(v[2]))
	return "<munder>"+s+"<mrow><msup>"+mathml_equal2arrow(v[1],contextptr)+"<mo>+</mo></msup></mrow></munder>"
	  +gen2mathml(eval(v[0],eval_level(contextptr),contextptr),contextptr);
      if (is_zero(eval(v[2]+1,eval_level(contextptr),contextptr))) 
	return "<munder>"+s+"<mrow><msup>"+mathml_equal2arrow(v[1],contextptr)+"<mo>-</mo></msup></mrow></munder>"
	  +gen2mathml(eval(v[0],eval_level(contextptr),contextptr),contextptr);
      else  
	return "<munder>"+s+mathml_equal2arrow(v[1],contextptr)+"</munder>"+gen2mathml(eval(v[0],eval_level(contextptr),contextptr),contextptr);
    }
    return s;
  }
  // ----------------------------- fin provenant de series.cc ----------------------------------

  // ----------------------------- provenant de intg.cc ----------------------
  static string mathml_printasintegrate(const gen g,GIAC_CONTEXT){
    string s("<mo>&int;</mo>");
    if (g.type!=_VECT)
      return s+gen2mathml(g,contextptr);
    vecteur v(*g._VECTptr);
    int l(v.size());
    if (!l)
      return s;
    if (l==1)
      return s+gen2mathml(v.front(),contextptr);
    if (l==2)
      return s+gen2mathml(v.front(),contextptr)+"<mi>d</mi>"+gen2mathml(v.back(),contextptr);
    if (l==4)
      return "<msubsup>"+s+"<mi>"+v[2].print(contextptr)+"</mi>"
	+"<mi>"+v[3].print(contextptr)+"</mi></msubsup>"+gen2mathml(eval(v.front(),eval_level(contextptr),contextptr),contextptr)
	+"<mi>d</mi><mi>"+v[1].print(contextptr)+"</mi>";
    return s;
  } 
  // ---------------------------- fin provenant de intg.cc ----------------------



  // --------------------------- provenant de derive ----------------------------

  static string mathml_printasderive(const gen & feuille,GIAC_CONTEXT){
    if (feuille.type!=_VECT || feuille._VECTptr->size()<2){
      if (feuille.type==_IDNT || feuille.type<=_CPLX || feuille.is_symb_of_sommet(at_derive))
	return gen2mathml(feuille,contextptr)+"'";
      else
	return "<msup><mrow><mo>(</mo>"+gen2mathml(feuille,contextptr)+"<mo>)</mo></mrow><mi>'</mi></msup>";
    }
    vecteur & v = *feuille._VECTptr;
    bool needpar=v[0].type==_SYMB;
    if (v.size()>2)
      return "<mfrac><mrow><msup><mo>&part;</mo><mrow>"+gen2mathml(v[2],contextptr)+"</mrow></msup>"+(needpar?"<mo>(</mo>":"")+gen2mathml(v[0],contextptr)
	+(needpar?"<mo>)</mo>":"")+"</mrow><mrow><mo>&part;</mo><msup>"+gen2mathml(v[1],contextptr)+"<mrow>"+gen2mathml(v[2],contextptr)+"</mrow></msup></mrow></mfrac>";
    return string("<mfrac><mrow><mo>&part;</mo>")+(needpar?"<mo>(</mo>":"")+gen2mathml(v[0],contextptr)
      +(needpar?"<mo>)</mo>":"")+"</mrow><mrow><mo>&part;</mo>"+gen2mathml(v[1],contextptr)+"</mrow></mfrac>";
  }
  // ----------------------- fin provenant de derive ---------------------------

  static string mathml_print(const symbolic & mys,GIAC_CONTEXT){


    unary_function_ptr u =mys.sommet;
    if (u==at_equal || u==at_equal2)
      return mathml_printsommetasoperator(mys.feuille,"<mo>=</mo>",contextptr);  
    if (u==at_different)
      return mathml_printsommetasoperator(mys.feuille,"<mo>≠</mo>",contextptr);
    if (u==at_inferieur_egal)
      return mathml_printsommetasoperator(mys.feuille,"<mo>&le;</mo>",contextptr);
    if (u==at_superieur_egal)
      return mathml_printsommetasoperator(mys.feuille,"<mo>&ge;</mo>",contextptr);  
    if (u==at_inferieur_strict)
      return mathml_printsommetasoperator(mys.feuille,"<mo>&lt;</mo>",contextptr);  
    if (u==at_superieur_strict)
      return mathml_printsommetasoperator(mys.feuille,"<mo>&gt;</mo>",contextptr);
    if (u==at_re)
      return mathml_printasre(mys.feuille,contextptr);
    if (u==at_im)
      return mathml_printasim(mys.feuille,contextptr);
    if (u==at_exp)
      return  mathml_printasexp(mys.feuille,contextptr);
    if (u==at_sqrt)
      return mathml_printassqrt(mys.feuille,contextptr);
    if (u==at_integrate)
      return mathml_printasintegrate(mys.feuille,contextptr);
    if (u==at_derive) 
      return mathml_printasderive(mys.feuille,contextptr);
    if (u==at_sto) 
      return mathml_printassto(mys.feuille, "<mi>:=</mi>",contextptr);
    if (u==at_limit) 
      return mathml_printaslimit(mys.feuille,contextptr);
    string s=mys.print(contextptr);
    elimine_inf_sup(s);
    delete_cr(s);
    return "<mtext>"+s+"</mtext>";
  }
     
  static string mathml_printassum(const gen e,GIAC_CONTEXT){
    vecteur v = *e._SYMBptr->feuille._VECTptr;
    if (v.size()!=4)
      return "<mtext>"+e.print(contextptr)+"</mtext>";
    return "<munderover><mi>&Sigma;</mi><mrow>"+gen2mathml(v[1],contextptr)+"<mo>=</mo>"+gen2mathml(v[2],contextptr)+"</mrow><mrow>"+gen2mathml(v[3],contextptr)+"</mrow></munderover>"+gen2mathml(eval(v[0],eval_level(contextptr),contextptr),contextptr);
  }

  static string mathml_printasabs(const gen e,GIAC_CONTEXT){
    return  "<mo>&VerticalBar;</mo>"+gen2mathml(e._SYMBptr->feuille,contextptr)+ "<mo>&VerticalBar;</mo>";
  }

  static string mathml_printasunit(const gen e,GIAC_CONTEXT){
    vecteur v = *e._SYMBptr->feuille._VECTptr;
    if (v.size()!=2)
      return "<mtext>"+e.print(contextptr)+"</mtext>";
    return  "<msub><mrow>"+gen2mathml(eval(v[0],eval_level(contextptr),contextptr),contextptr)+"</mrow><mrow>"+gen2mathml(v[1],contextptr)+"</mrow></msub>";
  }


  //---------------- Zone SVG  ---------------

  static void svg_dx_dy(double svg_width, double svg_height, double & dx, double & dy){
    // calibrage des graduations
    int p_h=(int) std::log10(svg_height);
    int p_w=(int) std::log10(svg_width);
    dx=std::pow(10.0,p_w-1);
    dy=std::pow(10.0,p_h-1);
    if (dx==0)
      dx=1;
    if (dy==0)
      dy=1;
    if (svg_width/dx>25){
      dx *= 5;
    }
    if (svg_height/dy>25){
      dy *=5;
    }
  }

  string svg_preamble(double svg_width_cm, double svg_height_cm,bool xml){
    return svg_preamble(svg_width_cm,svg_height_cm,gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,true,xml);
  }

  string svg_preamble(double svg_width_cm, double svg_height_cm,double xmin,double xmax,double ymin,double ymax,bool ortho,bool xml){
    double svg_width=xmax-xmin;
    double svg_height=ymax-ymin;
    double x_scale=(xmax-xmin)/10;
    double y_scale=(ymax-ymin)/10;
    int i;
    char grid_color[]="cyan";
    char grid_color2[]="blue";
    char buffer[16384];
    char * pos=buffer;

    if (ortho){
      if (svg_width<svg_height){
	svg_width_cm=svg_width_cm*svg_width/svg_height+1; 
	svg_height_cm=svg_height_cm+1;
      }  else {
	svg_height_cm=svg_height_cm*svg_height/svg_width+1;
	svg_width_cm=svg_width_cm+1;
      }
    }
    if (xml){
      sprintf(pos,"<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.2\" ");
      // sortie<<"<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>"<<endl;
      // sortie<<"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.0\" ";
    }
    else {
      sprintf(pos,"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.2\" baseProfile=\"tiny\"");
      // sortie << "<svg ";
    }
    pos=buffer+strlen(buffer);
    if (ortho){
      sprintf(pos,"preserveAspectRatio=\"xMidyMin meet\" width=\"%.5gcm\" height=\"%.5gcm\" viewBox=\"%.5g %.5g %.5g %.5g\" >\n<g transform=\"translate(0,%.5g) scale(1,-1)\">\n",svg_width_cm+2,svg_height_cm+1,xmin-x_scale,ymin-y_scale,svg_width+3*x_scale,svg_height+2*x_scale,svg_height+2*ymin);
      // sortie << "preserveAspectRatio=\"xMidyMin meet\"";
      // sortie << " width=\""<<svg_width_cm+2<<"cm\" height=\""<<svg_height_cm+1<<"cm\" viewBox=\""<<xmin-x_scale<<" "<<ymin-y_scale<<" "<<svg_width+3*x_scale<<" "<<svg_height+2*x_scale<<"\" >\n<g transform=\"translate(0,"<<svg_height+2*ymin<<") scale(1,-1)\">\n";
    }
    else {
      sprintf(pos,"width=\"%.5gcm\" height=\"%.5gcm\" preserveAspectRatio=\"none\" viewBox=\"%.5g %.5g %.5g %.5g\" >\n<g transform=\"translate(0,%.5g) scale(1,-1)\">\n",svg_width_cm*1.2,svg_height_cm*1.2,xmin-x_scale,ymin-y_scale,svg_width+2*x_scale,svg_height+2*y_scale,svg_height+2*ymin);
      // sortie << " width=\""<<svg_width_cm+2<<"cm\" height=\""<<svg_height_cm+1<<"cm\" viewBox=\""<<xmin-x_scale<<" "<<ymin-y_scale<<" "<< svg_width+2*x_scale<<" "<<svg_height+2*y_scale<<'"';
      // sortie << " preserveAspectRatio=\"none\" >" ;
      // sortie << "\n<g transform=\"translate(0,"<<svg_height+2*ymin<<") scale(1,-1)\">\n";
    }
    pos=buffer+strlen(buffer);
    // calibrage des graduations
    double  dx, dy;
    svg_dx_dy(svg_width, svg_height, dx, dy);
    double i_min_x= xmin/dx;
    double i_min_y= ymin/dy;
  
    //grille  
    double x,y;
    double xthickness((xmax-xmin)/svg_epaisseur1/3),ythickness((ymax-ymin)/svg_epaisseur1/3);
    // double thickness((xmax+ymax-xmin-ymin)/2000);
    
    for (i=(int) i_min_x; i*dx<xmax; i++){
      x=i*dx;
      sprintf(pos,"<line x1=\"%.5g\" y1=\"%.5g\" x2=\"%.5g\" y2=\"%.5g\"",x,ymin,x,ymax);
      pos=buffer+strlen(buffer);
      // sortie<<"<line x1=\""<<x <<"\" y1=\""<<ymin <<"\" x2=\""<<x <<"\" y2=\""<<ymax << "\" ";
      if(i%5==0){
	sprintf(pos,"stroke=\"%s\" stroke-width=\"%.5g\" />\n",grid_color2,2*xthickness);
	// sortie<<"stroke=\""<<grid_color2<<"\"  stroke-width=\""<<2*thickness<<"\" />"<<endl;
      }
      else {
	sprintf(pos,"stroke=\"%s\" stroke-width=\"%.5g\" />\n",grid_color,xthickness);
	// sortie<<"stroke=\""<<grid_color<<"\"  stroke-dasharray=\""<<10*thickness<<","<<10*thickness <<"\" stroke-width=\""<<thickness<<"\" />"<<endl;
      }
      pos=buffer+strlen(buffer);
    }
    for (i=(int) i_min_y; i*dy<ymax; i++){
      y=i*dy;
      sprintf(pos,"<line x1=\"%.5g\" y1=\"%.5g\" x2=\"%.5g\" y2=\"%.5g\"",xmin,y,xmax,y);
      pos=buffer+strlen(buffer);
      // sortie<<"<line x1=\""<<xmin <<"\" y1=\""<<y <<"\" x2=\""<<xmax <<"\" y2=\""<<y;
      if (i%5==0){
	sprintf(pos,"stroke=\"%s\" stroke-width=\"%.5g\" />\n",grid_color2,2*ythickness);	
	// sortie<<"\" stroke=\""<<grid_color2<<"\"  stroke-width=\""<<2*thickness<<"\" />"<<endl;
      }
      else {
	sprintf(pos,"stroke=\"%s\" stroke-width=\"%.5g\" />\n",grid_color,ythickness);
	// sortie<<"\" stroke=\""<<grid_color<<"\"  stroke-dasharray=\"" <<10*thickness<<","<<10*thickness <<"\" stroke-width=\""<<thickness<<"\" />"<<endl;
      }
      pos=buffer+strlen(buffer);
    }
    //cadre
    sprintf(pos,"<rect stroke=\"black\" stroke-width=\"%.5g\" fill=\"none\" x=\"%.5g\" y=\"%.5g\" width=\"%.5g\" height=\"%.5g\" />",2*std::min(xthickness,ythickness),xmin,ymin,svg_width,svg_height);
    // sortie<<"<rect stroke=\""<<"black"<<"\"  stroke-width=\""<<2*thickness<<"\" fill=\"none\" x=\""<<xmin<<"\" y=\""<<ymin<<"\" width=\""<<svg_width<<"\" height=\""<<svg_height<<"\" />"<<endl;  
    // string s=sortie.str();
    return buffer;
  }

  string svg_grid(){
    return svg_grid(gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax);
  }
  
  string svg_grid(double xmin,double xmax,double ymin,double ymax){
    double svg_width=xmax-xmin;
    double svg_height=ymax-ymin;
#ifdef HAVE_SSTREAM
    // ostringstream sortie;
#else
    // ostrstream sortie;
#endif

    // 4 rectangles to mask the borders
    double x_scale=(xmax-xmin)/10;
    double y_scale=(ymax-ymin)/10;
    double ratio=y_scale/x_scale;
    double fontscale=x_scale*.6; // (x_scale<y_scale?y_scale:x_scale)*.3;
    char buffer[16384];
    char * pos=buffer;
    sprintf(pos,"<rect x=\"%.5g\" y=\"%.5g\" height=\"%.5g\" width=\"%.5g\" stroke=\"none\" fill=\"white\"/\n>\n",xmin-x_scale,ymax,y_scale,svg_width+2*x_scale);
    // sortie<<"<rect x=\""<<xmin-x_scale<<"\" y=\""<<ymax<<"\" height=\""<<y_scale<<"\" width=\""<<svg_width+2*x_scale<< "\" stroke=\"none\" fill=\"white\"/>"<<endl;
    pos = buffer+strlen(buffer);
    sprintf(pos,"<rect x=\"%.5g\" y=\"%.5g\" height=\"%.5g\" width=\"%.5g\" stroke=\"none\" fill=\"white\"/\n>\n",xmin-x_scale,ymin,x_scale,svg_height);
    // sortie<<"<rect x=\""<<xmin-x_scale<<"\" y=\""<<ymin<<"\" width=\""<<x_scale <<"\" height=\""<<svg_height<< "\" stroke=\"none\" fill=\"white\"/>"<<endl;
    pos = buffer+strlen(buffer);
    sprintf(pos,"<rect x=\"%.5g\" y=\"%.5g\" height=\"%.5g\" width=\"%.5g\" stroke=\"none\" fill=\"white\"/\n>\n",xmax,ymin,x_scale*2,svg_height);
    // sortie<<"<rect x=\""<<xmax<<"\" y=\""<<ymin<<"\" width=\""<<x_scale*2 <<"\" height=\""<<svg_height<<"\" stroke=\"none\" fill=\"white\"/>"<<endl;
    pos = buffer+strlen(buffer);
    sprintf(pos,"<rect x=\"%.5g\" y=\"%.5g\" height=\"%.5g\" width=\"%.5g\" stroke=\"none\" fill=\"white\"/\n></g>\n",xmin-x_scale,ymin-y_scale,svg_width+2*x_scale,y_scale);
    pos = buffer+strlen(buffer);
    // sortie<<"<rect x=\""<<xmin-x_scale<<"\" y=\""<<ymin-y_scale<<"\" width=\""<<svg_width+2*x_scale <<"\" height=\""<<y_scale <<"\" stroke=\"none\" fill=\"white\"/>\n</g>"<<endl;

    // calibrage des graduations
    double dx,dy;
    svg_dx_dy(svg_width, svg_height, dx, dy);
    double x,y;
    int i;
    if ((xmax-xmin)/dx>9)
      dx*=5;
    if ((ymax-ymin)/dy>9)
      dy*=5;
    double i_min_x= xmin/dx;
    double i_min_y= ymin/dy;
    // index des graduations 
    for (i=(int) i_min_x;  i*dx<=xmax ; ++i){
      x=i*dx;
      sprintf(pos,"<text x=\"%.5g\" y=\"%.5g\" transform=\"scale(%.5g,%.5g)\" style=\"font-size:%.5gpt; text-anchor:middle;\">%.5g</text>\n",x/fontscale,(ymax+0.6*y_scale)/ratio/fontscale,fontscale,ratio*fontscale,1.0,x);
      pos = buffer+strlen(buffer);
      // sortie << setprecision(5)<<"<text x=\""<<x<<"\" y=\""<<ymax+0.6*y_scale<<"\" ";
      // sortie <<" style=\"font-size:"<<fontscale<<"pt; text-anchor:middle;\"";
      // sortie << ">" << (float)x <<"</text>"<<endl;
    } 
    for (i=(int) i_min_y;  i*dy<=ymax ; ++i){
      y=i*dy;
      sprintf(pos,"<text x=\"%.5g\" y=\"%.5g\" transform=\"scale(%.5g,%.5g)\" style=\"font-size:%.5gpt; text-anchor:middle;\">%.5g</text>\n",(xmax+0.3*x_scale)/fontscale,(ymax+ymin-y+0.1*y_scale)/ratio/fontscale,fontscale,ratio*fontscale,1.0,y);
      pos = buffer+strlen(buffer);
      // sortie<<setprecision(5)<<"<text x=\""<<xmax+0.2*x_scale<<"\" y=\""<<ymax+ymin-y+0.1*y_scale << "\" ";
      // sortie <<"style=\"font-size:"<<fontscale<<"pt\"";
      // sortie << ">" << (float)y<<"</text>"<<endl;
    }
    return buffer;
  }


  static string color_string(svg_attribut attr){
    switch (attr.color) {
    case 0:
      return "black";
    case _RED:
      return "red";
    case _GREEN:
      return "green";
    case _YELLOW:
      return "yellow";
    case _BLUE:
      return "blue";
    case _MAGENTA:
      return "magenta";
    case _CYAN:
      return "cyan";
    }
    return "black";
  }

#if 0
  static int color_int(string color){
    if (color=="black" || color=="noir")
      return 0;
    if (color=="red" || color=="rouge")
      return 1;
    if (color=="green" || color=="vert")
      return 2;
    if (color=="yellow" || color=="jaune")
      return 3;
    if (color=="blue" || color=="bleu")
      return 4;
    return 0;
  }
#endif
 
  static string svg_text(gen A, string legende, svg_attribut attr,double xmin,double xmax,double ymin,double ymax,GIAC_CONTEXT){
    double x_scale=(xmax-xmin)/10;
    double y_scale=(ymax-ymin)/10;
    double fontscale=0.3*x_scale;
    double ratio=y_scale/x_scale/0.6;
    if (legende=="")
      return legende;
    // remove " from legende
    int end=legende.size()-1;
    if (legende[0]=='\"')
      legende=legende.substr(1,end-1);
    gen x=re(A,contextptr); // should take care of legend position
    if (x.type!=_DOUBLE_){
      x=evalf(x,1,contextptr);
      x=evalf_double(x,1,contextptr);
    }
    if (is_greater(x,xmax,contextptr))
      x=xmax-x_scale;
    if (is_greater(xmin,x,contextptr))
      x=xmin+x_scale;    
    gen y(ymax+ymin-im(A,contextptr));
    if (y.type!=_DOUBLE_){
      y=evalf(y,1,contextptr);
      y=evalf_double(y,1,contextptr);
    }
    if (is_greater(y,ymax,contextptr))
      y=ymax-y_scale;
    if (is_greater(ymin,y,contextptr))
      y=ymin+y_scale;    
    string res= "<text  fill=\"" +color_string(attr)+"\"  x=\""+(x/fontscale).print(contextptr)+"\" y=\""+(y/ratio/fontscale).print(contextptr)
      +"\" transform=\"translate(0,"+print_DOUBLE_(ymin+ymax,contextptr)+") scale("+print_DOUBLE_(fontscale,contextptr)+","+print_DOUBLE_(-ratio*fontscale,contextptr)+")\" style=\"font-size:1.0pt; text-anchor:end;\">"
      +legende
      +"</text>\n";
    return res;
  }
  
  static double geo_thickness(double xmin,double xmax,double ymin,double ymax){
    double dx=xmax-xmin,dy=ymax-ymin,m;
    if (dx<dy) m=dx; else m=dy;
    double res= dx*dy*m*m;
    //COUT << dx << " " << dy << " " << m << " " << res << endl;
    res=std::sqrt(res);
    //COUT << res << endl;
    res=std::sqrt(res);
    // COUT << res << endl;
    return res;
  }
  
  static string svg_circle(const gen & diameter0, const gen & diameter1, const gen & angle1,const gen & angle2,svg_attribut attr, string legende,double xmin,double xmax,double ymin,double ymax,GIAC_CONTEXT){
    string s="";
    gen center=evalf_double((diameter0+diameter1)/2,1,contextptr);
    gen centerx,centery;
    reim(center,centerx,centery,contextptr);
    double cx=evalf_double(centerx,1,contextptr)._DOUBLE_val;
    double cy=evalf_double(centery,1,contextptr)._DOUBLE_val;
    gen d01=evalf_double(diameter0-diameter1,1,contextptr);
    gen rotg=arg(d01,0);
    double rot=M_PI+evalf_double(rotg,1,contextptr)._DOUBLE_val;
    gen di,dr;
    reim(d01,dr,di,contextptr);
    double did=di._DOUBLE_val,drd=dr._DOUBLE_val;
    // COUT << center << " " << d01 << " " << drd << " " << did << endl;
    double r=std::sqrt(drd*drd+did*did)/2.0;
    // COUT << r << endl;
    double a1=evalf_double(angle1,1,contextptr)._DOUBLE_val+rot;
    double a2=evalf_double(angle2,1,contextptr)._DOUBLE_val+rot;
    bool arc=std::abs(a2-a1-2*M_PI)>1e-4;
    s= string(arc?"<path ":"<circle ");
    if (attr.ie){
      // COUT << xmin << " " << xmax << " " << ymin << " " << ymax << " " << svg_epaisseur1 << " " << attr.width << endl;
      double thickness=geo_thickness(xmin,xmax,ymin,ymax)/svg_epaisseur1*attr.width;
      // COUT << thickness << endl;
      s = s+" stroke-width=\""+print_DOUBLE_(thickness,5)+"\" stroke=\""+color_string(attr);
    }
    else
      s = s+"vector-effect=\"non-scaling-stroke\" stroke=\""+color_string(attr)+"\"  stroke-width=\""+print_INT_(attr.width);
    // COUT << s << endl;
    if (attr.fill_polygon)
      s = s+"\" fill=\""+color_string(attr)+"\" ";
    else
      s = s+"\" fill=\"none\" ";
    if (arc){
      // arc of circle: compute start.x, start.y, end.x end.y
      // from polar coord (radius,a1) and (radius,a2)
      // largeArcFlag=1 if a2-a1>=M_PI
      // <path d="M start.x start.y 
      //          A radius radius rotation largeArcFlag 0 end.x end.y
      //          L center.x center.y Z">
      // move to start, arc to end, segment to center, loop to start
      s = s+" d=\"M "+print_DOUBLE_(cx+r*std::cos(a1),5)+" "+print_DOUBLE_(cy+r*std::sin(a1),5);
#if 1
      s = s+" A " + print_DOUBLE_(r,5)+ " "+print_DOUBLE_(r,5)+" 0"+((a2-a1<M_PI)?" 0 1 ":" 1 1 ");
#else
      s = s+" A " + print_DOUBLE_(r,5)+ " "+print_DOUBLE_(r,5)+" "+print_DOUBLE_(rot*rad2deg_d,5)+((a2-a1<M_PI)?" 0 1 ":" 1 1 ");
#endif
      s = s+ print_DOUBLE_(cx+r*std::cos(a2),5)+" "+print_DOUBLE_(cy+r*std::sin(a2),5);
      if (attr.fill_polygon)
	s = s+" L "+print_DOUBLE_(cx,5)+" "+print_DOUBLE_(cy,5)+" Z";
      s = s+"\" />\n";
      //CERR << s << endl;
    }
    else 
      s = s + "cx=\""+centerx.print(contextptr)+"\" cy=\""
	+ centery.print(contextptr)+"\" r=\""
	+ print_DOUBLE_(r,contextptr)
	+ "\" />\n";
    if (legende!="")
      s = s+svg_text(evalf(center+r,1,contextptr),legende,attr,xmin,xmax,ymin,ymax,contextptr);
    // COUT << s << endl;
    return s;
  }


  static string svg_segment(gen A, gen B, svg_attribut attr, string legende,double xmin,double xmax,double ymin,double ymax,GIAC_CONTEXT){
    string s;
    // gen thickness((xmax+ymax-xmin-ymin)/svg_epaisseur1);
    A=evalf(A,1,contextptr); B=evalf(B,1,contextptr);
    if (attr.ie){
      double thickness=geo_thickness(xmin,xmax,ymin,ymax)/svg_epaisseur1*attr.width;
      s= "<line stroke-width=\""+print_DOUBLE_(thickness,5);
    }
    else
      s= "<line vector-effect=\"non-scaling-stroke\" stroke-width=\""+print_INT_(attr.width);
    s = s+"\" stroke=\""+color_string(attr)+"\" x1=\""
      + re(A,contextptr).print(contextptr)+"\" y1=\""
      + im(A,contextptr).print(contextptr)+"\" x2=\""
      + re(B,contextptr).print(contextptr)+"\" y2=\""
      + im(B,contextptr).print(contextptr)+"\"/>\n";
    //CERR << s << endl;
    s = s+svg_text(B,legende,attr,xmin,xmax,ymin,ymax,contextptr);
    return s;
  }

  static string svg_vecteur(gen A, gen B, svg_attribut attr, string legende,double xmin,double xmax,double ymin,double ymax,GIAC_CONTEXT){
    string s;
    // gen thickness((xmax+ymax-xmin-ymin)/svg_epaisseur1);
    s = svg_segment(A,B,attr,legende,xmin,xmax,ymin,ymax,contextptr);
    gen Ax,Ay,Bx,By;
    reim(A,Ax,Ay,contextptr);
    reim(B,Bx,By,contextptr); 
    Ax=evalf_double(Ax,1,contextptr); Bx=evalf_double(Bx,1,contextptr);
    Ay=evalf_double(Ay,1,contextptr); By=evalf_double(By,1,contextptr);
    if (Ax.type==_DOUBLE_ && Bx.type==_DOUBLE_ && Ay.type==_DOUBLE_ && By.type==_DOUBLE_){
      double dx=Ax._DOUBLE_val-Bx._DOUBLE_val,dy=Ay._DOUBLE_val-By._DOUBLE_val;
      double dxy=std::sqrt(dx*dx+dy/attr.ysurx*dy/attr.ysurx);
      if (dxy){
	dxy =dxy/(std::min(5.0,dxy/10.0)+std::max(1.0,double(attr.width)));
	dx = dx/dxy;
	dy = dy/dxy;
	dxy = std::min(xmax-xmin,ymax-ymin)/70.0;
	dx = dx*dxy;
	dy = dy*dxy;
	double dxp=-dy/attr.ysurx,dyp=dx*attr.ysurx; // apparent perpendicular
	dx = dx*std::sqrt(3.0);
	dy = dy*std::sqrt(3.0);
	//CERR << "step3 " << dx << " " << dxp << " " << dx+dxp << " " << dy << endl;
	double dx1=dx+dxp,dx2=dx-dxp,dy1=dy+dyp,dy2=dy-dyp;
	gen C=B+gen(dx1,dy1);
	gen D=B+gen(dx2,dy2);
	//CERR << C << " " << D << endl;
	s += svg_segment(B,C,attr,"",xmin,xmax,ymin,ymax,contextptr);
	s += svg_segment(B,D,attr,"",xmin,xmax,ymin,ymax,contextptr);
      }
    }
    return s;
  }

  static string svg_point(gen center, svg_attribut attr, string legende,double xmin,double xmax,double ymin,double ymax,GIAC_CONTEXT){
    double svg_width=xmax-xmin;
    double svg_height=ymax-ymin;
    gen i=cst_i;
    gen dx(svg_width/100);
    gen dy(svg_height/100);
    switch (attr.type_point << 25){
    case _POINT_LOSANGE:
      return svg_segment(center-dx,center-i*dy,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_segment(center-i*dy,center+dx,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_segment(center+dx,center+i*dy,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_segment(center+i*dy,center-dx,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_text(center,legende,attr,xmin,xmax,ymin,ymax,contextptr);
    case _POINT_PLUS:
      return svg_segment(center-dx,center+dx,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_segment(center-i*dy,center+i*dy,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_text(center,legende,attr,xmin,xmax,ymin,ymax,contextptr);
    case _POINT_INVISIBLE:
      return svg_text(center,legende,attr,xmin,xmax,ymin,ymax,contextptr);
    case _POINT_CARRE:
      return svg_segment(center-dx-i*dy,center+dx-i*dy,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_segment(center+dx-i*dy,center+dx+i*dy,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_segment(center-dx+i*dy,center+dx+i*dy,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_segment(center-dx+i*dy,center-dx-i*dy,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_text(center,legende,attr,xmin,xmax,ymin,ymax,contextptr);
    case _POINT_TRIANGLE:
      return svg_segment(center-dx-i*dy,center-dx+i*dy,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_segment(center-dx-i*dy,center+i*dy,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_segment(center-dx+i*dy,center+i*dy,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_text(center,legende,attr,xmin,xmax,ymin,ymax,contextptr);
    case _POINT_POINT: // must make a small segment otherwise nothing visible
      return svg_segment(center-dx/2,center+dx/2,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_text(center,legende,attr,xmin,xmax,ymin,ymax,contextptr);
    }
    return svg_segment(center-dx-i*dy,center+dx+i*dy,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_segment(center-dx+i*dy,center+dx-i*dy,attr,"",xmin,xmax,ymin,ymax,contextptr)+svg_text(center,legende,attr,xmin,xmax,ymin,ymax,contextptr);
  }

  static string svg_line(gen A, gen B, svg_attribut attr, string legende,double xmin,double xmax,double ymin,double ymax,GIAC_CONTEXT){
    gen i=cst_i, C,D;
    A=evalf(A,1,contextptr); B=evalf(B,1,contextptr);
    // recherche de l'??quation de la droite
    if (is_zero(eval(re(A,contextptr)-re(B,contextptr),eval_level(contextptr),contextptr))){
      gen x=re(A,contextptr);
      C=x+i*ymin; D=x+i*ymax;
    } else {
      gen a=eval((im(A,contextptr)-im(B,contextptr))/(re(A,contextptr)-re(B,contextptr)),eval_level(contextptr),contextptr);
      gen b=eval(im(A,contextptr)-a*re(A,contextptr),eval_level(contextptr),contextptr);
      C=xmin+i*(a*xmin+b);
      D=xmax+i*(a*xmax+b);
    }
    return svg_segment(C,D,attr,legende,xmin,xmax,ymin,ymax,contextptr);
  }

  static string svg_half_line(gen A, gen B, svg_attribut attr, string legende,double xmin,double xmax,double ymin,double ymax,GIAC_CONTEXT){
    gen i=cst_i, C,D;
    A=evalf(eval(A,1,contextptr),1,contextptr); B=evalf(eval(B,1,contextptr),1,contextptr);
    gen reA,imA,reB,imB;
    reim(A,reA,imA,contextptr);
    reim(B,reB,imB,contextptr);
    // recherche de l'equation de la droite
    if (is_zero(reA-reB)){
      gen x=reA;
      if (is_positive(imB-imA,contextptr))
	C=x+i*ymax;
      else
	C=x+i*ymin;
    } 
    else {
      gen a=(imB-imA)/(reB-reA);
      gen b=imA-a*reA;
      if (is_positive(reB-reA,contextptr))
	C=xmax+i*(a*xmax+b);
      else
	C=xmin+i*(a*xmin+b); 
    }
    return svg_segment(A,C,attr,legende,xmin,xmax,ymin,ymax,contextptr);
  }


  static string svg_polyline(gen g,svg_attribut attr, string name,double xmin,double xmax,double ymin,double ymax,GIAC_CONTEXT){
    string x="x", y="y", s;
    int i;
    g=eval(g,eval_level(contextptr),contextptr);
    vecteur v=*(g._VECTptr);
    if (v[0]==v[v.size()-1])
      s= "<polygon ";
    else
      s= "<polyline ";
    if (attr.ie){
      double thickness=geo_thickness(xmin,xmax,ymin,ymax)/svg_epaisseur1*attr.width;
      s = s+"stroke-width=\""+print_DOUBLE_(thickness,5);
    }
    else
      s = s+"vector-effect=\"non-scaling-stroke\"  stroke-width=\""+print_INT_(attr.width);
    if (attr.fill_polygon)
      s = s+"\" fill=\""+color_string(attr)+"\" points=\"";
    else
      s = s+"\" stroke=\""+color_string(attr)+"\" fill=\"none\" points=\"";
    for (i=0 ; i<signed(v.size())-1 ; i++){
      s = s+re(evalf(v[i],1,contextptr),contextptr).print(contextptr)+" "+im(evalf(v[i],1,contextptr),contextptr).print(contextptr)+", ";
    }
    s = s+re(evalf(v[i],1,contextptr),contextptr).print(contextptr)+" "+im(evalf(v[i],1,contextptr),contextptr).print(contextptr)+"\" /> ";
    s = s+svg_text(v[i],name,attr,xmin,xmax,ymin,ymax,contextptr);
    return s;
  }

  // Trace de courbes
  // un point sur 2 sert de point de contr??le
  // on peut sophistiquer
  static string svg_bezier_curve(gen g, svg_attribut attr, string legende,double xmin,double xmax,double ymin,double ymax,GIAC_CONTEXT){ 
    string x="x", y="y";
    int i;
    string s= "<path ";
    if (attr.ie){
      double thickness=geo_thickness(xmin,xmax,ymin,ymax)/svg_epaisseur1*attr.width;
      s = s+"stroke-width=\""+print_DOUBLE_(thickness,5);
    }
    else
      s = s+"vector-effect=\"non-scaling-stroke\"  stroke-width=\""+print_INT_(attr.width);
    if (attr.fill_polygon)
      s = s+"\" fill=\""+color_string(attr)+"\" d=\"M";
    else
      s = s+"\" stroke=\""+color_string(attr)+"\" fill=\"none\" d=\"M";
    g=evalf(g,1,contextptr);
    vecteur v=*(g._VECTptr);
    for (i=0 ; i<signed(v.size()) ; i++){
      s=s+re(v[i],contextptr).print(contextptr)+" "+im(v[i],contextptr).print(contextptr)+" ";
      if (i%2==0)
	s=s+"Q ";
      else
	s=s+", ";
    }
    i--;
    if (i%2==0)
      s=s+re(v[i],contextptr).print(contextptr)+" "+im(v[i],contextptr).print(contextptr)+" ,  "+re(v[i],contextptr).print(contextptr)+" "+im(v[i],contextptr).print(contextptr);
    if (i%2==1)
      s=s+re(v[i],contextptr).print(contextptr)+" "+im(v[i],contextptr).print(contextptr)+" ";
    s=s+"\" />\n ";
    // on ??crit la l??gende
    //recherche d'un point dans le cadre pour ancrer la l??gende
    for (i=0 ; i<signed(v.size()) ; i++){
      if (is_positive(re(v[i],contextptr)-xmin,contextptr) 
	  && is_positive(im(v[i],contextptr)-ymin,contextptr)
	  && is_positive(xmax-re(v[i],contextptr),contextptr) 
	  && is_positive(ymax-im(v[i],contextptr),contextptr) ){
	s = s+svg_text(v[i],legende,attr,xmin,xmax,ymin,ymax,contextptr);
	break;
      }
    }
    return s;
  }


  static string symbolic2svg(const symbolic & mys,double xmin,double xmax,double ymin,double ymax,double ysurx,GIAC_CONTEXT);

  //fonction appelee ssi v est un vecteur
  static string vect2svg(gen v, svg_attribut attr, string name,double xmin,double xmax,double ymin,double ymax,GIAC_CONTEXT){
    if (v.type != _VECT)
      return "error";
    if (v.subtype==_SYMB){
      if (v[0].type==_VECT){
	return vect2svg(v[0], attr, name,xmin,xmax,ymin,ymax,contextptr);
      }
      if (v[0].type==_SYMB)
	return symbolic2svg(*v[0]._SYMBptr,xmin,xmax,ymin,ymax,attr.ysurx,contextptr);
    }
    if (v.subtype==_GROUP__VECT || v._VECTptr->size()>2)
      return svg_polyline(v, attr, name,xmin,xmax,ymin,ymax,contextptr);
    if (v.subtype==_VECTOR__VECT)
      return svg_vecteur(v[0],v[1], attr, name,xmin,xmax,ymin,ymax,contextptr);
    if (v.subtype==_LINE__VECT)
      return svg_line(v[0],v[1], attr, name,xmin,xmax,ymin,ymax,contextptr);
    if (v.subtype==_HALFLINE__VECT)
      return svg_half_line(v[0],v[1], attr, name,xmin,xmax,ymin,ymax,contextptr);
    return "vect2svg error";
  }

#if defined EMCC && !defined GIAC_GGB
#include <emscripten.h>
#endif

  static string symbolic2svg(const symbolic & mys,double xmin,double xmax,double ymin,double ymax,double ysurx,GIAC_CONTEXT){ 
    int color=default_color(contextptr);
    string name="";
    if (mys.sommet==at_pnt){ 
      vecteur v=*(mys.feuille._VECTptr);
      string the_legend;
      vecteur style(get_style(v,the_legend));
      int styles=style.size();
      // color
      int ensemble_attributs = style.front().val;
      bool hidden_name = false;
      if (style.front().type==_ZINT){
	ensemble_attributs = mpz_get_si(*style.front()._ZINTptr);
	hidden_name=true;
      }
      else
	hidden_name=ensemble_attributs<0;
      int width           =(ensemble_attributs & 0x00070000) >> 16; // 3 bits
      int epaisseur_point =(ensemble_attributs & 0x00380000) >> 19; // 3 bits
      int type_line       =(ensemble_attributs & 0x01c00000) >> 22; // 3 bits
      if (type_line>4)
	type_line=(type_line-4)<<8;
      int type_point      =(ensemble_attributs & 0x0e000000) >> 25; // 3 bits
      int labelpos        =(ensemble_attributs & 0x30000000) >> 28; // 2 bits
      bool fill_polygon   =(ensemble_attributs & 0x40000000) >> 30;
      color         =(ensemble_attributs & 0x0000ffff);
      epaisseur_point += 2;
      bool ie=false; // detect here if we are using IE
#if defined EMCC && !defined GIAC_GGB
      ie=EM_ASM_INT_V({
	  if (Module.worker) return 0;
	  var ua = window.navigator.userAgent;
	  var old_ie = ua.indexOf('MSIE ');
	  var new_ie = ua.indexOf('Trident/');
	  if ((old_ie > -1) || (new_ie > -1))
	    return 1;
	  else
	    return 0;
	});
#endif
      svg_attribut attr={ysurx,color,width+1,epaisseur_point,type_line,type_point,fill_polygon,hidden_name,ie};
      if(v.size()==3)
	name=v[2].print(contextptr);
      if (v[0].type==_VECT){
	return vect2svg(v[0], attr, name,xmin,xmax,ymin,ymax,contextptr);
      }                     
      if (v[0].type==4)           //indispensable, mais je ne sais pas pourquoi
	v[0]=gen(v[0].print(contextptr),contextptr);
      if (v[0].type==_SYMB){ 
	symbolic figure=*v[0]._SYMBptr; 
	if (figure.sommet == at_curve){
	  //CERR << attr.ysurx << endl;
	  gen curve=figure.feuille[1];
	  string s;
	  if (type_line>4 && curve.type==_VECT && curve._VECTptr->size()>2){
	    vecteur & v = *curve._VECTptr;
	    s=svg_vecteur(v[v.size()/2],v[v.size()/2+1],attr,"",xmin,xmax,ymin,ymax,contextptr);
	  }
	  s=s+svg_bezier_curve(curve,attr,name,xmin,xmax,ymin,ymax,contextptr);
	  return s;
	}
	if (figure.sommet == at_pnt)
	  return symbolic2svg(figure,xmin,xmax,ymin,ymax,attr.ysurx,contextptr);
	if (figure.sommet==at_segment || figure.sommet==at_vector){
	  gen segment=figure.feuille;
	  return svg_segment(segment[0],segment[1], attr, name,xmin,xmax,ymin,ymax,contextptr); 
	}   
	if (figure.sommet==at_droite ){
	  gen segment=figure.feuille;
	  return svg_line(segment[0],segment[1], attr, name,xmin,xmax,ymin,ymax,contextptr);
	}
	if (figure.sommet==at_demi_droite ){
	  gen segment=figure.feuille;
	  return svg_half_line(segment[0],segment[1], attr, name,xmin,xmax,ymin,ymax,contextptr);
	}
	if (figure.sommet==at_cercle ){
	  gen diametre;
	  gen angle1=0,angle2=2*M_PI;
	  gen f=figure.feuille;
	  if (f.type==_VECT && !f._VECTptr->empty() && f._VECTptr->front().type==_VECT){
	    if (f._VECTptr->size()>=3){
	      angle1=f[1]; 
	      angle2=f[2];
	    }
	    diametre=f._VECTptr->front();
	  }
	  else
	    diametre=f;
	  if (diametre.type==_VECT && diametre._VECTptr->size()>=2){
	    vecteur v=*diametre._VECTptr;
	    return svg_circle(v[0],v[1], angle1,angle2,attr, name,xmin,xmax,ymin,ymax,contextptr);
	  }
	  return "svg circle error";
	}
	return svg_point(v[0], attr, name,xmin,xmax,ymin,ymax,contextptr); 
      }
      return svg_point(v[0], attr, name,xmin,xmax,ymin,ymax,contextptr); 
    }
    return "undef";
  }


  string gen2svg(const gen &e,double xmin,double xmax,double ymin,double ymax,double ysurx,GIAC_CONTEXT){
    if (e.type== _SYMB)
      return symbolic2svg(*e._SYMBptr,xmin,xmax,ymin,ymax,ysurx,contextptr);
    if (e.type==_VECT){
      string s;
      vecteur v=*e._VECTptr;
      for (int i=0; i<signed(v.size()); i++){
	if (v[i].type==_SYMB){
	  symbolic sym=*v[i]._SYMBptr; 
	  if (sym.sommet==at_pnt)
	    s=s+symbolic2svg(sym,xmin,xmax,ymin,ymax,ysurx,contextptr);
	}
	if (v[i].type==_VECT){
	  s=s+gen2svg(v[i],xmin,xmax,ymin,ymax,ysurx,contextptr);
	}
      }
      return s;
    }
    return "error";
  }
  string gen2svg(const gen &e,double xmin,double xmax,double ymin,double ymax,GIAC_CONTEXT){
    return gen2svg(e,xmin,xmax,ymin,ymax,1.0,contextptr);
  }
  string gen2svg(const gen &e,GIAC_CONTEXT){
    return gen2svg(e,gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,1.0,contextptr);
  }
  gen _svg(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT && g.subtype==_SEQ__VECT && g._VECTptr->size()>1 && (*g._VECTptr)[1].type==_STRNG){
      ofstream of((*g._VECTptr)[1]._STRNGptr->c_str());
      of << gen2svg(g._VECTptr->front(),contextptr) << endl;
      return plus_one;
    }
    return string2gen(gen2svg(g,contextptr),false);
  }
  static const char _svg_s []="svg";
  static define_unary_function_eval (__svg,&_svg,_svg_s);
  define_unary_function_ptr5( at_svg ,alias_at_svg,&__svg,0,true);

  // --------------------- End SVG --------------------


  static string symbolic2mathml(const symbolic & mys, string &svg,GIAC_CONTEXT){

    string opstring(mys.sommet.ptr()->print(contextptr));
    if (opstring!="/" && (mys.sommet.ptr()->texprint || mys.sommet==at_different))  
      return mathml_print(mys,contextptr);
    if (mys.sommet==at_pnt) { 
      svg=svg+symbolic2svg(mys,gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,1.0,contextptr);
      return "<mtext>"+mys.print(contextptr)+"</mtext>";
    }
    gen tmp,value;
    if (mys.sommet==at_rootof && (tmp=mys.feuille).type==_VECT && tmp._VECTptr->size()==2 && tmp._VECTptr->front().type==_VECT && has_rootof_value(tmp._VECTptr->back(),value,contextptr)){
      value=horner_rootof(*tmp._VECTptr->front()._VECTptr,value,contextptr);
      return gen2mathml(value,svg,contextptr);
    }
    if ( (mys.feuille.type==_VECT) && (mys.feuille._VECTptr->empty()) )
      return string(provisoire_mbox_begin)+mys.sommet.ptr()->print(contextptr)+string("()")+string(provisoire_mbox_end);
    if ( (mys.feuille.type!=_VECT) || (mys.feuille._VECTptr->front().type==_VECT)){
      if (mys.sommet==at_factorial){
	if (mys.feuille.type==_SYMB)
	  return "<mrow><mo>(</mo>" + gen2mathml(mys.feuille,contextptr) +"<mo>)</mo></mrow><mtext>!</mtext>";
	return gen2mathml(mys.feuille,contextptr)+"<mtext>!</mtext>";
      }
      if ((mys.sommet==at_neg) || (mys.sommet==at_plus)){
	if (mys.feuille.type!=_SYMB) 
	  return string("<mo>")+mys.sommet.ptr()->print(contextptr)+"</mo>"+gen2mathml(mys.feuille,contextptr); 
	if (mys.feuille._SYMBptr->sommet==at_inv || mys.feuille._SYMBptr->sommet==at_pow )
	  return string("<mo>")+mys.sommet.ptr()->print(contextptr)+"</mo>"+gen2mathml(mys.feuille,contextptr) ;
	return string("<mo>")+mys.sommet.ptr()->print(contextptr)+"</mo>"+string("<mrow><mo>(</mo>") 
	  + gen2mathml(mys.feuille,contextptr) +string("<mo>)</mo></mrow>");
      }
      if (mys.sommet==at_inv){
	return string("<mfrac><mrow><mn>1</mn></mrow><mrow>") + gen2mathml(mys.feuille,contextptr) 
	  + string("</mrow></mfrac>");
      }
      if (mys.sommet==at_pow) {
	 return "<msup><mrow>"+gen2mathml((*(mys.feuille._VECTptr))[0],contextptr)+"</mrow><mrow>"+gen2mathml((*(mys.feuille._VECTptr))[1],contextptr)+"</mrow></msup>";
      }
      return string(provisoire_mbox_begin) +mys.sommet.ptr()->print(contextptr)+ string(provisoire_mbox_end)
	+ "<mrow><mo>(</mo>" + gen2mathml(mys.feuille,contextptr) +"<mo>)</mo></mrow>" ;
    }
    string s;
    int l=mys.feuille._VECTptr->size();
    if ( mys.sommet==at_plus ){
      for (int i=0;i<l;++i){
	gen e((*(mys.feuille._VECTptr))[i]);
	if ((e.type==_SYMB) && (e._SYMBptr->sommet==at_neg)){
	  if ( (e._SYMBptr->feuille).type==_SYMB && (e._SYMBptr->feuille)._SYMBptr->sommet==at_plus)
	    s = s+string("<mo>-</mo><mrow><mo>(</mo>")
	      + gen2mathml(e._SYMBptr->feuille,contextptr)+
	      string("<mo>)</mo></mrow>");
	  else
	    s = s+string("<mo>-</mo><mrow>") + gen2mathml(e._SYMBptr->feuille,contextptr)+string("</mrow>");
	}
	else {
	  if ( ( (e.type==_INT_) || (e.type==_ZINT) ) && (!is_positive(e,contextptr)) )
	    s = s+"<mo>-</mo><mn>"+(-e).print(contextptr)+"</mn>";
	  else {
	    string adds=gen2mathml(e,contextptr);
	    if (i && (adds.size()<5 || adds.substr(0,5)!="<mo>-"))
	      s = s+"<mo>"+opstring+"</mo>";
	    s = s+adds;
	  }
	}
      } // end_for
      return s;
    }
    if (mys.sommet==at_prod) {
      vecteur num;
      vecteur den;
      for (int i=0;i<l;++i){

	gen e((*(mys.feuille._VECTptr))[i]);
	if ( (e.type==_SYMB) && (e._SYMBptr->sommet==at_inv) )
	  den.push_back(e._SYMBptr->feuille);
	else {
	  if (!den.empty()){
	    s = s+"<mfrac><mrow>"+prod_vect2mathml_no_bra(num,contextptr)+"</mrow><mrow>"+prod_vect2mathml_no_bra(den,contextptr)
	      +"</mrow></mfrac>";
	    s = s+"<mo>&times;</mo>"; // A revoir ?  F.H: est ce que le seul cas utile est '1/y*(-33)' sinon un espace pour couper les barres de fractions serait plus joli
	    num.clear();
	    den.clear();
	  }
	  num.push_back(e);
	}
      }

      if (den.empty())
	return s+prod_vect2mathml(num,contextptr);
      return s+"<mfrac><mrow>"+prod_vect2mathml_no_bra(num,contextptr)+"</mrow><mrow>"
	+prod_vect2mathml_no_bra(den,contextptr)+"</mrow></mfrac>";
    } // end if sommet_is_prod

    if (mys.sommet==at_pow){
      if ( (mys.feuille._VECTptr->back()==plus_one_half)  ){
    return "<msqrt><mrow>"+gen2mathml(mys.feuille._VECTptr->front(),contextptr)+"</mrow></msqrt>";
      }
      if ( (mys.feuille._VECTptr->back()==minus_one_half ) || 
	   (mys.feuille._VECTptr->back()==fraction(minus_one,plus_two) ) )
	return "<mfrac><mn>1</mn><msqrt>"+gen2mathml(mys.feuille._VECTptr->front(),contextptr)+"</msqrt></mfrac>";
      string s0=gen2mathml((*(mys.feuille._VECTptr))[0],contextptr),s1=gen2mathml((*(mys.feuille._VECTptr))[1],contextptr);
      string s_bra="<msup><mfenced open=\"(\" close=\")\"><mrow>("+s0+")</mrow></mfenced><mrow>"+s1+"</mrow></msup>";
      string s_no_bra= "<msup><mrow> "+s0+"</mrow><mrow>"+s1+"</mrow></msup>";
      if (mys.feuille._VECTptr->front().type==_SYMB){

	symbolic mantisse(*mys.feuille._VECTptr->front()._SYMBptr);
	if ( (mantisse.feuille.type==_VECT) && (mantisse.feuille._VECTptr->empty()) )
	  return s_bra;
	if ( (mantisse.feuille.type!=_VECT) || (mantisse.feuille._VECTptr->front().type==_VECT))
	  return s_bra;
	if (mantisse.feuille._VECTptr->size()>1)
	  return  s_bra;
	else
	  return  s_no_bra;
      }
      else if (mys.feuille._VECTptr->front().type==_FRAC)
	return s_bra;
      else if (mys.feuille._VECTptr->front().type==_CPLX){
          if  (is_zero(im(mys.feuille._VECTptr->front(),contextptr))) return s_no_bra;
          else return s_bra;
      }
      //F.H 2*(-1)^n
      //_REAL et _DOUBLE_ inutiles (-1.5)^n passe en exp
      else if ((mys.feuille._VECTptr->front().type==_INT_)||(mys.feuille._VECTptr->front().type==_ZINT)){
	if  (is_positive(mys.feuille._VECTptr->front(),contextptr)) return s_no_bra;
          else return s_bra;
      }
      else
	return s_no_bra;
    }

    if (opstring=="/") { //at_division non reconnu
      return "<mfrac><mrow>"+gen2mathml((*(mys.feuille._VECTptr))[0],contextptr)
	+"</mrow><mrow>"+gen2mathml((*(mys.feuille._VECTptr))[1],contextptr)+"</mrow></mfrac>";
    } 

    s = string(provisoire_mbox_begin)+opstring + string(provisoire_mbox_end) +"<mrow><mo>(</mo>";
    for (int i=0;;++i){
      s = s+gen2mathml((*(mys.feuille._VECTptr))[i],contextptr);
      if (i==l-1)
	return s+"<mo>)</mo></mrow>";
      s = s+',';
    }
  }




  // assume math mode enabled
  string gen2mathml(const gen &e,GIAC_CONTEXT){
    string svg="";
    return gen2mathml(e, svg,contextptr);
  }

#ifdef EMCC
  static string mathml_split(const string & s,int slicesize){
    if (s.size()<=slicesize)
      return s;
    string res;
    for (int l=0;;){
      int n=giacmin(slicesize,s.size()-l);
      res = res + s.substr(l,n);
      l += slicesize;
      if (l>s.size()) break;
      res = res +"\\<br>";
    }
    return res;
  }
#else
  static string mathml_split(const string & s,int slicesize){
    return s;
  }
#endif

  static string indice_mathml(const string & s1,const string & s2){
    return "<msub><mi>"+s1+"</mi><mn>"+s2+"</mn></msub>";
  }

  static string idnt2mathml_(const string & sorig){
    string s0=sorig;
    int n=int(s0.size()),j;
    for (j=n-1;j>=2;--j){
      if (s0[j]>32 && isalpha(s0[j]))
	break;
    }
    string s=s0.substr(0,j+1),sadd;
    if (j<n-1)
      sadd=s0.substr(j+1,n-1-j);
    switch (s.size()){
    case 2:
      if (s=="mu" || s=="nu" || s=="pi" || s=="xi" || s=="Xi")
	return "&"+s+';'+sadd;
      if (s=="im")
	return "&Im"+s+';'+sadd;
      if (s=="re")
	return "&Re"+sadd;
      break;
    case 3:
      if (s=="chi" || s=="phi" || s=="Phi" || s=="eta" || s=="rho" || s=="tau" || s=="psi" || s=="Psi")
	return "&"+s+';'+sadd;
      break;
    case 4:
      if (s=="beta" || s=="zeta")
	return "&"+s+';'+sadd;
      break;
    case 5:
      if (s=="alpha" || s=="delta" || s=="Delta" || s=="gamma" || s=="Gamma" || s=="kappa" || s=="theta" || s=="Theta" || s=="sigma" || s=="Sigma" || s=="Omega" || s=="omega" || s=="aleph")
	return "&"+s+';'+sadd;      
      break;
    case 6:
      if (s=="lambda" || s=="Lambda" || s=="approx")
	return "&"+s+';'+sadd;      
      break;
    case 7:
      if (s=="epsilon" || s=="product")
	return "&"+s+';'+sadd;      
      break;
    }
    return s0;
  }

  static string idnt2mathml(const string & s0){
    int n=int(s0.size()),j;
    for (j=n-1;j>=1;--j){
      if (j<n-1 && s0[j]=='_')
	return indice_mathml(idnt2mathml_(s0.substr(0,j)),s0.substr(j+1,n-1-j));
    }
    return "<mi>"+idnt2mathml_(s0)+"</mi>";
  }

  string gen2mathml(const gen &e, string &svg,GIAC_CONTEXT){
    string part_re="", part_im="<mi>i</mi>";
    if (e.type==_SYMB && e._SYMBptr->sommet==at_sum)
      return mathml_printassum(e,contextptr);
    if (e.type==_SYMB && e._SYMBptr->sommet==at_abs)
      return mathml_printasabs(e,contextptr);
    if (e.type==_SYMB && e._SYMBptr->sommet==at_unit)      
      return mathml_printasunit(e,contextptr);
    switch (e.type){
    case _INT_: case _ZINT:                        
      return "<mn>"+mathml_split(e.print(contextptr),50)+"</mn>";
    case _DOUBLE_:                        
      /* FH: This should be tuned in the context
	 if (fabs(e._DOUBLE_val)<1.1e-5)
	 return "<mn>0.0</mn>";
	 else
      */
      return "<mn>"+e.print(contextptr)+"</mn>"; 
    case _REAL:                        
      return "<mn>"+mathml_split(e.print(contextptr),50)+"</mn>"; 
    case _CPLX:
      if (!is_zero(re(e,contextptr)))
	part_re="<mn>"+re(e,contextptr).print(contextptr)+"</mn>";
      if (!is_zero(im(e,contextptr))){
	if (is_positive(im(e,contextptr),contextptr)){
	  if (!is_zero(re(e,contextptr)))
	    part_re = part_re+"<mo>+</mo>";
	}
	else
	  part_re = part_re+"<mo>-</mo>";
      }
      if (!is_one(-im(e,contextptr)) && ! is_one(im(e,contextptr)))
	part_im="<mn>"+abs(im(e,contextptr),contextptr).print(contextptr)+"</mn>"+part_im;	
      //the is_zero test should be the last one  
      //Ex: 3+10.0**(-13)*i avec Digits 12 et 10.0**(-13)*i avec Digits 12 et 
      if (is_zero(im(e,contextptr))){
	part_im="";
	if (is_zero(re(e,contextptr)))
	  part_re="<mn>0.0</mn>";
      }
      return part_re+part_im;
    case _IDNT:                        
      if (e==unsigned_inf)
	return "<mn>&infin;</mn>";
      if (e==cst_pi)
	return "<mi>&pi;</mi>";
      if (e==undef)
	return "<mi>undef</mi>";
      return  idnt2mathml(e.print(contextptr));
    case _SYMB: {
      gen tmp=aplatir_fois_plus(e);
      if (tmp.type!=_SYMB)
	return gen2mathml(tmp,svg,contextptr);
      return symbolic2mathml(*tmp._SYMBptr, svg,contextptr);
    }
    case _VECT: {
      vector<int> V; int p=0;
      if (is_mod_vecteur(*e._VECTptr,V,p) && p!=0){
	gen gm=makemodquoted(unmod(e),p);
	return gen2mathml(gm,svg,contextptr);
      }
      if (e.subtype==_SPREAD__VECT)
	return spread2mathml(*e._VECTptr,1,contextptr); //----------------v??rifier le 2??me param??tre
      if (e.subtype!=_SEQ__VECT && ckmatrix(*e._VECTptr)){
	vector< vector<int> > M; p=0;
	if (is_mod_matrice(*e._VECTptr,M,p) && p!=0){
	  gen gm=makemodquoted(unmod(e),p);
	  return gen2mathml(gm,svg,contextptr);
	}
	return matrix2mathml(*e._VECTptr,contextptr);
      }
      return _VECT2mathml(*e._VECTptr,e.subtype, svg,contextptr);
    }
    case _SPOL1:
      return _SPOL12mathml(*e._SPOL1ptr,svg,contextptr);
    case _POLY:
      return string("<mi>polynome</mi>");
    case _FRAC:                        
      return string("<mfrac><mrow>")+gen2mathml(e._FRACptr->num,contextptr)+"</mrow><mrow>"
	+gen2mathml(e._FRACptr->den,contextptr)+"</mrow></mfrac>";
    case _EXT: 
      return "";
    case _STRNG:
      return string2mathml(*e._STRNGptr);
      // return "<mi>"+(*e._STRNGptr)+"</mi>";
    case _FUNC: case _MAP:
      return "<mi>"+e.print(contextptr)+"</mi>";
    case _USER:
      return string2mathml(e.print(contextptr));
      // return "<mi>"+e._USERptr->texprint(contextptr)+"</mi>"; 
    case _MOD:
      return gen2mathml(*e._MODptr,contextptr)+"<mo>%</mo>"+gen2mathml(*(e._MODptr+1),contextptr);
    default:
      settypeerr(gettext("MathMl convert ")+e.print(contextptr));
    }
    return "mathml error (gen2mathml)";
  }

  
  string ingen2mathml(const gen & g,bool html5,GIAC_CONTEXT){
    if (html5) return "\n<math display=\"inline\" xmlns=\"http://www.w3.org/1998/Math/MathML\">\n\n"+gen2mathml(g,contextptr)+"\n\n</math>\n";
    return "\n<math mode=\"display\" xmlns=\"http://www.w3.org/1998/Math/MathML\">\n\n"+gen2mathml(g,contextptr)+"\n\n</math><br/>\n";
  }

  static string gen2mathmlfull(const gen & g,GIAC_CONTEXT){
    return string(mathml_preamble)+ingen2mathml(g,false,contextptr)+mathml_end+'\n';
  }
  unsigned max_prettyprint_equation=5000;
  gen _mathml(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
#ifndef EMCC
    if (g.type==_VECT && g.subtype==_SEQ__VECT && g._VECTptr->size()>1 && (*g._VECTptr)[1].type==_STRNG && *((*g._VECTptr)[1]._STRNGptr)!="Done"){
      ofstream of((*g._VECTptr)[1]._STRNGptr->c_str());
      of << gen2mathmlfull(g._VECTptr->front(),contextptr) << endl;
      return plus_one;
    }
#endif
    unsigned ta=taille(g,max_prettyprint_equation);
    if (ta>max_prettyprint_equation)
      return string2gen("Expression_too_large",false);
    if (g.type==_VECT && g._VECTptr->size()==2 && g._VECTptr->back().type==_INT_)
      return string2gen(ingen2mathml(g._VECTptr->front(),g._VECTptr->back().val,contextptr),false);
    if (g.type==_VECT && g.subtype==_SEQ__VECT && g._VECTptr->size()>2 && g._VECTptr->back().type==_INT_){
      vecteur v(g._VECTptr->begin(),g._VECTptr->end()-1);
      gen g1(v,g.subtype);
      return string2gen(ingen2mathml(g1,g._VECTptr->back().val,contextptr),false);
    }
    return string2gen(gen2mathmlfull(g,contextptr),false);
  }
  static const char _mathml_s []="mathml";
  static define_unary_function_eval (__mathml,&_mathml,_mathml_s);
  define_unary_function_ptr5( at_mathml ,alias_at_mathml,&__mathml,0,true);

  static string spread2mathmlfull(const matrice m, int formule,GIAC_CONTEXT){
    return mathml_preamble+spread2mathml(m, formule,contextptr)+mathml_end+'\n';
  }

  gen _spread2mathml(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type!=_VECT || g._VECTptr->size()<2)
      return string2gen(" syntax error ");
    vecteur &v=*g._VECTptr; 
    if (g.type==_VECT && g.subtype==_SEQ__VECT && g._VECTptr->size()>2 && (*g._VECTptr)[2].type==_STRNG){
      ofstream of((*g._VECTptr)[2]._STRNGptr->c_str());
      of << spread2mathmlfull(*(v[0]._VECTptr), is_one(v[1]),contextptr) << endl;
      return plus_one;
    }
    return string2gen(spread2mathmlfull(*(v[0]._VECTptr) , is_one(v[1]),contextptr));
  }
  static const char _spread2mathml_s []="spread2mathml";
  static define_unary_function_eval (__spread2mathml,&_spread2mathml,_spread2mathml_s);
  define_unary_function_ptr5( at_spread2mathml ,alias_at_spread2mathml,&__spread2mathml,0,true);

#endif // RTOS_THREADX

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
