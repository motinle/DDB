package demo.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;



/**
 * @author mycat
 *
 */
public class TestClass1 {

	static int KInsertTime = 5;
	
    public static void main( String args[] ) throws SQLException , ClassNotFoundException {
        String jdbcdriver="com.mysql.jdbc.Driver";
//        String jdbcurl="jdbc:mysql://127.0.0.1:8066/TESTDB?useUnicode=true&characterEncoding=utf-8";
        String jdbcurl="jdbc:mysql://localhost:8066/test?useUnicode=true&characterEncoding=utf-8";
        String username="root";
        String password="123456";
        System.out.println("开始连接mysql:"+jdbcurl);
        Class.forName(jdbcdriver);
        Connection c = DriverManager.getConnection(jdbcurl,username,password); 
        c.setAutoCommit(false);// 设置事务为非自动提交 
        
        //insert test
//        NotBatch->cast(1000) : 1 s
//        NotBatch->cast(1000) : 0 s
//        NotBatch->cast(1000) : 0 s
        
//        NotBatch->cast(5000) : 4 s
//        NotBatch->cast(5000) : 3 s
//        NotBatch->cast(5000) : 3 s
        
//        NotBatch->cast(20000) : 15 s
//        NotBatch->cast(20000) : 14 s
//        NotBatch->cast(20000) : 13 s
           
        
//        NotBatch->cast(200000) : 153 s
//        NotBatch->cast(200000) : 150 s
//        NotBatch->cast(200000) : 140 s
        
        
        
        //NotBatch  
        Statement st = c.createStatement();
        Long begin = new Date().getTime();  
        for(int i = 0;i< KInsertTime;i++)
        {
        	String sql = String.format("INSERT INTO hotnews(title,author_id,create_tm,content) VALUES ('%s',911,'2015-06-09 12:32:30','%s')", "PreparedStatement","no");
        	st.execute(sql);
        }
        st.close();
        Long end = new Date().getTime();  
        System.out.printf("NotBatch->cast(%d) : " + (end - begin) / 1000 + " s\n",KInsertTime);  
        
        
        //Statement
        st = c.createStatement();
        begin = new Date().getTime();
        for (int j = 1; j <= KInsertTime; j++) {  
        	String sql = String.format("INSERT INTO hotnews(title,author_id,create_tm,content) VALUES ('%s',911,'2015-06-09 12:32:30','%s')", "PreparedStatement","no");
        	st.addBatch(sql);
        }
        st.executeBatch();
        st.close();
        c.commit();
        end = new Date().getTime();  
        System.out.printf("NotBatch->cast(%d) : " + (end - begin) / 1000 + " s\n",KInsertTime); 
        
      //PreparedStatement
        try {  
        	begin = new Date().getTime();
            PreparedStatement pst = c.prepareStatement("INSERT INTO hotnews(title,author_id,create_tm,content) VALUES (?,911,'2015-06-09 12:32:30',?)");
            for (int j = 1; j <= KInsertTime; j++) {  
            	pst.setString(1,"PreparedStatement");
            	pst.setString(2,"yes");  
            	pst.addBatch();
            }
            pst.executeBatch();  
            c.commit();  
            end = new Date().getTime(); 
            System.out.printf("NotBatch->cast(%d) : " + (end - begin) / 1000 + " s\n",KInsertTime); 
        } catch (SQLException e) {  
            e.printStackTrace();  
        } 
        System.out.println("OK......");
        
        st = c.createStatement();
        print( "test jdbc " , st.executeQuery("select count(*) as cou from hotnews"));// where id = 202122 
        c.close();
    }

         static void print( String name , ResultSet res )
                    throws SQLException {
                    System.out.println( name);
                    ResultSetMetaData meta=res.getMetaData();                       
                    //System.out.println( "\t"+res.getRow()+"条记录");
                    String  str="";
                    for(int i=1;i<=meta.getColumnCount();i++){
                        str+=meta.getColumnName(i)+"   ";
                        //System.out.println( meta.getColumnName(i)+"   ");
                    }
                    System.out.println("\t"+str);
                    str="";
                    while ( res.next() ){
                        for(int i=1;i<=meta.getColumnCount();i++){  
                            str+= res.getString(i)+"   ";       
                            } 
                        System.out.println("\t"+str);
                        str="";
                    }
                }
}
