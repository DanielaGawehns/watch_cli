# nl.liacs.watch_cli

## How to compile

First, fetch the required submodules:
```
git submodule update --init --recursive
```

Then, you can compile the software to a packed JAR using Gradle:
```
./gradlew shadowJar
```

This will create a jar in the `./build/libs/` folder.

## How to Run

You can run the software by simply running `java -jar <jar>` where `<jar>` is the JAR file as generated in the previous section.
If you want to run with a key store you need to give the `-Dkeystore.path=<keystore>` parameter with `<keystore>` being the path to the key store.

For example, if you want to run with key store `keystore.jks`, using the jar compiled as with the previous section, you'd run:
```
java -Dkeystore.path=./keystore.jks -jar build/libs/nl.liacs.watch_cli-*-all.jar
```

## TLS connections

TLS connections are supported using self signed certificates.
These certificates can be generated using they `keytool` program distributed
with the Java platform.
The program can be started with normal TCP connections, or connections secured
using the generated TLS key store.

## Creating a keystore

A key store is required before one can generate certificates.
The key store can simply be created with the following shell command:
```
keytool -genkey -keyalg RSA -alias store -keystore keystore.jks -validity 365 -keysize 2048
```

This command creates a key store that is valid for 365 days in the file `keystore.jks`.

The command will prompt you for a password, make this secure and remember it.
You will need it to start the program and to create new certificates.

## Creating a new certificate

When you have created a key store you can now create a certificate.
It is up to you if you want a certificate for every watch or for every set of watches, however, the first is preferable.
The certificate is once created with a single shell command:
```
keytool --export --rfc --file watch.pem --keystore keystore.jks
```

This command creates a certificate named `watch.pem` in the current directory, using the key store `keystore.jks`.
The command will prompt you for the password of the key store, this is the password you have given in the previous section.

## Putting the certificate on the watch

TODO
