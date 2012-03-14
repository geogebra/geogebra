module opertens;

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


% This module generalizes CANONICAL to make it active
% on expressions which are arguments of OPERATORS. The typical
% case, presently implemented, is when the expression is under
% the derivative df.
% A general  operator, to be treated as df must be endowed
% with a specific property which makes it "transparent" to canonical
% so that CANONICAL can see the argument(s) it contains, recognize  the
% (eventually explicitly declared) dummy indices these depend on
% and, finally, find their normal form.

switch onespace;

!*onespace:=t;  % working inside a unique space is the default.

fluid '(opertensnewids!*);

symbolic procedure restorealldfs u;
begin scalar y,z,w;
 z:=fullcopy u;
 w:=z;
 l: if domainp z then return w
    else if (not atom mvar z) and (y:=get(car mvar z, 'Translate2))
       then mvar z:=apply1(car y,mvar z);
     z:= lc z;
     go to l;
end;

%symbolic procedure restorealldfs u;
%begin scalar y,z;
% z:=u;
% l: if domainp z then return u
%    else if (not atom mvar z) and (y:=get(car mvar z, 'Translate2))
%       then mvar z:=apply1(car y,mvar z);
%     z:= lc z;
%     go to l;
%end;

symbolic procedure clearallnewids;
% the ephemerous operators created by 'dftypetooper' must
% be eliminated after the normal form is found.
% This is done here.
<<for each x in opertensnewids!* do
    <<if flagp(x,'tensor) then
        rem_tensor1 x
      else clear x;
      remprop(x,'Translate2)>>;
  opertensnewids!*:=nil>>;

symbolic procedure dftypetooper(u);
% (df (g a) (n b) 2) as arg and gives back (df_g_n_2 a b)
% df_g_n_2 gets property (dfprop df (g 1) (n 1) 2)
% same occurs for dfpart if it is given the prop ('Transtocanonical 'dftypetooper)
% Declares the results as being a tensor if one of the args at least is tensor
begin scalar name,proplist,arglist,varlist,switchid,IsTens,spacel,z;
 name:=list(car u);
 proplist:= name;
 for each y in cdr u do
   << if listp y then
       << name:=car y . ('!_ . name);
          if flagp(car y,'tensor) then
            << IsTens:=t;
               if null !*onespace and null((z:=get(car y,'belong_to_space)) memq spacel)
                 then spacel:=z . spacel;
               if (listp cadr y) and ((caadr y) eq 'list ) then
                 << proplist:= list(car y, length cdr y - 1, length cadr y - 1) . proplist;
                    varlist:=append(varlist, cdadr y);
                    for each z in cddr y do
                      arglist:=<<if switchid then id_switch_variance z
                                 else z>> . arglist ;>>
               else
                 << proplist:= list(car y, length cdr y) . proplist ;
                    for each z in cdr y do
                      arglist:= <<if switchid then id_switch_variance z
                                      else z>> . arglist ;>>;  >>
          else
           << proplist:= list(car y,length cdr y) . proplist;
              varlist:=append(varlist,cdr y); >>;
        >>
      else
        << name:= y . ('!_ . name);
           proplist:= y . proplist ; >>;
      switchid:=t;
   >>;
 arglist:=reverse(arglist);
 proplist:=reverse(proplist);
 name:=list_to_ids!:(reverse name);
 if IsTens then
  << if flagp(name,'tensor)
       then
         << if get(name,'translate2) and ((cdr get(name,'translate2)) neq proplist) then
              rerror(cantens,13,"problem in number of arg") >>
       else
         <<make_tensor(name,t);
           intern name;
           if (null !*onespace) and (length(spacel)=1)
             then put(name,'belong_to_space,car spacel);
           opertensnewids!*:= name . opertensnewids!* ;
           put(name,'translate2,'opertodftype . proplist)>>;
     if varlist then arglist := ('list . varlist) . arglist >>
           else
  << if (get(name,'translate2)) and ( cdr get(name,'translate2) neq proplist) then
         rerror(cantens,13,"problem in number of arg")
       else
         <<if null (gettype name = 'operator)
             then << mkop name;
                     opertensnewids!*:= name . opertensnewids!* ;
                     intern name>>;
           put(name,'Translate2,'opertodftype . proplist);
           arglist:=varlist>>  >>;
  return name . arglist;
end;

symbolic procedure opertodftype(u);
% u is an operator (df_g_n_2 a b) where df_g_n_2 has property
% (dfprop (g 1) (n 1) 2)
% gives back the df : (df (g a) (n b) 2)
begin scalar proplist,idslist,varlist,argres,name,i,switchid,y,idsl,varl;
  proplist:=cdr get(car u,'translate2);
  name:=car proplist;
  proplist:=cdr proplist;
  idslist:=cdr u;
  % get variables if there are some
  if ((listp car idslist) and (caar idslist eq 'list)) then
    <<varlist:=cdar idslist; idslist:=cdr idslist>>;
  if flagp(car u,'tensor) then
    for each y in proplist do
      <<if listp y then
          if flagp(car y,'tensor) then
            << idsl:=nil;
               for i:=1:cadr y do
                 << idsl:=(if switchid then id_switch_variance car idslist
                           else car idslist) . idsl;
                    idslist:=cdr idslist; >>;
               idsl:=reverse idsl;
               if cddr y then
                 << varl:=nil;
                    for i:=1:caddr y do
                       << varl:= car varlist . varl;
                          varlist:=cdr varlist >>;
                    varl:=reverse varl;
                    argres:=((car y . ( ('list . varl) . idsl)) . argres) >>
               else argres:=((car y . idsl) . argres); >>
          else
           << varl:=nil;
              for i:=1:cadr y do
                << varl:=(car varlist) . varl;
                   varlist:=cdr varlist >>;
              varl:=reverse varl;
              argres:=(((car y) . varl) . argres)>>
        else argres:=y . argres;
        switchid:=t; >>
  else
    << for each y in proplist do
         if listp y then
           << varl:=nil;
              for i:=1:cadr y do
                << varl:=((car idslist) . varl);
                   idslist:=cdr idslist >>;
              varl:=reverse varl;
              argres:=(((car y) . varl) . argres)>>
         else argres:= y. argres; >>;
  return name . (reverse argres)
end;

symbolic procedure makedfperm;
  put('df,'Translate1,'dftypetooper);

flag ('(makedfperm), 'opfn);
deflist('((makedfperm endstat)),'stat);

makedfperm;

endmodule;

end;
