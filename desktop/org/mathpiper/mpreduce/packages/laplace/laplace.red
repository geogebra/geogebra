module laplace;  % Package for Laplace and inverse Laplace transforms.

% Authors:  C. Kazasov, M. Spiridonova, V. Tomov.

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


% Date:  24 October 1988.

% Revisions:
%     5 Nov 1993   H. Melenk: adapt code for REDUCE 3.5:
%                  - safe restoration of environment.
%                  - moved *mcd/*exp:=nil after initial
%                           simp/reval call for safer pattern
%                           match
%                  - enable invlap(1/x^n,x,t) (wrong termination
%                           condition)
%                  - repair fctrf call in invlap (incomplete input
%                           conversion and incomplete result test)
%                  - repair of pattern matching for rules
%                           with 2 argument laplace and invlap
%                           expressions as used in the xmpl file
%
%
%     2 Dec 1988.  Commented out rule for sqrt(-x), since it interferes
%                  with integrator.
%    20 Nov 1988.  Converted to lower case and tabs removed.
%
%*******************************************************************
%*                                                                 *
%*                 L A P L A C E   2.0                             *
%*                                                                 *
%*      AN EXPERIMENTAL PACKAGE FOR PERFORMING IN REDUCE 3         *
%*        DIRECT  AND  INVERSE  LAPLACE  TRANSFORMATIONS           *
%*                                                                 *
%*          SOFIA  UNIVERSITY - B U L G A R I A                    *
%*                                                                 *
%*******************************************************************

create!-package('(laplace),'(contrib misc));

fluid '(!*exp !*limitedfactors !*mcd !*precise !*rounded depl!* kord!*
        subfg!* transvar!* varstack!*);

global '(lpsm!* lpcm!* lpshm!* lpchm!* lpse!* lpce!* lpshe!* lpche!*
         lpexpt!* ile1!* ile2!* ile3!* ile4!* ile5!* lpvar!* ilvar!*
         lpshift!* !*lmsg !*lmon !*ltrig !*lhyp !*ldone !*lione );

switch lhyp,lmon,ltrig;

% Default value:

!*lmsg:= t;

% put('intl,'simpfn,'simpiden);
% put('one, 'simpfn,'simpiden);
% put('delta,'simpfn,'simpiden);
% put('gamma,'simpfn,'simpiden);

if not (gettype 'intl = 'operator) then algebraic operator intl;
if not (gettype 'one = 'operator) then algebraic operator one;
if not (gettype 'delta = 'operator) then algebraic operator delta;
if not (gettype 'gamma = 'operator) then algebraic operator gamma;

%*******************************************************************
%*                                                                 *
%*            Save and restore environment                         *
%*                                                                 *
%*******************************************************************

symbolic procedure lap!-save!-environment();
  begin scalar u;
   u:={ !*exp,!*mcd,kord!*,depl!*,
        get('expt,'opmtch),
        get('sin,'opmtch),
        get('cos,'opmtch),
        get('sinh,'opmtch),
        get('cosh,'opmtch),
        get('gamma,'simpfn),
        get('one,'simpfn),
        get('delta,'simpfn),
        get('intl,'simpfn),
        get('laplace,'simpfn),
        get('invlap,'simpfn)
   };
   % copy lists such that rplac* don't touch the environment
   kord!* := append(kord!*,nil);
   depl!*:=for each d in depl!* collect append(d,nil);
   return u;
  end;

symbolic procedure lap!-restore!-environment(u);
  begin
    !*exp := car u; u := cdr u;
    !*mcd := car u; u := cdr u;
    kord!*:= car u; u := cdr u;
    depl!*:= car u; u := cdr u;
    put('expt,'opmtch, car u); u:=cdr u;
    put('sin,'opmtch, car u); u:=cdr u;
    put('cos,'opmtch, car u); u:=cdr u;
    put('sinh,'opmtch, car u); u:=cdr u;
    put('cosh,'opmtch, car u); u:=cdr u;
    put('gamma,'simpfn, car u); u:=cdr u;
    put('one,'simpfn, car u); u:=cdr u;
    put('delta,'simpfn, car u); u:=cdr u;
    put('intl,'simpfn, car u); u:=cdr u;
    put('laplace,'simpfn, car u); u:=cdr u;
    put('invlap,'simpfn, car u); u:=cdr u;
  end;


%*******************************************************************
%*                                                                 *
%*            DIRECT  LAPLACE  TRANSFORMATION                      *
%*                                                                 *
%*******************************************************************


put('laplace, 'simpfn, 'simplaplace);


lpsm!*:='( ((minus !=x))
           (nil depends (reval (quote !=x)) lpvar!* )
           (minus (times (one (minus !=x)) (sin !=x)) ) nil );

lpcm!*:='( (( minus !=x ))
           (nil depends (reval (quote !=x)) lpvar!* )
           (times (one (minus !=x)) (cos !=x)) nil );

lpshm!*:='( ((minus !=x))
           (nil depends (reval (quote !=x)) lpvar!* )
           (minus (times (one (minus !=x)) (sinh !=x)) ) nil );

lpchm!*:='( (( minus !=x ))
           (nil depends (reval (quote !=x)) lpvar!* )
           (times (one (minus !=x)) (cosh !=x)) nil );


lpse!*:= '( (!=x) (nil depends (reval(quote !=x)) lpvar!* )
   (times (one !=x) (quotient (difference (expt e (times i !=x))
                                  (expt e (minus (times i !=x))) )
                              (times 2 i) ) ) nil ) ;

lpce!*:= '( (!=x) (nil depends (reval(quote !=x)) lpvar!* )
   (times (one !=x) (quotient (plus (expt e (times i !=x))
                                  (expt e (minus (times i !=x))) )
                              2 ) )  nil ) ;

lpshe!*:= '( (!=x) (nil depends (reval(quote !=x)) lpvar!* )
    (times (one !=x) (quotient (difference (expt e !=x)
                                   (expt e (minus !=x)) )
                              2 ) ) nil );

lpche!*:= '( (!=x) (nil depends (reval(quote !=x)) lpvar!* )
   (times (one !=x) (quotient (plus (expt e !=x)
                                    (expt e (minus !=x)) )
                              2 ) ) nil );

lpexpt!*:= '( (e (plus !=x !=y)) (nil . t)
   (times (expt e !=x) (expt e !=y) (one (plus !=x !=y)) ) nil );


symbolic procedure simplaplace u;
 begin scalar e,r;
    e:=lap!-save!-environment();
    r:=errorset({'simplaplace!*,mkquote u},nil,nil);
    lap!-restore!-environment(e);
    if errorp r then typerr('laplace.u,"Laplace form")
     else return laplace_fixup car r
   end;

symbolic procedure laplace_fixup u;
   % For some reason, results do not always come out in the most
   % natural form.  This is an attempt to fix this.
   <<put('laplace,'simpfn,'simpiden);
     u := simp aeval!* prepsq u;
     put('laplace,'simpfn,'simplaplace);
     u>> where varstack!* = nil;

symbolic procedure simplaplace!*  u;
 % Main procedure for Laplace transformation.
 % U is in prefix form: (<expr> <lp.var> <il.var>), where
 %   <expr> is the object function,
 %   <lp.var> is the var. of the object function (intern. lp!&),
 %   <il.var> is the var. of the laplace transform(intern. il!&),
 %      and can be omitted - then il!& is assumed.
 % Returns a standard quotient of Laplace transform.
 begin scalar !*exp,!*mcd,v,w,transvar!*,!*precise;
   % We need to make this run with precise on.
   if null subfg!* then return mksq('laplace . u, 1);
   if cddr u and null idp(w:=caddr u) or null idp(v:=cadr u)
      then go to err;
   v:= caaaar simp v;
   transvar!* := w;     % Needed for returning a Laplace form.
   % Should the following be an error?
   if null transvar!* then transvar!* := 'il!&;
   if null idp v then go to err;
   u:= car u ;
 % Make environment for Laplace transform.
   !*mcd := !*exp := t;
   kord!*:= 'lp!& . 'il!& . kord!* ;
   put('one,'simpfn,'lpsimp1);
   put('gamma,'simpfn,'lpsimpg);
   if !*ldone then put('expt,'opmtch,lpexpt!*.get('expt,'opmtch));
   if !*lmon then
    << put('sin,'opmtch, lpse!* . get('sin,'opmtch));
       put('cos,'opmtch, lpce!* . get('cos,'opmtch));
       put('sinh,'opmtch, lpshe!* . get('sinh,'opmtch));
       put('cosh,'opmtch, lpche!* . get('cosh,'opmtch)) >>
             else
    << put('sin,'opmtch, lpsm!* . get('sin,'opmtch));
       put('cos,'opmtch, lpcm!* . get('cos,'opmtch));
       put('sinh,'opmtch, lpshm!* . get('sinh,'opmtch));
       put('cosh,'opmtch, lpchm!* . get('cosh,'opmtch)) >>;
   lpvar!*:= v; lpshift!*:=t;
   if v neq 'lp!& then kord!*:=v . kord!*;
   for each x in depl!* do if v memq cdr x then rplacd(x,'lp!& . cdr x);
   % HM: resimplify u for rules before mcd goes off.
   % ACH: However, it gives wrong results e.g. for laplace(sin(-x),x,p)
%  rmsubs(); u := reval u;
   off mcd;
   u:= laplace1 list(u,v);
   if w then u:=subf(numr u, list('il!& . w));
 % Restore old env.
   for each x in depl!* do
      if 'lp!& memq cdr x then rplacd(x,delete('lp!&,cdr x));
   put('one,'simpfn,'simpiden);
   put('gamma,'simpfn,'simpiden);
   kord!*:= cddr kord!*;
   put('sin,'opmtch, cdr get('sin,'opmtch) );
   put('cos,'opmtch, cdr get('cos,'opmtch) );
   put('sinh,'opmtch, cdr get('sinh,'opmtch) );
   put('cosh,'opmtch, cdr get('cosh,'opmtch) );
   if !*ldone then put('expt,'opmtch,cdr get('expt,'opmtch) );
   if erfg!* then erfg!*:=nil;
   return u;
 err: msgpri("Laplace operator incorrect",nil,nil,nil,t)
   end where !*exp = !*exp, !*mcd = !*mcd;

put('sin,'lpfn,'(quotient k (plus (expt il!& 2) (expt k 2) )) );
put('cos,'lpfn,'(quotient il!& (plus (expt il!& 2) (expt k 2) )) );
put('sinh,'lpfn,'(quotient k (plus (expt il!& 2)
                                   (minus (expt k 2)) )) );
put('cosh,'lpfn,'(quotient il!& (plus (expt il!& 2)
                                      (minus (expt k 2)) )) );
put('one,'lpfn,'(quotient 1 il!&) );
put('expt,'lpfn,'(quotient (times (expt k d) (gamma (plus d 1)) )
                           (expt il!& (plus d 1)) ) );
put('delta,'lpfn, 1 );


symbolic procedure laplace1 u;
 % Car u is in pref. form, cadr u is the var of the object function.
 % Returns standard quotient of Laplace transform.
 begin  scalar  v,w,z;
   v := cadr u;
   u := car u;
   z:= simp!* u;
   if denr z neq 1 then z := simp prepsq z;   % *SQ must have occurred.
   if denr z neq 1 then rederr list(u,"has non-trivial denominator");
   z := numr z;
   if v neq 'lp!& then << kord!*:=cdr kord!*;
                        z:=subla(list(v.'lp!&),z); z:=reorder z >>;
   if erfg!* then return !*kk2q list
       ('laplace, subla(list('lp!& . lpvar!*), u), lpvar!*,transvar!*);
   w:= nil ./ 1;  u:=z; !*exp:=nil;
   while u do
     if domainp u
        then << w:=addsq(w, lpdom u); u:=nil >>
        else << w:=addsq(w, if (z:=lptermx lt u) then z
                     else !*kk2q list('laplace, subla
          (list('lp!&.lpvar!*),prepsq !*t2q lt u),lpvar!*,transvar!*));
                u:= red u >>;
   return w;
 end;

symbolic procedure lptermx  u ;
 % U is standard term, which may contain integer power of lp!&.
 % Returns standard quot or nil, if Laplace transform is impossible.
 begin  scalar  w ; integer  n ;
   if tvar u neq 'lp!& then return lpterm u
    else if fixp cdar u
     then if (n:=cdar u)>0 then nil else return lpunknown u
    else return lpterm
       ( (list('expt,'lp!&,prepsq(cdar u ./ 1)) to 1) .* cdr u );
   if (w:=lpform cdr u) then nil else return nil ;
  a: % We use here the rule:
     %  laplace(x*fun(x),x)=-df(laplace(fun(x),x),il!&) ,or
     %  laplace(x**n*fun(x),x)=(-1)**n*df(laplace(fun(x),x),il!&,n);
   if n=0 then return w;
   w:=negsq diffsq(w,'il!&);
   n:=n-1; go to a;
 end;

symbolic procedure lpdom  u ;
 % We use here the rule: laplace(const,lp!&)=const/lp!&.
 % U is domain. Returns standard quotient.
   !*t2q (('il!& to -1) .* u) ;

symbolic procedure  lpform u ;
 % U is standard form, not containing integer powers of lp!&.
 % Returns standard quot or nil, if Laplace transform is impossible.
 begin  scalar  y,z ;
   if domainp u
      then return lpdom u
      else if red u
              then return
        ( if (y:=lpterm lt u) and (z:=lpform red u)
             then addsq(y,z) else nil )
              else return lpterm lt u ;
 end ;

symbolic procedure  lpterm  u ;
 % U is standard term, not containing integer powers of lp!&.
 % Returns standard quot or nil, if Laplace transform is impossible.
 begin  scalar  v,w,w1,y,z ;
   v:=car u; % l.pow. - the first factor.
   w:=cdr u; % l.coeff. - i.e. st.f.
   if atom (y:=car v) or atom car y % I.e. atom or Lisp func.
      then if not depends(y,'lp!&)
              then return if (z:=lpform w)
                             then multpq(v,z) else nil
              else if atom y then return lpunknown u
              else if car y = 'expt
                   then return lpexpt(v,nil,w) else nil % Go next.
      else return if not depends(prepsq(y./1),'lp!&)
                     then if (z:=lpform w)
                             then multpq(v,z) else nil
                     else lpunknown u;
 % We can't handle v now, because nothing is known for w for now.
   if domainp w then return lpfunc(v,w);
 % If we have sum, and off exp.
   if cdr w then return if (y:=lpterm list(v,car w)) and
       (z:=lpterm(v . cdr w))then addsq(y,z) else nil;
   w1:=cdar w; % l.coeff - i.e. st.f.
   w :=caar w; % l.pow. - the second factor.
  if not depends(if domainp(y:=car w) then y else prepsq(y./1),'lp!&)
      then return if (z:=lpterm(v.w1)) then multpq(w,z) else nil
      else if car y = 'expt then return lpexpt(w,v,w1);
 % Now we have multiply of two functions.
   if caar v = 'one and caar w = 'one
      then return lpmult1(v,w,w1)
      else return lpunknown u;
 end ;

symbolic procedure  lpunknown  u ;
 % Try to apply any previously given let rules for Laplace operator.
 % U is standard term.
 % Returns standard quotient or nil if matching not successful.
 begin  scalar  d,z,w;
   if domainp (d:=cdr u)  and  not !:onep d
      then (u:= !*p2q car u) else (u:= !*t2q u);
   u:= list('laplace, prepsq u, 'lp!&,transvar!*);
   w:= list('laplace, cadr u,'lp!&); % HM: short rule form
   if get('laplace,'opmtch) and
      ( (z:=opmtch u) or (z:=opmtch w))
      then << !*exp:=t;
              put('laplace,'simpfn,'laplace1);
              z:=simp z; !*exp:=nil;
              put('laplace,'simpfn,'simplaplace) >>;
   if null z then return if !*lmsg
      then msgpri("Laplace for", subla(list('lp!& . lpvar!*), cadr u),
                  if !*lmon or atom cadr u then "not known"
                  else "not known - try ON LMON",nil,nil)
      else nil;
   z:=subla(list('lp!&.lpvar!*), z);
   return if domainp d and not !:onep d then multsq(z,d./1) else z;
 end ;

symbolic procedure  lpsimp1  u ;
 % Simplify the one-function. % U is in prefix form.
 % Returns standard quotient or nil ./ 1 if an error occurs.
 begin  scalar  v,l,r ;
   v:=subla(list(lpvar!* . 'lp!&),u);
   if not depends(car v,'lp!&) then return 1 ./ 1;
   v:= car simpcar v; % Standard form.
   if mvar v neq 'lp!& then << !*mcd:=t; v:=subf(v,nil); !*mcd:=nil;
                               v:=multf(car v, recipf!* cdr v) >>;
   if not(mvar v eq 'lp!&  and  !:onep ldeg v)
      then go to err;
   l:=lc v; r:=red v;  % Standard form.
   if null r then if minusf l then go to err else return 1 ./ 1;
   v:=if minusf l then multsq(negf r ./ 1, 1 ./ negf l)
                  else multsq(r ./ 1, 1 ./ l);
   if not minusf numr v then return 1 ./ 1;
   if null lpshift!* then go to err
      else return mksq(list('one,prepsq addsq(!*k2q 'lp!&, v)), 1);
 err: if !*lmsg then msgpri("Laplace induces", 'one.u,
          " which is not allowed", nil, 'hold);
   return nil ./ 1;
 end ;

symbolic procedure  lpsimpg  u ;
 % Simplifies gamma(k), if k is rational and semiinteger.
 % U is in prefix form. Returns standard quotient.
 begin  scalar  n,v ;
   u:= simpcar cdr u;  % Gamma is now flagged "full".
   if denr u neq 1
     % Maybe we can do better than this.
     then return mksq(list('gamma,prepsq u),1);
   u := car u;
   if domainp u and eqcar(u,'!:rn!:) and (cddr u = 2) % Semiint.
      then return if (n:=cadr u) = 1
                     then mksq(list('sqrt,'pi),1)
                     else if n > 0 then
    << v:='!:rn!: . difference(n,2) . 2 ;
     resimp !*t2q ( (list('gamma,rnprep!: v) to 1) .* v ) >>
                                   else % N negative.
  resimp !*t2q ( (list('gamma,rnprep!:('!:rn!:.plus(n,2) . 2)) to 1)
                 .* ('!:rn!:.(-2).(-n)) )
      else return mksq(list('gamma,prepsq(u./1)),1);
 end ;

symbolic procedure  lpmult1 (u,v,w) ;
 % Perform: one(l1*lp!&-r1)*one(l2*lp!&-r2) = one(l*lp!&-r),
 %   where l,r are those for the rightmost shifted one-function.
 % U and v are standard powers for one-func., w is leading coeff.
 % Returns standard quotient if all coeff. are domains, otherwise nil.
 begin  scalar  u1,v1,l1,r1,l2,r2 ;
   u1:= car simp cadar u;
   v1:= car simp cadar v;
   l1:=lc u1; l2:=lc v1;
   r1:=red u1; r2:=red v1;
   if domainp l1 and domainp l2 and domainp r1 and domainp r2
      then if !:minusp adddm(multdm(r1,l2), !:minus multdm(r2,l1))
              then return lpterm(u . w)
              else return lpterm(v . w)
      else return lpunknown list(u, v.w);
 end ;

symbolic procedure  lpexpt (u,v,w) ;
 % Perform the rule: laplace(e**(l*lp!&)*fun(lp!&), lp!&) =
 %                   sub(il!&=il!&-l, laplace(fun(lp!&),lp!&)),
 %   or call lpfunc for gamma-function.
 % U is lpow for expt-func, v is other lpow or nil.  W is lcoeff.
 % Returns standard quotient or nil.
 begin  scalar  p,q,r,z,l,la ;
   r:=cdr u;  % Degree for expt-func.
   p:=cadar u;  % First arg for expt.
   q:=caddar u;  % Second arg for expt.
   if depends(p,'lp!&) then go to gamma;
   !*exp:=t; q:=car simp q;
   if mvar q neq 'lp!& then << !*mcd:=t; q:=subf(q,nil); !*mcd:=nil;
                               q:=multf(car q, recipf!* cdr q) >>;
   if not !:onep r then q:=multf(q,r); !*exp:=nil;
   if not(mvar q eq 'lp!& and !:onep ldeg q)
      then return if null v then lpunknown(u . w)
                            else lpunknown list(u, v . w);
   if (r:=red q) then
     << if !*ldone then << !*exp:=t;
          w:=multf(w, car lpsimp1 list prepsq(q./1)); !*exp:=nil >>;
        q:=list(lt q); r:=!*p2q(list('expt,p,prepsq(r./1)) to 1) >>;
   if p neq 'e then q:=multf(q, !*kk2f list('log,p) );
   z:= if null v then lpform w else lpterm(v.w);
   if null z then return nil;
   l:= prepsq !*f2q lc q;
   la:=list('il!& . list('difference,'il!&,l) );
   % Provide for those forms that contain the true transform variable.
   if not(transvar!* eq 'il!&)
     then z := subsq(z,list(transvar!* . 'il!&));
   z:=subf(numr z,la);
   return if r then multsq(r,z) else z;
 gamma: % Check and call lpfunc for gamma-func.
   return if null v
      then if domainp w
              then lpfunc(u,w)
              else % if off exp
 % if red w then if (z:=lpexpt(u,v,list(car w)) ) and
 %  (l:=lpexpt(u,v,cdr w)) then addsq(z,l) else nil else
                   if not depends((l:=mvar w),'lp!&)
                      then if (z:=lpexpt(u,nil,lc w))
                              then multpq(lpow w,z) else nil
                      else if not atom l and car l = 'expt
                              then lpexpt(lpow w,u,lc w)
                              else lpunknown(u . w)
      else lpunknown list(u, v . w);
 end ;

symbolic procedure  lpfunc (u,v) ;
 % Perform Laplace transform for intl-operator and simple functions:
 % expt(arg,const), sin,cos,sinh,cosh,one,
 % with args: k*lp!&-tau, where k>0, tau>=0 are const.
 % U is standard power, v a domain element.
 % Returns standard quotient or nil.
 begin  scalar  ld,fn,w,var,ex,k,tau,c ;
   ld:=cdr u;  % Degree of func.
   w:=car u;   % Func in prefix form.
   fn:=car w;  % Name of func.
 lintl: if fn neq 'intl then go to lexpt;
 % Perform Laplace(intl(<expr>,<var>,0,lp!&), lp!&).
   if not ( !:onep ld  and  cadddr w =0  and
            car cddddr w = 'lp!&  and  idp(var:=caddr w) )
      then return if !*lmsg then msgpri("Laplace integral",
           subla(list('lp!& . lpvar!*), prepsq !*p2q u),
           "not allowed", nil, nil) else nil;
   ex:= subla(list(var . 'lp!&), cadr w);
   lpshift!*:=nil;  w:= laplace1 list(ex,'lp!&); lpshift!*:=t;
 return if w then multsq(multd(v,!*p2f('il!& to -1))./1, w) else nil;
 lexpt: if fn neq 'expt then go to lfunc;
 % Perform Laplace(expt,(k*lp!&-tau),d), for d - not int. const.
   ld:= multf(ld, car simp caddr w);
   if minusf(addd(1,ld))  or  depends(prepsq(ld./1), 'lp!&)
      then return lpunknown(u.v);
   ld:= prepsq !*f2q ld;
 lfunc: % Perform Laplace transform for simple and one-function.
   if fn = 'expt  or (fn = 'one) or  !:onep ld
      then nil else return lpunknown(u.v);
   !*exp:=t; ex:= car simp cadr w; !*exp:=nil;
   if not( mvar ex = 'lp!&  and  !:onep ldeg ex )
      then return lpunknown(u.v);
   k:=lc ex; tau:=red ex;
   if minusf k or (null lpshift!* and tau) then return
       if !*lmsg then msgpri("Laplace for",
    subla(list('lp!&.lpvar!*), w),"not allowed",nil,nil) else nil;
   if tau  and  not minusf tau  then return lpunknown(u.v);
   c:= prepsq !*f2q k;
 % Ind. lpfn gives Laplace transform for func(k*lp!&).
   if (w:= get(fn,'lpfn))
      then w:=car simp subla(list('k.c, 'd.ld), w);
   return if null w
             then lpunknown(u.v)
             else if null tau
                     then multd(v, w) ./ 1
                     else multd(v, multf( w,!*kk2f list
  ('expt,'e,prepsq multsq(!*k2q 'il!&, quotsq(tau./1, k./1)) )
                               ) ) ./ 1 ;
 end ;

% Tables for Explicit Transforms for Delta Function.  Note explicit
% construction for difference of arguments to reflect parser.

algebraic;

for all x,y,z let laplace(z*delta x,x,y) = sub(x=0,z);

for all k,x,y,z let laplace(z*delta(x+(-k)),x,y) = e**(y*-k)*sub(x=k,z);

for all x,y let laplace(df(delta x,x),x,y) = y;

for all n,x,y let laplace(df(delta x,x,n),x,y) = y**n;

for all k,x,y let laplace(df(delta(x+(-k)),x),x,y) = y*e**(-k*y);

for all k,n,x,y let laplace(df(delta(x+(-k)),x,n),x,y) = y**n*e**(-k*y);

symbolic;


%*******************************************************************
%*                                                                 *
%*              INVERSE  LAPLACE  TRANSFORMATION                   *
%*                                                                 *
%*******************************************************************


put('invlap, 'simpfn, 'simpinvlap);

ile1!*:='( (e (times i !=x))
           (nil depends(reval (quote !=x)) lpvar!*)
           (plus (cos !=x) (times i (sin !=x)))  nil );

ile2!*:='( (e (minus (times i !=x)))
           (nil depends(reval (quote !=x)) lpvar!*)
           (difference (cos !=x) (times i (sin !=x)))  nil );

ile3!*:='( (e !=x )
           (nil depends(reval (quote !=x)) lpvar!*)
           (plus (cosh !=x) (sinh !=x))  nil );

ile4!*:='( (e (minus !=x))
           (nil depends(reval (quote !=x)) lpvar!*)
           (difference (cosh !=x) (sinh !=x))  nil );

ile5!*:='( (e (plus !=x !=y))
           (nil and (not(depends(reval(quote !=x)) (quote i)))
                    (depends(reval(quote !=y)) (quote i)) )
           (times (expt e !=x) (expt e !=y)) nil );

symbolic procedure simpinvlap u;
  begin scalar r,e;
   e:=lap!-save!-environment();
   r:=errorset({'simpinvlap!*,mkquote u},nil,nil);
   lap!-restore!-environment e;
   if errorp r then typerr('invlap.u,"Laplace form")
    else return invlap_fixup car r
  end;

symbolic procedure invlap_fixup u;
   % For some reason, results do not always come out in the most
   % natural form.  This is an attempt to fix this.
   <<put('invlap,'simpfn,'simpiden);
     u := simp aeval!* prepsq u;
     put('invlap,'simpfn,'simpinvlap);
     u>> where varstack!* = nil;

symbolic procedure simpinvlap!*  u ;
 % Main procedure for inverse Laplace transformation.
 % U is in prefix form: (<expr> <il.var> <lp.var>) ,where
 %   <expr> is the laplace transform,
 %   <il.var> is the var. of the Laplace transform (intern. il!&),
 %   <lp.var> is the var. of the object function (intern. lp!&),
 %      and can be omitted - then lp!& is assumed.
 % Returns a standard quotient of inverse Laplace transform.
 begin scalar !*exp,!*mcd,v,w,!*precise;
   % We need to make this run with precise on.
   if null subfg!* then return mksq('invlap . u, 1);
   if cddr u and null idp(w:=caddr u) then go to err;
   v:= caaaar simp cadr u;
   transvar!* := w;
   if null idp v then go to err;
   u:= car u ;
 % Make environment for invlap transform.
   !*exp := !*mcd := nil;
   kord!*:= 'il!& . 'lp!& . kord!* ;
   put('gamma,'simpfn,'lpsimpg);
   put('one,'simpfn,'ilsimp1);
   ilvar!*:=v; if v neq 'il!& then kord!*:=v.kord!*;
   for each x in depl!* do if v memq cdr x then rplacd(x,'il!& . cdr x);
   u:= invlap1 list(u,v);
   put('invlap,'simpfn,'simpiden);
   if w then << lpvar!*:=w; u:=subla(list('lp!& . w), u) >>
        else lpvar!*:='lp!& ;
   if !*ltrig or !*lhyp then << !*exp:=t;
 if !*lhyp then put('expt,'opmtch,ile3!*.ile4!*.get('expt,'opmtch));
 if !*ltrig then put('expt,'opmtch,ile1!*.ile2!*.get('expt,'opmtch));
       put('expt,'opmtch, ile5!*.get('expt,'opmtch));
       u:= simp prepsq u;
       if !*ltrig and !*lhyp
          then put('expt,'opmtch, cdr cddddr get('expt,'opmtch))
          else put('expt,'opmtch, cdddr get('expt,'opmtch)) >>
                        else u:= resimp u;
 % Restore old env.
   for each x in depl!* do
      if 'il!& memq cdr x then rplacd(x,delete('il!&,cdr x));
   put('gamma,'simpfn,'simpiden);
   put('one,'simpfn,'simpiden);
   kord!*:= cddr kord!*;
   return u;
 err: msgpri("Invlap operator incorrect",nil,nil,nil,t);
 end where !*exp = !*exp, !*mcd = !*mcd;

symbolic procedure invlap1 u;
 % Car U is in prefix form, cadr u is the var of the Laplace transform.
 % Returns standard quotient of inverse Laplace transform.
 begin  scalar  v,w,z;
   v := cadr u;
   u := car u;
   z:= simp!* u;
   if denr z neq 1 then z := simp prepsq z;   % *SQ must have occurred.
   if denr z neq 1 then rederr list(u,"has non-trivial denominator");
   z := numr z;
   u := z;
   if v neq 'il!& then << kord!*:=cdr kord!*;
                     u:=subla(list(v.'il!&),u); u:=reorder u >>;
   w:= nil ./ 1;
   while u do
     if domainp u
       then << w:=addsq(w, !*t2q((list('delta,'lp!&) to 1) .* u) );
               u:= nil >>
       else << w:=addsq(w, if (z:=ilterm (lt u,1,1,nil)) then z
                     else !*kk2q list('invlap, subla
        (list('il!&.ilvar!*),prepsq !*t2q lt u), ilvar!*,transvar!*));
               u:= red u >>;
   return w;
 end;

symbolic procedure ilterm (u, numf, denf, rootl) ;
 % U is standard term, numf is standard form, with one term, and
 % contains only powers from numerator of expression, depends on il!&,
 % but not exponent.  Denf is standard form, with one term, and
 % contains only powers from denominator of expression, depends on il!&
 % but not exponent.  Rootl is assoc. list of: (<root> . <multiplity>).
 % Returns standard quotient, or nil if inverse Laplace transform is
 % impossible.
 begin  scalar  v,v1,v2,w,y,z,p,p1 ;
   v:=car u; w:=cdr u; v1:=car v; v2:=cdr v;
   if not depends(if domainp v1 then v1 else prepsq(v1./1), 'il!&)
      then return if (z:=ilform(w,numf,denf,rootl))
                     then multpq (v,z) else nil;
   % V depends on il!&.
   if atom v1
         % the following clause "if n1 neq il& then" introduced by HM
       then (if not(v1 = 'il!&) then return ilunknown(u,numf,denf))
    else if atom car v1  % I.e. Lisp func.
     then return
           if car v1 = 'expt
            then ilexpt(v,nil,w,numf,denf,rootl)
           else if domainp w
            then ilexptfn(v,w,numf,denf)
           else if cdr w
            then if(y:=ilterm(list(v,lt w),numf,denf,rootl))
                       and (z:=ilterm(v.cdr w,numf,denf,rootl))
                  then addsq(y,z)
                 else nil
           else ilterm(list(lpow w,v.(lc w)),numf,denf,rootl);

   % May be infinite recursion above, if mult. of two unknown func.
   % Mvar is atom 'il!& or standard form, since exp off.
   if numberp v2 and fixp v2
    then
     if v2 > 0
      then
       if atom v1
        then return ilform(w, multf(!*p2f v,numf), denf, rootl)
        else nil
      else return ilroot(v, w, numf, denf, rootl)
    else return
          ilexpt(list('expt,
                      if domainp v1 then v1 else prepsq(v1./1),
                      prepsq(v2./1)) to 1, nil, w, numf,
                      denf, rootl);

   % Now v1 remains as a standard form and v2>0.
   v:= if !:onep v2 then v1 else !*p2f v;
   if red v1 then
      << !*exp:=t; y:=numr subf(v,nil); z:=y;
         while z do if domainp z then z:=nil
            else if ldeg z < 0 then if depends
          (if domainp(p1:=mvar z) then p1 else prepsq(p1 ./1), 'il!&)
                then << p:=t; z:=nil >> else z:=addf(lc z, red z)
              else z:=addf(lc z,red z);
         if p then w:=multf(y, w) else numf:=multf(v,numf);
         !*exp:=nil >>  else numf:=multf(v,numf);
   return ilform(w,numf,denf,rootl);
 end;

symbolic procedure  ilform (u, numf, denf, rootl) ;
 % U is a standard form.  Numf, denf, rootl are the same as in ILTERM.
 % Returns standard quotient or nil if invlap is impossible.
 begin  scalar  y,z ;
   return if domainp u
             then if (z:=ilresid(numf,denf,rootl))
                     then multsq(u ./ 1, z) else nil
             else if null red u
                     then ilterm(lt u,numf,denf,rootl)
                     else if (y:=ilterm(lt u,numf,denf,rootl)) and
                             (z:=ilform(red u,numf,denf,rootl))
                             then addsq(y,z) else nil;
 end ;

symbolic procedure  ilunknown (u, numf, denf) ;
 % We try here to apply any previously given let rules for Laplace
 % operator.  U is standard term, numf, denf are the same.
 % Returns standard quotient or nil if matching not successful.
 begin  scalar  d,z,w;
   if domainp (d:=cdr u) then if !:onep d
          then u:=!*t2q u else u:=!*p2q car u
      else u:=!*t2q u;
   if numf neq 1 then u:=multsq(u, numf./1);
   if denf neq 1 then u:=multsq(u,1 ./denf);
   u:= list('invlap, prepsq u,'il!&,transvar!*);
       % HM: alternative shorter form for rule match
   w:= list('invlap, cadr u, 'il!&);
   if get('invlap,'opmtch) and
        ((z:=opmtch u) or (z:=opmtch w))
      then << !*exp:=t;
              put('invlap,'simpfn,'invlap1);
              z:=simp z; !*exp:=nil;
              put('invlap,'simpfn,'simpinvlap) >>;
   if null z and !*lmsg then msgpri("Invlap for",
                     subla(list('il!& . ilvar!*), cadr u),
                     "not known", nil, nil);
   return if null z then nil
      else if domainp d and not !:onep d
              then multsq(z, d ./ 1) else z;
 end ;

symbolic procedure  ilsimp1  u ;
 % Simplify the one-function.  U is in prefix form.
 % Returns standard quotient.
 if atom car u then 1 ./ 1 else mksq('one . u, 1);

symbolic procedure  ilexpt (u, v, w, numf, denf, rootl) ;
 % Perform the rule: invlap(e**(-l*il!&)*fun(il!&), il!&) =
 %     sub(lp!&=lp!&-l, invlap(fun(il!&),il!&)), for l > 0,
 % or call ilfunc for gamma-function.
 % U is lpow for expt-function, v is other lpow or nil,
 % W is lcoeff (standard form), numf, denf, rootl are the same.
 % Returns standard quotient or nil.
 begin  scalar  p,q,r,z,l ;
   r:=cdr u;  % Degree for expt-func.
   p:=cadar u;  % First arg for expt.
   q:=caddar u;  % Second arg for expt.
   if depends(p,'il!&)then go to gamma;
   !*exp:=t; q:=car simp q;
   if mvar q neq 'il!& then << !*mcd:=t; q:=subf(q,nil); !*mcd:=nil;
                               q:=multf(car q, recipf!* cdr q) >>;
   if not !:onep r then q:=multf(q,r); !*exp:=nil;
   if not((mvar q = 'il!&) and !:onep ldeg q and minusf lc q)
      then return if null v then ilunknown(u.w,numf,denf)
                            else ilunknown(list(u,v.w),numf,denf);
   if (r:=red q) then<< q:=list(lt q);
                        r:=!*p2q(list('expt,p,prepsq(r./1)) to 1) >>;
   if p neq 'e then q:=multf(q, !*kk2f list('log,p) );
   z:= if null v then ilform(w,numf,denf,rootl)
                 else ilterm(v.w,numf,denf,rootl);
   if null z then return nil;
   l:= list('plus, 'lp!&, prepsq((lc q)./1));
   z:= subf(numr z, list('lp!& . l) ) ;  % Standard quotient.
 % If you want shifted one-func. to remain always in obj. func.
   if !*lione then z:=multsq(z, !*kk2q list('one,l) );
   return if r then multsq(r,z) else z ;
 gamma: % Check and call ilfunc if gamma-func. case.
   return if null v
     then if domainp w
       then ilexptfn(u,w,numf,denf)
       else if red w
         then if (z:=ilexpt(u,nil,list(car w),numf,denf,rootl)) and
                 (l:=ilexpt(u,nil,cdr w,numf,denf,rootl))
                 then addsq(z,l) else nil
         else if not depends(if domainp(l:=mvar w) then l
                                else prepsq(l./1), 'il!&)
           then if (z:=ilexpt(u,nil,lc w,numf,denf,rootl))
                   then multpq(lpow w,z) else nil
           else if not atom l and (car l = 'expt)
             then ilexpt(lpow w,u,lc w,numf,denf,rootl)
             else if atom l or not atom car l
               then ilterm(list(lpow w,u.(lc w)),numf,denf,rootl)
               else ilunknown(u.w,numf,denf)
     else ilunknown(list(u,v.w),numf,denf) ;
 end ;

symbolic procedure  ilexptfn (u, v, numf, denf) ;
 % Perform invlap for expt function - i.e., gamma-function case.
 % U is standard power for expt, v is domain, numf, denf the same.
 % Returns standard quotient or nil.
 begin  scalar  ex,dg,fn,k,a,b,y,d ;
   ex:=car u; dg:=cdr u; fn:=car ex;
   if fn neq 'expt then go to unk;
   d:=caddr ex; if atom(ex:=cadr ex) then k:=t;
   !*exp:=t; ex:=car simp ex;
   dg:=multd(dg,car simp d); a:=lc ex;
   if not(domainp a and !:onep a) then
     << ex:=multf(ex, recipf!* a);
        a:=!*kk2f list('expt,prepsq(a./1),prepsq(dg./1)) >>;
   b:=red ex; !*exp:=nil;
   if (mvar ex neq 'il!&) or (ldeg ex neq 1) or
      depends(prepsq(b./1),'il!&) then go to unk;
   if (numf=1) and (denf=1) then go to ret;
 % We must have identical monomials in numf, denf and in expt-func.
   y:= multf(multf(numf, !*kk2f list('expt,
       prepsq(ex./1),prepsq(dg./1)) ), recipf!* denf);
   if cdr y or (lc lc y neq 1) or (car mvar lc y neq 'expt)
      or (not k and (mvar y neq ex))
      or (k and (mvar y neq mvar ex)) then go to unk;
   dg:=addd(ldeg y,dg);
 ret: if minusf dg then d:=prepsq(negf dg ./ 1) else go to unk;
   if (y:=get(fn,'ilfn))
      then y:=car simp subla(list('d.d), y) else go to unk;
   if b then y:=multd(v, multf(y, !*kk2f list
          ('expt,'e,prepsq(multf(!*k2f 'lp!&,negf b) ./1)) ))
        else y:=multd(v, y);
   return if domainp a and !:onep a then y./1 else multf(a,y)./1;
 unk: return ilunknown(u.v, numf, denf);
   end   ;

put('expt,'ilfn,'(quotient (expt lp!& (plus d (minus 1))) (gamma d)));

symbolic procedure  addrootl (root,mltpl,rootl) ;
 % Add roots with multiplity at head of rootl - an assoc. list.
 begin  scalar  parr ;
   parr:=assoc(root,rootl);
   if parr then << mltpl:= mltpl + cdr parr;
                   rootl:= delete(parr,rootl) >>;
   return (root . mltpl) . rootl ;
 end     ;

symbolic procedure  recipf!*  u ;
 % U is standard form. Returns st.f. for u to (-1), by off mcd.
 begin scalar  d;
   if domainp u then if !:onep u then return 1
        else if !:onep negf u then return -1
          else if fieldp u then nil
            else if (d:=get(dmode!*,'i2d))
              then u:=apply1(d,u) else u:=mkratnum u
      else return if cdr u then !*p2f(u to (-1))
        else multf(!*p2f(mvar u to (-ldeg u)), recipf!* lc u);
   return dcombine(1,u,'quotient);
 end   ;

symbolic procedure ilroot (u,v,numf,denf,rootl);
 % Find the roots of polynomial of first and second degree.
 % U is standard power - the polynomial, v is the remaining st.f.
 % Numf, denf, rootl are the same. Returns standard quot or nil.
 begin  scalar  dg,ex,a,b,c,z,x1,x2 ;
   dg:=-cdr u; ex:=car u; % dg>0;
   if atom ex then return ilform(v,numf,
           multf(!*p2f('il!& to dg),denf), addrootl(nil,dg,rootl) );
   if atom car ex then return ilunknown(u.v,numf,denf);
   !*exp:=t; ex:=subf(ex,nil); !*exp:=nil;
   if not depends(prepsq ex, 'il!&) then return
      if (z:=ilform(v,numf,denf,rootl)) then multpq(u,z) else nil;
   ex:=car ex;
   if ldeg ex > 2 then return il3pol(u,v,numf,denf,rootl);
   a:=lc ex;
   if depends(prepsq(a./1),'il!&)
      then return ilunknown(u.v,numf,denf);
   if not(domainp a and !:onep a) then
      << !*exp:=t; a:=recipf!* a; ex:=multf(ex,a);
         if dg>1 then a:=exptf(a,dg); !*exp:=nil >>;
   if ldeg ex = 2 then go to lbin;
 lmon: if (b:=red ex)
          then << rootl:=addrootl(negf b, dg, rootl);
                  denf:= if !:onep dg then multf(ex, denf)
                            else multpf(ex to dg, denf) >>
          else << rootl:=addrootl(nil, dg, rootl);
                  denf:= multpf('il!& to dg, denf) >>;
   go to ret;
 lbin: if (b:=red ex)
          then if domainp b then << c:=b; b:=nil >>
             else if mvar b = 'il!& then << c:=red b; b:=lc b >>
                                    else << c:=b; b:=nil >>
          else c:=nil ;
   if depends(prepsq(b./1),'il!&) or depends(prepsq(c./1),'il!&)
      then return ilunknown(u.v,numf,denf);
   if null b and null c
     then << rootl:=addrootl(nil, 2*dg, rootl);
             denf:=multpf('il!& to (2*dg), denf) >>
     else << !*exp:=t; b:=multd('!:rn!: . ((-1) . 2), b);
       c := simp list('sqrt,prepsq(addf(multf(b,b),negf c)./1));
       if fixp denr c
         then c := multd(('!:rn!: . 1 . denr c),numr c)
        else rederr {"invalid laplace denominator",denr c};
             x1:=addf(b,c); x2:=addf(b,negf c); !*exp:=nil;
             if x1 = x2 then << rootl:=addrootl(x1,2*dg,rootl);
                                x1:=(('il!& to 1).*1) .+ negf x1;
                                denf:=multpf(x1 to (2*dg),denf) >>
                else << rootl:=addrootl(x2,dg,addrootl(x1,dg,rootl));
                        x1:=(('il!& to 1).*1) .+ negf x1;
                        x2:=(('il!& to 1).*1) .+ negf x2;
                        if not !:onep dg then
                     << x1:=!*p2f(x1 to dg); x2:=!*p2f(x2 to dg) >>;
                        denf:=multf(x2,multf(x1,denf)) >>  >>;
 ret: z:=ilform(v,numf,denf,rootl);
   return if (domainp a and !:onep a) then z
             else if null z then nil else multsq(a./1, z);
   end;

symbolic procedure  il3pol (u, v, numf, denf, rootl) ;
 % Find the roots of polynomial of third and higher degree.
 % U is standard power - the polynomial, v is the remaining st.f.
 % Numf, denf, rootl are the same. Returns standard quot or nil.
 (begin  scalar  a,d,p,y,z,w;
   if !*rounded then go to unk;
   d:=-cdr u; p:=car u;
   !*exp:=t; !*mcd:=t;
   % We must now convert rationals, if any, to standard quotients.
   % Since MCD was previously off, we must use limitedfactors here,
   % since the regular factorization turns EZGCD on.
   !*limitedfactors := t;
   y:=p; p:=nil./1;
   while y do if domainp y then << p:=addsq(p,!*d2q y); y:=nil >>
       else << a:=1; z:=list car y; % S.F. with 1 term only.
               while not domainp z do
                 << w:=lpow z;
                     % distinguish between mvar=kernel/form
                    w:=if kernlp car w then !*p2f w else
                          exptf(car w,cdr w);
                    a:=multf(a,w);
                    z:=lc z
                 >>;
               p:=addsq(p,multsq(a./1,!*d2q z)); y:=red y >>;
   if ((a:=cdr p) neq 1) and (d neq 1) then a:=exptf(a,d);
   z := fctrf car p;
   !*exp:=nil; !*mcd:=nil;
   % if length z = 2 then go to unk;   % No factors.
   % corrected (HM):
   if length z=2 and cdr cadr z=1 then go to unk;
   if car z neq 1 then errach list(car z,"found in IL3POL");
   z:=cdr z; y:=v;
   while z do << p:= caar z;
                 if cdar z neq 1 then p := exptf(p,cdar z);
                 if d neq 1 then p:=exptf(p,d);
                 y:=multf(y,recipf!* p); z:=cdr z >>;
   y:=ilform(y,numf,denf,rootl);
   if null y then go to unk
      else return if a = 1 then y else multsq(a./1, y);
 unk: return ilunknown(u.v,numf,denf);
 end) where !*limitedfactors := !*limitedfactors;

symbolic procedure  ilresid (numf, denf, rootl) ;
 % Apply the residue theorem at last.
 % Numf, denf, rootl are standard forms.  Returns standard quot or nil.
 begin scalar  n,d,ndeg,ddeg,m,x,y,z,w ;
   !*exp:=t; n:=numr subf(numf,nil); !*exp:=nil;
   z:=nil ./ 1; w:=nil ./ 1; x:=n; % Result accumulated in w.
   while x and not domainp x do
     << y:=car x; x:=cdr x;
        if depends(prepsq(cdr y./1),'il!&) or (caar y neq 'il!&
               and depends(caar y,'il!&) )
           then if (z:=ilterm(y,1,denf,rootl))
                   then << w:=addsq(w,z); n:=delete(y,n) >>
                   else x:=nil >> ;
   if null z then return ;
 % Now n is polynomial of il!& with constant coeff.
   ndeg:=if not domainp n and mvar n = 'il!& then ldeg n else 0;
   !*exp:=t; d:=numr subf(denf,nil); !*exp:=nil;
   ddeg:=if not domainp d and mvar d = 'il!& then ldeg d else 0;
   if ndeg < ddeg then go to resid;
   !*exp:=t; y:=qremf(n,d); !*exp:=nil;
   n:=cdr y; x:=car y; % N is remainder polynomial.
   while x do if domainp x
     then << w:=addsq(w, !*t2q(('(delta lp!&) to 1).* x)); x:=nil >>
     else if mvar x neq 'il!&
       then << w:=addsq(w, multsq(!*kk2q '(delta lp!&), !*t2q lt x) );
               x:=red x >>
       else << w:=addsq(w, multsq(!*kk2q list('df,list('delta,'lp!&),
                  'lp!&,ldeg x), lc x ./ 1) ); x:=red x >> ;
 resid: if null rootl then return w ;
   x:=caar rootl; m:=cdar rootl;
   if null x then y:=!*p2f('il!& to m)
      else << y:=(('il!& to 1) .* 1) .+ negf x;
              if m neq 1 then y:=!*p2f(y to m) >>;
   !*exp:=t; y:=numr subf(y,nil);
   y:=car qremf(d,y); !*exp:=nil; % D is quotient - remainder = 0.
   z:=multpf('(expt e (times il!& lp!&)) to 1, n); % Numerator.
   y:=recipf!* y;
   z:=multf(z,y) ./ 1;
   while (m:=m-1) > 0 do  z:=diffsq(z, 'il!&);
   x:= if null x then 0 else prepsq(x./1); % Root in prefix form.
   !*exp:=t; z:=subf(numr z, list('il!&.x)); % One residue as st.q.
   if not depends(prepsq z, 'lp!&)
      then z:=multsq(z, !*kk2q '(one lp!&));
   if (m:=cdar rootl) > 2 then while (m:=m-1) > 1 do
       z:=multf(car z,'!:rn!: . 1 . m)./1;
   w:=addsq(w,z); !*exp:=nil;
   rootl:=cdr rootl; go to resid;
 end;

endmodule;

end;
