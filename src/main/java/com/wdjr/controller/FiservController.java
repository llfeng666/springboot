package com.wdjr.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.wdjr.entity.CardInfo;
import com.wdjr.support.FiservService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Import(JsonMapper.class)
@Controller
@Slf4j
@RequiredArgsConstructor
public class FiservController {

    private final FiservService fiservService;

    @RequestMapping("/test/{cardNo}")
    public String test3DsByCardNo(@PathVariable String cardNo,Model model) throws Exception {
        return fiservService.create3DsPayIn(cardNo,model);
    }

    @RequestMapping(value="/callback/{idempotencyKey}",method = RequestMethod.POST)
    public String callback(final @RequestBody String payload,
                           @PathVariable String idempotencyKey,
                           final Model model) throws Exception {
        log.info("receive callback: {}", payload);
        final String redirectUrl = fiservService.handleWebhook(payload,model);
        return redirectUrl;
    }


    @RequestMapping(value="/notifiction/{idempotencyKey}",method = RequestMethod.POST)
    public String methodNotifiction(final @RequestBody String payload,
                                    @PathVariable String idempotencyKey,
                                    Model model) throws Exception {
        log.info("receive notifiction: {}", payload);
        final String redirectUrl = fiservService.handleWebhook(payload,model);
        return redirectUrl;
    }

}
