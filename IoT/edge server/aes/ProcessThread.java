package com;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
public class ProcessThread extends Thread{
	Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
	DefaultTableModel dtm;
public ProcessThread(Socket soc,DefaultTableModel dtm){
	socket = soc;
	this.dtm = dtm;
	try{
		out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }catch(Exception e){
        e.printStackTrace();
    }
}
public void generateKey(String user)throws Exception{
	Socket soc = new Socket("localhost",3333);
	ObjectOutputStream out1 = new ObjectOutputStream(soc.getOutputStream());
	Object req[] = {"generatekey",user};
	out1.writeObject(req);
	ObjectInputStream in1 = new ObjectInputStream(soc.getInputStream());
	Object res[] = (Object[])in1.readObject();
	byte key[] = (byte[])res[0];
	DBCon.saveKey(user,key);
}

public String saveToCloud(String user,String file,byte enc[],String keywords)throws Exception{
	Socket soc = new Socket("localhost",1111);
	ObjectOutputStream out1 = new ObjectOutputStream(soc.getOutputStream());
	Object req[] = {"upload",user,file,enc,keywords};
	out1.writeObject(req);
	ObjectInputStream in1 = new ObjectInputStream(soc.getInputStream());
	Object res[] = (Object[])in1.readObject();
	String response = (String)res[0];
	return response;
}

@Override
public void run(){
	try{
		Object input[]=(Object[])in.readObject();
        String type=(String)input[0];
		if(type.equals("Register")){
			String user = (String)input[1];
			String pass = (String)input[2];
			String contact = (String)input[3];
			String mail = (String)input[4];
			String address = (String)input[5];
			String input_data[]={user,pass,contact,mail,address};
			String msg = DBCon.register(input_data);
			if(msg.equals("Registration process completed")){
				generateKey(user);
			}
			Object res[] = {msg};
			out.writeObject(res);
			Object value[] = {msg};
			dtm.addRow(value);
		}
		if(type.equals("Login")){
			String user = (String)input[1];
			String pass = (String)input[2];
			String input_data[]={user,pass};
			String msg = DBCon.login(input_data);
			Object res[] = {msg};
			out.writeObject(res);
			String res1="";
			if(!msg.equals("fail")){
				res1 = user+" login successfully";
			}
			else
				res1 = user+" login failed";
			Object value[] = {res1};
			dtm.addRow(value);
		}
		if(type.equals("logout")){
			String user = (String)input[1];
			Object res[] = {user+" logout successfully"};
			out.writeObject(res);
			Object value[] = {user+" logout successfully from server"};
			dtm.addRow(value);
		}
		if(type.equals("download")){
			String user = (String)input[1];
			String file = (String)input[2];
			byte key[] = DBCon.getKey(user);
			String hash = DBCon.getHash(file);
			Socket soc = new Socket("localhost",1111);
			ObjectOutputStream out1 = new ObjectOutputStream(soc.getOutputStream());
			Object req[] = {"download",user,file};
			out1.writeObject(req);
			ObjectInputStream in1 = new ObjectInputStream(soc.getInputStream());
			Object res[] = (Object[])in1.readObject();
			byte enc[] = (byte[])res[0];
			String hash1 = Hash.getHash(enc,key);
			if(hash.equals(hash1)){
				byte dec[] = AES.decrypt(enc,key);
				Object res1[] = {dec};
				out.writeObject(res1);
				out.flush();
				Object res2[] = {"File integrity successfull"};
				dtm.addRow(res2);
			}else{
				Object res1[] = {"File integrity failed"};
				dtm.addRow(res1);
				Object res2[] = {"File integrity failed".getBytes()};
				out.writeObject(res2);
				out.flush();
			}
		}
		if(type.equals("upload")){
			String user = (String)input[1];
			String file = (String)input[2];
			byte filedata[] = (byte[])input[3];
			String keywords = new String(filedata);
			byte key[] = DBCon.getKey(user);
			long start = System.nanoTime();
			byte encrypt[] = AES.encrypt(filedata,key);
			long end = System.nanoTime();
			StringBuilder sb = new StringBuilder();
			sb.append(user+"#"+file+"#");
			String kwords[] = keywords.split("\\s+");
			for(int i=0;i<kwords.length;i++){
				sb.append(Enc.encrypt(kwords[i])+",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(System.getProperty("line.separator"));
			String hash = Hash.getHash(encrypt,key);
			DBCon.saveHash(user,file,hash);
			String response = saveToCloud(user,file,encrypt,sb.toString());
			Object res[] = {response,(end-start)+""};
			out.writeObject(res);
			Object value[] = {file+" file saved at cloud storage"};
			dtm.addRow(value);
		}
		if(type.equals("search")){
			String query = (String)input[1];
			String arr[] = query.split("\\s+");
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<arr.length;i++){
				sb.append(Enc.encrypt(arr[i])+",");
			}
			sb.deleteCharAt(sb.length()-1);
			Socket soc = new Socket("localhost",1111);
			ObjectOutputStream out1 = new ObjectOutputStream(soc.getOutputStream());
			Object req[] = {"search",sb.toString()};
			out1.writeObject(req);
			ObjectInputStream in1 = new ObjectInputStream(soc.getInputStream());
			Object res[] = (Object[])in1.readObject();
			ArrayList<String> response = (ArrayList<String>)res[0];
			Object res1[] = {response};
			out.writeObject(res1);
			Object value[] = {"query result sent to user"};
			dtm.addRow(value);
		}
	}catch(Exception e){
        e.printStackTrace();
    }
}
}
