from pyggb import Point, Segment, pywindow, selection, Color

from javax.swing import JPanel, JButton
from java.awt.event import ActionListener
import random

__all__ = ["Controls", "Kruskal", "complete_graph"]


def get_weight(segment):
    if segment.label_mode == "caption":
        return float(segment.caption)
    else:
        return float(segment)

class Controls(ActionListener):
    def __init__(self):
        self.panel = JPanel()
        self.actions = {}
    def show(self):
        pywindow.add_component(self.panel)
    def hide(self):
        # TODO
        pass
    def add_button(self, text, action):
        index = len(self.actions)
        name = "button%i" % index
        button = JButton(text)
        self.panel.add(button)
        button.setActionCommand(name)
        button.addActionListener(self)
        self.actions[name] = action
        return button
    def actionPerformed(self, ev):
        cmd = ev.getActionCommand()
        self.actions[cmd]()


class Kruskal(object):
    def __init__(self, segments=None):
        if segments is None:
            segments = []
        self.set_graph(segments)
        self.controls = Controls()
        self.create_controls(self.controls)
    def set_graph(self, segments):
        self.reset()
        self.segments = list(segments)
        self.vertices = vertices = set()
        self.weights = weights = {}
        for s in segments:
            p, q = s.startpoint, s.endpoint
            vertices.add(p)
            vertices.add(q)
            p.onpudate = q.onupdate = self.update
            weights[s] = get_weight(s)
        self.draw()
    def set_graph_to_selection(self):
        self.set_graph(selection.segments)
    def create_controls(self, controls):
        controls.add_button("Set Graph", self.set_graph_to_selection)
        controls.add_button("Reset", self.reset)
        self.step_button = controls.add_button("Step", self.step)
        self.step_button.enabled = False
    def reset(self):
        self.tree = []
        if hasattr(self, "segments"):
            for s in self.segments:
                s.thickness = 1
                s.color = Color.BLACK
            self.step_iterator = self.steps()
            self.step_button.enabled = True
    def step(self):
        try:
            self.step_iterator.next()
        except StopIteration:
            self.step_button.enabled = False
    def steps(self):
        print "=== Start Kruskal ==="
        print "Sorting segments in ascending order of weight"
        self.segments.sort(key=self.weights.__getitem__)
        print " ".join("%s%s(%.2f)" % (s.startpoint, s.endpoint, self.weights[s])
                       for s in self.segments)
        edges_left = len(self.vertices) - 1
        print "Edges required: %s" % edges_left
        yield
        components = dict((v, [v]) for v in self.vertices)
        paths = dict(((v, v), v.label) for v in self.vertices)
        self.tree = tree = []
        for s in self.segments:
            if edges_left <= 0:
                break
            p, q = s.startpoint, s.endpoint
            print "%s%s:" % (p, q),
            s.thickness = 4
            s.color = Color.GREEN
            yield
            c1, c2 = components[p], components[q]
            if c1 is not c2:
                edges_left -= 1
                print "include (no cycle) - %i more edge(s)" % edges_left
                s.thickness = 4
                s.color = Color.RED
                c1.extend(c2)
                tree.append(s)
                for a in c1:
                    for b in c2:
                        if (a, b) not in paths:
                            paths[a, b] = paths[a, p] + paths[q, b]
                        if (b, a) not in paths:
                            paths[b, a] = paths[b, q] + paths[p, a]
                for v in c2:
                    components[v] = c1
            else:
                s.thickness = 1
                s.color = Color.BLACK
                print "exclude (cycle %s)" % paths[p, q]
            yield
        self.weight = sum(map(self.weights.__getitem__, tree))
        print "Done"
        print "=== End Kruskal ==="
    def draw(self):
        if (hasattr(self, 'step_button')):
            self.step_button.enabled = False
        self.segments.sort(key=self.weights.__getitem__)
        components = dict((v, [v]) for v in self.vertices)
        self.tree = tree = []
        for s in self.segments:
            p, q = s.startpoint, s.endpoint
            c1, c2 = components[p], components[q]
            if c1 is not c2:
                s.thickness = 4
                s.color = Color.RED
                c1.extend(c2)
                tree.append(s) 
                for v in c2:
                    components[v] = c1
            else:
                s.thickness = 1
                s.color = Color.BLACK
        self.weight = sum(map(self.weights.__getitem__, tree))
    def update(self, p):
        last_weight = -1
        redraw = False
        for s in self.segments:
            weight = self.weights[s] = get_weight(s)
            if weight < last_weight:
                redraw = True
            last_weight = weight
        if redraw:
            self.draw()

def complete_graph(size):
    """
    Create a complete graph with size vertices
    Return the list of its edges (as Segments)
    """
    u = random.uniform
    points = [Point(u(-2, 5), u(-2, 5)) for i in range(size)]
    return [Segment(points[i], points[j])
            for i in range(size) for j in range(i)]
