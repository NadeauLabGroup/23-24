## Monday January 15th, 2024 Notes

###Working in Java:
- Compilation command to access packages and check for errors:
    ```bash
    javac -classpath "full/path/to/ij.jar" name_of_project.java
    ```
    - For some reason, I am unable to access the ij.jar file aside from the one in the ImageJ file in my Downloads folder.
    - Fiji uses Java `version 1.8.0_322`, my own machine (my Macbook) uses `version 21.0.1`. this renders Fiji/ImageJ unable to run code I compile on my machine.
    Fix: Compile using `-target` and/or `-source` flags
    `-target` specifies the oldest version of Java you want supported. `-source` specifies which version you are compiling with. 
    **For sake of consistency, compile with source and target flags at `1.8`**
        ```bash
        javac -target 1.8 -classpath "full/path/to/ij.jar" name_of_project.java
        ```
        ```bash
        javac -target 1.8 -source 1.8 -classpath "full/path/to/ij.jar" name_of_project.java
        ```
    Doing so generated the warning message:
    ```bash
    warning: [options] bootstrap class path not set in conjunction with -source 8
    warning: [options] source value 8 is obsolete and will be removed in a future release
    warning: [options] target value 8 is obsolete and will be removed in a future release
    warning: [options] To suppress warnings about obsolete options, use -Xlint:-options.
    ```
    As of now I don't necessarily see a need for concern so we can ignore these warnings for now by using the suggestion in the last warning.

- VScode configuration in `settings.json` file:
    ```json
    "java.project.referencedLibraries": [ 
        "full/path/to/ij.jar",
    ]
    ```
    - Doing this allows VScode to access the ImageJ `ij.jar` package, removing error messages and provides helpful comments.


### Importing plugins to Fiji
- There are 2 ways
    - Drag and dropping the .java file
    - Drag and dropping the .class files
