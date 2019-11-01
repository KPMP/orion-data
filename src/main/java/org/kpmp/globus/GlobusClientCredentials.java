package org.kpmp.globus;

public class GlobusClientCredentials {

    /** Value of the "API Key". */
    public static final String API_KEY = "36d0efc1-a1ea-4d8d-be32-b29b1de65b92";

    /** Value of the "API Secret". */
    public static final String API_SECRET = "H98krys6PaUV/tYs4sDNas8MiYXRtKFxBrbI1EqIyDs=";

    /** Port in the "Callback URL". */
    public static final int PORT = 8888;

    /** Domain name in the "Callback URL". */
    public static final String DOMAIN = "localhost";

    public static void errorIfNotSpecified() {
        if (API_KEY.startsWith("Enter ") || API_SECRET.startsWith("Enter ")) {
            System.out.println(
                    "Enter API Key and API Secret from http://www.dailymotion.com/profile/developer"
                            + " into API_KEY and API_SECRET in " + GlobusClientCredentials.class);
            System.exit(1);
        }
    }
}
