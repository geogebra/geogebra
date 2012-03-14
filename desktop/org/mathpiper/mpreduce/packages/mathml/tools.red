%  Description: This file contains various important functions which are used by all modules
%                of the program. Of importance is the lexer, and the functions dealing with
%                XML attributes for both OpenMath and MathML as well as the error message
%                generator.
%
%  Date: 25 March 2000
%
%  Author: Luis Alvarez Sobreviela
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


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

% Declaration of two switches.
% _mathml_ allows all output to be printed in mathml.
% _both_ allows all output to be printed in mathml and in normal reduce
% output.

load assist;
load matrix;


global '(f dfunctions!* file!*);

%Initialisation of REDUCE switches.

global '(!*mathml);
switch mathml;
global '(!*both);
switch both;
global '(!*web);
switch web;

LISP (FILE!*:=nil);
!*mathml:=nil;
!*both:=nil;
!*web:=nil;

off both;
off mathml;
off web;



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% The following functions are the lexer. When called they return the next       %
% mathml token in the input stream.                                           %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure lex();
begin scalar token,safe_atts;
% princ "Char: ";print char;
 token:=nil;
 char:=nil;
 if atts neq nil then safe_atts:=atts;
 atts:=nil;
 if ch eq int2id(10) then ch:=readch();
 if ch neq !$EOF!$ then <<
   if ch=space then while (ch:=readch())=space do
   else
    if ch='!< then char:=get_token()
    else char:=get_content();

    if char neq nil then
     <<  count:=count+1;
         token:=reverse char;
         if notstring char then <<
            char:=butes(token);          % a token is striped from its attributes.
            isvalid(char);               % Make sure token is not a string
            attributes(char,token)>>     % and they are stored by the function attributes
     >>
    else lex(); >>
end;

% Returns anything until the XML element '>' closing character

symbolic procedure get_token();
begin scalar d;
 d:='();
 while (ch:=readch()) neq '!> do d:=cons(ch,d);
 return cons('!$,d);
end;


% This function reads the elements within XML tags. It will skip and ignore
% unnecessary spaces. However if the element is a string then it will keep
% the spaces.

symbolic procedure get_content();
begin scalar d, d2;
 d:='();
 while (ch:=readch()) neq '!< AND ch neq !$EOF!$  do <<
   if ch neq int2id(10) then
   d:=cons(ch,d)
 >>;
 d2:=delall('!  , d);
 if d2 eq nil then d:=nil
 else
   <<if car d2 neq '!"  AND car reverse d2 neq '!"  then
          d:=d2 else return reverse d>>;
 if d neq nil then d:=cons('!$,d);
 return d;
end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% The following fuctions deal with XML attributes.                                 %
%                                                                                  %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


% The following function will search a list of attributes _att_ for the attribute
% named _key_. It is useful for getting the value of a particular attribute from
% a MathML token which contains various attributes

symbolic procedure search_att( att, key);
begin scalar l, stop,d;
 l:=nil;
 d:=();
 stop:=0;
 att:= find2(att, key);
 if att neq '(stop) then
 <<
 while (car att='! ) do att:=cdr att;
 if (car att = '!=) then
   <<
     att:=cdr att;
     while (car att='! ) do att:=cdr att;
     if (car att='!") then
      << att:=cdr att;
         while (stop=0) do
          << d:=cons(car att, d);
             att:=cdr att;
             if (car att='!  ) OR (car att='!$) then stop:=1
          >>
      >>
      else
         while (stop=0) do
          << d:=cons(car att, d);
             att:=cdr att;
             if (car att='!  ) OR (car att='!$) then stop:=1
          >>
   >>
 else
 errorML(compress key,1);
 if car d='!" then d:=cdr d;
 return reverse d
 >>
end;

% _attributes(a,b)_ reads the attributes of a MathML token and
% stores them in global variable atts

symbolic procedure attributes(a,b);
begin scalar l;
 l:=length a;
 for a:=1:l do b:=cdr b;
 while (car b='! ) do b:=cdr b;
 if b neq '(!$) then  atts:=b;
end;

% butes removes all attributes to a token. Necessary when parsing. The attributes of the
% current character are always stored in atts in case they are necessary.

symbolic procedure butes( str );
begin scalar cha;
cha:=car str;
return if (cha='!  OR cha='!$) then <<'(); >>
        else  cons(car str, butes cdr str);
end;



% This function takes a list of attributes
% and their corresponding values _fatt_ and
% the name of the attribute wanted _fkey_.
% It then returns the value of that attribute.
% eg: find('...., 'type);

symbolic procedure find(fatt, fkey);
begin scalar a;
  fkey := explode fkey;
  a:=find2(fatt, fkey);
%  debug("find a: ",a);
  if car a neq '!= then a:=find2(a, fkey);
%  debug("find a: ",a);
%  debug("",);
  a:=delall('!", a);
  a:=delall('!=, a);
  a:=delall('!$, a);
  if a neq '(stop) then
    if car reverse a = '!/ then
        a:=reverse cdr reverse a;              %will remove the !/ character at the end.
  if a neq '(stop) then
    if fkey = '(d e f i n i t i o n u r l)  then return delall('!  ,a)
    else return compress!* a
  else return nil;
end;

symbolic procedure compress!* u;
   begin scalar x;
      if digit car u then return compress u;
      for each j in u do
         if j eq '!/ or j eq '!- or j eq '!; or j eq '!.
            then x := j . '!! . x
          else x := j . x;
      return intern compress reversip x
   end;

symbolic procedure find2(fatt, fkey);
begin;
return if fkey= '() then if fatt neq nil then cdr fatt else '(stop)
          else
          find2(member(car fkey, fatt), cdr fkey);
end;

% Given a list of attributes _ats_ and a list of attributes
% of interest _list_ it will return a list containing
% the attribute names and their corresponding attribute values.

symbolic procedure retattributes( ats, list );
begin scalar a;
  if list eq nil then nil
         else <<
            a:=find(ats, car list);
            if a neq nil then
                return cons(list(car list, a ), retattributes(ats,cdr list))
            else return retattributes(ats,cdr list);
         >>;
end;



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  The following functions are handy tools. Some of them are very useful      %
%  Others are modifications of REDUCE functions which were not perfectly   %
%  suitable for the tasks required by this program                               %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



% When a token has its attributes stripped off, it looses the !/ character
% at the end. This function restores this character only if the token is valid.
% It is valid if it is part of the functions!* list. If not it doesn't restore
% the !/ character and calls an error

symbolic procedure isvalid(a);
begin;
   if IDP compress a neq t then return compress a;
   if assoc(compress!* a, functions!*) then return t;
   a:=reverse cons('!/,  reverse a);
   if assoc(compress!* a, functions!*) then <<char:=a; return t>>;
   return nil;
end;

% This function checks that a given token or element
% produced by the lexer is not a string.

symbolic procedure notstring(a);
begin scalar a, a2;
 a2:=delall('!  , a);
 if car a2 neq '!"  AND car reverse a2 neq '!"
 then return t else return nil;
end;

% This function will take a list as argument and return a list where
% only one copy is kept of elements appearing more than once.

symbolic procedure norepeat(args);
begin;
return if args=nil then nil else
 if length args=1 then list car args
 else append(list car args, norepeat(delall(car args, cdr args)));
end;

% This function will delete all occurences of element x in list l

symbolic procedure delall(x,l);
if l=nil then nil
else if x=car l then delall(x, cdr l)
     else append(list car l ,delall(x, cdr l));


% This function takes a list of characters and prints them out together.
% It is like compress but works better when it comes to uniting and
% printing the elements of a list.

symbolic procedure list2string(a);
begin;
  if a neq nil then <<princ car a; list2string(cdr a)>>;
end;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% The following function is in charge of providing the correct error message        %
% as well as closing the input/output stream, and exiting the program              %
% correctly.                                                                     %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

symbolic procedure errorML( str, msg );
begin;
 terpri();
 princ "***** Error in token number ";
 princ count;
 princ " (<";
 princ compress char;
 princ ">)";
 terpri();
 if msg=1 then
  << princ "Needed attribute";
     princ str;
     princ " and none was found.">> else
 if msg=2 then
  << princ "Missing tag: ";
     princ str >> else
 if msg=3 then
  << princ "Undefined error!" >> else
 if msg=4 then
  << princ "Numerical constant ";
     princ str;
     princ " was enclosed between <ci></ci> tags.";
     terpri();
     princ "Correct syntax: <cn>";
     princ str;
     princ "</cn>.">> else
 if msg=5 then
  << princ "All arguments must be sets";
     terpri();
     princ str;
     princ " does not represent a set.">> else
 if msg=6 then
  << princ "Non-numeric argument in arithmetic.">> else
 if msg=7 then
  << princ "The degree quantifier is of no use in the sumation";
     princ "operator.">> else
 if msg=8 then
  << princ "The degree quantifier is of no use in the limit";
     princ " operator.">> else
 if msg=9 then
  << princ "The index of sumation has not been specified.";
     terpri();
     princ "Please use <bvar></bvar> tags to specify an index.">>
 else
 if msg=10 then
  << princ "Upperlimit not specified.">> else
 if msg=11 then
  << princ "Upper and lower limits have not been specified.">> else
 if msg=12 then
  << princ "The degree quantifier is of no use in the product";
     princ " operator.">> else
 if msg=13 then
  << princ "The degree quantifier is not allowed in the integral";
     princ " operator.">> else
 if msg=14 then
  << princ "Variable of integration not specified.";
     princ "Please use <bvar></bvar> tags to specify variable.">>
 else
 if msg=15 then
  << princ "Incorrect use of <bvar></bvar> tags.";
     princ " Correct use:";
     terpri();
     princ
"<bvar> bound_var </bvar> [<degree> degree </degree>] </bvar>">> else
 if msg=16 then
  << princ "Symbolic constant ";
     princ str;
     princ " was enclosed between <cn></cn> tags.";
     terpri();
     princ "Correct syntax: <ci> ";
     princ str;
     princ " </ci>";
     terpri();
     princ "or <cn type=""constant""> </cn>";
     princ "if using constants &ImaginaryI;, &ii;, &ExponentialE;, &gamma;, &ee; or &pi;."
  >> else
 if msg=17 then
  << princ "Unknown tag: <";
     princ str;princ ">.";
     terpri();
     princ "Token not allowed within <apply></apply> tags.";
     terpri();
     princ "Might be: <"; princ str; princ "/>.">> else
 if msg=18 then
  << princ "Unknown tag: <";
     princ str;princ ">.";
     terpri();
     princ "Not allowed within <reln></reln> tags.">> else
 if msg=19 then
  << princ "Undefined error!";
     princ " Token "; princ sub1 count;
     princ " is probably mispelled";
     terpri();
     princ "or unknown, ";
     princ "or the </math> tag is missing">> else
 if msg=20 then
  << princ "Function ";
     princ str;
     princ "()";
     princ " was not enclosed in <ci></ci> tags.";
     terpri();
     princ "Correct syntax: <fn><ci>";
     princ str;
     princ "</ci></fn>.">> else
 if msg=21 then
  << princ "Error, division by 0">> else
 if msg=22 then
  << princ "<tendsto/> should contain a type attribute";
     terpri();
     princ "example: <tendsto type=""above""/>";>>;


 terpri();
 if FILE!*=t then close rds !*f!*;
 FILE!*:=nil;
 rederr("");
 rederr("");
 terpri();
end;


% This function transforms a list representing a list of matrix columns
% to a list representing a list of matrix rows
% Very important in order to deal with OpenMath's way of
% representing Matrices which can be both with columns
% or rows.

symbolic procedure cols2rows(l);
begin scalar len;
%  return l;
  len := length car l;
  return reverse cols2rows2(l, len);
end;

symbolic procedure cols2rows2(l, s);
begin;
  if s neq 0 then return cons(ithListElem(l, s), cols2rows2(l, s-1));
end;

% This function is given a list of lists (ie a matrix) and an index i.
% It then returns a list containing the ith element of the lists in the list lst
% for example: listelem('((1 2)(3 4)(5 6)), 2) --> (2 4 6)

symbolic procedure ithListElem(lst, i);
begin;
  if lst neq nil then return cons(nth(car lst, i), ithlistelem (cdr lst, i));
end;


% The function subst(a1,a2,a3) substitutes a1 for all occurences
% of a2 in list a3

% Allows printing out two variables. Usually a
% string and a variable.

symbolic procedure debug(s1, s2);
begin;
  terpri!* t;
  princ s1; princ s2;
  terpri!* t;
end;

% If v=t then there is a 2 space indentation,
% if v=nil then the next print will be
% 2 spaces less.

fluid '(indent ind);

symbolic procedure indent!* (v);
begin;
 if v=t then indent:=indent+ind;
 if v=nil then indent:=indent-ind;
end;

end;
