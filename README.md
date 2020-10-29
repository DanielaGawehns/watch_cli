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

For example, you'd run:
```
java -jar build/libs/nl.liacs.watch_cli-1.0-all.jar
```
## How to use
If watch_cli is running on the host computer, and the watch is connected with device manager, 
an ID will be given to the watch when it is connected. This happens automatically.

Using ```devices```, it is possible to view the connection between the host and the watch.

Once the software is running, the help command can be used to get more information about the other commands
