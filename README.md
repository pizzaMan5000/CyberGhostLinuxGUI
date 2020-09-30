# CyberGhostLinuxGUI

 This is a simple java GUI frontend for the Cyberghost Linux app version 1.3.2. You can choose your connection type, web traffic, streaming, or torrents. Then you can choose from a list of countries, then cites, then server or you can just connect and let the Cyberghost app choose for you. Information is printed on the right side, like if a connection is succesful or not. It will NOT tell you if your VPN connection gets cut off or act as a "kill switch". There is a "check connection" button to run "cyberghostvpn --status" which checks for any VPN connections and it will print the results on the right.
 
 Installation Instructions:
 
 Simply copy the JAR file to a location that works for you, then in a terminal, type "sudo java -jar CyberGhostGUI.jar". I made a script in the /usr/bin folder called "cyberghostgui" that contained:

#!/bin/bash <br>
/usr/bin/java -jar /opt/pizzaMan5000/CyberGhostGUI.jar

Change the last part of last line to the location of the file on your computer. Now you can run it by typing "sudo cyberghostgui &". If nothing happens, it's because the "&" doesn't work with sudo asking for a password. If that happens, just type "sudo ls" and enter your password, then type "sudo cyberghostgui &". You can close the terminal after it is running.

How to use:

The app is simple to use. Click on a service type, web, streaming or torrents. The countries list will fill with available countries. You can select a country then, you can choose a city from the list or just connect. And if you choose a city, you can select a server or just connect. The "Check Connection" button runs "cyberghost --status" which will print out if there is a connection already or not on the right side and enable/disable buttons if necessary. If you minimize, it will become an icon in the system tray (next to the clock). Double click the icon to un-minimize it.
