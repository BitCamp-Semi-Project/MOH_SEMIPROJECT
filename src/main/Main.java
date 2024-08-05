package main;

import reservation.ReservationService;
import reservation.ReservationServiceImpl;
import users.UsersService;
import users.UsersServiceImpl;

public class Main {

    public static void main(String[] args) {
        UsersService userService = new UsersServiceImpl();
        ReservationService reservationService = new ReservationServiceImpl();
        MainServiceImpl mainService = new MainServiceImpl(userService, reservationService);
        mainService.execute();
    }
}
