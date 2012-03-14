module limits;

%% A fast limit package for REDUCE for functions which are continuous
%% except for computable poles and singularities.

%% Author: Stanley L. Kameny.

% Redistribution and use in source and binary forms, with or without
% modification, are permitted provided that the following conditions are met:
%
%    * Redistributions of source code must retain the relevant copyright
%      notice, this list of conditions and the following disclaimer.
%    * Redistributions in binary form must reproduce the above copyright
%      notice, this list of conditions and the following disclaimer in the
%      documentation and/or other materials provided with the distribution.
%
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
% THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
% PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR
% CONTRIBUTORS
% BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
% POSSIBILITY OF SUCH DAMAGE.
%


%% Revised 23 Mar 1993.  Version 1.4.

%% Modifications by:  Winfried Neun.

%% Added capability for using either the Taylor series package or the
%% Truncated Power Series Package.

%% Added provisions for transformation of certain irrational functions
%% into rational functions before limit calculation in order to be able
%% to compute series.

%% Changed the algebraic interface so that if limit package fails, an
%% equivalent of the original expression is returned.

%% Allowed for limited recursion through limsimp.

%% Corrected several bugs.

%% Date: 10 Oct 1990. Original version.

%% The Truncated Power Series package is used for non-critical points.
%% L'Hopital's rule is used in critical cases, with preprocessing of
%% <infinity - infinity> forms and reformatting of product forms in
%% order to be able to apply l'Hopital's rule.  A limited amount of
%% bounded arithmetic is also employed where applicable.

%% This limits package makes use of the ideas embodied in the
%% limit.red package, by Ian Cohen and John Fitch, 11 July 1990
%% that is in reduce-netlib;  in fact, some code is lifted bodily.
%% The idea of using the Truncated Power Series package to compute
%% limits at non-critical points, and the substitutions used in limit!+
%% and limit!- come from there.

load!-package 'tps; %load!-package 'taylor;

lisp(ps!:order!-limit := 100);

switch usetaylor; off usetaylor;

fluid '(!*precise lhop!# lplus!# !*protfg !*msg !*rounded !*complex
        !#nnn lim00!# !*crlimtest !*lim00rec);

!*lim00rec := t;  % Default value.

global '(erfg!* exptconv!#);

global '(abslims!#);
symbolic(abslims!# := {0,1,-1,'infinity,'(minus infinity)});
 % others may be added.

fluid '(lsimpdpth); global '(ld0!#); symbolic(ld0!# := 3);

flag('(limit limit!+ limit!- limit2),'full);

symbolic
for each c in '(limit limit!+ limit!- limit2) do
  <<remflag({c},'opfn); put(c,'simpfn,'simplimit)>>;

symbolic procedure limit2(top,bot,xxx,a);
   lhopital(top,bot,xxx,a) where lhop!#=0;

symbolic procedure limit!+(ex,x,a);
<<ex := simp!* limlogsort ex;
  if a = 'infinity then rederr "Cannot approach infinity from above"
  else if a = '(minus infinity) then
     limit(prepsq subsq(ex,list(x .
        list('quotient,-1,list('expt,'!*eps!*,2)))),'!*eps!*,0)
  else limit(prepsq subsq(ex,list(x .
        list('plus,a,list('expt,'!*eps!*,2)))),'!*eps!*,0)>>;

symbolic procedure limit!-(ex,x,a);
<<ex := simp!* limlogsort ex;
  if a = 'infinity then
     limit(prepsq subsq(ex,list(x .
        list('quotient,1,list('expt,'!*eps!*,2)))),'!*eps!*,0)
  else if a = '(minus infinity) then
     rederr "Cannot approach -infinity from below"
  else limit(prepsq subsq(ex,list(x .
        list('difference,a,list('expt,'!*eps!*,2)))),'!*eps!*,0)>>;

symbolic procedure limit(ex,xxx,a); limit0(limlogsort ex,xxx,a)
  where !*combinelogs=nil,lhop!#=0,lplus!#=0,lim00!#=nil,lsimpdpth=0;

symbolic procedure limlogsort x;
   begin scalar !*precise;
      x := prepsq simp!* x;
      return if countof('log,x)>1 then logsort x else x
   end;

symbolic procedure countof(u,v);
   if u = v then 1 else if atom v then 0
      else countof(u,car v)+countof(u,cdr v);

symbolic procedure simplimit u;
   % The kludgey handling of cot needs to be fixed some day.
   begin scalar fn,exprn,var,val,old,v,!*precise,!*protfg;
     if length u neq 4
       then rerror(limit,1,
                   "Improper number of arguments to limit operator");
     fn:= car u; exprn := cadr u; var := !*a2k caddr u; val := cadddr u;
     !*protfg := t;   % ACH: I'm not sure why this is needed.
     old := get('cot,'opmtch);
     put('cot,'opmtch,
         '(((!~x) (nil . t) (quotient (cos !~x) (sin !~x)) nil)));
     v := errorset!*({'apply,mkquote fn,mkquote {exprn,var,val}},nil);
     put('cot,'opmtch,old);
     !*protfg := nil;
     return if errorp v or (v := car v) = aeval 'failed then mksq(u,1)
             else simp!* v
   end;

symbolic procedure limit0(exp,x,a);
   begin scalar exp1;
     exp1 := simp!* exp;
     if a = 'infinity then
        return limit00(subsq(exp1,{x . {'quotient,1,{'expt,x,2}}}),x);
     if a = '(minus infinity) then
        return limit00(subsq(exp1,{x . {'quotient,-1,{'expt,x,2}}}),x);
     return
        (<<!*protfg := t;
           y := errorset!*
             ({'subsq,mkquote(exp := simp!* exp),mkquote{(x . a)}},nil)
              where !*expandlogs=t;
           !*protfg := nil;
           if not (errorp y) and not ((y := car y) = aeval 'failed)
              then mk!*sq y
           else if neq(a,0) then limit00(subsq(exp1,{x .
              {'plus,a,x}}),x)
           else limit00(exp1,x)>> where y=nil) end;

symbolic procedure limit00(ex,x);
   begin scalar p,p1,z,xpwrlcm,lim,ls;
     if (lim := crlimitset(p := prepsq ex,x)) then go to ret;
     if not lim00!# then
       <<lim00!# := not !*lim00rec;
         p1 := factrprep prepsq ex;
         if (xpwrlcm := xpwrlcmp(p1,x)) neq 1 then
           <<ex := subsq(ex,{x . {'expt,x,xpwrlcm}});
             p1 := factrprep prepsq ex>>;
         if (z := pwrdenp(p1,x)) neq 1 then
            ex := simp!*{'expt,p1,z};
         if (lim := crlimitset(p := prepsq ex,x)) then go to ret>>;
     % tps has failed because ex has a branch point at a or is undefined
     % at a or tps itself has failed or Reduce has not recognized the
     % numeric value of an expression.
     if %xpwrlcm and xpwrlcm>1 or
     lsimpdpth>ld0!#
       then lim := aeval 'failed else
       <<lsimpdpth := lsimpdpth + 1; ls := t;
         lim := limsimp(p,x);
         if prepsq simp!* lim = 'failed and lsimpdpth=1 then
           <<exptconv!# := nil; p := expt2exp(p,x);
             if exptconv!# then lim := limsimp(p,x)>> >>;
ret: return
       <<if ls then lsimpdpth := lsimpdpth - 1;
         if not z or z = 1 or lim=0 then lim
         else if (ls := prepsq simp!* lim) = '(minus infinity)
           then if (-1)^z = 1 then aeval 'infinity else lim
         else if ls member '(infinity failed) then lim
         else mk!*sq simp!* {'expt,prepsq simp!* lim,{'quotient,1,z}}>>
    end;

symbolic procedure factrprep p;
   begin scalar !*factor;
     !*factor := t;
     return prepsq simp!* p end;

symbolic procedure expt2exp(p,x);
   if atom p then p
   else if eqcar(p,'expt)
      and not freeof(cadr p,x) and not freeof(caddr p,x) then
        <<exptconv!# := t; {'expt,'e,{'times,{'log,cadr p},caddr p}}>>
   else expt2exp(car p,x) . expt2exp(cdr p,x);

symbolic procedure xpwrlcmp(p,x);
   if atom p then 1
   else if eqcar(p,'expt) and cadr p = x then getdenom caddr p
   else if eqcar(p,'sqrt) then getdenomx(cadr p,x)
   else lcm(xpwrlcmp(car p,x),xpwrlcmp(cdr p,x));

symbolic procedure getdenomx(p,x);
   if freeof(p,x) then 1
   else if eqcar(p,'minus) then getdenomx(cadr p,x)
   else if p = x or eqcar(p,'times) and x member cdr p then 2
   else xpwrlcmp(p,x);

symbolic procedure getdenom p;
   if eqcar(p,'minus) then getdenom cadr p
   else if eqcar(p,'quotient) and numberp caddr p then caddr p
   else 1;

symbolic procedure pwrdenp(p,x);
   if atom p then 1
   else if eqcar(p,'expt) and not freeof(cadr p,x)
      then getdenom caddr p
   else if eqcar(p,'sqrt) and not freeof(cadr p,x) then 2
   else if eqcar(p,'minus) then pwrdenp(cadr p,x)
   else if car p member '(times quotient) then
      (<<for each c in cdr p do m := lcm(m,pwrdenp(c,x)); m>>
       where m=1)
   else if atom car p then 1
   else lcm(pwrdenp(car p,x),pwrdenp(cdr p,x));

symbolic procedure limitset(ex,x,a);
 if !*usetaylor then
  <<!*protfg := t;
    ex := errorset!*({'limit1t,mkquote ex,mkquote x,mkquote a},nil);
    !*protfg := nil;
    if errorp ex then nil else car ex>>
 else % use tps.
  begin scalar oldpslim;
      !*protfg := t; oldpslim := simppsexplim '(1);
      ex := errorset!*({'limit1p,mkquote ex,mkquote x,mkquote a},nil);
      !*protfg := nil; simppsexplim list car oldpslim;
      return if errorp ex then nil else car ex
  end;

symbolic procedure limit1t(ex,x,a);
   begin scalar nnn, vvv,oldklist;
     oldklist := get('taylor!*,'klist);
     ex := {ex,x,a,0};
     vvv := errorset!*({'simptaylor,mkquote ex},!*backtrace);
     put('taylor!*,'klist,oldklist);
     if errorp vvv then <<if !*backtrace then break();return nil>>
      else ex := car vvv;
     if kernp ex then ex := mvar numr ex
      else return nil;
     if not eqcar(ex,'taylor!*) then return nil
       else ex := cadr ex;
    % ex is now the list of coefs and values, but we need the lowest
    % order non-zero value, which may not be the first of these.
    % if this list is empty the result is zero
    while ex and null numr cdr car ex do ex := cdr ex;
    if null ex then return (!#nnn := 0) else
       !#nnn := nnn := caaaar ex;
     vvv := cdar ex;
     return
       if tayexp!-greaterp(nnn,0) then 0
       else if nnn=0 then mk!*sq vvv
       else if !*complex then 'infinity
       else if domainp(nnn := numr vvv) then
         (if !:minusp nnn
         then aeval '(minus infinity) else 'infinity)
       else aeval{'times,{'sign,prepsq vvv},'infinity}
   end;

symbolic procedure limit1p(ex,x,a);
   begin scalar aaa, nnn, vvv;
     aaa := mk!*sq simpps1(ex,x,a);
     !#nnn := nnn := mk!*sq simppsorder list aaa;
     vvv := simppsterm1(aaa,min(nnn,0));
     return
       if nnn>0 then 0
       else if nnn=0 then mk!*sq vvv
       else if !*complex then 'infinity
       else if domainp(nnn := car vvv) then
         (if !:minusp nnn then aeval '(minus infinity)
                          else 'infinity)
       else aeval{'times,{'sign,prepsq vvv},'infinity}
   end;

symbolic procedure crlimitset(ex,x);
 (begin scalar lim1,lim2,n1,fg,limcr,!#nnn;
    lim1 := limitset(ex,x,0);
    if null lim1 then if r and c then return nil else go to a;
    if (n1 := !#nnn) < 0 or lim1 member abslims!#
       or r and c then return lim1;
 a: if not !*crlimtest then return lim1;
    if not r then on rounded; if not c then on complex;
    if not (lim2 := limitset(ex,x,0))
      or !#nnn > n1 then <<fg := t; go to ret>>;
    if !#nnn < n1 or lim2 member abslims!# then go to ret;
   % at this point, both lim1 and lim2 have values.  If they are
   % equivalent, we want lim1; otherwise lim2.
    if (limcr := topevalsetsq lim1) and
       evalequal(prepsq simp!* lim2,prepsq limcr)
         then fg := t;
ret:if not r then off rounded; if not c then off complex;
    return if fg then lim1 else lim2 end)
  where r=!*rounded,c=!*complex,!*msg=nil;

symbolic procedure topevalsetsq u;
  <<!*protfg := t;
    if not r then on rounded; if not c then on complex;
    u := errorset!*({'simp!*,{'aeval,{'prepsq,{'simp!*,mkquote u}}}},
      nil);
    !*protfg := nil;
    if not r then off rounded;if not c then off complex;
    if errorp u then nil else car u>>
  where r=!*rounded,c=!*complex,!*msg=nil;

put('times,'limsfn,'ltimesfn);
put('quotient,'limsfn,'lquotfn);
put('plus,'limsfn,'lplusfn);
put('expt,'limsfn,'lexptfn);

symbolic procedure limsimp(ex,x);
  % called when limit1 has failed, to apply more sophisticated methods.
  % output must be aeval form.
   begin scalar y,c,z,m,ex0;
      if eqcar(ex,'minus) then <<m := t; ex := cadr ex>>;
      ex0 := ex;
      if not atom ex then  % check for plus, times, or quotient.
         <<if(z := get(y := car ex,'limsfn))
              then ex := apply(z,list(ex,x))>>
         else <<if ex eq x then ex := 0; go to ret>>;
      if y eq 'plus then go to ret;
      if y eq 'expt then if ex then return ex else ex := ex0 . 1;
      if z then<<z := car ex; c := cdr ex>>
         else <<z := prepsq !*f2q numr(ex := simp!* ex);
                c := prepsq !*f2q denr ex>>;
      ex := lhopital(z,c,x,0);
 ret: if m and prepsq simp!* ex neq 'failed then
         ex := aeval lminus2 ex;
      return ex end;

symbolic procedure lminus2 ex;
   if numberp ex then -ex
   else if eqcar(ex,'minus) then cadr ex
   else list('minus,ex);

symbolic procedure ltimesfn(ex,x); specchk(ex,1,x);

symbolic procedure lquotfn(ex,x);
 %  (if eqcar(n,'expt) and (nlim :=lexptfn(n,x))
specchk(cadr ex,caddr ex,x);

symbolic procedure lexptfn(ex,x);
   if not evalequal(cadr ex,0) and
     freeof (cadr ex,x) and limit00(simp!* caddr ex,x)=0
     then 1;

symbolic procedure specchk(top,bot,x);
   begin scalar tlist,blist,tinfs,binfs,tlogs,blogs,tzros,bzros,
         tnrms,bnrms,m;
      if eqcar(top,'minus) then <<m := t; top := cadr top>>;
      if eqcar(bot,'minus) then <<m := not m; bot := cadr bot>>;
      tlist := limsort(timsift(top,x),x);
      blist := limsort(timsift(bot,x),x);
      tinfs := cdr(tlogs := logcomb(cadr tlist,x)); tlogs := car tlogs;
      binfs := cdr(blogs := logcomb(cadr blist,x)); blogs := car blogs;
      tzros := car tlist; tnrms := caddr tlist;
      bzros := car blist; bnrms := caddr blist;
      if tlogs and not blogs then
         <<top := triml append(tlogs,tnrms);
           bot := triml append(bzros,append(binfs,
              append(bnrms,trimq append(tinfs,tzros))))>>
      else if blogs and not tlogs then
         <<bot := triml append(blogs,bnrms);
           top := triml append(tzros,append(tinfs,
              append(tnrms,trimq append(binfs,bzros))))>>
      else
         <<top := triml append(cadr tlist,trimq bzros);
           bot := triml append(cadr blist,
              append(bnrms,trimq append(tzros,tnrms)))>>;
      if m then top := list('minus,top);
      return top . bot end;

symbolic procedure trimq l;
   if l then list list('quotient,1,
      if length l>1 then 'times . l else car l);

symbolic procedure triml l;
   if null l then 1 else if length l>1 then 'times . l else car l;

symbolic procedure limsort(ex,x);
   begin scalar zros,infs,nrms,q,s;
      for each c in ex do
         if (q := numr(s := simp!* limit00(simp!* c,x)))
              and numberp q and not zerop q then nrms := q . nrms
         else if null q or zerop q then zros := c . zros
         else if caaar q memq '(failed infinity) then infs := c.infs
         else nrms := (prepsq s) . nrms;
      return list(zros,infs,nrms) end;

symbolic procedure logcomb(tinf,x);
  % separate product list into log terms and others.
   begin scalar tlog,c,z;
      while tinf do
         <<c := car tinf; tinf := cdr tinf;
           if eqcar(c,'log)
              or eqcar(c,'expt) and eqcar(cadr c,'log)
              or eqcar(c,'plus) and
                (eqcar(cadr(c := logjoin(c,x)),'log)
                  or eqcar(cadr c,'minus) and eqcar(cadadr c,'log))
                    and freeof(cddr c,x)
           then tlog := c . tlog else z := c . z>>;
      return tlog . reversip z end;

symbolic procedure logjoin(p,x);
  % combine log terms in sum list into a single log.
   begin scalar ll,z;
      for each c in cdr p do
         if freeof(c,x) then z := c . z
         else if eqcar(c,'log) then ll := (cadr c) . ll
         else if eqcar(c,'minus) and eqcar(cadr c,'log) then
            ll := list('quotient,1,cadadr c) . ll
         else z := c . z;
      if ll then ll := list list('log,'times . ll);
      return (car p) . append(ll,reversip z) end;

symbolic procedure timsift(ex,x);
   if eqcar(ex,'times) then cdr ex
   else if eqcar(ex,'plus) then list logjoin(ex,x)
    % for plus, combine log terms, change infinity - infinity to
    % inner quotient.
   else list ex;

symbolic procedure lplusfn(ex,x);
  % combine logs and evaluate each limit term.  if infinity - infinity
  % is found, attempt conversion to quotient form for lhopital.
   begin scalar z,infs,nrms,vals,vp,vm,cz,vnix;
      lplus!# := lplus!# + 1;
     % write "lplus#=",lplus!#; terpri();
      if lplus!#>4 then return aeval 'failed;
      z := limsort(cdr ex,x); % ignore car z, a list of 0's.
      nrms := caddr z; infs := cadr z;
      if length infs>1 then
         <<infs := logjoin('plus . infs,x);
           infs := if eqcar(infs,'plus) then cdr infs else list infs>>;
     % at this point, only infs needs to be evaluated.
      vals := for each c in infs collect
         minfix prepsq simp!* limit00(simp!* c,x);
      z := infs;
      for each c in vals do
         <<cz := car z; z := cdr z;
           if c eq 'infinity then vp := cz . vp
           else if c = '(minus infinity) then vm := cz . vm
           else if c eq 'failed then vnix := cz . vnix
           else nrms := cz . nrms>>;
      if vm and not vp or vp and not vm or length vnix = 1
         or length vm > 1 or length vp > 1 then return aeval 'failed;
      if vm then vm := qform(car vp,vm);
      if vnix then vnix := qform(car vnix,cdr vnix);
      vm := append(nrms,append(vm,vnix));
      return if null vm then 0 else
         limit00(simp!* if length vm>1 then 'plus . vm else car vm,x)
         end;

symbolic procedure minfix v;
   if eqcar(v,'minus) and numberp cadr v then -cadr v else v;

symbolic procedure qform(a,b);
   list list('quotient,list('plus,1,
      list('quotient,if length b = 1 then car b else 'plus . b,a)),
         list ('quotient,1,a));

symbolic procedure lhopital(top,bot,xxx,a);
  begin scalar limt, limb, nvt, nvb;
     nvt := notval(limt := limfix(top,xxx,a));
     nvb := notval(limb := limfix(bot,xxx,a));
 % possibilities for lims are {failed, infinity, -infinity, bounded,
 % nonzero, zero} and each combination of cases has to be handled.
     if limt=0 and limb=0 or nvt and nvb then go to lhop;
     if specval limt or specval limb then return speccomb(limt,limb);
     if limb=0 then return aeval 'infinity;  % maybe impossible.
     return aeval list('quotient,limt,limb);
lhop: lhop!# := lhop!#+1;
    %  write "lhop#=",lhop!#; terpri();
      if lhop!#>6 then return aeval 'failed;
      return limit0(prepsq quotsq(diffsq(simp!* top,xxx),
          diffsq(simp!* bot,xxx)),xxx,a) end;

symbolic procedure notval lim;
   not lim or infinp prepsq simp!* lim;

symbolic procedure infinp x; member(x,'(infinity (minus infinity)));

symbolic procedure specval lim;
   notval lim or lim eq 'bounded;


symbolic procedure speccomb(a,b);
 aeval
  (if not a or not b or b eq 'bounded then 'failed
   else if notval b then 0
   else if notval a then
      if numberp b then
         if b>=0 then a
         else if a eq 'infinity then '(minus infinity) else 'infinity
      else ((if c then
        <<c := prepsq c;
          if evalgreaterp(c,0) then cc := 1 else if evallessp(c,0)
            then cc := -1;
          if cc then c := if a eq 'infinity then 1 else -1;
          if cc then
             if c*cc = 1 then 'infinity else '(minus infinity)
            else {'times,{'sgn,b},a}>> else {'quotient,a,b})
          where c=topevalsetsq prepsq simp!* b,cc=nil)
   else 'failed);

symbolic procedure limfix(ex,x,a);
   (if val then val
       else limitest(ex,x,a))
    where val=limitset(ex,x,a);

symbolic procedure limitest(ex,x,a);
  if ex then if atom ex then if ex eq x then a else ex else
  begin scalar y,arg,val;
    if eqcar(ex,'expt) then
       if cadr ex eq 'e then ex := list('exp,caddr ex)
       else return exptest(cadr ex,caddr ex,x,a);
    if (y := get(car ex,'fixfn)) then
       <<arg := cadr ex; val := limitset(arg,x,a);
         return apply1(y,
          if val then val else limitest(arg,x,a))>>
    else if (y := get(car ex,'limcomb)) then
       return apply3(y,cdr ex,x,a) end;

symbolic procedure exptest(b,n,x,a);
   if numberp n then
      if n<0 then limquot1(1,exptest(b,-n,x,a))
      else if n=0 then 1 else
        ((if 2*y=n then limlabs limitest(b,x,a) else limitest(b,x,a))
         where y=n/2)
   else if numberp b and b>1 then limitest(list('exp,n),x,a);

symbolic procedure limlabs a;
   if null a then nil
   else if infinp a then 'infinity
   else if a eq 'bounded then 'bounded else
     begin scalar n,d; d := denr(n := simp!* a); n := numr n;
     return if null n then a else if not numberp n then nil
         else mk!*sq abs a ./ d end;

symbolic procedure limplus(exl,x,a);
   if null exl then 0
   else limplus1(mkalg limfix(car exl,x,a),limplus(cdr exl,x,a));

symbolic procedure limplus1(a,b);
   if null a or null b then nil
   else if infinp a
      then if infinp b
         then if a eq b then a else nil else a
   else if infinp b then b
   else if a eq 'bounded or b eq 'bounded then 'bounded
   else mk!*sq addsq(simp!* a,simp!* b);

symbolic procedure limtimes(exl,x,a);
   if null exl then 1
  else ltimes1(mkalg limfix(car exl,x,a),limtimes(cdr exl,x,a));

symbolic procedure mkalg x;
   minfix if eqcar(x,'!*sq) then prepsq simp!* x else x;

symbolic procedure ltimes1(a,b);
 begin scalar c;
  return if null a or null b then nil
   else if infinp a then
         if infinp b  then
           if a = b then 'infinity else '(minus infinity)
           else if b eq 'bounded or b=0 then nil
           else if (c := limposp b) eq 'failed then nil
           else if c then a else lminus1 a
   else if infinp b then
      if a eq 'bounded or a=0 then nil
      else if (c := limposp a) eq 'failed then nil
      else if c then b else lminus1 b
   else if a eq 'bounded or b eq 'bounded then 'bounded
   else mk!*sq multsq(simp!* a,simp!* b) end;

symbolic procedure limposp a;
  (if n and not numberp n then 'failed else n and n>0)
  where n=numr simp!* a;

symbolic procedure lminus(exl,x,a);
   lminus1 mkalg limfix(car exl,x,a);

symbolic procedure lminus1 a; if a then
   if a eq 'infinity then '(minus infinity)
   else if a = '(minus infinity) then 'infinity
   else if a eq 'bounded then a
   else mk!*sq negsq simp!* a;

symbolic procedure limquot(exl,x,a);
   limquot1(mkalg limfix(car exl,x,a),mkalg limfix(cadr exl,x,a));

symbolic procedure limquot1(a,b);
 begin scalar c;
  return if null a or null b then nil
   else if infinp a then
         if infinp b then nil
           else if b eq 'bounded then nil
           else if b=0 then a
           else if (c := limposp b) eq 'failed then nil
           else if c then a else lminus1 a
   else if infinp b then 0
   else if a eq 'bounded then if b=0 then nil else 'bounded
   else if b=0 or b eq 'bounded then nil
   else mk!*sq quotsq(simp!* a,simp!* b) end;

put('log,'fixfn,'fixlog);
put('sin,'fixfn,'fixsin);
put('cos,'fixfn,'fixsin);
put('sqrt,'fixfn,'fixsqrt);
put('cosh,'fixfn,'fixcosh);
put('sinh,'fixfn,'fixsinh);
put('exp,'fixfn,'fixexp);
put('plus,'limcomb,'limplus);
put('minus,'limcomb,'lminus);
put('times,'limcomb,'limtimes);
put('quotient,'limcomb,'limquot);

symbolic procedure fixlog x;
   if zerop x then '(minus infinity) else if infinp x then 'infinity;

symbolic procedure fixsqrt x;
   if zerop x then 0 else if infinp x then 'infinity;

symbolic procedure fixsin x;
   if infinp x then 'bounded;

symbolic procedure fixcosh x;
   if infinp x then 'infinity;

symbolic procedure fixsinh x;
   if infinp x then x;

symbolic procedure fixexp x;
   if x eq 'infinity then x else if x = '(minus infinity) then 0;

% Special case rules.

algebraic let {
 limit((~a + (~b)^(~x))^(~c/x),x,infinity) => b^c
    when b freeof x and a freeof x and c freeof x};

algebraic let {
 limit((~a + (~b)^((~x)^(~n)))^(~c/((x)^n)),x,infinity) => b^c
        when (b freeof x) and a freeof x and fixp n and (n > 1)
        and c freeof x };

algebraic let {
 limit(1/(~a + (~b)^(~x))^(~c/x),x,infinity) => b^(-c)
    when b freeof x and a freeof x and c freeof x};

algebraic let {
 limit(1/(~a + (~b)^((~x)^(~n)))^(~c/((x)^n)),x,infinity) => b^(-c)
        when (b freeof x) and a freeof x and fixp n and (n > 1)
        and c freeof x };

endmodule;

end;
