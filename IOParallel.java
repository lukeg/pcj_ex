import org.pcj.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@RegisterStorage(IOParallel.Shared.class)
public class IOParallel implements StartPoint {

    @Storage(IOParallel.class)
    enum Shared {
        line;
    }
    String line;

    public static void main(String[] args) {

        PCJ.start(IOParallel.class, new NodesDescription(Collections.nCopies(4, "localhost").toArray(new String[0])));
    }

    @Override
    public void main() throws Throwable {
        if (PCJ.myId() == 0) {
            List<String> lines = Files.readAllLines(Paths.get("in.txt"));
            for (int i = 0; i < lines.size(); i++) {
                PCJ.put(lines.get(i), i, Shared.line);
            }
        }
            PCJ.waitFor(Shared.line);
            System.out.format("Value %s is held by thread %d\n", line, PCJ.myId());

    }



}