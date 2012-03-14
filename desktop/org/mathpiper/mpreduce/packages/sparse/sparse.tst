% Test file for Sparse Matrices and the Linear Algebra Package for
% Sparse Matrices.

% Author: Stephen Scowcroft.                    Date: June 1995.

% Firstly, the matrices need to be created.

% This is the standard way to create a sparse matrix.

% Create a sparse matrix.

sparse mat1(5,5);

%Fill the sparse matrix with data

mat1(1,1):=2;
mat1(2,2):=4;
mat1(3,3):=6;
mat1(4,4):=8;
mat1(5,5):=10;

sparse mat4(5,5);

mat4(1,1):=x;
mat4(2,2):=x;
mat4(3,3):=x;
mat4(4,4):=x;
mat4(5,5):=x;

% A small function to automatically fill a sparse matrix with data.

procedure makematsp(nam,row);
 begin; 
  sparse nam(row,row);
    for i := 1:row do <<nam(i,i):=i>>
 end;

clear mat2;
makematsp(mat2,100);

% Matrices created in the standard Matrix way.

zz1:=mat((1,2),(3,4));
zz2:=mat((x,x),(x,x));
zz3:=mat((i+1,i+2,i+3),(4,5,2),(1,i,0));

% I have taken advantage of the Linear Algebra Package (Matt Rebbeck)
% in order to create some Sparse Matrices.

mat3:=diagonal(zz1,zz1,zz1);
mat5:=band_matrix({1,3,1},100)$
mat6:=diagonal(zz3,zz3);
mat7:=band_matrix({a,b,c},4);

% These are then "translated" into the Sparse operator using the 
% function transmat.
% This is a destructive function in the sense that the matrices are no
% longer of type 'matrix but are now 'sparse.

transmat mat3;
transmat mat5;
transmat mat6;
transmat mat7;

poly  := x^7+x^5+4*x^4+5*x^3+12;
poly1 := x^2+x*y^3+x*y*z^3+y*x+2+y*3;

% Firstly some basic matrix operations.
% These are the same as the present matrix package

mat1^-1;
mat4^-1;
mat2 + mat5$
mat2 - mat5$
mat1-mat1;
mat4 + mat1;
mat4 * mat1;

2*mat1 + (3*mat4 + mat1);
% It is also possible to combine both 'matrix and 'sparse type matrices
% in these operations.

pp:=band_matrix({1,3,1},100)$
mat5*pp;

mat5^2$

det(mat1);
det(mat4);
trace(mat1);
trace(mat4);

rank(mat1);
rank mat5;

tp(mat3);

spmateigen(mat3,eta);

% Next, tests for the Linear Algebra Package for Sparse Matrices.

%Basic matrix manipulations.

spadd_columns(mat1,1,2,5*y);
spadd_rows(mat1,1,2,x);

spadd_to_columns(mat1,3,1000);
spadd_to_columns(mat5,{1,2,3},y)$
spadd_to_rows(mat1,2,1000);
spadd_to_rows(mat5,{1,2,3},x)$

spaugment_columns(mat3,2);  
spaugment_columns(mat1,{1,2,5});
spstack_rows(mat1,3);  
spstack_rows(mat1,{1,3,5});  

spchar_poly(mat1,x);

spcol_dim(mat2);
sprow_dim(mat1);

spcopy_into(mat7,mat1,2,2);
spcopy_into(mat7,mat1,5,5);
spcopy_into(zz1,mat1,1,1);

spdiagonal(3);
% spdiagonal can take both a list of arguments or just the arguments.
spdiagonal({mat2,mat5})$
spdiagonal(mat2,mat5)$
% spdiagonal can also take a mixture of 'sparse and 'matrix types.
spdiagonal(zz1,mat4,zz1);

spextend(mat1,3,2,x);

spfind_companion(mat5,x);

spget_columns(mat1,1);
spget_columns(mat1,{1,2});
spget_rows(mat1,3);
spget_rows(mat1,{1,3});

sphermitian_tp(mat6);

% matrix_augment and matrix_stack can take both a list of arguments 
% or just the arguments.

spmatrix_augment({mat1,mat1});
spmatrix_augment(mat5,mat2,mat5)$
spmatrix_stack(mat2,mat2)$

spminor(mat1,2,3);

spmult_columns(mat1,3,y);
spmult_columns(mat2,{2,3,4},100)$
spmult_rows(mat2,2,x);
spmult_rows(mat1,{1,3,5},10);

sppivot(mat3,3,3);
sprows_pivot(mat3,1,1,{2,4});

spremove_columns(mat1,3);
spremove_columns(mat3,{2,3,4});
spremove_rows(mat1,2);
spremove_rows(mat2,{1,3})$
spremove_rows(mat1,{1,2,3,4,5});

spswap_cols(mat1,2,4);
spswap_rows(mat5,1,2)$
spswap_entries(mat1,{1,1},{5,5});



% Constructors - functions that create matrices.

spband_matrix(x,500)$
spband_matrix({x,y,z},6000)$

spblock_matrix(1,2,{mat1,mat1});
spblock_matrix(2,3,{mat3,mat6,mat3,mat6,mat3,mat6});

spchar_matrix(mat3,x);

cfmat := spcoeff_matrix({y+4*+-5*w=10,y-z=20,y+4+3*z,w+x+50});
first cfmat * second cfmat;
third cfmat;

spcompanion(poly,x);

sphessian(poly1,{w,x,y,z});

spjacobian({x^4,x*y^2,x*y*z^3},{w,x,y,z});

spjordan_block(x,500)$

spmake_identity(1000)$

on rounded; % makes output easier to read.
ch := spcholesky(mat1);
tp first ch - second ch;
tmp := first ch * second ch;
tmp - mat1;
off rounded;

% The gram schmidt functions takes a list of vectors.
% These vectors are matrices of type 'sparse with column dimension 1.

%Create the "vectors".
sparse a(4,1);
sparse b(4,1);
sparse c(4,1);
sparse d(4,1);

%Fill the "vectors" with data.
a(1,1):=1;
b(1,1):=1;
b(2,1):=1;
c(1,1):=1;
c(2,1):=1;
c(3,1):=1;
d(1,1):=1;
d(2,1):=1;
d(3,1):=1;
d(4,1):=1;

spgram_schmidt({{a},{b},{c},{d}});

on rounded; % again, makes large quotients a bit more readable.
% The algorithm used for splu_decom sometimes swaps the rows of the 
% input matrix so that (given matrix A, splu_decom(A) = {L,U,vec}), 
% we find L*U does not equal A but a row equivalent of it. The call 
% spconvert(A,vec) will return this row equivalent 
% (ie: L*U = convert(A,vec)).
lu := splu_decom(mat5)$
tmp := first lu * second lu$
tmp1 := spconvert(mat5,third lu);
tmp - tmp1;
% and the complex case..
on complex;
lu1 := splu_decom(mat6);
mat6;
tmp := first lu1 * second lu1;
tmp1 := spconvert(mat6,third lu1);
tmp - tmp1;
off complex;

mat3inv := sppseudo_inverse(mat3);
mat3 * mat3inv;


% Predicates.

matrixp(mat1);
matrixp(poly);

squarep(mat2);
squarep(mat3);

symmetricp(mat1);
symmetricp(mat3);

sparsematp(mat1);
sparsematp(poly);

off rounded;

end;
