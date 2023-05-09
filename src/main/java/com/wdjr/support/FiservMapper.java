package com.wdjr.support;

import java.math.BigDecimal;
import java.util.Random;

import com.github.GBSEcom.model.ACSResponse;
import com.github.GBSEcom.model.Amount;
import com.github.GBSEcom.model.Expiration;
import com.github.GBSEcom.model.InstallmentOptions;
import com.github.GBSEcom.model.Order;
import com.github.GBSEcom.model.PaymentCard;
import com.github.GBSEcom.model.PaymentCardPaymentMethod;
import com.github.GBSEcom.model.PaymentCardPreAuthTransaction;
import com.github.GBSEcom.model.PaymentCardSaleTransaction;
import com.github.GBSEcom.model.PostAuthTransaction;
import com.github.GBSEcom.model.ReturnTransaction;
import com.github.GBSEcom.model.Secure3DAuthenticationRequest;
import com.github.GBSEcom.model.Secure3DAuthenticationUpdateRequest;
import com.github.GBSEcom.model.VoidPreAuthTransactions;
import com.github.GBSEcom.model.VoidTransaction;
import com.wdjr.entity.CardInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

@Import(
        FiservConfig.class
)
@Service
public class FiservMapper {

    private final FiservConfig fiservConfig;

    @Autowired
    FiservMapper(
            final FiservConfig fiservConfig
    ) {
        this.fiservConfig = fiservConfig;
        
    }

    public PaymentCardSaleTransaction toSaleTransactionRequest(final CardInfo cardNo) {
        final PaymentCardSaleTransaction paymentCardSaleTransaction =
                (PaymentCardSaleTransaction) new PaymentCardSaleTransaction()
                        .transactionAmount(toAmount()).
                        storeId(fiservConfig.getStoreId())
                        .requestType(FiservRequestType.CARD_PAYMENT.getTextValue())
                        .order(toOrder());
        paymentCardSaleTransaction.setPaymentMethod(toPaymentCardPaymentMethod(cardNo));
        final Secure3DAuthenticationRequest secure3DAuthenticationRequest =
                new Secure3DAuthenticationRequest()
                        .methodNotificationURL(fiservConfig.getTermURL())
                        .termURL(fiservConfig.getTermURL())
                        .challengeIndicator(
                                Secure3DAuthenticationRequest.ChallengeIndicatorEnum._04);
        secure3DAuthenticationRequest.setAuthenticationType("Secure3DAuthenticationRequest");
        paymentCardSaleTransaction.setAuthenticationRequest(secure3DAuthenticationRequest);
        return paymentCardSaleTransaction;
    }

    public PaymentCardSaleTransaction toSaleTransactionRequest() {
        final PaymentCardSaleTransaction paymentCardSaleTransaction =
                (PaymentCardSaleTransaction) new PaymentCardSaleTransaction()
                        .transactionAmount(toAmount()).
                        storeId(fiservConfig.getStoreId())
                        .requestType(FiservRequestType.CARD_PAYMENT.getTextValue())
                        .order(toOrder());
        paymentCardSaleTransaction.setPaymentMethod(toPaymentCardPaymentMethod());
        final Secure3DAuthenticationRequest secure3DAuthenticationRequest =
                new Secure3DAuthenticationRequest()
                        .methodNotificationURL("https://pengpeng.requestcatcher.com/")
                        .termURL("https://pengpeng.requestcatcher.com/")
                        .challengeIndicator(
                                Secure3DAuthenticationRequest.ChallengeIndicatorEnum._04);
        secure3DAuthenticationRequest.setAuthenticationType("Secure3DAuthenticationRequest");
        paymentCardSaleTransaction.setAuthenticationRequest(secure3DAuthenticationRequest);
        return paymentCardSaleTransaction;
    }

    public PaymentCardPreAuthTransaction toPreAuthTransactionRequest() {
        final PaymentCardPreAuthTransaction paymentCardSaleTransaction =
                (PaymentCardPreAuthTransaction) new PaymentCardPreAuthTransaction()
                        .transactionAmount(toAmount()).storeId(fiservConfig.getStoreId())
                        .requestType(FiservRequestType.CARD_PRE_AUTH.getTextValue())
                        .order(toOrder());
        paymentCardSaleTransaction.setPaymentMethod(toPaymentCardPaymentMethod());
        return paymentCardSaleTransaction;
    }

    public PostAuthTransaction toPostAuthTransactionRequest() {
        PostAuthTransaction postAuthTransaction = (PostAuthTransaction) new PostAuthTransaction()
                .requestType(FiservRequestType.POST_AUTH.getTextValue());
        postAuthTransaction.setTransactionAmount(toAmount());
        return postAuthTransaction;
    }

    public ReturnTransaction toRefundRequest() {
        return (ReturnTransaction) new ReturnTransaction()
                .transactionAmount(toAmount()).requestType(FiservRequestType.RETURN.getTextValue());
    }

    public VoidTransaction toCancelOrderRequest() {
        final VoidTransaction voidTransaction = new VoidTransaction();
        voidTransaction.setRequestType(FiservRequestType.VOID.getTextValue());
//        voidTransaction.setComments("Cancellation Test");
        return voidTransaction;
    }

    public VoidPreAuthTransactions toCancelPreAuthOrderRequest() {
        final VoidPreAuthTransactions cancellationTest =
                (VoidPreAuthTransactions) new VoidPreAuthTransactions()
                        .requestType(FiservRequestType.VOID_PRE_AUTH.getTextValue())
                        .comments("Cancellation Test");
        cancellationTest.setRequestType(FiservRequestType.VOID_PRE_AUTH.getTextValue());
        return cancellationTest;

    }

    private Amount toAmount() {
        return new Amount().currency("MXN")
                .total(new BigDecimal(new Random().nextInt(99)));
    }

    private Order toOrder() {

        return new Order().installmentOptions(
                new InstallmentOptions()
                        .numberOfInstallments(1));
    }

    private PaymentCardPaymentMethod toPaymentCardPaymentMethod(final CardInfo cardInfo) {

        return new PaymentCardPaymentMethod().paymentCard(
                new PaymentCard()
                        .number(cardInfo.getCardNumber())
                        .securityCode(cardInfo.getCvc())
                        .expiryDate(
                                new Expiration()
                                        .month(cardInfo.getExpirationMonth())
                                        .year(cardInfo.getExpirationYear())
                        )
        );
    }

    private PaymentCardPaymentMethod toPaymentCardPaymentMethod() {

        return new PaymentCardPaymentMethod().paymentCard(
                new PaymentCard()
                        .number("4931580001642617")
                        .securityCode("873")
                        .expiryDate(
                                new Expiration()
                                        .month("12")
                                        .year("24")
                        )
        );
    }


    public Secure3DAuthenticationUpdateRequest toSecure3D21AuthenticationUpdateRequest(
            String cRes
    ) {
        final Secure3DAuthenticationUpdateRequest request =
                (Secure3DAuthenticationUpdateRequest) new Secure3DAuthenticationUpdateRequest()
                        .authenticationType("Secure3DAuthenticationUpdateRequest")
                        .storeId(fiservConfig.getStoreId());
        return cRes.contains("cres") ?
                request.acsResponse(new ACSResponse().cRes(cRes.substring(cRes.indexOf("=") + 1))) :
                request.methodNotificationStatus(
                        Secure3DAuthenticationUpdateRequest.MethodNotificationStatusEnum.RECEIVED);
    }
}
