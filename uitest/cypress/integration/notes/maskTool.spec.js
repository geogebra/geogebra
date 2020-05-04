import {selectors} from '@geogebra/web-test-harness/selectors'

describe('Mask tool test', () => {
    beforeEach(() => {
        cy.visit('notes.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("Mask tool should hide other objects", () => {
        
        selectors.toolsPanelButton.click();
        selectors.maskToolButton.click();
        selectors.euclidianView.get()
            .mouseEvent('down', 100, 100) // drag to create a mask
            .mouseEvent('move', 800, 300)
            .mouseEvent('up', 800, 300)
            .mouseEvent('down', 50, 150) // click outside to deselect
            .mouseEvent('up', 50, 150);
        cy.window().then((win) => {
            before = win.ggbApplet.getPNGBase64(1);
            win.ggbApplet.evalCommand("Polygon((3, 3), (4, 3), (4, 4), (3, 4))");
            expect(before).to.equal(win.ggbApplet.getPNGBase64(1));
        });
    });
});