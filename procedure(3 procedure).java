import java.sql.*;
import java.io.*;
import java.util.*;

class tProcedureOne {
  public static void main(String[] args) {

        Connection conn = null;
        String mysql = "jdbc:mysql://10.161.142.65:13306/nwind7?useServerPrepStmts=true&useLocalSessionState=true";
        String id = "sd_test";
        String pw = "nhn123!@#";

        //prepare call을 위한 CallableStatement 객체
        CallableStatement insertCst = null;
        CallableStatement selectCst = null;
        CallableStatement updateCst = null;

        try {
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("Found Driver");
                conn = DriverManager.getConnection(mysql, id, pw);
                System.out.println("Connection Success");

                long StartTime = System.nanoTime();

                //prepare call을 위한 prepare 구문
                insertCst = conn.prepareCall("{call usp_tst_tbl_insert(?, ?)}");
                selectCst = conn.prepareCall("{call usp_tst_tbl_select(?)}");
                updateCst = conn.prepareCall("{call usp_tst_tbl_update(?, ?)}");

                for(int i = 1; i <= 100000; i++) {
                        //INSERT procedure execute 구문
                        insertCst.setInt("_sno", (int)(Math.random() * 10000));
                        insertCst.setString("_uuid1", UUID.randomUUID().toString().replace("-",""));
                        insertCst.execute();

                        //LAST_INSERT_KEY 받아오는 구문
                        ResultSet rs = insertCst.getResultSet();
                        rs.next();
                        int last_key = rs.getInt(1);

                        //SELECT procedure execute 구문
                        selectCst.setInt("_seq", last_key);
                        selectCst.execute();

                        //UPDATE procedure execute 구문
                        updateCst.setInt("_seq", last_key);
                        updateCst.setString("_uuid2",UUID.randomUUID().toString().replace("-",""));
                        updateCst.execute();

                        if(i%10000 == 0)
                                System.out.println(i/1000 + "%");
                }
                long EndTime = System.nanoTime();
                double output = (EndTime - StartTime) / 1000000000.0;
                System.out.println("Execute Time : " + output);

        } catch(Exception e) {
                e.printStackTrace();
        } finally {
                try {
                        conn.close();
                        insertCst.close();
                        selectCst.close();
                        updateCst.close();
                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }
  }
}