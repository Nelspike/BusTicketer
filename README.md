#BusTicketer

###Overview

BusTicketer is an application for Android devices (minimum 4.3) that consists on the marketing process of bus tickets. The current project is divided into three different applications that consist on three different modules:

* Passenger Mode - The application to be used by the passengers of the bus system;
* Inspector Mode - The application to be used by the ticket inspectors/checkers;
* Validation Mode - The application to be used by the validation modules in each one of the buses;
* Server in Node.js - The server that handles all of the requests.

Each one of these applications has a distinct level of communitcation with one another: the NFC technology.

### Passenger Mode

Each passenger has the power to buy and store tickets in their application, as well as use them for various ocasions. The user can then:

* Buy tickets - The user can buy up to 10 tickets of each type (T1, T2 and T3), and use them accordingly;
* Store tickets - Everytime the user has bought tickets ,these get stored within the application;
* Validate/Use Tickets - As simple as it gets: The user can validate each one of the purchased tickets. Upon validation, the user get a certain time limit to use the recently validated ticket.

This application is structured using the Fragment Technology from Android. Each one of the fragments represents a different section of the app.

### Inspector Mode

Since each passenger will have tickets stored within the application, each inspector will have to be able to validate them.

* Firstly, the inspector boards the vehicle, and checks in by telling the bus' terminal he/she is there;
* With this validation, the inspector can now inspect all of the tickets that have been validated aboard this vehicle;
* Upon validation, a new screen is prompted to the inspector showing the ticket's validity status.

### Validation Mode

Simply, each validation is done via the NFC technology. Each passenger can validate tickets on the terminal, as much as each inspector can check in the vehicle throughout this very same terminal. The application consists of one screen only, which will be static and only used for NFC communications.

### Screenshots

<div style="text-align: center">
  <img src="/images/Intro.png" width="200px" >
  <img src="/images/ShowFragment.png" width="200px" >
  <img src="/images/TicketFragment.png" width="200px" >
  <img src="/images/PurchaseConfirm.png" width="200px" >
  <img src="/images/PurchaseConcluded.png" width="200px" >
  <img src="/images/ValidTicket.png" width="200px" >
</div>
