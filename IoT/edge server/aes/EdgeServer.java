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
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class EdgeServer extends JFrame{
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
		server = new ServerSocket(2222);
		Object res[] = {"Edge Server Services Started"};
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
public EdgeServer(){
	super("Edge Server Services Started");
	
	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());
	p1 = new StandardPanel(Theme.STANDARD_BLUE_THEME,PanelType.PANEL_ROUNDED);
	p1.setPreferredSize(new Dimension(200,50));
	f1 = new Font("Courier New",Font.BOLD,14);
	p2 = new TitlePanel(600,60);
	p2.setBackground(new Color(104, 210, 55));
	title = new JLabel("<HTML><BODY><CENTER>Secure Data Sharing and Searching at the Edge of Cloud-Assisted<br/>Internet of Things</BODY></HTML>".toUpperCase());
	title.setFont(new Font("Courier New",Font.BOLD,16));
	p2.add(title);
	panel.add(p1,BorderLayout.CENTER);
	panel.add(p2,BorderLayout.NORTH);

	l1 = new JLabel("Edge Server Screen");
	l1.setFont(f1);
	l1.setForeground(Color.white);
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
	EdgeServer cs = new EdgeServer();
	cs.setVisible(true);
	cs.setExtendedState(JFrame.MAXIMIZED_BOTH);
	new ServerThread(cs);
}
}