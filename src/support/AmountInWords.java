package support;

import java.util.*;

public class AmountInWords
{
    private static String amount; private static int num;
    private static String[] units={""," One"," Two"," Three"," Four"," Five"," Six"," Seven"," Eight"," Nine"};
    private static String[] teen={" Ten"," Eleven"," Twelve"," Thirteen"," Fourteen"," Fifteen"," Sixteen"," Seventeen"," Eighteen"," Nineteen"};
    private static String[] tens={" Twenty"," Thirty"," Fourty"," Fifty"," Sixty"," Seventy"," Eighty"," Ninety"};
    private static String[] maxs={"",""," Hundred"," Thousand"," Lakh"," Crore"};
    public AmountInWords()
    {
        amount="";
    }
    public String convertToWords(int n)
    {
        amount=numToString(n); String converted=""; int pos=1; boolean hun=false;
        while(amount.length()>0)
        {
            if(pos==1) // TENS AND UNIT POSITION
            {   if(amount.length()>=2) // 2DIGIT NUMBERS
                {   String C=amount.substring(amount.length()-2,amount.length()); amount=amount.substring(0,amount.length()-2);   converted+=digits(C);    }
                else if(amount.length()==1) // 1 DIGIT NUMBER
                {   converted+=digits(amount); amount="";   }    pos++; // INCREASING POSITION COUNTER
            }
            else if(pos==2) // HUNDRED POSITION
            {   String C=amount.substring(amount.length()-1,amount.length()); amount=amount.substring(0,amount.length()-1);
                if(converted.length()>0&&digits(C)!=""){   converted=(digits(C)+maxs[pos]+" and")+converted;hun=true;  }   else{   if(digits(C)=="")  ; else  converted=(digits(C)+maxs[pos])+converted;hun=true;}   pos++; // INCREASING POSITION COUNTER
            }
            else if(pos>2) // REMAINING NUMBERS PAIRED BY TWO
            {
                if(amount.length()>=2) // EXTRACT 2 DIGITS
                {   String C=amount.substring(amount.length()-2,amount.length()); amount=amount.substring(0,amount.length()-2);
                    if(!hun&&converted.length()>0)converted=digits(C)+maxs[pos]+" and"+converted;    else{ if(digits(C)=="")  ; else converted=digits(C)+maxs[pos]+converted; }   }
                else if(amount.length()==1) // EXTRACT 1 DIGIT
                {   if(!hun&&converted.length()>0)converted=digits(amount)+maxs[pos]+" and"+converted;    else{ if(digits(amount)=="")  ; else converted=digits(amount)+maxs[pos]+converted;   amount="";  } }    pos++; // INCREASING POSITION COUNTER
            }
        }
        if(converted.trim().equalsIgnoreCase("")){
            return "";
        }else{
            return "Rupees " + converted.trim() +" Only";
        }
    }
    private String digits(String C) // TO RETURN SELECTED NUMBERS IN WORDS
    {
        String converted="";
        for(int i=C.length()-1;i>=0;i--)
        {   int ch=C.charAt(i)-48;
            if(i==0&&ch>1&&C.length()>1)    converted=tens[ch-2]+converted; // IF TENS DIGIT STARTS WITH 2 OR MORE IT FALLS UNDER TENS
            else if(i==0&&ch==1&&C.length()==2) // IF TENS DIGIT STARTS WITH 1 IT FALLS UNDER TEENS
            {   int sum=0;      for(int j=0;j<2;j++)    sum=(sum*10)+(C.charAt(j)-48);      return teen[sum-10];    }
            else{   if(ch>0)converted=units[ch]+converted;  } // IF SINGLE DIGIT PROVIDED    
        }   return converted;
    }
    private String numToString(int n) // CONVERT THE NUMBER TO STRING
    {
        String num="";  while(n!=0) {   num=((char)((n%10)+48))+num;  n/=10;  }      return num;
    }
    private void input()
    {
        Scanner in=new Scanner(System.in);
        try{System.out.print("Enter Amount to Convert in Words : ");
        num=in.nextInt();}catch(Exception e){System.out.println("Number Less than 1 Arab(1000000000) Only Possible.");System.exit(1);}
    }
    public static void main(String[] args)
    {
        AmountInWords obj=new AmountInWords();
        obj.input();
        System.out.println("Amount in Words : "+obj.convertToWords(num));
    }
}