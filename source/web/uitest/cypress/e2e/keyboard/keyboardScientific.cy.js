import '../../support/embed/commands.js'
import {selectors} from '@geogebra/web-test-harness/selectors'
/*global cy*/

describe('Keyboard button visibility test', () => {
    afterEach(cy.setSaved);

    beforeEach(() => {
        cy.visit('scientific.html');
    });

    it("Keyboard should be shown when loaded",
    () => {
        cy.window().then(win => win.localStorage.setItem("keyboardwanted", "true"));
        cy.get("body.application");
        cy.keyboardShouldPresent();
    });

    it("Keyboard button should be shown when loaded",
    () => {
        cy.window().then(win => win.localStorage.setItem("keyboardwanted", "false"));
        cy.get("body.application");
        selectors.showKeyboardButton.get().should('be.visible');
    });
})