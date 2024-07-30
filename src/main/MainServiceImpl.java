package main;



import lombok.RequiredArgsConstructor;
import user.UsersService;

import java.util.InputMismatchException;
import java.util.Scanner;


@RequiredArgsConstructor
public class MainServiceImpl implements MainService{
    private final UsersService userService;
    private Scanner scanner = new Scanner(System.in);
    private boolean end;

    @Override
    public void execute() {
        while (true) {
            if(end){
                System.out.println("프로그램을 종료합니다.");
            }
            loginPage();
        }
    }
    public void loginPage(){
        System.out.println("*************************************");
        System.out.println("*                                   *");
        System.out.println("*        Welcome to Movie Kiosk     *");
        System.out.println("*                                   *");
        System.out.println("*************************************");
        System.out.println();
        System.out.println("1. 로그인");
        System.out.println("2. 회원가입");
        System.out.println("3. 프로그램 종료.");
        System.out.print("메뉴를 선택해주세요 : ");

        boolean exit = false;
        while(true){
            try{
                if(exit) break;
                int select = scanner.nextInt();

                switch (select){
                    case 1:
                        exit = true;
                        userService.login(); break;
                    case  2:
                        exit = true;
                        userService.join(); break;
                    case  3:
                        end = true; break;
                    default:
                        System.out.println("메뉴에 없는 입력값입니다. 다시 입력해주세요"); break;
                }
            }catch(InputMismatchException e){
                System.out.println("잘못된 입력값입니다. 다시 입력해주세요");
                scanner.nextLine();
            }
        }


    }
}
