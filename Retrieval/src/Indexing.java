import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Indexing{
  //Map<Term, Map<SceneId, Position>>
  Map<String, HashMap<String, ArrayList<Integer>>> termMap = new HashMap<String, HashMap<String, ArrayList<Integer>>>();
  //Map<Term, PlayId>
  Map<String, HashSet<String>> playMap = new HashMap<String, HashSet<String>>();
  //Map<SceneId, SceneLength>
  Map<String, Integer> lengthMap = new HashMap<String, Integer>();

  double averageLength = 0.0;
  int scene_num = 0;
  int C = 0;

  public void readFile(){
    JSONParser parser = new JSONParser();

    try{
        Object obj = parser.parse(new FileReader("shakespeare-scenes.json"));
        JSONObject jsonObject = (JSONObject) obj;

        JSONArray object = (JSONArray) jsonObject.get("corpus");

        Iterator<JSONObject> iterator = object.iterator();
        int shortest_scene_count = Integer.MAX_VALUE;
        String shortest_scene = "";
        int play_count = 0;
        String play = "";
        int longest_play_count = 0;
        String longest_play = "";
        int shortest_play_count = Integer.MAX_VALUE;
        String shortest_play = "";
        List thee_thou = new ArrayList<Integer>();
        List you = new ArrayList<Integer>();
        while (iterator.hasNext()) {
          int thee_thou_count = 0;
          int you_count = 0;
          scene_num++;
          JSONObject newObject= (JSONObject)iterator.next();
          //get playId
          String playId = (String) newObject.get("playId");
          //get sceneId
          String sceneId = (String) newObject.get("sceneId");
          //get text and split it by space
          String text = (String) newObject.get("text");
          String[] splited = text.split("\\s+");
          lengthMap.put(sceneId, splited.length);

          if(play.equals(playId)&&iterator.hasNext()){
            play_count += splited.length;
          }
          else{
            if(play_count > longest_play_count){
              longest_play_count = play_count;
              longest_play = play;
            }
            if(play_count!=0&&play_count < shortest_play_count){
              shortest_play_count = play_count;
              shortest_play = play;
            }
            play = playId;
            play_count = splited.length;
          }

          if(splited.length < shortest_scene_count){
            shortest_scene_count = splited.length;
            shortest_scene = sceneId;
          }
          for(int i = 0; i < splited.length; i++){
            String term = splited[i];
            if(term.equals("thee")||term.equals("thou")){
              thee_thou_count++;
            }
            else if(term.equals("you")){
              you_count++;
            }

            //save data to termMap
            if(!termMap.containsKey(term)){
              HashMap<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();
              ArrayList<Integer> list = new ArrayList<Integer>();
              list.add(i+1);
              map.put(sceneId, list);
              termMap.put(term, map);
            }
            else{
              HashMap<String, ArrayList<Integer>> map = termMap.get(term);
              if(!map.containsKey(sceneId)){
                ArrayList<Integer> list = new ArrayList<Integer>();
                list.add(i+1);
                map.put(sceneId, list);
                termMap.put(term, map);
              }
              else{
                ArrayList<Integer> list = map.get(sceneId);
                list.add(i+1);
                map.put(sceneId, list);
                termMap.put(term, map);
              }
            }

            if(!playMap.containsKey(term)){
              HashSet<String> set = new HashSet<String>();
              set.add(playId);
              playMap.put(term, set);
            }
            else{
              HashSet<String> set = playMap.get(term);
              set.add(playId);
              playMap.put(term, set);
            }
          }
          thee_thou.add(thee_thou_count);
          you.add(you_count);
          // this.output(thee_thou, "thee_thou");
          // this.output(you, "you");
        }
        //System.out.println("Scene count: " + scene_num);

        int term_count = 0;
        for(String key: termMap.keySet()){
          for(String k: termMap.get(key).keySet()){
            term_count += termMap.get(key).get(k).size();
          }
        }
        // System.out.println("Terms count: " + term_count);
        // System.out.println("Average length: " + term_count/scene_num);
        averageLength = term_count/scene_num;
        C = term_count;
        // System.out.println("Shortest scene: " + shortest_scene + " count: "  + shortest_scene_count);
        // System.out.println("Shortest Play: " + shortest_play + " count: " + shortest_play_count);
        // System.out.println("Longest Play: " + longest_play + " count: " + longest_play_count);
      } catch (FileNotFoundException e) {
       e.printStackTrace();
      } catch (IOException e) {
       e.printStackTrace();
      } catch (ParseException e) {
       e.printStackTrace();
      }

  }

  //Find scene(s) where "thee" or "thou" is used more than "you"
  public List compareScene(String a1, String a2, String b){
    HashMap<String, ArrayList<Integer>> mapA1 = termMap.get(a1);
    HashMap<String, ArrayList<Integer>> mapA2 = termMap.get(a2);
    HashMap<String, ArrayList<Integer>> mapB = termMap.get(b);
    Set<String> idSet = new HashSet<String>();
    for(String key: mapA1.keySet()){
      int size = 0;
      if(mapA2.containsKey(key)){
        size = mapA2.get(key).size();
      }
      if(mapB.containsKey(key)?mapA1.get(key).size()+size>mapB.get(key).size():true){
        idSet.add(key);
      }
    }
    for(String key: mapA2.keySet()){
      int size = 0;
      if(mapA1.containsKey(key)){
        size = mapA1.get(key).size();
      }
      if(mapB.containsKey(key)?mapA2.get(key).size()+size>mapB.get(key).size():true){
        idSet.add(key);
      }
    }
    List sortList = new ArrayList(idSet);
    Collections.sort(sortList);

    return sortList;
  }

  public List scenMentioned(String s){
    if(!termMap.containsKey(s)){
      List list = new ArrayList();
      return list;
    }
    else{
      HashMap<String, ArrayList<Integer>> map = termMap.get(s);
      HashSet<String> set = new HashSet();
      for(String key: map.keySet()){
        set.add(key);
      }
      List sortList = new ArrayList(set);
      Collections.sort(sortList);
      return sortList;
    }
  }

  public List playMentioned(String s){
    if(!playMap.containsKey(s)){
      List list = new ArrayList();
      return list;
    }
    else{
      HashSet set = playMap.get(s);
      List sortList = new ArrayList(set);
      Collections.sort(sortList);
      return sortList;
    }
  }

  public List phraseMentioned(String s){
    return this.phraseHelper(s, termMap.get(s.split("\\s+")[0]));
  }

  public List phraseHelper(String s, HashMap<String, ArrayList<Integer>> map){
    if(s.split("\\s+").length==1){
      List res = new ArrayList<String>();
      for(String key: map.keySet()){
        res.add(key);
      }
      Collections.sort(res);
      return res;
    }
    else{
      String term = s.split("\\s+")[1];
      HashMap<String, ArrayList<Integer>> nextMap = termMap.get(term);
      HashMap<String, ArrayList<Integer>> newMap = new HashMap<String, ArrayList<Integer>>();
      for(String key: map.keySet()){
        for(int i: map.get(key)){
          if(nextMap.containsKey(key)?nextMap.get(key).contains(i+1):false){
            if(!newMap.containsKey(key)){
              ArrayList<Integer> newList = new ArrayList<Integer>();
              newList.add(i+1);
              newMap.put(key, newList);
            }
            else{
              ArrayList<Integer> list = newMap.get(key);
              list.add(i+1);
              newMap.put(key, list);
            }
          }
        }
      }
      return phraseHelper(s.substring(s.indexOf(' ')+1, s.length()), newMap);
    }
  }

  public void output(List l, String s){
    try {
      PrintWriter out=new PrintWriter(s);
      for(int i = 0; i < l.size(); i++){
        out.write(l.get(i)+"\n");
      }
      out.flush();
      out.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
  }

  public void bm25(String q1, String q2, PrintWriter p){
    double k1 = 1.2;
    double k2 = 100.0;
    double b = 0.75;
    double N = scene_num;
    String[] a = q1.split(" ");
    Map<String, Integer> map = new HashMap<String, Integer>();
    for(String aa: a){
      if(map.containsKey(aa)){
        map.put(aa, map.get(aa)+1);
      }
      else{
        map.put(aa, 1);
      }
    }
    Queue<Node> queue = new PriorityQueue<Node>((int)N, new Comparator<Node>(){
      public int compare(Node n1, Node n2){
        if(n1.score>n2.score)return -1;
        else return 1;
      }
    });
    for(String key: lengthMap.keySet()){
      double score = 0.0;
      double K = k1*((1-b)+b*(lengthMap.get(key)/averageLength));
      for(String i: map.keySet()){
        double n = termMap.get(i).size();
        double f = termMap.get(i).get(key)==null ? 0 : termMap.get(i).get(key).size();
        score+=Math.log((N-n+0.5)/(n+0.5))*(k1+1)*f/(K+f)*(k2+1)*map.get(i)/(k2+map.get(i));
      }
      if(score!=0){
        Node node = new Node(score, key);
        queue.offer(node);
      }
    }
    int i = 1;
    while(queue.peek()!=null){
      Node n = queue.remove();
      StringBuilder sb = new StringBuilder();
      sb.append(q2);
      sb.append(" ");
      sb.append("skip");
      sb.append(" ");
      sb.append(n.scene);
      for(int j=0;j<40-n.scene.length();j++){
        sb.append(" ");
      }
      sb.append(i);
      sb.append(" ");
      sb.append(n.score);
      sb.append(" ");
      sb.append("yifanzhu-bm25\n");
      String res = sb.toString();
      p.write(res);
      //p.write(q2+" "+"skip"+" "+n.scene+"                    "+i+" "+n.score+" "+"yifanzhu-bm25\n");
      i++;
    }
  }

  public void ql(String q1, String q2, PrintWriter p){
    double mu = 1500.0;
    double N = scene_num;
    String[] a = q1.split(" ");
    Queue<Node> queue = new PriorityQueue<Node>((int)N, new Comparator<Node>(){
      public int compare(Node n1, Node n2){
        if(n1.score>n2.score)return -1;
        else return 1;
      }
    });
    for(String key: lengthMap.keySet()){

      System.out.println(key);
      double score = 0.0;
      double d = lengthMap.get(key);
      for(String i: a){

        double c = 0.0;
        for(String k: termMap.get(i).keySet()){
          c += termMap.get(i).get(k).size();
        }
        System.out.println(c);
        double f = termMap.get(i).get(key)==null ? 0 : termMap.get(i).get(key).size();
        System.out.println(f);
        System.out.println(C);
        System.out.println(d);
        score+=Math.log((f+mu*(c/C))/(d+mu));
        System.out.println(score);
        break;
      }

      if(score!=0){
        Node node = new Node(score, key);
        queue.offer(node);
      }
      break;
    }
    int i = 1;
    while(queue.peek()!=null){
      Node n = queue.remove();
      StringBuilder sb = new StringBuilder();
      sb.append(q2);
      sb.append(" ");
      sb.append("skip");
      sb.append(" ");
      sb.append(n.scene);
      for(int j=0;j<40-n.scene.length();j++){
        sb.append(" ");
      }
      sb.append(i);
      sb.append(" ");
      sb.append(n.score);
      sb.append(" ");
      sb.append("yifanzhu-ql\n");
      String res = sb.toString();
      p.write(res);
      i++;
    }
  }

  public static void main(String[] args) throws IOException, ParseException{
    Indexing indexing = new Indexing();
    indexing.readFile();
    String Q1 = "the king queen royalty";
    String Q2 = "servant guard soldier";
    String Q3 = "hope dream sleep";
    String Q4 = "ghost spirit";
    String Q5 = "fool jester player";
    String Q6 = "to be or not to be";
    PrintWriter writer = new PrintWriter("../bm25.trecrun");
    indexing.bm25(Q1,"Q1", writer);
    indexing.bm25(Q2,"Q2", writer);
    indexing.bm25(Q3,"Q3", writer);
    indexing.bm25(Q4,"Q4", writer);
    indexing.bm25(Q5,"Q5", writer);
    indexing.bm25(Q6,"Q6", writer);
    writer.close();
    PrintWriter write = new PrintWriter("../ql.trecrun");
    //indexing.ql(Q1,"Q1", write);
    //indexing.ql(Q2,"Q2", write);
    indexing.ql(Q3,"Q3", write);
    //indexing.ql(Q4,"Q4", write);
    //indexing.ql(Q5,"Q5", write);
    //indexing.ql(Q6,"Q6", write);
    write.close();
    //print terms0.txt
    // Long startTime=System.nanoTime();
    // List terms0 = indexing.compareScene("thee", "thou", "you");
    // Long endTime=System.nanoTime();
    // System.out.println("term0 Running time： "+(endTime-startTime)+"ns");
    // indexing.output(terms0, "../terms0.txt");

    //print terms1.txt
    // startTime=System.nanoTime();
    // List terms1 = indexing.scenMentioned("verona");
    // List terms1_1 = indexing.scenMentioned("rome");
    // List terms1_2 = indexing.scenMentioned("italy");
    // terms1.addAll(terms1_1);
    // terms1.addAll(terms1_2);
    // HashSet set = new HashSet(terms1);
    // terms1.clear();
    // terms1.addAll(set);
    // Collections.sort(terms1);
    // endTime=System.nanoTime();
    // System.out.println("term1 Running time： "+(endTime-startTime)+"ns");
    // indexing.output(terms1, "../terms1.txt");

    //print terms2.txt
    // startTime=System.nanoTime();
    // List terms2 = indexing.playMentioned("falstaff");
    // endTime=System.nanoTime();
    // System.out.println("term2 Running time： "+(endTime-startTime)+"ns");
    // indexing.output(terms2, "../terms2.txt");

    //print term3.txt
    // startTime=System.nanoTime();
    // List terms3 = indexing.playMentioned("soldier");
    // endTime=System.nanoTime();
    // System.out.println("term3 Running time： "+(endTime-startTime)+"ns");
    // indexing.output(terms3, "../terms3.txt");

    //print phrase0.txt
    // startTime=System.nanoTime();
    // List phrase0 = indexing.phraseMentioned("lady macbeth");
    // endTime=System.nanoTime();
    // System.out.println("phrase0 Running time： "+(endTime-startTime)+"ns");
    // indexing.output(phrase0, "../phrase0.txt");

    //print phrase1.txt
    // startTime=System.nanoTime();
    // List phrase1 = indexing.phraseMentioned("a rose by any other name");
    // endTime=System.nanoTime();
    // System.out.println("phrase1 Running time： "+(endTime-startTime)+"ns");
    // indexing.output(phrase1, "../phrase1.txt");

    //print phrase2.txt
    // startTime=System.nanoTime();
    // List phrase2 = indexing.phraseMentioned("cry havoc");
    // endTime=System.nanoTime();
    // System.out.println("phrase2 Running time： "+(endTime-startTime)+"ns");
    // indexing.output(phrase2, "../phrase2.txt");

  }
}

class Node {
   double score ;
   String scene;
   Node(double x, String y) { score = x; scene=y;}
}
