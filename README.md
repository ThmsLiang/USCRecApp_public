
# USCRecApp

An Android app for USC students to make reservations at recreation centers conveniently

## Before Viewing!

This is a public repository of USC RecApp project. To make it work, you have to have a Sendgid API Key and insert it into `USCRecApp_public\app\src\main\java\com\example\myapplication\DeleteAppointmentActivity.java` because Sendgrid does not allow public API Key.

## Preview

<img src="https://github.com/ThmsLiang/USCRecApp_public/blob/main/screenshots/image-20220427184426008.png" width="140" height="300" /><img src="https://github.com/ThmsLiang/USCRecApp_public/blob/main/screenshots/image-20220427184507432.png" width="140" height="300" /><img src="https://github.com/ThmsLiang/USCRecApp_public/blob/main/screenshots/image-20220427184550417.png" width="140" height="300" />

## Release News and Changes Since Project 2.4 !!!!

We now have a new feature, the use will be notified through a system notification for their upcoming appointments. (Not the email notification for the users in the waiting list, which has been implemented in Project 2.4.)
The user can navigate to notification settings through the gear button on the Google map page, and they are able modify how long before the appointment they receive a notification. And they will be notified according to the time they set.

We also made great UI changes to the timeslot detail page. Now the page fits the overall theme of the app and show our trojan pride. This page is now prettier, more usable, and more user-friendly.

We also constructed this detailed in-depth readme file in this sprint, not only to make this project looks more professional, but also to get users started with our product.

More timeslots were added to each of the recreation centers. And since we are using firebase cloud database, updating the timeslot information is very easy through the firebase console. This guarantees the usability and ease of maintenance of our app.

By clicking on compass button on the Google map page, the app now will lead the user to their location.

At last, we now use Tommy Trojan for the app icon!

Note: The "locate me" functionality only works on a local device. If you click on the button on an emulator, it will always lead you to Google's head quarter instead of your location.

## Description

This is an Android app designed for USC students during the COVID-19 pandemic.

Under USC's COVID-19 policy, students has to make a reservation before entering the recreation centers on campus. There will be several timeslots per day, but each timeslots will have a capacity.

First time users will be able to register. Patrons can login directly.

Upon register or login, users will be able to see their profile photo and USC ID number. They will always have the chance to update them.

This app currently supports three recreation centers, the Lyon Center, the Cromwell Track, and the UAC Lap Swim. Upon launching, users will see a Google map displaying the locations of the recreation centers. By clicking on the clip on the map, the app will jump to a booking page displaying all the upcoming timeslots. Each timeslots will contain information including the date, the time, and the remaining spots.

Click to select a timeslot to enter the timeslot's detail page. There will be two buttons on this page, a "Reserve" button and a "Remind me" button. The "Reserve" button will be clickable if there are still remaining spots for the current time slot; otherwise, the "Remind me" button will be clickable. The user can click on "Reserve" to make an appointment, or click on "Remind me" to sign up for an reminder when another user drop their reservation or additional spots open up. They will receive an email notification if they have signed up for an reminder.

The user can also click on the button on the top right corner of the screen when they are in the Google map page. They will be directed into a page where they can browse through all their upcoming appointments, as well as the past reservations.

## Getting Started



### Dependencies



* This app supports a minimum Sdk version of 28.  But the target Sdk version is 32.

* All the other dependencies are listed and handled in the app level build.gradle file.


### Installing



* Clone this repository to your local machine and open it in Android Studio.

* Create a new device emulator that satisfies the SDK requirements (>=28) to install the app.



### Executing program


* Do gradle sync first, then select the app in the drop down menu on the top right of Android studio. Click on the triangle to install and run the app.

* Select on one the JUnit tests or the espresso unit tests to run the automatic tests.


## Help

Comment on Github for any issues and we would love to help!

## Authors



Contributors names and contact info



**Ryan (Yuzhe) Wang**
[@Ryan (Yuzhe) Wang](https://github.com/Yuzhe-Wang)

**Thomas Liang**
[@Thomas Liang](https://github.com/ThmsLiang)

**Eli Morris**
[@Eli Morris](https://github.com/silorriem)

## Version History


2.5

* Doing a sprint cycle of 2 weeks for some additional features and wrapping it up. See the sprint document for detail.

2.4

* Adding 30 unit tests for testing purposes. Including JUnit tests and Espresso tests.

2.3

* Initial implementation

2.2

* Creating the design document according to the requirements finished in 2.1

2.1

* Consulting the customer to generate the requirements document


## Acknowledgments

Inspiration for is Readme file

* [awesome-readme](https://github.com/matiassingers/awesome-readme)

* [PurpleBooth](https://gist.github.com/PurpleBooth/109311bb0361f32d87a2)

* [dbader](https://github.com/dbader/readme-template)

* [zenorocha](https://gist.github.com/zenorocha/4526327)
