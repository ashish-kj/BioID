package com.bioid.authenticator.base.network.bioid.webservice.token;

/**
 * NoopToken is placeholder for abstract layers where token is required although it isn't important for implementation
 */
public class NoopToken extends BwsToken  {
    NoopToken() {
        super(new JwtParser(), "noopToken");
    }
}
