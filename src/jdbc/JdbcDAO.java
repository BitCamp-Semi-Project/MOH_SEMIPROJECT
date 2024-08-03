package jdbc;

import users.UsersDTO;
import reservation.ReservationDTO;

import java.sql.*;

public class JdbcDAO {
    private static JdbcDAO instance = new JdbcDAO();

    private final String driver = "oracle.jdbc.driver.OracleDriver";
    private final String url = "jdbc:oracle:thin:@localhost:1521:xe";
    private final String user = "---";      //최종 반영 이후 내용 지움
    private final String password = "----"; //최종 반영 이후 내용 지움

    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    public JdbcDAO() {
        try {
            Class.forName(driver);
            System.out.println("driver loading");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static JdbcDAO getInstance() {
        return instance;
    }

    public void getConnection() {
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
            System.out.println("connection closed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void checkDuplicate(String id) {
        try {
            pstmt = conn.prepareStatement("select * from users where user_id=?");
            pstmt.setString(1, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void join(UsersDTO usersDTO) {
        this.getConnection();
        try {
            checkDuplicate(usersDTO.getUser_id());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("이미 기존에 있는 회원입니다.");
            } else {
                try {
                    pstmt = conn.prepareStatement("INSERT INTO users (user_id, password, age) VALUES (?, ?, ?)");
                    pstmt.setString(1, usersDTO.getUser_id());
                    pstmt.setString(2, usersDTO.getPassword());
                    pstmt.setInt(3, usersDTO.getAge());

                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        System.out.println("회원 가입이 완료되었습니다.\n");
                    } else {
                        System.out.println("회원가입에 실패하였습니다.\n");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public void login(String id, String password) {
        this.getConnection();
        try {
            checkDuplicate(id);
            rs = pstmt.executeQuery();

            if (rs.next() && rs.getString("password").equals(password)) {
                System.out.println("로그인에 성공합니다.\n");
            } else {
                System.out.println("존재하지 않는 회원이거나 비밀번호가 틀립니다.\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public void showTheaterList() {
        this.getConnection();
        try {
            pstmt = conn.prepareStatement("select * from theater");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("THEATER_ID") + ". " + rs.getString("NAME")
                        + " (" + rs.getString("LOCATION") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public void showMovieList() {
        this.getConnection();
        try {
            pstmt = conn.prepareStatement("select * from movie");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString("MOVIE_ID") + ". " + rs.getString("TITLE")
                        + " ( 장르 : " + rs.getString("GENRE") + ", " + rs.getString("LIMIT_AGE") + "세 이용 관가)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public void showTimeTable() {
        this.getConnection();
        try {
            pstmt = conn.prepareStatement("SELECT TO_CHAR(start_time, 'HH24:MI') AS start_time FROM showTime GROUP BY TO_CHAR(start_time, 'HH24:MI')");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("상영 시작 시간: " + rs.getString("start_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
    }

    public void showSeatsTable(int showTimeId) {
        this.getConnection();
        try {
            pstmt = conn.prepareStatement("SELECT s.seat_row, s.seat_number, " +
                    "CASE WHEN r.status = 1 THEN '*' ELSE 'ㅁ' END AS seat_status " +
                    "FROM Seats s " +
                    "LEFT JOIN Showtime_Seat ss ON s.seat_id = ss.seat_id " +
                    "LEFT JOIN Reservation r ON ss.showtime_seat_id = r.showtime_seat_id " +
                    "WHERE ss.showtime_id = ? " +
                    "ORDER BY s.seat_row, s.seat_number;");
            pstmt.setInt(1, showTimeId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                String seatRow = rs.getString("seat_row");
                int seatNumber = rs.getInt("seat_number");
                String seatStatus = rs.getString("seat_status");
                System.out.println(seatRow + seatNumber + ": " + seatStatus);
            }
        } catch (SQLException e) {
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
}
