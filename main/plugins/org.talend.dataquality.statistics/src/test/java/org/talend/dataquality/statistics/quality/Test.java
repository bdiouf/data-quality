package org.talend.dataquality.statistics.quality;

import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class Test {
    public static void main(String[] args) {
        
        String testDate = "Sun,30/8/2015";
//        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//        try {
//            Date date = formatter.parse(testDate);
//            System.out.println(date);
//        } catch (ParseException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        new BigInteger("2");
        System.out.println(1*100.00/(10*100));
    
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE,d/M/yyyy",Locale.US);
        System.out.println(formatter.format(formatter.parse(testDate)));
    }

}
