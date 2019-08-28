package env;

import java.io.*;
import java.util.Arrays;

public class ServerEnv {

    private BufferedReader serverIn;
	private PrintStream	serverOut;
    
	ServerEnv()
	{
		serverIn 	= new BufferedReader(new InputStreamReader(System.in));
		serverOut 	= new PrintStream(new FileOutputStream(FileDescriptor.out));
	}

	void sendJointActionToConsole(String[] jointAction)
	{
			System.out.println(Arrays.toString(jointAction));
			//System.in.read();
	}
	
	public void sendJointActionToServer(String[] jointAction)
	{
		try 
		{
			serverOut.println(Arrays.toString(jointAction));
			serverIn.readLine(); // Necessary to wait for server
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
