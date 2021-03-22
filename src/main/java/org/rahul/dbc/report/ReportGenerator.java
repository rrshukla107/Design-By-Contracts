package org.rahul.dbc.report;

import org.rahul.dbc.engine.ChainResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ReportGenerator {

    String generatePreConditionReport(Map<String, CompletableFuture<ChainResult>> resultMappings, Double timeInMillis);

    String generatePostConditionReport(Map<String, CompletableFuture<ChainResult>> resultMappings, Double timeInMillis);
}
