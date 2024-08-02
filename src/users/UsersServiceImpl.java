package users;

import jdbc.JdbcDAO;

import java.util.InputMismatchException;
import java.util.Scanner;

public class UsersServiceImpl implements UsersService {
    private Scanner scanner = new Scanner(System.in);


    @Override
    public String join() {
        String userIdResult = "";
        String userId = "";
        boolean userExists = true;
        while (true) {
            if(!userExists) break;
            System.out.print("아이디를 입력하시오 : ");
            userId = scanner.next();
            userExists = JdbcDAO.getInstance().checkDuplicate(userId);
        }


        System.out.print("비밀번호를 입력하시오 : ");
        String userPassword = scanner.next();

        while(true){
            try{
                System.out.print("나이를 입력하시오 : ");
                int age = scanner.nextInt();

                userIdResult = JdbcDAO.getInstance().join(new UsersDTO(userId, userPassword, age));
                break;
            }catch (InputMismatchException e){
                System.out.println("잘못된 형식의 나이 입력입니다.");
                scanner.nextLine();
            }
        }
        return userIdResult;
    }

    @Override
    public String login() {
        String userIdResult = "";
        System.out.print("아이디를 입력하시오 : ");
        String userId = scanner.next();

        System.out.print("비밀번호를 입력하시오 : ");
        String userPassword = scanner.next();

        userIdResult = JdbcDAO.getInstance().login(userId,userPassword);
        return userIdResult;
    }
}
