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
import com.github.GBSEcom.model.Secure3DAuthenticationResponse;
import com.github.GBSEcom.model.Secure3DAuthenticationResponseParams;
import com.github.GBSEcom.model.Secure3DAuthenticationUpdateRequest;
import com.github.GBSEcom.model.TransactionResponse;
import com.github.GBSEcom.simple.ClientContext;
import com.github.GBSEcom.simple.ClientContextImpl;
import com.github.GBSEcom.simple.ClientFactory;
import com.github.GBSEcom.simple.MerchantCredentials;
import com.wdjr.entity.FromData;
import com.wdjr.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;

@Import(
        FiservConfig.class
)
@Service
@Slf4j
public class FiservService {
    private ClientFactory factory;
    private ClientContext context;
    private final FiservConfig fiservConfig;
    private final  FiservMapper fiservMapper;
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

    public String create3DsPayIn(final String cardNo,Model model) throws Exception {
        final PaymentCardSaleTransaction paymentCardSaleTransaction =
                fiservMapper.toSaleTransactionRequest(cardNo);
        final TransactionResponse transactionResponse;
        try {
            transactionResponse = factory.getPaymentApi().submitPrimaryTransaction(
                    paymentCardSaleTransaction);
        } catch (ApiException e) {
            log.error("create3DsPayIn error",e.getResponseBody());
            throw new RuntimeException(e);
        }
        String transactionId = transactionResponse.getIpgTransactionId();
        treansactionIds.add(transactionId);
        final FiservTestDataType fiservTestDataType = fiservConfig.getFiservCardMap().get(cardNo);
        return FiservTestDataType.FRICTIONLESS_WITHOUT_IFRAME.equals(fiservTestDataType)?
                finishPrint(transactionResponse,model):
         FiservTestDataType.CHALLENG_WITHOUT_IFRAME.equals(fiservTestDataType) ?
                withOutIframe(transactionResponse) :
                withIframe(transactionResponse);
    }


    public String handleWebhook(final String cRes,Model model) throws Exception {
        final Secure3DAuthenticationUpdateRequest request =
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
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(transactionResponse.getAuthenticationResponse()).map(
                        Secure3DAuthenticationResponse::getParams)
                .map(Secure3DAuthenticationResponseParams::getAcsURL)
                .isPresent() ? withOutIframe(transactionResponse) : finishPrint(transactionResponse,model);

    }


    private String finishPrint(final TransactionResponse transactionResponse,Model model) throws Exception {
        final PaymentMethodDetails paymentMethodDetails =
                transactionResponse.getPaymentMethodDetails();
        final FromData fromData = FromData.builder().
                last4(paymentMethodDetails.getPaymentCard().getLast4())
                .transactionId(transactionResponse.getIpgTransactionId())
                .transactionStatus(transactionResponse.getTransactionStatus().name()).build();
        model.addAttribute("fromData", fromData);
        return "success/finish";
    }

    public String withOutIframe(final TransactionResponse transactionResponse) throws Exception {
        final String cReq =
                transactionResponse.getAuthenticationResponse().getParams().getcReq();
        final String acsURL =
                transactionResponse.getAuthenticationResponse().getParams().getAcsURL();
        final String result = HttpUtil.post(acsURL, Map.of("creq", cReq));
        //request result
        log.info(result);
        final File file = ResourceUtils.getFile("classpath:templates/withOutIframe.html");
        boolean createFile = file.delete() ? file.createNewFile() : false;
        FileUtils.writeTxtFile(result, file);
        return "withOutIframe";
    }

    public String withIframe(final TransactionResponse transactionResponse)
            throws Exception {
        String methodForm =
                transactionResponse.getAuthenticationResponse().getSecure3dMethod()
                        .getMethodForm();
        String inputStr = methodForm.substring(methodForm.indexOf("<input"), methodForm.indexOf("<script"));
        log.info("inputStr: {}", inputStr);
        final String threeDSMethodData =
                inputStr.substring(inputStr.indexOf("value=\"") + 7, inputStr.indexOf("\"/"));
        final Map<String, Object> requestMap =
                Map.of("3DSMethodData", threeDSMethodData, "threeDSMethodData", threeDSMethodData);

        String postUrl = methodForm.substring(methodForm.indexOf("action=\"")+8,methodForm.indexOf(" method=\"")-1);
        final String result = HttpUtil.post(postUrl,
                requestMap);
        log.info(result);
        final File file = ResourceUtils.getFile("classpath:templates/withIframe.html");
        boolean createFile = file.delete() ? file.createNewFile() : false;
        FileUtils.writeTxtFile(result, file);
        return "withIframe";
    }

}
