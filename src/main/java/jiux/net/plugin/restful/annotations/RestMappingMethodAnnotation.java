package jiux.net.plugin.restful.annotations;

/**
 * @author QiLing
 * @date 2022/4/26 11:33 AM
 **/
public enum RestMappingMethodAnnotation {
    /**
     * RequestMapping
     */
    REST_MAPPING("com.timevale.mandarin.common.annotation.RestMapping", null),;

    private final String qualifiedName;

    private final String methodName;

    RestMappingMethodAnnotation(String qualifiedName, String methodName) {
        this.qualifiedName = qualifiedName;
        this.methodName = methodName;
    }

    public String methodName() {
        return this.methodName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getShortName() {
        return qualifiedName.substring(qualifiedName.lastIndexOf(".") - 1);
    }

    public static RestMappingMethodAnnotation getByQualifiedName(String qualifiedName) {
        for (RestMappingMethodAnnotation springRequestAnnotation : RestMappingMethodAnnotation.values()) {
            if (springRequestAnnotation.getQualifiedName().equals(qualifiedName)) {
                return springRequestAnnotation;
            }
        }
        return null;
    }

    public static RestMappingMethodAnnotation getByShortName(String requestMapping) {
        for (RestMappingMethodAnnotation springRequestAnnotation : RestMappingMethodAnnotation.values()) {
            if (springRequestAnnotation.getQualifiedName().endsWith(requestMapping)) {
                return springRequestAnnotation;
            }
        }
        return null;
    }

}
