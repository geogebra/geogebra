import '../../support/embed/commands.js'
import {selectors} from '@geogebra/web-test-harness/selectors'

describe('Keyboard button visibility test', () => {
    beforeEach(() => {
        cy.visit('classic.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("Keyboard button shouldn't be shown when an input lost the focus",
    () => {
        cy.writeInAVInput("f(x)=x");
        console.log(selectors)
        cy.keyboardShouldPresent();
        selectors.euclidianView.get()
                    .mouseEvent('down', 100, 100)
                    .mouseEvent('up', 100, 100);

        cy.keyboardShouldNotPresent();
        selectors.showKeyboardButton.get().should('not.be.visible');
    });

    it("Keyboard button should be shown after the keyboard is closed with X",
    () => {
        cy.writeInAVInput("g(x)=x");

        selectors.closeKeyboardButton.get().should('be.visible');
        cy.wait(200); // wait for finishing keyboard up animation
        selectors.closeKeyboardButton.get().click()
        cy.keyboardShouldNotPresent();
        selectors.closeKeyboardButton.get().should('not.be.visible');
        // AV still has focus
        selectors.showKeyboardButton.get().should('be.visible');
    });

    it("Keyboard button should be visible when input gains focus and keyboard was closed earlier",
    () => {
        cy.writeInAVInput("g(x)=x");
        cy.wait(200); // wait for finishing keyboard up animation
        selectors.closeKeyboardButton.get().click()

        selectors.euclidianView.get()
                       .mouseEvent('down', 100, 100)
                       .mouseEvent('up', 100, 100);

        cy.writeInAVInput("h(x)=x");

        cy.keyboardShouldNotPresent();
        selectors.showKeyboardButton.get().should('be.visible');
        selectors.showKeyboardButton.get().click();
        cy.keyboardShouldPresent();
    });
})
