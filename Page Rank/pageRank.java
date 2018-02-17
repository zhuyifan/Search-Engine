import java.io.*;
import java.util.*;
import java.lang.*;

public class pageRank{
  public static void main(String[] args){

    //read file
    File f = new File("links.srt");
    Scanner scan = null;
    try {
        scan = new Scanner(f);
    } catch (Exception e) {
    }

    ArrayList<String> list = new ArrayList<String>();
    while(scan.hasNext()) {
      StringBuffer sb = new StringBuffer();
      sb.append(scan.next()).append(" ").append(scan.next());
      list.add(sb.toString());
    }

    int N = list.size();

    Map<String, Integer> Lv = new HashMap<String, Integer>();
    Map<String, ArrayList<String>> inLinks = new HashMap<String, ArrayList<String>>();
    Map<String, Double> pageRank = new HashMap<String, Double>();
    int count = 1;

    for(int i = 1; i < N; i++){
      //split source page and target page
      String[] str0 = list.get(i-1).split(" ");
      String[] str1 = list.get(i).split(" ");

      pageRank.put(str0[0],0.0);
      pageRank.put(str0[1],0.0);

      if(i == N - 1){
        pageRank.put(str1[0],0.0);
        pageRank.put(str1[1],0.0);
      }

      //count the number of out links in each source page
      if(!str0[0].equals(str1[0])){
        Lv.put(str0[0], count);
        count = 1;
      }
      else count++;

      //count inlinks
      if(inLinks.containsKey(str0[1])){
        ArrayList<String> newlist = inLinks.get(str0[1]);
        newlist.add(str0[0]);
        inLinks.put(str0[1], newlist);
      }
      else{
        ArrayList<String> newlist = new ArrayList<String>();
        newlist.add(str0[0]);
        inLinks.put(str0[1], newlist);
      }

      //add the last line
      if(i == N-1){
        if(inLinks.containsKey(str1[1])){
          ArrayList<String> newlist = inLinks.get(str1[1]);
          newlist.add(str1[0]);
          inLinks.put(str1[1], newlist);
        }
        else{
          ArrayList<String> newlist = new ArrayList<String>();
          newlist.add(str1[0]);
          inLinks.put(str1[1], newlist);
        }
      }
    }

    int size = pageRank.size();
    for(String key: pageRank.keySet()){
      pageRank.put(key, 1.0/size);
    }

    Queue<Node> queue = new PriorityQueue<>(50, new Comparator<Node>(){
      public int compare(Node n1, Node n2){
        if(n1.count > n2.count)return -1;
        else return 1;
      }
    });

    for(String key: inLinks.keySet()) {
      Node node = new Node(inLinks.get(key).size(), key);
      queue.offer(node);
    }

    //print frequency
    try {
      PrintWriter out=new PrintWriter("inlinks.txt");
      for(int i=0;i<50;i++){
        Node n = queue.remove();
        out.write(n.link+" "+(i+1)+" "+n.count+"\n");
      }
      out.flush();
      out.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

    //compute page rank
    double tau = 1;
    while(tau>0.02){
      System.out.println(1);
      tau = 0;
      for(String key: pageRank.keySet()){
        ArrayList<String> l = inLinks.get(key);
        if(l == null){
          double newRank = 0.2/pageRank.size();
          pageRank.put(key, newRank);
        }
        else{
          double sum = 0;
          for(String s: l){
            if(Lv.get(s)!=null){
              sum+=pageRank.get(s)/Lv.get(s);
            }
          }
          double newRank = 0.2/pageRank.size()+0.8*sum;
          //System.out.println(newRank);
          tau+=Math.abs(pageRank.get(key)-newRank);
          pageRank.put(key,newRank);
        }
      }
    }

    Queue<Node2> queue2 = new PriorityQueue<>(50, new Comparator<Node2>(){
      public int compare(Node2 n1, Node2 n2){
        if(n1.count > n2.count)return -1;
        else return 1;
      }
    });

    for(String key: pageRank.keySet()) {
      Node2 node = new Node2(pageRank.get(key), key);
      queue2.offer(node);
    }

    //print frequency
    try {
      PrintWriter out=new PrintWriter("pagerank.txt");
      for(int i=0;i<50;i++){
        Node2 n = queue2.remove();
        out.write(n.link+" "+(i+1)+" "+n.count+"\n");
      }
      out.flush();
      out.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
  }
}

class Node{
  int count;
  String link;
  Node(int x, String y){count = x; link = y;}
}

class Node2{
  double count;
  String link;
  Node2(double x, String y){count = x; link = y;}
}
