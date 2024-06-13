package hr.java.waterUsageJavaFxApplication.threads;

import hr.java.waterUsageJavaFxApplication.model.Changes;

import java.util.List;

public class SerializeChangesThread extends DatabaseThread implements Runnable{
    private List<Changes> changesList;

    public SerializeChangesThread(List<Changes> changesList) {
        this.changesList = changesList;
    }

    @Override
    public void run() {
        super.serializeChanges(changesList);
    }
}
