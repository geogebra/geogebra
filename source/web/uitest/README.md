## Web UI Test harness
This repository contains some code for UI automation and testing in GeoGebra web
apps. 

## Requirements
- The framework requires NodeJS version 8+ ([download](https://nodejs.org/en/download/)). 
The instructions below assume that `npm` (which is part of NodeJS distribution) 
is installed on your path.
- In parallel with the tests you need to run a server that serves GeoGebra web.
  - The test harness looks by default at `localhost:8888`, so default GWT dev server works
  - The URL can be overridden using the `CYPRESS_baseUrl` environment variable
    (should work with autotest server).
  - To reduce the network latency you can also copy the `web3d` files to `content`
    and run `npx http-server -p 888 content` to serve the files from a lightweight 
    server.

## Running the tests
First install the dependencies
```
npm install
```

Then you can run the tests as
```
npx cypress run --browser chrome
```