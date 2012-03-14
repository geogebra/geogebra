module plotimp3; % Implicit plot: compute the varity {x,y,z|f(x,y,z)=0}.

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


% data structure: cubes.

symbolic procedure ploteval3impl(x,y,z);
  begin scalar rx,ry,rz,f,fcn;
     rx:=plotrange(x,
      reval(plot_xrange or '(!*interval!* -10 10)));
     ry:=plotrange(y,
      reval(plot_yrange or '(!*interval!* -10 10)));
     rz:=plotrange(z,
      reval(plot_zrange or '(!*interval!* -10 10)));
     fcn := car reverse plotfunctions!*;
     f:= ploteval3impl1(cdar plotfunctions!*,
         x,car rx,cadr rx,
         y,car ry,cadr ry,
         z,car rz,cadr rz);
     plotdriver(plot!-3exp!-reg,x,y,z,f);
  end;

symbolic procedure ploteval3impl1(f,x,xlo,xhi,y,ylo,yhi,z,zlo,zhi);
   begin scalar u,dx,dy,dz,xx,yy,zz,l,ff,pts,val,w,q,qq,pt,found,done;
        integer nx,ny,nz;
     ff := rdwrap f;
     xlo:=rdwrap xlo; xhi:=rdwrap xhi;
     ylo:=rdwrap ylo; yhi:=rdwrap yhi;
     dx:=float(xhi-xlo)/float(nx:=plot!-points(x));
     dy:=float(yhi-ylo)/float(ny:=plot!-points(y));
     dz:=float(zhi-zlo)/float(nz:=plot!-points(z));
     pts := mk!-p!-array3(nx,ny,nz);
     val:= mk!-p!-array3(nx,ny,nz);

        % Step 1: compute a complete grid in 3d.
     for i:=0:nx do
     <<
       xx:=(xlo+i*dx);
       for j:=0:ny do
       <<
        yy:=(ylo+j*dy);
        for k:=0:nz do
        <<
          zz:=(zlo+k*dz);
          p!-put3(pts,i,j,k,{xx,yy,zz});
          u:=plotevalform(ff,f,{x . xx,y . yy,z . zz});
          if eqcar(u,'overflow) then u:=1.0;
          p!-put3(val,i,j,k,u);
        >>;
       >>
     >>;

        % Step 2: extract zero points.
   done := t;
   while done do
   <<done := nil; w:=
     for i:=0:nx #-1 collect
      for j:=0:ny #-1 collect
      <<q:=nil; found:=nil;
       pt := p!-get3(pts,i,j,1);
       for k:=nz step -1 until 0 do
        if null found then
        <<if null q then q:=p!-get3(val,i,j,k);
          qq:=p!-get3(val,i,j,k);
          if q and qq and q*qq <= 0.0 then
            found := if q=0.0 then caddr p!-get3(pts,i,j,k)
                else ploteval3impl3(caddr p!-get3(pts,i,j,k),qq,
                                    caddr p!-get3(pts,i,j,k#+1),q);
          if q=0.0 or qq=0.0 or not found then p!-put3(val,i,j,k,nil);
          done:=done or found;
          q:=qq;
        >>;
        {t,t,car pt,cadr pt,found}
       >>;
     if done then l:=w.l;
    >>;
    return ploteval3xy3 l;
   end;

symbolic procedure ploteval3impl3(p1,f1,p2,f2);
  % linear interpolation
  <<
    fdeclare(f1,f2,p1,p2);
    (f1*p2 - f2*p1)/(f1-f2)>>;

endmodule;

end;
