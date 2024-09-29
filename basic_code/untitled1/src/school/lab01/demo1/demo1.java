package school.lab01.demo1;

import java.util.Scanner;

public class demo1 {
    public static  boolean IsLeapYear(int x){
        if(x % 4 == 0 && x % 100 != 0 || x % 400 == 0)  return true;
        return false;
    }

    public static void Solve() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("输入年份:");
        int year = scanner.nextInt();
        if(IsLeapYear(year)) {
            System.out.println(year + "是闰年");
        } else {
            System.out.println(year + "是平年");
        }
    }
}
