// This file contains material supporting section 3.7 of the textbook:

// "Object Oriented Software Engineering" and is issued under the open-source

// license found at www.lloseng.com 



import java.io.*;

import common.ChatIF;

import ocsf.src.ocsf.server.*;



/**

 * This class overrides some of the methods in the abstract 

 * superclass in order to give more functionality to the server.

 *

 * @author Dr Timothy C. Lethbridge

 * @author Dr Robert Lagani&egrave;re

 * @author Fran&ccedil;ois B&eacute;langer

 * @author Paul Holden

 * @version July 2000

 */

public class EchoServer extends AbstractServer 

{

  //Class variables *************************************************

	ChatIF serverUI;

  

  /**

   * The default port to listen on.

   */

  final public static int DEFAULT_PORT = 5555;

  

  //Constructors ****************************************************

  

  /**

   * Constructs an instance of the echo server.

   *

   * @param port The port number to connect on.

   */

  public EchoServer(int port) 

  {    super(port);
 }

 
  public EchoServer(int port, ChatIF serverUI) throws IOException{
	  super(port);
	  this.serverUI = serverUI;
}



  

  //Instance methods ************************************************

  

  /**

   * This method handles any messages received from the client.

   *

   * @param msg The message received from the client.

   * @param client The connection from which the message originated.

   */

  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  if (client.getInfo("loginID") == null){
	        try{
	          client.sendToClient("login first.");
	          client.close();
	        }
	        catch (IOException e) {}
	  }
	  else if (msg.toString().startsWith("#login "))
	    {
	      if (client.getInfo("loginID") != null){
	        try {
	          client.sendToClient("logged in.");
	        }
	        catch (IOException e) {}
	      }
	    }
    System.out.println("Message received: " + msg + " from " + client);
    this.sendToAllClients(msg);
 }


  public void  handleMessageFromServerUI(String message) {
	  if(message.charAt(0) == '#') {
		  clientCommandFromServer(message);
	  }
	  else {
		  serverUI.display(message);
		  this.sendToAllClients(message);
	  }
}

  private void clientCommandFromServer(String message){


	    if (message.equalsIgnoreCase("#quit"))
	    	closeTheChat();
	    else if (message.equalsIgnoreCase("#stop"))
	      stopListening();
	    else if (message.equalsIgnoreCase("#close"))
	    {
	      try{
	        close();
	      }
	      catch(IOException e) {}
	    }
	    else if (message.toLowerCase().startsWith("#setport")){
	      if (getNumberOfClients() == 0 && !isListening()){

	        int newPort = Integer.parseInt(message.substring(5555));
	        setPort(newPort);
	        serverUI.display
	          ("New Port " + getPort());
	      }
		    else if (message.equalsIgnoreCase("#getport")) {

			      serverUI.display(Integer.toString(getPort()));
			  }
	    }
	    else if (message.equalsIgnoreCase("#start")){
	      if (!isListening()){
	        try{
	          listen();
	        }
	        catch(Exception ex){
	          serverUI.display("Cannotlisten for clients!");
	        }
	      }
	    }
}


	  

	  

    

  /**

   * This method overrides the one in the superclass.  Called

   * when the server starts listening for connections.

   */

  protected void serverStarted(){
    System.out.println
    	("Server listening for connections on port " + getPort());
    }

  

  /**

   * This method overrides the one in the superclass.  Called

   * when the server stops listening for connections.

   */

  protected void serverStopped(){
    System.out.println
    ("Server has stopped listening for connections.");
  }

  

  //Class methods ***************************************************

  

  /**

   * This method is responsible for the creation of 

   * the server instance (there is no UI in this phase).

   *

   * @param args[0] The port number to listen on.  Defaults to 5555 

   *          if no argument is entered.

   */

  public void closeTheChat(){
    try
    {
      close();
    }
    catch(IOException e){
    }
    System.exit(0);
  }

  public static void main(String[] args) {
    int port = 0; //Port to listen on
    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
    EchoServer sv = new EchoServer(port);
    try 

    {
      sv.listen(); //Start listening for connections

    } 

    catch (Exception ex) 

    {

      System.out.println("ERROR - Could not listen for clients!");

    }

  }

}

//End of EchoServer class