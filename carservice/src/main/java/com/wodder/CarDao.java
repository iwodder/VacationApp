package com.wodder;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class CarDao {

    private final MongoClient mongoClient;
    private MongoDatabase vacationDb;
    private MongoCollection<Document> carCollection;
    private MongoCollection<Document> reservations;

    public CarDao() {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://191.168.0.25:27017"));
    }

    public void connectToCollection() {
        vacationDb = mongoClient.getDatabase("vacation_db");
        carCollection = vacationDb.getCollection("cars");
        reservations = vacationDb.getCollection("reservations");
    }

    public List<String> getAllCars() {
        FindIterable<Document> results = carCollection.find();
        return mapCollectionToCar(results);
    }

    private List<String> mapCollectionToCar(FindIterable<Document> collection) {
        List<String> results = new ArrayList<>();
        for (Document d : collection) {
            CarReservation c = new CarReservation();
            c.setMake(d.getString("MAKE"));
            c.setModel(d.getString("MODEL"));
            c.setId(Integer.parseInt(d.getString("CAR")));
            c.setType(d.getString("TYPE"));
            c.setBooked(d.getString("BOOKED").equalsIgnoreCase("Y"));
            results.add(c.toString());
        }
        return results;
    }

    public boolean bookCar(long reservationNum, String name) {
        Document result = carCollection.find(Filters.eq("flight", (int) reservationNum)).first();
        if (result != null) {
            if (result.getString("BOOKED").equalsIgnoreCase("N")) {
                UpdateResult ur = carCollection.updateOne(result, Updates.set("booked", "Y"));
                result.put("booked", "Y");
                updateReservation(name, result);
                return ur.wasAcknowledged();
            }
        }
        return false;
    }

    private void updateReservation(String name, Document car) {
        Document result = reservations.find(Filters.eq("name", name)).first();
        if (result != null) {
            reservations.updateOne(result, Updates.set("car", car));
        } else {
            Document newReservation = new Document("_id", new ObjectId());
            newReservation.append("name", name);
            newReservation.append("car", car);
            reservations.insertOne(newReservation);
        }
    }

    public boolean removeReservation(long reservationNum) {
        Document car = carCollection.find(Filters.eq("CAR", (int) reservationNum)).first();
        if (car != null) {
            if (car.getString("booked").equalsIgnoreCase("Y")) {
                UpdateResult ur = carCollection.updateOne(car, Updates.set("booked", "N"));
                removeFromReservation(reservationNum);
                return ur.wasAcknowledged();
            }
        }
        return false;
    }

    private void removeFromReservation(long reservationNum) {
        Document reservation = reservations.findOneAndUpdate(Filters.eq("car.car", reservationNum), Updates.unset("car"));
    }
}
