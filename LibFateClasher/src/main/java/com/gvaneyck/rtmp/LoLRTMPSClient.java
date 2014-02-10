package com.gvaneyck.rtmp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import com.gvaneyck.rtmp.encoding.Base64;
import com.gvaneyck.rtmp.encoding.JSON;
import com.gvaneyck.rtmp.encoding.ObjectMap;
import com.gvaneyck.rtmp.encoding.TypedObject;

/**
 * A very basic RTMPS client for connecting to League of Legends
 * 
 * @author Gabriel Van Eyck
 */
public class LoLRTMPSClient extends RTMPSClient {
    /** Server information */
    private static final int port = 2099; // Must be 2099
    private ServerInfo serverInfo;
    private String server;
    private String region;

    /** Login information */
    private boolean loggedIn = false;
    private String loginQueue;
    private String user;
    private String pass;

    /** Garena information */
    private boolean useGarena = false;
    private String garenaToken;
    private String userID;

    /** Secondary login information */
    private String clientVersion;
    private String ipAddress;
    private String locale;

    /** Connection information */
    private String authToken;
    private String sessionToken;
    private int accountID;

    /**
     * A basic test for LoLRTMPSClient
     * 
     * @param args Unused
     */

    /**
     * Hidden constructor
     */
    @SuppressWarnings("unused")
    private LoLRTMPSClient() {
        super();
    }

    /**
     * Sets up a RTMPSClient for this client to use
     * 
     * @param serverInfo The region to connect to (NA/EUW/EUN)
     * @param clientVersion The current client version for LoL (top left of
     *            client)
     * @param user The user to login as
     * @param pass The user's password
     */
    public LoLRTMPSClient(ServerInfo serverInfo, String clientVersion, String user, String pass) {
        this.serverInfo = serverInfo;
        this.region = serverInfo.region;
        this.server = serverInfo.hostName;
        this.loginQueue = serverInfo.loginQueue;
        this.useGarena = serverInfo.useGarena;

        this.clientVersion = clientVersion;
        this.user = user;
        this.pass = pass;

        // I believe this matters for running the game client
        this.locale = "en_US";

        setConnectionInfo(this.server, port, "", "app:/mod_ser.dat", null);
    }

    /**
     * Sets the locale. I believe this matters for starting the game (looks for
     * fontconfig_locale.txt)
     * 
     * @param locale The locale to use
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Retrieves the server info used to create this client
     * 
     * @return The client's server info
     */
    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    /**
     * Connects and logs in using the information previously provided
     * 
     * @throws IOException
     */
    public void connectAndLogin() throws IOException {
        connect();
        login();
    }

    /**
     * Logs into Riot's servers
     * 
     * @throws IOException
     */
    public void login() throws IOException {
        if (useGarena)
            getGarenaToken();

        getIPAddress();
        getAuthToken();

        TypedObject result, body;

        // Login 1
        body = new TypedObject("com.riotgames.platform.login.AuthenticationCredentials");
        if (useGarena)
            body.put("username", userID);
        else
            body.put("username", user);
        body.put("password", pass); // Garena doesn't actually care about
                                    // password here
        body.put("authToken", authToken);
        body.put("clientVersion", clientVersion);
        body.put("ipAddress", ipAddress);
        body.put("locale", locale);
        body.put("domain", "lolclient.lol.riotgames.com");
        body.put("operatingSystem", "LoLRTMPSClient");
        body.put("securityAnswer", null);
        body.put("oldPassword", null);
        if (useGarena)
            body.put("partnerCredentials", "8393 " + garenaToken);
        else
            body.put("partnerCredentials", null);
        int id = invoke("loginService", "login", new Object[] { body });

        // Read relevant data
        result = getResult(id);
        if (result.get("result").equals("_error"))
            throw new IOException(getErrorMessage(result));

        body = result.getTO("data").getTO("body");
        sessionToken = body.getString("token");
        accountID = body.getTO("accountSummary").getInt("accountId");

        // Login 2
        byte[] encbuff = null;
        if (useGarena)
            encbuff = (userID + ":" + sessionToken).getBytes("UTF-8");
        else
            encbuff = (user.toLowerCase() + ":" + sessionToken).getBytes("UTF-8");

        body = wrapBody(Base64.encodeBytes(encbuff), "auth", 8);
        body.objectType = "flex.messaging.messages.CommandMessage";

        id = invoke(body);
        result = getResult(id); // Read result (and discard)

        // Subscribe to the necessary items
        body = wrapBody(new Object[] { new TypedObject() }, "messagingDestination", 0);
        body.objectType = "flex.messaging.messages.CommandMessage";
        TypedObject headers = body.getTO("headers");
        // headers.put("DSRemoteCredentialsCharset", null); // unneeded
        // headers.put("DSRemoteCredentials", "");

        // bc
        headers.put("DSSubtopic", "bc");
        body.put("clientId", "bc-" + accountID);
        id = invoke(body);
        result = getResult(id); // Read result and discard

        // cn
        headers.put("DSSubtopic", "cn-" + accountID);
        body.put("clientId", "cn-" + accountID);
        id = invoke(body);
        result = getResult(id); // Read result and discard

        // gn
        headers.put("DSSubtopic", "gn-" + accountID);
        body.put("clientId", "gn-" + accountID);
        id = invoke(body);
        result = getResult(id); // Read result and discard

        // Start the heartbeat
        new LCDSHeartbeat(this);

        loggedIn = true;

        System.out.println("Connected to " + region);
    }

    /**
     * Closes the connection
     */
    public void close() {
        loggedIn = false;

        if (out != null) {
            // And attempt to logout, but don't care if we fail
            try {
                int id = invoke("loginService", "logout", new Object[] { authToken });
                join(id);
            }
            catch (IOException e) {
                // Ignored
            }
        }

        super.close();
    }

    /**
     * Additional reconnect steps for logging in after a reconnect
     */
    public void reconnect() {
        // Socket/RTMP reconnect
        super.reconnect();

        // Then login
        while (!isLoggedIn()) {
            try {
                login();
            }
            catch (IOException e) {
                System.err.println("Error when reconnecting: ");
                e.printStackTrace(); // For debug purposes

                sleep(5000);
                super.reconnect(); // Need to reconnect again here
            }
        }
    }

    /**
     * Returns the login state
     * 
     * @return True if passed login queue and commands
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Extracts the rootCause from an error message
     * 
     * @param message The packet result
     * @return The error message
     */
    public String getErrorMessage(TypedObject message) {
        // Works for clientVersion
        return message.getTO("data").getTO("rootCause").getString("message");
    }

    /**
     * Calls Riot's IP address informer
     * 
     * @throws IOException
     */
    private void getIPAddress() throws IOException {
        // Don't need to retrieve IP address on reconnect (probably)
        if (ipAddress != null)
            return;

        String response = readURL("http://ll.leagueoflegends.com/services/connection_info");

        // If we can't get an IP address for whatever reason (site's down, etc.)
        // use localhost
        if (response == null) {
            ipAddress = "127.0.0.1";
            return;
        }

        ObjectMap result = (ObjectMap)JSON.parse(response);
        ipAddress = result.getString("ip_address");
    }

    /**
     * Gets an authentication token from Garena to log in
     * 
     * @throws IOException
     */
    private void getGarenaToken() throws IOException {
        try {
            // This is sloppy reverse engineered (via Wireshark) code
            byte[] md5 = MessageDigest.getInstance("MD5").digest(pass.getBytes("UTF-8"));
            int[] junk;
            Socket sock;
            OutputStream out;
            InputStream in;
            int c;

            // Find our user ID
            sock = new Socket("203.117.158.170", 9100);
            out = sock.getOutputStream();
            junk = new int[] { 0x49, 0x00, 0x00, 0x00, 0x10, 0x01, 0x00, 0x79, 0x2f };
            for (int j : junk)
                out.write(j);

            out.write(user.getBytes());
            for (int i = 0; i < 16 - user.length(); i++)
                out.write(0x00);

            for (byte b : md5)
                out.write(String.format("%02x", b).getBytes());
            out.write(0x00);

            out.write(0x01);
            junk = new int[] { 0xD4, 0xAE, 0x52, 0xC0, 0x2E, 0xBA, 0x72, 0x03 };
            for (int j : junk)
                out.write(j);
            int timestamp = (int)(System.currentTimeMillis() / 1000);
            for (int i = 0; i < 4; i++)
                out.write((timestamp >> (8 * i)) & 0xFF);
            out.write(0x00);

            out.write("intl".getBytes());
            out.write(0x00);

            out.flush();

            // Read the result
            in = sock.getInputStream();

            // Skip the first 5 bytes
            for (int i = 0; i < 5; i++)
                in.read();

            // Get our ID
            int id = 0;
            for (int i = 0; i < 4; i++)
                id += in.read() * (1 << (8 * i));
            userID = String.valueOf(id);

            // Don't care about the rest
            sock.close();

            // Get our token
            sock = new Socket("lol.auth.garenanow.com", 12000);

            // Write our login info
            out = sock.getOutputStream();
            junk = new int[] { 0x32, 0x00, 0x00, 0x00, 0x01, 0x03, 0x80, 0x00, 0x00 };
            for (int j : junk)
                out.write(j);

            out.write(user.getBytes());
            out.write(0x00);

            md5 = MessageDigest.getInstance("MD5").digest(pass.getBytes("UTF-8"));
            for (byte b : md5)
                out.write(String.format("%02x", b).getBytes());
            out.write(0x00);

            out.write(0x00);
            out.write(0x00);

            out.flush();

            // Read our token
            in = sock.getInputStream();
            StringBuilder buff = new StringBuilder();

            // Skip the first 5 bytes
            for (int i = 0; i < 5; i++)
                in.read();

            // Read the result
            while ((c = in.read()) != 0)
                buff.append((char)c);

            garenaToken = buff.toString();

            sock.close();
        }
        catch (NoSuchAlgorithmException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Gets an authentication token for logging into Riot's servers
     * 
     * @throws IOException
     */
    private void getAuthToken() throws IOException {
        // login-queue/rest/queue/authenticate
        // {"rate":60,"token":"d9a18f08-8159-4c27-9f3a-7927462b5150","reason":"login_rate","status":"LOGIN","delay":10000,"user":"USERHERE"}
        // --- OR ---
        // {"node":388,"vcap":20000,"rate":30,
        // "tickers":[
        // {"id":267284,"node":388,"champ":"Soraka","current":248118}, CHAMP
        // MATTERS
        // {"id":266782,"node":389,"champ":"Soraka","current":247595},
        // {"id":269287,"node":390,"champ":"Soraka","current":249444},
        // {"id":270005,"node":387,"champ":"Soraka","current":249735},
        // {"id":267732,"node":391,"champ":"Soraka","current":248190}
        // ],
        // "backlog":4,"reason":"login_rate","status":"QUEUE","champ":"Soraka","delay":10000,"user":"USERHERE"}

        // IF QUEUE
        // login-queue/rest/queue/ticker/CHAMPHERE
        // {"backlog":"8","387":"3d23b","388":"3cba5","389":"3c9ac","390":"3d10a","391":"3cc67"}

        // THEN
        // login-queue/rest/queue/authToken/USERHERE

        // Then optionally
        // login-queue/rest/queue/cancelQueue/USERHERE

        // Initial authToken request
        String payload;
        if (useGarena)
            payload = garenaToken;
        else
            payload = "user=" + user + ",password=" + pass;
        String query = "payload=" + URLEncoder.encode(payload, "ISO-8859-1");

        URL url = new URL(loginQueue + "login-queue/rest/queue/authenticate");

        HttpURLConnection connection;
        if (loginQueue.startsWith("https:")) {
            // Need to ignore certs (or use the one retrieved by RTMPSClient?)
            HttpsURLConnection.setDefaultSSLSocketFactory((SSLSocketFactory)DummySSLSocketFactory.getDefault());
            connection = (HttpsURLConnection)url.openConnection();
        }
        else {
            connection = (HttpURLConnection)url.openConnection();
        }

        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        // Open up the output stream of the connection
        DataOutputStream output = new DataOutputStream(connection.getOutputStream());

        // Write the POST data
        output.writeBytes(query);
        output.close();

        // Read the response
        String response;
        ObjectMap result;
        try {
            response = readAll(connection.getInputStream());
            result = (ObjectMap)JSON.parse(response);
        }
        catch (IOException e) {
            System.err.println("Incorrect username or password");
            throw e;
        }

        // Check for banned or other failures
        // {"rate":0,"reason":"account_banned","status":"FAILED","delay":10000,"banned":7647952951000}
        if (result.get("status").equals("FAILED"))
            throw new IOException("Error logging in: " + result.get("reason"));

        // Handle login queue
        if (!result.containsKey("token")) {
            int node = result.getInt("node"); // Our login queue ID
            String nodeStr = "" + node;
            String champ = result.getString("champ"); // The name of our login
                                                      // queue
            int rate = result.getInt("rate"); // How many tickets are processed
                                              // every queue update
            int delay = result.getInt("delay"); // How often the queue status
                                                // updates

            int id = 0;
            int cur = 0;
            Object[] tickers = result.getArray("tickers");
            for (Object o : tickers) {
                ObjectMap to = (ObjectMap)o;

                // Find our queue
                int tnode = to.getInt("node");
                if (tnode != node)
                    continue;

                id = to.getInt("id"); // Our ticket in line
                cur = to.getInt("current"); // The current ticket being
                                            // processed
                break;
            }

            // Let the user know
            System.out.println("In login queue for " + region + ", #" + (id - cur) + " in line");

            // Request the queue status until there's only 'rate' left to go
            while (id - cur > rate) {
                sleep(delay); // Sleep until the queue updates
                response = readURL(loginQueue + "login-queue/rest/queue/ticker/" + champ);
                result = (ObjectMap)JSON.parse(response);
                if (result == null)
                    continue;

                cur = hexToInt(result.getString(nodeStr));
                System.out.println("In login queue for " + region + ", #" + (int)Math.max(1, id - cur) + " in line");
            }

            // Then try getting our token repeatedly
            response = readURL(loginQueue + "login-queue/rest/queue/authToken/" + user.toLowerCase());
            result = (ObjectMap)JSON.parse(response);
            while (response == null || !result.containsKey("token")) {
                sleep(delay / 10);
                response = readURL(loginQueue + "login-queue/rest/queue/authToken/" + user.toLowerCase());
                result = (ObjectMap)JSON.parse(response);
            }
        }

        // Read the auth token
        authToken = result.getString("token");
    }

    /**
     * Reads all data available at a given URL
     * 
     * @param url The URL to read
     * @return All data present at the given URL
     * @throws IOException
     */
    private String readURL(String url) {
        try {
            return readAll(new URL(url).openStream());
        }
        catch (MalformedURLException e) {
            // Should never happen
            e.printStackTrace();
            return null;
        }
        catch (IOException e) {
            // Only happens when we try to get our token too fast
            return null;
        }
    }

    /**
     * Reads all data from the given InputStream
     * 
     * @param in The InputStream to read from
     * @return All data from the given InputStream
     * @throws IOException
     */
    private String readAll(InputStream in) throws IOException {
        StringBuilder ret = new StringBuilder();

        // Read in each character until end-of-stream is detected
        int c;
        while ((c = in.read()) != -1)
            ret.append((char)c);

        return ret.toString();
    }

    /**
     * Converts a hex string to an integer
     * 
     * @param hex The hex string
     * @return The equivalent integer
     */
    private int hexToInt(String hex) {
        int total = 0;
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            if (c >= '0' && c <= '9')
                total = total * 16 + c - '0';
            else
                total = total * 16 + c - 'a' + 10;
        }

        return total;
    }

    /**
     * Returns the account ID for this connection
     * 
     * @return The account ID
     */
    public int getAccountID() {
        return accountID;
    }

    /**
     * Returns the session token for this connection
     * 
     * @return The session token
     */
    public String getSessionToken() {
        return sessionToken;
    }
}
