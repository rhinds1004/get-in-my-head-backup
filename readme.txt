Get in my Head
Authors: Robbert Hinds, Zachariah Wingo
Version: 1.0

User Stores Implemented:
- Login
- Display a list of texts to memorize
- Import a text file.
- Save texts to the cloud

Login:
This version allows a user to login to the app with a username and password (u:testUser@uw.edu p:testpassword).

Display List of Texts:
After logging in the user will see two buttons to add/delete texts and a list of available texts.

Import Text file: 
The Add button allows the user to browse the file system and select their own text to import.

Save texts to the cloud (Websevice)
After selecting the text the text is sent to a websevice and stored where it will later be used to populate the main list.


Additional Notes
-At this time the list displays texts but is only used with random pre-populated data.
-The file manager works and allows the selection of text files and sends them to the web
service for storage but we do not currently retrieve them to populate the list.
-The Login system works and all the code to create new accounts is implemented in the backend but we need to create the form.
