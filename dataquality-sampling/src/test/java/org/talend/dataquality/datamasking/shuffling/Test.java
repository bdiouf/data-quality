package org.talend.dataquality.datamasking.shuffling;

public class Test {

    public static void main(String[] args) throws ClassNotFoundException {
        Integer x = 10;
        Class cls = Class.forName("Integer");
        System.out.println(cls.isInstance(x));
    }

}
