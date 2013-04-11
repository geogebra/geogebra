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
#include <cstdio>
#include <iomanip>
#include <math.h>

#ifdef HAVE_SSTREAM
#include <sstream>
#else
#include <strstream>
#endif
using namespace std;

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

#ifdef RTOS_THREADX
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
  string svg_legend("");
  const char svg_end[]="</svg>";

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

  static string _VECT2mathml(const vecteur & v, unsigned char type,string &svg,GIAC_CONTEXT){
    string s("<mfenced open=\"");
    if (type==_SEQ__VECT) s.append("(\" close=\")\">");
    else {
      if (type==_SET__VECT) s.append("{\" close=\"}\">");
      else s.append("[\" close=\"]\">");
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
  }

  static bool needs_times(gen g,GIAC_CONTEXT){
    if (eval(g,eval_level(contextptr),contextptr).is_constant())
      return true;
    else {
      if (g.type==_SYMB) {
	symbolic symb = *(g._SYMBptr);
	if ( !strcmp(symb.sommet.ptr()->print(contextptr),"/") )
	  return true;
      }
    }
    return false;
  }

  static string prod_vect2mathml(const vecteur & v,GIAC_CONTEXT){
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
      else 
{	s += gen2mathml(*it,contextptr);

      }
      ++it;
      if (it==itend)
	return s;
      else if (needs_times(*it,contextptr))
    s += "<mo>&times;</mo>";
    }
  }

  static string prod_vect2mathml_no_bra(const vecteur & v,GIAC_CONTEXT){
    if (v.empty())
      return "<mn>1</mn>";
    vecteur::const_iterator it=v.begin(),itend=v.end();
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
      else 
	s += gen2mathml(*it,contextptr);
      ++it;
      if (it==itend)
	return s;
      else if (needs_times(*it,contextptr))
	s += "<mo>*</mo>";  
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
    if (g._SYMBptr->sommet!=at_equal)
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
    if (feuille.type!=_VECT)
      return "<msup><mrow><mo>(</mo>"+gen2mathml(feuille,contextptr)+"<mo>)</mo></mrow><mi>'</mi></msup>";
    return "<mfrac><mrow><mo>&part;</mo><mo>(</mo>"+gen2mathml(feuille._VECTptr->front(),contextptr)
      +"<mo>)</mo></mrow><mrow><mo>&part;</mo>"+gen2mathml(feuille._VECTptr->back(),contextptr)+"</mrow></mfrac>";
  }
  // ----------------------- fin provenant de derive ---------------------------

  static string mathml_print(const symbolic & mys,GIAC_CONTEXT){


    unary_function_ptr u =mys.sommet;
    if (u==at_equal)
      return mathml_printsommetasoperator(mys.feuille,"<mo>=</mo>",contextptr);  
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



  //---------------- Zone SVG  ---------------

  static void svg_dx_dy(double svg_width, double svg_height, int * dx, int *dy){
    // calibrage des graduations
    int p_h=(int) std::log10(svg_height);
    int p_w=(int) std::log10(svg_width);
    *dx=(int) std::pow((double)10,p_w-1);
    *dy=(int) std::pow((double)10,p_h-1);
    if (*dx==0)
      *dx=1;
    if (*dy==0)
      *dy=1;
    if (svg_width/(*dx)>25)
      *dx=5*(*dx);
    if (svg_height/(*dy)>25)
      *dy=5*(*dy);
  }

  string svg_preamble(double svg_width_cm, double svg_height_cm){
    double svg_width=gnuplot_xmax-gnuplot_xmin;
    double svg_height=gnuplot_ymax-gnuplot_ymin;
    double x_scale=(gnuplot_xmax-gnuplot_xmin)/10;
    double y_scale=(gnuplot_ymax-gnuplot_ymin)/10;
    int i;
    string grid_color="blue";
    string grid_color2="navy";
#ifdef HAVE_SSTREAM
    ostringstream sortie;
#else
    ostrstream sortie;
#endif
    if (svg_width<svg_height){
      svg_width_cm=svg_width_cm*svg_width/svg_height+1; 
      svg_height_cm=svg_height_cm+1;
    }  else {
      svg_height_cm=svg_height_cm*svg_height/svg_width+1;
      svg_width_cm=svg_width_cm+1;
    }
    sortie<<"<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>"<<endl;
    sortie<<"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.0\" preserveAspectRatio=\"xMidyMin meet\" width=\""<<svg_width_cm+2<<"cm\" height=\""<<svg_height_cm+1<<"cm\" viewBox=\""<<gnuplot_xmin-x_scale<<" "<<gnuplot_ymin-y_scale<<" "<<svg_width+3*x_scale<<" "<<svg_height+2*x_scale<<"\" >\n<g transform=\"translate(0,"<<svg_height+2*gnuplot_ymin<<") scale(1,-1)\">\n";


    // calibrage des graduations
    int  dx, dy;
    svg_dx_dy(svg_width, svg_height, &dx, &dy);
    double i_min_x= gnuplot_xmin/dx;
    double i_min_y= gnuplot_ymin/dy;
  
    //grille  
    double x,y;
    //gen thickness((gnuplot_xmax-gnuplot_xmin)/1000);
    double thickness((gnuplot_xmax+gnuplot_ymax-gnuplot_xmin-gnuplot_ymin)/2000);
    
    for (i=(int) i_min_x; i*dx<gnuplot_xmax; i++){
      x=i*dx;
      sortie<<"<line x1=\""<<x <<"\" y1=\""<<gnuplot_ymin <<"\" x2=\""<<x <<"\" y2=\""<<gnuplot_ymax;
      if(i%5==0)
	sortie<<"\" stroke=\""<<grid_color2<<"\"  stroke-width=\""<<2*thickness<<"\" />"<<endl;
      else   
	sortie<<"\" stroke=\""<<grid_color<<"\"  stroke-dasharray=\""
	      <<10*thickness<<","<<10*thickness
	      <<"\" stroke-width=\""<<thickness<<"\" />"<<endl;
    }

    for (i=(int) i_min_y; i*dy<gnuplot_ymax; i++){
      y=i*dy;
      sortie<<"<line x1=\""<<gnuplot_xmin <<"\" y1=\""<<y <<"\" x2=\""<<gnuplot_xmax <<"\" y2=\""<<y;
      if (i%5==0)
	sortie<<"\" stroke=\""<<grid_color2<<"\"  stroke-width=\""<<2*thickness<<"\" />"<<endl;
      else
	sortie<<"\" stroke=\""<<grid_color<<"\"  stroke-dasharray=\""
	      <<10*thickness<<","<<10*thickness
	      <<"\" stroke-width=\""<<thickness<<"\" />"<<endl;
    }

    //cadre
    sortie<<"<rect stroke=\""<<"black"<<"\"  stroke-width=\""<<2*thickness<<"\" fill=\"none\" x=\""<<gnuplot_xmin<<"\" y=\""<<gnuplot_ymin<<"\" width=\""<<svg_width<<"\" height=\""<<svg_height<<"\" />"<<endl;  
    return sortie.str();
  }

  string svg_grid(){
    double svg_width=gnuplot_xmax-gnuplot_xmin;
    double svg_height=gnuplot_ymax-gnuplot_ymin;
#ifdef HAVE_SSTREAM
    ostringstream sortie;
#else
    ostrstream sortie;
#endif
    string s;

    // 4 rectangles to mask the borders
    double x_scale=(gnuplot_xmax-gnuplot_xmin)/10;
    double y_scale=(gnuplot_ymax-gnuplot_ymin)/10;
    sortie<<"<rect x=\""<<gnuplot_xmin-x_scale<<"\" y=\""<<gnuplot_ymax<<"\" height=\""<<y_scale<<"\" width=\""<<svg_width+2*x_scale<<
      "\" stroke=\"none\" fill=\"white\"/>"<<endl;
    sortie<<"<rect x=\""<<gnuplot_xmin-x_scale<<"\" y=\""<<gnuplot_ymin<<"\" width=\""<<x_scale <<"\" height=\""<<svg_height<<
      "\" stroke=\"none\" fill=\"white\"/>"<<endl;
    sortie<<"<rect x=\""<<gnuplot_xmax<<"\" y=\""<<gnuplot_ymin<<"\" width=\""<<x_scale*2 <<"\" height=\""<<svg_height<<
      "\" stroke=\"none\" fill=\"white\"/>"<<endl;
    sortie<<"<rect x=\""<<gnuplot_xmin-x_scale<<"\" y=\""<<gnuplot_ymin-y_scale<<"\" width=\""<<svg_width+2*x_scale <<
      "\" height=\""<<y_scale <<"\" stroke=\"none\" fill=\"white\"/>\n</g>"<<endl;

    // calibrage des graduations
    int dx,dy;
    svg_dx_dy(svg_width, svg_height, & dx, &dy);
    double x,y;
    int i;
    double i_min_x= gnuplot_xmin/dx;
    double i_min_y= gnuplot_ymin/dy;
    int di_x=1;
    int di_y=1;
    if ((gnuplot_xmax-gnuplot_xmin)/dx>9)
      di_x=5;
    if ((gnuplot_ymax-gnuplot_ymin)/dx>9)
      di_y=5;
    // index des graduations 
    for (i=(int) i_min_x;  i*dx<=gnuplot_xmax ; i=i+di_x){
      x=i*dx;
      sortie<<setprecision(5)<<"<text x=\""<<x<<"\" y=\""<<gnuplot_ymax+0.6*y_scale<<"\" style=\"font-size:"<<0.3*x_scale<<"pt; text-anchor:middle;\">"<<(float)x<<"</text>"<<endl;
    } 
    for (i=(int) i_min_y;  i*dy<=gnuplot_ymax ; i=i+di_y){
      y=i*dy;
      sortie<<setprecision(5)<<"<text x=\""<<gnuplot_xmax+0.2*x_scale<<"\" y=\""<<gnuplot_ymax+gnuplot_ymin-y+0.1*y_scale<<"\" style=\"font-size:"<<0.3*x_scale<<"pt\">"<<(float)y<<"</text>"<<endl;
    }
    return string(sortie.str())+"\n";
  }


  static string color_string(int color){
    switch (color) {
    case 0:
      return "black";
    case 1:
      return "red";
    case 2:
      return "green";
    case 3:
      return "yellow";
    case 4:
      return "blue";
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
 
  static void svg_text(gen A, string legende, int color,GIAC_CONTEXT){
    static gen right_space((gnuplot_xmax-gnuplot_xmin)/50);
    static gen text_size(0.3*(gnuplot_ymax-gnuplot_ymin)/10);
    if (legende=="")
      return;
    // remove " from legende
    int end=legende.size()-1;
    if (legende[0]=='\"')
      legende=legende.substr(1,end-1);
    gen x=re(A,contextptr)-right_space;
    gen y(gnuplot_ymax+gnuplot_ymin-im(A,contextptr));
    if (x==gnuplot_xmax){
      // A traiter, la l??gende sort du cadre
    }
    svg_legend=svg_legend+ "<text  fill=\"" +color_string(color)+"\"  x=\""+x.print(contextptr)+"\" y=\""+y.print(contextptr)
      +"\" style=\"font-size:"+text_size.print(contextptr)+"pt; text-anchor:end;\">"
      +legende
      +"</text>\n";                                      
  }

  static string svg_circle(gen diameter0, gen diameter1, int color, string legende,GIAC_CONTEXT){
    string s="";
    gen center=evalf((diameter0+diameter1)/2,1,contextptr);
    gen radius=evalf(abs(diameter1-diameter0)/2,1,contextptr);
    gen thickness((gnuplot_xmax+gnuplot_ymax-gnuplot_xmin-gnuplot_ymin)/500);
    s=  "<circle stroke=\""+color_string(color)+"\"  stroke-width=\""+thickness.print(contextptr)+"\" fill=\"none\" cx=\""
      +re(center,contextptr).print(contextptr)+"\" cy=\""
      +im(center,contextptr).print(contextptr)+"\" r=\""
      +radius.print(contextptr)
      +"\" />\n";
    if (legende!="")
      svg_text(evalf(center+radius,1,contextptr),legende,color,contextptr);
    return s;
  }


  static string svg_segment(gen A, gen B, int  color, string legende,GIAC_CONTEXT){
    string s;
    gen thickness((gnuplot_xmax+gnuplot_ymax-gnuplot_xmin-gnuplot_ymin)/500);
    A=evalf(A,1,contextptr); B=evalf(B,1,contextptr);
    s= "<line  stroke-width=\""+thickness.print(contextptr)+"\" stroke=\""
      +color_string(color)+"\" x1=\""
      +re(A,contextptr).print(contextptr)+"\" y1=\""
      +im(A,contextptr).print(contextptr)+"\" x2=\""
      +re(B,contextptr).print(contextptr)+"\" y2=\""
      +im(B,contextptr).print(contextptr)+"\"/>\n";
    svg_text(B,legende,color,contextptr);
    return s;
  }

  static string svg_point(gen center, int color, string legende,GIAC_CONTEXT){
    double svg_width=gnuplot_xmax-gnuplot_xmin;
    // double svg_height=gnuplot_ymax-gnuplot_ymin;
    gen i=cst_i;
    gen dx(svg_width/100);
    gen dy(svg_width/100);
    svg_text(center,legende,color,contextptr);
    return svg_segment(center-dx-i*dy,center+dx+i*dy,color,"",contextptr)+
      svg_segment(center-dx+i*dy,center+dx-i*dy,color,"",contextptr);
  }

  static string svg_line(gen A, gen B, int color, string legende,GIAC_CONTEXT){
    gen i=cst_i,xmin(gnuplot_xmin),ymin(gnuplot_ymin),xmax(gnuplot_xmax),ymax(gnuplot_ymax), C,D;
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
    return svg_segment(C,D,color,legende,contextptr);
  }

  static string svg_half_line(gen A, gen B, int color, string legende,GIAC_CONTEXT){
    gen i=cst_i,xmin(gnuplot_xmin),ymin(gnuplot_ymin),xmax(gnuplot_xmax),ymax(gnuplot_ymax), C,D;
    A=evalf(A,1,contextptr); B=evalf(B,1,contextptr);
    // recherche de l'??quation de la droite
    if (is_zero(eval(re(A,contextptr)-re(B,contextptr),eval_level(contextptr),contextptr))){
      gen x=re(A,contextptr);
      if (is_positive(eval(im(B,contextptr)-im(A,contextptr),eval_level(contextptr),contextptr),contextptr))
	C=x+i*ymax;
      else
	C=x+i*ymin;
    } 
    else {
      gen a=eval((im(A,contextptr)-im(B,contextptr))/(re(A,contextptr)-re(B,contextptr)),eval_level(contextptr),contextptr);
      gen b=eval(im(A,contextptr)-a*re(A,contextptr),eval_level(contextptr),contextptr);
      if (is_positive(eval(im(B,contextptr)-im(A,contextptr),eval_level(contextptr),contextptr),contextptr))
	C=xmax+i*(a*xmax+b);
      else
	C=xmin+i*(a*xmin+b); 
    }
    return svg_segment(A,C,color,legende,contextptr);
  }


  static string svg_polyline(gen g,int color, string name,GIAC_CONTEXT){
    string x="x", y="y", s;
    int i;
    gen thickness((gnuplot_xmax+gnuplot_ymax-gnuplot_xmin-gnuplot_ymin)/500);
    g=eval(g,eval_level(contextptr),contextptr);
    vecteur v=*(g._VECTptr);
    if (v[0]==v[v.size()-1])
      s= "<polygon  stroke-width=\""+thickness.print(contextptr)+"\" stroke=\""+color_string(color)+"\" fill=\"none\" points=\"";
    else
      s= "<polyline  stroke-width=\""+thickness.print(contextptr)+"\" stroke=\""+color_string(color)+"\" fill=\"none\" points=\"";
    for (i=0 ; i<signed(v.size())-1 ; i++){
      s=s+re(evalf(v[i],1,contextptr),contextptr).print(contextptr)+" "+im(evalf(v[i],1,contextptr),contextptr).print(contextptr)+", ";
    }
    s=s+re(evalf(v[i],1,contextptr),contextptr).print(contextptr)+" "+im(evalf(v[i],1,contextptr),contextptr).print(contextptr)+"\" /> ";
    svg_text(v[i],name,color,contextptr);
    return s;
  }

  // Trac?? de courbes
  // un point sur 2 sert de point de contr??le
  // on peut sophistiquer
  static string svg_bezier_curve(gen g, int color, string legende,GIAC_CONTEXT){ 
    string x="x", y="y";
    int i;
    gen thickness((gnuplot_xmax-gnuplot_xmin)/250);
    string s= "<path  stroke-width=\""+thickness.print(contextptr)+"\" stroke=\""+color_string(color)+"\" fill=\"none\" d=\"M";
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
      if (is_positive(re(v[i],contextptr)-gnuplot_xmin,contextptr) 
	  && is_positive(im(v[i],contextptr)-gnuplot_ymin,contextptr)
	  && is_positive(gnuplot_xmax-re(v[i],contextptr),contextptr) 
	  && is_positive(gnuplot_ymax-im(v[i],contextptr),contextptr) ){
	svg_text(v[i],legende,color,contextptr);
	break;
      }
    }
    return s;
  }


  static string symbolic2svg(const symbolic & mys,GIAC_CONTEXT);

  //fonction appel??e ssi v est un vecteur
  static string vect2svg(gen v, int color, string name,GIAC_CONTEXT){
    if (v.type != _VECT)
      return "error";
    if (v.subtype==_SYMB){
      if (v[0].type==_VECT){
	return vect2svg(v[0], color, name,contextptr);
      }
      if (v[0].type==_SYMB)
	return symbolic2svg(*v[0]._SYMBptr,contextptr);
    }
    if (v.subtype==_GROUP__VECT)
      return svg_polyline(v, color, name,contextptr);
    if (v.subtype==_LINE__VECT)
      return svg_line(v[0],v[1], color, name,contextptr);
    if (v.subtype==_HALFLINE__VECT)
      return svg_half_line(v[0],v[1], color, name,contextptr);
    return "vect2svg error";
  }


  static string symbolic2svg(const symbolic & mys,GIAC_CONTEXT){ 
   int color=default_color(contextptr);
    string name="";
    if (mys.sommet==at_pnt){ 
      vecteur v=*(mys.feuille._VECTptr);
      if(v.size()==3)
	name=v[2].print(contextptr);
      if (v[0].type==_VECT){
	return vect2svg(v[0], color, name,contextptr);
      }                     
      if (v[0].type==4)           //indispensable, mais je ne sais pas pourquoi
	v[0]=gen(v[0].print(contextptr),contextptr);
      if (v[0].type==_SYMB){ 
	symbolic figure=*v[0]._SYMBptr; 
	if (figure.sommet == at_curve){
	  gen curve=figure.feuille;
	  return svg_bezier_curve(curve[1],color,name,contextptr);
	}
	if (figure.sommet == at_pnt)
	  return symbolic2svg(figure,contextptr);
	if (figure.sommet==at_segment){
	  gen segment=figure.feuille;
	  return svg_segment(segment[0],segment[1], color, name,contextptr); 
	}   
	if (figure.sommet==at_droite ){
	  gen segment=figure.feuille;
	  return svg_line(segment[0],segment[1], color, name,contextptr);
	}
	if (figure.sommet==at_demi_droite ){
	  gen segment=figure.feuille;
	  return svg_half_line(segment[0],segment[1], color, name,contextptr);
	}
	if (figure.sommet==at_cercle ){
	  gen diametre;
	  if (figure.feuille[0].type==_VECT)
	    diametre=figure.feuille[0];
	  else
	    diametre=figure.feuille;
	  return svg_circle(diametre[0],diametre[1], color, name,contextptr);
	}
	return svg_point(v[0], color, name,contextptr); 
      }
      return svg_point(v[0], color, name,contextptr); 
    }
    return "undef";
  }


  string gen2svg(const gen &e,GIAC_CONTEXT){  
    if (e.type== _SYMB)
      return symbolic2svg(*e._SYMBptr,contextptr);
    if (e.type==_VECT){
      string s;
      vecteur v=*e._VECTptr;
      for (int i=0; i<signed(v.size()); i++){
	if (v[i].type==_SYMB){
	  symbolic sym=*v[i]._SYMBptr; 
	  if (sym.sommet==at_pnt)
	    s=s+symbolic2svg(sym,contextptr);
	}
      }
      return s;
    }
    return "error";
  }

  // --------------------- End SVG --------------------


  static string symbolic2mathml(const symbolic & mys, string &svg,GIAC_CONTEXT){

    string opstring(mys.sommet.ptr()->print(contextptr));
    if (opstring!="/" && mys.sommet.ptr()->texprint)  
      return mathml_print(mys,contextptr);
    if (mys.sommet==at_pnt) { 
      svg=svg+symbolic2svg(mys,contextptr);
      return "<mtext>"+mys.print(contextptr)+"</mtext>";
    }
    if ( (mys.feuille.type==_VECT) && (mys.feuille._VECTptr->empty()) )
      return string(provisoire_mbox_begin)+mys.sommet.ptr()->print(contextptr)+string("()")+string(provisoire_mbox_end);
    if ( (mys.feuille.type!=_VECT) || (mys.feuille._VECTptr->front().type==_VECT)){

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
          return "<msup><mrow>"+gen2mathml((*(mys.feuille._VECTptr))[0],contextptr)
        +"</mrow><mrow>"+gen2mathml((*(mys.feuille._VECTptr))[1],contextptr)+"</mrow></msup>";

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
	    s += string("<mo>-</mo><mrow><mo>(</mo>")
	      + gen2mathml(e._SYMBptr->feuille,contextptr)+
	      string("<mo>)</mo></mrow>");
	  else
	    s += string("<mo>-</mo><mrow>") + gen2mathml(e._SYMBptr->feuille,contextptr)+string("</mrow>");
	}
	else {
	  if ( ( (e.type==_INT_) || (e.type==_ZINT) ) && (!is_positive(e,contextptr)) )
	    s += "<mo>-</mo><mn>"+(-e).print(contextptr)+"</mn>";
	  else {
	    if (i)
	      s += "<mo>"+opstring+"</mo>";
	    s += gen2mathml(e,contextptr);
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
	    s += "<mfrac><mrow>"+prod_vect2mathml_no_bra(num,contextptr)+"</mrow><mrow>"+prod_vect2mathml_no_bra(den,contextptr)
	      +"</mrow></mfrac><mo>*</mo>"; // A revoir ?  //"} \\* ";
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
      string s_bra="<msup><mfenced open=\"(\" close=\")\"><mrow>"+gen2mathml((*(mys.feuille._VECTptr))[0],contextptr)
    +"</mrow></mfenced><mrow>"+gen2mathml((*(mys.feuille._VECTptr))[1],contextptr)
	+"</mrow></msup>";
      string s_no_bra= "<msup><mrow>"+gen2mathml((*(mys.feuille._VECTptr))[0],contextptr) 
	+"</mrow><mrow>"+gen2mathml((*(mys.feuille._VECTptr))[1],contextptr)+"</mrow></msup>";
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
      else
	return s_no_bra;
    }

    if (opstring=="/") { //at_division non reconnu
      return "<mfrac><mrow>"+gen2mathml((*(mys.feuille._VECTptr))[0],contextptr)
	+"</mrow><mrow>"+gen2mathml((*(mys.feuille._VECTptr))[1],contextptr)+"</mrow></mfrac>";
    } 

    s = string(provisoire_mbox_begin)+opstring + string(provisoire_mbox_end) +"<mrow><mo>(</mo>";
    for (int i=0;;++i){
      s += gen2mathml((*(mys.feuille._VECTptr))[i],contextptr);
      if (i==l-1)
	return s+"<mo>)</mo></mrow>";
      s += ',';
    }
  }




  // assume math mode enabled
  string gen2mathml(const gen &e,GIAC_CONTEXT){
    string svg="";
    return gen2mathml(e, svg,contextptr);
  }

  string gen2mathml(const gen &e, string &svg,GIAC_CONTEXT){
    string part_re="", part_im="<mi>i</mi>";
    if (e.type==_SYMB && e._SYMBptr->sommet==at_sum)
      return mathml_printassum(e,contextptr);
    else if (e.type==_SYMB && e._SYMBptr->sommet==at_abs)
      return mathml_printasabs(e,contextptr);
    else
      switch (e.type){
      case _INT_: case _ZINT:                        
	return "<mn>"+e.print(contextptr)+"</mn>";
      case _DOUBLE_:                        
	if (fabs(e._DOUBLE_val)<1.1e-5)
	  return "<mn>0.0</mn>";
	else
	  return "<mn>"+e.print(contextptr)+"</mn>"; 
      case _REAL:                        
	  return "<mn>"+e.print(contextptr)+"</mn>"; 
      case _CPLX:
	if (!is_zero(re(e,contextptr)))
	    part_re="<mn>"+re(e,contextptr).print(contextptr)+"</mn>";
	if (!is_zero(im(e,contextptr))){
	  if (is_positive(im(e,contextptr),contextptr)){
	    if (!is_zero(re(e,contextptr)))
	      part_re+="<mo>+</mo>";
	  }
	  else
	    part_re+="<mo>-</mo>";
	}
	if (is_zero(im(e,contextptr)))
	  part_im="";
	if (!is_one(-im(e,contextptr)) && ! is_one(im(e,contextptr)))
	  part_im="<mn>"+abs(im(e,contextptr),contextptr).print(contextptr)+"</mn>"+part_im;	
      	return part_re+part_im;
      case _IDNT:                        
	if (e==unsigned_inf)
	  return "<mn>&infin;</mn>";
	if (e==cst_pi)
	  return "<mi>&pi;</mi>";
	if (e==undef)
      return "<mi>undef</mi>";
	return  "<mi>"+e.print(contextptr)+"</mi>";
      case _SYMB:                        
	return symbolic2mathml(*e._SYMBptr, svg,contextptr);
      case _VECT:                        
	if (e.subtype==_SPREAD__VECT)
	  return spread2mathml(*e._VECTptr,1,contextptr); //----------------v??rifier le 2??me param??tre
	if (e.subtype!=_SEQ__VECT && ckmatrix(*e._VECTptr))
	  return matrix2mathml(*e._VECTptr,contextptr);
    else return _VECT2mathml(*e._VECTptr,e.subtype, svg,contextptr);
      case _POLY:
	return string("<mi>polynome</mi>");
      case _FRAC:                        
	return string("<mfrac><mrow>")+gen2mathml(e._FRACptr->num,contextptr)+"</mrow><mrow>"
	  +gen2mathml(e._FRACptr->den,contextptr)+"</mrow></mfrac>";
      case _EXT: 
	return "";
      case _STRNG:
	return "<mi>"+(*e._STRNGptr)+"</mi>";
      case _FUNC: case _MAP:
	return "<mi>"+e.print(contextptr)+"</mi>";
      case _USER:
	return "<mi>"+e._USERptr->texprint(contextptr)+"</mi>"; // <--------------------------- A traduire ?
      case _MOD:
	return gen2mathml(*e._MODptr,contextptr)+"<mo>%</mo>"+gen2mathml(*(e._MODptr+1),contextptr);
      default:
	settypeerr(gettext("MathMl convert ")+e.print(contextptr));
      }
    return "mathml error (gen2mathml)";
  }



  static string gen2mathmlfull(const gen & g,GIAC_CONTEXT){
    return string(mathml_preamble)+"\n<math mode=\"display\" xmlns=\"http://www.w3.org/1998/Math/MathML\">\n\n"+gen2mathml(g,contextptr)+"\n\n</math><br/>\n"+mathml_end+'\n';
  }
  gen _mathml(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (g.type==_VECT && g.subtype==_SEQ__VECT && g._VECTptr->size()>1 && (*g._VECTptr)[1].type==_STRNG){
      ofstream of((*g._VECTptr)[1]._STRNGptr->c_str());
      of << gen2mathmlfull(g._VECTptr->front(),contextptr) << endl;
      return plus_one;
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
