package com.example.smsmessaging;

import java.math.BigInteger;

public class Convert {
    public static BigInteger StringToInt(String string){
        char c;
        int d;
        StringBuilder s = new StringBuilder();

        for(int i=0;i<string.length();i++){
            d=(int)string.charAt(i);
            if((d<100)){
                s.append("0");
                s.append((int)string.charAt(i));
            }
            else{s.append((int)string.charAt(i));}



        }
        BigInteger b = new BigInteger(s.toString());
        return b;
    }
    public static String IntToString(BigInteger b){
        String s = b.toString();
        char d;
        StringBuilder sb = new StringBuilder();
        System.out.println(s);
        for(int i=0;i<s.length();i=i+3){
            d = (char)Integer.parseInt(s.substring(i,i+3));
            sb.append(Character.toString(d));
        }
        System.out.println(sb.toString());
        return sb.toString();

    }
    public static String comp(BigInteger b){
        //System.out.println(b.toString());

        if(b.equals(BigInteger.valueOf(0))){return "";}
        return(comp(b.divide(BigInteger.valueOf(127)))+(Character.toString((char)(b.mod(BigInteger.valueOf(127))).intValue())));
    }
    public static BigInteger decomp(String string){
        BigInteger n=BigInteger.valueOf(0);
        BigInteger r = BigInteger.valueOf(127);
        for(int i=0;i<string.length();i++){

            n=n.add(BigInteger.valueOf((int)string.charAt(string.length()-i-1)).multiply(r.pow(i)));
        }
        return n;
    }
    /*
    def comp(number):
            if number == 0:
            return ''

            return comp(number//127) + str(chr(number%127))

                                def decomp(string):
    number = 0
            for i in range(0,len(string)):
    number+=ord(string[len(string)-1-i])*(127**i)
            return number




     */
    public static void main(String[] args) {
        System.out.println("hello");
        System.out.println(StringToInt("heddsdafdalskfa"));
        IntToString(StringToInt("sdlkafjalskdf"));
        System.out.println(comp(BigInteger.valueOf(8239874)));
        System.out.println(decomp("\u0004\u0002nr"));

    }


}

