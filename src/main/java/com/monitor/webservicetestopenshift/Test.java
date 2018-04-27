/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.monitor.webservicetestopenshift;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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

    private String putCSV(String username, String[][] data) {

        String userDir = System.getProperty("user.dir");

        System.out.println("com.mycompany.webservicetest.Test.putCSV() " + userDir);

        String generatedFilesPath = userDir + File.pathSeparator + "generatedFiles";
        File generatedFilesDirectory = new File(generatedFilesPath);

        if (!generatedFilesDirectory.exists()) {
            return "userDir " + userDir + " ,(new File(userDir)).canWrite() " + (new File(userDir)).canWrite() + " ,generatedFilesPath " + generatedFilesPath + " ,generatedFilesDirectory.canWrite() " + generatedFilesDirectory.canWrite() + " ,generatedFilesDirectory.mkdirs() " + generatedFilesDirectory.mkdirs();
        }

        File coverageR = new File(generatedFilesPath + "\\" + username + "_coverageR.csv");
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
