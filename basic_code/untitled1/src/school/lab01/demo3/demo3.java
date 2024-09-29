package school.lab01.demo3;

import java.util.Scanner;

public class demo3 {
    public static void Solve(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入月份: ");
        int n = scanner.nextInt();

        int ans = f(n);

        System.out.println("第" + n + "个月的兔子总数为: " + ans + "对");
    }

    private static int f(int n) {
        if (n == 1 || n == 2)   return 1;

        int a = 1,b = 1,sum = 0;
        for (int i = 3; i <= n; i++) {
            sum = a + b;
            b = a;
            a = sum;
        }
        return sum;
    }
}
