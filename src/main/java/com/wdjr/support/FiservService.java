package com.wdjr.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;

import cn.hutool.http.HttpUtil;
import com.github.GBSEcom.client.ApiException;
import com.github.GBSEcom.model.PaymentCardSaleTransaction;
import com.github.GBSEcom.model.PaymentMethodDetails;
import com.github.GBSEcom.model.ReturnTransaction;
import com.github.GBSEcom.model.Secure3DAuthenticationResponse;
import com.github.GBSEcom.model.Secure3DAuthenticationResponseParams;
import com.github.GBSEcom.model.TransactionResponse;
import com.github.GBSEcom.simple.ClientContext;
import com.github.GBSEcom.simple.ClientContextImpl;
import com.github.GBSEcom.simple.ClientFactory;
import com.github.GBSEcom.simple.MerchantCredentials;
import com.wdjr.entity.CardInfo;
import com.wdjr.entity.FromData;
import com.wdjr.entity.Secure3DSAuthenticationUpdateRequest;
import com.wdjr.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;

@Import(
        FiservConfig.class
)
@Service
@Slf4j
public class FiservService {
    private ClientFactory factory;
    private ClientContext context;
    private final FiservConfig fiservConfig;
    private final FiservMapper fiservMapper;
    private List<String> treansactionIds = new ArrayList<>();

    @Autowired
    public FiservService(FiservConfig fiservConfig, FiservMapper fiservMapper) {
        this.fiservConfig = fiservConfig;
        this.fiservMapper = fiservMapper;
    }


    @PostConstruct
    void initialize() {
        final String basePath = fiservConfig.getBaseUrl();
        final String apiSecret = fiservConfig.getApiSecret();
        final String apiKey = fiservConfig.getApiKey();
        context = ClientContextImpl.create(
                MerchantCredentials.of(
                        apiSecret,
                        apiKey),
                basePath);
        factory = context.getFactory();
    }

    public String create3DsPayIn(final String cardNo, Model model) throws Exception {
        final CardInfo cardInfo = fiservConfig.getCardInfo(cardNo);
        final PaymentCardSaleTransaction paymentCardSaleTransaction =
                fiservMapper.toSaleTransactionRequest(cardInfo);
        final TransactionResponse transactionResponse;
        try {
            transactionResponse = factory.getPaymentApi().submitPrimaryTransaction(
                    paymentCardSaleTransaction);
        } catch (ApiException e) {
            log.error("create3DsPayIn error :{}", e.getResponseBody());
            return createView(e, model);
        }

        String transactionId = transactionResponse.getIpgTransactionId();
        treansactionIds.add(transactionId);
        return TransactionResponse.TransactionStatusEnum.APPROVED.equals(
                transactionResponse.getTransactionStatus()) ?
                finishPrint(transactionResponse, model) :
                Optional.ofNullable(transactionResponse.getAuthenticationResponse())
                        .map(Secure3DAuthenticationResponse::getParams).isPresent() ?
                        withOutIframe(transactionResponse, model) :
                        withIframe(transactionResponse);
    }

    private String createView(ApiException e, Model model) {
        final FromData fromData = FromData.builder().errorMsg(e.getResponseBody()).build();
        model.addAttribute("fromData", fromData);
        return "success/error";
    }


    public String handleWebhook(final String cRes, Model model) throws Exception {
        final Secure3DSAuthenticationUpdateRequest request =
                fiservMapper.toSecure3D21AuthenticationUpdateRequest(cRes);
        final TransactionResponse transactionResponse;
        final String transactionId = treansactionIds.get(treansactionIds.size() - 1);
        try {
            log.info("transactionId: {}", transactionId);
            transactionResponse = factory.getPaymentApi()
                    .finalizeSecureTransaction(treansactionIds.get(treansactionIds.size() - 1),
                            request, context.getDefaultRegion());
        } catch (ApiException e) {
            log.error("finalizeSecureTransaction errorMsg: {}", e.getResponseBody(), e);
            return createView(e, model);
        }

        return Optional.ofNullable(transactionResponse.getAuthenticationResponse()).map(
                        Secure3DAuthenticationResponse::getParams)
                .map(Secure3DAuthenticationResponseParams::getAcsURL)
                .isPresent() ? withOutIframe(transactionResponse, model) :
                finishPrint(transactionResponse, model);

    }


    private String finishPrint(TransactionResponse transactionResponse, Model model)
            throws Exception {
        transactionResponse = factory.getPaymentApi()
                .transactionInquiry(transactionResponse.getIpgTransactionId(),
                        context.getDefaultRegion(),
                        fiservConfig.getStoreId());
        final PaymentMethodDetails paymentMethodDetails =
                transactionResponse.getPaymentMethodDetails();
        final FromData fromData = FromData.builder().
                last4(paymentMethodDetails.getPaymentCard().getLast4())
                .transactionId(transactionResponse.getIpgTransactionId())
                .transactionStatus(transactionResponse.getTransactionState().name()).build();
        model.addAttribute("fromData", fromData);
        return "success/finish";
    }

//    public String withOutIframe(final TransactionResponse transactionResponse,final Model model) throws Exception {
//        final String cReq =
//                transactionResponse.getAuthenticationResponse().getParams().getcReq();
//        final String acsURL =
//                transactionResponse.getAuthenticationResponse().getParams().getAcsURL();
//        final String result = HttpUtil.post(acsURL, Map.of("creq", cReq));
//        //request result
//        log.info(result);
//        postReq(result);
//        return finishPrint(transactionResponse,model);
//    }

    public String withOutIframe(final TransactionResponse transactionResponse, final Model model)
            throws Exception {
        final String cReq =
                transactionResponse.getAuthenticationResponse().getParams().getcReq();
        final String acsURL =
                transactionResponse.getAuthenticationResponse().getParams().getAcsURL();
        String html = "<html>\n" +
                "<head><title>withOutIframe</title></head>\n" +
                "<body onLoad=\"document.tdsMmethodForm.submit();\">\n" +
                "<p><h1>Order Form</h1></p>\n" +
                "  \n" +
                "<form id=\"tdsMmethodForm\" name=\"tdsMmethodForm\" action=\"" + acsURL +
                "\" method=\"post\" target=\"tdsMmethodTgtFrame\" xmlns=\"http://www.w3.org/1999/xhtml\">  " +
                "  <input type=\"hidden\" name=\"creq\" value=\"" + cReq + "\" />     </form>\n" +
                "   \n" +
                "</body>\n" +
                "</html>";

//        final String result = HttpUtil.post(acsURL, Map.of("creq", cReq));
        //request result
        log.info(html);
        final File file = ResourceUtils.getFile("classpath:templates/withOutIframe.html");
        boolean createFile = file.delete() ? file.createNewFile() : false;
        FileUtils.writeTxtFile(html, file);
        return "withOutIframe";
    }

    private void postReq(String result) {
        String postUrl =
                result.substring(result.indexOf("action=\"") + 8, result.indexOf(" method=\"") - 1);
        log.info(": {}", postUrl);
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("result", "y");
        param.add("slowdownMs", "0");
        final String confirmPage = WebClient.builder().baseUrl(postUrl)
                .build().post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(param)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info(confirmPage);
        final String inputToLast = confirmPage.substring(confirmPage.lastIndexOf("<input"),
                confirmPage.lastIndexOf("</form>"));
        final String value = inputToLast.substring(inputToLast.lastIndexOf("value=") + 7,
                inputToLast.lastIndexOf("\">"));
        final String webhookResult =
                HttpUtil.post(fiservConfig.getTermURL(), Map.of("cres", value));
        log.info("webhookResult: {}", webhookResult);
    }


//    public String withIframe(final TransactionResponse transactionResponse)
//            throws Exception {
//        String methodForm =
//                transactionResponse.getAuthenticationResponse().getSecure3dMethod()
//                        .getMethodForm();
//        String inputStr = methodForm.substring(methodForm.indexOf("<input"), methodForm.indexOf("<script"));
//        log.info("inputStr: {}", inputStr);
//        final String threeDSMethodData =
//                inputStr.substring(inputStr.indexOf("value=\"") + 7, inputStr.indexOf("\"/"));
//        final Map<String, Object> requestMap =
//                Map.of("3DSMethodData", threeDSMethodData, "threeDSMethodData", threeDSMethodData);
//
//        String postUrl = methodForm.substring(methodForm.indexOf("action=\"")+8,methodForm.indexOf(" method=\"")-1);
//        final String result = HttpUtil.post(postUrl,
//                requestMap);
//        log.info(result);
//        final File file = ResourceUtils.getFile("classpath:templates/withIframe.html");
//        boolean createFile = file.delete() ? file.createNewFile() : false;
//        FileUtils.writeTxtFile(result, file);
//        return "withIframe";
//    }

    private String withIframe(final TransactionResponse transactionResponse) throws Exception {
        String methodForm = transactionResponse.getAuthenticationResponse().getSecure3dMethod()
                .getMethodForm();
        String result = "<html>\n" +
                "<head><title>withIframe</title></head>\n" +
                "<body onLoad=\"document.tdsMmethodForm.submit();\">\n" +
                "<p><h1>Order Form</h1></p>" +
                methodForm.substring(methodForm.lastIndexOf("<form id"))
                + "</body>\n" +
                "</html>";
        final File file = ResourceUtils.getFile("classpath:templates/withIframe.html");
        boolean createFile = file.delete() ? file.createNewFile() : false;
        FileUtils.writeTxtFile(result, file);
        return "withIframe";
    }

    public String refund(String transactionId, Model model) {
        final ReturnTransaction refund3dsRequest = fiservMapper.toRefund3dsRequest();
        try {
            final TransactionResponse transactionResponse = factory.getPaymentApi()
                    .submitSecondaryTransaction(transactionId,
                            refund3dsRequest,
                            context.getDefaultRegion(),
                            fiservConfig.getStoreId());
            final TransactionResponse.TransactionStatusEnum transactionStatus =
                    transactionResponse.getTransactionStatus();
            log.info("transactionStatus : {}",transactionStatus);
            log.info("transactionId:{}",transactionId);
            log.info("transactionResponse:{}",transactionResponse);
            final FromData fromData = FromData.builder().errorMsg("退款成功").build();
            model.addAttribute("fromData", fromData);
            return "success/error";
        } catch (ApiException e) {
            log.error("ReturnTransaction errorMsg: {}", e.getResponseBody(), e);
            return createView(e, model);
        }
    }
}
