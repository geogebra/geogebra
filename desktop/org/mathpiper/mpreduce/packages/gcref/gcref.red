% ----------------------------------------------------------------------
% $Id: gcref.red 1319 2011-08-25 09:56:37Z thomas-sturm $
% ----------------------------------------------------------------------
% (c) 2001 A. Dolzmann and T. Sturm, 2011 T. Sturm
% ----------------------------------------------------------------------
% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions
% are met:
%
%    * Redistributions of source code must retain the relevant
%      copyright notice, this list of conditions and the following
%      disclaimer.
%    * Redistributions in binary form must reproduce the above
%      copyright notice, this list of conditions and the following
%      disclaimer in the documentation and/or other materials provided
%      with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
% "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
% LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
% A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
% OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
% SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
% LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
% DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
% THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
% (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
% OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
%

lisp <<
   fluid '(gcref_rcsid!* gcref_copyright!*);
   gcref_rcsid!* := "$Id: gcref.red 1319 2011-08-25 09:56:37Z thomas-sturm $";
   gcref_copyright!* := "(c) 2001  A. Dolzmann, T. Sturm, 2011 T. Sturm"
>>;

module gcref;
% Graphical cross referencer.
% Adapted from rcref by Martin L. Griss.

loadtime load_package 'assert;
loadtime off1 'assert;

load!-package 'rcref;

exports gcref_off;

fluid '(!*defn dfprint!* gcref_nolist!* gcref_exportl!*);

global '(undefg!* gseen!* btime!* haveargs!* entpts!* seen!* tseen!*
   pfiles!* !*saveprops !*algebraics);

switch gcref,saveprops,gcrefall,gcrefudg;

on1 'gcrefall;
off1 'gcrefudg;

put('gcref ,'simpfg ,'((t (crefon)) (nil (gcref_off))));

gcref_nolist!* := '(cell_desc cell_testpt cell_mk testpt_defpol testpt_ivl testpt_var testpt_mk ivl_lb ivl_ub ivl_mk);

anlfn procedure exports(u);
   <<
      gcref_exportl!* := append(gcref_exportl!*,w);
      flag(w,'exported)
   >> where w=eval car u;

procedure gcref_off;
   % Main call, sets up, alphabetizes and prints.
   begin scalar tim;
      gcref_off1();
      tim := time()-btime!*;
      pfiles!* := for each z in pfiles!* collect <<
	 put(cdr z,'cref_name,car z);
	 cdr z
      >>;
      if !*gcrefall then
	 gcref_mkgraph gcref_select seen!*
      else
      	 gcref_mkegraph gcref_eselect seen!*;
      if not !*saveprops then
	 gcref_remprops()
   end;

procedure gcref_select(idl);
   for each fn in idl join
      if flagp(fn,'defd) and not memq(fn,gcref_nolist!*) then
 	 {fn};

procedure gcref_eselect(idl);
   for each fn in idl join
      if flagp(fn,'exported) and not memq(fn,gcref_nolist!*) then
 	 {fn};

procedure gcref_mkegraph(efnl);
   for each efn in efnl do <<
      prin2t "[";
      gcref_rmknode efn;
      prin2t "]";
      terpri()
   >>;

procedure gcref_rmknode(fn);
   begin scalar fnl,readyl;
      gcref_mknode fn;
      readyl := {fn};
      fnl := gcref_select get(fn,'calls);
      while fnl do <<
      	 fn := car fnl;
	 fnl := cdr fnl;
	 if not memq(fn,readyl) then <<
	    readyl := fn . readyl;
	    prin2t ",";
	    if flagp(fn,'exported) then
	       gcref_mknode1(fn,nil)
	    else <<
	       gcref_mknode fn;
	       fnl := nconc(fnl,gcref_select get(fn,'calls))
	    >>
	 >>
      >>
   end;

procedure gcref_mkgraph(seen);
   if !*gcrefudg then
      gcref_mkgraph!-udg(seen)
   else
      gcref_mkgraph!-tgf(seen);

procedure gcref_mkgraph!-tgf(seen);
   <<
      for each fn in seen do
	 gcref_mknode!-tgf fn;
      prin2t "#";
      for each fn in seen do
	 gcref_mkedges!-tgf(fn,gcref_select get(fn,'calls))
   >>;

procedure gcref_mknode!-tgf(fn);
   <<
      prin2 fn;
      prin2 " ";
      prin2t fn;
   >>;

procedure gcref_mkedges!-tgf(fn,calls);
   <<
      for each c in calls do <<
	 prin2 fn;
	 prin2 " ";
	 prin2t c
      >>
   >>;

procedure gcref_mkgraph!-udg(seen);
   <<
      prin2t "[";
      if seen then <<
      	 gcref_mknode!-udg car seen;
	 seen := cdr seen
      >>;
      for each fn in seen do <<
	 prin2t ",";
	 gcref_mknode!-udg fn
      >>;
      prin2t "]"
   >>;

procedure gcref_mknode!-udg(fn);
   gcref_mknode1!-udg(fn,gcref_select get(fn,'calls));

procedure gcref_mknode1!-udg(fn,c);
   <<
      prin2 "l(""";
      prin2 fn;
      prin2t """,";
      prin2 "n(""mytype"",[a(""OBJECT"",""";
      prin2 fn;
      prin2 """)";
      if flagp(fn,'exported) then <<
	 prin2t ",";
	 prin2 "a(""BORDER"",""double"")"
      >>;
      prin2 "],[";
      gcref_mkedges!-udg c;
      prin2 "]))"
   >>;

procedure gcref_mkedges!-udg(calls);
   <<
      if calls then <<
	 terpri();
      	 gcref_mkedge!-udg car calls;
      	 calls := cdr calls
      >>;
      for each c in calls do <<
	 prin2t ",";
	 gcref_mkedge!-udg c
      >>
   >>;

procedure gcref_mkedge!-udg(c);
   <<
      prin2 "l(""";
      prin2 gensym();
      prin2 """,e(""mytype"",[],r(""";
      prin2 c;
      prin2 """)))"
   >>;

procedure gcref_remprops();
   <<
      rempropss(seen!*,'(gall calls globs calledby alsois sameas));
      remflagss(seen!*,'(seen cinthis defd));
      remflagss(gcref_exportl!*,'(exported));
      rempropss(gseen!*,'(usedby usedunby boundby setby));
      remflagss(gseen!*,'(dclglb gseen glb2rf glb2bd glb2st));
      for each z in tseen!* do remprop(car z,'funs);
      %       for each z in haveargs!* do remprop(z,'number!-of!-args);
      haveargs!* := gseen!* := seen!* := tseen!* := nil
   >>;

procedure gcref_off1();
   begin scalar x;
      dfprint!* := nil;
      !*defn := nil;
      if not !*algebraics then
	 remprop('algebraic,'newnam);     % Back to normal.
      for each fn in seen!* do <<
	 if null get(fn,'calledby) then
	    entpts!*:=fn . entpts!*;
	 undefdchk fn
      >>;
      tseen!* := for each z in idsort tseen!* collect <<
	 remprop(z,'tseen);
	 for each fn in (x:=get(z,'funs)) do <<
	    undefdchk fn;
	    remprop(fn,'rccnam)
	 >>;
	 z . x
      >>;
      for each z in gseen!* do
         if get(z,'usedunby) then
	    undefg!* := z . undefg!*;
   end;

endmodule;  % gcref

end;  % of file
