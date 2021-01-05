## Load balancing and peak shaving on multi-vehicle electric charging systems
 
 ### Description
 A JADE implementation of a electrical vehicle charging station.  
 This charging station is capable of balancing its load between the vehicles, according to their needs, and also reducing
 peak hours energy demand by using the vehicles' batteries.  
 There are three types of vehicles implemented with increasingly more complex communication protocols and attributes.  
 The purpose of this project is to simulate the usage of these smart grid systems in the real world, trying to prove that
 with more advanced electrical vehicles and full vehicle-to-grid capabilities we can improve a city's electrical efficiency.
 
 ### Dependencies
 In order to compile and run our code, you need to install JavaFX. You can follow the [OpenJFX docs](https://openjfx.io/openjfx-docs/) on how you can get started.
 
 ### Compile & Run
 Make sure you have installed OpenJFX and that you have exported the path to fx. Something like this:
 ```
    export PATH_TO_FX=path/to/javafx-sdk-15.0.1/lib
```
 To compile the code, run the following command on the root of the project:
 ```
    javac --module-path $PATH_TO_FX --add-modules javafx.controls -cp "jade/lib/jade.jar:src" -d out src/JADELauncher.java
```
Afterwards, run it by executing:
 ```
    java -cp "jade/lib/jade.jar:out" JADELauncher
```
### Running our simulations
To mimic the simulations that we performed, choose a test from the `simulation_tests.txt` and change the variables in `utils/Constants.java` to match the ones writen in the test. Compile and run to view simulation.

### Authors

* **Carlos Albuquerque** - [CajoAlbuquerque](https://github.com/CajoAlbuquerque)
* **Maria Inês Alves** - [inesfalves](https://github.com/inesfalves)
* **Tito Griné** - [TitoGrine](https://github.com/TitoGrine)

### Evaluation

**Project Grade:** 20.00
