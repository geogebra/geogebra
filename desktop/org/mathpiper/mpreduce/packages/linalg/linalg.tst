if lisp !*rounded then rounded_was_on := t 
 else rounded_was_on := nil;

mat1  := mat((1,2,3,4,5),(2,3,4,5,6),(3,4,5,6,7),(4,5,6,7,8),(5,6,7,8,9));
mat2  := mat((1,1,1,1),(2,2,2,2),(3,3,3,3),(4,4,4,4));
mat3  := mat((x),(x),(x),(x));
mat4  := mat((3,3),(4,4),(5,5),(6,6)); 
mat5  := mat((1,2,1,1),(1,2,3,1),(4,5,1,2),(3,4,5,6));
mat6  := mat((i+1,i+2,i+3),(4,5,2),(1,i,0));
mat7  := mat((1,1,0),(1,3,1),(0,1,1));
mat8  := mat((1,3),(-4,3));
mat9 :=  mat((1,2,3,4),(9,8,7,6));
poly  := x^7+x^5+4*x^4+5*x^3+12;
poly1 := x^2+x*y^3+x*y*z^3+y*x+2+y*3;

on errcont;


% Basis matrix manipulations.

add_columns(mat1,1,2,5*y);
add_rows(mat1,1,2,x);

add_to_columns(mat1,3,1000);
add_to_columns(mat1,{1,2,3},y);
add_to_rows(mat1,2,1000);
add_to_rows(mat1,{1,2,3},x);

augment_columns(mat1,2);  
augment_columns(mat1,{1,2,5});
stack_rows(mat1,3);  
stack_rows(mat1,{1,3,5});  

char_poly(mat1,x);

column_dim(mat2);
row_dim(mat1);

copy_into(mat7,mat1,2,3);
copy_into(mat7,mat1,5,5);

diagonal(3);
% diagonal can take both a list of arguments or just the arguments.
diagonal({mat2,mat6});
diagonal(mat1,mat2,mat5);

extend(mat1,3,2,x);

find_companion(mat5,x);

get_columns(mat1,1);
get_columns(mat1,{1,2});
get_rows(mat1,3);
get_rows(mat1,{1,3});

hermitian_tp(mat6);

% matrix_augment and matrix_stack can take both a list of arguments 
% or just the arguments.
matrix_augment({mat1,mat2});
matrix_augment(mat4,mat2,mat4);
matrix_stack(mat1,mat2);
matrix_stack({mat6,mat((z,z,z)),mat7});

minor(mat1,2,3);

mult_columns(mat1,3,y);
mult_columns(mat1,{2,3,4},100);
mult_rows(mat1,2,x);
mult_rows(mat1,{1,3,5},10);

pivot(mat1,3,3);
rows_pivot(mat1,3,3,{1,5});

remove_columns(mat1,3);
remove_columns(mat1,{2,3,4});
remove_rows(mat1,2);
remove_rows(mat1,{1,3});
remove_rows(mat1,{1,2,3,4,5});

swap_columns(mat1,2,4);
swap_rows(mat1,1,2);
swap_entries(mat1,{1,1},{5,5});


% Constructors - functions that create matrices.

band_matrix(x,5);
band_matrix({x,y,z},6);

block_matrix(1,2,{mat1,mat2});
block_matrix(2,3,{mat2,mat3,mat2,mat3,mat2,mat2});

char_matrix(mat1,x);

cfmat := coeff_matrix({x+y+4*z=10,y+x-z=20,x+y+4});
first cfmat * second cfmat;
third cfmat;

companion(poly,x);

hessian(poly1,{w,x,y,z});

hilbert(4,1);
hilbert(3,y+x);

% NOTE WELL. The function tested here used to be called just "jacobian"
% however us of that name was in conflict with another Reduce package so
% now it is called mat_jacobian.
mat_jacobian({x^4,x*y^2,x*y*z^3},{w,x,y,z});

jordan_block(x,5);

make_identity(11);

on rounded; % makes things a bit easier to read.
random_matrix(3,3,100);
on not_negative;
random_matrix(3,3,100);
on only_integer;
random_matrix(3,3,100);
on symmetric;
random_matrix(3,3,100);
off symmetric;
on upper_matrix;
random_matrix(3,3,100);
off upper_matrix;
on lower_matrix;
random_matrix(3,3,100);
off lower_matrix;
on imaginary;
off not_negative;
random_matrix(3,3,100);
off rounded;

% toeplitz and vandermonde can take both a list of arguments or just 
% the arguments.
toeplitz({1,2,3,4,5});
toeplitz(x,y,z);

vandermonde({1,2,3,4,5});
vandermonde(x,y,z);

% kronecker_product

a1 := mat((1,2),(3,4),(5,6));
a2 := mat((1,x,1),(2,2,2),(3,3,3));

kronecker_product(a1,a2);

clear a1,a2;

% High level algorithms.

on rounded; % makes output easier to read.
ch := cholesky(mat7);
tp first ch - second ch;
tmp := first ch * second ch;
tmp - mat7;
off rounded;

gram_schmidt({1,0,0},{1,1,0},{1,1,1});
gram_schmidt({1,2},{3,4});

on rounded; % again, makes large quotients a bit more readable.
% The algorithm used for lu_decom sometimes swaps the rows of the input 
% matrix so that (given matrix A, lu_decom(A) = {L,U,vec}), we find L*U 
% does not equal A but a row equivalent of it. The call convert(A,vec) 
% will return this row equivalent (ie: L*U = convert(A,vec)).
lu := lu_decom(mat5); 
mat5;
tmp := first lu * second lu;
tmp1 := convert(mat5,third lu);
tmp - tmp1;
% and the complex case...
lu1 := lu_decom(mat6);
mat6;
tmp := first lu1 * second lu1;
tmp1 := convert(mat6,third lu1);
tmp - tmp1;

mat9inv := pseudo_inverse(mat9);
mat9 * mat9inv;

simplex(min,2*x1+14*x2+36*x3,{-2*x1+x2+4*x3>=5,-x1-2*x2-3*x3<=2});

simplex(max,10000 x1 + 1000 x2 + 100 x3 + 10 x4 + x5,{ x1 <= 1, 20 x1 +
 x2 <= 100, 200 x1 + 20 x2 + x3 <= 10000, 2000 x1 + 200 x2 + 20 x3 + x4
 <= 1000000, 20000 x1 + 2000 x2 + 200 x3 + 20 x4 + x5 <= 100000000});

simplex(max, 5 x1 + 4 x2 + 3 x3,
           { 2 x1 + 3 x2 + x3 <= 5, 
             4 x1 + x2 + 2 x3 <= 11, 
             3 x1 + 4 x2 + 2 x3 <= 8 });

simplex(min,3 x1 + 5 x2,{ x1 + 2 x2 >= 2, 22 x1 + x2 >= 3});

simplex(max,10x+5y+5.5z,{5x+3z<=200,0.2x+0.1y+0.5z<=12,0.1x+0.2y+0.3z<=9,
                         30x+10y+50z<=1500});

%example of extra variables (>=0) being added.
simplex(min,x-y,{x>=-3});

% unfeasible as simplex algorithm implies all x>=0.
simplex(min,x,{x<=-100});

% three error examples.
simplex(maxx,x,{x>=5});
simplex(max,x,x>=5);
simplex(max,x,{x<=y});

simplex(max, 346 X11 + 346 X12 + 248 X21 + 248 X22 + 399 X31 + 399 X32 + 
             200 Y11 + 200 Y12 + 75 Y21 + 75 Y22 + 2.35 Z1 + 3.5 Z2,
{ 
 4 X11 + 4 X12 + 2 X21 + 2 X22 + X31 + X32 + 250 Y11 + 250 Y12 + 125 Y21 + 
  125 Y22 <= 25000,
 X11 + X12 + X21 + X22 + X31 + X32 + 2 Y11 + 2 Y12 + Y21 + Y22 <= 300,
 20 X11 + 15 X12 + 30 Y11 + 20 Y21 + Z1 <= 1500,
 40 X12 + 35 X22 + 50 X32 + 15 Y12 + 10 Y22 + Z2  = 5000,
 X31  = 0,
 Y11 + Y12 <= 50,
 Y21 + Y22 <= 100
});


% from Marc van Dongen. Finding the first feasible solution for the 
% solution of systems of linear diophantine inequalities.
simplex(max,0,{
  3*X259+4*X261+3*X262+2*X263+X269+2*X270+3*X271+4*X272+5*X273+X229=2,
  7*X259+11*X261+8*X262+5*X263+3*X269+6*X270+9*X271+12*X272+15*X273+X229=4,
  2*X259+5*X261+4*X262+3*X263+3*X268+4*X269+5*X270+6*X271+7*X272+8*X273=1,
  X262+2*X263+5*X268+4*X269+3*X270+2*X271+X272+2*X229=1,
  X259+X262+2*X263+4*X268+3*X269+2*X270+X271-X273+3*X229=2,
  X259+2*X261+2*X262+2*X263+3*X268+3*X269+3*X270+3*X271+3*X272+3*X273+X229=1,
  X259+X261+X262+X263+X268+X269+X270+X271+X272+X273+X229=1});

svd_ans := svd(mat8);
tmp := tp first svd_ans * second svd_ans * third svd_ans;
tmp - mat8;

mat9inv := pseudo_inverse(mat9);
mat9 * mat9inv;

% triang_adjoint(in_mat) calculates the
% triangularizing adjoint of in_mat

triang_adjoint(mat1);
triang_adjoint(mat2);
triang_adjoint(mat5);
triang_adjoint(mat6);
triang_adjoint(mat7);
triang_adjoint(mat8);
triang_adjoint(mat9);

% testing triang_adjoint with random matrices

% the range of the integers is in one case from
% -1000 to 1000. in the other case it is from
% -1 to 1 so that the deteminant of the i-th
% submatrix equals very often to zero.

% random matrix contains arbitrary real values
off only_integer;
tmp:=random_matrix(5,5,1000);
triang_adjoint tmp;

tmp:=random_matrix(1,1,1000);
triang_adjoint tmp;

% random matrix contains complex real values
on imaginary;
tmp:=random_matrix(5,5,1000);
triang_adjoint tmp;

tmp:=random_matrix(1,1,1000);
triang_adjoint tmp;
off imaginary;

% random matrix contains rounded real values
on rounded;
tmp:=random_matrix(5,5,1000);
triang_adjoint tmp;

tmp:=random_matrix(1,1,1000);
triang_adjoint tmp;
off rounded;

% random matrix contains only integer values
on only_integer;
tmp:=random_matrix(7,7,1000);
triang_adjoint tmp;

tmp:=random_matrix(7,7,1);
triang_adjoint tmp;

% random matrix contains only complex integer
% values

on imaginary;
tmp:=random_matrix(5,5,1000);
triang_adjoint tmp;

tmp:=random_matrix(5,5,2);
triang_adjoint tmp;

% Predicates.

matrixp(mat1);
matrixp(poly);

squarep(mat2);
squarep(mat3);

symmetricp(mat1);
symmetricp(mat3);

if not rounded_was_on then off rounded;


END;


