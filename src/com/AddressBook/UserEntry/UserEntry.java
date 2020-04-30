 /*
  * Title:          com.AddressBook
  * Authors:        Miles Maloney, Caden Keese, Kanan Boubion, Maxon Crumb, Scott Spinali
  * Last Modified:  4/22/20
  * Description: The UserEntry class holds all of the fields , constructors, and a toString method for each user's login data
  * */
 package com.AddressBook.UserEntry;

 public class UserEntry {
     final public String userId;
     final public String passwordHash;
     private long timeStamp;
     private int failedConsecutiveLogins;
     final public String encryptedPrivateKey;
     final public String publicKey;

     public UserEntry(String userId, String passwordHash, String encryptedPrivateKey, String publicKey) {
         this.userId = userId;
         this.passwordHash = passwordHash;
         this.encryptedPrivateKey = encryptedPrivateKey;
         this.publicKey = publicKey;
         timeStamp = System.currentTimeMillis();
         failedConsecutiveLogins = 0;
     }

     protected UserEntry(UserEntry ue) {
         this.userId = ue.userId;
         this.passwordHash = ue.passwordHash;
         this.timeStamp = ue.timeStamp;
         this.failedConsecutiveLogins = ue.failedConsecutiveLogins;
         this.encryptedPrivateKey = ue.encryptedPrivateKey;
         this.publicKey = ue.publicKey;
     }

     public UserEntry(String userEntryString) {
         String[] fields = userEntryString.split(";");
         if (fields.length != 6) {
             throw new RuntimeException("invalid user entry string \n");

         } else {
             this.userId = fields[0];
             this.passwordHash = ((fields[1].equals("none")) ? null : fields[1]);
             this.timeStamp = Long.parseLong(fields[2], 10);
             this.failedConsecutiveLogins = Integer.parseInt(fields[3]);
             this.encryptedPrivateKey = fields[4];
             this.publicKey = fields[5];
         }
     }

     public int getAuthLevel() {
         return (userId.equals("admin")) ? 2 : 1;
     }

     public String toString() {
         return new StringBuilder()
           .append(this.userId).append(";")
           .append((this.passwordHash == null) ? "none" : this.passwordHash).append(";")
           .append(this.timeStamp).append(";")
           .append(this.failedConsecutiveLogins).append(";")
           .append(this.encryptedPrivateKey).append(";")
           .append(this.publicKey)
           .toString();
     }

     public boolean hasLoggedIn() {
         return this.passwordHash != null;
     }

     public long getTimeStampMillis() {
        return timeStamp;
     }

     public void setTimeStampMillis(long millis) {
        timeStamp = millis;
     }

     public int getFailedAttempts() {
        return failedConsecutiveLogins;
     }

     public void setFailedAttempts(int failedConsecutiveLogins) {
        this.failedConsecutiveLogins = failedConsecutiveLogins;
     }
 }
