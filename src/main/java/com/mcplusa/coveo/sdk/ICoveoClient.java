package com.mcplusa.coveo.sdk;

import java.io.IOException;

public interface ICoveoClient {
    public CoveoResponse ping() throws IOException;
}
