module liqsimp2; % interval simplification level2 by
                 % removal of non-tight hyperplanes.

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



fluid'(infinities!*);

symbolic procedure liqsimp2!-maxmin w;
 % W is a list of forms {x.x0 , l , u} where the interval [l,u]
 % has been assigned to the variable x. l and u may be formal
 % expressions dominated by an operator MAX or MIN and including
 % variables of the following intervals. I try to simplify the
 % bounds as far as possible by computing inequality chains.
  begin scalar r;
    infinities!* := {simp 'infinity, simp '(minus infinity)};
    w:= for each q in w collect
      {car q, minmax2ql cadr q, minmax2ql caddr q};
     r:= liqsimp2!-maxmin1 w;
    return for each q in r collect
      {car q, ql2minmax('max,cadr q),ql2minmax('min,caddr q)};
  end;

symbolic procedure ql2minmax(m,l);
  <<l:=for each q in l collect prepsq q;
    if cdr l then m.l else car l>>;

symbolic procedure minmax2ql(l);
  if pairp l and car l memq '(min max) then
     for each q in cdr l collect simp q else {simp l};

symbolic procedure liqsimp2!-maxmin1 w;
   if null w then nil else
   liqsimp2!-reducecases(car w,liqsimp2!-maxmin1 cdr w);


symbolic procedure liqsimp2!-reducecases(w,ll);
 % ll is alreayd a simplified chain of intervals.
  begin scalar x,l,u,t1,e1,e2,pts,eqns,y;
   x:=caar w; l:=cadr w; u:=caddr w;
   if null cdr l and null cdr u then return w.ll;

     % If I have more than one inequality in the upper
     % or lower part, I compute all pairwise crossing point
     % because these form the new contribution to the edges.
     % An inequality which has no valid point can be excluded
     % from the set. I may ignore infinite points because each
     % line must have at least two points.
   eqns := for each q in delete(car infinities!*,
              delete(cadr infinities!*,append(l,u)))
      collect {q};
     % Compute crossing points.
   t1:=eqns;
   while t1 and cdr t1 do
   <<e1 := car t1; t1:= cdr t1;
     for each e2 in t1 do
     <<pts :=liqsimp2_mk_edges(x,car e1,car e2,l,u,ll);
       cdr e1:=append(cdr e1,pts); cdr e2 := append(cdr e2,pts);
   >>>>;
   l:=for each q in l join
     if null (y:=assoc(q,eqns)) or cdr y then {q};
   u:=for each q in u join
     if null (y:=assoc(q,eqns)) or cdr y  then {q};
   return{car w,l,u}.ll;
  end;

symbolic procedure liqsimp2_mk_edges(x,e1,e2,l,u,ll);
  % x: current variable,
  % e1,e2: forms defining an edge contribution in x=e1,x=e2 at their
  %        intersection points. e1,e2 free of x.
  % l:     complete lower bounds for x,
  % u:     complete upper bounds for x,
  % ll:    simplified bounds for the lower variables.
 begin scalar form,pts,pl;
   form := subtrsq(e1,e2);
   pl := liqsimp2_mk_edges1(form,ll);
   pts := liqsimp2_mk_edges_check(pl,x,e1,l,u);
   return pts;
  end;

symbolic procedure sfnegativep u;
  if domainp u then minusf u else
  if mvar u = 'infinity then sfnegativep lc u else
  typerr(prepf u,"numeric expression");

symbolic procedure liqsimp2_mk_edges1(f,ll);
  % check f=0 by substituting the hyperplanes in ll.
  if null ll and null numr f then '(nil)
   else
     if null ll then typerr (prepsq f,"soll nicht vorkommen")
   else
  begin scalar fx,fxx,t1,x,l,u,points,pl;
    x:=caaar ll; l:=cadar ll; u:=caddar ll; ll:=cdr ll;
    t1 := delete(car infinities!*,
              delete(cadr infinities!*,append(l,u)));
    if null t1 then t1:='((nil . 1));
    fx:=liqsimp2_mk_edges2(f,x);
    fxx := '(nil . 1);

    points:= if null fx then
       % case 1: f does not depend of x. I must extend all
       % solutions of f wrt the remaining variables
       % by all possible edges from the interval bounds for x.
    <<pl :=liqsimp2_mk_edges1(fxx,ll);
      for each q in t1 join
        for each p in pl collect
         (x . prepsq subsq(q,p)) . p
    >>
    else
      if domainp numr fx then
       % case 2: f has the solution x=a where a does not depend
       % of any further variable. I must select those
       % extensions of x=a which are compatible under the local
       % inequalities.
    << pl:=liqsimp2_mk_edges1(fxx,ll);
       pl := liqsimp2_mk_edges_check(pl,x,fx,l,u);
      pl>>
    else
       % case 3: f depends of x and some more variables.
       % I compute all possible intrsection points with the
       % current interval bounds and extend the to solutions
       % with the remaining variables.
      for each p in t1 join
    <<fxx := subtrsq(fx,p);
      pl :=liqsimp2_mk_edges1(fxx,ll);
       % and remove points which don't satixfy the restrictions.
      pl := liqsimp2_mk_edges_check(pl,x,fx,l,u);
      pl>>;
    return points;
   end;

symbolic procedure liqsimp2_mk_edges_check(pl,x,fx,l,u);
 % select those points of pl where sub(x=p,fx) is compatible
 % with the l and u bounds. Extend the compatible points by
 % a value for x.
  for each p in pl join
  begin scalar fine,x1;
   fine:=t;
   x1:=subsq(fx,p);
   for each l1 in l do
      if fine and sfnegativep numr subtrsq(x1,subsq(l1,p)) then
         fine:=nil;
   for each u1 in u do
      if fine and sfnegativep numr subtrsq(subsq(u1,p),x1) then
         fine:=nil;
   return if fine then {(x.prepsq x1).p};
  end;

symbolic procedure liqsimp2_mk_edges2(f,x);
  % solve f(x)=0 for linear standard quotient f. Return
  % nil if x does not occur in f.
  if not smemq(x,f) then nil else
  begin scalar w;
    w:=(reorder numr f) where kord!*={x};
    return quotsq(negf red w./ 1,lc w ./ 1) ;
  end;

% ============= printing ================================

symbolic procedure linineqprint1(text,lh,rh);
         <<writepri(text,'first);
           writepri(mkquote prepsq lh,nil);
           writepri(" >= ",nil);
           writepri(mkquote prepsq rh,'last)>>;


symbolic procedure linineqprint2(text,prob);
    <<prin2t "--------------------------------";
          if atom text then text:={text};
      for each u in text do prin2 u; terpri();
          writepri(mkquote('list .
         for each p in prob collect
                    {'geq,prepsq car p,prepsq cdr p}),'last)
    >>;

symbolic procedure linineqprint3(text,res);
    <<writepri(text,'first);
          writepri(mkquote('list . for each p in res collect
                     {'equal,car p,cdr p}), 'last);
        >>;

endmodule;

end;
