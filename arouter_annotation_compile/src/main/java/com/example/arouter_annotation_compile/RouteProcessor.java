package com.example.arouter_annotation_compile;


import com.example.arouter_annotation.BindPath;
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
import java.io.Writer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

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

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        boolean parseResult = false;
        Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(BindPath.class);
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
            TypeElement type_IRouteGroup = elementUtil.getTypeElement("com.example.arouter.IRouteGroup");


            /**     Map<String,String> altas
             *  Build input type, format as :
             *
             *  ```Map<String, String>```
             */
            ParameterizedTypeName inputMapTypeOfRoot = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(String.class)

            );


            /**
             * Build input param name.
             *      "atlas"
             */
            ParameterSpec groupParamSpec = ParameterSpec.builder(inputMapTypeOfRoot, "atlas").build();
            /**
             * Build method : 'putActivitys'
             */
            MethodSpec.Builder putActivitysMethodOfRootBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(groupParamSpec);

            for (Element routeElement : routeElements) {
                TypeElement typeElement = (TypeElement) routeElement;
                BindPath annotation = typeElement.getAnnotation(BindPath.class);
                /**
                 *
                 */
                putActivitysMethodOfRootBuilder.addStatement(
                        "atlas.put($S,$S)",
                        annotation.value(),
                        typeElement.getQualifiedName());

                // Generate groups
                String groupFileName = NAME_OF_GROUP ;
                JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                        TypeSpec.classBuilder(groupFileName)
                                .addJavadoc(WARNING_TIPS)
                                .addSuperinterface(ClassName.get(type_IRouteGroup))
                                .addModifiers(PUBLIC)
                                .addMethod(putActivitysMethodOfRootBuilder.build())
                                .build()
                ).build().writeTo(mFiler);
            }
        } else {
            logger.info(">>> Found routes, size is 0 ");
        }
    }
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportAnnotations = new HashSet<>();
        supportAnnotations.add(BindPath.class.getCanonicalName());     // This annotation mark class which can be router.
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
