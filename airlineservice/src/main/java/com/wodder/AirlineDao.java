package com.wodder;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class AirlineDao {

    private final MongoClient mongoClient;
    private MongoDatabase vacationDb;
    private MongoCollection<Document> airlineCollection;
    private MongoCollection<Document> reservations;

    public AirlineDao() {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://191.168.0.25:27017"));
    }

    public void connectToCollection() {
        vacationDb = mongoClient.getDatabase("vacation_db");
        airlineCollection = vacationDb.getCollection("airlines");
        reservations = vacationDb.getCollection("reservations");
    }

    public List<String> getAllAirlines() {
        FindIterable<Document> flights = airlineCollection.find();
        return mapDocumentToReservations(flights);
    }

    private List<String> mapDocumentToReservations(FindIterable<Document> flights) {
        List<String> results = new ArrayList<>();
        for (Document d : flights) {
            AirlineReservation r = new AirlineReservation();
            r.setAirline(d.getString("name"));
            r.setOrigin(d.getString("from"));
            r.setDest(d.getString("to"));
            r.setBooked(!d.getString("booked").equalsIgnoreCase("n"));
            r.setFlightId(d.getInteger("flight"));
            results.add(r.toString());
        }
        return results;
    }

    public boolean bookFlight(long reservationNum, String name) {
        Document result = airlineCollection.find(Filters.eq("flight", (int) reservationNum)).first();
        if (result != null) {
            if (result.getString("booked").equalsIgnoreCase("N")) {
                UpdateResult ur = airlineCollection.updateOne(result, Updates.set("booked", "Y"));
                result.put("booked", "Y");
                updateReservation(name, result);
                return ur.wasAcknowledged();
            }
        }
        return false;
    }

    private void updateReservation(String name, Document flight) {
        Document result = reservations.find(Filters.eq("name", name)).first();
        if (result != null) {
            reservations.updateOne(result, Updates.set("flight", flight));
        } else {
            Document newReservation = new Document("_id", new ObjectId());
            newReservation.append("name", name);
            newReservation.append("flight", flight);
            reservations.insertOne(newReservation);
        }
    }

    public boolean removeReservation(long reservationNum) {
        Document flight = airlineCollection.find(Filters.eq("flight", (int) reservationNum)).first();
        if (flight != null) {
            if (flight.getString("booked").equalsIgnoreCase("Y")) {
                UpdateResult ur = airlineCollection.updateOne(flight, Updates.set("booked", "N"));
                removeFromReservation(reservationNum);
                return ur.wasAcknowledged();
            }
        }
        return false;
    }

    private void removeFromReservation(long reservationNum) {
        Document reservation = reservations.findOneAndUpdate(Filters.eq("flight.flight", reservationNum), Updates.unset("flight"));
    }
}
