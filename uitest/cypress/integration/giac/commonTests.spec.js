import giac from '../../../../common/src/main/resources/giac/giacTests.js';

describe('CAS tests', () => {
    const categories = {};
    for (const testcase of giac) {
        if (testcase.cat != "SolveLambertIneq") {
            categories[testcase.cat] = categories[testcase.cat] || [];
            categories[testcase.cat].push(testcase);
        }
    }

    const normalizeActual = (str) => {
        return str.replace(/c_[0-9]/g, "c_0")
                .replace(/k_[0-9]/g, "k_0")
                .replace(/c_\{[0-9]+\}/g, "c_0")
                .replace(/k_\{[0-9]+\}/g, "k_0")
                .replace(/ /g,"")
                .replace(/ggbvect\(((\((\([^()]*\)|[^()])*\)|[^()])*)\)/g, "$1"); // only needed for 4D vectors
    }

    const normalizeExpected = (str) => {
        return str.replace(/c_[0-9]+/g, "c_0")
                .replace(/n_[0-9]+/g, "k_0")
                .replace(/ /g, "")
                .replace(/GEOGEBRAERROR/, "")
                .replace(/ggbvect\(((\((\([^()]*\)|[^()])*\)|[^()])*)\)/g, "$1");
    }

    beforeEach(() => {
        cy.window().then((win) => {
            win.ggbApplet.newConstruction();
        });
    })

    before(() => {
        cy.visit('classic.html');
        cy.get("body.application");
    })

    for (const [name, cat] of Object.entries(categories)) {
        it(name + " commands", () => {
            cy.window().then((win) => {
                win.ggbApplet.asyncEvalCommand("casload=CASLoaded[]");
                // cas loaded
                cy.get(".avValue").should("contain", "true").then(() => {
                    let failedIn = [];
                    for (const testcase of cat) {
                        if (testcase.result != "RANDOM") {
                            const rounding = testcase.rounding ? testcase.rounding + "" : "2";
                            const result = normalizeActual(win.ggbApplet.evalCommandCAS(testcase.cmd, rounding));
                            const options = (testcase.round || "").split('|OR|').concat(testcase.result.split('|OR|'))
                                    .filter(Boolean).map(normalizeExpected);
                            if (!options.includes(result)) {
                                failedIn.push(`${result} should be ${options[0]} for input ${testcase.cmd}`);
                            }
                        }
                    }
                    expect(failedIn.join("\n")).to.be.equal('');
                });
            });
        });
    }
});