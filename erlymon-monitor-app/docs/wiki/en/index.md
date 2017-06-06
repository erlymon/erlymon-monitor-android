## Documentation for Erlymon Monitor

### About application

Erlymon Monitor - the application works with the [Erlymon Server] (http://erlymon.org) and [Traccar Server] (http://traccar.org), and allows real-time monitor for monitoring objects.

Key features of the application:
- Monitoring devices
- User Manager
- Access to server settings

### 1. Connection settings to the server

By default, the application is configured to interact with the [Erlymon] (http://www.erlymon.org).

To connect to your server, you must change the connection settings to the server.

To do this, you must perform a few steps:

1. In the login screen you must click on the icon "gear"
![Login window](/images/active_signin_0.png)

2. After pressing the button, a pop-up menu, select "Settings"
![Login window with a pop-up menu](/images/active_signin_1.png)

3. After clicking on the "Settings" menu item, a dialog box appears
![Login window with a dialog box](/images/active_signin_2.png)

In this dialog box you can change the dns/ip, protocol and server version.

"DNS or IP:PORT" - this field in which you must specify the dns or dns:port, and ip:port your server.
"Used Tls/SSL" - this field in which you must specify the use of the https protocol or not (if the checkbox is enabled, use the https protocol).
"Server Version" - this field in which you must choose the version of your server.

Important: At this point, the application works only with the server Erlymon v1.0.0-v1.0.1, or with the server Traccar v3.4.