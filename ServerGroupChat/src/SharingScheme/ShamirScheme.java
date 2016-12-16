/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SharingScheme;

import FileReader.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author yoga
 */
public class ShamirScheme {

    static long p = 1234567890133l;
    static public ArrayList<Long> equation = new ArrayList();

    //untuk membentuk array share 
    static public ArrayList<Long> Share(int t) throws IOException {
        ArrayList<Long> ret = new ArrayList();

        
        for(int i=0 ; i<t ;i++){
            Long a = randomval(); 
            if (a < 0){
                a=a+p;
            }
            if (i>0)
                System.out.print("S = ");
            else 
                System.out.print("M = ");
            System.out.println(a);
            
            equation.add(a);
        }
        byte[] content= equation.get(0).toString().getBytes();
        FileReader.savefile("secret.txt", content);

        for (int i = 1; i <= t; i++) {
            Long y = new Long(0);
            for (int j = 0; j < equation.size(); j++) {
                y = y + ((equation.get(j) * pow(i, j)));
                y=y % p ;
            }
            ret.add(y);
        }
        return ret;
    }

    static public boolean CheckShare(ArrayList<BigInteger> x, ArrayList<BigInteger> y) throws IOException {

        BigInteger pb = new BigInteger(String.valueOf(p));
        BigInteger ret = new BigInteger("0");
        for (int i = 0; i < x.size(); i++) {
            BigInteger atas = new BigInteger("1");
            BigInteger bawah = new BigInteger("1");
            for (int j = 0; j < x.size(); j++) {
                if (i != j) {
                    atas=atas.multiply(new BigInteger("-" + x.get(j).toString()));
                    bawah=bawah.multiply(new BigInteger(x.get(i).subtract(x.get(j)).toString()));
                }
            }
            BigInteger temp = atas.multiply(bawah.modInverse(pb)).multiply(y.get(i)).mod(pb);
            ret= ret.add(temp);
        }
        ret = ret.mod(pb);
        System.out.println("ini m dapetnya" + ret);
        String mess= FileReader.FileToString("secret.txt");
        BigInteger check = new BigInteger(mess);
        System.out.println("ini m awal" + check);
        return ret.compareTo(check) == 0;
    }

    static public long pow(long a, long b) {
        long ret = 1;
        for (int i = 0; i < b; i++) {
            ret = ret * a;
        }
        return ret;
    }

    //untuk random value dalam mod p
    static public Long randomval() {
        Random rand = new Random();
        Long val = rand.nextLong() % p;
        return val;
    }
}
