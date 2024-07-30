package user;

import java.sql.*;

public class UsersDAO {
    private final String driver = "oracle.jdbc.driver.OracleDriver";
    private final String url = "jdbc:oracle:thin:@localhost:1521:XE";
    private final String user = "C##java";
    private final String password = "oracle";

    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    private static UsersDAO instance = new UsersDAO();

    public UsersDAO() {
        try{
            Class.forName(driver);
            System.out.println("driver loading");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    public static UsersDAO getInstance() {
        return instance;
    }

    public void getConnection(){
        try {
            conn = DriverManager.getConnection(url,user,password);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try{
            if(rs != null){rs.close();}
            if(pstmt!=null){pstmt.close();}
            if(conn!=null){conn.close();}
            System.out.println("connection closed");
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void checkDuplicate(String id, String password){
        try{
            pstmt = conn.prepareStatement("select * from users where user_id=?");
            pstmt.setString(1,id);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void join(UsersDTO usersDTO){
        this.getConnection();
        try{
            checkDuplicate(usersDTO.getUser_id(), usersDTO.getPassword());
            rs = pstmt.executeQuery();

            if(rs.next()){
                System.out.println("이미 기존에 있는 회원입니다.");
            }
            else{
                try{
                    pstmt = conn.prepareStatement("INSERT INTO users (user_id, password, age) VALUES (?, ?, ?)");
                    pstmt.setString(1, usersDTO.getUser_id());
                    pstmt.setString(2, usersDTO.getPassword());
                    pstmt.setString(3, String.valueOf(usersDTO.getAge()));


                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        System.out.println("회원 가입이 완료되었습니다.");
                    } else {
                        System.out.println("회원가입에 실패하였습니다.");
                    }
                } catch (SQLException e){
                    e.printStackTrace();
                }

            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        closeConnection();
    }

    public void login(String id, String password){
        this.getConnection();
        try{
            checkDuplicate(id,password);
            rs = pstmt.executeQuery();

            if(rs.next()){
                System.out.println("로그인에 성공합니다.");
            }
            else{
                System.out.println("존재하지 않는 회원입니다.");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

}
