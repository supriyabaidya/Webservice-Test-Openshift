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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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

//    final private String gusekDir = (getClass().getResource("../../../../../gusek").getPath()).substring(1).replace("/", File.separator);
    final private String userDir = System.getProperty("user.dir");
    final private String generatedFilesPath = userDir + File.separator + "generatedFiles";
    final private File generatedFilesDirectory = new File(generatedFilesPath);
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;

    /**
     * This is a sample web service operation
     *
     * @param txt
     * @return
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
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
    public String mainComputations(@WebParam(name = "username") String username, @WebParam(name = "noOfSensors") String noOfSensors, @WebParam(name = "noOfTargets") String noOfTargets, @WebParam(name = "matrix") String[][] matrix) {

        System.out.println("com.monitor.webservicetestopenshift.Test.mainComputations() : generatedFilesPath -> " + generatedFilesPath);

        if (!generatedFilesDirectory.exists()) {
            System.out.println(" generatedFilesDirectory is created");
            generatedFilesDirectory.mkdirs();
        }

        String result;

        result = genData(username, noOfSensors, noOfTargets);

        if (!result.equals("succeeded")) {
            return result;
        }

        result = genCoverageR(username, matrix);

        if (!result.equals("succeeded")) {
            return result;
        }

        result = genSubsetC(username, noOfSensors);

        if (!result.equals("succeeded")) {
            return result;
        }

        result = callGLPK(username);

        if (!result.equals("succeeded")) {
            return result;
        }

        sendNotification();

        return "succeeded";
    }

    private String genData(String username, String noOfSensors, String noOfTargets) {

        File data = new File(generatedFilesPath + File.separator + username + "_data.dat");

        try {
            fileWriter = new FileWriter(data);
            bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write("data;\n\n");

            bufferedWriter.write("param noOfSensors:= " + noOfSensors + " ;\n");
            bufferedWriter.write("param noOfTargets:= " + noOfTargets + " ;\n");
            bufferedWriter.write("param coverageR:= \"" + generatedFilesPath + File.separator + username + "_coverageR.csv\" ;\n");
            bufferedWriter.write("param subsetC:= \"" + generatedFilesPath + File.separator + username + "_subsetC.csv\" ;\n");
            bufferedWriter.write("param coverageNodes:= \"" + generatedFilesPath + File.separator + username + "_coverageNodes.csv\" ;\n\n");

            bufferedWriter.write("end;\n");

            bufferedWriter.close();
            fileWriter.close();

            return "succeeded";

        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            return ex.toString();
        }
    }

    private String genCoverageR(String username, String[][] matrix) {

        File coverageR = new File(generatedFilesPath + File.separator + username + "_coverageR.csv");

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

    private String genSubsetC(String username, String noOfSensors) {

        File subsetC = new File(generatedFilesPath + File.separator + username + "_subsetC.csv");

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

    private String callGLPK(String username) {

        Runtime runtime = Runtime.getRuntime();
        Process process;

        try {
            System.out.println("com.monitor.webservicetestopenshift.Test.callGLPK()  ::   " + "\"" + userDir + File.separator + "gusek" + File.separator + "glpsol\" -m \"" + userDir + File.separator + "gusek" + File.separator + "target.mod\" -d \"" + generatedFilesPath + File.separator + username + "_data.dat\"");
            process = runtime.exec("\"" + userDir + File.separator + "gusek" + File.separator + "glpsol\" -m \"" + userDir + File.separator + "gusek" + File.separator + "target.mod\" -d \"" + generatedFilesPath + File.separator + username + "_data.dat\"");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

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

    private String sendNotification() {

        return "";
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
