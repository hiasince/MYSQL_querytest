import java.sql.*;
import java.io.*;
import java.util.*;

class pProcedureOne {
  public static void main(String[] args) {
        Connection conn = null;
        String mysql = "jdbc:mysql://10.161.142.65:13306/nwind7?useServerPrepStmts=true&useLocalSessionState=true";
        String id = "sd_test";
        String pw = "nhn123!@#";
        //prepare call을 위한 CallableStatement 객체
        CallableStatement cst= null;

        try {
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("Found Driver");
                conn = DriverManager.getConnection(mysql, id, pw);
                System.out.println("Connection Success");

                //Prepare Call에 prepare 하는 구문
                cst = conn.prepareCall("{call usp_tst_tbl(?, ?, ?)}");

                long StartTime = System.nanoTime();
                for(int i = 1; i <= 100000; i++) {
                        //Prepare Call execute 하는 구문
                        cst.setInt("_sno", (int)(Math.random() * 10000));
                        cst.setString("_uuid1", UUID.randomUUID().toString().replace("-",""));
                        cst.setString("_uuid2", UUID.randomUUID().toString().replace("-",""));
                        cst.execute();
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
                        cst.close();
                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }
  }
}
