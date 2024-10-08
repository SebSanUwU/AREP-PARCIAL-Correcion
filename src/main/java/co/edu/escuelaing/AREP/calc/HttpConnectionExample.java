package co.edu.escuelaing.AREP.calc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnectionExample {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost:36000";

    /**
     * Procesa las peticiones del cliente para ser enviadas al servidor.
     * @param request Peticion enviada desde el cliente
     * @return La respuesta del servidor.
     * @throws IOException En caso de no poder ejecutar la coneccion o algun error se manda la excepcion.
     */
    public static String getResponse(String request) throws IOException {

        URL obj = new URL(GET_URL + request);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        System.out.println("PATH"+obj.getPath());
        System.out.println("REQ"+obj.getQuery());

        //The following invocation perform the connection implicitly before getting the code
        int responseCode = con.getResponseCode();
        System.out.println(responseCode);
        System.out.println("GET Response Code :: " + responseCode);

        String responseString = "Error";

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            responseString = response.toString();
            System.out.println(responseString);
        } else {
            System.out.println("GET request not worked");
        }
        System.out.println("GET DONE");
        return responseString;
    }

}
