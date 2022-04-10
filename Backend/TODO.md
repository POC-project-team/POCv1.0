# Auth
  - Learn about JWT
  - How to implement them in code
  - Create POST method for giving token
  - Parse token from header

    ## Idea
    Not needed to use this stuff, we can do it by the "starinka", can store login and hash for password
    
    When the user passes the login and password need to hash password and check inside the db for the instance
    
    So adding new user is gonna be the same, except the moment, when you need to store the login data, but it's okay

    Seems like not such a bad idea


# API
  - Rewrite API to Swagger methods (7.04.2022)
  - Refactor handler to subrouters and make package for subrouters (if needed)
    It's might become an idea afer auth
    * Server
      |-- Handlers
          |-- UserHandlers
          |-- TagHandler
          |-- NoteHandler
      |-- Server

  ## Rewrite the API
    1. "/getUsers" -> "Users" GET ✅
      Method for me to see the users in the system
    2. "/createUser" -> "/signup" POST {"login":"", "passwd":""} ✅
      Method for creating a new user with his data
    3. "/login" POST
      Method for login in the system
    3. "/GetTags" -> "/Tags" GET ✅
      Method to get all the tags from the user
    4. "/GetNotes" -> "Notes" GET ❌
      Method to get the notes from the user, the tagID need to be passed inside the header
    5. "/addNote" -> "addNote" POST ✅
      Method to add the note to the specific tag, pass the note in body, else - header


# Testing
  - Implement UNIT tests for code
  - Mock DataBase, test and then delete it
  - Test the system complex with Postman (or something like this)