%----------------------------------------------------------------------------
%                                                                           |
% A new Exp-Log limits package in REDUCE                                   |
%                                                                           |
% Author: Neil Langmead                                                        |
% Place:  ZIB, Berlin                                                           |
% Date:   April 1997                                                     |
% e/mail: langmead@zib.de                                             |
%                                                                           |
% some cleanup and a minor fix by WN 14 Dec 2005                            |
% all bugs and comments or suggestions please report to Winfried Neun,      |
% ZIB, Takustrasse 7, D-14195, Berlin Dahlem, Germany: e/mail neun@zib.de   |
%----------------------------------------------------------------------------

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


module mrvlimit;
create!-package ('(mrvlimit expon),nil);

global '(tracelimit!*); % for the tracing facility)

switch tracelimit;

off tracelimit; % off by default

flag('(sqchk),'opfn);

%symbolic procedure max_power(f,x);
%  if domainp f then 0 else
%    if mvar f eq x then ldeg f else
%          max(max_power(lc f,x),max_power(red f,x))

%put('max2,'psopfn,'max_power)
load_package limits;
load_package sets;

algebraic;
off mcd;

symbolic procedure maxi(f,g);
begin scalar c;
if(freeof(f,'x)) then return g;
if(freeof(g,'x)) then return f;
if(f=g) then return  f else
<<
if(null f) then return  g else
<<
  if(null g) then return   f
  else <<
    if (intersection(f,g) neq '(list)) then return union(f,g)
      else <<
        if(evalb('x member f)='true) then return  g
         else <<
            if(evalb('x member g)='true) then return  f
             else  <<
              if(car f='list and cadr f='list) then % double list
                            << % only want caddr f to be given to compare
                                      c:=compare(caddr f,cadr g);
                                 %write "c is ", c; write length(c)
                                return c;
                            >>
                  else <<
                          if(car g='list and cadr g='list) then
                             <<
                             c:=compare(cadr f,caddr g);
                             %write "c is ", c;
                                 return c;
                          >>
                          else <<
                                 c:=compare(cadr f, cadr g);
                                  %write "c is ", c;
                                  return c;
                                 >>;
                        >>;

              %if(c=cadr f) then return cadr f else <<
              %         if(c=cadr g) then return (cadr g)
              % else return union(cdr f,cdr g);
                                           %   >>;
                    >>;
                >>;
            >>;
          >>;
 >>;
>>;
end; % of max

 %max
%-------------------------------------------------------------------------

algebraic;
procedure maxi1(f,g);  lisp cadr (lisp (list('list,maxi(f,g))));

algebraic;
expr procedure compare(f,g);
begin scalar logg, logf;
   logf:=log(f);
   logg:=log(g);

   if(mrv_limit(logf/logg,x,infinity)=0) then return {g} else <<
        if(mrv_limit(logg/logf,x,infinity)=0) then return {f} else
                return {f,g}; >>;
end;

procedure comp(f,g); lisp('list.compare(f,g));

%----------------------------------------------------------------------------
load_package assist;

symbolic procedure mrv(li);
begin
off mcd;  on factor;

% The next line doesn't do anything in symbolic mode. Presumably li
% should be simplified in some way.  However, li is not always an
% algebraic expression. Sometimes it is a list of one, or a list of a
% number.

li:=li;
if(numberp li) then return nil else <<
if(li='(list)) then return nil else <<
if(atom li) then return lisp ('list.{li}) else <<
  if(car li='times) then <<
               if(atom cadr li and atom caddr li) then
                     << if(length(cddr li)=1) then
                        return  lisp ('list.maxi1({cadr li}, {caddr li}))
                        else return maxi1({cadr li},mrv(cddr li))
                      >>
               else return  maxi1(mrv(cadr li), mrv(cddr li))
                           >>
    else <<
     if(car li='minus) then <<
          if(atom cadr li) then return 'list.{cadr li} else return
                mrv(cadr li)
                             >>
                            %return mrv(append({'plus},cdr li))
      else <<
     if(car li='plus) then <<
   %if(null caddr li) then return mrv(cadr li)
   if(length cdr li=1) then %only one argument to plus
      return mrv(cadr li) else <<
             if(atom cadr li and atom caddr li) then
               << if(length(cddr li)=1) then return
                       lisp ('list.maxi1({cadr li},{caddr li}))
                  else return
                  lisp ('list.maxi1({cadr li},mrv(append({'plus},cddr li))))
                >>
                else <<
                   if(atom cadr li and pairp caddr li) then
                  return
           maxi1('list.{cadr li}, mrv(cddr li)) % here as well
                else  <<
         if(pairp cadr li and null caddr li) then return mrv(cadr li) else
       <<
         if(pairp cadr li and atom caddr li) then
             <<
               if(length(cdr li)>2) then % we have plus with > two args
return lisp cdr ('list.maxi1(mrv(cadr li),mrv(append({'plus},cddr li)))) %her
 else
    return lisp cdr ('list.maxi1(mrv(cadr li), mrv(cddr li))) >>
    else
<< if(null caddr li) then return mrv(cadr li)
      else return maxi1(mrv(cadr li), mrv(append({'plus},cddr li))) >>
             >>
                      >>
                            >>
                                 >>
                                      >>
           else <<
           if(car li='expt) then <<
               if(cadr li neq 'e) then
               return  mrv(cadr li)
               else <<  %we have e to the power of something
                       if sqchk mrv_limit(caddr li,'x,'infinity)
                             eq 'infinity
                           then
                         return
                        maxi1('list.{li},mrv(caddr li)) else
                         <<
                           if sqchk mrv_limit(caddr li,'x,'infinity)
                              = '(minus infinity)
                               then
                     return  maxi1('list.{li},'list.mrv(cddr li)) else return
                            mrv(caddr li)
                          >>
                     >>
                                  >>
                else << if(car li='log) then
                        << if(atom cadr li) then return mrv(cadr li) else
                           return mrv(cdr li)
                         >> else <<
                       if(car li='sqrt) then return mrv(cdr li) else
                        return mrv(car li)
                                  >>
                      >>
                  >>
            >>
     >> % for minus
  >> %for null
 >> % for numberp
              >>;
off mcd;
end; % of mrv

algebraic;

procedure mrv1(li);  lisp (mrv(li));
%----------------------------------------------------------------------------
% procedure to return a list of subexpressions of exp
% this will then be used for the mrv function

symbolic procedure flatten(li);
  % This procedure turns a list with possibly nested sub_lists into a single
  % List with no nested sub-lists. Easier to search this list.
  makeflat(li,nil);

symbolic procedure makeflat(li,answer);
  if li=nil then nil
  else
    if atom li then li.answer
    else if (cdr li)=nil then makeflat(car li,answer)
       else append(makeflat(car li,answer),makeflat(cdr li,answer));

algebraic;
procedure flat(li); lisp(flatten li);
procedure mkflat(li); lisp(makeflat(li,nil));
%in "max";
%trst maxi;

symbolic procedure lim(exp,var,val);
begin scalar mrv_list, rule;
mrv_list:=mrv1(exp);
if(mrv_list='(list)) then rederr "unable to compute mrv set"
  else
    <<
       rule:=list(list ('replaceby, cdr mrv_list,'w));
       let rule;
    >>;

end;
% nedd to consider if x belongs to mrv(exp), then follow rest of alg.
algebraic;
expr procedure move_up(exp,x);
sub({log(x)=x,x=e^x},exp);

expr procedure move_down(exp,x);
sub({e^x=x,x=log(x)},exp);

%off mcd;
algebraic;
expr procedure rewrite(m);
begin scalar ans_list,summ,k,g,c,A;

ans_list:={};
g:=part(m,1); write length g;
for k:=1:arglength(m) do
<<
  summ:=length(den(part(m,k)))+length(num(part(m,k))); write summ;
  if(summ<(length(den(g))+length(num(g)))) then g:=part(m,k);
>>;
write "g is ", g;

for each f in m do
  <<
    c:=limit(log(f)/log(g),x,infinity);
    A:=e^(log(f)-c*log(g));
    f:=A*w^c;
    ans_list:=append({f}, ans_list);
  >>;
return ans_list;
end;

%expr procedure smallest(li);
%begin scalar current,k;
%current:=part(li,1);
%for k:=1:arglength(li) do <<
%             if(length(current)>length(part(li,k))) then
%             current:=part(li,k);
%                           >>;
%return current;
%end;

expr procedure smallest(li);
begin scalar l1,l2;
if(length li=1) then return part(li,1) else
            <<
l1:=lngth2(part(li,1));
l2:=lngth2(part(li,2));
if(l1>l2) then return part(li,2) else
 << if(l1<l2) then return part(li,1) else return part(li,1); >>;
             >>;

end;

symbolic procedure lngth u;
 begin
     if(u='list) then return nil else
    <<
      if(atom u) then return 1 else
      <<
        if(atom car u) then return (1+lngth cdr u)
        else return lngth car u + lngth cdr u;
      >>;
    >>;
 end;

%put('lngth2,'psopfn,'lngth);
algebraic;
procedure lngth2 u; lisp lngth u;

%-------------------------------------------------------------------------

% main routine to compute limits of exp-log functions as the variable tends
% to infinity.

operator x;
operator series;
algebraic;

expr procedure mrv_limit(f,var,val);

begin scalar mrv_f,mrv1_f,w, mrv_f2,tt, lead_term, series_exp,f1, small, rule1,
const, e0,sig,rule2,k, nu,de,h,recurse;
off mcd; off factor; off rational; off exp; off precise;
%if(val neq infinity) then return sub(var=val,f);

% trigonometric expressions aren't dealt with by the algorithm

if(not(freeof(f,sin))) then rederr "input not an exp log function";
if(not(freeof(f,cos))) then rederr "input not an exp log function";
if(not(freeof(f,tan))) then rederr "input not an exp log function";
if(freeof(f,var)) then % possible cases: f can be a number, an expression
                       % independent of var, or possibly not an exp log
                       % function. In all cases, return f as the answer
return f;
if(val neq infinity) then return sub(var=val,f);

%on rational;
%on rounded;
%this checks for numbers. red doesn't recognise some objects as numbers unless
% the rounded switch is on

f1:=f; if(numberp(f1)) then return f; off rational;
off rounded;
if(var neq x) then f:=sub(var=x,f) else nil;
if(numberp(f)) then return f;
%%%% special case where f=e, or pi. Don't want to use the on rounded switch
%%%% in these cases
if(f=e) then return e;
if(f=pi) then return pi;
%if(f=x) then return plus_infinity;
on factor; off mcd; mrv_f:=mrv1(f);
%write "*********************************************************************";
if(lisp !*tracelimit) then
write "mrv_f is ", mrv_f;
lisp if null mrv_f then % emergency exit for now
         return ('limit . list(f,var,val));

%write "*********************************************************************";
off factor;
%on mcd;

if(member(x,mrv_f)) then
  <<
    off exp; off mcd;
    while(member(x,mrv_f)) do
      <<
        f:=move_up(f,x); %write "f is ", f;
        %mrv_f:=mrv1(f);
        mrv_f:=for k:=1:arglength(mrv_f) collect move_up(part(mrv_f,k),x);
        %write "mrv is ", mrv_f;
      >>;
    % we know that x was a member of mrv(f), so now, the mrv set will contain
    % e^x at least. Hence, write directly in terms of w=e^(-x)

    small:=e^(-x);
    % now have the smallest element, but don't know its limiting behaviour
    % if lim is infinity, need to set w to 1/small
    rule1:={small => ww };
    let rule1;
    f:=f;
    on mcd; nu:=num(f); de:=den(f); f:=nu/de; off mcd;
    f:=f; % write f;
    clearrules rule1;
       % f now rewritten
   >>
  else <<
         %mrv_f2:=rewrite(mrv_f); % write "f2 is ", mrv_f2;
                 % now need to rewrite f itself
        small:=smallest(mrv_f);
        h:=log(small); %write "h is ", h;
        if(mrv_limit(h,x,infinity)=infinity) then
         <<
            small:=small^-1;
            % write "small has been changed", small;
         >>;

          rule1:=
                { small => ww,
                  1/small => ww^-1 };
         let rule1; off mcd;

         f:=f;
         on mcd;
         off mcd; %f:=f;
         clearrules rule1;

         % now rewritten in terms of w, and mrv(f)=w hopefully
        >>;

% at this point, f has been rewritten. Now check lcof of series expansion


% lisp !*mcd:=nil; lisp !*factor:=nil; lisp !*exp:=t; lisp !*rational:=nil;

off mcd; on factor; off exp; off rational;
series_exp:=taylor(f,ww,0,1);
if(lisp !*tracelimit) then
   write "performing taylor on: ", f;

%off mcd; on exp; on factor; off rational;
if(not taylorseriesp series_exp and part(series_exp,0)=taylor)
  then rederr "could not compute the Taylor series expansion";

tt:=log(small);

series_exp:=sub(log(ww)=tt,series_exp); %off mcd; off factor; off exp;
series_exp:=taylortostandard series_exp;
if(lisp !*tracelimit) then write "series expansion is ", series_exp;
% should now have the lead term of the series expansion in terms of w
if(numberp(series_exp)) then return series_exp
else <<
       if(lisp !*tracelimit) then
       write "series is ", series_exp;
       off rational;  off mcd;  off exp; off factor;
       series_exp:=series_exp; off factor;
       const:=coeffn(series_exp,ww,0); %write "const is ", const;
       if(const neq 0) then
       <<
         if(numberp(const)) then return const else
         <<
             if(lisp !*tracelimit) then
             write "performing recursion on ", const;
             off mcd; on factor; off exp;  on rational; const:=const;
         return mrv_limit(const,x,infinity); >>;
       >>
       else
       <<
       % need to look at exponent of ww. If e0>0 then return 0, if
       % e0<0 return infinity, if e0=0 return mrv_limit(c)
       %write "series_exp is ", series_exp;
       series exp:=series_exp; off mcd; % try it here!
       %if(lisp !*tracelimit) then
       %write "series exp is  ", series_exp;
       %  series_exp:=lisp reval series_exp;
       %off mcd;
       e0:=find_least_expt(series_exp);
        if(lisp !*tracelimit) then write "leading exponent e0 is ", e0;
       off mcd; on factor; off exp; off rational;
       %if(part(e0,1)=expt) then
       %<< e0:=part(e0,2);
       %e0:=part(e0,1); if(e0=e) then return e;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% possible cases: e0:={expt_list,num_list,x_list}
% if both num_list and x_list are empty, then e0 takes the value of the
% smallest exponent in expt_list.
% if numbers in expt_list are all positive, then e0 is the value in either
% num_list, or expt_list: if num_list, then this number is returned, if
% expt_list, then we apply algorithm recusively to the value of x_exp
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

       if(part(e0,1)=expt) then <<
                                               e0:=part(e0,2);
       %if(not numberp e0) then return part(e0,2);
       if(e0<0 and ((lisp car series_exp) neq minus))
        then return infinity else
                << %*sign(lcof(series_exp,ww))
                     if(e0<0) then return -infinity

                               else
                               <<
                               if(e0>0) then return 0 else <<
                          off mcd; off factor; off rational; on exp;
                           recurse:=lcof(series_exp,ww); return
                          mrv_limit(recurse,x,infinity); >>;
                               >>;
                 >>;
                                 >>
       else <<
       if(part(e0,1)=number) then return part(e0,2) else
        <<if sqchk part(e0,1) = 'minus then return -infinity else nil>>
            >>;
       e0:=lpower(series_exp,ww); off mcd;
       % expr free of neg powers ? sort of
       if(numberp e0) then <<
        on mcd; on exp; e0:=lpower(num series_exp,ww)/lpower(den series_exp,ww);
        off mcd; e0:=e0;  >>;

       lisp (e0:=lisp prepsq cadr e0);

       if(e0=ww) then return 0 else
       <<
         if(part(e0,0)=expt) then
          <<
            if(part(e0,2)<0) then % have plus /minus infinity
               <<
                   if(sign(lcof(series_exp,ww))=-1) then
                   return -infinity else  << if
                  (sign(lcof(series_exp,ww))=1) then return infinity
                 else <<
                         rule2:= {
                                   sign(log(~x)) => 1
                                 };
                         let rule2;
                         sig:=sign(lcof(series_exp,ww));
                         clearrules rule2;
                         return infinity;
                       >>;
                                                 >>;
               >>
            else
            <<
              if(part(e0,2)>0) then return 0 else
                  % the limiting behaviour of f depends on that of c
                << on rational;
                off mcd; return mrv_limit(lcof(series_exp,ww),x,infinity); >>;
                >>;
             >>;
          >>;
        >>;
     >>;
off mcd;
end;
%---------------------------------------------------------------------------

%-----------------------------------------------------------------------------
% for examples, please see the test flie included with the documentation. The
% examples labelled from ex1 to ex12 are all taken from Dominik Gruntz' Thesis
% paper. Most are complicated examples, as he himself admits. This package
% does not use the standard limits operator in REDUCE: it has been written to
% COMMENT by WN: only as emergency exit!!
% be independent, and can be presented as a separted facility in REDUCE.

%-----------------------------------------------------------------------------
endmodule;
end;
