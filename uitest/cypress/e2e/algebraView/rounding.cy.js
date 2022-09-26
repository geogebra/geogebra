import rounding from '../../../../common/src/main/resources/testData/rounding.json';

describe('Sliders test', () => {
    beforeEach(() => {
        cy.visit('graphing.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("rounding should be consistent with desktop", () => {
        for (const testCase of rounding) {
            cy.window().then((win) => {
                win.ggbApplet.evalCommand("a=" + testCase.in);
                for (const [precision, result] of Object.entries(testCase.out)) {
                    win.ggbApplet.setRounding(precision);
                    const val = win.ggbApplet.getValueString("a");
                    expect(val).to.be.equal("a = " + result);
                }
            });
        }
    });
});