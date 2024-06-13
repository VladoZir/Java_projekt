package hr.java.waterUsageJavaFxApplication.threads;

import hr.java.waterUsageJavaFxApplication.HelloApplication;
import hr.java.waterUsageJavaFxApplication.model.Changes;
import javafx.application.Platform;

import java.util.Optional;

public class GetTheLatestChangeThread extends DatabaseThread implements Runnable{

    @Override
    public void run() {

        while (true){
            Optional<Changes> latestChangeOptional = super.getTheLatestChange();

            if (latestChangeOptional.isPresent()){
                Changes latestChange = latestChangeOptional.get();

                Platform.runLater(()->{
                    HelloApplication.getMainStage().setTitle(
                            "Posljednja promjena-> " + latestChange.getChangeDescription()
                    );
                });

            }

            try {
                Thread.sleep(10000);
            }catch (InterruptedException e){
                throw new RuntimeException(e);
            }

        }

    }

}
