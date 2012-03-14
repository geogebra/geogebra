module avector;   % Vector algebra and calculus package.

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


create!-package('(avector),'(contrib avector));

global '(avector!-loaded!*);

avector!-loaded!* := t;   % To keep CSL happy.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                      %
%   Copyright notice                                                   %
%   ----------------                                                   %
%                                                                      %
%              Author : David Harper                                   %
%                       Computer Laboratory,                           %
%                       University of Liverpool                        %
%                       P.O. Box 147                                   %
%                       Liverpool L69 3BX                              %
%                       ENGLAND                                        %
%                                                                      %
%                       (adh@maths.qmw.ac.uk)                          %
%                                                                      %
%              Date   : 29 February 1988                               %
%                                                                      %
%              Title  : Vector algebra and calculus package            %
%                                                                      %
%       Copyright (c) David Harper 1988                                %
%                                                                      %
%                                                                      %
% (note that David Harper has given explicit permission to remove a    %
%  previous licensing statement in favour of use of the BSD terms      %
%  shown above)                                                        %
%                                                                      %
%   End of copyright notice                                            %
%                                                                      %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                      %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  ***************  This code is designed to operate  ***************  %
%  ***************  with version 3.4 of REDUCE and    ***************  %
%  ***************  the Standard Lisp interpreter.    ***************  %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                      %
%              VECTOR DECLARATIONS MODULE                              %
%                                                                      %
%  This section contains the routines required to interface the        %
%  vector package to REDUCE. The most important routine is the         %
%  vector predicate function VECP which tests an expression to         %
%  determine whether it must be evaluated using the  routine           %
%  VECSM*.                                                             %
%                                                                      %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


fluid '(!*coords !*csystems !*vectorfunctionlist
        !*vtrace !*vectortracelevel!*);

switch vtrace;

symbolic procedure vecp u;
begin scalar x;
return
  if null u or numberp u then nil
  else if atom u then (get(u,'rtype)='!3vector or threevectorp u)
  else if threevectorp u then t
  else if (atom(x:=car u) and get(x,'rtype)='!3vector)
                            then isvectorindex cadr u
  else if flagp(x,'vectorfn) then t
  else if (flagp(x,'varithop) or flagp(x,'vectormapping))
                            then hasonevector cdr u
  else nil;
 end;

% The following procedure checks for a vector with three components

symbolic procedure threevectorp u;
  vectorp u and (upbv u = 2);

% The following procedure checks to ensure that the arg list contains
% at least one vector
symbolic procedure hasonevector u;
  if null u then nil
    else (vecp car u) or (hasonevector cdr u);

% The following procedure checks that the arg list consists entirely
% of vectors
symbolic procedure areallvectors u;
  if null u then nil
  else if null cdr u then vecp car u
  else (vecp car u) and (areallvectors cdr u);

% The following function checks to see whether its argument is a valid
% vector index i.e. whether it evaluates to an integer 0,1 or 2 or
% is equal to one of the coordinate names
symbolic procedure isvectorindex u;
  not null getvectorindex(u,t);

% The following function evaluates its argument to a vector index
% or NIL if it isn't an integer 0,1 or 2 or one of the coordinate
% names. Set FLG to true if hard-error is required for invalid
% argument.
symbolic procedure getvectorindex(u,flg);
  begin scalar vindx;
  vindx := u;
  if not fixp vindx then vindx:=locate(vindx,!*coords);
  if ((null vindx) or (fixp vindx and (vindx<0 or vindx>2)))
    and flg then rerror(avector,1,list(u,"not a valid vector index"));
  return vindx
  end;

% This routine gives the position of an object in a list. The first
% object is numbered zero. Returns NIL if the object can't be found.

symbolic procedure locate(u,v);
  if not (u memq v) then nil
  else if u=car v then 0
  else 1+locate(u,cdr v);

% We may as well define some utility operators here too.
symbolic smacro procedure first u; car u;
symbolic smacro procedure second u; cadr u;
symbolic smacro procedure third u; caddr u;

% Here we redefine getrtype1 and getrtype2 to handle vectors.

remflag('(getrtype1 getrtype2),'lose);  % We must use these definitions.

symbolic procedure getrtype1 u;
   if threevectorp u then '!3vector else nil;

symbolic procedure getrtype2 u;
   begin scalar x;
     % Next line is maybe only needed by EXCALC.
      return if vecp u then '!3vector
              else if (x:= get(car u,'rtype)) and (x:= get(x,'rtypefn))
               then apply1(x,cdr u)
              else if x := get(car u,'rtypefn) then apply1(x,cdr u)
              else nil
   end;

% The following function declares a list of objects as vectors.

symbolic procedure vec u;
  begin scalar y;
  for each x in u do
  << % Check that the identifier is not already declared as an array
     % or matrix or function
    if not atom x then write("Cannot declare ",x," as a vector")
    else
    << y := gettype x;
       if y memq '(array procedure matrix operator) then
         write("Object ",x," has already been declared as ",y)
       else makenewvector x
    >>;
  >>;
  return nil
  end;

deflist('((vec rlis)),'stat);

% This procedure actually creates a vector.

symbolic procedure makenewvector u;
  begin
%   write("Declaring ",U," a vector");terpri();
    put(u,'rtype,'!3vector);
    put(u,'avalue,list('vector,mkvect(2)));
    return nil
  end;


% Vector function declarations : these are the routines that link
% our new data type into the REDUCE system.

  put('!3vector,'letfn,'veclet);           % Assignment routine
  put('!3vector,'name,'!3vector);          % Our name for the data type
  put('!3vector,'evfn,'!*vecsm!*);         % Evaluation function
  put('!3vector,'prifn,'vecpri!*);         % Printing function
  flag('(!3vector),'sprifn);

  symbolic procedure vecpri!*(u,v,w);
    vecpri(u,v);

% The following routine prints out a vector in a neat way (cf.
% the way in which matrices are printed !)

  symbolic procedure vecpri(u,x);
  begin scalar y,v0,v1,v2,xx;
  y:= if vectorp u then u else getavalue u;
    xx := x;
    if null y then return nil;
%   if null x then xx := 'vec;
    xx := 'vec;
    v0 := getv(y,0);
    v1 := getv(y,1);
    v2 := getv(y,2);
    v0 := aeval v0; v1 := aeval v1; v2 := aeval v2;
    assgnpri(v0,list list(xx,first !*coords),'only);
    assgnpri(v1,list list(xx,second !*coords),'only);
    assgnpri(v2,list list(xx,third !*coords),'only);
    terpri!* t;
  end;

symbolic procedure getavalue u;
   (if x then cadr x else nil) where x=get(u,'avalue);

symbolic procedure indexedvectorp u;
  (vecp car u) and (isvectorindex cadr u);

put('!3vector,'setelemfn,'setvectorelement);

% The following function sets one element of a vector object

symbolic procedure setvectorelement(u,v);
  begin scalar vindx;
  vindx := getvectorindex(cadr u,t);
  putv(getavalue car u,vindx,v);
  return nil
  end;

% If SETK is invoked with an vector atom as its first argument, then
% control will be passed to the routine VECLET

symbolic procedure veclet(u,v,utype,b,vtype);
  begin
  if zerop v then return setvectortozero(u,utype);
  if not equal(vtype,'!3vector)
    then rerror(avector,2,"RHS is not a vector");
  if equal(utype,'!3vector) then put(u,'avalue,list('!3vector,v))
  else if utype memq '(array matrix) then
    rerror(avector,3,list(u,"already defined as ",utype))
  else
  << % We force U to be a vector
     vec u;
     write("*** ",u," re-defined as vector"); terpri();
     put(u,'avalue,list('vector,v))
  >>;
  return v
  end;

% A quick and dirty way of declaring a null vector

symbolic procedure setvectortozero(u,utype);
  begin scalar x;
  x := mkvect 2;
  for k:=0:2 do putv(x,k,aeval 0);
  return veclet(u,x,utype,t,'!3vector)
  end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                      %
%                   VECTOR EVALUATION MODULE                           %
%                                                                      %
% This section contains the routines required to evaluate vector       %
% expressions. The main routine, VECSM!*, is mainly table-driven.      %
% If you wish to include your own routines then you should be          %
% aware of the mechanism used to invoke vector evaluation.             %
%                                                                      %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
symbolic procedure !*vecsm!*(u,v); vecsm!* u;


% The following routine is the main vector evaluation procedure. It
% takes a single argument which is either a vector atom or a
% vector expression in SLISP function form. The value returned may
% be a vector or scalar.

% Note that if the argument is not a vector expression then an
% error results. This is true in particular if the argument is
% an expression in standard-quotient form i.e. a list whose CAR
% is *SQ.

!*vectortracelevel!* := 0;

symbolic procedure prtblanks n;
  for k:=1:min(n,15) do write "  ";

symbolic procedure vecsimp!* u;
  if vecp u then vecsm!* u else u;

symbolic procedure vecsm!* u;
  begin scalar y,vecopr,vargs,v;
  !*vectortracelevel!* := !*vectortracelevel!* + 1;
  if !*vtrace then <<prtblanks(!*vectortracelevel!*);
                     write("VECSM called with args ",u);
                     terpri()>>;
  if atom u then v := (if vectorp u then u else getavalue u)
  else if threevectorp u then v := u
  else if (atom(y:= car u) and get(y,'rtype)='!3vector) then
    v := getv(getavalue y,getvectorindex(cadr u,t))

% Those were the simple cases. Now for the tricky operators

% Separate the operator from its operands

  else <<
    vecopr := car u;
    vargs := for each j in cdr u collect vecsimp!* j;

% Select a course of action dependent upon the operator

    if y := get(vecopr,'vectorfunction) then
    % We must check (if the op is an arithmetic operator)
    % to ensure that there are vectors in the argument list
    << if (flagp(vecopr,'vectorfn) or (flagp(vecopr,'varithop) and
       hasonevector vargs)) then v := apply(y,list vargs)
                            else v := aeval append(list(vecopr),vargs)
    >>
    else if flagp(vecopr,'vectormapping) then
    << % Check that the argument is really a vector
      y := car vargs;
      v := if threevectorp y then vectorapply(vecopr,y,cdr vargs)
                             else scalarapply(vecopr,y,cdr vargs)
    >>
    else <<!*vectortracelevel!* := 0;
           rerror(avector,4,
                  list(vecopr,"is not a valid vector operator"))>>;
  >>;
  if !*vtrace then <<
    y := threevectorp v;
    prtblanks(!*vectortracelevel!*);
    write(if y then "** Vector" else "** Scalar"," result is ",v);
    terpri()>>;
  !*vectortracelevel!* := !*vectortracelevel!* - 1;
  return v
 end;

% Now we define a function to declare a list of scalar functions as
% valid in vector mode too. This means that we can pass them vector
% arguments and get a vector result.

symbolic procedure vectormapping u;
  flag(u,'vectormapping);

deflist('((vectormapping rlis)),'stat);% Deflist used for bootstrapping.

% We will allow the basic transcendental functions to be vector-valued.
% Then we can, for example, evaluate Sin of a vector.

vectormapping 'sin,'cos,'log,'exp,'tan,'asin,'atan,'sinh,'cosh,'tanh;
vectormapping 'quotient,'minus,'df,'int,'sqrt;

% We must put appropriate flags upon the arithmetic operators and
% vector operators too ...
flag('(sub minus difference quotient plus times expt),'varithop);
flag('(avec cross dot vmod grad div curl delsq),'vectorfn);

% We must now define the procedures to carry out vector algebra and
% calculus. They must be given a VECTORFUNCTION property

symbolic smacro procedure vectorfn(oper,vfn);
  put(oper,'vectorfunction,vfn);

% Scalar-vector multiplication

vectorfn('times,'vectormultiply);
symbolic procedure vectormultiply vargs;
  % This routine multiplies together a list made up of scalars,
  % 3x3 matrices and a vector. Note that the combinations
  % vector*vector and vector*matrix are illegal.
  begin scalar  lht,rht,lhtype,rhtype;
  lht := aeval car vargs;         % Begin with first multiplicand
  for each v in cdr vargs do
  << % Deal with each multiplicand in turn
    rht := if vecp v then vecsm!* v
                     else v;
    lhtype := !*typeof  lht;
    rhtype := !*typeof  rht;
    lht :=
      if not (lhtype='!3vector or rhtype='!3vector) then
        aeval list('times,lht,rht)

      else if lhtype='!3vector then
        if null rhtype then vectorapply('times,lht,list rht)
           else rerror(avector,5,"Illegal operation vec*vec or vec*mat")

      else if null lhtype then vectorapply('times,rht,list lht)

      else matrixtimesvector(lht,rht)
  >>;
  return lht
  end;

% Multiplication of a vector by a 3x3 matrix from the left

symbolic procedure matrixtimesvector(mymat,myvec);
  begin scalar rows,myrow,x;
  if atom mymat and idp mymat and null getavalue mymat then
    rerror(avector,6,"Unset matrix in vector multiplication");
  rows := if idp mymat then cdr getavalue mymat else cdr mymat;
  if not (length(rows)=3 and length(car rows)=3) then
    rerror(avector,7,"Matrix must be 3x3 for vector multplication");
  x := mkvect(2);
  for k:=0:2 do
  << % Multiply out a row at a time
    myrow := car rows;
    putv(x,k,aeval list('plus,
                        list('times, first  myrow, getv(myvec,0)),
                        list('times, second myrow, getv(myvec,1)),
                        list('times, third  myrow, getv(myvec,2))));
    rows := cdr rows
  >>;
  return x
  end;

symbolic procedure !*typeof u;
  getrtype u;
% if vecp u then '!3vector
% else if matp u then 'matrix
% else if arrayp u then 'array
% else nil;

% Vector addition

vectorfn('plus,'vectorplus);
symbolic procedure vectorplus vargs;
  % Add an arbitrarily-long list of vectors
  begin scalar x;
  x := vecsm!* car vargs;
  for each v in cdr vargs do x:=vectoradd(x,vecsm!* v);
  return x
  end;

symbolic procedure vectoradd(u,v);
  % Add two vectors or two scalars
  begin scalar x,uisvec,visvec;
  uisvec := vecp u; visvec := vecp v;
  if uisvec and visvec then
  << % Adding two vectors
    x :=mkvect(2);
    for k:=0:2 do putv(x,k,aeval list('plus, getv(u,k), getv(v,k)));
    return x
  >>
  else if not (uisvec or visvec) then
  << % Adding two scalars
    return aeval list('plus, u, v)
  >>
  else rerror(avector,8,"Type mismatch in VECTORADD");
 end;

% Difference of two vectors

vectorfn('difference,'vectordiff);
symbolic procedure vectordiff vargs;
  % Vector - Vector
  begin scalar x,y;
  x := vecsm!* car vargs;
  y := vecsm!* list('minus,cadr vargs);   % Negate the second operand
  return vectoradd(x,y)
  end;

% General case of a quotient involving vectors

vectorfn('quotient,'vectorquot);
symbolic procedure vectorquot vargs;
  % This code deals with the cases
  %
  % (1) Vector / scalar
  % (2) Vector / (scalar-valued vector expression)
  % (3) Scalar / (scalar-valued vector expression)
  %
  begin scalar vdivisor,vdividend;
  vdivisor := aeval cadr vargs;
  if vecp vdivisor
    then rerror(avector,9,"Attempt to divide by a vector");
  vdividend := aeval car vargs;
  if threevectorp vdividend then return vectorapply('quotient,
                                          vdividend, list vdivisor)
  else return aeval list('quotient, vdividend, vdivisor);
 end;

% Vector cross product

vectorfn('cross,'vectorcrossprod);
symbolic procedure vectorcrossprod vargs;
  begin scalar x,y,u0,u1,u2,v0,v1,v2,w0,w1,w2;
  x := vecsm!* car vargs;
  y := vecsm!* cadr vargs;
  u0 := getv(x,0); u1 := getv(x,1); u2 := getv(x,2);
  v0 := getv(y,0); v1 := getv(y,1); v2 := getv(y,2);
  % Calculate each component of the cross product
  w0 := aeval list('difference,
                    list('times,u1,v2),
                    list('times,u2,v1));
  w1 := aeval list('difference,
                    list('times,u2,v0),
                    list('times,u0,v2));
  w2 := aeval list('difference,
                    list('times,u0,v1),
                    list('times,u1,v0));
  x := mkvect(2);
  putv(x,0,w0); putv(x,1,w1); putv(x,2,w2);
  return x
  end;

% Vector modulus

vectorfn('vmod,'vectormod);

% There are two definitions due to the existence of a bug in the REDUCE
% code for SQRT : in the IBM version of REDUCE 3.3 an attempt to take
% SQRT of 0 results in an error, so I have coded round it.

% The first version which follows is the succinct version which will
% work if SQRT(0) doesn't give an error.

% symbolic procedure vectormod u;
%   aeval list('sqrt,list('dot,car u,car u));

% This version is a little longer but it works even if SQRT(0) doesn't.

symbolic procedure vectormod u;
  begin scalar v;
  v := aeval list('dot, car u, car u);
  if zerop v then return 0
             else return aeval list('sqrt,v);
  end;

% Vector dot product

vectorfn('dot,'vectordot);
symbolic procedure vectordot vargs;
  begin scalar x,y,u0,u1,u2,v0,v1,v2;
  x := car vargs;
  y := cadr vargs;
  u0 := getv(x,0); u1 := getv(x,1); u2 := getv(x,2);
  v0 := getv(y,0); v1 := getv(y,1); v2 := getv(y,2);
  % Calculate the scalar product
  return aeval list('plus,
                    list('times,u0,v0),
                    list('times,u1,v1),
                    list('times,u2,v2))
  end;

% Component-wise assignment of a vector (AVEC)

vectorfn('avec,'vectoravec);

deflist('((oper vfn)),'vectorfunction);   % For bootstrapping.

symbolic procedure vectoravec vargs;
  begin scalar x;
  % Build a vector from the argument list
  if not eqn(length(vargs),3) then
            rerror(avector,10,"Incorrect number of args in AVEC");
  x := mkvect(2);
  putv(x,0,aeval first vargs);
  putv(x,1,aeval second vargs);
  putv(x,2,aeval third vargs);
  return x
  end;

% Gradient of a scalar

vectorfn('grad,'vectorgrad);
symbolic procedure vectorgrad vargs;
  begin scalar x,y;
  x := mkvect(2);
  y := aeval car vargs;
  putv(x,0,aeval list('quotient,
                       list('df,y,first !*coords),
                       !*hfac 0));
  putv(x,1,aeval list('quotient,
                       list('df,y,second !*coords),
                       !*hfac 1));
  putv(x,2,aeval list('quotient,
                       list('df,y,third !*coords),
                       !*hfac 2));
  return x
  end;

% Divergence of a vector

vectorfn('div,'vectordiv);
symbolic procedure vectordiv vargs;
  begin scalar x,u0,u1,u2;
  x := vecsm!* car vargs;
  u0 := getv(x,0); u1 := getv(x,1); u2 := getv(x,2);
  u0 := aeval list('times,u0,!*hfac 1,!*hfac 2);
  u1 := aeval list('times,u1,!*hfac 0,!*hfac 2);
  u2 := aeval list('times,u2,!*hfac 0,!*hfac 1);
  x :=  aeval list('plus,
                    list('df,u0,first !*coords),
                    list('df,u1,second !*coords),
                    list('df,u2,third !*coords));
  x := aeval list('quotient,x,list('times,
                          !*hfac 0,!*hfac 1,!*hfac 2));
  return x
  end;

% Curl of a vector

vectorfn('curl,'vectorcurl);
symbolic procedure vectorcurl vargs;
  begin scalar x,u0,u1,u2,v0,v1,v2,w0,w1,w2;
  x := vecsm!* car vargs;
  u0 := aeval list('times,getv(x,0),!*hfac 0);
  u1 := aeval list('times,getv(x,1),!*hfac 1);
  u2 := aeval list('times,getv(x,2),!*hfac 2);
  v0 := first !*coords; v1 := second !*coords; v2 := third !*coords;
  x := mkvect(2);
  w0 := aeval list('times,
                    list('difference,
                          list('df,u2,v1),
                          list('df,u1,v2)),
                    !*hfac 0);
  w1 := aeval list ('times,
                     list('difference,
                          list('df,u0,v2),
                          list('df,u2,v0)),
                    !*hfac 1);
  w2 := aeval list('times,
                    list('difference,
                          list('df,u1,v0),
                          list('df,u0,v1)),
                    !*hfac 2);
  putv(x,0,w0); putv(x,1,w1); putv(x,2,w2);
  x := aeval list('quotient,
                   x,
                   list('times, !*hfac 0, !*hfac 1, !*hfac 2));
    return x
  end;

% Del-squared (Laplacian) of a scalar or vector

vectorfn('delsq,'vectordelsq);
symbolic procedure vectordelsq vargs;
  begin scalar x,y,v0,v1,v2,w0,w1,w2;
  x := vecsm!* car vargs;
  if vecp x then
    % Cunning definition of Laplacian of a vector in terms of
    % grad, div and curl
    return aeval list('difference,
                       list('grad, list('div,x)),
                       list('curl, list('curl,x)))
  else
  << % Laplacian of a scalar ... which simply requires lots of
     % calculus
     if null x then x := car vargs;
     y := aeval list('times,!*hfac 0, !*hfac 1, !*hfac 2);
     v0 := first !*coords;
     v1 := second !*coords;
     v2 := third !*coords;
     w0 := aeval list('df,
                      list('quotient,
                           list('times,
                                !*hfac 1,
                                !*hfac 2,
                                list('df,x,v0)),
                           !*hfac 0),
                      v0);
     w1 := aeval list('df,
                      list('quotient,
                           list('times,
                                !*hfac 2,
                                !*hfac 0,
                                list('df,x,v1)),
                           !*hfac 1),
                      v1);
     w2 := aeval list('df,
                      list('quotient,
                           list('times,
                                !*hfac 0,
                                !*hfac 1,
                                list('df,x,v2)),
                           !*hfac 2),
                      v2);
     return aeval list('quotient,
                       list('plus,w0,w1,w2),
                       y)
  >>;
  end;

% Vector substitution - definition of SUB as a VECTORFN
% function.
vectorfn('sub,'vectorsub);

% Now we have to define mapping for SUB. It's made a little complicated
% by the fact that the argument list for SUB has the interesting bit
% i.e. the vector, at the end.

symbolic procedure vectorsub vargs;
  begin scalar subslist,vexpr,x;
  vexpr := car reverse vargs;    % That was the easy part !
  % Now we need to get the rest of the list
  subslist := reverse cdr reverse vargs; % Dirty, but effective !
  x := mkvect(2);
  for k:=0:2 do
    putv(x,k,aeval append('(sub),
                          append(subslist,list getv(vexpr,k))));
  return x
  end;

% Component-wise application of a scalar operation to a vector

symbolic procedure vectorapply(vecopr,v,args);
  begin scalar vv,x,y;
  x := mkvect(2);
  vv := if not vectorp v then vecsm!* v
                         else v;
  for k:=0:2 do
  << % Apply the operation to each component
    y := getv(vv,k);
    y := if null args then aeval list(vecopr,y)
                      else aeval append(list(vecopr,y),args);
    putv(x,k,y);
  >>;
  return x
  end;

% We need to define a dummy routine to apply a function to a scalar

symbolic procedure scalarapply(op,v,args);
  if null args then aeval list(op,v)
               else aeval append(list(op,v),args);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                                                                      %
%                     COORDINATE SYSTEM MODULE                         %
%                                                                      %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% We begin with a function which declares the names of the coordinates
% to be used

symbolic procedure coordinates u;
  begin scalar x;
  if not eqn(length(u),3)
    then rerror(avector,11,"Wrong number of args");
  for each y in u do if (x := gettype y) and not(x eq 'operator) then
    rerror(avector,12,"Name declared as coordinate is not a kernel");
  remflag(!*coords,'reserved);
  !*coords := u;
  x := aeval list('avec,first u,second u,third u);
  remflag('(coords),'reserved);
  setk('coords,x);
  flag('(coords),'reserved);
  %flag(u,'reserved);
  return u
  end;


symbolic operator coordinates;

remflag('(dvolume hfactors),'reserved);

algebraic procedure scalefactors(h1,h2,h3);
begin
  remflag('(dvolume hfactors),'reserved);
  hfactors := avec(h1,h2,h3);
  dvolume := h1*h2*h3;
  flag('(dvolume hfactors),'reserved);
 end;

flag('(dvolume hfactors),'reserved);

% We define a procedure that extracts the n-th scale factor

symbolic procedure !*hfac n;
  if not fixp n or n<0 or n>2 then rerror(avector,13,"Invalid index")
  else getv(getavalue 'hfactors,n);

% Now we define two useful operators that allow us to define and
% refer to a coordinate system by name

symbolic procedure getcsystem u;
  begin scalar x,y;
  if not atom u
    then rerror(avector,14,"Invalid name for coordinate system");
  if not flagp(u,'coordinatesystem)
    then rerror(avector,15,"Unknown system");
  x := get(u,'coordinates);
  y := get(u,'scalefactors);
  if x and y then
  << % Put the coordinates and scalefactors in place
    remflag(!*coords,'reserved);
    !*coords := x;
    remflag('(coords),'reserved);
    setk('coords,aeval list('avec,first x, second x, third x));
    flag('(coords),'reserved);
    %flag(x,'reserved);
    put('hfactors,'avalue,list('!3vector,y));
    remflag('(dvolume),'reserved);
    setk('dvolume,aeval list('times, !*hfac 0, !*hfac 1, !*hfac 2));
    flag('(dvolume),'reserved);
    return x
  >>
  else rerror(avector,16,"Incompletely specified coordinate system")
  end;

symbolic procedure putcsystem u;
  begin
  if not atom u
    then rerror(avector,17,"Invalid name for coordinate system");
  flag(list u,'coordinatesystem);
  put(u,'coordinates,!*coords);
  put(u,'scalefactors,getavalue 'hfactors);
  !*csystems := union(list u,!*csystems);
  return u
  end;


deflist('((coordinates rlis)),'stat);
!*coords := '(x y z);
!*csystems := nil;

% The following procedure calculates the derivative of a vector
% function of a scalar variable, including the scale factors in
% the coefficients.

% symbolic operator vecdf;
% Commented out by M.MacCallum or surfint fails

flag('(vecdf), 'vectorfn);
   % To replace previous line - M. MacCallum

vectorfn('vecdf,'vectordf);

symbolic procedure vectordf u;
  begin scalar v,idv,x;
  v := vecsm!* car u;
  idv := cadr u;
  if not vecp v then rerror(avector,18,"First arg is not a vector");
  if not atom idv then rerror(avector,19,"Second arg is not an atom");
  x := mkvect(2);
  for k:=0:2 do
  << % Calculate components one at a time
    putv(x,k,aeval list('times,
                        !*hfac k,
                        list('df,
                             getv(v,k),
                             idv)))
  >>;
  return x
  end;

% We define three popular curvilinear coordinate systems :
% Cartesian, spherical polar and cylindrical

algebraic;
vec coords,hfactors;
% flag('(coords hfactors),'reserved); % Interferes with EXCALC.
infix dot,cross;
precedence dot,*;
precedence cross,*;

coordinates x,y,z;
scalefactors(1,1,1);
putcsystem 'cartesian;

coordinates r,theta,phi;
scalefactors(1,r,r*sin(theta));
putcsystem 'spherical;

coordinates r,z,phi;
scalefactors(1,1,r);
putcsystem 'cylindrical;

% And we choose to use Cartesians initially ...

getcsystem 'cartesian;

% Extensions to REDUCE vector package

% Definite-integral routine ... trivially simple

algebraic procedure defint(fn,x,xlower,xupper);
  begin scalar indefint;
  indefint:=int(fn,x);
  return sub(x=xupper,indefint)-sub(x=xlower,indefint)
  end;

vectormapping 'defint;  % DEFINT is now a vector function too

% Component-extraction utility - allows us to get components
% of vectors which are arguments to algebraic procedures

symbolic procedure component(v,n);
  if not vecp v then rerror(avector,20,"Argument is not a vector")
  else getv(vecsm!* v,n);

symbolic operator component,vecp;

algebraic procedure volintegral(fn,vlower,vupper);
  begin scalar integrand,idpvar,xlower,xupper,kindex;
  integrand := fn*hfactors(0)*hfactors(1)*hfactors(2);
  for k:=0:2 do
  << % Perform each integration separately. The order of integration
     % is determined by the control vector VOLINTORDER
     kindex := volintorder(k);
     idpvar := coords(kindex);
     xlower := component(vlower,kindex);
     xupper := component(vupper,kindex);
     integrand := defint(integrand,idpvar,xlower,xupper);
  >>;
  return integrand
  end;

% Define the initial setting of VOLINTORDER

volintorder := avec(0,1,2);

% Line integral

algebraic procedure lineint(v,curve,ivar);
  begin scalar scalfn,vcomp,hcomp,dcurve;
  scalfn := 0;
  for k:=0:2 do
  << % Form the integrand
    vcomp := component(v,k);
    hcomp := hfactors(k);
    dcurve := df(component(curve,k),ivar);
    scalfn := scalfn + vcomp*hcomp*dcurve
  >>;
  scalfn := vecsub(coords,curve,scalfn) ;     % Added by M. MacCallum
  return int(scalfn,ivar)
  end;

algebraic procedure deflineint(v,curve,ivar,ilb,iub);
  begin scalar indfint;
  indfint := lineint(v,curve,ivar);
  return sub(ivar=iub,indfint)-sub(ivar=ilb,indfint)
  end;

% Attempt to implement dot and cross as single-character infix
%  operators upon vectors

symbolic;

% Cross-product is easy : we simply tell Reduce that up-arrow is a
%  synonym for CROSS

newtok '((!^) cross);

% Dot is more difficult : the period (.) is already defined as the
%  CONS function, and unfortunately REVAL1 spots this before it
%  checks the type of the arguments, so declaring CONS to be
%  VECTORMAPPING won't work. What is required is a hack to the
%  routine that carries out CONS at SYMBOLIC level.

% We now redefine RCONS which is the function invoked when CONS is used
% in Reduce.

remflag('(rcons),'lose);   % We must use this definition.

symbolic procedure rcons u;
   begin scalar x,y;
      argnochk ('cons . u);
      if (y := getrtype(x := reval cadr u)) eq 'vector
        then return mk!*sq simpdot u
% The following line was added to enable . to be used as vector product
% (Amended by M. MacCallum)
       else if (y eq '!3vector)
         then return apply('vectordot,
                           {for each j in u collect vecsimp!* j})
       else if not(y eq 'list) then typerr(x,"list")
       else return 'list . reval car u . cdr x
   end;

vectorfn('cons,'vectordot);

% Rest added by M. MacCallum
flag('(surfint vecsub),'vectorfn);
vectorfn('surfint,'vsurfint);
symbolic procedure vsurfint vargs;
 begin scalar sivar1, sivar2, sivar3, sivar4, sivar5 ;
  if not (length vargs = 8) then rerror(avector,21,
             "Wrong number of args to SURFINT");
  if not (vecp(sivar1 := car vargs) and
          vecp(sivar2 := cadr vargs) and
          idp car(sivar3 := cddr vargs) and
          idp car(sivar4 := cdddr sivar3))
     then rerror(avector,22,
                 "Wrong type(s) of arguments supplied to SURFINT");
  sivar2 := vecsm!* sivar2 ;
  sivar3 := reverse cdddr reverse sivar3 ;
  sivar5 := aeval list('cross,
                       list('vecdf, sivar2, car sivar3),
                       list('vecdf, sivar2, car sivar4)) ;
  sivar1 := vecsm!* sivar1 ;
  sivar5 := aeval list('dot, sivar1, sivar5) ;
  sivar5 := aeval list('vecsub,'coords,sivar2,sivar5);
  return aeval append(list('defint,
                      append(list('defint, sivar5), sivar3)),
                      sivar4) ;
 end ;

vectorfn('vecsub,'vvecsub);
symbolic procedure vvecsub vargs ;
 begin scalar vsarg1, vsarg2, vsarg3;
  if not (length vargs = 3) then rerror(avector,23,
                           "Wrong number of arguments to VECSUB");
  if not (vecp car vargs and vecp cadr vargs) then
        rerror(avector,24,
               "First two arguments to VECSUBS must be vectors");
  vsarg1 := vecsm!* car vargs;
  vsarg2 := vecsm!* cadr vargs;
  vsarg3 := caddr vargs;
  if not vecp vsarg3 then vsarg3 := prepsq cadr vsarg3;
  return  aeval list('sub,
             list('equal, !*a2k component(vsarg1, 0),
                          component(vsarg2, 0)),
             list('equal, !*a2k component(vsarg1, 1),
                          component(vsarg2, 1)),
             list('equal, !*a2k component(vsarg1, 2),
                          component(vsarg2, 2)),
             vsarg3);
 end ;

endmodule;


end;
