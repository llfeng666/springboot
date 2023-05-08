package com.wdjr.controller;

import com.wdjr.support.FiservService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@Slf4j
@RequiredArgsConstructor
public class FiservController {

    private final FiservService fiservService;

    @RequestMapping("/test/{cardNo}")
    public String test3DsByCardNo(@PathVariable String cardNo,Model model) throws Exception {
       return fiservService.create3DsPayIn(cardNo,model);
    }

    @RequestMapping(value="/webhook",method = RequestMethod.POST)
    public String update3Decure(final @RequestBody String payload,Model model) throws Exception {
        log.info("receive webhook: {}", payload);
        final String redirectUrl = fiservService.handleWebhook(payload,model);
        return redirectUrl;
    }

}
