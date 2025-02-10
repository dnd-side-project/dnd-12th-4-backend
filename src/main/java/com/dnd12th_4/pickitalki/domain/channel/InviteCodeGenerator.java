package com.dnd12th_4.pickitalki.domain.channel;

import java.security.SecureRandom;
import java.util.UUID;

public class InviteCodeGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateInviteCode(UUID channelUuid) {
        int randomPart = RANDOM.nextInt(900000) + 100000; // 100000 ~ 999999 (6자리 랜덤)
        int hashPart = Math.abs(channelUuid.hashCode()) % 1000000; // UUID 해시값 활용

        // 두 숫자를 조합 후 6자리 숫자로 변환
        int inviteCode = (randomPart + hashPart) % 1_000_000;
        return String.format("%06d", inviteCode);
    }

}
