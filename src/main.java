import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

public class main {

    private static HashMap<String, TreeMap<String, Integer>>[] phrases = new HashMap[4];
    private static final String FILENAME = "Your File Of Words";

    public static void main(String[] args) {
        ArrayList<String> words = readFile(FILENAME);
        if (words == null)
            System.out.println("The file does not exist... probably.");
        createTrainingData(words, 2);
        Scanner scnr = new Scanner(System.in);
        System.out.println("What word would you like to make sentences for next or would you like it to be random?");
        writeSentences(scnr.next().trim().toLowerCase(), words);

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
        if (numWords < 2 || numWords > phrases.length + 1)
            throw new IllegalArgumentException("numWords is out of bounds");
        try {
            int index = numWords - 2;
            phrases[index] = new HashMap<>();

            for (int i = numWords; i < words.size() - numWords; i++) {
                StringBuilder sb = new StringBuilder();
//                for (int w = 0; w < numWords; w++)
                int w = 0;
                while (sb.toString().trim().split(" ").length < numWords)
                    sb.append(words.get(i + w++ + 1)).append(" ");
                String word = words.get(i);
                String phrase = sb.toString().trim();
                phrases[index].putIfAbsent(word, new TreeMap<>());
                phrases[index].get(word).putIfAbsent(phrase, 0);
                phrases[index].get(word).put(phrase, phrases[index].get(word).get(phrase) + 1);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    private static void writeSentences(String orgStartWord, ArrayList<String> words) {
        for (int i = 0; i <= 10; i++) {
            StringBuilder sb = new StringBuilder();
            String startWord;
            if (orgStartWord.equals("random"))
                startWord = words.get(new SecureRandom().nextInt(words.size()));
            else
                startWord = orgStartWord;
            sb.append(startWord + " ");
            while (!sb.toString().contains(".") && !endLine(sb.toString())) {
                if (phrases[0].containsKey(startWord)) {
                    String[] topPhrases = new String[10];
                    SortedSet<Map.Entry<String, Integer>> sorted = entriesSortedByValues(phrases[0].get(startWord));
                    int counter = 0;
                    for (Map.Entry<String, Integer> map : sorted) {
                        topPhrases[counter++] = map.getKey();
                        if (counter == 9)
                            break;
                    }
                    SecureRandom sr = new SecureRandom();
                    int index = sr.nextInt(phrases[0].get(startWord).size() < 10 ? phrases[0].get(startWord).size() : 9);
                    startWord = topPhrases[index].split(" ")[1];
                    sb.append(topPhrases[index] + " ");
                } else {
                    throw new NullPointerException("The starter word '" + startWord + "' is not in the file given");
                }
            }
            if(sb.toString().split(" ").length > 3)
                System.out.println(sb.toString().split("[.?!]")[0] + ".");
        }
    }


    private static ArrayList<String> readFile(String filename) {
        try (Scanner scnr = new Scanner(new File(filename))) {
            ArrayList<String> words = new ArrayList<>();
            while (scnr.hasNext()) {
                String s = scnr.next().toLowerCase().trim();
                words.add(s.replaceAll("[^a-zA-Z.!?']", " ").trim());
            }
//            for (String word : words) System.out.println(word);
            Set<String> getWordsSize = new HashSet<>(words);
            System.out.println(getWordsSize.size());
            return words;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean endLine(String sentence) {
        return sentence.contains(".") || sentence.contains("!") || sentence.contains("?");
    }
}
