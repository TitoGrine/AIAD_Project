## Load balancing and peak shaving on multi-vehicle electric charging systems
Agentes e Inteligência Artificial Distribuída - Grupo 43  
 - Carlos Jorge Direito Albuquerque - up201706735   
 - Maria Inês Fernandes Alves - up201605335   
 - Tito Alexandre Trindade Griné - up201706732  
 
 ### Description
 A JADE + Repast + Sajas implementation of a electrical vehicle charging station.  
 This charging station is capable of balancing its load between the vehicles, according to their needs, and also reducing
 peak hours energy demand by using the vehicles' batteries.  
 There are three types of vehicles implemented with increasingly more complex communication protocols and attributes.  
 The purpose of this project is to simulate the usage of these smart grid systems in the real world, trying to prove that
 with more advanced electrical vehicles and full vehicle-to-grid capabilities we can improve a city's electrical efficiency 
 and use the Repast functionalities to display graphs and networks in runtime.
 
 ### Compile & Run
 To compile the code, run the following command on the root of the project:
 ```
    javac -cp "lib/*:src" -d out src/RepastLauncher.java
```
Afterwards, run it by executing:
 ```
    java -cp "lib/*:out" RepastLauncher
```
### Running our simulations
To mimic the simulations that we performed, choose a test from the `simulation_tests.txt` and change the variables in `utils/Constants.java`, in the `src/parameters.txt` or in the GUI settings to match the ones written in the test. Compile and run to view the simulation.