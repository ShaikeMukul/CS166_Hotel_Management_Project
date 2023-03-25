# CS166_Hotel_Management_Project

### How to Get Started
Start by following the path, `cd CS166_Hotel_Management_Project/` 

Then start the server by running the command,

    source ./server/startPostgreSQL.sh

  

Create Database by running the command,

    source ./server/createPostgreDB.sh

  

Then proceed to copy dataand run all SQL table by running,

    source ./sql/scripts/create_db.sh

  

Finally, run the java file by,

    source ./java/scripts/compile.sh

  

### High Level Description

#### Hotel Search: 
The 'viewHotels' function asks the user for a latitude and longitude, then creates and runs a SQL query to find hotels within a 30-unit radius. The query makes use of a custom 'calculate distance' function to compute the distance between two sets of coordinates. The resulting hotel list is presented in a tabular format.

  

#### Room Search: 
The 'viewRooms' function requests that the user enter a hotel ID and a date. It then creates and runs a SQL query to retrieve all rooms in the specified hotel as well as their availability status on the specified date. The query joins the 'Rooms' and 'RoomBookings' tables using an LEFT JOIN operation and determines the availability of each room based on the existence of a corresponding entry in the 'RoomBookings' table.



#### Room Booking: 
This function books a room specifying a hotelID, roomNumber and booking date. Then validating the information against hotel and room tables in the database. It also queries the RoomBookings table to see if the room is available on the specified date. If the room is available, new booking is added to the RoomBookings table and cost is displayed. To read input from the console, the function uses BufferReader and InputStreamReader. Then it uses SQL queries to validate the input against the database’s hotel, rooms, and roombookings tables. Also it uses date formatting and conversion to handle the input date and display in the correct format.



#### Viewing Recent Bookings: 
Customers can see their most recent five bookings by sorting them by booking date in descending order using this function. The function creates and runs a query to retrieve booking information by joining the RoomBookings and Rooms tables based on hotelID and roomNumber.



#### Updating Room Information: 
This function first checks if the current user is a manager , as only managers are allowed to access this option. Then it asks for hotelID and roomNumber that they want to update. It checks whether the manager manages that hotel and also whether that room exists in the database. If everything goes well then the function prompts the user(manager) to enter a new price and image url for the room. Then it updates the Rooms table in the database with new data and logs the room update in the RoomUpdateLog table.



#### View Recent Update: 
The recent update function allows for a manager to view their 5 most recent room updates for any hotel that they manage. This function requires no input from the user(manager). There is no prompt that the manager will have to input as the manager's unique ID is stored at log in. The managers ID is queried against the RoomUpdatesLog table returning their five most recent room updates consisting of the columns; managerId, hotelID , roomNumber and updatedOn.



#### View Booking History of Hotel: 
The view booking history of hotel function allows for managers to view book history of a specific hotel within a given range of dates from the RooomBookings table. The manager is required to type in the hotelId, room number, and dates in order to query the booking history table. For each prompt, the user input is verified for correctness. The hotel prompt will verify that the hoteldID is managed by the user executing the query. A prompt for a valid date from a start date to an end date.



#### View Regular Customers: 
The view regular customers function allows for a manager to view the top 5 most recurring customers for a specific hotel that is under their management. This function will prompt the user for the ID of a specific hotel for viewing. The hotelID is then queried with the managerID to verify that the manager manages that hotel. The last query will select all of the customers of that hotel and count each visit associated with that customer's ID giving us a total count for each customer's visit. The results are trimmed with a limit of 5 being that the top most recurring customers will be displayed.



#### Place Room Repair Requests: 
This function allows for a hotel manager to place a maintenance repair order for a specific room of a hotel that they manage. The function requires the manager to input the HotelID, roomNumber, and companyID and repair-Date for the repair request. For each prompt, the user input is first verified for correctness. The hotel prompt verifies that the manager is managing that hotel. Room number is verified by checking if that room exists for the given hotel. The company ID is an existing company. And the Date is a valid date that has not yet passed. Once the input data is validated, it is then inserted into the RoomRepairs table which generates a unique repair ID. This unique repair ID is then inserted into the roomRepairRequests table along with the managerID.



#### View Room Repair History: 
This function allows for a hotel manager to view all of their room request history for every hotel that they manage. There is no prompt that the manager will have to input as the manager's unique ID is stored at log in. The managers ID is queried against the roomRepairRequest table to get all repairID’s that are associated with the managers ID. Finally, the repair ID is queried with the roomRepairs table to view the companyID, hotelID, roomNumber and repair Date for each repairID.




### Changes to original template
Separated user type to distinguish between a customer and manager accessing the system.. Stored the user type along with the curruserID from the original template. We used the user type to change the display and selection options for the user at the menu prompt. Customers can not interact with manager logic.. Customers can see options 1-4 and 20 for log out while managers see all options. 
Also added helper function for verification purposes for tasks that were commonly required to call in order to reduce duplicating code.  
  

-   public static int isHotelManager(Hotel esql)
    
-   public static String promptDate()
    
-   public static int validRoom(Hotel esql, int hotelID)
    
-   Public static validCompany(Hotel esql, int hotelID)



### Triggers and Indexes
Implemented index for commonly queried searches such as in managerID and hotelID from the hotel table. Trigger implemented for room repair queries which will call the trigger to update the room repair requests table.
