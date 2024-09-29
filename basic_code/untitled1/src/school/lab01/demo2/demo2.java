package school.lab01.demo2;

public class demo2 {
    public static void Solve(){
        long ans = 0;

        for(int i = 1;i<= 10;i++){
            long Sum = 1;
            for(int j = 1;j <= i;j++) Sum *= j;
            ans += Sum;
        }

        System.out.println(ans);
    }
}
