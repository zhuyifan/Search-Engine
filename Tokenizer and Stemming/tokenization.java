import java.io.*;
import java.util.*;

public class tokenization{
  public static void main(String[] args){

    //Implement tokenization

    //read file
    File f = new File("tokenization-input-part-A.txt");
    Scanner scan = null;
    try {
        scan = new Scanner(f);
    } catch (Exception e) {
    }
    StringBuffer sb = new StringBuffer();
    while (scan.hasNext()) {
        sb.append(scan.next()).append(" ");
    }

    //lowercase all letters
    String input = sb.toString().toLowerCase();

    //seperate words by punctuation except period
    input=input.replaceAll("[^a-z0-9\\.]", " ");

    int start = 0;
    StringBuffer s = new StringBuffer();
    for(int i=0;i<input.length();i++){
      if(input.charAt(i)==' '){
        String word = input.substring(start, i);
        
        //test Abbreviation
        if(word.length()>2&&word.charAt(1)=='.'){
          StringBuffer w = new StringBuffer();
          for(int j=0;j<word.length();j+=2){
            w.append(word.charAt(j));
          }
          word=w.toString();
        }
        s.append(word).append(" ");
        while(input.charAt(i)==' '&&i<input.length()-1){
            i++;
        }
        start = i;
      }
    }
    String res = s.toString();

    //seperate words by period
    res = res.replaceAll("[\\.]"," ");

    //delete blank rows
    res = res.replaceAll(" +", " ");

    //Implement stopword removal

    //transfer tokenization result to an ArrayList

    start = 0;
    List<String> list = new ArrayList<String>();
    for(int i=0;i<res.length();i++){
      if(res.charAt(i)==' '){
        list.add(res.substring(start,i));
        while(res.charAt(i)==' '&&i<res.length()-1){
            i++;
        }
        start = i;
      }
    }

    //put stopwords in a set
    Set stopword = new HashSet();
    File f1 = new File("stopwords.txt");
    Scanner scan1 = null;
    try {
        scan1 = new Scanner(f1);
    } catch (Exception e) {
    }
    while (scan1.hasNext()) {
        stopword.add(scan1.next());
    }

    //delete stopwords
    for(int i=0;i<list.size();i++){
      if(stopword.contains(list.get(i))){
        list.set(i, "");
      }
    }


    //Implement stemming
    //step 1a
    for(int i=0;i<list.size();i++){
      String st=list.get(i);
      //Replace sses by ss
      st = st.replaceAll("sses", "ss");

      //Replace ied or ies by i if preceded by more than one letter, otherwise by ie
      int len=st.length();
      if(len>3&&(st.substring(len-3,len).equals("ied")||st.substring(len-3,len).equals("ies"))){
        if(len==4){
          st=st.substring(0, len-1);
        }
        else{
          st=st.substring(0,len-2);
        }
      }

      //Delete s if the preceding word part contains a vowel not immediately before the s
      List<Character> l = Arrays.asList('a', 'e', 'i', 'o', 'u');
      if(st.length()>=3){
        for(int j=0;j<st.length()-2;j++){
          if(l.contains(st.charAt(j))&&st.charAt(st.length()-1)=='s'&&st.charAt(st.length()-2)!='s'){
            st=st.substring(0, st.length()-1);
            break;
          }
        }
      }

      list.set(i, st);
    }

    //step 1b
    for(int i=0;i<list.size();i++){
      String st=list.get(i);
      //Replace eed, eedly by ee if it is in the part of the word a er the  rst nonvowel following a vowel
      List<Character> l = Arrays.asList('a', 'e', 'i', 'o', 'u');
      int len=st.length();
      int flag=0;//mark the first non vowel
      for(int j=0;j<len-1;j++){
        if(!l.contains(st.charAt(j)))flag=1;
        if(flag==0&&!l.contains(st.charAt(j+1))&&l.contains(st.charAt(j))){
          if(len>3&&st.substring(len-3,len).equals("eed")){
            st=st.substring(0,len-1);
            break;
          }
          if(len>5&&st.substring(len-5,len).equals("eedly")){
            st=st.substring(0,len-3);
            break;
          }
        }
      }

      //Delete ed, edly, ing, ingly
      if(st.length()==len){
        if(len>2&&st.substring(len-2,len).equals("ed")){
          for(int j=0;j<len-2;j++){
            if(l.contains(st.charAt(j))){
              st=st.substring(0,len-2);
              break;
            }
          }
        }
        else if(len>3&&st.substring(len-3,len).equals("ing")){
          for(int j=0;j<len-3;j++){
            if(l.contains(st.charAt(j))){
              st=st.substring(0,len-3);
              break;
            }
          }
        }
        else if(len>4&&st.substring(len-4,len).equals("edly")){
          for(int j=0;j<len-4;j++){
            if(l.contains(st.charAt(j))){
              st=st.substring(0,len-4);
              break;
            }
          }
        }
        else if(len>5&&st.substring(len-5,len).equals("ingly")){
          for(int j=0;j<len-5;j++){
            if(l.contains(st.charAt(j))){
              st=st.substring(0,len-5);
              break;
            }
          }
        }
        // if the word ends in at, bl, or iz add e
        if(len!=st.length()){
          len=st.length();
          if(st.substring(len-2,len).equals("at")||st.substring(len-2,len).equals("bl")||st.substring(len-2,len).equals("iz")){
            StringBuffer sbf = new StringBuffer(st);
            sbf.append("e");
            st=sbf.toString();
          }
          //if the word ends with a double letter that is not ll, ss, or zz, remove the last letter
          else if(st.charAt(len-1)==(st.charAt(len-2))&&st.charAt(len-1)!='l'&&st.charAt(len-1)!='s'&&st.charAt(len-1)!='z'){
            st=st.substring(0, len-1);
          }
          //if the word is short, add e
          else if(len<=3){
            StringBuffer sbff = new StringBuffer(st);
            sbff.append("e");
            st=sbff.toString();
          }
        }
      }
      list.set(i,st);
    }

    //transfer arraylist to String
    StringBuffer sb1 = new StringBuffer();
    for(int i=0;i<list.size();i++){
      if(list.get(i)!=""){
        sb1.append(list.get(i)).append("\n");
      }
    }
    input=sb1.toString();

    try {
      PrintWriter out=new PrintWriter("tokenized.txt");
      out.write(input);
      out.flush();
      out.close();
    } catch (Exception e) {
        e.printStackTrace();
      }
  }
}
