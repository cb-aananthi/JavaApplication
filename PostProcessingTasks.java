package BulkTesting;
import com.chargebee.Environment;
import com.chargebee.Result;
import com.chargebee.models.Subscription;
import java.util.List;
import java.util.function.Consumer;
import com.chargebee.models.*;
import javafx.util.*;

public class PostProcessingTasks {	 
	 
    public static Consumer<List<Object>> updateSubscriptions = (arguments)->{
        List<String> subscriptionIdList = (List<String>) arguments.get(0);
        String planId  = (String) arguments.get(1);
        try {
            Environment.configure("mannar-test", "test___dev__5pxqKvLawvrwei5dehlYctcugEZmc2izY");

            for (String subscriptionId : subscriptionIdList) {
                Result result = Subscription.update(subscriptionId)
                        .planId(planId)
                        .endOfTerm(false)
                        .request();
                result.subscription();
            }
        }
        catch (Exception e){
            throw new RuntimeException(e.getCause());
        }
    };

    public static Consumer<List<Object>> cancelSubscriptions = (arguments) -> {
        Environment.configure("mannar-test", "test___dev__5pxqKvLawvrwei5dehlYctcugEZmc2izY");

        List<String> subscriptionIdList = (List<String>)arguments.get(0);
        try {
            for (String subscriptionId : subscriptionIdList) {
                Subscription.cancel(subscriptionId)
                        .endOfTerm(false)
                        .request();
            }
        }
        catch (Exception e){
            throw new RuntimeException(e.getCause());
        }
    };

    public  static Consumer<List<Object>> applyDiscountOnSubscriptions = (arguments) -> {
        Environment.configure("mannar-test", "test___dev__5pxqKvLawvrwei5dehlYctcugEZmc2izY");

        List<String> subscriptionIdList = (List<String>)arguments.get(0);
        String couponIDs = (String) arguments.get(1);
        try{
            for (String subscriptionId : subscriptionIdList) {
                Subscription.update(subscriptionId).couponIds(couponIDs).invoiceImmediately(true).request();
            }
        }
        catch (Exception e){
            throw new RuntimeException(e.getCause());
        }
    };

    public static Consumer<List<Object>> createUnBilledChargesForSubscriptions = (arguments) -> {
        try {
            List<String> subscriptionIds = (List<String>) arguments.get(0);
            for (String subscriptionId : subscriptionIds) {
                Subscription.addChargeAtTermEnd(subscriptionId).amount(1000).description("Service Charge").request();
            }
        }
        catch (Exception e){
            throw new RuntimeException(e.getCause());
        }

    };

    public static Consumer<List<Object>> addCharges = (arguments) ->{
        List<String> subscriptionIds = (List<String>) arguments.get(0);
        try{
            for(String subscriptionId : subscriptionIds){
                Invoice.charge()
                        .subscriptionId(subscriptionId)
                        .amount(1000)
                        .description("Support Charge")
                        .request();
            }
        }
        catch (Exception e){
              throw new RuntimeException(e.getCause());
        }
    };

	public static Consumer updateSubscription;

    public static void executePostProcessingFunctions(List<Pair<Consumer,List<Object>>> consumer_list){
        for(Pair<Consumer,List<Object>> element  : consumer_list){
            Consumer consumer = element.getKey();
            List<Object> arguments = element.getValue();
            consumer.accept(arguments);
        }
    }

}
