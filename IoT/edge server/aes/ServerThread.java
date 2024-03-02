package com;
public class ServerThread extends Thread
{
	EdgeServer server;
public ServerThread(EdgeServer server){
	this.server=server;
	start();
}
public void run(){
	server.start();
}
}