package com.zpj.arithmetic_gui;

import com.jfoenix.controls.*;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import word.api.interfaces.IDocument;
import word.w2004.Document2004;
import word.w2004.elements.BreakLine;
import word.w2004.elements.Heading1;
import word.w2004.elements.Paragraph;
import word.w2004.style.HeadingStyle;

import javax.annotation.PostConstruct;
import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ViewController
 * PreviewApplication视图控制器
 * @author Z-P-J
 * value: fxml文件的路径
 * title: 标题
 */
@ViewController(value = "/fxml/Preview.fxml", title = "标题")
public final class PreviewController {

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML
    private StackPane root;

    @FXML
    private TextField rowCountText;

    @FXML
    private JFXToggleButton showResultButton;

    @FXML
    private JFXButton generateDocButton;

    @FXML
    private JFXButton printButton;

    @FXML
    private Label titleLabel;

    @FXML
    private TextArea textArea;

    /**
     * init fxml when loaded.
     * @throws Exception 抛出的异常
     */
    @PostConstruct
    public void init() throws Exception {
        StageManager.putController(getClass(), this);

        MainController mainApplication = (MainController) StageManager.getController(MainController.class);
        ArithmeticBean arithmeticBean = mainApplication.getArithmeticBean();


        generateDocButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                generateDoc(arithmeticBean, false);
            }
        });

        printButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                generateDoc(arithmeticBean, true);
            }
        });

        showResultButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
//                initTextArea(arithmeticBean);
            }
        });

        rowCountText.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                System.out.println(rowCountText.getText());
//                initTextArea(arithmeticBean);
            }
        });

        if (arithmeticBean != null) {
//            initTextArea(arithmeticBean);
        } else {
            generateDocButton.setDisable(true);
            printButton.setDisable(true);
            rowCountText.setDisable(true);
        }
    }

    /**
     * 生成word文档
     * @param arithmeticBean {@link ArithmeticBean}
     * @param isPrint 是否打印
     */
    private void generateDoc(ArithmeticBean arithmeticBean, boolean isPrint) {
        long tempTime = System.currentTimeMillis();
        String docStr = getDocString(arithmeticBean);
        String filePath = String.format("D:\\%d道算术题 %s.doc", arithmeticBean.getQuestionCount(), format.format(new Date()));
        File fileObj = new File(filePath);
        //将生成的xml内容写入到文件中。
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fileObj);
            writer.println(docStr);
            if (isPrint) {
                PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
                DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
                PrintService[] printService = PrintServiceLookup.lookupPrintServices(flavor, pras);
                PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
                PrintService service = ServiceUI.printDialog(null, 400, 400, printService
                        , defaultService, flavor, pras);
                if (service != null) {
                    try {
                        DocPrintJob job = service.createPrintJob();
                        FileInputStream fis = new FileInputStream(fileObj);
                        DocAttributeSet das = new HashDocAttributeSet();
                        Doc doc = new SimpleDoc(fis, flavor, das);
                        job.print(doc, pras);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
//                System.out.println("delta_time=" + (System.currentTimeMillis() - tempTime));
                showDialog("导出成功！", "文件已保存至" + filePath + " 用时：" + (System.currentTimeMillis() - tempTime) + "ms");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showDialog("出错了！", "错误信息：" + e.getMessage());
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    /**
     * 将字符串转换为word文档字符串
     * @param arithmeticBean {@link ArithmeticBean}
     * @return word文档字符串
     */
    private String getDocString(ArithmeticBean arithmeticBean) {
        IDocument myDoc = new Document2004();
        myDoc.addEle(Heading1.with(arithmeticBean.getQuestionCount() + "道算术题", HeadingStyle.Align.CENTER).create());
        myDoc.addEle(BreakLine.times(1).create());
        StringBuilder content = new StringBuilder();
        int row = Integer.valueOf(rowCountText.getText());
        for (int i = 0; i < arithmeticBean.getQuestionCount(); i++) {
            content.append("(")
                    .append(i + 1)
                    .append(")  ")
                    .append(arithmeticBean.getArithmeticList().get(i));
            if (showResultButton.isSelected()) {
                content.append(arithmeticBean.getResultList().get(i));
            }
            if ((i + 1) % row == 0) {
                myDoc.addEle(Paragraph.with(content.toString()).create());
                content = new StringBuilder();
            } else {
                content.append("          ");
            }
        }

        if (!content.toString().isEmpty()) {
            myDoc.addEle(Paragraph.with(content.toString()).create());
        }
        return myDoc.toString();
    }

    /**
     * 将生成的题目显示在窗口上
     * @param arithmeticBean {@link ArithmeticBean}
     */
    private void initTextArea(ArithmeticBean arithmeticBean) {
        StringBuilder content = new StringBuilder();
        int row = Integer.valueOf(rowCountText.getText());
        for (int i = 0; i < arithmeticBean.getQuestionCount(); i++) {
            content.append("(")
                    .append(i + 1)
                    .append(")  ")
                    .append(arithmeticBean.getArithmeticList().get(i));
            if (showResultButton.isSelected()) {
                content.append(arithmeticBean.getResultList().get(i));
            }
            if ((i + 1) % row == 0) {
                content.append("\n");
            } else {
                content.append("\t\t");
            }
        }
        textArea.setText(content.toString());
    }

    /**
     * 显示提示框
     * @param title 标题
     * @param content 内容
     */
    private void showDialog(String title, String content) {
        AlertDialog.with((Stage) generateDocButton.getScene().getWindow())
                .setTitle(title)
                .setBody(content)
                .addActionButton("知道了", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(AlertDialog dialog, MouseEvent event) {
                        dialog.close();
                    }
                })
                .show();
    }
}
