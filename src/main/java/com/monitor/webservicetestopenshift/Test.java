/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.monitor.webservicetestopenshift;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author TomHardy
 */
@WebService(serviceName = "Test")
public class Test {

    public boolean test = true;

    final private String userDir = System.getProperty("user.dir");
    final private String generatedFilesPath = userDir + File.separator + "generatedFiles";
    final private File generatedFilesDirectory = new File(generatedFilesPath);
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;
    private FileReader fileReader;
    private BufferedReader bufferedReader;

    private Statement statement = null;
    private Connection connection = null;
    private ResultSet resultset = null;

    /**
     * This is a sample web service operation
     *
     * @param txt
     * @return
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {

        System.out.println("com.monitor.webservicetestopenshift.Test.hello() " + test);

        if (test) {
            test = false;
        } else {
            test = true;
        }

        System.out.println("com.monitor.webservicetestopenshift.Test.hello() " + test);

        return "Hello " + txt + " ! " + connectDB() + " , " + disconnectDB();
    }

    private String connectDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if (connection == null) {
//                connection = DriverManager.getConnection("jdbc:mysql://johnny.heliohost.org/supriyo_sensor_cloud?useSSL=false", "supriyo_63", "sb@9051568624");
//                connection = DriverManager.getConnection("jdbc:mysql://localhost/sensor_cloud?useSSL=false", "root", "");
                connection = DriverManager.getConnection("jdbc:mysql://jws-app-mysql-1-4trr8:3306/sensor_cloud?useSSL=false", "userTY1", "VQvd3Ea0");
            }
            statement = connection.createStatement();

            return "Database Connected successfully.";

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return "webservice: " + ex.toString();
        }
    }

    private String disconnectDB() {
        try {
            connection.close();
            connection = null;

            return "Database Disconnected successfully.";

        } catch (SQLException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return "webservice: " + ex.toString();
        }
    }

    @WebMethod(operationName = "twoDimesionArray")
    public String twoDimesionArray(@WebParam(name = "username") String username, @WebParam(name = "array") String[][] array) {
        int noOfRow = array.length;
        int noOfColomn;

        for (int i = 0; i < noOfRow; i++) {

            noOfColomn = array[i].length;
            for (int j = 0; j < noOfColomn; j++) {
                System.out.println(i + " , " + j + " => " + array[i][j]);
            }
        }
        return putCSV(username, array);
    }

    @WebMethod(operationName = "mainComputations")
    public String mainComputations(@WebParam(name = "service_usersUsername") String service_usersUsername, @WebParam(name = "noOfSensors") String noOfSensors, @WebParam(name = "noOfTargets") String noOfTargets, @WebParam(name = "matrix") String[][] matrix) {

        System.out.println("com.monitor.webservicetestopenshift.Test.mainComputations() : generatedFilesPath -> " + generatedFilesPath);

        if (!generatedFilesDirectory.exists()) {
            System.out.println(" generatedFilesDirectory is created");
            generatedFilesDirectory.mkdirs();
        }

        String result;

        result = genData(service_usersUsername, noOfSensors, noOfTargets);

        if (!result.equals("succeeded")) {
            return result;
        }

        result = genCoverageR(service_usersUsername, matrix);

        if (!result.equals("succeeded")) {
            return result;
        }

        result = genSubsetC(service_usersUsername, noOfSensors);

        if (!result.equals("succeeded")) {
            return result;
        }

        result = callGLPK(service_usersUsername);

        if (!result.equals("succeeded")) {
            return result;
        }

        File coverageNodes = new File(generatedFilesPath + File.separator + service_usersUsername + "_coverageNodes.csv");

        try {
            fileReader = new FileReader(coverageNodes);
            bufferedReader = new BufferedReader(fileReader);

//            System.out.println("line start:");
            String line, rowData[];
            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(" line " + line);
                rowData = line.split(",");
                if (rowData[1].equals("1")) {
                    sendNotification(rowData[0], service_usersUsername);
                }
            }

//            System.out.println("line end:");
            bufferedReader.close();
            fileReader.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return ex.toString();
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return ex.toString();
        }

//        sendNotification("C");
        return "succeeded";
    }

    private String genData(String service_usersUsername, String noOfSensors, String noOfTargets) {

        File data = new File(generatedFilesPath + File.separator + service_usersUsername + "_data.dat");

        try {
            fileWriter = new FileWriter(data);
            bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write("data;\n\n");

            bufferedWriter.write("param noOfSensors:= " + noOfSensors + " ;\n");
            bufferedWriter.write("param noOfTargets:= " + noOfTargets + " ;\n");
            bufferedWriter.write("param coverageR:= \"" + generatedFilesPath + File.separator + service_usersUsername + "_coverageR.csv\" ;\n");
            bufferedWriter.write("param subsetC:= \"" + generatedFilesPath + File.separator + service_usersUsername + "_subsetC.csv\" ;\n");
            bufferedWriter.write("param coverageNodes:= \"" + generatedFilesPath + File.separator + service_usersUsername + "_coverageNodes.csv\" ;\n\n");

            bufferedWriter.write("end;\n");

            bufferedWriter.close();
            fileWriter.close();

            return "succeeded";

        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return ex.toString();
        }
    }

    private String genCoverageR(String service_usersUsername, String[][] matrix) {

        File coverageR = new File(generatedFilesPath + File.separator + service_usersUsername + "_coverageR.csv");

        try {
            fileWriter = new FileWriter(coverageR);
            bufferedWriter = new BufferedWriter(fileWriter);

            int row = matrix.length;
            int column;
            for (int i = 0; i < row; i++) {
                column = matrix[i].length;
                for (int j = 0; j < column; j++) {
                    bufferedWriter.write(matrix[i][j]);
                    if (j < column - 1) {
                        bufferedWriter.write(",");
                    } else {
                        bufferedWriter.write("\n");
                    }
                }
            }

            bufferedWriter.close();
            fileWriter.close();
            return "succeeded";

        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return ex.toString();
        }
    }

    private String genSubsetC(String service_usersUsername, String noOfSensors) {

        File subsetC = new File(generatedFilesPath + File.separator + service_usersUsername + "_subsetC.csv");

        try {
            fileWriter = new FileWriter(subsetC);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("i,y\n");

            int noOfSensorsInt = Integer.parseInt(noOfSensors);
            for (int i = 0; i < noOfSensorsInt; i++) {
                bufferedWriter.write(i + 1 + "," + 1 + "\n");
            }

            bufferedWriter.close();
            fileWriter.close();
            return "succeeded";

        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return ex.toString();
        }
    }

    private String callGLPK(String service_usersUsername) {

        Runtime runtime = Runtime.getRuntime();
        Process process;

        try {
            System.out.println("com.monitor.webservicetestopenshift.Test.callGLPK()  ::   " + "\"" + userDir + File.separator + "gusek" + File.separator + "glpsol\" -m \"" + userDir + File.separator + "gusek" + File.separator + "target.mod\" -d \"" + generatedFilesPath + File.separator + service_usersUsername + "_data.dat\"");
            process = runtime.exec("\"" + userDir + File.separator + "gusek" + File.separator + "glpsol\" -m \"" + userDir + File.separator + "gusek" + File.separator + "target.mod\" -d \"" + generatedFilesPath + File.separator + service_usersUsername + "_data.dat\"");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = "", result = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line + "\n";
//                if (line.matches("[0,1]*")) {
//                    System.out.println(line);
//                }
            }
            System.out.println(result);

            return "succeeded";

        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return ex.toString();
        }
    }

    private String sendNotification(String sensorsId, String temp_service_usersUsername) {

        System.out.println("com.monitor.webservicetestopenshift.Test.sendNotification() " + sensorsId);

        return genOutput(temp_service_usersUsername, sensorsId, "10.2", "10.1");

//        return "succeeded";
    }

    @WebMethod(operationName = "genOutput")
    public String genOutput(@WebParam(name = "service_usersUsername") String service_usersUsername, @WebParam(name = "sensorsId") String sensorsId, @WebParam(name = "proximity") String proximity, @WebParam(name = "light") String light) {

        File tempOutput = new File(generatedFilesPath + File.separator + service_usersUsername + "_tempOutput.csv");

        try {
            fileWriter = new FileWriter(tempOutput, true);
            bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.append(sensorsId + "," + proximity + "," + light + "," + new Date() + "\n");

            bufferedWriter.close();
            fileWriter.close();
            return "succeeded";

        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return ex.toString();
        }
    }

    @WebMethod(operationName = "showOutput")
    public String[][] showOutput(@WebParam(name = "service_usersUsername") String service_usersUsername) {

        File tempOutput = new File(generatedFilesPath + File.separator + service_usersUsername + "_tempOutput.csv");

        List<List<String>> outputList = new ArrayList<>();

        try {
            fileReader = new FileReader(tempOutput);
            bufferedReader = new BufferedReader(fileReader);

            String line, rowData[];
            int length, noOfRow = 0;
            while ((line = bufferedReader.readLine()) != null) {

                rowData = line.split(",");
                List<String> rowDataList = new ArrayList<>();
                length = rowData.length;
                for (int i = 0; i < length; i++) {
                    rowDataList.add(rowData[i]);
                }

                outputList.add(rowDataList);
                noOfRow++;
            }

            String[][] output = new String[noOfRow][4];     //noOfColomn=4

            for (int i = 0; i < noOfRow; i++) {
                for (int j = 0; j < 4; j++) {     //noOfColomn=4
                    output[i][j] = outputList.get(i).get(j);
                }
            }

            bufferedReader.close();
            fileReader.close();
            return output;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            String[][] output = new String[1][1];
            output[0][0] = ex.toString();
            return output;
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            String[][] output = new String[1][1];
            output[0][0] = ex.toString();
            return output;
        }
    }

    @WebMethod(operationName = "clearOutput")
    public String clearOutput(@WebParam(name = "service_usersUsername") String service_usersUsername) {

        System.out.println("com.monitor.webservicetestopenshift.Test.clearOutput() ::\n" + service_usersUsername + "_tempOutput.csv file is deleted -> " + (new File(generatedFilesPath + File.separator + service_usersUsername + "_tempOutput.csv")).delete() + " ;\n" + service_usersUsername + "_coverageNodes.csv file is deleted -> " + (new File(generatedFilesPath + File.separator + service_usersUsername + "_coverageNodes.csv")).delete());

        return "succeeded";
    }

    private String putCSV(String username, String[][] data) {

        String userDir = System.getProperty("user.dir");

        System.out.println("com.mycompany.webservicetest.Test.putCSV() " + userDir);

        String generatedFilesPath = userDir + File.separator + "generatedFiles";
        File generatedFilesDirectory = new File(generatedFilesPath);

        if (!generatedFilesDirectory.exists()) {
            generatedFilesDirectory.mkdirs();
        }

        File coverageR = new File(generatedFilesPath + File.separator + username + "_coverageR.csv");
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;

        try {
            fileWriter = new FileWriter(coverageR);
            bufferedWriter = new BufferedWriter(fileWriter);

            int row = data.length;
            int column;
            for (int i = 0; i < row; i++) {
                column = data[i].length;
                for (int j = 0; j < column; j++) {
                    bufferedWriter.write(data[i][j]);
                    if (j < column - 1) {
                        bufferedWriter.write(",");
                    } else {
                        bufferedWriter.write("\n");
                    }
                }
            }

            bufferedWriter.close();
            fileWriter.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return ex.toString();
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return ex.toString();
        }

        return "success";
    }
}
