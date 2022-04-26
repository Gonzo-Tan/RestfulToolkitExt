package jiux.net.plugin.restful.common.restserver;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import jiux.net.plugin.restful.annotations.RestMappingMethodAnnotation;
import jiux.net.plugin.restful.annotations.SpringRequestMethodAnnotation;
import jiux.net.plugin.restful.common.PsiAnnotationHelper;
import jiux.net.plugin.restful.common.RestSupportedAnnotationHelper;
import jiux.net.plugin.restful.method.RequestPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author QiLing
 * @date 2022/4/25 8:33 PM
 **/
public class RestServerAnnotationHelper implements RestSupportedAnnotationHelper {

    public static List<RequestPath> getRequestPaths(PsiClass psiClass) {
        PsiAnnotation[] annotations = psiClass.getModifierList().getAnnotations();
        if (annotations == null) {
            return null;
        }

        PsiAnnotation requestMappingAnnotation = null;
        List<RequestPath> list = new ArrayList<>();
        for (PsiAnnotation annotation : annotations) {
            for (RestMappingMethodAnnotation mappingAnnotation : RestMappingMethodAnnotation.values()) {
                if (annotation.getQualifiedName().equals(mappingAnnotation.getQualifiedName())) {
                    requestMappingAnnotation = annotation;
                }
            }
        }

        if (requestMappingAnnotation != null) {
            List<RequestPath> requestMappings = getRequestMappings(requestMappingAnnotation, "");
            if (requestMappings.size() > 0) {
                list.addAll(requestMappings);
            }
        } else {
            // TODO : Inheritance RequestMapping
            PsiClass superClass = psiClass.getSuperClass();
            if (superClass != null && !superClass.getQualifiedName().equals("java.lang.Object")) {
                list = getRequestPaths(superClass);
            } else {
                list.add(new RequestPath("/", null));
            }
        }

        return list;
    }

    /**
     * @param annotation
     * @param defaultValue
     * @return
     */
    private static List<RequestPath> getRequestMappings(PsiAnnotation annotation, String defaultValue) {
        List<RequestPath> mappingList = new ArrayList<>();
        RestMappingMethodAnnotation requestAnnotation = RestMappingMethodAnnotation.getByQualifiedName(annotation.getQualifiedName());

        if (requestAnnotation == null) {
            return new ArrayList<>();
        }

        List<String> methodList;
        if (requestAnnotation.methodName() != null) {
            methodList = Arrays.asList(requestAnnotation.methodName());
        } else {
            methodList = PsiAnnotationHelper.getAnnotationAttributeValues(annotation, "method");
        }

        List<String> pathList = PsiAnnotationHelper.getAnnotationAttributeValues(annotation, "path");

        if (pathList.size() == 0) {
            pathList.add(defaultValue);
        }

        // todo: Handle RequestMapping without setting value or path

        if (methodList.size() > 0) {
            for (String method : methodList) {
                for (String path : pathList) {
                    mappingList.add(new RequestPath(path, method));
                }
            }
        } else {
            for (String path : pathList) {
                // RestMapping 默认是post类型
                mappingList.add(new RequestPath(path, "POST"));
            }
        }

        return mappingList;
    }

    public static List<RequestPath> getRequestPaths(PsiMethod psiMethod) {
        PsiAnnotation annotation = psiMethod.getAnnotation(RestMappingMethodAnnotation.REST_MAPPING.getQualifiedName());
        if (annotation == null) {
            return getRequestPathsFromRestServer(psiMethod);
        } else {
            return getRequestMappings(annotation, "");
        }

    }

    public static List<RequestPath> getRequestPathsFromRestServer(PsiMethod psiMethod) {
        List<RequestPath> mappingList = new ArrayList<>();

        StringBuilder sb = new StringBuilder(psiMethod.getName());
        PsiParameter[] parameters = psiMethod.getParameterList().getParameters();

        for (PsiParameter parameter : parameters) {
            sb.append("/");
            sb.append(parameter.getName());
        }

        String path = sb.toString();
        mappingList.add(new RequestPath(path, "POST"));
        mappingList.add(new RequestPath(path, "GET"));
        return mappingList;
    }

}
