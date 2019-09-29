package com.zpj.arithmetic_gui;

import com.jfoenix.controls.JFXDecorator;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 预览窗口，当生成
 * @author Z-P-J
 */
public class PreviewApplication extends Application {

    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    /**
     * 重写Application{@link Application}的start方法，
     * 当调用launch启动程序时，将在start方法初始化图形化界面
     * @param stage {@link Stage}
     * @throws Exception 抛出的异常
     */
    @Override
    public void start(Stage stage) throws Exception {
        StageManager.putState(getClass(), stage);
        //设置Controller
        Flow flow = new Flow(PreviewController.class);
        DefaultFlowContainer container = new DefaultFlowContainer();

        //上下文对象
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", stage);
        flow.createHandler(flowContext).start(container);

        //The root node of the scene graph
        JFXDecorator decorator = new JFXDecorator(stage, container.getView());
        decorator.setCustomMaximize(true);

        //设置标题
        stage.setTitle("算式生成器");
        //设置窗口宽高
        Scene scene = new Scene(decorator, 900, 900);
        //添加css样式
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(PreviewApplication.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                PreviewApplication.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                           PreviewApplication.class.getResource("/css/jfoenix-main-demo.css").toExternalForm());

        stage.setScene(scene);
        //显示窗口
        stage.show();
    }

}
