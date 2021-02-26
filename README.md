# About native executable loader

The native executable loader is a utility that assists with loading native
executables from Java. It provides the ability to painlessly identify, extract
and run the correct platform-specific native executable. This is work in progress, 
and isn't ready for use yet.


##### License

Simplified BSD License


# Usage

### Package native executables

Native executables should be packaged into a single jar file, with the
following directory & file structure:

```
/natives
  /linux_32
     xxx[-vvv]
  /linux_64
     xxx[-vvv]
  /osx_32
     xxx[-vvv]
  /osx_64
     xxx[-vvv]
  /osx_arm64
     xxx[-vvv]
  /windows_32
     xxx[-vvv].exe
  /windows_64
     xxx[-vvv].exe
  /aix_32
     xxx[-vvv]
     xxx[-vvv]
  /aix_64
     xxx[-vvv]
     xxx[-vvv]
```

Here "xxx" is the name of the native executable and "-vvv" is an optional version number.
Depending on the platform at runtime, a native executable will be unpacked into a temporary file
and will be loaded from there.

The version information will be grabbed from the MANIFEST.mf file
from "Implementation-Version" entry. So it's recommended to follow Java's
[package version information](https://docs.oracle.com/javase/tutorial/deployment/jar/packageman.html)
convention. 

### Run executable

If you want to run 'awesome.exe' (on Windows) or 'awesome' (on Linux or AIX),
simply do like this ...

```Java
NativeExe.runExe("awesome");
```
