% TeX-REDUCE-Interface 0.70
% set GREEK asserted
% set LOWERCASE asserted
% \tolerance 10
% \hsize=150mm
module tri;

% ======================================================================
%           T h e  T e X - R e d u c e - I n t e r f a c e
% ======================================================================
% (C) 1987/1988 by Rechenzentrum der Universitaet zu Koeln
%                  (University of Cologne Computer Center)
%                  Abt. Anwendungssoftware
%                  (Application Software Department)
%                  ATTN: Werner Antweiler
%                  Robert-Koch-Str. 10
%                  5000 Koeln 41
%                  Federal Republic of Germany
%                  E-Mail: reduce@rrz.Uni-Koeln.DE
% This  software  product  has  been  developed by
% WERNER ANTWEILER  at the  University of Cologne Computer Center,  West
% Germany. The TeX-Reduce-Interface  has been totally written in REDUCE-
% LISP.
% ======================================================================
% Authors: Werner Antweiler, Andreas Strotmann, Volker Winkelmann.
% Modifications: David Hartley.
%
% Last Update: 14-Jul-96                                    Version 0.70
% Permission to distribute under BSD License granted by authors January 2009
% ======================================================================


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
%
%
%
%                              Section Survey
% ----------------------------------------------------------------------
% Section Contents                                                  Page
% ----------------------------------------------------------------------
% 0       Main Procedure (and Interfacing)                             2
% 1       Creating a TeX item list                                     5
% 1.1     Operator Administration Routines                             5
% 1.2     Prefix to Infix Conversion                                   6
% 1.3     Making a TeX item                                            9
% 2       Inserting Glue Items                                        16
% 3       Line Breaking                                               18
% 3.1     Resolving Fraction Expressions                              20
% 3.2     Creating a Break List                                       21
% 3.3     Major Line Breaking Routine                                 23
% 4       TeX-Output Routines                                         28
% 5       User Interface                                              30
% ----------------------------------------------------------------------
% Note: page breaks (form feeds) are indicated by "%ff" lines
%ff
% ----------------------------------------------------------------------
% Section 0: Global Variables, Main Procedure and Interfacing
% ----------------------------------------------------------------------


% IMPORTANT NOTICE FOR REDUCE 3.2 USERS:
% This code was written to run under REDUCE 3.3.   Users  of  REDUCE 3.2
% therefore  have  to change two lines of this code before compiling it:
% 1) the line `switch ...;` must be deleted
% 2) the construct `FOR EACH ... IN ... JOIN ...` must be changed
%    to `FOR EACH ... IN ... CONC ...` because only the latter is
%    accepted by REDUCE 3.2.
% Furthermore, the TRI supports features of REDUCE that are new in ver-
% sion 3.3. You cannot take advantage of them under version 3.2. In
% particular, some of the examples in the accompanying test file may
% fail.

create!-package('(tri),'(contrib misc));

fluid '(
% -----------------+---------------------------------------------------+
% FLUID VARIABLES  | EXPLANATION                                       |
% -----------------+---------------------------------------------------+
  !*tex            % flag to be switched ON and OFF (TeX Output Mode)
  !*texbreak       % flag to be switched ON and OFF (Break Facility)
  !*texindent      % flag to be switched ON and OFF (Indentation Mode)
  texstack!*       % stack to save expressions for an unfilled line
  hsize!*          % total page width in scaled points (sp)
                   % note: 65536sp = 1pt = 1/72.27 inch
  hss!*            % total line stretchability/shrinkability (in sp)
  hww!*            % optimum page fit width (= 3/4 hsize)    (in sp)
  tolerance!*      % value within break points are considered to be
                   % feasible  (range: 0..10000)
  !*lower    % used in REDUCE 3.5 to make everything lower case
% -----------------+---------------------------------------------------+
);

global '(
% ------------------+---------------------------------------------------+
% GLOBAL VARIABLES  | EXPLANATION                                       |
% ------------------+---------------------------------------------------+
  metricu!*         % EXCALC
  indxl!*           % EXCALC
% ------------------+---------------------------------------------------+
);

% declare switches:
switch tex,texbreak,texindent;

% declare switch dependencies:
put('texindent,'simpfg,'((t (progn (setq !*tex t)
                                   (setq !*texbreak t)))  ));
put('texbreak,'simpfg,'((t (setq !*tex t)) ));
put('tex,'simpfg,'((nil (progn (setq !*texbreak nil)
                               (setq !*texindent nil))) ));

symbolic procedure tri!-error(strlst,errclass);
<< for each x in strlst do prin2 x; terpri();
   if errclass='fatal then
      rederr "Aborting."
>>;

% Code called by ASSGNPRI.

symbolic procedure texpri(u,v,w);
   (if x and get(x,'texprifn) then apply3(get(x,'texprifn),u,v,w)
     else texvarpri(u,v,w)) where x = getrtype u;

symbolic procedure texvarpri(u,v,w); % same parameters as above
begin scalar !*lower;
   if w memq '(first only) then texstack!*:=nil;
   if v then for each x in reverse v do u:=list('setq,x,u);
   texstack!* := nconc(texstack!*,mktag(u,0,nil));
   if (w=t) or (w='only) or (w='last) then
   << if !*texbreak then
      << texstack!* := insertglue(texstack!*);
         texstack!* := trybreak(texstack!*,breaklist(texstack!*))
      >>;
      texout(texstack!*,!*texbreak); texstack!*:=nil
   >>;
   %if (null w) or (w eq 'first) then
   %  texstack!* := nconc(texstack!*,list '!\!q!u!a!d! );
   nil
end;
%ff
% The following procedure interfaces to E. Schruefer's EXCALC package.
% Courtesy: E. Schruefer.

put('form!-with!-free!-indices,'texprifn,'texindxpri);

symbolic procedure texindxpri(u,v,w);
   begin scalar metricu,il,dnlist,uplist,r,x,y,z;
     if v then go to a;
     metricu := metricu!*;
     metricu!* := nil;
     il := allind !*t2f lt numr simp0 u;
     for each j in il do
          if atom revalind j
             then uplist := j . uplist
           else dnlist := cadr j . dnlist;
         for each j in intersection(uplist,dnlist) do
             il := delete(j,delete(revalind
                                  lowerind j,il));
         metricu!* := metricu;
     y := flatindxl il;
     r := simp!* u;
     for each j in mkaindxc(y,nil) do
       <<x := pair(y,j);
     z := exc!-mk!*sq2 multsq(subfindices(numr r,x),1 ./ denr r);
        if null(!*nero and (z = 0))
          then texvarpri(z,list subla(x,'ns . il),'only)>>;
     return u;
  a: v := car v;
     y := flatindxl allindk v;
     for each j in if flagp(car v,'antisymmetric) and
                      coposp cdr v then comb(indxl!*,length y)
                    else mkaindxc(y,nil) do
       <<x := pair(y,j);
         z := aeval subla(x,v);
         if null(!*nero and (z = 0))
            then texvarpri(z,list subla(x,v),'only)>>;
      return u
    end;
%ff
% ----------------------------------------------------------------------
% Section 1: Creating a TeX item list
% ----------------------------------------------------------------------
%
% Linearization is performed by expanding REDUCE prefix expressions into
% a so called "TeX item list".  Any TeX item is a readable TeX primitive
% or macro (i.e. a LISP atom), with properties 'CLASS, 'TEXTAG, 'TEXNAME
% and eventually 'TEXPREC, 'TEXPATT and 'TEXUBY bound to them, depending
% on what kind of TeX item it actually is.  (See Section 1.3 for further
% information.)
%    A REDUCE expression is expanded using the two  functions  "mktag"
% and "makefunc". Function "mktag" identifies the operator and is able
% to put some brackets around the expression if necessary. "makefunc" is
% a pattern oriented 'unification' function, which matches the arguments
% of a REDUCE expression in order of appearance with so called 'unifica-
% tion tags',  as explained below.   "mktag" and "makefunc" are highly
% recursive functions.
%    The patterns mentioned above are lists (consisting of 'tags') asso-
% ciated with each REDUCE operator.   A tag is defined as either an atom
% declared  as  a  TeX item  or one of the following 'unification tags':
%    (F)  ............ insert operator
%    (X)  ............ insert non-associative argument
%    (Y)  ............ insert (left/right-) associative argument
%    (Z)  ............ insert superscript/subscript argument
%    (R)  ............ use tail recursion to unify remaining arguments
%                      (associativity depends on previous (X) or (Y) )
%    (L)  ............ insert a list of arguments (eat up all arguments)
%    (M)  ............ insert a matrix (and eat up all arguments)
%    (APPLY <fun>) ... apply function <fun> to remaining argument list
% ----------------------------------------------------------------------

% ----------------------------------------------------------------------
% Section 1.1: Operator Administration Routines
% ----------------------------------------------------------------------

symbolic procedure makeop(op,prec,patt,uby);
<< put(op,'texprec,prec); put(op,'texpatt,patt);
   put(op,'texuby,if uby then (car uby).(cadr uby) else nil.nil)
>>;

symbolic procedure makeops(l);
for each w in l do makeop(car w,cadr w,caddr w,cadddr w);
%ff
makeops('(
%-----------+----------+---------------------+-------------------------+
% Name      |Precedence|Expansion List       |Unary/Binary Interchange |
%-----------+----------+---------------------+-------------------------+
(setq             1     ((x) (f) !\![ (x) !\!]) nil)
(or              30     ((x) (f) (r))           nil)
(and             40     ((x) (f) (r))           nil)
(equal           50     ((x) (f) !\![ (x) !\!]) nil)
(replaceby       50     ((x) (f) !\![ (x) !\!]) nil)
(greaterp        50     ((x) (f) !\![ (x) !\!]) nil)
(lessp           50     ((x) (f) !\![ (x) !\!]) nil)
(geq             50     ((x) (f) !\![ (x) !\!]) nil)
(leq             50     ((x) (f) !\![ (x) !\!]) nil)
(neq             50     ((x) (f) !\![ (x) !\!]) nil)
(member          50     ((x) (f) (x))           nil)
(when            50     ((x) (f) (x))           nil)
(plus           100     ((x) (f) (r))           (minus difference))
(minus          100     ((f) (y))               nil)
(difference     100     ((x) (f) (y))           nil)
(union          100     ((x) (f) (r))           nil)
(setdiff        100     ((x) (f) (y))           nil)
(taylor!*       100     ((apply maketaylor))    nil) % precedence like plus
(times          200     ((x) (f) (r))           (recip quotient))
(wedge          200     ((x) (f) (r))           nil) % EXCALC
(quotient       200     ((f) (z) !}!{ (z) !})   nil)
(intersection   200     ((x) (f) (r))           nil)
(!*sq           200     ((apply make!*sq))      nil) % precedence like quotient
(recip          700     ((f) !1 !}!{ (z) !})    nil)
(expt           850     ((x) !^!{ (z) !})       nil)
(sqrt           800     ((f) !  !  !  (z) !})   nil)
(!:rd!:         999     ((apply make!:rd!:))    nil)
(!:cr!:         999     ((apply makedomain))    nil)
(!:gi!:         999     ((apply makedomain))    nil)
(!:rn!:         999     ((apply makedomain))    nil)
(!:crn!:        999     ((apply makedomain))    nil)
(!:mod!:        999     ((apply makedomain))    nil)
(!:dn!:         999     ((apply makedomain))    nil)
(!:int!:        999     ((apply makedomain))    nil)
(not            999     ((f) (y))               nil)
(mat            999     ((f) (m !\!c!r!  !&) !}) nil)
(list           999     (!\!{ (l !\co! ) !\!})  nil)
(df             999     ((apply makedf))        nil)
(int            999     ((apply makeint))       nil)
(limit          999     ((apply makelimit))     nil)
(limit!+        999     ((apply makelimit))     nil)
(limit!-        999     ((apply makelimit))     nil)
(sum            999     ((apply makelimit))     nil)
(prod           999     ((apply makelimit))     nil)
(!~             999     ((f) (y))               nil)
(!*interval!*   999     ((x) !. !. (x))         nil)
(innerprod      999     (!{ !\!r!m!  !i !}
                            !_!{ (z) !} (x))    nil) % EXCALC
(liedf          999     (!\!h!b!o!x!  !{ !\!i!t!  !\!$ !}
                            !_!{ (z) !} (x))    nil) % EXCALC
(hodge          999     ((f) (y))               nil) % EXCALC
(partdf         999     ((f) (apply makepartdf)) nil)          % EXCALC
(d              999     (!\!d!  (x))            nil)           % EXCALC
(!:ps!:         999     ((apply make!:ps!:))    nil)           % TPS
(rest!_order    999     (!{ !\!r!m!  !O !} (x)) nil)           % TPS
%-----------+----------+----------------------+------------------------+
));

% ----------------------------------------------------------------------
% Section 1.2 : Prefix to Infix Conversion
% ----------------------------------------------------------------------

symbolic procedure mktag(tag,prec,assf);
% analyze an operator and decide what to do
% parameters: tag ....... the term itself
%             prec ...... outer precedence
%             assf ...... outer associativity flag
if null tag then nil else
if atom tag then texexplode(tag) else
begin
  scalar tagprec,term;
  tagprec:=get(car tag,'texprec) or 999; % get the operator's precedence
  term:=makefunc(car tag,cdr tag,tagprec); % expand expression and if it
  % is necessary, put a left and a right bracket around the expression.
  if (assf and (prec = tagprec)) or (tagprec < prec)
  then term:=nconc('!\!( . term , '!\!) . nil);
  return(term)
end;

symbolic procedure makearg(l,s);
% collect arguments from a list <l> and put seperators <s> between them
if null l then nil
else if null cdr l then mktag(car l,0,nil)
     else nconc(mktag(car l,0,nil), s . makearg(cdr l,s));

symbolic procedure makemat(m,v,h);
% make a matrix <m> and use  <h> as a horizontal seperator and <v> as
% a vertical terminator.
if null m then nil else nconc(makearg(car m,h), v . makemat(cdr m,v,h));
%ff
smacro procedure istag(v,w); car v=w;
smacro procedure unary(uby);  car uby;
smacro procedure binary(uby); cdr uby;
smacro procedure lcopy(a); for each x in a collect x;

symbolic procedure makefunc(op,arg,prec);
begin
  scalar term,tag,a,pattern,uby;
  term:=nil;
  pattern:=get(op,'texpatt)
           or ( if flagp(op,'indexvar) then '((apply makeexcinx))
                                       else '( (f) !\!( (l !,) !\!) ));
  uby:=get(op,'texuby);
  while pattern do
  << tag:=car pattern; pattern:=cdr pattern;
     if (atom tag) then a:=tag.nil
     else if (not atom car tag) then a:=nil
     else if istag(tag,'f) then
       % test for unary to binary operator interchange
       if arg and (not atom car arg) and uby and (caar arg=unary(uby))
       then << a:=texexplode(binary(uby)); arg:=cadar arg.cdr arg >>
       else a:=texexplode(op)
     else if istag(tag,'apply)
          then << a:=apply3(cadr tag,op,arg,prec); arg:=nil >>
     else if null arg then a:=nil
     else if istag(tag,'x)
          then << a:=mktag(car arg,prec,nil); arg:=cdr arg >>
     else if istag(tag,'y)
          then << a:=mktag(car arg,prec,t); arg:=cdr arg >>
     else if istag(tag,'z)
          then << a:=mktag(car arg,0,nil); arg:=cdr arg >>
     else if istag(tag,'r) then
       if cdr arg % more than one argument ?
       then << pattern:=get(op,'texpatt); a:=nil >>
       else << a:=mktag(car arg,prec,nil); arg:=cdr arg >>
     else if istag(tag,'l)
          then << a:=makearg(arg,cadr tag); arg:=nil >>
     else if istag(tag,'m)
          then << a:=makemat(arg,cadr tag,caddr tag); arg:=nil >>
     else a:=nil;
     if a then term:=nconc(term,a)
  >>;
  return(term)
end;
%ff
symbolic procedure make!*sq(op,arg,prec);
   % Convert !*sq's to true prefix form
   mktag(prepreform prepsq!* sqhorner!* car arg,0,nil);

symbolic procedure makedf(op,arg,prec); % DF operators are tricky
begin
  scalar dfx,f,vvv; integer degree;
  dfx:=lcopy(f:=texexplode op); degree:=0;
  nconc(dfx,mktag(car arg,prec,nil)); dfx:=nconc(dfx,list '!}!{);
  for each item in cdr arg do
    if numberp(item) then
    << dfx:= nconc(dfx,'!^!{ .texexplode(item));
       dfx:= nconc(dfx,list '!});
       degree:=degree+item-1;
    >>
    else
    << dfx:= nconc(dfx,append(f,mktag(item,prec,nil))); degree:=degree+1
    >>;
  if degree>1 then
  << vvv:=nconc(texexplode(degree), '!} . cdr dfx);
     rplacd(dfx,'!^!{ . vvv)
  >>;
  return ('!\!f!r!a!c!{ . nconc(dfx, list '!}))
end;

symbolic procedure makepartdf(op,arg,prec); % EXCALC extension
if cdr arg then
  ('!_!{ . nconc(makearg(cdr arg,'!,), '!} . mktag(car arg,prec,nil)))
else ('!_!{ . nconc(mktag(car arg,prec,nil), list '!}));

smacro procedure inxextend(item,ld,rd);
  nconc(result,ld.nconc(texexplode(item),list rd));

symbolic procedure makeexcinx(op,arg,prec); % EXCALC extension
begin scalar result;
  result:=nconc('!{.nil,texexplode(op));
  for each item in arg do
    if numberp item then
       if minusp item then  inxextend(-item,'!{!}!_!{,'!})
       else                 inxextend(item ,'!{!}!^!{,'!}) else
    if atom item then       inxextend(item ,'!{!}!^!{,'!}) else
    if car item='minus then inxextend(cadr item ,'!{!}!_!{,'!})
    else                    inxextend('! ,'!{!}!_!{,'!});
  return nconc(result,'!}.nil)
end;

symbolic procedure make!:rd!:(op,arg,prec);
   begin scalar digits,str; integer dotpos,xp;
   op := rd!:explode(op . arg);
   digits := car op; xp := cadr op; dotpos := caddr op;
   for i:=1:dotpos do
   << str := car digits . str;
      digits := cdr digits; if null digits then digits := '(!0) >>;
   str := '!. . str;
   for each c in digits do str := c . str;
   if not(xp=0) then
   << for each c in '(!\!, !1 !0 !^!{) do str := c . str;
      for each c in explode2 xp do str := c . str;
      str := '!} . str >>;
   return reverse str;
   end;

symbolic procedure makedomain(op,arg,prec);
   if get(op,'prepfn) then
      mktag(apply1(get(op,'prepfn),op . arg),prec,nil)
   else if get(op,'prepfn2) then
      mktag(apply1(get(op,'prepfn2),op . arg),prec,nil)
   else if get(op,'simpfn) then
      mktag(apply1(get(op,'simpfn),op . arg),prec,nil)
   else rerror(tri,000,
      {"Don't know how to print domain",get(op,'dname) or op});

symbolic procedure makelimit(op,arg,prec);
   % for operators like limit, sum and prod which may have limit scripts
   begin scalar a,term,limits;
   if arg then limits := cdr arg;
   term := texexplode(op);
   if limits then
   << a := '!_!{ . mktag(car limits,0,nil);
      limits := cdr limits;
      term := nconc(term,a) >>;
   if limits then
   << a :=
               if op = 'limit then '!\!t!o!                      % spaces critical
            else if op = 'limit!+ then '!\!u!p!a!r!r!o!w!     %
            else if op = 'limit!- then '!\!d!o!w!n!a!r!r!o!w! %
            else '!=;
      a := a . mktag(car limits,0,nil);
      limits := cdr limits;
      term := nconc(term,a) >>;
   if limits then
   << a := '!} . '!^!{ . mktag(car limits,0,nil);
      term := nconc(term,a) >>;
   a := '!{ . if arg then mktag(car arg,prec,nil) else nil;
   if arg and cdr arg then a := '!} . a;
   term := nconc(term,a);
   term := nconc(term,'!} . nil);
   return term;
   end;

symbolic procedure texgroup u;
   % surround u by TeX {}
   % NB Destructive!!
   nconc('!{ . if null u or listp u then u else {u},'!} . nil);

symbolic procedure makeint(op,arg,prec);
   % for operators like int which may have limit scripts
   begin scalar a,term,limits;
   if arg and cdr arg then limits := cddr arg;
   term := texexplode(op);
   if limits then
   << a := '!_!{ . cdr texgroup mktag(car limits,0,nil);
      limits := cdr limits;
      term := nconc(term,a) >>;
   if limits then
   << a := '!^!{ . cdr texgroup mktag(car limits,0,nil);
      limits := cdr limits;
      term := nconc(term,a) >>;
   a := if arg then mktag(car arg,0,nil);
   a := nconc(a,if arg and cdr arg then '!\!, . '!d . mktag(cadr arg,0,nil));
   term := nconc(term,texgroup a);
   return term;
   end;

symbolic procedure maketaylor(op,arg,prec);
   mktag(apply1(get(op,'fancy!-reform),op . arg),prec,nil);

% The following is part of the interface to TPS.
% Andreas Strotmann, 19 Mar 93

% ps:numberp smacro required for compilation; copied over from tps.red

symbolic smacro procedure ps!:numberp u;
  numberp u or (car u neq '!:ps!: and get(car u,'dname));

% fluid declaration to avoid compiler warnings
fluid '(ps!:exp!-lim);

% symbolic procedure ps!:prin!: p;
symbolic procedure make!:ps!:(op, arg, prec);  % TPS interface,
%  (lambda (first,u,delta,symbolic!-exp!-pt,about,atinf);
 (lambda (first,u,delta,symbolic!-exp!-pt,about,atinf,texps,p);
  <<  % if !*nat and posn!*<20 then orig!*:=posn!*;
      atinf:=(about='ps!:inf);
      ps!:find!-order p;
      delta:=prepf((ps!:depvar p) .** 1 .*1 .+
              (negf  if atinf then nil
                      % expansion about infinity
                      else if idp about then !*k2f about
                      else if ps!:numberp about then !*n2f about
                      else if (u:=!*pre2dp about) then !*n2f u
                      else !*k2f(symbolic!-exp!-pt:= compress
                         append(explode ps!:depvar p, explode '0))));
%      if symbolic!-exp!-pt then prin2!* "[";
%      prin2!* "{";
      texps := nconc(texps, list '!\!{ );
%
      for i:=(ps!:order p): ps!:exp!-lim do
        << u:=ps!:term(p,i);
           if not null numr u then
              <<if minusf numr u then <<u:=negsq u; % prin2!* " - ">>
                                        texps := nconc(texps, list '!-)
                                        >>
                  else if not first then % prin2!* " + ";
                                        texps := nconc(texps, list '!+);
                first := nil;
%                if posn!*>55 then <<terpri!* nil;prin2!* "  ">>;
                if denr u neq 1 then % prin2!* "(";
                                     texps := nconc(texps, list '!\!( );
                if u neq '(1 . 1) then
                        % maprint(prepsq u,get('times,'infix))
                        texps := nconc(texps,
                                       mktag(prepsq u,
                                             get('times, 'texprec),
                                             nil))
                  else if i=0 then % prin2!* 1;
                                   texps := nconc(texps, list '!1);
                if denr u neq 1 then % prin2!* ")";
                                     texps := nconc(texps, list '!\!) );
                if i neq 0 and u neq '(1 . 1) then % prin2!* "*";
                        texps := nconc(texps,list get('times,'texname));
                if i neq 0 then
                % xprinf(!*p2f mksp(delta,
                %        if atinf then -i else i),nil,nil)
                  texps := (lambda i;
                             nconc(texps,
                                   mktag (if (i = 1) then delta
                                            else list('expt, delta, i),
                                          get('times, 'texprec),
                                          nil)))
                           (if atinf then -i else i);
              >>
       >>;
      if first then % prin2!* "0";
                    texps := nconc(texps, list '!0 );
      % if posn!*>55 then terpri!* nil;
      u:=ps!:exp!-lim +1;
      texps := (lambda u;
                nconc(texps,
                      '!+ . mktag(list('rest!_order,
                                        if (u = 1) then delta
                                            else list('expt, delta, u)),
                                  get('plus, 'texprec),
                                  nil)))
                (if atinf then -u else u);
      %if (u=1) and not atinf and (about neq 0) then
      %      prin2!* " + O"
      %else prin2!* " + O(";
      %xprinf(!*p2f mksp(delta,if atinf then -u else u),nil,nil);
      %if (u=1) and not atinf and (about neq 0) then
      %        prin2!* "}"
      %   else prin2!* ")}";
      texps := nconc(texps, list '!\!} );
      if symbolic!-exp!-pt then
        << %if posn!*>45 then terpri!* nil;
           %prin2!* "  where ";
           texps := nconc(texps, list '!_!{ );
           %prin2!* symbolic!-exp!-pt;
           texps := nconc(texps, texexplode symbolic!-exp!-pt);
           %prin2!* " = ";
           texps := nconc(texps, list '!= );
           %maprin about;
           texps := nconc(texps, mktag(makeprefix about,
                                         get('equal, 'texprec),  nil));
           texps := nconc(texps, list '!} );
           %prin2!* "]"
        >>;
  texps
  >>)
%  (t,nil,nil,nil,ps!:expansion!-point p,nil);
 (t,nil,nil,nil,ps!:expansion!-point(op . arg),nil,nil,op . arg);

%ff
% ----------------------------------------------------------------------
% Section 1.3 : Making a TeX Item
% ----------------------------------------------------------------------
% Properties of TeX items:
%   'CLASS ..... one of the following class specifiers
%      'ORD .... ordinary symbols
%      'LOP .... large operators
%      'BIN .... binary operators
%      'REL .... relational operators
%      'OPN .... opening symbols (left parenthesis)
%      'CLO .... closing symbols (right parenthesis)
%      'PCT .... punctuation symbols
%      'INN .... inner TeX group delimiters
%  'TEXTAG ..... one of the following lists or atoms
%      <kind> .. an atom describing an 'INN class group delimiter
%      (<w1> <w2> <w3>) ... where is
%                <w1> ..... width for text style        (cmmi10)
%                <w2> ..... width for scriptstyle       (cmmi8)
%                <w3> ..... width for scriptscriptstyle (cmmi5)
% The parital lists of the list which is passed to makeitems have the
% following general structure:
%  (<TeX-item> <class> <TeX-tag> <v1> <v2> ... )
%  where is
%      <TeX-item> .... the atom which actually is the TeX code
%      <class> ....... the 'CLASS property as explained above
%      <TeX-tag> ..... the 'TEXTAG property as explained above
%      <v1> etc. ..... atoms which will be bound to specific TeX items
%                      by its property 'TEXNAME
% ----------------------------------------------------------------------

smacro procedure triassert(name,item); put(name,'texname,item);
smacro procedure assertl(l); for each v in l do triassert(car v,cadr v);
smacro procedure retract(name); put(name,'texname,nil);
smacro procedure retractl(l); for each v in l do retract(car v);
smacro procedure gettexitem(a); get(a,'texname) or (get(a,'class)and a);

put ('texitem,'stat,'rlis); % handle argument passing for func. TeXitem

symbolic procedure texitem(arglist);
begin scalar x,ok,item,class,tag;
  if length arglist neq 3
  then rederr "Usage: TeXitem(item,class,width-list);";
  item:=car arglist; class:= cadr arglist; tag:= caddr arglist;
  ok:=memq(class,'(ord bin rel pct opn clo lop));
  if not ok then << prin2 "% illegal item class "; print class >>;
  if atom tag then ok:=nil else
  << if car(tag)='list then tag:=cdr tag; % accept algebraic lists
     for each x in tag do if not numberp x then ok:=nil
  >>;
  if not ok then << prin2 "% illegal width tag "; print tag >>;
  if ok then
  << item:=intern(item); put(item,'class,class); put(item,'textag,tag)
  >>;
  prin2 "% Item "; prin2 item;
  if not ok then prin2 "not "; prin2 " added"; terpri();
  return nil
end;
%ff
symbolic procedure makeitems(l);
for each w in l do
  begin scalar iw;
   iw:=intern(car w);
   put(iw,'class,cadr w); put(iw,'textag,caddr w);
   for each v in cdddr w do triassert(v,iw);
  end;

fluid '(texunknowncounter!*);
texunknowncounter!*:= 0;

symbolic procedure unknownitem(a);
<< texunknowncounter!* := texunknowncounter!* +1;
   prin2 "% non-fatal error: unknown atom "; prin2 a;
   prin2 " replaced by ?_{"; prin2 texunknowncounter!*;
   prin2 "}"; terpri();
   '!? . '!_!{ . nconc(explode texunknowncounter!*, list '!})
>>;

symbolic procedure texexplode(a);
begin scalar b;
  b:=if a and (atom a) then
     (gettexitem(a)
      or if numberp(a) then texcollect(explode(a))
         else if stringp(a) then strcollect(explode2(a))
         else texexplist(texcollect(explode2(a))));
   b:=if null b then list '!  else if not atom b then b else list b;
   return b
end;

symbolic procedure texcollect(l);
  for each el in l join
    if null gettexitem(el) then unknownitem(el)
    else gettexitem(el).nil;

smacro procedure strtexitem(e);
  if e='!  then list '!\!        % space after ! is necessary
  else if e='!	 then list '!\!   % there is a tab before the "then"
  else if liter(e) then {e}
  else if gettexitem(e) then {gettexitem(e)}
  else unknownitem(e); % or '! ;

symbolic procedure strcollect(l);
  for each el in l join strtexitem el;

symbolic procedure texexplist(r);
begin scalar r,v;
  v:=nil;
  for each rl on r do
    if digit(car rl) and not v then v:=rl
    else if v and not digit(car rl) then v:=nil;
  if v then
  << rplacd(v,car v.cdr v); rplaca(v,'!_!{); nconc(r,'!}.nil) >>;
  return r
end;
%ff
makeitems('(
  (!                    inn     dmy)   % no nonsense dummy item
  (!{                   inn     beg)   % begin of a TeX inner group
  (!^!{                 inn     sup)   % superscript
  (!_!{                 inn     sub)   % subscript nolimits
  (!{!}!^!{             inn     sup)   % spread superscript
  (!{!}!_!{             inn     sub)   % spread subscript
  (!}!{                 inn     sep)   % general group seperator
  (!}!^!{               inn     esp)   % end of group and superscript
  (!}!_!{               inn     esb)   % end of group and subscript
  (!}                   inn     end)   % end of TeX inner group
  (!\!f!r!a!c!{         inn     frc    recip quotient)  % fraction group
  (!\!s!q!r!t!{         inn     frc    sqrt)            % square root
  (!\!p!m!a!t!r!i!x!{   inn     mat    mat)             % matrix group
  (!&                   inn     tab)   % horizontal tabulation
  (!\!c!r!              inn     cr )   % vertical tabulation
  (!\!n!l!              inn     cr )   % vertical tabulation (special)
  (!\!(                 opn     (327680 276707 241208)) % test value
  (!\!)                 clo     (327680 276707 241208)) % ...
  (!\!{                 opn     (327680 276707 241208)) % ...
  (!\!}                 clo     (327680 276707 241208)) % ...
  (!\![                 opn     (0))
  (!\!]                 clo     (0))
  (!\!<                 opn     (254863 212082 195700))
  (!\!>                 clo     (254863 212082 195700))
  (!\!,                 ord     (80960))
  (!\!q!u!a!d!          rel     (655360))
  (!                    ord     (0)) % dummy item
  (!\!r!m!              ord     (0)) % dummy def of font change
  (!\!i!t!              ord     (0)) % dummy def of font change
  (!\!b!f!              ord     (0)) % dummy def of font change
  (!\!h!b!o!x!          ord     (0)) % dummy def of box opening
  (!!                   ord     (182045 148367 131984))
  (!?                   ord     (309476 247127 211630))
  (!\!l!b!r!a!c!e!      ord     (327681 268516 241211) !{)
  (!\!r!b!r!a!c!e!      ord     (327681 268516 241211) !})
  (!\!l!b!r!a!c!k!      ord     (182045 148367 131984) ![)
  (!\!r!b!r!a!c!k!      ord     (182045 148367 131984) !])
  (!\!b!a!c!k!s!l!a!s!h!   ord  (327681 268516 241211) !\)
  (!\!%                 ord     (546135 430537 359544) !%)
  (!\!#                 ord     (546135 430537 359544) !#)
  (!\!&                 ord     (509726 402320 336788) !&)
  (!@                   ord     (509726 402320 336788))
  (!\!_                 ord     (235930) !_)
  (!\!$                 ord     (327681 261235 223008) !$)
  (!;                   ord     (182045 148367 131984))
  (!:                   ord     (182045 148367 131984))
  (!.                   ord     (182045 148367 131984))
  (!,                   ord     (182045 148367 131984))
  (!|                   ord     (182045 148367 131984))
  (!'                   ord     (183865 177267))
  (!`                   ord     (182045 148367 131984))
  (!\!                  ord     (218453))
%ff
% Fonts  ammi10, ammi7, ammi5; ordered by index number
  (!\!G!a!m!m!a!        ord     (394126 317121 266467))
  (!\!D!e!l!t!a!        ord     (546133 451470 377742))
  (!\!T!h!e!t!a!        ord     (481689 395400 331866))
  (!\!L!a!m!b!d!a!      ord     (418702 346612 293546))
  (!\!X!i!              ord     (447374 366819 309020))
  (!\!P!i!              ord     (553870 446190 368185))
  (!\!S!i!g!m!a!        ord     (511090 417791 348842))
  (!\!U!p!s!i!l!o!n!    ord     (382293 320398 275342))
  (!\!P!h!i!            ord     (436906 364088 309475))
  (!\!P!s!i!            ord     (419430 354622 304150))
  (!\!O!m!e!g!a         ord     (461596 382217 322806))
  (!\!a!l!p!h!a!        ord     (419233 350253 299280))
  (!\!b!e!t!a!          ord     (370688 303376 259231))
  (!\!g!a!m!m!a!        ord     (353318 296277 256227))
  (!\!d!e!l!t!a!        ord     (273066 229467 203070))
  (!\!e!p!s!i!l!o!n!    ord     (266012 222822 197791))
  (!\!z!e!t!a!          ord     (223686 195060 178221))
  (!\!e!t!a!            ord     (352407 300373 261688))
  (!\!t!h!e!t!a!        ord     (298553 247580 216177))
  (!\!i!o!t!a!          ord     (231955 198883 180224))
  (!\!k!a!p!p!a!        ord     (377590 315392 271246))
  (!\!l!a!m!b!d!a!      ord     (382293 320398 275342))
  (!\!m!u!              ord     (394885 326314 278528))
  (!\!n!u!              ord     (341940 283534 244849))
  (!\!x!i!              ord     (327680 276707 241208))
  (!\!p!i!              ord     (370293 312456 270222))
  (!\!r!h!o!            ord     (329728 269699 232379))
  (!\!s!i!g!m!a!        ord     (361737 300646 258776))
  (!\!t!a!u!            ord     (250083 220910 200430))
  (!\!u!p!s!i!l!o!n!    ord     (354076 299008 259413))
  (!\!p!h!i!            ord     (390485 322764 275888))
  (!\!c!h!i!            ord     (410055 334506 283534))
  (!\!p!s!i!            ord     (426894 357262 304924))
  (!\!o!m!e!g!a!        ord     (407931 339968 290360))
  (!\!v!a!r!e!p!s!i!l!o!n!  ord (312433 358776 225097))
  (!\!v!a!r!t!h!e!t!a!  ord     (388513 326997 281713))
  (!\!v!a!r!p!i!        ord     (504945 424800 359719))
  (!\!v!a!r!r!h!o!      ord     (329728 369699 232379))
  (!\!v!a!r!s!i!g!m!a!  ord     (312433 258776 225097))
  (!\!v!a!r!p!h!i!      ord     (465123 383749 323675))
  % omitted: codes 40-47
  (!0   ord     (327680 276707 241208))
  (!1   ord     (327680 276707 241208))
  (!2   ord     (327680 276707 241208))
  (!3   ord     (327680 276707 241208))
  (!4   ord     (327680 276707 241208))
  (!5   ord     (327680 276707 241208))
  (!6   ord     (327680 276707 241208))
  (!7   ord     (327680 276707 241208))
  (!8   ord     (327680 276707 241208))
  (!9   ord     (327680 276707 241208))
  (!.   pct     (182044 160198 150186) cons)
  (!,   rel     (182044 160198 150186))
  (!\co!  rel   (182044 160198 150186))
%ff
  % omitted: code 60
  (!/   bin     (327680 262143 204800))
  % omitted : codes 62,63
  (!\!p!a!r!t!i!a!l!    ord     (384341 314982 268105)  partdf df)
  (!A   ord     (491520 404866 339057))
  (!B   ord     (497095 406550 339569))
  (!C   ord     (542583 439273 363451))
  (!D   ord     (542583 439273 363451))
  (!E   ord     (468400 387026 326360))
  (!F   ord     (412330 331684 277845))
  (!G   ord     (515276 418884 348660))
  (!H   ord     (544768 439409 363520))
  (!I   ord     (288085 236475 204913))
  (!J   ord     (371825 302512 257706))
  (!K   ord     (556373 450104 371598))
  (!L   ord     (446008 369914 312888))
  (!M   ord     (635790 512227 420408))
  (!N   ord     (526563 424846 352142))
  (!O   ord     (499893 409964 343244))
  (!P   ord     (420750 341242 286606))
  (!Q   ord     (518098 424527 354622))
  (!R   ord     (482417 399041 335644))
  (!S   ord     (392760 323128 274887))
  (!T   ord     (382976 318122 272270))
  (!U   ord     (447465 366409 309179))
  (!V   ord     (375011 304014 260266))
  (!W   ord     (577991 469310 389973))
  (!X   ord     (533845 433811 359651))
  (!Y   ord     (388210 317485 270506))
  (!Z   ord     (429170 352256 397642))
  % omitted: codes 91-96
  (!a   ord     (346415 291999 253770))
  (!b   ord     (281258 235383 207621))
  (!c   ord     (283610 240571 212810))
  (!d   ord     (341105 277890 242392))
  (!e   ord     (283610 240571 212810))
  (!f   ord     (320853 260778 224369))
  (!g   ord     (300980 247580 215995))
  (!h   ord     (377590 315392 271246))
  (!i   ord     (231500 191601 174762))
  (!j   ord     (238933 198883 177493))
  (!k   ord     (341181 296265 248490))
  (!l   ord     (195546 169756 157468))
  (!m   ord     (575411 479687 402318))
  (!n   ord     (393367 334051 288540))
  (!o   ord     (317667 264510 230377))
  (!p   ord     (329728 277435 242392))
  (!q   ord     (292560 245577 215995))
  (!r   ord     (277466 235292 208668))
  (!s   ord     (307200 253041 219818))
  (!t   ord     (234837 204799 186595))
  (!u   ord     (375163 319487 277162))
  (!v   ord     (317667 269881 236657))
  (!w   ord     (463303 386389 327680))
  (!x   ord     (361813 296732 253951))
  (!y   ord     (321308 273066 239388))
  (!z   ord     (304772 257137 225735))
  % omitted: codes 123-127
%ff
  % Fonts amsy10, amsy7, amsy5; not ordered.
  (!+           bin     (509724 422343 354986)  plus)
  (!-           bin     (509724 422343 354986)  difference minus)
  (!*           ord     (509724 422343 354986) hodge)
  (!"           ord     (509724 422343 354986))
  (!\!c!d!o!t!  bin     (182044 160198 150186)  times)
  (!=           rel     (509724 422343 354986)  eq equal)
  (!:!=         rel     (691771 550687 468772)  setq)
  (!\!s!u!m!    lop     (1000000 700000 500000)  sum)
  (!\!p!r!o!d!  lop     (1000000 700000 500000)  prod)
  (!\!i!n!t!    lop     (1000000 700000 500000)  int)
  (!\!l!i!m!    ord     (910221 771866 678114)  limit limit!+ limit!-)
  (!\!s!i!n!    ord     (804635 687398 612123)  sin)
  (!\!c!o!s!    ord     (877454 745653 657634)  cos)
  (!\!t!a!n!    ord     (946630 800994 700869)  tan)
  (!\!l!n!      ord     (700000 600000 500000)  log)
  (!\!e!x!p!    ord     (1001243 844685 735003)  exp)
  (!\!a!r!c!t!a!n!  ord (1824539 1543734 1356227)  atan)
  (!\!w!e!d!g!e!    ord (436908 353167 309480) wedge !^)
  (!\!b!a!c!k!s!l!a!s!h!  ord (327681 268516 241211) !\ setdiff)
  (!\!d!        ord     (364090))
  (!\!l!a!n!d!  bin     (436908 353167 309480) and)
  (!\!l!o!r!    bin     (436908 353167 309480) or)
  (!\!l!n!o!t!  ord     (436908 353167 309480) not)
  (!\!c!a!p!    bin     (436908 353167 309480) intersection)
  (!\!c!u!p!    bin     (436908 353167 309480) union)
  (!\!i!n!      rel     (436908 353167 309480) member)
  (!\!t!o!      rel     (655361 522469 446015))
  (!\!u!p!a!r!r!o!w!      rel     (327681 268516 241211))
  (!\!d!o!w!n!a!r!r!o!w!  rel     (327681 268516 241211))
  (!<           rel     (509726 409601 354991) lessp)
  (!>           rel     (509726 409601 354991) greaterp)
  (!\!l!e!q!    rel     (509726 409601 354991) leq)
  (!\!g!e!q!    rel     (509726 409601 354991) geq)
  (!\!n!e!q!    rel     (509726 402230 336788) neq)
  (!\!m!i!d!    rel     (182045 155648 150188) when)
  (!\!f!o!r!a!l!l!  ord (364090 296733 263968) !~)
  (!\!R!i!g!h!t!a!r!r!o!w!   rel     (655361 522469 446015)  replaceby)
  (!(           ord     (254863 204801 177495))
  (!)           ord     (254863 204801 177495))
  (!\!i!n!f!t!y!    ord (655361 522469 446015) infinity)
  % The rest are non-standard TeX macros defined in tridefs.tex
  (!\!c!d!o!t!  ord     (109224 89505 80403)     times)
  (!\!a!s!i!n!  ord     (1132319 906677 780527)  asin)
  (!\!a!c!o!s!  ord     (1205136 963111 826038)  acos)
  (!\!a!t!a!n!  ord     (1274315 1016723 869275) atan)
  (!\!A!l!p!h!a!    ord (491521 386847 321314))
  (!\!B!e!t!a!  ord     (464215 366366 306295))
  (!\!E!p!s!i!l!o!n!  ord (446010 352257 294916))
  (!\!Z!e!t!a!  ord     (400498 317669 268520))
  (!\!E!t!a!    ord     (491521 386847 321314))
  (!\!I!o!t!a!  ord     (236658 189328 162021))
  (!\!K!a!p!p!a!    ord (509726 400956 332691))
  (!\!M!u!      ord     (600748 471498 389581))
  (!\!N!u!      ord     (491521 386847 321314))
  (!\!R!h!o!    ord     (446010 352257 294916))
  (!\!T!a!u!    ord     (473316 374103 314031))
  (!\!C!h!i!    ord     (491521 386847 321314))
  (!\!O!m!e!g!a!    ord (473316 374103 314031))
));
%ff
% ----------------------------------------------------------------------
% You  can  choose  to  have  some  default  TEXNAME properties for your
% variables.  Function "trimakeset" defines a set of such default names.
% If  you want to activate the set,  call "TeXassertset(<setname>)" , or
% if  you want to deactivate the set,  call "TeXretractset(<setname>)" .
% The current <setname>s available are:
%    *     GREEK    : lowercase greek letters
%    *     LOWERCASE: roman lowercase letters
% ----------------------------------------------------------------------
% handle argument passing
deflist( '((texassertset rlis) (texretractset rlis)), 'stat);

symbolic procedure texassertset(arglist);
if length arglist neq 1 then rederr "Usage: TeXassertset(setname);"
else begin scalar sym; sym:= car arglist;
  if get('texsym,sym) then
  << assertl(get('texsym,sym)); prin2 "% set ";
     prin2 sym; prin2 " asserted"; terpri()
  >> else << prin2 "% no such set"; terpri() >>
end;

symbolic procedure texretractset(arglist);
if length arglist neq 1 then rederr "Usage: TeXretractset(setname);"
else begin scalar sym; sym := car arglist;
  if get('texsym,sym) then
  << retractl(get('texsym,sym)); prin2 "% set ";
     prin2 sym; prin2 " retracted"; terpri()
  >> else << prin2 "% no such set"; terpri() >>
end;

symbolic procedure trimakeset(sym,a!_set);
   <<put('texsym,sym,a!_set); nil>>;

trimakeset('greek,'(
  (alpha        !\!a!l!p!h!a!   )
  (beta         !\!b!e!t!a!     )
  (gamma        !\!g!a!m!m!a!   )
  (delta        !\!d!e!l!t!a!   )
  (epsilon      !\!e!p!s!i!l!o!n! )
  (zeta         !\!z!e!t!a!     )
  (eta          !\!e!t!a!       )
  (theta        !\!t!h!e!t!a!   )
  (iota         !\!i!o!t!a!     )
  (kappa        !\!k!a!p!p!a!   )
  (lambda       !\!l!a!m!b!d!a! )
  (mu           !\!m!u!         )
  (nu           !\!n!u!         )
  (xi           !\!x!i!         )
  (pi           !\!p!i!         )
  (rho          !\!r!h!o!       )
  (sigma        !\!s!i!g!m!a!   )
  (tau          !\!t!a!u!       )
  (upsilon      !\!u!p!s!i!l!o!n! )
  (phi          !\!p!h!i!       )
  (chi          !\!c!h!i!       )
  (psi          !\!p!s!i!       )
  (omega        !\!o!m!e!g!a!   ) ));
trimakeset('lowercase,'(
  (a !a) (b !b) (c !c) (d !d) (e !e) (f !f) (g !g) (h !h) (i !i) (j !j)
  (k !k) (l !l) (m !m) (n !n) (o !o) (p !p) (q !q) (r !r) (s !s) (t !t)
  (u !u) (v !v) (w !w) (x !x) (y !y) (z !z) ));
trimakeset('!Greek,'(
  (!Alpha        !\!A!l!p!h!a!   )
  (!Beta         !\!B!e!t!a!     )
  (!Gamma        !\!G!a!m!m!a!   )
  (!Delta        !\!D!e!l!t!a!   )
  (!Epsilon      !\!E!p!s!i!l!o!n! )
  (!Zeta         !\!Z!e!t!a!     )
  (!Eta          !\!E!t!a!       )
  (!Theta        !\!T!h!e!t!a!   )
  (!Iota         !\!I!o!t!a!     )
  (!Kappa        !\!K!a!p!p!a!   )
  (!Lambda       !\!L!a!m!b!d!a! )
  (!Mu           !\!M!u!         )
  (!Nu           !\!N!u!         )
  (!Xi           !\!X!i!         )
  (!Pi           !\!P!i!         )
  (!Rho          !\!R!h!o!       )
  (!Sigma        !\!S!i!g!m!a!   )
  (!Tau          !\!T!a!u!       )
  (!Upsilon      !\!U!p!s!i!l!o!n! )
  (!Phi          !\!P!h!i!       )
  (!Chi          !\!C!h!i!       )
  (!Psi          !\!P!s!i!       )
  (!Omega        !\!O!m!e!g!a!   ) ));
trimakeset('!Uppercase,'(
  (!A !A) (!B !B) (!C !C) (!D !D) (!E !E) (!F !F) (!G !G) (!H !H) (!I !I)
  (!J !J) (!K !K) (!L !L) (!M !M) (!N !N) (!O !O) (!P !P) (!Q !Q) (!R !R)
  (!S !S) (!T !T) (!U !U) (!V !V) (!W !W) (!X !X) (!Y !Y) (!Z !Z) ));
%ff
% ----------------------------------------------------------------------
% Section 2: Inserting Glue into a TeX-Item-List
% ----------------------------------------------------------------------
%
% Glue Items to be inserted between consecutive TeX-Items (similar to
% what TeX does with its items, but this table is slightly modified.)
%
%    Class|ORD|LOP|BIN|REL|OPN|CLO|PCT|INN|
%    -----+---+---+---+---+---+---+---+---+
%    ORD  | 0 | 1 |(2)|(3)| 0 | 0 | 0 | 0 |
%    LOP  | 1 | 1 | * |(3)| 0 | 0 | 0 |(1)|
%    BIN  |(2)|(2)| * | * |(2)| * | * |(2)|
%    REL  |(3)|(3)| * | 0 |(3)| 0 | 0 |(3)|     columns: right items
%    OPN  | 0 | 0 | * | 0 | 0 | 0 | 0 | 0 |     lines:   left items
%    CLO  | 0 | 1 |(2)|(3)| 0 | 0 | 0 | 0 |
%    PCT  |(1)|(1)| * |(1)|(1)|(1)|(1)|(1)|
%    INN  | 0 | 1 |(2)|(3)|(1)| 0 |(1)| 0 |
%    -----+---+---+---+---+---+---+---+---+
%
% The glue items and its meanings:
%   0 ......... no space
%   1 (1) ..... thin space   (no space if sub-/superscript)
%   2 (2) ..... medium space (no space if sub-/superscript)
%   3 (3) ..... thick space  (no space if sub-/superscript)
%   * ......... this case never arises (really?)
% ----------------------------------------------------------------------

symbolic procedure makeglue(mx);
if null mx then nil else
begin
  scalar id1,id2,row,col;
  row:=cdr mx; id1:=car mx;
  while(row) do
  << id2:=car mx; col:=car row;
     while (col) do
     << put(car id1,car id2,car col);
        col:=cdr col; id2:=cdr id2
     >>;
     row:=cdr row; id1:=cdr id1
  >>
end;

makeglue('(
(ord lop bin rel opn clo pct inn)
( 0   1  -2  -3   0   0   0   0 )
( 1   1   0  -3   0   0   0  -1 )
(-2  -2   0   0  -2   0   0  -2 )
(-3  -3   0   0  -3   0   0  -3 )
( 0   0   0   0   0   0   0   0 )
( 0   1  -2  -3   0   0   0   0 )
(-1  -1   0  -1  -1  -1  -1  -1 )
( 0   1  -2  -3  -1   0  -1   0 )
));

smacro procedure kindof(item);  get(item,'textag);
smacro procedure classof(item); get(item,'class);
%ff
smacro procedure groupbeg(kind); % beginning of a group
  memq(kind,'(beg sup sub frc mat));
smacro procedure groupend(kind); (kind='end);
smacro procedure grouphs(kind);  (kind='tab);
smacro procedure groupvs(kind); % vertical group seperator
  memq(kind,'(esp esb sep cr));

symbolic procedure interglue(left,right,depth,nesting);
% compute the glue to be inserted between two TeX items
% parameters: left,right .......... left/right TeX item
%             depth ............... superscript/subscript level
%             nesting ............. depth of parenthesis level
% a glue item is a list consisting of two numbers, i.e.
%            (<width> <penalty>)
% where <width> is the width of the glue in scaled points and <penalty>
% is a negative numeric value indicating 'merits' for a breakpoint.
if (null left)or(null right)or(not atom left)or(not atom right) then nil
else begin
  scalar glue,lc,rc; % glue code and item classes
  lc:=classof(left); rc:=classof(right); glue:=get(lc,rc);
  if null(glue) then return nil;
  if (left='!\co! ) then return(list(0,-10000));
  if glue<0 then if depth>0 then return nil else glue:=(-glue);
  if glue=1 then return(list(80960,nesting*10 +20))
  else if glue=2 then
  << if (left='!+ or left='!-) then return nil;
     if (right='!+) then return(list(163840,nesting*30-390));
     if (right='!- and (lc='ord or lc='clo))
     then return(list(163840,nesting*30-210));
     if (left='!\!c!d!o!t! ) then return(list(163840,nesting*10+50));
     if (right='!\!c!d!o!t! ) then return nil;
     return(list(163840,nesting*10))
  >>
  else if glue=3 then return(list(655360,nesting*10-50)) else return nil
end;

symbolic procedure insertglue(term);
% insert glue into a TeX-Item-List
begin
  scalar glueitem,succ,pred,prev,backup; integer depth,nesting;
  depth:=nesting:=0; succ:=nil; backup:=term;
  while term do
  << pred:=succ; succ:=car term;
     glueitem:=interglue(pred,succ,depth,nesting);
     if glueitem then rplacd(prev,glueitem.term);
     prev:=term; term:=cdr term;
     if classof(succ)='inn then
     << if (groupbeg kindof succ) and
           (not ((kindof(succ)='frc) and (depth=0)))
        then depth:=depth+1
        else if (groupend kindof succ) and (depth>0) then depth:=depth-1
     >>
     else if classof(succ)='opn then nesting:=nesting+1
     else if classof(succ)='clo then nesting:=nesting-1
  >>;
  return(backup)
end;
%ff
% ----------------------------------------------------------------------
% Section 3 : Line Breaking
% ----------------------------------------------------------------------
%
%     How to break up a TeX item list into several independent lines
% ----------------------------------------------------------------------
% Setting break points requires "breaklists". A  breaklist is a sequence
% of passive and active nodes,  where each active node is followed by an
% pasive node and vice versa. Active nodes represent glue items. Passive
% nodes are integer atoms which represent the width of a sequence of or-
% dinary  TeX  items.   This sequence must not be interspersed with glue
% items.  Every breaklist consists of at least one passive node surroun-
% ded by delta nodes representing the beginning and ending of the list.
%     <breaklist>    ::= ( <delta-node> <passive-node> <active-node> ...
%                                       <passive-node> <active_node> ...
%                                       <passive-node> <delta-node>)
%     <active-node>  ::= ( <width> <penalty> <offset> )
%     <passive-node> ::= <width>
%     <delta-node>   ::= ( <width> <penalty> <offset>
%                          <id-num> <ptr> <demerits> <indentation> )
% The breaklist will be created using the function "breaklist".  Setting
% the break points (i.e. break items) in the breaklist is done using the
% functions "trybreak".  During  this  phase, some active nodes are con-
% sidered to be "feasible" break points. Thus, they will be extended and
% named "delta nodes" furtheron. By default the first and last node in a
% breaklist are delta nodes.  When trybreak has finished, the <ptr>'s of
% the delta nodes recursively pointed to  from  the  last  delta  node's
% <ptr> represent the best path for breaking the whole breaklist.
% It is:
%   <width>      : width of item (including glue items)
%   <penalty>    : a numeric value which prohibits line breaking (if
%                  negative, line breaking will be merited)
%   <offset>     : distance to most previous opening bracket
%   <id-num>     : the identity number of the delta node {1,2,3,...}
%   <ptr>        : pointer to the best delta node to come from with
%                  respect to the minimal demerits path. note: a zero
%                  pointer indicates the very bottom of the stack
%   <demerits>   : total demerits distance to delta node which is
%                  pointed to by <ptr>
%   <indentation>: amount of indentation when breaking at this point
% ----------------------------------------------------------------------
%ff
symbolic procedure width(item,style);
begin scalar tag;
   tag:=get(item,'textag);
   if null tag then tri!-error(list("cannot find item ",item),'fatal);
   while (style>0)and(cdr tag) do << tag:=cdr tag; style:=style-1 >>;
   return car tag or 0
end;

smacro procedure sp2mm(x); (x/186468); % scaled points to millimeters

symbolic procedure settolerance(tol);
<< if tol<0 then tol:=0 else if tol>10000 then tol:=10000;
   prin2 "% \tolerance "; print tol; tolerance!*:=tol; nil
>>;
symbolic procedure setpagewidth(hsize);
% hsize can be given either in millimeters or scaled points.
<< if hsize>400 then hsize!*:=hsize else hsize!*:=hsize*186468;
   prin2 "% \hsize="; prin2 sp2mm(hsize!*); prin2 "mm"; terpri();
   hss!*:=float hsize!*/6;      % default stretch/shrink width
   hww!*:=float (3*hsize!*)/4;  % optimum line width
>>;
symbolic procedure setbreak(hsize,tol);
<< settolerance(tol); setpagewidth(hsize) >>;

smacro procedure badness(hlen,ibadness);
% The badness is 100*(hlen/hss)**3, corrected for indentation badness
begin
  real r;
  r:=abs(hlen-hww!*)/hss!*;
  return fix min(10000.0,r*r*r*100.0+ibadness)
end;

smacro procedure isglue(l);         (not atom l) and (numberp car l);
smacro procedure isactive(x);       not numberp x;
smacro procedure ispassive(x);      numberp x;
smacro procedure isdelta(x);        cdddr x;
smacro procedure addup(x);          if x then eval('plus.x) else 0;
smacro procedure tpush(stack,item); stack:=item.stack;

smacro procedure tpop(stack);
  if null stack then nil % Error
  else begin scalar z; z:=car stack; stack:=cdr stack; return(z) end;

smacro procedure poke(stack,ptr,val);
if null ptr then stack:=nconc(stack,val.nil)
else << if val>car(ptr) then rplaca(ptr,val); ptr:=cdr ptr >>;

smacro procedure concatenate(l);
begin scalar r;
  for each e in l do r:=nconc(r,explode e);
  return compress r
end;
%ff
% ----------------------------------------------------------------------
% Section 3.1:  Resolving Fraction Expressions
% ----------------------------------------------------------------------

symbolic procedure resolve(term);
% resolve a  \frac{...}{...}  sequence and transform it into a   .../...
% sequence, where any ... argument may become parenthesized depending on
% the question if there is any non-ORD-class item within this argument.
% Furthermore, resolve a \sqrt{...} expression to \(...\)^{\frac{1}{2}}.
begin
  scalar item,l,m,r,lflag,rflag;
  integer depth;
  l:=term;                            % save pointer to functor
  depth:=0; m:=r:=lflag:=rflag:=nil; item:=t;
  while term and item do
  << item:=car term;                  % take first item from list
     if classof(item)='inn then       % check inner class item
     << item:=kindof(item);
        if groupbeg(item) then depth:=depth+1
        else if groupend(item) then
          if depth=1 then             % outermost level ?
          << r:=term; item:=nil       % save pointer to right bracket
          >>                          % and quit using item as a flag
          else depth:=depth-1
        else if groupvs(item) then    % if outermost level then save
         if (depth=1) then m:=term    % pointer to intermediate brackets
     >>
     else if not(classof(item)='ord) then % non-ORD-class item ?
     << if m then rflag:=t else lflag:=t
     >>;
     term:=cdr term                   % step ahead
  >>;
  if car l='!\!f!r!a!c!{ then
  << if lflag and rflag
       then item:=list('!/,list(655360,-10000))
       else item:=list('!/);
     if lflag then << rplaca(l,'!\!(); item:='!\!).item >>
     else rplaca(l,'! );
     if rflag then << rplaca(r,'!\!)); nconc(item,'!\!(.nil) >>
     else rplaca(r,'! );
     rplaca(m,car item); item:=cdr item;
     if item then rplacd(m,nconc(item,cdr m))
  >> else if car l='!\!s!q!r!t!{ then
  << rplaca(l,'!\!(); rplaca(r,'!\!));
     rplacd(r,'!^!{ .  '!1 . '!/ . '!2 . '!} . cdr r)
  >>;
  return(l)                           % return changed list pointer
end;
%ff
% ----------------------------------------------------------------------
% Section 3.2 : Create a Break List
% ----------------------------------------------------------------------

symbolic procedure breaklist(term);
begin
  scalar item,result,kind,vstack,hstack,fstack,pstack,p,flag,backup;
  integer depth,acc,aux,lopw,total,indent;
  p:=result:=vstack:=hstack:=fstack:=nil; backup:=term;
  depth:=total:=acc:=lopw:=indent:=0;
  while term do
  << item:=car term; flag:=t;              % get first item from term
     if null item
     then tri!-error(list("found NIL in term : ",backup),'fatal);
     if (isglue(item)) then                % do we have glue ahead ?
       if (depth<1) then                   % are we on the top level ?
       << % insert a passive node followed by an active node, clear acc.
          total:=total+acc+car item; nconc(item,indent.nil);
          result:=nconc(result,acc.item.nil); acc:=0
       >>
       else acc:=acc+car item              % add up glue width
     else if (classof(item)='lop) then lopw:=width(item,depth)
     else if classof(item)='inn then
       << kind:=kindof(item);
          if kind='frc then
          << tpush(fstack,term); tpush(fstack,depth)
          >>;
          if groupend(kind) then           % end of TeX group ?
          << depth:=depth-1;               % decrement term depth
             if acc>0                      % if <acc> hasn't been poked
             then poke(vstack,p,acc);      % yet, then poke it
             acc:=tpop(hstack);            % get old acc value
             aux:=addup(vstack);           % compute vstack width
             if fstack and (depth=car fstack) then
             << tpop(fstack);              % first waste depth info
                if aux>hww!* then          % check if it doesn't fit
                << term:=resolve tpop fstack;% resolve fraction
                   flag:=nil               % evaluate new list
                >>
                else                       % waste fraction term pointer
                << tpop(fstack); acc:=acc+aux
                >>
             >> else acc:=acc+aux;
             p:=tpop(hstack); vstack:=tpop(hstack) % reset old status
          >>
          else if groupbeg(kind) then      % begin of TeX group ?
          << depth:=depth+1;               % increment term depth
             tpush(hstack,vstack);         % save current <vstack> and
             tpush(hstack,p);              % current <p> as well as
             tpush(hstack,acc);            % current <acc> to <hstack>
             acc:=0; p:=vstack:=nil;       % clear vertical stack
             if lopw>0 then poke(vstack,p,lopw); lopw:=0
          >>
          else if grouphs(kind) then       % horizontal separator ?
          << poke(vstack,p,acc); acc:=0    % poke <acc> to <vstack>
          >>
          else if groupvs(kind) then       % vertical separator ?
          << poke(vstack,p,acc); acc:=0; p:=vstack % reset
          >>
       >>
%ff
     else if depth<1 then
     << aux:=width(item,depth);            % add up item width
        if classof(item)='opn then
        << tpush(pstack,indent); indent:=total+acc+aux
        >>;
        if classof(item)='clo then indent:=tpop(pstack) or 0;
        acc:=acc+aux
     >>
     else acc:=acc+width(item,depth);      % add up item width
     if lopw>0 then << acc:=acc+lopw; lopw:=0 >>;
     if flag then term:=cdr term
  >>;
  if acc then total:=total+acc;
  if (total<hsize!*) then return nil % need no breaking
  else return(list(0,0,0,0,0,0,0).nconc(result,acc.
        list(0,0,total,-1,0,2147483647,0).nil))     % return break list
end;

%ff
% ----------------------------------------------------------------------
% Section 3.3 : Major Line Breaking Routine
% ----------------------------------------------------------------------

smacro procedure widthof(deltanode);   car deltanode;
smacro procedure penaltyof(deltanode); cadr deltanode;
smacro procedure totalof(deltanode);   cadr deltanode;
smacro procedure offsetof(deltanode);  caddr deltanode;
smacro procedure idof(deltanode);      cadddr deltanode;
smacro procedure ptrof(deltanode);     car cddddr deltanode;
smacro procedure indentof(deltanode);  caddr cddddr deltanode;
smacro procedure tailof(deltanode);    cddddr deltanode;

symbolic procedure offsetitem(item);
  concatenate list('!\!o!f!f!{,item,'!} );

smacro procedure stepahead(ptr,val);
<< if ispassive car ptr then val:=val+car ptr else val:=val+caar ptr;
   ptr:=cdr ptr
>>;

smacro procedure findindent(offt,ptr);
if offt=lastoff and ptr=lastptr then lastindent else
begin % search the deltastack for previous indentation
  scalar node,p,stack; integer tot;
  stack:=deltastack; p:=lastptr:=ptr; lastoff:=offt;
  while stack do
  << if p=idof (node:=car stack) then
     << p:=ptrof node; tot:=totalof node;
        if tot<offt then stack:=nil
     >>;
     if stack then stack:=cdr stack;
  >>;
  return(lastindent:=offt-tot+indentof node)
end;
%ff
symbolic procedure trybreak(term,brkl);
% parameters: term .... TeX item list, as created by "interglue"
%             brkl .... the breaklist to be processed by this routine
begin
  scalar bottom,top,base,item,deltastack,pred;
  integer depth;               % depth of expression when rebuilding
  integer feasible,id;         % number of feasible delta node
  integer len,total;           % current and total length so far
  integer dm,basedm;           % current and base demerits
  integer bd;                  % current badness
  integer penalty;
  integer offset,baseoffset;   % current and base parenthesis offset
  integer baseptr;             % pointer to best way to come from
  integer indent,baseindent;   % current and base indentation
  integer lastoff,lastindent,lastptr; % temp. var. for speedup
  real indentbadness;          % correction for indentation badness
  if null brkl then goto retain;
  bottom:=brkl;
  lastoff:=lastptr:=lastindent:=feasible:=indent:=total:=0;
  while bottom do
  << top:=cdr bottom; base:=car bottom; pred:=tailof base;
     id:=idof base;            % id of current delta node
     if penaltyof base=-10000  % break item ?
     then rplaca(cdr pred,0);  % new line
     basedm:=cadr pred;        % demerits so far
     % save the delta node to the delta-stack. thus deltastack holds
     % all the feasible breakpoints in reverse order.
     deltastack:=base.deltastack;
     len:=baseindent:=indentof(base); % indentation for this line
     indentbadness:=2500.0*(float(baseindent)/float(hww!*));
     baseoffset:=offsetof base;% current offset amount
     baseptr:=car pred;        % pointer to best node to come from
     total:=total+widthof base;% correct total length
%--- debug ---
% prin2 "Base ["; prin2 id; prin2 "] basedm="; prin2 basedm;
% prin2 " ibd="; prin2 indentbadness;
% prin2 " indent="; prin2 baseindent; terpri();
%--- debug ---
%ff
     while top and len<hsize!* do % loop once thru a potential line
     % note that we use the local hsize instead of the full hsize
     << item:=car top;
        if ispassive(item) then len:=len+item else
        << bd:=badness(len,indentbadness);
           penalty:=penaltyof item;
           offset:=offsetof item;
           if (bd<tolerance!*)       % is the breakpoint feasible?
           or (bd+penalty<1)         % got a break bonus ?
           or (null cdr top) then    % or did we reach last delta node?
           << dm:=bd*bd+basedm+penalty*abs(penalty);
              if isdelta(item) then
              << pred:=tailof item;
                 if dm<cadr pred then % found a better path?
                 << % save the pointer to best breakpoint to come from
                    % and the minimum demerits to reach it
                    rplaca(pred,id); rplaca(cdr pred,dm);
                    if !*texindent then % save the current indentation
                    << if offset>total
                       then indent:=offset-total+baseindent
                       else if offset<baseoffset
                         then indent:=findindent(offset,baseptr)
                         else indent:=baseindent;
                       rplaca(cddr pred,indent)
                    >>
                 >>
              >>
              else             % create a new delta node
              << feasible:=feasible+1;
                 if !*texindent then
                   if offset>total
                   then indent:=offset-total+baseindent
                   else if offset<baseoffset
                     then indent:=findindent(offset,baseptr)
                     else indent:=baseindent
                 else indent:=0;
                 rplacd(cddr item,feasible.id.dm.indent.nil)
              >>;
%--- debug ---
% prin2 "-->["; prin2 idof item; prin2 "] dm="; prin2 dm;
% prin2 " bd="; prin2 bd; prin2 " p="; prin2 penalty;
% if !*TeXindent then << prin2 " ind="; prin2 indent >>; terpri();
%--- debug ---
              if penalty=-10000 then top:=nil
           >>;
           len:=len+car item   % count the length anyway
        >>;
        if top then top:=cdr top
     >>;
%ff
     rplaca(cdr base,total);   % replace penalty by total width so far
     bottom:=cdr bottom;       % depart from this delta node
     while bottom and (ispassive(car bottom) or not isdelta(car bottom))
     do stepahead(bottom,total); % move to next delta node in list
  >>;
  bottom:=deltastack; feasible:=-1; top:=nil;
  while bottom do              % loop thru the delta-node stack
  << id:=idof car bottom;      % id is the current id number
     if id=feasible then       % is this node the one pointed to?
     << feasible:=ptrof car bottom; % feasible is the new back-pointer
        top:=id.top;           % save the path element
     >>;
     bottom:=cdr bottom        % step ahead
  >>;                          % now deltastack contains the best path
  deltastack:=cdr top;         % in forward order
%--- debug ---
% print term; print deltastack;
%--- debug ---
  if car deltastack= -1 then
  << prin2 "% Warning:    no suitable way of breaking found"; terpri();
     prin2 "% ========    retry with a greater tolerance..."; terpri();
     prin2 "% (output will produce overfull box if printed)"; terpri()
  >>;
  brkl:=cdr brkl;              % strip the dummy node at the list's head
%ff
  % --------------------------------------------------------------------
  % now remove all glue items but retain all break items
  retain: % ------------------------------------------------------------
  offset:=depth:=0; bottom:=term;
  if brkl then brkl:=cdr brkl; % ensure first item is an active node
  while term and (cdr term) do
  << item:=car term;
     if isglue(item) then           % if this is a glue item
       if (depth=0) and brkl then   % and we are on the top level
       << top:=car brkl;
          if isdelta(top) then      % consider delta nodes only
          << if (idof top=car deltastack) then % break point?
             << deltastack:=cdr deltastack;
%--- debug ---
% prin2 "% ["; prin2 idof top; prin2 "] ";
% prin2 sp2mm(totalof(top)+indentof(top)-offset); terpri();
% offset:=totalof(top);
%--- debug ---
                if (len:=indentof top)>0
                then rplacd(pred,'!\!n!l! . offsetitem(len) . cdr term)
                else rplacd(pred,'!\!n!l! . cdr term)
             >>
             else rplacd(pred,cdr term)
          >>
          else rplacd(pred,cdr term);
          if brkl and (cdr brkl)    % check for next active node
          then brkl:=cddr brkl      % skip to next active node
       >>
       else rplacd(pred,cdr term)  % remove glue item
     else if classof(item)='inn then
     << if groupbeg(kindof(item)) then depth:=depth+1 else
        if groupend(kindof(item)) then depth:=depth-1
     >>;
     pred:=term; term:=cdr term
  >>;
%--- debug ---
% top:=car term; prin2 "% [-1] ";
% prin2 sp2mm(totalof(top)+indentof(top)-offset); terpri();
%--- debug ---
  return(bottom)
end;
%ff
% ----------------------------------------------------------------------
% Section 4 : Output of TeX-Code
% ----------------------------------------------------------------------

symbolic procedure texstrlen(s);
begin
  integer length;
  scalar flag;
  length:=0; flag:=nil;
  for each c in s do
    if not flag and c='!! then flag:=t
    else << length:=length+1; flag:=nil >>;
  return length
end;

smacro procedure newline();
  if nlflag then cc:=indent
  else if (cc>indent) then << terpri(); cc:=indent; nlflag:=t >>;

%ff
symbolic procedure texout(itemlist,flag);
if null itemlist then nil else
begin
   integer cc,len,indent,ccmax,lines;
   scalar item,class,tag,oldtag,lasttag,indentstack,ispd,nlflag;
   ccmax:=64; cc:=indent:=lines:=0;        % initializations
   tag:=ispd:=nlflag:=indentstack:=nil;    % initializations
   prin2('!$!$);                           % begin TeX math group
   if flag then prin2('!\!d!i!s!p!l!a!y!l!i!n!e!s!{!\!q!d!d);
   terpri();                               % start new line
   while itemlist do
   << item:=car itemlist; itemlist:=cdr itemlist;
      len:=texstrlen(explode(item)); oldtag:=nil; lasttag:=tag or class;
      class:=classof(item); tag:=(class='inn)and(kindof(item));
      %ispd:=(class='ORD)and itemlist and(classof(car itemlist)='OPN);
      if (tag='mat)or(tag='frc)or(class='opn) %or ispd
      then newline();                      % start new line
      if (groupbeg(tag))or(class='opn) then
      << tpush(indentstack,indent);        % push it to the stack
         tpush(indentstack,lasttag);       % the reason for pushing
         if (cc+cc < ccmax)                % within left half of page ?
         then if ((class='opn)and(lasttag='ord))or % predicate?
                 (groupbeg(tag)and not((tag='frc)or(tag='mat)))
           then indent:=cc+len             % take current position
           else indent:=indent+len         % compute new indentation
      >>
      else if (groupend(tag))or(class='clo) then
      << oldtag:=tpop(indentstack); indent:=tpop(indentstack)
      >>;
      if (cc+len > ccmax) or      % beyond right margin ?
         (item='!+)or(item='!-)or(class='clo) % important item?
      then newline();
      if nlflag then << nlflag:=nil; spaces(cc) >>;
      if tag='cr  then lines:=lines+1;
      if not(item='! ) then prin2(item);   % print the item and
      cc:=cc+len;                          % count the characters
      if groupvs(tag) or                   % vertical seperator ?
         (groupend(tag) and                % end of a large group,
          ((oldtag='frc) or (oldtag='mat)))% i.e. fraction, matrix ?
         or (class='clo) or                % closing parenthesis ?
         (((class='rel)or(class='bin))and  % binary/relational operator?
          (cc+cc+cc > ccmax+ccmax))        % within last third of page?
         or item='!,  or null class
      then newline()
   >>;
   newline();                              % start final line
   if flag then
     if lines=0 then prin2('!\!c!r!})
     else prin2('!\!N!l!});                % end multi-line output
   prin2('!$!$); terpri(); return(nil)     % end math group
end;
%ff
% ----------------------------------------------------------------------
% Section 5: User Interface
% ----------------------------------------------------------------------
% handle argument passing for following the functions, compelling that
% properties are used during compile time
deflist( '((texdisplay rlis) (texlet rlis)), 'stat);

algebraic procedure texsetbreak(hsize,tol); lisp setbreak(hsize,tol);
algebraic procedure textolerance(tol); lisp settolerance(tol);
algebraic procedure texpagewidth(hsize); lisp setpagewidth(hsize);

symbolic procedure texlet(arglist);
begin scalar class,sym,item;
  if length arglist neq 2 then rederr "Usage: TeXlet(symbol,item);";
  sym:= car arglist; item:=intern cadr arglist; class:=classof(item);
  if null class then
  << prin2 "% No such TeX symbol available"; terpri()
  >>
  else if (class='inn) then % prevent from TeXequiv'ing inner symbols
  << prin2 "% cannot assign inner TeX symbols yet"; terpri()
  >>
  else triassert(sym,item);
  return nil
end;

symbolic procedure texdisplay(arglist);
begin scalar item,tag,class;
  if length arglist neq 1 then rederr "Usage: TeXdisplay(item);";
  item:=get(car arglist,'texname);
  if not item then
  << prin2 "% "; prin2 car arglist; prin2 " is not defined"; terpri()
  >>;
  if not item then return nil;
  tag:=get(item,'textag);  class:=get(item,'class);
  prin2 "% TeX item "; prin2 item; prin2 " is of class "; prin2 class;
  prin2 " and has following widths: "; terpri(); prin2 "% ";
  for each w in tag do
  begin real v; v:=w/65536.0; prin2 v; prin2 "pt  " end;
  terpri(); return nil
end;

% ----------------------- share name between both modes ----------------

symbolic operator texlet;
symbolic operator texitem;
symbolic operator texdisplay;
symbolic operator texassertset;
symbolic operator texretractset;

% ------------------------ Default Initializations ---------------------

<< prin2 "% TeX-REDUCE-Interface 0.70"; terpri() >>;
texassertset(greek); texassertset(lowercase);
texassertset '!Greek; texassertset '!Uppercase;
textolerance(10); texpagewidth(150);

endmodule;


end;

