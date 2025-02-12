package com.dnd12th_4.pickitalki.domain.channel;

import java.util.Arrays;

public enum ChannelMemberLevel {
    LV1(1, "level1.png"),
    LV2(2, "level2.png"),
    LV3(3, "level3.png"),
    LV4(4, "level4.png"),
    LV5(5, "level5.png");

    private final int level;
    private final String imageUrl;

    ChannelMemberLevel(int level, String imageUrl) {
        this.level = level;
        this.imageUrl = imageUrl;
    }

    public static String getImageByLevel(int memberLevel) {
        return Arrays.stream(ChannelMemberLevel.values())
                .filter(lv -> lv.level == memberLevel)
                .findFirst()
                .map(lv -> "/images/" + lv.imageUrl)
                .orElse("/images/default.png");
    }
}
