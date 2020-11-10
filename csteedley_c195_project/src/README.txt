Title: Scheduler for All

By: Chris Steedley
contact: cms16000@gmail.com

Application version: 1.00 2020-11-07

*****************************
TO RUN: (hopefully this is what you meant by instructions to run)
*****************************
not compiled to an executable,  unzip folder and navigate to src/main/main.java and open in ide and then run.


Additional report: first I added a report giving a customer schedule by name, but since it was so close to the contact schedule we already
had to do. I also added view total reports by the year to make sure I was good.

additional search: added search feature on home page to search appointments by month/ year. defaults to current year/ month if left blank.
 note leave it blank when using radio to view current month.

 lambda stuff is all near the top of main screen controller for whomever it concerns.


 This was a huge challenge for me. coming into the CS program with no experience this was a large undertaking. while I'm sure there
 are way better ways to implement certain things. I think It turned Out quite well.


*******************************************
 software used:

 IDE: Intellij community version 2020.2.3
 JDK SE 11.0.8
 Java fx version:javafx-sdk-11.0.2

********************************************

Application version 1.01
fixed several errors. forgot a ! to make an if statement check run only if something was not true. (no overlap appointment check)
added appointment Id to delete confirmation.
changed login screen from country display in corner to zoneId (note in video about project one professor said using country
 there was fine)

Created new method in add customer customer to filter divison on country selected. Hopefully this is what you wanted fixed.
thanks enjoy  location line 159 addcustomer controller.

version: 1.02
fixed bug where if appointment encompassed entire appointment it would allow overlapping
modapptcontroller lines 384
addapptcontroller lines 344