const { defineConfig } = require('cypress')

module.exports = defineConfig({
  watchForFileChanges: false,
  pageLoadTimeout: 200000,
  defaultCommandTimeout: 200000,
  video: true,
  videoCompression: true,
  reporter: 'junit',
  reporterOptions: {
    mochaFile: 'results/ui-tests.[hash].xml',
    jenkinsMode: true,
  },
  e2e: {
    // We've imported your old cypress plugins here.
    // You may want to clean this up later by importing these.
    setupNodeEvents(on, config) {
      return require('./cypress/plugins/index.js')(on, config)
    },
    testIsolation: false, // allows keeping CAS running between CAS tests
    baseUrl: 'http://127.0.0.1:8888/',
  },
})
