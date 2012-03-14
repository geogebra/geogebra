module makefour;

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


%% User interface; all rather iffy at present

symbolic procedure harmonicp u; get(u, 'fourier!-angle);

symbolic procedure harmonic u;
<<
    for each x in u do if not(get(x, 'fourier!-angle)) then <<
        if (next!-angle!* > 7) then rerror(fourier,3,"Too many angles");
        put(x, 'fourier!-angle, next!-angle!*);
        putv!.unsafe(fourier!-name!*, next!-angle!*, x);
        next!-angle!* := next!-angle!* #+ 1;
      >>
>>;

put('harmonic, 'stat, 'rlis);

symbolic procedure simpfourier u;
%% Handle the form fourier(...)  with treating sin and cos as special
begin
  if not(length u = 1) then
        rerror(fourier,1,"Argument should be single expression");
  return simpfourier1 prepsq simp!* car u;;
end;

symbolic procedure simpfourier1 u;
begin scalar ff;
  if atom u then <<
        if harmonicp u
          then rerror(fourier,2,"Secular angle not allowed");
        return (!*sq2fourier simp u) . 1;
  >>
  else if eqcar(u, '!:fs!:) then return u
  else if (ff := get(car u, 'simpfour)) then return apply1(ff, cdr u)
  else <<
    rerror(fourier,4,"Unknown function" . car u);
    return (!*sq2fourier u) . 1;
  >>
end;

put('fourier, 'simpfn, 'simpfourier);

symbolic procedure simpfouriersin u;
% Creation of a simple angle expression and function
begin scalar ans, vv;
  u := car u;
  if atom u then
        if harmonicp u then <<
              ans:=mkvect 3;
              fs!:set!-coeff(ans,(1 . 1));
              fs!:set!-fn(ans,'sin);
              vv := mkvect 7;
              for i:=0:7 do putv!.unsafe(vv,i,0);
              putv!.unsafe(vv, get(u, 'fourier!-angle), 1);
              fs!:set!-angle(ans,vv);
              fs!:set!-next(ans,nil);
              return (get('fourier,'tag) . ans) . 1 >>
        else return !*sq2fourier(simp list('sin, u)) . 1;
  if angle!-expression!-p u then <<
      ans:=mkvect 3;
      fs!:set!-coeff(ans,(1 . 1));
      fs!:set!-fn(ans,'sin);
      vv := mkvect 7;
      for i:=0:7 do putv!.unsafe(vv,i,0);
      compile!-angle!-expression(u,vv);
      fs!:set!-angle(ans,vv);
      fs!:set!-next(ans,nil);
      return (get('fourier,'tag) . ans) . 1 >>;
  rerror(fourier,99,"Not finished yet");
end;

put('sin, 'simpfour, 'simpfouriersin);

symbolic procedure simpfouriercos u;
% Creation of a simple angle expression and function
begin scalar ans, vv;
  u := car u;
  if atom u then
        if harmonicp u then <<
              ans:=mkvect 3;
              fs!:set!-coeff(ans,(1 . 1));
              fs!:set!-fn(ans,'cos);
              vv := mkvect 7;
              for i:=0:7 do putv!.unsafe(vv,i,0);
              putv!.unsafe(vv, get(u, 'fourier!-angle), 1);
              fs!:set!-angle(ans,vv);
              fs!:set!-next(ans,nil);
              return (get('fourier,'tag) . ans) . 1 >>
        else return !*sq2fourier(simp list('cos, u)) . 1;
  if angle!-expression!-p u then <<
      ans:=mkvect 3;
      fs!:set!-coeff(ans,(1 . 1));
      fs!:set!-fn(ans,'cos);
      vv := mkvect 7;
      for i:=0:7 do putv!.unsafe(vv,i,0);
      compile!-angle!-expression(u,vv);
      fs!:set!-angle(ans,vv);
      fs!:set!-next(ans,nil);
      return (get('fourier,'tag) . ans) . 1 >>;
  rerror(fourier,99,"Not finished yet");
end;

put('cos, 'simpfour, 'simpfouriercos);

%% Is the prefix expression u a sum of angles??

symbolic procedure angle!-expression!-p u;
if atom u and harmonicp u then t
else if eqcar(u,'plus) or eqcar(u,'difference) then
    angle!-expression!-p cadr u and angle!-expression!-p caddr u
else if eqcar(u,'minus) then angle!-expression!-p cadr u
else if eqcar(u,'times) then
  if numberp cadr u then angle!-expression!-p caddr u
  else angle!-expression!-p cadr u and numberp caddr u
else nil;

%% We know that u is a sum of angles, so create vector of coefficients.

symbolic procedure compile!-angle!-expression(u,v);
if atom u and harmonicp u then
    putv!.unsafe(v, get(u, 'fourier!-angle),
                 1+getv!.unsafe(v, get(u, 'fourier!-angle)))
else if eqcar(u,'plus) then <<
        u := cdr u;
        while u do <<
            compile!-angle!-expression(car u,v);
            u := cdr u
        >>;
        v  >>
else if eqcar(u,'difference) then begin scalar vv;
    compile!-angle!-expression(cadr u,v);
    vv := mkvect 7;
    for i:=0:7 do putv!.unsafe(vv,i,0);
    compile!-angle!-expression(caddr u,vv);
    for i:=0:7 do putv!.unsafe(v,i,getv!.unsafe(v,i)
                                   - getv!.unsafe(vv,i));
    return v
  end
else if eqcar(u,'minus) then
  begin scalar vv;
        vv := mkvect 7;
        for i:=0:7 do putv!.unsafe(vv,i,0);
        compile!-angle!-expression(cadr u,vv);
        for i:=0:7 do putv!.unsafe(v,i,getv!.unsafe(v,i)
                                       - getv!.unsafe(vv,i));
        return v;
  end
else if eqcar(u,'times) then
  if numberp cadr u then begin scalar vv;
      vv := mkvect 7;
      for i:=0:7 do putv!.unsafe(vv,i,0);
      compile!-angle!-expression(caddr u,vv);
      for i:=0:7 do putv!.unsafe(v, i,
                        cadr u*getv!.unsafe(vv, i) + getv!.unsafe(v,i))
  end
  else begin scalar vv;
      vv := mkvect 7;
      for i:=0:7 do putv!.unsafe(vv,i,0);
      compile!-angle!-expression(cadr u,vv);
      for i:=0:7 do putv!.unsafe(v, i,
                      caddr u * getv!.unsafe(vv, i) + getv!.unsafe(v,i))
  end
else nil;

symbolic procedure simpfouriertimes(u);
begin scalar z;
     z := car simpfourier1 car u;
     u := cdr u;
  a: if null u then return z ./ 1;
     z := fs!:times!:(car simpfourier1 car u,z);
     u := cdr u;
     go to a
   end;

put('times, 'simpfour, 'simpfouriertimes);

symbolic procedure simpfourierexpt(u);
  fs!:expt!:(car simpfourier1 car u, cadr u) . 1;

put('expt, 'simpfour, 'simpfourierexpt);

symbolic procedure simpfourierplus(u);
begin scalar z;
     z := car simpfourier1 car u;
     u := cdr u;
  a: if null u then return z ./ 1;
     z := fs!:plus!:(car simpfourier1 car u,z);
     u := cdr u;
     go to a
   end;

put('plus, 'simpfour, 'simpfourierplus);

symbolic procedure simpfourierdifference(u);
  fs!:difference!:(car simpfourier1 car u, car simpfourier1 cadr u)
     ./ 1;

put('difference, 'simpfour, 'simpfourierdifference);

symbolic procedure simpfourierminus(u);
  fs!:negate!:(car simpfourier1 car u) . 1;

put('minus, 'simpfour, 'simpfourierminus);

symbolic procedure simpfourierquot(u);
begin scalar v;
  v := simp!* cadr u;
  v := cdr v . car v;
  return fs!:times!:(car simpfourier1 car u, !*sq2fourier v) ./ 1
end;

put('quotient, 'simpfour, 'simpfourierquot);

symbolic procedure simphsin u;
begin
  if not(length u = 1) then
        rerror(fourier,5,"Argument should be single expression");
  return simpfouriersin list(u := prepsq simp!* car u)
end;

put('hsin, 'simpfn, 'simphsin);

symbolic procedure simphcos u;
begin
  if not(length u = 1) then
        rerror(fourier,6,"Argument should be single expression");
  return simpfouriercos list(u := prepsq simp!* car u)
end;

put('hcos, 'simpfn, 'simphcos);

endmodule;

end;
