package org.rahul.dbc.report;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.rahul.dbc.engine.ChainResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TextReportGenerator implements ReportGenerator {

    public static final String ANSI_RESET = "\u001B[0m";


    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_CYAN = "\u001B[36m";


    public static final String ANSI_BRIGHT_RED = "\u001B[91m";
    public static final String ANSI_BRIGHT_GREEN = "\u001B[92m";
    public static final String ANSI_BRIGHT_YELLOW = "\u001B[93m";
    public static final String ANSI_BRIGHT_BLUE = "\u001B[94m";
    public static final String ANSI_BRIGHT_WHITE = "\u001B[97m";
    public static final String ANSI_BRIGHT_CYAN = "\u001B[96m";


    public static final String ANSI_BG_RED = "\u001B[41m";
    public static final String ANSI_BG_GREEN = "\u001B[42m";
    public static final String ANSI_BG_YELLOW = "\u001B[43m";
    public static final String ANSI_BG_BLUE = "\u001B[44m";
    public static final String ANSI_BG_PURPLE = "\u001B[45m";
    public static final String ANSI_BG_WHITE = "\u001B[47m";

    public static final String ANSI_BRIGHT_BG_BLACK = "\u001B[100m";
    public static final String ANSI_BRIGHT_BG_RED = "\u001B[101m";
    public static final String ANSI_BRIGHT_BG_GREEN = "\u001B[102m";
    public static final String ANSI_BRIGHT_BG_YELLOW = "\u001B[103m";
    public static final String ANSI_BRIGHT_BG_BLUE = "\u001B[104m";
    public static final String ANSI_BRIGHT_BG_PURPLE = "\u001B[105m";
    public static final String ANSI_BRIGHT_BG_CYAN = "\u001B[106m";
    public static final String ANSI_BRIGHT_BG_WHITE = "\u001B[107m";

    public static final String CHAIN_NAME = ANSI_BRIGHT_YELLOW + "CHAIN NAME - " + ANSI_RESET;
    public static final String STATUS = ANSI_BRIGHT_YELLOW + "STATUS - " + ANSI_RESET;

    public static final String PASS = ANSI_BG_GREEN + ANSI_BRIGHT_WHITE + "PASS" + ANSI_RESET;
    public static final String FAIL = ANSI_BG_RED + ANSI_BRIGHT_WHITE + "FAIL" + ANSI_RESET;

    public static final String PRE_CONDITION = ANSI_BRIGHT_CYAN + "PRE-CONDITIONS (%.2f milliseconds)" + ANSI_RESET;

    public static final String CONTRACT_NAME = ANSI_BRIGHT_BLUE + "Contract Name" + ANSI_RESET;
    public static final String CONTRACT_STATUS = ANSI_BRIGHT_BLUE + "Status" + ANSI_RESET;
    public static final String EXECUTION_TIME = ANSI_BRIGHT_BLUE + "Execution Time(mills)" + ANSI_RESET;
    public static final String PIPE = "|";

    public static final String CONTRACT_PASS = ANSI_BRIGHT_GREEN + "PASS" + ANSI_RESET;
    public static final String CONTRACT_FAIL = ANSI_BRIGHT_RED + "FAIL" + ANSI_RESET;

    public static final String UNDERLYING_EXCEPTION = ANSI_BRIGHT_BG_YELLOW +
            ANSI_BRIGHT_RED + "Failed Due to Underlying Exception - " + ANSI_RESET;


    public static final String POST_CONDITION = ANSI_BRIGHT_CYAN + "PRE-CONDITIONS (%.2f milliseconds)" + ANSI_RESET;


    @Override
    public String generatePreConditionReport(Map<String, CompletableFuture<ChainResult>> resultMappings, Double timeInMillis) {

        StringBuilder result = new StringBuilder();

        this.createPreConditionHeader(timeInMillis, result, PRE_CONDITION);
        this.createContractResult(resultMappings, result);

        return result.toString();
    }

    @Override
    public String generatePostConditionReport(Map<String, CompletableFuture<ChainResult>> resultMappings, Double timeInMillis) {
        StringBuilder result = new StringBuilder();

        this.createPreConditionHeader(timeInMillis, result, POST_CONDITION);
        this.createContractResult(resultMappings, result);

        return result.toString();
    }


    private void createPreConditionHeader(Double timeInMillis, StringBuilder result, String header) {
        result.append("\n");
        result.append("-----------------------------------------------------------------------\n");
        result.append(StringUtils.center(String.format(PRE_CONDITION, timeInMillis), 70));
        result.append("\n");
        result.append("-----------------------------------------------------------------------\n");
    }

    private void createContractResult(Map<String, CompletableFuture<ChainResult>> resultMappings, StringBuilder result) {
        resultMappings.entrySet().forEach(entry -> {
            ChainResult chainResult = this.getChainResult(entry);
            String chainName = entry.getKey();

            result.append(this.getChainStatus(chainResult, chainName));

            result.append("\n\n");
            result.append(this.getChainDetails(chainResult));
            result.append("\n\n");
        });
    }

    private StringBuilder getChainDetails(ChainResult chainResult) {

        StringBuilder result = new StringBuilder();

        //header
        this.appendHeader(result);

        chainResult.getExecutionTimes().forEach((k, v) -> {

            result.append(PIPE);
            result.append(this.contractNameColumn(this.getBrightWhiteText(k)));
            result.append(PIPE);
            result.append(this.contractStatusColumn(this.getStatusOfContract(k, chainResult)));
            result.append(PIPE);
            result.append(this.contractExecutionTime(this.getBrightWhiteText(Double.toString(v))));
            result.append(PIPE);

            result.append("\n");
        });

        chainResult.getUnderlyingException().ifPresent(ex -> {
            result.append(UNDERLYING_EXCEPTION);
            result.append("\n");
            result.append(ANSI_BRIGHT_RED + ANSI_BRIGHT_BG_WHITE +
                    ExceptionUtils.getStackTrace(ex) + ANSI_RESET);
            result.append("\n");
        });

        return result;
    }

    private String getStatusOfContract(String contractName, ChainResult chainResult) {

        if (chainResult.getFailedContractName()
                .map(failedContractName -> failedContractName.equalsIgnoreCase(contractName))
                .orElse(false)) {
            return CONTRACT_FAIL;
        }

        return CONTRACT_PASS;

    }

    private void appendHeader(StringBuilder result) {
        result.append(PIPE);
        result.append(this.contractNameColumn(CONTRACT_NAME));
        result.append(PIPE);
        result.append(this.contractStatusColumn(CONTRACT_STATUS));
        result.append(PIPE);
        result.append(this.contractExecutionTime(EXECUTION_TIME));
        result.append(PIPE);
        result.append("\n");
        result.append(StringUtils.rightPad("", 97 - 25, "-"));

        result.append("\n");
    }

    private String contractNameColumn(String contractName) {
        return StringUtils.rightPad(contractName, 50);
    }

    private String contractStatusColumn(String contractStatus) {
        return StringUtils.center(contractStatus, 15);
    }

    private String contractExecutionTime(String executionTime) {
        return StringUtils.leftPad(executionTime, 30);

    }

    private StringBuilder getChainStatus(ChainResult chainResult, String chainName) {
        StringBuilder result = new StringBuilder();

        result.append(StringUtils.leftPad(CHAIN_NAME, 20));
        result.append(StringUtils.rightPad(this.getBrightWhiteText(chainName), 40));

        result.append(StringUtils.leftPad(STATUS, 5));

        if (chainResult.isSuccessful()) {
            result.append(StringUtils.rightPad(PASS, 4));

        } else {
            result.append(StringUtils.rightPad(FAIL, 4));
        }

        return result;
    }

    private String getBrightWhiteText(String chainName) {
        return ANSI_BRIGHT_WHITE + chainName + ANSI_RESET;
    }

    private ChainResult getChainResult(Map.Entry<String, CompletableFuture<ChainResult>> entry) {
        ChainResult chainResult = null;
        try {
            chainResult = entry.getValue().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chainResult;
    }

}
