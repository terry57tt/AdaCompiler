Projet PCL1 & PCL2 du groupe:
- ARIES Lucas,
- AURAND-AUGIER Mathias,
- TEMPESTINI Terry,
- ZHEN Julie

# Start the project

## Linux & Mac
```shell
# in root project PCL

# to setup the gradle project 
./gradlew build

# or
./gradlew run -Pfiles="pathFile1 pathFile2 pathFile3"

# all tests
./gradlew test

# specific test 
./gradlew test --tests ProjectConfigurationTest

# help menu 
./gradlew tasks
```
## Windows
```shell
# in root project PCL

# to setup the gradle project 
gradlew.bat build

# or
gradlew.bat run -Pfiles="pathFile1 pathFile2 "

# all tests
gradlew.bat test

# specific test 
gradlew.bat test --tests ProjectConfigurationTest

# help menu 
gradlew.bat  tasks
```

# Execute the print ASM program
```shell
# in root project PCL
java -jar pcl-run.jar output.s
```

# Environment
- Gradle Groovy
- Java
- JUnit Jupiter