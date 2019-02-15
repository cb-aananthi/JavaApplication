package BulkTesting;
import java.util.*;
import com.chargebee.*;
import com.chargebee.models.*;
import com.chargebee.models.enums.*;
import com.chargebee.models.Addon.*;
import java.util.function.*;
public class ProcessingTasks {
    static Map<Double, String> customerMap = new HashMap<Double, String>();
    static Map<Double, String> subscriptionMap = new HashMap<Double, String>();
    static Map<Double, String> invoiceMap = new HashMap<Double, String>();
    static List<String> tasks = new ArrayList<>();

    public  static  void InitializeTasks(){
        tasks.add("CreateCustomer");
        tasks.add("CreateSubscription");
    }

    public static void executeTasks(){
        createCustomers.accept(0);
        createSubscriptions.accept(0);
        createInvoices.accept(0);
    }
    public static void main(String args[]) throws  Exception{
        InitializeTasks();
        PreProcessingTasks.executeTasks(tasks);
        executeTasks();
       //PostProcessingTasks.executeTasks();
    }
    public static Consumer createCustomers = arguments ->{
        try {
            for(Double rank : PreProcessingTasks.customers_index) {
                Result result = Customer.create()
                        .firstName("Test_User_"  + System.currentTimeMillis())
                        .email("Test_User_"  + System.currentTimeMillis()+"@test.com")
                        .request();
                Customer customer = result.customer();
                customerMap.put(rank, customer.id());
            }
        }
        catch(Exception e) {
            throw new RuntimeException(e.getCause()) ;
        }
    };

    public static Supplier<String> createPlanWithSetupFee = () -> {
        Plan plan;
        try{
            plan = Plan.create()
                        .id("Test_plan_" + System.currentTimeMillis())
                        .name("Test_plan_" + System.currentTimeMillis())
                        .currencyCode("USD")
                        .price(20000)
                        .setupCost(200000)
                        .request().plan();

        }
        catch (Exception e){
            throw new RuntimeException(e.getCause());
        }
        return  plan.id();
    };

    public static Consumer createSubscriptions = (arguments) ->{
        try {
            for(Double rank : PreProcessingTasks.subscriptions_index) {
                String planID;
                if(rank == 2.6){
                    planID = createPlanWithSetupFee.get();
                }
                else{
                    planID = "plan2";
                }
                Result result = Subscription.create().id("newSubs")
                        .planId(planID)
                        .autoCollection(AutoCollection.OFF)
                        .invoiceImmediately(true)
                        .customerFirstName("Test_User_"  + System.currentTimeMillis())
                        .customerEmail("Test_User_"  + System.currentTimeMillis() + "@test.com")
                        .billingAddressFirstName("Test_User_"  + System.currentTimeMillis())
                        .billingAddressLine1("PO Box 9999")
                        .billingAddressCity("Walnut")
                        .billingAddressState("California")
                        .billingAddressZip("91789")
                        .billingAddressCountry("US")
                        .request();
                Subscription subscription = result.subscription();
                subscriptionMap.put(rank,subscription.id());
            }
        }
        catch(Exception e) {
            throw new RuntimeException(e.getCause()) ;
        }
    };
    public static Consumer createInvoices = (arguments) ->{
        try {
            for (Double rank : PreProcessingTasks.invoices_index) {
                Addon addon = Addon.create()
                        .id("Test_Addon_" + System.currentTimeMillis())
                        .name("Test_Addon_" + System.currentTimeMillis())
                        .invoiceName("Test Recurring addon pack")
                        .chargeType(ChargeType.NON_RECURRING)
                        .price(2000)
                        .currencyCode("USD")
                        .pricingModel(PricingModel.FLAT_FEE)
                        .request().addon();
                Customer customer = Customer.retrieve(customerMap.get(rank)).request().customer();
                Result result = Invoice.create()
                        .customerId(customer.id())
                        .addonId(0,"non_recurring_addon")
                        .addonUnitPrice(0,2000)
                        .addonQuantity(0,2)
                        .shippingAddressFirstName(customer.firstName())
                        .shippingAddressCity("Walnut")
                        .shippingAddressState("California")
                        .shippingAddressZip("91789")
                        .shippingAddressCountry("US")
                        .request();
                Invoice invoice = result.invoice();
                invoiceMap.put(rank,invoice.id());
            }
        }
            catch(Exception e) {
                throw new RuntimeException(e.getCause()) ;
            }
    };
}
