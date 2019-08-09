package com.example.arouter_api.launcher;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.arouter_api.exception.InitException;
import com.example.arouter_api.template.ILogger;
import com.example.arouter_api.util.DefaultLogger;

import java.util.HashMap;

import static com.example.arouter_api.util.Consts.TAG;


public class ARouter {
    //todo 把这个类做成单列
    private  static ARouter router = new ARouter();
    private static Context mContext;
    private static HashMap<String,String> activitys = new HashMap<>();
    private static boolean hasInit = false;
    private static ILogger logger ;

    public static ARouter getInstance() {
        return router;
    }

    public static void init(Application context) {
        // These class was generate by arouter-compiler.
        mContext = context;
        logger = new DefaultLogger(TAG);
        try {
//            List<String> classFileNames = ClassUtils.getFileNameByPackageName(context, ROUTE_ROOT_PAKCAGE);
//            for (String classFileName : classFileNames) {
//                Class<?> aClass = Class.forName(classFileName);
//                IRouteGroup iRouteGroup = (IRouteGroup) aClass.newInstance();
//                iRouteGroup.loadInfo(activitys);
//            }
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
            String className = activitys.get(path);
            if (TextUtils.isEmpty(className)) {
                return;
            }
            Intent intent = new Intent(mContext, Class.forName(className));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (ClassNotFoundException e) {
            logger.error(ILogger.defaultTag,e.getMessage());
        }
    }

}
