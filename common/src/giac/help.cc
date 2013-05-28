// -*- mode:C++ ; compile-command: "g++ -I.. -g -c help.cc -Wall" -*-
#include "giacPCH.h"

#include "path.h"
/*
 *  Copyright (C) 2000,7 B. Parisse, Institut Fourier, 38402 St Martin d'Heres
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
#include <algorithm>
#include "gen.h"
#include "help.h"
#include <iostream>
#include "global.h"

#if defined VISUALC || defined BESTA_OS

#ifndef RTOS_THREADX
#include <io.h>
#endif

#define opendir FindFirstFile
#define readdir FindNextFile
#define closedir FindClose
#define DIR WIN32_FIND_DATA
#define GNUWINCE 1

#else // VISUALC or BESTA_OS

#ifdef HAVE_SYS_PARAM_H
#include <sys/param.h>
#endif

#ifndef BESTA_OS // test should always return true
#include <dirent.h>
#endif

#endif // VISUALC or BESTA_OS

#include "input_lexer.h"

#ifndef NO_NAMESPACE_GIAC
namespace giac {
#endif // ndef NO_NAMESPACE_GIAC
  
  const int HELP_LANGUAGES=4;

  struct static_help_t {
    const char * cmd_name;
    const char * cmd_howto[HELP_LANGUAGES];
    const char * cmd_syntax;
    const char * cmd_related;
    const char * cmd_examples;
  };

  const static_help_t static_help[]={
    //#if !defined RTOS_THREADX && !defined BESTA_OS
#include "static_help.h"
    //#endif
  };

  const int static_help_size=sizeof(static_help)/sizeof(static_help_t);

  inline bool static_help_sort (const static_help_t & a ,const static_help_t & b){
    return strcmp(a.cmd_name, b.cmd_name) < 0;
  }

  // NB: cmd_name may be localized but related is not localized
  bool has_static_help(const char * cmd_name,int lang,const char * & howto,const char * & syntax,const char * & related,const char * & examples){
    const char nullstring[]="";
    if (lang<=0)
      lang=2;
    if (lang>HELP_LANGUAGES)
      lang=2;
    string s=unlocalize(cmd_name);
    int l=s.size();
    if ( (l>2) && (s[0]=='\'') && (s[l-1]=='\'') )
      s=s.substr(1,l-2);
    static_help_t h={s.c_str(),0,0,0,0};
    std::pair<const static_help_t *,const static_help_t *> p=equal_range(static_help,static_help+static_help_size,h,static_help_sort);
    if (p.first!=p.second && p.first!=static_help+static_help_size){
      howto=p.first->cmd_howto[lang-1];
      if (!howto)
	howto=p.first->cmd_howto[1];
      syntax=p.first->cmd_syntax;
      if (!syntax)
	syntax=nullstring;
      related=p.first->cmd_related;
      if (!related)
	related=nullstring;
      examples=p.first->cmd_examples;
      if (!examples)
	examples=nullstring;
      return true;
    }
    return false;
  }

  static std::string output_quote(const string s){
    string res;
    int ss=s.size();
    for (int i=0;i<ss;++i){
      switch (s[i]){
      case '"':
      case '\\':
	res += '\\';
      default:
	res += s[i];
      }
    }
    return res;
  }

  // Run ./icas with export GIAC_DEBUG=-2 to print static_help.h and static_help_w.h
  static bool output_static_help(vector<aide> & v,const vector<int> & langv){
    ofstream of("static_help.h");
    vector<aide>::iterator it=v.begin(),itend=v.end();
    for (;it!=itend;){
      of << "{";
      of << '"' << output_quote(it->cmd_name) << '"' << ",";
      std::vector<localized_string> & blabla = it->blabla;
      sort(blabla.begin(),blabla.end());
      int bs=blabla.size();
      of << "{";
      for (int i=0;i<HELP_LANGUAGES;i++){
	if (i<bs && equalposcomp(langv,i+1))
	  of << '"' << output_quote(blabla[i].chaine) << '"' ;
	else
	  of << 0 ;
	if (i==HELP_LANGUAGES-1)
	  of << '}';
	else
	  of << ',';
      }
      of << "," << '"' << output_quote(it->syntax) << '"' << ',' ;
      std::vector<std::string> & examples = it->examples;
      bs=examples.size();
      if (bs){
	of << '"';
	for (int i=0;i<bs;i++){
	  of << output_quote(examples[i]) ;
	  if (i==bs-1)
	    of << '"';
	  else
	    of << ';';
	} 
      }
      else
	of << 0 ;
      of << "," ;
      std::vector<indexed_string> & related = it->related;
      bs=related.size();
      if (bs){
	of << '"';
	for (int i=0;i<bs;i++){
	  of << output_quote(related[i].chaine) ;
	  if (i==bs-1)
	    of << '"';
	  else
	    of << ',';
	}       
      }
      else
	of << 0;
      of << "}";
      ++it;
      if (it==itend)
	break;
      of << "," << endl;
    }
    of << endl;
    ofstream ofw("static_help_w.h");
    ofstream ofwindex("index_w.h");
    ofwindex << "const TChooseItem index_w[]={" << endl;
    for (it=v.begin();it!=itend;){
      ofw << "{";
      string cmd=it->cmd_name;
      ofw << 'L' << '"' << output_quote(cmd) << '"' << ",";
      if (cmd.size()>16)
	cmd=cmd.substr(0,16);
      ofwindex << "{NULL,NULL, " << 'L' << '"' << output_quote(cmd) << '"' << ", HIDInherit }" ;
      std::vector<localized_string> & blabla = it->blabla;
      sort(blabla.begin(),blabla.end());
      int bs=blabla.size();
      ofw << "{";
      for (int i=0;i<HELP_LANGUAGES;i++){
	if (i<bs && equalposcomp(langv,i+1))
	  ofw << 'L' << '"' << output_quote(blabla[i].chaine) << '"' ;
	else
	  ofw << 0 ;
	if (i==HELP_LANGUAGES-1)
	  ofw << '}';
	else
	  ofw << ',';
      }
      ofw << ",L" << '"' << output_quote(it->cmd_name) << '(' << output_quote(it->syntax) << ')' << '"' << ',' ;
      std::vector<std::string> & examples = it->examples;
      bs=examples.size();
      if (bs>=1){
	ofw << 'L' << '"';
	ofw << output_quote(examples[0]) ;
	ofw << '"' << ',';
	if (bs>=2){
	  ofw << 'L' << '"';
	  ofw << output_quote(examples[1]) ;
	  ofw << '"' << ',';
	}
	else
	  ofw << 0 << ",";
      }
      else
	ofw << 0 << "," << 0 << ",";
      std::vector<indexed_string> & related = it->related;
      bs=related.size();
      if (bs>=1){
	ofw << 'L' << '"';
	ofw << output_quote(related[0].chaine) ;
	ofw << '"' << ',';
	if (bs>=2){
	  ofw << 'L' << '"';
	  ofw << output_quote(related[1].chaine) ;
	  ofw << '"' << ',';
	}
	else
	  ofw << 0 << ",";
      }
      else
	ofw << 0 << "," << 0 << ",";
      ofw << "}";
      ++it;
      if (it==itend)
	break;
      ofw << "," << endl;
      ofwindex << "," << endl;
    }
    ofw << endl;
    ofwindex << "};" << endl;
    return true;
  }

  bool operator < (const indexed_string & is1,const indexed_string & is2){ 
    if (is1.index!=is2.index) return is1.index<is2.index;
    return (is1.chaine<is2.chaine);
  }

  const char default_helpfile[]=giac_aide_location; // help filename
  const int HELP_MAXLENSIZE = 1600; // less than 20 lines of 80 chars

  string printint(int i){
    if (!i)
      return string("0");
    if (i<0)
      return string("-")+printint(-i);      
    int length = (int) std::floor(std::log10((double) i));
#if defined VISUALC || defined BESTA_OS
    char * s =new char[length+2];
#else
    char s[length+2];
#endif
    s[length+1]=0;
    for (;length>-1;--length,i/=10)
      s[length]=i%10+'0';
#if defined VISUALC || defined BESTA_OS
     string res=s;
     delete [] s;
     return res;
#else
    return s;
#endif
  }

  inline int mon_max(int a,int b){
    if (a>b)
      return a;
    else
      return b;
  }

  inline int max(int a,int b,int c){
    if (a>=b){
      if (a>=c)
	return a;
      else
	return c;
    }
    if (b>=c)
      return b;
    else
      return c;
  }

  int score(const string & s,const string & t){
    int ls=s.size(),lt=t.size();
    if (!ls) return -1;
    vector<int> cur_l, new_l(lt+1,0);
    for (int j=0;j<=lt;++j)
      cur_l.push_back(-j);
    vector<int>::iterator newbeg=new_l.begin(),newend=new_l.end(),newit;
    vector<int>::iterator curbeg=cur_l.begin(),curit;//curend=cur_l.end(),
    for (int i=0;i<ls;++i){
      newit=newbeg;
      curit=curbeg;
      int oldres=-i,res;
      for (int j=0;j<lt;++curit,++j){
	*newit=oldres;
	if (s[i]==t[j])
	  res=max(oldres-1,*(curit+1)-1,*curit+3);
	else {
          if (abs(s[i]-t[j])==32)
	    res=max(oldres-1,*(curit+1)-1,*curit+2);
          else
	    res=max(oldres-1,*(curit+1)-1,*curit-2);
	}
	++newit;
	oldres=res;
      }
      *newit=oldres;
      copy(newbeg,newend,curbeg);
    }
    // alignement would be return *newit;
    // we modify the returned value to increase the weight of the first char
    if (!s.empty() && !t.empty()){
      if (s[0]==t[0])
	return * newit+2;
      else
	return *newit-2;
    }
    return *newit;
  }

  bool alpha_order(const aide & a1,const aide & a2){
    string s1 =a1.cmd_name;
    string s2 =a2.cmd_name;
    for (unsigned i=0;i<s1.size();++i) 
      s1[i]=tolower(s1[i]);
    for (unsigned i=0;i<s2.size();++i) 
      s2[i]=tolower(s2[i]);
    if (s1!=s2)
      return s1<s2;
    return a1.cmd_name<= a2.cmd_name;
  }

  static void find_synonymes(const std::string & cmd_name,vector<localized_string> & current_synonymes){
    current_synonymes.clear();
    // parse curren_aide.cmd_name for synonyms
    string s=cmd_name,s1;
    int i;
    for (;;){
      // cout << s << endl;
      i=s.find(' ');
      if (i<=0){
	if (!s.empty())
	  current_synonymes.push_back(localized_string(0,s));
	break;
      }
      s1=s.substr(0,i);
      current_synonymes.push_back(localized_string(0,s1));
      /* add also keyword translations of s1
	 multimap<string,localized_string>::iterator it=back_lexer_localization_map().find(s1),backend=back_lexer_localization_map().end(),itend=back_lexer_localization_map().upper_bound(s1);
	 if (it!=backend){
	 for (;it!=itend;++it){
	 current_synonymes.push_back(it->second);
	 }
	 }
      */
      s=s.substr(i+1,s.size()-i-1);
    } // end for (;;)    
  }
  

  vector<aide> readhelp(const char * f_name,int & count,bool warn){
      vector<aide> v(1);
      readhelp(v,f_name,count,warn);
    return v;
  }
  void readhelp(vector<aide> & v,const char * f_name,int & count,bool warn){
    count=0;
    if (access(f_name,R_OK)){
      if (warn)
	std::cerr << "Help file " << f_name << " not found" << endl;
      return ;
    }
    // v.reserve(1600);
    ifstream f(f_name);
    char fs[HELP_MAXLENSIZE+1];
    vector<localized_string> current_blabla;
    vector<indexed_string> current_related;
    vector<string> current_examples;
    aide current_aide;
    vector<int> vposition;
    int vpositions;
    string current_line;
    vector<localized_string> current_synonymes;
    while (f){
      f.getline(fs,HELP_MAXLENSIZE,'\n');
      if (!fs[0])
	continue;
      current_line=fs;
      if (fs[0]=='#'){
	current_aide.blabla=current_blabla;
	current_aide.examples=current_examples;
	current_aide.related=current_related;
	if (!current_aide.cmd_name.empty()){
	  find_synonymes(current_aide.cmd_name,current_synonymes);
	  current_aide.synonymes=current_synonymes;
	  vector<localized_string>::const_iterator it=current_synonymes.begin(),itend=current_synonymes.end();
	  vpositions=vposition.size();
	  for (int pos=0;it!=itend;++it,++pos){
	    current_aide.cmd_name=it->chaine;
	    if (pos<vpositions)
	      v[vposition[pos]]=current_aide;
	    else
	      v.push_back(current_aide);
	    ++count;
	  }
	} // end if (!current_aide.cmd_name.empty())
	current_blabla.clear();
	current_examples.clear();
	current_related.clear();
	vposition.clear();
	current_aide.cmd_name=current_line.size()>2?current_line.substr(2,current_line.size()-2):"";
	// search if cmd_name is already present in v
	// if so set vposition, current_blabla/examples/related accordingly
	find_synonymes(current_aide.cmd_name,current_synonymes);
	vector<localized_string>::const_iterator itbeg=current_synonymes.begin(),itend=current_synonymes.end(),it;
	vector<aide>::iterator itpos;
	for (it=itbeg;it!=itend;++it){
	  itpos=lower_bound(v.begin(),v.end(),current_aide,alpha_order);
	  if (itpos!=v.begin()){
	    --itpos;
	    if (itpos->cmd_name==it->chaine){ // already documented
	      current_synonymes=itpos->synonymes;
	      current_blabla=itpos->blabla;
	      current_examples=itpos->examples;
	      current_related=itpos->related;
	      vposition.push_back(itpos-v.begin());
	    }
	  }
	}
	continue;
      }
      // look for space
      int l=current_line.find_first_of(' ');
      if ( (l==1) && (current_line[0]=='0') ){
	int cs=current_line.size();
	while (l<cs && current_line[l]==' '){ ++l; }
        current_aide.syntax=current_line.substr(l,cs-l);
        continue;
      }
      int n=0;
      bool positif=true;
      int i=0;
      if (current_line[i]=='-'){
	positif=false;
	++i;
      }
      for (;i<l;++i){
	if ((current_line[i]<'0') || (current_line[i]>'9')){
	  n=0;
	  break;
	}
	else
	  n=10*n+(current_line[i]-int('0'));
      }
      if (!positif)
	n=-n;
      if (n>0)
	current_blabla.push_back(localized_string(n,current_line.substr(l+1,current_line.size()-l)));
      else {
	if (n<0)
	  current_related.push_back(indexed_string(-n,current_line.substr(l+1,current_line.size()-l)));
	else
	  current_examples.push_back(current_line);
      }
    } // end reading help from file
    if (!current_aide.cmd_name.empty()){
      current_aide.synonymes=vector<localized_string>(1,localized_string(0,current_aide.cmd_name));
      current_aide.blabla=current_blabla;
      current_aide.examples=current_examples;
      current_aide.related=current_related;
      v.push_back(current_aide);
      count++;
    }
    sort(v.begin(),v.end(),alpha_order);
    if (debug_infolevel==-2){
      vector<int> langv;
      langv.push_back(1);
      langv.push_back(2);
      // langv.push_back(3);
      // langv.push_back(4);
      output_static_help(v,langv);
    }
  }

  static aide add_synonyme_name_to_examples(const aide & a){
    aide res(a);
    std::vector<std::string>::iterator it=res.examples.begin(),itend=res.examples.end();
    for (;it!=itend;++it){
      if (!it->empty() && (*it)[0]==' ')
	continue;
      // look for a (
      unsigned i=it->find('(');
      if (i>0 && i<it->size()){ // check whether the beginning of the string is in synonyms
	string cmd=it->substr(0,i);
	std::vector<localized_string>::const_iterator jt=res.synonymes.begin(),jtend=res.synonymes.end();
	for (;jt!=jtend;++jt){
	  if (jt->chaine==cmd)
	    break;
	}
	if (jt!=jtend) // Yes, replace it
	  *it=res.cmd_name+it->substr(i,it->size()-i);
	else
	  *it=res.cmd_name+'('+*it+')';
      }
      else
	*it=res.cmd_name+'('+*it+')';
    }
    return res;
  }

  aide helpon(const string & demande,const vector<aide> & v,int language,int count,bool with_op){
    aide result;
    string current(demande);
    if (with_op)
      result.syntax = gettext("No help available for ") +current +"\n";
    else
      result.syntax="NULL";
    if (!count){
      return result;
    }
    for (int i=1;;++i){
      if (i==count){
	if (!with_op)
	  return result;
	// Find closest string
	int best_score=0,cur_score;
	vector<int> best_j;
	for (int j=1;j<count;++j){
	  cur_score=score(current,v[j].cmd_name);
	  if (cur_score>best_score){
	    best_j.clear();
	    best_j.push_back(j);
	    best_score=cur_score;
	    continue;
	  }
	  if (cur_score>=mon_max(best_score-6,0))
	    best_j.push_back(j);
	}
	if (best_score>0){
	  vector<int>::iterator it=best_j.begin(),itend=best_j.end();
	  for (int k=1;(k<10) && (it!=itend);++k,++it)
	    result.related.push_back(indexed_string(k, v[*it].cmd_name));
	}
	result.syntax += gettext("Best match has score ") + printint(best_score) + "\n";
	result.cmd_name = current;
	return result;
      }
      if (current==v[i].cmd_name){
	result=v[i];
	if (!with_op)
	  return add_synonyme_name_to_examples(result);
	result.syntax= current + "(" +result.syntax +")\n";
	return add_synonyme_name_to_examples(result);
      }
    } // end for i
  }

  string writehelp(const aide & cur_aide,int language){
    string result=cur_aide.syntax;
    vector<localized_string>::const_iterator it=cur_aide.blabla.begin(),itend=cur_aide.blabla.end();
    for (;it!=itend;++it){
      if (it->language==language){
	result += it->chaine +'\n' ;
	break;
      }
    }
    vector<indexed_string>::const_iterator iti=cur_aide.related.begin(),itiend=cur_aide.related.end();
    if (itiend!=iti){
      result +=  gettext("See also: ");
      for (;iti!=itiend;++iti){
	result += printint(iti->index) + "/ " + iti->chaine + " ";
      }
      result += '\n' ;
    }
    vector<string>::const_iterator its=cur_aide.examples.begin(),itsend=cur_aide.examples.end();
    for (int i=1;its!=itsend;++its,++i){
      string current = "Ex" + printint(i)+':'+*its ;
      result += current +'\n' ;
      // system(current.c_str());
    }
    return result;
  }

#ifndef RTOS_THREADX
  multimap<string,string> html_mtt,html_mall;
  std::vector<std::string> html_vtt,html_vall;

  // find index nodes in file file
  static bool find_index(const std::string & current_dir,const std::string & file,multimap<std::string,std::string>&mtt,multimap<std::string,std::string>&mall,bool is_index=false,bool warn=false){
    if (access(file.c_str(),R_OK))
      return false;
    ifstream i(file.c_str());
    // Skip navigation panel
#if defined VISUALC || defined BESTA_OS
    char * buf=new char[BUFFER_SIZE+1];
#else
    char buf[BUFFER_SIZE+1];
#endif
    for (;i && !i.eof();){
      i.getline(buf,BUFFER_SIZE,'\n');
      string s(buf);
      if (s=="<!--End of Navigation Panel-->")
	break;
      int t=s.size();
      if (t>24 && s.substr(t-24,24)=="<LI CLASS=\"li-indexenv\">"){
	// hevea file contains index
	for (;i && !i.eof(); ){
	  i.getline(buf,BUFFER_SIZE,'\n');
	  s=buf;
	  t=s.size();
	  if (t>29 && s.substr(0,29)=="</LI><LI CLASS=\"li-indexenv\">"){
	    s=s.substr(29,s.size()-29);
	    t=s.size();
	    if (!t || s[0]=='<') // skip index words with special color/font
	      continue;
	    int endcmd=s.find("<"); // position of end of commandname
	    if (endcmd>2 && endcmd<t){
	      string cmdname=s.substr(0,endcmd-2);
	      s=s.substr(endcmd,t-endcmd); // s has all the links
	      vector<string> hrefs;
	      for (;;){
		t=s.size();
		endcmd=s.find("<A HREF=\"");
		if (endcmd<0 || endcmd+9>=t)
		  break;
		s=s.substr(endcmd+9,s.size()-endcmd-9);
		t=s.size();
		endcmd=s.find("\"");
		if (endcmd<0 || endcmd+2>=t)
		  break;
		string link=s.substr(0,endcmd);
		if (link[0]=='#')
		  link = file + link;
		else
		  link = current_dir + link;
		s=s.substr(endcmd+2,s.size()-endcmd-2);
		t=s.size();
		if (t<3)
		  break;
		if (s.substr(0,3)=="<B>")
		  hrefs.insert(hrefs.begin(),link);
		else
		  hrefs.push_back(link);
	      }
	      vector<string>::const_iterator it=hrefs.begin(),itend=hrefs.end();
	      for (;it!=itend;++it){
		if (it==hrefs.begin())
		  mtt.insert(pair<string,string>(cmdname,*it));
		mall.insert(pair<string,string>(cmdname,*it));
	      }
	    } // if (endcmd>2 && endcmd<t)
	  } // if (t>29 &&...
	} // end of file
#if defined VISUALC || defined BESTA_OS
	delete [] buf;
#endif
	return true;
      }
      if (t>14 && s.substr(t-14,14)=="Index</A></B> "){
	// look in the corresponding index file instead
	int t1=s.find("HREF")+6;
	if (t1>=0 && t1<t-16){
	  s=s.substr(t1,t-16-t1);
	  if (warn)
	    cerr << "Using index " << s << endl;
	  find_index(current_dir,current_dir+s,mtt,mall,true,warn);
#if defined VISUALC || defined BESTA_OS
	  delete [] buf;
#endif
	  return true;
	}
      }
#if defined VISUALC || defined BESTA_OS
      delete [] buf;
#endif
    }
    string tmp;
    char c;
    for (;i && !i.eof();){
      // read i search for a <A word
      i >> tmp;
      int l=tmp.size();
      string tts;
      if (is_index){
	if (l<13)
	  continue;
	int tmpl=0;
	if (tmp.substr(0,8)=="<STRONG>")
	  tmpl=8;
	if (tmp.substr(0,12)=="<DT><STRONG>")
	  tmpl=12;
	if (!tmpl)
	  continue;
	int l1=tmp.find("</STRONG>");
	if (l1<=tmpl || l1>=l)
	  continue;
	tts=tmp.substr(tmpl,l1-tmpl);
      } 
      else {
	if (l<2 || tmp.substr(l-2,2)!="<A")
	  continue;
      }
      // read the link
      tmp="";
      int s=0;
      for (;i && !i.eof();){
	i.get(c);
	++s;
	tmp += c; 
	if (s>4 && tmp.substr(s-4,4)=="<DT>"){
	  // no <B> found, truncate tmp to the first </A> found
	  int l=tmp.find("</A>");
	  if (l<s && l>0)
	    tmp=tmp.substr(0,l);
	  s=tmp.size();
	  break;
	}
	if (s>8 && tmp.substr(s-8,8)=="</B></A>"){
	  // Find backward the first occurence of <A
	  int l=s-8;
	  for (;l>0;--l){
	    if (tmp[l]=='<' && tmp[l+1]=='A')
	      break;
	  }
	  if (l){
	    tmp=tmp.substr(l,s-l);
	    s -= l;
	  }
	  break;
	}
      }
      // cerr << tmp << endl;
      // analysis, search for HREF
      int href=tmp.find("HREF=\"");
      if (href<0 || href+6>=s)
	continue;
      string hrefs(current_dir);
      int hrefend=0;
      for (int j=href+6;j<s;++j){
	if (tmp[j]=='"'){ // remove HREF=
	  hrefend=j+1;
	  break;
	}
	hrefs += tmp[j];
      }
      if (!hrefend)
	continue;
      if (is_index){
	mtt.insert(pair<string,string>(tts,hrefs));
	mall.insert(pair<string,string>(tts,hrefs));
      }
      else {
	// search for TT
	int tt=tmp.find("<TT>"),ttend=tt;
	if (tt>=0 && tt+6<s){
	  for (ttend+=4;ttend<s;++ttend){
	    if (tmp[ttend]=='<'){
	      ttend +=5;
	      break;
	    }
	    tts += tmp[ttend];
	  }
	  mtt.insert(pair<string,string>(tts,hrefs));
	  mall.insert(pair<string,string>(tts,hrefs));
	  tmp=tmp.substr(0,tt)+tmp.substr(ttend,tmp.size()-ttend);
	}
	// add href for all normal words
	s=tmp.size();
	int j=hrefend+1;
	for (;j<s;){
	  // read word
	  int pos=tmp.find(' ',j);
	  if (pos>j && pos<s){
	    // add it
	    string tmpins(tmp.substr(j,pos-j));
	    mall.insert(pair<string,string>(tmpins,hrefs));
	  }
	  if (pos==-1){
	    string tmpins(tmp.substr(j,s-j));
	    mall.insert(pair<string,string>(tmpins,hrefs));
	    break;
	  }
	  j=pos+1;
	}
      } // end else is_index
    }
    return false;
  }

  static const string subdir_strings[]={"cascmd","casgeo","casrouge","cassim","castor","tutoriel","casinter","casexo","cascas"};
  static const int subdir_taille=sizeof(subdir_strings)/sizeof(string);
  static int equalposcomp(const string * tab,const string & s){
    int i=s.size()-1;
    for (;i>=0;--i){
      if (s[i]=='/')
	break;
    }
    ++i;
    string t=s.substr(i,s.size()-i);
    i=t.size()-1;
    for (;i>=0;--i){
      if (t[i]=='_')
	t=t.substr(0,i);
    }
    for (i=0;i<subdir_taille;++i){
      // cerr << *(tab+i) << " " << t << endl;
      if (*(tab+i)==t)
	return i+1;
    }
    return 0;
  }

#if ! (defined VISUALC || defined BESTA_OS)
#ifdef WIN32
  static int dir_select (const struct dirent *d){
    string s(d->d_name);
    // cerr << s << endl;
    int t=s.size();    
    if (s[t-1]=='\\'){
      return s!="." && s!="..";
    }
    if (t<9)
      return 0;
    if (s[t-1]=='l'){
      s=s.substr(0,t-1);
      --t;
    }
    if (t>9)
      s=s.substr(t-9,9);
    return  s=="index.htm";
  }
#else
#if defined(__APPLE__) || ( defined(__FreeBSD_version) && __FreeBSD_version<800501)
  static int dir_select (struct dirent *d){
#else
  static int dir_select (const struct dirent *d){
#endif
    string s(d->d_name);
    if (d->d_type==DT_DIR || equalposcomp(subdir_strings,s)){
      return s!="." && s!="..";
    }
    int t=s.size();
    if (t<9)
      return 0;
    if (s[t-1]=='l'){
      s=s.substr(0,t-1);
      --t;
    }
    if (t>9)
      s=s.substr(t-9,9);
    return  s=="index.htm";
  }
#endif
#endif // visualc

  void find_all_index(const std::string & subdir,multimap<std::string,std::string> & mtt,multimap<std::string,std::string> & mall){
#if defined GNUWINCE || defined __MINGW_H || defined __ANDROID__ || defined EMCC
    return;
#else
    // cerr << "HTML help Scanning " << subdir << endl;
    DIR *dp;
    struct dirent *ep;
    
    dp = opendir (subdir.c_str());
    if (dp != NULL){
      string s;
      int t;
      while ( (ep = readdir (dp)) ){
	s=ep->d_name;
	t=s.size();
	if (t>5 && s.substr(t-4,4)=="html")
	  html_vall.push_back(subdir+s);
      }
      closedir (dp);
    }

    struct dirent **eps;
    int n;
    n = scandir (subdir.c_str(), &eps, dir_select, alphasort);
    if (n >= 0){
      bool index_done=false;
      int cnt;
      for (cnt = -1; cnt < n; ++cnt){
	string s;
	if (cnt==-1)
	  s="index.html";
	else
	  s=eps[cnt]->d_name;
	s= subdir+s;
#ifdef WIN32
        int t=s.size();
        if (s[t-1]=='\\')
	  find_all_index(s+"/",mtt,mall);
        else {
	  if (!index_done)
	    index_done=find_index(subdir,s,mtt,mall);
	}
#else
	unsigned char type=cnt>=0?eps[cnt]->d_type:0;
	if (type==DT_DIR || equalposcomp(subdir_strings,s))
	  find_all_index(s+"/",mtt,mall);
	else {
	  if (!index_done)
	    index_done=find_index(subdir,s,mtt,mall);
	}
#endif
      }
    }
#endif // GNUWINCE
  }

  // Return all HTML nodes refered to s in mtt
  std::vector<std::string> html_help(multimap<std::string,std::string> & mtt,const std::string & s){
    vector<string> v;
    multimap<string,string>::const_iterator it=mtt.lower_bound(s),itend=mtt.upper_bound(s);
    for (;it!=itend;++it){
      v.push_back(it->second);
    }
    return v;
  }

  string xcasroot_dir(const char * arg){
    string xcasroot;
    if (getenv("XCAS_ROOT")){
      xcasroot=string(getenv("XCAS_ROOT"));
      if (xcasroot[xcasroot.size()-1]!='/')
	xcasroot+='/';
    }
    else {
      xcasroot=arg;
      int xcasroot_size=xcasroot.size()-1;
      for (;xcasroot_size>=0;--xcasroot_size){
	if (xcasroot[xcasroot_size]=='/')
	  break;
      }
      if (xcasroot_size>0)
	xcasroot=xcasroot.substr(0,xcasroot_size)+"/";
      else {
	if (access("/usr/bin/xcas",R_OK)==0)
	  xcasroot="/usr/bin/";
	else {
#ifdef __APPLE__
	if (access("/Applications/usr/bin/xcas",R_OK)==0)
	  xcasroot="/Applications/usr/bin";
#else
	  if (access("/usr/local/bin/xcas",R_OK)==0)
	    xcasroot="/usr/local/bin/";
#endif
	  else
	    xcasroot="./";
	}
      }
    }
    // ofstream of("/tmp/xcasroot");
    // of << xcasroot << endl;
    return xcasroot;
  }

  // extern int debug_infolevel;
  static bool get_index_from_cache(const char * filename, multimap<string,string> & multi,bool verbose){
#if defined VISUALC || defined BESTA_OS
    char * buf = new char[BUFFER_SIZE];
#else
    char buf[BUFFER_SIZE];
#endif
    ifstream if_mtt(filename);
    int n=0;
    while (if_mtt && !if_mtt.eof()){
      if_mtt.getline(buf,BUFFER_SIZE,'¤');
      if (!if_mtt || if_mtt.eof()){
	if (verbose)
	  cerr << "// Read " << n << " entries from cache " << filename << endl;
	return true;
      }
      string first(buf);
      if_mtt.getline(buf,BUFFER_SIZE,'¤');
      if (!if_mtt || if_mtt.eof()){
#if defined VISUALC || defined BESTA_OS
	delete [] buf;
#endif
	return false;
      }
      multi.insert(pair<string,string>(first,buf));
      if (!(n%100)){ // check every 100 links if link exists
	first=buf;
	int l=first.size(),j;
	char ch=0;
	for (j=l-1;j>=0;--j){
	  ch=first[j];
	  if (ch=='#' || ch=='/')
	    break;
	}
	if (j>0 && ch=='#')
	  first=first.substr(0,j);
	if (access(first.c_str(),R_OK)){
	  multi.clear();
	  cerr << "Wrong cache! " << filename << endl;
	  if_mtt.close();
	  #ifndef RTOS_THREADX
	  #ifndef BESTA_OS
	  if (unlink(filename)==-1)
	    cerr <<  "You don't have write permissions on " << filename <<".\nYou must ask someone who has write permissions to remove " << filename << endl;
	  else
	    cerr << "Cache file "<< filename << " has been deleted" << endl;
	  #endif
	  #endif
	  return false;
	}
      }
      ++n;
      if_mtt.getline(buf,BUFFER_SIZE,'\n');
    }
    if (verbose)
      cerr << "// Read " << n << " entries from cache " << filename ;
    return true;
  }

  static bool get_index_from_cache(const char * filename, vector<string> & multi,bool verbose){
#if defined VISUALC || defined BESTA_OS
    char * buf = new char[BUFFER_SIZE];
#else
    char buf[BUFFER_SIZE];
#endif
    ifstream if_mtt(filename);
    int n=0;
    while (if_mtt && !if_mtt.eof()){
      if_mtt.getline(buf,BUFFER_SIZE,'¤');
      if (!if_mtt || if_mtt.eof()){
#if defined VISUALC || defined BESTA_OS
	delete [] buf;
#endif
	if (verbose)	
	  cerr << "// Read " << n << " entries from cache " << filename << endl;
	return true;
      }
      multi.push_back(buf);
      ++n;
      if_mtt.getline(buf,BUFFER_SIZE,'\n');
    }
#if defined VISUALC || defined BESTA_OS
    delete [] buf;
#endif
    if (verbose)
      cerr << "// Read " << n << " entries from cache " << filename ;
    return true;
  }

  string html_help_init(const char * arg,int language,bool verbose,bool force_rebuild){
    string xcasroot=xcasroot_dir(arg);
    // HTML online help
    string html_help_dir=xcasroot+"doc/";
    if (access(html_help_dir.c_str(),R_OK)){
#ifdef __APPLE__
      if (!access("/Applications/usr/bin/icas",R_OK))
	html_help_dir="/Applications/usr/share/giac/doc/";
#else
      if (!access("/usr/bin/xcas",R_OK))
	html_help_dir="/usr/share/giac/doc/";
#endif
      else {
	if (!access("/usr/local/bin/xcas",R_OK))
	  html_help_dir="/usr/local/share/giac/doc/";
      }
    }
    if (access(html_help_dir.c_str(),R_OK) && xcasroot.size()>4 && xcasroot.substr(xcasroot.size()-4,4)=="bin/")
      html_help_dir=xcasroot.substr(0,xcasroot.size()-4)+"share/giac/doc/";
    if (access(html_help_dir.c_str(),R_OK))      
      cerr << "Unable to open HTML doc directory " << html_help_dir << endl;
    html_help_dir += find_lang_prefix(language);
#ifdef WIN32
   html_help_dir +="cascmd_"+find_lang_prefix(giac::language(context0)); // temporary workaround, for win archive copy doc/fr/html_vall to doc/fr/cascmd_fr/html_vall and change path
#endif
    html_mtt.clear();
    html_mall.clear();
    html_vall.clear();
    // Get indices from file cache if it exists
    if (!force_rebuild && !access((html_help_dir+"html_mtt").c_str(),R_OK) && !access((html_help_dir+"html_mall").c_str(),R_OK) && !access((html_help_dir+"html_vall").c_str(),R_OK)){
      if (get_index_from_cache((html_help_dir+"html_mtt").c_str(),html_mtt,verbose)&&
	  get_index_from_cache((html_help_dir+"html_mall").c_str(),html_mall,verbose)&&
	  get_index_from_cache((html_help_dir+"html_vall").c_str(),html_vall,verbose) )
	return html_help_dir;
    }
    find_all_index(html_help_dir,html_mtt,html_mall);
    // Write all indices in a file cache
    ofstream of_mtt((html_help_dir+"html_mtt").c_str());
    multimap<string,string>::const_iterator it=html_mtt.begin(),itend=html_mtt.end();
    for (;it!=itend;++it)
      of_mtt << it->first << '¤' << it->second << '¤' << endl;
    of_mtt.close();
    ofstream of_mall((html_help_dir+"html_mall").c_str());
    it=html_mall.begin();itend=html_mall.end();
    for (;it!=itend;++it)
      of_mall << it->first << '¤' << it->second << '¤' << endl;
    of_mall.close();
    ofstream of_vall((html_help_dir+"html_vall").c_str());
    vector<string>::const_iterator st=html_vall.begin(),stend=html_vall.end();
    for (;st!=stend;++st)
      of_vall << *st << '¤' << endl;
    of_vall.close();
    /*
    if (debug_infolevel){
      vector<string>::const_iterator it=html_vall.begin(),itend=html_vall.end();
      for (;it!=itend;++it)
	cerr << *it << endl;
    }
    */
    return html_help_dir;
  }

  static bool multigrep(FILE * f,const string & s){
    int l=s.size();
    // find spaces
    string tmp;
    vector<string> vs;
    for (int i=0;i<l;++i){
      if (s[i]==' '){
	if (!tmp.empty())
	  vs.push_back(tmp);
	tmp="";
      }
      else
	tmp+=s[i];
    }
    if (!tmp.empty())
      vs.push_back(tmp);
    l=vs.size();
    if (!f || !l)
      return false;
    char c;
    for (tmp="";;){
      if (feof(f) || ferror(f)){
	return false;
      }
      c=fgetc(f);
      if (c==char(0xc3)){
	if (feof(f) || ferror(f))
	  return false;
	unsigned code=fgetc(f);
	switch (code){
	case 0xa8: case 0xa9: case 0xaa:
	  c='e';
	  break;
	case 0xa0: case 0xa1: case 0xa2:
	  c='a';
	  break;
	case 0xae: case 0xaf:
	  c='i';
	  break;
	case 0xb4:
	  c='o';
	  break;
	case 0xb9: case 0xbb:
	  c='u';
	  break;
	case 0xa7:
	  c='c';
	  break;
	}
      }
      c=tolower(c);
      if (c=='&'){
	c=fgetc(f);
	if (c=='#'){
	  unsigned code=0,base=10;
	  for (;;){
	    if (feof(f) || ferror(f))
	      return false;
	    c=fgetc(f);
	    if (c=='x' || c=='X')
	      base=16;
	    if (c=='o' || c=='O')
	      base=8;
	    if (c!=';'){
	      if (base!=16)
		code = code*base+c-'0';
	      else {
		if (c>='A' && c<='F')
		  code = code*base + c-'A'+10;
		if (c>='a' && c<='f')
		  code = code*base + c-'a'+10;
		if (c>='0' && c<='9')
		  code = code*base + c-'0';
	      }
	    }
	    else{
	      switch (code){
	      case 0xe8: case 0xe9: case 0xea:
		c='e';
		break;
	      case 0xe0: case 0xe2:
		c='a';
		break;
	      case 0xf4:
		c='o';
		break;
	      case 0xf9: case 0xfb:
		c='u';
		break;
	      case 0xe7:
		c='c';
		break;
	      case 238:
		c='i';
		break;
	      }
	      break;
	    }
	  }
	}
      }
      if (c==' '){
	if (!tmp.empty()){ // search tmp in vs
	  unsigned tmpl=tmp.size(),tmpvs;
	  for (int i=0;i<l;++i){
	    if ( (tmpvs=vs[i].size())<=tmpl && tmp.substr(0,tmpvs)==vs[i]){
	      vs.erase(vs.begin()+i);
	      --l;
	      if (l<=0)
		return true;
	    }
	  }
	}
	tmp="";
      }
      else
	tmp+=c;
    }
  }

  bool grep(FILE * f,const string & s){
    int l=s.size();
    int pos=0;
    if (!f || !l)
      return false;
    char c0=tolower(s[0]),c;
    for (;;){
      if (feof(f) || ferror(f)){
	return false;
      }
      c=tolower(fgetc(f));
      if (c==tolower(s[pos])){
	++pos;
	if (pos==l){
	  return true;
	}
      }
      else {
	if (c==c0)
	  pos=1;
	else
	  pos=0;
      }
    }
  }

  bool grep(const string & filename,const string & s){
    FILE * f=fopen(filename.c_str(),"r");
    bool res=multigrep(f,s);
    if (f)
      fclose(f);
    return res;
  }
#endif // RTOS_THREADX

  // static char otherchars[]="_.~ ¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ";

  bool isalphan(char ch){
    if (ch>='0' && ch<='9')
      return true;
    if (ch>='a' && ch<='z')
      return true;
    if (ch>='A' && ch<='Z')
      return true;
    if (unsigned(ch)>128)
      return true;
    if (ch=='_' || ch=='.' || ch=='~')
      return true;
    /*
    char * ptr=otherchars;
    for (;*ptr;++ptr){
      if (ch==*ptr)
	return true;
    }
    */
    return false;
  }

  std::string unlocalize(const std::string & s){
    std::string res,tmp;
    int ss=s.size();
    std::map<std::string,std::string>::const_iterator it,itend=lexer_localization_map().end();
    int mode=0; // 1 if inside a string
    for (int i=0;;++i){
      char ch=s[i];
      if (mode){
	if (ch=='"'){
	  if (res.empty() || res[res.size()-1]!='\\')
	    mode=0;
	}
	res += ch;
	if (i==ss)
	  break;
	continue;
      }
      if (i<ss && isalphan(ch))
	tmp += ch;
      else { // search if tmp is in lexer_localization_map
	it=lexer_localization_map().find(tmp);
	if (it!=itend)
	  tmp = it->second; // it is -> we must translate to giac
	res += tmp;
	tmp = "";
	if (ch=='"'){
	  if (res.empty() || res[res.size()-1]!='\\')
	    mode=1;
	}
	if (i<ss)
	  res += ch;
	else
	  break;
      }
    }
    return res;
  }

  std::string localize(const std::string & s,int language){
    std::string res,tmp;
    int ss=s.size();
    int mode=0; // 1 if inside a string
    std::multimap<std::string,localized_string>::const_iterator it0,it,itend,backend=back_lexer_localization_map().end();
    for (int i=0;;++i){
      char ch=s[i];
      if (mode){
	if (ch=='"'){
	  if (res.empty() || res[res.size()-1]!='\\')
	    mode=0;
	}
	res += ch;
	if (i==ss)
	  break;
	continue;
      }
      if (i<ss && isalphan(s[i]))
	tmp += s[i];
      else { // search if tmp is in back_lexer_localization_map()
	it0=it=back_lexer_localization_map().find(tmp);
	itend=back_lexer_localization_map().upper_bound(tmp);
	if (it!=backend){
	  for (;it!=itend;++it){
	    if (it->second.language==language){
	      tmp = it->second.chaine; 
	      break;
	    }
	  }
	  if (it==itend)
	    tmp = it0->second.chaine;
	}
	res += tmp;
	tmp = "";
	if (ch=='"'){
	  if (res.empty() || res[res.size()-1]!='\\')
	    mode=1;
	}
	if (i<ss)
	  res += s[i];
	else
	  break;
      }
    }
    return res;
  }

#ifndef NO_NAMESPACE_GIAC
} // namespace giac
#endif // ndef NO_NAMESPACE_GIAC
