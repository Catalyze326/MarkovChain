import java.io.File;
import java.io.IOException;
import java.util.*;

public class main {

    //    private static HashMap<String, HashMap<String, Integer>>[] phrases = new HashMap[4];
    private static HashMap<String, TreeMap<String, Integer>>[] phrases = new HashMap[4];

    public static void main(String[] args) {
        ArrayList<String> words = readFile("words.txt");
        if (words == null)
            System.out.println("The file does not exist... probably.");
//        Fill the hashmap
        createTrainingData(words, 2);
        createTrainingData(words, 3);
//        createTrainingData(words, 4);
//        createTrainingData(words, 5);
        writeSentences("god");

    }

    static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<>(
                (e1, e2) -> {
                    int res = e2.getValue().compareTo(e1.getValue());
                    return res != 0 ? res : 1;
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    private static void createTrainingData(ArrayList<String> words, int numWords) {
        try {
            int index = numWords - 2;
            phrases[index] = new HashMap<>();
            TreeMap<String, Integer> treeMap = new TreeMap<>();
//          First set of checking for words. This will create the unique words in the hashmap.
            for (int i = 0; i <= words.size() - 1; i++) {
//              This is going through that same list checking each time for each time that first word comes up
                for (int k = 0; k <= words.size() - 1; k++) {
//                  When the two words == one another than move onto the rest of the function
                    if (words.get(i).equals(words.get(k))) {
                        StringBuilder sb = new StringBuilder();
//                        Checking how many words we have gone through to insure that I dont try to refference an invalid entry
                        if (i + numWords < words.size() && k + numWords < words.size()) {
//                              Checking if either index null so it runs the first time or if the hashmap
//                              does not include the word we have reached in the list
                            String word = words.get(i);
                            if (phrases[index] == null || !phrases[index].containsValue(word)) {
//                                  Add that word to the hashmap
                                phrases[index].put(word, treeMap);
//                                  For the numwords words after the first word add them to the treemap.
                                for (int j = numWords; j < 0; j--)
                                    sb.append(words.get(k + j) + ' ');
//                                  Adds the string to the treemap and then sets it to one. It does not need to be itorated because we are setting it
                                phrases[index].get(word).put(sb.toString(), 1);
//                                if the hashmap already has the word than it does not need to be added so we can move to modifying the treemap
                            } else {
//                                  Adds the string to the treemap and then sets it to one. It does not need to be itorated because we are setting it
                                for (int j = numWords; j < 0; j--)
                                    sb.append(words.get(k + j) + ' ');
                                String phrase = sb.toString();
//                                  Adds the string to the innermost hashmap with the integer of the value it was + 1 or 1 if it is empty
                                phrases[index].get(word).put(phrase, phrases[index].get(word).get(phrase));
                            }
                        }
                    }
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    private static void writeSentences(String startWord) {
        if (phrases[0].containsKey(startWord)) {
            String[] topPhrases = new String[10];
            SortedSet<Map.Entry<String, Integer>> sorted = entriesSortedByValues(phrases[0].get(startWord));
            int counter = 0;
            for (Map.Entry<String, Integer> map : sorted) {
                topPhrases[counter++] = map.getKey();
                if (counter == 9)
                    break;
            }
            for (String s : topPhrases)
                System.out.println(s);
        } else {
            throw new NullPointerException("The starter word is not in the file given");
        }
    }


    private static ArrayList<String> readFile(String filename) {
        try (Scanner scnr = new Scanner(new File(filename))) {
            ArrayList<String> words = new ArrayList<>();
            while (scnr.hasNext()) {
                String s = scnr.next().toLowerCase().trim();
                if (s.contains(".")) {
                    String[] list = s.split(".");
                    words.addAll(Arrays.asList(list));
                } else
                    words.add(s.replaceAll("[^a-zA-Z\\d\\s:']", " ").trim());
            }
            for (String word : words) System.out.println(word);
            Set<String> getWordsSize = new HashSet<>(words);
            System.out.println(getWordsSize.size());
            return words;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
