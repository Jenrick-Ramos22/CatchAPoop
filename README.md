Setup Guide (READ ME)

3.1 Prerequisites

Before installing and running Catch a Poop, make sure that the computer meets the following requirements:
- Operating System: Windows
- Java Version: Java Development Kit (JDK) 21
- Development Environment: Apache NetBeans
- Game Files: Complete Catch a Poop ZIP file

  
3.2 Installation Instructions

To install and launch Catch a Poop, follow these steps:

1. Download and install Java 21 on your computer using the Eclipse Temurin Java download website.
   Suggestion Link: https://adoptium.net/temurin/releases/?version=21
2. Download the Catch A Poop v1.0.1 ZIP file in the github’s releases section on the right or through the link below.
   Github: https://github.com/Jenrick-Ramos22/CatchAPoop/releases/tag/v1.0.0
3. Extract the ZIP file to your preferred folder.
4. Open the extracted Catch a Poop folder.
5. Locate the file named Play-CatchAPoop.bat.
6. Double click to run the file and launch the game.
7. Wait for the game window to appear before starting.


   
3.3 Database Setup
Catch a Poop uses SQLite to store player information, characters, and game scores. The database is automatically created when the game starts, so users do not need to install or set up a separate database.



The database uses the following Java files:

- DatabaseConnection.java — Connects the game to the SQLite database.
- DatabaseInitializer.java — Creates the database tables using schema.sql.
- PlayerDAO.java — Manages player information.
- ScoreDAO.java — Saves and retrieves game scores.
The schema.sql file creates three tables:
- Players - stores player names and IDs.
- Characters - stores four playable characters.
- Scores - stores player scores and dates.
The database is saved at:
C:\Users\<WindowsUsername>.catchapoop\catchapoop.db



Database Setup Steps:

1. Open Catch a Poop.
2. The game automatically connects to SQLite.
3. Missing database tables are created using schema.sql.
4. The game is ready to use.
Player information and scores remain saved even after closing the game.
3.4 Configuration
Catch a Poop requires Java 21 to run.
Installing Java 21:
1. Download Java 21 from https://adoptium.net/temurin/releases/?version=21
2. Run the installer and enable Add to PATH, if available.
3. Finish the installation.
4. Open Command Prompt and type: java -version
The result should show Java 21.


3.5 Building and Running the Project

Developers and instructors can build Catch a Poop using its Java source code.
Prerequisites:
- Java Development Kit (JDK) 21
- Apache NetBeans with Maven support
- Internet connection for downloading Maven dependencies

  
Build Procedure Using Apache NetBeans:
1. Visit the Catch a Poop GitHub Repository.
2. Download the ZIP file named “Catch A Poop (5)”, or clone the repository.
3. Extract the downloaded ZIP file.
4. Open Apache NetBeans.
5. Select File → Open Project.
6. Locate the extracted CatchAPoop folder or the folder where you cloned the repository
7. Open the project.
8. Right-click the project and select Clean and Build.
9. Wait for the build process to finish.
A successful build displays: BUILD SUCCESS


Running the Project:
After building, right-click the project in Apache NetBeans and select Run. The game should launch using Java 21.


3.6 Troubleshooting

Problem 1: Java Is Not Recognized
Error:
'java' is not recognized as an internal or external command


Possible Cause:

Java is not installed or is not included in the system's PATH.

Solution:

Install Java 21, enable Add to PATH if available, and open a new Command Prompt.

Verify the installation using:
java -version


Problem 2: Unsupported Java Version
Error:
UnsupportedClassVersionError


Possible Cause:

Catch a Poop requires Java 21, but the computer is using an older version, such as Java 8.

Solution:
1. Install Java 21 from https://adoptium.net/temurin/releases/?version=21.
2. Open Command Prompt and enter java -version.
3. If an older version appears, configure the system PATH to use Java 21 or update the game launcher to use the installed Java 21 executable.
4. Relaunch Catch a Poop.
Problem 3: JAR File Cannot Be Found
Error:
Unable to access jarfile CatchAPoop.jar


Possible Cause:

The JAR file is missing or Command Prompt is opened in the wrong folder.

Solution:

Ensure that CatchAPoop.jar and Play-CatchAPoop.bat are in the same extracted folder. When using Command Prompt, run the command from the folder containing the JAR file.
Problem 4: Smart App Control Blocks the Launcher
Windows Smart App Control may block the downloaded launcher because it cannot verify the application's trustworthiness.

The solution used during testing was to temporarily disable Smart App Control to allow Catch a Poop to launch. Then re-enable afterwards.
