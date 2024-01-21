package galeev.prizeservice.util;

import galeev.prizeservice.entity.Prize;

import java.util.List;

public class PrizeBuilder {
    public static List<Prize> generatePrizes() {
        return List.of(new Prize(null,
                        "Фродооо",
                        "Фродоооо",
                        "AgACAgIAAxkBAAIGCmWcF_ebRR91kuBurqFY6AKYr_t-AAKf0TEbN2HhSIuxMVLkSKqcAQADAgADbQADNAQ",
                        false,
                        true,
                        744875628L),
                new Prize(null,
                        "Гомер табурет",
                        "Гомер",
                        null,
                        true,
                        false,
                        null)
        );
    }
}
