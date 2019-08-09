package com.example.arouter_annotation_compile;


import com.example.arouter_annotation.Route;
import com.example.arouter_annotation.bean.RouteMeta;
import com.example.arouter_annotation_compile.utils.Consts;
import com.example.arouter_annotation_compile.utils.Logger;
import com.google.auto.service.AutoService;
import com.google.common.collect.Sets;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.example.arouter_annotation_compile.utils.Consts.KEY_MODULE_NAME;
import static com.example.arouter_annotation_compile.utils.Consts.METHOD_LOAD_INTO;
import static com.example.arouter_annotation_compile.utils.Consts.NAME_OF_GROUP;
import static com.example.arouter_annotation_compile.utils.Consts.PACKAGE_OF_GENERATE_FILE;
import static com.example.arouter_annotation_compile.utils.Consts.WARNING_TIPS;
import static javax.lang.model.element.Modifier.PUBLIC;

@AutoService(Processor.class)
//@SupportedSourceVersion(SourceVersion.RELEASE_7)
//@SupportedAnnotationTypes("com.example.arouter.IRouteGroup")
public class RouteProcessor extends AbstractProcessor {
    private Filer mFiler;       // File util, write class file into disk.
    private Logger logger;
    private Types typeUtil;
    private Elements elementUtil;
    private String moduleName;
    private Map<String ,Class<?>> rootMap;
    private Map<String ,Set<RouteMeta>> groupMap;

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        boolean parseResult = false;
        Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        try {
            this.parseRoutes(routeElements);
            parseResult = true;
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }
        return parseResult;
    }

    private void parseRoutes(Set<? extends Element> routeElements) throws IOException {
        if (CollectionUtils.isNotEmpty(routeElements)) {
            logger.info(">>> Found routes, size is " + routeElements.size() + " <<<");
            TypeElement type_IRouteGroup = elementUtil.getTypeElement(Consts.IROUTE_GROUP);


            /**     Map<String,RouteMeta> atlas
             *  Build input type, format as :
             *
             *  ```Map<String, RouteMeta>```
             */
            ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouteMeta.class)

            );


            /**
             * Build input param name.
             *      "atlas"
             */
            ParameterSpec groupParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "atlas").build();

            groupMap.clear();
            //给注解的所有元素分组
            for (Element routeElement : routeElements) {
                TypeElement typeElement = (TypeElement) routeElement;//todo 有可能添加注解的不是类哦
                Route route = null;
                try {
//                    route = routeElement.asType().getAnnotation(Route.class);//route报出null
                    route = typeElement.getAnnotation(Route.class);
                    RouteMeta routeMeta = new RouteMeta(route, typeElement);
                    categories(routeMeta);
                } catch (Exception e) {
                    logger.info(">>> Found routes, size is " + routeElements.size() + " <<<"+e.getMessage());
                    e.printStackTrace();
                }

            }

            //generate file
            Set<Map.Entry<String, Set<RouteMeta>>> entries = groupMap.entrySet();
            for (Map.Entry<String, Set<RouteMeta>> entry : entries) {
                String group = entry.getKey();
                Set<RouteMeta> metas = entry.getValue();
                /**
                 * Build method : 'loadInfo'
                 */
                MethodSpec.Builder loadInfoMethodOfGroupBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC)
                        .addParameter(groupParamSpec);
                //Build group method body
                for (RouteMeta meta : metas) {

                    loadInfoMethodOfGroupBuilder.addStatement(
                            "atlas.put($S,$T.build($S, $S, $T.class))",
                            meta.getPath(),
                            ClassName.get(RouteMeta.class),
                            meta.getPath(),
                            meta.getGroup(),
                            meta.getElementType()
                            );
                }
                // Generate groups
                String groupFileName = NAME_OF_GROUP + group;
                JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                        TypeSpec.classBuilder(groupFileName)
                                .addJavadoc(WARNING_TIPS)
                                .addSuperinterface(ClassName.get(type_IRouteGroup))
                                .addModifiers(PUBLIC)
                                .addMethod(loadInfoMethodOfGroupBuilder.build())
                                .build()
                ).build().writeTo(mFiler);
            }

        } else {
            logger.info(">>> Found routes, size is 0 ");
        }
    }

    /**
     * sort in group
     * @param routeMeta
     */
    private void categories(RouteMeta routeMeta) {
        if (verityRouteMeta(routeMeta)) {
            logger.info("categories is start , group is " + routeMeta.getGroup()+" >>>path:"+routeMeta.getPath());
            Set<RouteMeta> routeMetas = groupMap.get(routeMeta.getGroup());
            if (CollectionUtils.isEmpty(routeMetas)) {
                routeMetas = new TreeSet<RouteMeta>(new Comparator<RouteMeta>() {
                    @Override
                    public int compare(RouteMeta routeMeta, RouteMeta t1) {
                        return routeMeta.getPath().compareTo(t1.getPath());
                    }
                });
                routeMetas.add(routeMeta);
                groupMap.put(routeMeta.getGroup(), routeMetas);
            } else {
                routeMetas.add(routeMeta);
            }
        } else {
            logger.warning("route meta verify group failed , group is" + routeMeta.getGroup());
        }

    }

    /**
     * Verify the route meta
     *
     * @param routeMeta raw meta
     */
    private boolean verityRouteMeta(RouteMeta routeMeta) {
        String path = routeMeta.getPath();
        if (StringUtils.isEmpty(path)||!path.startsWith("/")) {
            return false;
        }
        String group = routeMeta.getGroup();
        if (StringUtils.isEmpty(group)) {
            try {
                //使用默认的group（path的第一个单词）
                group = path.substring(1, path.indexOf("/", 1));
                if (StringUtils.isEmpty(group)) {
                    return false;
                }
                routeMeta.setGroup(group);
                return true;
            } catch (NullPointerException e) {
                logger.info("extract group failed" + e.getMessage());
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportAnnotations = new HashSet<>();
        supportAnnotations.add(Route.class.getCanonicalName());     // This annotation mark class which can be router.
        return supportAnnotations;
    }
    /**
     * If the processor class is annotated with {@link
     * SupportedSourceVersion}, return the source version in the
     * annotation.  If the class is not so annotated, {@link
     * SourceVersion#RELEASE_6} is returned.
     *
     * @return the latest source version supported by this processor
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
    @Override
    public Set<String> getSupportedOptions() {
        return Sets.newHashSet(KEY_MODULE_NAME);
    }
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnv.getFiler();                  // Generate class.
        typeUtil = processingEnv.getTypeUtils();            // Get type utils.
        elementUtil = processingEnv.getElementUtils();
        logger = new Logger(processingEnvironment.getMessager());
        groupMap = new HashMap<>();

        // Attempt to get user configuration [moduleName]
        Map<String, String> options = processingEnv.getOptions();
        if (MapUtils.isNotEmpty(options)) {
            moduleName = options.get(KEY_MODULE_NAME);
        }

        if (StringUtils.isNotEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");

            logger.info("The user has configuration the module name, it was [" + moduleName + "]");
        } else {
            logger.error("These no module name, at 'build.gradle', like :\n" +
                    "apt {\n" +
                    "    arguments {\n" +
                    "        moduleName project.getName();\n" +
                    "    }\n" +
                    "}\n");
            throw new RuntimeException("ARouter::Compiler >>> No module name, for more information, look at gradle log.");
        }

        logger.info(">>> RouteProcessor init. <<<");
    }
}
