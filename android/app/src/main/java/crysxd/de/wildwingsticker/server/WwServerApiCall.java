package crysxd.de.wildwingsticker.server;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * A class for calling API URLs at the server.
 */
public class WwServerApiCall {

    /* The server secret */
    private final String API_KEY_HEADER = "Api-Key";
    private final String API_KEY = "xUSMhPJR7tfd2FnEh9Zz8LvKLaRQqYWMJFqRkYzhhEn9azDhzvHx737H4PqhDbUdHaVTYk27Pe6PDAX4UwrSppsg3FrxHaAfamYZwRWaVTQrjDj5ZAMeN4nGzC57DwdE";

    /* The complete URL */
    private final URL FUNCTION_URL;

    /**
     * Creates a new instance for the given function. The function name is e.g "beacons/reportLowBattery" if the
     * PHP-File reportLowBattery.php in the directory beacons should be called.
     * @param functionName the name of the api function which should be called
     */
    public WwServerApiCall(String functionName) throws MalformedURLException {
        this.FUNCTION_URL = new WwServerURLBuilder(functionName).build();

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
        /* Send Request */
        HttpURLConnection con = (HttpURLConnection) this.FUNCTION_URL.openConnection();
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

        StringBuilder urlString = new StringBuilder(this.FUNCTION_URL.toString());
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