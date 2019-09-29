package com.zpj.arithmetic_gui;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Z-P-J
 */
public class ArithmeticBean {

    private final List<String> arithmeticList = new ArrayList<>();
    private final List<String> resultList = new ArrayList<>();

    private int operandCount;
    private int questionCount = 10;
    private boolean enableBracket = false;
    private boolean enableExactDivision = false;
    private boolean equalProbability = true;
    private int minNum = 0;
    private int maxNum = 100;
    private int minIntermediateResult = Integer.MIN_VALUE;
    private int maxIntermediateResult = Integer.MAX_VALUE;
    private int minFinalResult = Integer.MIN_VALUE;
    private int maxFinalResult = Integer.MAX_VALUE;
    private String docStr;

    public void addArithmetic(String arithmetic) {
        arithmeticList.add(arithmetic);
    }

    public void addResult(String result) {
        resultList.add(result);
    }

    public List<String> getArithmeticList() {
        return arithmeticList;
    }

    public List<String> getResultList() {
        return resultList;
    }

    public void setDocStr(String docStr) {
        this.docStr = docStr;
    }

    public String getDocStr() {
        return docStr;
    }

    public int getOperandCount() {
        return operandCount;
    }

    public void setOperandCount(int operandCount) {
        this.operandCount = operandCount;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public boolean isEnableBracket() {
        return enableBracket;
    }

    public void setEnableBracket(boolean enableBracket) {
        this.enableBracket = enableBracket;
    }

    public boolean isEnableExactDivision() {
        return enableExactDivision;
    }

    public void setEnableExactDivision(boolean enableExactDivision) {
        this.enableExactDivision = enableExactDivision;
    }

    public boolean isEqualProbability() {
        return equalProbability;
    }

    public void setEqualProbability(boolean equalProbability) {
        this.equalProbability = equalProbability;
    }

    public int getMinNum() {
        return minNum;
    }

    public void setMinNum(int minNum) {
        this.minNum = minNum;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public int getMinIntermediateResult() {
        return minIntermediateResult;
    }

    public void setMinIntermediateResult(int minIntermediateResult) {
        this.minIntermediateResult = minIntermediateResult;
    }

    public int getMaxIntermediateResult() {
        return maxIntermediateResult;
    }

    public void setMaxIntermediateResult(int maxIntermediateResult) {
        this.maxIntermediateResult = maxIntermediateResult;
    }

    public int getMinFinalResult() {
        return minFinalResult;
    }

    public void setMinFinalResult(int minFinalResult) {
        this.minFinalResult = minFinalResult;
    }

    public int getMaxFinalResult() {
        return maxFinalResult;
    }

    public void setMaxFinalResult(int maxFinalResult) {
        this.maxFinalResult = maxFinalResult;
    }

    @Override
    public ArithmeticBean clone() {
        ArithmeticBean bean = new ArithmeticBean();
        for (String arithmetic : arithmeticList) {
            bean.addArithmetic(arithmetic);
        }
        for (String result : resultList) {
            bean.addResult(result);
        }
        bean.setDocStr(docStr);
        bean.setEnableBracket(enableBracket);
        bean.setEnableExactDivision(enableExactDivision);
        bean.setEqualProbability(equalProbability);
        bean.setOperandCount(operandCount);
        bean.setQuestionCount(questionCount);
        bean.setMinNum(minNum);
        bean.setMaxNum(maxNum);
        bean.setMinIntermediateResult(minIntermediateResult);
        bean.setMaxIntermediateResult(maxIntermediateResult);
        bean.setMinFinalResult(minFinalResult);
        bean.setMaxFinalResult(maxFinalResult);
        return bean;
    }
}
