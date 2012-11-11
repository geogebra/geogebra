% ----------------------------------------------------------------------
% $Id: profile.red 1764 2012-10-10 16:25:12Z mkosta $
% ----------------------------------------------------------------------
% Copyright (c) 2012 T. Sturm
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

module profile;

loadtime load!-package 'rltools;

global '(!*comp);

fluid '(!*utf8);

fluid '(profile_alist!* profile_list!* profile_time!* profile_gctime!*
        profile_stack!* profile_recursion1l!* profile_recursion2l!*);

operator prousage;
procedure prousage();
   <<
      prin2t "Usage: profile p_1, ..., p_n;           profile expr procedures p_1, ..., p_n";
      prin2t "       unprofile p_1, ..., p_n;         stop profiling p_1, ..., p_n";
      prin2t "       proprint([p_1,..., p_n | all]);  print collected data in tables";
      prin2t "       proall();                        list all profiled procedures";
      prin2t "       pronested();                     list procedures with nested calls";
      prin2t "       proreset();                      delete all collected data";
      prin2t "       prousage();                      this information";
      nil
>>;

loadtime prousage();

put('profile,'stat,'rlis);
procedure profile(fnl);
   for each fn in fnl do profile_profile fn;

put('unprofile,'stat,'rlis);
procedure unprofile(fnl);
   for each fn in fnl do profile_unprofile fn;

put('proprint,'psopfn,'proprint);
procedure proprint(argl);
   if not argl then
      profile_print nil
   else if eqcar(car argl,'list) and not cdr argl then
      profile_print cdar argl
   else
      profile_print argl;

operator proreset;
procedure proreset();
   profile_reset();

operator prorefresh;
procedure prorefresh();
   profile_refresh();

operator proall;
procedure proall();
   'list . sort(profile_list!*,'ordp);

operator pronested;
procedure pronested();
   'list . sort(for each fn in profile_list!* join
      if profile_nestedp fn then {fn}, 'ordp);

procedure profile_profile(fn);
   begin scalar fn,d,args,svcomp;
      if get(fn,'profile_origfn) then <<
	 lprim {fn,"is already profiled"};
	 return
      >>;
      d := getd fn;
      if not d or car d neq 'expr then
	 rederr {fn,"is not an expr procedure"};
      profile_list!* := fn . profile_list!*;
      profile_updAlist();
      d := cdr d;
      args := for i:=1:get(fn,'number!-of!-args) collect mkid('a,i);
      put(fn,'profile_origfn,d);
      svcomp := !*comp;
      !*comp := t;
      errorset({'de,fn,args,{'apply,''profile_exec,
	 {'list,mkquote fn,mkquote d,'list . args}}},t,nil);
      !*comp := svcomp;
      profile_reset()
   end;

procedure profile_updAlist();
      for each fn in profile_list!* do
      	 if not assoc({fn},profile_alist!*) then
      	    profile_alist!* := ({fn} . {0,0,0}) . profile_alist!*;

procedure profile_refresh();
   <<
      profile_stack!* := nil;
      profile_time!* := time();
      profile_gctime!* := gctime();
      nil
   >>;

procedure profile_unprofile(fn);
   <<
      if not get(fn,'profile_origfn) then
	 lprim {fn,"is not profiled"}
      else <<
      	 putd(fn,'expr,remprop(fn,'profile_origfn));
      	 profile_list!* := delqip(fn,profile_list!*);
      	 profile_reset()
      >>
   >>;

procedure profile_reset();
   <<
      profile_recursion1l!* := nil;
      profile_recursion2l!* := nil;
      profile_alist!* := nil;
      profile_updAlist();
      profile_refresh()
   >>;

procedure profile_exec(fn,fvalue,args);
   begin scalar w,otime,ogctime,ntime,ngctime,res,key;
      if eqcar(profile_stack!*,fn) then <<
	 % Record direct recursion.
	 profile_recursion1l!* := lto_insertq(fn,profile_recursion1l!*);
	 return apply(fvalue,args)
      >>;
      if memq(fn,profile_stack!*) then <<
	 % Record indirect recursion.
	 profile_recursion2l!* :=
	    lto_insertq(fn . profile_stack!*,profile_recursion2l!*);
	 return apply(fvalue,args)
      >>;
      % Maintain a calling stack for the timed functions modulo recursion.
      profile_stack!* := lto_insertq(fn,profile_stack!*);
      % Perform the actual function call with timing.
      otime := time();
      ogctime := gctime();
      if 'psl memq lispsystem!* then otime := otime - ogctime;
      res := apply(fvalue,args);
      ntime := time();
      ngctime := gctime();
      if 'psl memq lispsystem!* then ntime := ntime - ngctime;
      % Update the calling stack. If the calling stack gets empty here, then we
      % are in a toplevel call.
      if eqcar(profile_stack!*,fn) then
	 profile_stack!* := cdr profile_stack!*;
      % Find or create an entry and record the time there.
      key := fn . profile_stack!*;
      w := assoc(key,profile_alist!*);
      if not w then <<
	 w := key . {0,0,0};
	 profile_alist!* := w . profile_alist!*
      >>;
      w := cdr w;
      car w := car w + 1;
      cadr w := cadr w + (ntime - otime);
      caddr w := caddr w + (ngctime - ogctime);
      return res
   end;

procedure profile_print(argl);
   % Print. Format and print the information collected in fluids during
   % computation.
   begin scalar alist,d,p2,p3; integer ts2,ts3;
      % Determine the relevant cpu times since the last call of either
      % this procedure or profile_qualtime. We assume that all computations
      % since then are relavant.
      ts2 := time() - profile_time!*;
      ts3 := gctime() - profile_gctime!*;
      if 'psl memq lispsystem!* then
 	 ts2 := ts2 - ts3;
      alist := copy profile_alist!*;
      % Compute percentages.
      for each pr in alist do <<
	 d := cdr pr;
	 p2 := profile_percent(cadr d,ts2);
	 p3 := profile_percent(caddr d,ts3);
	 cdr pr := {car d,cadr d,p2,caddr d,p3}
      >>;
      if not argl or argl = '(all) then <<
      	 profile_toplevel!-table(alist,ts2,ts3);
      	 terpri()
      >>;
      if argl = '(all) then <<
      	 for each pr in alist do
	    if not cdr car pr then
	       profile_special!-table(alist,caar pr,ts2,ts3);
	 return
      >>;
      for each arg in argl do
	 if profile_nestedp arg then
	    profile_special!-table(alist,arg,ts2,ts3)
   end;

procedure profile_nestedp(fn);
   profile_nestedp1(fn,profile_alist!*);

procedure profile_nestedp1(fn,alist);
   alist and (caaar alist eq fn and cdaar alist or
      profile_nestedp1(fn,cdr alist));

procedure profile_percent(part,all);
   if eqn(part,0) then
      0.0
   else
      profile_truncate(float part * 100 / float all,1);

procedure profile_toplevel!-table(alist,ts2,ts3);
   % Process and print profile_alist!*, which contains all
   % information about non-recusive calls.
   begin scalar tlalist;
      terpri();
      ioto_tprin2t "Analysis of all relative toplevel calls to the specified functions.";
      ioto_tprin2t "Note that relatively nested calls are not counted here:";
      % Sort descending by cpu time spent in the function.
      tlalist := for each pr in alist join
	 if not cdr car pr then {pr};
      tlalist := sort(tlalist,function(lambda(x,y); caddr x > caddr y));
      % Add headline and footlines to the list and print.
      tlalist := lto_nconcn {
	 {nil}, {profile_qtHeadline("Toplevel Calls")},
 	 {nil}, tlalist,
	 if cdr tlalist then {nil},
	 if cdr tlalist then {profile_qtSum tlalist},
	 {nil}, {profile_qtTotal(ts2,ts3)}, {nil}};
      profile_print1(tlalist,10)
   end;

procedure profile_qtHeadline(title);
   title . '("calls" "time(ms)" "time(%)"  "gc(ms)" "gc(%)");

procedure profile_qtSum(al);
   begin integer d,s1,s2,s3,s4,s5;
      % Sum everything up.
      for each pr in al do <<
      	 d := cdr pr;
      	 s1 := s1 + nth(d,1);
      	 s2 := s2 + nth(d,2);
      	 s3 := s3 + nth(d,3);
      	 s4 := s4 + nth(d,4);
      	 s5 := s5 + nth(d,5)
      >>;
      return {"sum",s1,s2,s3,s4,s5}
   end;

procedure profile_qtTotal(ts2,ts3);
   {"total"," ",ts2,100.0,ts3,100.0};

procedure profile_truncate(x,d);
   float fix(10^d*x) / 10^d;

procedure profile_special!-table(alist,fn,ts2,ts3);
   begin scalar spalist;
      spalist := for each pr in alist join
	 if car car pr eq fn then
	    {pr};
      if not cdr spalist then return;
      terpri();
      ioto_tprin2 {"Analysis of all calls to ",fn};
      if fn memq profile_recursion1l!* then
	 ioto_prin2 {" (relatively direct recursion)"};
      if fn memq profile_recursion2l!* then
	 ioto_prin2 {" (relatively indirect recursion)"};
      ioto_prin2t ":";
      spalist := sort(spalist,
	 function(lambda(x,y); length car x < length car y));
      spalist := lto_nconcn {
	 {nil}, {profile_qtHeadline(fn)},
	 {nil}, spalist,
	 if cdr spalist then {nil},
	 if cdr spalist then {profile_qtSum(spalist)},
	 {nil}, {profile_qtTotal(ts2,ts3)}, {nil}};
      profile_print1(spalist,10);
      terpri()
   end;

procedure profile_print1(al,cw);
   begin integer n, cw1;
      cw1 := max(linelength nil, 5*cw + 20) - 5*cw - 1;
      for each pr in al do
      	 if pr then <<
	    n := profile_print!-idl car pr;
	    if n geq cw1 then <<
	       terpri();
	       for i := 1 : cw1 do prin2 " "
	    >>;
	    for i := n+1 : cw1 do prin2 " ";
	    for each x in cdr pr do <<
	       for i := length explode2 x + 1 : cw do prin2 " ";
	       prin2 x
	    >>;
	    terpri()
      	 >> else <<
	    profile_print!-line(length cdr cadr al,cw1,cw);
	    terpri()
      	 >>
   end;

procedure profile_print!-line(n,cw1,cw);
   for i := 1 : cw1 + cw * n do
      if !*utf8 then utf8_tyo({1,14*16+2,8*16,9*16+4}) else prin2 "-";

procedure profile_print!-idl(idl);
   begin integer n;
      if pairp idl then <<
      	 idl := reverse idl;
      	 prin2 car idl;
	 n := n + length explode2 car idl;
      	 for each id in cdr idl do <<
	    prin2 " ";
      	    if !*utf8 then
	       utf8_tyo({1,14*16+2,8*16+6,9*16+2})
      	    else
	       prin2 ">";
	    prin2 " ";
	    n := n + 3;
      	    prin2 id;
	    n := n + length explode2 id
      	 >>
      >> else <<
      	 prin2 idl;
	 n := length explode2 idl
      >>;
      return n
   end;

endmodule;

end;  % of file
