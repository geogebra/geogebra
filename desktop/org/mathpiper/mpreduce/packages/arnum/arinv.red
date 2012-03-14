module arinv;   % Routines for inversion of algebraic numbers.

% Author: Eberhard Schruefer.

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


fluid '(dmode!*);

global '(arbase!* curdefpol!*);

symbolic procedure arquot(u,v);
   % U is an ar domain element, result is a matrix form which
   % needs to be inverted for calculating the inverse of ar.
   begin scalar mv,r,sgn,x,y,z,w,dmode!*;
         integer n;
     mv := mvar curdefpol!*;
     x := u;
     w := for each k in cdr arbase!* collect
             (x := reducepowers multf(!*k2f mv,x));
     x := nil;
     w := negf v . (u . w);
     for j := (ldeg curdefpol!* - 1) step -1 until 0 do
       <<y := nil;
         z := 1;
         n := -2;
         w := for each k in w collect
                if (degr(k,mv) = j) and k then
                   <<y := list(n := n+1)
                          . * (r := if domainp k then k else lc k)
                          .+ y;
                     if eqcar(r,'!:rn!:) then z := ilcm(z,cddr r);
                     if null domainp k then red k>>
                 else <<n := n + 1; k>>;
         if z neq 1 then
            y := for each j on y collect
                     lpow j .* if eqcar(lc j,'!:rn!:) then
                                  cadr lc j*z/cddr lc j
                                else lc j*z;
         if null x then x := y
          else x := b!:extmult(y,x)>>;
     sgn := evenp length lpow x;
     z := nil;
     for each j in lpow x do
         z := addf(if j = 0 then arnum!-mkglsol(0,x,sgn := not sgn,-1)
                    else multpf(mv to j,
                                arnum!-mkglsol(j,x,sgn:=not sgn,-1)),z);
     return z
   end;

symbolic procedure arquot1 u;
   % U is an ar domain element, result is a matrix form which
   % needs to be inverted for calculating the inverse of ar.
   begin scalar mv,r,sgn,x,y,z,w,dmode!*;
         integer n;
     mv := mvar curdefpol!*;
     x := u;
     w := for each k in cdr arbase!* collect
             (x := reducepowers multf(!*k2f mv,x));
     x := nil;
     w := -1 . (u . w);
     for j := (ldeg curdefpol!* - 1) step -1 until 0 do
       <<y := nil;
         z := 1;
         n := -2;
         w := for each k in w collect
                if (degr(k,mv) = j) and k then
                   <<y := list(n := n+1)
                          . * (r := if domainp k then k else lc k)
                          .+ y;
                     if eqcar(r,'!:rn!:) then z := ilcm(z,cddr r);
                     if null domainp k then red k>>
                 else <<n := n + 1; k>>;
         if z neq 1 then
            y := for each j on y collect
                     lpow j .* if eqcar(lc j,'!:rn!:) then
                                  cadr lc j*z/cddr lc j
                                else lc j*z;
         if null x then x := y
          else x := b!:extmult(y,x)>>;
     sgn := evenp length lpow x;
     z := nil;
     for each j in lpow x do
         z := addf(if j = 0 then arnum!-mkglsol(0,x,sgn := not sgn,-1)
                    else multpf(mv to j,
                             arnum!-mkglsol(j,x,sgn := not sgn,-1)),z);
     return z
   end;

symbolic procedure arinv u;
   % Sort of half-extended gcd. No B-technique applied yet.
   % Performance is pretty bad.
   begin scalar mv,sgn,x,z,v,dmode!*;
         integer k,m,n;
     m := ldeg curdefpol!*;
     n := ldeg u;
     v := curdefpol!*;
     mv := mvar curdefpol!*;
     x := list(m-1) .* lc u .+ (list(-1) .* lc v .+ nil);
     for j := 2:(n+m) do
       begin scalar y;
         if j=(n+m) then y := list(-n-1) .* -1 .+ nil
          else nil;
         if (n + m - j - degr(v,mv) + 1) = 0 then v := red v;
         if (n + m - j - degr(u,mv) + 1) = 0 then u := red u;
             z := u;
         a:  if z and ((k := m - j + n - degr(z,mv))<m) then
               y := list k .* (if domainp z then z else lc z) .+ y
              else go to b;
             if null domainp z then z := red z
              else z := nil;
             go to a;
         b:  z := v;
         c:  if z and ((k := - j + m - degr(z,mv))<0) then
               y := list k .* (if domainp z then z else lc z) .+ y
              else go to d;
             if null domainp z then z := red z
              else z := nil;
             go to c;
         d:  x := b!:extmult(y,x)
       end;
     sgn := evenp length lpow x;
     z := nil;
     for each j in lpow x do
       if j > -1 then
         z := addf(if j = 0 then arnum!-mkglsol(0,x,sgn := not sgn,-n-1)
                    else multpf(mv to j,
                             arnum!-mkglsol(j,x,sgn:=not sgn,-n-1)),z);
     return z
   end;

symbolic procedure arnum!-mkglsol(u,v,sgn,n);
   begin scalar s,x,y,dmode!*;
     dmode!* := '!:rn!:;
     y := lpow v;
     for each j on red v do
       if s := arnum!-glsolterm(u,y,j,n)
          then x := s;
     return int!-equiv!-chk mkrn(if sgn then -x else x,lc v)
   end;

symbolic procedure arnum!-glsolterm(u,v,w,n);
   begin scalar x,y,sgn;
     x := lpow w;
     a: if null x then return
           if car y = n then lc w;
        if car x = u then return nil
         else if car x member v then <<x := cdr x;
                                     if y then sgn := not sgn>>
         else if y then return nil
               else <<y := list car x; x := cdr x>>;
        go to a
   end;

endmodule;

end;
