module plotexp3; % Computing surface z=f(x,y) with regular grid.

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


% A rectangular grid is encoded as list of rows.
% A row is a list of points.
% A point is a list {v,h,x,y,z} where
%   z,y are the coordinates and z is the function value.
% The boolean values v ("vertical" = y direction) and
%    h ("horizontal" = x direction) are true,
%   if the connection to the neighbour point in that
%   direction is valid, nil if the connection is broken.

symbolic procedure ploteval3xy(x,y,z);
  begin scalar rx,ry,rz,f,fcn;
     rx:=plotrange(x,
      reval(plot_xrange or '(!*interval!* -10 10)));
     ry:=plotrange(y,
      reval(plot_yrange or '(!*interval!* -10 10)));
     rz:=plotrange(z, reval(plot_zrange or nil));
     fcn := car reverse plotfunctions!*;
     if z='implicit then
       return ploteval2xyimpl(rx,ry,fcn,x,y);
     f:= if eqcar(fcn,'points) then caddr fcn else
        ploteval3xy1(cdar plotfunctions!*,
         z,if rz then car rz, if rz then cadr rz,
         x,car rx,cadr rx,
         y,car ry,cadr ry);
     plotdriver(plot!-3exp!-reg,x,y,z,f);
  end;

symbolic procedure ploteval3xy1(f,z,zlo,zhi,x,xlo,xhi,y,ylo,yhi);
   begin scalar l,ff,r,w;
%        scalar x1,x2,y1,y2,xx,yy,p1,p2,p3,p4,f1,f2,f3,f4;
        integer nx,ny;
     z := nil;
     ff := rdwrap f;
     xlo:=rdwrap xlo; xhi:=rdwrap xhi;
     ylo:=rdwrap ylo; yhi:=rdwrap yhi;
     nx:=plot!-points(x); ny:=plot!-points(y);
       % compute the basic grid.
     r:=ploteval3xy1pts(f,ff,z,zlo,zhi,x,xlo,xhi,nx,y,ylo,yhi,ny);
     l:=cdr r;
     w:=car r;
     r:={l};
        % create refined environments for the bad points
     for each q in w do
      r:=cdr
       ploteval3xy1pts(f,ff,z,zlo,zhi,x,car q,cadr q,4,y,caddr q,
                       cadddr q,4)
         .r;
%       % look for singularities or points of big variation
     return ploteval3xy3 r;
   end;

symbolic procedure ploteval3xy1pts
          (f,ff,z,zlo,zhi,x,xlo,xhi,nx,y,ylo,yhi,ny);
  % Compute orthogonal graph over ordinary grid. Return additionally
  % a list of inner rectangles with singular points.
   begin scalar u,dx,dy,xx,yy,l,w;
     z := nil;
     dx:=float(xhi-xlo)/float(nx);
     dy:=float(yhi-ylo)/float(ny);
     l:=
     for j:=0:nx collect
     <<
       for i:=0:ny collect
       <<
        xx:=(xlo+i*dx); yy:=(ylo+j*dy);
        u:=plotevalform(ff,f,{x . xx,y . yy});
        if null u then  % look for an isolated singularity WN
          u:=ploteval3xysingular(ff,f,x,xx,dx,y,yy,dy,zhi,zlo);

        if null u or eqcar(u,'overflow)
         or numberp u and
             (zhi and u>zhi or zlo and u<zlo) then
        <<u:=nil;
          if 0<j and j<nx and 0<i and i<ny then
             w:={xx-dx,xx+dx,yy-dy,yy+dy}.w;
        >>;
        {t,t,xx,yy,u}
       >>
     >>;
     return w.l;
   end;

symbolic procedure ploteval3xy2 l;
    ploteval3xy3 {l};

symbolic procedure ploteval3xy3 ll;
  % Decompose ll into maximal regular grids.
 begin scalar l,lr,c,w,y,r,nw;
       integer n,m;
   while ll do
   <<l:=car ll; ll:=cdr ll;
       % find stripe with maximal lower left rectangle.
     w:={car l,cadr l}; l:=cdr l;
     n:=min(ploteval3xybkpt(car w,0,nil),     % hor. only
            ploteval3xybkpt(cadr w,0,t));     %  hor. and vert
     c := t;
     while c and l and cdr l do
     <<m:=ploteval3xybkpt(cadr l,0,t);
       if izerop n and izerop m or n #>0 and not(n #> m) then
          <<l:= cdr l; w:=nconc(w,{car l})>>
       else c:=nil;
     >>;
     if cdr l then ll:=l . ll;
       % cut off subnet
     l:=nil; r:=nil; nw:=nil;
     for each row in w do
     <<if izerop n then row := cdr row
        else
       r:=(for i:=1:n collect <<y:=cddar row; row:=cdr row; y>>).r;
       if row then nw:=row.nw;
     >>;
     nw:= reversip nw;
        %print n;print{"streifen",length w,cddr caar w,
        %      cddr lastcar lastcar w};
        %print "gut";print r;print "rest";print nw;
        %if yesp "kill" then rederr "schluss";
     if nw then ll:=nw.ll;
     if r and cdr r then lr:=r.lr;
    >>;
    return lr;
  end;

symbolic procedure ploteval3xybkpt(w,n,m);
  % m=t: look for horizontal and vertical interrupts. Otherwise
  % look only for horizontal interrupts.
   if null w then n else
   if nil memq cddar w then n    % undefined point
    else
   if null cadar w               % x - break.
        or (m and null caar w)   % y - break
      then n+1 else
   ploteval3xybkpt(cdr w,n#+1,m);

endmodule;

end;
