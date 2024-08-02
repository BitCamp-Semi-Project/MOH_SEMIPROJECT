package jdbc;

import users.UsersDTO;

import java.sql.*;
import java.util.Map;
import java.util.TreeMap;

public class JdbcDAO {
    private final String driver = "oracle.jdbc.driver.OracleDriver";
    private final String url = "jdbc:oracle:thin:@localhost:1521:XE";
    private final String user = "C##java";
    private final String password = "oracle";

    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    private static JdbcDAO instance = new JdbcDAO();

    public JdbcDAO() {
        try{
            Class.forName(driver);
            System.out.println("driver loading");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    public static JdbcDAO getInstance() {
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


    // USERS
    public boolean checkDuplicate(String id){
        this.getConnection();
        boolean exist = false;
        try{
            pstmt = conn.prepareStatement("select * from users where user_id=?");
            pstmt.setString(1,id);

            rs = pstmt.executeQuery();
            if(rs.next()){
                exist = true;
                System.out.println("중복된 아이디입니다.");
            }
            else{
                System.out.println("사용 가능한 아이디입니다.");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        closeConnection();
        return exist;
    }

    public void checkUser(String id, String password){
        boolean exist = false;
        try{
            pstmt = conn.prepareStatement("select * from users where user_id=? AND password=?");
            pstmt.setString(1,id);
            pstmt.setString(2,password);

            rs = pstmt.executeQuery();

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public String join(UsersDTO usersDTO){
        this.getConnection();
        try{
            checkUser(usersDTO.getUser_id(), usersDTO.getPassword());
            rs = pstmt.executeQuery();

            if(rs.next()){
                System.out.println("이미 기존에 있는 회원입니다.");
            }
            else{
                try{
                    pstmt = conn.prepareStatement("INSERT INTO users (user_id, password, age) VALUES (?, ?, ?)");
                    pstmt.setString(1, usersDTO.getUser_id());
                    pstmt.setString(2, usersDTO.getPassword());
                    pstmt.setInt(3, usersDTO.getAge());


                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        System.out.println("회원 가입이 완료되었습니다.\n");
                    } else {
                        System.out.println("회원가입에 실패하였습니다.\n");
                        usersDTO.setUser_id("");
                    }
                } catch (SQLException e){
                    e.printStackTrace();
                }

            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        closeConnection();
        return usersDTO.getUser_id();
    }

    public String login(String id, String password){
        this.getConnection();
        try{
            checkUser(id,password);
            rs = pstmt.executeQuery();

            if(rs.next()){
                System.out.println("로그인에 성공합니다.\n");
            }
            else{
                System.out.println("존재하지 않는 회원입니다.\n");
                id="";
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
        return id;
    }

    public void showTheaterList(){
        this.getConnection();
        try{
            pstmt = conn.prepareStatement("select * from theater");
            rs = pstmt.executeQuery();

            int i= 1;
            while(rs.next()){
                System.out.println(rs.getString("THEATER_ID")+". "+rs.getString("NAME")
                        +" ("+rs.getString("LOCATION")+")");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        closeConnection();
    }
    public void SearchTheaterId(int inputId){
        boolean exist=false;
        this.getConnection();
        try{
            pstmt = conn.prepareStatement("select theater_id from theater where theater_id=?");
            pstmt.setInt(1, inputId);
            rs = pstmt.executeQuery();

            while(rs.next()){
                System.out.println(rs.next());
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        closeConnection();
    }
    public int searchUserAge(String userId){
        this.getConnection();
        int age=0;
        try{
            pstmt = conn.prepareStatement("select age from users where user_id = ?");
            pstmt.setString(1, userId);

            rs = pstmt.executeQuery();
            while(rs.next()){
                age = rs.getInt("age");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        closeConnection();
        return age;
    }

    public int searchMovieLimitAge(int movieId){
        this.getConnection();
        int limitAge = 0;
        try{
            pstmt = conn.prepareStatement("select limit_age from movie where movie_id = ?");
            pstmt.setInt(1, movieId);

            rs = pstmt.executeQuery();
            while(rs.next()){
                limitAge = rs.getInt("limit_age");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        closeConnection();
        return limitAge;
    }

    public int getAvailableSeatsCount(int showtimeId) {
        this.getConnection();
        int availableSeats = 0;
        try {
            pstmt = conn.prepareStatement("SELECT COUNT(*) AS available_seats FROM Showtime_Seat WHERE showtime_id = ? AND status = 1");
            pstmt.setInt(1, showtimeId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                availableSeats = rs.getInt("available_seats");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
        return availableSeats;
    }


    public void showMovieList(){
        this.getConnection();
        try{
            pstmt = conn.prepareStatement("select * from movie");
            rs = pstmt.executeQuery();


            while(rs.next()){
                System.out.println(rs.getString("MOVIE_ID")+". "+rs.getString("TITLE")
                +" ( 장르 : "+rs.getString("GENRE")+", "+rs.getString("LIMIT_AGE")+"세 이용 관가)");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        closeConnection();
    }
    public void showTimeTable(){
        this.getConnection();
        try{
            pstmt = conn.prepareStatement("SELECT TO_CHAR(start_time, 'HH24:MI') " +
                    "AS start_time FROM showTime GROUP BY TO_CHAR(start_time, 'HH24:MI')");

            rs = pstmt.executeQuery();
            int i=1;
            while (rs.next()) {
                System.out.println("상영 시작 시간 "+i+". "+  rs.getString("start_time")); i++;
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        closeConnection();
    }

    public int getShowtimeId(int movieId, int theaterId, String timeString) {
        int showtimeId = -1;
        this.getConnection();
        try {
            String query = "SELECT showtime_id FROM Showtimes_View WHERE movie_id = ? AND theater_id = ? AND start_time_str = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, movieId);
            pstmt.setInt(2, theaterId);
            pstmt.setString(3, timeString);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                showtimeId = rs.getInt("showtime_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
        return showtimeId;
    }

    public void showSeatsTable(int showTimeId){
        this.getConnection();
        try {
            pstmt = conn.prepareStatement("SELECT s.seat_row, s.seat_number, " +
                    "CASE WHEN ss.status = 1 THEN 'ㅁ' ELSE '*' END AS seat_status " +
                    "FROM Seats s " +
                    "LEFT JOIN Showtime_Seat ss ON s.seat_id = ss.seat_id " +
                    "WHERE ss.showtime_id = ? " +
                    "ORDER BY s.seat_row, s.seat_number");

            pstmt.setInt(1, showTimeId);
            rs = pstmt.executeQuery();

            // Initialize a map to store seat information
            Map<String, Map<Integer, String>> seatMap = new TreeMap<>();

            while (rs.next()) {
                String seatRow = rs.getString("seat_row");
                int seatNumber = rs.getInt("seat_number");
                String seatStatus = rs.getString("seat_status");

                // Initialize row if not already present
                seatMap.putIfAbsent(seatRow, new TreeMap<>());
                // Store seat status in the map
                seatMap.get(seatRow).put(seatNumber, seatStatus);
            }

            // Print the seat numbers at the top
            System.out.print("    "); // Padding for row labels
            for (int i = 1; i <= 20; i++) {
                System.out.print(i + " ");
            }
            System.out.println();

            // Print the seats in the desired format
            for (Map.Entry<String, Map<Integer, String>> rowEntry : seatMap.entrySet()) {
                String seatRow = rowEntry.getKey();
                System.out.print(seatRow + "   ");
                for (int i = 1; i <= 20; i++) {
                    String seatStatus = rowEntry.getValue().getOrDefault(i, "ㅁ");
                    System.out.print(seatStatus + " ");
                }
                System.out.println();
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        closeConnection();
    }

    public boolean updateSeatStatus(int showtimeId, String seatRow, int seatNumber, String userId, int theaterId) {
        this.getConnection();
        boolean result = true;
        try {
            pstmt = conn.prepareStatement("SELECT seat_id FROM Seats WHERE seat_row = ? AND seat_number = ? AND theater_id=?");
            System.out.println(seatRow.toUpperCase()+" "+ seatNumber);
            pstmt.setString(1, seatRow.toUpperCase());
            pstmt.setInt(2, seatNumber);
            pstmt.setInt(3, theaterId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int seatId = rs.getInt("seat_id");

                // Find the showtime_seat_id for the given showtimeId and seatId
                pstmt = conn.prepareStatement("SELECT showtime_seat_id,status FROM Showtime_Seat WHERE showtime_id = ? AND seat_id = ?");
                pstmt.setInt(1, showtimeId);
                pstmt.setInt(2, seatId);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    int showtimeSeatId = rs.getInt("showtime_seat_id");
                    int seatStatus = rs.getInt("status");

                    if (seatStatus == 0) {
                        System.out.println("이미 예약된 좌석입니다. 다른 좌석을 선택해주세요.");
                        result = false;
                    }
                    else{
                        pstmt = conn.prepareStatement("UPDATE Showtime_Seat SET status = 0 WHERE showtime_seat_id = ?");
                        pstmt.setInt(1, showtimeSeatId);
                        pstmt.executeUpdate();

                        pstmt = conn.prepareStatement("INSERT INTO Reservation (reservation_id, showtime_seat_id, user_id, reservation_time, status) " +
                                "VALUES (reservation_seq.NEXTVAL, ?, ?, SYSTIMESTAMP, 1)");
                        pstmt.setInt(1, showtimeSeatId);
                        pstmt.setString(2, userId);
                        pstmt.executeUpdate();
                    }

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
        return result;
    }


}
