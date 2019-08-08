package com.example.arouter_api.launcher;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.example.arouter_api.exception.InitException;
import com.example.arouter_api.template.ILogger;
import com.example.arouter_api.template.IRouteGroup;
import com.example.arouter_api.util.ClassUtils;
import com.example.arouter_api.util.DefaultLogger;

import java.util.HashMap;
import java.util.List;

import static com.example.arouter_api.util.Consts.ROUTE_ROOT_PAKCAGE;
import static com.example.arouter_api.util.Consts.TAG;


public class AzpRouter {
    //todo 把这个类做成单列
    private  static AzpRouter router = new AzpRouter();
    private static Context mContext;
    private static HashMap<String,String> activitys = new HashMap<>();
    private static boolean hasInit = false;
    private static ILogger logger ;

    public static AzpRouter getInstance() {
        return router;
    }

    private static void init(Application context) {
        // These class was generate by arouter-compiler.
        mContext = context;
        logger = new DefaultLogger(TAG);
        try {
            List<String> classFileNames = ClassUtils.getFileNameByPackageName(context, ROUTE_ROOT_PAKCAGE);
            for (String classFileName : classFileNames) {
                Class<?> aClass = Class.forName(classFileName);
                IRouteGroup iRouteGroup = (IRouteGroup) aClass.newInstance();
                iRouteGroup.putActivitys(activitys);
            }
            hasInit = true;
        } catch (Exception e) {
            logger.error(ILogger.defaultTag,e.getMessage());
        }
    }

    public void build(String path) {
//        Intent intent = new Intent(context, LoginActivity.class);
        if (!hasInit) {
            throw new InitException("使用该方法前，必须调用init 方法");
        }
        try {
            Intent intent = new Intent(mContext, Class.forName(activitys.get(path)));
        } catch (ClassNotFoundException e) {
            logger.error(ILogger.defaultTag,e.getMessage());
        }
    }

}
