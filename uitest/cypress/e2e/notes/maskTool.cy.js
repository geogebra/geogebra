import {selectors} from '@geogebra/web-test-harness/selectors'
/*global cy,expect*/

describe('Mask tool test', () => {
    beforeEach(() => {
        cy.visit('notes.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("Mask tool should hide other objects", () => {
        
        cy.get('[data-title="Shape"]').click();
        cy.get('[data-title="Mask"]').click();
        selectors.euclidianView.get()
            .mouseEvent('down', 100, 40) // drag to create a mask
            .mouseEvent('move', 800, 300)
            .mouseEvent('up', 800, 300)
            .mouseEvent('down', 50, 150) // click outside to deselect
            .mouseEvent('up', 50, 150);
        cy.window().then((win) => {
            const before = win.ggbApplet.getPNGBase64(1);
            win.ggbApplet.evalCommand("Polygon((3, 3), (4, 3), (4, 4), (3, 4))");
            expect(before).to.equal(win.ggbApplet.getPNGBase64(1));
        });
    });
});