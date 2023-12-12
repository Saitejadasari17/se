package com;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JOptionPane;
import com.jd.swing.util.Theme;
import com.jd.swing.util.PanelType;
import com.jd.swing.custom.component.panel.StandardPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import java.net.ServerSocket;
import java.net.Socket;
public class CloudServer extends JFrame{
	StandardPanel p1;
	JPanel p2,p3;
	JLabel title,l1;
	Font f1;
	JTable table;
	JScrollPane jsp;
	DefaultTableModel dtm;
	ServerSocket server;
	ProcessThread thread;
public void start(){
	try{
		server = new ServerSocket(1111);
		Object res[] = {"Cloud Storage Services Started"};
		dtm.addRow(res);
		while(true){
			Socket socket = server.accept();
			socket.setKeepAlive(true);
			thread=new ProcessThread(socket,dtm);
			thread.start();
		}
	}catch(Exception e){
		e.printStackTrace();
	}
}
public CloudServer(){
	super("Cloud Storage Services Started");
	
	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());
	p1 = new StandardPanel(Theme.STANDARD_GREEN_THEME,PanelType.PANEL_ROUNDED);
	p1.setPreferredSize(new Dimension(200,50));
	f1 = new Font("Courier New",Font.BOLD,14);
	p2 = new TitlePanel(600,60);
	p2.setBackground(new Color(204, 110, 155));
	title = new JLabel("<HTML><BODY><CENTER>Secure Data Sharing and Searching at the Edge of Cloud-Assisted<br/>Internet of Things</BODY></HTML>".toUpperCase());
	title.setFont(new Font("Courier New",Font.BOLD,16));
	p2.add(title);
	panel.add(p1,BorderLayout.CENTER);
	panel.add(p2,BorderLayout.NORTH);

	l1 = new JLabel("Cloud Storage Screen");
	l1.setFont(f1);
	p1.add(l1);
	
	p3 = new JPanel();
	p3.setLayout(new BorderLayout());
	dtm = new DefaultTableModel(){
		public boolean isCellEditable(int r,int c){
			return false;
		}
	};
	table = new JTable(dtm);
	table.setFont(f1);
	table.getTableHeader().setFont(new Font("Courier New",Font.BOLD,15));
	table.setRowHeight(30);
	jsp = new JScrollPane(table);
	jsp.getViewport().setBackground(Color.white);
	dtm.addColumn("Client Request Processing Status");
	
	p3.add(jsp,BorderLayout.CENTER);

	getContentPane().add(panel,BorderLayout.NORTH);
	getContentPane().add(p3,BorderLayout.CENTER);
}
public static void main(String a[])throws Exception{
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	CloudServer cs = new CloudServer();
	cs.setVisible(true);
	cs.setExtendedState(JFrame.MAXIMIZED_BOTH);
	new ServerThread(cs);
}
}
ProcessThread.java
package com;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.table.DefaultTableModel;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
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

@Override
public void run(){
	try{
		Object input[]=(Object[])in.readObject();
        String type=(String)input[0];
		if(type.equals("download")){
			String user = (String)input[1];
			String filename = (String)input[2];
			FileInputStream fin = new FileInputStream("CloudUser/"+user+"/"+filename);
			byte b[] = new byte[fin.available()];
			fin.read(b,0,b.length);
			fin.close();
			Object res[] = {b};
			out.writeObject(res);
			Object res1[] = {filename+" sent to edge server"};
			dtm.addRow(res1);
		}
		if(type.equals("upload")){
			String user = (String)input[1];
			String filename = (String)input[2];
			byte enc[] = (byte[])input[3];
			String keywords = (String)input[4];
			File file = new File("CloudUser/"+user);
			if(!file.exists())
				file.mkdir();
			byte kword[] = keywords.getBytes();
			FileOutputStream fout = new FileOutputStream("trapdoor.txt",true);
			fout.write(kword,0,kword.length);
			fout.close();
			fout = new FileOutputStream(file.getPath()+"/"+filename);
			fout.write(enc,0,enc.length);
			fout.close();
			Object res[] = {filename+" File saved at cloud server"};
			out.writeObject(res);
			dtm.addRow(res);
		}
		if(type.equals("search")){
			String query = (String)input[1];
			String qry[] = query.split(",");
			ArrayList<String> list = new ArrayList<String>();
			ArrayList<String> dup = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader("trapdoor.txt"));
			String line = null;
			System.out.println(query);
			while((line=br.readLine())!=null){
				String data[] = line.split("#");
				for(int j=0;j<qry.length;j++){
					//System.out.println(data[2]+" "+qry[j]+" "+data[2].indexOf(qry[j]));
					if(data[2].indexOf(qry[j]) != -1){
						if(!dup.contains(data[1])){
							dup.add(data[1]);
							list.add(data[0]+","+data[1]);
						}
					}
				}
			}
			br.close();
			Object res[] = {list};
			out.writeObject(res);
			Object res1[] = {"Search result sent to user for query "+query};
			dtm.addRow(res1);
		}
	}catch(Exception e){
        e.printStackTrace();
    }
}
}
