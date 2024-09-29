package school.lab01.demo5;

import java.util.Scanner;

public class demo5 {
    public static void Solve(){
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入一行字符: ");
        String s = scanner.nextLine();

        int LetterCount = 0;
        int SpaceCount = 0;
        int DigitCount = 0;
        int OtherCount = 0;

        for (char c : s.toCharArray()) {
            if (Character.isLetter(c))  LetterCount++;
            else if (Character.isDigit(c))  DigitCount++;
            else if (Character.isWhitespace(c)) SpaceCount++;
            else  OtherCount++;
        }

        System.out.println("英文字母的个数: " + LetterCount);
        System.out.println("空格的个数: " + SpaceCount);
        System.out.println("数字的个数: " + DigitCount);
        System.out.println("其它字符的个数: " + OtherCount);
    }
}
