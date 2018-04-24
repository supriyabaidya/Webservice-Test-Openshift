/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.monitor.webservicetestopenshift;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

/**
 *
 * @author TomHardy
 */
//@SOAPBinding(style = Style.RPC)
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
    public String twoDimesionArray(@WebParam(name = "array") String[][] array) {
        int noOfRow = array.length;
        int noOfColomn;

        for (int i = 0; i < noOfRow; i++) {

            noOfColomn = array[i].length;
            for (int j = 0; j < noOfColomn; j++) {
                System.out.println(i + " , " + j + " => " + array[i][j]);
            }
        }
        return "success";
    }
}
