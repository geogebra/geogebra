from logo import LogoCompiler, RootContext, Error
from builtins import builtin_list

class StringStream(object):
    def __init__(self):
        self.lines = []
    def append(self, text):
        self.lines.extend(l + '\n' for l in  text.split('\n'))
    def __iter__(self):
        return self
    def __nonzero__(self):
        return bool(self.lines)
    def next(self):
        if self.lines:
            return self.lines.pop(0)
        else:
            raise StopIteration


class Logo(object):
    def __init__(self, turtle):
        self.root = RootContext()
        self.turtle = turtle
        self.stream = StringStream()
        self.compiler = LogoCompiler(self.stream)
        for b in builtin_list:
            self.root.add_builtin(b)
    def run(self, text):
        self.stream.append(text)
        while self.stream:
            code = self.compiler.compile_line()
            code.run(self.root)
    def _setturtle(self, turtle):
        self._turtle = turtle
        self.root.turtle = turtle
    def _getturtle(self, turtle):
        return self._turtle
    turtle = property(_getturtle, _setturtle)

