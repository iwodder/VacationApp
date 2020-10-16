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

public class HotelDao {

    private final MongoClient mongoClient;
    private MongoDatabase vacationDb;
    private MongoCollection<Document> hotelCollection;
    private MongoCollection<Document> reservations;

    public HotelDao() {
        mongoClient = new MongoClient(new MongoClientURI("mongodb://191.168.0.25:27017"));
    }

    public void connectToCollection() {
        vacationDb = mongoClient.getDatabase("vacation_db");
        hotelCollection = vacationDb.getCollection("hotels");
        reservations = vacationDb.getCollection("reservations");
    }

    public List<String> getAllHotels() {
        FindIterable<Document> results = hotelCollection.find();
        return mapCollectionToHotel(results);
    }

    private List<String> mapCollectionToHotel(FindIterable<Document> collection) {
        List<String> results = new ArrayList<>();
        for (Document d : collection) {
            HotelReservation hotelReservation = new HotelReservation();
            hotelReservation.setHotelId(d.getInteger("Hotel"));
            hotelReservation.setName(d.getString("Name"));
            hotelReservation.setCity(d.getString("City"));
            hotelReservation.setState(d.getString("State"));
            hotelReservation.setVacancy(d.getString("Vacancy").equalsIgnoreCase("Y"));
            hotelReservation.setRooms(Integer.parseInt(d.getString("Rooms")));
            results.add(hotelReservation.toString());
        }
        return results;
    }

    public boolean bookHotel(long reservationNum, String name) {
        Document result = hotelCollection.find(Filters.eq("Hotel", (int) reservationNum)).first();
        if (result != null) {
            if (result.getString("Vacancy").equalsIgnoreCase("Y")) {
                int rooms = Integer.parseInt(result.getString("Rooms"));
                if (rooms > 0) {
                    rooms -= 1;
                    UpdateResult ur = hotelCollection.updateOne(result, Updates.set("Rooms", String.valueOf(rooms)));
                    if (rooms == 0) {
                        hotelCollection.updateOne(result, Updates.set("Vacancy", "N"));
                    }
                    updateReservation(name, result);
                    return ur.wasAcknowledged();
                }
            }
        }
        return false;
    }

    private void updateReservation(String name, Document hotel) {
        Document result = reservations.find(Filters.eq("name", name)).first();
        if (result != null) {
            reservations.updateOne(result, Updates.set("hotel", hotel));
        } else {
            Document newReservation = new Document("_id", new ObjectId());
            newReservation.append("name", name);
            newReservation.append("hotel", hotel.getInteger("hotel"));
            reservations.insertOne(newReservation);
        }
    }

    public boolean removeReservation(long reservationNum) {
        Document hotel = hotelCollection.find(Filters.eq("hotel", (int) reservationNum)).first();
        if (hotel != null) {
            int rooms = hotel.getInteger("rooms");
            if (rooms == 0) {
                hotelCollection.updateOne(hotel, Updates.set("vacancy", "Y"));
            }
            UpdateResult ur = hotelCollection.updateOne(hotel, Updates.set("rooms", ++rooms));
            removeFromReservation(reservationNum);
            return ur.wasAcknowledged();
        }
        return false;
    }

    private void removeFromReservation(long reservationNum) {
        Document reservation = reservations.findOneAndUpdate(Filters.eq("hotel.hotel", reservationNum), Updates.unset("hotel"));
    }
}

