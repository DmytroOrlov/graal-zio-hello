# Build image with native-image
```sh
$ docker build --tag graalvm-native-image .
```
# Build application docker image
```sh
$ sbt dockerGraalvmNative
```
# Run application
```sh
$ docker run -it --rm graal-zio-hello
```
# All together
```sh
$ docker build --tag graalvm-native-image . && \
  sbt dockerGraalvmNative && \
  docker run -it --rm graal-zio-hello
```
