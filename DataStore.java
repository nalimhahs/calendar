import java.util.ArrayList;
import java.util.Date;

import java.sql.*;

public class DataStore {

    static Connection con = null;

    DataStore() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://sql12.freemysqlhosting.net:3306/sql12356243", "sql12356243", "i4MHL3KZe6");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String args[]) {

        DataStore db = new DataStore();
        db.fetch(new Date());
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void add(String text, Date date) {
        try {
            PreparedStatement pstmt = con.prepareStatement("insert into events (edesc, edate) " + " values (?, ?)");
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            pstmt.setString(1, text);
            pstmt.setDate(2, sqlDate);
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void delete(Event event) {
        try {
            PreparedStatement pstmt = con.prepareStatement("delete from events where eid=(?)");
            pstmt.setInt(1, event.id);
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    ArrayList<Event> fetch(Date date) {
        ArrayList<Event> events = new ArrayList<Event>();
        try {
            PreparedStatement pstmt = con.prepareStatement("select * from events where edate=(?)");
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            pstmt.setDate(1, sqlDate);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
                events.add(new Event(rs.getInt(1), rs.getDate(2), rs.getString(3)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    void close() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class Event {
    int id;
    Date date;
    String event_description;

    Event(int i, Date d, String e) {
        date = d;
        event_description = e;
        id = i;
    }
}
