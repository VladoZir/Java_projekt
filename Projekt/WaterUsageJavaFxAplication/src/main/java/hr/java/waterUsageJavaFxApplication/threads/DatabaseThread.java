package hr.java.waterUsageJavaFxApplication.threads;

import hr.java.waterUsageJavaFxApplication.model.Changes;
import hr.java.waterUsageJavaFxApplication.utils.DatabaseUtils;
import hr.java.waterUsageJavaFxApplication.utils.FileUtils;

import java.util.List;
import java.util.Optional;

public class DatabaseThread extends Thread{
    public static boolean isDatabaseOperationInProgress = false;

    public synchronized Optional<Changes> getTheLatestChange(){
        while(isDatabaseOperationInProgress){
            try {
                wait();
            }catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        }

        isDatabaseOperationInProgress = true;
        Optional<Changes> latestChangeOptional = DatabaseUtils.getTheLatestChange();
        isDatabaseOperationInProgress = false;
        notifyAll();

        return latestChangeOptional;

    }

    public synchronized void serializeChanges(List<Changes> changesList){
        while(isDatabaseOperationInProgress){
            try {
                wait();
            }catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        }

        isDatabaseOperationInProgress = true;
        FileUtils.serializeChanges(changesList);
        isDatabaseOperationInProgress = false;
        notifyAll();

    }

}
