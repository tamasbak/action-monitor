# action-monitor
The application is built upon spring-boot, hence it has embedded tomcat web server, and building the application is done with built-in maven support. 

This means building and running the application is straight forward. After importing it to Eclipse IDE the following maven goal should be started:  

	spring-boot:run

The application is using Java8 (jdk 1.8.0_74). 

Once built the application can be started at: localhost:8080

When the application is started HSQL Database Manager will start additionally to make DB interaction easier.

