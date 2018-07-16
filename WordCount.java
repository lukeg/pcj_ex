import org.pcj.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

@RegisterStorage(WordCount.Shared.class)
public class WordCount implements StartPoint {

    @Storage(WordCount.class)
    enum Shared {
        localCounts
    }
    HashMap<String, Integer> localCounts = new HashMap<>();

    public static void main (String[] args) {
        PCJ.deploy(WordCount.class, new NodesDescription(Collections.nCopies(4, "localhost").toArray(new String[0])));
    }
    String myFileName;
    private void readConfiguration() throws IOException {
        List<String> configurationFileLines = Files.readAllLines(Paths.get("wordcount.config"));
        myFileName = configurationFileLines.get(PCJ.myId());

    }

    @Override
    public void main() throws Throwable {
        readConfiguration();
        PCJ.barrier();
        countWords();
        PCJ.barrier();
        if (PCJ.myId() == 0) {
            Map<String, Integer> results = serialReduction();
            results.forEach( (k, v) -> System.out.println(k + ": " + v));
        }
    }

    private Map<String, Integer> serialReduction() {
        Map<String, Integer> resultMap = new HashMap<>();
        for (int i = 0; i < PCJ.threadCount(); i++) {
            Map<String, Integer> remoteMap = PCJ.get(i, Shared.localCounts);
            remoteMap.forEach((k, v) -> resultMap.merge(k, v, Integer::sum));
        }
        return resultMap;
    }

    private void countWords() throws IOException {
        final Pattern WORD_BOUNDARY = Pattern.compile("\\s*\\b\\s*");
        Files.readAllLines(Paths.get(myFileName))
                .stream()
                .map(WORD_BOUNDARY::split)
                .flatMap(Arrays::stream)
                .filter(s -> !s.isEmpty())
                .forEach( word -> localCounts.merge(word, 1, Integer::sum));

    }
}
