import '../../support/embed/commands.js'
import {selectors} from '@geogebra/web-test-harness/selectors'

describe('Keyboard button visibility test', () => {
    beforeEach(() => {
        cy.visit('classic.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("Keyboard button should be shown when an input is focused and keyboard was closed before",
    () => {
        // Test that first the keyboard button is not present and keyboard opens/closes
        // according to the focus
        cy.writeInAVInput("f(x)=x");
        console.log(selectors)
        cy.keyboardShouldPresent();
        selectors.euclidianView.get()
                    .mouseEvent('down', 100, 100)
                    .mouseEvent('up', 100, 100);

        cy.keyboardShouldNotPresent();
        selectors.showKeyboardButton.get().should('not.be.visible');

        // Test that keyboard button is not visible after the keyboard is closed with X
        cy.writeInAVInput("g(x)=x");

        selectors.closeKeyboardButton.get().should('be.visible');
        selectors.closeKeyboardButton.get().click()
        cy.keyboardShouldNotPresent();
        selectors.showKeyboardButton.get().should('not.be.visible');

        selectors.euclidianView.get()
                    .mouseEvent('down', 100, 100)
                    .mouseEvent('up', 100, 100);

        // Test that keyboard button is shown when an input gets focus and it opens keyboard after
        // user clicks on it
        cy.writeInAVInput("h(x)=x");

        cy.keyboardShouldNotPresent();
        selectors.showKeyboardButton.get().should('be.visible');
        selectors.showKeyboardButton.get().click();
        cy.keyboardShouldPresent();

    });
})
