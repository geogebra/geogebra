import {selectors} from '@geogebra/web-test-harness/selectors'

describe('Accessibility test', () => {
    beforeEach(() => {
        cy.visit('calculator.html');
        cy.window().then(win => win.localStorage.setItem("keyboardwanted", "false"));
        cy.injectAxe();
        cy.press(Cypress.Keyboard.Keys.ENTER)
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    function pressTab() {
        cy.press(Cypress.Keyboard.Keys.TAB)
    }

    it("Probability tabbing order by default", () => {
     	cy.get("[data-test='appPickerButton']").click();
     	cy.get("[data-test='probability']").click();
     	cy.wait(1000);
     	pressTab();
        cy.get("[data-test='mainMenu']").should("be.focused");
        pressTab();
        cy.get("#logoID").should("be.focused");
        pressTab();
        cy.get("[data-test='appPickerButton']").should("be.focused");
        pressTab();
        cy.get("#shareButton").should("be.focused");
        pressTab();
        cy.get("#assignButton").should("be.focused");
        pressTab();
        cy.get("#signInTextID").should("be.focused");
        pressTab();
        cy.get("[data-test='Distribution']").should("be.focused");
        pressTab();
        cy.get("[data-test='Cumulative']").should("be.focused");
        pressTab();
        cy.get("[data-test='IntervalProb']").should("be.focused");
        pressTab();
        cy.get("[data-test='Parameter μ']").should('have.class','active');
        pressTab();
        cy.get("[data-test='Parameter σ']").should('have.class','active');
        pressTab();
        cy.get("[data-test='Lower.Bound']").should('have.class','focusState');
        pressTab();
        cy.get("[data-test='Upper.Bound']").should('have.class','focusState');
    });
});
