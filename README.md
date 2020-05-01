# AddressBook
Project for COMP 365 [![CodeFactor](https://www.codefactor.io/repository/github/cadeo111/addressbook/badge)](https://www.codefactor.io/repository/github/cadeo111/addressbook)




### To Fix
- [ ] replace IOexceptions that should be shown to the user with a UserVisibleExceptions
   - [X] create UserVisibleExceptions
   - [ ] in system show DatabaseException's but no IOExceptions
   - [X] replace exeptions in DBs
- [X] when user changes password need to reset all the things
   - privateKey
   - adresses
- [X] when admin changes password
   - all user stuff
   - auditLog
      - log entries
      - private key
- [x] unnecessary exception thrown in audit log
-  [ ] Audit log encrypted entry error at ln 81 -> prints private key to system, which is not good
-  [ ] displays  recoverable error message at log in even though it logs the user in

### Bug-Free
- DeleteRecord.java  - fixed 4/27/20 - 9:26pm
- GetRecord.java - fixed 4/27/20 - 9:26pm
- Logout.java
- Help.java
- Exit.java
- Command.java
- CommandException.java
- AddUser.java
- ChangePassword.java
- DeleteUser.java
- ImportDatabase.java
- ExportDatabase.java
- AddRecord.java
- EditRecord.java
