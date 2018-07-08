import java.sql.*;
import java.io.*;
import java.util.*;

class StaticOne {
  public static void main(String[] args) {
        Connection conn = null;
        Statement st = null;
        String mysql = "jdbc:mysql://10.161.142.65:13306/nwind7";
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
                        //INSERT 구문
                        SQL = "insert tst_tbl (sno, item_id) values ("+ (int)(Math.random() * 10000) +",'" + UUID.randomUUID().toString().replace("-","") +"')";
                        st.executeUpdate(SQL, Statement.RETURN_GENERATED_KEYS);

                        //LAST_INSERTED_ID 찾는 구문
                        ResultSet rs = st.getGeneratedKeys();
                        rs.next();
                        int last_key = rs.getInt(1);

                        //SELECT 구문
                        SQL = "select * from tst_tbl where seq = " + last_key;
                        st.executeQuery(SQL);

                        //UPDATE 구문
                        SQL = "update  tst_tbl set item_id = '" + UUID.randomUUID().toString().replace("-","") + "' where seq = " + last_key;
                        st.executeUpdate(SQL);

                        if(i%10000 == 0)
                                System.out.println(i/1000 + "0 %");
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
  }
}
