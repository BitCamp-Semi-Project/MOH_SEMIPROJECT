package reservation;

import jdbc.JdbcDAO;
import lombok.Getter;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

@Getter
public class ReservationServiceImpl implements ReservationService{
    private Scanner sc = new Scanner(System.in);
    private int num;
    private ReservationDTO.ShowtimeIdDTO showtimeIdDTO = new ReservationDTO.ShowtimeIdDTO();
    private int showtimeId;
    private final int child = 10000;
    private final int adult = 15000;
    private int totalCharge;
    private boolean discount = false;



    @Override
    public void executeReservation(String userId) {
        ReservationServiceImpl reservationService = new ReservationServiceImpl();
        reservationService.selectTheater();
        reservationService.selectMovie();
        reservationService.selectShowTime();
        if(reservationService.insertPeopleNumber(userId)){
            reservationService.selectSeats(userId);
            reservationService.calCharge();
        }
        System.out.println();
        System.out.println("이용해주셔서 감사합니다 :)");
    }

    @Override
    public void selectTheater() {
        while(true){
            try {
                JdbcDAO.getInstance().showTheaterList();
                System.out.print("영화관을 선택하십쇼: ");
                int theaterId = sc.nextInt();
                JdbcDAO.getInstance().SearchTheaterId(theaterId);
                showtimeIdDTO.setTheaterId(theaterId); break;
            }catch (InputMismatchException e){
                System.out.println("올바른 입력값이 아닙니다. 다시 입력해주세요");
                System.out.println("************************************\n");
                sc.nextLine();
            }

        }
    }

    @Override
    public void selectMovie() {
        while(true){
            try {
                JdbcDAO.getInstance().showMovieList();
                System.out.print("영화를 선택하십쇼: ");
                int movieId = sc.nextInt();
                showtimeIdDTO.setMovieId(movieId); break;
            } catch (InputMismatchException e) {
                System.out.println("올바른 입력값이 아닙니다. 다시 입력해주세요");
                System.out.println("************************************\n");
                sc.nextLine();
            }
        }
    }

    @Override
    public void selectShowTime() {
        while(true){
            try{
                JdbcDAO.getInstance().showTimeTable();
                System.out.print("상영 시간을 선택하세요: ");
                int timeChoice;
                String timeString="";
                timeChoice = sc.nextInt();

                switch (timeChoice) {
                    case 1:
                        timeString = "08:00";
                        System.out.println("조조할인이 적용이 됩니다.");
                        discount = true;
                        break;
                    case 2:
                        timeString = "16:00";
                        break;
                    case 3:
                        timeString = "21:00";
                        break;
                    default:
                        System.out.println("메뉴에 해당하지 않는 입력값입니다. 다시 입력해주세요.");
                        break;
                }
                showtimeIdDTO.setTime(timeString);
                showtimeId = JdbcDAO.getInstance().getShowtimeId(showtimeIdDTO.getMovieId(), showtimeIdDTO.getTheaterId(), timeString);
                break;
            }catch (InputMismatchException e){
                System.out.println("올바른 입력값이 아닙니다. 다시 입력해주세요");
                System.out.println("************************************\n");
                sc.nextLine();
            }
        }

    }

    @Override
    public boolean insertPeopleNumber(String userId) {
        boolean result = false;
        int userAge = JdbcDAO.getInstance().searchUserAge(userId);
        if(userAge > 19){
            totalCharge +=adult;
        }
        else{
            totalCharge +=child;
        }
        int limitMovieAge = JdbcDAO.getInstance().searchMovieLimitAge(showtimeIdDTO.getMovieId());
        if(userAge<limitMovieAge){
            System.out.println("회원이 예약할 수 없는 관람가입니다.");
        }
        else{
            System.out.print("인원 수를 입력하세요(자신을 포함) :");
            num = sc.nextInt();

            if(num>JdbcDAO.getInstance().getAvailableSeatsCount(showtimeId)){
                System.out.println("수용할 수 없는 인원입니다.");
            }
            else{
                for(int i=0;i<num-1;i++){
                    System.out.print("동반자 " + (i+1) + "의 나이를 입력해주세요");
                    int age = sc.nextInt();
                    if(age > 19){
                        totalCharge +=adult;
                    }
                    else{
                        totalCharge +=child;
                    }
                }
                result = true;
            }
        }
        return result;
    }

    @Override
    public void selectSeats(String userId) {
        String seatInfo;
        for (int i = 0; i < num; i++) {
            boolean seatSelected = false;
            while (!seatSelected) {
                JdbcDAO.getInstance().showSeatsTable(showtimeId);

                if (i == 0) {
                    System.out.print("회원님의 좌석을 선택해주세요(대소문자 상관x): ");
                } else {
                    System.out.print("동반자 " + i + "의 좌석을 선택해주세요(대소문자 상관x): ");
                }
                seatInfo = sc.next();
                String seatRow = seatInfo.substring(0, 1).toUpperCase();
                int seatNumber = Integer.parseInt(seatInfo.substring(1));


                if(JdbcDAO.getInstance().updateSeatStatus(showtimeId, seatRow, seatNumber, userId, showtimeIdDTO.getTheaterId())){
                    seatSelected = true;
                }
            }
        }
    }
    @Override
    public void calCharge() {
        if(discount){
            System.out.println("[조조할인 50% 적용]");
            System.out.println("기존 가격 :  "+totalCharge);
            System.out.println("할인 가격 :  "+totalCharge/2);
        }else{
            System.out.println("결제 요금 : "+totalCharge);
        }
    }
}
