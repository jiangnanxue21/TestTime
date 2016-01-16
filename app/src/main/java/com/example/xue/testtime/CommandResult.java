package com.example.xue.testtime;

/**
 * Created by xue on 16/1/16.
 */
public class CommandResult {
    public static final int EXIT_VALUE_TIMEOUT=-1;

    private String output;
    void setOutput(String error) {
        output=error;
    }

    String getOutput(){
        return output;
    }

    int exitValue;

    void setExitValue(int value) {
        exitValue=value;
    }

    int getExitValue(){
        return exitValue;
    }

    private String error;


    public String getError() {
        return error;
    }


    public void setError(String error) {
        this.error = error;
    }
}
