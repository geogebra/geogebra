module chebysh;  % Chebyshev approximation.
   % Literature: Press, Flannery, Teukolski, Vetterling: Numerical
   %                Recipes, Cambridge University Press.

% Author: Herbert Melenk

% Copyright (c) 1993 ZIB Berlin, RAND.  All rights reserved.

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


put('chebyshev_fit,'psopfn,'(lambda(u)(chebysheveval u 'fit)));
put('chebyshev_eval,'psopfn,'(lambda(u)(chebysheveval u 'eval)));
put('chebyshev_int,'psopfn,'(lambda(u)(chebysheveval u 'int)));
put('chebyshev_df,'psopfn,'(lambda(u)(chebysheveval u 'df)));

symbolic procedure chebysheveval(u,mode);
     % interface function;
  begin scalar w,x,c,e,y,r,oldmode;
        integer n;
    u := for each x in u collect reval x;
    u := accuracycontrol(u,3,20);
    e :=car u; u :=cdr u;
      % variable and interval.
    w := car u;
    if not eqcar(w,'equal) then typerr(w,"interval bounds");
    oldmode:=switch!-mode!-rd nil;
    y:=revalnuminterval(caddr w,t);
    x:=cadr w;
    if mode = 'fit then
    <<
       n:=if cdr u then ieval cadr u else 20;
       c := chebcoeffs(e,x,car y,cadr y,n);
       r:= {'list,
            chebpol(c,x,car y,cadr y),
            'list . c}; %  for each q in c collect prepf q};
    >>
    else if mode = 'eval then
    <<
       if null cdr u or not eqcar(cadr u,'equal) then
          rederr("Chebyshev_eval: point missing");
       e:=cdr listeval(e,t);
       w:=numr simp caddr cadr u;
       r:= prepf chebeval(e,x,car y,cadr y,w);
    >>
   else if mode = 'int or mode = 'df then
    <<
       e:=cdr listeval(e,t);
       r:= if mode ='int then
                chebint(e,x,car y,cadr y)
           else chebdf (e,x,car y,cadr y);
       r:='list . for each q in r collect prepf q;
    >>;
    switch!-mode!-rd oldmode;
    return r;
  end;

symbolic procedure chebcoeffs(func,x,a,b,n);
  begin
        integer k,j,n1;
        scalar fac,bpa,bma,f,!1pi,c,y,su,nn,half,w;

    x:={x};
    !1PI := rdpi!*();
    nn := dm!:(1/n);
    n1 := n-1;
    half := dm!:(1/2);

     dm!: <<bma:=(b-a)/2; bpa:=(b+a)/2>>;
     w := dm!:(!1PI*nn);
     f:=for k:=0:n1 collect
     <<y := dm!: (rdcos!*(w*(k+half))*bma+bpa);
           evaluate(func,x,{y})
     >>;

     dm!: <<fac:=2*nn>>;
     c:=for j:=0:n1 collect
     <<
        w:= dm!:(!1PI*j*nn);
        su:=nil;
        for k:=0:n1 do
          dm!:(su := su+nth(f,iadd1 k) *rdcos!*(w*(k+half)));
        dm!: (fac*su)
     >>;
    return c;
  end;

symbolic procedure chebpol(c,x,a,b);
  begin scalar d,dd,sv,fac,cnst;
        integer n;
    n:=length c;
    d:=for i:=1:n collect nil;
    dd:=for i:=1:n collect nil;
    car dd := nth(c,n);
    for j:=(n-2) step -1 until 1 do
    <<
      for k:=(n-j) step -1 until 1 do
       << sv := nth(d,k+1);
          nth(d,k+1) := dm!:(2*nth(d,k) - nth(dd,add1 k));
          nth(dd,k+1) := sv
       >>;
      sv:=car d;
      car d := dm!:(- car dd + nth(c,add1 j));
      car dd := sv;
    >>;
    for j:=(n-1) step -1 until 1 do
       nth(d,j+1) := dm!:(nth(d,j) - nth(dd,add1 j));
    car d := dm!:(-car dd + car c/2);

    cnst:=dm!:(2/(b-a));
    fac :=cnst;
    for j:=1:(n-1) do
     <<nth(d,add1 j) := dm!:(nth(d,add1 j) * fac);
       fac := dm!:(fac*cnst);
     >>;
    cnst:=dm!:((a+b)/2);
    for j:=0:(n-2) do
      for k:=(n-2) step -1 until j do
        nth(d,add1 k) := dm!:(nth(d,add1 k) - cnst*nth(d,add1 add1 k));

    return reval ('plus. for i:=1:n collect
      {'times,nth(d,i),{'expt,x,i-1}});
  end;

symbolic procedure chebeval(c,xx,a,b,x);
  begin scalar d,dd,y,y2,sv;
        integer m;
    xx := nil;
    c:=for each q in c collect numr simp q;
    m:=length c;
    y2 := dm!:(2*(y:=(2*x-a-b)/(b-a)));
    for j:=(m-1) step -1 until 1 do
    << sv:=d;
       d:= dm!:(y2*d-dd+nth(c,add1 j));
       dd := sv;
    >>;
    return dm!:(y*d-dd + car c/2);
  end;

symbolic procedure chebint(c,xx,a,b);
  begin scalar su,fac,con,cint,w;
        integer n,jj;
    xx := nil;
    n := length c;
    c:=for each q in c collect numr simp q;
    fac := 1;
    con := dm!:((b-a)/4);
    cint := for j:=1:(n-2) collect
    << jj:=j+2;
       dm!:(w:= con*(nth(c,j)-nth(c,jj))/j );
       dm!:(su := su + fac*w);
       dm!:(fac := -fac);
       w
    >>;
    cint := append(cint,{dm!: (con*nth(c,sub1 n)/(n-1))});
    dm!:( su := su+fac*nth(c,n));
    cint := (dm!:(su+su)) . cint;
    return cint;
  end;

symbolic procedure chebdf(c,xx,a,b);
  begin scalar con,cder;
        integer n,jj;
    xx := nil;
    n := length c;
    c:=for each q in c collect numr simp q;
    cder := for i:=1:n collect nil;

    nth(cder,n-1) := dm!:(2*(n-1)*nth(c,n));
    for j:=(n-3) step -1 until 0 do
    <<jj:=j+3;
      nth(cder,j+1):=
       dm!:(nth(cder,jj)+2*(j+1)*nth(c,add1 add1 j));
    >>;

    con := dm!:(2/(b-a));

    return for each q in cder collect dm!:(con * q);
  end;

endmodule;

end;
