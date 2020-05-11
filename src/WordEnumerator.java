import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.Collator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordEnumerator {

    private final int WORDS_PER_LINE = 100;

    private List<String> lines;
    private Map<String, Set<Integer>> words;

    public static void main(String[] args) {
        String filePath = "zadanie.txt";
        WordEnumerator wordEnumerator = new WordEnumerator();
        try {
            wordEnumerator.enumerateFromFile(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void enumerateFromFile(String filePath) throws FileNotFoundException {
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(filePath));
        lines = reader
                .lines()
                .collect(Collectors.toList());
        enumerate();
    }

    private void enumerate() {
        words = new HashMap<String, Set<Integer>>(lines.size() * WORDS_PER_LINE);

        Iterator<String> linesIterator = lines.iterator();
        String line;
        int lineNumber = 0;
        String word;
        Set<Integer> wordLines;
        Pattern pattern = Pattern.compile("(\\d+,\\d+)|([A-Ża-ż\\d]+)|(%)");
        Matcher matcher;

        while (linesIterator.hasNext()) {
            lineNumber++;
            line = linesIterator.next();
            matcher = pattern.matcher(line);
            while (matcher.find()) {
                word = matcher.group();
                if (words.containsKey(word)) {
                    words.get(word).add(lineNumber);
                } else {
                    wordLines = new HashSet<Integer>(lines.size());
                    wordLines.add(lineNumber);
                    words.put(word, wordLines);
                }
            }
        }

        words.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Set<Integer>>comparingByKey(new IgnoreCaseComparator()))
                .forEach(WordEnumerator::println);
    }

    private static void println(Map.Entry<String, Set<Integer>> entry) {
        String word = entry.getKey();
        Set<Integer> wordLinesSet = entry.getValue();
        Iterator<Integer> wordLinesIterator = wordLinesSet.iterator();
        StringBuilder wordLines = new StringBuilder();

        while(wordLinesIterator.hasNext()) {
            wordLines.append(wordLinesIterator.next());
            if (wordLinesIterator.hasNext())
                wordLines.append(", ");
        }

        System.out.println(word + " - " + wordLinesSet.size() + " - pozycje -> [" + wordLines + "]");
    }

    private final class IgnoreCaseComparator implements Comparator<String> {

        Collator collator = Collator.getInstance((new Locale("pl", "PL")));

        @Override
        public int compare(String string1, String string2) {
            return collator.compare(string1.toLowerCase(), string2.toLowerCase());
        }
    }
}
