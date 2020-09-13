# Build and run
```sh
$ sbt graalvm-native-image:packageBin && \
  sbt dockerGraalvmNative && \
  docker run -it --rm graal-zio-hello
```
