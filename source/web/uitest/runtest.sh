#!/bin/bash
set -eu

npm install
npm run lint
find content -type f -print0 | xargs -0 gzip -k
npx http-server -p 8888 content --gzip & npx wait-on http://localhost:8888
npm run test -- --env userAgent="Chrome iPad" --spec="cypress/integration/**/*-iPad.spec.js" || true
npm run test || true
npx lighthouse http://localhost:8888/editor.html --output json --output-path=./lighthouse.report.json --chrome-flags="--headless --no-sandbox"
