package galeev.prizeservice.util;

import galeev.prizeservice.entity.Prize;

import java.util.List;
import java.util.UUID;

public class PrizeBuilder {
    public static List<Prize> generatePrizes() {
        return List.of(new Prize(UUID.fromString("f0f920f1-8687-42af-a0ad-e361b29dad1f"),
                        "Фродооо",
                        "Фродоооо",
                        "AgACAgIAAxkBAAIGCmWcF_ebRR91kuBurqFY6AKYr_t-AAKf0TEbN2HhSIuxMVLkSKqcAQADAgADbQADNAQ",
                        false,
                        true,
                        744875628L),
                new Prize(UUID.fromString("806bc015-11d1-49ac-b018-6ef780b09f66"),
                        "Гомер табурет",
                        "Гомер",
                        "CgACAgIAAxkBAAIGEmWcGIRq4hyBIzOhkTo_9JQc6NBSAAI4OwACN2HhSHg_xoRFd--UNAQ",
                        true,
                        true,
                        744875628L)
        );
    }
}
