module trigsmp2;

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


% Executable code.

% remove_element by W. Neun, 06.06.1996
algebraic procedure remove_element(l, n);
        lisp ('list . remove1(cdr reval l, n));

symbolic procedure remove1(x, n);
        if (n = 1) then cdr x else (car x) . remove1(cdr x, n-1);

symbolic procedure indets(term);
% Watch out!!! Expect to see the exponential function as "e" here
begin
   scalar vars;
   vars:= {};
   vars:= find_indets(reval term, reval vars);
   return 'list . vars;
end$

symbolic operator indets$

symbolic procedure find_indets(term, vars);
begin
   if numberp(term) then return vars;
   if idp(term) then begin
      if (not memq(term,vars)) then
         vars:= term . vars;
      end
   else
      if null(cdr(term)) then
         vars:= find_indets(car(term), vars)
      else
         begin
            vars:= find_indets(cadr(term), vars);
            if not null(cddr(term)) then
               vars:= find_indets('list . cddr(term), vars);
         end;
   return vars;
end$

symbolic operator find_indets$


symbolic procedure trig_argumentlist(term);
begin
   scalar vars;
   vars:= {};
   vars:= get_trig_arguments(term, vars);
        return 'list .vars;
end$
symbolic operator trig_argumentlist;

symbolic procedure get_trig_arguments(term, vars);
begin
        scalar f, r;
   if (arglength(term)= -1) then return vars;
        f:= car(term);
        r:= cdr(term);
   if (f = 'sin) or (f = 'cos) or (f = 'sinh) or (f = 'cosh) then
      return << r:= car r;
                                         if not (r member vars) then
                vars:= r . vars; vars>>;
          f:= r;
   for each j in f do vars:= get_trig_arguments(j, vars);
   return vars;
end$
symbolic operator get_trig_arguments;


symbolic procedure more_variables(a, b);
   if (length(indets(a)) > length(indets(b))) then t else nil;
symbolic operator more_variables;

% auxiliary variables

algebraic;

operator auxiliary_symbolic_var!*;


procedure subs_symbolic_multiples(arg_term);
begin
   scalar term, var_list, tmp_list, j, x, y, x_nu, x_lcm, y_den, unsubs;
   term:= arg_term;
        if (term = 0) then return {0, {}};
        var_list:= trig_argumentlist(term);
   var_list:= sort(var_list, 'more_variables);
   unsubs:= {};
        j:= 0;
   while (var_list neq {}) do begin
                j:= j + 1;
      x:= first(var_list);
      var_list:= rest(var_list);
      x_nu:= numberget(x);
      x_lcm:= den(x_nu);
                tmp_list:= var_list;
      for k:=1:length(var_list) do begin
         y:= part(var_list, k);
         if numberp(x/y) then begin
            tmp_list:= remove_element(var_list, k);
            y_den:= den(numberget(y));
            x_lcm:= (x_lcm * y_den) / gcd(x_lcm,y_den);
         end; % of if
      end;
                var_list:= tmp_list;
                if (x_lcm neq 1) then begin
                        x:= x / x_nu;
                        unsubs:= append(unsubs,
                                 {auxiliary_symbolic_var!*(j)=x/x_lcm});
                        term:= (term where
                                (x=>auxiliary_symbolic_var!*(j)*x_lcm));
                end; % of if
   end;
   return({term, unsubs});
end;


procedure behandle(ex);
  begin scalar p,q,p2,q2;
    p := num ex;
    q := den ex;
    let exp2trig1!*;
    p2 := p;
    clearrules exp2trig1!*;
    let exp2trig2!*;
    q2 := q;
    clearrules exp2trig2!*;
    return p2/q2;
  end;

procedure trigsimp1(f,l);
  begin
    scalar u,p,trigpreferencelist,hyppreferencelist,
    directionlist,modelist,keepalltriglist,err,onlytan;

    err:=0;
    if freeof(f,sin) and freeof(f,cos) and freeof(f,cosh) and
    freeof(f,sinh) and freeof(f,csc) and
    freeof(f,sec) and freeof(f,csch) and freeof(f,sech) then
       onlytan:=1 else onlytan:=0;
    trigpreferencelist:={};
    hyppreferencelist:={};
    directionlist:={};
    modelist:={};
    keepalltriglist:={};
    while length(l) neq 0 do begin
      u:=first(l);
      l:=rest(l);
      if u=sin or u=cos then trigpreferencelist:=u.trigpreferencelist
      else if (u=sinh) or (u=cosh) then hyppreferencelist:=
         u.hyppreferencelist
      else if (u=expand) or (u=combine) or (u=compact) then
         directionlist:=u.directionlist
      else if u=hyp or u=trig or u=expon then modelist:=u.modelist
      else if (u=keepalltrig) then keepalltriglist:=u.keepalltriglist
      else <<write u," not possible. Use sin,cos,cosh,sinh,
      expand,combine,compact,hyp,trig,expon,keepalltrig!";err:=1;>>;
    end;

%Defaults
    if trigpreferencelist={} then trigpreferencelist:={sin};
    if hyppreferencelist={} then hyppreferencelist:={sinh};
    if directionlist={} then directionlist:={expand};

%contradictions?
    if length(trigpreferencelist)>1 then <<write trigpreferencelist,
    " not possible. Use either sin or cos.";err:=1;>>;
    if length(hyppreferencelist)>1 then <<write hyppreferencelist,
    " not possible. Use either sinh or cosh.";err:=1;>>;
    if length(directionlist)>1 then <<write directionlist,
    " not possible. Use either expand or combine or compact.";err:=1;>>;
    if length(modelist)>1 then <<write modelist," not possible.
       Use either hyp or expon.";err:=1;>>;

    if err=0 then begin
%application

      if first(trigpreferencelist)=sin then trig_preference:=sin;
      if first(trigpreferencelist)=cos then trig_preference:=cos;
      if first(hyppreferencelist)=sinh then hyp_preference:=sinh;
      if first(hyppreferencelist)=cosh then hyp_preference:=cosh;

      let trig_normalize!*;
      p:=f;

      if keepalltriglist={} or directionlist={combine} or
         directionlist={compact}
      then <<let trig_standardize!*;
      p:=p+0;clearrules(trig_standardize!*);>>;

      if modelist neq {} then begin
        if first(modelist)=trig then <<let hyp2trig!*;
           p:=p+0;clearrules(hyp2trig!*);p:=behandle(p);>>;
        if first(modelist)=hyp then
        <<p:=behandle(p);let trig2hyp!*;p:=p+0;clearrules(trig2hyp!*)>>;
        if first(modelist)=expon then
          <<let trig2exp!*;p:=p+0;clearrules(trig2exp!*);>>;
      end;

      if first(directionlist)=expand then <<
                        % Handling of dependent variables
                        let trig_expand_addition!*;
                        p:= p;
                        u:= subs_symbolic_multiples(p);
                        let trig_expand_multiplication!*;
                        p:= part(u, 1);
                        p:= sub(part(u,2), p);
                        clearrules(trig_expand_addition!*);
                        clearrules(trig_expand_multiplication!*);
                >>;
      if first(directionlist)=combine then
      <<let trig_combine!*;p:=p+0;clearrules(trig_combine!*);
      if onlytan=1 and
      (keepalltriglist={keepalltrig}) then
      <<let subtan!*;p:=p+0;clearrules(subtan!*)>>;>>;
      clearrules(trig_normalize!*);
      if first(directionlist)=compact then begin
      % load compact;  % Loaded at beginning.
        let trig_expand!*;p:=p+0;clearrules(trig_expand!*);
        p:=compact(f,{sin(x)**2+cos(x)**2=1});
      end;
    end;
    return p;
  end;

procedure degree(p,x);
  begin
  scalar h;
  if p=0 then h:=inf else h:=deg(num(p),x)-deg(den(p),x);
  return h;
  end;

procedure balanced(p,x);
  if deg(num(p),x)=2*deg(den(p),x) then 1 else 0;

procedure coordinated(p,x);
  begin
    scalar k,pneu,e,o,j;
    k:={};
    e:=0;
    o:=0;
    pneu:=num(p);
    for j:=0:deg(pneu,x) do
      <<if coeffn(pneu,x,j) neq 0 then k:=j.k>>;
    for j:=1:length(k) do
      <<if fixp(part(k,j)/2) then e:=1 else o:=1>>;
     if o=e then return 0 else return 1;
  end;

procedure trig2ord(p,x,y);
  begin
    if balanced(p,x) neq 1 or balanced(p,y) neq 1 then write
    "error using trig2ord:
      polynomial not balanced.";
    if coordinated(p,x) neq 1 or coordinated(p,y) neq 1 then write
    "error using trig2ord:
      polynomial not coordinated";
    return sub(x=sqrt(x),y=sqrt(y),x**degree(p,x)*y**degree(p,y)*p);
  end;

procedure ord2trig(p,x,y);
  x**(-degree(p,x))*y**(-degree(p,y))*sub(x=x**2,y=y**2,p);

procedure factor_trig_poly(p,x,y);
  begin
    scalar j,p1,flist1,flist,d;
    p1:=trig2ord(p,x,y);
    d:=den(p1);
    flist1:= old_factorize(num(p1));
    flist:={};
    for j:=1:length(flist1) do
        flist:=ord2trig(part(flist1,j),x,y).flist;
    if d neq 1 then flist:=(1/d).flist;
    return flist;
  end;

procedure subpoly2trig(p,x);
  begin
    scalar r,d;
    d:=degree(den(p),x);
    r:=p*x**d;
    r:=sub(x=cos(x)+i*sin(x),r);
    r:=r*(cos(x)-i*sin(x))**d;
    return r;
  end;

procedure subpoly2hyp(p,x);
  begin
    scalar r,d;
    d:=degree(den(p),x);
    r:=p*x**d;
    r:=sub(x=cosh(x)+sinh(x),r);
    r:=r*(cosh(x)-sinh(x))**d;
    return r;
  end;

procedure varget(p);
  begin
    scalar q,l,h;
    q:= old_factorize(p);
    h:=0;
    for each l in q do
      <<if not(numberp(l)) then
        begin
          if (h=0) and length(l)=1  then h:=l else
          h:=1;
        end;
      >>;
    if h=0 then h:=1;
    return h;
  end;

procedure numberget(p);
  begin
    scalar q,d,l,h;
    q:= old_factorize(p);
    d:=1;
    h:=0;
    for each l in q do if numberp(l) then d:=d*l;
    return d;
  end;

procedure triggcd(p,q,x);
  begin
    scalar p1,q1,g1,g,u,d,nu,h,complex!*,err,l;
    on complex;
    nu:=numberget(x);
    err:=0;
    if varget(x)=1 then err:=1 else begin
      l:=trig_argumentlist(p);
      for d:=1:length(l) do if not(fixp(df(part(l,d),varget(x))/nu))
      and not(freeof(part(l,d),varget(x))) then err:=1;
      l:=trig_argumentlist(q);
      for d:=1:length(l) do if not(fixp(df(part(l,d),varget(x))/nu))
      and not(freeof(part(l,d),varget(x))) then err:=1;
    end;
    if err=0 then begin
      p1:=trigsimp1(p,{});
      p1:=sub(sin(varget(x))=sin(varget(x)/nu),
      cos(varget(x))=cos(varget(x)/nu),sinh(varget(x))
                    =sinh(varget(x)/nu),
      cosh(varget(x))=cosh(varget(x)/nu),p1);
      p1:=trigsimp1(p1,{});
      q1:=trigsimp1(q,{});
      q1:=sub(sin(varget(x))=sin(varget(x)/nu),
      cos(varget(x))=cos(varget(x)/nu),sinh(varget(x))
                    =sinh(varget(x)/nu),
      cosh(varget(x))=cosh(varget(x)/nu),q1);
      q1:=trigsimp1(q1,{});
      p1:=sub(sin(varget(x))=(xx_x-1/xx_x)/(2i),
      cos(varget(x)) =>xx_x/2+1/(2xx_x),
      sinh(varget(x))=(yy_y-1/yy_y)/2,
      cosh(varget(x))=yy_y/2+1/(2yy_y),p1);
      q1:=sub(sin(varget(x))=(xx_x-1/xx_x)/(2i),
      cos(varget(x)) =>xx_x/2+1/(2xx_x),
      sinh(varget(x))=(yy_y-1/yy_y)/2,
      cosh(varget(x))=yy_y/2+1/(2yy_y),q1);
      if balanced(p1,xx_x)+balanced(q1,xx_x)+coordinated(p1,xx_x)+
      coordinated(q2,xx_x)+balanced(p1,yy_y)+balanced(q1,yy_y)+
      coordinated(p1,yy_y)+coordinated(q2,yy_y) neq 8
      then d:=1
      else begin
        p1:=trig2ord(p1,xx_x,yy_y);
        q1:=trig2ord(q1,xx_x,yy_y);
        g1:=gcd(num(p1),num(q1));
        g:=ord2trig(g1,xx_x,yy_y)/lcm(den(p1),den(p2));
        h:=subpoly2trig(g,xx_x);
        h:=subpoly2hyp(h,yy_y);
        h:=sub(xx_x=varget(x)*nu,yy_y=varget(x)*nu,h);
        h:=trigsimp1(h,{});
        h:= old_factorize(num(h));
        d:=1;
        for each r in h do if not(numberp(r)) then d:=d*r;
      end;
    end else d:="error using triggcd, basis not possible.";
    return d;
  end;

procedure trigfactorize(p,x);
  begin
    scalar l,q,f,r,d,h,s,u,err,complex!*;
    on complex;
    nu:=numberget(x);
    err:=0;
    if varget(x)=1 then err:=1 else begin
      l:=trig_argumentlist(p);
      for d:=1:length(l) do if not(fixp(df(part(l,d),varget(x))/nu))
      and not(freeof(part(l,d),varget(x))) then err:=1;
    end;
    if err=1
      then rederr("error using trigfactorize, basis not possible")
    else begin
      q:=trigsimp1(p,{});
      q:=sub(sin(varget(x))=sin(varget(x)/nu),
      cos(varget(x))=cos(varget(x)/nu),sinh(varget(x))
                    =sinh(varget(x)/nu),
      cosh(varget(x))=cosh(varget(x)/nu),q);
      q:=trigsimp1(q,{});
      q:=sub(sin(varget(x))=(xx_x-1/xx_x)/(2i),
      cos(varget(x)) =xx_x/2+1/(2xx_x),
      sinh(varget(x))=(yy_y-1/yy_y)/2,cosh(varget(x))
                     =yy_y/2+1/(2yy_y),q);
      if balanced(q,xx_x)+coordinated(q,xx_x)+
      balanced(q,yy_y)+coordinated(q,yy_y)<4 then f:={p}
      else begin
        q:=factor_trig_poly(q,xx_x,yy_y);
        f:={};
        d:=1;
        for each r in q do
        <<h:=subpoly2trig(r,xx_x);
          h:=subpoly2hyp(h,yy_y);
          h:=sub(xx_x=varget(x)*nu,yy_y=varget(x)*nu,h);
          h:=trigsimp1(h,{});
          if freeof(h,varget(x)) then d:=d*h else begin
            for each u in old_factorize(h) do
            <<if freeof(u,varget(x)) then begin
              d:=d*u;
              h:=h/u;
            end;
          >>;
          f:=reverse(h.reverse(f));
          end;
        >>;
        if d neq 1 then f:=d.f;
      end;
    end;
    return f;
  end;

symbolic procedure trigsimp!*(f);
begin scalar fff;
  fff :=  reval car f;
  return
  if eqcar (fff,'list) then
    'list . for each ff in cdr fff collect
       trigsimp1(ff,'list.for each w in cdr f collect reval w)
  else
  trigsimp1(reval car f,'list.for each w in cdr f collect reval w);
 end;

symbolic put('trigsimp,'psopfn, 'trigsimp!*);

endmodule;

end;

