module precoats;

% Author: James H. Davenport.

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


fluid '(!*tra
        basic!-listofallsqrts
        basic!-listofnewsqrts
        sqrt!-intvar
        taylorvariable
        thisplace);

exports precoates;
imports mksp,algint!-subf,subzero2,substitutesq,removeduplicates,
        printsq,basicplace,extenplace,interr,get!-correct!-sqrts,
        printplace,simptimes,subzero,negsq,addsq,involvesq,taylorform,
        taylorevaluate,mk!*sq,!*exptsq,!*multsq,!*invsq,sqrt2top,
        jfactor,sqrtsave,antisubs;


symbolic procedure infsubs(w);
if caar w = thisplace
  then (cdar w).(cdr w)
  else (thisplace.(car w)).(cdr w);
% thisplace is (z quotient 1 z) so we are moving to infinity.


symbolic procedure precoates(residues,x,movedtoinfinity);
begin
  scalar answer,placeval,reslist,placelist,placelist2,thisplace;
  reslist:=residues;
  placelist:=nil;
  while reslist do <<
    % car reslist = <substitution list>.<value>;
    placeval:=algint!-subf((mksp(x,1) .* 1) .+ nil,caar reslist);
    if 0 neq cdar reslist
      then if null numr subzero2(denr placeval,x)
        then <<
          if null answer
            then answer:='infinity
            else if answer eq 'finite
              then answer:='mixed;
          if !*tra
            then printc "We have an residue at infinity" >>
        else <<
          if null answer
            then answer:='finite
            else if answer eq 'infinity
              then answer:='mixed;
          placelist:=placeval.placelist;
          if !*tra
            then printc "This is a finite residue" >>;
    reslist:=cdr reslist >>;
  if answer eq 'mixed
    then return answer;
  if answer eq 'infinity
    then <<
      thisplace:=list(x,'quotient,1,x);
      % maps x to 1/x.
      answer:=precoates(for each u in residues collect infsubs u,x,t);
                % derivative of 1/x is -1/x**2.
      if atom answer
        then return answer
        else return substitutesq(answer,list(thisplace)) >>;
  placelist2:=removeduplicates placelist;
  answer := 1 ./ 1;
  % the null divisor.
  if !*tra then <<
    printc "The divisor has elements at:";
    for each j in placelist2 collect printsq j>>;
  while placelist2 do begin
    scalar placelist3,extrasubs,u,bplace;
    % loop over all distinct places.
    reslist:=residues;
    placelist3:=placelist;
    placeval:=nil;
    while reslist do <<
      if car placelist2 = car placelist3
        then <<
          placeval:=(cdar reslist).placeval;
          thisplace:= caar reslist;
          % the substitutions defining car placelist.
          u:=caar reslist;
          bplace:=basicplace u;
          u:=extenplace u;
          extrasubs:=u.extrasubs >>;
      reslist:=cdr reslist;
      placelist3:=cdr placelist3 >>;
    % placeval is a list of all the residues at this place.
    if !*tra then <<
      princ "List of multiplicities at this place:";
      printc placeval;
      princ "with substitutions:";
      superprint extrasubs >>;
    if 0 neq mapply(function plus2,placeval)
      then interr "Divisor not effective";
    get!-correct!-sqrts bplace;
    u:=pbuild(x,extrasubs,placeval);
    sqrtsave(basic!-listofallsqrts,basic!-listofnewsqrts,bplace);
    if atom u
      then <<
        placelist2:=nil;
        % set to terminate loop.
        answer:=u >>
      else <<
        answer:=substitutesq(!*multsq(answer,u),antisubs(thisplace,x));
        placelist2:=cdr placelist2 >>
    end;
    % loaded in pbuild to check for poles at the correct places.
  return answer
  end;



symbolic procedure dlist(u);
% Given a list of lists,converts to a list.
if null u
  then nil
  else if null car u
    then dlist cdr u
    else append(car u,dlist cdr u);


symbolic procedure debranch(extrasubs,reslist);
begin
  scalar substlist;
  % remove spurious substitutions.
  for each u in dlist extrasubs do
    if not ((car u) member substlist)
      then substlist:=(car u).substlist;
  % substlist is a list of all the possible substitutions).
  while substlist do
    begin scalar tsqrt,usqrt;
      scalar with1,with2,without1,without2,wres;
    scalar a1,a2,b1,b2;
    % decide if tsqrt is redundant.
    tsqrt:=car substlist;
    substlist:=cdr substlist;
    wres:=reslist;
    for each place in extrasubs do <<
      usqrt:=assoc(tsqrt,place);
        % usqrt is s.s' or s.(minus s').
      if null usqrt
        then interr "Places not all there";
      if cadr usqrt eq 'sqrt
        then<<
          with2:=(car wres).with2;
          with1:=delete(usqrt,place).with1>>
        else<<
          if not (cadr usqrt eq 'minus)
            then interr "Ramification format error";
          without2:=(car wres).without2;
          without1:=delete(usqrt,place).without1 >>;
      wres:=cdr wres>>;
    % first see if one item appears passim.
    if null with1
      then go to itswithout;
    if null without1
      then go to itswith;
    % Now must see if WITH2 matches WITHOUT2 in order WITH1/WITHOUT1.
    a1:=with1;
    a2:=with2;
  outerloop:
    b1:=without1;
    b2:=without2;
  innerloop:
    if (car a1) = (car b1)
      then << if (car a2) neq (car b2)
           then return nil
           else go to outeriterate >>;
    b1:=cdr b1;
    b2:=cdr b2;
    if null b1
      then return nil
      else go to innerloop;
      % null b1 => lists do not match at all.
  outeriterate:
    a1:=cdr a1;
    a2:=cdr a2;
    if a1
      then go to outerloop;
    if !*tra then <<
      princ "Residues reduce to:";
      printc without2;
      printc "at ";
      mapc(without1,function printplace) >>;
    extrasubs:=without1;
    reslist:=without2;
    return;
  itswithout:
    % everything is in the "without" list.
    with1:=without1;
    with2:=without2;
  itswith:
    % remove usqrt from the with lists.
    extrasubs:=for each u in with1 collect delete(assoc(tsqrt,u),u);
    if !*tra then <<
      printc "The following appears throughout the list ";
      printc tsqrt >>;
    reslist:=with2
    end;
  return extrasubs.reslist
  end;


symbolic procedure pbuild(x,extrasubs,placeval);
begin
  scalar multivals,u,v,answer;
  u:=debranch(extrasubs,placeval);
  extrasubs:=car u;
  placeval:=cdr u;
  % remove spurious entries.
  if (length car extrasubs) > 1
    then return 'difficult;
  % hard cases not allowed for.
  multivals := mapovercar dlist extrasubs;
  u:=simptimes removeduplicates multivals;
  answer:= 1 ./ 1;
    while extrasubs do <<
      v:=substitutesq(u,car extrasubs);
      v:=!*addsq(u,negsq subzero(v,x));
      v:=mkord1(v,x);
      if !*tra then <<
        princ "Required component is ";
        printsq v >>;
      answer:=!*multsq(answer,!*exptsq(v,car placeval));
      % place introduced with correct multiplicity.
      extrasubs:=cdr extrasubs;
      placeval:=cdr placeval >>;
  if length jfactor(denr sqrt2top !*invsq  answer,x) > 1
    then return 'many!-poles
    else return answer
  end;


symbolic procedure findord(v,x);
begin
  scalar nord,vd;
  %given v(x) with v(0)=0, makes v'(0) nonzero.
  nord:=0;
  taylorvariable:=x;
  while involvesq(v,sqrt!-intvar) do
    v:=substitutesq(v,list(x.list('expt,x,2)));
  vd:=taylorform v;
loop:
  nord:=nord+1;
  if null numr taylorevaluate(vd,nord)
    then go to loop;
  return nord
  end;


symbolic procedure mkord1(v,x);
begin
  scalar nord;
  nord:=findord(v,x);
  if nord iequal 1
    then return v;
  if !*tra then <<
    princ "Order reduction: ";
    printsq v;
    princ "from order ";
    princ nord;
    printc " to order 1" >>;
  % Note that here we do not need to simplify, since SIMPLOG will
  % remove all these SQRTs or EXPTs later.
  return !*p2q mksp(list('nthroot,mk!*sq v,nord),1)
  end;

endmodule;

end;
