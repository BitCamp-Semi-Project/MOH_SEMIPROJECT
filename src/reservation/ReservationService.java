package reservation;

public interface ReservationService {
    public void selectTheater();

    public void selectMovie();

    public void selectShowTime();

    public boolean insertPeopleNumber(String userId);

    public void selectSeats(String userId);

    public void calCharge();

    public void executeReservation(String userId);
}