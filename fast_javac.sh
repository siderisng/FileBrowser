mkdir build
mkdir build/jar

javac src/gr/uth/inf/ce325/fileBrowser/*.java src/init/*java -d build

jar cvf  build/jar/fileBrowser.jar -C build/ . >/dev/null
