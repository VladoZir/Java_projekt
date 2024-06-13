package hr.java.waterUsage.utils;

import hr.java.waterUsage.main.Main;
import hr.java.waterUsage.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String TENANTS_TEXT_FILE = "dat/tenants.txt";
    private static final String HOUSEHOLDS_TEXT_FILE = "dat/households.txt";
    private static final String TENANTS_SERIALIZATION_FILE = "dat/tenantsSerialization.dat";
    private static final String HOUSEHOLDS_SERIALIZATION_FILE = "dat/householdsSerialization.dat";
    private static final String BILLS_SERIALIZATION_FILE = "dat/billsSerialization.dat";

    public static List<Tenant> getTenantsFromFile() {
        List<Tenant> tenantList = new ArrayList<>();

        File tenantsFile = new File(TENANTS_TEXT_FILE);

        try(BufferedReader reader = new BufferedReader(new FileReader(tenantsFile))){

            Optional<String> tenantIdOptional = Optional.empty();

            while((tenantIdOptional = Optional.ofNullable(reader.readLine())).isPresent()){

                String tenantIdString = tenantIdOptional.get();
                Long tenantId = Long.parseLong(tenantIdString);

                String tenantName = reader.readLine();

                String showerDuartionString = reader.readLine();
                BigDecimal showerDuration = new BigDecimal(showerDuartionString);
                Shower shower = new Shower(showerDuration);

                String dishWashingDurationString = reader.readLine();
                BigDecimal dishWashingDuration = new BigDecimal(dishWashingDurationString);
                Dishwasher dishwasher = new Dishwasher(dishWashingDuration);

                String washingMachineDurationString = reader.readLine();
                BigDecimal washingMachineDuration = new BigDecimal(washingMachineDurationString);
                WashingMachine washingMachine = new WashingMachine(washingMachineDuration);

                String carWashingDurationString = reader.readLine();
                BigDecimal carWashingDuration = new BigDecimal(carWashingDurationString);
                CarWash carWash = new CarWash(carWashingDuration);

                Tenant tenant = new Tenant(tenantId, tenantName, shower, dishwasher, washingMachine, carWash);

                tenantList.add(tenant);

            }

        }catch (IOException e){
            String message = "Greška prilikom dohvaćanja podataka o stanarima iz datoteke";
            logger.error(message, e);
            System.out.println(message);
        }


        return tenantList;
    }

    public static List<Household> getHouseholdsFromFile(List<Tenant> tenantList){
        List<Household> householdList = new ArrayList<>();

        File householdsFile = new File(HOUSEHOLDS_TEXT_FILE);

        try (BufferedReader reader = new BufferedReader(new FileReader(householdsFile))){

            Optional<String> householdIdOptional = Optional.empty();

            while((householdIdOptional = Optional.ofNullable(reader.readLine())).isPresent()){

                String householdIdString = householdIdOptional.get();
                Long householdId = Long.parseLong(householdIdString);

                String householdAddress = reader.readLine();

                String tenantIdsString = reader.readLine();
                List<Long> tenantIds = Arrays.stream(tenantIdsString.split(","))
                        .map(Long::parseLong)
                        .toList();

                List<Tenant> householdTenantList = new ArrayList<>();
                BigDecimal totalHouseholdWaterUsage = BigDecimal.valueOf(0);
                for (int i=0;i<tenantIds.size();i++){
                    for (int j=0;j<tenantList.size();j++){
                        if (tenantIds.get(i).equals(tenantList.get(j).getId())){
                            totalHouseholdWaterUsage = totalHouseholdWaterUsage.add(tenantList.get(j).getTotalWaterUsed());
                            householdTenantList.add(tenantList.get(j));
                        }
                    }
                }

                Optional<Leak> leakOptional = Optional.empty();
                String leakString = reader.readLine();
                if (leakString.equals("imaCurenje")){
                    leakOptional = Optional.of(new Leak(true));
                } else {
                    leakOptional = Optional.of(new Leak(false));
                }


                Household household = new Household.Builder(householdId)
                        .atAddress(householdAddress)
                        .tenantList(householdTenantList)
                        .totalHouseholdWaterUsage(totalHouseholdWaterUsage)
                        .leak(leakOptional.get())
                        .build();


                householdList.add(household);

            }

        }catch (IOException e){
            String message = "Greška prilikom dohvaćanja podataka o kućanstvima iz datoteke";
            logger.error(message, e);
            System.out.println(message);
        }

        return householdList;
    }

    public static Set<Bill<BigDecimal>> getBillsFromFile(List<Household> householdList){

        Set<Bill<BigDecimal>> billSet = new HashSet<>();

        Long idCounter = 1L;
        for (int i=0;i<householdList.size();i++){
            BigDecimal totalBill = householdList.get(i).calculateTotalBill();
            BigDecimal totalWaterUsed = householdList.get(i).getTotalHouseholdWaterUsage();
            Bill<BigDecimal> bill = new Bill<BigDecimal>(idCounter, householdList.get(i).getAddress(), totalBill, totalWaterUsed);
            billSet.add(bill);
            idCounter++;
        }

        return billSet;

    }

    public static void serializeTenants(List<Tenant> tenantList){
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(TENANTS_SERIALIZATION_FILE))){
            outputStream.writeObject(tenantList);
        } catch (IOException e){
            String message = "Greška prilikom serijalizacije podataka o stanarima";
            logger.error(message, e);
            System.out.println(message);
        }
    }

    public static List<Tenant> deserializeTenants(){
        List<Tenant> tenantList = new ArrayList<>();

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(TENANTS_SERIALIZATION_FILE))){
            tenantList = (List<Tenant>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e){
            String message = "Greška prilikom deserijalizacije podataka o stanarima";
            logger.error(message, e);
            System.out.println(message);
        }

        return tenantList;
    }

    public static void serializeHouseholds(List<Household> householdList){
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(HOUSEHOLDS_SERIALIZATION_FILE))){
            outputStream.writeObject(householdList);
        } catch (IOException e){
            String message = "Greška prilikom serijalizacije podataka o kućanstvima";
            logger.error(message, e);
            System.out.println(message);
        }
    }

    public static List<Household> deserializeHouseholds(){
        List<Household> householdList = new ArrayList<>();

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(HOUSEHOLDS_SERIALIZATION_FILE))){
            householdList = (List<Household>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e){
            String message = "Greška prilikom deserijalizacije podataka o kućanstvima";
            logger.error(message, e);
            System.out.println(message);
        }

        return householdList;
    }

    public static void serializeBills(Set<Bill<BigDecimal>> billSet){
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(BILLS_SERIALIZATION_FILE))){
            outputStream.writeObject(billSet);
        } catch (IOException e){
            String message = "Greška prilikom serijalizacije podataka o računima";
            logger.error(message, e);
            System.out.println(message);
        }
    }

    public static Set<Bill<BigDecimal>> deserializeBills(){
        Set<Bill<BigDecimal>> billSet = new HashSet<>();

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(BILLS_SERIALIZATION_FILE))){
            billSet = (Set<Bill<BigDecimal>>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e){
            String message = "Greška prilikom deserijalizacije podataka o računima";
            logger.error(message, e);
            System.out.println(message);
        }

        return billSet;
    }

}
