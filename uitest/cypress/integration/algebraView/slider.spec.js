import { selectors } from '@geogebra/web-test-harness/selectors'

describe('Context menu test', () => {
    beforeEach(() => {
        cy.visit('graphing.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("Slider should be editable", () => {
        // Enter x^2-2 in the input line
        cy.writeInAVInput("5{enter}");
        cy.get(".avSliderValue").click().type("00{enter}");
    });
});
