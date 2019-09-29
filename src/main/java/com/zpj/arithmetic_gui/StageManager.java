package com.zpj.arithmetic_gui;

import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * 该类用于管理Stage{@link Stage} 以及Controller
 * 主要用于多个窗口间传递数据
 * @author Z-P-J
 */
public class StageManager {

    private static final Map<String, Stage> STAGE_MAP = new HashMap<>();
    private static final Map<String, Object> CONTROLLER_MAP = new HashMap<>();

    /**
     * 将Stage{@link Stage}装入map中
     * @param clazz Stage{@link Stage}的Class对象，其name作为map中的key
     * @param stage {@link Stage}
     */
    public static void putState(Class clazz, Stage stage) {
        STAGE_MAP.put(clazz.getName(), stage);
    }

    /**
     * 将Controller装入map中
     * @param clazz Controller的Class对象，其name作为map中的key
     * @param controller Controller对象
     */
    public static void putController(Class clazz, Object controller) {
        CONTROLLER_MAP.put(clazz.getName(), controller);
    }

    /**
     * 根据Class{@link Class}对象的name获取Stage{@link Stage}
     * @param clazz Stage{@link Stage}对象的Class对象{@link Class}
     * @return Stage对象{{@link Stage}
     */
    public static Stage getStage(Class clazz) {
        return STAGE_MAP.get(clazz.getName());
    }

    /**
     * 根据Class{@link Class}对象的name获取Controller
     * @param clazz Controller对象的Class对象{@link Class}
     * @return Object
     */
    public static Object getController(Class clazz) {
        return CONTROLLER_MAP.get(clazz.getName());
    }

}
