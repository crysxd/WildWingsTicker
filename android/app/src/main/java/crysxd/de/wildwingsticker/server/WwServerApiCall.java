package crysxd.de.wildwingsticker.server;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * A class for calling API URLs at the server.
 */
public class WwServerApiCall {

    /* The server domain */
    private final String SERVER_HOST;

    /* The server secret */
    private final String API_KEY_HEADER = "Api-Key";
    private final String API_KEY = "xUSMhPJR7tfd2FnEh9Zz8LvKLaRQqYWMJFqRkYzhhEn9azDhzvHx737H4PqhDbUdHaVTYk27Pe6PDAX4UwrSppsg3FrxHaAfamYZwRWaVTQrjDj5ZAMeN4nGzC57DwdE";

    /* The name of the API function to be called. e.g. "beacon/reportLowBattery" */
    private final String FUNCTION_NAME;

    /* The complete URL */
    private final String FUNCTION_URL;

    /**
     * Creates a new instance for the given function. The function name is e.g "beacons/reportLowBattery" if the
     * PHP-File reportLowBattery.php in the directory beacons should be called.
     * @param functionName the name of the api function which should be called
     */
    public WwServerApiCall(String functionName) {
        this("wwticker.nunki.uberspace.de", functionName);

    }

    /**
     * Creates a new instance for the given function. The function name is e.g "beacons/reportLowBattery" if the
     * PHP-File reportLowBattery.php in the directory beacons should be called.
     * @param host the host address of the server e.g. "safeguard24.care"
     * @param functionName the name of the api function which should be called
     */
    public WwServerApiCall(String host, String functionName) {
        this.FUNCTION_NAME = functionName;
        this.SERVER_HOST = host;
        this.FUNCTION_URL = "https://" + this.SERVER_HOST + "/api/1.0/" + FUNCTION_NAME;

    }

    /**
     * Checks the response of the server. If the server call was unsuccessful, a Exception is raised.
     * @param response the response {@link String}
     * @return a {@link JSONObject} representing the response
     * @throws Exception
     */
    private JSONObject checkAnswer(String response) throws Exception {
        JSONObject answer = new JSONObject(response);
        if (!answer.getBoolean("success")) {
            throw new Exception("Received unsuccessful answer when calling REST API. Error: [" + answer.get("err_no") + "] " + answer.get("err_msg"));

        }

        return answer;

    }

    /**
     * Triggers a POST request at the server with the given data as form data.
     * @param formData a {@link Map} with the data which should be transmitted as POST data
     * @throws Exception
     */
    public JSONObject performPostApiCall(Map<String, String> formData) throws Exception {
        /* Create URL */
        URL url = new URL(this.FUNCTION_URL);

        /* Send Request */
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty(API_KEY_HEADER, API_KEY);
        con.setDoInput(true);
        con.setDoOutput(true);

        /* Write params */
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));

        /* Write all entries as form data */
        for(Map.Entry<String, String> e : formData.entrySet()) {
            String key = e.getKey() == null ? "null" : e.getKey();
            String value = e.getValue() == null ? "null" : e.getValue();
            bw.write(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8") + "&");

        }

        /* Close and flush */
        bw.flush();
        con.getOutputStream().close();

        /* Read response */
        String response = "", line;
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while ((line = br.readLine()) != null) {
            response += line;
        }

        /* Check response */
        return this.checkAnswer(response);
    }

    /**
     * Triggers a GET request at the server with the given data as URL params.
     * @param urlParams a {@link Map} with the data which should be transmitted as URL params
     * @throws Exception
     */
    public JSONObject performGetApiCall(Map<String, String> urlParams) throws Exception {

        StringBuilder urlString = new StringBuilder(this.FUNCTION_URL);
        for(String key : urlParams.keySet()) {
            urlString.append('&');
            urlString.append(key);
            urlString.append('=');
            urlString.append(urlParams.get(key));
        }

        /* Create URL */
        URL url = new URL(urlString.toString());

        /* Send Request */
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty(API_KEY_HEADER, API_KEY);

        /* Read response */
        String response = "", line;
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        while ((line = br.readLine()) != null) {
            response += line;
        }

        /* Check response */
        return this.checkAnswer(response);

    }
}