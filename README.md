# Ticket Booking System

### How To Run
Open Intellij, in the top right run configuration, add `Main.java` as the Main class. Once the Main class is added as an application, click the Run Button. 

If the run is successful, you will see this in the console
```
__________________________________________________________
Welcome to Ticket Booking!
__________________________________________________________
Opened database successfully
Table created successfully
> 
```
You can then type any command. For example
```$xslt
__________________________________________________________
Welcome to Ticket Booking!
__________________________________________________________
Opened database successfully
Table created successfully
> Setup 3 3 3 3
```
An in-memory SQLite database is automatically created inside `test.db`. You can run this in command-line to access the db
```$xslt
sqlite3 test.db
```
### Available Commands
Note, these commands are <b>case-sensitive</b>.

* `Setup` To setup the number of seats per show
```$xslt
// Setup show number 4 with 3 rows, 2 seats per row, 1 minute cancellation window
Setup 4 3 2 1
```

* `View` To display Show Number, Ticket#, Buyer Phone#, Seat Numbers allocated to the buyer
```$xslt
// View bookings under show number 4
View 4
```

* `Availability` To list all available seat numbers for a show. E,g A1, F4 etc
```$xslt
// View available seats under show number 4
Availability 4
```

* `Book` To book a ticket. This must generate a unique ticket # and display
```$xslt
// Book seats A1, A2, B1 for show number 4, using phone no 82341234 
Book 4 82341234 A1,A2,B1
```

* `Cancel` To cancel a ticket.
```$xslt
// Cancel ticket id 3901306770 with phone no 82341234
Cancel 3901306770 82341234
```
### Assumptions

1) No access control required for admin and buyer, i.e. buyer can execute admin-only commands like `Setup` and `View`. Access control is normally required in enterprise applications, but due to time constraint and the added complexity, this is not implemented.
2) Assume max seats per row is 10 and max rows are 26. Example seat number A1,  H5 etc. The “Add” command for admin must ensure rows cannot be added beyond the upper limit of 26.
3) After booking, User can cancel the seats within a time window of 2 minutes (configurable).   Cancellation after that is not allowed.
4) Only one booking per phone# is allowed per show.