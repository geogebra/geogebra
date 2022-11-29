import {selectors} from '@geogebra/web-test-harness/selectors'
/*global Cypress,cy*/

Cypress.Commands.add("writeInMathField", {}, text => {
    selectors.mathField.get().focus().type(text, {force: true});
});

Cypress.Commands.add("mathFieldShouldBe", {}, text => {
    selectors.mathField.get().should('have.text', text);
});

Cypress.Commands.add("clickMathField", {}, () => {
    selectors.editor.get().eq(0).click();
 });

Cypress.Commands.add("blurMathField", {}, () => {
    selectors.mathField.get().focus().blur();
});

Cypress.Commands.add("snapshotEditor", {}, () => {
    var options = {
      failureThreshold: 18, // threshold for entire image
      failureThresholdType: 'pixel', // percent of image or number of pixels
      customDiffConfig: { threshold: 0.0 }, // threshold for each pixel
      capture: 'viewport', // capture viewport in screenshot
    };

    selectors.editor.get().matchImageSnapshot("mathfieldeditor", options);
});

Cypress.Commands.add("keyboardShouldPresent", {}, () => {
     cy.get(".TabbedKeyBoard").should('be.visible');
});

Cypress.Commands.add("keyboardShouldNotPresent", {}, () => {
     cy.get(".TabbedKeyBoard").should('not.be.visible');
});
