import {selectors} from '@geogebra/web-test-harness/selectors'

describe('Pan tool test', () => {
    beforeEach(() => {
        cy.visit('notes.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("Pan tool should be deselected after click, but not after drag", () => {
        cy.window().then((win) => {
            win.ggbApplet.evalCommand("Polygon((-7, -7), (7, -7), (7, 7), (-7, 7))");
            win.ggbApplet.evalCommand("Polygon((3, 3), (4, 3), (4, 4), (3, 4))");

            let selected;

            win.ggbApplet.registerClientListener((event) => {
                if (event[0] === 'select') {
                    selected = event[1];
                }
            });

            selectors.panViewTool.click();

            let before;

            before = win.ggbApplet.getPNGBase64(1);
            selectors.euclidianView.get()
                .mouseEvent('down', 300, 300)
                .mouseEvent('move', 310, 300)
                .mouseEvent('up')
                .then(() => expect(before).to.not.equal(win.ggbApplet.getPNGBase64(1)))
                .then(() => expect(selected).to.equal(undefined));

            selectors.panViewTool.get().should('have.class', 'selected');

            before = win.ggbApplet.getPNGBase64(1);
            selectors.euclidianView.get()
                .mouseEvent('down', 300, 300)
                .mouseEvent('up')
                .then(() => expect(before).to.not.equal(win.ggbApplet.getPNGBase64(1)))
                .then(() => expect(selected).to.equal("q1"));

            selectors.panViewTool.get().should('not.have.class', 'selected');
        });
    });
});
