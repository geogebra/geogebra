load_package redlog;
rlset pasf;
off rlverbose;

% Information Flow Control
sec := (a < b and cong(a+b,0,2) and 2*n = a+b and
      (a < b and b-a = n or a >= b and a-b = n)) or
   (a < b and ncong(a+b,0,2) and 2*n = a+b+1 and
      (a < b and b-a = n or a >= b and a-b = n))$

rlqe rlex sec;

rlqea rlex sec;

rlqe ex(n,sec);

% Information Flow Control, Nonlinear Variant
phi := ex(n,
   (a < b and cong(a+b,0,2) and 2*n = a+b and
      ((a<b and b-a=n^2) or (a >= b and a-b = n^2))) or
   (a < b and ncong(a+b,0,2) and 2*n = a+b+1 and
      ((a < b and b-a = n^2) or (a >= b and a-b = n^2))))$

rlwqe phi;

% Integer Roots
phi := ex(x,x^5-3x^2+1 = 0 and 3x >= 1 and x <= 3)$

rlwqe phi;

% Integer Roots of Generic Polynomial
phi := ex(x,a*x^2+b*x+c=0)$

rlwqe phi;

% Feasibility of Parametric Integer Constraints
las := ex(x,a*x>=b and c*x<=d)$

rlwqe las;

procedure t1(m);
   rlsimpl ex(for i:=1:m collect mkid(x,i),
      (for i:=1:m sum mkid(x,i))=a and
      for i:=1:m mkand mkid(x,i)>=0);

procedure t2(m);
   rlsimpl ex(for i:=1:m join for j:=1:m collect mkid(mkid(x,i),j),
      for i:=1:m mkand (for j:=1:m sum mkid(mkid(x,i),j))=mkid(a,i) and
      for j:=1:m mkand (for i:=1:m sum mkid(mkid(x,i),j))=mkid(b,j) and
      for i:=1:m mkand for j:=1:m mkand mkid(mkid(x,i),j)>=0);

% We compute $T_{1,8}$. In the literature we have treated instances
% with in the range t1(5), ..., t1(11):

f:=t1(5)$ s:=rlwqe f$ rlatnum s; rlexpand s$ ws;

% We compute $T_{2,2}$. In the literature we have treated instances with
% in the range t1(1), ..., t1(3):

f:=t2(2)$ s:=rlwqe f$ rlatnum s; rlexpand s$ ws;

% Dependency Analysis for Automatic Parallelization
dep := ex({ii,j,ip,jp},0<=ii<=m and 0<=j<=m and 0<=ip<=m and 0<=jp<=m and
(ii<>ip or j<>jp) and ii+j<>ip+jp and n*ii+j=n*ip+jp)$

depsol := rlwqe dep$
rlatnum depsol;

rlexpand rlsimpl sub(m=4,n=4,depsol);

rlqe sub(m=4,n=4,dep);

rlqe sub(m=4,n=5,dep);

% Parametric Linear Optimization Problem with Univariately Nonlinear
% Constraints
f := ex({x,y},x+y <= z and x >= 0 and y >= 0 and x+y >= 0 and x^2-a >= 0);

sol := rlwqe f$
rlatnum sol;
rlexpand sub(a=10,sol);

end;  % of file
