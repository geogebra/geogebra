module intfierz; % Interface with Rodionov-Fierzing Routine.

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


  exports  calc_map_tar,calc_den_tar,pre!-calc!-map_ $
  imports    mk!-numr,map_!-to!-strand $

lisp$

%----------- DELETING VERTS WITH _0'S ------------------------------$

%symbolic procedure sort!-map_(map_,tadepoles,deltas,s)$
%if null map_ then list(s,tadepoles,deltas)
%else
%  begin
%     scalar vert,edges$
%            vert:=incident1('!_0,car map_,'ll)$
%   return
%       if null vert then sort!-map_(cdr map_,tadepoles,deltas,
%                                                car map_ . s)
%       else if car vert = cadr vert then
%                sort!-map_(cdr map_,caar vert . tadepoles,deltas,s)
%            else sort!-map_(cdr map_,tadepoles,list('cons,caar vert,
%                                    caadr vert) . deltas,s)
% end$
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%% modified 17.09.90 A.Taranov %%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
symbolic procedure sort!-map_(map_,tadepoles,deltas,poles,s)$
% tadepoles are verts with 1 0_ edge and contracted others
% deltas are verts with 1 0_ edge
% poles are verts with at list 2 0_ edges
if null map_ then list(s,tadepoles,deltas,poles)
else
  begin
  scalar vert,tdp$
    vert:=incident1('!_0,car map_,'ll)$
    if null vert then tdp:=tadepolep car map_
    else %%%% vertex contain !_0 edge
      return
        if (caar vert = '!_0) then
          sort!-map_(cdr map_,tadepoles,deltas,caadr vert . poles,s)
        else if (caadr vert = '!_0) then
             sort!-map_(cdr map_,tadepoles,deltas,caar vert . poles,s)
             else if car vert = cadr vert then
                    sort!-map_(cdr map_,caar vert . tadepoles,deltas,
                                poles,s)
                  else sort!-map_(cdr map_,tadepoles,list('cons,
                                   caar vert,caadr vert) . deltas,poles,
                                   s)$
   %%%%% here car Map_  was checked to be a real tadpole
   return
     if null tdp then sort!-map_(cdr map_,tadepoles,deltas,
                                        poles,car map_ . s)
     else sort!-map_(cdr map_,cadr tdp . tadepoles,deltas,
                      caar tdp . poles,s)
 end$

symbolic procedure tadepolep vrt;   %%%%%% 17.09.90
% return edge1 . edge2 if vrt is tadpole,
% NIL otherwise.
% edge1 correspond to 'pole', edge2 - to 'loop' of a tadpole.
if car vrt = cadr vrt then caddr vrt . car vrt
else if car vrt = caddr vrt then cadr vrt . car vrt
     else if cadr vrt = caddr vrt then car vrt . cadr vrt
          else nil;

symbolic procedure del!-tades(tades,edges)$
if null tades then edges
else del!-tades(cdr tades,delete(car tades,edges))$

symbolic procedure del!-deltas(deltas,edges)$
if null cdr deltas then edges
else del!-deltas(cdr deltas,del!-tades(cdar deltas,edges))$

%--------------- EVALUATING MAP_S -----------------------------------$

symbolic procedure pre!-calc!-map_(map_,edges)$
% : (STRAND NEWMAP_ TADEPOLES DELTAS)$
begin
  scalar strand,w$
         w:=sort!-map_(map_,nil,list 1,nil,nil)$

%        delete from edge list deltas,poles and tades
         edges:=del!-deltas(caddr w,
                    del!-tades(cadr w,delete('!_0,edges)))$
         strand:= if car w then map_!-to!-strand(edges,car w)
                  else nil$
    return strand . w
end$


symbolic procedure calc_map_tar(gstrand,alst)$
% THIRD  VERSION.$
begin
 scalar poles,edges,strand,deltas,tades,map_$
        strand:=car gstrand$
        map_:=cadr gstrand$
        tades:=caddr gstrand     $
        deltas:=car cdddr gstrand $
        poles:= car cddddr gstrand $
 if ev!-poles(poles,alst) then   return 0;   %%%%% result is zero
 return constimes list(constimes deltas,
                      constimes ev!-tades(tades,alst),
                     (if null map_ then 1
                      else strand!-alg!-top(strand,map_,alst)))
end$

symbolic procedure ev!-poles(poles,alst)$ %%% 10.09.90
 if null poles then nil
 else if getedge(car poles,alst) = 0 then ev!-poles(cdr poles,alst)
      else poles$


symbolic procedure ev!-deltas(deltas)$
  if null deltas then list 1
  else ('cons . car deltas) . ev!-deltas(cdr deltas)$

symbolic procedure ev!-tades(tades,alst)$
  if null tades then list 1
  else binc(ndim!*,getedge(car tades,alst))
                                        . ev!-tades(cdr tades,alst)$

%------------------------ DENOMINATOR CALCULATION -------------------$

symbolic        procedure ev!-edgeloop(edge,alst)$
% EVALUATES LOOP OF 'EDGE' COLORED VIA 'ALST'$
binc(ndim!*,getedge(s!-edge!-name edge,alst) )$

symbolic procedure ev!-denom2(vert,alst)$
% EVALUATES DENOM FOR PROPAGATOR$
ev!-edgeloop(car vert,alst)$

symbolic procedure ev!-denom3(vert,alst)$
% EVALUATES DENOM FOR 3 - VERTEX$
begin
    scalar e1,e2,e3,lines,sign,!3j,numr$
    e1:=getedge(s!-edge!-name car vert,alst)$
    e2:=getedge(s!-edge!-name cadr vert,alst)$
    e3:=getedge(s!-edge!-name caddr vert,alst)$
    lines:=(e1+e2+e3)/2$
    e1:=lines-e1$
    e2:=lines-e2$
    e3:=lines-e3$
    sign:=(-1)**(e1*e2+e1*e3+e2*e3)$
    numr:=mk!-numr(ndim!*,0,lines)$
    numr:=(if numr then (constimes numr)
           else 1)$
    !3j:=listquotient(numr,
                   factorial(e1)*factorial(e2)*factorial(e3)*sign)$
 return !3j
end$

symbolic procedure binc(n,p)$
% BINOMIAL COEFF C(N,P)$
if 0 = p then 1 else
listquotient(constimes mk!-numr(n,0,p),factorial p)$

symbolic procedure calc_den_tar(den_,alst)$
(lambda u$  if null u then 1
            else if null cdr u then car u
                 else constimes u           )
                                            denlist(den_,alst)$

symbolic procedure denlist(den_,alst)$
if null den_ then nil
else if length car den_ = 2 then
                   ev!-denom2(car den_,alst) . denlist(cdr den_,alst)
     else ev!-denom3(car den_,alst) . denlist(cdr den_,alst)$

endmodule;

end;
