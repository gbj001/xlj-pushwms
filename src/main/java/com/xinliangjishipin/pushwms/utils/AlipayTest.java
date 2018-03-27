package com.xinliangjishipin.pushwms.utils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipayFundTransOrderQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayFundTransOrderQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;

public class AlipayTest {

    public void testAlipay() {
        String appId = "2018030202301851";
        String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCUexAA9XtqfU9s24YXCZrrbY3RVZwUT4GEYJK6ETcbZp2Dz1R3TzzWJjYXAQMY0rN+ZDFvXKMD3nzltY5tIXFKhUn6ykCU98ilRvBtxgWASeUpOL3JC4Rs0wSS9eV/ti89UnkJ0ZGpAPGjKk6Zup7VK5PbCqPeQfwRVpS9VxfdV2deQFOSF053EwSXLHgrNXIP+3d0Zy5DynUAgSch6VDuvYsdfQTUjThgW2/rejPHDm1EFfN2ybGBy15hI3TLA3Rh8KR7mijTMnTZsk8jblI+jght5CVUBjHFGrk7a5nOj9HCloSAsLIJq7gAUPNXKlKpkY/yLOzHGj3PrjRxOwOLAgMBAAECggEAY/Xpk0uw98M/KoEr5+yOgHrz4/9noYDZKB7ACUY3vFm8J5X4Po543Y9CEQCcbiTDtI6NfXR3Rs6NdTRim3PNSqcrZAyvp9qdGTAGA1EyOOkGv1a05lm7oGv8A1hKVk13xif01rhhAM9i3j9IRVSPQ+Ifm3KxWZtAsQeCAWpV5au7gWHDKz8X7C6AiBUID1Dm09Z0aj5F51+uC/PgSF/QDAmqHs9jtHIeYFrzpim2QSF4CZkK7eFeFuw7hi9Joj/qboQgUe46UMiPa21raev9wAk39/BN4TXjIZI4tA+0a2eBUQueYqRbood+E6h/PIFL7HoayNOgEKNSBMhksv/d8QKBgQDtlmuPOlB5aL90NFllVp6UGQlSWWIwUW/QsMzuDoodh2iRKomRtkWOCPR+81ozKx57WW4KlyFi2Lc4t/z/v3TOotRUgJ8719yxujhk2q63FU96MtQV0ODJ48jvJg8s3riscb5U4rX6Y19rQTJNldZP3LkJOraIkj+T3sT/zeok4wKBgQCf/NJAXA6lOgPEhX1SVu5FXJ9YgN+i6umH42xd6CCZH5JZ9KjgYYXgvYZyhUqDSyeNc2ELRQs3zcah8SawusIOmJYdGyxtyocEIBeaaiBWP3eRd6pXw7EBXpxXMSJI0UBjXg54GNTVQk67s59HEw+fmFRm+RtBqg6PmS3NcuqPOQKBgQCTIWjAjONTHEwb4WNO/2I64npEVfgZZgUxnpt5/OUJPlbCNy50XwUZ3W/Twk4ki9pXlt0vj8HsHbrxU/dIRb9HS8zj+7cgbyBLq7/KrSYvWPIcAagXToA2ZmqDtvUE23RPziyJEtCRG8L2f6xwIY9Ta6PnFEX/s1nN79HdfB448wKBgCEVNqT4ZE7mYEETGYcdUsglDw5OF/CogwIGlTIV/ierz6eqYAGGKRkAF/02cuITeGpXoYmjDV7MvnZeV5HUDKzYALKkG9vYNXM076yOpYEwPplmFWNwo/mUht/A2UYVfysNGBDdkaVHwOAvlJAt8N0fstRYTrqVX81x73a8fKSBAoGBAMJw/Lakdmbz5hZK0BW867vWHsEPtZflkoRSjzfZ5b7iNn19Jz+zFELaniyE3ktwgWYOA6pJL59JVEJBNLBJ+QBF9aTSTFDtx8MlGHA36ZjblCXlcgONzvOjxfGFABdMFZBHPeg5tnhrADBqCcUob2/95uV7iZYZb+czXsNcInCF";
        //String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlHsQAPV7an1PbNuGFwma622N0VWcFE+BhGCSuhE3G2adg89Ud0881iY2FwEDGNKzfmQxb1yjA9585bWObSFxSoVJ+spAlPfIpUbwbcYFgEnlKTi9yQuEbNMEkvXlf7YvPVJ5CdGRqQDxoypOmbqe1SuT2wqj3kH8EVaUvVcX3VdnXkBTkhdOdxMElyx4KzVyD/t3dGcuQ8p1AIEnIelQ7r2LHX0E1I04YFtv63ozxw5tRBXzdsmxgcteYSN0ywN0YfCke5oo0zJ02bJPI25SPo4IbeQlVAYxxRq5O2uZzo/RwpaEgLCyCau4AFDzVypSqZGP8izsxxo9z640cTsDiwIDAQAB";
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArNNqg1qmdRWjH1XTUTZ4dp4hk7iHo4mi5w2e4uYPpt3ep22yVzrqRT6px9E9nKiEYjRheSPRrN9BOH/xFLcFkUkSONZn6OwYKvBXMsV8HhkUbTJhurPku7Ts7+aREt0dYd3/9cC1a/QCCokcQJLrCAzWYbg11FpI302ahlsJZdL5EnlTxNomLu2thswDcd9SWJhXPiqihTY/Up1DGOKO/h8jrm/16D5Jik/sM3pxAWUTe5TYdS6tlpRN34ATsD27SdX8BigmTL/S5MneS7yi+4HqVxrfJaikWXJ4G8aSZl2mxh2yAlw/A2Zz2cLiPuZDzfPhkavl1XQG1ohgr2TcWQIDAQAB";

        //AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",appId,privateKey,"json","GBK",publicKey,"RSA2");
        //AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
        //request.setBizContent("{" +
        //        "\"bill_type\":\"trade\"," +
        //        "\"bill_date\":\"2018-03-09\"" +
        //        "  }");
        //AlipayDataDataserviceBillDownloadurlQueryResponse response = null;
        //try {
        //    response = alipayClient.execute(request);
        //} catch (AlipayApiException e) {
        //    e.printStackTrace();
        //}
        //if(response.isSuccess()){
        //    System.out.println("调用成功");
        //} else {
        //    System.out.println("调用失败");
        //}



        //AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, privateKey, "json", "gbk", publicKey, "RSA2");
        //AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        //request.setBizContent("{" +
        //        "\"out_trade_no\":\"\"," +
        //        "\"trade_no\":\"2018030921001004260530796404\"" +
        //        "}");
        //AlipayTradeQueryResponse response = null;
        //try {
        //    response = alipayClient.execute(request);
        //    System.out.println(response.getBody());
        //} catch (AlipayApiException e) {
        //    e.printStackTrace();
        //}
        //if(response.isSuccess()){
        //    System.out.println("调用成功");
        //} else {
        //    System.out.println("调用失败");
        //}




        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, privateKey, "json", "gbk", publicKey, "RSA2");
        //AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
        request.setBizContent("{" +
                "\"bill_type\":\"trade\"," +
                "\"bill_date\":\"2018-03-09\"" +
                "  }");
        AlipayDataDataserviceBillDownloadurlQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
            System.out.println(response.getBody());
            System.out.println(response.getBillDownloadUrl());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }



        //AlipayFundTransOrderQueryRequest request = new AlipayFundTransOrderQueryRequest();
        //request.setBizContent("{" +
        //        "\"order_id\":\"20180306200040011100090031951576\"" +
        //        "  }");
        //AlipayFundTransOrderQueryResponse response = null;
        //try {
        //    response = alipayClient.execute(request);
        //    System.out.println(response.getBody());
        //} catch (AlipayApiException e) {
        //    e.printStackTrace();
        //}
        //if (response.isSuccess()) {
        //    System.out.println("调用成功");
        //} else {
        //    System.out.println("调用失败");
        //}
    }

    public static void main(String args[]) {

    }


}
