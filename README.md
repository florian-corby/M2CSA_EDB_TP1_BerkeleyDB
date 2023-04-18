# M2CSA_EDB_TP1_BerkeleyDB

## How to use this project

### In Eclipse
1. Clone this repository wherever you want
2. Import the project in Eclipse (Import Project > Git > Smart Import)
3. [OPTIONAL] If you run into a 'ClassNotFound' error then set-up the dependencies using the included jars 
    - Right click on the project folder then click on Build Path > Configure Build Path > Libraries
    - Click on 'Classpath' and 'Add External JARs'
    - In the folders browser, go to this project's folder 'lib' and select all the jars
    - [OPTIONAL] Click on the newly added je*.jar and add je-src.zip in the lib folder a 'Source attachment'
    - Click on 'Apply and Close'
4. In the 'Package Explorer' of Eclipse, click on src > com.sleepycat.sample > ParkingDemo.java
5. Right click on the main method and run as a java application

### Manually
The shared source code has been tested under Windows with Eclipse. The "BASE" API is working, which is enough for the small project. Other APIs are not tested.

Here are the main steps:

1. From the official website (https://www.oracle.com/database/technologies/related/berkeleydb.html):
    - download the Berkeley DB Java Edition, unzip it
    - download Parking lot example code bundle, unzip it
2. download the updated source code of the Parking lot example (src.zip) available in the Moodle space of our course
3. replace the src folder in the Parking lot example by the updated version
4. Import the sample project
5. In the Java Build Path of the project, Add an external JAR "je-7.5.11.jar", which is located in the folder ...\V1006774-01\je-7.5.11\lib (inside the downloaded Berkeley DB Java Edition)
6. Build the project and run the ParkingDemo.

### Expected Program Out For ParkingDemo.java
Below is an example output of the program.

======= Simulation starts =======\
car4 has entered the parkinglot and got ticket 1701\
car4 has left the parking lot and paid 3 dollar(s) 58 cent(s)\
car1 has entered the parkinglot and got ticket 1702\
car2 has entered the parkinglot and got ticket 1703\
car3 has entered the parkinglot and got ticket 1704\
car2 has left the parking lot and paid 1 dollar(s) 38 cent(s)\
car1 has left the parking lot and paid 23 dollar(s) 96 cent(s)\
car5 has entered the parkinglot and got ticket 1705\
car5 has left the parking lot and paid 4 dollar(s) 78 cent(s)\

======= Simulation ends =======\
Total number of cars entered on 2014-12-20: 4\
Total fees collected on 2014-12-20: 28 dollar(s) 92 cent(s).\
