
namespace java com.wodder

service Cars {

    bool RemoveReservation(1:i64 reservationNum);

    bool AddReservation(1:i64 airlineId, 2:string name);

    string GetReservationList();

    list<string> GetList();

}