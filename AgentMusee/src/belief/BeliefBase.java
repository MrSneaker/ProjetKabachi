package belief;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeliefBase {
    private List<String> groupTourists;
    private Map<Integer, String> exhibition;

    public BeliefBase() {
        groupTourists = new ArrayList<>();
        exhibition = new HashMap<>();
    }

    public void addTourist(String tourist) {
        groupTourists.add(tourist);
    }

    public String getExhibitionInfo(int tableauID) {
        return exhibition.get(tableauID);
    }
}
