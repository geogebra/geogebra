from geogebra.plugin.jython import CommonsMathLinearAPI as API
from pygeo.generic import (
    generic, specmethod, GenericMethods, GenericError, sign
)

__all__ = 'Vector', 'Matrix', 'SVDecomposition'

Number = (int, long, float)

class Vector(GenericMethods):
    @generic
    def __init__(self):
        self._vector = API.ArrayRealVectorClass()
    
    @specmethod.__init__
    @sign(list)
    def init_fromlist(self, data):
        self._vector = API.ArrayRealVectorClass(data)
    
    @specmethod.__init__
    @sign(API.ArrayRealVectorClass)
    def init_fromcommons(self, v):
        self._vector = v
    
    def __repr__(self):
        return str(self._vector)
    
    def __add__(self, other):
        return Vector(API.add(self._vector, other._vector))
    
    def __sub__(self, other):
        return Vector(API.subtract(self._vector, other._vector))
    
    def __mul__(self, other):
        if isinstance(other, Number):
            return Vector(API.mapMultiply(self._vector, other))
        else:
            return Vector(API.dotProduct(self._vector, other._vector))

    def __getitem__(self, i):
        if 0 <= i < len(self):
            return API.getEntry(self._vector, i)
        else:
            raise IndexError
    
    def __setitem__(self, i, val):
        if 0 <= i < len(self):
            API.setEntry(self._vector, i, val)
        else:
            raise IndexError

    def __len__(self):
        return API.getDimension(self._vector)
    
    @property
    def norm(self):
        return API.getNorm(self._vector)

    
class Matrix(GenericMethods):
    @generic
    def __init__(self):
        self._matrix = API.Array2DRowRealMatrixClass()
    
    @specmethod.__init__
    @sign(API.RealMatrixClass)
    def init_fromcommons(self, mat):
        self._matrix = mat
    
    @specmethod.__init__
    @sign(int, int)
    def init_fromsize(self, rows, columns):
        self._matrix = API.Array2DRowRealMatrixClass(rows, columns)
    
    @specmethod.__init__
    @sign(list)
    def init_fromdata(self, data):
        self._matrix = API.Array2DRowRealMatrixClass(data)
    
    def __repr__(self):
        return str(self._matrix)
    
    def __add__(self, other):
        if isinstance(other, Number):
            return Matrix(API.scalarAdd(self._matrix, other))
        else:
            return Matrix(API.add(self._matrix, other._matrix))
    
    def __sub__(self, other):
        if isinstance(other, Number):
            return Matrix(API.scalarAdd(self._matrix, -other))
        else:
            return Matrix(API.subtract(self._matrix, other._matrix))
    
    def __mul__(self, other):
        if isinstance(other, Number):
            return Matrix(API.scalarMultiply(self._matrix, other))
        elif isinstance(other, Vector):
            return Vector(API.operate(self._matrix, other._vector))
        else:
            return Matrix(API.multiply(self._matrix, other._matrix))
    
    def __pow__(self, n):
        return Matrix(API.power(self._matrix, n))
    
    
    def __setitem__(self, ij, val):
        i, j = ij
        API.setEntry(self._matrix, i, j, val)
    
    def __getitem__(self, ij):
        i, j = ij
        return API.getEntry(self._matrix, i, j)
    
    def increment(self, i, j, val):
        API.addToEntry(self._matrix, i, j, val)
    
    @property
    def rows(self):
        return API.getRowDimension(self._matrix)

    @property
    def columns(self):
        return API.getColumnDimension(self._matrix)
    
    @property
    def norm(self):
        return API.getNorm(self._matrix)
    
    @property
    def trace(self):
        return API.getTrace(self._matrix)


class Solver(GenericMethods):
    def __init__(self, solver):
        self._solver = solver
    
    @generic
    def solve(self):
        raise GenericError
    
    @specmethod.solve
    @sign(Matrix)
    def solve_matrix(self, mat):
        return Matrix(API.solve(self._solver, mat._matrix))
    
    @specmethod.solve
    @sign(Vector)
    def solve_vector(self, vec):
        return Vector(API.solve(self._solver, vec._vector))


class Decomposition(object):
    @property
    def solver(self):
        try:
            solver = self._solver
        except AttributeError:
            solver = self._solver = Solver(API.getSolver(self._dec))
        return solver

    @classmethod
    def solve(cls, A, B):
        solver = cls(A).solver
        return solver.solve(B)

    
class SVDecomposition(Decomposition):
    def __init__(self, mat):
        self._dec = API.SingularValueDecompositionImplClass(mat._matrix)

if __name__ == "__main__":
    A = Matrix([[0, 1], [1, 0]])
    v = Vector([1, 1])
    x = SVDecomposition.solve(A, v)
    for c in A*x - v: print c
