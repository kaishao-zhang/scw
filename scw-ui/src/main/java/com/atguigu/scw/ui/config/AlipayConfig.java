package com.atguigu.scw.ui.config;
import java.io.FileWriter;
import java.io.IOException;
public class AlipayConfig {
	public static String app_id = "2016101700706335";
	//用户的私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQClYqkbUqbloWfBqpA+IR9X1h59k5+5cr96AgCvRW1/q8w4hynNwIKm4FwpLbAiRbc0A7D/tOdenGF2rxqbrq7pAexWtMOWc/lNY7jEPkQXEV2It/uoeyf3/d9/+Tg2WKOqrjgrFrk6FF1d/H4SDdMxfjK2fwawf8859QnI6/Y5KiF9YWkzgBlylUW/1aYiWwE8FlBqftTH4fCx+e5zmaH1sJwPw336BZFjHPI33kdP8mkBLKAiwPXSoNGXcX08Z70ykKf8K0oGMo0OAFh6C4TG7Sc4J2Evh9SQc7HOghvwn0JQ8APJv6bAxOPbEzeKcxhOYJtyFeN0/Kh+SmpWfv6nAgMBAAECggEAfmghTqPmsn6iWjDunL4eQbmDhoHNxCWDWlKriUtsfPenWNFeT0fD1J6JcmPcSuW0OEpV/6uaaALor5j4omNqhueUZ1U0ZQtxUghVUZEx49u+/N96tvSiwPZG3H50y30GY09T2QM+QbkM/+gbx9rPP/Xv9vKx/zdBAVBb0Tkg8eEZaOazvDzBkUa5pHTGYIZlapZTz2ilCh2rJg7313By6wvQNy2Z6N2w9AlrlxbydqegT4Ty+GJI+LnffxzQfMv5ZANAtCrdoitXmhlaJ8lfQ/68bhShwmsCkEW5/fBgvjVHTR++AvJRbN//eW+0V+5gQA5o8m3Oqx+Y6l0flwJ2IQKBgQDdLWXw2pMn+KVnBrmPpZEaHzkoGgWUoFq00JA0Jedhh015DceSc2Ol3chGTvNkTXAdHcMJDt4oRPCi38Bd9+RK4cqqBF/9wGOQ61HvpwpVxN0lv+YAgnLJuWgQbP2Hw/hiO9lWZ3WaP+Qqkhif9Pa6QKSNMg6Guepg+TdDnP+ptwKBgQC/bI4/AXF29RaHGrYyWsSWhGHlR1anbCLBl9a6r0no6w3MBhGFwmDsTdzIE9BhPbC+HecwLAxLfcJLLdmCSMdOMXOmYj3xMIxTxX+HGWwYFF/IFSibXfrgK1rqmoadTzHoxgvaJSC6GPhVWnYvbgRvB3h6FOFhB3mY2LiukkcSkQKBgBaTCKGRnY1p2m/uhsf7jRjOcQX/yewGhduIof9AkyIoLicZ2fG/pz8JqORMMJlfMf82x25IPzJ6/tiQ39B3eayO6SF7OtTqav8ilYVRVkO0ySGOOYp5xf+C1fU/Oo092j8kfqVL//vTY5VYvfytgvU1ndXvcv4bQanOJe7Xulx3AoGBALk/1f0VaXgEwZNn/1CF1OARJygavLpzhMMzLUQnJPC6oPSEHusCbIfi5jneKvggGm4yRCyzRAa+oovcyDU0N2TtqI0pX+aM5BCh/cOBj8FR3kukZlLNRV8xtd96QbH1FUi+eHPZIG3YgmMfk2ZdIv9MDISgtmgPMtb/TOLsfslxAoGBALafxaJDDtY7wEwpOUccIvjhbvUVI5AFRMHiTccBkbknQqoUOTUs1nuKdw54lqv5xzOZ9ruZ1U/0zi3uPCtOMH5Ty9W+37DVQR7yuxGOM0DvCz7AkvdP0b0yCT30A8ISJ40p4fEoCGfmd1WjVqi+u8oo2VhUu+nE9c4tAsHunUWC";
    //alipay的公钥
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnRqtsnZJbVBKE5JDanfU/jpAAb5CAtxo5/MiivUM7jcF+Goe5aOQHMKfi2EkITMxlKJnpDAp0W8r7pnYqcTwJ1qgMqLNeVOGj54CnW3uJOK9IzRJ/WzkV39WjOj1w8E4QsUJRBFQkd4RPE9Q6HDmS0tNbXSG5scWD0KCOBRkkslECaoJ9D4jceeqeuE26kw0DrnQ4k82JTMjT0HackWuuo+G8f1lAK9DR8U9U2hGsFWMGhH6dyGEpCT1uYgBa7UryAUgAZCjCHdhRfGkwCQ57pxbUdO1HZpUsEO48wrTWxjyqb8GQwp0TR/uciTish3Q5tu9H4/gauCSh+PlSN4O6QIDAQAB";
	public static String notify_url = "http://2g7743n917.qicp.vip/order/notify_url";
	public static String return_url = "http://2g7743n917.qicp.vip/order/return_url";
	//加密方式
	public static String sign_type = "RSA2";
	public static String charset = "utf-8";
	//网关
	public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
	public static String log_path = "F:\\";
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}