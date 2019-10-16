package main;

import java.util.Date;

import java.util.HashMap;

import org.apache.log4j.Logger;

import org.json.JSONArray;

import org.json.JSONObject;

import ui.URLConfiguration;

public class Main25_10 {


    private static Logger log = Logger.getLogger("Main25_10");
    public static int totalEmployee;
    public static int postedEmployee;

    public static void main(String args[]) {

        log.info("--------------------------------------------------------------------------------" +
                 (new Date().toString()) +
                 "---------------------------------------------------------------------------------\r\n");
        URLConfiguration cfg = Utility25_10.getConfiguration();

        HashMap ethnicityData = Utility25_10.getFormatData("formatMapping.txt");


        JSONArray searchResults =
            Utility25_10.getEmployeeData(cfg.getTaleoUrl() + "object/employee/search?" +
                                         (cfg.getStatus().replaceAll(" ", "%20")), cfg.getTaleoUserName(),
                                         cfg.getTaleoPassword());


        JSONObject temp = new JSONObject();

        for (int i = 0; i < searchResults.length(); i++) {

            JSONObject allEmployee = searchResults.getJSONObject(i);
            JSONObject employee = (JSONObject) allEmployee.get("employee");
            JSONObject relationshipUrls = employee.getJSONObject("relationshipUrls");


            if (employee.getString("PostedtoFusion").equalsIgnoreCase("Posted") |
                employee.getString("PostedtoFusion").equalsIgnoreCase("Failed")) {
                continue;
            }
            totalEmployee++;
            log.debug("\r\n************************************************************************************************************************************************************************\r\n");
            log.info("Employee\t\t: " + employee.getString("firstName") + " " + employee.getString("lastName"));
            if (Utility25_10.isError(employee.get("LegalEntity").toString())) {
                log.error("Validation Error\t: ED-1 : Cannot find legal entity [\"LegalEntity\": \"\"] in Taleo ");
            }


            //            Rest call in Fusion to search for an employee in Uppercase
            if (Utility25_10.checkFusion(employee.getString("firstName").toUpperCase().replaceAll(" ", "%20"),
                                         employee.getString("middleInitial").toUpperCase().replaceAll(" ", "%20"),
                                         employee.getString("lastName").toUpperCase().replaceAll(" ", "%20"))) {

                Utility25_10.updateTaleo("object/employee/" + employee.get("employeeId"), cfg, "Failed");
                log.error("Validation Error\t: ED-6 : Employee found in fusion. This is a possible case of a Re-Hire. Please verify data and Re-hire the person manually in Fusion");
                log.debug("\r\n************************************************************************************************************************************************************************\r\n");
                continue;
            }
            //            Rest call in Fusion to search for an employee in Lowercase
            else if (Utility25_10.checkFusion(employee.getString("firstName").toLowerCase().replaceAll(" ", "%20"),
                                              employee.getString("middleInitial").toLowerCase().replaceAll(" ", "%20"),
                                              employee.getString("lastName").toLowerCase().replaceAll(" ", "%20"))) {

                Utility25_10.updateTaleo("object/employee/" + employee.get("employeeId"), cfg, "Failed");
                log.error("Validation Error\t: ED-6 : Employee found in fusion. This is a possible case of a Re-Hire. Please verify data and Re-hire the person manually in Fusion");
                log.debug("\r\n************************************************************************************************************************************************************************\r\n");
                continue;
            }
            //            Rest call in Fusion to search for an employee in InitCap
            else if (Utility25_10.checkFusion(Utility25_10.toInitCap(employee.getString("firstName")).replaceAll(" ",
                                                                                                                 "%20"),
                                              Utility25_10.toInitCap(employee.getString("middleInitial")).replaceAll(" ",
                                                                                                                     "%20"),
                                              Utility25_10.toInitCap(employee.getString("lastName")).replaceAll(" ",
                                                                                                                "%20"))) {

                Utility25_10.updateTaleo("object/employee/" + employee.get("employeeId"), cfg, "Failed");
                log.error("Validation Error\t: ED-6 : Employee found in fusion. This is a possible case of a Re-Hire. Please verify data and Re-hire the person manually in Fusion");
                log.debug("\r\n************************************************************************************************************************************************************************\r\n");
                continue;
            }


            else {
                try {
                    //   Reading Persnol Data from person mapping file
                    temp =
                        Utility25_10.readPersonFile("personMapping.txt", "personDefaultValues.txt", cfg, employee,
                                                    ethnicityData);

                    //   Reading Assignment Data from assignment file
                    JSONArray assignmentArray =
                        Utility25_10.readAssignmentFile("assignmentMapping.txt", "assignmentDefaultValues.txt",
                                                        employee, cfg, relationshipUrls);
                    temp.put("assignments", assignmentArray);

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
             
            String postData = temp.toString().replaceAll("\"null\"", "null");
            System.out.println(postData);

            int code =
                Utility25_10.postData(postData, cfg.getHcmUrlString() + "emps", cfg.getHcmUserName(),
                                      cfg.getHcmPassword());
            if (code == 201) {
                Main25_10.postedEmployee++;
                log.info("Fusion Status\t\t: Posted");
                Utility25_10.updateTaleo("object/employee/" + employee.get("employeeId"), cfg, "Posted");
            } else {
                log.info("Fusion Status\t\t: Not Posted");
            }
            log.debug("\r\n************************************************************************************************************************************************************************\r\n");

        }
        Utility25_10.logStatistics();
    }

}

