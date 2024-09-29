package com.itTest.d9_modifer;

public class F {
    private void PrivatePrint(){
        System.out.println("Private Print");
    }

    void Print(){
        System.out.println("Print");
    }

    protected void ProtectedPrint(){
        System.out.println("Protected Print");
    }

    public void PublicPrint(){
        System.out.println("Public Print");
    }

    public void Test(){
        Print();
        ProtectedPrint();
        ProtectedPrint();
        PublicPrint();
    }

}
