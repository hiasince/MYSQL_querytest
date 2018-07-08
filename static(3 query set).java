import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.io.*;
import java.util.*;

class compareProcedure {
  public static void main(String[] args) {
        java.util.Date today = new java.util.Date();
        System.out.println(today);

        Connection conn = null;
        Statement st = null;
        String mysql = "jdbc:mysql://10.161.142.65:13306/nwind7?allowMultiQueries=true";
        String id = "sd_test";
        String pw = "nhn123!@#";

        try {
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("Found Driver");
                conn = DriverManager.getConnection(mysql, id, pw);
                System.out.println("Connection Success");
                st = conn.createStatement();
                String SQL = "";
                long StartTime = System.nanoTime();
                for(int i = 1; i <= 100000; i++) {
                        SQL = "insert tst_tbl (sno, item_id) values ("+ (int)(Math.random() * 10000) +",'" + UUID.randomUUID().toString().replace("-","") +"'); select * from tst_tbl where seq = LAST_INSERT_ID(); update tst_tbl set item_id = '" + UUID.randomUUID().toString().replace("-","") + "' where seq = LAST_INSERT_ID();";
                        st.executeUpdate(SQL);
                        if(i%10000 == 0)
                                System.out.println(i/10000 + "0 %");
                }
                long EndTime = System.nanoTime();
                double output = (EndTime - StartTime) / 1000000000.0;
                System.out.println("Execute Time : " + output);

        } catch(Exception e) {
                e.printStackTrace();
        } finally {
                try {
                        conn.close();
                        st.close();
                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }
        today = new java.util.Date();
        System.out.println(today);

  }
}
