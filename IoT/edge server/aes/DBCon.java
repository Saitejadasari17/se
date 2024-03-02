package com;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
public class DBCon{
    private static Connection con;
	
public static Connection getCon()throws Exception {
    Class.forName("com.mysql.jdbc.Driver");
    con = DriverManager.getConnection("jdbc:mysql://localhost/IOT","root","root");
     return con;
}
public static void saveHash(String user,String file,String hash)throws Exception{
    con = getCon();
    PreparedStatement stat=con.prepareStatement("insert into hashdata values(?,?,?)");
	stat.setString(1,user);
	stat.setString(2,file);
	stat.setString(3,hash);
	stat.executeUpdate();
	stat.close();con.close();
}
public static String getHash(String file)throws Exception{
	String hash = null;
    con = getCon();
    Statement stmt=con.createStatement();
    ResultSet rs=stmt.executeQuery("select hash from hashdata where file='"+file+"'");
    if(rs.next()){
        hash = rs.getString(1);
    }
	rs.close();stmt.close();con.close();
    return hash;
}
public static void saveKey(String user,byte key[])throws Exception{
    con = getCon();
    PreparedStatement stat=con.prepareStatement("insert into keydata values(?,?)");
	stat.setString(1,user);
	stat.setBytes(2,key);
	stat.executeUpdate();
	stat.close();con.close();
}
public static byte[] getKey(String user)throws Exception{
    byte key[] = null;
    con = getCon();
    Statement stmt=con.createStatement();
    ResultSet rs=stmt.executeQuery("select user_key from keydata where user='"+user+"'");
    if(rs.next()){
        key = rs.getBytes(1);
    }
	rs.close();stmt.close();con.close();
    return key;
}
public static String register(String[] input)throws Exception{
    String msg="fail";
    con = getCon();
    Statement stmt=con.createStatement();
    ResultSet rs=stmt.executeQuery("select user from newuser where user='"+input[0]+"'");
    if(rs.next()){
		msg = "Username already exist";
    }else{
		PreparedStatement stat=con.prepareStatement("insert into newuser values(?,?,?,?,?)");
		stat.setString(1,input[0]);
		stat.setString(2,input[1]);
		stat.setString(3,input[2]);
		stat.setString(4,input[3]);
		stat.setString(5,input[4]);
		int i = stat.executeUpdate();
		if(i > 0){
			msg = "Registration process completed";
		}
    }
    return msg;
}
public static String login(String input[])throws Exception{
    String msg="fail";
    con = getCon();
    Statement stmt=con.createStatement();
    ResultSet rs=stmt.executeQuery("select user from newuser where user='"+input[0]+"' && pass='"+input[1]+"'");
    if(rs.next()){
        msg = rs.getString(1);
    }
    return msg;
}
}
