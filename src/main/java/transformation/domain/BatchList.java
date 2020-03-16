package transformation.domain;

import java.util.List;

public class BatchList {

    private List<Batch> batches;

    public BatchList(List<Batch> batches) {
        this.batches = batches;
    }

    public List<Batch> getBatches() {
        return batches;
    }

    public void setBatches(List<Batch> batches) {
        this.batches = batches;
    }
}
