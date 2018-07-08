import java.sql.*;
import java.io.*;
import java.util.*;

class DynamicOne {
  public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement insertPst = null;
        PreparedStatement selectPst = null;
        PreparedStatement updatePst = null;
        //ServerPrepStmt = false
        String mysql = "jdbc:mysql://10.161.142.65:13306/nwind7?useServerPrepStmts=false";
        String id = "sd_test";
        String pw = "nhn123!@#";

        try {
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("Found Driver");
                conn = DriverManager.getConnection(mysql, id, pw);
                System.out.println("Connection Success");

                long StartTime = System.nanoTime();
                for(int i = 0; i < 100000; i++) {
                        //INSERT prepare 구문
                        String insertSQL = "insert tst_tbl (sno, item_id) values (?, ?)";
                        insertPst = conn.prepareStatement(insertSQL, insertPst.RETURN_GENERATED_KEYS);

                        //INSERT execute 구문
                        insertPst.setInt(1,(int)(Math.random() * 10000));
                        insertPst.setString(2,UUID.randomUUID().toString().replace("-",""));
                        insertPst.executeUpdate();

                        //LAST_INSERTED_ID 찾는구문
                        ResultSet rs = insertPst.getGeneratedKeys();
                        rs.next();
                        int last_key = rs.getInt(1);

                        //SELECT prepare 구문
                        String selectSQL = "select * from tst_tbl where seq = ?";
                        selectPst = conn.prepareStatement(selectSQL);

                        //SELECT execute 구문
                        selectPst.setInt(1, last_key);
                        selectPst.executeQuery();

                        //UPDATE prepare 구문
                        String updateSQL = "update tst_tbl set item_id = ? where seq = ?";
                        updatePst = conn.prepareStatement(updateSQL);

                        //UPDATE execute 구문
                        updatePst.setString(1,UUID.randomUUID().toString().replace("-",""));
                        updatePst.setInt(2, last_key);
                        updatePst.executeUpdate();

                        //close 구문
                        insertPst.close();
                        selectPst.close();
                        updatePst.close();

                        if(i%10000 == 0)
                                System.out.println(i%100);
                }
                long EndTime = System.nanoTime();
                double output = (EndTime - StartTime) / 1000000000.0;
                System.out.println("Execute Time : " + output);

        } catch(Exception e) {
                e.printStackTrace();
        } finally {
                try {
                        conn.close();
                        insertPst.close();
                        selectPst.close();
                        updatePst.close();
                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }
  }
}