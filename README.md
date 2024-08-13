# SpringBoot-Running-Tracker-Application

Hi, this is a SpringBoot Running and User Tracking Application

Project Overview:

This project is a backend application developed using Spring Boot, designed to manage 'User' and 'Run' entities through RESTful APIs. The application leverages microservices architecture to ensure scalability and modularity, making it easier to manage and extend.

Key Features:

Entity Management: Implemented a one-to-many relationship between 'User' and 'Run' entities, with data persisted in a MySQL database. This setup efficiently handles the associations between users and their respective runs, supporting comprehensive CRUD operations.

Security: The application is secured using JWT (JSON Web Token) authentication, ensuring that only authenticated users can access protected resources. Additionally, email verification is integrated into the user registration process, adding an extra layer of security to user management.

Role-Based Access Control: Developed RESTful endpoints with role-based access control, ensuring that users have access only to the functionalities appropriate for their roles. This feature is critical for maintaining security and proper resource management in a multi-user environment.

Testing and Stability: The application's stability and reliability were rigorously tested using JUnit and Mockito. Extensive unit tests were conducted to ensure that the role-based access control and other core functionalities perform as expected under various scenarios.

How to Set Up the Project:
In application.properties fill out spring datasource username and password with username and password from your MySQL workbench
under jwt secret add a string that acts as your secret key
add jwt token validity -> this affects how long a token is good for until it expires(3600000 is an hour)
set up spring mail username -> enter in the email you want mail verification links to be sent from
set up spring mail password -> go to google account settings when logged in under the email, go to security, set up 2FA. now go to myaccount.google.om/apppasswords and et up a new app and get your 16 digit/letter password (ex : ABCD EFGH IJKL MNOP)
