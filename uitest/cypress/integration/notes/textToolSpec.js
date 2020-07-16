import {selectors} from '@geogebra/web-test-harness/selectors'
import {parseString} from 'xml2js'

describe('Text tool test', () => {
    beforeEach(() => {
        cy.visit('notes.html');
        cy.get("body.application");
    });

    afterEach(cy.setSaved);

    it("Text tool should maintain focus on resize", () => {
        // text tool selected as first on media panel
        selectors.mediaPanelButton.click(); 
        // use mouse down+up to avoid "scroll to view" behavior of cy.click
        selectors.euclidianView.get()
            .mouseEvent('down', 100, 100)
            .mouseEvent('up', 100, 100);
        cy.window().then((win) => {
            expect(win.document.activeElement.parentElement).to.have.class("murokTextArea");
        })
        cy.wait(500); // visible pause before resizing
        cy.viewport(800, 600);
        cy.window().then((win) => {
            expect(win.document.activeElement.parentElement).to.have.class("murokTextArea");
        })
    });

    it("Bold button should  change text format", () => {
            // text tool selected as first on media panel
            selectors.mediaPanelButton.click();
            // use mouse down+up to avoid "scroll to view" behavior of cy.click
            selectors.euclidianView.get()
                .mouseEvent('down', 100, 300)
                .mouseEvent('up', 100, 300);
            cy.get(".murokTextArea textarea").type("GeoGebra Rocks");
            cy.wait(500);
            cy.get(".btnBold img").click();

            cy.window().then((win) => {
                parseString(win.ggbApplet.getXML("a"), (err, data) => {
                    expect(data.element.content[0]["$"].val).to.equal("[{\"text\":\"GeoGebra Rocks\\n\",\"bold\":true}]");
                });
            })
    });

    it("Underline button should  change text format", () => {
           // text tool selected as first on media panel
           selectors.mediaPanelButton.click();
           // use mouse down+up to avoid "scroll to view" behavior of cy.click
           selectors.euclidianView.get()
                .mouseEvent('down', 100, 300)
                .mouseEvent('up', 100, 300);
           cy.get(".murokTextArea textarea").type("Text element example");
           cy.wait(500);
           cy.get(".btnUnderline img").click();

           cy.window().then((win) => {
               parseString(win.ggbApplet.getXML("a"), (err, data) => {
                   expect(data.element.content[0]["$"].val).to.equal("[{\"text\":\"Text element example\\n\",\"underline\":true}]");
               });
           })
        });
});