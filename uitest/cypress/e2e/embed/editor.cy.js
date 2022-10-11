import '../../support/embed/commands.js'
/*global cy*/

describe('Evaluator test', () => {
    beforeEach(() => {
        cy.visit('evaluator.html');
    })

   const formula = "sqrt(1/2)/sqrt(1/2)";

   it("Fraction should not be jumpy", () => {
        // space is for separating the cursor
        // from the formula to pass the threshold.
        cy.clickMathField();
        cy.writeInMathField(formula + " ");
        cy.blurMathField();
        cy.clickMathField();
        cy.snapshotEditor();
        cy.blurMathField();
        cy.snapshotEditor();
   })

   it("Keyboard show/hide", () => {
        cy.clickMathField();
        cy.writeInMathField(formula);
        cy.keyboardShouldPresent();
        cy.blurMathField();
        cy.keyboardShouldNotPresent();
   })
})
