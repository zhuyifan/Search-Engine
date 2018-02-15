import java.util.*;
import java.io.*;

public class plot{
  public static void main(String[] args){
    File f = new File("tokenization_outputB.txt");
    Scanner scan = null;
    try {
        scan = new Scanner(f);
    } catch (Exception e) {
    }

    try {
      PrintWriter out=new PrintWriter("plot2");
      //create a map contains every word and its frequency
      Map<String, Integer> map = new HashMap<>();
      int countAll = 0;
      int count = 0;
      while (scan.hasNext()) {
          countAll++;
          String s = scan.next();
          if(map.containsKey(s)){
            map.put(s, map.get(s)+1);
          }
          else map.put(s, 0);
          count = map.size();
          out.write(count+"\n");
      }

      out.flush();
      out.close();


  } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
