# gradle-utilities

Currently provided steps:

## DeleteKeychain
Deletes keychain `keychainName`

## CreateKeychain
Creates, unlocks and activates temporary keychain `keychainName` 
protected by password `keychainPassword` and imports `certificates`.
This may have overlaps with login keychain, make sure to pass `--keychain` option to codesign.
