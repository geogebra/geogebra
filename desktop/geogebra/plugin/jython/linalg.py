from org.apache.commons.math.linear import Array2DRowRealMatrix, ArrayRealVector, SingularValueDecompositionImpl
from generic import generic, specmethod, GenericMethods, GenericError, sign

Number = (int, long, float)

class Vector(GenericMethods):
    @generic
    def __init__(self):
        self._vector = ArrayRealVector()
    
    @specmethod.__init__
    @sign(list)
    def init_fromlist(self, data):
        self._vector = ArrayRealVector(data)
    
    @specmethod.__init__
    @sign(ArrayRealVector)
    def init_fromcommons(self, v):
        self._vector = v
    
    def __repr__(self):
        return str(self._vector)
    
    def __add__(self, other):
        return Vector(self._vector.add(other._vector))
    
    def __sub__(self, other):
        return Vector(self._vector.subtract(other._vector))
    
    def __mul__(self, other):
        if isinstance(other, Number):
            return Vector(self._vector.mapMultiply(other))
        else:
            return self._vector.dotProduct(other._vector)

    def __getitem__(self, i):
        if 0 <= i < len(self):
            return self._vector.getEntry(i)
        else:
            raise IndexError
    
    def __setitem__(self, i, val):
        if 0 <= i < len(self):
            return self._vector.setEntry(i, val)
        else:
            raise IndexError

    def __len__(self):
        return self._vector.getDimension()
    
    @property
    def norm(self):
        return self._vector.getNorm()

    
class Matrix(GenericMethods):
    @generic
    def __init__(self):
        self._matrix = Array2DRowRealMatrix()
    
    @specmethod.__init__
    @sign(Array2DRowRealMatrix)
    def init_fromcommons(self, mat):
        self._matrix = mat
    
    @specmethod.__init__
    @sign(int, int)
    def init_fromsize(self, rows, colums):
        self._matrix = Array2DRowRealMatrix(rows, columns)
    
    @specmethod.__init__
    @sign(list)
    def init_fromdata(self, data):
        self._matrix = Array2DRowRealMatrix(data)
    
    def __repr__(self):
        return str(self._matrix)
    
    
    def __add__(self, other):
        if isinstance(other, Number):
            return Matrix(self._matrix.scalarAdd(other))
        else:
            return Matrix(self._matrix.add(other._matrix))
    
    def __sub__(self, other):
        if isinstance(other, Number):
            return Matrix(self._matrix.scalarAdd(-other))
        else:
            return Matrix(self._matrix.subtract(other))
    
    def __mul__(self, other):
        if isinstance(other, Number):
            return Matrix(self._matrix.scalarMultiply(other))
        elif isinstance(other, Vector):
            return Vector(self._matrix.operate(other._vector))
        else:
            return Matrix(self._matrix.multiply(other._matrix))
    
    def __pow__(self, n):
        return Matrix(self._matrix.power(n))
    
    
    def __setitem__(self, ij, val):
        i, j = ij
        self._matrix.setEntry(i, j, val)
    
    def __getitem__(self, ij):
        i, j = ij
        return self._matrix.getEntry(i, j)
    
    def increment(self, i, j, val):
        self._matrix.addToEntry(i, j, val)
    
    @property
    def rows(self):
        return self._matrix.getRowDimension()

    @property
    def columns(self):
        return self._matrix.getColumnDimension()
    
    @property
    def norm(self):
        return self._matrix.getNorm()
    
    @property
    def trace(self):
        return self._matrix.getTrace()


class Solver(GenericMethods):
    def __init__(self, solver):
        self._solver = solver
    
    @generic
    def solve(self):
        raise GenericError
    
    @specmethod.solve
    @sign(Matrix)
    def solve_matrix(self, mat):
        return Matrix(self._solver.solve(mat._matrix))
    
    @specmethod.solve
    @sign(Vector)
    def solve_vector(self, vec):
        return Vector(self._solver.solve(vec._vector))


class Decomposition(object):
    @property
    def solver(self):
        try:
            solver = self._solver
        except AttributeError:
            solver = self._solver = Solver(self._dec.getSolver())
        return solver

    @classmethod
    def solve(cls, A, B):
        solver = cls(A).solver
        return solver.solve(B)

    
class SVDecomposition(Decomposition):
    def __init__(self, mat):
        self._dec = SingularValueDecompositionImpl(mat._matrix)

if __name__ == "__main__":
    A = Matrix([[0, 1], [1, 0]])
    v = Vector([1, 1])
    x = SVDecomposition.solve(A, v)
    for c in A*x - v: print c