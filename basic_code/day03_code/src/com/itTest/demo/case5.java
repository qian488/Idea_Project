package com.itTest.demo;

public class case5 {
    public static void main(String[] args) {
        //数组拷贝的案例
        //把一个整型数组拷贝成一个一模一样的新数组
        int[] arr = {11,22,33};
        int[] arr2 = copy(arr);
        printArray(arr2);

        //注意,前者是将数组变量赋值给另一个数组变量，但是指向的是同一个地址，而拷贝数组是创建了一个新的位置，互不干扰
//        int[] arr3 = arr;
//        arr3[1] = 666;
//        System.out.println(arr[1]);
//
//        arr2[1] = 666;
//        System.out.println(arr[1]);
    }

    public static void printArray(int[] arr) {
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(i == arr.length - 1 ? arr[i] : arr[i] + ",");
        }
        System.out.print("]");
    }

    public static int[] copy(int[] arr)
    {
        //1.创建一个长度一样的整型数组
        int[] arr2 = new int[arr.length];
        //2.把原数组对应值付给新数组
        for (int i = 0; i <arr.length ; i++) {
            arr2[i] = arr[i];
        }
        return arr2;
    }
}
