/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
//import javax.swing.*;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class ProfNetwork {
// Java GUI
//  private static void createAndShowGUI(){
//    //Create and set up the window.
//    JFrame frame = new JFrame("HelloWorldSwing");
//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//    //Add the ubiquitous "Hello World" label.
//    JLabel label = new JLabel("Hello World");
//    frame.getContentPane().add(label);
//
//    //Display the window.
//    frame.pack();
//    frame.setVisible(true);
//  }

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of ProfNetwork
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end ProfNetwork

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
          List<String> record = new ArrayList<String>();
         for (int i=1; i<=numCol; ++i)
            record.add(rs.getString (i));
         result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            ProfNetwork.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      ProfNetwork esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the ProfNetwork object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new ProfNetwork (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Goto Friend List");
                System.out.println("2. Update Profile");
                System.out.println("3. Write a new message");
                System.out.println("4. Send Friend Request");
                System.out.println("5. Search Person");
                System.out.println("6. Goto Requests List");
                System.out.println("7. Goto Message List");
                System.out.println("8. View Friends List");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: FriendList(esql, authorisedUser); break;
                   case 2: displayUser(esql, authorisedUser);
                           UpdateProfile(esql, authorisedUser); break;
                   case 3: NewMessage(esql, authorisedUser); break;
                   case 4: SendRequest(esql, authorisedUser); break;
                   case 5: SearchPerson(esql); break;
                   case 6: AcceptorReject(esql, authorisedUser); break;
                   case 7: displayMessage(esql, authorisedUser); break;
                   case 8: viewFriends(esql, authorisedUser); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with provided login, password and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user email: ");
         String email = in.readLine();

         //Creating empty contact\block lists for a user
         // Removing contact list temporarily for testing. Not sure if we need it??!!
         //	 String query = String.format("INSERT INTO USR (userId, password, email, contact_list) VALUES ('%s','%s','%s')", login, password, email);
         String query = String.format("INSERT INTO USR (userId, password, email) VALUES ('%s','%s','%s')", login, password, email);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(ProfNetwork esql){
     try{
       System.out.print("\tEnter user login: ");
       String login = in.readLine();
       System.out.print("\tEnter user password: ");
       String password = in.readLine();

       String query = String.format("SELECT * FROM USR WHERE userid = '%s' AND password = '%s'", login, password);
       int userNum = esql.executeQuery(query);
       if (userNum > 0)
         return login;
       return null;
     }catch(Exception e){
       System.err.println (e.getMessage ());
       return null;
     }
   }//end

// Rest of the functions definitions go in here
  /*
   * Gets the list for an existing User and
   * prints them to the screen.
   *
   * @param User the string holding the userid
   */
  public static void FriendList(ProfNetwork esql, String User){
    try{
      String query = String.format("SELECT connectionId FROM CONNECTION_USR WHERE userid = '%s' AND status = 'Accept'", User);
      esql.executeQueryAndPrintResult(query);
    }catch(Exception e){
      System.err.println (e.getMessage ());
    }
  }
  /*
   * Prints a list of commands for the User
   * to update his profile. 
   *
   * @param User the string holding the userid
   */
  public static void UpdateProfile(ProfNetwork esql, String User){
    System.out.println("UpdateProfile");
    System.out.println("---------");
    System.out.println("1. Change Password");
    System.out.println("2. Add Education Detail");
    System.out.println("3. Add Work Experience");
    switch (readChoice()){
      case 1: ChangePassword(esql, User); break;
      case 2: AddEduDetail(esql, User); break;
      case 3: AddWorkExpr(esql, User); break;
      default : System.out.println("Unrecognized choice!"); break;
    }//end switch
  }
  /*
   * Changes the User password if he knows the old password.
   * Also make sure the User wants to change his password.
   *
   * @param User the string holding the userid
   */
  public static void ChangePassword(ProfNetwork esql, String User){
      try{
         System.out.print("\tAre you sure you want to change your password?: ");
         String check = in.readLine();
         if(!"yes".equalsIgnoreCase(check)){return;}
         System.out.print("\tEnter previous password: ");
         String password = in.readLine();
         String query = String.format("SELECT * FROM USR WHERE userid = '%s' AND password = '%s'", User, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0){
           System.out.print("\tEnter new password: ");
           String newpassword = in.readLine();
           query = String.format("UPDATE USR SET password = '%s' WHERE userid = '%s'", newpassword, User);
           esql.executeUpdate(query);
           System.out.print("\tPassword updated successfully!\n");
         }
      }catch(Exception e){
        System.err.println (e.getMessage ());
        return;
      }
  }
  /*
   * Need to handle User input
   * Also need to handle optional startdate and enddate.
   */
  /*
   * Prompts the User to enter in education details. 
   * First three fields required and last two optional.
   *
   * @param User the string holding the userid
   */
  public static void AddEduDetail(ProfNetwork esql, String User){
    try{
      System.out.print("\tEnter Institution Name: ");
      String instName = in.readLine();
      System.out.print("\tEnter Major: ");
      String major = in.readLine();
      System.out.print("\tEnter Degree: ");
      String degree = in.readLine();
      System.out.print("\tEnter startdate: ");
      String startdate = in.readLine();
      System.out.print("\tEnter enddate: ");
      String enddate = in.readLine();
      String query;
      if("".equals(enddate)) {
        query = String.format("INSERT INTO educational_details VALUES('%s', '%s', '%s', '%s', '%s')", User, instName, major, degree, startdate);
      }
      else {
        query = String.format("INSERT INTO educational_details VALUES('%s', '%s', '%s', '%s', '%s', '%s')", User, instName, major, degree, startdate, enddate);
      }
      esql.executeUpdate(query);
    }catch(Exception e){
      System.err.println (e.getMessage ());
      return;
    }
  }
  /*
   * Prompts the User to enter in work experience.
   * First four fields requiered and last one is optional.
   *
   * @param User the string holding the userid
   */
  public static void AddWorkExpr(ProfNetwork esql, String User){
    try{
      System.out.print("\tEnter Company Name: ");
      String compName = in.readLine();
      System.out.print("\tEnter Role: ");
      String role = in.readLine();
      System.out.print("\tEnter Location: ");
      String location = in.readLine();
      System.out.print("\tEnter startdate: ");
      String startdate = in.readLine();
      System.out.print("\tEnter enddate: ");
      String enddate = in.readLine();
      String query;
      if("".equals(enddate)) {
        query = String.format("INSERT INTO work_expr VALUES('%s', '%s', '%s', '%s', '%s')", User, compName, role, location, startdate);
      }
      else {
        query = String.format("INSERT INTO work_expr VALUES('%s', '%s', '%s', '%s', '%s', '%s')", User, compName, role, location, startdate, enddate);
      }
      esql.executeUpdate(query);
    }catch(Exception e){
      System.err.println (e.getMessage ());
      return;
    }
  }
  /*
   * Creates a message between the current User
   * and a valid userid.
   * Other values should be inserted by trigger.
   *
   * @param User the string holding the userid
   */
  public static void NewMessage(ProfNetwork esql, String senderId){
    try{
      System.out.print("\tEnter Recipient: ");
      String receiverId = in.readLine();
      System.out.print("\tEnter message: ");
      String contents = in.readLine();
      String query = String.format("INSERT INTO message(senderId, receiverId, contents) VALUES('%s', '%s', '%s')", senderId, receiverId, contents);
      esql.executeUpdate(query);
    }catch(Exception e){
      System.err.println (e.getMessage ());
      return;
    }
  }
  public static void SendMessage(ProfNetwork esql, String senderId, String receiverId){
    try{
      System.out.print("\tEnter message: ");
      String contents = in.readLine();
      String query = String.format("INSERT INTO message(senderId, receiverId, contents) VALUES('%s', '%s', '%s')", senderId, receiverId, contents);
      esql.executeUpdate(query);
    }catch(Exception e){
      System.err.println (e.getMessage ());
      return;
    }
  }
  /*
   * Sends a request for a connection between the User
   * and a valid userid. May only send request to users
   * who are within three levels of connection.
   *
   * Ex: A->B->C->D->E
   * user A may send a request to C and D but not E.
   *
   * @param User the string holding the userid
   */
  public static void SendRequest(ProfNetwork esql, String userId){
    try{
      System.out.print("\tEnter Name of connection: ");
      String connectionId = in.readLine();
      String query = String.format("SELECT COUNT(*) FROM connection_usr where userId = '%s'", userId);
      int num = esql.executeQuery(query);
      if(num <= 5){
        query = String.format("INSERT INTO connection_usr VALUES('%s', '%s', '%s')", userId, connectionId, "Request");
        esql.executeUpdate(query);
      }
      //Check list of your friends friends. 
      else{
        System.out.println("\tImplement three level friend check");
      }
    }catch(Exception e){
      System.err.println (e.getMessage ());
      return;
    }
  }
  public static void PassRequest(ProfNetwork esql, String userId, String connectionId){
    try{
      String query = String.format("SELECT COUNT(*) FROM connection_usr where userId = '%s'", userId);
      int num = esql.executeQuery(query);
      if(num <= 5){
        query = String.format("INSERT INTO connection_usr VALUES('%s', '%s', '%s')", userId, connectionId, "Request");
        esql.executeUpdate(query);
      }
      //Check list of your friends friends. 
      else{
        System.out.println("\tImplement three level friend check");
      }
    }catch(Exception e){
      System.err.println (e.getMessage ());
      return;
    }
  }
  public static void SearchPerson(ProfNetwork esql){
    try{
      System.out.print("\tEnter Name of User: ");
      String searchId = in.readLine();
      displayUser(esql, searchId);
    }catch(Exception e){
      System.err.println (e.getMessage ());
      return;
    }
  }
  public static void displayUser(ProfNetwork esql, String userId){
    try{
      String query = String.format("SELECT u.userId, email, dateofbirth, instituitionname, major, degree, e.startdate, e.enddate, company, role, location, w.startdate, w.enddate FROM usr u, work_expr w, educational_details e where u.userId = '%s' AND w.userId = '%s' AND e.userId = '%s'", userId, userId, userId);
      int num = esql.executeQuery(query);
      if(num == 0)
      {
        query = String.format("SELECT userId, email, dateofbirth FROM usr u where u.userId = '%s'", userId);
      }
      esql.executeQueryAndPrintResult(query);
    }catch(Exception e){
      System.err.println (e.getMessage ());
      return;
    }
  }
  public static void AcceptorReject(ProfNetwork esql, String connectionId){
    try{
      String query = String.format("SELECT * FROM CONNECTION_USR WHERE connectionId = '%s' AND status = 'Request'", connectionId);
      esql.executeQueryAndPrintResult(query);
      System.out.print("\tEnter Name of User: ");
      String userId = in.readLine();
      System.out.println("\tAccept or Reject: ");
      String choice = in.readLine();
      if("Accept".equalsIgnoreCase(choice))
      {
        query = String.format("UPDATE connection_usr SET status = 'Accept' WHERE userId = '%s'AND connectionid = '%s'", userId, connectionId);
      }
      else
      {
        query = String.format("UPDATE connection_usr SET status = 'Reject' WHERE userId = '%s'AND connectionid = '%s'", userId, connectionId);
      }
      esql.executeUpdate(query);
    }catch(Exception e){
      System.err.println (e.getMessage ());
      return;
    }
  }
  public static void displayMessage(ProfNetwork esql, String userId){
    try{
      String query = String.format("SELECT * FROM message where receiverId = '%s' and deletestatus = 0", userId);
      esql.executeQueryAndPrintResult(query);
      System.out.print("\tEnter msgId: ");
      String msgId = in.readLine();
      System.out.println("\tDelete: ");
      System.out.print("\tYes: y, No: n: ");
      String choice = in.readLine();
      if("y".equals(choice))
      {
        query = String.format("UPDATE message SET deleteStatus = 1 WHERE receiverId = '%s'AND msgId = '%s'", userId, msgId);
      }
      esql.executeUpdate(query);
    }catch(Exception e){
      System.err.println (e.getMessage ());
      return;
    }
  }

  public static void viewFriends(ProfNetwork esql, String userId){
    try{
      String currentId = userId;
      boolean usermenu = true;
      while(usermenu) 
      {
        FriendList(esql, currentId);
        System.out.println("Menu");
        System.out.println("---------");
        System.out.println("1. Select friend");
        System.out.println("2. Main Menu");
        switch (readChoice()){
          case 1: 
            {
              System.out.print("\tEnter Name of User: ");
              currentId = in.readLine();
              displayUser(esql, currentId);
              System.out.println("Menu");
              System.out.println("---------");
              System.out.println("1. Send Request");
              System.out.println("2. Send Message");
              System.out.println("3. View Friend List");
              System.out.println("4. Main Menu");
              switch (readChoice()){
                case 1: PassRequest(esql, userId, currentId); break;
                case 2: SendMessage(esql, userId, currentId); break;
                case 3: break;
                case 4: return;
                default : System.out.println("Unrecognized choice!"); break;
              }
              break;
            }
          case 2: return;
          default : System.out.println("Unrecognized choice!"); break;
        }
      }
    }catch(Exception e){
      System.err.println(e.getMessage());
      return;
    }
  }

}//end ProfNetwork
