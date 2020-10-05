FROM maven:3.6.3-adoptopenjdk-8 as build

COPY ./carservice/src /VacationApp/carservice/src
COPY ./carservice/pom.xml /VacationApp/carservice/

COPY ./airlineservice/src /VacationApp/airlineservice/src
COPY ./airlineservice/pom.xml /VacationApp/airlineservice/

COPY ./hotelservice/src /VacationApp/hotelservice/src
COPY ./hotelservice/pom.xml /VacationApp/hotelservice/

COPY ./loggingservice/src /VacationApp/loggingservice/src
COPY ./loggingservice/pom.xml /VacationApp/loggingservice/

COPY ./client/src /VacationApp/client/src
COPY ./client/pom.xml /VacationApp/client/

COPY ./pom.xml /VacationApp/

WORKDIR /VacationApp/

RUN mvn package -pl hotelservice

EXPOSE 55003

ENTRYPOINT ["java","-jar","/VacationApp/hotelservice/target/hotelservice-1.0-SNAPSHOT.jar"]


