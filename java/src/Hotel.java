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
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Hotel {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));
   static String curruserID;
   static String curruserType;

   /**
    * Creates a new instance of Hotel 
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Hotel(String dbname, String dbport, String user, String passwd) throws SQLException {

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
   }//end Hotel

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
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
       while (rs.next()){
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

   public int getNewUserID(String sql) throws SQLException {
      Statement stmt = this._connection.createStatement ();
      ResultSet rs = stmt.executeQuery (sql);
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
            Hotel.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Hotel esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Hotel object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Hotel (dbname, dbport, user, "");

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
              while(usermenu) 
	      { 
		System.out.println("curruserType:" + curruserType + ":\n");
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Hotels within 30 units");
                System.out.println("2. View Rooms");
                System.out.println("3. Book a Room");
                System.out.println("4. View recent booking history");
		if(curruserType.equals("manager"))
		{
                  //the following functionalities basically used by managers
                  System.out.println("5. Update Room Information");
                  System.out.println("6. View 5 recent Room Updates Info");
                  System.out.println("7. View booking history of the hotel");
                  System.out.println("8. View 5 regular Customers");
                  System.out.println("9. Place room repair Request to a company");
                  System.out.println("10. View room repair Requests history");
                }
                System.out.println(".........................");
                System.out.println("20. Log out");
                
		if(curruserType.equals("customer"))
		{
		  switch(readChoice())
		  {
		    case 1: viewHotels(esql); break;
                    case 2: viewRooms(esql); break;
                    case 3: bookRooms(esql); break;
                    case 4: viewRecentBookingsfromCustomer(esql); break;
		    case 20: usermenu = false; break;
                    default : System.out.println("Unrecognized choice!"); break;
		  }

		}
		else
		{//manager
		  switch (readChoice())
		  {
                    case 1: viewHotels(esql); break;
                    case 2: viewRooms(esql); break;
                    case 3: bookRooms(esql); break;
                    case 4: viewRecentBookingsfromCustomer(esql); break;
                    case 5: updateRoomInfo(esql); break;
                    case 6: viewRecentUpdates(esql); break;
                    case 7: viewBookingHistoryofHotel(esql); break;
                    case 8: viewRegularCustomers(esql); break;
                    case 9: placeRoomRepairRequests(esql); break;
                    case 10: viewRoomRepairHistory(esql); break;
                    case 20: usermenu = false; break;
                    default : System.out.println("Unrecognized choice!"); break;
                  }
                }
            } }
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
    * Creates a new user
    **/
   public static void CreateUser(Hotel esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine(); 
         String type="Customer";
			String query = String.format("INSERT INTO USERS (name, password, userType) VALUES ('%s','%s', '%s')", name, password, type);
         esql.executeUpdate(query);
         System.out.println ("User successfully created with userID = " + esql.getNewUserID("SELECT last_value FROM users_userID_seq"));
         
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Hotel esql){
      try{
         System.out.print("\tEnter userID: ");
         String userID = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE userID = '%s' AND password = '%s'", userID, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0){
            query = String.format("SELECT userType FROM USERS WHERE userID = '%s'", userID);
            curruserType = esql.executeQueryAndReturnResult(query).get(0).get(0).trim();
            curruserID = userID;
            return userID;}
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here
public static void viewHotels(Hotel esql) {
        try {
            System.out.print("Enter latitude: ");
            String latitudeStr = in .readLine();
            double latitude = Double.parseDouble(latitudeStr);

            System.out.print("Enter longitude: ");
            String longitudeStr = in .readLine();
            double longitude = Double.parseDouble(longitudeStr);

            String query = "SELECT hotelID, hotelName, latitude, longitude " +
                "FROM Hotel " +
                "WHERE calculate_distance(latitude, longitude, " + latitude + ", " + longitude + ") <= 30";

            List < List < String >> results = esql.executeQueryAndReturnResult(query);

            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("| %-10s | %-30s | %-15s | %-15s |\n", "Hotel ID", "Hotel Name", "Latitude", "Longitude");
            System.out.println("--------------------------------------------------------------------------------");
            for (List < String > row: results) {
                System.out.printf("| %-10s | %-30s | %-15s | %-15s |\n", row.get(0), row.get(1), row.get(2), row.get(3));
            }
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Total row(s): " + results.size());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void viewRooms(Hotel esql) {
        try {
            // Ask user for hotelID and date
            System.out.print("Enter hotel ID: ");
            String hotelID = in .readLine().trim();

            System.out.print("Enter date (mm/dd/yyyy): ");
            String dateStr = in .readLine().trim();

            // Convert input date to SQL date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            java.util.Date date = dateFormat.parse(dateStr);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());

            // Construct the SQL query
            String query = "SELECT Rooms.roomNumber, Rooms.price, CASE WHEN RoomBookings.bookingID IS NULL THEN 'Available' ELSE 'Booked' END AS availability " +
                "FROM Rooms " +
                "LEFT JOIN RoomBookings ON Rooms.hotelID = RoomBookings.hotelID AND Rooms.roomNumber = RoomBookings.roomNumber AND RoomBookings.bookingDate = '" + sqlDate + "' " +
                "WHERE Rooms.hotelID = " + hotelID +
                " ORDER BY Rooms.roomNumber";

            // Execute the query and print the result
            List < List < String >> results = esql.executeQueryAndReturnResult(query);

            System.out.println("--------------------------------------------");
            System.out.printf("| %-10s | %-10s | %-8s |\n", "Room Number", "Price", "Availability");
            System.out.println("--------------------------------------------");
            for (List < String > row: results) {
                System.out.printf("| %-10s | $%-11s | %-8s |\n", row.get(0), row.get(1), row.get(2));
            }
            System.out.println("Total row(s): " + results.size());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }



    public static void bookRooms(Hotel esql) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            // Ask the user for the hotel ID
            System.out.print("\tEnter Hotel ID: ");
            String hotelID = in .readLine();

            // Check if the hotel ID is valid
            while (esql.executeQueryAndPrintResult("SELECT hotelID FROM Hotel WHERE hotelID = " + hotelID + ";") == 0) {
                System.out.print("\tInvalid Hotel ID. Enter hotel ID: ");
                hotelID = in .readLine();
            }

            // Ask the user for the room number
            System.out.print("\tEnter Room Number: ");
            String roomNumber = in .readLine();

            // Check if the room number is valid for the given hotel ID
            while (esql.executeQueryAndPrintResult("SELECT hotelID FROM Rooms WHERE hotelID = " + hotelID + " AND roomNumber = " + roomNumber + ";") == 0) {
                System.out.print("\tInvalid Room No. Enter Room no: ");
                roomNumber = in .readLine();
            }

            System.out.print("Enter date (mm/dd/yyyy): ");
            String bookingDate = in .readLine().trim();

            // Convert input date to SQL date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            java.util.Date date = dateFormat.parse(bookingDate);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());

            // Check if room is available on the given date
            String queryprice = "SELECT price FROM Rooms " +
                "WHERE hotelID = " + hotelID +
                " AND roomNumber = " + roomNumber +
                " AND NOT EXISTS (SELECT * FROM RoomBookings " +
                "WHERE hotelID = " + hotelID +
                " AND roomNumber = " + roomNumber +
                " AND bookingDate = '" + sqlDate + "')";

            List < List < String >> result = esql.executeQueryAndReturnResult(queryprice);

            if (result.size() > 0) {
                // Room is available, so book it and display the price
                String price = result.get(0).get(0);
                System.out.println("Room is available!");


                // Insert the new booking into the RoomBookings table
                String query = "INSERT INTO RoomBookings (customerID, hotelID, roomNumber, bookingDate) " +
                    "VALUES ('" + curruserID + "', ' " + hotelID + " ', ' " + roomNumber + " ', '" + bookingDate + "');";
                esql.executeUpdate(query);
                System.out.println("Booking successful! \nYour cost: $" + price);
            } else {
                // Room is not available
                System.out.println("Sorry, that room is booked.");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    public static void viewRecentBookingsfromCustomer(Hotel esql) {
        try {
            // Get the user ID of the currently logged in user
            String userID = curruserID;

            String query = "SELECT RoomBookings.hotelID, Rooms.roomNumber, RoomBookings.bookingDate, Rooms.price " +
                "FROM RoomBookings, Rooms " +
                "WHERE RoomBookings.roomNumber = Rooms.roomNumber AND " +
                "RoomBookings.hotelID = Rooms.hotelID AND " +
                "customerID = " + userID +
                " ORDER BY bookingDate DESC " +
                "LIMIT 5";

            List < List < String >> results = esql.executeQueryAndReturnResult(query);
            if (results.isEmpty()) {
                System.out.println("No bookings found for current customer.");
            } else {
                System.out.println("Recent bookings for current customer:");
                System.out.println("-----------------------------------------------------");
                System.out.printf("| %-10s | %-10s | %-15s | %-10s |\n", "hotelID", "roomNumber", "bookingDate", "price");
                System.out.println("-----------------------------------------------------");
                for (List < String > row: results) {
                    System.out.printf("| %-10s | %-10s | %-15s | %-10s |\n", row.get(0), row.get(1), row.get(2), row.get(3));
                }
                System.out.println("-----------------------------------------------------");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void updateRoomInfo(Hotel esql) {
        try {
            String userTypeQuery = "SELECT userType FROM Users WHERE userID = " + curruserID + ";";
            List < List < String >> userTypeResult = esql.executeQueryAndReturnResult(userTypeQuery);
            if (userTypeResult.isEmpty() || !userTypeResult.get(0).get(0).trim().equals("manager")) {
                System.out.println("Only managers can access this option.");
                return;
            }

            // Get the user ID of the logged in manager
            String managerID = curruserID;

            // Ask for the hotel ID and room number to update
            System.out.print("Enter hotel ID: ");
            int hotelID = Integer.parseInt( in .readLine());
            System.out.print("Enter room number: ");
            int roomNumber = Integer.parseInt( in .readLine());

            // Check if the manager manages the specified hotel
            String checkManagerQuery = "SELECT * FROM Hotel WHERE hotelID = " + hotelID + " AND managerUserID = " + managerID + ";";
            int numRows = esql.executeQuery(checkManagerQuery);
            if (numRows == 0) {
                System.out.println("You don't manage the specified hotel.");
                return;
            }

            // Get the current room information
            String getRoomQuery = "SELECT * FROM Rooms WHERE hotelID = " + hotelID + " AND roomNumber = " + roomNumber + ";";
            List < List < String >> roomResult = esql.executeQueryAndReturnResult(getRoomQuery);
            if (roomResult.isEmpty()) {
                System.out.println("Room not found.");
                return;
            }
            List < String > room = roomResult.get(0);

            // Ask for the new room information
            System.out.print("Enter new price: ");
            int price = Integer.parseInt( in .readLine());
            System.out.print("Enter new image URL: ");
            String imageURL = in .readLine();

            // Update the Rooms table
            String updateRoomQuery = "UPDATE Rooms SET price = " + price + ", imageURL = '" + imageURL + "' WHERE hotelID = " + hotelID + " AND roomNumber = " + roomNumber + ";";
            esql.executeUpdate(updateRoomQuery);

            // Log the room update in the RoomUpdatesLog table
            String logUpdateQuery = "INSERT INTO RoomUpdatesLog (managerID, hotelID, roomNumber, updatedOn) VALUES (" + managerID + ", " + hotelID + ", " + roomNumber + ", CURRENT_TIMESTAMP());";
            esql.executeUpdate(logUpdateQuery);

            System.out.println("Room information updated successfully.");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void viewRecentUpdates(Hotel esql) {
        try {
            String userTypeQuery = "SELECT userType FROM Users WHERE userID = " + curruserID + ";";
            List < List < String >> userTypeResult = esql.executeQueryAndReturnResult(userTypeQuery);
            if (userTypeResult.isEmpty() || !userTypeResult.get(0).get(0).trim().equals("manager")) {
                System.out.println("Only managers can access this option.");
                return;
            }

            // Get the user ID of the logged in manager
            String managerID = curruserID;

            // Get the recent room updates for the specified hotel
            String getUpdatesQuery = "SELECT managerID, hotelID, roomNumber, updatedOn FROM RoomUpdatesLog WHERE managerID = " + managerID + " ORDER BY updatedOn DESC LIMIT 5;";
            List < List < String >> updatesResult = esql.executeQueryAndReturnResult(getUpdatesQuery);

            // Print the recent room updates
            System.out.println("Recent room updates :");
            if (updatesResult.isEmpty()) {
                System.out.println("No updates found.");
            } else {
                for (List < String > update: updatesResult) {
                    String manager = update.get(0);
                    String room = update.get(2);
                    String date = update.get(3);
                    System.out.println("- Manager " + manager + " updated room " + room + " on " + date);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void viewBookingHistoryofHotel(Hotel esql) {
        try {
            String userTypeQuery = "SELECT userType FROM Users WHERE userID = " + curruserID + ";";
            List < List < String >> userTypeResult = esql.executeQueryAndReturnResult(userTypeQuery);
            if (userTypeResult.isEmpty() || !userTypeResult.get(0).get(0).trim().equals("manager")) {
                System.out.println("Only managers can access this option.");
                return;
            }

            // Get the user ID of the logged in manager
            String managerID = curruserID;

            // Ask for the hotel ID to view booking history
            System.out.print("Enter hotel ID: ");
            int hotelID = Integer.parseInt( in .readLine());

            // Check if the manager manages the specified hotel
            String checkManagerQuery = "SELECT * FROM Hotel WHERE hotelID = " + hotelID + " AND managerUserID = " + managerID + ";";
            int numRows = esql.executeQuery(checkManagerQuery);
            if (numRows == 0) {
                System.out.println("You don't manage the specified hotel.");
                return;
            }

            // Ask for the date range to view booking history
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            System.out.print("Enter start date (mm/dd/yyyy): ");
            String startDateStr = in .readLine();
            System.out.print("Enter end date (mm/dd/yyyy): ");
            String endDateStr = in .readLine();

            // Parse the date range
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);

            // Query the RoomBookings table to get the booking history for the specified hotel and date range
            String getBookingHistoryQuery = "SELECT rb.bookingID, u.name, rb.hotelID, rb.roomNumber, rb.bookingDate " +
                "FROM RoomBookings rb, Users u " +
                "WHERE rb.customerID = u.userID AND rb.hotelID = " + hotelID + " AND rb.bookingDate BETWEEN '" + sdf.format(startDate) + "' AND '" + sdf.format(endDate) + "';";
            List < List < String >> bookingHistoryResult = esql.executeQueryAndReturnResult(getBookingHistoryQuery);

            // Print the booking history
            System.out.println("Booking history for hotel " + hotelID + " from " + startDateStr + " to " + endDateStr + ":");
            if (bookingHistoryResult.isEmpty()) {
                System.out.println("No bookings found.");
            } else {
                for (List < String > booking: bookingHistoryResult) {
                    String bookingID = booking.get(0);
                    String customerName = booking.get(1);
                    String roomNumber = booking.get(3);
                    String bookingDate = booking.get(4);
                    System.out.println("- Booking ID: " + bookingID + ", Customer Name: " + customerName + ", Room Number: " + roomNumber + ", Booking Date: " + bookingDate);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void viewRegularCustomers(Hotel esql) {
        try {
            String userTypeQuery = "SELECT userType FROM Users WHERE userID = " + curruserID + ";";
            List < List < String >> userTypeResult = esql.executeQueryAndReturnResult(userTypeQuery);
            if (userTypeResult.isEmpty() || !userTypeResult.get(0).get(0).trim().equals("manager")) {
                System.out.println("Only managers can access this option.");
                return;
            }

            // Get the user ID of the logged in manager
            String managerID = curruserID;

            // Ask for the hotel ID to view regular customers
            System.out.print("Enter hotel ID: ");
            int hotelID = Integer.parseInt( in .readLine());

            // Check if the manager manages the specified hotel
            String checkManagerQuery = "SELECT * FROM Hotel WHERE hotelID = " + hotelID + " AND managerUserID = " + managerID + ";";
            int numRows = esql.executeQuery(checkManagerQuery);
            if (numRows == 0) {
                System.out.println("You don't manage the specified hotel.");
                return;
            }

            // Query the RoomBookings table to get the top 5 regular customers for the specified hotel
            String getRegularCustomersQuery = "SELECT u.name, COUNT(rb.customerID) AS numBookings " +
                "FROM RoomBookings rb, Users u " +
                "WHERE rb.customerID = u.userID AND rb.hotelID = " + hotelID + " " +
                "GROUP BY rb.customerID, u.name " +
                "ORDER BY numBookings DESC " +
                "LIMIT 5;";
            List < List < String >> regularCustomersResult = esql.executeQueryAndReturnResult(getRegularCustomersQuery);

            // Print the regular customers
            System.out.println("Top 5 regular customers for hotel " + hotelID + ":");
            if (regularCustomersResult.isEmpty()) {
                System.out.println("No regular customers found.");
            } else {
                for (List < String > customer: regularCustomersResult) {
                    String customerName = customer.get(0);
                    String numBookings = customer.get(1);
                    System.out.println("- Customer Name: " + customerName + ", Number of Bookings: " + numBookings);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

   
   public static void placeRoomRepairRequests(Hotel esql) 
   {
      try{

            // Ask for the hotel ID, room, companyID, DATE
            //System.out.print("Enter hotel ID: ");
            int hotelID = isHotelManager(esql);
            int roomNumber = validRoom(esql, hotelID);
	    int companyID = validCompany(esql);
	    String repairDate = promptDate();
            
	    // INSERT INTO RoomRepairs table
            String query = "INSERT INTO RoomRepairs ( companyID, hotelID, roomNumber, repairDate) "
		   + "VALUES (" + companyID + ", " + hotelID + ", " + roomNumber + ", '" + repairDate + "')";

            esql.executeUpdate(query);
            /*
    	    // Retrieve repairID to INSERT to RoomRepairRequests
            String queryRepairID = "SELECT repairID FROM RoomRepairs " 
		    + "WHERE companyID = " + companyID + " AND hotelID = " + hotelID + " AND roomNumber = " + roomNumber;
            
            String repairID = esql.executeQueryAndReturnResult(queryRepairID).get(0).get(0);
            System.out.printf("Created repairID: '%s' %n", repairID);
	    
	    // Insert user(manageID) and repair ID  into the RoomRepairRequests table
            
	    query = "INSERT INTO RoomRepairRequests (managerID, repairID) " +
                       "VALUES (" + curruserID + ", " + repairID + ")";
     	    esql.executeUpdate(query);
            */
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

   public static void viewRoomRepairHistory(Hotel esql) 
    {
       try{
	
	String query = "SELECT companyID, hotelID, roomNumber, repairDate FROM RoomRepairs WHERE  RoomRepairs.repairID IN (SELECT repairID FROM RoomRepairRequests WHERE managerID = " + curruserID + ")";
	int rowCount = esql.executeQueryAndPrintResult(query);
        System.out.println("Total row(s): " + rowCount);
        
        List < List < String >> results = esql.executeQueryAndReturnResult(query);
        if (results.isEmpty()) 
	{
          System.out.println("No bookings found for current customer.");
        } 
	else 
	{
          System.out.println("Room Repair History:");
          System.out.println("------------------------------------------------------");
          System.out.printf("| %-10s | %-10s | %-10s | %-10s |\n", "CompanyID", "HotelID", "Room Number", "RepairDate");
          System.out.println("------------------------------------------------------");
          for (List < String > row: results) 
	  {
            System.out.printf("| %-10s | %-10s | %-11s | %-10s |\n", row.get(0), row.get(1), row.get(2), row.get(3));
          }
          System.out.println("------------------------------------------------------");
        }



      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   } 

 /*================= HELPER FUNC ====================*/

 public static int isHotelManager(Hotel esql)
  {
    	  
        int hotelID;
        do
	{ // Ask for the hotel ID and room number to update
            try{
		System.out.print("Enter hotel ID: ");
                hotelID = Integer.parseInt(in.readLine());

                // Check if the manager manages the specified hotel
                String checkManagerQuery = "SELECT * FROM Hotel WHERE hotelID = " + hotelID + " AND managerUserID = " + curruserID + ";";
                int numRows = esql.executeQuery(checkManagerQuery);
                if (numRows == 0)
                {
                  System.out.println("You don't manage the specified hotel.\n");

                }
                else
                  return hotelID;
	    }
            catch(Exception e){
                System.err.println (e.getMessage ());
            }     
        }while(true);
  }

   public static String promptDate()
   {
        String inputDate;
	Date currDate = new Date();

	System.out.println("current date: " + currDate + "\n");
        do {
             try{   // Ask the user for the booking date
                  System.out.print("\tEnter Date (mm/dd/yyyy): ");
              
                  inputDate = in.readLine().trim();
                  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                  LocalDate formattedDate = LocalDate.parse(inputDate, formatter);
		  if(formattedDate.isBefore(LocalDate.now()))
		  {
                    System.out.print("\t The date you entered is not valid. Please enter a date that has not yet passed.  \n");
                    continue;
		  }
		  else
                    break;
             }catch (Exception e) {
                System.err.println(e.getMessage());
                continue;
             }
        }while(true);
        return inputDate;
   }

   public static int validRoom(Hotel esql, int hotelID)
   {
	   int roomNumber;
	do{    
	    try{
		 System.out.print("Enter room number: ");
                 roomNumber = Integer.parseInt( in.readLine());

                 // Get the current room information
                 String getRoomQuery = "SELECT * FROM Rooms WHERE hotelID = " + hotelID + " AND roomNumber = " + roomNumber + ";";
                 List < List < String >> roomResult = esql.executeQueryAndReturnResult(getRoomQuery);
                 if (roomResult.isEmpty()) 
		 {
                    System.out.println("Room not found.");
		    continue;
                 }
		 else 
	            return roomNumber;
	    } catch ( Exception e){
		System.err.println(e.getMessage());
	    }
	}while(true);
   }  

   public static int validCompany(Hotel esql)
   {
           int companyID;
        do{
            try{
                 System.out.print("Enter Company ID: ");
                 companyID = Integer.parseInt( in.readLine());

                 // Get the current room information
                 String getCompanyQuery = "SELECT * FROM MaintenanceCompany WHERE CompanyID = " + companyID + ";";
                 List < List < String >> roomResult = esql.executeQueryAndReturnResult(getCompanyQuery);
                 if (roomResult.isEmpty())
                 {
                    System.out.println("Room not found.");
                    continue;
                 }
                 else
                    return companyID;
            } catch ( Exception e){
                System.err.println(e.getMessage());
            }
        }while(true);
   }

  
   /*============== END HELPER FUNC ==========================*/


}//end Hotel

