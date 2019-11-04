package com.zpj.arithmetic_gui;

import com.jfoenix.controls.*;
import com.jfoenix.controls.JFXPopup.PopupHPosition;
import com.jfoenix.controls.JFXPopup.PopupVPosition;
import com.zpj.arithmetic.ArithmeticGenerator;
import com.zpj.arithmetic.impl.ArithmeticOperator;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import javax.swing.*;

/**
 * ViewController
 * MainApplication的视图控制器
 * @author Z-P-J
 * value: fxml文件的路径
 * title: 标题
 */
@ViewController(value = "/fxml/Main.fxml", title = "标题")
public final class MainController implements EventHandler<MouseEvent> {

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private StackPane root;

    @FXML
    private BorderPane borderPane;

    @FXML
    private StackPane titleBurgerContainer;
    @FXML
    private JFXHamburger titleBurger;

    @FXML
    private StackPane optionsBurger;

    @FXML
    private JFXRippler optionsRippler;

    private JFXPopup toolbarPopup;

    @FXML
    private JFXTextField operandCountText;

    @FXML
    private JFXTextField questionCountText;

    @FXML
    private TextField minNumRange;

    @FXML
    private TextField maxNumRange;

    @FXML
    private TextField minIntermediateResultRange;

    @FXML
    private TextField maxIntermediateResultRange;

    @FXML
    private TextField minFinalResultRange;

    @FXML
    private TextField maxFinalResultRange;

    @FXML
    private JFXToggleButton enableBracketButton;

    @FXML
    private JFXToggleButton enableExactDivisionButton;

    @FXML
    private JFXToggleButton equalProbabilityButton;

    @FXML
    private JFXButton generateButton;

    private final ArithmeticBean arithmeticBean = new ArithmeticBean();


    /**
     * init fxml when loaded.
     * @throws Exception 抛出的异常
     */
    @PostConstruct
    public void init() throws Exception {
        StageManager.putController(getClass(), this);

        enableBracketButton.setSelected(true);
        enableExactDivisionButton.setSelected(true);
        equalProbabilityButton.setSelected(true);
        generateButton.setOnMouseClicked(this);

        //加载fxml文件，初始化设置菜单
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainPopup.fxml"));
        loader.setController(new InputController());
        toolbarPopup = new JFXPopup(loader.load());

        optionsBurger.setOnMouseClicked(e ->
            toolbarPopup.show(optionsBurger,
                PopupVPosition.TOP,
                PopupHPosition.RIGHT,
                -12,
                15)
        );
    }

    /**
     * 处理鼠标点击事件，开始生成算术题
     * @param event 鼠标事件MouseEvent{@link MouseEvent}
     */
    @Override
    public void handle(MouseEvent event) {
        if (checkInput()) {
            initArithmeticBean();
            AlertDialog dialog = AlertDialog.with((Stage) generateButton.getScene().getWindow());
            JFXSpinner spinner = new JFXSpinner();
            int questionCount = Integer.valueOf(questionCountText.getText()) - 1;
            long tempTime = System.currentTimeMillis();
            Thread thread = new Thread(new ArithmeticGeneratorRunnable(new ArithmeticGenerator.OnGenerateArithmeticListener() {

                long deltaTime;

                @Override
                public void onGenerate(int index, String arithmetic, String result) {
                    if (index == questionCount) {
                        deltaTime = System.currentTimeMillis() - tempTime;
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            spinner.setProgress((double) index / (double) questionCount);
                        }
                    });
                    System.out.println(arithmetic + result);
                    arithmeticBean.addArithmetic(arithmetic);
                    arithmeticBean.addResult(result);
                    if (index == questionCount && !dialog.isClosed()) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                dialog.close();
                                AlertDialog.with((Stage) generateButton.getScene().getWindow())
                                        .setTitle("算式生成完成")
                                        .setBody("用时：" + deltaTime + "ms")
                                        .addActionButton("确定", new AlertDialog.OnClickListener() {
                                            @Override
                                            public void onClick(AlertDialog dialog, MouseEvent event) {
                                                dialog.close();
                                                try {
                                                    new PreviewApplication().start(new Stage());
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        })
                                        .show();
                            }
                        });
                    }
                }
            }));

            dialog.setTitle("生成算式中。。。")
                    .setBody(spinner)
                    .addActionButton("取消", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(AlertDialog dialog, MouseEvent event) {
                            thread.interrupt();
                            dialog.hideWithAnimation();
                        }
                    })
                    .show();
            thread.start();
        }
    }

    /**
     * 初始化arithmeticBean {@link ArithmeticBean}
     */
    private void initArithmeticBean() {
        arithmeticBean.setOperandCount(Integer.valueOf(operandCountText.getText()));
        arithmeticBean.setQuestionCount(Integer.valueOf(questionCountText.getText()));
        arithmeticBean.setMinNum(Integer.valueOf(minNumRange.getText()));
        arithmeticBean.setMaxNum(Integer.valueOf(maxNumRange.getText()));
        arithmeticBean.setMinIntermediateResult(Integer.valueOf(minIntermediateResultRange.getText()));
        arithmeticBean.setMaxIntermediateResult(Integer.valueOf(maxIntermediateResultRange.getText()));
        arithmeticBean.setMinFinalResult(Integer.valueOf(minFinalResultRange.getText()));
        arithmeticBean.setMaxFinalResult(Integer.valueOf(maxFinalResultRange.getText()));
        arithmeticBean.setEnableBracket(enableBracketButton.isSelected());
        arithmeticBean.setEnableExactDivision(enableExactDivisionButton.isSelected());
        arithmeticBean.setEqualProbability(equalProbabilityButton.isSelected());
    }

    /**
     * 当开始生成算术题前检测用户输入是否正确
     * @return 用户输入参数是否有效
     */
    private boolean checkInput() {
        if (!NumUtil.isInteger(operandCountText.getText())) {
            showInvalidParamsAlertDialog("请检测输入的运算符数量！");
            return false;
        } else if (Integer.valueOf(operandCountText.getText()) < 2) {
            showInvalidParamsAlertDialog("运算符数量必须不小于2！");
            return false;
        }
        if (!NumUtil.isInteger(questionCountText.getText())) {
            showInvalidParamsAlertDialog("请检测输入的问题数量！");
            return false;
        } else if (Integer.valueOf(questionCountText.getText()) < 1) {
            showInvalidParamsAlertDialog("生成的算术题数量必须不小于1！");
            return false;
        }
        if (!NumUtil.isInteger(minNumRange.getText())
                || !NumUtil.isInteger(maxNumRange.getText())) {
            showInvalidParamsAlertDialog("请检测输入的运算数范围！");
            return false;
        } else if (Integer.valueOf(minNumRange.getText())
                >= Integer.valueOf(maxNumRange.getText())) {
            showInvalidParamsAlertDialog("运算数范围最大值必须大于最小值！");
            return false;
        }
        if (!NumUtil.isInteger(minIntermediateResultRange.getText())
                || !NumUtil.isInteger(maxIntermediateResultRange.getText())) {
            showInvalidParamsAlertDialog("请检测输入的中间计算结果范围！");
            return false;
        } else if (Integer.valueOf(minIntermediateResultRange.getText())
                >= Integer.valueOf(maxIntermediateResultRange.getText())) {
            showInvalidParamsAlertDialog("中间计算结果范围最大值必须大于最小值！");
            return false;
        }
        if (!NumUtil.isInteger(minFinalResultRange.getText())
                || !NumUtil.isInteger(maxFinalResultRange.getText())) {
            showInvalidParamsAlertDialog("请检测输入的最终计算结果范围");
            return false;
        } else if (Integer.valueOf(minFinalResultRange.getText())
                >= Integer.valueOf(maxFinalResultRange.getText())) {
            showInvalidParamsAlertDialog("最终计算结果范围最大值必须大于最小值！");
            return false;
        }
        return true;
    }

    /**
     * 显示参数有误的提示框
     * @param content 待显示的内容
     */
    private void showInvalidParamsAlertDialog(String content) {
        AlertDialog.with((Stage) generateButton.getScene().getWindow())
                .setTitle("参数有误！")
                .setBody(content)
                .addActionButton("知道了", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(AlertDialog dialog, MouseEvent event) {
                        dialog.hideWithAnimation();
                    }
                })
                .show();
    }

    /**
     * 获取arithmeticBean {@link ArithmeticBean}
     * @return arithmeticBean的克隆对象
     */
    public ArithmeticBean getArithmeticBean() {
        return arithmeticBean.clone();
    }

    /**
     * 算式生成器Runnable，若生成的算术题过多会阻塞UI主线程，
     * 所以算术生成器应该运行在子线程中
     */
    private class ArithmeticGeneratorRunnable implements Runnable {

        private final ArithmeticGenerator.OnGenerateArithmeticListener listener;

        ArithmeticGeneratorRunnable(ArithmeticGenerator.OnGenerateArithmeticListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            ArithmeticGenerator.with()
                    // 设置运算数个数
                    .setOperandCount(arithmeticBean.getOperandCount())
                    // 设置生成的问题个数
                    .setQuestionCount(arithmeticBean.getQuestionCount())
                    // 设置算式中运算数的范围，支持负数，默认0-100
                    .setNumRange(arithmeticBean.getMinNum(),
                            arithmeticBean.getMaxNum())
                    // 设置中间计算结果的范围
                    .setIntermediateResultRange(arithmeticBean.getMinIntermediateResult(),
                            arithmeticBean.getMaxIntermediateResult())
                    // 设置最终计算结果的范围
                    .setFinalResultRange(arithmeticBean.getMinFinalResult(),
                            arithmeticBean.getMaxFinalResult())
                    // 是否启用括号
                    .setEnableBracket(arithmeticBean.isEnableBracket())
                    // 是否启用整除
                    .setEnableExactDivision(arithmeticBean.isEnableExactDivision())
                    // 是否启用算术运算符等概率出现
                    .setEqualProbability(arithmeticBean.isEqualProbability())
                    // 添加加法运算符
                    .addOperator(ArithmeticOperator.ADDITION)
                    // 添加减法运算符
                    .addOperator(ArithmeticOperator.SUBTRACTION)
                    // 添加乘法运算符
                    .addOperator(ArithmeticOperator.MULTIPLICATION)
                    // 添加除法运算符
                    .addOperator(ArithmeticOperator.DIVISION)
                    //生成算式生成监听器
                    .setListener(listener)
                    // 启动生成器
                    .start();
        }
    }

    /**
     *
     */
    private static final class InputController {
        @FXML
        private JFXListView<?> toolbarPopupList;

        // close application
        @FXML
        private void submit() {
            switch (toolbarPopupList.getSelectionModel().getSelectedIndex()) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    Platform.exit();
                    break;
                default:
                    break;
            }
        }
    }
}
