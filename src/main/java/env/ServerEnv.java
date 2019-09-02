package env;

import java.io.*;
import java.util.Arrays;

public class ServerEnv {

    public static BufferedReader serverIn;
	private static PrintStream	serverOut;
    
	public ServerEnv()
	{
		serverIn 	= new BufferedReader(new InputStreamReader(System.in));
		serverOut 	= new PrintStream(new FileOutputStream(FileDescriptor.out));
	}

	static void sendJointActionToConsole(String[] jointAction)
	{
			System.out.println(Arrays.toString(jointAction));
			//System.in.read();
	}
	
	static void sendJointActionToServer(String[] jointAction)
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
