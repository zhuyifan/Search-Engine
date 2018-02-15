import java.util.*;
import java.io.*;

public class frequency{
  public static void main(String[] args){
    File f = new File("tokenization_outputB.txt");
    Scanner scan = null;
    try {
        scan = new Scanner(f);
    } catch (Exception e) {
    }

    //create a map contains every word and its frequency
    Map<String, Integer> map = new HashMap<>();
    Queue<Node> queue = new PriorityQueue<>(200, new Comparator<Node>(){
      public int compare(Node n1, Node n2){
        if(n1.freq>n2.freq)return -1;
        else return 1;
      }
    });
    while (scan.hasNext()) {
        String s = scan.next();
        if(map.containsKey(s)){
          map.put(s, map.get(s)+1);
        }
        else map.put(s, 0);
    }

    //create node which contains frequency and word. Then offer them to a PriorityQueue
    for (String key : map.keySet()) {
      Node node = new Node(map.get(key), key);
      queue.offer(node);
    }
    
    //print frequency
    try {
      PrintWriter out=new PrintWriter("terms.txt");
      for(int i=0;i<200;i++){
        Node n = queue.remove();
        out.write(n.word+", "+n.freq+"\n");
      }
      out.flush();
      out.close();
    } catch (Exception e) {
        e.printStackTrace();
      }
  }
}

class Node {
   int freq;
   String word;
   Node(int x, String y) { freq = x; word=y;}
}
