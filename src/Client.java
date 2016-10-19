/**
 * Created by Zortrox on 10/12/2016.
 */

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.net.*;

public class Client extends NetObject{

	private String mName = "";
	private int mNumMsgs = 0;

	Client(String IP, int port, String name, int numMsgs, int xPos) {
		mIP = IP;
		mPort = port;
		mName = name;
		mNumMsgs = numMsgs;

		JFrame frame = new JFrame("Client: " + mName);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(new Dimension(300, 200));
		frame.setLocation(xPos, 0);

		textArea = new JTextArea(1, 50);
		scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		textArea.setEditable(false);
		frame.getContentPane().add(scrollPane);

		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		frame.setVisible(true);
	}

	public void run() {
		try {
			TCPConnection();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void TCPConnection() throws Exception {
		Socket socket = null;
		boolean bServerFound = false;

		//keep trying to connect to server
		while(!bServerFound)
		{
			try
			{
				socket = new Socket(mIP, mPort);
				bServerFound = true;
			}
			catch(ConnectException e)
			{
				writeMessage("Server refused, retrying...");

				try
				{
					Thread.sleep(2000); //2 seconds
				}
				catch(InterruptedException ex){
					ex.printStackTrace();
				}
			}
		}

		//initialize message
		String msgSend = "Hello, this is " + mName + ".";
		writeMessage(msgSend);

		Message msg = new Message();
		msg.mData = prepData(msgSend);
		sendTCPData(socket, msg);
		receiveTCPData(socket, msg);
		String msgReceive = new String(msg.mData);
		writeMessage("<server>: " + msgReceive);
		socket.close();

		for (int i = 0; i < mNumMsgs; i++) {
			socket = new Socket(mIP, mPort);
			//custom message
			msgSend = "test " + i;
			writeMessage(msgSend);

			msg = new Message();
			msg.mData = prepData(msgSend);
			sendTCPData(socket, msg);
			receiveTCPData(socket, msg);
			msgReceive = new String(msg.mData);
			writeMessage("<server>: " + msgReceive);
			socket.close();
		}
	}

	private byte[] prepData(String msg) {
		return (mName + "=" + msg).getBytes();
	}
}
