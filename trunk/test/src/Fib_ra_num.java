

//计算斐波那契数列(Fibonacci)的第n个值并打印
public class Fib_ra_num{
    public static void main(String args[]){
        int n = 10;
        Fib_ra_num t = new Fib_ra_num();
        for(int i=1;i<=n;i++){
            t.print(i);
        }
    }
    public void print(int n){
        int n1 = 1;//第一个数
        int n2 = 1;//第二个数
        int sum = 0;//和
        if(n<=0){
            System.out.println("参数错误!");
            return;
        }
        if(n<=2){
            sum = 1;           
        }else{
            for(int i=3;i<=n;i++){
                sum = n1+n2;
                n1 = n2;
                n2 = sum;
            }
        }
        System.out.println(sum);
    }
}