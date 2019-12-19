import {selectors} from '@geogebra/web-test-harness/selectors'

describe('Text tool test', () => {
    beforeEach(() => {
        cy.visit('notes.html');
        cy.window().then((win) => {
            win.PointerEvent = null;
        });
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("Text tool should maintain focus on resize", () => {
        // text tool selected as first on media panel
        selectors.mediaPanelButton.click(); 
        // use mouse down+up to avoid "scroll to view" behavior of cy.click
        selectors.euclidianView.get()
            .mouseEvent('down', 100, 100)
            .mouseEvent('up', 100, 100);
        cy.window().then((win) => {
            expect(win.document.activeElement).to.have.class("mowTextEditor");
        })
        cy.wait(500); // visible pause before resizing
        cy.viewport(800, 600);
        cy.window().then((win) => {
            expect(win.document.activeElement).to.have.class("mowTextEditor");
        })
    });
});