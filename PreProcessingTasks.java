package BulkTesting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PreProcessingTasks {
    static List<Double> customers_index = new ArrayList<>();;
    static List<Double> subscriptions_index = new ArrayList<>();;
    static List<Double> invoices_index = new ArrayList<>();;
    static List<Double> credit_notes_index = new ArrayList<>();;
    static List<Double> coupons_index = new ArrayList<>();;
    static List<Double> plans_index = new ArrayList<>();;
    static Map<String, Double> taskRankMap = new HashMap<>();;
    static Map<Double, Consumer> rankFunctionMap = new HashMap<>();;

    public static void executeTasks(List<String> tasks){
        Initialize_TaskRank_Map();
        Initialize_Rank_Function_Map();
        for(String task : tasks){
            Double rank = taskRankMap.get(task);
            rankFunctionMap.get(rank).accept(rank);
        }
    }

    public static void Initialize_TaskRank_Map(){
        taskRankMap.put("CreateCustomer",1.0);
        taskRankMap.put("UpdateCustomer",1.1);
        taskRankMap.put("DeleteCustomer",1.2);


        taskRankMap.put("CreateSubscription",2.0);
        taskRankMap.put("UpgradeSubscription",2.1);
        taskRankMap.put("DowngradeSubscription",2.2);
        taskRankMap.put("DeleteSubscription",2.3);
        taskRankMap.put("pauseSubscription",2.4);
        taskRankMap.put("CancelSubscription",2.5);
        taskRankMap.put("ResumeSubscription",2.6);
        taskRankMap.put("SubscriptionWithSetupFee",2.7);
        taskRankMap.put("DiscountSpecificSubscription",2.8);
        taskRankMap.put("DiscountAllSubscription",2.9);

        taskRankMap.put("CreateInvoice",3.0);
        taskRankMap.put("UnbilledChargesInvoice",3.1);
        taskRankMap.put("AddChargesInvoice",3.2);
        taskRankMap.put("UpdateInvoice",3.3);
        taskRankMap.put("DeleteInvoice",3.4);
        taskRankMap.put("VoidInvoice",3.5);

        taskRankMap.put("CreateCreditNote",4.0);
        taskRankMap.put("VoidCreditNote",4.1);
        taskRankMap.put("DeleteCreditNote",4.2);
    }

    public static void Initialize_Rank_Function_Map() {
        rankFunctionMap.put(1.0,createCustomerIndex);
        rankFunctionMap.put(1.1,updateCustomerIndex);
        rankFunctionMap.put(1.2,deleteCustomerIndex);
        rankFunctionMap.put(2.0,createSubscriptionIndex);
        rankFunctionMap.put(2.1,upgradeSubscriptionIndex);
        rankFunctionMap.put(2.2,downgradeSubscriptionIndex);
        rankFunctionMap.put(2.3,deleteSubscriptionIndex);
        rankFunctionMap.put(2.4,pauseSubscriptionIndex);
        rankFunctionMap.put(2.5,cancelSubscriptionIndex);
        rankFunctionMap.put(2.6,resumeSubscriptionIndex);
        rankFunctionMap.put(2.7,subscriptionWithSetupFeeIndex);
        rankFunctionMap.put(2.8,discountSpecificIndex);
        rankFunctionMap.put(2.9,discountAllIndex);
        rankFunctionMap.put(3.0,createInvoiceIndex);
        rankFunctionMap.put(3.1,unbilledChargesInvoiceIndex);
        rankFunctionMap.put(3.2,addChargeInvoiceIndex);
        rankFunctionMap.put(3.3,updateInvoiceIndex);
        rankFunctionMap.put(3.4,deleteInvoiceIndex);
        rankFunctionMap.put(3.5,voidInvoiceIndex);
        rankFunctionMap.put(4.0,createCreditNoteIndex);
        rankFunctionMap.put(4.1,voidCreditNoteIndex);
        rankFunctionMap.put(4.2,deleteCreditNoteIndex);
    }
    public static Consumer<Double> createCustomerIndex = ( rank) ->{
        customers_index.add(rank);
    };

    public static Consumer<Double> updateCustomerIndex = ( rank) ->{
        createCustomerIndex.accept(rank);
    };

    public static Consumer<Double> deleteCustomerIndex = ( rank)-> {
        createCustomerIndex.accept(rank);
    };


    public  static Consumer<Double> createSubscriptionIndex = (rank) ->{
        subscriptions_index.add(rank);
        createCustomerIndex.accept(rank);
    };

    public  static Consumer<Double> upgradeSubscriptionIndex = (rank) ->{
        createSubscriptionIndex.accept(rank);
        rankFunctionMap.put(rank,PostProcessingTasks.updateSubscription);
    };

    public  static Consumer<Double> downgradeSubscriptionIndex = (rank) ->{
        createSubscriptionIndex.accept(rank);
        rankFunctionMap.put(rank,PostProcessingTasks.updateSubscription);
    };

    public  static Consumer<Double> pauseSubscriptionIndex = ( rank) ->{
        createSubscriptionIndex.accept(rank);
    };

    public  static Consumer<Double> deleteSubscriptionIndex = ( rank) ->{
        createSubscriptionIndex.accept(rank);
    };

    public  static Consumer<Double> cancelSubscriptionIndex = ( rank) ->{
        createSubscriptionIndex.accept(rank);
        rankFunctionMap.put(rank,PostProcessingTasks.cancelSubscriptions);
    };

    public  static Consumer<Double> resumeSubscriptionIndex = ( rank) ->{
        createSubscriptionIndex.accept(rank);
    };

    public  static Consumer<Double> subscriptionWithSetupFeeIndex = ( rank) ->{
        createSubscriptionIndex.accept(rank);
    };

    public  static Consumer<Double> discountSpecificIndex = ( rank) -> {
        subscriptions_index.add(rank);
        createCustomerIndex.accept(rank);
        rankFunctionMap.put(rank,PostProcessingTasks.applyDiscountOnSubscriptions);
    };

    public  static Consumer<Double> discountAllIndex = ( rank) -> {
        subscriptions_index.add(rank);
        createCustomerIndex.accept(rank);
        rankFunctionMap.put(rank,PostProcessingTasks.applyDiscountOnSubscriptions);
    };

    public  static Consumer<Double> createInvoiceIndex = ( rank) -> {
        invoices_index.add(rank);
        createSubscriptionIndex.accept(rank);
    };

    public  static Consumer<Double> unbilledChargesInvoiceIndex = (Double rank) -> {
        createSubscriptionIndex.accept(rank);
        rankFunctionMap.put(rank,PostProcessingTasks.createUnBilledChargesForSubscriptions);
    };

    public  static Consumer<Double> addChargeInvoiceIndex = (Double rank)-> {
        createSubscriptionIndex.accept(rank);
        rankFunctionMap.put(rank,PostProcessingTasks.addCharges);
    };

    public  static Consumer<Double> updateInvoiceIndex = ( rank) ->{
        createInvoiceIndex.accept(rank);
    };

    public  static Consumer<Double> deleteInvoiceIndex = (Double rank)->{
        createInvoiceIndex.accept(rank);
    };

    public  static Consumer<Double> voidInvoiceIndex = (Double rank) -> {
        createInvoiceIndex.accept(rank);
    };

    public  static Consumer<Double> createCreditNoteIndex = (Double rank) ->{
        credit_notes_index.add(rank);
    };

    public  static Consumer<Double> voidCreditNoteIndex = (Double rank) ->{
        createCreditNoteIndex.accept(rank);
    };

    public  static Consumer<Double> deleteCreditNoteIndex = (Double rank) -> {
        createCreditNoteIndex.accept(rank);
    };
}
