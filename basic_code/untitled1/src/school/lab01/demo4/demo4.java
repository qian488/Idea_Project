package school.lab01.demo4;

import java.util.Scanner;

public class demo4 {
    public void Solve() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("输入两个正整数:");
        int n = scanner.nextInt();
        int m = scanner.nextInt();

        int Gcd = gcd(n,m);
        int Lcm = lcm(n,m,Gcd);

        System.out.println("最大公约数是: " + Gcd);
        System.out.println("最小公倍数是: " + Lcm);
    }

    private int gcd(int a,int b){
        while(b != 0){
            int temp = a % b;
            a = b;
            b = temp;
        }
        return a;
    }

    private int lcm(int a,int b,int gcd){
        return (a*b)/gcd;
    }
}
