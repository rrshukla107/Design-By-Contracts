package org.rahul.dbc.engine;

import java.util.Optional;

public interface ChainResult {

    boolean getResult();

    boolean hasFailed();

    boolean isSuccessful();

    Optional<Throwable> getUnderlyingException();

}
