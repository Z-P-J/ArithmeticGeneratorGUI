package com.zpj.arithmetic;

import com.zpj.arithmetic.base.Operable;
import com.zpj.arithmetic.base.Operand;
import com.zpj.arithmetic.base.Operator;
import com.zpj.arithmetic.impl.ArithmeticOperator;
import com.zpj.arithmetic.impl.BracketOperator;
import com.zpj.arithmetic.impl.IntegerOperand;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 算式生成器
 * @author Z-P-J
 */
public class ArithmeticGenerator {

    private final List<ArithmeticOperator> operatorList = new ArrayList<>();
    private final List<Integer> integers = new ArrayList<>();
    private int operandCount;
    private int questionCount = 10;
    private boolean limitFinalResult = false;
    private boolean limitIntermediateResult = false;
    private boolean enableBracket = false;
    private boolean enableExactDivision = false;
    private boolean equalProbability = true;
    private boolean debug = false;
    private int minNum = 0;
    private int maxNum = 100;
    private int minIntermediateResult = Integer.MIN_VALUE;
    private int maxIntermediateResult = Integer.MAX_VALUE;
    private int minFinalResult = Integer.MIN_VALUE;
    private int maxFinalResult = Integer.MAX_VALUE;
    private OnGenerateArithmeticListener listener;

    public interface OnGenerateArithmeticListener{
        /**
         * 生成一条算式时回调的监听器
         * @param index index
         * @param arithmetic the arithmetic formula
         * @param result 计算结果
         */
        void onGenerate(int index, String arithmetic, String result);
    }

    private ArithmeticGenerator() {

    }

    public static ArithmeticGenerator with() {
        return new ArithmeticGenerator();
    }

    /**
     * 设置题目中运算数个数
     * @param operandCount 运算数个数
     * @return this
     */
    public ArithmeticGenerator setOperandCount(int operandCount) {
        if (operandCount < 2) {
            throw new RuntimeException("运算数个数不能低于2个");
        }
        this.operandCount = operandCount;
        return this;
    }

    /**
     * 设置生成题目数量
     * @param questionCount 题目数量
     * @return this
     */
    public ArithmeticGenerator setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
        return this;
    }

    /**
     * 算式中是否可以包含括号
     * @param enableBracket 是否启用括号
     * @return this
     */
    public ArithmeticGenerator setEnableBracket(boolean enableBracket) {
        this.enableBracket = enableBracket;
        return this;
    }

    /**
     * 是否允许整除
     * @param enableExactDivision 是否整除
     * @return this
     */
    public ArithmeticGenerator setEnableExactDivision(boolean enableExactDivision) {
        this.enableExactDivision = enableExactDivision;
        return this;
    }

    /**
     * 算术运算符是否等概率出现
     * @param equalProbability 是否等概率
     * @return this
     */
    public ArithmeticGenerator setEqualProbability(boolean equalProbability) {
        this.equalProbability = equalProbability;
        return this;
    }

    /**
     * 设置生成题目中数字的范围
     * @param min 最小值
     * @param max 最大值
     * @return this
     */
    public ArithmeticGenerator setNumRange(int min, int max) {
        this.minNum = min;
        this.maxNum = max;
        return this;
    }

    /**
     * 设置中间计算结果范围
     * @param min 最小值
     * @param max 最大值
     * @return this
     */
    public ArithmeticGenerator setIntermediateResultRange(int min, int max) {
        limitIntermediateResult = true;
        this.minIntermediateResult = min;
        this.maxIntermediateResult = max;
        return this;
    }

    /**
     * 设置最终计算结果范围
     * @param minFinalResult 最小值
     * @param maxFinalResult 最大值
     * @return this
     */
    public ArithmeticGenerator setFinalResultRange(int minFinalResult, int maxFinalResult) {
        limitFinalResult = true;
        this.minFinalResult = minFinalResult;
        this.maxFinalResult = maxFinalResult;
        return this;
    }

    /**
     * 添加运算符
     * @param operator 操作符
     * @return this
     */
    public ArithmeticGenerator addOperator(ArithmeticOperator operator) {
        if (!this.operatorList.contains(operator)) {
            this.operatorList.add(operator);
        }
        return this;
    }

    /**
     * 设置多个操作符
     * @param operators 操作符列表
     * @return this
     */
    public ArithmeticGenerator addOperators(List<ArithmeticOperator> operators) {
        for (ArithmeticOperator operator : operators) {
            addOperator(operator);
        }
        return this;
    }

    /**
     * 设置监听器
     * @param listener 监听器
     * @return this
     */
    public ArithmeticGenerator setListener(OnGenerateArithmeticListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 是否开启debug模式
     * @param debug 是否开启debug
     * @return this
     */
    public ArithmeticGenerator setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    /**
     * 启动算式生成器
     */
    public void start() {
        System.out.println("算式生成器启动");
        if (operatorList.isEmpty()) {
            throw new RuntimeException("请添加算术运算符");
        }
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < questionCount; i++) {
            println("\n------开始生成算式" + i + "------");
            if (listener != null) {
//                Calculator calculator = new Calculator();
                String[] results = generateArithmetic2();
                listener.onGenerate(i, results[0], results[1]);
            }
            println("------结束生成算式" + i + "------");
        }

        long finishTime = System.currentTimeMillis();
        System.out.println(questionCount + "个算式生成完毕，共花费：" + (finishTime - startTime) + "ms");
    }

    /**
     *  生成算式2
     * @return String[]
     */
    private String[] generateArithmetic2() {

        BinaryArithmeticTree tree = new BinaryArithmeticTree();
        BinaryArithmeticNode root = new BinaryArithmeticNode(getRandomOperand(), getRandomOperator());
        tree.setRoot(root);

        int count = 1;
        LinkedList<BinaryArithmeticNode> queue = new LinkedList<>();
        queue.push(root);
        while (count != operandCount) {
            BinaryArithmeticNode currentNode = queue.getFirst();
            ArithmeticOperator operator = currentNode.getOperator();
            IntegerOperand operand = currentNode.getOperand();

            BinaryArithmeticNode left = new BinaryArithmeticNode();
            BinaryArithmeticNode right = new BinaryArithmeticNode();
            left.setOperator(getRandomOperator());
            right.setOperator(getRandomOperator());
            int num = operand.getNum();
            int max = maxNum, min = minNum;
            int leftNum = 0;
            int rightNum = 0;
            if (ArithmeticOperator.ADDITION == operator) {
                if (min >= 0) {
                    if (max < 2 * min || num < 2 * min) {
                        currentNode.setOperator(getRandomOperator());
                        continue;
                    }
                    max = num - min;
                } else if (max <= 0) {
                    if (min > 2 * max || num > 2 * max) {
                        currentNode.setOperator(getRandomOperator());
                        continue;
                    }
                    min = num - max;
                } else {
                    if (num >= max + min) {
                        min = num - max;
                    } else {
                        max = num - min;
                    }
                }
                leftNum = (int) (Math.random() * (max - min)) + min;
                rightNum = num - leftNum;
            } else if (ArithmeticOperator.SUBTRACTION == operator){
                if (min >= 0) {
                    if (max < 2 * min || num > max - min) {
                        currentNode.setOperator(getRandomOperator());
                        continue;
                    }
                    max = max - num;
                } else if (max <= 0) {
                    if (min > 2 * max || num < min - max) {
                        currentNode.setOperator(getRandomOperator());
                        continue;
                    }
                    min = min - num;
                } else {
                    if (num >= 0) {
                        max = max - num;
                    } else {
                        min = min - num;
                    }
                }
                rightNum = (int) (Math.random() * (max - min)) + min;
                leftNum = num + rightNum;
            } else if (ArithmeticOperator.MULTIPLICATION == operator){
                List<Integer> integers = new ArrayList<>();
                for (int i = minNum; i < num; i++) {
                    if (i == 0 || i == 1) {
                        continue;
                    }
                    int res = num / i;
                    if (num % i == 0 && res >= minNum && res <= minNum) {
                        integers.add(i);
                    }
                }
                if (integers.isEmpty()) {
//                    leftNum = 1;
                    currentNode.setOperator(getRandomOperator());
                    continue;
                } else {
                    leftNum = integers.get((int) (Math.random() * integers.size()));
                }
                rightNum = num / leftNum;
            } else if (ArithmeticOperator.DIVISION == operator){
                if (num == 0) {
                    currentNode.setOperator(getRandomOperator());
                    continue;
                }
                List<Integer> integers = new ArrayList<>();
                for (int i = minNum; i <= maxNum; i++) {
                    if (i == 0) {
                        continue;
                    }
                    int res = i * num;
                    if (res >= minNum && res <= maxNum) {
                        integers.add(i);
                    }
                }
                if (integers.isEmpty()) {
                    rightNum = 1;
                } else {
                    rightNum = integers.get((int) (Math.random() * integers.size()));
                }
                leftNum = num * rightNum;
            }
            queue.pop();
            left.setOperand(new IntegerOperand(leftNum));
            right.setOperand(new IntegerOperand(rightNum));
            currentNode.setLeftNode(left);
            currentNode.setRightNode(right);

            queue.push(left);
            queue.push(right);

            count++;
        }

        String[] results = new String[2];
        results[0] = tree.getArithmetic();
        results[1] = tree.getRoot().getOperand().toString();
        return results;
    }

    /**
     * 生成算式
     * @return 算式的字符串
     */
    private String[] generateArithmetic() {
        integers.clear();
        Calculator calculator = new Calculator();
        calculator.setEnableExactDivision(enableExactDivision);
        ArithmeticOperator preOperator = null;
        IntegerOperand preOperand = null;
        for (int i = 0; i < operandCount; i++) {
            IntegerOperand operand = null;
            println("preOperand=" + preOperand + " preOperator=" + preOperator);
            if (preOperand != null && preOperator != null) {
                if (preOperator.equal(ArithmeticOperator.DIVISION)) {
                    operand = getDivisor(preOperand.getNum());
                    if (operand == null) {
                        return generateArithmetic();
                    }
                } else if (preOperator.equal(ArithmeticOperator.MOD)) {
                    do {
                        operand = getRandomOperand();
                    } while (operand.getNum() == 0);
                }

            }
            if (operand == null) {
                operand = getRandomOperand();
            }
            calculator.addOperable(operand);
            preOperand = operand;

            if (i != operandCount - 1) {
                ArithmeticOperator operator = getRandomOperator();
                calculator.addOperable(operator);
                preOperator = operator;
            }
        }

        if (enableBracket) {
            generateBrackets(calculator);
        }

        if (limitIntermediateResult) {
            calculator.setListener(new Calculator.OnCalculateListener() {
                @Override
                public boolean onIntermediateCal(int intermediateValue) {
                    return intermediateValue > maxIntermediateResult || intermediateValue < minIntermediateResult;
                }
            });
        }

        if (limitFinalResult) {
            int result = calculator.calculate();
            if (!calculator.isCalculated()
                    || result > maxFinalResult
                    || result < minFinalResult) {
                return generateArithmetic();
            }

        }
        String[] results = new String[2];
        results[0] = calculator.getArithmetic();
        results[1] = String.valueOf(calculator.calculate());
        return results;
    }

    /**
     * 随机获取一个操作数对象
     * @return 操作数对象
     */
    private IntegerOperand getRandomOperand() {
        return new IntegerOperand((int) (Math.random() * (maxNum - minNum)) + minNum);
    }

    /**
     * 随机生成除数
     * @param dividend 被除数
     * @return 除数
     */
    private IntegerOperand getDivisor(int dividend) {
        if (enableExactDivision) {
            if (dividend > maxNum) {
                dividend = maxNum;
            } else if (dividend <= 1) {
                return new IntegerOperand(1);
            }
            List<Integer> integers = new ArrayList<>();
            for (int i = 1; i < dividend; i++) {
                if (dividend % i == 0) {
                    integers.add(i);
                }
            }
            // 这里是避免被除数是质数
            if (integers.size() == 1) {
                return null;
            }
            return new IntegerOperand(integers.get((int) (Math.random() * integers.size())));
        } else {
            return getRandomOperand();
        }
    }

    /**
     * 随机生成算术运算符
     * @return 算术运算符
     */
    private ArithmeticOperator getRandomOperator() {
        if (equalProbability) {
            if (integers.isEmpty()) {
                for (int i = 0; i < operatorList.size(); i++) {
                    integers.add(i);
                }
            }
            int index = integers.remove((int) (Math.random() * integers.size()));
            return operatorList.get(index);
        }
        return operatorList.get((int) (Math.random() * operatorList.size()));
    }

    /**
     *
     */
    class Pair{
        int left;
        int right;
        Operable startOperable;
        Operable endOperable;
        Pair(int left, int right) {
            this.left = left;
            this.right = right;
        }

        boolean check(int left, int right) {
            if (this.left >= right) {
                return false;
            }
            if (this.right <= left) {
                return false;
            }
            if (this.left == left && this.right == right) {
                return true;
            }
            if (this.left == left && this.right < right) {
                return false;
            }
            if (this.left > left && this.right == right) {
                return false;
            }
            if (this.left <= left && this.right >= right) {
                return false;
            }
            return true;
        }
    }

    /**
     * 随机给算式生成括号
     * @param calculator 计算器对象
     */
    private void generateBrackets(Calculator calculator) {
        int count = (int) (Math.random() * (operandCount - 1));
        if (count > 0) {
            LinkedList<Operable> operableList = calculator.getOperableList();
            List<Pair> pairs = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                int leftPosition = (int) (Math.random() * (operandCount - 1));
                int rightPosition = (int) (Math.random() * (operandCount - leftPosition - 1)) + leftPosition + 2;
                if ((rightPosition - leftPosition) == operandCount) {

                } else {
                    boolean flag = true;
                    for (Pair pair : pairs) {
                        if (pair.check(leftPosition, rightPosition)) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        Pair newPair = new Pair(leftPosition, rightPosition);
                        newPair.startOperable = operableList.get(leftPosition * 2);
                        newPair.endOperable = operableList.get(2 * rightPosition - 2);
                        pairs.add(newPair);
                    }
                }
            }
            for (Pair pair : pairs) {
                Operable startOperable = pair.startOperable;
                Operable endOperable = pair.endOperable;
                operableList.add(operableList.indexOf(endOperable) + 1, BracketOperator.RIGHT_BRACKET);
                operableList.add(operableList.indexOf(startOperable), BracketOperator.LEFT_BRACKET);
            }
            calculator.resetArithmetic();
        }
    }

    private void println(String str) {
        if (debug) {
            System.out.println(str);
        }
    }

}
