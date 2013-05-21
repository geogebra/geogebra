// -*- mode:C++ ; compile-command: "g++-3.4 -I.. -g -c tex.cc" -*-
#include "giacPCH.h"

/*
 *  Copyright (C) 2002,7 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
 *  Figure printing adapted from eukleides (c) 2002, Christian Obrecht
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
//#include <fcntl.h>
#include <stdlib.h>
#include <stdio.h>
using namespace std;
#include "gen.h"
#include "gausspol.h"
#include "identificateur.h"
#include "symbolic.h"
#include "poly.h"
#include "usual.h"
#include "tex.h"
#include "prog.h"
#include "rpn.h"
#include "plot.h"
#include "giacintl.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC

  // find if a*x+b*y=c intersects the rectangle xmin..xmax,ymin..ymax
  // if true set x0,y0 and x1,y1 to the intersections
  bool is_clipped(double a,double xmin,double xmax,double b,double ymin,double ymax,double c,double & x0,double &y0,double & x1,double &y1){
    int found=0;
    // find the intersects with x=xmin/x=xmax
    double x,y;
    // test whether a or b is too small
    double theta=std::atan2(b,a);
    if (std::abs(M_PI/2-std::abs(theta))<1e-3){
      // line y=cst
      x0=xmin;
      x1=xmax;
      y0=y1=c/b;
      return y0>=ymin && y0<=ymax;
    }
    if (std::abs(theta)<1e-3 || (M_PI-std::abs(theta))<1e-3){
      // line x=cst
      y0=ymin;
      y1=ymax;
      x0=x1=c/a;
      return x0>=xmin && x0<=xmax;
    }
    y=(c-a*xmin)/b;
    if (y>=ymin && y<=ymax){
      ++found;
      x0=xmin;
      y0=y;
    }
    y=(c-a*xmax)/b;
    if (y>=ymin && y<=ymax){
      if (found){
	x1=xmax;
	y1=y;
	return true;
      }
      ++found;
      x0=xmax;
      y0=y;
    }
    x=(c-b*ymin)/a;
    if (x>=xmin && x<=xmax){
      if (found){
	x1=x;
	y1=ymin;
	return true;
      }
      ++found;
      x0=x;
      y0=ymin;
    }
    x=(c-b*ymax)/a;
    if (x>=xmin && x<=xmax){
      if (found){
	x1=x;
	y1=ymax;
	return true;
      }
      ++found;
      x0=x;
      y0=ymax;
    }
    return false;
  }

  bool in_rectangle(double ax,double ay,double xmin,double ymin,double xmax,double ymax){
    return (ax>=xmin && ax<=xmax && ay>=ymin && ay<=ymax);
  }

  // clip line or segment or halfline, result in xa,ya xb,yb
  // mode can be _LINE__VECT (line), _HALFLINE__VECT (halfline)
  // anything else is considered to be a segment
  // return true if there is something to draw
  bool clip_line(double x1,double y1,double x2,double y2,double xmin,double ymin,double xmax,double ymax,int mode,double & xa,double & ya,double & xb,double & yb){
    bool in1=in_rectangle(x1,y1,xmin,ymin,xmax,ymax);
    bool in2=in_rectangle(x2,y2,xmin,ymin,xmax,ymax);
    if (mode!=_LINE__VECT && mode !=_HALFLINE__VECT && in1 && in2){
      xa=x1; ya=y1; xb=x2; yb=y2;
      return true;
    }
    // Line equation is (y2-y1)*x-(x2-x1)*y=(y2*x1-x2*y1)
    double dy=y2-y1;
    double dx=x2-x1;
    double c=(y2*x1-x2*y1);
    bool a=false,b=false;
    // Find 2 intersections y of x=xmin and x=xmax with line
    // Check if they are inside clip region >=ymin and <=ymax
    if (dx){
      double y=(dy*xmin-c)/dx;
      if (y>=ymin && y<=ymax){
	xa=xmin;
	ya=y;
	a=true;
      }
      y=(dy*xmax-c)/dx;
      if (y>=ymin && y<=ymax){
	if (a){ xb=xmax; yb=y; b=true; } 
	else { xa=xmax; ya=y; a=true; }
      }
    }
    // Find 2 intersections of y=ymin and y=ymax with line
    if (!b && dy){
      double x=(dx*ymin+c)/dy;
      if (x>=xmin && x<=xmax){
	if (a){ xb=x; yb=ymin; b=true; }
	else { xa=x; ya=ymin; a=true;}
      }
      x=(dx*ymax+c)/dy;
      if (x>=xmin && x<=xmax){
	if (a){ xb=x; yb=ymax; b=true; }
	else { xa=x; ya=ymax; a=true;}
      }
    }
    if (a && b){ // if not true there is nothing to draw
      // First case is line, we are done
      if (mode==_LINE__VECT)
	return true;
      if (mode!=_HALFLINE__VECT){ // segment
	// we know that both points are not inside
	if (!in1 && !in2) // both are outside, it's like a line
	  return (xa-xmin)*(xb-xmin)<0;
	if (in1){ // 1 is in, 2 not, find if a is between 1 and 2
	  if ((xa-x1)*(x2-x1)>0 || (ya-y1)*(y2-y1)>0){
	    xb=x1; yb=y1;
	  }
	  else { 
	    xa=x1; ya=y1; 
	  }
	  return true;
	}
	if (in2){ // 2 is in, 1 not, find if a is between 2 and 1
	  if ((xa-x2)*(x1-x2)>0 || (ya-y2)*(y1-y2)>0){
	    xb=x2; yb=y2;
	  }
	  else { 
	    xa=x2; ya=y2; 
	  }
	  return true;
	}
      }
    }
    return false;
  }

  // dimension of the LaTeX output figures default 12 cm x 12 cm
  double horiz_latex=12.;
  double vert_latex=12.;
  const char tex_preamble[]="\\documentclass{article} \n\\usepackage{pst-plot,color} \n\\usepackage{graphicx} \n\\begin{document}\n";
#ifdef RTOS_THREADX
  const char tex_color[]="";
#else
  const char tex_color[]="\\newrgbcolor{fltkcolor0}{0 0 0}\n\\newrgbcolor{fltkcolor1}{0.9961 0 0}\n\\newrgbcolor{fltkcolor2}{0 0.9961 0}\n\\newrgbcolor{fltkcolor3}{0.9961 0.9961 0}\n\\newrgbcolor{fltkcolor4}{0 0 0.9961}\n\\newrgbcolor{fltkcolor5}{0.9961 0 0.9961}\n\\newrgbcolor{fltkcolor6}{0 0.9961 0.9961}\n\\newrgbcolor{fltkcolor7}{0.9961 0.9961 0.9961}\n\\newrgbcolor{fltkcolor8}{0.332 0.332 0.332}\n\\newrgbcolor{fltkcolor9}{0.7734 0.4414 0.4414}\n\\newrgbcolor{fltkcolor10}{0.4414 0.7734 0.4414}\n\\newrgbcolor{fltkcolor11}{0.5547 0.5547 0.2188}\n\\newrgbcolor{fltkcolor12}{0.4414 0.4414 0.7734}\n\\newrgbcolor{fltkcolor13}{0.5547 0.2188 0.5547}\n\\newrgbcolor{fltkcolor14}{0.2188 0.5547 0.5547}\n\\newrgbcolor{fltkcolor15}{0 0 0.5}\n\\newrgbcolor{fltkcolor16}{0.6562 0.6562 0.5938}\n\\newrgbcolor{fltkcolor17}{0.9062 0.9062 0.8438}\n\\newrgbcolor{fltkcolor18}{0.4062 0.4062 0.3438}\n\\newrgbcolor{fltkcolor19}{0.5938 0.6562 0.6562}\n\\newrgbcolor{fltkcolor20}{0.8438 0.9062 0.9062}\n\\newrgbcolor{fltkcolor21}{0.3438 0.4062 0.4062}\n\\newrgbcolor{fltkcolor22}{0.6094 0.6094 0.6562}\n\\newrgbcolor{fltkcolor23}{0.8594 0.8594 0.9062}\n\\newrgbcolor{fltkcolor24}{0.3594 0.3594 0.4062}\n\\newrgbcolor{fltkcolor25}{0.6094 0.6562 0.6094}\n\\newrgbcolor{fltkcolor26}{0.8594 0.9062 0.8594}\n\\newrgbcolor{fltkcolor27}{0.3594 0.4062 0.3594}\n\\newrgbcolor{fltkcolor28}{0.5625 0.5625 0.5625}\n\\newrgbcolor{fltkcolor29}{0.75 0.75 0.75}\n\\newrgbcolor{fltkcolor30}{0.3125 0.3125 0.3125}\n\\newrgbcolor{fltkcolor31}{0.625 0.625 0.625}\n\\newrgbcolor{fltkcolor32}{0 0 0}\n\\newrgbcolor{fltkcolor33}{0.05078 0.05078 0.05078}\n\\newrgbcolor{fltkcolor34}{0.1016 0.1016 0.1016}\n\\newrgbcolor{fltkcolor35}{0.1484 0.1484 0.1484}\n\\newrgbcolor{fltkcolor36}{0.1914 0.1914 0.1914}\n\\newrgbcolor{fltkcolor37}{0.2383 0.2383 0.2383}\n\\newrgbcolor{fltkcolor38}{0.2812 0.2812 0.2812}\n\\newrgbcolor{fltkcolor39}{0.332 0.332 0.332}\n\\newrgbcolor{fltkcolor40}{0.3711 0.3711 0.3711}\n\\newrgbcolor{fltkcolor41}{0.4141 0.4141 0.4141}\n\\newrgbcolor{fltkcolor42}{0.457 0.457 0.457}\n\\newrgbcolor{fltkcolor43}{0.5 0.5 0.5}\n\\newrgbcolor{fltkcolor44}{0.5391 0.5391 0.5391}\n\\newrgbcolor{fltkcolor45}{0.582 0.582 0.582}\n\\newrgbcolor{fltkcolor46}{0.625 0.625 0.625}\n\\newrgbcolor{fltkcolor47}{0.6641 0.6641 0.6641}\n\\newrgbcolor{fltkcolor48}{0.707 0.707 0.707}\n\\newrgbcolor{fltkcolor49}{0.75 0.75 0.75}\n\\newrgbcolor{fltkcolor50}{0.793 0.793 0.793}\n\\newrgbcolor{fltkcolor51}{0.832 0.832 0.832}\n\\newrgbcolor{fltkcolor52}{0.875 0.875 0.875}\n\\newrgbcolor{fltkcolor53}{0.9141 0.9141 0.9141}\n\\newrgbcolor{fltkcolor54}{0.957 0.957 0.957}\n\\newrgbcolor{fltkcolor55}{0.9961 0.9961 0.9961}\n\\newrgbcolor{fltkcolor56}{0 0 0}\n\\newrgbcolor{fltkcolor57}{0 0.1406 0}\n\\newrgbcolor{fltkcolor58}{0 0.2812 0}\n\\newrgbcolor{fltkcolor59}{0 0.4258 0}\n\\newrgbcolor{fltkcolor60}{0 0.5664 0}\n\\newrgbcolor{fltkcolor61}{0 0.7109 0}\n\\newrgbcolor{fltkcolor62}{0 0.8516 0}\n\\newrgbcolor{fltkcolor63}{0 0.9961 0}\n\\newrgbcolor{fltkcolor64}{0.2461 0 0}\n\\newrgbcolor{fltkcolor65}{0.2461 0.1406 0}\n\\newrgbcolor{fltkcolor66}{0.2461 0.2812 0}\n\\newrgbcolor{fltkcolor67}{0.2461 0.4258 0}\n\\newrgbcolor{fltkcolor68}{0.2461 0.5664 0}\n\\newrgbcolor{fltkcolor69}{0.2461 0.7109 0}\n\\newrgbcolor{fltkcolor70}{0.2461 0.8516 0}\n\\newrgbcolor{fltkcolor71}{0.2461 0.9961 0}\n\\newrgbcolor{fltkcolor72}{0.4961 0 0}\n\\newrgbcolor{fltkcolor73}{0.4961 0.1406 0}\n\\newrgbcolor{fltkcolor74}{0.4961 0.2812 0}\n\\newrgbcolor{fltkcolor75}{0.4961 0.4258 0}\n\\newrgbcolor{fltkcolor76}{0.4961 0.5664 0}\n\\newrgbcolor{fltkcolor77}{0.4961 0.7109 0}\n\\newrgbcolor{fltkcolor78}{0.4961 0.8516 0}\n\\newrgbcolor{fltkcolor79}{0.4961 0.9961 0}\n\\newrgbcolor{fltkcolor80}{0.7461 0 0}\n\\newrgbcolor{fltkcolor81}{0.7461 0.1406 0}\n\\newrgbcolor{fltkcolor82}{0.7461 0.2812 0}\n\\newrgbcolor{fltkcolor83}{0.7461 0.4258 0}\n\\newrgbcolor{fltkcolor84}{0.7461 0.5664 0}\n\\newrgbcolor{fltkcolor85}{0.7461 0.7109 0}\n\\newrgbcolor{fltkcolor86}{0.7461 0.8516 0}\n\\newrgbcolor{fltkcolor87}{0.7461 0.9961 0}\n\\newrgbcolor{fltkcolor88}{0.9961 0 0}\n\\newrgbcolor{fltkcolor89}{0.9961 0.1406 0}\n\\newrgbcolor{fltkcolor90}{0.9961 0.2812 0}\n\\newrgbcolor{fltkcolor91}{0.9961 0.4258 0}\n\\newrgbcolor{fltkcolor92}{0.9961 0.5664 0}\n\\newrgbcolor{fltkcolor93}{0.9961 0.7109 0}\n\\newrgbcolor{fltkcolor94}{0.9961 0.8516 0}\n\\newrgbcolor{fltkcolor95}{0.9961 0.9961 0}\n\\newrgbcolor{fltkcolor96}{0 0 0.2461}\n\\newrgbcolor{fltkcolor97}{0 0.1406 0.2461}\n\\newrgbcolor{fltkcolor98}{0 0.2812 0.2461}\n\\newrgbcolor{fltkcolor99}{0 0.4258 0.2461}\n\\newrgbcolor{fltkcolor100}{0 0.5664 0.2461}\n\\newrgbcolor{fltkcolor101}{0 0.7109 0.2461}\n\\newrgbcolor{fltkcolor102}{0 0.8516 0.2461}\n\\newrgbcolor{fltkcolor103}{0 0.9961 0.2461}\n\\newrgbcolor{fltkcolor104}{0.2461 0 0.2461}\n\\newrgbcolor{fltkcolor105}{0.2461 0.1406 0.2461}\n\\newrgbcolor{fltkcolor106}{0.2461 0.2812 0.2461}\n\\newrgbcolor{fltkcolor107}{0.2461 0.4258 0.2461}\n\\newrgbcolor{fltkcolor108}{0.2461 0.5664 0.2461}\n\\newrgbcolor{fltkcolor109}{0.2461 0.7109 0.2461}\n\\newrgbcolor{fltkcolor110}{0.2461 0.8516 0.2461}\n\\newrgbcolor{fltkcolor111}{0.2461 0.9961 0.2461}\n\\newrgbcolor{fltkcolor112}{0.4961 0 0.2461}\n\\newrgbcolor{fltkcolor113}{0.4961 0.1406 0.2461}\n\\newrgbcolor{fltkcolor114}{0.4961 0.2812 0.2461}\n\\newrgbcolor{fltkcolor115}{0.4961 0.4258 0.2461}\n\\newrgbcolor{fltkcolor116}{0.4961 0.5664 0.2461}\n\\newrgbcolor{fltkcolor117}{0.4961 0.7109 0.2461}\n\\newrgbcolor{fltkcolor118}{0.4961 0.8516 0.2461}\n\\newrgbcolor{fltkcolor119}{0.4961 0.9961 0.2461}\n\\newrgbcolor{fltkcolor120}{0.7461 0 0.2461}\n\\newrgbcolor{fltkcolor121}{0.7461 0.1406 0.2461}\n\\newrgbcolor{fltkcolor122}{0.7461 0.2812 0.2461}\n\\newrgbcolor{fltkcolor123}{0.7461 0.4258 0.2461}\n\\newrgbcolor{fltkcolor124}{0.7461 0.5664 0.2461}\n\\newrgbcolor{fltkcolor125}{0.7461 0.7109 0.2461}\n\\newrgbcolor{fltkcolor126}{0.7461 0.8516 0.2461}\n\\newrgbcolor{fltkcolor127}{0.7461 0.9961 0.2461}\n\\newrgbcolor{fltkcolor128}{0.9961 0 0.2461}\n\\newrgbcolor{fltkcolor129}{0.9961 0.1406 0.2461}\n\\newrgbcolor{fltkcolor130}{0.9961 0.2812 0.2461}\n\\newrgbcolor{fltkcolor131}{0.9961 0.4258 0.2461}\n\\newrgbcolor{fltkcolor132}{0.9961 0.5664 0.2461}\n\\newrgbcolor{fltkcolor133}{0.9961 0.7109 0.2461}\n\\newrgbcolor{fltkcolor134}{0.9961 0.8516 0.2461}\n\\newrgbcolor{fltkcolor135}{0.9961 0.9961 0.2461}\n\\newrgbcolor{fltkcolor136}{0 0 0.4961}\n\\newrgbcolor{fltkcolor137}{0 0.1406 0.4961}\n\\newrgbcolor{fltkcolor138}{0 0.2812 0.4961}\n\\newrgbcolor{fltkcolor139}{0 0.4258 0.4961}\n\\newrgbcolor{fltkcolor140}{0 0.5664 0.4961}\n\\newrgbcolor{fltkcolor141}{0 0.7109 0.4961}\n\\newrgbcolor{fltkcolor142}{0 0.8516 0.4961}\n\\newrgbcolor{fltkcolor143}{0 0.9961 0.4961}\n\\newrgbcolor{fltkcolor144}{0.2461 0 0.4961}\n\\newrgbcolor{fltkcolor145}{0.2461 0.1406 0.4961}\n\\newrgbcolor{fltkcolor146}{0.2461 0.2812 0.4961}\n\\newrgbcolor{fltkcolor147}{0.2461 0.4258 0.4961}\n\\newrgbcolor{fltkcolor148}{0.2461 0.5664 0.4961}\n\\newrgbcolor{fltkcolor149}{0.2461 0.7109 0.4961}\n\\newrgbcolor{fltkcolor150}{0.2461 0.8516 0.4961}\n\\newrgbcolor{fltkcolor151}{0.2461 0.9961 0.4961}\n\\newrgbcolor{fltkcolor152}{0.4961 0 0.4961}\n\\newrgbcolor{fltkcolor153}{0.4961 0.1406 0.4961}\n\\newrgbcolor{fltkcolor154}{0.4961 0.2812 0.4961}\n\\newrgbcolor{fltkcolor155}{0.4961 0.4258 0.4961}\n\\newrgbcolor{fltkcolor156}{0.4961 0.5664 0.4961}\n\\newrgbcolor{fltkcolor157}{0.4961 0.7109 0.4961}\n\\newrgbcolor{fltkcolor158}{0.4961 0.8516 0.4961}\n\\newrgbcolor{fltkcolor159}{0.4961 0.9961 0.4961}\n\\newrgbcolor{fltkcolor160}{0.7461 0 0.4961}\n\\newrgbcolor{fltkcolor161}{0.7461 0.1406 0.4961}\n\\newrgbcolor{fltkcolor162}{0.7461 0.2812 0.4961}\n\\newrgbcolor{fltkcolor163}{0.7461 0.4258 0.4961}\n\\newrgbcolor{fltkcolor164}{0.7461 0.5664 0.4961}\n\\newrgbcolor{fltkcolor165}{0.7461 0.7109 0.4961}\n\\newrgbcolor{fltkcolor166}{0.7461 0.8516 0.4961}\n\\newrgbcolor{fltkcolor167}{0.7461 0.9961 0.4961}\n\\newrgbcolor{fltkcolor168}{0.9961 0 0.4961}\n\\newrgbcolor{fltkcolor169}{0.9961 0.1406 0.4961}\n\\newrgbcolor{fltkcolor170}{0.9961 0.2812 0.4961}\n\\newrgbcolor{fltkcolor171}{0.9961 0.4258 0.4961}\n\\newrgbcolor{fltkcolor172}{0.9961 0.5664 0.4961}\n\\newrgbcolor{fltkcolor173}{0.9961 0.7109 0.4961}\n\\newrgbcolor{fltkcolor174}{0.9961 0.8516 0.4961}\n\\newrgbcolor{fltkcolor175}{0.9961 0.9961 0.4961}\n\\newrgbcolor{fltkcolor176}{0 0 0.7461}\n\\newrgbcolor{fltkcolor177}{0 0.1406 0.7461}\n\\newrgbcolor{fltkcolor178}{0 0.2812 0.7461}\n\\newrgbcolor{fltkcolor179}{0 0.4258 0.7461}\n\\newrgbcolor{fltkcolor180}{0 0.5664 0.7461}\n\\newrgbcolor{fltkcolor181}{0 0.7109 0.7461}\n\\newrgbcolor{fltkcolor182}{0 0.8516 0.7461}\n\\newrgbcolor{fltkcolor183}{0 0.9961 0.7461}\n\\newrgbcolor{fltkcolor184}{0.2461 0 0.7461}\n\\newrgbcolor{fltkcolor185}{0.2461 0.1406 0.7461}\n\\newrgbcolor{fltkcolor186}{0.2461 0.2812 0.7461}\n\\newrgbcolor{fltkcolor187}{0.2461 0.4258 0.7461}\n\\newrgbcolor{fltkcolor188}{0.2461 0.5664 0.7461}\n\\newrgbcolor{fltkcolor189}{0.2461 0.7109 0.7461}\n\\newrgbcolor{fltkcolor190}{0.2461 0.8516 0.7461}\n\\newrgbcolor{fltkcolor191}{0.2461 0.9961 0.7461}\n\\newrgbcolor{fltkcolor192}{0.4961 0 0.7461}\n\\newrgbcolor{fltkcolor193}{0.4961 0.1406 0.7461}\n\\newrgbcolor{fltkcolor194}{0.4961 0.2812 0.7461}\n\\newrgbcolor{fltkcolor195}{0.4961 0.4258 0.7461}\n\\newrgbcolor{fltkcolor196}{0.4961 0.5664 0.7461}\n\\newrgbcolor{fltkcolor197}{0.4961 0.7109 0.7461}\n\\newrgbcolor{fltkcolor198}{0.4961 0.8516 0.7461}\n\\newrgbcolor{fltkcolor199}{0.4961 0.9961 0.7461}\n\\newrgbcolor{fltkcolor200}{0.7461 0 0.7461}\n\\newrgbcolor{fltkcolor201}{0.7461 0.1406 0.7461}\n\\newrgbcolor{fltkcolor202}{0.7461 0.2812 0.7461}\n\\newrgbcolor{fltkcolor203}{0.7461 0.4258 0.7461}\n\\newrgbcolor{fltkcolor204}{0.7461 0.5664 0.7461}\n\\newrgbcolor{fltkcolor205}{0.7461 0.7109 0.7461}\n\\newrgbcolor{fltkcolor206}{0.7461 0.8516 0.7461}\n\\newrgbcolor{fltkcolor207}{0.7461 0.9961 0.7461}\n\\newrgbcolor{fltkcolor208}{0.9961 0 0.7461}\n\\newrgbcolor{fltkcolor209}{0.9961 0.1406 0.7461}\n\\newrgbcolor{fltkcolor210}{0.9961 0.2812 0.7461}\n\\newrgbcolor{fltkcolor211}{0.9961 0.4258 0.7461}\n\\newrgbcolor{fltkcolor212}{0.9961 0.5664 0.7461}\n\\newrgbcolor{fltkcolor213}{0.9961 0.7109 0.7461}\n\\newrgbcolor{fltkcolor214}{0.9961 0.8516 0.7461}\n\\newrgbcolor{fltkcolor215}{0.9961 0.9961 0.7461}\n\\newrgbcolor{fltkcolor216}{0 0 0.9961}\n\\newrgbcolor{fltkcolor217}{0 0.1406 0.9961}\n\\newrgbcolor{fltkcolor218}{0 0.2812 0.9961}\n\\newrgbcolor{fltkcolor219}{0 0.4258 0.9961}\n\\newrgbcolor{fltkcolor220}{0 0.5664 0.9961}\n\\newrgbcolor{fltkcolor221}{0 0.7109 0.9961}\n\\newrgbcolor{fltkcolor222}{0 0.8516 0.9961}\n\\newrgbcolor{fltkcolor223}{0 0.9961 0.9961}\n\\newrgbcolor{fltkcolor224}{0.2461 0 0.9961}\n\\newrgbcolor{fltkcolor225}{0.2461 0.1406 0.9961}\n\\newrgbcolor{fltkcolor226}{0.2461 0.2812 0.9961}\n\\newrgbcolor{fltkcolor227}{0.2461 0.4258 0.9961}\n\\newrgbcolor{fltkcolor228}{0.2461 0.5664 0.9961}\n\\newrgbcolor{fltkcolor229}{0.2461 0.7109 0.9961}\n\\newrgbcolor{fltkcolor230}{0.2461 0.8516 0.9961}\n\\newrgbcolor{fltkcolor231}{0.2461 0.9961 0.9961}\n\\newrgbcolor{fltkcolor232}{0.4961 0 0.9961}\n\\newrgbcolor{fltkcolor233}{0.4961 0.1406 0.9961}\n\\newrgbcolor{fltkcolor234}{0.4961 0.2812 0.9961}\n\\newrgbcolor{fltkcolor235}{0.4961 0.4258 0.9961}\n\\newrgbcolor{fltkcolor236}{0.4961 0.5664 0.9961}\n\\newrgbcolor{fltkcolor237}{0.4961 0.7109 0.9961}\n\\newrgbcolor{fltkcolor238}{0.4961 0.8516 0.9961}\n\\newrgbcolor{fltkcolor239}{0.4961 0.9961 0.9961}\n\\newrgbcolor{fltkcolor240}{0.7461 0 0.9961}\n\\newrgbcolor{fltkcolor241}{0.7461 0.1406 0.9961}\n\\newrgbcolor{fltkcolor242}{0.7461 0.2812 0.9961}\n\\newrgbcolor{fltkcolor243}{0.7461 0.4258 0.9961}\n\\newrgbcolor{fltkcolor244}{0.7461 0.5664 0.9961}\n\\newrgbcolor{fltkcolor245}{0.7461 0.7109 0.9961}\n\\newrgbcolor{fltkcolor246}{0.7461 0.8516 0.9961}\n\\newrgbcolor{fltkcolor247}{0.7461 0.9961 0.9961}\n\\newrgbcolor{fltkcolor248}{0.9961 0 0.9961}\n\\newrgbcolor{fltkcolor249}{0.9961 0.1406 0.9961}\n\\newrgbcolor{fltkcolor250}{0.9961 0.2812 0.9961}\n\\newrgbcolor{fltkcolor251}{0.9961 0.4258 0.9961}\n\\newrgbcolor{fltkcolor252}{0.9961 0.5664 0.9961}\n\\newrgbcolor{fltkcolor253}{0.9961 0.7109 0.9961}\n\\newrgbcolor{fltkcolor254}{0.9961 0.8516 0.9961}\n\\newrgbcolor{fltkcolor255}{0.9961 0.9961 0.9961}\n";
#endif
  const char tex_end[]="\n\\end{document}";
  const char mbox_begin[]="\\mathrm{"; // ("\\mbox{");
  const char mbox_end[]="}";

  string spread2tex(const matrice & m,int formule,GIAC_CONTEXT){
    int l=m.size();
    if (!l)
      return "\\mbox{empty_spread}";
    int c=m.front()._VECTptr->size();
    string s("\\ \\mbox{\\begin{tabular}{|");
    for (int j=0;j<=c;++j)
      s += "r|";
    s += "}\\hline\n & ";
    for (int k=0;;){ // write first line
      string tmp;
      int i=k;
      for(int j=0;;++j){
	tmp=char('A'+i%26-(j!=0))+tmp;
	i=i/26;
	if (!i)
	  break;
      }
      s += tmp;
      ++k;
      if (k==c){
	s += " \\\\\\hline\n";
	break;
      }
      else
	s += " & ";
    }
    for (int i=0;i<l;++i){
      s += print_INT_(i)+" & ";
      for (int j=0;j<c;++j){
	if (formule)
	  s += "$"+gen2tex(m[i][j][formule],contextptr)+"$" ;
	else {
	  int save_r=printcell_current_row(contextptr),save_c=printcell_current_col(contextptr);
	  printcell_current_row(contextptr)=i,printcell_current_col(contextptr)=j;
	  string t(m[i][j][0].print(contextptr)),tt;
	  int ll=t.size();
	  for (int l=0;l<ll;++l){
	    if (t[l]!='$')
	      tt += t[l];
	    else
	      tt += "\\$";
	  }
	  s += "{\\tt $"+tt+"$}";
	  printcell_current_row(contextptr)=save_r;printcell_current_col(contextptr)=save_c;
	}
	if (j!=c-1)
	  s += " & ";
      }
      if (i!=l-1){
	if (i%5==4)
	  s += " \\\\\\hline";
	else
	  s += " \\\\";
      }
      else
	s += " \\\\\\hline";
      s+='\n';
    }
    s += "\\end{tabular} } ";
    return s;
  }

  static string matrix2tex(const matrice & m,GIAC_CONTEXT){
    int l=m.size();
    if (!l)
      return string("()");
    int c=m.front()._VECTptr->size();
    string s("\\left(\\begin{array}{");
    for (int j=0;j<c;++j)
      s += 'c';
    s += "}\n";
    for (int i=0;i<l;++i){
      for (int j=0;j<c;++j){
	  s += gen2tex(m[i][j],contextptr) ;
	  if (j!=c-1)
	    s += " & ";
      }
      if (i!=l-1)
	s += " \\\\";
      s+='\n';
    }
    s += "\\end{array}\\right) ";
    return s;
  }

  static string _VECT2tex(const vecteur & v,int subtype,GIAC_CONTEXT){
    string s(begin_VECT_string(subtype,true,contextptr));
    vecteur::const_iterator it=v.begin(),itend=v.end();
    for (;it!=itend;){
      s += gen2tex(*it,contextptr);
      ++it;
      if (it!=itend)
	s += ',';
    }
    s += end_VECT_string(subtype,true,contextptr);
    return s;
  }

  static string prod_vect2tex(const vecteur & v,GIAC_CONTEXT){
    if (v.empty())
      return "1";
    vecteur::const_iterator it=v.begin(),itend=v.end();
    string s;
    for (;;){
      if ( (it->type==_CPLX && !is_zero(*it->_CPLXptr) && !is_zero(*(it->_CPLXptr+1))) || (it->type==_SYMB && ( it->_SYMBptr->sommet==at_plus || (it->_SYMBptr->sommet==at_neg && need_parenthesis(it->_SYMBptr->feuille)) ) ) )
	s += string("(")+gen2tex(*it,contextptr)+string(")");
      else 
	s += gen2tex(*it,contextptr);
      ++it;
      if (it==itend)
	return s;
      if (it->type<=_IDNT || (it->type==_SYMB && 
			      (it->_SYMBptr->sommet==at_neg || (it->_SYMBptr->sommet.ptr()->printsommet && it->_SYMBptr->feuille.type==_VECT && !it->_SYMBptr->feuille._VECTptr->empty() && it->_SYMBptr->feuille._VECTptr->front().type<_IDNT))
			      )) // second part of the test added for e.g. latex('2*3*5^2')
	s += "\\cdot ";
      else
	s += " ";
    }
  }

  string translate_underscore(const string & s){
    string res;
    string::const_iterator it=s.begin(),itend=s.end();
    for (;it!=itend;++it){
      switch (*it){
      case '_':
	res += "\\_";
	break;
      case '^':
	res += "{\\tt\\symbol{94}}";
	break;
      case '~':
	res += "{\\tt\\symbol{126}}";
	break;
      case '<':
	res += "{\\tt\\symbol{60}}";
	break;
      case '>':
	res += "{\\tt\\symbol{62}}";
	break;
      case '\n':
	res += "\\\\\n";
	break;
      case '&':
	res += "\\&";
	break;
      case '{':
	res += "\\{";
	break;
      case '}':
	res += "\\}";
	break;
      case '\\':
	res += "$\\backslash $";
	break;
      case '%':
	res += "\\%";
	break;
      case '#':
	res += "\\#";
	break;
      case '$':
	res += "\\$";
	break;
      default:
	res += *it;
      }
    }
    string s0;
    greek2tex(res,s0,false);
    return s0;
  }

  static string fill_flag(bool flag){
    if (flag)
      return "linestyle=none,fillstyle=solid,";
    else
      return "";
  }

  /*
  static string line_flag(int flag){
    switch(flag) {
    case _STYLE_FULL : return "";
    case _STYLE_DOTTED : return "[linestyle=dotted]";
    case _STYLE_DASHED : return "[linestyle=dashed]";
    }
    return "error";
  }
  */

  static string vector_flag(int flag){
    switch(flag) {
    case _VECTOR__VECT : return "{->}";
      // case BACKARROW : return  "{<-}";
      // case DOUBLEARROW : return "{<->}";
    default : return "";
    }
  }

  static string point_flag(int flag){
    switch(flag) {
    case _STYLE_BOX : return "[dotstyle=square*]"; break;
    case _STYLE_CROSS : return "[dotstyle=x]"; break;
    case _STYLE_PLUS : return "[dotstyle=+]"; break;
    default : return "[dotstyle=*]"; break;
    }
  }

#ifdef GIAC_HAS_STO_38
  static string flcolor2pstrickscolor(int color){
    switch(color) {
    case FL_BLACK: return "black";
    case FL_WHITE: return "white";
    case FL_DARK1: return "darkgray"; 
    case FL_GRAY: return "gray"; 
    }
    return "unknown";
  }
#else

  static string flcolor2pstrickscolor(int color){
    switch(color) {
    case FL_BLACK: return "black";
    case FL_DARK1: return "darkgray"; 
    case FL_GRAY: return "gray"; 
    case FL_LIGHT1  : return "lightgray"; 
#ifdef HAVE_LIBFLTK
    case _WHITE: return "white"; 
#endif
    case FL_WHITE : return "white"; 
#ifdef HAVE_LIBFLTK
    case _RED: return "red"; 
#endif
    case FL_RED	: return "red"; 
#ifdef HAVE_LIBFLTK
    case _GREEN: return "green"; 
#endif
    case FL_GREEN : return "green"; 
#ifdef HAVE_LIBFLTK
    case _BLUE: return "blue"; 
#endif
    case FL_BLUE : return "blue"; 
#ifdef HAVE_LIBFLTK
    case _CYAN: return "cyan"; 
#endif
    case FL_CYAN : return "cyan"; 
#ifdef HAVE_LIBFLTK
    case _MAGENTA: return "magenta"; 
#endif
    case FL_MAGENTA : return "magenta"; 
#ifdef HAVE_LIBFLTK
    case _YELLOW: return "yellow"; 
#endif
    case FL_YELLOW : return "yellow"; 
    default: return "black";
    } 
    return "fltkcolor"+print_INT_(color); 
  }
#endif // GIAC_HAS_STO_38

  static string double2tex(double d){
    char s[32];
	
#if 0 // def BESTA_OS
    assert(0);
    // returning an auto-var, when the string is constructed before the copy?
    // not the niceest of things to do....
    // BP: I don't understand where you see a problem, a string is constructed from s
    // then the std::string is returned
#else
    if (d<=-1e30 || d>=1e30)
      sprintfdouble(s,"%13g",d);
    else
      sprintfdouble(s,"%13.6f",d);
#endif
    return s;
  }

  // convert UTF-8 string to wchar_t *, return adjusted length
  // FIXME: use utf82unicode instead?
  static unsigned int utf8(wchar_t * wline,const char * line,unsigned int n){
    unsigned int i=0,j=0,c;
    for (;i<n;i++){
      c=line[i];
      if ( (c & 0xc0) == 0x80)
	continue;
      if (c < 128){
	wline[j]=c;
	j++;
	continue;
      }
      if ( (c & 0xe0) == 0xc0) {
	i++;
	c = (c & 0x1f) << 6 | (line[i] & 0x3f);
	wline[j]=c;
	j++;
	continue;
      } 
      if ( (c & 0xf0) == 0xe0) {
	i++;
	c = (c & 0x0f) << 6 | (line[i] & 0x3f);
	i++;
	c = c << 6 | (line[i] & 0x3f);
	wline[j]=c;
	j++;
	continue;
      } 
      if ( (c & 0xf1) == 0xf0) {
	i++;
	c = (c & 0x0f) << 6 | (line[i] & 0x3f);
	i++;
	c = c << 6 | (line[i] & 0x3f);
	i++;
	c = c << 6 | (line[i] & 0x3f);
      } else 
	c = 0xfffd;
      wline[j]=c;
      j++;
    }
    wline[j]=0;
    return j;
  }


  int greek2tex(const string & s,string & texs,bool mathmode){
    int n=s.size(),res=0;
    wchar_t * w = new wchar_t[n+1];
    n=utf8(w,s.c_str(),n);
    for (int j=0;j<n;j++){
      switch(w[j]){
      case 0x0386:  texs+="\\'A"; break;
      case 0x0388:  texs+="\\'E"; break;
      case 0x0389:  texs+="\\'H"; break;
      case 0x038A:  texs+="\\'I"; break;
      case 0x038C:  texs+="\\'O"; break;
      case 0x038E:  texs+="\\'Y"; break;
      case 0x038F:  if (!mathmode) texs+="$"; texs+="\\Omega"; texs+=mathmode?' ':'$'; res++; break;
      case 0x0390:  texs+="\'i"; break;
      case 0x0391:  texs+="A"; break;
      case 0x0392:  texs+="B"; break;
      case 0x0393:  if (!mathmode) texs+="$"; texs+="\\Gamma"; texs+=mathmode?' ':'$'; res++; break;
      case 0x0394:  if (!mathmode) texs+="$"; texs+="\\Delta"; texs+=mathmode?' ':'$'; res++; break;
      case 0x0395:  texs+="E"; break;
      case 0x0396:  texs+="Z"; break;
      case 0x0397:  texs+="H"; break;
      case 0x0398:  if (!mathmode) texs+="$"; texs+="\\Eta"; texs+=mathmode?' ':'$'; res++; break;
      case 0x0399:  texs+="I"; break;
      case 0x039A:  texs+="K"; break;
      case 0x039B:  if (!mathmode) texs+="$"; texs+="\\Lambda"; texs+=mathmode?' ':'$'; res++; break;
      case 0x039C:  texs+="M"; break;
      case 0x039D:  texs+="N"; break;
      case 0x039E:  if (!mathmode) texs+="$"; texs+="\\Xi"; texs+=mathmode?' ':'$'; res++; break;
      case 0x039F:  texs+="O"; break;
      case 0x03A0:  if (!mathmode) texs+="$"; texs+="\\Pi"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03A1:  texs+="P"; break;
      case 0x03A3:  if (!mathmode) texs+="$"; texs+="\\Sigma"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03A4:  texs+="T"; break;
      case 0x03A5:  texs+="Y"; break;
      case 0x03A6:  if (!mathmode) texs+="$"; texs+="\\Phi"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03A7:  texs+="X"; break;
      case 0x03A8:  if (!mathmode) texs+="$"; texs+="\\Psi"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03A9:  if (!mathmode) texs+="$"; texs+="\\Omega"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03AA:  texs+="I"; break;
      case 0x03AB:  texs+="Y"; break;
      case 0x03AC:  if (!mathmode) texs+="$"; texs+="\\alpha"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03AD:  if (!mathmode) texs+="$"; texs+="\\epsilon"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03AE:  if (!mathmode) texs+="$"; texs+="\\eta"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03AF:  texs+="i"; res++; break;
      case 0x03B0:  if (!mathmode) texs+="$"; texs+="\\upsilon"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03B1:  if (!mathmode) texs+="$"; texs+="\\alpha"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03B2:  if (!mathmode) texs+="$"; texs+="\\beta"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03B3:  if (!mathmode) texs+="$"; texs+="\\gamma"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03B4:  if (!mathmode) texs+="$"; texs+="\\delta"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03B5:  if (!mathmode) texs+="$"; texs+="\\epsilon"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03B6:  if (!mathmode) texs+="$"; texs+="\\zeta"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03B7:  if (!mathmode) texs+="$"; texs+="\\eta"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03B8:  if (!mathmode) texs+="$"; texs+="\\theta"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03B9:  texs+="i"; res++; break;
      case 0x03BA:  if (!mathmode) texs+="$"; texs+="\\kappa"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03BB:  if (!mathmode) texs+="$"; texs+="\\lambda"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03BC:  if (!mathmode) texs+="$"; texs+="\\mu"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03BD:  if (!mathmode) texs+="$"; texs+="\\nu"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03BE:  if (!mathmode) texs+="$"; texs+="\\xi"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03BF:  if (!mathmode) texs+="$"; texs+="\\omicron"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03C0:  if (!mathmode) texs+="$"; texs+="\\pi"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03C1:  if (!mathmode) texs+="$"; texs+="\\rho"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03C2:  texs+="V"; res++; break;
      case 0x03C3:  if (!mathmode) texs+="$"; texs+="\\sigma"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03C4:  if (!mathmode) texs+="$"; texs+="\\tau"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03C5:  if (!mathmode) texs+="$"; texs+="\\upsilon"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03C6:  if (!mathmode) texs+="$"; texs+="\\varphi"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03C7:  if (!mathmode) texs+="$"; texs+="\\chi"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03C8:  if (!mathmode) texs+="$"; texs+="\\psi"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03C9:  if (!mathmode) texs+="$"; texs+="\\omega"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03CA:  texs+="i"; res++; break;
      case 0x03CB:  if (!mathmode) texs+="$"; texs+="\\upsilon"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03CC:  if (!mathmode) texs+="$"; texs+="\\omicron"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03CD:  if (!mathmode) texs+="$"; texs+="\\upsilon"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03CE:  if (!mathmode) texs+="$"; texs+="\\omega"; texs+=mathmode?' ':'$'; res++; break;
      case 0x03D1:  texs+="J"; res++; break;
      case 0x03D2:  texs+="j"; res++; break;
      case 0x03D6:  texs+="v"; res++; break;
      default: texs+=(w[j] & 0xff);
      }
    }
    delete [] w;
    return res;
  }


  static string idnt2tex(const string & sorig,bool & mathmode){
    string s0;
    mathmode=greek2tex(sorig,s0,true)!=0;
    if (mathmode)
      return s0;
    mathmode=true;
    int n=s0.size(),j;
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
	return "\\"+s+sadd;
      if (s=="im")
	return "\\Im"+s+sadd;
      if (s=="re")
	return "\\Re"+sadd;
      break;
    case 3:
      if (s=="chi" || s=="phi" || s=="Phi" || s=="eta" || s=="rho" || s=="tau" || s=="psi" || s=="Psi")
	return "\\"+s+sadd;
      break;
    case 4:
      if (s=="beta" || s=="zeta")
	return "\\"+s+sadd;
      break;
    case 5:
      if (s=="alpha" || s=="delta" || s=="Delta" || s=="gamma" || s=="Gamma" || s=="kappa" || s=="theta" || s=="Theta" || s=="sigma" || s=="Sigma" || s=="Omega" || s=="omega" || s=="aleph")
	return "\\"+s+sadd;      
      break;
    case 6:
      if (s=="lambda" || s=="Lambda" || s=="approx")
	return "\\"+s+sadd;      
      break;
    case 7:
      if (s=="epsilon" || s=="product")
	return "\\"+s+sadd;      
      break;
    }
    mathmode=false;
    return s0;
  }

  static string idnt2tex(const string & e){
    bool mathmode;
    if (e.size()==3 && (e=="sin" || e=="cos" || e=="tan" || e=="exp" || e=="log"))
      return "\\"+e;
    if (e.size()==2 && (e=="ln"))
      return "\\"+e;
    string s=idnt2tex(e,mathmode);
    if (mathmode || s.size()==1)
      return s;
    else
      return mbox_begin+translate_underscore(s)+mbox_end;
  }

  static string idnt2tex(const gen & e,GIAC_CONTEXT){
    if (e==unsigned_inf)
      return "\\infty ";
    if (e==cst_pi)
      return "\\pi ";
    if (e==undef)
      return "\\,"+(mbox_begin+string("undef")+mbox_end)+"\\,";
    return idnt2tex(e.print(contextptr));
  }

  static void pnt2texlegende(FILE * file,const vecteur & arg,double xd,double yd,double xunit,int couleur,int labelpos,string * sres){
    // draw legende at xd,yd + 
    string ss;
    if (arg[2].type==_STRNG)
      ss=*arg[2]._STRNGptr;
    else
      ss=arg[2].print(context0);
    bool mathmode;
    ss=idnt2tex(ss,mathmode);
    if (mathmode)
      ss="$"+ss+"$";
    ss="\\color{"+flcolor2pstrickscolor(couleur) +"} "+ss;
    double labelsep(0.1/xunit),angle(45+labelpos*90);
    if (file)
      fprintf(file,"\\uput{%.4f}[%.4f](%.4f,%.4f){%s}\n",labelsep,angle,xd,yd, ss.c_str());
    if (sres)
      *sres += "\\uput{"+double2tex(labelsep)+"}["+double2tex(angle)+"]("+double2tex(xd)+","+double2tex(yd)+"){"+ss+"}\n";
  }

  // This function is an adaptation of drawing.c from eukleides by C. Obrecht
  // v is the output history
  // output is written to file or to sres if file==NULL
  static bool pnt2tex(FILE * file,const vecteur & v,double xmin,double ymin,double xmax,double ymax,double & xd,double & yd,bool & drawlegende, int & labelpos,int & couleur,string * sres,GIAC_CONTEXT){
    drawlegende=true;
    int s=v.size();
    if (!s)
      return false;
    // set color
    int ensemble_attributs=0;
    if (s>=2){
      if (v[1].type==_INT_) 
	ensemble_attributs=v[1].val;
      if (v[1].type==_VECT){
	vecteur & w=*v[1]._VECTptr;
	if (!w.empty() && (w.front().type==_INT_) )
	  ensemble_attributs=w.front().val;
      }
    }
    int width           =(ensemble_attributs & 0x00070000) >> 16; // 3 bits
    // FIXME should be used!
    // int epaisseur_point =(ensemble_attributs & 0x00380000) >> 19; // 3 bits
    // int type_line       =(ensemble_attributs & 0x01c00000) >> 22; // 3 bits
    int type_point      =(ensemble_attributs & 0x0e000000) >> 25; // 3 bits
    labelpos        =(ensemble_attributs & 0x30000000) >> 28; // 2 bits
    bool fill_polygon   =((ensemble_attributs & 0x40000000) >> 30)!=0;
    couleur         =(ensemble_attributs & 0x0000ffff);
    string col(flcolor2pstrickscolor(couleur));
    ++width;
    if (file)
      fprintf(file,"\\psset{linecolor=%s}\n\\psset{linewidth=%.4fpt}\n", col.c_str(),width*0.5);
    if (sres)
      *sres += "\\psset{linecolor="+col+"}\n\\psset{linewidth="+double2tex(width*0.5)+"pt}\n";
    // draw point/line/parametric
    gen point=v[0],x,y;
    // point is either a symb that evals to a complex or a param curve
    // or a vector of complex points
    // next line should be replaced by postscript (rpn) translation of curve
    // equation if possible
    if ( (point.type==_SYMB) && (point._SYMBptr->sommet==at_curve) && (point._SYMBptr->feuille.type==_VECT) && (point._SYMBptr->feuille._VECTptr->size()) )
      point=point._SYMBptr->feuille._VECTptr->back();
    if ( (point.type==_SYMB) && (point._SYMBptr->sommet==at_cercle)){
      vecteur v=*point._SYMBptr->feuille._VECTptr;
      gen diametre=remove_at_pnt(v[0]);
      gen e1=diametre._VECTptr->front().evalf(1,contextptr),e2=diametre._VECTptr->back().evalf(1,contextptr);
      gen centre=rdiv(e1+e2,gen(2.0),contextptr),rayon=abs(rdiv(e2-e1,gen(2.0),contextptr),contextptr);
      x=evalf_double(re(centre,contextptr),1,contextptr);
      y=evalf_double(im(centre,contextptr),1,contextptr);
      if ( (x.type==_DOUBLE_) && (y.type==_DOUBLE_) && (rayon.type==_DOUBLE_) ){
	xd=x._DOUBLE_val;
	yd=y._DOUBLE_val;
	if (v.size()>2){
	  gen theta1=evalf_double(v[1],1,contextptr),theta2=evalf_double(v[2],1,contextptr);
	  bool rad=angle_radian(contextptr);
	  angle_radian(true,contextptr);
	  gen angle=arg(e2-e1,contextptr).evalf_double(1,contextptr);
	  angle_radian(rad,contextptr);
	  if (theta1.type==_DOUBLE_ && theta2.type==_DOUBLE_ && angle.type==_DOUBLE_){
	    double t1=theta1._DOUBLE_val,t2=theta2._DOUBLE_val;
	    if (t1>t2){
	      double tmp=t1;
	      t1=t2;
	      t2=tmp;
	    }
	    t1=(t1+angle._DOUBLE_val)*rad2deg_d;
	    t2=(t2+angle._DOUBLE_val)*rad2deg_d;
	    if (file)
	      fprintf (file,"\\pswedge[%sfillcolor=%s](%.4f,%.4f){%.4f}{%.4f}{%.4f}\n",fill_flag(fill_polygon).c_str(),col.c_str(),xd,yd,rayon._DOUBLE_val,t1,t2); // was psarc
	    if (sres)
	      *sres += "\\pswedge[" + fill_flag(fill_polygon) + 
		"fillcolor=" + col + "](" + 
		double2tex(xd)+","+double2tex(yd)+"){" + 
		double2tex(rayon._DOUBLE_val) + "}{" + 
		double2tex(t1)+ "}{" + double2tex(t2) + "}\n";
	    xd=x._DOUBLE_val+rayon._DOUBLE_val*std::cos(t1);
	    yd=y._DOUBLE_val+rayon._DOUBLE_val*std::sin(t1);
	    return true;
	  }
	}
	if (file)
	  fprintf(file,"\\pscircle[%sfillcolor=%s](%.4f,%.4f){%.4f}\n",fill_flag(fill_polygon).c_str(),col.c_str(),xd,yd,rayon._DOUBLE_val);
	if (sres)
	  *sres += "\\pscircle["+fill_flag(fill_polygon)+"fillcolor="+col+
	    "]("+ double2tex(xd)+","+double2tex(yd)+"){"+
	    double2tex(rayon._DOUBLE_val)+"}\n";
	xd=x._DOUBLE_val+rayon._DOUBLE_val;
	return true;
      }
      return false;
    } // end cercle
    if (point.type!=_VECT){ // single point
      point=evalf_double(point,1,contextptr);
      x=re(point,contextptr);
      y=im(point,contextptr).evalf(1,contextptr);
      if ( (x.type==_DOUBLE_) && (y.type==_DOUBLE_) ){
	xd=x._DOUBLE_val;
	yd=y._DOUBLE_val;
	if (file)
	  fprintf(file,"\\psdots%s(%.4f,%.4f)\n", point_flag(type_point).c_str(), xd, yd);
	if (sres)
	  *sres += "\\psdots"+point_flag(type_point)+"("+double2tex(xd)+","+double2tex(yd)+")\n";
	return true;
      }
      return false;
    }
    vecteur & w=*point._VECTptr;
    int ws=w.size();
    if (ws==2){ 
      gen A=w[0].evalf(1,contextptr),B=w[1].evalf(1,contextptr);
      gen Ax=re(A,contextptr),Ay=im(A,contextptr).evalf(1,contextptr),
	Bx=re(B,contextptr),By=im(B,contextptr).evalf(1,contextptr);
      if ( (Ax.type==_DOUBLE_) && (Ay.type==_DOUBLE_) 
	   && (Bx.type==_DOUBLE_) && (By.type==_DOUBLE_) ){
	double ax=Ax._DOUBLE_val,ay=Ay._DOUBLE_val,bx=Bx._DOUBLE_val,by=By._DOUBLE_val;
	xd=(ax+bx)/2.;
	yd=(ay+by)/2.;
 	if (point.subtype==_GROUP__VECT)
	  drawlegende=false;
	double x1,y1,x2,y2;
	if (point.subtype!=_HALFLINE__VECT){
	  clip_line(ax,ay,bx,by,xmin,ymin,xmax,ymax,point.subtype,x1,y1,x2,y2);
	  if (file)
	    fprintf(file,"\\psline%s(%.4f,%.4f)(%.4f,%.4f)\n",vector_flag(point.subtype).c_str(), x1,y1,x2,y2);	
	  if (sres)
	    *sres += "\\psline"+vector_flag(point.subtype)+
	      "("+double2tex(x1)+","+double2tex(y1)+")("+
	      double2tex(x2)+","+double2tex(y2)+")\n";
	  return true;
	}
	double vx=bx-ax,vy=by-ay;
	// check for line/halfline
	bx=bx+5*vx;
	by=by+5*vy;
	// FIXME: improve clipping!! Removed here
	if (file)
	  fprintf(file,"\\psline%s(%.4f,%.4f)(%.4f,%.4f)\n",vector_flag(point.subtype).c_str(),ax,ay,bx,by);	
	if (sres)
	  *sres += "\\psline"+vector_flag(point.subtype)+"("+double2tex(ax)+","+double2tex(ay)+")("+double2tex(bx)+","+double2tex(by)+")\n";
	return true;
      } // end DOUBLE types
      return false;
    } // end w.size()==2
    // multi-line
    string prefixstr,printstring;
    if (fill_polygon)
      printstring="\\pspolygon["+fill_flag(fill_polygon)+"fillcolor="+col+"]";
    else 
      prefixstr = "\\psline[fillcolor="+col+"]";
    double xprec=0,yprec=0,xa,ya,xb,yb;
    for (int i=0;i<ws;++i){
      x=w[i].evalf(1,contextptr);
      y=im(x,contextptr).evalf(1,contextptr);
      x=re(x,contextptr);
      if ( (x.type!=_DOUBLE_) || (y.type!=_DOUBLE_) )
	continue;
      xd=x._DOUBLE_val;
      yd=y._DOUBLE_val;
      if (fill_polygon){
	if (in_rectangle(xd,yd,xmin,ymin,xmax,ymax))
	  printstring += "("+double2tex(xd)+","+double2tex(yd)+")";
	else {
	  if (i && clip_line(xprec,yprec,xd,yd,xmin,ymin,xmax,ymax,0,xa,ya,xb,yb) ){
	    printstring += "("+double2tex(xa)+","+double2tex(ya)+")";
	    printstring += "("+double2tex(xb)+","+double2tex(yb)+")";
	  }
	}
      }
      else {
	if (i && clip_line(xprec,yprec,xd,yd,xmin,ymin,xmax,ymax,0,xa,ya,xb,yb)){
	  printstring += prefixstr+"("+double2tex(xa)+","+double2tex(ya)+")";
	  printstring += "("+double2tex(xb)+","+double2tex(yb)+")\n";
	}
      }
      xprec=xd;
      yprec=yd;
    }
    printstring += '\n';
    if (file)
      fprintf(file,"%s",printstring.c_str());
    if (sres)
      *sres += printstring;
    return true;
  }

  static void invectpnt2tex(FILE * file,const gen & g,double X1,double X2,double Y1,double Y2,double xunit,double yunit,string * resptr,GIAC_CONTEXT){
    if (g.type==_VECT){
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it)
	invectpnt2tex(file,*it,X1,X2,Y1,Y2,xunit,yunit,resptr,contextptr);
    }
    else {
      if (g.is_symb_of_sommet(at_pnt) && !is3d(g)){
	gen & feu = g._SYMBptr->feuille;
	if (feu.type==_VECT){
	  vecteur & arg=*feu._VECTptr;
	  double xd,yd;
	  bool drawlegende;
	  int labelpos,couleur;
	  if (pnt2tex(file,arg,X1,Y1,X2,Y2,xd,yd,drawlegende,labelpos,couleur,resptr,contextptr)){
	    if (drawlegende && (arg.size()>2)){
	      xunit=(gnuplot_xmax-gnuplot_xmin);
	      pnt2texlegende(file,arg,xd,yd,xunit,couleur,labelpos,resptr);
	    }
	  }
	}
      }
    }
  }

  // evalf_double of a and splt re/im
  void evalfdouble2reim(const gen & a,gen & e,gen & f0,gen & f1,GIAC_CONTEXT){
    if (a.type==_CPLX){
      f0=a._CPLXptr->evalf2double(1,contextptr);
      f1=(a._CPLXptr+1)->evalf2double(1,contextptr);
      if (a._CPLXptr->type==_DOUBLE_ && (a._CPLXptr+1)->type==_DOUBLE_)
	e=a;
      else
	e=gen(f0._DOUBLE_val,f1._DOUBLE_val);
      return ;
    }
#ifndef NO_STDEXCEPT
    try {
#endif
      e=a.evalf_double(1,contextptr); // FIXME? level 1 does not work for non 0 context
#ifndef NO_STDEXCEPT
    } catch (std::runtime_error & error ){
      cerr << error.what() << endl;
    }
#endif
    if (e.type==_CPLX){
      f0=*e._CPLXptr;
      f1=*(e._CPLXptr+1);
    }
    else {
      f0=e;
      f1=0.0;
    }
  }

  static bool in_autoscale(const gen & g,vector<double> & vx,vector<double> & vy,vector<double> & vz,GIAC_CONTEXT){
    if (g.type==_VECT && g.subtype==_POINT__VECT && g._VECTptr->size()==3){
      vecteur v=*evalf_double(g,1,contextptr)._VECTptr;
      if (v[2].type==_CPLX)
	v[2]=abs(v[2],contextptr);
      if (v[0].type==_DOUBLE_ && v[1].type==_DOUBLE_ && v[2].type==_DOUBLE_ ){
	vx.push_back(v[0]._DOUBLE_val);
	vy.push_back(v[1]._DOUBLE_val);
	vz.push_back(v[2]._DOUBLE_val);
	return false;
      }
    }
    if (g.type==_VECT ){
      bool ortho=false;
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it){
	ortho = ortho | in_autoscale(*it,vx,vy,vz,contextptr);
      }
      return ortho;
    }
    if (g.is_symb_of_sommet(at_curve)){
      gen & gf=g._SYMBptr->feuille;
      if (gf.type==_VECT && gf._VECTptr->size()>1){
	in_autoscale((*gf._VECTptr)[1],vx,vy,vz,contextptr);
      }
      return false;
    }
    if (g.is_symb_of_sommet(at_hypersurface)){
      gen & g0=g._SYMBptr->feuille;
      if (g0.type==_VECT && g0._VECTptr->size()>2){
	gen & gf=g0._VECTptr->front();
	if (gf.type==_VECT && gf._VECTptr->size()>4){
	  in_autoscale((*gf._VECTptr)[4],vx,vy,vz,contextptr);
	}
      }	
      return false;
    }
    if (g.is_symb_of_sommet(at_cercle)){
      gen c,r;
      centre_rayon(g,c,r,false,contextptr);
      if (is_zero(r)) r=1;
      vecteur v;
      if (g._SYMBptr->feuille.type==_VECT && g._SYMBptr->feuille._VECTptr->size()>=3){
	v=*g._SYMBptr->feuille._VECTptr;
	gen delta=v[2]-v[1];
	v=makevecteur(c-r*exp(cst_i*(v[1]-0.1*delta),contextptr),
		      c-r*exp(cst_i*v[1],contextptr),
		      c-r*exp(cst_i*(3*v[1]+v[2])/gen(4),contextptr),
		      c-r*exp(cst_i*(v[1]+v[2])/gen(2),contextptr),
		      c-r*exp(cst_i*(v[1]+3*v[2])/gen(4),contextptr),
		      c-r*exp(cst_i*v[2],contextptr),
		      c-r*exp(cst_i*(v[2]+0.1*delta),contextptr)); 
	// FIXME? centre_rayon returns (a-b)/2 for rayon instead of (b-a)/2...
      }
      else
	v=makevecteur(c-r,c+r,c-cst_i*r,c+cst_i*r);
      in_autoscale(v,vx,vy,vz,contextptr);
      return true;
    }
    // FIXME sphere etc.
    if (g.type!=_VECT){
      gen e,f0,f1;
      evalfdouble2reim(g,e,f0,f1,contextptr);
      if (f0.type==_DOUBLE_ && f1.type==_DOUBLE_){
	vx.push_back(f0._DOUBLE_val);
	vy.push_back(f1._DOUBLE_val);
      }
    }
    return false;
  }

  bool autoscaleg(const gen & g,vector<double> & vx,vector<double> & vy,vector<double> & vz,GIAC_CONTEXT){
    if (g.type==_VECT){
      bool ortho=false;
      const_iterateur it=g._VECTptr->begin(),itend=g._VECTptr->end();
      for (;it!=itend;++it)
	ortho = ortho | autoscaleg(*it,vx,vy,vz,contextptr);
      return ortho;
    }
    if (g.is_symb_of_sommet(at_pnt)){
      gen & gf=g._SYMBptr->feuille;
      if (gf.type==_VECT && !gf._VECTptr->empty()){
	gen & f=gf._VECTptr->front();
	return in_autoscale(f,vx,vy,vz,contextptr);
      }
    }
    return false;
  }

  static void zoom(double &m,double & M,double d){
    double x_center=(M+m)/2;
    double dx=(M-m);
    if (dx==0)
      dx=1;
    dx *= d/2;
    m = x_center - dx;
    M = x_center + dx;
  }

  void autoscaleminmax(vector<double> & v,double & m,double & M){
    int s=v.size();
    if (s>1){    
      sort(v.begin(),v.end());
      m=v[s/10];
      M=v[9*s/10];
      if (2*(M-m)>(v[s-1]-v[0])){
	M=v[s-1];
	m=v[0];
	zoom(m,M,1.1);
      }
      else
	zoom(m,M,1/0.8);
    }
  }

  static string vectpnt2tex(const gen & g,GIAC_CONTEXT){
    string res;
    double X1=gnuplot_xmin,X2=gnuplot_xmax,Y1=gnuplot_ymin,Y2=gnuplot_ymax;
    vector<double> vx,vy,vz;
    autoscaleg(g,vx,vy,vz,contextptr);
    autoscaleminmax(vx,X1,X2);
    autoscaleminmax(vy,Y1,Y2);
    double xunit=giac::horiz_latex/(X2-X1);
    double yunit=(X2-X1)/(Y2-Y1)*xunit;
    res="\\begin{pspicture}("+double2tex(X1*xunit)+","+double2tex(Y1*xunit)+
      ")("+double2tex(X2*xunit)+","+double2tex(Y2*xunit)+
      ")\n\\psset{unit="+double2tex(xunit)+
      "cm}\n\\psset{linewidth=.5pt}\n\\psset{arrowsize=2pt 4}\n";
    res+="\\psset{linecolor=black}\n";
    if (show_axes(contextptr) && (Y2>=0) && (Y1<=0) ) 
      res+="\\psline[linestyle=dashed]{->}("+double2tex(X1)+","+double2tex(0.0)+")("+double2tex(X2)+","+double2tex(0.0)+")\n";
    if (show_axes(contextptr) && (X2>=0) && (X1<=0) ) 
      res+="\\psline[linestyle=dashed]{->}("+double2tex(0.0)+","+double2tex(Y1)+")("+double2tex(0.0)+","+double2tex(Y2)+")\n";
    invectpnt2tex(0,g,X1,X2,Y1,Y2,xunit,yunit,&res,contextptr);
    res += "\\end{pspicture}\n";
    return res;
  }
   
  static string symbolic2tex(const symbolic & mys,GIAC_CONTEXT){
    const gen &feu=mys.feuille;
    if (mys.sommet==at_pnt && feu.type==_VECT && !is3d(mys))
      return vectpnt2tex(mys,contextptr);
    if (mys.sommet.ptr()->texprint)
      return mys.sommet.ptr()->texprint(feu,mys.sommet.ptr()->s,contextptr);
    string opstring=idnt2tex(mys.sommet.ptr()->print(contextptr));
    if ( (feu.type==_VECT) && (feu._VECTptr->empty()) )
      return opstring+string("()");
    if ( (feu.type!=_VECT) || (feu._VECTptr->front().type==_VECT && mys.sommet!=at_pow) ){
      if ((mys.sommet==at_neg) || (mys.sommet==at_plus)){
	if (feu.type!=_SYMB) 
	  return opstring+gen2tex(feu,contextptr) ;
	if (feu._SYMBptr->sommet==at_inv || !need_parenthesis(feu._SYMBptr->feuille))
	  return opstring+gen2tex(feu,contextptr) ;
	return opstring+string("\\left(") + gen2tex(feu,contextptr) +string("\\right)");
      }
      if (mys.sommet==at_inv && (feu.is_symb_of_sommet(at_prod) || feu.type<=_IDNT) ){
	return string("\\frac{1}{") + gen2tex(feu,contextptr) +string("}");
      }
      return opstring + "\\left(" + gen2tex(feu,contextptr) +"\\right)" ;
    }
    string s;
    int l=feu._VECTptr->size();
    if ( mys.sommet==at_plus ){
      for (int i=0;i<l;++i){
	gen e((*(feu._VECTptr))[i]);
	if ((e.type==_SYMB) && (e._SYMBptr->sommet==at_neg)){
	  s += "-";
	  if (e._SYMBptr->feuille.type!=_CPLX && (e._SYMBptr->feuille.type!=_SYMB || e._SYMBptr->feuille._SYMBptr->sommet==at_inv ||  e._SYMBptr->feuille._SYMBptr->sommet==at_prod|| !need_parenthesis(e._SYMBptr->feuille)) )
	    s += gen2tex(e._SYMBptr->feuille,contextptr) ;
	  else
	    s += "\\left(" + gen2tex(e._SYMBptr->feuille,contextptr) + "\\right)";
	}
	else {
	  if ( ( (e.type==_INT_) || (e.type==_ZINT) ) && (!is_positive(e,0)) )
	    s += e.print(contextptr);
	  else {
	    if (i)
	      s += opstring;
	    s += gen2tex(e,contextptr);
	  }
	}
      } // end_for
      return s;
    }
    if (mys.sommet==at_prod) {
      vecteur num;
      vecteur den;
      for (int i=0;i<l;++i){
	gen e((*(feu._VECTptr))[i]);
	if ( (e.type==_SYMB) && (e._SYMBptr->sommet==at_inv) )
	  den.push_back(e._SYMBptr->feuille);
	else {
	  if (!den.empty()){
	    s += "\\frac{"+prod_vect2tex(num,contextptr)+"}{"+prod_vect2tex(den,contextptr)+"} \\cdot ";
	    num.clear();
	    den.clear();
	  }
	  num.push_back(e);
	}
      }
      if (den.empty())
	return s+prod_vect2tex(num,contextptr);
      return s+"\\frac{"+prod_vect2tex(num,contextptr)+"}{"+prod_vect2tex(den,contextptr)+'}';
    } // end if sommet_is_prod
    if (mys.sommet==at_pow){
      vecteur & v=*feu._VECTptr;
      if ( (v.back()==plus_one_half)  )
	return "\\sqrt{"+gen2tex(v.front(),contextptr)+"}";
      if ( v.back()==minus_one_half || v.back()==fraction(minus_one,plus_two) )
	return "\\frac{1}{\\sqrt{"+gen2tex(v.front(),contextptr)+"}}";
      string res=gen2tex(v.front(),contextptr);
      bool par = v.front().type>=_CPLX && v.front().type!=_IDNT && !ckmatrix(v.front());
      if (par){
	int ress=res.size(),i;
	for (i=1;i<ress;++i){
	  if (res[i]<=32 || !isalpha(res[i]))
	    break;
	}
	if (i+12<ress && res.substr(i,6)=="\\left(" && res.substr(ress-6,6)=="right)")
	  par=false;
      }
      if (par)
	res ="\\left("+res+"\\right)";
      return res+"^{"+gen2tex(v.back(),contextptr)+'}';
    }
    s = opstring +"\\left(";
    for (int i=0;;++i){
      s += gen2tex((*(feu._VECTptr))[i],contextptr);
      if (i==l-1)
	return s+"\\right)";
      s += ',';
    }
  }

  // assume math mode enabled
  string gen2tex(const gen & e,GIAC_CONTEXT){
    switch (e.type){
    case _INT_: case _ZINT: case _REAL:
      return e.print(contextptr);
    case _DOUBLE_:
      if (specialtexprint_double(contextptr))
	return double2tex(e._DOUBLE_val);
      else
	return e.print(contextptr);
    case _CPLX:
      return e.print(contextptr);
    case _IDNT:
      return idnt2tex(e,contextptr);
    case _SYMB:
      return symbolic2tex(*e._SYMBptr,contextptr);
    case _VECT:
      if (e.subtype==_SPREAD__VECT)
	return spread2tex(*e._VECTptr,1,contextptr);
      if (!e._VECTptr->empty() && e._VECTptr->back().is_symb_of_sommet(at_pnt) && !is3d(e._VECTptr->back()) )
	return vectpnt2tex(e,contextptr);
      if (ckmatrix(*e._VECTptr))
	return matrix2tex(*e._VECTptr,contextptr);
      else
	return _VECT2tex(*e._VECTptr,e.subtype,contextptr);
    case _POLY:
      return mbox_begin+string("polynome")+mbox_end+" "; 
    case _FRAC:
      return string("\\frac{")+gen2tex(e._FRACptr->num,contextptr)+"}{"+gen2tex(e._FRACptr->den,contextptr)+'}';
    case _EXT: 
      return "";
    case _STRNG:
      return idnt2tex(*e._STRNGptr);
    case _FUNC:
      return idnt2tex(e.print(contextptr));
    case _USER:
      return e._USERptr->texprint(contextptr);
    case _MOD:
      return gen2tex(*e._MODptr,contextptr)+"\\%"+gen2tex(*(e._MODptr+1),contextptr);
    case _POINTER_:
      if (e.subtype==_FL_WIDGET_POINTER &&fl_widget_texprint_function )
	return fl_widget_texprint_function(e._POINTER_val);
      return "Done";
    default:
      return "Error in Tex conversion for "+e.print(contextptr);
    }
    return 0;
  }
  gen _latex(const gen & g,GIAC_CONTEXT){
    if ( g.type==_STRNG && g.subtype==-1) return  g;
    if (!secure_run && g.type==_VECT && g.subtype==_SEQ__VECT && g._VECTptr->size()>1 && (*g._VECTptr)[1].type==_STRNG){
      ofstream of((*g._VECTptr)[1]._STRNGptr->c_str());
      of << gen2tex(g._VECTptr->front(),contextptr) << endl;
      return plus_one;
    }
    return string2gen(gen2tex(g,contextptr),false);
  }
  static const char _latex_s []="latex";
  static define_unary_function_eval (__latex,&_latex,_latex_s);
  define_unary_function_ptr5( at_latex ,alias_at_latex,&__latex,0,true);

  static const char _TeX_s []="TeX";
  static define_unary_function_eval (__TeX,&_latex,_TeX_s);
  define_unary_function_ptr5( at_TeX ,alias_at_TeX,&__TeX,0,true);



  int graph2tex(FILE * file,const vecteur & v,double X1,double X2,double Y1,double Y2,double Unit,const char * filename,bool logo,GIAC_CONTEXT){
    return graph2tex(file,v,X1,X2,Y1,Y2,Unit,Unit,filename,logo,contextptr);
  }

  static int ingraph2tex(FILE * file,const vecteur & v,double xunit,double yunit,double xmin, double ymin,double xmax,double ymax,GIAC_CONTEXT){
    vecteur w;
    const_iterateur it=v.begin(),itend=v.end();
    for (;it!=itend;++it){
      gen sortie=*it;
      // cerr << "graph2tex " << *it << endl;
      if (sortie.type==_POINTER_ && sortie.subtype==_FL_WIDGET_POINTER && fl_widget_updatepict_function)
	sortie = fl_widget_updatepict_function(sortie);
      if (sortie.type!=_VECT)
	w=vecteur(1,sortie);
      else
	w=*sortie._VECTptr;
      const_iterateur jt=w.begin(),jtend=w.end();
      for (;jt!=jtend;++jt){
	if (jt->type==_VECT)
	  ingraph2tex(file,*jt->_VECTptr,xunit,yunit,xmin,ymin,xmax,ymax,contextptr);
	if ( (jt->type!=_SYMB) || (jt->_SYMBptr->sommet!=at_pnt) )
	  continue;
	gen & g=jt->_SYMBptr->feuille;
	if (g.type!=_VECT)
	  continue;
	vecteur & arg=*g._VECTptr;
	double xd,yd;
	bool drawlegende;
	int labelpos,couleur;
	if (pnt2tex(file,arg,xmin,ymin,xmax,ymax,xd,yd,drawlegende,labelpos,couleur,0,contextptr) && drawlegende && (arg.size()>2))
	  pnt2texlegende(file,arg,xd,yd,xunit,couleur,labelpos,0);
      }
    }
    return 1;
  }

  int graph2tex(FILE * file,const vecteur & v,double X1,double X2,double Y1,double Y2,double xunit,double yunit,const char * filename,bool logo,GIAC_CONTEXT){
    double z(0);
    if (filename)
      fprintf(file,"\n%%file %s",filename);
    fprintf(file,"\n%% Generated by xcas\n\\noindent\n");
    // BUG xunit/yunit do not work for circles, FIXME
    if (xunit!=yunit)
      fprintf(file,"\\begin{pspicture}(%.4f,%.4f)(%.4f,%.4f)\n\\psset{unit=%.4fcm}\n\\psset{xunit=%.4fcm}\n\\psset{yunit=%.4fcm}\n\\psset{linewidth=.5pt}\n\\psset{arrowsize=2pt 4}\n", X1*xunit,Y1*yunit,X2*xunit,Y2*yunit,xunit,xunit,yunit);
    else
      fprintf(file,"\\begin{pspicture}(%.4f,%.4f)(%.4f,%.4f)\n\\psset{unit=%.4fcm}\n\\psset{linewidth=.5pt}\n\\psset{arrowsize=2pt 4}\n", X1*xunit, Y1*xunit, X2*xunit, Y2*xunit,xunit);
    fprintf(file,"\\psset{linecolor=black}\n");
    if (logo){
      // fprintf(file,"\\psframe[fillstyle=solid,fillcolor=gray](%.4f,%.4f)(%.4f,%.4f)\n",X1,Y1,X2,Y2);
      vector<logo_turtle> w=vecteur2turtlevect(v);
      vector<logo_turtle> * turtleptr=&w;
      int l=turtleptr->size();
      if (l>0){
	double labelsep(0.1/xunit),angle(45);
	fprintf(file,"%s{%.4f}[%.4f](%.4f,%.4f){%s%i}\n","%\\uput",labelsep,angle,X2-100,Y2-25, "x ",int(turtleptr->back().x+.5));
	fprintf(file,"%s{%.4f}[%.4f](%.4f,%.4f){%s%i}\n","%\\uput",labelsep,angle,X2-100,Y2-48, "y ",int(turtleptr->back().y+.5));
	fprintf(file,"%s{%.4f}[%.4f](%.4f,%.4f){%s%i}\n","%\\uput",labelsep,angle,X2-100,Y2-65, "t ",int(turtleptr->back().theta+.5));
	logo_turtle prec =(*turtleptr)[0];
	for (int k=1;k<l;++k){
	  logo_turtle current =(*turtleptr)[k];
	  if (!current.s.empty()){ // Write a string
	    // FIXME fl_font(FL_HELVETICA,current.radius);
	    fprintf(file,"\\uput{%.4f}[%.4f](%.4f,%.4f){%s}\n",labelsep,angle,current.x,current.y, current.s.c_str());
	  }
	  else {
	    if (current.color!=prec.color){
	      string col(flcolor2pstrickscolor(current.color));
	      fprintf(file,"\\psset{linecolor=%s}\n", col.c_str());
	    }
	    if (current.radius>0){
	      int r=current.radius & 0x1ff; // bit 0-8
	      double theta1,theta2;
	      if (current.direct){
		theta1=prec.theta+double((current.radius >> 9) & 0x1ff); // bit 9-17
		theta2=prec.theta+double((current.radius >> 18) & 0x1ff); // bit 18-26
	      }
	      else {
		theta1=prec.theta-double((current.radius >> 9) & 0x1ff); // bit 9-17
		theta2=prec.theta-double((current.radius >> 18) & 0x1ff); // bit 18-26
	      }
	      bool rempli=(current.radius >> 27) & 0x1;
	      double angle;
	      double x,y;
	      angle = M_PI/180*(theta2-90);
	      if (current.direct){
		x=current.x - r*std::cos(angle);
		y=current.y - r*std::sin(angle);
	      }
	      else {
		x=current.x + r*std::cos(angle);
		y=current.y + r*std::sin(angle);
	      }
	      if (current.direct){
		if (rempli){
		  fprintf(file,"\\pswedge*[fillcolor=");
		  fprintf(file,"%s",flcolor2pstrickscolor(prec.color).c_str());
		  fprintf(file,"]");
		}
		else
		  fprintf(file,"\\psarc");
		fprintf(file,"(%.4f,%.4f){%.4f}{%.4f}{%.4f}\n",x,y,double(r),theta1-90,theta2-90);
	      }
	      else {
		if (rempli){
		  fprintf(file,"\\pswedge*[fillcolor=");
		  fprintf(file,"%s",flcolor2pstrickscolor(prec.color).c_str());
		  fprintf(file,"]");
		}
		else
		  fprintf(file,"\\psarc");
		fprintf(file,"(%.4f,%.4f){%.4f}{%.4f}{%.4f}\n",x,y,double(r),theta2+90,theta1+90);
	      }
	    } // end radius>0
	    else {
	      if (prec.mark && (prec.x!=current.x || prec.y!=current.y) ){
		fprintf(file,"\\psline(%.4f,%.4f)(%.4f,%.4f)\n", prec.x,prec.y,current.x,current.y);	
	      }
	    }
	    if (current.radius<-1 && k+current.radius>=0){
	      // poly-line from (*turtleptr)[k+current.radius] to (*turtleptr)[k]
	      string printstring("\\pspolygon[linestyle=none,fillstyle=solid,fillcolor="+flcolor2pstrickscolor(prec.color)+"]");
	      for (int i=0;i>=current.radius;--i){
		logo_turtle & t=(*turtleptr)[k+i];
		printstring += "("+double2tex(t.x)+","+double2tex(t.y)+")";
	      }
	      printstring += "("+double2tex(current.x)+","+double2tex(current.y)+")\n";
	      fprintf(file,"%s",printstring.c_str());
	    }
	  } // end else (non-string turtle record)
	  prec=current;
	} // end for (all turtle records)
	logo_turtle & t = (*turtleptr)[l-1];
	double x=t.x,y=t.y;
	double cost=std::cos(t.theta*deg2rad_d);
	double sint=std::sin(t.theta*deg2rad_d);
	double Dx=t.turtle_length*cost/2;
	double Dy=t.turtle_length*sint/2;
	string col(flcolor2pstrickscolor(prec.color));
	fprintf(file,"\\psset{linecolor=%s}\n", col.c_str());
	if (t.visible){
	  fprintf(file,"\\psline(%.4f,%.4f)(%.4f,%.4f)\n", x+Dy, y-Dx, x-Dy, y+Dx);	
	  // fl_line(deltax+x+Dy,deltay+h()-(y-Dx),deltax+x-Dy,deltay+h()-(y+Dx));
	  if (!t.mark){
	    string col(flcolor2pstrickscolor(t.color+1));
	    fprintf(file,"\\psset{linecolor=%s}\n", col.c_str());
	  }
	  fprintf(file,"\\psline(%.4f,%.4f)(%.4f,%.4f)\n", x+Dy, y-Dx, x+3*Dx, y+3*Dy);	
	  fprintf(file,"\\psline(%.4f,%.4f)(%.4f,%.4f)\n", x-Dy, y+Dx, x+3*Dx, y+3*Dy);	
	} // end if t.visible
      } // end if l>0
    } // end if logo
    else {
      if (show_axes(contextptr) && (Y2>=0) && (Y1<=0) ) 
	fprintf(file,"\\psline[linestyle=dashed]{->}(%.4f,%.4f)(%.4f,%.4f)\n",X1,z,X2,z);
      if (show_axes(contextptr) && (X2>=0) && (X1<=0) ) 
	fprintf(file,"\\psline[linestyle=dashed]{->}(%.4f,%.4f)(%.4f,%.4f)\n",z,Y1,z,Y2);
      ingraph2tex(file,v,xunit,yunit,X1,Y1,X2,Y2,contextptr);
    }
    fprintf(file,"\\end{pspicture} \n\n");
    return 1;
  }

  int graph2tex(const string &s,const vecteur & v,double X1,double X2,double Y1,double Y2,double Unit,bool logo,GIAC_CONTEXT){
    return graph2tex(s,v,X1,X2,Y1,Y2,Unit,Unit,logo,contextptr);
  }

  std::string get_path(const string & st){
    int s=st.size(),i;
    for (i=s-1;i>=0;--i){
      if (st[i]=='/')
	break;
    }
    return st.substr(0,i+1);
  }

  std::string remove_path(const std::string & st){
    int s=st.size(),i;
    for (i=s-1;i>=0;--i){
      if (st[i]=='/')
	break;
    }
    return st.substr(i+1,s-i-1);
  }


  int graph2tex(const string &s,const vecteur & v,double X1,double X2,double Y1,double Y2,double xunit,double yunit,bool logo,GIAC_CONTEXT){
    if (is_undef(check_secure()))
      return 0;
    // int file;
    // file = open (s.c_str(), O_WRONLY);
    // if (file==-1)
    //  return file;
    // int savestdout;
    // dup2(STDOUT_FILENO,savestdout);
    // dup2(file,STDOUT_FILENO);
    // Begin eukleides code with redirected stdout
    FILE * filecol=fopen((get_path(s)+"fltkcol.tex").c_str(),"w");
    if (!filecol){
      cerr << "Unable to open color file fltkcol.tex" << endl;
      return 0;
    }
    fprintf(filecol,"%s",tex_color);
    fclose(filecol);
    FILE * file=fopen(s.c_str(), "w");
    if (!file){
      cerr << "Unable to open file "+s << endl;
      return 0;
    }
    fprintf(file,"%s",tex_preamble);
    fprintf(file,"\\input fltkcol.tex");
    graph2tex(file,v,X1,X2,Y1,Y2,xunit,yunit,s.c_str(),logo,contextptr);
    fprintf(file,"\\end{document}\n");
    fclose(file);
    return 1;
    // End eukleides code, restore stdout
    // dup2(savestdout,STDOUT_FILENO);  
  }

  gen _graph2tex(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    int i=erase_pos(contextptr);
    vecteur hist(history_out(contextptr).begin()+i,history_out(contextptr).end());
    return graph2tex(args,hist,contextptr);
  }

  gen graph2tex(const gen & args,const vecteur & hist,GIAC_CONTEXT){
    if (args.type==_STRNG){
      double horiz_unit=horiz_latex/(gnuplot_xmax-gnuplot_xmin);
      double vert_unit=vert_latex/(gnuplot_ymax-gnuplot_ymin);
      double unit=horiz_unit;
      if (horiz_unit>vert_unit)
	unit=vert_unit;
      return graph2tex(*args._STRNGptr,hist,gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,unit,false,contextptr);
    }
    if ( (args.type!=_VECT) || (args._VECTptr->size()<2) )
      return symbolic(at_graph2tex,args);
    vecteur & v=*args._VECTptr;
    int vs=v.size();
    if ( (v[0].type!=_STRNG) || (v[1].type!=_DOUBLE_) )
      return gensizeerr();
    if ( (vs>2) && (v[2].type==_DOUBLE_) )
      gnuplot_xmin=v[2]._DOUBLE_val;
    if ( (vs>3) && (v[3].type==_DOUBLE_) )
      gnuplot_xmax=v[3]._DOUBLE_val;
    if ( (vs>4) && (v[4].type==_DOUBLE_) )
      gnuplot_ymin=v[4]._DOUBLE_val;
    if ( (vs>5) && (v[5].type==_DOUBLE_) )
      gnuplot_ymax=v[5]._DOUBLE_val;
    return graph2tex(*v[0]._STRNGptr,hist,gnuplot_xmin,gnuplot_xmax,gnuplot_ymin,gnuplot_ymax,v[1]._DOUBLE_val,false,contextptr);
  }
  static const char _graph2tex_s []="graph2tex";
  static define_unary_function_eval (__graph2tex,&_graph2tex,_graph2tex_s);
  define_unary_function_ptr5( at_graph2tex ,alias_at_graph2tex,&__graph2tex,0,true);

  gen _graph3d2tex(const gen & args,GIAC_CONTEXT){
    if ( args.type==_STRNG && args.subtype==-1) return  args;
    gen tmp=check_secure();
    if (is_undef(tmp)) return tmp;
    if (args.type==_STRNG){
      string & s=*args._STRNGptr;
#ifdef WITH_GNUPLOT
      bool clrplot;
      int out_handle;
      FILE * gnuplot_out_readstream,* stream = open_gnuplot(clrplot,gnuplot_out_readstream,out_handle);
      latex_replot(stream,s);
      gnuplot_wait(out_handle,gnuplot_out_readstream,5);
#endif
      return string2gen(s,false);
    }
    else { // search in history for the last plot command answer == to an int
      int s=giacmin(history_out(contextptr).size(),history_in(contextptr).size());
      for (int i=s-1;i>=0;--i){
	if (history_out(contextptr)[i].is_symb_of_sommet(at_pnt) && history_out(contextptr)[i].subtype>=0)
	  return history_out(contextptr)[i].subtype;
	if ( history_out(contextptr)[i].type==_INT_ && history_in(contextptr)[i].type==_SYMB && ( equalposcomp(implicittex_plot_sommets,history_in(contextptr)[i]._SYMBptr->sommet) || equalposcomp(notexprint_plot_sommets,history_in(contextptr)[i]._SYMBptr->sommet) ) )
	  return history_out(contextptr)[i];
      }
      return undef;
    }
  }
  static const char _graph3d2tex_s []="graph3d2tex";
  static define_unary_function_eval (__graph3d2tex,&_graph3d2tex,_graph3d2tex_s);
  define_unary_function_ptr5( at_graph3d2tex ,alias_at_graph3d2tex,&__graph3d2tex,0,true);
#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
