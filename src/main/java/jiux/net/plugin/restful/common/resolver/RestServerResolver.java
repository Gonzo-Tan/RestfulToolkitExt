package jiux.net.plugin.restful.common.resolver;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.java.stubs.index.JavaAnnotationIndex;
import com.intellij.psi.search.GlobalSearchScope;
import jiux.net.plugin.restful.annotations.PathMappingAnnotation;
import jiux.net.plugin.restful.annotations.RestServerAnnotation;
import jiux.net.plugin.restful.common.restserver.RestServerAnnotationHelper;
import jiux.net.plugin.restful.common.spring.RequestMappingAnnotationHelper;
import jiux.net.plugin.restful.method.RequestPath;
import jiux.net.plugin.restful.method.action.PropertiesHandler;
import jiux.net.plugin.restful.navigation.action.RestServiceItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author QiLing
 * @date 2022/4/25 8:20 PM
 **/
public class RestServerResolver extends BaseServiceResolver {
    PropertiesHandler propertiesHandler;

    public RestServerResolver(Module module) {
        myModule = module;
        propertiesHandler = new PropertiesHandler(module);
    }

    public RestServerResolver(Project project) {
        myProject = project;

    }

    @Override
    public List<RestServiceItem> getRestServiceItemList(Project project, GlobalSearchScope globalSearchScope) {

        List<RestServiceItem> itemList = new ArrayList<>();
        RestServerAnnotation[] restServerAnnotations = RestServerAnnotation.values();
        for (PathMappingAnnotation restServerAnnotation : restServerAnnotations) {
            // java: Classes marked with the (Rest)Controller annotation, i.e. the Controller class
            // FIXME java.lang.Throwable: Slow operations are prohibited on EDT. See SlowOperations.assertSlowOperationsAreAllowed javadoc.
            // https://youtrack.jetbrains.com/issue/IDEA-273415
            Collection<PsiAnnotation> psiAnnotations = JavaAnnotationIndex.getInstance()
                    .get(restServerAnnotation.getShortName(), project, globalSearchScope);

            for (PsiAnnotation psiAnnotation : psiAnnotations) {
                PsiModifierList psiModifierList = (PsiModifierList) psiAnnotation.getParent();
                PsiElement psiElement = psiModifierList.getParent();

                PsiClass psiClass = (PsiClass) psiElement;
                List<RestServiceItem> serviceItemList = getServiceItemList(psiClass);
                itemList.addAll(serviceItemList);
            }

        }
        return itemList;
    }

    protected List<RestServiceItem> getServiceItemList(PsiClass psiClass) {
        PsiMethod[] psiMethods = psiClass.getMethods();

        List<RestServiceItem> itemList = new ArrayList<>();

        List<RequestPath> classRequestPaths = RestServerAnnotationHelper.getRequestPaths(psiClass);

        for (PsiMethod psiMethod : psiMethods) {
            List<RequestPath> methodRequestPaths = RestServerAnnotationHelper.getRequestPaths(psiMethod);

            for (RequestPath classRequestPath : classRequestPaths) {
                for (RequestPath methodRequestPath : methodRequestPaths) {
                    String path = classRequestPath.getPath();
                    RestServiceItem item = createRestServiceItem(psiMethod, path, methodRequestPath);
                    itemList.add(item);
                }
            }

        }
        return itemList;
    }
}
