### OrientDB POJO Schema Annotation
Provide a Java POJO Annotation capability to enforce Schema for
the Orient NoSql Database.

Although OrientDB is by it's nature is schema-less, but when you need
to apply schema, this annotation library may help with that definition and enforcement.

Just like existing JPA/ORM frameworks, this library allows for
annotation of your Java POJOs to be used as schema definition for the
Orient NoSql Database.

#### Requirements
* Java 8
* Maven 3
* Orient DB Community Version, at least 2.2.12, which can be downloaded [here][1].


#### OrientDB Setup
* You will need a Java JDK to build and run the Test per your Environment.
* Download Orient DB Community from link provided above.
* Install as per Orient Documentation.
** In specifying the credentials for the root admin, the Test Application is set to '__orientdb__'.
   Use that password for testing, or you will need to specify the correct password in the next step.
* Start the Server instance.
* Verify the Server is started, by using the following command:
  
  ```
     ps-ef | grep orient
  ```
  
* Now that OrientDB is Running you can access the Studio IDE by going to
  [http://localhost:2480](http://localhost:2480).

* You should be able to login using the root admin and associated credentials you specified during the
  OrientDB installation process.
  
* If you do not have OrientDB up, go back and do so...  

#### Test Application Example Setup
* We need to modify the Test
  ```
    src/test/resources/application.properties
  ```
  to:
  * Specify our OrientDB credentials, 
    if you changed the password to something other than '__orientdb__'.
  * Specify the Name of the DB you wish to be created during the execution of the Test.
  * Now run the Test in either:
   * In your IDE run src/test/java/jeffaschenk/orientdb/Test
   * On Command Line Simply issue:
     ```
       mvn clean package
     ```  
  * Now using the OrientDB IDE, you can view the updated Schema for your Database which were applied based upon the Annotated Entity Classes.
  
  * See [here][2] for a sample log output of the schema enforcer bootstrap.
  
  * See ![Schema of new DB Prior to Example Test][Schema_Prior]


#### Examples
Currently see src/test/java/jeffaschenk/examples/model/entities/ for data model entities annotated to enforce schemata for OrientDB Database instance.


#### OrientDB Releases
I would recommend the following in using OrientDB:
* Stay as current as possible, as Orient is always making enhancements and providing necessary fixes.  
* Use the latest version 2.2.13+, as it has great enhancements and fixes past than previous 1.x or 2.1 versions.




[1]:http://orientdb.com/download/
[2]:https://github.com/jaschenk/orientdb-annotations/blob/master/doc/sample_Test_output.txt
[Schema_Prior]
