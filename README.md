Setting of [palm] file
===============================
1. Install Xamp
2.paste unzip palm folder inside www/htdocs folder 

Setting of PalmScanner (Android)
=================================
1.Setting projects accounding to your gradle
2.set ip address/domain name into PalmScanner\app\src\main\java\com\vritaventures\palmscanner\JConfig.java
  " this is ip/domain of system where xamp is running

Projects Details:
=================
1.PALM SCANNER Will capture the palm and send it to xamp server and finally start matching
2.if matching not found then register it through PALM SCANNER
3.if found then dummy transaction would be fired.

Project Payment in Real time
============================
1. for real time payment we need a token of that account by BANK that will be
   saved into our database system with account holder palm.
2. if match matched in our system then we send respective Token to bank to validate the token
3. if validation goes ahead then transaction would be sent to BANK to debit the amount from that
   account. 
 
	
