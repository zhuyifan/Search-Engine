import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Indexing{
  //Map<Term, Posting>>
  //Map<String, ArrayList<Posting>> termPosting = new HashMap<String, ArrayList<Posting>>();
  //Map<Term, Map<SceneId, Position>>
  Map<String, HashMap<String, ArrayList<Integer>>> termMap = new HashMap<String, HashMap<String, ArrayList<Integer>>>();
  //Map<Term, PlayId>
  Map<String, HashSet<String>> playMap = new HashMap<String, HashSet<String>>();

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
        int scene_num = 0;
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
            //save data to termPosting
            // if(!termPosting.containsKey(term)){
            //   ArrayList<Posting> postings = new ArrayList<Posting>();
            //   Posting p = new Posting(sceneId, i+1);
            //   postings.add(p);
            //   termPosting.put(term, postings);
            // }
            // else{
            //   ArrayList postings = termPosting.get(term);
            //   Posting p = new Posting(sceneId, i+1);
            //   postings.add(p);
            //   termPosting.put(term, postings);
            // }

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
          this.output(thee_thou, "thee_thou");
          this.output(you, "you");
        }
        System.out.println("Scene count: " + scene_num);

        int term_count = 0;
        for(String key: termMap.keySet()){
          for(String k: termMap.get(key).keySet()){
            term_count += termMap.get(key).get(k).size();
          }
        }
        System.out.println("Terms count: " + term_count);
        System.out.println("Average length: " + term_count/scene_num);
        System.out.println("Shortest scene: " + shortest_scene + " count: "  + shortest_scene_count);
        System.out.println("Shortest Play: " + shortest_play + " count: " + shortest_play_count);
        System.out.println("Longest Play: " + longest_play + " count: " + longest_play_count);
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

  public static void main(String[] args) throws IOException, ParseException{
    Indexing indexing = new Indexing();
    indexing.readFile();

    //print terms0.txt
    Long startTime=System.nanoTime();
    List terms0 = indexing.compareScene("thee", "thou", "you");
    Long endTime=System.nanoTime();
    System.out.println("term0 Running time： "+(endTime-startTime)+"ns");
    indexing.output(terms0, "../terms0.txt");

    //print terms1.txt
    startTime=System.nanoTime();
    List terms1 = indexing.scenMentioned("verona");
    List terms1_1 = indexing.scenMentioned("rome");
    List terms1_2 = indexing.scenMentioned("italy");
    terms1.addAll(terms1_1);
    terms1.addAll(terms1_2);
    HashSet set = new HashSet(terms1);
    terms1.clear();
    terms1.addAll(set);
    Collections.sort(terms1);
    endTime=System.nanoTime();
    System.out.println("term1 Running time： "+(endTime-startTime)+"ns");
    indexing.output(terms1, "../terms1.txt");

    //print terms2.txt
    startTime=System.nanoTime();
    List terms2 = indexing.playMentioned("falstaff");
    endTime=System.nanoTime();
    System.out.println("term2 Running time： "+(endTime-startTime)+"ns");
    indexing.output(terms2, "../terms2.txt");

    //print term3.txt
    startTime=System.nanoTime();
    List terms3 = indexing.playMentioned("soldier");
    endTime=System.nanoTime();
    System.out.println("term3 Running time： "+(endTime-startTime)+"ns");
    indexing.output(terms3, "../terms3.txt");

    //print phrase0.txt
    startTime=System.nanoTime();
    List phrase0 = indexing.phraseMentioned("lady macbeth");
    endTime=System.nanoTime();
    System.out.println("phrase0 Running time： "+(endTime-startTime)+"ns");
    indexing.output(phrase0, "../phrase0.txt");

    //print phrase1.txt
    startTime=System.nanoTime();
    List phrase1 = indexing.phraseMentioned("a rose by any other name");
    endTime=System.nanoTime();
    System.out.println("phrase1 Running time： "+(endTime-startTime)+"ns");
    indexing.output(phrase1, "../phrase1.txt");

    //print phrase2.txt
    startTime=System.nanoTime();
    List phrase2 = indexing.phraseMentioned("cry havoc");
    endTime=System.nanoTime();
    System.out.println("phrase2 Running time： "+(endTime-startTime)+"ns");
    indexing.output(phrase2, "../phrase2.txt");

  }
}

// class Posting{
//   String docId;
//   int position;
//   Posting(String x, int y){docId= x; position = y;}
// }
