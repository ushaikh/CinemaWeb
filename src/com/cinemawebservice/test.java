package com.cinemawebservice;

import java.util.*;

public class test
{  

    public static void generate(List<List<String>> outerList, String outPut) {
        List<String> list = outerList.get(0);

        for(String str : list) {
            List<List<String>> newOuter = new ArrayList<List<String>>(outerList);
            newOuter.remove(list);

            if(outerList.size() > 1) {
                generate(newOuter, outPut+str);
             } else {
               System.out.println(outPut+str);
             }
        }
    }

    
    
    public static void main(String[] args) 
    {
        List<List<String>> outerList = new ArrayList<List<String>>();

        List<String> list1 = new ArrayList<String>();
        List<String> list2 = new ArrayList<String>();

        List<String> list3 = new ArrayList<String>();
        List<String> list4 = new ArrayList<String>();
        list1.add("A");
        list1.add("B");

        list2.add("C");
        list2.add("D");
        
        list3.add("E");
        list3.add("F");

        list4.add("G");
        list4.add("H");

        outerList.add(list1);
        outerList.add(list2);
        outerList.add(list3);
        outerList.add(list4);

        test.generate(outerList, "");
    }      
}