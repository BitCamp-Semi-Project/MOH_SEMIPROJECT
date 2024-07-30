package main;

import user.UsersService;
import user.UsersServiceImpl;

public class Main {

    public static void main(String[] args) {
        UsersService userService = new UsersServiceImpl();
        MainServiceImpl mainService = new MainServiceImpl(userService);
        mainService.execute();
    }
}
