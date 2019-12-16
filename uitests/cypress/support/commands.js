import { addMatchImageSnapshotCommand } from 'cypress-image-snapshot/command';
import { selectors } from './selectors';

addMatchImageSnapshotCommand();

Cypress.Commands.add("writeInAVInput", {}, text => {
    selectors.avInput.get().focus().type(text, {force: true});
});

Cypress.Commands.add("expectGgbValue", {}, (key, value) => {
    cy.window().then((win) => expect(win.ggbApplet
        .getValue(key)).to.equal(value));
});

Cypress.Commands.add("expectGgbString", {}, (key, value) => {
    cy.window().then((win) => expect(win.ggbApplet
        .getValueString(key)).to.equal(value));
});

Cypress.Commands.add("setBase64", {}, data => {
    cy.window().then((win) => win.ggbApplet
        .setBase64(data));
});

Cypress.Commands.add("mouseEvent", {prevSubject: true}, (subject, event, x, y) => {
    let options = {
        force: true
    };

    if (typeof(x) !== 'undefined' && typeof(y) !== 'undefined') {
        options.clientX = x;
        options.clientY = y;
    }

    return cy.wrap(subject).trigger('mouse' + event, options)
        .wait(100);
});

Cypress.Commands.add("setSaved", {}, () => {
    cy.window().then((win) => (win.mainApplet || win.ggbApplet).setSaved());
});

