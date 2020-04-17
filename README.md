# Mongo Automatic Client Side Field Level Encryption (CSFLE)

#### Important
This application probably wouldn't execute without code change. The instructions are in the comments in the code. It has been explicitly left like this to make sure that the developer understands that the keys have to be generated and then the same keys have to be used for future. It's also importatnt to understand that the master key has to be kept at a secure place (KMS) when implementing this in production application rather than in local filesystem.

### What is this repository about?

This repository is about the demonstration of Automatic Client Side Field Level Encryption with MongoDB Enterprise Edition and SpringBoot.
This sample SpringBoot application demonstrates the insertion of a `Person` entity with encrypted and indexed PAN and then query the entity by PAN.

The use case of using CSFLE is to have encryption on a field that needs to be indexed and the Entity needs to be queries by that field. For this, deterministic encryption algorithm is used.

### Steps to Execute

Note: The class `KeyManager` has helper methods to generate master and local keys that are required for this sample to execute.

- Uncomment the methods in `MongoConfig` to generate master key and local key during the first execution. You need this local key for further execution. Maybe, print it on the console.
- Make sure that the local key is plugged into proper place and not generated again.
- Execute the application after substituting the Local key.
- The `Person` entity would be printed in local. You can use mongo shell to find the Entity to verify that the `Person` exists in Database with encrypted PAN.

#### Note:
- The Local Key is saved in `demo.__keyVault`.
- The Person entity is saved in `encrypt-test.person`.
- Automatic Client Side Field Level Encryption only works with MongoDB Enterprise Edition.
