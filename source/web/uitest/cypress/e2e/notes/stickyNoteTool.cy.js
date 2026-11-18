import {selectors} from '@geogebra/web-test-harness/selectors';

function getRuns(win) {
    const xml = new win.DOMParser().parseFromString(win.ggbApplet.getXML("a"), "text/xml");
    // Murok omits formatting values equal to its defaults when serializing runs.
    return JSON.parse(xml.querySelector("content").getAttribute("val"))
        .map(run => ({size: win.murok.runs.defaultFormatting.size, ...run}));
}

function expectText(text) {
    cy.window().should((win) => {
        expect(getRuns(win).map(run => run.text).join('').trimEnd()).to.equal(text);
    });
}

function expectUniformSize(size) {
    cy.window().should((win) => {
        const runs = getRuns(win);
        expect(runs).not.to.be.empty;
        expect(runs.every(run => run.size === size)).to.equal(true);
    });
}

describe('Sticky note Auto font size (MOW-1944)', () => {
    beforeEach(() => {
        cy.visit('notes.html');
        cy.get('body.application');
        cy.get('[data-title="Sticky Note"]').click();
        selectors.euclidianView.get()
            .mouseEvent('down', 200, 300)
            .mouseEvent('up', 200, 300);
    });

    afterEach(cy.setSaved);

    it('shows the rounded fixed font size throughout a resize', () => {
        cy.get('.murokTextArea textarea').type('Hi', {force: true});
        cy.get('input[aria-label="Font Size"]').clear().type('20{enter}');
        selectors.euclidianView.get().mouseEvent('down', 360, 460);
        cy.get('input[aria-label="Font Size"]').should('be.visible').and('have.value', '20');
        selectors.euclidianView.get().mouseEvent('move', 400, 500);
        cy.get('input[aria-label="Font Size"]').should('be.visible').and('have.value', '25');
        selectors.euclidianView.get().mouseEvent('move', 440, 540);
        cy.get('input[aria-label="Font Size"]').should('be.visible').and('have.value', '30');
        selectors.euclidianView.get().mouseEvent('up', 440, 540);
        cy.get('input[aria-label="Font Size"]').should('be.visible').and('have.value', '30');
        expectUniformSize(30);
        cy.get('input[aria-label="Font Size"]').click();
        cy.get('[role="option"][aria-selected="true"]').should('not.exist');
    });

    it('keeps Auto visible throughout resizing and refits on release', () => {
        cy.get('.murokTextArea textarea').type('Hi', {force: true});
        selectors.euclidianView.get().mouseEvent('down', 360, 460)
            .mouseEvent('move', 400, 500);
        cy.get('input[aria-label="Font Size"]').should('be.visible').and('have.value', 'Auto');
        selectors.euclidianView.get().mouseEvent('move', 440, 540);
        cy.get('input[aria-label="Font Size"]').should('be.visible').and('have.value', 'Auto');
        selectors.euclidianView.get().mouseEvent('up', 440, 540);
        cy.get('input[aria-label="Font Size"]').should('be.visible').and('have.value', 'Auto');
        expectUniformSize(120);
    });

    it('applies a fixed font size to the whole note with a partial selection', () => {
        cy.get('.murokTextArea textarea').type('Hello', {force: true});
        cy.get('input[aria-label="Font Size"]').clear().type('12{enter}');
        expectUniformSize(12);
        selectors.euclidianView.get()
            .mouseEvent('down', 250, 350)
            .mouseEvent('up', 250, 350);
        cy.get('.murokTextArea textarea').focus().type('{ctrl}a', {force: true});
        cy.get('.murokTextArea textarea').type('{leftarrow}', {force: true});
        cy.get('.murokTextArea textarea').type('{shift}{rightarrow}{rightarrow}', {force: true});
        cy.get('.murokTextArea textarea').should('have.value', 'He');
        cy.get('input[aria-label="Font Size"]').click();
        cy.get('[role="option"]').contains(/^24$/).click();
        expectText('Hello');
        expectUniformSize(24);
        cy.get('input[aria-label="Font Size"]').should('have.value', '24');
        cy.get('.murokTextArea textarea').focus().should('be.focused')
            .type('{ctrl}z', {force: true});
        expectUniformSize(12);
        cy.get('.murokTextArea textarea').focus().should('be.focused')
            .type('{ctrl}y', {force: true});
        expectUniformSize(24);
    });

    it('disables overflowing suggestions and rejects typed overflowing sizes', () => {
        cy.get('.murokTextArea textarea').type('Hi', {force: true});
        expectUniformSize(96);
        cy.get('input[aria-label="Font Size"]').click();
        cy.get('[role="option"]').contains(/^120$/)
            .should('have.attr', 'aria-disabled', 'true')
            .and('have.class', 'gwt-MenuItem-disabled');
        cy.get('[role="option"]').contains(/^96$/)
            .should('have.attr', 'aria-disabled', 'false');
        cy.get('input[aria-label="Font Size"]').type('{esc}').clear().type('120{enter}');
        cy.get('input[aria-label="Font Size"]').should('have.value', 'Auto');
        expectUniformSize(96);
    });

    it('skips disabled suggestions during keyboard navigation', () => {
        cy.get('.murokTextArea textarea').type('Hi', {force: true});
        cy.get('input[aria-label="Font Size"]').click().type('{uparrow}{enter}');
        cy.get('input[aria-label="Font Size"]').should('have.value', '96');
        expectUniformSize(96);
    });

    it('shrinks on insertion and grows on deletion while retaining Auto', () => {
        cy.get('.murokTextArea textarea').type('Hi', {force: true});
        expectUniformSize(96);

        cy.get('.murokTextArea textarea').type(
            ' A sticky note with enough words to wrap onto several lines.', {force: true});
        cy.window().should((win) => {
            const sizes = getRuns(win).map(run => run.size);
            expect(new Set(sizes).size).to.equal(1);
            expect(sizes[0]).to.be.lessThan(96);
        });

        cy.get('.murokTextArea textarea')
            .type('{ctrl}a', {force: true})
            .type('Hi', {force: true});
        expectUniformSize(96);
        cy.get('input[aria-label="Font Size"]').should('have.value', 'Auto');
        cy.get('.murokEditorCanvas').should('have.length', 1);
    });

    it('fits again when switching from a fixed size to Auto without typing', () => {
        cy.get('.murokTextArea textarea').type('Hi', {force: true});
        expectUniformSize(96);
        cy.get('input[aria-label="Font Size"]').clear().type('12{enter}');
        expectUniformSize(12);
        cy.get('input[aria-label="Font Size"]').clear().type('Auto{enter}');
        expectUniformSize(96);
    });
    it('commits a rounded non-preset fixed size and normalizes the layout on release', () => {
        cy.get('.murokTextArea textarea').type('Hi', {force: true});
        cy.get('input[aria-label="Font Size"]').clear().type('20{enter}');
        expectUniformSize(20);

        selectors.euclidianView.get()
            .mouseEvent('down', 360, 460)
            .mouseEvent('move', 396, 496)
            .mouseEvent('up', 396, 496);

        // 20 * (196 / 160) = 24.5: retain 25 instead of snapping to 24.
        expectUniformSize(25);
        cy.window().should((win) => {
            const xml = new win.DOMParser().parseFromString(win.ggbApplet.getXML("a"), "text/xml");
            const size = xml.querySelector('contentSize');
            expect(Number(size.getAttribute('width'))).to.be.closeTo(196, 1e-8);
            expect(Number(size.getAttribute('height'))).to.be.closeTo(196, 1e-8);
        });
    });

    it('refits Auto after release instead of retaining the proportional preview', () => {
        cy.get('.murokTextArea textarea').type('Hi', {force: true});
        expectUniformSize(96);

        selectors.euclidianView.get()
            .mouseEvent('down', 360, 460)
            .mouseEvent('move', 440, 540)
            .mouseEvent('up', 440, 540);

        // The preview is 144; the largest allowed Auto size is 120.
        expectUniformSize(120);
        selectors.euclidianView.get()
            .mouseEvent('down', 650, 100)
            .mouseEvent('up', 650, 100)
            .mouseEvent('down', 250, 350)
            .mouseEvent('up', 250, 350);
        cy.get('input[aria-label="Font Size"]').should('have.value', 'Auto');
    });

    it('undoes and redoes the text edit together with its Auto font adjustment', () => {
        cy.get('.murokTextArea textarea').type('Hi', {force: true});
        expectUniformSize(96);
        cy.get('.murokTextArea textarea').type('W', {force: true});
        expectText('HiW');
        cy.window().should((win) => {
            expect(getRuns(win)[0].size).to.be.lessThan(96);
        });

        cy.get('.murokTextArea textarea').type('{ctrl}z', {force: true});
        expectText('Hi');
        expectUniformSize(96);
        cy.get('.murokTextArea textarea').type('{ctrl}y', {force: true});
        expectText('HiW');
        cy.get('.murokTextArea textarea').type('{ctrl}z', {force: true});
        expectText('Hi');
        cy.get('.murokTextArea textarea').type('{ctrl}z', {force: true});
        expectText('H');
    });

    it('preserves Redo when Undo itself needs to refit Auto after resizing', () => {
        cy.get('.murokTextArea textarea').type('HiW', {force: true});
        expectText('HiW');
        selectors.euclidianView.get()
            .mouseEvent('down', 360, 460)
            .mouseEvent('move', 440, 540)
            .mouseEvent('up', 440, 540);
        expectUniformSize(96);

        cy.get('.murokTextArea textarea').type('{ctrl}z', {force: true});
        expectText('Hi');
        expectUniformSize(120);
        cy.get('.murokTextArea textarea').type('{ctrl}y', {force: true});
        expectText('HiW');
        expectUniformSize(96);
        cy.get('.murokTextArea textarea').type('{ctrl}z', {force: true});
        expectText('Hi');
        expectUniformSize(120);
        cy.get('.murokTextArea textarea').type('{ctrl}z', {force: true});
        expectText('H');
        cy.get('.murokTextArea textarea').type('{ctrl}y', {force: true});
        expectText('Hi');
        cy.get('.murokTextArea textarea').type('{ctrl}y', {force: true});
        expectText('HiW');
        expectUniformSize(96);
    });

    it('keeps earlier edits undoable after typing following a resized Undo', () => {
        cy.get('.murokTextArea textarea').type('HiW', {force: true});
        selectors.euclidianView.get()
            .mouseEvent('down', 360, 460)
            .mouseEvent('move', 440, 540)
            .mouseEvent('up', 440, 540);
        expectUniformSize(96);

        cy.get('.murokTextArea textarea').type('{ctrl}z', {force: true});
        expectText('Hi');
        expectUniformSize(120);
        cy.get('.murokTextArea textarea').type('X', {force: true});
        expectText('HiX');
        expectUniformSize(120);
        cy.get('.murokTextArea textarea').type('{ctrl}z', {force: true});
        expectText('Hi');
        cy.get('.murokTextArea textarea').type('{ctrl}z', {force: true});
        expectText('H');
        cy.get('.murokTextArea textarea').type('{ctrl}y', {force: true});
        expectText('Hi');
        cy.get('.murokTextArea textarea').type('{ctrl}y', {force: true});
        expectText('HiX');
    });

});
