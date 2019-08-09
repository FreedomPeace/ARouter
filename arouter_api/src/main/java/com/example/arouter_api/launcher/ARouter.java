package com.example.arouter_api.launcher;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.arouter_annotation.bean.RouteMeta;
import com.example.arouter_api.exception.InitException;
import com.example.arouter_api.template.ILogger;
import com.example.arouter_api.template.IRouteGroup;
import com.example.arouter_api.template.IRouterRoot;
import com.example.arouter_api.util.ClassUtils;
import com.example.arouter_api.util.DefaultLogger;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.arouter_api.util.Consts.DOT;
import static com.example.arouter_api.util.Consts.ROUTE_ROOT_PAKCAGE;
import static com.example.arouter_api.util.Consts.SDK_NAME;
import static com.example.arouter_api.util.Consts.SEPARATOR;
import static com.example.arouter_api.util.Consts.SUFFIX_ROOT;
import static com.example.arouter_api.util.Consts.TAG;


public class ARouter {
    //todo 把这个类做成单列
    private  static ARouter router = new ARouter();
    private static Context mContext;
    private static HashMap<String, Class<? extends IRouteGroup>> routes = new HashMap<>();
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
            List<String> classFileNames = ClassUtils.getFileNameByPackageName(context, ROUTE_ROOT_PAKCAGE);
            for (String classFileName : classFileNames) {
                if (classFileName.startsWith(ROUTE_ROOT_PAKCAGE+DOT+SDK_NAME+SEPARATOR+SUFFIX_ROOT)) {
                    Class<?> aClass = Class.forName(classFileName);
                    IRouterRoot iRouterRoot = (IRouterRoot) aClass.newInstance();
                    iRouterRoot.loadInfo(routes);
                }

            }
            hasInit = true;
        } catch (Exception e) {
            logger.error(ILogger.defaultTag,e.getMessage());
        }
    }

    public void build(String path) {
        build(path,null);
    }

    public void build(String path, String group) {

        if (!hasInit) {
            throw new InitException("使用该方法前，必须调用init 方法");
        }
        RouteMeta meta = new RouteMeta(path,group,null);
        if (verifyPath(meta)) {
            try {
                Class<? extends IRouteGroup> aClass = routes.get(meta.getGroup());
                IRouteGroup iRouteGroup = aClass.newInstance();
                Map<String, RouteMeta> altas = new HashMap<>();
                iRouteGroup.loadInfo(altas);
                RouteMeta meta1 = altas.get(meta.getPath());
                String  destination= meta1.getDestination().getCanonicalName();
                if (TextUtils.isEmpty(destination)) {
                    return;
                }
                Intent intent = new Intent(mContext, Class.forName(destination));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } catch (Exception e) {
                logger.error(ILogger.defaultTag,e.getMessage());
                Toast.makeText(mContext, "找不到路由--\npath::" + path + "\ngroup:: " +group, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "路由的path:" + path + "是无效的", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    /**
     * 校验传进来的path是合规的
     * @param meta
     * @return
     */
    private boolean verifyPath(RouteMeta meta) {
        String path = meta.getPath();
        String group = meta.getGroup();
        if (StringUtils.isEmpty(path) || !path.startsWith("/")) {
            return false;
        }
        if (StringUtils.isEmpty(group)) {
            group = path.substring(1, path.indexOf("/", 1));//group (default value :path first word)
            if (StringUtils.isEmpty(group)) {
                return false;
            }
            meta.setGroup(group);
        }

        return true;
    }

}
