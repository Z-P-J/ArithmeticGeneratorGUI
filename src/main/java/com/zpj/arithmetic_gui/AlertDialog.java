package com.zpj.arithmetic_gui;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * 对JFXAlert进行封装
 * @author Z-P-J
 */
public class AlertDialog {

    /**
     * 点击监听器
     */
    public interface OnClickListener{
        /**
         * 点击时回调
         * @param dialog {@link AlertDialog}
         * @param event 鼠标事件{@link MouseEvent}
         */
        void onClick(AlertDialog dialog, MouseEvent event);
    }

    private final JFXAlert alert;
    private final JFXDialogLayout layout = new JFXDialogLayout();
    private Label label = new Label("取消");
    private boolean isClosed = false;

    private AlertDialog(Stage stage) {
        alert = new JFXAlert(stage);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        alert.setContent(layout);
    }

    public static AlertDialog with(Stage stage) {
        return new AlertDialog(stage);
    }

    /**
     * 设置标题
     * @param title 标题
     * @return this
     */
    public AlertDialog setTitle(String title) {
        label.setText(title);
        layout.setHeading(label);
        return this;
    }

    /**
     * 设置标题label
     * @param label {@link Label} 标题标签对象
     * @return this
     */
    public AlertDialog setTitle(Label label) {
        this.label = label;
        layout.setHeading(label);
        return this;
    }

    /**
     * 设置弹窗内容
     * @param body 弹窗内容
     * @return this
     */
    public AlertDialog setBody(String body) {
        layout.setBody(new Label(body));
        return this;
    }

    /**
     * 设置弹窗内容Node {@link Node}
     * @param nodes the nodes
     * @return this
     */
    public AlertDialog setBody(Node...nodes) {
        layout.setBody(nodes);
        return this;
    }

    /**
     * 给弹窗添加按钮
     * @param text 按钮显示的文字
     * @param listener 按钮点击监听器 {@link OnClickListener}
     * @return this
     */
    public AlertDialog addActionButton(String text, OnClickListener listener) {
        JFXButton closeButton = new JFXButton(text);
        closeButton.getStyleClass().add("dialog-accept");
        closeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (listener != null) {
                    listener.onClick(AlertDialog.this, event);
                }
            }
        });
        layout.setActions(closeButton);
        return this;
    }

    /**
     * 显示弹窗
     * @return this
     */
    public AlertDialog show() {
        alert.show();
        return this;
    }

    /**
     * 显示弹窗并等待用户操作
     * @return this
     */
    public AlertDialog showAndWait() {
        alert.showAndWait();
        return this;
    }

    /**
     * 关闭弹窗
     */
    public void close() {
        isClosed = true;
        alert.close();
    }

    /**
     * 弹窗关闭动画
     */
    public void hideWithAnimation() {
        isClosed = true;
        alert.hideWithAnimation();
    }

    /**
     * 弹窗是否关闭
     * @return 是否关闭
     */
    public boolean isClosed() {
        return isClosed;
    }

    public void updateTitle(String title) {
        label.setText(title);
    }
}
