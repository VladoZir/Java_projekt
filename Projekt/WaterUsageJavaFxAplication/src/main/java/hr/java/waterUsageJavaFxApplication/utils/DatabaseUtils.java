package hr.java.waterUsageJavaFxApplication.utils;

import hr.java.waterUsageJavaFxApplication.model.*;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import static hr.java.waterUsageJavaFxApplication.HelloApplication.logger;

public class DatabaseUtils {
    private static final String DATABASE_FILE = "conf/database.properties";

    private static Connection connectToDatabase() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(DATABASE_FILE));
        String databaseUrl = properties.getProperty("databaseUrl");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        Connection connection = DriverManager.getConnection(databaseUrl, username, password);
        return connection;
    }

    public static List<Tenant> getTenantsFromDataBase(){
        List<Tenant> tenantList = new ArrayList<>();

        try(Connection connection = connectToDatabase()){

            String sqlQuery = "SELECT * FROM TENANT;";
            Statement statement = connection.createStatement();
            statement.execute(sqlQuery);

            ResultSet rs = statement.getResultSet();
            while(rs.next()){
                Long id = rs.getLong("ID");
                String tenantName = rs.getString("NAME");
                BigDecimal showerDuration = rs.getBigDecimal("SHOWER");
                Shower shower = new Shower(showerDuration);
                BigDecimal dishwasherDuration = rs.getBigDecimal("DISHWASHER");
                Dishwasher dishwasher = new Dishwasher(dishwasherDuration);
                BigDecimal washingMachineDuration = rs.getBigDecimal("WASHING_MACHINE");
                WashingMachine washingMachine = new WashingMachine(washingMachineDuration);
                BigDecimal carWashDuration = rs.getBigDecimal("CAR_WASH");
                CarWash carWash = new CarWash(carWashDuration);

                Tenant tenant = new Tenant(id, tenantName, shower, dishwasher, washingMachine, carWash);
                tenantList.add(tenant);
            }

        }catch (IOException | SQLException e){
            String message = "Greška kod dohvaćanja stanara iz baze!";
            logger.error(message, e);
            System.out.println(message);
        }

        return tenantList;
    }

    public static void saveTenantToDatabase(Tenant tenant){
        try(Connection connection = connectToDatabase()){

            String sqlQuery = "INSERT INTO TENANT(NAME, SHOWER, DISHWASHER, WASHING_MACHINE, CAR_WASH) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, tenant.getFullName());
            preparedStatement.setBigDecimal(2, tenant.getShower().getDurationMinutes());
            preparedStatement.setBigDecimal(3, tenant.getDishwasher().getDurationMinutes());
            preparedStatement.setBigDecimal(4, tenant.getWashingMachine().getDurationMinutes());
            preparedStatement.setBigDecimal(5, tenant.getCarWash().getDurationMinutes());

            preparedStatement.execute();

        }catch (IOException | SQLException e){
            String message = "Greška kod spremanja stanara u bazu!";
            logger.error(message, e);
            System.out.println(message);
        }
    }

    public static void deleteTenantFromDatabase(Tenant tenant){
        try(Connection connection = connectToDatabase()){

            String sqlQuery1 = "DELETE FROM HOUSEHOLD_TENANT WHERE TENANT_ID = ?;";
            PreparedStatement preparedStatement1 = connection.prepareStatement(sqlQuery1);
            preparedStatement1.setLong(1, tenant.getId());
            preparedStatement1.execute();

            String sqlQuery = "DELETE FROM TENANT WHERE ID = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, tenant.getId());
            preparedStatement.execute();

        }catch (IOException | SQLException e){
            String message = "Greška kod brisanja stanara iz baze!";
            logger.error(message, e);
            System.out.println(message);
        }
    }

    public static void updateTenantInDatabase(Tenant tenant){
        try (Connection connection = connectToDatabase()) {
            String sqlQuery = "UPDATE TENANT SET NAME = ?, SHOWER = ?, DISHWASHER = ?, WASHING_MACHINE = ?, CAR_WASH = ? WHERE ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, tenant.getFullName());
            preparedStatement.setBigDecimal(2, tenant.getShower().getDurationMinutes());
            preparedStatement.setBigDecimal(3, tenant.getDishwasher().getDurationMinutes());
            preparedStatement.setBigDecimal(4, tenant.getWashingMachine().getDurationMinutes());
            preparedStatement.setBigDecimal(5, tenant.getCarWash().getDurationMinutes());
            preparedStatement.setLong(6, tenant.getId());
            preparedStatement.executeUpdate();
        } catch (IOException | SQLException e) {
            String message = "Greška kod ažuriranja stanara u bazi!";
            logger.error(message, e);
            System.out.println(message);
        }
    }

    public static List<LinkedTenant> getHouseholdTenants(){
        List<LinkedTenant> linkedTenantList = new ArrayList<>();

        try (Connection connection = connectToDatabase()){

            String sqlQuery = "SELECT * FROM HOUSEHOLD_TENANT;";
            Statement statement = connection.createStatement();
            statement.execute(sqlQuery);

            ResultSet rs = statement.getResultSet();
            while(rs.next()){
                Long householdId = rs.getLong("HOUSEHOLD_ID");
                Long tenantId = rs.getLong("TENANT_ID");

                LinkedTenant linkedTenant = new LinkedTenant(householdId, tenantId);
                linkedTenantList.add(linkedTenant);
            }

        }catch (IOException | SQLException e){
            String message = "Greška kod dohvaćanja stanara kućanstva iz baze!";
            logger.error(message, e);
            System.out.println(message);
        }

        return  linkedTenantList;
    }

    public static List<Household> getHouseholdsFromDatabase(){
        List<Household> householdList = new ArrayList<>();

        try (Connection connection = connectToDatabase()){

            String sqlQuery = "SELECT * FROM HOUSEHOLD;";
            Statement statement = connection.createStatement();
            statement.execute(sqlQuery);

            ResultSet rs = statement.getResultSet();
            while(rs.next()){
                Long id = rs.getLong("ID");
                String address = rs.getString("ADDRESS");
                String leakingStatus = rs.getString("LEAK_STATUS");

                Optional<Leak> leakOptional = Optional.empty();
                if (leakingStatus.equals("imaCurenje")){
                    leakOptional = Optional.of(new Leak(true));
                } else {
                    leakOptional = Optional.of(new Leak(false));
                }

                List<Tenant> tenantList = getTenantsFromDataBase();
                List<LinkedTenant> linkedTenantList = getHouseholdTenants();

                List<Tenant> householdTenantsList = new ArrayList<>();
                BigDecimal totalHouseholdWaterUsage = BigDecimal.valueOf(0);
                for (LinkedTenant linkedTenant : linkedTenantList){
                    if (linkedTenant.getHouseholdId().equals(id)){
                        for (Tenant tenant : tenantList){
                            if (linkedTenant.getTenantId().equals(tenant.getId())){
                                householdTenantsList.add(tenant);
                                totalHouseholdWaterUsage = totalHouseholdWaterUsage.add(tenant.getTotalWaterUsed());
                            }
                        }
                    }
                }

                Household household = new Household.Builder(id)
                        .atAddress(address)
                        .tenantList(householdTenantsList)
                        .totalHouseholdWaterUsage(totalHouseholdWaterUsage)
                        .leak(leakOptional.get())
                        .build();
                householdList.add(household);
            }

        }catch (IOException | SQLException e){
            String message = "Greška kod dohvaćanja kućanstva iz baze!";
            logger.error(message, e);
            System.out.println(message);
        }


        return householdList;
    }

    public static void saveHouseholdToDatabase(Household household, List<Tenant> tenantsToGoInHouseholdList){
        try(Connection connection = connectToDatabase()){

            String sqlQuery = "INSERT INTO HOUSEHOLD(ADDRESS, LEAK_STATUS) VALUES (?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, household.getAddress());
            if(household.getLeak().isLeaking()){
                preparedStatement.setString(2, "imaCurenje");
            }else{
                preparedStatement.setString(2, "nemaCurenje");
            }

            preparedStatement.execute();

            String insertFactorySql = "INSERT INTO HOUSEHOLD_TENANT(HOUSEHOLD_ID, TENANT_ID) VALUES (?, ?);";
            List<Household> householdList = getHouseholdsFromDatabase();
            for (Household household1 : householdList){
                if (household1.equals(household)){
                    household.setId(household1.getId());
                }
            }
            for (Tenant tenant : tenantsToGoInHouseholdList) {
                PreparedStatement preparedStatement2 = connection.prepareStatement(insertFactorySql);
                preparedStatement2.setLong(1, household.getId());
                preparedStatement2.setLong(2, tenant.getId());

                preparedStatement2.execute();

            }


        }catch (IOException | SQLException e){
            String message = "Greška kod spremanja kućanstva u bazu!";
            logger.error(message, e);
            System.out.println(message);
        }
    }

    public static void deleteHouseholdFromDatabase(Household household){
        try(Connection connection = connectToDatabase()){

            String sqlQuery1 = "DELETE FROM HOUSEHOLD_TENANT WHERE HOUSEHOLD_ID = ?;";
            PreparedStatement preparedStatement1 = connection.prepareStatement(sqlQuery1);
            preparedStatement1.setLong(1, household.getId());
            preparedStatement1.execute();

            String sqlQuery = "DELETE FROM HOUSEHOLD WHERE ID = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setLong(1, household.getId());

            preparedStatement.execute();

        }catch (IOException | SQLException e){
            String message = "Greška kod brisanja kućanstva iz baze!";
            logger.error(message, e);
            System.out.println(message);
        }
    }

    public static void updateHouseholdInDatabase(Household household){
        try (Connection connection = connectToDatabase()) {

            String sqlQuery = "UPDATE HOUSEHOLD SET ADDRESS = ?, LEAK_STATUS = ? WHERE ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, household.getAddress());
            if(household.getLeak().isLeaking()){
                preparedStatement.setString(2, "imaCurenje");
            }else{
                preparedStatement.setString(2, "nemaCurenje");
            }
            preparedStatement.setLong(3, household.getId());
            preparedStatement.executeUpdate();

            String deleteSqlQuery = "DELETE FROM HOUSEHOLD_TENANT WHERE HOUSEHOLD_ID = ?;";
            PreparedStatement deletePreparedStatement = connection.prepareStatement(deleteSqlQuery);
            deletePreparedStatement.setLong(1, household.getId());
            deletePreparedStatement.execute();

            for (int i=0;i<household.getTenantList().size();i++){
                String insertSqlQuery = "INSERT INTO HOUSEHOLD_TENANT(HOUSEHOLD_ID, TENANT_ID) VALUES (?, ?);";
                PreparedStatement insertPreparedStatement = connection.prepareStatement(insertSqlQuery);
                insertPreparedStatement.setLong(1, household.getId());
                insertPreparedStatement.setLong(2, household.getTenantList().get(i).getId());
                insertPreparedStatement.execute();
            }


        } catch (IOException | SQLException e) {
            String message = "Greška kod ažuriranja kućanstva u bazi!";
            logger.error(message, e);
            System.out.println(message);
        }
    }

    public static LeakingHouseholdGeneric<Household> getHouseholdsWithLeak(){
        List<Household> householdList = new ArrayList<>();
        Optional<LeakingHouseholdGeneric<Household>> leakingHouseholdGenericOptional = Optional.empty();

        try (Connection connection = connectToDatabase()){

            String sqlQuery = "SELECT * FROM HOUSEHOLD WHERE LEAK_STATUS='imaCurenje';";
            Statement statement = connection.createStatement();
            statement.execute(sqlQuery);

            ResultSet rs = statement.getResultSet();
            while(rs.next()){
                Long id = rs.getLong("ID");
                String address = rs.getString("ADDRESS");
                String leakingStatus = rs.getString("LEAK_STATUS");

                Optional<Leak> leakOptional = Optional.empty();
                if (leakingStatus.equals("imaCurenje")){
                    leakOptional = Optional.of(new Leak(true));
                } else {
                    leakOptional = Optional.of(new Leak(false));
                }

                List<Tenant> tenantList = getTenantsFromDataBase();
                List<LinkedTenant> linkedTenantList = getHouseholdTenants();

                List<Tenant> householdTenantsList = new ArrayList<>();
                BigDecimal totalHouseholdWaterUsage = BigDecimal.valueOf(0);
                for (LinkedTenant linkedTenant : linkedTenantList){
                    if (linkedTenant.getHouseholdId().equals(id)){
                        for (Tenant tenant : tenantList){
                            if (linkedTenant.getTenantId().equals(tenant.getId())){
                                householdTenantsList.add(tenant);
                                totalHouseholdWaterUsage = totalHouseholdWaterUsage.add(tenant.getTotalWaterUsed());
                            }
                        }
                    }
                }

                Household household = new Household.Builder(id)
                        .atAddress(address)
                        .tenantList(householdTenantsList)
                        .totalHouseholdWaterUsage(totalHouseholdWaterUsage)
                        .leak(leakOptional.get())
                        .build();
                householdList.add(household);
            }

            leakingHouseholdGenericOptional= Optional.of(new LeakingHouseholdGeneric<>(householdList));


        }catch (IOException | SQLException e){
            String message = "Greška kod dohvaćanja kućanstva iz baze!";
            logger.error(message, e);
            System.out.println(message);
        }

        return leakingHouseholdGenericOptional.get();
    }

    public static Set<Bill<BigDecimal>> getBillsFromDatabase(){
        List<Household> householdList = getHouseholdsFromDatabase();
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

    public static List<Tenant> getTenantsNotInHousehold(){
        List<Tenant> tenantList = getTenantsFromDataBase();
        List<Tenant> tenantsNotInHouseholdList = new ArrayList<>();
        List<Household> householdList = getHouseholdsFromDatabase();

        for (int i=0;i<tenantList.size();i++){
            boolean isInHousehold = false;
            for (int j=0;j<householdList.size();j++){
                for (int k=0;k<householdList.get(j).getTenantList().size();k++){
                    if (tenantList.get(i).getId().equals(householdList.get(j).getTenantList().get(k).getId())){
                        isInHousehold = true;
                    }
                }
            }
            if (!isInHousehold){
                tenantsNotInHouseholdList.add(tenantList.get(i));
            }
        }


        return  tenantsNotInHouseholdList;
    }

    public static Optional<Changes> getTheLatestChange() {
        List<Changes> changesList = FileUtils.deserializeChanges();
        Optional<Changes> latestChangeOptional = Optional.of(changesList.getLast());

        FileUtils.serializeChanges(changesList);
        return latestChangeOptional;
    }

}
