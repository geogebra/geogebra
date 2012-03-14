module intro;  % Introductory material for algebraic mode.

% Author: Anthony C. Hearn.

% Copyright (c) 1991 RAND.  All rights reserved.

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


fluid '(!*cref !*exp !*factor !*fort !*ifactor !*intstr !*lcm !*mcd
        !*msg !*mode !*nat !*nero !*period !*precise !*pri !*protfg
        !*rationalize !*reduced !*sub2 !*varopt !*wsm posn!* subfg!*);

global '(!*resubs
         !*val
         erfg!*
         exlist!*
         initl!*
         nat!*!*
         ofl!*
         simpcount!*
         simplimit!*
         tstack!*);

% Non-local variables needing top level initialization.

!*exp := t;             % expansion control flag;
!*lcm := t;             % least common multiple computation flag;
!*mcd := t;             % common denominator control flag;
!*mode := 'symbolic;    % current evaluation mode;
!*msg := t;             % flag controlling message printing;
!*nat := t;             % specifies natural printing mode;
!*period := t;          % prints a period after a fixed coefficient
                        % when FORT is on;
!*precise := t;         %  Specifies more precise handling of surds.
!*resubs := t;          % external flag controlling resubstitution;
!*val := t;             % controls operator argument evaluation;
!*varopt := t;          % Used by SOLVE, etc.
exlist!* := '((!*));    % property list for standard forms used as
                        %  kernels;
initl!* := append('(subfg!* !*sub2 tstack!*),initl!*);
simpcount!* := 0;       % depth of recursion within simplifier;
simplimit!* := 2000;    % allowed recursion limit within simplifier;
subfg!* := t;           % flag to indicate whether substitution
                        % is required during evaluation;
tstack!* := 0;          % stack counter in SIMPTIMES;

% Initial values of some global variables in BEGIN1 loops.

put('subfg!*,'initl,t);

put('tstack!*,'initl,0);


% Description of some non-local variables used in algebraic mode.

% alglist!* := nil . nil; %association list for previously simplified
                        %expressions;
% asymplis!* := nil;    %association list of asymptotic replacements;
% cursym!*              current symbol (i. e. identifier, parenthesis,
%                       delimiter, e.t.c,) in input line;
% dmode!* := nil;       %name of current polynomial domain mode if not
                        %integer;
% domainlist!* := nil;  %list of currently supported poly domain modes;
% dsubl!* := nil;       %list of previously calculated derivatives of
                        % expressions;
% exptl!* := nil;       %list of exprs with non-integer exponents;
% frlis!* := nil;       %list of renamed free variables to be found in
                        %substitutions;
% kord!* := nil;        %kernel order in standard forms;
% kprops!* := nil;      %list of active non-atomic kernel plists;
% mchfg!* := nil;       %indicates that a pattern match occurred during
                        %a cycle of the matching routines;
% mul!* := nil;         %list of additional evaluations needed in a
                        %given multiplication;
% nat!*!* := nil;       %temporary variable used in algebraic mode;
% ncmp!* := nil;        %flag indicating non-commutative multiplication
                        %mode;
% ofl!* := nil;         %current output file name;
% posn!* := nil;        %used to store output character position in
                        %printing functions;
% powlis!* := nil;      %association list of replacements for powers;
% powlis1!* := nil;     %association list of conditional replacements
                        %for powers;
% subl!* := nil;        %list of previously evaluated expressions;
% wtl!* := nil;         %tells that a WEIGHT assignment has been made;
% !*ezgcd := nil;       %ezgcd calculation flag;
% !*float := nil;       %floating arithmetic mode flag;
% !*fort := nil;        %specifies FORTRAN output;
% !*gcd := nil;         %greatest common divisor mode flag;
% !*group := nil;       %causes expressions to be grouped when EXP off;
% !*intstr := nil;      %makes expression arguments structured;
% !*int                 indicates interactive system use;
% !*match := nil;       %list of pattern matching rules;
% !*nero := nil;        %flag to suppress printing of zeros;
% !*nosubs := nil;      %internal flag controlling substitution;
% !*numval := nil;      %used to indicate that numerical expressions
                        %should be converted to a real value;
% !*outp := nil;        %holds prefix output form for extended output
                        %package;
% !*pri := nil;         %indicates that fancy output is required;
% !*reduced := nil;     %causes arguments of radicals to be factored.
                        %E.g., sqrt(-x) --> i*sqrt(x);
% !*sub2 := nil;        %indicates need for call of RESIMP;


% ***** UTILITY FUNCTIONS *****.

symbolic procedure reversip2(a, b);
  begin
    scalar w;
    while a do <<
      w := cdr a;
      rplacd(a, b);
      b := a;
      a := w >>;
    return b
  end; 

symbolic procedure mkid(x,y);
  % creates the ID XY from identifier X and (evaluated) object Y.
  if not idp x then typerr(x,"MKID root")
   else if atom y and (idp y or fixp y and not minusp y)
    then intern compress nconc(explode x,explode y)
   else typerr(y,"MKID index");

flag('(mkid),'opfn);

symbolic procedure multiple!-result(z,w);
   % Z is a list of items (n . prefix-form), in ordering in descending
   % order wrt n, which must be non-negative.  W is either an array
   % name, another id, a template for a multi-dimensional array or NIL.
   % Elements of Z are accordingly stored in W if it is non-NIL, or
   % returned as a list otherwise.
   begin scalar x,y;
        if null w then return 'list . reversip!* fillin z;
        x := getrtype w;
        if x and not(x eq 'array) then typerr(w,"array or id");
        lpriw("*****",
              list(if x eq 'array then "ARRAY" else "ID",
                   "fill no longer supported --- use lists instead"));
        if atom w then (if not arrayp w
           then (if numberp(w := reval w) then typerr(w,'id)))
         else if not arrayp car w then typerr(car w,'array)
         else w := car w . for each x in cdr w
                            collect if x eq 'times then x else reval x;
        x := length z-1;  % don't count zeroth element;
        if not((not atom w and atom car w
                         and (y := dimension car w))
             or ((y := dimension w) and null cdr y))
         then <<y := explode w;
                w := nil;
                for each j in z do
                   <<w := intern compress append(y,explode car j) . w;
                     setk1(car w,cdr j,t)>>;
                lprim if length w=1 then list(car w,"is non zero")
                       else aconc!*(reversip!* w,"are non zero");
                return x>>
         else if atom w
          then <<if caar z neq (car y-1)
                   then <<y := list(caar z+1);
                          % We don't use put!-value here.
                          put(w,'avalue,
                              {'array,mkarray1(y,'algebraic)});
                          put(w,'dimension,y)>>;
                 w := list(w,'times)>>;
        y := pair(cdr w,y);
        while y and not smemq('times,caar y) do y := cdr y;
        if null y then errach "MULTIPLE-RESULT";
        y := cdar y-reval subst(0,'times,caar y)-1;
           %-1 needed since DIMENSION gives length, not highest index;
        if caar z>y
          then rerror(alg,3,list("Index",caar z,"out of range"));
        repeat
           if null z or y neq caar z
             then setelv(subst(y,'times,w),0)
            else <<setelv(subst(y,'times,w),cdar z); z := cdr z>>
          until (y := y-1) < 0;
        return x
   end;

symbolic procedure fillin u;
   % fills in missing terms in multiple result argument list u
   % and returns list of coefficients.
   if null u then nil else fillin1(u,caar u);

symbolic procedure fillin1(u,n);
   if n<0 then nil
    else if u and caar u=n then cdar u . fillin1(cdr u,n-1)
    else 0 . fillin1(u,n-1);


% ***** FUNCTIONS FOR PRINTING DIAGNOSTIC AND ERROR MESSAGES *****

symbolic procedure msgpri(u,v,w,x,y);
   begin integer posn!*; scalar nat1,z,pline!*;
        if null y and null !*msg then return;
        nat1 := !*nat;
        !*nat := nil;
        if ofl!* and (!*fort or not nat1) then go to c;
    a:  terpri();
        lpri ((if null y then "***" else "*****")
                 . if u and atom u then list u else u);
        posn!* := posn();
        maprin v;
        prin2 " ";
        lpri if w and atom w then list w else w;
        posn!* := posn();
        maprin x;
        terpri!*(t); % if not y or y eq 'hold then terpri();
        if null z then go to b;
        wrs cdr z;
        go to d;
    b:  if null ofl!* then go to d;
    c:  z := ofl!*;
        wrs nil;
        go to a;
    d:  !*nat := nat1;
        if y then if y eq 'hold then erfg!* := y else error1()
   end;

symbolic procedure errach u;
   begin
        terpri!* t;
        lprie "CATASTROPHIC ERROR *****";
        printty u;
        lpriw(" ",nil);
        rerror(alg,4,
   "Please report output and input listing on the sourceforge bug tracker")
   end;

symbolic procedure errpri1 u;
   msgpri("Substitution for",u,"not allowed",nil,t);  % was 'HOLD

symbolic procedure errpri2(u,v);
   msgpri("Syntax error:",u,"invalid",nil,v);

symbolic procedure redmsg(u,v);
    if !*wsm or null !*msg or v neq "operator" then nil
     else if terminalp() then yesp list("Declare",u,v,"?") or error1()
     else lprim list(u,"declared",v);

symbolic procedure typerr(u,v);
   % Note this replaces definition in rlisp/lpri. If outputhandler!* is
   % non-nil I go back to the simple ould version, which may be less
   % pretty but that does not end up with messages getting lost so often!
   if outputhandler!*
     then rerror('rlisp,6, 
                 if not atom u and atom car u and cdr u and atom cadr u
                    and null cddr u
                   then list(car u,cadr u,"invalid as",v)
                  else list(u,"invalid as",v))
   else
   <<if not !*protfg
      then  <<terpri!* t;
              prin2!* "***** ";
              if not atom u and atom car u and cdr u and atom cadr u
                 and null cddr u
                then <<prin2!* car u; prin2!* " "; prin2!* cadr u>>
               else if null u then prin2!* u
               else maprin u;
              prin2!* " invalid as "; prin2!* v;
              terpri!* nil>>;
     erfg!* := t; error1()>>;


%                 ***** ALGEBRAIC MODE DECLARATIONS *****

flag ('(aeval cond getel go prog progn prog2 return
        reval setq setk setel assgnpri !*s2i),'nochange);

flag ('(or and not member memq equal neq eq geq greaterp leq
        fixp lessp numberp ordp freeof),'boolean);

flag ('(or and not),'boolargs);

deflist ('((exp ((nil (rmsubs)) (t (rmsubs))))
        (factor ((nil (setq !*exp t) (rmsubs))
                 (t (setq !*exp nil) (rmsubs))))
        (fort ((nil (setq !*nat nat!*!*)) (t (setq !*nat nil))))
        (gcd ((t (rmsubs))))
        (intstr ((nil (rmsubs)) (t (rmsubs))))
        (mcd ((nil (rmsubs)) (t (rmsubs))))
        (nat ((nil (setq nat!*!* nil)) (t (setq nat!*!* t))))
        (numval ((t (rmsubs))))
        (rationalize ((t (rmsubs))))
        (reduced ((t (rmsubs))))
        (val ((t (rmsubs))))),'simpfg);

switch exp,cref,factor,fort,gcd,ifactor,intstr,lcm,mcd,nat,nero,numval,
       period,precise,pri,rationalize,reduced,varopt;   % resubs, val.

endmodule;

end;
