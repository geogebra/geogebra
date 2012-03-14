% ----------------------------------------------------------------------
% $Id: mma.red 1392 2011-09-10 06:38:45Z thomas-sturm $
% ----------------------------------------------------------------------
% Copyright (c) 2008-2010 Thomas Sturm
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
   fluid '(mma_rcsid!* mma_copyright!*);
   mma_rcsid!* := "$Id: mma.red 1392 2011-09-10 06:38:45Z thomas-sturm $";
   mma_copyright!* := "(c) 2008-2010 T. Sturm"
>>;

module mma;

create!-package('(mma),nil);

load!-package 'redlog;
load!-package 'ofsf;
loadtime load!-package 'rltools;

fluid '(!*redefmsg !*rlqepnf !*rlverbose !*echo !*time !*backtrace mma_call!*
   mma_wd!* mma_awk!* !*fancy);

switch rlqefbmma;

mma_call!* := "/Applications/Mathematica.app/Contents/MacOS/MathKernel";
mma_wd!* := "/tmp/";
mma_awk!* := lto_sconcat {rltools_trunk(),"packages/redlog/mma/mma.awk"};

put('ofsf,'rl_services,
   '(rl_mma!* . mma_mma) . get('ofsf,'rl_services));

rl_mkserv('mma,'(rl_simp),'(reval),'(nil),
   function(lambda x; if x then rl_mk!*fof x),T);

rl_set '(ofsf);

procedure mma_mma(f,fn);
   begin scalar w,oldpprifn,oldprtch,scsemic,oldecho,origoh,ll,isfancy;
      ll := linelength(2^(32-5)-1);
      oldpprifn := get('times,'pprifn);
      oldprtch := get('expt,'prtch);
      scsemic := semic!*;
      oldecho := !*echo;
      origoh := outputhandler!*;
      isfancy := !*fancy;
      if isfancy then
      	 off1 'fancy;
      w := errorset({'mma_mma1,mkquote f,mkquote fn},T,!*backtrace);
      if isfancy then
      	 on1 'fancy;
      if errorp w then <<
      	 put('times,'pprifn,oldpprifn);
      	 put('expt,'prtch,oldprtch);
      	 semic!* := scsemic;
	 !*echo := oldecho;
      	 outputhandler!* := origoh;
	 mma_myscprint nil;
      	 if w neq 99 then
	    rederr w;
	 % CTRL-C
	 return nil
      >>;
      linelength ll;
      return car w
   end;

procedure mma_mma1(f,fn);
   begin scalar w,free,oldprtch,oldpprifn,fn1,fn2,fh,result,oldecho,scsemic,
	 call,mma,rnd;
      scsemic := semic!*;
      rnd := lto_at2str random(10^5);
      fn1 := fn or lto_sconcat{mma_wd!*,getenv "USER",rnd,".mma"};
      if null fn then
      	 fn2 := lto_sconcat{mma_wd!*,getenv "USER",rnd,".red"};
      if !*rlverbose then ioto_prin2 {"+++ creating ",fn1," ... "};
      oldpprifn := get('times,'pprifn);
      oldprtch := get('expt,'prtch);
      put('expt,'prtch,'!^);
      if !*rlqepnf then f := cl_pnf f;
      out(fn1);
      terpri!* nil;
      mma_myscprint t;
      mma_cadprint f;
      terpri!* t;
      mma_myscprint nil;
      prin2t "TimeUsed[]";
      shut(fn1);
      put('expt,'prtch,oldprtch);
      if !*rlverbose then ioto_prin2t "done";
      mma := getenv("RLMMA") or mma_call!*;
      if null fn then <<
      	 call := lto_sconcat {mma," < ",fn1," | awk -v rf=",fn2,
	    " -v verb=",lto_at2str !*rlverbose," -v time=",lto_at2str !*time,
	    " -f ",mma_awk!*};
	 if !*rlverbose then
	    ioto_prin2t lto_sconcat {"+++ calling ",call};
	 system call;
	 oldecho := !*echo;
	 !*echo := nil;
	 fh := rds open(fn2,'input);
	 result := xread t;
	 close rds fh;
	 !*echo := oldecho;
	 system lto_sconcat{"rm -f ",fn1," ",fn2};
	 if null result then
	    lprim "Mathematica failed"
	 else
	    result := rl_simp result
      >>;
      semic!* := scsemic:
      return result
   end;

procedure mma_cadprint(f);
   begin scalar w,!*nat;
      prin2!* "InputForm[Resolve[";
      mma_cadprint1 f;
      prin2!* ",";
      w := cl_varl f;
      maprin('list . nconc(car w,cdr w));
      prin2!* ",";
      prin2!* "Reals]]";
      return nil
   end;

procedure mma_cadprint1(f);
   begin scalar op,!*nat;
      op := rl_op f;
      if op eq 'ex then <<
	 prin2!* "Exists[";
	 prin2!* rl_var f;
	 prin2!* ",";
	 mma_cadprint1 rl_mat f;
	 prin2!* "]";
	 return nil
      >>;
      if op eq 'all then <<
	 prin2!* "ForAll[";
	 prin2!* rl_var f;
	 prin2!* ",";
	 mma_cadprint1 rl_mat f;
	 prin2!* "]";
	 return nil
      >>;
      mma_cadprint2(f)
   end;

procedure mma_cadprint2(f);
   begin scalar op,argl;
      op := rl_op f;
      if rl_cxp op then <<
	 if rl_tvalp op then <<
	    mma_cadprinttval f;
	    return nil
	 >>;
	 prin2!* "(";
	 argl := rl_argn f;
	 mma_cadprint2(car argl);
	 for each x in cdr argl do <<
	    mma_cadprintop op;
	    mma_cadprint2 x
	 >>;
	 prin2!* ")";
	 return nil
      >>;
      maprin prepf ofsf_arg2l f;
      mma_cadprintop op;
      prin2!* "0";
      return nil
   end;

procedure mma_cadprinttval(tv);
   if tv eq 'true then
      prin2!* "True"
   else  % [tv eq 'false]
      prin2!* "False";

procedure mma_cadprintop(op);
   <<
      prin2!* " ";
      prin2!*(cdr atsoc(op,'((equal . "==") (neq . "!=") (lessp . "<")
      	 (greaterp . ">") (geq . ">=") (leq . "<=") (or . "||") (and . "&&")
      	    (impl . nil) (equiv . nil))) or
 	 rederr {"cannot translate",op,"to mma"});
      prin2!* " "
   >>;

copyd('mma_scprint!-orig,'scprint);

procedure mma_scprint(u,n);
   <<
      mma_scprint!-orig(u,n);
      prin2 "\"
   >>;

procedure mma_myscprint(flg);
   copyd('scprint,if flg then 'mma_scprint else 'mma_scprint!-orig)
      where !*redefmsg=nil;

endmodule;

end;
