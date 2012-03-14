
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

omfuncs!*:=
'((oma . (omaIR))
(oms . (omsIR))
(omi . (omiIR))
(omv . (omvIR))
(omf . (omfIR))
(omstr . (omstrIR))
(ombind . (ombindIR))
(omattr . (omattrIR)));

symbolic procedure om2ir();
begin scalar res;
 % Initialisation of important variables used by the lexer.

 res:=nil;
 FLUID '(safe_atts char ch atts count temp space temp2);
 space:=int2id(32);
 count:=0;
 ch:=readch();
 temp2:=nil;

 % Begining of lexing and parsing

 lex();
 if char='(o m o b j) then <<
     lex();
     res:=omobj();
 >>
   else errorML("<omobj>",2);

 lex();

 if char='(!/ o m o b j) then
   terpri()
   else errorML("</omobj>",19);

 return res;
end;

symbolic procedure omobj();
begin scalar aa, res;
  % We check what the OpenMath tag is and call the appropriate function. The relationship
  % between OpenMath tag and function to be called is in table omfuncs!*.
  if (aa:=assoc(compress!* char, omfuncs!*)) then return apply(cadr aa, nil);
end;

% The following function recursively reads in objects as defined in
% the OpenMath grammar in the OpenMath standard.

symbolic procedure omobjs();
begin scalar obj, objs;
  if char neq '(!/ o m a) then <<
    obj:=omobj();
    lex();
    objs:=omobjs();
    if obj eq nil then
     return append(obj, objs)
    else
     return cons(obj, objs);
  >>;
end;

% Checks if the current token is equivalent to the given tag.

symbolic procedure checkTag(tag);
begin;
  if char neq tag then errorML("Problem", "problem");
end;

% This function returns the symbol read within an <OMS> tag.
% It will also check in the mmleq!* table what the equivalent
% MathML symbol is. If there isnt any it encodes the symbol
% for use in a <semantic> tag.

symbolic procedure omsIR();
begin scalar cd, name, sem, aa, bb, cd, attr, symb, validcd;
  attr:=nil;

  % We read in from the input the name and CD contained
  % in the OMS tag.

  name:= intern find(atts, 'name);
  cd:= intern find(atts, 'cd);

  % We check if the symbol has a MathML equivalent
  % in the mmleq!* table.
  % But when dealing with a vector, REDUCE works differently
  % hence we have to deal with vectors independently.

  if explode name = '(v e c t o r) then aa:='(vectorml linalg1)
  else aa:=member (intern name, mmleq!*);


  % If nothing was found, we check to see if we are
  % dealing with a special case.
  % If so, we retrieve from the table special_cases!*
  % the equivalent MathML symbol and the correct
  % attribute to add.

  if aa=nil then <<
    if (aa:=assoc(name, special_cases!*)) then <<
      attr:=car reverse aa;
      if attr neq nil then attr:=list attr;
      aa:=cadr reverse aa . reverse cddr reverse cdr aa
    >>
    else
      % Here we call special case functions
      % because the tranlation needs some care.
      if (bb:=assoc(name, special_cases2!*)) neq nil then <<
        return apply(cadr bb, cddr bb)
      >>;
  >>;




  % We now check if aa is still nothing, or if the CD
  % given in the input does not match one of the CDs
  % contained in aa which map to MathML. If so, we
  % envelope the input into a semantic tag.


  if aa neq nil then validcd:= assoc(car aa, valid_om!*);
  if validcd neq nil then validcd:=cadr validcd;
  %debug("validcd: ",validcd);

  if aa eq nil OR cd=validcd eq nil then <<
    sem:=encodeIR(name);
    return sem;
  >>;

  % If we are dealing with a vector, we change it to IR rep which
  % is vectorml

  return list(car aa, attr);
end;

% The following function encodes an unknown symbol into a
% valid representation for use within <semantic> tags.

symbolic procedure encodeIR(name);
begin scalar sem;
  sem:=append(char, cons('!  , atts));
  sem:=delall('!$, sem);
  return cons('semantic, list cons(name, list sem));
end;

lisp operator om2mml;

symbolic procedure omiIR();
begin scalar int;
  lex();
  int := compress char;
  lex();
  return int;
end;

symbolic procedure omvIR();
begin scalar name;
  name:=find(atts, 'name);
  if find(atts, 'hex) neq nil then errorML("wrong att", 2);
  if find(atts, 'dec) neq nil then errorML("wrong att", 2);
  return name;
end;

symbolic procedure variablesIR();
begin scalar var, vars;
  if char neq '(!/ o m b v a r) then <<
    var:=omvIR();
  lex();
    vars:=variablesIR();
    if var eq nil then
     return append(var, vars)
    else
     return cons(var, vars);
  >>;
end;

symbolic procedure omfIR();
begin scalar float;
  float:=find(atts, 'dec);
  if find(atts, 'name) neq nil then errorML("wrong att", 2);
  return float;
end;

symbolic procedure omstrIR();
begin scalar str;
  lex();
  str := compress char;
  lex();
  return cons('string, list str);
end;

symbolic procedure omaIR();
begin scalar obj, elems;
  lex();
  obj:=omobj();

  % If we are dealing with a matrix the following code
  % is not executed because the MatrixIR function
  % does the input reading and checks when it has
  % reached the closing </OMA> tag.

  if car obj neq 'matrix then <<
    lex();
    elems:=omobjs();
    checkTag('(!/ o m a));
  >>;

  return append(obj, elems);
end;

symbolic procedure ombindIR();
begin scalar symb, vars, obj;
  lex();
  symb:=omobj();
  lex();
  vars:=toBvarIR variablesIR();
  lex();
  obj:=omobj();
  lex();
  checkTag('(!/ o m b i n d));
  return append(symb , append(vars, list obj));
end;

symbolic procedure omattrIR();
begin scalar omatp, var;
  lex();
  omatp:=omatpIR();
  lex();
  var:=omobj();
  lex();
  checkTag('(!/ o m a t t r));
  if PAIRP omatp then if cadar omatp = 'csymbol then return (var . list nil);
  if NUMBERP var then return list('cn, omatp, var);
  return list('ci, omatp, var);
end;

symbolic procedure omatpIR();
begin scalar symb ,obj;
  lex();
  symb:=car omsIR();

  lex();
  obj:=car omobj();

  lex();
  checkTag('(!/ o m a t p));

  return list (symb . list obj);
end;

% The following function transforms a list of variables
% into a list of bvar constructs. ie: (x y)->((bvar x 1)(bvar y 1))

symbolic procedure toBvarIR(bv);
begin;
  if bv neq nil then return cons(cons('bvar, list(car bv, 1)), toBvarIR(cdr bv));
end;


% From here onwards, functions necessary to deal with
% OpenMath special operators are defined. This is where
% matrix, int, sum, prod, diff etc... are treated.

symbolic procedure matrixIR();
begin scalar res;
  lex();
  res:=omobjs();
  if caadr cadr res = 'matrixcolumn then res := 'matrixcolumn . list matrixelems(res)
  else res := 'matrixrow . list matrixelems(res);
  return 'matrix . nil . res;
end;

symbolic procedure matrixelems(elem);
  if elem neq nil then cons(cddr car elem, matrixelems cdr elem);

symbolic procedure sum_prodIR();
begin scalar var, fun, int, name;
  name:=intern find(atts, 'name);
  lex();
  int:=omobj();
  int:='lowupperlimit . (cdr int);
  lex();
  fun:=omobj();
  var:=lambdaVar fun;
  fun:=lambdaFun fun;
  return append(list(name , nil) , append(var  , int . list fun));
  return name . nil . var . int . list fun;
end;

symbolic procedure integralIR();
begin scalar int, fun, var, tag;
  tag:=intern find(atts, 'name);

  var:=list '(bvar x 1);
  int:=nil;

  % if dealing with defint, determine the interval
  % and store inside variable int

  if tag = 'defint then <<
    lex();
    int:=omobj();
  >>;

  lex();
  fun:=omobj();

  if PAIRP fun then if car fun = 'lambda then <<
      var:=lambdaVar fun;
      fun:=lambdaFun fun;
  >>;
  return append(list(tag , nil) , append(var  , list fun));
end;

symbolic procedure partialdiffIR();
begin scalar lis, fun, var, tag, vars;
  tag:=intern find(atts, 'name);

  lex();
  lis:=omobj();

  if car lis='list then lis:=cddr lis
  else errorML("",3);

  lex();
  fun:=omobj();

  if PAIRP fun then
    if car fun = 'lambda then <<
       var:=lambdaVar fun;
       fun:=lambdaFun fun;
       vars:= pdiffvars(lis, var);
    >>;

  return append(list('partialdiff , nil) , append(vars  , list fun));
end;

symbolic procedure pdiffvars(ind, v);
begin;
  return if ind neq nil then nth(v, car ind) . pdiffvars(cdr ind, v);
end;

symbolic procedure selectIR();
begin scalar name, cd, a,b, c, tag;
  name:=intern find(atts, 'name);
  cd:=intern find(atts, 'cd);
  tag:=list 'selector;
  if member(cd, '(linalg3)) eq nil then tag:=encodeIR(name);
  lex();
  a:=omobj();
  if name='matrix_selector then <<
  lex();
  b:=omobj();
  >>;
  lex();
  c:=omobj();
  if name='matrix_selector then <<
    return append(tag,  nil . c . a . list b);
  >>;
  return append(tag,  nil . c . list a);
end;


symbolic procedure limitIR();
begin scalar val, type, cd, fun, var, res, tag;
  cd:=intern find(atts, 'cd);
  tag:=list 'limit;
  if member(cd, '(limit1)) eq nil then tag:=encodeIR('limit);
  lex();
  val:=omobj();
  lex();
  type:=omobj();
  lex();
  fun:=omobj();




  % Extract the necessary information from the OpenMath read in just above.
  type:=caadr type;

  if member(type, '(below above both_sides null)) eq nil then errorML("wrong method of approach", 2);
  if type='null then type:='both_sides;

  var:= lambdaVar fun;
  fun:= lambdaFun fun;

  % Transform that information into intermediate representation.
  res:= append(tag, (nil . var ));
  if type neq 'both_sides then
    res:= append(res , list ('condition . list ('tendsto . list ('type . list type) . cadr car var . list val)))
  else
    res:= append(res , list ('condition . list ('tendsto . nil . cadr car var . list val)));
  res:= append(res, list fun);

  return res;
end;

symbolic procedure numIR();
begin scalar base, a1, a2, tag;
  tag:=intern find(atts, 'name);
  lex();
  a1:=omobj();
  lex();
  a2:=omobj();

  if tag = 'complex_cartesian then <<
    if IDP a1 OR IDP a2 then return 'plus . nil . a1 . list ('times . nil . a2 . list '!&imaginaryi!;)
  >>;

  if tag = 'complex_polar then <<
    if IDP a1 OR IDP a2 then return 'times . nil . a1 . list ('exp . nil . list ('times . nil . a2 . list '!&imaginaryi!;))
  >>;

  if tag = 'rational then <<
    if IDP a1 OR IDP a2 then return 'divide . nil . a1 . list a2;
  >>;

  return tag . nil . a1. list a2;

end;

% The following function deals with OpenMath symbols
% not taking any arguments such as false, true, zero, etc...

symbolic procedure unaryIR(validcd, tag);
begin scalar name, cd;
  name:=intern find(atts, 'name);
  cd:=intern find(atts, 'cd);
  if cd neq validcd then return encodeIR name;
  return tag;
end;

% Returns the first main variable of a lambda expression

symbolic procedure lambdaVar(l);
begin;
  return cdr reverse cddr l;
end;

symbolic procedure lambdaVar2(l);
begin;
  return cadr caddr l;
end;

% Returns the function of a lambda expression

symbolic procedure lambdaFun(l);
begin;
  return car reverse l;
end;


% This function is the one the user types to
% translate OpenMath to MathML.


symbolic procedure om2mml();
begin scalar ir;
  ir:=om2ir();
  terpri!* t;
  princ "Intermediate representation:";
  terpri!* t;
  princ ir;
  terpri!* t;
  ir2mml ir;
end;

end;
