package com.example.finder.utils.logger;

import com.example.finder.utils.StringUtil;

import java.time.LocalDateTime;
import java.util.Map;


public class Printer {
    private static final Map<String, String> logStarters = Map.of(
            "error", "*****",
            "errorDetail", "=============",
            "trace", ">>>"
    );

    public static void printLog(String message){
        LocalDateTime logDate = LocalDateTime.now();
        String log = StringUtil.concat(
                logStarters.get("trace"),
                logDate.toString(),
                "->",
                message);
        System.out.println(log);
    }

    public static void printErrorLog(Exception e){
        LocalDateTime logDate = LocalDateTime.now();
        String log = StringUtil.concat(
                logStarters.get("error"),
                logDate.toString(),
                "error ->",
                e.getClass().getSimpleName(),
                ":",
                e.getMessage());
        System.err.println(log);
    }

    public static void printErrorLogWithDetails(Exception e){
        LocalDateTime logDate = LocalDateTime.now();
        StackTraceElement origin = e.getStackTrace()[0];

        String logF = StringUtil.concat(
                logStarters.get("error"),
                logDate.toString(),
                "->",
                e.getClass().getSimpleName() + ":",
                "\"" + e.getMessage() + "\"",
                "at",
                origin.getClassName() + "." + origin.getMethodName() + "()",
                "line",
                String.valueOf(origin.getLineNumber())
        );

        String log = StringUtil.concat(
                logDate.toString(),
                "->",
                e.getMessage());

        String details = StringUtil.concat(
                "Caused by :",
                origin.getClassName() + "." + origin.getMethodName() + "()",
                "line",
                String.valueOf(origin.getLineNumber()));


        System.err.println(StringUtil.concat(logStarters.get("error"), "ERROR SUMMARY", logStarters.get("error")));
        System.err.println(logStarters.get("errorDetail"));

        System.err.println(log);
        System.err.println(details);
        System.err.println(logStarters.get("errorDetail"));
    }
}
