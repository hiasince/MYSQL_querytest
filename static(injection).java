import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class StaticOne {
  final static String UNSECURED_CHAR_REX = "[^\\p{Alnum}]|select|delete|update|insert|create|alter|drop";
  final static int MAX_UUID_LENGTH = 300;

  final static Pattern unsecuredCharPattern = Pattern.compile(UNSECURED_CHAR_REX, Pattern.CASE_INSENSITIVE);

  public static String makeSecureString(String str, int maxLength) {
                //String secureStr = str.substring(0, maxLength);
                Matcher matcher = unsecuredCharPattern.matcher(str);
                return matcher.replaceAll("");
  }

  public static int makeSecureInt(int str, int maxLength) {
        return str;
  }
  public static void main(String[] args) {
        java.util.Date today = new java.util.Date();
        System.out.println(today);

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
                for(int i = 0; i < 100000; i++) {
                        SQL = "insert tst_tbl (sno, item_id) values ("+ makeSecureInt((int)Math.random() * 10000,5) +",'" + makeSecureString(UUID.randomUUID().toString().replace("-",""), MAX_UUID_LENGTH) +"')";
                        st.executeUpdate(SQL, Statement.RETURN_GENERATED_KEYS);
                        ResultSet rs = st.getGeneratedKeys();
                        rs.next();
                        int last_key = rs.getInt(1);
                        SQL = "select * from tst_tbl where seq = " + last_key;
                        st.executeQuery(SQL);
                        SQL = "update  tst_tbl set item_id = '" + makeSecureString(UUID.randomUUID().toString().replace("-",""), MAX_UUID_LENGTH) + "' where seq = " + last_key;
                        st.executeUpdate(SQL);
                        if(i%10000 == 0)
                                System.out.println(i + "0 %");
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
