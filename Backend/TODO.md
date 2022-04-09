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

# Testing
  - Implement UNIT tests for code
  - Mock DataBase, test and then delete it
  - Test the system complex with Postman (or something like this)

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
