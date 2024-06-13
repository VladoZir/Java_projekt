package hr.java.waterUsage.main;

import hr.java.waterUsage.exceptions.DuplicateHouseholdException;
import hr.java.waterUsage.exceptions.DuplicateTenantException;
import hr.java.waterUsage.exceptions.LeakException;
import hr.java.waterUsage.exceptions.NumberTooLowException;
import hr.java.waterUsage.utils.FileUtils;
import hr.java.waterUsage.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        Scanner inputScanner = new Scanner(System.in);

        //unos broja stanara
        boolean error;
        /*
        Integer numOfTenants = -1;
        do {
            System.out.println("Unesi broj stanara > ");
            error = false;
            try {
                numOfTenants = inputScanner.nextInt();
                checkInputNumber(BigDecimal.valueOf(numOfTenants));
            } catch (InputMismatchException e) {
                logger.error("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                System.out.println("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                error = true;
            }catch (NumberTooLowException e){
                logger.error(e.getMessage());
                System.out.println(e.getMessage());
                error = true;
            }
            inputScanner.nextLine();
        }while (error);
         */

        //unos podataka o stanarima
        List<Tenant> tenantList = FileUtils.getTenantsFromFile();
        /*
        List<Tenant> tenantList = new ArrayList<>();
        Long tenantIdCounter = 1L;
        for (int i=0;i<numOfTenants;i++){
            enterTenant(i, inputScanner, tenantList, tenantIdCounter);
            tenantIdCounter++;
        }

         */

        //unos broja kućanstava
        /*
        Integer numOfHouseholds = -1;
        do {
            System.out.println("Unesi broj kućanstava > ");
            error = false;
            try {
                numOfHouseholds = inputScanner.nextInt();
                checkInputNumber(BigDecimal.valueOf(numOfHouseholds));
            } catch (InputMismatchException e) {
                logger.error("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                System.out.println("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                error = true;
            }catch (NumberTooLowException e){
                logger.error(e.getMessage());
                System.out.println(e.getMessage());
                error = true;
            }
            inputScanner.nextLine();
        }while (error);
         */

        //unos podataka o kućanstvima
        List<Household> householdList = FileUtils.getHouseholdsFromFile(tenantList);
        /*
        List<Household> householdList = new ArrayList<>();
        List<Tenant> tenantListToRemoveFrom = new ArrayList<>(tenantList);
        Long householdIdCounter = 1L;
        for (int i=0;i<numOfHouseholds;i++){
            enterHouseholds(i, inputScanner, householdList, tenantListToRemoveFrom, householdIdCounter);
            householdIdCounter++;
        }

         */

        //izračun računa potrošnje vode po kućanstvu
        Set<Bill<BigDecimal>> billSet = FileUtils.getBillsFromFile(householdList);
        /*
        Set<Bill<BigDecimal>> billSet = new HashSet<>();
        Long billIdCounter = 1L;
        for (int i=0;i<householdList.size();i++){
            BigDecimal totalBill = householdList.get(i).calculateTotalBill();
            Bill<BigDecimal> bill = new Bill<BigDecimal>(billIdCounter,
            householdList.get(i).getAddress(), totalBill, householdList.get(i).getTotalHouseholdWaterUsage());
            billSet.add(bill);
            billIdCounter++;
        }

         */

        //ispis detalja o kućanstvima
        boolean notFinished = true;
        do {

            Integer chosenHouseholdIndex = -1;
            do {
                error = false;
                System.out.println("Odaberi kućanstvo za koje želiš vidjeti detalje >");
                for (int i = 0; i < householdList.size(); i++) {
                    System.out.println(i + 1 + " - " + householdList.get(i).getAddress());
                }
                System.out.println("0 - Spremi!");
                try {
                    chosenHouseholdIndex = inputScanner.nextInt();
                    inputScanner.nextLine();
                } catch (InputMismatchException e) {
                    logger.error("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                    System.out.println("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                    error = true;
                }
                if (chosenHouseholdIndex < 0 || chosenHouseholdIndex > householdList.size()) {
                    System.out.println("Greška pri odabiru! Unesi broj između 0 i " + householdList.size());
                }
            } while (error || chosenHouseholdIndex < 0 || chosenHouseholdIndex > householdList.size());

            if (chosenHouseholdIndex==0){
                notFinished = false;
            }else {
                System.out.println("Odabrao si kućanstvo na adresi " + householdList.get(chosenHouseholdIndex - 1).getAddress());
                System.out.println("Stanara u kućanstvu: ");
                if (householdList.get(chosenHouseholdIndex - 1).getTenantList().isEmpty()) {
                    System.out.println("Nema stanara!");
                }
                for (int i = 0; i < householdList.get(chosenHouseholdIndex - 1).getTenantList().size(); i++) {
                    System.out.println(householdList.get(chosenHouseholdIndex - 1).getTenantList().get(i).getFullName()
                            + " - " + householdList.get(chosenHouseholdIndex - 1).getTenantList().get(i).getTotalWaterUsed()
                            + " litara vode!");
                }
                try {
                    checkForLeak(householdList.get(chosenHouseholdIndex - 1));
                } catch (LeakException e) {
                    System.out.println(e.getMessage());
                    logger.error(e.getMessage());
                }
                System.out.println("Ukupna potrošnja vode kućanstva: "
                        + householdList.get(chosenHouseholdIndex - 1).getTotalHouseholdWaterUsage()
                        + " litara, a račun iznosi: " + householdList.get(chosenHouseholdIndex - 1).calculateTotalBill() + " eura!");
            }
        }while (notFinished);

        //ispis kućanstava koja imaju curenje (generics)
        List<Household> leakingHouseholds = new ArrayList<>();
        for (Household household : householdList){
            if (household.getLeak().isLeaking()){
                leakingHouseholds.add(household);
            }
        }
        LeakingHouseholdGeneric<Household> leakingHouseholdGeneric = new LeakingHouseholdGeneric<>(leakingHouseholds);
        System.out.println("\nKućanstva koja imaju curenje: ");
        for (Household household : leakingHouseholdGeneric.getLeakingHouseholdList()){
            System.out.println(household.getAddress());
        }

        //sortiranje stanara po potrošnji vode
        Set<Tenant> sortedTenantSetByWaterUsage = new TreeSet<>(Comparator.comparing(Tenant::getTotalWaterUsed).reversed());
        sortedTenantSetByWaterUsage.addAll(tenantList);
        System.out.println("\nStanari sortirani po potrošnji vode: ");
        for (Tenant tenant : sortedTenantSetByWaterUsage){
            System.out.println(tenant.getFullName() + " - " + tenant.getTotalWaterUsed() + " litara vode!");
        }

        //sortiranje kućanstava po potrošnji vode
        List<Household> sortedHouseholdListByWaterUsage = householdList.stream()
                .sorted(Comparator.comparing(Household::getTotalHouseholdWaterUsage).reversed())
                .toList();
        System.out.println("\nKućanstva sortirana po potrošnji vode: ");
        for (Household household : sortedHouseholdListByWaterUsage){
            System.out.println(household.getAddress() + " - " + household.getTotalHouseholdWaterUsage() + " litara vode!");
        }

        //sortirana kucanstva sa iznad procjenom potrosnje vode
        BigDecimal averageHouseholdWaterUsage = calculateAverageHouseholdWaterUsage(householdList);
        List<Household> sortedHouseholdListByAboveAverageWaterUsage = householdList.stream()
                .filter(household -> household.getTotalHouseholdWaterUsage().compareTo(averageHouseholdWaterUsage) > 0)
                .sorted(Comparator.comparing(Household::getTotalHouseholdWaterUsage).reversed())
                .toList();
        System.out.println("\nKućanstva sa iznad prosječnom potrošnjom vode (" + averageHouseholdWaterUsage + " l): ");
        for (Household household : sortedHouseholdListByAboveAverageWaterUsage){
            System.out.println(household.getAddress() + " - " + household.getTotalHouseholdWaterUsage() + " litara vode!");
        }

        //filitrirani računi sa iznad prosjecnim iznosom računa
        BigDecimal averageBillAmount = calculateAverageBillAmount(billSet);
        List<Bill<BigDecimal>> filteredBillListByAboveAverageBillAmount = billSet.stream()
                .filter(bill -> bill.getTotalBill().compareTo(averageBillAmount) > 0)
                .toList();
        System.out.println("\nRačuni sa iznad prosječnim iznosom računa (" + averageBillAmount + " eura): ");
        for (Bill<BigDecimal> bill : filteredBillListByAboveAverageBillAmount){
            System.out.println(bill.getAddress() + " - " + bill.getTotalBill() + " eura!");
        }



    }

    private static void enterTenant(int i, Scanner inputScanner, List<Tenant> tenantList, Long tenantIdCounter) {
        System.out.println("Unesi podatke o potrošnji vode "+ (i +1) + ". stanara!");

        boolean duplicateTenant = false;
        Optional<String> optionalFullName = Optional.empty();
        do {
            duplicateTenant = false;

            System.out.println("Unesi puno ime stanara >");
            String fullName = inputScanner.nextLine();

            try {
                duplicateTenant = checkDuplicateTenant(fullName, tenantList);
                optionalFullName = Optional.of(fullName);
            }catch (DuplicateTenantException e){
                System.out.println(e.getMessage());
                logger.error(e.getMessage());
                duplicateTenant = true;
            }

        }while (duplicateTenant);


        Optional<Shower> showerOptional = Optional.empty();
        boolean error;
        do {
            error = false;
            System.out.println("Unesi trajanje tuširanja u minutama >");
            try {
                BigDecimal showerDuration = inputScanner.nextBigDecimal();
                checkInputNumber(showerDuration);
                showerOptional = Optional.of(new Shower(showerDuration));
            } catch (InputMismatchException e) {
                logger.error("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                System.out.println("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                error = true;
            }catch (NumberTooLowException e){
                logger.error(e.getMessage());
                System.out.println(e.getMessage());
                error = true;
            }
            inputScanner.nextLine();

        }while (error);
        Shower shower = showerOptional.get();

        Optional<Dishwasher> dishwasherOptional = Optional.empty();
        do {
            error = false;
            System.out.println("Unesi trajanje pranja posuđa u minutama > ");
            try {
                BigDecimal dishwashingDuration = inputScanner.nextBigDecimal();
                checkInputNumber(dishwashingDuration);
                dishwasherOptional = Optional.of(new Dishwasher(dishwashingDuration));
            } catch (InputMismatchException e) {
                logger.error("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                System.out.println("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                error = true;
            }catch (NumberTooLowException e){
                logger.error(e.getMessage());
                System.out.println(e.getMessage());
                error = true;
            }
            inputScanner.nextLine();
        }while (error);
        Dishwasher dishwasher = dishwasherOptional.get();

        Optional<WashingMachine> optionalWashingMachine = Optional.empty();
        do {
            error = false;
            System.out.println("Unesi trajanje pranja odjeće u minutama > ");
            try {
                BigDecimal laundryDuration = inputScanner.nextBigDecimal();
                checkInputNumber(laundryDuration);
                optionalWashingMachine = Optional.of(new WashingMachine(laundryDuration));
            }catch (InputMismatchException e){
                logger.error("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                System.out.println("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                error = true;
            }catch (NumberTooLowException e){
                logger.error(e.getMessage());
                System.out.println(e.getMessage());
                error = true;
            }
            inputScanner.nextLine();
        }while (error);
        WashingMachine washingMachine = optionalWashingMachine.get();

        Optional<CarWash> carWashOptional = Optional.empty();
        do {
            error = false;
            System.out.println("Unesi trajanje pranja automobila u minutama > ");
            try {
                BigDecimal carWashDuration = inputScanner.nextBigDecimal();
                checkInputNumber(carWashDuration);
                carWashOptional = Optional.of(new CarWash(carWashDuration));
            }catch (InputMismatchException e){
                logger.error("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                System.out.println("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                error = true;
            }catch (NumberTooLowException e){
                logger.error(e.getMessage());
                System.out.println(e.getMessage());
                error = true;
            }
            inputScanner.nextLine();
        }while (error);
        CarWash carWash = carWashOptional.get();

        String fullName = optionalFullName.get();
        Tenant tenant = new Tenant(tenantIdCounter, fullName, shower, dishwasher, washingMachine, carWash);
        tenantList.add(tenant);
    }

    private static void enterHouseholds(int i, Scanner inputScanner, List<Household> householdList, List<Tenant> tenantList, Long householdIdCounter) {
        System.out.println("Unesi podatke o "+ (i +1) + ". kućanstvu!");

        boolean duplicateHousehold = false;
        Optional<String> optionalAddress = Optional.empty();
        do {
            duplicateHousehold = false;

            System.out.println("Unesi adresu kućanstva >");
            String address = inputScanner.nextLine();

            try {
                duplicateHousehold = checkDuplicateHousehold(address, householdList);
                optionalAddress = Optional.of(address);
            }catch (DuplicateHouseholdException e){
                System.out.println(e.getMessage());
                logger.error(e.getMessage());
                duplicateHousehold = true;
            }

        }while (duplicateHousehold);

        List<Tenant> tenantsInHousehold = new ArrayList<>();
        BigDecimal totalWaterUsedByHousehold = BigDecimal.valueOf(0);
        boolean notFinished = true;
        do {
            System.out.println("Odaberi stanare u kućanstvu >");
            for (int j = 0; j < tenantList.size(); j++) {
                System.out.println(j + 1 + " - " + tenantList.get(j).getFullName());
            }
            System.out.println("0 - Spremi!");

            boolean error = false;
            Integer chosenTenantIndex = -1;
            do {
                error = false;
                try {
                    chosenTenantIndex = inputScanner.nextInt();
                } catch (InputMismatchException e) {
                    logger.error("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                    System.out.println("Pogeška pri unosu! Potrebno je unjeti broj, pokušaj ponovno!");
                    error = true;
                }
                inputScanner.nextLine();

                if (chosenTenantIndex<0 || chosenTenantIndex>tenantList.size()){
                    System.out.println("Greška pri odabiru! Unesi broj između 0 i " + tenantList.size());
                }

            }while (error || chosenTenantIndex<0 || chosenTenantIndex>tenantList.size());

            if (chosenTenantIndex==0){
                notFinished = false;
            }else {
                totalWaterUsedByHousehold = totalWaterUsedByHousehold.add(tenantList.get(chosenTenantIndex - 1).getTotalWaterUsed());
                tenantsInHousehold.add(tenantList.get(chosenTenantIndex - 1));
                tenantList.remove(chosenTenantIndex - 1);
            }
        }while(notFinished);

        String address = optionalAddress.get();
        Household household = new Household.Builder(householdIdCounter)
                .atAddress(address)
                .tenantList(tenantsInHousehold)
                .totalHouseholdWaterUsage(totalWaterUsedByHousehold)
                .build();
        household.setLeak(household.determineLeakingStatus());

        householdList.add(household);
    }

    public static Boolean checkDuplicateTenant(String tenantToCheck, List<Tenant> tenantList){
        for(Tenant tenant : tenantList){
            if (tenant.getFullName().equals(tenantToCheck)){
                throw new DuplicateTenantException("Stanar već postoji! Pokušaj ponovno!");
            }
        }

        return false;
    }

    private static Boolean checkDuplicateHousehold(String addressToCheck, List<Household> householdList)throws DuplicateHouseholdException {
        for(Household household : householdList){
            if (household.getAddress().equals(addressToCheck)){
                throw new DuplicateTenantException("Kućanstvo već postoji! Pokušaj ponovno!");
            }
        }

        return false;
    }

    private static void checkForLeak(Household householdToCheck) {
        if (householdToCheck.getLeak().isLeaking()){
            throw new LeakException("Kućanstvo ima curenje! Račun će biti uvećan za 15%!");
        }

    }

    private static void checkInputNumber(BigDecimal numberToCheck) throws NumberTooLowException {
        if (numberToCheck.compareTo(BigDecimal.valueOf(0))<0){
            throw new NumberTooLowException("Broj ne smije biti manji od 0! Pokušaj ponovno!");
        }
    }

    private static BigDecimal calculateAverageHouseholdWaterUsage(List<Household> householdList) {
    	BigDecimal totalWaterUsage = BigDecimal.valueOf(0);
    	for (Household household : householdList) {
    		totalWaterUsage = totalWaterUsage.add(household.getTotalHouseholdWaterUsage());
    	}
    	return totalWaterUsage.divide(BigDecimal.valueOf(householdList.size()), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateAverageBillAmount(Set<Bill<BigDecimal>> billSet) {
        BigDecimal totalBillAmount = BigDecimal.valueOf(0);
        for (Bill<BigDecimal> bill : billSet) {
            totalBillAmount = totalBillAmount.add(bill.getTotalBill());
        }
        return totalBillAmount.divide(BigDecimal.valueOf(billSet.size()), 2, RoundingMode.HALF_UP);
    }

}