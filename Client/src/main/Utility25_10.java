package main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;


import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import sun.misc.BASE64Encoder;

import ui.URLConfiguration;


public class Utility25_10 {

    private static Logger log = Logger.getLogger("Utility25_10");


    public static String getDataHttpClient(String url, String userName, String password) {

        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet getRequest = new HttpGet(url);
            getRequest.addHeader("accept", "application/json");
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(userName, password);
            getRequest.addHeader(new BasicScheme().authenticate(creds, getRequest, null));
            HttpResponse response = httpClient.execute(getRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity httpEntity = response.getEntity();
                String apiOutput = EntityUtils.toString(httpEntity);
                httpClient.getConnectionManager().shutdown();
                return apiOutput;
            } else {
                httpClient.getConnectionManager().shutdown();
                return statusCode + "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //    public static HttpURLConnection getHttpURLConnection(String urlString, String userName, String password,
    //                                                         String requestType) {
    //
    //        HttpURLConnection con = null;
    //        try {
    //
    //            URL url = new URL(urlString);
    //            con = (HttpURLConnection) url.openConnection();
    //
    //            // set up url connection to get retrieve information back
    //            con.setRequestMethod(requestType);
    //            con.setDoInput(true);
    //            con.setDoOutput(true);
    //
    //            // stuff the Authorization request header
    //            byte[] encodedPassword = (userName + ":" + password).getBytes();
    //            BASE64Encoder encoder = new BASE64Encoder();
    //            con.setRequestProperty("Authorization", "Basic " + encoder.encode(encodedPassword));
    //            if (con.getResponseCode() == 401) {
    //                log.error("Validation Error\t: AUTH-1  Cannot establish connection");
    //                log.info("Error Code\t\t: " + con.getResponseCode());
    //                log.info("URL\t\t: " + con.getURL());
    //                Utility25_10.logStatistics();
    //            }
    //
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //
    //        }
    //        return con;
    //    }
    //
    //    //get JSON data from URL connection
    //    public static JSONObject getData(HttpURLConnection con) {
    //        JSONObject dataObject = null;
    //        InputStream is = null;
    //        try {
    //
    //            if (con.getResponseCode() == 200) {
    //                is = con.getInputStream();
    //                StringBuffer buf = new StringBuffer();
    //                int c;
    //                while ((c = is.read()) != -1) {
    //                    buf.append((char) c);
    //                }
    //                dataObject = new JSONObject(buf.toString());
    //                is.close();
    //                con.disconnect();
    //                return dataObject;
    //            }
    //        } catch (Exception e) {
    //            //            log.error("getData() " + e);
    //        } finally {
    //            con.disconnect();
    //            try {
    //                is.close();
    //            } catch (Exception e) {
    //            }
    //
    //        }
    //        return dataObject;
    //    }


    // Get Data from Taleo based on status = EMPLOYED

    public static JSONArray getEmployeeData(String urlStringForEmployee, String taleoUserName, String taleoPassword) {

        JSONObject data;
        JSONArray searchResults = null;

        try {
            String stringData = getDataHttpClient(urlStringForEmployee, taleoUserName, taleoPassword);
            if (stringData != null) {
                data = new JSONObject(stringData);
                JSONObject response = (JSONObject) data.get("response");
                searchResults = (JSONArray) response.get("searchResults");
            } else {
                log.error("Validation Error\t: Employees not found in taleo");
                log.info("URL\t\t: " + urlStringForEmployee);
                Utility25_10.logStatistics();
            }
        } catch (Exception e) {
            log.error("Validation Error\t: Employees not found in taleo");
            log.info("URL\t\t: " + urlStringForEmployee);
            Utility25_10.logStatistics();
        }
        return searchResults;
    }

    public static String getLegalEntityID(String hcmUrlString, String hcmUserName, String hcmPassword) {

        String legalEntityId = null;

        JSONObject data = new JSONObject();

        try {
            String stringData = getDataHttpClient(hcmUrlString, hcmUserName, hcmPassword);
            if (stringData != null) {
                data = new JSONObject(stringData);
                JSONArray items = (JSONArray) data.get("items");
                JSONObject n = items.getJSONObject(0);
                legalEntityId = n.get("OrganizationId") + "";
            }

        } catch (Exception e) {
            log.error("Validation Error\t: ED-1 : Legal Entity not found. Legal Entity in Taleo does not match with organization Name in Fusion");
        }

        return legalEntityId;

    }


    public static String getSalaryBasisId(String hcmUrlString, String hcmUserName, String hcmPassword) {

        String salaryBasisId = null;
        String name =
            hcmUrlString.substring((hcmUrlString.lastIndexOf("=") + 1), (hcmUrlString.length())).replaceAll("%20", " ");

        JSONObject data = new JSONObject();
        try {
            String stringData = getDataHttpClient(hcmUrlString, hcmUserName, hcmPassword);
            if (stringData != null) {
                data = new JSONObject(stringData);

                JSONArray items = (JSONArray) data.get("items");

                data = items.getJSONObject(0);

                salaryBasisId = data.get("SalaryBasisId") + "";
            }

        } catch (Exception e) {
            log.error("Validation Error\t: ED-6 : SalaryBasisId not found in Fusion. salaryBasis Name [" + name +
                      "] in Taleo does not match with salaryBasis Name in Fusion");
        }
        return salaryBasisId;
    }


    public static String getJobID(String hcmUrlString, String hcmUserName, String hcmPassword) {

        String jobId = null;
        String name =
            hcmUrlString.substring((hcmUrlString.lastIndexOf("=") + 1), (hcmUrlString.length())).replaceAll("%20", " ");

        JSONObject data = new JSONObject();
        try {
            String stringData = getDataHttpClient(hcmUrlString, hcmUserName, hcmPassword);
            if (stringData != null) {
                data = new JSONObject(stringData);

                JSONArray items = (JSONArray) data.get("items");

                data = items.getJSONObject(0);

                jobId = data.get("JobId") + "";
            }

        } catch (Exception e) {
            log.error("Validation Error\t: ED-5 : JobID not found in Fusion. Job Title [" + name +
                      "] in Taleo does not match with JobName Name in Fusion");
        }
        return jobId;
    }


    public static String getBusinessUnitID(String hcmUrlString, String hcmUserName, String hcmPassword) {

        String businessUnitID = null;

        String name =
            hcmUrlString.substring((hcmUrlString.lastIndexOf("=") + 1), (hcmUrlString.length())).replaceAll("%20", " ");
        JSONObject data = new JSONObject();

        try {
            String stringData = getDataHttpClient(hcmUrlString, hcmUserName, hcmPassword);
            if (stringData != null) {
                data = new JSONObject(stringData);
                JSONArray items = (JSONArray) data.get("items");
                data = items.getJSONObject(0);

                businessUnitID = data.get("OrganizationId") + "";
            }
        } catch (Exception e) {
            log.error("Validation Error\t: ED-2 : Business Unit not found in fusion. Division Name [" + name +
                      "] in Taleo does not match with Business Unit Name in Fusion");
            //            log.error("ED-2 - Cannot find Business unit in Fusion HCM \nPlease check division name ");
        }

        return businessUnitID;
    }

    public static String getDepartmentID(String hcmUrlString, String hcmUserName, String hcmPassword) {

        String departmentID = null;

        String name =
            hcmUrlString.substring((hcmUrlString.lastIndexOf("=") + 1), (hcmUrlString.length())).replaceAll("%20", " ");
        JSONObject data = new JSONObject();

        try {

            String stringData = getDataHttpClient(hcmUrlString, hcmUserName, hcmPassword);
            if (stringData != null) {
                data = new JSONObject(stringData);

                JSONArray items = (JSONArray) data.get("items");

                data = items.getJSONObject(0);

                departmentID = data.get("OrganizationId") + "";
            }
        } catch (Exception e) {
            log.error("Validation Error\t: ED-3 : DepartmentID not found in fusion. Department Name [" + name +
                      "] in Taleo does not match with organization Name in Fusion");
        }

        return departmentID;
    }


    public static String getLocationID(String hcmUrlString, String hcmUserName, String hcmPassword) {

        String locationID = null;


        String name =
            hcmUrlString.substring((hcmUrlString.lastIndexOf("=") + 1), (hcmUrlString.length())).replaceAll("%20", " ");
        JSONObject data = new JSONObject();

        try {

            String stringData = getDataHttpClient(hcmUrlString, hcmUserName, hcmPassword);
            if (stringData != null) {
                data = new JSONObject(stringData);
                JSONArray items = (JSONArray) data.get("items");

                data = items.getJSONObject(0);

                locationID = data.get("LocationId") + "";
            }
        } catch (Exception e) {
            log.error("Validation Error\t: ED-4 : LocationID not found in fusion. Location Name [" + name +
                      "] in Taleo does not match with Location in Fusion");
        }
        return locationID;
    }

    //    static HttpURLConnection con;

    //    public static int postData(String data, String urlString, String userName, String password, String requestMethod) {
    //        HttpURLConnection con = null;
    //        DataOutputStream output = null;
    //        InputStream in = null;
    //        BufferedReader br = null;
    //        int status = 0;
    //
    //        try {
    //            con = Utility25_10.getHttpURLConnection(urlString, userName, password, requestMethod);
    //
    //            con.setDoInput(true);
    //
    //            con.setDoOutput(true);
    //
    //            con.setRequestMethod(requestMethod);
    //
    //            con.setRequestProperty("Content-Type", "application/json");
    //
    //            con.connect();
    //
    //            output = new DataOutputStream(con.getOutputStream());
    //
    //            output.writeBytes(data);
    //            output.flush();
    //            output.close();
    //
    //            if (con.getResponseCode() == 201) {
    //                in = con.getInputStream();
    //                br = new BufferedReader(new InputStreamReader(in));
    //                String line;
    //                String msg = "";
    //                while ((line = br.readLine()) != null) {
    //                    msg = msg + line;
    //                }
    //
    //                JSONObject newJson = new JSONObject(msg);
    //                int personNumber = newJson.getInt("PersonNumber");
    //                log.info("Fusion Person No.\t: " + personNumber);
    //                in.close();
    //
    //            } else if (con.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST & con.getResponseCode() != 200) {
    //                log.info("Error Code\t\t: " + con.getResponseCode() + " (" + con.getResponseMessage() + ")");
    //                // get error from server
    //                in = con.getErrorStream();
    //                br = new BufferedReader(new InputStreamReader(in));
    //                String line;
    //                String msg = "";
    //                while ((line = br.readLine()) != null) {
    //                    msg = msg + line;
    //                }
    //                log.error("Responce Error\t\t: " + msg);
    //                in.close();
    //            }
    //
    //            status = con.getResponseCode();
    //
    //            con.disconnect();
    //
    //        } catch (Exception e) {
    //            System.out.println("postData() : " + e);
    //        }
    //
    //        return status;
    //    }


    public static int postData(String data, String urlString, String userName, String password) {


        int responseCode = 0;

        try {

            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(urlString);

            //            HttpPut httpPut= new HttpPut("url");

            String json = data;
            StringEntity entity = new StringEntity(json);

            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(userName, password);
            httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));

            CloseableHttpResponse response = client.execute(httpPost);

            responseCode = response.getStatusLine().getStatusCode();


            if (responseCode == 201) {
                ResponseHandler<String> handler = new BasicResponseHandler();
                String responseBody = handler.handleResponse(response);

                JSONObject newJson = new JSONObject(responseBody);
                int personNumber = newJson.getInt("PersonNumber");
                log.info("Fusion Person No.\t: " + personNumber);
                client.close();
                return responseCode;

            } else if (responseCode == 200) {
                client.close();
                return responseCode;
            } else {
                log.info("Error Code\t\t: " + responseCode);
                // get error from server
                String responseJSON = EntityUtils.toString(response.getEntity(), "UTF-8");
                log.error("Responce Error\t\t: " + responseJSON);
                client.close();
                return responseCode;
            }


        } catch (Exception e) {
            System.out.println("postData() : " + e);
        }

        return responseCode;
    }
    //PUT data
    public static int putData(String data, String urlString, String userName, String password) {


        int responseCode = 0;

        try {

            CloseableHttpClient client = HttpClients.createDefault();


            HttpPut httpPut = new HttpPut(urlString);

            String json = data;
            StringEntity entity = new StringEntity(json);

            httpPut.setEntity(entity);
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");

            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(userName, password);
            httpPut.addHeader(new BasicScheme().authenticate(creds, httpPut, null));

            CloseableHttpResponse response = client.execute(httpPut);

            responseCode = response.getStatusLine().getStatusCode();


            if (responseCode == 200) {
                client.close();
                return responseCode;
            } else {
                log.info("Error Code\t\t: " + responseCode);
                // get error from server
                ResponseHandler<String> handler = new BasicResponseHandler();
                String responseBody = handler.handleResponse(response);
                log.error("Responce Error\t\t: " + responseBody);
                client.close();
                return responseCode;
            }


        } catch (Exception e) {
            System.out.println("putData() : " + e);
        }

        return responseCode;
    }

    public static URLConfiguration getConfiguration() {

        URLConfiguration cfg = new URLConfiguration();
        FileReader reader = null;
        try {
            reader = new FileReader("authentication.properties");

            Properties p = new Properties();
            p.load(reader);
            cfg.setTaleoUrl(p.getProperty("taleoURL"));
            cfg.setTaleoUserName(p.getProperty("taleoUserName"));
            cfg.setTaleoPassword(p.getProperty("taleoPassword"));

            cfg.setHcmUrlString(p.getProperty("fusionURL"));
            cfg.setHcmUserName(p.getProperty("fusionUserName"));
            cfg.setHcmPassword(p.getProperty("fusionPassword"));

            cfg.setFromMailId(p.getProperty("fromMailID"));
            cfg.setFromMailPassword(p.getProperty("fromMailPassword"));
            cfg.setToRecipient(p.getProperty("toMailID"));
            cfg.setCcRecipient(p.getProperty("ccMailID"));
            cfg.setSmtpHost(p.getProperty("smtpHost"));
            cfg.setSmtpPort(p.getProperty("smtpPort"));
            cfg.setStatus(p.getProperty("status"));

            reader.close();

        } catch (Exception e) {
            log.error("Validation Error\t:\t" + e);
            log.error("Validation Error\t:\t" + "Configuration file not found");
            System.exit(0);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }

        return cfg;
    }


    public static boolean isError(String data) {
        if (data.equalsIgnoreCase(null) || data.equals("null") || data.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public static JSONObject readPersonFile(String personFileName, String defaultFileName, URLConfiguration cfg,
                                            JSONObject employee, HashMap ethnicityData) {

        JSONObject temp = new JSONObject();
        FileReader defaultFileReader = null;
        BufferedReader defaultBufferReader = null;
        FileReader personFileReader = null;
        BufferedReader personBufferReader = null;
        try {
            //Read Persin Default File
            defaultFileReader = new FileReader(defaultFileName);
            defaultBufferReader = new BufferedReader(defaultFileReader);
            String defaultLine;
            while ((defaultLine = defaultBufferReader.readLine()) != null) {

                String s1 = defaultLine.substring(0, defaultLine.indexOf(" "));
                String s2 = defaultLine.substring(defaultLine.indexOf(" ") + 1, defaultLine.length());
                temp.put(s1, s2);
            }
            defaultBufferReader.close();
            defaultFileReader.close();

            //Read Person File
            personFileReader = new FileReader(personFileName);
            personBufferReader = new BufferedReader(personFileReader);
            String personLine;
            JSONArray personDFFArray = new JSONArray();
            JSONObject dffObject = new JSONObject();
            while ((personLine = personBufferReader.readLine()) != null) {

                String[] arr = personLine.split(" ", Integer.MAX_VALUE);

                if (arr[0].equalsIgnoreCase("LegalEntityId")) {
                    String organizationName = employee.get("LegalEntity").toString().replaceAll(" ", "%20");

                    String legalEntityID =
                        Utility25_10.getLegalEntityID(cfg.getHcmUrlString() + "organizations?q=Name=" +
                                                      organizationName, cfg.getHcmUserName(), cfg.getHcmPassword());
                    if (isError(legalEntityID)) {
                        log.error("Validation Error\t: ED-1 : Legal Entity not found. Legal Entity ID in Taleo does not match with organization Name in Fusion");
                    }
                    if (employee.getString(arr[1]).length() == 0) {
                        temp.put(arr[0], arr[4]);
                        continue;
                    }
                    temp.put(arr[0], legalEntityID);
                    continue;
                } else if (arr[2].equalsIgnoreCase("y")) {
                    if (employee.getString(arr[1]).length() == 0) {
                        temp.put(arr[0], arr[4]);
                        continue;
                    }
                    dffObject.put(arr[0], employee.get(arr[1]));
                    personDFFArray.put(dffObject);
                    temp.put("employeetDFF", personDFFArray);
                    continue;
                } else if (arr[3].equalsIgnoreCase("Y")) {
                    if (employee.getString(arr[1]).length() == 0) {
                        temp.put(arr[0], arr[4]);
                        continue;
                    }
                    temp.put(arr[0], (ethnicityData.get(employee.getString(arr[1]).toLowerCase())));
                    continue;
                } else {
                    if (employee.getString(arr[1]).length() == 0) {
                        temp.put(arr[0], arr[4]);
                        continue;
                    }
                    temp.put(arr[0], employee.get(arr[1]));
                }
            }
            personBufferReader.close();
            personFileReader.close();

        } catch (Exception e) {

        } finally {
            try {
                personBufferReader.close();
                personFileReader.close();
                defaultBufferReader.close();
                defaultFileReader.close();
            } catch (IOException e) {
            }
        }
        return temp;
    }


    public static HashMap getFormatData(String fileName) {
        HashMap format = new HashMap();
        FileReader formatFileReader = null;
        BufferedReader formatBufferReader = null;
        try {

            formatFileReader = new FileReader(fileName);
            formatBufferReader = new BufferedReader(formatFileReader);
            String line;
            while ((line = formatBufferReader.readLine()) != null) {
                String s1 = line.substring(0, line.indexOf("~"));
                String s2 = line.substring(line.lastIndexOf("~") + 1, line.length());
                format.put(s1.toLowerCase(), s2);
            }

            formatBufferReader.close();
            formatFileReader.close();

        } catch (Exception e) {
            log.error("Validation Error\t: Ethnicity file not found ");
        } finally {
            try {
                formatBufferReader.close();
                formatFileReader.close();
            } catch (IOException e) {
            }
        }

        return format;

    }

    public static JSONArray readAssignmentFile(String fileName, String defaultFileName, JSONObject employee,
                                               URLConfiguration cfg, JSONObject relationshipUrls) {
        JSONArray assignmentArray = new JSONArray();
        JSONObject temp1 = new JSONObject();
        FileReader defaultFileReader = null;
        BufferedReader defaultBufferReader = null;
        FileReader assignmentFileReader = null;
        BufferedReader assignmentBufferReader = null;
        try {

            //Read Assignment Default File
            defaultFileReader = new FileReader(defaultFileName);
            defaultBufferReader = new BufferedReader(defaultFileReader);
            String defaultLine;
            while ((defaultLine = defaultBufferReader.readLine()) != null) {

                String s1 = defaultLine.substring(0, defaultLine.indexOf(" "));
                String s2 = defaultLine.substring(defaultLine.indexOf(" ") + 1, defaultLine.length());
                temp1.put(s1, s2);
            }

            defaultFileReader.close();
            defaultBufferReader.close();

            //Read Assignment File
            assignmentFileReader = new FileReader(fileName);
            assignmentBufferReader = new BufferedReader(assignmentFileReader);
            String assignmentLine;

            JSONArray assignmentDFFArray = new JSONArray();
            JSONObject dffObject = new JSONObject();
            while ((assignmentLine = assignmentBufferReader.readLine()) != null) {

                String s1 = assignmentLine.substring(0, assignmentLine.indexOf(" "));
                String s2 = assignmentLine.substring(assignmentLine.indexOf(" ") + 1, (assignmentLine.length() - 2));
                String s3 = assignmentLine.charAt(assignmentLine.length() - 1) + "";


                if (s1.equalsIgnoreCase("SalaryBasisId")) {

                    if (isError(employee.get(s2).toString())) {
                        log.error("Validation Error\t: ED-6 : SalaryBasis [\"SalaryBasis\": \"\"] not found in Taleo.");
                    }

                    String getSalaryBasisId =
                        Utility25_10.getSalaryBasisId(cfg.getHcmUrlString() + "salaryBasisLov?q=SalaryBasisName=" +
                                                      employee.get(s2).toString().replaceAll(" ", "%20"),
                                                      cfg.getHcmUserName(), cfg.getHcmPassword());
                    if (isError(getSalaryBasisId)) {
                        log.error("Validation Error\t: ED-6 : SalaryBasisID not found in Fusion. SalaryBasis [" +
                                  employee.get(s2) + "] in Taleo does not match with SalaryBasis Name in Fusion");
                    }
                    temp1.put(s1, getSalaryBasisId);
                    continue;
                }


                if (s1.equalsIgnoreCase("JobId")) {

                    if (isError(employee.get(s2).toString())) {
                        log.error("Validation Error\t: ED-5 : JobTitle not [\"JobTitle\": \"\"] found in Taleo.");
                    }

                    String getJobID =
                        Utility25_10.getJobID(cfg.getHcmUrlString() + "jobs?q=Name=" +
                                              employee.get(s2).toString().replaceAll(" ", "%20"), cfg.getHcmUserName(),
                                              cfg.getHcmPassword());
                    if (isError(getJobID)) {
                        log.error("Validation Error\t: ED-5 : JobID not found in Fusion. Job Title [" +
                                  employee.get(s2) + "] in Taleo does not match with JobName Name in Fusion");
                    }
                    temp1.put(s1, getJobID);
                    continue;
                }
                if (s1.equalsIgnoreCase("BusinessUnitId")) {

                    if (isError(employee.get(s2).toString())) {
                        log.error("Validation Error\t: ED-2 : Division relationship URL not found in Taleo.");
                    }

                    JSONObject division = getRelationshipURLData(relationshipUrls, s2, cfg);

                    if (division == null) {
                        log.error("Validation Error\t: ED-2 : Division Name [\"divisionName\": \"\"] not found in Taleo.");
                        continue;
                    }

                    String getBusinessUnitID =
                        Utility25_10.getBusinessUnitID(cfg.getHcmUrlString() + "organizations?q=Name=" +
                                                       division.getString("divisionName"), cfg.getHcmUserName(),
                                                       cfg.getHcmPassword());
                    if (isError(getBusinessUnitID)) {
                        log.error("Validation Error\t: ED-2 : Business Unit not found in fusion. Division Name [" +
                                  employee.get(s2) + "] in Taleo does not match with Business Unit Name in Fusion");
                    }
                    temp1.put(s1, getBusinessUnitID);
                    continue;
                }

                if (s1.equalsIgnoreCase("DepartmentId")) {

                    if (isError(employee.get(s2).toString())) {
                        log.error("Validation Error\t: ED-3 : Department relationship URL not found in Taleo.");
                    }

                    JSONObject department = getRelationshipURLData(relationshipUrls, s2, cfg);
                    if (department == null) {
                        log.error("Validation Error\t: ED-3 : Department Name [\"departmentName\": \"\"] not found in Taleo.");
                        continue;
                    }
                    String departmentName = department.getString("departmentName").replaceAll(" ", "%20");

                    String getDepartmentID =
                        Utility25_10.getDepartmentID(cfg.getHcmUrlString() + "organizations?q=Name=" + departmentName,
                                                     cfg.getHcmUserName(), cfg.getHcmPassword());
                    if (isError(getDepartmentID)) {
                        log.error("Validation Error\t: ED-3 : DepartmentID not found in fusion. Department Name [" +
                                  employee.get(s2) + "] in Taleo does not match with organization Name in Fusion");
                    }
                    temp1.put(s1, getDepartmentID);
                    continue;
                }
                if (s1.equalsIgnoreCase("LocationId")) {

                    if (isError(employee.get(s2).toString())) {
                        log.error("Validation Error\t: ED-4 : Location relationship URL not found in Taleo.");
                    }

                    JSONObject location = getRelationshipURLData(relationshipUrls, s2, cfg);

                    if (location == null) {
                        log.error("Validation Error\t: ED-4 : Location Name [\"locationName\": \"\"] not found in Taleo.");
                        continue;
                    }

                    String locationName = location.getString("locationName").replaceAll(" ", "%20");
                    String getLocationID =
                        Utility25_10.getLocationID(cfg.getHcmUrlString() + "locations?q=LocationName=" + locationName,
                                                   cfg.getHcmUserName(), cfg.getHcmPassword());
                    if (isError(getLocationID)) {
                        log.error("Validation Error\t: ED-4 : LocationID not found in fusion. Location Name [" +
                                  employee.get(s2) + "] in Taleo does not match with Location in Fusion");
                    }
                    temp1.put(s1, getLocationID);
                    continue;
                }
                if (s3.equalsIgnoreCase("y")) {
                    if (employee.getString(s2).length() == 0) {
                        continue;
                    }
                    dffObject.put(s1, employee.get(s2));
                    continue;
                }
                if (s1.equalsIgnoreCase("SalaryAmount")) {
                    String salary = employee.get(s2).toString();
                    salary = salary.replaceAll(",", "");
                    salary = salary.substring(0, salary.indexOf("."));
                    temp1.put(s1, salary);
                    continue;
                }

                temp1.put(s1, employee.get(s2));
            }
            assignmentDFFArray.put(dffObject);
            temp1.put("assignmentDFF", assignmentDFFArray);
            assignmentArray.put(temp1);
            assignmentFileReader.close();
            assignmentBufferReader.close();

        } catch (Exception e) {


        } finally {
            try {
                assignmentFileReader.close();
                assignmentBufferReader.close();
                defaultFileReader.close();
                defaultBufferReader.close();
            } catch (IOException e) {
            }
        }
        return assignmentArray;
    }

    public static JSONObject getRelationshipURLData(JSONObject relationshipUrls, String name, URLConfiguration cfg) {

        JSONObject data = new JSONObject();
        JSONObject relationshipData = new JSONObject();
        String stringData =
            getDataHttpClient(relationshipUrls.get(name).toString(), cfg.getTaleoUserName(), cfg.getTaleoPassword());
        if (stringData != null) {
            data = new JSONObject(stringData);
            if (data == null) {
                return data;
            }
            JSONObject responce = data.getJSONObject("response");
            relationshipData = responce.getJSONObject(name);
        }

        return relationshipData;

    }

    public static void updateTaleo(String url, URLConfiguration cfg, String msg) {

        JSONObject employee = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("PostedtoFusion", msg);
        employee.put("employee", data);

        String forPosting = employee.toString();

        int code =
            Utility25_10.putData(forPosting, cfg.getTaleoUrl() + url, cfg.getTaleoUserName(), cfg.getTaleoPassword());
        if (code == 200) {

            log.debug("Taleo Status\t\t: Posted");
        } else {

            log.error("Taleo Status\t\t: Failed to post taleo");
        }

    }

    public static String toInitCap(String param) {
        if (param != null && param.length() > 0) {
            char[] charArray = param.toCharArray();
            charArray[0] = Character.toUpperCase(charArray[0]);
            return new String(charArray);
        } else {
            return "";
        }
    }

    // Check Fusion Duplicate
    public static boolean checkFusion(String firstName, String middleName, String lastName) {


        JSONObject data;
        URLConfiguration cfg = Utility25_10.getConfiguration();

        try {

            String stringData =
                getDataHttpClient(cfg.getHcmUrlString() + "emps/?q=FirstName=" + firstName + ";MiddleName=" +
                                  middleName + ";LastName=" + lastName, cfg.getHcmUserName(), cfg.getHcmPassword());

            data = new JSONObject(stringData);
            int count = data.getInt("count");
            if (count > 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return false;
    }

    public static void logStatistics() {
        log.info("-------------------------------- Statistics --------------------------------------------------\r\n");
        log.info("Rows processed\t\t: " + Main25_10.totalEmployee);
        log.info("Posted to fusion\t: " + Main25_10.postedEmployee);
        log.info("Not Posted\t\t: " + (Main25_10.totalEmployee - Main25_10.postedEmployee));
        log.info("----------------------------------------------------------------------------------------------\r\n");

        SendMail.sendMail();
        System.exit(0);

    }
}

