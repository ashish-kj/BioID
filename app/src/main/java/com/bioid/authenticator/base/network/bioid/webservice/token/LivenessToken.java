package com.bioid.authenticator.base.network.bioid.webservice.token;

import android.support.annotation.NonNull;

import com.bioid.authenticator.base.network.bioid.webservice.MovementDirection;

import java.util.Arrays;

/**
 * LivenessToken which can be used for the liveness detection process on the BioID Webservice (BWS).
 */
public class LivenessToken extends BwsToken {
    // use BwsTokenFactory instead
    LivenessToken(JwtParser jwtParser, String token) {
        super(jwtParser, token);
    }

    /**
     * Returns true if Challenge Response should be used for the liveness detection.
     */
    public boolean isChallengeResponse() {
        return challenges != null;
    }

    @NonNull
    public MovementDirection[][] getChallenges() {
        if (challenges == null) {
            throw new IllegalStateException("token does not support challenge-response");
        }
        return challenges;
    }

    @Override
    public String toString() {
        return "LivenessToken{" +
                "token='" + getToken() + '\'' +
                ", expirationTime=" + getExpirationTime() +
                ", maxTries=" + getMaxTries() +
                ", hasFaceTrait=" + hasFaceTrait() +
                ", hasPeriocularTrait=" + hasPeriocularTrait() +
                ", hasVoiceTrait=" + hasVoiceTrait() +
                ", challenges=" + Arrays.deepToString(challenges) +
                '}';
    }
}
