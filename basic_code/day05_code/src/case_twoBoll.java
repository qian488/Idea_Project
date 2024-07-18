import com.itTest.demo.case5;
import com.itTest.demo.case6;
import com.itTest.demo.case7;

public class case_twoBoll {
    public static void main(String[] args) {
        //双色球案例
        //1.投注一组号码，并返回用户的号码
        int[] userNumbers = case5.userSelectNumber();
        System.out.println("您投注的号码为：");
        case5.printArray(userNumbers);
        //2.随机生成一组中将号码，返回中奖号码
        int[] luckNumbers = case6.createLuckNumbers();
        System.out.println("中奖的号码为：");
        case5.printArray(luckNumbers);
        //判断中奖情况,传入两组号码，判断中奖情况，并输出
        case7.judge(userNumbers,luckNumbers);
    }
}
