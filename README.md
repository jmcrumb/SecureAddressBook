# AddressBook
Project for COMP 365 [![CodeFactor](https://www.codefactor.io/repository/github/cadeo111/addressbook/badge)](https://www.codefactor.io/repository/github/cadeo111/addressbook)




### To Fix
- [ ] replace IOexceptions that should be shown to the user with a DatabaseException
      - create DatabaseException
      - in system show DatabaseException's but no IOExceptions
- [ ] when user changes password need to reset all the things
   - privateKey
   - adresses
- [ ] when admin changes password
   - all user stuff
   - auditLog
      - log entries
      - private key


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
