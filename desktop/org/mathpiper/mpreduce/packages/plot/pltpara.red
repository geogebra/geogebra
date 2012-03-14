module pltpara; % Computing parametric curve.
                 % (x,y) = (x(t),y(t))
                 %     or
                 % (x,y,z) = (x(t),y(t),z(t))
                 %     or
                 % (x,y,z) = (x(t,u),y(t,u),z(t,u))

% Author: Herbert Melenk, ZIB Berlin.

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


symbolic procedure plotevalpara1(x);
  begin scalar xlo,xhi,ylo,yhi,rx,ry,fcn,fcns,pts;
     rx:=plotrange(x,
       reval(plot_xrange or '(!*interval!* -10 10)));
     xlo:=car rx;
     xhi:=cadr rx;
     fcns:= reverse plotfunctions!*;
     % ry:=plotrange(y, reval(plot_yrange or nil));
     if ry then <<ylo:=car ry; yhi:=cadr ry>>;
     while fcns do
     <<fcn := cddar fcns; fcns := cdr fcns;
       pts:=plotevalpara11(fcn,x,xlo,xhi).pts;
     >>;
     if length fcn=2 then  plotdriver(plot!-2exp,'x,'y,list pts,nil)
        else plotdriver(plot!-3exp!-reg,'x,'y,'z,list pts) % WN list added
   end;

symbolic procedure plotevalpara11(fm,x,xlo,xhi);
   begin scalar plotsynerr!*,l,d,d0,u,v,p,fl;
        scalar plotderiv!*;
        integer nx;
    fl:= for each f in fm collect rdwrap f.f;
    p:=float (nx:=plot!-points(x));
    d:=(d0:=(xhi-xlo))/p;
    v:=xlo;
    for i:=0:nx  do
     <<u:= for each f in fl collect plotevalform(car f,cdr f,{x.v});
       if plotsynerr!* then typerr(fm,"function to plot");
       if smemq('overflow,u) then u:=nil;
       l:=u.l;
       v:=v+d;
     >>;
    return reversip l;
   end;

symbolic procedure plotevalpara2(x,y);
  begin scalar xlo,xhi,ylo,yhi,rx,ry,fcn,fcns,pts;
     rx:=plotrange(x,
       reval(plot_xrange or '(!*interval!* -10 10)));
     xlo:=car rx; xhi:=cadr rx;
     fcns:= reverse plotfunctions!*;
     ry:=plotrange(y,
       reval(plot_yrange or '(!*interval!* -10 10)));
     ylo:=car ry; yhi:=cadr ry;
     fcn := cddar fcns; fcns := cdr fcns;
     if length fcn neq 3 then typerr(cdar fcns,"function to plot");
     pts:=plotevalpara21(fcn,x,xlo,xhi,y,ylo,yhi);
     plotdriver(plot!-3exp!-reg,'x,'y,'z,list pts)  % WN list added
   end;

symbolic procedure plotevalpara21(fm,x,xlo,xhi,y,ylo,yhi);
   begin scalar plotsynerr!*,l,ll,dx,dy,u,v,p,fl,w;
        scalar plotderiv!*;
        integer nx,ny;
    fl:= for each f in fm collect rdwrap f.f;
    p:=float(nx:=plot!-points(x));
    dx:=(xhi-xlo)/p;
    p:=float(ny:=plot!-points(y));
    dy:=(yhi-ylo)/p;
    v:=xlo;
    for i:=0:nx  do
     <<w:= ylo; l:=nil;
       for j:=0:ny do
       <<u:= for each f in fl collect
              plotevalform(car f,cdr f,{x.v,y.w});
         if plotsynerr!* then typerr(fm,"function to plot");
         if smemq('overflow,u) then u:=nil;
         l:=u.l;
         w:=w+dy
       >>;
       v:=v+dx;
       ll:=l.ll;
     >>;
    return ll;
   end;

endmodule;

end;
