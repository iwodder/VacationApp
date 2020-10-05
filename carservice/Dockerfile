FROM maven:3.6.3-adoptopenjdk-8 as build

COPY ./carservice/src /VacationApp/carservice/src
COPY ./carservice/pom.xml /VacationApp/carservice/

COPY ./airlineservice/src /VacationApp/airlineservice/src
COPY ./airlineservice/pom.xml /VacationApp/airlineservice/

COPY ./hotelservice/src /VacationApp/hotelservice/src
COPY ./hotelservice/pom.xml /VacationApp/hotelservice/

COPY ./client/src /VacationApp/client/src
COPY ./client/pom.xml /VacationApp/client/

COPY ./loggingservice/src /VacationApp/loggingservice/src
COPY ./loggingservice/pom.xml /VacationApp/loggingservice/

COPY ./pom.xml /VacationApp/

WORKDIR /VacationApp/

RUN mvn package -pl carservice

EXPOSE 55002

ENTRYPOINT ["java","-jar","/VacationApp/carservice/target/carservice-1.0-SNAPSHOT.jar"]
