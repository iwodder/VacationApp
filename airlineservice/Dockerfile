FROM maven:3.6.3-adoptopenjdk-8 as build

COPY ./carservice/src /VacationApp/carservice/src
COPY ./carservice/pom.xml /VacationApp/carservice/

COPY ./airlineservice/src /VacationApp/airlineservice/src
COPY ./airlineservice/pom.xml /VacationApp/airlineservice/

COPY ./hotelservice/src /VacationApp/hotelservice/src
COPY ./hotelservice/pom.xml /VacationApp/hotelservice/

COPY ./client/src /VacationApp/client/src
COPY ./client/pom.xml /VacationApp/client/

COPY ./pom.xml /VacationApp/

WORKDIR /VacationApp/

RUN mvn package -pl airlineservice

EXPOSE 55001

ENTRYPOINT ["java","-jar","/VacationApp/airlineservice/target/airlineservice-1.0-SNAPSHOT.jar"]
